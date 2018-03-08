package com.tair.dataware.metadata;


import org.apache.commons.logging.Log;


public class AreaStatistics {
	int area;
	
	long evictCount;
	long getCount;
	long hitCount;
	long itemCount;
	long putCount;
	long removeCount;
	
	long useSize;
	long dataSize;
	long quota;

	public AreaStatistics() {
		area = -1;
		
		evictCount = 0;
		getCount = 0;
		hitCount = 0;
		itemCount = 0;
		putCount = 0;
		removeCount = 0;
		
		useSize = 0;
		dataSize = 0;
		quota = -1;
	}

	public void showdata(Log log){
		log.info("area is "+area +" getCount "+getCount +" putCount "+putCount +" useSize "+useSize);
	}
	
	public long getQuota() {
		return quota;
	}

	public void setQuota(long quota) {
		this.quota = quota;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public long getDataSize() {
		return dataSize;
	}

	public void setDataSize(long dataSize) {
		this.dataSize = dataSize;
	}

	public long getEvictCount() {
		return evictCount;
	}

	public void setEvictCount(long evictCount) {
		this.evictCount = evictCount;
	}

	public long getGetCount() {
		return getCount;
	}

	public void setGetCount(long getCount) {
		this.getCount = getCount;
	}

	public long getHitCount() {
		return hitCount;
	}

	public void setHitCount(long hitCount) {
		this.hitCount = hitCount;
	}

	public long getItemCount() {
		return itemCount;
	}

	public void setItemCount(long itemCount) {
		this.itemCount = itemCount;
	}

	public long getPutCount() {
		return putCount;
	}

	public void setPutCount(long putCount) {
		this.putCount = putCount;
	}

	public long getRemoveCount() {
		return removeCount;
	}

	public void setRemoveCount(long removeCount) {
		this.removeCount = removeCount;
	}

	public long getUseSize() {
		return useSize;
	}

	public void setUseSize(long useSize) {
		this.useSize = useSize;
	}
	
	public void addup(AreaStatistics this_stat) {
		this.dataSize+=this_stat.getDataSize();
		this.evictCount+=this_stat.getEvictCount();
		this.getCount+=this_stat.getGetCount();
		this.hitCount+=this_stat.getHitCount();
		this.itemCount+=this_stat.getItemCount();
		this.putCount+=this_stat.getPutCount();
		this.removeCount+=this_stat.getRemoveCount();
		this.useSize+=this_stat.getUseSize();
	}
}
