package com.taobao.common.tair.comm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.common.tair.etc.TairClientException;
import com.taobao.common.tair.etc.TairUtil;
import com.taobao.common.tair.packet.BasePacket;
import com.taobao.common.tair.packet.PacketStreamer;
import com.taobao.common.tair.packet.RequestCommandCollection;

/**
 * 同时发送多个请求，然后最后进行等待。
 *
 * @author cjxrobot
 */
public class MultiSender {
    private PacketStreamer   packetStreamer = null;

    public MultiSender(PacketStreamer packetStreamer) {
        this.packetStreamer = packetStreamer;
    }

    /**
     * 
     * @param r
     * @param timeout
     *
     * @return
     */
    public boolean sendRequest(RequestCommandCollection rcList, int timeout) {
        Map<Long, BasePacket> map       = rcList.getRequestCommandMap();
        MultiReceiveListener               listener  = new MultiReceiveListener(rcList.getResultList());
        int                                sendCount = 0;

        for (Long addr : map.keySet()) {
            
            TairClient client = null;
            
            try {
            	client = TairClientFactory.getInstance().get(TairUtil.idToAddress(addr), timeout, packetStreamer);
			} catch (TairClientException e) {				
			}

            if (client == null) {
                continue;
            }

            
           client.invokeAsync(map.get(addr), timeout, listener);
           sendCount ++;
        }

        listener.await(sendCount, timeout);

        return (sendCount == listener.doneCount);
    }

    // 接收
    public class MultiReceiveListener implements ResponseListener {
    	private final Log log = LogFactory.getLog(MultiReceiveListener.class);
        private List<BasePacket> resultList = null;
        private ReentrantLock    lock       = null;
        private Condition        cond       = null;
        private int              doneCount  = 0;        

        public MultiReceiveListener(List<BasePacket> resultList) {
            this.resultList = resultList;
            this.lock       = new ReentrantLock();
            this.cond       = this.lock.newCondition();
        }
        
		public void responseReceived(Object response) {
			lock.lock();

			try {
				resultList.add((BasePacket) response);
				this.doneCount++;
				cond.signal();
			} finally {
				lock.unlock();
			}
		}

		public void exceptionCaught(TairClientException exception) {	
			if (log.isDebugEnabled()) {
				log.debug("", exception);
			}
		}

		/**
         * 等待接收完成
         *
         * @param count
         * @param timeout 单位: ms
         *
         * @return
         */
        public boolean await(int count, int timeout) {
            long t = TimeUnit.MILLISECONDS.toNanos(timeout);

            lock.lock();

            try {
                while (this.doneCount < count) {
                    if ((t = cond.awaitNanos(t)) <= 0) {
                        return false;
                    }
                }
            } catch (InterruptedException e) {
                return false;
            } finally {
                lock.unlock();
            }
    
            return true;
        }
    }
}
