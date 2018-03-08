package com.taobao.common.tair.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.common.tair.CTDBMStat;
import com.taobao.common.tair.DataEntry;
import com.taobao.common.tair.Result;
import com.taobao.common.tair.ResultCode;
import com.taobao.common.tair.TairManager;
import com.taobao.common.tair.comm.DefaultTranscoder;
import com.taobao.common.tair.comm.MultiSender;
import com.taobao.common.tair.comm.TairClient;
import com.taobao.common.tair.comm.TairClientFactory;
import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairClientException;
import com.taobao.common.tair.etc.TairConstant;
import com.taobao.common.tair.etc.TairUtil;
import com.taobao.common.tair.packet.BasePacket;
import com.taobao.common.tair.packet.RequestCommandCollection;
import com.taobao.common.tair.packet.RequestGetPacket;
import com.taobao.common.tair.packet.RequestIncDecPacket;
import com.taobao.common.tair.packet.RequestInvalidPacket;
import com.taobao.common.tair.packet.RequestPutPacket;
import com.taobao.common.tair.packet.RequestRemovePacket;
import com.taobao.common.tair.packet.ResponseGetPacket;
import com.taobao.common.tair.packet.ResponseIncDecPacket;
import com.taobao.common.tair.packet.ReturnPacket;
import com.taobao.common.tair.packet.TairPacketStreamer;
import com.taobao.monitor.MonitorLog;

/**
 * 
 * @author duolong
 */
public class DefaultTairManager implements TairManager {
    private static final Log        log                  = LogFactory.getLog(DefaultTairManager.class);
    private static final String     clientVersion        = "TairClient 2.2.3";
    private List<String>            configServerList     = null;
    private String                  groupName            = null;
    public ConfigServer             configServer         = null;
    private MultiSender             multiSender          = null;
    private int                     timeout              = TairConstant.DEFAULT_TIMEOUT;
    private int                     maxWaitThread        = TairConstant.DEFAULT_WAIT_THREAD;
    private TairPacketStreamer      packetStreamer       = null;
    private Transcoder              transcoder           = null;
    private int                     compressionThreshold = 0;
    private String                  charset              = null;
    private String                  name                 = null;
    private AtomicInteger           failCounter          = new AtomicInteger(0);
    /*
     * 用于统计峰值
     */
    private AtomicInteger counter = new AtomicInteger(0); 
    private long interval = 1 * 60 * 1000;//间隔一分钟打印
    public DefaultTairManager() {
        this("DefaultTairManager");
        new Check("DefaultTairManager").start();
    }

    public DefaultTairManager(String name) {
        this.name = name;
        new Check(name).start();
    }
    private class Check extends Thread{
    	private String name;
    	public Check(String name ){
    		this.name = name;
    	}
    	public void run(){
    		try{
    			while(true){
    				Thread.sleep(interval);
    				int count = counter.getAndAdd(-counter.get());
    				log.error("---" + name + "one minitue count:" + count + " freq:" + (count / interval));
    			}
    		}catch(Exception e){
    			log.error(e,e);
    		}
    	}
    }
    /**
     * 初始化
     */
    public void init() {    
		transcoder = new DefaultTranscoder(compressionThreshold, charset);
		packetStreamer = new TairPacketStreamer(transcoder);
		configServer = new ConfigServer(groupName, configServerList, packetStreamer);
		configServer.retrieveConfigure();
		multiSender = new MultiSender(packetStreamer);
		log.warn(name + " [" + getVersion() + "] started...");
    }
    
    /**
     * 供MultiTairManager调用，MultiConfigServer会根据configserver推过来的配置初始化本地的serverList
     */
    public void doInit() {
    	transcoder = new DefaultTranscoder(compressionThreshold, charset);
		packetStreamer = new TairPacketStreamer(transcoder);
		configServer = new MultiConfigServer(groupName, configServerList, packetStreamer);
		configServer.retrieveConfigure();
		multiSender = new MultiSender(packetStreamer);
    }

    /**
     * destroy
     */
    public void destroy() {
	}

    /**
     * 得到一机器
     *
     * @return
     */
    private TairClient getClient(Object key, boolean isRead) {
        long address = configServer.getServer(transcoder.encode(key), isRead);

        String host = TairUtil.idToAddress(address);
        if (host != null) {
            try {
				return TairClientFactory.getInstance().get(host, timeout, packetStreamer);
			} catch (TairClientException e) {
				log.error("getClient failed ", e);
			}            
        }

        return null;
    }

