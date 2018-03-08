package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.taobao.common.tair.CTDBMStat;
import com.taobao.common.tair.DataEntry;
import com.taobao.common.tair.Result;
import com.taobao.common.tair.impl.DefaultTairManager;
import junit.framework.TestCase;

public class testStat extends TestCase {
	
	public static int TDBM_STAT_TOTAL = 1;
	public static int TDBM_STAT_AREA = 4;
	
	public void testBase() 	{
		DefaultTairManager tm = new DefaultTairManager();

		List<String> cs = new ArrayList<String>();

		cs.add("10.232.12.140:5198");
		//cs.add("10.232.35.40:5199");
		//≤‚ ‘”√tdbm

		tm.setConfigServerList(cs);
		tm.setGroupName("group1");
		
		tm.init();
		tm.get(56565656, "dede");
		System.out.println("--dedededede --");	
		/*
		
		tm.put(0, "123", "qwertyuiop");
		System.out.println("--get--");	
		Result<DataEntry> tem = tm.get(0, "123");
		System.out.println(tem);
		*/
		/*
		System.out.println("--getstat--");	
		Map<String, CTDBMStat> foo = tm.getStat(0);
		for(String bar : foo.keySet()){
			System.out.println(bar);
			foo.get(bar);
		}
		*/
	}
	
	
	public static void main(String []args) {
		new testStat().testBase();
	}
}
