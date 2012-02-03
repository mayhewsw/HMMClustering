package edu.washington.cs.uei.scoring;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import edu.washington.cs.uei.disktable.BasicDiskTable;


public class LinearPrecisionRecallCalculator {
	
	//private static final String workspace = 
	//	"C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\small_test_hist_sci\\under_1000\\";
	
	//private static final String goldObjectClusterFile   = "object_scoring_clusters.txt";
	//private static final String goldRelationClusterFile = "relation_scoring_clusters.txt";
	
	public static String workdir = 
			"/home/stephen/Documents/Classes/Fall2011/NLP/resolver-export/MyReverbData/";
	
	public static String goldfile = workdir + "converted_clusters5.txt";
	public static String hypfile = workdir + "clustered_clusters5.txt";
	
	public static final int thresholdObjectCount   = 429;
	public static final int thresholdRelationCount = 851;
	
	private int totalClusters = 0;
	private int totalElements = 0;
	
	//private static final int numGoldObjects = 189;
	
	
	public HashSet<HashSet<String>> readInClustering(String clusterFilename)
	{
		HashSet<HashSet<String>> ret = new HashSet<HashSet<String>>();
		
		BasicDiskTable clusters = new BasicDiskTable(new File(clusterFilename));
		clusters.open();
		
		String [] line = clusters.readLine();
		HashSet<String> cluster = new HashSet<String>();
		while(line!=null) {
			
			if(line.length>0 && line[0].trim().length()>0) {
				//System.out.println(join(line, " :: "));
			
				totalElements++;
				
				cluster.add(line[0]);					
			}
			else { 
				if(cluster!=null && cluster.size()>0) {
					ret.add(cluster);
					totalClusters++;
				}
				cluster = new HashSet<String>();
			}
			
			line = clusters.readLine();
		}
		if(cluster!=null && cluster.size()>0) {
			ret.add(cluster);
			totalClusters++;
		}
		
		return ret;		
	}
	
	
	public HashSet<HashSet<String>> readInClustering(
			String clusterFilename, boolean forObjects) 
	{
		int thresholdCount = thresholdObjectCount;
		if(!forObjects) {
			thresholdCount = thresholdRelationCount;
		}
		
		HashSet<HashSet<String>> ret = new HashSet<HashSet<String>>();
		
		BasicDiskTable clusters = new BasicDiskTable(new File(clusterFilename));
		clusters.open();
		
		String [] line = clusters.readLine();
		HashSet<String> cluster = new HashSet<String>();
		boolean aboveThreshold = false;
		while(line!=null) {
			
			if(line.length>0 && line[0].trim().length()>0) {
				//System.out.println(join(line, " :: "));
			
				totalElements++;
				
				cluster.add(line[0]);
				if(line.length>2 &&
				   Integer.parseInt(line[2])>=thresholdCount) 
				{
						aboveThreshold = true;
				}
					
			}
			else { 
				if(cluster!=null && cluster.size()>1) {
					if(aboveThreshold) {
						//System.out.println("Found cluster above threshold:");
						//printCluster(hc);
						ret.add(cluster);
					}
					
					totalClusters++;
				}
				cluster = new HashSet<String>();
				aboveThreshold = false;
			}
			
			line = clusters.readLine();
		}
		if(cluster!=null && cluster.size()>0) {
			if(aboveThreshold) {
				//System.out.println("Found cluster above threshold:");
				//printCluster(hc);
				ret.add(cluster);
				totalClusters++;
			}
		}
		
		
		return ret;
	}
	
	
	private void printCluster(HashSet<String> cluster) 
	{
		for(Iterator<String> it = cluster.iterator(); it.hasNext(); )
		{
			String el = it.next();
			System.out.println(el);
		}
	}
	

	public void printClustering(HashSet<HashSet<String>> clustering) {
		for(Iterator<HashSet<String>> it = clustering.iterator(); it.hasNext(); ) {
			HashSet<String> cluster = it.next();
			printCluster(cluster);
			System.out.println();
		}
	}
	
	
	private class ClusterAndSize {
		HashSet<String> cluster;
		int size;
		
