package com.tair.dataware.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Expression;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tair.dataware.db.Groupinfo;

/**
 * Servlet implementation class SiteMap
 */
public class SiteMapWiki extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(SiteMapWiki.class);
	
	static Configuration  cfg;        
	static SessionFactory sFactory;  
	{
		cfg = new Configuration().configure();
		sFactory = cfg.buildSessionFactory();
	}
	
    public SiteMapWiki() {
        super();
    }

	@SuppressWarnings("deprecation")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String Domain = "";
		String IP = "";
		int Port = -1 ;
		String GroupName= "";
		try {
			Domain = request.getParameter("Domain");
			IP = request.getParameter("IP");
			GroupName = request.getParameter("GroupName");
			Port = Integer.parseInt(request.getParameter("Port"));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		try {
			Session session = sFactory.openSession() ;
				Transaction tx = session.beginTransaction();
				Criteria foo = session.createCriteria(Groupinfo.class);
				if(Domain!=null) foo.add(Expression.eq("domainA", Domain));
				if(IP!=null) foo.add(Expression.eq("ipa", IP));
				if(Port!=-1) foo.add(Expression.eq("portA", Port));
				if(GroupName!=null) foo.add(Expression.eq("groupName", GroupName));
				@SuppressWarnings("rawtypes")
				List allgroups = foo.list();
				tx.commit();
			session.close() ;
			
			if(allgroups.size()==1){
				Groupinfo bar = (Groupinfo)allgroups.get(0);
				if(bar.getWikiUrl()!=null){
					response.sendRedirect(bar.getWikiUrl());
					return ;
				}
			} else {
				log.error("can not find a fit link");
				response.sendRedirect("./");
				return ;
			}
		} catch (HibernateException e) {
			log.error(e.toString());
		}
		
		response.sendRedirect("./");
		return ;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
