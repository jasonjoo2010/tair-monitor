package com.tair_2_2.statmonitor.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tair_2_2.statmonitor.TairStatInfoReaderDeamon;
import com.taobao.common.tair.DataEntry;
import com.taobao.common.tair.Result;
import com.taobao.common.tair.etc.TairConstant;



/**
 * Servlet implementation class getrequest
 */
public class getrequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory
	.getLog(getrequest.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getrequest() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean result = true;
		
		int Area = -1 ;
		String Key = null ;
		int Type = -1;
		String Value = null ;
		
		
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			Object o = e.nextElement();
			log.info((String) o + ":"
					+ request.getParameter((String) o));
		}
		 
		 
		try {
			Area = Integer.parseInt(request.getParameter("Area"));
			Type = Integer.parseInt(request.getParameter("Type"));
			Key = request.getParameter("Key");
		} catch (Exception e) {
			log.error(e.toString());
			result = false;
		}
		
		JSONObject res = null;
		try {
			res = new JSONObject();
			res.put("totalProperty", result?1:0);
			res.put("successProperty",result);
			JSONArray root = new JSONArray();
			if(result){
				Result<DataEntry> ret = null;
				try {
					switch (Type){
					case TairConstant.TAIR_STYPE_INT : 
						ret = TairStatInfoReaderDeamon.getTask().tm.get(Area,  new Integer(Key)); 
						break;
					case TairConstant.TAIR_STYPE_STRING : 
						ret = TairStatInfoReaderDeamon.getTask().tm.get(Area,  Key); 
						break;
					case TairConstant.TAIR_STYPE_BOOL : 
						ret = TairStatInfoReaderDeamon.getTask().tm.get(Area,  new Boolean(Key)); 
						break;
					case TairConstant.TAIR_STYPE_LONG : 
						ret = TairStatInfoReaderDeamon.getTask().tm.get(Area,  new Long(Key)); 
						break;
					case TairConstant.TAIR_STYPE_DATE : 
						ret = TairStatInfoReaderDeamon.getTask().tm.get(Area,  new Date(Key)); 
						break;
					case TairConstant.TAIR_STYPE_BYTE : 
						ret = TairStatInfoReaderDeamon.getTask().tm.get(Area,  new Byte(Key)); 
						break;
					case TairConstant.TAIR_STYPE_FLOAT : 
						ret = TairStatInfoReaderDeamon.getTask().tm.get(Area,  new Float(Key)); 
						break;
					case TairConstant.TAIR_STYPE_DOUBLE : 
						ret = TairStatInfoReaderDeamon.getTask().tm.get(Area,  new Double(Key)); 
						break;
					case TairConstant.TAIR_STYPE_BYTEARRAY : 
						ret = TairStatInfoReaderDeamon.getTask().tm.get(Area,  Key.getBytes()); 
						break;
					}
				} catch (NumberFormatException e) {
					log.error(e.toString());
					ret = null;
				}
				
					if(ret == null){
						Value="result is null";
					}else{
						Value=ret.toString();
					}
					JSONObject foo = new JSONObject();
					foo.put("Area", "");
					foo.put("Key", "Key");
					foo.put("Value", Value);
					root.put(foo);
				
			}
			res.put("root", root);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.getOutputStream().print(res.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
