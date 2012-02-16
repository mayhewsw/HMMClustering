package edu.rhit.tools;

public class Tuple {

	public String s1;
	public String s2;
	
	public Tuple(){
		
	}
	
	public Tuple(String a, String b){
		s1 = a;
		s2 = b;
	}

	@Override
	public String toString() {
		return "( " + s1 + ", " + s2 + " )";		
	}
	
	
}
