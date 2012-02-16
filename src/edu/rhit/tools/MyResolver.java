package edu.rhit.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.rhit.cs.cluster.SimpleSSM;
import edu.washington.cs.uei.disktable.BasicDiskTable;
import edu.washington.cs.uei.scoring.LinearPrecisionRecallCalculator;
import edu.washington.cs.uei.util.GeneralUtility;

public class MyResolver {

	public static String workdir = 
			"/home/stephen/Documents/Classes/Fall2011/NLP/resolver-export/MyReverbData/";
	
	public static String infile = workdir + "randomized_clusters6.txt";
	public static String hypfile = workdir + "clustered_clusters6.txt";
	public static String goldfile = workdir + "converted_clusters6.txt";
	
	public static String sep = " :::: ";
	
	public static int Max = 50;
	public static float threshold = 20;
	
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
	
	/*
	 * This will return all pairs given a list of id's. 
	 * For example:
	 * in: {3, 5, 8}
	 * out: {(3, 5), (3, 8), (5, 8)}
	 * except each integer in each pair is the associated string
	 * as decided by the elements in Cluster
	 */
	private ArrayList<Tuple> getPairs(ArrayList<Integer> propList, HashMap<String, Integer> Cluster) {
		ArrayList<Tuple> allPairs = new ArrayList<Tuple>();
		for(int i = 0; i < propList.size(); i++){
			for(int j = i+1; j < propList.size(); j++){
				
				String a = getKeyFromValue(Cluster, propList.get(i));
				String b = getKeyFromValue(Cluster, propList.get(j));
				
				allPairs.add(new Tuple(a, b));
			}
			
		}
		return allPairs;
	}
	
	
	private String getKeyFromValue(HashMap<String, Integer> Cluster, int value){
		if(!Cluster.containsValue(value)){
			System.out.println("ERROR: Cluster doesn't contain this value.");
			return null;
		}
		
		
		ArrayList<String> keys = new ArrayList<String>();
		
		for(String key : Cluster.keySet()){
			if (Cluster.get(key) == value){
				keys.add(key);
			}
		}
		
		if(keys.size() > 1){
			System.out.println("ERROR: more than one property for this Cluster ID.");
		}
		
		return keys.get(0);
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
		HashMap<Tuple, Float> Scores = new HashMap<Tuple, Float>();
		
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
		for (Map.Entry<String, ArrayList<Integer>> entry : Index.entrySet()) {
		    //String property = entry.getKey();
		    ArrayList<Integer> propList = entry.getValue();
		    
		    if (propList.size() < Max){
		    	ArrayList<Tuple> allPairs = getPairs(propList, Cluster); 
		    	// For each pair in propList
		    	
		    	// Get the strings in that pair, get similarity
		    	// Store it in Scores
		    	for (Tuple t : allPairs){
		    		float getScore = similarity(t);
		    		Scores.put(t, getScore);
		    	}

		    	// For debugging. 
//		    	if (propList.size() > 2){
//		    		System.out.println("of interest");
//		    	}	
		    }
		}
		
		// 5. Repeat until no merges can be performed.
		boolean moreMerges = true;
		while(moreMerges){
			// Sort scores
			ArrayList<Tuple> sortedScores = sortByValueArray(Scores); // could this just get the smallest?
			LinkedHashMap<Tuple, Float> testing = (LinkedHashMap<Tuple, Float>) sortByValue(Scores);
			
			HashSet<Integer> usedclusters = new HashSet<Integer>();
			
			boolean cont = true;

			// Since sortedScores are sorted ascending, the smallest scores
			// are at the beginning. Also, once we reach a score that is
			// too large, then all following scores are also too large.
			int i = 0;
			Tuple currTup;
			float currScore;
			while(cont){
				// do stuff
				currTup = sortedScores.get(i);
				currScore = Scores.get(currTup);
				
				if (currScore > threshold){
					cont = false;
				}
				
				// Now do merging of two strings in currTup.
				// DUUDE
				String c1String = currTup.s1;
				String c2String = currTup.s2;
				
				int c1 = Cluster.get(c1String);
				int c2 = Cluster.get(c2String);
				
				if (!usedclusters.contains(c1) && !usedclusters.contains(c2)){
					// update elements
					ArrayList<String> c1elems = Elements.get(c1);
					ArrayList<String> c2elems = Elements.get(c2);
					
					c1elems.addAll(c2elems);
					Elements.put(c1, c1elems);

					// Update clusters (loop)
					for(String s : c2elems){
						Cluster.put(s, c1);
					}
					
					// delete c2 from Elements
					Elements.remove(c2);
					
					// Update used clusters
					usedclusters.add(c1);
					usedclusters.add(c2);
				}
				
				
				// Important!
				i++;
			}
			
			
			moreMerges = false;
		}
		//HashMap<Integer, ArrayList<String>> newCluster = reverseClusters(Cluster);

		HashSet<Cluster> newclusters = transitivity2(E, Cluster);
		produceOutput(newclusters, hypfile);
		
		return 0;
	}
	
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
	
