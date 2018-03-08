package com.tair_2_2.statmonitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
					"com.tair_2_2.statmonitor.TairStatInfoReaderDeamon")
					.getResourceAsStream("MonitorArgs");
			Properties config = new Properties();

			config.load(RA);
			interval = Integer.parseInt(config.getProperty("interval"));
			RA.close();
		} catch (NumberFormatException e) {
			log.error(e.getMessage());
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	static Timer timer;
	static MonitorOutputRetrieve task;

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		timer = new Timer();
		try {
			task = new MonitorOutputRetrieve(interval);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		timer.schedule(task, 0, interval * 1000);
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

}
