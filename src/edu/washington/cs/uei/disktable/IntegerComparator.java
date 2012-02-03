package edu.washington.cs.uei.disktable;

import java.io.File;

public class IntegerComparator 
extends AbstractStringArrayComparator
implements Comparator 
{
	private int [] argColumns = null;
	private int ascending = 1;
	
	class IntegerComparisonObject 
	extends AbstractComparisonObject 
	implements ComparisonObject 
	{
		public String str;
		public int[] compareArr;
	}
	
	class OneIntegerComparisonObject 
	extends AbstractComparisonObject 
	implements ComparisonObject 
	{
		public String str;
		public int compareInt;
	}
	
	public IntegerComparator(int [] argColumns, boolean ascending) 
	{
		this.argColumns = argColumns;
		if(ascending) {
			this.ascending = 1;
		}
		else {
			this.ascending = -1;
		}
	}
	
	public IntegerComparator(int argColumn, boolean ascending) 
	{
		this.argColumns = new int[1];
		this.argColumns[0] = argColumn;
		if(ascending) {
			this.ascending = 1;
		}
		else {
			this.ascending = -1;
		}
	}
	
	public int compare(String[] s1, String[] s2) {
		for(int i=0; i<argColumns.length; i++) {
			int col = argColumns[i];
			if(s1[col].length()<s2[col].length()) {
				return -1 * ascending;
			}
			else if(s2[col].length()<s1[col].length()) {
				return 1 * ascending;
			}
			int comp = s1[col].compareTo(s2[col]);
			if(comp!=0) {
				return comp * ascending;
			}
		}
		
		return 0;
	}

	public ComparisonObject getComparisonObject(String[] s1) {
		if(argColumns.length==1) {
			OneIntegerComparisonObject ret = new OneIntegerComparisonObject();
			ret.setString(s1);
			ret.compareInt = Integer.parseInt(s1[argColumns[0]]);
			return ret;
		}
		
		IntegerComparisonObject ret = new IntegerComparisonObject();
		ret.setString(s1);
		ret.compareArr = new int[argColumns.length];
		for(int i=0; i<argColumns.length; i++) {
			ret.compareArr[i] = Integer.parseInt(s1[argColumns[i]]);
		}
		return ret;
	}
	
	public int compare(OneIntegerComparisonObject oico1, OneIntegerComparisonObject oico2) {
		if(oico2.compareInt>oico1.compareInt) {
			return -1 * ascending;
		}
		else if(oico2.compareInt<oico1.compareInt) {
			return 1 * ascending;
		}
		return 0;
	}
	
	public int compare(IntegerComparisonObject ico1, IntegerComparisonObject ico2) {
		for(int i=0; i<argColumns.length; i++) {
			if(ico2.compareArr[i]>ico1.compareArr[i]) {
				return -1 * ascending;
			}
			else if(ico2.compareArr[i]<ico1.compareArr[i]) {
				return 1 * ascending;
			}
		}
		
		return 0;
	}
	
	public int compare(ComparisonObject o1, ComparisonObject o2) {
		if(o1.getClass().getName().equals(OneIntegerComparisonObject.class.getName()) &&
				o2.getClass().getName().equals(OneIntegerComparisonObject.class.getName())) {
			return compare((OneIntegerComparisonObject)o1, (OneIntegerComparisonObject)o2);
		}
		else if(o1.getClass().getName().equals(IntegerComparisonObject.class.getName()) &&
				o2.getClass().getName().equals(IntegerComparisonObject.class.getName())) {
			return compare((IntegerComparisonObject)o1, (IntegerComparisonObject)o2);
		}
		return 0;
	}

	
	private void test() {
		int max = 1000;
		
		String [][]s1 = new String [max][];
		String [][]s2 = new String [max][];

		for(int i=0; i<max; i++) {
			s1[i] = new String[1];
			s1[i][0] = String.valueOf((int)(Math.random()*100));
			s2[i] = new String[1];
			s2[i][0] = String.valueOf((int)(Math.random()*100));
		}
		
		for(int i=0; i<max; i++) {
			ComparisonObject co1 = getComparisonObject(s1[i]);
			ComparisonObject co2 = getComparisonObject(s2[i]);
			int arrPred = compare(s1[i], s2[i]);
			int coPred  = compare(co1, co2);
			boolean correct = (arrPred>0 && coPred>0) ||
							  (arrPred<0 && coPred<0) ||
							  (arrPred==0 && coPred==0);
			System.out.println(s1[i][0] + " vs. " + s2[i][0] + " :  " + arrPred + ", " + coPred + " -- " + correct);
		}
	}

	public static void main(String[] args) {
		/*
		IntegerComparator ic = new IntegerComparator(0, false);
		ic.test();
		*/
		
		String workspace = 
			"C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\hist_sci_clean_06_23_06\\proper_tuples\\";
		String infile = workspace + "object_file_0.txt";
		String outfile = workspace + "objects_sorted_by_count.txt";
		IntegerComparator ic = new IntegerComparator(2, false);
		BasicDiskTable dt = new BasicDiskTable(new File(infile));
		File out = new File(outfile);
		dt.sort(ic, out);
	}
}
