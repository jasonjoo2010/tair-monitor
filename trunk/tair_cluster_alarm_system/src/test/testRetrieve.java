package test;

import java.util.HashMap;
import java.util.Map;

import com.tair.dataware.MetaDataRetrieve;
import com.tair.dataware.db.Groupinfo;
import com.tair.dataware.metadata.AreaStatistics;
import com.tair.dataware.metadata.DataServerStatistics;
import com.tair.dataware.metadata.RealTimeGroupInfo;

import junit.framework.TestCase;

public class testRetrieve extends TestCase {
	public void testRetrieve(){
		
		MetaDataRetrieve ret = new MetaDataRetrieve();
		RealTimeGroupInfo state = new RealTimeGroupInfo(0,new HashMap<String,DataServerStatistics>(), new HashMap<Integer, AreaStatistics>(),new Groupinfo());
		ret.RetrieveFromAreainfo2json("http://110.75.14.61/tair2/group_comm", state );
	}
	
}