		public ClusterAndSize(HashSet<String> c) {
			cluster = c;
			if(c==null) {
				size = 0;
			}
			else {
				size = c.size();
			}
		}
		
		public int hashCode() {
			return cluster.hashCode() + size;
		}
		
		public boolean equals(Object o) {
			if(o==null || !o.getClass().getName().equals("edu.washington.cs.uei.scoring.LinearPrecisionRecallCalculator$ClusterAndSize")) {
				return false;
			}
			ClusterAndSize cas = (ClusterAndSize)o;
			if(this.cluster==null && cas.cluster!=null) {
				return false;
			}
			if(this.cluster!=null && cas.cluster==null) {
				return false;
			}
			if(this.cluster!=null && !this.cluster.equals(cas.cluster)) {
				return false;
			}
			if(this.size!=cas.size) {
				return false;
			}
			return true;
		}
	}

	private class ClusterSizeComparator implements Comparator<ClusterAndSize>
	{
		public int compare(ClusterAndSize arg0, ClusterAndSize arg1) {
			return arg1.size-arg0.size;
		}
	}
	
	
	private LinkedList<HashSet<String>> sortClustersForSize(HashSet<HashSet<String>> clusters)
	{
		ClusterAndSize [] clustArray = new ClusterAndSize[clusters.size()];
		
		int i=0;
		for(Iterator<HashSet<String>>it = clusters.iterator(); it.hasNext(); ) {
			clustArray[i] = new ClusterAndSize(it.next());
			i++;
		}
		
		ClusterSizeComparator comp = new ClusterSizeComparator();
		
		Arrays.sort(clustArray, comp);
		
		LinkedList<HashSet<String>> ret = new LinkedList<HashSet<String>>();
		for(i=0; i<clustArray.length; i++) {
			ret.add(clustArray[i].cluster);
		}
		
		return ret;
	}
	
	
	// gold clustering may not contain all of the strings
	// rule is:
	// if hypothesis cluster element is contained in SOME gold cluster,
	// 		it counts towards the precision stats
	// if it isn't contained in ANY gold cluster,
	// 		it does not count towards the precision stats
	// hypothesis clustering only contains clusters of size >= 2
	// doesn't matter, since we're only calculating recall for clusters of size >= 2
	// return 6 ints:
	// num correct hyp elements in clusters of size >= 2
	// num hypothesis elements in clusters of size >= 2
	// num hypothesis clusters with size >= 2
	// num found gold elements in clusters of size >= 2
	// num gold elements in clusters of size >= 2
	// num gold clusters with size >= 2
	public int [] getPrecisionRecallStats(HashSet<HashSet<String>> h,
										  HashSet<HashSet<String>> g)
	{
		HashSet<String> hclust, gclust, maxG;
		HashSet<HashSet<String>> usedG = new HashSet<HashSet<String>>(); 
		HashMap<HashSet<String>, Integer> goldToHypMatchSize = 
			new HashMap<HashSet<String>,Integer>();
		
		// note that hyp clustering doesn't contain any clusters of size 1
		// elements are added after they are found to intersect with gold clustering elements
		HashSet<String> allHypElements  = new HashSet<String>();
		
		// note that gold clustering for relations is only a sample of the full 
		// set of clusters & elements
		HashSet<String> allGoldElements = new HashSet<String>();
		for(Iterator<HashSet<String>> it = g.iterator(); it.hasNext(); ) {
			allGoldElements.addAll(it.next());
		}
		
		int maxHScore = 0;
		int HGScore = 0;
		
		int totalCorrectHToG = 0;
		int totalCorrectGToH = 0;
		int totalG = 0;
		int totalH = 0;
		int numGoldClusters = 0;
		int numHypClusters = 0;
		
		LinkedList<HashSet<String>> hypClustersSortedForSize =
			sortClustersForSize(h);
		
		int hClustSize = 0;
		String firstOne = null;
		for(Iterator<HashSet<String>> itHypClusters = hypClustersSortedForSize.iterator(); 
			itHypClusters.hasNext(); ) 
		{
			hclust = itHypClusters.next();
			maxHScore = 0;
			maxG = null;
			
			hClustSize = 0;
			for(Iterator<String> itHyp = hclust.iterator(); itHyp.hasNext(); ) {
				String el = itHyp.next();
				if(allGoldElements.contains(el)) {
					hClustSize++;
					if(hClustSize==1) {
						firstOne = el;
					}
					else if(hClustSize==2) {
						allHypElements.add(firstOne);
						allHypElements.add(el);
					}
					else {
						allHypElements.add(el);
					}
				}
			}
			if(hClustSize<2) {
				continue;
			}
			totalH += hClustSize;
			numHypClusters++;
			
			for(Iterator<HashSet<String>> itGClusters = g.iterator(); itGClusters.hasNext(); ) {
				gclust = itGClusters.next();
				HGScore = 0;
				
				if(usedG.contains(gclust)) {
					continue;
				}
				
				for(Iterator itHyp = hclust.iterator(); itHyp.hasNext(); ) {
					if(gclust.contains(itHyp.next())) {
						HGScore++;
					}
				}
				
				if(HGScore > maxHScore) {
					maxHScore = HGScore;
					maxG = gclust;
				}
			}
						
			if(maxG!=null) {
				usedG.add(maxG);
				goldToHypMatchSize.put(maxG, new Integer(maxHScore));
			}
			
			totalCorrectHToG += maxHScore;
		}
		
		int clusterSize = 0;
		for(Iterator<HashSet<String>> itGoldClusters = g.iterator(); itGoldClusters.hasNext(); ) {
			gclust = itGoldClusters.next();
			clusterSize = gclust.size();
			if(clusterSize<2) {
				continue;
			}
			totalG += clusterSize;
			numGoldClusters++;
			
			if(goldToHypMatchSize.containsKey(gclust)) {
				totalCorrectGToH += goldToHypMatchSize.get(gclust).intValue();
			}
			else {
				// give gold cluster credit for having one element found in hypothesis clustering
				// IF at least one gold cluster element is contained in a singleton hyp cluster
				for(Iterator<String> gIt = gclust.iterator(); gIt.hasNext(); ) {
					if(!allHypElements.contains(gIt.next())) {
						totalCorrectGToH++;
						break;
					}
				}
			}
		}
		
		// return num correct hyp elements, num hyp elements, num hyp clusters,
		// num found gold elements, num gold elements, num gold clusters
		// same for over2 versions
		int [] ret = new int [6];
		ret[0]  = totalCorrectHToG;
		ret[1]  = totalH;
		ret[2]  = numHypClusters;
		ret[3]  = totalCorrectGToH;
		ret[4]  = totalG;
		ret[5]  = numGoldClusters;
		return ret;
	}
	
	
	public void writePrecisionRecallStats(
			int [] stats, 
			String param, 
			int mergeIter,
			BasicDiskTable out,
			boolean compObject)
	{
		double precision = (stats[0] / ((double)stats[1]));
		double recall    = (stats[3] / ((double)stats[4]));
		double F1        = 2 * precision * recall / (precision + recall);
		
		System.out.println("input parameter:    " + param);
		System.out.println("merge iteration:    " + mergeIter);
		System.out.println("num correct hyp:    " + stats[0]);
		System.out.println("num hyp elements:   " + stats[1]);
		System.out.println("num found gold:     " + stats[3]);
		System.out.println("num gold elements:  " + stats[4]);
		System.out.println("precision =         " + precision);
		System.out.println("recall =            " + recall);
		System.out.println("F1 =                " + F1);
		System.out.println("num hyp clusters:   " + stats[2]);
		System.out.println("num gold clusters:  " + stats[5]);
		
		out.openForWriting(true);
		String objRel = "OBJ";
		if(!compObject) objRel = "REL";
		String [] outline = {objRel, 
							 String.valueOf(param),
							 String.valueOf(mergeIter),
							 String.valueOf(stats[0]),
							 String.valueOf(stats[1]),
							 String.valueOf(stats[3]),
							 String.valueOf(stats[4]),
							 String.valueOf(precision),
							 String.valueOf(recall),
							 String.valueOf(F1),
							 String.valueOf(stats[2]),
							 String.valueOf(stats[5])};
//							 "Over2",
//							 String.valueOf(stats[6]),
//							 String.valueOf(stats[7]),
//							 String.valueOf(stats[9]),
//							 String.valueOf(stats[10]),
//							 String.valueOf(precisionOver2),
//							 String.valueOf(recallOver2),
//							 String.valueOf(F1Over2),
//							 String.valueOf(stats[8]),
//							 String.valueOf(stats[11])};
		out.println(outline);
		out.flush();
		out.closeForWriting();		
	}
	
