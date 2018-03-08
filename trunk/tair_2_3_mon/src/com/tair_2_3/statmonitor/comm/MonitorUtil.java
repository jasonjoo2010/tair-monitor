package com.tair_2_3.statmonitor.comm;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.tair_2_3.statmonitor.TairStatInfoReaderDeamon;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.etc.TairUtil;

public class MonitorUtil {
	public static String ADMIN = "tair´ðÒÉ";

	@SuppressWarnings("deprecation")
	public static Serializable GetObject(int Type, String Key) {
		Serializable obj = null;

		switch (Type) {
		case TairConstant.TAIR_STYPE_INT:
			obj = new Integer(Key);
			break;
		case TairConstant.TAIR_STYPE_STRING:
			obj = Key;
			break;
		case TairConstant.TAIR_STYPE_BOOL:
			obj = new Boolean(Key);
			break;
		case TairConstant.TAIR_STYPE_LONG:
			obj = new Long(Key);
			break;
		case TairConstant.TAIR_STYPE_DATE:
			obj = new Date(Key);
			break;
		case TairConstant.TAIR_STYPE_BYTE:
			obj = new Byte(Key);
			break;
		case TairConstant.TAIR_STYPE_FLOAT:
			obj = new Float(Key);
			break;
		case TairConstant.TAIR_STYPE_DOUBLE:
			obj = new Double(Key);
			break;
		case TairConstant.TAIR_STYPE_BYTEARRAY:
			obj = Key.getBytes();
			break;
		}

		return obj;
	}

	public static String FormalizeReturnMesg(String returnMsg)
			throws UnsupportedEncodingException {
		return new String(returnMsg.getBytes("UTF-8"), "ISO-8859-1");
	}

	public static String GeneralLog(HttpServletRequest request, String mesg) {
		String clusterId = TairStatInfoReaderDeamon.getTask().getClusterId();
		String clientIp = getRemoteAddress(request);
		String rslt = "request of cluster(" + clusterId + ") from ip("
				+ clientIp + "), mesg is " + mesg;
		return rslt;
	}

	public static String LogParameter(HttpServletRequest request) {
		return MonitorUtil.GeneralLog(request, GetAllParameter(request));
	}

	public static String GetAllParameter(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (Enumeration<String> e = request.getParameterNames(); e
				.hasMoreElements();) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			Object o = e.nextElement();
			sb.append((String) o + ":" + request.getParameter((String) o));
		}
		return sb.toString();
	}

	public static String getRemoteAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String FormalizeGetResult(Result<DataEntry> ret) {
		String value = ret.toString();
		if (value.length() > 4096) {
			StringBuffer sb = new StringBuffer();
			sb.append("Result: [").append(ret.getRc().toString()).append("]\n");
			sb.append("\t");
			if (ret.getValue() != null) {
				int version = ret.getValue().getVersion();
				int cdate = ret.getValue().getCreateDate();
				int mdate = ret.getValue().getModifyDate();
				int edate = ret.getValue().getExpriedDate();
				sb.append("key: ").append(ret.getValue().getKey());
				sb.append(", value: ").append(
						"your value is too large to display");
				sb.append(", version: ").append(version).append("\n\t");
				sb.append("cdate: ").append(TairUtil.formatDate(cdate))
						.append("\n\t");
				sb.append("mdate: ").append(TairUtil.formatDate(mdate))
						.append("\n\t");
				sb.append("edate: ")
						.append(edate > 0 ? TairUtil.formatDate(edate)
								: "NEVER").append("\n");
			}
			sb.append("\n");
			value = sb.toString();
		}

		return value;
	}

	public static String getNoHeaderRslt(Result<DataEntry> ret) {
		StringBuffer sb = new StringBuffer();
		sb.append("Result: [").append(ret.getRc().toString()).append("]\n");
		DataEntry value = ret.getValue();
		if (value != null) {
			if (value instanceof DataEntry) {
				sb.append("\t");
				sb.append("key: ").append(new String((byte[]) value.getKey()));
				sb.append(", value: ");
				byte[] val = (byte[]) value.getValue();
				if (6 == val.length && val[0] == 0 && val[1] == 22) {
					sb.append(String.valueOf(byteArrayToInt(val, 2)));
				} else {
					sb.append(new String(val));
				}
				sb.append(", version: ").append(value.getVersion())
						.append("\n\t");
				sb.append("cdate: ")
						.append(TairUtil.formatDate(value.getCreateDate()))
						.append("\n\t");
				sb.append("mdate: ")
						.append(TairUtil.formatDate(value.getModifyDate()))
						.append("\n\t");
				sb.append("edate: ")
						.append(value.getExpriedDate() > 0 ? TairUtil
								.formatDate(value.getExpriedDate()) : "NEVER")
						.append("\n");
				sb.append("\n");
			} else {
				sb.append("\tvalue: ").append(value);
			}
		}
		return sb.toString();
	}

	public static int byteArrayToInt(byte[] bytes, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = i * 8;
			value += (bytes[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	public static boolean isString(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			if (!Character.isLetterOrDigit(bytes[i]))
				return false;
		}
		return true;
	}
}
