package test;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import com.tair.utils.GlobalClock;

public class testGlobalCloack {
	private static final Log log = LogFactory.getLog(testGlobalCloack.class);

	public static void main(String argv[]){
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = null ;
    	try {
    		sched = sf.getScheduler();
		} catch (SchedulerException e) {
			log.error(e.toString());
		}
		
		JobDetail job = newJob(GlobalClock.class).build();

		SimpleTrigger trigger = newTrigger().withSchedule(
				simpleSchedule().withIntervalInSeconds(2).repeatForever())
				.build();

		Date ft = null;
		try {
			ft = sched.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			log.error(e.toString());
		}

		log.info(job.getKey() + " will run at: " + ft + " and repeat: "
				+ trigger.getRepeatCount() + " times, every "
				+ trigger.getRepeatInterval() / 1000 + " seconds");
    	try {
			sched.start();
		} catch (SchedulerException e) {
			log.error(e.toString());
		}
		
		try {
			Thread.sleep(1024*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
