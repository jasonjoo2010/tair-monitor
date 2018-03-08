package com.tair.monlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tair.monlet.realtimemonlet.CrashWarning;
import com.tair.monlet.realtimemonlet.EvictCauseByQuotaLimitWaring;

/**
 * Application Lifecycle Listener implementation class MonletTriggerDeamon
 *
 */ 
public class MonletTriggerDeamon implements ServletContextListener {
	private static final Log log = LogFactory.getLog(MonletTriggerDeamon.class);
    /**
     * Default constructor. 
     */
    public MonletTriggerDeamon() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
        log.info("!!!Starting MonletTriggerDeamon!!!");
        log.info("is registion for monlet 1 successful? "+
        		RealTimeMonitorContainer.registerMonlet(1, new CrashWarning(), false));
        log.info("is registion for monlet 2 successful? "+
        		RealTimeMonitorContainer.registerMonlet(2, new EvictCauseByQuotaLimitWaring(), false));
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}
