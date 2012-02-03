package edu.washington.cs.uei.util;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import edu.washington.cs.uei.disktable.BasicDiskTable;

public class GeneralUtility {

	public static final String separator = " :::: ";
	public static final String _ARG1_ = "A1";
	public static final String _REL_  = "R";
	public static final String _ARG2_ = "A2";
	private static final int   strCountCutoff = 2;
	
	private static int start_size = 4400000;
	private static float fill_factor = 0.9f;

	private static int start_size_cmap = 100000;
	private static float fill_factor_cmap = 0.75f;
	
	
	public String getKey(String s1, String s2)
	{
		return s1 + separator + s2;
	}
	
	public void setHashMapParameters(int startSize, float fillFactor) {
		start_size = startSize;
		fill_factor = fillFactor;
	}
	

	public static String join(String [] sarr, String sep, int start, int end) {
		StringBuffer ret = new StringBuffer("");
		
		ret.append(sarr[start]);
		for(int i=start+1; i<=end; i++) {
			ret.append(sep);
			ret.append(sarr[i]);
		}
		
		return ret.toString();
	}

	
	public HashMap<String, LinkedList<String []>> clusterHashMap(String objectFile) {
		System.out.println("Reading in Cluster_ID->List_of_objects Map");
		
		BasicDiskTable in = new BasicDiskTable(new File(objectFile));
		HashMap<String, LinkedList<String []>> ret = 
			new HashMap<String, LinkedList<String[]>>(start_size_cmap, fill_factor_cmap);
		
		in.open();
		int i=0;
		for(String [] inline = in.readLine(); inline!=null; inline=in.readLine()) 
		{
			String key = inline[1];
			
			if(Integer.parseInt(inline[2])<strCountCutoff) {
				continue;
			}
			
			if(Integer.parseInt(inline[2])>=Integer.parseInt(inline[3])) {
				continue;
			}
			//System.out.println("Found string with cluster count > string count:  " + inline[0]);
			
			if(ret.containsKey(key)) {
				ret.get(key).add(inline);
			}
			else {
				LinkedList<String []> val = new LinkedList<String []>();
				val.add(inline);
				ret.put(key, val);
			}
			
			i++;
			if(i%100000==0) {
				System.out.println(i);
				System.out.flush();
			}
		}
		
		return ret;
	}
	
	public HashMap<String, String> objectClusterHashMapForArg(String objectFile, String arg)
	{
		System.out.println("Reading in (object,isEntity)->Cluster_id Map");
		System.out.println("filtering for arg " + arg);
		BasicDiskTable in = new BasicDiskTable(new File(objectFile));
		HashMap<String, String> ret = new HashMap<String, String>(start_size, fill_factor);
		String sep = BasicDiskTable.getSeparatorString();
		
		in.open();
		int i=0;
		for(String [] inline = in.readLine(); inline!=null; inline=in.readLine()) 
		{
			if(Integer.parseInt(inline[2])<strCountCutoff) {
				continue;
			}
			if(!inline[4].equals(arg)) { 
				continue;
			}
			
			ret.put(inline[0], join(inline,sep,0,5));
			
			i++;
			if(i%100000==0) {
				System.out.println(i);
				System.out.flush();
			}
		}
		
		return ret;
	}
	
	public HashMap<String, String> objectClusterHashMap(String objectFile) 
	{
		System.out.println("Reading in (object,isEntity)->Cluster_id Map");
		BasicDiskTable in = new BasicDiskTable(new File(objectFile));
		HashMap<String, String> ret = new HashMap<String, String>(start_size, fill_factor);
		
		in.open();
		int i=0;
		for(String [] inline = in.readLine(); inline!=null; inline=in.readLine()) 
		{
			if(Integer.parseInt(inline[2])<strCountCutoff) {
				continue;
			}
			String key = inline[0]+ " :::: " + inline[4];
			ret.put(key, join(inline," :::: ",0,5));
			
			i++;
			if(i%100000==0) {
				System.out.println(i);
				System.out.flush();
			}
		}
		
		return ret;
	}
	
	public double harmonicMean(double x1, double x2) {
		return 2*x1*x2 / (x1+x2);
	}
	
	public double generalizedMean(double x1, double x2, double t) {
		return Math.pow(0.5*Math.pow(x1, t)+0.5*Math.pow(x2, t), 1/t);
	}
	
	public double generalizedMean(double [] xs, double t) {
		double powsum = 0;
		for(int i=0; i<xs.length; i++) {
			powsum += Math.pow(xs[i], t);
		}
		return Math.pow(powsum/xs.length, 1/t);
	}
	
	public double geometricMean(double x1, double x2) {
		return Math.sqrt(x1*x2);
	}
	
	public double geometricMean(double [] xs) {
		double logsum = 0;
		for(int i=0; i<xs.length; i++) {
			logsum += Math.log(xs[i]);
		}
		logsum /= xs.length;
		return Math.exp(logsum);
	}
	

	public static void main(String [] args) {
		GeneralUtility gu = new GeneralUtility();
		String workspace = 
			"C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\small_test_hist_sci\\under_1000\\";
		HashMap<String,String > hm = gu.objectClusterHashMap(workspace + "/object_file_0.txt");
		System.out.println(hm.size());
		System.out.println(hm.get("Albert Einstein :::: A1"));
		
		
		hm.clear();
		hm = null;
		HashMap<String,LinkedList<String []>> hm2 = gu.clusterHashMap(workspace + "/object_file_0.txt");
		System.out.println(hm2.size());
		System.out.println(gu.join(hm2.get("114396").get(0)," :::: ",0,5));
	}
	
}
