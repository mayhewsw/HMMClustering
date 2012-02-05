package edu.rhit.tools;

import edu.rhit.cs.cluster.Algorithm;
import edu.rhit.cs.cluster.SimpleSSM;
import edu.washington.cs.uei.scoring.LinearPrecisionRecallCalculator;

public class MyResolver {

	public static String workdir = 
			"/home/stephen/Documents/Classes/Fall2011/NLP/resolver-export/MyReverbData/";
	
	public static String infile = workdir + "randomized_clusters6.txt";
	public static String hypfile = workdir + "clustered_clusters6.txt";
	public static String goldfile = workdir + "converted_clusters6.txt";
	
	
	public MyResolver() {
		
	}

	// This is copied almost exactly out of the paper. pp269.
	public int ClusterAlgorithm(){
		// E is all assertions. Array? Arraylist? of String [] ? Only need to iterate. No insert or delete
		// S is each unique relation or object. Hashset<String>?
		// Cluster = list of clusters, HashMap?
		// Elements = list of ?? HashMap?
		
		// 1. For each s in S:
		
		// 2. Scores is a list, (HashMap?) 
		//    Index is a list. (HashMap?)
		
		// 3. For each extraction in E:
		// Do various things.
		
		// 4. For each property p in Index
		// Do stuff
		
		// 5. Repeat until no merges can be performed.
		
		return 0;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Do algo work
		System.out.println("Clustering...");
		
		Algorithm a = new SimpleSSM();
		a.DoWork(infile, hypfile);
		
		System.out.println("Calculating score...");
		// Run test
		LinearPrecisionRecallCalculator prc = new LinearPrecisionRecallCalculator();
		prc.printResults(goldfile, hypfile);	

	}

}
