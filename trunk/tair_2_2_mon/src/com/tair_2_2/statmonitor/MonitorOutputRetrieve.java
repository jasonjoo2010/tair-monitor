package com.tair_2_2.statmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.Semaphore;
import java.util.regex.MatchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.common.tair.CTDBMStat;
import com.taobao.common.tair.ServerDelay;
import com.taobao.common.tair.etc.TairConstant;
import com.taobao.common.tair.etc.TairUtil;
import com.taobao.common.tair.impl.DefaultTairManager;

public class MonitorOutputRetrieve extends java.util.TimerTask {

	private static final Log log = LogFactory
			.getLog(MonitorOutputRetrieve.class);
	
	public DefaultTairManager tm;
	private Semaphore mutex;

	private String configserverA;
	private String configserverB;
	private String groupname;
	private String arealist;
	long interval;
	private List<Integer> areas;
	
	private int nodecount, areacount;
	HashMap<String, ServerStat> server_stat;
	HashMap<Integer, AreaStatistics> aggregate_area_statistics;
	HashMap<String, ServerStat> ori_server_stat;
	HashMap<Integer, AreaStatistics> ori_aggregate_area_statistics;
	
	public MonitorOutputRetrieve(long interval) throws ClassNotFoundException, IOException {
		mutex = new Semaphore(1);
		this.interval = interval;
		try {
			InputStream RA = Class.forName(
					"com.tair_2_2.statmonitor.MonitorOutputRetrieve")
					.getResourceAsStream("MonitorArgs");
			Properties config = new Properties();
			config.load(RA);
			configserverA = config.getProperty("configserverA");
			configserverB = config.getProperty("configserverB");
			groupname = config.getProperty("groupname");
			RA.close();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		server_stat = new HashMap<String, ServerStat>();
		aggregate_area_statistics = new HashMap<Integer, AreaStatistics>();
		ori_server_stat = new HashMap<String, ServerStat>();
		ori_aggregate_area_statistics = new HashMap<Integer, AreaStatistics>();
		init();
	}
	
	private void init(){
	
		try {
			tm = new DefaultTairManager();
			
			List<String> cs = new ArrayList<String>();
			if (configserverA != null) {
				cs.add(configserverA);
			}
			if (configserverB != null) {
				cs.add(configserverB);
			}
			tm.setConfigServerList(cs);
			tm.setGroupName(groupname);
			
			tm.init();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void run() {

		HashMap<String, ServerStat> _server_stat = new HashMap<String, ServerStat>();
		HashMap<Integer, AreaStatistics> _aggregate_area_statistics = new HashMap<Integer, AreaStatistics>();
		int _nodecount = -1, _areacount = -1;
		
		Map<String, CTDBMStat> stat = null; 
		Set<String> keys  =null ;
		
		areas = tm.getAreaList();
		try {
			log.info("Retrieve Info From " + configserverA +" GroupName = " +groupname);
			for(int i = 0 ; i<areas.size() ;i++)
			{
				int thisarea = areas.get(i);
				try {																																																																																																																																																																																																																																																																								
					stat = tm.getStat(thisarea);
					if(stat.size()==0) continue;
				} catch (Exception e) {
					log.error(e.getMessage());
				}

				Set<String> serverids = stat.keySet();
				for(String serverid : serverids){
					CTDBMStat this_stat = stat.get(serverid);
					
					ServerStat serverstat = null;
					if(_server_stat.containsKey(serverid)){
						serverstat = _server_stat.get(serverid);
					}else {
						serverstat = new ServerStat();
						_server_stat.put(serverid,serverstat);
					}
					
					if(ServerDelay.delay.containsKey(serverid.trim())){
						serverstat.setDelay(ServerDelay.delay.get(serverid.trim()));
					}
					
					serverstat.setStat("alive");
					String[] sep = serverid.split(":");
					serverstat.setIp(sep[0]);
					serverstat.setPort(Integer.parseInt(sep[1]));
					AreaStatistics areainfo = null;
					if(serverstat._area_statistics.containsKey(thisarea)){
						areainfo =serverstat._area_statistics.get(thisarea);
					}else{
						areainfo = new AreaStatistics();
						areainfo.setArea(thisarea);
						serverstat._area_statistics.put(thisarea, areainfo);
					}
					areainfo.addup(this_stat);

					areainfo = null;
					if (_aggregate_area_statistics.containsKey(thisarea)){
						areainfo =_aggregate_area_statistics.get(thisarea);
					}else{
						areainfo = new  AreaStatistics();
						areainfo.setArea(thisarea);
						_aggregate_area_statistics.put(thisarea, areainfo);
					}
					areainfo.addup(this_stat);
				}
				
				_nodecount = _server_stat.size();
				_areacount = _aggregate_area_statistics.size();
			}
		} catch (NumberFormatException e) {
				log.error(e.getMessage());
		}
		try {
			mutex.acquire();
			log.info("transmit data");
			//server_stat = _server_stat;
			//aggregate_area_statistics = _aggregate_area_statistics;
			
			for(String tem : server_stat.keySet()) {
				server_stat.get(tem).setStat("dead");
			}
			for(String tem : _server_stat.keySet()) {
				if(ori_server_stat.containsKey(tem)){
					ServerStat A = ori_server_stat.get(tem);
					A.setStat("alive");
					ServerStat B = _server_stat.get(tem);
					ServerStat foobar = server_stat.get(tem);
					
						for(Integer i : foobar.get_area_statistics().keySet()){
							foobar.get_area_statistics().get(i).setArea(-2);
						}
						for(Integer i : B.get_area_statistics().keySet()){
							if(A.get_area_statistics().containsKey(i)){
								AreaStatistics foo = A.get_area_statistics().get(i);
								foo.sub(B.get_area_statistics().get(i));
								foo.average(interval);
								foo.setArea(i);
								foobar.get_area_statistics().put(i, foo);
							} else {
								foobar.get_area_statistics().put(i, B.get_area_statistics().get(i));
							}
						}
					
					server_stat.put(tem, A);
				}else{
					server_stat.put(tem, _server_stat.get(tem));
				}
			}
			ori_server_stat = _server_stat;
			
			//合并Area时，这里有个问题 area能删除吗?没有考虑这个情况，粗糙的实现一下,保留所有出现过的area
			for(Integer i : aggregate_area_statistics.keySet()){
				aggregate_area_statistics.get(i).setArea(-2);
			}
			for(Integer i : _aggregate_area_statistics.keySet()){
				if(ori_aggregate_area_statistics.containsKey(i)){
					AreaStatistics foo = ori_aggregate_area_statistics.get(i);
					foo.sub(_aggregate_area_statistics.get(i));
					foo.average(interval);
					foo.setArea(i);
					aggregate_area_statistics.put(i, foo);
				} else {
					aggregate_area_statistics.put(i, _aggregate_area_statistics.get(i));
				}
			}
			ori_aggregate_area_statistics = _aggregate_area_statistics ;

			nodecount = _nodecount;
			areacount = _areacount;
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		mutex.release();
	}
	
	public HashMap<String, ServerStat> get_server_stat() {
		HashMap<String, ServerStat> server_stat_tem = null;
		try {
			mutex.acquire();
			server_stat_tem = server_stat;
		} catch (InterruptedException e) {
			log.error(e.getMessage());
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
			log.error(e.getMessage());
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
			log.error(e.getMessage());
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
			log.error(e.getMessage());
		}
		mutex.release();
		return areacount_tem;
	}

	

}
