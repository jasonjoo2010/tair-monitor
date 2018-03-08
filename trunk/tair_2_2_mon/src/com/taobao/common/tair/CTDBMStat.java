package com.taobao.common.tair;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CTDBMStat {
	private static final Log log = LogFactory.getLog(CTDBMStat.class);
	long getCount;
	long putCount;
	long evictCount;
	long removeCount;
	long hitCount;
	long requestCount;
	long readBytes;
	long writeBytes;

	long dataSize;
	long useSize;
	int itemCount;

	int currLoad;
	int startupTime;

	public long getGetCount() {
		return getCount;
	}

	public void setGetCount(long getCount) {
		this.getCount = getCount;
	}

	public long getPutCount() {
		return putCount;
	}

	public void setPutCount(long putCount) {
		this.putCount = putCount;
	}

	public long getEvictCount() {
		return evictCount;
	}

	public void setEvictCount(long evictCount) {
		this.evictCount = evictCount;
	}

	public long getRemoveCount() {
		return removeCount;
	}

	public void setRemoveCount(long removeCount) {
		this.removeCount = removeCount;
	}

	public long getHitCount() {
		return hitCount;
	}

	public void setHitCount(long hitCount) {
		this.hitCount = hitCount;
	}

	public long getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(long requestCount) {
		this.requestCount = requestCount;
	}

	public long getReadBytes() {
		return readBytes;
	}

	public void setReadBytes(long readBytes) {
		this.readBytes = readBytes;
	}

	public long getWriteBytes() {
		return writeBytes;
	}

	public void setWriteBytes(long writeBytes) {
		this.writeBytes = writeBytes;
	}

	public long getDataSize() {
		return dataSize;
	}

	public void setDataSize(long dataSize) {
		this.dataSize = dataSize;
	}

	public long getUseSize() {
		return useSize;
	}

	public void setUseSize(long useSize) {
		this.useSize = useSize;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public int getCurrLoad() {
		return currLoad;
	}

	public void setCurrLoad(int currLoad) {
		this.currLoad = currLoad;
	}

	public int getStartupTime() {
		return startupTime;
	}

	public void setStartupTime(int startupTime) {
		this.startupTime = startupTime;
	}

	public void show_stat(){
		log.debug(" getCount="+getCount);
		log.debug(" putCount="+putCount);
		log.debug(" evictCount="+evictCount);
		log.debug(" removeCount="+removeCount);
		log.debug(" hitCount="+hitCount);
		log.debug(" requestCount="+requestCount);
		log.debug(" readBytes="+readBytes);
		log.debug(" writeBytes="+writeBytes);

		log.debug(" dataSize="+dataSize);
		log.debug(" useSize="+useSize);
		log.debug(" itemCount="+itemCount);

		log.debug(" currLoad="+currLoad);
		log.debug(" startupTime="+startupTime);
	}
}
