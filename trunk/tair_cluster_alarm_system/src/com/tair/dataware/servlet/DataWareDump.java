package com.tair.dataware.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tair.dataware.DataWare;
import com.tair.dataware.metadata.RealTimeGroupInfo;
import com.tair.dataware.metadata.RealTimeGroupInfoCollector;

/**
 * Servlet implementation class DataWareDump
 */
public class DataWareDump extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(DataWareDump.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataWareDump() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ServletOutputStream out = response.getOutputStream();
		
		int gid = -1;
		try {
			gid = Integer.parseInt(request.getParameter("gid"));
		} catch (Exception e) {
			log.error(e.toString());
		}
		RealTimeGroupInfoCollector foo =
			DataWare.getGroupInfoCollector(gid);
		
		if(foo!=null){
			out.println(foo.toString());
			for(int i=0;i<1024;i++){
				RealTimeGroupInfo bar =foo.getRealTimeGroupInfo(i);
				if(bar!=null){
					String con = "";
					int size = bar.getDSInfo().keySet().size();
					for(String key : bar.getDSInfo().keySet()){
						con=con+bar.getDSInfo().get(key).getStat()+"|";
					}
					out.println(bar.toString()+"-->"+con);
				}
				else
					out.println("RealTimeGroupInfo is null");
			}
		}
		else
			out.println("RealTimeGroupInfoCollector is null");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