    private BasePacket sendRequest(Object key, BasePacket packet) {
        return sendRequest(key, packet, false);
    }

    /**
     * 同步发送
     */
    private BasePacket sendRequest(Object key, BasePacket packet, boolean isRead) {
        TairClient client = getClient(key, isRead);

        if (client == null) {
            int value = failCounter.incrementAndGet();

            if (value > 100) {
                configServer.checkConfigVersion(0);
                failCounter.set(0);
                log.warn("connection failed happened 100 times, sync configuration");
            }

            log.warn("conn is null ");
            return null;
        }

        long       startTime    = System.currentTimeMillis();
        BasePacket returnPacket = null;
        try {
        	counter.incrementAndGet();
			returnPacket = (BasePacket) client.invoke(packet, timeout);
		} catch (TairClientException e) {
			log.error("send request failed" + e);
		}
        long       endTime      = System.currentTimeMillis();

        if (returnPacket == null) {
//            log.warn("key=" + key + ", timeout: " + timeout + ", used: " + (endTime - startTime)
//                     + " (ms), happened: " + failCounter.get() + ", address: " + conn);

            if (failCounter.incrementAndGet() > 100) {
                configServer.checkConfigVersion(0);
                failCounter.set(0);
                log.warn("connection failed happened 100 times, sync configuration");
            }

            return null;
        } else {
            if (log.isInfoEnabled()) {
                log.info("key=" + key + ", timeout: " + timeout + ", used: "
                          + (endTime - startTime) + " (ms), client: " + client);
            }
        }
        
        return returnPacket;
    }

    public Result<Integer> decr(int namespace, Object key, int value, int defaultValue) {
        return incr(namespace, key, -value, defaultValue);
    }

    public ResultCode delete(int namespace, Object key) {
        if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
            return ResultCode.NSERROR;
        }
        
        long s = System.currentTimeMillis();

        RequestRemovePacket packet = new RequestRemovePacket(transcoder);

        packet.setNamespace((short) namespace);
        packet.addKey(key);

        int ec = packet.encode();

        if (ec == 1) {
            return ResultCode.KEYTOLARGE;
        }

        ResultCode rc           = ResultCode.CONNERROR;
        BasePacket returnPacket = sendRequest(key, packet);

