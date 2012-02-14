package edu.rhit.cs.cluster;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import edu.rhit.tools.Cluster;
import edu.rhit.tools.NgramLM;
import edu.washington.cs.uei.disktable.BasicDiskTable;
import edu.washington.cs.uei.util.GeneralUtility;

public class Ngram implements Algorithm {

	public static String workdir = 
			"/home/stephen/Documents/Classes/Fall2011/NLP/resolver-export/MyReverbData/";
	
	public static String infile = workdir + "randomized_clusters6.txt";
	public static String outfile = workdir + "clustered_clusters6.txt";
	
	
	ArrayList<String[]> lines;

	public Ngram() {
		this.lines = new ArrayList<String[]>();
	}

	public HashSet<Cluster> divideToClusters(int whichFeature) {
		if (lines == null) {
			System.out.println("Uh-oh : null clusters...prepare for explosion...");
		}

		HashSet<Cluster> ret = new HashSet<Cluster>();

		ArrayList<Integer> ignoreUs = new ArrayList<Integer>();
		
		// Find every pair
		for (int i = 0; i < lines.size() - 1; i++) {
			if (ignoreUs.contains(i)) {
				continue;
			}
			ignoreUs.add(i);
			Cluster c = new Cluster();
			NgramLM model = new NgramLM();
			
			c.addStringNoConvert(GeneralUtility.join(lines.get(i), " :::: ", 0, lines.get(i).length-1));

			// Create the ngram model for the i string
			String istring = GeneralUtility.join(lines.get(i), " ", 0, 2);
			// Start with higher N in ngrams
			model.trainString(istring);
			
			
			for (int j = i + 1; j < lines.size(); j++) {
				if (ignoreUs.contains(j)) {
					continue;
				}
				
				// compare j against the ngram model for i
				String jstring = GeneralUtility.join(lines.get(j), " ", 0, 2);
				
				// If j is within the correct boundary??? cluster it.
				
				
				// If j is clustered.
				ignoreUs.add(j);
				// update ngram model with new data from j string

			}
			ret.add(c);
		}

		return ret;
	}

	/*
	 * Return value is error code This will read in values from a cluster
	 * 
	 * @see edu.rhit.cs.cluster.AlgorithmInterface#getData()
	 */
	@Override
	public int getData(String clusterFilename) {
		BasicDiskTable clusters = new BasicDiskTable(new File(clusterFilename));
		clusters.open();

		String[] line = clusters.readLine();

		while (line != null) {
			lines.add(line);
			line = clusters.readLine();
		}

		return 0;
	}
	
	/*
	 * Given three hashsets of clusters, this returns a single hashset of clusters that
	 * has merged all three. That is: a line gets added to the result if and only if
	 * each of it's components (arg1, rel, arg1) are in the same clusters in their 
	 * respective hashsets. 
	 */
	private HashSet<Cluster> transitivity(HashSet<Cluster> obj1clusters,
			HashSet<Cluster> relclusters, HashSet<Cluster> obj2clusters) {
		HashSet<Cluster> clusters = new HashSet<Cluster>();
		
		ArrayList<Integer> ignoreUs = new ArrayList<Integer>();
		
		// loop through lines
		// two objects are clustered iff for both: arg1 is in the same cluster as arg2, etc.
		for(int i = 0; i < lines.size(); i++){
			if (ignoreUs.contains(i)){
				continue;
			}
			ignoreUs.add(i);
			
			Cluster c = new Cluster();
			String iLine = GeneralUtility.join(lines.get(i), " :::: ", 0, lines.get(i).length-1);
			c.addStringNoConvert(iLine);
			
			for (int j = i+1; j < lines.size(); j++){
				if (ignoreUs.contains(j)) {
					continue;
				}
				
				String jLine = GeneralUtility.join(lines.get(j), " :::: ", 0, lines.get(j).length-1);
				
				// Check obj1clusters, relclusters, obj2clusters to see if 
				// iLine and jLine are in the same clusters in each.
				// If so, add these lines i and j to clusters. Then add j to ignoreus
				boolean objs1 = stringsInSameCluster(iLine, jLine, obj1clusters);
				boolean rels = stringsInSameCluster(iLine, jLine, relclusters);
				boolean objs2 = stringsInSameCluster(iLine, jLine, obj2clusters);
				if (objs1 && rels && objs2){
					c.addStringNoConvert(jLine);
					ignoreUs.add(j);
				}
			}
			clusters.add(c);
					
		}
		
		
		return clusters;
	}
	
	public boolean stringsInSameCluster(String s1, String s2, HashSet<Cluster> hc){
		
		for (Iterator<Cluster> it = hc.iterator(); it.hasNext();) {
			Cluster c = it.next();
			if (c.contains(s1) && c.contains(s2)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public int produceOutput(HashSet<Cluster> clusters, String outfile) {
		// This will write the lines to a file.
		try {
			FileWriter outFile = new FileWriter(outfile);
			PrintWriter out = new PrintWriter(outFile);

			// Also could be written as follows on one line
			// Printwriter out = new PrintWriter(new FileWriter(args[0]));

			for (Iterator<Cluster> it = clusters.iterator(); it.hasNext();) {
				Cluster t = it.next();
				String string_clust = t.toString();
				out.println(string_clust);
			}
			
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	
	public void DoWork(String infile, String outfile){
		Ngram s = new Ngram();
		s.getData(infile);
		HashSet<Cluster> obj1clusters = s.divideToClusters(0);
		HashSet<Cluster> relclusters = s.divideToClusters(1);
		HashSet<Cluster> obj2clusters = s.divideToClusters(2);
		
		String dir = "/home/stephen/Documents/Classes/Fall2011/NLP/resolver-export/MyReverbData/";
		s.produceOutput(obj1clusters, dir + "obj1clusters.txt");
		s.produceOutput(relclusters, dir + "relclusters.txt");
		s.produceOutput(obj2clusters, dir + "obj2clusters.txt");
				
		
		HashSet<Cluster> clusters = s.transitivity(obj1clusters, relclusters, obj2clusters);
		
		s.produceOutput(clusters, outfile);
	}
	
	


	public static void main(String[] args) {
		Ngram s = new Ngram();
		s.getData(infile);
		HashSet<Cluster> clusters = s.divideToClusters(0);
		s.produceOutput(clusters, outfile);
		System.out.println("Done");
		
//		ArrayList<String []> a = new ArrayList<String []>();
//		String [] s1 = {"ab", "e ", "li", "nc", "ol", "n ", "be", " b", "ea", "r ", "on", " f", "eb", "ru", "ar", "y ", "12", ", ", "18", "09"};
//		String [] s2 = {"ab", "e ", "li", "nc", "ol" ,"n ", "be", " b", "ea", "r ", "in", " 18", "09"};
//		String [] s3 = {"GE", "ZZ"};
//		a.add(s1);
//		a.add(s2);
//		a.add(s3);

	}

}
