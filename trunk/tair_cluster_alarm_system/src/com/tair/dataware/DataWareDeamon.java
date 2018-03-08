package com.tair.dataware;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Expression;
import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import com.tair.dataware.db.Groupinfo;
import com.tair.dataware.metadata.RealTimeGroupInfoCollector;
import com.tair.utils.GlobalClock;
import com.tair.utils.VMemGC;

/**
 * Application Lifecycle Listener implementation class DataWareDeamon
 *
 */
public class DataWareDeamon implements ServletContextListener {
	private static final Log log = LogFactory.getLog(DataWareDeamon.class);
	
	static Configuration  cfg;
	static SessionFactory sFactory;  
	static{
		cfg = new Configuration().configure();
		sFactory = cfg.buildSessionFactory();
	}
    static SchedulerFactory sf ;
    static Scheduler sched;
    static{
    	sf = new StdSchedulerFactory();
    	try {
			sched = sf.getScheduler();
		} catch (SchedulerException e) {
			log.error(e.toString());
		}
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
	public void contextInitialized(ServletContextEvent arg0) {
		
        // globalclock will run indefinitely, every 60 seconds
        JobDetail job = newJob(GlobalClock.class)
            .build();
     
        SimpleTrigger trigger = newTrigger()
            .withSchedule(simpleSchedule()
                    .withIntervalInSeconds(60)
                    .repeatForever())
            .build();
        
        Date ft = null;
		try {
			ft = sched.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			log.error(e.toString());
		}
		
		log.info(job.getKey() +
                " will run at: " + ft +  
                " and repeat: " + trigger.getRepeatCount() + 
                " times, every " + trigger.getRepeatInterval() / 1000 + " seconds");
		
        // VMemGC will run indefinitely, every 60 seconds
        job = newJob(VMemGC.class)
            .build();
        trigger = newTrigger()
            .withSchedule(simpleSchedule()
                    .withIntervalInSeconds(60)
                    .repeatForever())
            .build();
		try {
			ft = sched.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			log.error(e.toString());
		}
		log.info(job.getKey() +
                " will run at: " + ft +  
                " and repeat: " + trigger.getRepeatCount() + 
                " times, every " + trigger.getRepeatInterval() / 1000 + " seconds");
		
		//MetaDataRetrieve will run indefinitely, every 60 seconds
    	Session session = sFactory.openSession() ;
    	
		Transaction tx = session.beginTransaction();
		Criteria foo = session.createCriteria(Groupinfo.class);
		@SuppressWarnings("rawtypes")
		List allgroups = foo.list();
		tx.commit();
    	session.close() ;
    	
    	for(Groupinfo bar : (List<Groupinfo>) allgroups){
    		int gid = bar.getGid();
    		String MonitorUrlbase  = bar.getMonitorUrlbase();
    		log.info("Initialize Dataware for Group " + gid + ", Monitor URL is " + MonitorUrlbase);
    		DataWare.putGroupInfoCollector(gid, new RealTimeGroupInfoCollector(bar));
    		
    		Date startTime = DateBuilder.nextGivenMinuteDate(new Date(), 1);
            // job will run indefinitely, every X seconds
            job = newJob(MetaDataRetrieve.class)
                .withIdentity("retrieveJob"+gid, "DataWare")
                .build();
         
            trigger = newTrigger()
                .withIdentity("retrieveJob"+gid, "DataWare")
                .startAt(startTime)
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(60)
                        .repeatForever())
                .build();
            
            RealTimeGroupInfoCollector foobar = 
            	DataWare.getGroupInfoCollector(bar.getGid());
            trigger.getJobDataMap().put("gid", bar.getGid());
            trigger.getJobDataMap().put("urlbase", bar.getMonitorUrlbase());
            trigger.getJobDataMap().put("data", foobar);
            
			try {
				ft = sched.scheduleJob(job, trigger);
			} catch (SchedulerException e) {
				log.error(e.toString());
			}
            log.info(job.getKey() +
                    " will run at: " + ft +  
                    " and repeat: " + trigger.getRepeatCount() + 
                    " times, every " + trigger.getRepeatInterval() / 1000 + " seconds");
    	}
    	try {
			sched.start();
		} catch (SchedulerException e) {
			log.error(e.toString());
		}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
    	try {
			sched.shutdown(true);
	    	SchedulerMetaData metaData = sched.getMetaData();
	        log.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
    	} catch (SchedulerException e) {
			log.error(e.toString());
		}
    }
	
}
