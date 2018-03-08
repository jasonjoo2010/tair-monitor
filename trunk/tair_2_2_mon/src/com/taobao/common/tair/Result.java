/**
 * 
 */
package com.taobao.common.tair;

import java.util.Collection;

/**
 * 
 * @author ruohai
 */
public class Result<V> {
	private ResultCode rc;
	private V value;

	public Result(ResultCode rc) {
		this.rc = rc;
	}

	public Result(ResultCode rc, V value) {
		this.rc = rc;
		this.value = value;
	}

	public boolean isSuccess() {
		return rc.isSuccess();
	}

	public V getValue() {
		return this.value;
	}

	/**
	 * 
	 * @return the rc
	 */
	public ResultCode getRc() {
		return rc;
	}

	/**
	 * 
	 * @param rc
	 *            the rc to set
	 */
	public void setRc(ResultCode rc) {
		this.rc = rc;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Result: [").append(rc.toString()).append("]\n");
		if(value != null) {
			if(value instanceof DataEntry) {
				sb.append("\t").append(value.toString()).append("\n");
			} else if (value instanceof Collection) {
				Collection<DataEntry> des = (Collection<DataEntry>) value;
				sb.append("\tentry size: ").append(des.size()).append("\n");
				for (DataEntry de : des) {
					sb.append("\t").append(de.toString()).append("\n");
				}
			} else {
				sb.append("\tvalue: ").append(value);
			}
		}
		return sb.toString();
	}
}
