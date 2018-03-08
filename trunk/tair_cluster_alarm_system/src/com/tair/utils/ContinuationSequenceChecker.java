package com.tair.utils;

import org.apache.commons.logging.Log;

public class ContinuationSequenceChecker {
	private Long Sequence ;
	private final Log log ;
	
	public ContinuationSequenceChecker(Log log,long Sequence){
		this.log = log ;
		this.Sequence = Sequence;
	}
	
	public void UpdateAndCheck(Long now){
		if(Sequence + 1 < now){
			log.debug("Sequence Order is keeped");
		} else if(Sequence + 1 == now){
			log.error("Sequence Spans " + (now-Sequence) + (now-Sequence==1?"space":"spaces"));
		}
	}
	
}
