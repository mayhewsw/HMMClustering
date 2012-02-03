package edu.washington.cs.uei.disktable;

public interface Comparator {
	// returns 0 if s1 and s2 are 'equal', 
	// less than zero if s1 < s2, 
	// and more than zero if s1 > s2 
	public int compare(String[] s1, String[] s2);
	
	// Can be more efficient to compare packed representations of String arrays,
	// so we use the ComparisonObject interface for some sorts
	public ComparisonObject getComparisonObject(String [] s1);
	
	public int compare(ComparisonObject co1, ComparisonObject co2);
}
