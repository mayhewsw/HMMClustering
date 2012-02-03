package edu.washington.cs.uei.scoring;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import edu.washington.cs.uei.disktable.BasicDiskTable;
import edu.washington.cs.uei.util.GeneralUtility;

public class PrecisionRecallByNumberOfExtractions {

	GeneralUtility gu = new GeneralUtility();
	
	private static final String workspace = 
		"C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\small_test_hist_sci\\under_1000\\jair_resolver\\";
	
	private static final String goldObjectClusterFile   = "object_scoring_clusters_artificial.txt";
	private static final String goldRelationClusterFile = "relation_scoring_clusters_artificial.txt";
	
	private static final int numPRCurveParameters   = 8;
	
	private void addPairs(HashSet<String []> hc, HashSet<Pair> hs) {
		for(Iterator it=hc.iterator(); it.hasNext(); ) {
			String [] p1 = (String []) it.next();
		
			boolean foundMatch = false;
			for(Iterator it2 = hc.iterator(); it2.hasNext(); ) {
				String [] p2 = (String []) it2.next();
				
				if(p1[0].equals(p2[0])) {
					foundMatch = true;
				}
				else if(foundMatch) {
					/*
					String [][] pair = new String[2][];
					pair[0] = p1;
					pair[1] = p2;
					*/
					Pair pair = new Pair(p1, p2);
					hs.add(pair);
				}
			}
		}
	}
	
	/*
	 * This function converts a HashSet of strings into a HashSet of pairs.
	 * What is the form of string arrays? [?, ?, int, ..., ..., ?]
	 * 
	 */
	private void addPairs(HashSet<String []> hc, HashSet<Pair> hs, int minCount) {
		int count1 = 0;
		int count2 = 0;
		for(Iterator it=hc.iterator(); it.hasNext(); ) {
			String [] p1 = (String []) it.next();
			count1 = Integer.parseInt(p1[2]);
			
			boolean foundMatch = false;
			for(Iterator it2 = hc.iterator(); it2.hasNext(); ) {
				String [] p2 = (String []) it2.next();
				count2 = Integer.parseInt(p2[2]);
				
				if(p1[0].equals(p2[0])) {
					foundMatch = true;
				}
				else if(foundMatch && (count1>=minCount || count2>=minCount)) {
					/*
					String [][] pair = new String[2][];
					pair[0] = p1;
					pair[1] = p2;
					*/
					Pair pair = new Pair(p1, p2);
					hs.add(pair);
				}
			}
		}
	}
	
	public HashSet<Pair> readInClustering(String clusterFilename, int minCount)
	{
		HashSet<Pair> ret = new HashSet<Pair>();
		
		BasicDiskTable clusters = new BasicDiskTable(new File(clusterFilename));
		clusters.open();
		
		// Reads lines separated by _sep_ = " :::: "
		String [] line = clusters.readLine();
		HashSet<String []> hc = new HashSet<String []>();
		while(line!=null) {
			// If the line is not empty.
			if(line.length>0 && 
					line[0].trim().length()>0) {
				
				hc.add(line);
			}
			// Line is empty. 
			else { 
				if(hc!=null && hc.size()>1) {
					addPairs(hc, ret, minCount);
				}
				hc = new HashSet<String []>();
			}
			
			line = clusters.readLine();
		}
		
		// ??
		if(hc!=null && hc.size()>1) {
			addPairs(hc, ret, minCount);
		}
		
		return ret;
	}
	
	private String getArgFromEntityName(String entity) {
		return Character.isUpperCase(entity.charAt(0)) ? 
				GeneralUtility._ARG1_ : 
				GeneralUtility._REL_;
	}
	
	private HashSet<String> readInClusterStrings(String clusterFilename, int minCount)
	{
		BasicDiskTable clusters = new BasicDiskTable(new File(clusterFilename));
		clusters.open();
		
		HashSet<String> ret = new HashSet<String>();
		
		for(String[] line = clusters.readLine(); line!=null; line = clusters.readLine()) {
			if(line.length>0 && line[0].trim().length()>0) {
				ret.add(line[0]);
			}
		}
		
		clusters.close();
		
		/*
		System.out.println("cluster strings:");
		for(Iterator<String> it = ret.iterator(); it.hasNext(); ) {
			System.out.println(it.next());
		}
		System.out.println("\n");
		*/
		
		return ret;
	}
	
