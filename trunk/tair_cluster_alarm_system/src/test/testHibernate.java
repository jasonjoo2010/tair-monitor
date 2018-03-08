package test;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.tair.dataware.db.Groupinfo;

public class testHibernate extends TestCase {
	private static final Log log = LogFactory
		.getLog(testHibernate.class);
	public void testHi()
	{
			Configuration cfg = new Configuration().configure() ;        
			SessionFactory  sFactory = cfg.buildSessionFactory() ;        
			
			Session session = sFactory.openSession() ;
			
			Transaction tx = session.beginTransaction();
			Groupinfo info = new Groupinfo();
			info.setDomainA("taobao.com");
			info.setPortA(5198);
			session.save(info);
			System.out.println(info.toString()) ;
			tx.commit();
			
			session.close() ;

	}
}
