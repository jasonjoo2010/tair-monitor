package test;

import junit.framework.TestCase;

public class testString extends TestCase {
	public void testA(){
		String foo = "1234567";
		System.out.printf("%s\n", foo.substring(0, foo.length()-1));
	}
	
}
