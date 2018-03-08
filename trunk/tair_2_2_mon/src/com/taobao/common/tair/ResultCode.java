/**
 * 
 */
package com.taobao.common.tair;

/**
 * ����ķ���ֵ
 * @author ruohai
 */
public class ResultCode {
	/** �ɹ� */
	public static final ResultCode SUCCESS = new ResultCode(0, "success");
	/** ���ݲ����� */
	public static final ResultCode DATANOTEXSITS = new ResultCode(1, "data not exist");
	
	/** ���������쳣 */
	public static final ResultCode CONNERROR = new ResultCode(-1,
			"connection error or timeout");
	/** �������˴��� */
	public static final ResultCode SERVERERROR = new ResultCode(-2, "server error");
	
	/** �汾���� */
	public static final ResultCode VERERROR = new ResultCode(-4, "version error");
	/** key���ȴ��� */
	public static final ResultCode KEYTOLARGE = new ResultCode(-5, "key length error");
	/** value���ȴ��� */
	public static final ResultCode VALUETOLARGE = new ResultCode(-6, "value length error");
	/** namespace ��С���� */
	public static final ResultCode NSERROR = new ResultCode(-7, "namsepace range error, should between 0 ~ 65535");
	/** ���ֳɹ� */
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
	 * ��ȡ�ڲ��Ĵ���
	 * @return
	 */
	public int getCode() {
		return code;
	}

	/**
	 * ��ȡ������Ϣ
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * ���ؽ���Ƿ�ɹ�
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
