package com.taobao.common.tair;

import java.util.concurrent.ConcurrentHashMap;

public class ServerDelay {
	static public ConcurrentHashMap<String, Double> delay;
	static {
		delay = new ConcurrentHashMap<String, Double>();
		delay.clear();
	}
	
	static public void putdelay(String key , double value){
		delay.put(key.trim(), value);
	}
}
