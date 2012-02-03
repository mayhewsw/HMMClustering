package edu.washington.cs.uei.disktable;

import java.io.File;

public class DoubleComparator 
	extends AbstractStringArrayComparator
	implements Comparator 
{
	int [] compArgs = null;
	int ascendingOrder = 1;
	
	class DoubleComparisonObject 
	extends AbstractComparisonObject 
	implements ComparisonObject 
	{
		public String str;
		public double [] compareArr;
	}
	
	class OneDoubleComparisonObject 
	extends AbstractComparisonObject 
	implements ComparisonObject 
	{
		public String str;
		public double compareDouble;
	}
	
	public DoubleComparator(int arg, boolean ascending) {
		compArgs = new int[1];
		compArgs[0] = arg;
		if(ascending) {
			ascendingOrder = 1;
		}
		else {
			ascendingOrder = -1;
		}
	}
	
	public DoubleComparator(int [] argSet, boolean ascending) {
		compArgs = argSet;
		if(ascending) {
			ascendingOrder = 1;
		}
		else {
			ascendingOrder = -1;
		}
	}
	
	public int compare(String[] s1, String[] s2) {
		if(compArgs==null) {
			return 0;
		}
		for(int i=0; i<compArgs.length; i++) {
			int col = compArgs[i];
			/*
			if(col>=s1.length || col>=s2.length) {
				for(int j=0; j<s1.length; j++) {
					System.out.println(s1[j]);
				}
				for(int j=0; j<s2.length; j++) {
					System.out.println(s2[j]);
				}
				System.out.flush();
			}*/
			
			double d1 = Double.parseDouble(s1[col]);
			double d2 = Double.parseDouble(s2[col]);
			if(d2>d1) {
				return -1 * ascendingOrder;
			}
			else if(d2<d1) {
				return 1 * ascendingOrder;
			}
		}
		
		return 0;
	}

	public ComparisonObject getComparisonObject(String[] s1) {
		if(compArgs.length==1) {
			OneDoubleComparisonObject ret = new OneDoubleComparisonObject();
			ret.setString(s1);
			ret.compareDouble = Double.parseDouble(s1[compArgs[0]]);
			return ret;
		}
		
		DoubleComparisonObject ret = new DoubleComparisonObject();
		ret.setString(s1);
		ret.compareArr = new double[compArgs.length];
		for(int i=0; i<compArgs.length; i++) {
			ret.compareArr[i] = Double.parseDouble(s1[compArgs[i]]);
		}
		return ret;
	}

	public int compare(OneDoubleComparisonObject odco1, OneDoubleComparisonObject odco2) {
		if(odco2.compareDouble>odco1.compareDouble) {
			return -1 * ascendingOrder;
		}
		else if(odco2.compareDouble<odco1.compareDouble) {
			return 1 * ascendingOrder;
		}
		return 0;
	}
	
	public int compare(DoubleComparisonObject dco1, DoubleComparisonObject dco2) {
		for(int i=0; i<compArgs.length; i++) {
			if(dco2.compareArr[i]>dco1.compareArr[i]) {
				return -1 * ascendingOrder;
			}
			else if(dco2.compareArr[i]<dco1.compareArr[i]) {
				return 1 * ascendingOrder;
			}
		}
		
		return 0;
	}
	
	public int compare(ComparisonObject o1, ComparisonObject o2) {
		if(o1.getClass().getName().equals(OneDoubleComparisonObject.class.getName()) &&
				o2.getClass().getName().equals(OneDoubleComparisonObject.class.getName())) {
			return compare((OneDoubleComparisonObject)o1, (OneDoubleComparisonObject)o2);
		}
		else if(o1.getClass().getName().equals(DoubleComparisonObject.class.getName()) &&
				o2.getClass().getName().equals(DoubleComparisonObject.class.getName())) {
			return compare((DoubleComparisonObject)o1, (DoubleComparisonObject)o2);
		}
		return 0;
	}

	private void test() {
		int max = 1000;
		
		String [][]s1 = new String [max][];
		String [][]s2 = new String [max][];

		for(int i=0; i<max; i++) {
			s1[i] = new String[1];
			s1[i][0] = String.valueOf((Math.random()*100));
			s2[i] = new String[1];
			s2[i][0] = String.valueOf((Math.random()*100));
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
		DoubleComparator dc = new DoubleComparator(0, false);
		dc.test();
		*/
		

		String workspace = 
			//"C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\hist_sci_clean_06_23_06\\proper_tuples\\";
			"C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\small_test_hist_sci\\under_1000\\polysemy_detection\\";
		String infile = workspace + "polysemy_scores.txt";
		String outfile = workspace + "sortedPolysemyScores.txt";
		DoubleComparator dc = new DoubleComparator(5, true);
		BasicDiskTable dt = new BasicDiskTable(new File(infile));
		File out = new File(outfile);
		dt.sort(dc, out);
	}
}
