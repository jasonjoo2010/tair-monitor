package com.test;

import java.util.ArrayList;
import java.util.List;

import com.taobao.tair.impl.DefaultTairManager;

import junit.framework.TestCase;

public class testPass extends TestCase {

	public void testA()
	{
		DefaultTairManager tm = new DefaultTairManager();

		List<String> cs = new ArrayList<String>();

		cs.add("10.13.117.11:5198");
		cs.add("10.13.117.11:5198");
		tm.setConfigServerList(cs);
		tm.setGroupName("group_1");

		tm.init();
	}
}

