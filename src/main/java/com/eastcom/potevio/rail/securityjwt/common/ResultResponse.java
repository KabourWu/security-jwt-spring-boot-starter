package com.eastcom.potevio.rail.securityjwt.common;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class ResultResponse {
	// HTTP Response Status Code
	private final Boolean success;

	// General Error message
	private final String message;

	// Error code
	private final Integer code;

	public ResultResponse(Boolean success, String message, int code) {
		this.success = success;
		this.message = message;
		this.code = code;
	}

	public static ResultResponse failed(final String message, final ErrorCode errorCode) {
		return new ResultResponse(false, message, errorCode.getErrorCode());
	}

	public Boolean getSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public Integer getCode() {
		return code;
	}
}
