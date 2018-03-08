package com.tair.dataware.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tair.dataware.db.Groupinfo;

/**
 * Servlet implementation class groupinfo
 */
public class Groupinfo2json extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(Groupinfo2json.class);
	
	static Configuration  cfg;        
	static SessionFactory sFactory;  
	{
		cfg = new Configuration().configure();
		sFactory = cfg.buildSessionFactory();
	}
	
    public Groupinfo2json() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();
		
		int start = 0;
		int size = 0;
		try {
			start = Integer.parseInt(request.getParameter("start"));
			size = Integer.parseInt(request.getParameter("limit"));
		} catch (Exception e) {
			log.error(e.toString());
		}
		
		Session session = sFactory.openSession() ;
		
		Transaction tx = session.beginTransaction();
		@SuppressWarnings("rawtypes")
		List allgroups = session.createCriteria(Groupinfo.class)
			.list();
		tx.commit();
		
		JSONObject obj = new JSONObject();
		JSONArray arr = new JSONArray();
		try {
			obj.put("totalproperty", allgroups.size());
			
				for (Groupinfo tem : (List<Groupinfo>) allgroups){
					JSONObject foo = new JSONObject();
					foo.put("Domain", tem.getDomainA());
					foo.put("IP", tem.getIpa());
					foo.put("Port", tem.getPortA());
					foo.put("GroupName", tem.getGroupName());
					foo.put("Application", "");
					foo.put("Version", tem.getTairRelease());
					foo.put("Scene", tem.getScene());
					arr.put(foo);
				}
			
			obj.put("root", arr);
		} catch (JSONException e) {
			log.error(e.getMessage());
		}
		out.println(obj.toString());
		log.debug("select all groupinfo from table, the amount of groups is " + allgroups.size());
		session.close() ;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
