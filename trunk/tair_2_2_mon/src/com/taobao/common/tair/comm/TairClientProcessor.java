/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) 淘宝(中国) 2003-2008
 */
package com.taobao.common.tair.comm;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;


/**
 * 描述：Tair Client，基于Mina的Processor，用于接收响应和处理一些异常现象
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
		// 暂时先这么做吧，虽然是有点龌龊了，simple is the best
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
		
		// 不做复杂处理，任何filter chain上抛出的异常，全部关闭连接
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
