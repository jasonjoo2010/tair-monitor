/**
 * 
 */
package com.taobao.common.tair;

import java.io.Serializable;

/**
 * @author ruohai
 * 
 */
public class DataEntry implements Serializable {

	private static final long serialVersionUID = -3492001385938512871L;
	private Object key;
	private Object value;
	private int version;

	public DataEntry(Object value) {
		this.value = value;
	}
	
	public DataEntry(Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	public DataEntry(Object key, Object value, int version) {
		this.key = key;
		this.value = value;
		this.version = version;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("key: ").append(key);
		sb.append(", value: ").append(value);
		sb.append(", version: ").append(version);
		return sb.toString();
	}

	
}
