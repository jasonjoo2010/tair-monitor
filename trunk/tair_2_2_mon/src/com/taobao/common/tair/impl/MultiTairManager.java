/**
 * 
 */
package com.taobao.common.tair.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.common.tair.DataEntry;
import com.taobao.common.tair.Result;
import com.taobao.common.tair.ResultCode;
import com.taobao.common.tair.TairManager;
import com.taobao.common.tair.etc.TairConstant;

/**
 * @author ruohai
 * 
 */
public class MultiTairManager implements TairManager {
	private static final Log log = LogFactory.getLog(MultiTairManager.class);
	private List<List<String>> configServerList = null;
	private String groupName = null;
	private List<TairManager> tairManagerList = new ArrayList<TairManager>();
	private int timeout = TairConstant.DEFAULT_TIMEOUT;
	private static int tairManagerIndex = 0;

	private int lpMax = 100;
	private int localPercents = -1; // 默认不切流量
	private Random lpRandom = new Random();

	private int proxyLevel = 1; // 1: 走本地，如果失败走异地；2：读全部走异地

	public void init() {
		if (configServerList == null || configServerList.size() == 0) {
			log.error("configServerList is empty");
			return;
		}

		for (List<String> configs : configServerList) {
			DefaultTairManager tm = new DefaultTairManager();
			tm.setConfigServerList(configs);
			tm.setGroupName(groupName);
			tm.setTimeout(timeout);
			tm.doInit();
			tairManagerList.add(tm);
		}
		log.warn(getVersion() + " inited, size: " + tairManagerList.size());
	}

	public Result<Integer> decr(int namespace, Object key, int value,
			int defaultValue) {
		try {
			return tairManagerList.get(0).decr(namespace, key, value,
					defaultValue);
		} catch (Exception e) {
			if (log.isDebugEnabled())
				log.debug("", e);
			return new Result<Integer>(ResultCode.CONNERROR);
		}
	}

	public ResultCode delete(int namespace, Object key) {
		ResultCode rc = ResultCode.SUCCESS;
		for (TairManager tairManager : tairManagerList) {
			try {
				ResultCode crc = tairManager.delete(namespace, key);
				if (crc.isSuccess() == false)
					rc = crc; // 有一个失败就返回失败，但是继续尝试其他集群
			} catch (Exception e) {
				if (log.isDebugEnabled())
					log.debug("", e);
				continue; // 如果对应的机器不可用了，那么忽略，这台机器恢复的时候应该保证数据的空的
			}
		}
		return rc;
	}

	public Result<DataEntry> get(int namespace, Object key) {
		if (proxyLevel != 3) {
			try {
				if (proxyLevel == 2 && localPercents > 0 && (lpRandom.nextInt() % lpMax) < localPercents) {
					// 返回一部分数据不存在，使得可以逐步热本地服务器
					return new Result<DataEntry>(ResultCode.DATANOTEXSITS);
				} else if (proxyLevel == 1) {				
					return tairManagerList.get(0).get(namespace, key);
				}
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug("", e);				
				}
			}
		}
		TairManager tm = getTairManager();
		return tm.get(namespace, key);
	}

	public Result<Integer> incr(int namespace, Object key, int value,
			int defaultValue) {
		try {
			return tairManagerList.get(0).incr(namespace, key, value,
					defaultValue);
		} catch (Exception e) {
			if (log.isDebugEnabled())
				log.debug("", e);
			return new Result<Integer>(ResultCode.CONNERROR);
		}
	}

	public ResultCode invalid(int namespace, Object key) {
		return delete(namespace, key);
	}

	public ResultCode mdelete(int namespace, List<Object> keys) {
		ResultCode rc = ResultCode.SUCCESS;
		for (TairManager tairManager : tairManagerList) {
			try {
				ResultCode crc = tairManager.mdelete(namespace, keys);
				if (crc.isSuccess() == false)
					rc = crc; // 有一个失败就返回失败，但是继续尝试其他集群
			} catch (Exception e) {
				if (log.isDebugEnabled())
					log.debug("", e);
				continue; // 如果对应的机器不可用了，那么忽略，这台机器恢复的时候应该保证数据的空的
			}
		}
		return rc;
	}

	public Result<List<DataEntry>> mget(int namespace, List<Object> keys) {
		if (proxyLevel != 3) {
			try {
				if (proxyLevel == 2 && localPercents > 0 && (lpRandom.nextInt() % lpMax) < localPercents) {
					// 返回一部分数据不存在，使得可以逐步热本地服务器
					return new Result<List<DataEntry>>(ResultCode.DATANOTEXSITS);
				} else if (proxyLevel == 1) {
					return tairManagerList.get(0).mget(namespace, keys);
				}
			} catch (Exception e) {
				if (log.isDebugEnabled())
					log.debug("", e);
			}
		}

		TairManager tm = getTairManager();
		return tm.mget(namespace, keys);

	}

	@SuppressWarnings("unchecked")
	public ResultCode minvalid(int namespace, List<? extends Object> keys) {
		return mdelete(namespace, (List<Object>) keys);
	}

	public ResultCode put(int namespace, Object key, Serializable value) {
		return put(namespace, key, value, 0);
	}

	public ResultCode put(int namespace, Object key, Serializable value,
			int version) {
		return put(namespace, key, value, version, 0);
	}

	public ResultCode put(int namespace, Object key, Serializable value,
			int version, int expireTime) {
		try {
			return tairManagerList.get(0).put(namespace, key, value, version,
					expireTime);
		} catch (Exception e) {
			if (log.isDebugEnabled())
				log.debug("", e);
			return getTairManager().put(namespace, key, value, version,
					expireTime);
		}
	}

	private TairManager getTairManager() {
		if (tairManagerList.size() == 2)
			return tairManagerList.get(1);

		int index = tairManagerIndex++ % tairManagerList.size();
		if (index == 0)
			index = 1;
		if (tairManagerIndex > Integer.MAX_VALUE)
			tairManagerIndex = 1;

		return tairManagerList.get(index);
	}

	public String getVersion() {
		return "MultiTairManager version 2.2.3";
	}

	public void setConfigServerList(List<List<String>> configServerList) {
		this.configServerList = configServerList;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void reset(int newLevel) {
		if (newLevel < 1 || newLevel > 3) {
			log
					.error("new reset level invalid, should in [1, 3], you provide: "
							+ newLevel);
			log.error("1 means everything is ok");
			log.error("2 means crash server is up, but can not fully service now");
			log.error("3 means all read request go remote instance");
			return;
		}

		if (newLevel != 3) {
			try {
				MultiConfigServer cfg = (MultiConfigServer) ((DefaultTairManager) tairManagerList.get(0)).configServer;
				cfg.reset();
				cfg.forceCheckConfig();
			} catch (Exception e) {
				log.error("cast error ", e);
			}

		}

		this.proxyLevel = newLevel;
		log.warn("current proxy level: " + this.proxyLevel);
	}

	public int getCurrentLevel() {
		return proxyLevel;
	}

	public int getLocalPercents() {
		return localPercents;
	}

	public void setLocalPercents(int localPercents) {
		this.localPercents = (localPercents % lpMax);
	}

}
