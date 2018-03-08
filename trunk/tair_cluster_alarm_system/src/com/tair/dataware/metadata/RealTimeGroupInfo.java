package com.tair.dataware.metadata;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.tair.dataware.db.Groupinfo;

public class RealTimeGroupInfo {
	
	final long timestamp;
	
	public Groupinfo groupinfo;
	
	Map<String, DataServerStatistics> DSInfo ;
	Map<Integer, AreaStatistics> AreaInfo;
	
	public RealTimeGroupInfo(long timestamp,
			Map<String, DataServerStatistics> DSInfo, 
			Map<Integer, AreaStatistics> AreaInfo,
			Groupinfo groupinfo){
		this.timestamp = timestamp ;
		this.DSInfo = DSInfo;
		this.AreaInfo = AreaInfo;
		this.groupinfo = groupinfo;
	}

	public Map<String, DataServerStatistics> getDSInfo() {
		return DSInfo;
	}

	public Map<Integer, AreaStatistics> getAreaInfo() {
		return AreaInfo;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
}
