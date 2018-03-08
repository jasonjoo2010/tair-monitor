/**
 * 
 */
package com.taobao.common.tair;

/**
 * 请求的返回值
 * @author ruohai
 */
public class ResultCode {
	/** 成功 */
	public static final ResultCode SUCCESS = new ResultCode(0, "success");
	/** 数据不存在 */
	public static final ResultCode DATANOTEXSITS = new ResultCode(1, "data not exist");
	
	/** 网络连接异常 */
	public static final ResultCode CONNERROR = new ResultCode(-1,
			"connection error or timeout");
	/** 服务器端错误 */
	public static final ResultCode SERVERERROR = new ResultCode(-2, "server error");
	
	/** 版本错误 */
	public static final ResultCode VERERROR = new ResultCode(-4, "version error");
	/** key长度错误 */
	public static final ResultCode KEYTOLARGE = new ResultCode(-5, "key length error");
	/** value长度错误 */
	public static final ResultCode VALUETOLARGE = new ResultCode(-6, "value length error");
	/** namespace 大小错误 */
	public static final ResultCode NSERROR = new ResultCode(-7, "namsepace range error, should between 0 ~ 65535");
	/** 部分成功 */
	public static final ResultCode PARTSUCC = new ResultCode(-10, "partly success");

	private int code;
	private String message;

	public ResultCode(int code) {
		this.code = code;
	}

	public ResultCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * 获取内部的代码
	 * @return
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 获取描述信息
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 返回结果是否成功
	 * @return
	 */
	public boolean isSuccess() {
		return code >= 0;
	}

	@Override
	public String toString() {
		return "code=" + code + ", msg=" + message;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj != null && (obj instanceof ResultCode)) {
			ResultCode rc = (ResultCode)obj;
			return rc.getCode() == this.code;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.code;
	}
}