        if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
            if (((ReturnPacket) returnPacket).getCode() == 0) {
                rc = ResultCode.SUCCESS;
            } else {
                rc = ResultCode.SERVERERROR;
            }
        } else {
        	MonitorLog.addStat(clientVersion, "delete exception", null);
        }
        
        long e = System.currentTimeMillis();
        
        MonitorLog.addStat(clientVersion, "delete", null, (e-s), 1);

        return rc;
    }

    public ResultCode invalid(int namespace, Object key) {
        if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
            return ResultCode.NSERROR;
        }
        
        long s = System.currentTimeMillis();

        RequestInvalidPacket packet = new RequestInvalidPacket(transcoder, getGroupName());

        packet.setNamespace((short) namespace);
        packet.addKey(key);

        int ec = packet.encode();

        if (ec == 1) {
            return ResultCode.KEYTOLARGE;
        }

        BasePacket returnPacket = null;
        ResultCode rc           = ResultCode.CONNERROR;
        int        retry        = 3;

        while (retry-- > 0) {
            long address = configServer.getInvalidServer();

            if (address == 0) {
                return rc;
            }

            String host = TairUtil.idToAddress(address);
			TairClient client = null;
			try {
				client = TairClientFactory.getInstance().get(host, timeout, packetStreamer);
				if (client != null) {
					returnPacket = (BasePacket) client.invoke(packet, timeout);
				}
			} catch (Exception e) {
				log.error("send request failed", e);
			}

            if (returnPacket == null) {
                configServer.setFailureInvalidServer(address);
                continue;
            }

            if (returnPacket instanceof ReturnPacket) {
                if (((ReturnPacket) returnPacket).getCode() == 0) {
                    rc = ResultCode.SUCCESS;
                } else {
                    rc = ResultCode.SERVERERROR;
                }
            }

            break;
        }
        
        if(!rc.isSuccess()) {
        	MonitorLog.addStat(clientVersion, "invalid exception", null);
        }
        
        long e = System.currentTimeMillis();
        MonitorLog.addStat(clientVersion, "invalid", null, (e-s), 1);

        return rc;
    }
    
	public ResultCode minvalid(int namespace, List<? extends Object> keys) {
	    if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
            return ResultCode.NSERROR;
        }
        
        long s = System.currentTimeMillis();

        RequestInvalidPacket packet = new RequestInvalidPacket(transcoder, getGroupName());

        packet.setNamespace((short) namespace);
        for (Object key : keys) {
			packet.addKey(key);
		}
       
        int ec = packet.encode();

        if (ec == 1) {
            return ResultCode.KEYTOLARGE;
        }

        BasePacket returnPacket = null;
        ResultCode rc           = ResultCode.CONNERROR;
        int        retry        = 3;

        while (retry-- > 0) {
            long address = configServer.getInvalidServer();

            if (address == 0) {
                return rc;
            }

            String host = TairUtil.idToAddress(address);
			TairClient client = null;
			try {
				client = TairClientFactory.getInstance().get(host, timeout, packetStreamer);
				if (client != null) {
					returnPacket = (BasePacket) client.invoke(packet, timeout);
				}
			} catch (Exception e) {
				log.error("send request failed", e);
			}

            if (returnPacket == null) {
                configServer.setFailureInvalidServer(address);
                continue;
            }

            if (returnPacket instanceof ReturnPacket) {
                if (((ReturnPacket) returnPacket).getCode() == 0) {
                    rc = ResultCode.SUCCESS;
                } else {
                    rc = ResultCode.SERVERERROR;
                }
            }

            break;
        }
        
        if(!rc.isSuccess()) {
        	MonitorLog.addStat(clientVersion, "invalid exception", null);
        }
        
        long e = System.currentTimeMillis();
        MonitorLog.addStat(clientVersion, "invalid", null, (e-s), 1);

        return rc;
	}

	public Result<DataEntry> get(int namespace, Object key) {
        if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
            return new Result<DataEntry>(ResultCode.NSERROR);
        }

        long s = System.currentTimeMillis();
        RequestGetPacket packet = new RequestGetPacket(transcoder);

        packet.setNamespace((short) namespace);
        packet.addKey(key);

        int ec = packet.encode();

        if (ec == 1) {
            return new Result<DataEntry>(ResultCode.KEYTOLARGE);
        }

        ResultCode rc           = ResultCode.CONNERROR;       
        BasePacket returnPacket = sendRequest(key, packet, true);

        if ((returnPacket != null) && returnPacket instanceof ResponseGetPacket) {
            ResponseGetPacket r = (ResponseGetPacket) returnPacket;

            DataEntry         resultObject = null;

            List<DataEntry> entryList = r.getEntryList();

            if (entryList.size() > 0) {
                resultObject = entryList.get(0);
                rc           = ResultCode.SUCCESS;
            } else {
                rc = ResultCode.DATANOTEXSITS;
            }

            // 检查configserver配置
            configServer.checkConfigVersion(r.getConfigVersion());

            return new Result<DataEntry>(rc, resultObject);
        } else {
        	MonitorLog.addStat(clientVersion, "get exception", null);
        }

        long e = System.currentTimeMillis();
        MonitorLog.addStat(clientVersion, "get", null, (e-s), 1);
        return new Result<DataEntry>(rc);
    }

    public String getVersion() {
        return clientVersion;
    }

    public Result<Integer> incr(int namespace, Object key, int value, int defaultValue) {
        if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
            return new Result<Integer>(ResultCode.NSERROR);
        }
        long s = System.currentTimeMillis();

        RequestIncDecPacket packet = new RequestIncDecPacket(transcoder);

        packet.setNamespace((short) namespace);
        packet.setKey(key);
        packet.setCount(value);
        packet.setInitValue(defaultValue);

        int ec = packet.encode();

        if (ec == 1) {
            return new Result<Integer>(ResultCode.KEYTOLARGE);
        }

        ResultCode rc           = ResultCode.CONNERROR;
        BasePacket returnPacket = sendRequest(key, packet);

        if ((returnPacket != null) && returnPacket instanceof ResponseIncDecPacket) {
            ResponseIncDecPacket r = (ResponseIncDecPacket) returnPacket;

            rc = ResultCode.SUCCESS;

            // 检查configserver配置
            configServer.checkConfigVersion(r.getConfigVersion());

            return new Result<Integer>(rc, r.getValue());
        } else {
        	MonitorLog.addStat(clientVersion, "counter exception", null);
        }
        
        long e = System.currentTimeMillis();
        MonitorLog.addStat(clientVersion, "counter", null, (e-s), 1);

        return new Result<Integer>(rc);
    }

    public ResultCode mdelete(int namespace, List<Object> keys) {
        if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
            return ResultCode.NSERROR;
        }
        
        long s = System.currentTimeMillis();

        RequestCommandCollection rcc = new RequestCommandCollection();

        for (Object key : keys) {
            long address = configServer.getServer(transcoder.encode(key), false);

            if (address == 0) {
                continue;
            }

            RequestRemovePacket packet = (RequestRemovePacket) rcc.findRequest(address);

            if (packet == null) {
                packet = new RequestRemovePacket(transcoder);
                packet.setNamespace((short) namespace);
                packet.addKey(key);
                rcc.addRequest(address, packet);
            } else {
                packet.addKey(key);
            }
        }

        int reqSize = 0;

        for (BasePacket p : rcc.getRequestCommandMap().values()) {
            RequestGetPacket rp = (RequestGetPacket) p;

            // calculate uniq key number
            reqSize += rp.getKeyList().size();

            //  check key size
            int ec = rp.encode();

            if (ec == 1) {
                log.error("key too larget: ");
                return ResultCode.KEYTOLARGE;
            }
        }

        ResultCode rc  = ResultCode.CONNERROR;
        boolean    ret = multiSender.sendRequest(rcc, timeout);

        if (ret) {
            int maxConfigVersion = 0;

            rc = ResultCode.SUCCESS;

            for (BasePacket rp : rcc.getResultList()) {
                if (rp instanceof ReturnPacket) {
                    ReturnPacket returnPacket = (ReturnPacket) rp;

                    if (returnPacket.getConfigVersion() > maxConfigVersion) {
                        maxConfigVersion = returnPacket.getConfigVersion();
                    }

                    if (returnPacket.getCode() != 0) {
                        rc = ResultCode.PARTSUCC;
                    }
                }
            }

            // 检查configserver配置
            configServer.checkConfigVersion(maxConfigVersion);
        } else {
        	MonitorLog.addStat(clientVersion, "mdelete exception", null);
        }
        
        long e = System.currentTimeMillis();
        MonitorLog.addStat(clientVersion, "mdelete", null, (e-s), 1);

        return rc;
    }

    public Result<List<DataEntry>> mget(int namespace, List<Object> keys) {
        if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
            return new Result<List<DataEntry>>(ResultCode.NSERROR);
        }

        long s = System.currentTimeMillis();
        RequestCommandCollection rcc = new RequestCommandCollection();

        for (Object key : keys) {
            long address = configServer.getServer(transcoder.encode(key), true);

            if (address == 0) {
                continue;
            }

            RequestGetPacket packet = (RequestGetPacket) rcc.findRequest(address);

            if (packet == null) {
                packet = new RequestGetPacket(transcoder);
                packet.setNamespace((short) namespace);
                packet.addKey(key);
                rcc.addRequest(address, packet);
            } else {
                packet.addKey(key);
            }
        }

        int reqSize = 0;

        for (BasePacket p : rcc.getRequestCommandMap().values()) {
            RequestGetPacket rp = (RequestGetPacket) p;

            // calculate uniq key number
            reqSize += rp.getKeyList().size();

            //  check key size
            int ec = rp.encode();

            if (ec == 1) {
                log.error("key too larget: ");
                return new Result<List<DataEntry>>(ResultCode.KEYTOLARGE);
            }
        }

        boolean ret = multiSender.sendRequest(rcc, timeout);

        if (!ret) {
        	MonitorLog.addStat(clientVersion, "mget exception", null);
            return new Result<List<DataEntry>>(ResultCode.CONNERROR);
        }

        List<DataEntry> results = new ArrayList<DataEntry>();

        ResultCode      rc   = ResultCode.SUCCESS;
        ResponseGetPacket resp = null;

        int maxConfigVersion = 0;

        for (BasePacket bp : rcc.getResultList()) {
            if (bp instanceof ResponseGetPacket) {
                resp = (ResponseGetPacket) bp;
                results.addAll(resp.getEntryList());

                // calculate max config version
                if (resp.getConfigVersion() > maxConfigVersion) {
                    maxConfigVersion = resp.getConfigVersion();
                }
            } else {
                log.warn("receive wrong packet type: " + bp);
            }
        }

        // 检查configserver配置
        configServer.checkConfigVersion(maxConfigVersion);

        if (results.size() == 0) {        	
            rc = ResultCode.DATANOTEXSITS;
        } else if (results.size() != reqSize) {
            if (log.isDebugEnabled()) {
                log.debug("mget partly success: request key size: " + reqSize + ", get "
                          + results.size());
            }

            rc = ResultCode.PARTSUCC;
        }

        long e = System.currentTimeMillis();
        MonitorLog.addStat(clientVersion, "mget", null, (e-s), 1);
        return new Result<List<DataEntry>>(rc, results);
    }

    public ResultCode put(int namespace, Object key, Serializable value) {
        return put(namespace, key, value, 0, 0);
    }

    public ResultCode put(int namespace, Object key, Serializable value, int version) {
        return put(namespace, key, value, version, 0);
    }

    public ResultCode put(int namespace, Object key, Serializable value, int version, int expireTime) {
        if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
            return ResultCode.NSERROR;
        }

        long s = System.currentTimeMillis();
        RequestPutPacket packet = new RequestPutPacket(transcoder);

        packet.setNamespace((short) namespace);
        packet.setKey(key);
        packet.setData(value);
        packet.setVersion((short) version);
        packet.setExpired(expireTime);

        int ec = packet.encode();

        if (ec == 1) {
            return ResultCode.KEYTOLARGE;
        } else if (ec == 2) {
            return ResultCode.VALUETOLARGE;
        }

        ResultCode rc           = ResultCode.CONNERROR;
        BasePacket returnPacket = sendRequest(key, packet);

        if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
            ReturnPacket r = (ReturnPacket) returnPacket;

            if (log.isDebugEnabled()) {
                log.debug("get return packet: " + returnPacket + ", code=" + r.getCode() + ", msg="
                          + r.getMsg());
            }

            if (r.getCode() == 0) {
                rc = ResultCode.SUCCESS;
            } else if (r.getCode() == 2) {
                rc = ResultCode.VERERROR;
            } else {
                rc = ResultCode.SERVERERROR;
            }

            // 检查configserver配置
            configServer.checkConfigVersion(r.getConfigVersion());
        } else {
        	MonitorLog.addStat(clientVersion, "put exception", null);
        }
        
        long e = System.currentTimeMillis();
        MonitorLog.addStat(clientVersion, "put", null, (e-s), 1);

        return rc;
    }
    public Map<String, CTDBMStat> getStat(int area){
    	return configServer.retrieveStat(area);
    }
    public List<Integer> getAreaList(){
    	return configServer.retrieveAreaList();
    }
    public List<Long> getDataServers(){
    	return configServer.retrieveServerList();
    }
    
    public List<Long> getServerList(){
    	return configServer.getServerList();
    }
    
	public int getBucketCount() {
		return configServer.getBucketCount();
	}

