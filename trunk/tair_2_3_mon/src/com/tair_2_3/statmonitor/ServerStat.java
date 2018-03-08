package com.tair_2_3.statmonitor;

import java.util.HashMap;

public class ServerStat {

	String ip;
	int port;
	String stat;
	int areacount;
	double delay;
	HashMap<Integer, AreaStatistics> _area_statistics;

	public ServerStat() {
		ip = null;
		port = -1;
		stat = null;
		areacount = 0;
		delay = 0 ;
		_area_statistics = new HashMap<Integer, AreaStatistics>();
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public HashMap<Integer, AreaStatistics> get_area_statistics() {
		return _area_statistics;
	}

	public int getAreacount() {
		return areacount;
	}
	
	public void incAreacount() {
		areacount++ ;
	}
	
	public void setDelay(double delay) {
		this.delay = delay;
	}

	public double getDelay() {
		return delay;
	}


	

	

}
