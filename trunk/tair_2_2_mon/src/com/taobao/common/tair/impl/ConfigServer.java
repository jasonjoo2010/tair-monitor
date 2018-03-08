package com.taobao.common.tair.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.common.tair.CTDBMStat;
import com.taobao.common.tair.comm.ResponseListener;
import com.taobao.common.tair.comm.TairClient;
import com.taobao.common.tair.comm.TairClientFactory;
import com.taobao.common.tair.etc.TairClientException;
import com.taobao.common.tair.etc.TairConstant;
import com.taobao.common.tair.etc.TairServerDownException;
import com.taobao.common.tair.etc.TairUtil;
import com.taobao.common.tair.packet.BasePacket;
import com.taobao.common.tair.packet.PacketStreamer;
import com.taobao.common.tair.packet.RequestGetGroupPacket;
import com.taobao.common.tair.packet.RequestStatPacket;
import com.taobao.common.tair.packet.ResponseGetGroupPacket;
import com.taobao.common.tair.packet.ResponseStatPacket;

public class ConfigServer implements ResponseListener {
	private static final Log log = LogFactory.getLog(ConfigServer.class);
	protected static final int MURMURHASH_M = 0x5bd1e995;
	protected String groupName = null;
	protected int configVersion = 0;
	protected AtomicLong retrieveLastTime = new AtomicLong(0);
	protected int lastConfigServerIndex = 0;
	protected static int aliveConfigServerIndex = 0;
	protected static int invalidServerListIndex = 0;

	// config server 列表, 静态配置的
	protected List<String> configServerList = new ArrayList<String>();

	// 服务器列表, 从config server取的
	protected List<Long> serverList;
	protected Map<Long, Long> invalidServerMap = new HashMap<Long, Long>(2);
	protected PacketStreamer pstream;

	public ConfigServer(String groupName, List<String> configServerList, PacketStreamer pstream) {
		this.groupName = groupName;
		this.pstream = pstream;

		for (String host : configServerList)
			this.configServerList.add(host.trim());
	}

	/**
	 * 取此key对应的的机器地址
	 * 
	 * @param key
	 * 
	 * @return
	 */
	public long getServer(byte[] keyByte, boolean isRead) {
		long addr = 0;
		long hash = murMurHash(keyByte);
		log.debug("original hashcode " + hash + ", server size: "
				+ serverList.size());

		if ((serverList != null) && (serverList.size() > 0)) {
			hash %= serverList.size();

			if ((isRead == false)
					&& (hash >= TairConstant.TAIR_SERVER_BUCKET_COUNT)) {
				// 如果是写，总是使用第一列的机器
				hash %= TairConstant.TAIR_SERVER_BUCKET_COUNT;
			}

			addr = serverList.get((int) hash);
		}
		log.debug("target server id: " + addr);
		
		if(TairUtil.isServerUp(addr) == false)
			throw new TairServerDownException("server down: " + TairUtil.idToAddress(addr));

		return addr;
	}

	/**
	 * 获取失效服务器的地址
	 */
	public long getInvalidServer() {
		Long[] keys = (Long[]) invalidServerMap.keySet().toArray(
				new Long[invalidServerMap.size()]);

		if (keys.length > 0) {
			int index = invalidServerListIndex++ % keys.length;

			for (int i = 0; i < keys.length; i++) {
				long addr = keys[index];
				long t = invalidServerMap.get(addr);

				if ((t == 0) || (t < (System.currentTimeMillis() - 5000))) {
					return addr;
				}

				index = invalidServerListIndex++ % keys.length;
			}
		}

		return 0;
	}

	/**
	 * 设置失效服务器不可用
	 * 
	 * @param address
	 */
	public void setFailureInvalidServer(long address) {
		if (invalidServerMap.containsKey(address)) {
			invalidServerMap.put(address, System.currentTimeMillis());
		}
	}

