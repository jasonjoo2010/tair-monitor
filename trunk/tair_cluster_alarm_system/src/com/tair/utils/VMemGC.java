package com.tair.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class VMemGC implements Job {
	private static final Log log = LogFactory.getLog(VMemGC.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		long mem = Runtime.getRuntime().freeMemory();
		log.info("free memory is " + mem/1024 + "k"); 
		System.gc(); 
	}

}
