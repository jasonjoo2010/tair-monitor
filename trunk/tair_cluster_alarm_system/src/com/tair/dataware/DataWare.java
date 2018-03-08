package com.tair.dataware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tair.dataware.metadata.RealTimeGroupInfoCollector;

public class DataWare {
	public static Map<Integer,RealTimeGroupInfoCollector> DataWare;
	
	static {
		DataWare = new ConcurrentHashMap<Integer,RealTimeGroupInfoCollector>();
	}
	
	public static void putGroupInfoCollector(int gid,RealTimeGroupInfoCollector foo){
		DataWare.put(gid, foo);
		return ;
	}
	
	public static RealTimeGroupInfoCollector getGroupInfoCollector(int gid){
		 return DataWare.get(gid);
	}
	
}