	public HashSet<Pair> readInMergePairs(
			String mergeFilename, 
			double threshold, 
			int minCount, 
			boolean objects, 
			HashSet<String> compStrs)
	{
		HashSet<Pair> ret = new HashSet<Pair>();
		BasicDiskTable in = new BasicDiskTable(new File(mergeFilename));
		
		String compArg = objects ? GeneralUtility._ARG1_ : GeneralUtility._REL_;
		in.open();
		String [] p1 = null;
		String [] p2 = null;
		Pair p       = null;
		double score = 0;
		int count1   = 0;
		int count2   = 0;
		for(String [] line = in.readLine(); line!=null; line = in.readLine()) {
			score = Double.parseDouble(line[4]);
			count1 = Integer.parseInt(line[2]);
			count2 = Integer.parseInt(line[3]);
			if(score>=threshold && 
					compStrs.contains(line[5]) && 
					compStrs.contains(line[6]) &&
					(count1 >= minCount || count2 >= minCount)) {
				p1 = new String[6];
				p1[0] = line[5];
				p1[1] = line[0];
				p1[2] = line[2];
				p1[3] = line[2];
				p1[4] = getArgFromEntityName(line[5]);
				p1[5] = "P";
				
				p2 = new String[6];
				p2[0] = line[6];
				p2[1] = line[1];
				p2[2] = line[3];
				p2[3] = line[3];
				p2[4] = getArgFromEntityName(line[6]);
				p2[5] = "P";
				
				if(p1[4].equals(compArg) && p2[4].equals(compArg)) {
					p = new Pair(p1, p2);
					ret.add(p);
				}
			}
			else if(!compStrs.contains(line[5])) {
				//System.out.println(line[5] + " not included in gold clusters");
			}
			else if(!compStrs.contains(line[6])) {
				//System.out.println(line[6] + " not included in gold clusters");
			}
		}
		
		in.close();
		
		return ret;
	}
	
	
	
	private void printCluster(HashSet hc) 
	{
		for(Iterator it = hc.iterator(); it.hasNext(); )
		{
			String [] line = (String [])it.next();
			System.out.println(gu.join(line, gu.separator,0,line.length-1));
		}
	}
	
	public void printBinaryRelations(HashSet<Pair> brs) 
	{
		for(Iterator<Pair> it = brs.iterator(); it.hasNext(); ) {
			Pair p = it.next();
			System.out.println(p.toString());
			/*
			String [][] pair = (String [][])it.next();
			for(int i=0; i<pair.length; i++) {
				for(int j=0; j<pair[i].length; j++) {
					System.out.print(pair[i][j]);
					System.out.print(" ");
				}
				System.out.print(" :::: ");
			}
			System.out.println("");
			*/
		}
		System.out.println("");
	}
	
	public double getPrecision(int [] prStats) {
		return getPrecision(prStats[0], prStats[1]);
	}
	
	public double getPrecision(int numCorrectHyp, int numHyp) {
		return (numCorrectHyp / ((double)numHyp));
	}
	
	public double getRecall(int [] prStats) {
		return getRecall(prStats[0], prStats[2]);
	}
	
	public double getRecall(int numCorrectHyp, int numGold) {
		return (numCorrectHyp / ((double)numGold));
	}
	
	public double getF1(double p, double r) {
		return gu.harmonicMean(p,r);
	}
	
	public int [] getPrecisionRecallStats(
			HashSet<Pair> h, HashSet<Pair> g)
	{
		int numGoldBinary = g.size();
		int numHypBinary = h.size();
		int numCorrectHypBinary = 0;
		
		for(Iterator<Pair> it = h.iterator(); it.hasNext(); ) 
		{
			//String [][]pair = (String [][])it.next();
			Pair pair = it.next();
			if(g.contains(pair)) {
				numCorrectHypBinary++;
			}
		}
				
		int [] ret = { numCorrectHypBinary, numHypBinary, numGoldBinary };
		return ret;
	}
	
