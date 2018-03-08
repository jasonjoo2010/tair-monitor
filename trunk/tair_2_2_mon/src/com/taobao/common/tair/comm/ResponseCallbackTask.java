/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) 淘宝(中国) 2003-2008
 */
package com.taobao.common.tair.comm;

import java.util.concurrent.atomic.AtomicBoolean;

import com.taobao.common.tair.etc.TairClientException;

/**
 * 描述：响应包回调Task
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class ResponseCallbackTask {

	private Integer requestId;
	
	private ResponseListener listener;
	
	private AtomicBoolean isDone=new AtomicBoolean(false);
	
	// 超时的时间
	private long timeout;
	
	public ResponseCallbackTask(Integer requestId,ResponseListener listener,long timeout){
		this.requestId=requestId;
		this.listener=listener;
		this.timeout=System.currentTimeMillis()+timeout;
	}

	public Integer getRequestId() {
		return requestId;
	}

	public ResponseListener getListener() {
		return listener;
	}
	
	public long getTimeout() {
		return timeout;
	}

	public AtomicBoolean getIsDone() {
		return isDone;
	}

	public void setResponse(Object response) {
		// 如已经完成了，则忽略继续调用
		if(!isDone.compareAndSet(false, true)){
			return;
		}
		// 当放置响应时，立刻调用回调线程
		if(response instanceof TairClientException){
			listener.exceptionCaught((TairClientException) response);
		}
		else{
			listener.responseReceived(response);
		}
	}
	
}
