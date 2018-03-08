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
import com.tair_2_2.statmonitor.TairStatInfoReaderDeamon;

/**
 * Servlet implementation class selectedareainfo
 */
public class selectedareainfo extends HttpServlet {
	
	private static final Log log = LogFactory.getLog(selectedareainfo.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public selectedareainfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ServletOutputStream out = response.getOutputStream();
		
		
		int area = -1;
		try {
			area = Integer.parseInt(request.getParameter("area"));
		} catch (Exception e) {
			log.error(e.getMessage());
			return;
		}


		String output = "";

			HashMap<Integer, AreaStatistics> _area_statistics =TairStatInfoReaderDeamon.getTask().get_aggregate_area_statistics();
			String buf="  ";
			if(_area_statistics.containsKey(area)) {
				AreaStatistics tem = _area_statistics.get(area);
				buf += "{";
				buf +=  "area:"+ area + "," 
						+ "dataSize:" + tem.getDataSize() + ","
						+ "evictCount:" + tem.getEvictCount() + ","
						+ "getCount:" + tem.getGetCount() + ","
						+ "hitRate:" + (tem.getGetCount()==0 ? 0 : (new java.text.DecimalFormat( "#.## ")).format(((double)tem.getHitCount() / tem.getGetCount()))) + ","
						+ "putCount:" + tem.getPutCount() + "," 
						+ "hitCount:" + tem.getHitCount() + "," 
						+ "itemCount:"	+ tem.getItemCount() + "," 
						+ "removeCount:" + tem.getRemoveCount() + "," 
						+ "useSize:" + tem.getUseSize() ;
				buf += "},\n";
				output = "{totalproperty: 1,\n" + "root:[" + buf.substring(0, buf.length()-2)
				+ "]}";
				out.print(output);
			}

	}

}
