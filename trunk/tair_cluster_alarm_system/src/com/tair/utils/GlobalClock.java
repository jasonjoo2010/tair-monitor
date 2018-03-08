package com.tair.utils;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import com.tair.dataware.DataWare;
import com.tair.dataware.MetaDataRetrieve;
import com.tair.dataware.metadata.RealTimeGroupInfoCollector;

public class GlobalClock implements Job{
	
	private static final Log log = LogFactory.getLog(GlobalClock.class);

    private static long timestamp;
	static{
		timestamp = System.currentTimeMillis()/(1000*60);
	}
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		long foo = System.currentTimeMillis()/(1000*60);
		if(foo - 1 == timestamp){
			timestamp = foo;
			log.info("synchronization global clock is updated to "+ timestamp);

		} else{
			log.error("can not keep time sequence in order, " +
					"oritimestamp is " +timestamp +" current one is" + foo);
			timestamp = foo;
		}
		return ;
	}
	
	public static long getTimestamp() {
		return timestamp;
	}
	
	
}
