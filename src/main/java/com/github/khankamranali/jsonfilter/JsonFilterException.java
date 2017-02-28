package com.github.khankamranali.jsonfilter;

/**
 * Runtime exception is thrown when there is json filter parsing error or Bean
 * parsing or filter execution error occurs.
 * 
 * @author Kamran Ali Khan (khankamranali@gmail.com)
 * 
 */
public class JsonFilterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JsonFilterException() {
		super();
	}

	public JsonFilterException(String message) {
		super(message);
	}

	public JsonFilterException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonFilterException(Throwable cause) {
		super(cause);
	}
}
