/**
 * 
 */
package com.taobao.common.tair.etc;

/**
 * Server不可用时抛出的异常，MultiTairManager捕获这个异常后会到配置的异地机房去请求
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
