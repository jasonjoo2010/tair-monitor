package com.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.impl.DefaultTairManager;

import junit.framework.Assert;
import junit.framework.TestCase;

public class testUsage extends TestCase {
	public void testBasicUsage()
	{
		DefaultTairManager tm = new DefaultTairManager();

		List<String> cs = new ArrayList<String>();

		cs.add("10.232.4.20:5198");
		//cs.add("10.13.117.11:8298");
		tm.setConfigServerList(cs);
		tm.setGroupName("group_1");

		tm.init();
		//tm.invalid(namespace, key)
//		byte[] key ;
//		byte[] value ;
//		
//
//		//----------------------------------------------------------------------
//		int ns = 0;
//		//String key = "4b782080870920fa9c7c54418882018e";
//		key = "123".getBytes();
//		//String value = "hello,world";
//		value = "sadfdsafasfasfasfasfasfasd12".getBytes();
//
//		System.out.println(tm.put(ns, key, value));
//		
		
		
		//step 1 add String value
		HashMap key1 = new HashMap();
		key1.put(1, "1");
		List value1 = new ArrayList();
		value1.add(1.1);
		int namespace =0 ;
		tm.delete(namespace, key1);
        ResultCode resultCode = tm.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        HashMap key2 = new HashMap();
		key2.put(2, "2");
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tm.delete(namespace, key2);
        resultCode = tm.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tm.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tm.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tm.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tm.getAndRemove(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tm.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        resultCode = tm.removeItems(namespace, key2,0,1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tm.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
		
        
		System.exit(0);

	}
}
