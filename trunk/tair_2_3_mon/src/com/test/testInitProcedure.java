package com.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

import com.taobao.tair.comm.DefaultTranscoder;
import com.taobao.tair.comm.MultiSender;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.impl.ConfigServer;
import com.taobao.tair.impl.DefaultTairManager;
import com.taobao.tair.packet.TairPacketStreamer;

public class testInitProcedure extends TestCase {
	private static final Log log = LogFactory.getLog(testInitProcedure.class);
	private static final String clientVersion = "TairClient 2.3.1";
	
	int timeout = TairConstant.DEFAULT_TIMEOUT;
	int maxWaitThread = TairConstant.DEFAULT_WAIT_THREAD;
	
	int compressionThreshold = 0;
	String charset = null;
	String groupName = null;
	String name = null;
	List<String> configServerList = null;
	
	TairPacketStreamer packetStreamer = null;
	Transcoder transcoder = null;
	ConfigServer configServer = null;
	MultiSender multiSender = null;
	
	public void testInit(){

//		configServerList = new ArrayList<String>();
//		configServerList.add("10.232.12.141:5198");
//		configServerList.add("10.232.12.142:5198");
//		groupName = "group_1";
//		
//		transcoder = new DefaultTranscoder(compressionThreshold, charset);
//		packetStreamer = new TairPacketStreamer(transcoder);
//		configServer = new ConfigServer(groupName, configServerList,
//				packetStreamer);
//		if (!configServer.retrieveConfigure()) {
//			throw new RuntimeException("init config failed");
//		}
//		multiSender = new MultiSender(packetStreamer);
//		log.warn(name + " [" + clientVersion + "] started...");
	}
}
