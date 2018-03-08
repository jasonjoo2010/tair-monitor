/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) �Ա�(�й�) 2003-2008
 */
package com.taobao.common.tair.comm;

import com.taobao.common.tair.etc.TairClientException;

/**
 * ��������Ӧlistener���������첽����
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public interface ResponseListener {

	/**
	 * ��Ӧ֪ͨ
	 * 
	 * @param response
	 * @param exception
	 */
	public void responseReceived(Object response);
	
	/**
	 * �쳣֪ͨ
	 * 
	 * @param exception
	 */
	public void exceptionCaught(TairClientException exception);
	
}
