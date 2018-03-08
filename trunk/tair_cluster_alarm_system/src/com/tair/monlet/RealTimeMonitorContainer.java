package com.tair.monlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RealTimeMonitorContainer {
	private static final Log log = LogFactory.getLog(RealTimeMonitorContainer.class);
	
	static public ConcurrentHashMap<Integer,RealTimeMonlet> RealtimeMonlets;
	
	static {
		RealtimeMonlets = new ConcurrentHashMap<Integer,RealTimeMonlet>();
	}
	
	public static Map<Integer,RealTimeMonlet> getRealtimeMonlets(){
		return RealtimeMonlets;
	}
	
	public static boolean registerMonlet(Integer id ,RealTimeMonlet foo, boolean cover){
		log.info("try to registerMonlet id ="+ id + " monlet = " + foo.toString());
		if(cover == true){
			RealtimeMonlets.put(id, foo);
			return true;
		} else if(RealtimeMonlets.contains(id)==true){
				return false;
		}
		RealtimeMonlets.put(id, foo);
		return true;
	}
	
	private RealTimeMonitorContainer(){
		
	}
}