	public HashSet<Cluster> transitivity2(ArrayList<String []> E, HashMap<String, Integer> Cluster){
		HashSet<Cluster> clusters = new HashSet<Cluster>();
		
		// Algorithm for dividing into clusters.
		ArrayList<Integer> ignoreus = new ArrayList<Integer>();
		for (int i = 0; i < E.size(); i++){
			if(ignoreus.contains(i)){
				continue;
			}
			ignoreus.add(i);
			
			Cluster c = new Cluster();
			String iLine = GeneralUtility.join(E.get(i), " :::: ");
			c.addStringNoConvert(iLine);
			
			for(int j = i+1; j < E.size(); j++){
				if(ignoreus.contains(j)){
					continue;
				}
				
				String jLine = GeneralUtility.join(E.get(j), " :::: ");
				
				boolean stillgood = true;
				for(int k = 0; k < 3; k++){
					stillgood = stillgood && (Cluster.get(E.get(j)[k]) == Cluster.get(E.get(i)[k]));
				}
				if (stillgood){
					c.addStringNoConvert(jLine);
					ignoreus.add(j);
				}
				
			}
			clusters.add(c);
		}
		
		return clusters;
	}
	
	/*
	 * This takes a HashMap<String, Integer>, and switches the order around, returning a hashmap 
	 * whose keys are Ints and whose values are string ArrayLists (in the expected case where
	 * several strings map to a single integer). 
	 */
	public static HashMap<Integer, ArrayList<String>> reverseClusters(HashMap<String, Integer> cluster){
		HashMap<Integer, ArrayList<String>> reversed = new HashMap<Integer, ArrayList<String>>();
		
		// Loop through clusters and if reverse it so all elements that have the same cluster id are clustered.
		for(Entry<String, Integer> e : cluster.entrySet()){
			String Ckey = e.getKey();
			Integer Cvalue = e.getValue();
			
			if(reversed.containsKey(Cvalue)){
				// add
				ArrayList<String> s = reversed.get(Cvalue);
				s.add(Ckey);
				reversed.put(Cvalue, s);
					
			}else{
				// Create new entry in reversed
				ArrayList<String> s = new ArrayList<String>();
				s.add(Ckey);
				reversed.put(Cvalue, s);
			}
			
		}
		
		return reversed;
		
	}
	
	public void separateToClusters(HashMap<Integer, ArrayList<String>> reversed){
		// first transitivity, then write to file.
		
	}
	
	
	// This was taken from 
	//http://stackoverflow.com/questions/7965132/java-sort-hashmap-by-value
	// With some tweaking.
	public static Map<Tuple, Float> sortByValue(HashMap<Tuple, Float> map) {
        List<Map.Entry<Tuple, Float>> list = new LinkedList<Map.Entry<Tuple, Float>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Tuple, Float>>() {

            public int compare(Map.Entry<Tuple, Float> m1, Map.Entry<Tuple, Float> m2) {
            	boolean ascending = true;
            	if (ascending){
                    return (m1.getValue()).compareTo(m2.getValue());
            	}
            	else{
                    return (m2.getValue()).compareTo(m1.getValue());            		
            	}
            }
        });

        Map<Tuple, Float> result = new LinkedHashMap<Tuple, Float>();
        for (Map.Entry<Tuple, Float> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
	
	/*
	 * This takes a HashMap<Tuple, Float> and sorts it (descending) and returns an arraylist
	 * which has all the keys (Tuples) in the correct sorted order.
	 */
	public static ArrayList<Tuple> sortByValueArray(HashMap<Tuple, Float> map) {
        List<Map.Entry<Tuple, Float>> list = new LinkedList<Map.Entry<Tuple, Float>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Tuple, Float>>() {

            public int compare(Map.Entry<Tuple, Float> m1, Map.Entry<Tuple, Float> m2) {
            	boolean ascending = true;
            	if (ascending){
                    return (m1.getValue()).compareTo(m2.getValue());
            	}
            	else{
                    return (m2.getValue()).compareTo(m1.getValue());            		
            	}
            }
        });

        ArrayList<Tuple> result = new ArrayList<Tuple>();
        for (Map.Entry<Tuple, Float> entry : list) {
            result.add(entry.getKey());
        }
        return result;
    }

	private float similarity(Tuple t) {
		int d = SimpleSSM.LevenshteinDistance(t.s1, t.s2);
		// These numbers and equations are taken from page 262
		float alpha = 20;
		float beta = 5;
		return (alpha * d + 1) / (alpha + beta);
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
		LinearPrecisionRecallCalculator prc = new LinearPrecisionRecallCalculator();
		prc.printResults(goldfile, hypfile);	

	}

}
