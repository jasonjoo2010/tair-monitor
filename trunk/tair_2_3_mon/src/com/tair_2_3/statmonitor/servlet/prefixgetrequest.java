package com.tair_2_3.statmonitor.servlet;

import java.io.IOException;
import java.io.Serializable;

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
 * Servlet implementation class prefixgetrequest
 */
public class prefixgetrequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(prefixgetrequest.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public prefixgetrequest() {
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
		String Pkey = null, Skey = null;
		int PkeyType = -1, SkeyType = -1;
		String Value = "invalid input, please check !!!!";

		log.info(MonitorUtil.LogParameter(request));

		try {
			Area = Integer.parseInt(request.getParameter("Area"));
			Pkey = Escape.unescape(request.getParameter("Pkey"));
			PkeyType = Integer.parseInt(request.getParameter("PkeyType"));
			Skey = Escape.unescape(request.getParameter("Skey"));
			SkeyType = Integer.parseInt(request.getParameter("SkeyType"));
		} catch (Exception e) {
			log.error(MonitorUtil.GeneralLog(request, e.toString()));
			result = false;
		}

		if (result) {
			Value = "key type error maybe..";
			try {
				Serializable op = MonitorUtil.GetObject(PkeyType, Pkey);
				Serializable os = MonitorUtil.GetObject(SkeyType, Skey);
				if (null != op && null != os) {
					Result<DataEntry> ret = TairStatInfoReaderDeamon.getTask().tm
							.prefixGet(Area, op, os);
					Value = MonitorUtil.FormalizeGetResult(ret);
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
		foo.put("Pkey", Escape.unescape(request.getParameter("Pkey")));
		foo.put("PkeyType", request.getParameter("PkeyType"));
		foo.put("Skey", Escape.unescape(request.getParameter("Skey")));
		foo.put("SkeyType", request.getParameter("SkeyType"));
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
