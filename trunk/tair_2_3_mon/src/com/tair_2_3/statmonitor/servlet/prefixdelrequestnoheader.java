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
import com.taobao.tair.ResultCode;
import com.taobao.tair.json.JSONArray;
import com.taobao.tair.json.JSONObject;

/**
 * Servlet implementation class prefixgetrequestnoheader
 */
public class prefixdelrequestnoheader extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(prefixdelrequestnoheader.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public prefixdelrequestnoheader() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean result = true;
		int Area = -1 ;
		String Pkey = null, Skey = null;
		String Value = "invalid input, please check !!!!";

		log.info(MonitorUtil.LogParameter(request));
		 
		try {
			Area = Integer.parseInt(request.getParameter("Area"));
			Pkey = Escape.unescape(request.getParameter("Pkey"));
			Skey = Escape.unescape(request.getParameter("Skey"));
		} catch (Exception e) {
			log.error(e.toString());
			result = false;
		}
		
		if(result){
			Value = "key type error maybe..";
			ResultCode rc = null;
			try {
				if (TairStatInfoReaderDeamon.getTask().tmNoHeader != null) {
					rc = TairStatInfoReaderDeamon.getTask().tmNoHeader.prefixDelete(Area, Pkey, Skey);
					Value = rc.toString();
				} else {
					Value = "the monitor of this cluster doesn't support c++ query, please contace ." + MonitorUtil.ADMIN;
				}
			}  catch (Throwable e) {
				log.error(MonitorUtil.GeneralLog(request, e.toString()));
			}
			
		}

		JSONObject res = new JSONObject();
		res.put("totalProperty", result?1:0);
		res.put("successProperty",result);
		JSONArray root = new JSONArray();
		JSONObject foo = new JSONObject();

		foo.put("Area", request.getParameter("Area"));
		foo.put("Pkey", request.getParameter("Pkey"));
		foo.put("Skey", request.getParameter("Skey"));
		foo.put("Result", Value);

		root.add(foo);
		res.put("root", root);

		response.getOutputStream().println(
				MonitorUtil.FormalizeReturnMesg(res.toString()));	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
