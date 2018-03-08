package com.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.etc.TairUtil;
import com.taobao.tair.impl.DefaultTairManager;

import junit.framework.TestCase;

public class testGetMigInfo extends TestCase {
	public void testMigInfo() {
		DefaultTairManager tm = new DefaultTairManager();

		List<String> cs = new ArrayList<String>();

		cs.add("10.13.117.11:5198");
		cs.add("10.13.117.11:5198");
		tm.setConfigServerList(cs);
		tm.setGroupName("group_1");

		tm.init();
		
		int sequence = 0;
		while(true){
			//System.out.println("miginfo "+ sequence++ +"\n");
			Map<String,String> stat = null; 
			stat = tm.getStat(TairConstant.Q_MIG_INFO, "group_1", TairUtil.hostToLong("10.232.35.40", 8198));
			
			Set<String> keys = stat.keySet();
			for(String statkey : keys){
				if(!stat.get(statkey).equals("false"))
					System.out.printf("%s->%s\n", statkey ,stat.get(statkey));
			}
		}
	}
}
