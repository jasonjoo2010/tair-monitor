package com.tair.dataware.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tair.dataware.db.Groupinfo;

public class RealTimeGroupInfoCollector {
	private static final Log log = LogFactory.getLog(RealTimeGroupInfoCollector.class);
	
	final int RecordCountLimit = 24*60 ;
	
	public Groupinfo groupinfo;
	RealTimeGroupInfo[] finitudeGroupInfo ;
	Groupinfo groupindentident;
	
	public RealTimeGroupInfoCollector(Groupinfo groupinfo){
		this.groupinfo = groupinfo;
		finitudeGroupInfo = new RealTimeGroupInfo[RecordCountLimit];
		log.info("Initalize " + RecordCountLimit + "Units of RealTimeGroupInfo");
	}

	public RealTimeGroupInfo getRealTimeGroupInfo(long timestamp){
		return finitudeGroupInfo[(int)(timestamp%(RecordCountLimit))];
		
	}
	
	public void setRealTimeGroupInfo(long timestamp,RealTimeGroupInfo foo){
		finitudeGroupInfo[(int)(timestamp%(RecordCountLimit))] = foo;
	}
	
	public Groupinfo getGroupindentident() {
		return groupindentident;
	}

	public void setGroupindentident(Groupinfo groupindentident) {
		this.groupindentident = groupindentident;
	}
	
	
}
