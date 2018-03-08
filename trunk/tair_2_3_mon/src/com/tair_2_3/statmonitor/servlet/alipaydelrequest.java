package com.tair_2_3.statmonitor.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alipay.sofa.platform.cache.data.GeneralConvertor;
import com.alipay.sofa.platform.cache.data.HessianConvertor;
import com.alipay.sofa.platform.cache.impl.TairCacheManagerImpl;
import com.tair_2_3.statmonitor.TairStatInfoReaderDeamon;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.json.JSONArray;
import com.taobao.tair.json.JSONObject;

/**
 * Servlet implementation class alipaydelrequest
 */
public class alipaydelrequest extends HttpServlet {
    private static final long    serialVersionUID  = 1L;
    private static final Log     log               = LogFactory.getLog(alipaydelrequest.class);
    public static final int      HESSIAN_CONVERTOR = 1;
    public static final int      GENERAL_CONVERTOR = 2;
    public static final int      ADDP_CONVERTOR    = 3;
    private addpconvertor        addpConvertor     = new addpconvertor();
    private HessianConvertor     hessianConvertor  = new HessianConvertor();
    private GeneralConvertor     generalConvertor  = new GeneralConvertor();
    private TairCacheManagerImpl tairCacheManager  = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public alipaydelrequest() {
        super();
        // TODO Auto-generated constructor stub
        tairCacheManager = new TairCacheManagerImpl();
        tairCacheManager.init();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                                                                                  throws ServletException,
                                                                                  IOException {
        boolean result = true;

        int Area = -1;
        String Key = null;
        String Result = null;
        int ConvertorType = -1;

        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            Object o = e.nextElement();
            log.info((String) o + ":" + request.getParameter((String) o));
        }

        try {
            Area = Integer.parseInt(request.getParameter("Area"));
            ConvertorType = Integer.parseInt(request.getParameter("Type"));
            Key = Escape.unescape(request.getParameter("Key"));
        } catch (Exception e) {
            log.error(e.toString());
            result = false;
        }

        JSONObject res = new JSONObject();
        res.put("totalProperty", result ? 1 : 0);
        res.put("successProperty", result);
        JSONArray root = new JSONArray();
        tairCacheManager.setNamespace(Area);
        tairCacheManager.setTairManager(TairStatInfoReaderDeamon.getTask().tm);
        if (result) {
            boolean ret = false;
            switch (ConvertorType) {
                case HESSIAN_CONVERTOR:
                    tairCacheManager.setConvertor(hessianConvertor);
                    break;
                case GENERAL_CONVERTOR:
                    tairCacheManager.setConvertor(generalConvertor);
                    break;
                case ADDP_CONVERTOR:
                    tairCacheManager.setConvertor(addpConvertor);
                    break;
            }
            ret = tairCacheManager.removeObject(Key);

            if (!ret) {
                Result = "err occur while delete";
            } else {
                Result = "delete success";
            }
            JSONObject foo = new JSONObject();
            foo.put("Area", Area);
            foo.put("Key", Key);
            foo.put("Type", ConvertorType);
            foo.put("Result", Result);
            root.add(foo);

        }
        res.put("root", root);
        String finalResult = res.toString();
        response.getOutputStream().println(new String(finalResult.getBytes("UTF-8"), "ISO-8859-1"));
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
                                                                                   throws ServletException,
                                                                                   IOException {
        doGet(request, response);
    }

}
