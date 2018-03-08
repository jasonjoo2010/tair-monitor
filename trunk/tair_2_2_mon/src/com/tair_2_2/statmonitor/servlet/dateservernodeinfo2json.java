package com.tair_2_2.statmonitor.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tair_2_2.statmonitor.AreaStatistics;
import com.tair_2_2.statmonitor.ServerStat;
import com.tair_2_2.statmonitor.TairStatInfoReaderDeamon;

/**
 * Servlet implementation class dateservernodeinfo2json
 */
public class dateservernodeinfo2json extends HttpServlet {
	private static final Log log = LogFactory
			.getLog(dateservernodeinfo2json.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public dateservernodeinfo2json() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int start = 0;
		int size = 0;
		try {
			start = Integer.parseInt(request.getParameter("start"));
			size = Integer.parseInt(request.getParameter("limit"));
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		ServletOutputStream out = response.getOutputStream();
		String output = "";

		HashMap<String, ServerStat> _server_stat = TairStatInfoReaderDeamon.getTask().get_server_stat();
		Iterator<String> iter = _server_stat.keySet().iterator();
		int areamount = -1;
		String buf = "  ";
		
		AreaStatistics total = new AreaStatistics();
		total.setArea(1024);
		
		while (iter.hasNext()) {
			String _node_identify = iter.next();
			ServerStat _server_area_stat = _server_stat.get(_node_identify);
			Iterator<Integer> area_iter = _server_area_stat
					.get_area_statistics().keySet().iterator();
			HashMap<Integer, AreaStatistics> _area_statistics = _server_area_stat
					.get_area_statistics();
			while (area_iter.hasNext()) {
				areamount++;
				int areanum ;
				if (areamount < start) {
					area_iter.next();
					continue;
				} else if (areamount >= start + size) {
					area_iter.next();
					continue;
				} else {
					areanum = area_iter.next();
				}
				AreaStatistics tem = _area_statistics.get(areanum);
				total.addup(tem);
				buf += "{";
				buf += "nodeidentifer:'" + _node_identify + "'," 
						+ "area:" + areanum + "," 
						+ "dataSize:" + tem.getDataSize()+","
						+ "evictCount:" + tem.getEvictCount() + ","
						+ "getCount:" + tem.getGetCount() + ","
						+ "hitRate:" + (tem.getGetCount()==0 ? 0 : (new java.text.DecimalFormat( "#.## ")).format(((double)tem.getHitCount() / tem.getGetCount()))) + ","
						+ "putCount:" + tem.getPutCount() + "," 
						+ "hitCount:" + tem.getHitCount() + "," 
						+ "itemCount:" + tem.getItemCount() + "," 
						+ "removeCount:" + tem.getRemoveCount() + "," 
						+ "useSize:" + tem.getUseSize();
				buf += "},\n";
	
			}
		}
		buf += "{";
		buf +=  "nodeidentifer:'total',"  
				+ "area:"+ total.getArea() + "," 
				+ "dataSize:" + total.getDataSize()+","
				+ "evictCount:" + total.getEvictCount() + ","
				+ "getCount:" + total.getGetCount() + ","
				+ "hitRate:" + (total.getGetCount()==0 ? 0 : (new java.text.DecimalFormat( "#.## ")).format(((double)total.getHitCount() / total.getGetCount()))) + ","
				+ "putCount:" + total.getPutCount() + "," 
				+ "hitCount:" + total.getHitCount() + "," 
				+ "itemCount:"	+ total.getItemCount() + "," 
				+ "removeCount:" + total.getRemoveCount() + ","
				+ "useSize:" + total.getUseSize() + ","
				+ "quota:" + total.getQuota();
		buf += "},\n"; 
			output = "{totalproperty:" + areamount + ",\n" + "root:[" + buf.substring(0, buf.length()-2)
					+ "]}";
		out.print(output);
	}

}
