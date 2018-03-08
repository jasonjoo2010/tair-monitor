/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) 淘宝(中国) 2003-2008
 */
package com.taobao.common.tair.comm;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoFuture;
import org.apache.mina.common.IoFutureListener;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.WriteFuture;

import com.taobao.common.tair.ServerDelay;
import com.taobao.common.tair.etc.TairClientException;
import com.taobao.common.tair.packet.BasePacket;

/**
 * 描述：Tair基于Mina的Client，实现与C的通信
 * 
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class TairClient {

	private static final Log LOGGER = LogFactory.getLog(TairClient.class);


	private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();
	
	private static ConcurrentHashMap<Integer, ResponseCallbackTask> callbackTasks=
		new ConcurrentHashMap<Integer, ResponseCallbackTask>();
	
	// 假设一次异步调用最少也是100毫秒的超时
	private static long minTimeout=100L;
	
	private static ConcurrentHashMap<Integer, ArrayBlockingQueue<Object>> responses=
										new ConcurrentHashMap<Integer, ArrayBlockingQueue<Object>>();
	
	private long timestamp = 0;//最近统计的时间消耗时间，
	private AtomicLong totalConsume = new AtomicLong();
	private AtomicLong totalCounter = new AtomicLong();
	private AtomicLong totalTTL = new AtomicLong();
	private ReentrantLock lock = new ReentrantLock();
	
	private final IoSession session;
	
	private String key;
	
	// 启动回调扫描线程
	static{
		new Thread(new CallbackTasksScan()).start();
	}

	protected TairClient(IoSession session,String key) {
		this.session = session;
		this.key=key;
	}

	/**
	 * 同步调用远程的Tair Server
	 * 
	 * @param payload
	 *            发送的参数
	 * @param timeout
	 *            超时时间
	 * @return Object 返回的响应
	 * @throws TairClientException
	 */
	public Object invoke(final BasePacket packet, final long timeout)
			throws TairClientException {
		
		long begtime = System.currentTimeMillis();
		
		if (isDebugEnabled) {
			LOGGER.debug("send request [" + packet.getChid() + "],time is:"
					+ begtime);
		}
		ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(1);
		responses.put(packet.getChid(), queue);
		
		ByteBuffer bb = packet.getByteBuffer();
		bb.flip();
		byte[] data = new byte[bb.remaining()];
		bb.get(data);		
		WriteFuture writeFuture = session.write(data);
		writeFuture.addListener(new IoFutureListener() {

			public void operationComplete(IoFuture future) {
				
				WriteFuture wfuture = (WriteFuture) future;
				if (wfuture.isWritten()) {
					return;
				}
				String error = "send message to tair server error ["
						+ packet.getChid() + "], tair server: "
						+ session.getRemoteAddress()
						+ ", maybe because this connection closed :"
						+ !session.isConnected();
				LOGGER.warn(error);
				TairResponse response = new TairResponse();
				response.setRequestId(packet.getChid());
				response.setResponse(new TairClientException(error));
				try {
					putResponse(packet.getChid(), response.getResponse());
				} catch (TairClientException e) {
					// IGNORE,should not happen
				}
				// close this session
				if(session.isConnected())
					session.close();
				else
					TairClientFactory.getInstance().removeClient(key);
			}
			

		});
		Object response = null;
		try {
			response = queue.poll(timeout, TimeUnit.MILLISECONDS);
			
			boolean isLock = false;
		
			try
			{
				isLock = lock.tryLock();
				
				long curConsume = System.currentTimeMillis() - begtime;
				long consume = totalConsume.addAndGet(curConsume);
				long counter = totalCounter.incrementAndGet();
				long timeOutTimes = 0;
				
				if (curConsume >= timeout)
					timeOutTimes = totalTTL.incrementAndGet();
				
				if (isLock)
				{
					if (System.currentTimeMillis() - timestamp > 60 * 1000)//一分钟作为统计维度，先写死
					{
						if (counter > 0)
							LOGGER.error(session.getServiceAddress().toString()
									+ ", tair operate consume average : "
									+ consume/counter + ",consume : " + consume + ",counter :" + counter
									+ " , timeout count : " + timeOutTimes);
						ServerDelay.putdelay(key.trim(), (consume*1.00)/counter);
						totalConsume.addAndGet(-consume);
						totalCounter.addAndGet(-counter);
						totalTTL.addAndGet(-timeOutTimes);
						
						timestamp = System.currentTimeMillis();
					}
				}	
			}
			catch(Exception ex)
			{
				LOGGER.warn("tair statistics error",ex);
			}
			finally
			{
				if (isLock)
					lock.unlock();
			}
			
			
			if (response == null) {
				throw new TairClientException(
						"tair client invoke timeout,timeout is: " + timeout
								+ ",requestId is: " + packet.getChid() + ",remote ip:" + session.getRemoteAddress());
			} else if (response instanceof TairClientException) {
				throw (TairClientException) response;
			}
		} catch (InterruptedException e) {
			throw new TairClientException("tair client invoke error", e);
		} finally {
			responses.remove(packet.getChid());
			// For GC
			queue = null;
		}
		if (isDebugEnabled) {
			LOGGER.debug("return response [" + packet.getChid() + "],time is:"
					+ System.currentTimeMillis());
			LOGGER.debug("current responses size: " + responses.size());
		}
		return response;
	}
	
	/**
	 * 异步调用
	 * 
	 * @param requestId
	 * @param payload
	 * @param timeout
	 */
	public void invokeAsync(final BasePacket packet, final long timeout,ResponseListener listener){
		if(isDebugEnabled){
			LOGGER.debug("send request ["+packet.getChid()+"] async,time is:"+System.currentTimeMillis());
		}
		// 用于做优化，以实现空队列等待
		if(minTimeout>timeout){
			minTimeout=timeout;
		}
		final ResponseCallbackTask callbackTask=new ResponseCallbackTask(packet.getChid(),listener,timeout);
		callbackTasks.put(packet.getChid(), callbackTask);
		
		ByteBuffer bb = packet.getByteBuffer();
		bb.flip();
		byte[] data = new byte[bb.remaining()];
		bb.get(data);
		WriteFuture writeFuture=session.write(data);
		writeFuture.addListener(new IoFutureListener(){

			public void operationComplete(IoFuture future) {
				WriteFuture wfuture=(WriteFuture)future;
				if(wfuture.isWritten()){
					return;
				}
				String error = "send message to tair server error [" + packet.getChid() + "], tair server: " + session.getRemoteAddress()+", maybe because this connection closed :"+ !session.isConnected();
	            LOGGER.warn(error);
	            callbackTask.setResponse(new TairClientException(error));
	            
				// close this session
				if(session.isConnected())
					session.close();
				else
					TairClientFactory.getInstance().removeClient(key);
			}
			
		});
	}


	protected void putResponse(Integer requestId, Object response)
			throws TairClientException {
		if (responses.containsKey(requestId)) {
			try {
				ArrayBlockingQueue<Object> queue = responses.get(requestId);
				if (queue != null) {
					queue.put(response);
					if (isDebugEnabled) {
						LOGGER.debug("put response [" + requestId
								+ "],time is:" + System.currentTimeMillis());
					}
				} else if (isDebugEnabled) {
					LOGGER.debug("give up the response,maybe because timeout,requestId is:"
									+ requestId);
				}
				
			} catch (InterruptedException e) {
				throw new TairClientException("put response error", e);
			}
		} else {
			if (isDebugEnabled)
				LOGGER
						.debug("give up the response,maybe because timeout,requestId is:"
								+ requestId);
		}
	}
	
	/**
	 * 判断是否为Callback
	 * 
	 * @param requestId
	 * @return boolean
	 */
	protected boolean isCallbackTask(Integer requestId){
		return callbackTasks.containsKey(requestId);
	}
	
	/**
	 * 放入异步调用后的响应对象
	 * 
	 * @param requestId
	 * @param response
	 * @throws TairClientException
	 */
	protected void putCallbackResponse(Integer requestId,Object response) throws TairClientException{
		ResponseCallbackTask task=callbackTasks.get(requestId);
		// 再次检查task是否已经被处理过了
		if(task==null)
			return;
		task.setResponse(response);
	}

	static class CallbackTasksScan implements Runnable{

		static final long DEFAULT_SLEEPTIME=10L;
		
		boolean isRunning=true;
		
		final TairClientException timeoutException=new TairClientException("receive response timeout");
		
		public void run() {
			while(isRunning){
				List<Integer> removeIds=new ArrayList<Integer>();
				// 找出需要remove的requestId，条件为task isDone或超时
				for (Entry<Integer, ResponseCallbackTask> entry: callbackTasks.entrySet()) {
					long currentTime=System.currentTimeMillis();
					ResponseCallbackTask task=entry.getValue();
					if((task.getIsDone().get())){
						removeIds.add(task.getRequestId());
					}
					else if(task.getTimeout() < currentTime){
						removeIds.add(task.getRequestId());
						task.setResponse(timeoutException);
					}
				}
				// 清除已经通知过的任务
				for (Integer removeId : removeIds) {
					callbackTasks.remove(removeId);
				}
				// 如果此时callbackTasks大小已经为0，则进入等待状态
				// 为了避免由于并发导致这里判断大小后callbackTasks大小变化，就简单把Thread的sleep时间延长算了
				// FIXME: 后续可以考虑一个更好的通知机制，可以做到当callbackTasks为0时，此线程完全不去耗CPU
				long sleepTime=DEFAULT_SLEEPTIME;
				if(callbackTasks.size()==0){
					sleepTime=minTimeout;
				}
				// 避免一直占据CPU
				try {
					Thread.sleep(sleepTime);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	public String toString() {
		if (this.session != null)
			return this.session.toString();
		return "null session client";
	}

}
