package com.tair.monlet;

import com.tair.dataware.metadata.RealTimeGroupInfo;

public interface RealTimeMonlet {
	public boolean doMonitor(RealTimeGroupInfo data);
}
