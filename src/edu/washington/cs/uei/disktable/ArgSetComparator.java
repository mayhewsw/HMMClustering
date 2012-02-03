package edu.washington.cs.uei.disktable;

public class ArgSetComparator 
	extends AbstractStringArrayComparator
	implements Comparator
{
	private static final String _sep_ = "\0";
	
	private int [] argSet = null;
	
	
	public ArgSetComparator(int [] args) {
		argSet = args;
	}
	
	class ArgSetComparisonObject 
	extends AbstractComparisonObject 
	implements ComparisonObject 
	{
		public String str;
		public String compareStr;
	}
	
	public ArgSetComparisonObject getComparisonObject(String [] s1) {
		ArgSetComparisonObject ret = new ArgSetComparisonObject();
		ret.setString(s1);
		StringBuffer comp = new StringBuffer("");
		for(int i=0; i<argSet.length-1; i++) {
			comp.append(s1[argSet[i]]);
			comp.append(_sep_);
		}
		comp.append(s1[argSet[argSet.length-1]]);
		ret.compareStr = comp.toString();
		return ret;
	}
	
	public int compare(ArgSetComparisonObject asco1, ArgSetComparisonObject asco2) {
		return asco1.compareStr.compareTo(asco2.compareStr);
	}
	
	public int compare(ComparisonObject o1, ComparisonObject o2) {
		if(o1.getClass().getName().equals(ArgSetComparisonObject.class.getName()) &&
				o2.getClass().getName().equals(ArgSetComparisonObject.class.getName())) {
			return compare((ArgSetComparisonObject)o1, (ArgSetComparisonObject)o2);
		}
		return 0;
	}
	
	public int compare(String[] s1, String[] s2) {
		if(argSet==null) {
			return 0;
		}
		
		int comp = 0;
		
		for(int i=0; i<argSet.length; i++) {
			if(s1[argSet[i]]==null) {
				return -1;
			}
			comp = s1[argSet[i]].compareTo(s2[argSet[i]]);
			if(comp!=0) {
				return comp;
			}
		}
		
		return 0;
	}
}