	/**
	 * 从configserver取配置
	 * 
	 * @return
	 */
	public boolean retrieveConfigure() {
		retrieveLastTime.set(System.currentTimeMillis());

		RequestGetGroupPacket packet = new RequestGetGroupPacket(null);

		packet.setGroupName(groupName);
		packet.setConfigVersion(configVersion);

		lastConfigServerIndex = aliveConfigServerIndex;

		for (int i = 0; i < configServerList.size(); i++) {
			int index = lastConfigServerIndex % configServerList.size();
			String addr = configServerList.get(index);

			BasePacket returnPacket = null;
			try {
				TairClient client = TairClientFactory.getInstance().get(addr,
						TairConstant.DEFAULT_TIMEOUT, pstream);
				returnPacket = (BasePacket) client.invoke(packet,
						TairConstant.DEFAULT_TIMEOUT);
			} catch (Exception e) {
				log.error("get config failed", e);
			}

			if ((returnPacket != null)
					&& returnPacket instanceof ResponseGetGroupPacket) {
				ResponseGetGroupPacket r = (ResponseGetGroupPacket) returnPacket;

				configVersion = r.getConfigVersion();

				log.warn("configuration init with version: " + configVersion);

				if ((r.getServerList() != null)
						&& (r.getServerList().size() > 0)) {
					this.serverList = r.getServerList();
					aliveConfigServerIndex = lastConfigServerIndex;
					if (log.isDebugEnabled()) {
						for (int idx = 0; idx < r.getServerList().size(); idx++) {
							log.debug("+++ " + idx + " => "
									+ r.getServerList().get(idx));
						}
					}
					if ((this.serverList.size() % TairConstant.TAIR_SERVER_BUCKET_COUNT) != 0) {
						log
								.error("server size % bucket number != 0, server size: "
										+ this.serverList.size()
										+ ", bucket number"
										+ TairConstant.TAIR_SERVER_BUCKET_COUNT);
					}
				} else {
					log
							.warn("server list from config server is null or size is 0");
				}

				// 获取失效服务器的配置信息
				Map<String, String> cmap = r.getConfigMap();

				if ((cmap != null) && !cmap.isEmpty()) {
					String s = cmap.get(TairConstant.INVALUD_SERVERLIST_KEY);

					if ((s != null) && (s.length() > 0)) {
						String[] ss = s.split(",");

						for (String is : ss) {
							log.info("add invalid server: " + is);

							long l = TairUtil.hostToLong(is);

							if (l != 0) {
								invalidServerMap.put(l, 0L);
							}
						}
					}
				} else {
					log.warn("invalid server not exist");
				}

				break;
			} else {
				log.error("retrive from config server " + addr
						+ " failed, result: " + returnPacket);
			}

			lastConfigServerIndex++;
		}

		return true;
	}
	public List<Long> retrieveServerList(){
		List<Long> DSList = new LinkedList<Long>();
		for(long serverID : serverList){
			if(!DSList.contains(serverID))
				DSList.add(serverID);
		}
		return DSList;
	}
	
	public int getBucketCount() {
		return TairConstant.TAIR_SERVER_BUCKET_COUNT;
	}

//	public int getCopyCount() {
//		return copyCount;
//	}
	
	public List<Long> getServerList() {
		return serverList;
	}

