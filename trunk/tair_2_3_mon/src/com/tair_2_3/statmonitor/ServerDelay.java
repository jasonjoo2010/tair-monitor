//package com.taobao.tair;
package com.tair_2_3.statmonitor;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServerDelay {
	private static final Log log = LogFactory.getLog(ServerDelay.class);
	static public ConcurrentHashMap<String, Double> delay;
	static {
		delay = new ConcurrentHashMap<String, Double>();
		delay.clear();
	}
	
	static public void putdelay(String key , double value){
		log.info("put delay of "+key);
		delay.put(key.trim(), value);
	}
}