	public void calculatePR(HashSet<HashSet<String>> h, 
							HashSet<HashSet<String>> g,
							String param,
							int mergeIter,
							BasicDiskTable out, 
							boolean compObject)
	{
		
		HashSet<String> hclust, gclust, maxG;
		HashSet<HashSet<String>> usedG = new HashSet<HashSet<String>>(); 
		HashMap<HashSet<String>, Integer> goldToHypMatchSize = 
			new HashMap<HashSet<String>,Integer>();
		
		int maxHScore = 0;
		int HGScore = 0;
		int totalCorrectHToG = 0;
		int totalCorrectGToH = 0;
		int totalG = 0;
		int totalH = 0;
		int numGoldClusters = g.size();
		int numHypClusters = h.size();
		
		boolean matchesGoldCluster = false;
		
		/*
		for(Iterator<HashSet<String>> itGClusters = g.iterator(); itGClusters.hasNext(); ) {
			totalG += itGClusters.next().size();
		}*/
		
		LinkedList<HashSet<String>> hypClustersSortedForSize =
			sortClustersForSize(h);
		
		for(Iterator<HashSet<String>> itHypClusters = hypClustersSortedForSize.iterator(); 
			itHypClusters.hasNext(); ) 
		{
			hclust = itHypClusters.next();
			maxHScore = 0;
			maxG = null;
			matchesGoldCluster = false;
			
			for(Iterator<HashSet<String>> itGClusters = g.iterator(); itGClusters.hasNext(); ) {
				gclust = itGClusters.next();
				HGScore = 0;
				
				for(Iterator itHyp = hclust.iterator(); itHyp.hasNext(); ) {
					if(gclust.contains(itHyp.next())) {
						HGScore++;
					}
				}
				
				if(HGScore > maxHScore) {
					if(!usedG.contains(gclust)) {
						maxHScore = HGScore;
						maxG = gclust;
					}
					matchesGoldCluster = true;
				}
			}
			
			/*
			if(maxHScore>0) {
				totalH += hclust.size();
			}*/
			totalH += hclust.size();
			
			if(maxG!=null) {
				//System.out.println("Found matching clusters:");
				//printCluster(maxG);
				//System.out.println();
				//printCluster(hclust);
				//System.out.println();
				
				usedG.add(maxG);
				goldToHypMatchSize.put(maxG, new Integer(maxHScore));
			}
			else {
				//System.out.println("Found no matching cluster:");
				//printCluster(hclust);
				//System.out.println();
			}
			
			if(!matchesGoldCluster) {
				maxHScore = 1;
			}
					
			totalCorrectHToG += maxHScore;
		}
		
		for(Iterator<HashSet<String>> itGoldClusters = g.iterator(); itGoldClusters.hasNext(); ) {
			gclust = itGoldClusters.next();
			totalG += gclust.size();
			
			if(goldToHypMatchSize.containsKey(gclust)) {
				totalCorrectGToH += goldToHypMatchSize.get(gclust).intValue();
			}
			else {
				totalCorrectGToH += 1;
			}
		}
		
		double precision = (totalCorrectHToG / ((double)totalH));
		double recall    = (totalCorrectGToH / ((double)totalG));
		double F1        = 2 * precision * recall / (precision + recall); 
		
		System.out.println("input parameter:    " + param);
		System.out.println("merge iteration:    " + mergeIter);
		System.out.println("num gold clusters:  " + numGoldClusters);
		System.out.println("num hyp clusters:   " + numHypClusters);
		System.out.println("num gold elements:  " + totalG);
		System.out.println("num hyp elements:   " + totalH);
		System.out.println("num correct hyp:    " + totalCorrectHToG);
		System.out.println("num found gold:     " + totalCorrectGToH);
		System.out.println("precision =         " + precision);
		System.out.println("recall =            " + recall);
		System.out.println("F1 =                " + F1);
		
		out.openForWriting(true);
		String objRel = "OBJ";
		if(!compObject) objRel = "REL";
		String [] outline = {objRel, 
							 String.valueOf(param),
							 String.valueOf(mergeIter),
							 String.valueOf(numGoldClusters),
							 String.valueOf(numHypClusters),
							 String.valueOf(totalG),
							 String.valueOf(totalH),
							 String.valueOf(totalCorrectHToG),
							 String.valueOf(totalCorrectGToH),
							 String.valueOf(precision),
							 String.valueOf(recall),
							 String.valueOf(F1)};
		out.println(outline);
		out.flush();
		out.closeForWriting();
	}
	
	
	private void writeGradedCluster(
			HashSet<String> hclust, 
			HashSet<String> gclust, 
			HashSet<String> gEls, 
			BasicDiskTable out)
	{
		out.printFlatLine("");
		String str;
		String [] outline = new String[2];
		boolean foundFirstOne = false;
		boolean printedFirstOne = false;
		String [] firstOne = new String[2];
		for(Iterator<String> hIt = hclust.iterator(); hIt.hasNext(); )
		{
			str = hIt.next();
			if(gEls.contains(str)) {
				if(!foundFirstOne) {
					foundFirstOne = true;
					firstOne[0] = str;
					firstOne[1] = (gclust!=null && gclust.contains(str)) ? 
							"correct" : "wrong";
				}
				else {
					if(!printedFirstOne) {
						printedFirstOne = true;
						out.println(firstOne);
					}
					outline[0] = str;
					outline[1] = (gclust!=null && gclust.contains(str)) ? 
							"correct" : "wrong";
					out.println(outline);
				}
			}
		}
		out.printFlatLine("");
	}
	
