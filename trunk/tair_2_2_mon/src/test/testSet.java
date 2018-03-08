package test;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class testSet extends TestCase {
	public void testIntersection(){
		Set<Integer> A = new HashSet<Integer>();
		Set<Integer> B = new HashSet<Integer>();
		
		A.add(1);
		A.add(2);
		A.add(3);
		A.add(4);
		
		//B.add(3);
		//B.add(4);
		B.add(5);
		B.add(6);
		
		Boolean flag = A.retainAll(B);
		
		for(int i:A){
			System.out.println(i);
		}
		System.out.println(flag);
	}
	public void testUnion(){
		Set<Integer> A = new HashSet<Integer>();
		Set<Integer> B = new HashSet<Integer>();
		
		A.add(1);
		A.add(2);
		A.add(3);
		A.add(4);
		
		B.add(3);
		B.add(4);
		B.add(5);
		B.add(6);
		
		Boolean flag = A.addAll(B);
		
		for(int i:A){
			System.out.println(i);
		}
		System.out.println(flag);
	}
	public void testSub(){
		Set<Integer> A = new HashSet<Integer>();
		Set<Integer> B = new HashSet<Integer>();
		
		A.add(1);
		A.add(2);
		A.add(3);
		A.add(4);
		
		B.add(3);
		B.add(4);
		B.add(5);
		B.add(6);
		
		Boolean flag = A.removeAll(B);
		
		for(int i:A){
			System.out.println(i);
		}
		System.out.println(flag);
	}
}