	// returns an array containing 8 doubles:
	// threshold parameter, count parameter, num correct hyp, num hyp, num gold, precision, recall, and F1
	public double [][] getPrecisionRecallByNumberOfExtractions(
			double threshold,
			int paramStart,
			int paramEnd,
			double paramStep,
			boolean objects,
			String mergeInstructionFile,
			String goldClusterFile) 
	{
		//int numSteps                   = (int) ((Math.log(paramEnd)-Math.log(paramStart)) / 
		//								  Math.log(paramStep));
		int numSteps                   = (int) ((paramEnd-paramStart) / paramStep);
		double [][] ret                = new double[numSteps][numPRCurveParameters];
		
		HashSet<Pair> goldPairs        = null;
		HashSet<String> includeStrings = null; 
		HashSet<Pair> hypPairs         = null;
		int minCount         = paramStart;
		int [] prStats          = null;
		for(int i=0; i<numSteps; i++) {
			//minCount = (int) (paramStart * Math.pow(paramStep, i));
			minCount = (int) (paramStart + paramStep * i);
			ret[i][0] = threshold;
			ret[i][1] = minCount;
			
			goldPairs      = readInClustering(goldClusterFile, minCount);
			includeStrings = readInClusterStrings(goldClusterFile, minCount);
			
			hypPairs  = readInMergePairs(mergeInstructionFile, threshold, minCount, objects, includeStrings);
			prStats   = getPrecisionRecallStats(hypPairs, goldPairs);
			ret[i][2] = prStats[0];
			ret[i][3] = prStats[1];
			ret[i][4] = prStats[2];
			ret[i][5] = getPrecision(prStats);
			ret[i][6] = getRecall(prStats);
			ret[i][7] = getF1(ret[i][5],ret[i][6]);
			
			for(int j=0; j<numPRCurveParameters; j++) {
				System.out.print(ret[i][j]);
				System.out.print("\t");
			}
			System.out.println("\n");
		}
		
		return ret;
	}
	
	public void writePrecisionRecallCurve(double [][] prCurve, String outFile)
	{
		BasicDiskTable out = new BasicDiskTable(new File(outFile));
		out.openForWriting();
		
		String [] outline = new String[numPRCurveParameters];
		for(int i=0; i<prCurve.length; i++) {
			for(int j=0; j<numPRCurveParameters; j++) {
				outline[j] = String.valueOf(prCurve[i][j]);
			}
			out.println(outline);
		}
		
		out.flush();
		out.closeForWriting();
	}
	
	public void runPRbyNE(String workdir) 
	{
		String outFile         = workdir + "ESP_object_prbne_one_high_pM_5_thresh_0.5.txt";
		String mergeFile       = workdir + "ESP_object_merge_instructions_pM_5.txt";
		String goldClusterFile = workdir + goldObjectClusterFile;
		boolean objects        = true;
		double threshold       = 0.5;
		int paramStart         = 25;
		int paramEnd           = 1500;
		double paramMultiple   = 50;
		
		double [][] prCurve = getPrecisionRecallByNumberOfExtractions(
				threshold, paramStart, paramEnd, paramMultiple, objects, mergeFile, goldClusterFile);
		writePrecisionRecallCurve(prCurve, outFile);
	}
	
	
	/*
	 * params
	 * hypothesis object clusters file
	 * hypothesis relation clusters file
	 * gold object clusters file
	 * gold relation clusters file
	 * object threshold parameter
	 * relation threshold parameter
	 * merge iteration
	 * output file
	 * 
	 */
	public static void main(String[] args) {
		PrecisionRecallByNumberOfExtractions prbne = new PrecisionRecallByNumberOfExtractions();
		String workdir = args[0];
		prbne.runPRbyNE(workdir);
	}
}
