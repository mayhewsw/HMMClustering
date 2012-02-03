package edu.washington.cs.uei.disktable;

public abstract class AbstractStringArrayComparator implements Comparator {

	abstract public ComparisonObject getComparisonObject(String [] s1);
	
	abstract public int compare(String [] s1, String []s2);
	
	abstract public int compare(ComparisonObject co1, ComparisonObject co2);
}
