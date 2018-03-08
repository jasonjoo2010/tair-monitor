package com.tair_2_3.statmonitor.servlet;

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

import com.tair_2_3.statmonitor.AreaStatistics;
import com.tair_2_3.statmonitor.ServerStat;
import com.tair_2_3.statmonitor.TairStatInfoReaderDeamon;

/**
 * Servlet implementation class nodestatistics
 */
public class nodestatistics extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory
	.getLog(nodestatistics.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public nodestatistics() {
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
		int start = 0;
		int size = 0;
		try {
			start = Integer.parseInt(request.getParameter("start"));
			size = Integer.parseInt(request.getParameter("limit"));
		} catch (Exception e) {
			log.error(e.toString());
		}

		ServletOutputStream out = response.getOutputStream();
		String output = " ";
		
		AreaStatistics total = new AreaStatistics();
		total.setArea(1024);
		
		HashMap<String, ServerStat> _server_stat = TairStatInfoReaderDeamon.getTask().get_server_stat();
		Iterator<String> iter = _server_stat.keySet().iterator();
		int areamount = 0;
		String buf = "  ";
		while (iter.hasNext()) {
			areamount++;

			String _node_identify = iter.next();
			ServerStat _server_area_stat = _server_stat.get(_node_identify);
			Iterator<Integer> area_iter = _server_area_stat
					.get_area_statistics().keySet().iterator();
			HashMap<Integer, AreaStatistics> _area_statistics = _server_area_stat
					.get_area_statistics();
			AreaStatistics tem = new AreaStatistics();
			
			while (area_iter.hasNext()) {
				
				int areanum = area_iter.next();
				AreaStatistics areanode = _area_statistics.get(areanum);
				
				tem.setDataSize(tem.getDataSize()+areanode.getDataSize());
				tem.setEvictCount(tem.getEvictCount()+areanode.getEvictCount());
				tem.setGetCount(tem.getGetCount()+areanode.getGetCount());
				tem.setHitCount(tem.getHitCount()+areanode.getHitCount());
				tem.setItemCount(tem.getItemCount()+areanode.getItemCount());
				tem.setPutCount(tem.getPutCount()+areanode.getPutCount());
				tem.setRemoveCount(tem.getRemoveCount()+areanode.getRemoveCount());
				tem.setUseSize(tem.getUseSize()+areanode.getUseSize());
	
			}
			total.addup(tem);
			
			buf += "{";
			buf += "nodeidentifer:'" + _node_identify + "'," 
					+ "nodestat:'" + _server_area_stat.getStat() + "'," 
					+ "delay:" + (new java.text.DecimalFormat( "#.## ")).format(_server_area_stat.getDelay()) + "," 
					+ "dataSize:" + tem.getDataSize()+","
					+ "evictCount:" + tem.getEvictCount() + ","
					+ "getCount:" + tem.getGetCount() + ","
					+ "hitRate:" + (tem.getGetCount()==0 ? 0 : (new java.text.DecimalFormat( "#.#### ")).format(((double)tem.getHitCount() / tem.getGetCount()))) + ","
					+ "putCount:" + tem.getPutCount() + "," 
					+ "hitCount:" + tem.getHitCount() + "," 
					+ "itemCount:" + tem.getItemCount() + "," 
					+ "removeCount:" + tem.getRemoveCount() + "," 
					+ "useSize:" + tem.getUseSize();
			buf += "},\n";
			
		}
		buf += "{";
		buf +=  "nodeidentifer:'total',"
				+ "nodestat:'virtual',"
				+ "area:" + total.getArea() + "," 
				+ "dataSize:" + total.getDataSize()+","
				+ "evictCount:" + total.getEvictCount() + ","
				+ "getCount:" + total.getGetCount() + ","
				+ "hitRate:" + (total.getGetCount()==0 ? 0 : (new java.text.DecimalFormat( "#.#### ")).format(((double)total.getHitCount() / total.getGetCount()))) + ","
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
