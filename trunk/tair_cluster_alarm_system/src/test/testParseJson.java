package test;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import junit.framework.TestCase;

public class testParseJson extends TestCase {
	public void testParseJson() throws JSONException{
		
		String jsonData = "{totalproperty:131,root:[{area:1,dataSize:442253,evictCount:172,getCount:86,hitRate:0.79 ,putCount:13,hitCount:68,itemCount:86,removeCount:0,useSize:468912,quota:-1}]}";
		
		JSONObject object = new JSONObject(jsonData);
		
        String jArray  = object.getString("totalproperty");
        System.out.println(jArray);
        
        JSONArray root = object.getJSONArray("root");
        System.out.println("array length = "+root.length());
        System.out.println(root.toString());
        
        JSONObject e1 = root.getJSONObject(0);
        System.out.println(e1.toString());
        System.out.println(e1.getLong("area"));
		
	}
}
