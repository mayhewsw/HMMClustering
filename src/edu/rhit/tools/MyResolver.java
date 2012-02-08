package edu.rhit.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.rhit.cs.cluster.Algorithm;
import edu.rhit.cs.cluster.SimpleSSM;
import edu.washington.cs.uei.disktable.BasicDiskTable;
import edu.washington.cs.uei.scoring.LinearPrecisionRecallCalculator;

public class MyResolver {

	public static String workdir = 
			"/home/stephen/Documents/Classes/Fall2011/NLP/resolver-export/MyReverbData/";
	
	public static String infile = workdir + "randomized_clusters6.txt";
	public static String hypfile = workdir + "clustered_clusters6.txt";
	public static String goldfile = workdir + "converted_clusters6.txt";
	
	public static String sep = " :::: ";
	
	public MyResolver() {
		
	}

	public ArrayList<String []> getData(String clusterFilename) {
		BasicDiskTable clusters = new BasicDiskTable(new File(clusterFilename));
		clusters.open();

		String[] line = clusters.readLine();
		ArrayList<String []> lines = new ArrayList<String[]>();
		
		while (line != null) {
			lines.add(line);
			line = clusters.readLine();
		}

		return lines;
	}
	
	// This is copied almost exactly out of the paper. pp269.
	public int ClusterAlgorithm(){
		// E is all assertions. Array? Arraylist? of String [] ? Only need to iterate. No insert or delete
		ArrayList<String[]> E = getData(infile);
		
		// S is each unique relation or object. Hashset<String>?
		HashSet<String> S = new HashSet<String>();
		
		// Iterate over E, and put each element into S. Hashset will maintain uniqueness.
		for (String[] extraction : E){
			// Each extraction is of the form (obj, rel, obj)
			S.add(extraction[0]);
			S.add(extraction[1]);
			S.add(extraction[2]);
			
		}
		
		// Cluster = list of clusters, HashMap?
		// Elements = list of ?? HashMap?
		HashMap<String, Integer> Cluster = new HashMap<String, Integer> ();
		HashMap<Integer, ArrayList<String>> Elements = new HashMap<Integer, ArrayList<String>>();
		
		int clusterid = 100;
		
		// 1. For each s in S: (I hate Java. What a stupid way to use lists.)
		for(String s : S){
			//System.out.println(s + ", " + clusterid);
			Cluster.put(s, clusterid);
			clusterid++;
			ArrayList<String> elList = new ArrayList<String>();
			elList.add(s);
			Elements.put(Cluster.get(s), elList);
		}
		
		
		// 2. Scores is a list, (HashMap?)
		HashMap<String [], Integer> Scores = new HashMap<String [], Integer>();
		
		//    Index is a list. (HashMap?)
		HashMap<String, ArrayList<Integer>> Index = new HashMap<String, ArrayList<Integer>>();


		// 3. For each extraction in E:
		for (String [] extraction : E){
			// Part 1
			ArrayList<Integer> val1 = new ArrayList<Integer>();
			String property1 = extraction[1] + sep + extraction[2];
			val1.add(Cluster.get(extraction[0]));
			
			// Also add whatever was already in Index[property]
			ArrayList<Integer> ip = Index.get(property1);
			if (ip != null){
				val1.addAll(ip);
			}
			
			Index.put(property1, val1);
			
			// Part 2
			String property2 = extraction[0] + sep + extraction[1];
			ArrayList<Integer> val2 = new ArrayList<Integer>();
			val2.add(Cluster.get(extraction[2]));
			
			// Also add whatever was already in Index[property]

			ip = Index.get(property2);
			if (ip != null){
				val2.addAll(ip);
			}
			
			Index.put(property2, val2);
			
			// Part 3
			String property3 = extraction[0] + sep + extraction[2];
			ArrayList<Integer> val3 = new ArrayList<Integer>();
			val3.add(Cluster.get(extraction[1]));
			
			// Also add whatever was already in Index[property]
			ip = Index.get(property3);
			if (ip != null){
				val3.addAll(ip);
			}
			
			Index.put(property3, val3);
		}
						
		// 4. For each property p in Index
		// Do stuff
		
		// 5. Repeat until no merges can be performed.
		
		return 0;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		MyResolver m = new MyResolver();
		m.ClusterAlgorithm();
		
		System.out.println("We're done man.");
		
		// Do algo work
		//System.out.println("Clustering...");
		
		//Algorithm a = new SimpleSSM();
		//a.DoWork(infile, hypfile);
		
		//System.out.println("Calculating score...");
		// Run test
		//LinearPrecisionRecallCalculator prc = new LinearPrecisionRecallCalculator();
		//prc.printResults(goldfile, hypfile);	

	}

}
