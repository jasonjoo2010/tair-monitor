package com.tair.dataware.metadata;


import java.util.HashMap;
import java.util.Map;

public class DataServerStatistics {

	String ip;
	int port;
	String stat;
	double delay;
	Map<Integer, AreaStatistics> _area_statistics; 
	//Please Cause On Concurrency Write , Concurrency Read is Allowed

	public DataServerStatistics() {
		ip = null;
		port = -1;
		stat = null;
		delay = -1;
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

	public Map<Integer, AreaStatistics> get_area_statistics() {
		return _area_statistics;
	}

	public int getAreacount() {
		return _area_statistics.size();
	}


	public double getDelay() {
		return delay;
	}


	public void setDelay(double delay) {
		this.delay = delay;
	}
	
	
}
