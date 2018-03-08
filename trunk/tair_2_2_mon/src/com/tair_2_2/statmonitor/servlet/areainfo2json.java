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
 * Servlet implementation class areainfo2json
 */
public class areainfo2json extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(areainfo2json.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public areainfo2json() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

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

			HashMap<Integer, AreaStatistics> _area_statistics =TairStatInfoReaderDeamon.getTask().get_aggregate_area_statistics();
			Iterator<Integer> area_iter = _area_statistics.keySet().iterator();
			int areamount = 0;
			String buf = "  ";
			while (area_iter.hasNext()) {
				areamount++;
				int areanum = area_iter.next();
			
				AreaStatistics tem = _area_statistics.get(areanum);
				buf += "{";
				buf +=  "area:"+ areanum + "," 
						+ "dataSize:" + tem.getDataSize()+","
						+ "evictCount:" + tem.getEvictCount() + ","
						+ "getCount:" + tem.getGetCount() + ","
						+ "hitRate:" + (tem.getGetCount()==0 ? 0 : (new java.text.DecimalFormat( "#.## ")).format(((double)tem.getHitCount() / tem.getGetCount()))) + ","
						+ "putCount:" + tem.getPutCount() + "," 
						+ "hitCount:" + tem.getHitCount() + "," 
						+ "itemCount:"	+ tem.getItemCount() + "," 
						+ "removeCount:" + tem.getRemoveCount() + ","
						+ "useSize:" + tem.getUseSize() + ","
						+ "quota:" + tem.getQuota();
				buf += "},\n";

			}
			output = "{totalproperty:" + areamount + ",\n" + "root:[" + buf.substring(0, buf.length()-2)
					+ "]}";
		out.print(output);
	}

}
