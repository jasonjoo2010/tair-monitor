/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) �Ա�(�й�) 2003-2008
 */
package com.taobao.common.tair.etc;

/**
 * ����������Tair Client���쳣
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
