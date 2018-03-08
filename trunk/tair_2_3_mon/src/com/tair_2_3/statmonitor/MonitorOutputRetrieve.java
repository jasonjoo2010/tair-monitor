package com.tair_2_3.statmonitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.regex.MatchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.taobao.tair.ServerDelay;
import com.taobao.tair.ResultCode;
import com.taobao.tair.comm.DefaultTranscoder;
import com.taobao.tair.comm.TairClient;
import com.taobao.tair.comm.TairClientFactory;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.etc.TairUtil;
import com.taobao.tair.extend.impl.DefaultExtendTairManager;
import com.taobao.tair.impl.AdminTairManager;
import com.taobao.tair.impl.DefaultTairManager;
import com.taobao.tair.packet.BasePacket;
import com.taobao.tair.packet.PacketStreamer;
import com.taobao.tair.packet.RequestPingPacket;
import com.taobao.tair.packet.ReturnPacket;
import com.taobao.tair.packet.TairPacketStreamer;

public class MonitorOutputRetrieve extends java.util.TimerTask {
	private static final Log log = LogFactory
			.getLog(MonitorOutputRetrieve.class);
	public DefaultTairManager tm = new DefaultTairManager("DefaultTairManager",
			false, Runtime.getRuntime().availableProcessors() + 1);
	public DefaultTairManager tmNoHeader = null;
	public DefaultExtendTairManager tmRdb = null;
	public AdminTairManager tmAdmin = null;
	public String passwd = "tairrdb";
	private Semaphore mutex;

	private TairClientFactory factory = new TairClientFactory(1);
	private Transcoder transcoder = new DefaultTranscoder(0, null);
	private PacketStreamer streamer = new TairPacketStreamer(transcoder);
	private double pingCount = 8;
	private int timeout = 1000;
	private String configserverA;
	private String configserverB;
	private String groupname;
	private boolean isRdb = false;
	private boolean needSupportCplusplus = false;
	private boolean isTestCluster = false;
	private String clusterId;
	private boolean inited = true;

	int g_area = -1;

	private int nodecount, areacount;
	HashMap<String, ServerStat> server_stat;
	HashMap<Integer, AreaStatistics> aggregate_area_statistics;

	public MonitorOutputRetrieve() throws ClassNotFoundException, IOException {
		mutex = new Semaphore(1);

		try {
			InputStream RA = Class.forName(
					"com.tair_2_3.statmonitor.MonitorOutputRetrieve")
					.getResourceAsStream("MonitorArgs");
			Properties config = new Properties();
			config.load(RA);
			configserverA = config.getProperty("configserverA");
			configserverB = config.getProperty("configserverB");
			groupname = config.getProperty("groupname");
			isRdb = Boolean.valueOf(config.getProperty("is_rdb"));
			needSupportCplusplus = Boolean.valueOf(config
					.getProperty("need_support_cplusplus"));
			isTestCluster = Boolean.valueOf(config
					.getProperty("is_test_cluster"));
			clusterId = configserverA + "#" + configserverB + "#" + groupname;
			RA.close();
		} catch (Exception e) {
			inited = false;
			log.error(e.toString());
		}

		if (inited) {
			server_stat = new HashMap<String, ServerStat>();
			aggregate_area_statistics = new HashMap<Integer, AreaStatistics>();
			init();
		}
	}

	public void setInited(boolean inited) {
		this.inited = inited;
	}

	public boolean isInited() {
		return inited;
	}

	private void init() {
		try {
			List<String> cs = new ArrayList<String>();
			if (configserverA != null) {
				cs.add(configserverA);
			}
			if (configserverB != null) {
				cs.add(configserverB);
			}
			tm.setConfigServerList(cs);
			tm.setGroupName(groupname);
			tm.setMonitorMode(true);
			tm.init();

			if (isRdb) {
				tmRdb = new DefaultExtendTairManager("RdbTairManager", false,
						Runtime.getRuntime().availableProcessors() + 1);
				tmRdb.setConfigServerList(cs);
				tmRdb.setGroupName(groupname);
				tmRdb.setMonitorMode(true);
				tmRdb.init();
			}

			if (needSupportCplusplus) {
				tmNoHeader = new DefaultTairManager("NoHeaderTairManager",
						false, Runtime.getRuntime().availableProcessors() + 1);
				tmNoHeader.setConfigServerList(cs);
				tmNoHeader.setGroupName(groupname);
				tmNoHeader.setHeader(false);
				tmNoHeader.setMonitorMode(true);
				tmNoHeader.init();
			}

			if (isTestCluster) {
				tmAdmin = new AdminTairManager();
				tmAdmin.setConfigServerList(cs);
				tmAdmin.setGroupName(groupname);
				tmAdmin.init();
			}
		} catch (Exception e) {
			log.error(e.toString());
			inited = false;
		}
	}

	public String getClusterId() {
		return clusterId;
	}

