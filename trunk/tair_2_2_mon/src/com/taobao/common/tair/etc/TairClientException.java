/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) 淘宝(中国) 2003-2008
 */
package com.taobao.common.tair.etc;

/**
 * 描述：所有Tair Client的异常
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class TairClientException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TairClientException(String message, Exception e) {
		super(message,e);
	}

	public TairClientException(String message) {
		super(message);
	}

}
