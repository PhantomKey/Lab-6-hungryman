package com.thehungryguy.data;

public class DataRequestException extends Exception {
	DataRequestException(String message) {
		super(message);
	}

	DataRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}
