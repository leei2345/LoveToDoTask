package com.aosbank.lovetodotask.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
	
	private static Logger httpLogger = LoggerFactory.getLogger("httpLogger");


	public static void HttpInfoLog (String log) {
		httpLogger.info(log);
	}
	
	public static void HttpDebugLog (String log) {
		httpLogger.debug(log);
	}
	
}
