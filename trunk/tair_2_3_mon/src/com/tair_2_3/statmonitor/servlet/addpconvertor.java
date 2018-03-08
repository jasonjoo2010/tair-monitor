/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.tair_2_3.statmonitor.servlet;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;

import com.alipay.sofa.platform.cache.data.Convertor;
import com.alipay.sofa.platform.cache.data.HessianConvertor;
import com.alipay.sofa.platform.cache.exception.TDBMBaseException;
import com.alipay.sofa.platform.cache.exception.TDBMDataConvertException;
import com.alipay.sofa.platform.cache.exception.TDBMDataSendingException;

/**
 * 
 * @author yunliang.shi
 * @version $Id: AddpConvertor.java, v 0.1 2012-9-24 ÏÂÎç07:20:14 yunliang.shi Exp $
 */
public class addpconvertor extends HessianConvertor {
    private final static Logger logger = Logger.getLogger(addpconvertor.class);
    
    static byte[] header = new byte[] { 0x01 }; 
    
    @Override
    public  byte[] convertKey(Object key) throws TDBMDataSendingException {
        if (key == null) {//code defence
            throw new TDBMDataSendingException("key in  can not be null");
        }
        byte[] keyBytes = null;
        
        byte[] bytes;
        try {
            bytes = ((String)key).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new TDBMDataSendingException("code error");
        }
        keyBytes = new byte[bytes.length + header.length]; 
        System.arraycopy(header, 0, keyBytes, 0, header.length);
        System.arraycopy(bytes, 0, keyBytes, header.length, bytes.length);

        return super.convertKey(keyBytes);
    }

    
    /**
     * convertor object to byte[]
     * @param o
     * @return
     * @throws IOException
     */
    @Override
    public  byte[] convertData(Object object) throws TDBMBaseException {
        byte[] valBytes = null;
        try {
            byte[] bytes = ((String)object).getBytes("utf-8");
            valBytes = new byte[bytes.length + header.length]; 
            System.arraycopy(header, 0, valBytes, 0, header.length);
            System.arraycopy(bytes, 0, valBytes, header.length, bytes.length);
        } catch (UnsupportedEncodingException e) {
            throw new TDBMDataSendingException("key in  can not be null");
        }
        
       return super.convertData(valBytes);
    }
    
    @Override
    public  Object bytes2Object(byte[] bytes) throws TDBMDataConvertException{
        byte[] val = (byte[])super.bytes2Object(bytes);
        
        try {
            return new String(val, 1, val.length - 1, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new TDBMDataConvertException("code error");
        }
    }
}
