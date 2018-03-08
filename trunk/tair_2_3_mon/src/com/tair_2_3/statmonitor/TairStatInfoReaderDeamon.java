package com.tair_2_3.statmonitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tair_2_3.statmonitor.comm.MonitorAcl;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.etc.TairUtil;

/**
 * Application Lifecycle Listener implementation class TairStatInfoReaderDeamon
 * 
 */
public class TairStatInfoReaderDeamon implements ServletContextListener {
	private static final Log log = LogFactory
		.getLog(TairStatInfoReaderDeamon.class);
	
	private long interval;
	
	public TairStatInfoReaderDeamon()  {
		try {
			InputStream RA = Class.forName(
					"com.tair_2_3.statmonitor.TairStatInfoReaderDeamon")
					.getResourceAsStream("MonitorArgs");
			Properties config = new Properties();

			config.load(RA);
			interval = Integer.parseInt(config.getProperty("interval"));
			RA.close();
		} catch (NumberFormatException e) {
			log.error(e.toString());
		} catch (ClassNotFoundException e) {
			log.error(e.toString());
		} catch (IOException e) {
			log.error(e.toString());
		}
	}

	static Timer timer;
	static MonitorOutputRetrieve task;
	static MonitorAcl aclModule;
	
	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			aclModule = new MonitorAcl();
			task = new MonitorOutputRetrieve();
			if (task.isInited()) {
				timer = new Timer();
				timer.schedule(task, 0, interval * 1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		task.cancel();
		timer.cancel();
	}

	public static MonitorOutputRetrieve getTask() {
		return task;
	}

	public static MonitorAcl getAclModule() {
		return aclModule;
	}
}
