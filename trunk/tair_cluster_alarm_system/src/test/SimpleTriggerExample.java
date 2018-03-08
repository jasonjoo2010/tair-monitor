/* 
 * Copyright 2005 - 2009 Terracotta, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 * 
 */

package test;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Example will demonstrate all of the basics of scheduling capabilities
 * of Quartz using Simple Triggers.
 * 
 * @author Bill Kratzer
 */
public class SimpleTriggerExample {

    
    public void run() throws Exception {
        Logger log = LoggerFactory.getLogger(SimpleTriggerExample.class);

        log.info("------- Initializing -------------------");

        // First we must get a reference to a scheduler
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        log.info("------- Initialization Complete --------");

        log.info("------- Scheduling Jobs ----------------");
        // jobs can be scheduled before sched.start() has been called

        // get a "nice round" time a few seconds in the future...
        Date startTime = DateBuilder.nextGivenSecondDate(null, 15);

        // job6 will run indefinitely, every X seconds
        JobDetail job = newJob(SimpleJob.class)
            .withIdentity("job6", "group1")
            .build();

        SimpleTrigger trigger = newTrigger()
            .withIdentity("trigger6", "group1")
            .startAt(startTime)
            .withSchedule(simpleSchedule()
                    .withIntervalInSeconds(2)
                    .repeatForever())
            .build();
        
        trigger.getJobDataMap().put("keyA", new Integer(128));
        
        Date ft = sched.scheduleJob(job, trigger);
        log.info(job.getKey() +
                " will run at: " + ft +  
                " and repeat: " + trigger.getRepeatCount() + 
                " times, every " + trigger.getRepeatInterval() / 1000 + " seconds");

        log.info("------- Starting Scheduler ----------------");

        // All of the jobs have been added to the scheduler, but none of the jobs
        // will run until the scheduler has been started
        sched.start();

               
        log.info("------- Waiting minutes... ------------");
        try {
            // wait five minutes to show jobs
            Thread.sleep(3000L * 1000L); 
            // executing...
        } catch (Exception e) {
        }

        log.info("------- Shutting Down ---------------------");

        sched.shutdown(true);

        log.info("------- Shutdown Complete -----------------");

        // display some stats about the schedule that just ran
        SchedulerMetaData metaData = sched.getMetaData();
        log.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");

    }

    public static void main(String[] args) throws Exception {

        SimpleTriggerExample example = new SimpleTriggerExample();
        example.run();

    }

}
