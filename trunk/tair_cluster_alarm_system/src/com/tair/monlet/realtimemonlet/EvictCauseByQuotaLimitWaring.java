package com.tair.monlet.realtimemonlet;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tair.dataware.metadata.AreaStatistics;
import com.tair.dataware.metadata.DataServerStatistics;
import com.tair.dataware.metadata.RealTimeGroupInfo;
import com.tair.monlet.RealTimeMonlet;
import com.tair.notify.MailServer;

public class EvictCauseByQuotaLimitWaring implements RealTimeMonlet {
	private static final Log log = LogFactory.getLog(EvictCauseByQuotaLimitWaring.class);
	
	@Override
	public boolean doMonitor(RealTimeGroupInfo data) {
		int exceptioncount = 0;
		StringBuffer content = new  StringBuffer();
		for(Integer key : data.getAreaInfo().keySet()){
			AreaStatistics areainfo = data.getAreaInfo().get(key);
			long quota = areainfo.getQuota();
			long datasize = areainfo.getDataSize();
			long evictcount = areainfo.getEvictCount();
			long itemcount = areainfo.getItemCount();
			if(quota!=-1)
			{
				if(((datasize*1.0)/quota)>0.95 && ((evictcount*1.0)/itemcount)> 0.8){
					log.fatal("!!exception!! some dataserver may use up all quota");
					exceptioncount ++ ;
					content.append("<tr><td>area" + areainfo.getArea() + " may use up all quotas</td></tr>");
				}
			}
		}
		
		if(exceptioncount > 0){
			StringBuffer groupinfo = new  StringBuffer();
			groupinfo.append("<tr><td>"+ data.groupinfo.getDomainA() +"</td></tr>");
			groupinfo.append("<tr><td>"+ data.groupinfo.getPortA() +"</td></tr>");
			groupinfo.append("<tr><td>"+ data.groupinfo.getIpa() +"</td></tr>");
			groupinfo.append("<tr><td>"+ data.groupinfo.getTairRelease() +"</td></tr>");
			groupinfo.append("<tr><td>"+ data.groupinfo.getWikiUrl()+"</td></tr>");
			groupinfo.append("<tr><td>"+ data.groupinfo.getMonitorUrlbase() +"</td></tr>");
			
			String mail = "<table border=\"1\">"+groupinfo.toString()+content.toString()+"</table>";

			MailServer.RealtimeMail(mail);
		}
		return false;
	}

}