//	public int getCopyCount() {
//		return configServer.getCopyCount();
//	}
		
    /**
     * 获取当前的字符集
     *
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 设置字符集，默认为utf-8，改参数将影响string类型的数据
     *
     * @param charset the charset to set
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * 获取当前的压缩阀值
     *
     * @return the compressionThreshold
     */
    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    /**
     * 设置压缩阀值，当value超过这个阀值时将启用压缩，单位为byte，默认8KB<p>新值不能小于key的最大值，key的最大值参见<code>TairConstant.TAIR_KEY_MAX_LENTH</code></p>
     *
     * @param compressionThreshold the compressionThreshold to set
     */
    public void setCompressionThreshold(int compressionThreshold) {
        if (compressionThreshold <= TairConstant.TAIR_KEY_MAX_LENTH) {
            log.warn("compress threshold can not bigger than max key length["
                     + TairConstant.TAIR_KEY_MAX_LENTH + "], you provided:[" + compressionThreshold
                     + "]");
        } else {
            this.compressionThreshold = compressionThreshold;
        }
    }

    /**
     * 获取当前的config server列表
     *
     * @return the configServerList
     */
    public List<String> getConfigServerList() {
        return configServerList;
    }

    /**
     * 设置config server列表，这里可能有多个config server做备份，所以需要list
     *
     * @param configServerList the configServerList to set
     */
    public void setConfigServerList(List<String> configServerList) {
        this.configServerList = configServerList;
    }

    /**
     * 获取config server上的组名
     *
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * 设置config server上的组名
     *
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 获取最大等待线程数
     *
     * @return the maxWaitThread
     */
    public int getMaxWaitThread() {
        return maxWaitThread;
    }

    /**
     * 设置最大等待线程数
     *
     * @param maxWaitThread the maxWaitThread to set
     */
    public void setMaxWaitThread(int maxWaitThread) {
        this.maxWaitThread = maxWaitThread;
    }

    /**
     * 获取现在的超时时间
     *
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置超时时间，单位为ms
     *
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

	public String toString() {
        return name + " " + getVersion();
    }
}
