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
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.json.JSONArray;
import com.taobao.tair.json.JSONObject;

/**
 * Servlet implementation class getrequestnoheader
 */
public class getrequestnoheader extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(getrequestnoheader.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public getrequestnoheader() {
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
		String Key = null;
		String Value = "invalid input, please check !!!!";

		log.info(MonitorUtil.LogParameter(request));

		try {
			Area = Integer.parseInt(request.getParameter("Area"));
			Key = Escape.unescape(request.getParameter("Key"));
		} catch (Exception e) {
			log.error(MonitorUtil.GeneralLog(request, e.toString()));
			result = false;
		}


		if (result) {
			Value = "key type error maybe..";
			try {
				if (TairStatInfoReaderDeamon.getTask().tmNoHeader != null) {
					Result<DataEntry> ret = TairStatInfoReaderDeamon.getTask().tmNoHeader.get(Area, Key);
					Value = MonitorUtil.getNoHeaderRslt(ret);
				} else {
					Value = "the monitor of this cluster doesn't support c++ query, please contact " + MonitorUtil.ADMIN;
				}
			} catch (Throwable e) {
				log.error(MonitorUtil.GeneralLog(request, e.toString()));
			}
		}

		JSONObject res = new JSONObject();
		res.put("totalProperty", result ? 1 : 0);
		res.put("successProperty", result);
		JSONArray root = new JSONArray();
		JSONObject foo = new JSONObject();
		foo.put("Area", request.getParameter("Area"));
		foo.put("Key", Escape.unescape(request.getParameter("Key")));
		foo.put("Value", Value);
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
