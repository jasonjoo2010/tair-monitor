/**
 * 
 */
package com.taobao.common.tair.etc;

/**
 * Server������ʱ�׳����쳣��MultiTairManager��������쳣��ᵽ���õ���ػ���ȥ����
 * 
 * @author ruohai
 * 
 */
public class TairServerDownException extends RuntimeException {
	private static final long serialVersionUID = 3596753870061389862L;

	public TairServerDownException() {
	}

	public TairServerDownException(String message) {
		super(message);
	}

	public TairServerDownException(Throwable cause) {
		super(cause);
	}

	public TairServerDownException(String message, Throwable cause) {
		super(message, cause);
	}

}
