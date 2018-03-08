package com.tair_2_3.statmonitor.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.alipay.sofa.platform.cache.hessian.HessianSerUtils;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

/**
 * @author Administrator
 * @email liang.chenl@alipay.com
 */
public class hessianconvertor {
    private final static Logger logger = Logger.getLogger(hessianconvertor.class);
    
    
    Object readObject(byte[] bytes) throws Exception {
        Object obj = null;
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            Hessian2Input in = new Hessian2Input(bin);
            in.setSerializerFactory(HessianSerUtils.getSerializerFactory());
            in.startMessage();
            obj = in.readObject();
            in.completeMessage();
            in.close();
            bin.close();
        } catch (Exception e) {
            ToStringBuilder sb = new ToStringBuilder(null);  
            sb.append("Hessian反序列化出错!");
            sb.append( bytes );
            logger.error(sb.toString(), e);
        }
        return obj;
    }


    byte[] writeObject(Object obj) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output out = new Hessian2Output(bos);
        out.setSerializerFactory(HessianSerUtils.getSerializerFactory());
        try {
            out.startMessage();
            out.writeObject(obj);
            out.completeMessage();
            out.close();
        } catch (Exception e) {
            logger.error("Hessian序列化出错!", e);
        }
        return bos.toByteArray();
    }

}