	@Override
	public void run() {

		HashMap<String, ServerStat> _server_stat = new HashMap<String, ServerStat>();
		_server_stat.clear();
		HashMap<Integer, AreaStatistics> _aggregate_area_statistics = new HashMap<Integer, AreaStatistics>();
		_aggregate_area_statistics.clear();
		int _nodecount = -1, _areacount = -1;

		Map<String, String> stat = null;
		Set<String> keys = null;

		log.info("Retrieve Info From " + configserverA + " GroupName = "
				+ groupname);
		try {
			stat = tm.getStat(TairConstant.Q_AREA_CAPACITY, groupname, 0);
			log.info("get Q_AREA_CAPACITY info");
		} catch (Exception e) {
			log.error(e.toString());
		}

		// scan areas with capacity setting
		try {
			keys = stat.keySet();
			_areacount = keys.size();
			log.info(_areacount);
			for (String key : keys) {
				try {
					String value = stat.get(key);
					String pattern = "area\\((\\d+)\\)";
					Scanner sc = new Scanner(key);
					sc.findInLine(pattern);
					MatchResult tem_num = sc.match();
					int quota_area = Integer.parseInt(tem_num.group(1).trim());
					log.debug("put area");
					AreaStatistics quota_areainfo = null;
					if (_aggregate_area_statistics.containsKey(quota_area)) {
						quota_areainfo = _aggregate_area_statistics
								.get(quota_area);
						log.debug("operate on quota_area:" + quota_area);
					} else {
						quota_areainfo = new AreaStatistics();
						log.debug("create quota_area:" + quota_area);
						quota_areainfo.setArea(quota_area);
						_aggregate_area_statistics.put(quota_area,
								quota_areainfo);
					}
					quota_areainfo.setQuota(Long.parseLong(value));
					g_area = quota_area;
				} catch (Exception e) {
					log.error(e.toString());
				}
			}
		} catch (Exception e) {
			log.error(e.toString());
		}

		// in case there is no capacity setting for the group
		if (g_area < 0)
			g_area = 0;

		try {
			stat = tm.getStat(TairConstant.Q_DATA_SEVER_INFO, groupname, 0);
		} catch (Exception e) {
			log.error(e.toString());
		}

		try {
			keys = stat.keySet();
			_nodecount = keys.size();
			for (String key : keys) {
				try {
					ServerStat foo = null;
					if (_server_stat.containsKey(key)) {
						foo = _server_stat.get(key);
					} else {
						foo = new ServerStat();
						_server_stat.put(key, foo);
					}

					String[] sec = key.split(":");
					sec[0] = sec[0].trim();
					foo.setIp(sec[0]);
					sec[1] = sec[1].trim();
					foo.setPort(Integer.parseInt(sec[1]));
					foo.setStat(stat.get(key));

					boolean setDelayOk = false;
					double delaySum = 0.;

					TairClient client = factory.get(key, timeout, timeout,
							streamer);
					if (null != client) {
						int i = 0;
						long beginTime = System.currentTimeMillis();
						for (i = 0; i < pingCount; ++i) {
							RequestPingPacket request = new RequestPingPacket(
									null);
							BasePacket response = (BasePacket) client.invoke(0,
									request, timeout);
							if (((ReturnPacket) response).getCode() != ResultCode.SUCCESS
									.getCode())
								break;
						}
						if (i == pingCount) {
							delaySum = System.currentTimeMillis() - beginTime;
							foo.setDelay(delaySum / pingCount);
							setDelayOk = true;
						}
					}

					if (!setDelayOk) {
						foo.setDelay(timeout);
					}

					Map<String, String> mystat = null;
					Set<String> mykeys = null;
					try {
						mystat = tm
								.getStat(
										TairConstant.Q_STAT_INFO,
										groupname,
										TairUtil.hostToLong(foo.getIp(),
												foo.getPort()));
					} catch (Exception e) {
						log.error(e.toString());
					}
					mykeys = mystat.keySet();
					for (String mykey : mykeys) {
						try {
							String myvalue = mystat.get(mykey);
							String[] myinfo = mykey.split(" ");
							myinfo[0] = myinfo[0].trim();
							int area = Integer.parseInt(myinfo[0]);
							AreaStatistics areainfo = null;
							if (foo.get_area_statistics().containsKey(area)) {
								areainfo = foo.get_area_statistics().get(area);
							} else {
								areainfo = new AreaStatistics();
								areainfo.setArea(area);
								foo.get_area_statistics().put(area, areainfo);
							}
							myinfo[1] = myinfo[1].trim();
							myvalue = myvalue.trim();
							Long tmpValue = null;
							try {
								tmpValue = Long.parseLong(myvalue);
							} catch (Exception e) {
								log.error("value of " + "column (" + myinfo[1]
										+ "), area (" + area + "), cluster("
										+ clusterId + ")" + " is invalid : "
										+ myvalue);
								tmpValue = 0L;
							}
							if (myinfo[1].toLowerCase().equals("datasize")) {
								areainfo.setDataSize(areainfo.getDataSize()
										+ tmpValue);
								continue;
							} else if (myinfo[1].toLowerCase().equals(
									"evictcount")) {
								areainfo.setEvictCount(areainfo.getEvictCount()
										+ tmpValue);
								continue;
							} else if (myinfo[1].toLowerCase().equals(
									"getcount")) {
								areainfo.setGetCount(areainfo.getGetCount()
										+ tmpValue);
								continue;
							} else if (myinfo[1].toLowerCase().equals(
									"hitcount")) {
								areainfo.setHitCount(areainfo.getHitCount()
										+ tmpValue);
								continue;
							} else if (myinfo[1].toLowerCase().equals(
									"itemcount")) {
								areainfo.setItemCount(areainfo.getItemCount()
										+ tmpValue);
								continue;
							} else if (myinfo[1].toLowerCase().equals(
									"putcount")) {
								areainfo.setPutCount(areainfo.getPutCount()
										+ tmpValue);
								continue;
							} else if (myinfo[1].toLowerCase().equals(
									"removecount")) {
								areainfo.setRemoveCount(areainfo
										.getRemoveCount() + tmpValue);
								continue;
							} else if (myinfo[1].toLowerCase()
									.equals("usesize")) {
								areainfo.setUseSize(areainfo.getUseSize()
										+ tmpValue);
								continue;
							}

							log.error("can not get suitable entity!");
						} catch (Exception e) {
							log.error(e.toString());
						}
					}
				} catch (Exception e) {
					log.error(e.toString());
				}
			}
		} catch (Exception e) {
			log.error(e.toString());
		}

		try {
			stat = tm.getStat(TairConstant.Q_STAT_INFO, groupname, 0);
		} catch (Exception e) {
			log.error(e.toString());
		}
		try {
			keys = stat.keySet();
			for (String key : keys) {
				try {
					String value = stat.get(key);
					String[] info = key.split(" ");
					info[0] = info[0].trim();
					int area = Integer.parseInt(info[0]);
					AreaStatistics areainfo = null;
					if (_aggregate_area_statistics.containsKey(area)) {
						areainfo = _aggregate_area_statistics.get(area);
					} else {
						areainfo = new AreaStatistics();
						_aggregate_area_statistics.put(area, areainfo);
					}
					info[1] = info[1].trim();
					value = value.trim();
					try {
						if (info[1].toLowerCase().equals("datasize")) {
							try {
								areainfo.setDataSize(Long.parseLong(value));
							} catch (Exception e) {
								areainfo.setDataSize(0);
							}
							continue;
						} else if (info[1].toLowerCase().equals("evictcount")) {
							areainfo.setEvictCount(Long.parseLong(value));
							continue;
						} else if (info[1].toLowerCase().equals("getcount")) {
							areainfo.setGetCount(Long.parseLong(value));
							continue;
						} else if (info[1].toLowerCase().equals("hitcount")) {
							areainfo.setHitCount(Long.parseLong(value));
							continue;
						} else if (info[1].toLowerCase().equals("itemcount")) {
							areainfo.setItemCount(Long.parseLong(value));
							continue;
						} else if (info[1].toLowerCase().equals("putcount")) {
							areainfo.setPutCount(Long.parseLong(value));
							continue;
						} else if (info[1].toLowerCase().equals("removecount")) {
							areainfo.setRemoveCount(Long.parseLong(value));
							continue;
						} else if (info[1].toLowerCase().equals("usesize")) {
							try {
								areainfo.setUseSize(Long.parseLong(value));
							} catch (Exception e) {
								areainfo.setUseSize(0);
							}
							continue;
						}
					} catch (Exception e) {
						log.error(e.toString());
					}
				} catch (Exception e) {
					log.error(e.toString());
				}
			}
		} catch (Exception e) {
			log.error(e.toString());
		}

		log.info(System.nanoTime() + " exchange statistics info!");
		try {
			mutex.acquire();
			server_stat = _server_stat;
			aggregate_area_statistics = _aggregate_area_statistics;
			nodecount = _nodecount;
			areacount = _areacount;
		} catch (InterruptedException e) {
			log.error(e.toString());
		}
		mutex.release();
	}

	public HashMap<String, ServerStat> get_server_stat() {
		HashMap<String, ServerStat> server_stat_tem = null;
		try {
			mutex.acquire();
			server_stat_tem = server_stat;
		} catch (InterruptedException e) {
			log.error(e.toString());
		}
		mutex.release();
		return server_stat_tem;
	}

	public HashMap<Integer, AreaStatistics> get_aggregate_area_statistics() {
		HashMap<Integer, AreaStatistics> aggregate_area_statistics_tem = null;
		try {
			mutex.acquire();
			aggregate_area_statistics_tem = aggregate_area_statistics;
		} catch (InterruptedException e) {
			log.error(e.toString());
		}
		mutex.release();
		return aggregate_area_statistics_tem;
	}

	public int getNodecount() {
		int nodecount_tem = -1;
		try {
			mutex.acquire();
			nodecount_tem = nodecount;
		} catch (InterruptedException e) {
			log.error(e.toString());
		}
		mutex.release();
		return nodecount_tem;
	}

	public int getAreacount() {
		int areacount_tem = -1;
		try {
			mutex.acquire();
			areacount_tem = areacount;
		} catch (InterruptedException e) {
			log.error(e.toString());
		}
		mutex.release();
		return areacount_tem;
	}

}
