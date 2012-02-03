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
