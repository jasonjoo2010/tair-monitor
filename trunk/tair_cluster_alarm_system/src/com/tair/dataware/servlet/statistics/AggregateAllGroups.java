package com.tair.dataware.servlet.statistics;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tair.dataware.DataWare;
import com.tair.dataware.metadata.AreaStatistics;
import com.tair.dataware.metadata.DataServerStatistics;
import com.tair.dataware.metadata.RealTimeGroupInfo;
import com.tair.utils.GlobalClock;

/**
 * Servlet implementation class AggregateAllGroups
 */
public class AggregateAllGroups extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AggregateAllGroups() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AreaStatistics accu = new AreaStatistics();
		accu.setArea(-1);
		accu.setQuota(-1);
		for(Integer gid : DataWare.DataWare.keySet()){
			Map<Integer, AreaStatistics> areainfos = DataWare.getGroupInfoCollector(gid).getRealTimeGroupInfo(GlobalClock.getTimestamp()).getAreaInfo();
			for(Integer area : areainfos.keySet()){
				AreaStatistics one = areainfos.get(area);
				accu.setDataSize(accu.getDataSize()+one.getDataSize());
				accu.setUseSize(accu.getUseSize()+one.getUseSize());
				
				accu.setEvictCount(accu.getEvictCount()+one.getEvictCount());
				accu.setGetCount(accu.getGetCount()+one.getGetCount());
				accu.setHitCount(accu.getHitCount()+one.getHitCount());
				accu.setItemCount(accu.getItemCount()+one.getItemCount());
				accu.setPutCount(accu.getPutCount()+one.getPutCount());
				accu.setRemoveCount(accu.getRemoveCount()+one.getRemoveCount());
			}
			
		}
		String buf =  "area:all," 
		+ "dataSize:" + accu.getDataSize()+","
		+ "useSize:" + accu.getUseSize() + ","
		+ "evictCount:" + accu.getEvictCount() + ","
		+ "getCount:" + accu.getGetCount() + ","
		+ "putCount:" + accu.getPutCount() + "," 
		+ "hitCount:" + accu.getHitCount() + "," 
		+ "itemCount:"	+ accu.getItemCount() + "," 
		+ "removeCount:" + accu.getRemoveCount();




		response.getOutputStream().print(buf);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
