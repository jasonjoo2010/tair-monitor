/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) 淘宝(中国) 2003-2008
 */
package com.taobao.common.tair.comm;

/**
 * 描述：Tair返回的响应对象
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class TairResponse {

	// 请求时产生的ID
	private Integer requestId;
	
	// 返回的响应包
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