	public void printMarkedClusters(
			HashSet<HashSet<String>> hyp,
			HashSet<HashSet<String>> gold, 
			String gradedHyp)
	{
		BasicDiskTable graded = new BasicDiskTable(new File(gradedHyp));
		graded.openForWriting();
		
		HashSet<String> hclust, gclust, maxG;
		HashSet<HashSet<String>> usedG = new HashSet<HashSet<String>>(); 
		
		// note that gold clustering for relations is only a sample of the full 
		// set of clusters & elements
		HashSet<String> allGoldElements = new HashSet<String>();
		for(Iterator<HashSet<String>> it = gold.iterator(); it.hasNext(); ) {
			allGoldElements.addAll(it.next());
		}
		
		int maxHScore = 0;
		int HGScore = 0;
		
		int totalHOver2 = 0;
		
		LinkedList<HashSet<String>> hypClustersSortedForSize =
			sortClustersForSize(hyp);
		
		int hClustSize = 0;
		for(Iterator<HashSet<String>> itHypClusters = hypClustersSortedForSize.iterator(); 
			itHypClusters.hasNext(); ) 
		{
			hclust = itHypClusters.next();
			maxHScore = 0;
			maxG = null;
			
			hClustSize = 0;
			for(Iterator<String> itHyp = hclust.iterator(); itHyp.hasNext(); ) {
				String el = itHyp.next();
				if(allGoldElements.contains(el)) {
					hClustSize++;
				}
			}
			if(hClustSize<2) {
				continue;
			}
			
			totalHOver2 += hClustSize;
			
			for(Iterator<HashSet<String>> itGClusters = gold.iterator(); itGClusters.hasNext(); ) {
				gclust = itGClusters.next();
				HGScore = 0;
				
				if(usedG.contains(gclust)) {
					continue;
				}
				
				for(Iterator itHyp = hclust.iterator(); itHyp.hasNext(); ) {
					if(gclust.contains(itHyp.next())) {
						HGScore++;
					}
				}
				
				if(HGScore > maxHScore) {
					maxHScore = HGScore;
					maxG = gclust;
				}
			}
						
			if(maxG!=null) {
				usedG.add(maxG);

			}
			writeGradedCluster(hclust, maxG, allGoldElements, graded);
		}
		
		System.out.println("totalHOver2 = " + totalHOver2);
		
		graded.flush();
		graded.closeForWriting();
	}
	
