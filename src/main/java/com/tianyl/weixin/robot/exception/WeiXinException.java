package com.tianyl.weixin.robot.exception;

public class WeiXinException extends RuntimeException {

	private static final long serialVersionUID = 6112007933875468566L;

	public WeiXinException() {
		super();
	}

	public WeiXinException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WeiXinException(String message, Throwable cause) {
		super(message, cause);
	}

	public WeiXinException(String message) {
		super(message);
	}

	public WeiXinException(Throwable cause) {
		super(cause);
	}

}
