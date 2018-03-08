package com.tair.dataware;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.tair.dataware.metadata.AreaStatistics;
import com.tair.dataware.metadata.DataServerStatistics;
import com.tair.dataware.metadata.RealTimeGroupInfo;
import com.tair.dataware.metadata.RealTimeGroupInfoCollector;
import com.tair.monlet.RealTimeMonitorContainer;
import com.tair.monlet.RealTimeMonlet;
import com.tair.utils.GlobalClock;

public class MetaDataRetrieve implements Job{
	private static final Log log = LogFactory.getLog(MetaDataRetrieve.class);
	private HttpClient httpclient;
	public MetaDataRetrieve(){
		httpclient = new DefaultHttpClient();
	}
	
	@Override
	public void execute(JobExecutionContext Context) throws JobExecutionException {
		Integer gid = null ;
		RealTimeGroupInfoCollector data = null;
		String urlbase = null; 
		try {
			gid = (Integer)Context.getTrigger().getJobDataMap().get("gid");
			data = (RealTimeGroupInfoCollector)Context.getTrigger().getJobDataMap().get("data");
			urlbase = (String)Context.getTrigger().getJobDataMap().get("urlbase");
		} catch (Exception e) {
			log.error(e.toString());
			return ;
		}
		long clockstamp = GlobalClock.getTimestamp();
		log.info("MetaDataRetrieve " + this.toString() + "is runing " +
				" gid =" + gid + 
				" urlbase =" + urlbase + 
				" clockstamp=" + clockstamp);
		if(urlbase.endsWith("/"))
			urlbase = urlbase.substring(0,urlbase.length()-1);
		urlbase.trim();
		
		RealTimeGroupInfo tem = data.getRealTimeGroupInfo(clockstamp);
		if(tem==null){
			tem = new RealTimeGroupInfo(clockstamp,
					new HashMap<String,DataServerStatistics>(), 
					new HashMap<Integer, AreaStatistics>(),
					data.groupinfo);
			data.setRealTimeGroupInfo(clockstamp, tem);
		}
		RetrieveMetaInfo(urlbase,tem);
		
		for( Integer id : RealTimeMonitorContainer.RealtimeMonlets.keySet()){
			RealTimeMonlet mon = (RealTimeMonlet)RealTimeMonitorContainer.RealtimeMonlets.get(id);
			log.info("Processing Monlet " + mon.toString());
			mon.doMonitor(tem);
		}
		
	}
	public void RetrieveMetaInfo(String URLBase,RealTimeGroupInfo state){
		RetrieveFromAreainfo2json(URLBase,state);
		RetrieveFromNodestatistics(URLBase,state);
		RetrieveFromDateservernodeinfo2json(URLBase,state);
	}
	
