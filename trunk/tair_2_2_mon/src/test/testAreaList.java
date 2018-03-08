package test;

import java.util.ArrayList;
import java.util.List;

import com.taobao.common.tair.impl.DefaultTairManager;

import junit.framework.TestCase;

public class testAreaList extends TestCase {

	
	public void testBase() 	{
		DefaultTairManager tm = new DefaultTairManager();

		List<String> cs = new ArrayList<String>();

		cs.add("10.232.12.140:5198");
		//cs.add("10.232.35.40:5199");
		//≤‚ ‘”√tdbm

		tm.setConfigServerList(cs);
		tm.setGroupName("group1");

		tm.init();
		/*
		System.out.println("--put--");	
		tm.put(0, "123", "qwertyuiop");
		System.out.println("--get--");	
		Result<DataEntry> tem = tm.get(0, "123");
		System.out.println(tem);
		*/
		System.out.println("--getstat--");	
		tm.getStat(0);
		List<Integer> tem = tm.getAreaList();
		for(Integer i : tem){
			System.out.println(i);
		}
		
	}
}