	public void printResults(String goldFileName, String hypFileName) {
		LinearPrecisionRecallCalculator prc = new LinearPrecisionRecallCalculator();
		
		boolean isObjects = false;
		
		String hypClusterFile = goldFileName;
		String goldClusterFile = hypFileName;
		
		int totalHypCl, totalHypEl;
		HashSet<HashSet<String>> hyp  = prc.readInClustering(hypClusterFile);
		totalHypCl = prc.totalClusters;
		totalHypEl = prc.totalElements;

		System.out.println("Total Hypothesis Clusters = " + totalHypCl);
		System.out.println("Total Hypothesis Elements = " + totalHypEl);

		HashSet<HashSet<String>> gold = prc.readInClustering(goldClusterFile);

		
		String param = "5"; 
		
		int mergeIteration = 4; 
		BasicDiskTable out = new BasicDiskTable(new File("justdeletemeOK.txt"));
		
		// Renames hypothesis_clusters.txt as hypothesis_clusters_marked.txt 
		String markedHypFile = hypClusterFile.substring(0,hypClusterFile.length()-4)+"_marked.txt";
		
		int [] precisionRecallStats = prc.getPrecisionRecallStats(hyp, gold);
		prc.printMarkedClusters(hyp, gold, markedHypFile);
		prc.writePrecisionRecallStats(
				precisionRecallStats, 
				param, 
				mergeIteration, 
				out, 
				isObjects);
		//prc.calculatePR(hyp, gold, param, mergeIteration, out, isObjects);
	}
	
	
	
