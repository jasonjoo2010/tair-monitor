package com.tair.monlet.periodmonlet;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tair.dataware.metadata.AreaStatistics;
import com.tair.dataware.metadata.DataServerStatistics;
import com.tair.dataware.metadata.RealTimeGroupInfo;
import com.tair.monlet.RealTimeMonlet;
import com.tair.notify.MailServer;

public class SumUp implements RealTimeMonlet {
	private static final Log log = LogFactory.getLog(SumUp.class);
	
	@Override
	public boolean doMonitor(RealTimeGroupInfo data) {
		int exceptioncount = 0;
		StringBuffer content = new  StringBuffer();
		for(String key : data.getDSInfo().keySet()){
			DataServerStatistics foo =data.getDSInfo().get(key);
			if(!foo.getStat().equals("alive"))
			{
				log.fatal("!!exception!! some dataserver may be down");
				exceptioncount ++ ;
				content.append("<tr><td>Dataserver"+foo.getIp()+"</td><td>"+foo.getPort()+"</td>" +
						"<td>"+foo.getStat()+"</td></tr>");
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