	public Map<String, CTDBMStat> retrieveStat(int area){
		RequestStatPacket packet = new RequestStatPacket(null);
		packet.setConfigVersion(TairConstant.TDBM_STAT_AREA & 0xff);
		packet.setValue(area);
		List<Long> DSList = retrieveServerList();
		log.debug("there is "+DSList.size()+" servers");
		Map<String, CTDBMStat> realtime_stat = new HashMap<String, CTDBMStat>();
		for(long serverID : DSList){
			String addr = TairUtil.idToAddress(serverID);
			BasePacket returnPacket = null;
			try {
				TairClient client = TairClientFactory.getInstance().get(addr,
						TairConstant.DEFAULT_TIMEOUT, pstream);
				returnPacket = (BasePacket) client.invoke(packet,
						TairConstant.DEFAULT_TIMEOUT);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			if ((returnPacket != null) && returnPacket instanceof ResponseStatPacket){
				ResponseStatPacket r = (ResponseStatPacket) returnPacket;
				
				log.debug("buffer size = "  + r.getBuffer().length);
				
				CTDBMStat stat = r.getCTDBMStat();
				stat.show_stat();
				realtime_stat.put(addr, stat);
			}
		}
		
		return realtime_stat;
		
	}
	
	public List<Integer> retrieveAreaList(){
		RequestStatPacket packet = new RequestStatPacket(null);
		packet.setConfigVersion(TairConstant.TDBM_STAT_GET_MAXAREA & 0xff);
		packet.setValue(0);
		List<Long> DSList = new LinkedList<Long>();
		for(long serverID : serverList){
			if(!DSList.contains(serverID))
				DSList.add(serverID);
		}
		log.fatal("there is "+DSList.size()+" servers");
		
		LinkedList<Integer> areaList = new LinkedList<Integer>();
		
		for(long serverID : DSList){
			String addr = TairUtil.idToAddress(serverID);
			BasePacket returnPacket = null;
			try {
				TairClient client = TairClientFactory.getInstance().get(addr,
						TairConstant.DEFAULT_TIMEOUT, pstream);
				returnPacket = (BasePacket) client.invoke(packet,
						TairConstant.DEFAULT_TIMEOUT);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			if ((returnPacket != null) && returnPacket instanceof ResponseStatPacket){
				ResponseStatPacket r = (ResponseStatPacket) returnPacket;
				log.info("buffer size = "  + r.getBuffer().length);
				List<Integer> serverArea = r.getArea();
				for(int i=0;i<serverArea.size();i++){
					if(!areaList.contains( serverArea.get(i)) )
						areaList.add(serverArea.get(i));
				}
			}
		}
		
		return areaList;
		
	}
	
	/**
	 * 检查是否更新
	 */
	public void checkConfigVersion(int version) {
		log.info("check config version, local: " + configVersion + ", dataserver: " + version);
		if (version == configVersion) {
			return;
		}

		if (retrieveLastTime.get() > (System.currentTimeMillis() - 5000)) {
			return;
		}

		retrieveLastTime.set(System.currentTimeMillis());

		// 发送更新
		RequestGetGroupPacket packet = new RequestGetGroupPacket(null);

		packet.setGroupName(groupName);
		packet.setConfigVersion(configVersion);

		for (int i = 0; i < configServerList.size(); i++) {
			int index = lastConfigServerIndex % configServerList.size();

			String host = configServerList.get(index);
			try {
				TairClient client = TairClientFactory.getInstance().get(host, TairConstant.DEFAULT_TIMEOUT, pstream);
				client.invokeAsync(packet, TairConstant.DEFAULT_TIMEOUT, this);
			} catch (TairClientException e) {
				log.error("get client failed", e);
			}
			lastConfigServerIndex++;
		}
	}

	public void responseReceived(Object packet) {

		if ((packet != null) && packet instanceof ResponseGetGroupPacket) {
			ResponseGetGroupPacket r = (ResponseGetGroupPacket) packet;

			if (configVersion == r.getConfigVersion()) {
				return;
			}

			log.warn("configuration synced oldversion: " + configVersion
					+ ", new verion: " + r.getConfigVersion());

			configVersion = r.getConfigVersion();

			if ((r.getServerList() != null) && (r.getServerList().size() > 0)) {
				this.serverList = r.getServerList();
				if (log.isDebugEnabled()) {
					for (int idx = 0; idx < r.getServerList().size(); idx++) {
						log.debug("+++ " + idx + " => "
								+ r.getServerList().get(idx));
					}
				}
			}

			// 获取失效服务器的配置信息
			Map<String, String> cmap = r.getConfigMap();

			if ((cmap != null) && !cmap.isEmpty()) {
				String s = cmap.get(TairConstant.INVALUD_SERVERLIST_KEY);

				if ((s != null) && (s.length() > 0)) {
					Map<Long, Long> tis = new HashMap<Long, Long>(2);
					String[] ss = s.split(",");

					for (String is : ss) {
						log.info("*update* add invalid server: " + is);

						long l = TairUtil.hostToLong(is);

						if (l != 0) {
							tis.put(l, 0L);
						}
					}

					if (tis.size() > 0) {
						invalidServerMap = tis;
					}
				}
			}
		} else {
			lastConfigServerIndex++;
		}

	}
	
	public void forceCheckConfig() {
		this.configVersion = 0;
	}

	public void exceptionCaught(TairClientException exception) {
		log.error("do async request failed", exception);
		
	}

	/**
	 * hashcode
	 */
	protected long murMurHash(byte[] key) {
		int len = key.length;
		int h = 97 ^ len;
		int index = 0;

		while (len >= 4) {
			int k = (key[index] & 0xff) | ((key[index + 1] << 8) & 0xff00)
					| ((key[index + 2] << 16) & 0xff0000)
					| (key[index + 3] << 24);

			k *= MURMURHASH_M;
			k ^= (k >>> 24);
			k *= MURMURHASH_M;
			h *= MURMURHASH_M;
			h ^= k;
			index += 4;
			len -= 4;
		}

		switch (len) {
		case 3:
			h ^= (key[index + 2] << 16);

		case 2:
			h ^= (key[index + 1] << 8);

		case 1:
			h ^= key[index];
			h *= MURMURHASH_M;
		}

		h ^= (h >>> 13);
		h *= MURMURHASH_M;
		h ^= (h >>> 15);
		return ((long) h & 0xffffffffL);
	}
	
}