	/*
	 * params
	 * hypothesis object clusters file 0
	 * hypothesis relation clusters file 1
	 * gold object clusters file 2
	 * gold relation clusters file 3
	 * object threshold parameter 4
	 * relation threshold parameter 5
	 * merge iteration 6
	 * output file 7
	 * 
	 */
	public static void main(String[] args) {
		LinearPrecisionRecallCalculator prc = new LinearPrecisionRecallCalculator();
		
		boolean isObjects = false; //args[5].equals("objects");
		
		String hypClusterFile = goldfile;
		String goldClusterFile = hypfile;
		
		int totalHypCl, totalHypEl;
		//HashSet<HashSet<String>> hyp  = prc.readInClustering(args[0], isObjects);
		HashSet<HashSet<String>> hyp  = prc.readInClustering(hypClusterFile);
		totalHypCl = prc.totalClusters;
		totalHypEl = prc.totalElements;

		System.out.println("Total Hypothesis Clusters = " + totalHypCl);
		System.out.println("Total Hypothesis Elements = " + totalHypEl);

		HashSet<HashSet<String>> gold = prc.readInClustering(goldClusterFile);

		
		String param = "5"; //args[2];
		
		int mergeIteration = 4; //Integer.parseInt(args[3]);
		BasicDiskTable out = new BasicDiskTable(new File("justdeletemeOK.txt"));
		
		// Renames hypothesis_clusters.txt as hypothesis_clusters_marked.txt 
		String markedHypFile = hypClusterFile.substring(0,hypClusterFile.length()-4)+"_marked.txt";
		
		int [] precisionRecallStats = prc.getPrecisionRecallStats(hyp, gold);
		prc.printMarkedClusters(hyp, gold, markedHypFile);
		prc.writePrecisionRecallStats(
				precisionRecallStats, 
				param, 
				mergeIteration, 
				out, 
				isObjects);
		//prc.calculatePR(hyp, gold, param, mergeIteration, out, isObjects);
	}

}