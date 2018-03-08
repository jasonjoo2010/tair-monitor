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
import com.taobao.tair.etc.TairUtil;
import com.taobao.tair.json.JSONArray;
import com.taobao.tair.json.JSONObject;

/**
 * Servlet implementation class getdsrequest
 */
public class getdsrequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(getdsrequest.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public getdsrequest() {
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
		String Key = null;
		int Type = -1;
		int IsPrefix = 1;
		String DataServer = "invalid input, please check !!!!";

		log.info(MonitorUtil.LogParameter(request));

		try {
			Type = Integer.parseInt(request.getParameter("Type"));
			Key = Escape.unescape(request.getParameter("Key"));
			IsPrefix = Integer.parseInt(request.getParameter("IsPrefix"));
		} catch (Exception e) {
			log.error(MonitorUtil.GeneralLog(request, e.toString()));
			result = false;
		}

		if (result) {
			DataServer = "key type error maybe..";
			try {
				Serializable obj = MonitorUtil.GetObject(Type, Key);
				if (null != obj) {
					int bucket = TairStatInfoReaderDeamon.getTask().tm.getBucketOfKey(obj, IsPrefix == 1);
					long serverIp = TairStatInfoReaderDeamon.getTask().tm.getConfigServer().getServer(bucket, true);
					DataServer = TairUtil.idToAddress(serverIp);
					log.info(MonitorUtil.GeneralLog(request, "obj-class("
							+ obj.getClass().getName() + "), content(" + obj
							+ "), bucket(" + bucket + "), serverIp("
							+ DataServer + ")"));
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
		foo.put("Key", Escape.unescape(request.getParameter("Key")));
		foo.put("Type", request.getParameter("Type"));
		foo.put("IsPrefix", request.getParameter("IsPrefix"));
		foo.put("DataServer", DataServer);
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
