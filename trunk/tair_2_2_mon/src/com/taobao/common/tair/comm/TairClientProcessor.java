/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) �Ա�(�й�) 2003-2008
 */
package com.taobao.common.tair.comm;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;


/**
 * ������Tair Client������Mina��Processor�����ڽ�����Ӧ�ʹ���һЩ�쳣����
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class TairClientProcessor extends IoHandlerAdapter{
	private static final Log log = LogFactory.getLog(TairClientProcessor.class);
	private TairClient client=null;
	
	private TairClientFactory factory=null;
	
	private String key=null;
	
	public void setClient(TairClient client){
		this.client=client;
	}
	
	public void setFactory(TairClientFactory factory,String targetUrl){
		this.factory=factory;
		key=targetUrl;
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {

		TairResponse response=(TairResponse)message;
		
		if(response.getResponse() == null) {
			log.error("error caused by receive null response\n");
			return ;
		}
		
		Integer requestId=response.getRequestId();
		// ��ʱ����ô���ɣ���Ȼ���е������ˣ�simple is the best
		if(client.isCallbackTask(requestId)){
			client.putCallbackResponse(requestId, response.getResponse());
		}
		else{
			client.putResponse(requestId, response.getResponse());
		}
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("connection exception", cause);
		
		// �������Ӵ����κ�filter chain���׳����쳣��ȫ���ر�����
		if(!(cause instanceof IOException)){
			session.close();
		}
	}

	public void sessionClosed(IoSession session) throws Exception {
		if (log.isDebugEnabled())
			log.debug(session + " closed");
		
		factory.removeClient(key);
	}
	
}
