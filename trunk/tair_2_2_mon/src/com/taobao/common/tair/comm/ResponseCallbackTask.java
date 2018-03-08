/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) �Ա�(�й�) 2003-2008
 */
package com.taobao.common.tair.comm;

import java.util.concurrent.atomic.AtomicBoolean;

import com.taobao.common.tair.etc.TairClientException;

/**
 * ��������Ӧ���ص�Task
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class ResponseCallbackTask {

	private Integer requestId;
	
	private ResponseListener listener;
	
	private AtomicBoolean isDone=new AtomicBoolean(false);
	
	// ��ʱ��ʱ��
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
		// ���Ѿ�����ˣ�����Լ�������
		if(!isDone.compareAndSet(false, true)){
			return;
		}
		// ��������Ӧʱ�����̵��ûص��߳�
		if(response instanceof TairClientException){
			listener.exceptionCaught((TairClientException) response);
		}
		else{
			listener.responseReceived(response);
		}
	}
	
}
