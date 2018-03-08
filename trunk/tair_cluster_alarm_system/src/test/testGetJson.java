package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tair.dataware.MetaDataRetrieve;

public class testGetJson extends TestCase {
	private static final Log log = LogFactory.getLog(testGetJson.class);
	
	public void testBaseGet() {

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(
				"http://110.75.14.61/tair2/group_comm/areainfo2json?start=1&limit=100");
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		String jsonData = "";
		if (entity != null) {
			try {
				jsonData = EntityUtils.toString(entity);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				entity.consumeContent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		JSONObject object;
		try {
			log.info(jsonData);
			
			object = new JSONObject(jsonData);
			int totalproperty = object.getInt("totalproperty");
			log.info(totalproperty);
			
			JSONArray root = object.getJSONArray("root");

			JSONObject e1 = root.getJSONObject(0);
			log.info(e1.getLong("area"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
/*
	public void testCreateJson() {
		JSONArray arr = new JSONArray();
		JSONObject obj = null;
		try {
			obj = new JSONObject();
			obj.put("name", "ff");
			obj.put("num", new Integer(100));
			obj.put("price", new Double(1000.21));
			obj.put("is_vip", new Boolean(true));
			obj.put("description", "a{a'a}aa");
			arr.put(obj);

			obj = new JSONObject();
			obj.put("name", "licy");
			obj.put("num", new Integer(101));
			obj.put("price", new Double(23.34));
			obj.put("is_vip", new Boolean(false));
			obj.put("description", "cc[{]cc");
			arr.put(obj);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.print(arr);
	}
*/
}
