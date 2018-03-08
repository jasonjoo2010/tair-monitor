/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) �Ա�(�й�) 2003-2008
 */
package com.taobao.common.tair.comm;

/**
 * ������Tair���ص���Ӧ����
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class TairResponse {

	// ����ʱ������ID
	private Integer requestId;
	
	// ���ص���Ӧ��
	private Object response;

	public Integer getRequestId() {
		return requestId;
	}

	public Object getResponse() {
		return response;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public void setResponse(Object response) {
		this.response = response;
	}
	
}
