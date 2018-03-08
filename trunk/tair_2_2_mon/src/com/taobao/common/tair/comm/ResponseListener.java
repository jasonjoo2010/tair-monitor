/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) 淘宝(中国) 2003-2008
 */
package com.taobao.common.tair.comm;

import com.taobao.common.tair.etc.TairClientException;

/**
 * 描述：响应listener，适用于异步调用
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public interface ResponseListener {

	/**
	 * 响应通知
	 * 
	 * @param response
	 * @param exception
	 */
	public void responseReceived(Object response);
	
	/**
	 * 异常通知
	 * 
	 * @param exception
	 */
	public void exceptionCaught(TairClientException exception);
	
}
