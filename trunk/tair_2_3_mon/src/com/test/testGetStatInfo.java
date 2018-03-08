package com.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.etc.TairUtil;
import com.taobao.tair.impl.DefaultTairManager;

public class testGetStatInfo extends TestCase {
public void testGetStat(){
	DefaultTairManager tm = new DefaultTairManager();
	
	List<String> cs = new ArrayList<String>();

	cs.add("10.232.12.141:5198");

	tm.setConfigServerList(cs);
	tm.setGroupName("group_1");

	tm.init();

	Set<Integer> query = new HashSet<Integer>();
	query.add(TairConstant.Q_AREA_CAPACITY);//area(487)->9663676416
	query.add(TairConstant.Q_DATA_SEVER_INFO);//10.232.12.144:5191->alive
	query.add(TairConstant.Q_GROUP_INFO);//group_1->status OK
	query.add(TairConstant.Q_MIG_INFO);//isMigrating->false
	for(int qtype : query)
	{
		System.out.println("qtype="+qtype);
		Map<String,String> stat = null; 
		stat = tm.getStat(qtype, "group_1", 0);
		
		Set<String> keys = stat.keySet();
		for(String statkey : keys){
			System.out.printf("%s->%s\n", statkey ,stat.get(statkey));
		}
	}
	
	return ;
}
public void testGetStatDetail(){
	DefaultTairManager tm = new DefaultTairManager();
	
	List<String> cs = new ArrayList<String>();

	cs.add("10.232.12.141:5198");

	tm.setConfigServerList(cs);
	tm.setGroupName("group_1");

	tm.init();
	
	int c = 0;
	
	Map<String,String> stat = null; 
	Set<String> keys = null ;
	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
	stat = tm.getStat(TairConstant.Q_STAT_INFO, "group_1", 0);
	keys = stat.keySet();
	c=0;
	for(String statkey : keys){
		if(statkey.endsWith("putCount"))
			System.out.printf("%d--%s->%s\n", c++ ,statkey ,stat.get(statkey));
	}

	
	System.out.println("1----------------------------------------------------");
	stat = tm.getStat(TairConstant.Q_STAT_INFO, "group_1", TairUtil.hostToLong("10.232.12.141",5191));
	keys = stat.keySet();
	c=0;
	for(String statkey : keys){
		//if(statkey.endsWith("putCount"))
		if(statkey.startsWith("132"))
			System.out.printf("%d--%s->%s\n", c++ ,statkey ,stat.get(statkey));
	}
	
	System.out.println("2----------------------------------------------------");
	stat = tm.getStat(TairConstant.Q_STAT_INFO, "group_1", TairUtil.hostToLong("10.232.12.142",5191));
	keys = stat.keySet();
	c=0;
	for(String statkey : keys){
		//if(statkey.endsWith("putCount"))
		if(statkey.startsWith("132"))
			System.out.printf("%d--%s->%s\n", c++ ,statkey ,stat.get(statkey));
	}
	return ;
}


}	
