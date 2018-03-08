package com.tair_2_3.statmonitor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tair_2_3.statmonitor.TairStatInfoReaderDeamon;
import com.tair_2_3.statmonitor.comm.MonitorUtil;
import com.taobao.tair.ResultCode;
import com.taobao.tair.json.JSONArray;
import com.taobao.tair.json.JSONObject;

/**
 * Servlet implementation class cleararearequest
 */
public class cleararearequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(cleararearequest.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public cleararearequest() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		boolean result = true;
		int Area = -1;
		String Passwd = null;
		String Result = "invalid input, please check !!!!";

		log.info(MonitorUtil.LogParameter(request));

		try {
			Area = Integer.parseInt(request.getParameter("Area"));
			Passwd = Escape.unescape(request.getParameter("Passwd"));
		} catch (Exception e) {
			log.error(MonitorUtil.GeneralLog(request, e.toString()));
			result = false;
		}

		if (result) {
			ResultCode ret = null;
			if (TairStatInfoReaderDeamon.getTask().tmAdmin != null) {
				// this is test cluster, no need passwd
				if (TairStatInfoReaderDeamon.getTask().tmRdb != null) {
					ret = TairStatInfoReaderDeamon.getTask().tmRdb
							.lazyRemoveArea(Area);
					Result = "the result of clear area " + Area + " is "
							+ ret.toString();
				} else {
					boolean clearSuccess = TairStatInfoReaderDeamon.getTask().tmAdmin
							.removeNamespace(Area);
					Result = "the result of clear area " + Area + " is "
							+ (clearSuccess ? "ok" : "fail");
				}
			} else if (TairStatInfoReaderDeamon.getTask().tmRdb != null) {
				if (Passwd.equals(TairStatInfoReaderDeamon.getTask().passwd)) {
					ret = TairStatInfoReaderDeamon.getTask().tmRdb
							.lazyRemoveArea(Area);
					Result = "the result of clear area " + Area + " is "
							+ ret.toString();
				} else {
					Result = "passwd error, the system has already record your ip for the sake of security...";
				}
			} else {
				Result = "this is neither a rdb cluster nor test cluster, so can not use clear area";
			}
		}

		JSONObject res = new JSONObject();
		res.put("totalProperty", result ? 1 : 0);
		res.put("successProperty", result);
		JSONArray root = new JSONArray();
		JSONObject foo = new JSONObject();
		foo.put("Area", request.getParameter("Area"));
		foo.put("Passwd", "");
		foo.put("Result", Result);
		root.add(foo);
		res.put("root", root);

		response.getOutputStream().println(
				MonitorUtil.FormalizeReturnMesg(res.toString()));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
