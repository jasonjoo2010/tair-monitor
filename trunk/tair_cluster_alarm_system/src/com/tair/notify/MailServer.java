package com.tair.notify;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.tair.dataware.db.Maillist;
import com.tair.utils.GlobalClock;

public class MailServer {
	private MailServer(){
		
	}
	
	private static final Log log = LogFactory.getLog(MailServer.class);
	
	static Configuration  cfg;        
	static SessionFactory sFactory;  
	static{
		cfg = new Configuration().configure();
		sFactory = cfg.buildSessionFactory();
	}
	
	static final long minSendInterval = 10;
	static long lastMailTimeStamp ;
	static {
		lastMailTimeStamp = GlobalClock.getTimestamp()-minSendInterval-1;
	}

	public static boolean RealtimeMail(String content){
		if(GlobalClock.getTimestamp()-lastMailTimeStamp < minSendInterval){
			log.info("can not send notify mail due to a high frequency");
			return false;
		}
			
		
		HtmlEmail email = new HtmlEmail();
		
		try {
			email.setHostName("smtp.126.com");
			email.setSmtpPort(25);
			email.setAuthenticator(new DefaultAuthenticator("brianzf", "*****"));
			email.setTLS(true);
			email.setFrom("brianzf@126.com");
			email.setSubject("TestMail");
			email.setMsg("This is a mail from Tair Monitor");
			
			Session session = sFactory.openSession() ;
			Transaction tx = session.beginTransaction();
			Criteria foo = session.createCriteria(Maillist.class);
			@SuppressWarnings("rawtypes")
			List maillist = foo.list();
			tx.commit();
	    	session.close() ;
	    	for(Maillist ma : (List<Maillist>)maillist){
				email.addTo(ma.getMail());
	    	}
			
		} catch (EmailException e) {
			log.error(e.toString());
			return false;
		}

		try {
			// embed the image and get the content id
			//URL url = new URL("http://www.apache.org/images/asf_logo_wide.gif");
			//String cid = email.embed(url, "Apache logo");
			// set the html message
			email.setHtmlMsg("<html><body>"+content+"</body></html>");
			// set the alternative message
			email.setTextMsg("Your email client does not support HTML messages");
		} catch (EmailException e) {
			log.error(e.toString());
			return false;
		}

		try {
			// send the email
			email.send();
		} catch (EmailException e) {
			log.error(e.toString());
			return false;
		}
		lastMailTimeStamp = GlobalClock.getTimestamp();
		
		return true;
	}
}