	public void RetrieveFromAreainfo2json(String URLBase,RealTimeGroupInfo state)
	{
		HttpGet httpget = new HttpGet(
				URLBase+"/areainfo2json?start=1&limit=1025");
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			log.error(e.toString());
		} catch (IOException e) {
			log.error(e.toString());
		}
		HttpEntity entity = response.getEntity();
		String jsonData = "";
		if (entity != null) {
			try {
				jsonData = EntityUtils.toString(entity);
			} catch (ParseException e) {
				log.error(e.toString());
			} catch (IOException e) {
				log.error(e.toString());
			}
			try {
				entity.consumeContent();
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
		try {
			JSONObject object = new JSONObject(jsonData);
			int totalproperty = object.getInt("totalproperty");

			JSONArray root = object.getJSONArray("root");
			for(int i =0 ;i<root.length();++i){
				JSONObject instance = root.getJSONObject(i);
				AreaStatistics ai = null ;
				int area = instance.getInt("area");
				if(state.getAreaInfo().containsKey(area)){
					ai = state.getAreaInfo().get(area);
				} else {
					ai = new AreaStatistics();
					ai.setArea(area);
					state.getAreaInfo().put(area, ai);
				}
				ai.setDataSize(instance.getLong("dataSize"));
				ai.setUseSize(instance.getLong("useSize"));
				ai.setQuota(instance.getLong("quota"));
				ai.setItemCount(instance.getLong("itemCount"));
				
				ai.setEvictCount(instance.getLong("evictCount"));
				ai.setGetCount(instance.getLong("getCount"));
				ai.setHitCount(instance.getLong("hitCount"));
				ai.setPutCount(instance.getLong("putCount"));
				ai.setRemoveCount(instance.getLong("removeCount"));
			}
		} catch (JSONException e) {
			log.error(e.toString());
		}
	}
	
	public void RetrieveFromNodestatistics(String URLBase,RealTimeGroupInfo state){
		HttpGet httpget = new HttpGet(
				URLBase+"/nodestatistics?start=1&limit=1025");
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			log.error(e.toString());
		} catch (IOException e) {
			log.error(e.toString());
		}
		HttpEntity entity = response.getEntity();
		String jsonData = "";
		if (entity != null) {
			try {
				jsonData = EntityUtils.toString(entity);
			} catch (ParseException e) {
				log.error(e.toString());
			} catch (IOException e) {
				log.error(e.toString());
			}
			try {
				entity.consumeContent();
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
		try {
			JSONObject object = new JSONObject(jsonData);
			int totalproperty = object.getInt("totalproperty");

			JSONArray root = object.getJSONArray("root");
			for(int i =0 ;i<root.length();++i){
				JSONObject instance = root.getJSONObject(i);
				String nodeidentifer = (String) instance.get("nodeidentifer");
				if(nodeidentifer.equals("total")) 
					continue;
				DataServerStatistics ds = null;
				if(state.getDSInfo().containsKey(nodeidentifer)){
					ds = state.getDSInfo().get(nodeidentifer);
				} else {
					ds = new DataServerStatistics();
					String[] id =nodeidentifer.split(":");
					ds.setIp(id[0]);
					ds.setPort(Integer.parseInt(id[1]));
					state.getDSInfo().put(nodeidentifer, ds);
				}
				ds.setStat(instance.getString("nodestat"));
				ds.setDelay(instance.getDouble("delay"));
			}
		} catch (JSONException e) {
			log.error(e.toString());
		}
	}
	
	public void RetrieveFromDateservernodeinfo2json(String URLBase,RealTimeGroupInfo state)
	{
		HttpGet httpget = new HttpGet(
				URLBase+"/dateservernodeinfo2json?start=1&limit=1025");
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			log.error(e.toString());
		} catch (IOException e) {
			log.error(e.toString());
		}
		HttpEntity entity = response.getEntity();
		String jsonData = "";
		if (entity != null) {
			try {
				jsonData = EntityUtils.toString(entity);
			} catch (ParseException e) {
				log.error(e.toString());
			} catch (IOException e) {
				log.error(e.toString());
			}
			try {
				entity.consumeContent();
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
		try {
			JSONObject object = new JSONObject(jsonData);
			int totalproperty = object.getInt("totalproperty");

			JSONArray root = object.getJSONArray("root");
			for(int i =0 ;i<root.length();++i){
				JSONObject instance = root.getJSONObject(i);
				String nodeidentifer = (String) instance.get("nodeidentifer");
				if(nodeidentifer.equals("total")) 
					continue;
				DataServerStatistics ds = null;
				if(state.getDSInfo().containsKey(nodeidentifer)){
					ds = state.getDSInfo().get(nodeidentifer);
				} else {
					ds = new DataServerStatistics();
					String[] id =nodeidentifer.split(":");
					ds.setIp(id[0]);
					ds.setPort(Integer.parseInt(id[1]));
					state.getDSInfo().put(nodeidentifer, ds);
				}
				int area = instance.getInt("area");
				AreaStatistics ai = null;
				if(ds.get_area_statistics().containsKey(area))
					ai = ds.get_area_statistics().get(area);
				else{
					ai = new AreaStatistics();
					ds.get_area_statistics().put(area, ai);
				}
				
				ai.setDataSize(instance.getLong("dataSize"));
				ai.setUseSize(instance.getLong("useSize"));
				ai.setItemCount(instance.getLong("itemCount"));
				
				ai.setEvictCount(instance.getLong("evictCount"));
				ai.setGetCount(instance.getLong("getCount"));
				ai.setHitCount(instance.getLong("hitCount"));
				ai.setPutCount(instance.getLong("putCount"));
				ai.setRemoveCount(instance.getLong("removeCount"));
				
			}
		} catch (JSONException e) {
			log.error(e.toString());
		}
	}
	
}












