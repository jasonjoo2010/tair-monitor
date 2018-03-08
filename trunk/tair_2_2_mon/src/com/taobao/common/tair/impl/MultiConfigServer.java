/**
 * 
 */
package com.taobao.common.tair.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.common.tair.comm.TairClient;
import com.taobao.common.tair.comm.TairClientFactory;
import com.taobao.common.tair.etc.TairConstant;
import com.taobao.common.tair.etc.TairUtil;
import com.taobao.common.tair.packet.BasePacket;
import com.taobao.common.tair.packet.PacketStreamer;
import com.taobao.common.tair.packet.RequestGetGroupPacket;
import com.taobao.common.tair.packet.ResponseGetGroupPacket;

/**
 * @author ruohai
 * 
 */
public class MultiConfigServer extends ConfigServer {
	private static final Log log = LogFactory.getLog(MultiConfigServer.class);
	public boolean hasServerDown = false;

	public MultiConfigServer(String groupName, List<String> configServerList,
			PacketStreamer pstream) {
		super(groupName, configServerList, pstream);
	}
	
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
				log.error("get config from " + addr +  " failed", e);
				continue;
			}

			if ((returnPacket != null)
					&& returnPacket instanceof ResponseGetGroupPacket) {
				ResponseGetGroupPacket r = (ResponseGetGroupPacket) returnPacket;

				configVersion = r.getConfigVersion();

				log.warn("configuration init with version: " + configVersion);

				Map<String, String> cmap = r.getConfigMap();
				String nodeList = null;
				if (cmap == null || cmap.size() == 0) {
					continue;
				}
				
				nodeList = cmap.get(TairConstant.INSTANCE_NODE_LIST);
				
				if (nodeList == null || nodeList.length() < 12) {
					log.error("location config is null, check " + TairConstant.INSTANCE_LOCATION_NAME + " from configserver's group.conf");
					continue;
				}
				
				if (initServerList(nodeList) == false) {
					log.error("init server list failed");
					continue;
				}
				
				if ((r.getServerList() != null)
						&& (r.getServerList().size() > 0)) {
					
					aliveConfigServerIndex = lastConfigServerIndex;
					if (log.isDebugEnabled()) {
						for (int idx = 0; idx < serverList.size(); idx++) {
							log.debug("+++ " + idx + " => "
									+ TairUtil.idToAddress(serverList.get(idx)) + " " + TairUtil.isServerUp(serverList.get(idx)));
						}
					}					
					
					Set<Long> avas = new HashSet<Long>();
					avas.addAll(r.getServerList());
					for(int idx=0; idx<serverList.size(); idx++) {
						long sid = serverList.get(idx);
						if (avas.contains(sid) == false) {
							serverList.set(idx, TairUtil.setServerDown(sid));
							hasServerDown = true;
							log.warn("server failed: " + TairUtil.idToAddress(sid));
						}
					}
				} else {
					log.warn("server list from config server" + addr + " is null or size is 0");
				}

			} else {
				log.error("retrive from config server " + addr
						+ " failed, result: " + returnPacket);
			}
			
			lastConfigServerIndex++;
		}

		return false;
	}

	private boolean initServerList(String nodeList) {
		boolean result = false;
		String[] nodes = nodeList.split(",");
		if (nodes != null && nodes.length > 0) {
			// 这个方法只会
			serverList = new ArrayList<Long>();
			for (String server : nodes) {
				long sid = TairUtil.hostToLong(server);
				if (sid > 0)
					serverList.add(sid);
			}
			log.warn("tair server list inited, size: " + serverList.size());
			if (serverList.size() > 0)
				result = true;
		}
		
		return result;
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
				Set<Long> avaliableServerIds = new HashSet<Long>();
				avaliableServerIds.addAll(r.getServerList());
				
				for (int i = 0; i < serverList.size(); i++) {
					long id = TairUtil.setServerUp(serverList.get(i));
					if (avaliableServerIds.contains(id)) {
						if (hasServerDown == false) // 这里很可能是人工reset过
							serverList.set(i, TairUtil.setServerUp(id));
						else
							log.info("server " + TairUtil.idToAddress(id) + " is up, but not reset, drop it");
					} else {
						// 有服务器不可用了
						serverList.set(i, TairUtil.setServerDown(id));
						hasServerDown = true;
					}
				}
				if (log.isDebugEnabled()) {
					for (int idx = 0; idx < serverList.size(); idx++) {
						log.debug("+++ " + idx + " => "
								+ TairUtil.idToAddress(serverList.get(idx)) + " " + TairUtil.isServerUp(serverList.get(idx)));
					}
				}
			}

		} else {
			lastConfigServerIndex++;
		}

	}
	
	public void reset() {
		hasServerDown = false;
		log.warn("hasServerDown has been reseted");
	}
}
