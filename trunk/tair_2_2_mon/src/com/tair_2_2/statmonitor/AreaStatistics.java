package com.tair_2_2.statmonitor;

import com.taobao.common.tair.CTDBMStat;

public class AreaStatistics {

	long quota;
	
	int area;
	long dataSize;
	long evictCount;
	long getCount;
	long hitCount;
	long itemCount;
	long putCount;
	long removeCount;
	long useSize;

	public AreaStatistics() {
		quota = -1;
		area = -1;
		dataSize = 0;
		evictCount = 0;
		getCount = 0;
		hitCount = 0;
		itemCount = 0;
		putCount = 0;
		removeCount = 0;
		useSize = 0;
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
	public void sub(AreaStatistics this_stat) {
		this.evictCount-=this_stat.getEvictCount();
		evictCount=evictCount>0?evictCount:-evictCount;
		this.getCount-=this_stat.getGetCount();
		getCount=getCount>0?getCount:-getCount;
		this.hitCount-=this_stat.getHitCount();
		hitCount=hitCount>0?hitCount:-hitCount;
		this.putCount-=this_stat.getPutCount();
		putCount=putCount>0?putCount:-putCount;
		this.removeCount-=this_stat.getRemoveCount();
		removeCount=removeCount>0?removeCount:-removeCount;
	}
	public void average(long interval){
		this.evictCount/=interval;
		this.getCount/=interval;
		this.hitCount/=interval;
		this.putCount/=interval;
		this.removeCount/=interval;
	}
	
	public void addup(CTDBMStat this_stat){
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
