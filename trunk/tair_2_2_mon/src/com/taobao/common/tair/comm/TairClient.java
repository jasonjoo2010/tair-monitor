/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) �Ա�(�й�) 2003-2008
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
 * ������Tair����Mina��Client��ʵ����C��ͨ��
 * 
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class TairClient {

	private static final Log LOGGER = LogFactory.getLog(TairClient.class);


	private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();
	
	private static ConcurrentHashMap<Integer, ResponseCallbackTask> callbackTasks=
		new ConcurrentHashMap<Integer, ResponseCallbackTask>();
	
	// ����һ���첽��������Ҳ��100����ĳ�ʱ
	private static long minTimeout=100L;
	
	private static ConcurrentHashMap<Integer, ArrayBlockingQueue<Object>> responses=
										new ConcurrentHashMap<Integer, ArrayBlockingQueue<Object>>();
	
	private long timestamp = 0;//���ͳ�Ƶ�ʱ������ʱ�䣬
	private AtomicLong totalConsume = new AtomicLong();
	private AtomicLong totalCounter = new AtomicLong();
	private AtomicLong totalTTL = new AtomicLong();
	private ReentrantLock lock = new ReentrantLock();
	
	private final IoSession session;
	
	private String key;
	
	// �����ص�ɨ���߳�
	static{
		new Thread(new CallbackTasksScan()).start();
	}

	protected TairClient(IoSession session,String key) {
		this.session = session;
		this.key=key;
	}

	/**
	 * ͬ������Զ�̵�Tair Server
	 * 
	 * @param payload
	 *            ���͵Ĳ���
	 * @param timeout
	 *            ��ʱʱ��
	 * @return Object ���ص���Ӧ
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
					if (System.currentTimeMillis() - timestamp > 60 * 1000)//һ������Ϊͳ��ά�ȣ���д��
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
	 * �첽����
	 * 
	 * @param requestId
	 * @param payload
	 * @param timeout
	 */
	public void invokeAsync(final BasePacket packet, final long timeout,ResponseListener listener){
		if(isDebugEnabled){
			LOGGER.debug("send request ["+packet.getChid()+"] async,time is:"+System.currentTimeMillis());
		}
		// �������Ż�����ʵ�ֿն��еȴ�
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
	 * �ж��Ƿ�ΪCallback
	 * 
	 * @param requestId
	 * @return boolean
	 */
	protected boolean isCallbackTask(Integer requestId){
		return callbackTasks.containsKey(requestId);
	}
	
	/**
	 * �����첽���ú����Ӧ����
	 * 
	 * @param requestId
	 * @param response
	 * @throws TairClientException
	 */
	protected void putCallbackResponse(Integer requestId,Object response) throws TairClientException{
		ResponseCallbackTask task=callbackTasks.get(requestId);
		// �ٴμ��task�Ƿ��Ѿ����������
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
				// �ҳ���Ҫremove��requestId������Ϊtask isDone��ʱ
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
				// ����Ѿ�֪ͨ��������
				for (Integer removeId : removeIds) {
					callbackTasks.remove(removeId);
				}
				// �����ʱcallbackTasks��С�Ѿ�Ϊ0�������ȴ�״̬
				// Ϊ�˱������ڲ������������жϴ�С��callbackTasks��С�仯���ͼ򵥰�Thread��sleepʱ���ӳ�����
				// FIXME: �������Կ���һ�����õ�֪ͨ���ƣ�����������callbackTasksΪ0ʱ�����߳���ȫ��ȥ��CPU
				long sleepTime=DEFAULT_SLEEPTIME;
				if(callbackTasks.size()==0){
					sleepTime=minTimeout;
				}
				// ����һֱռ��CPU
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
