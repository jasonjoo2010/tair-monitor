package com.tair_2_2.statmonitor.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tair_2_2.statmonitor.TairStatInfoReaderDeamon;
import com.taobao.common.tair.etc.TairUtil;



/**
 * Servlet implementation class bucketsdistrbution
 */
public class bucketsdistrbution extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public bucketsdistrbution() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Long> sl = TairStatInfoReaderDeamon.getTask().tm.getServerList();
		int BucketCount = TairStatInfoReaderDeamon.getTask().tm.getBucketCount();
		//int CopyCount = TairStatInfoReaderDeamon.getTask().tm.getCopyCount();
		
		Map< Long,List<Integer> > bucketsdis = new HashMap<Long,List<Integer> >();
		for(int i = 0 ;i<BucketCount;i++){
			long ds = sl.get(i);
			if(bucketsdis.containsKey(ds)){
				bucketsdis.get(ds).add(i);
			}else{
				List<Integer> foo = new ArrayList<Integer>();
				foo.add(i);
				bucketsdis.put(ds, foo);
			}
		}
		
		JSONObject output = null;
		try {
			output = new JSONObject();
			output.put("totalproperty", bucketsdis.size());
			
			JSONArray root = new JSONArray();
			for(Long key : bucketsdis.keySet())
			{
				List<Integer> buckets = bucketsdis.get(key);
				JSONObject item = new JSONObject();
				item.put("nodeidentifer", TairUtil.idToAddress(key));
				item.put("bucketcount", buckets.size());
				String bucketlist = "";
				for(Integer bucket : buckets){
					bucketlist+=bucket+", ";
				}
				item.put("bucketlist", bucketlist);
				root.put(item);
			}
			output.put("root", root);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.getOutputStream().print(output.toString());
	}

}
