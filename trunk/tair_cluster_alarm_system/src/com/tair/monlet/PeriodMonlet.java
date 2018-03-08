package com.tair.monlet;

import com.tair.dataware.metadata.RealTimeGroupInfo;

public interface PeriodMonlet {
	public boolean doMonitor(RealTimeGroupInfo data);
}
