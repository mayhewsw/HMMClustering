package edu.washington.cs.uei.scoring;

import java.io.File;
//import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import edu.washington.cs.uei.disktable.BasicDiskTable;
import edu.washington.cs.uei.util.GeneralUtility;

public class BinaryPrecisionRecallCalculator {

	GeneralUtility gu = new GeneralUtility();
	
	private static final String workspace = 
		"C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\small_test_hist_sci\\under_1000\\jair_resolver\\";
	
	private static final String goldObjectClusterFile   = "object_scoring_clusters_artificial.txt";
	private static final String goldRelationClusterFile = "relation_scoring_clusters_artificial.txt";
	
	private static final int numPRCurveParameters   = 7;
	
	public  static final int thresholdObjectCount   = 429;
	public  static final int thresholdRelationCount = 851;
	
	
	/*
	 * Given a hashset of string arrays, this populates a hashset of pairs
	 * A pair is virtually an association of two string arrays
	 * This is the way that pairs are formed:
	 * Given a hashset of: {a, b, c, d, e}
	 * We create pairs: {(a, b), (a, c), (a, d), (a, e), 
	 * 				     	 	 (b, c), (b, d), (b, e),
	 * 									 (c, d), (c, e),
	 * 											 (d, e)}
	 * If the input hashset has n members, the output hashset will have
	 * n*(n-1)/2 members.
	 */
	private void addPairs(HashSet<String []> hc, HashSet<Pair> hs) {
		for(Iterator<String[]> it=hc.iterator(); it.hasNext(); ) {
			String [] p1 = (String []) it.next();
		
			boolean foundMatch = false;
			for(Iterator<String[]> it2 = hc.iterator(); it2.hasNext(); ) {
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
	
	
	
	public HashSet<Pair> readInClustering(String clusterFilename)
	{
		HashSet<Pair> ret = new HashSet<Pair>();
		
		BasicDiskTable clusters = new BasicDiskTable(new File(clusterFilename));
		clusters.open();
		
		String [] line = clusters.readLine();
		HashSet<String []> hc = new HashSet<String []>();
		while(line!=null) {
			if(line.length>0 && line[0].trim().length()>0) {
				hc.add(line);
			}
			else { 
				if(hc!=null && hc.size()>1) {
					addPairs(hc, ret);
				}
				hc = new HashSet<String []>();
			}
			
			line = clusters.readLine();
		}
		if(hc!=null && hc.size()>0) {
			addPairs(hc, ret);
		}
		
		return ret;
	}
	
	public String getArgFromEntityName(String entity) {
		return Character.isUpperCase(entity.charAt(0)) ? 
				GeneralUtility._ARG1_ : 
				GeneralUtility._REL_;
	}
	
	public HashSet<String> readInClusterStrings(String clusterFilename)
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
			String mergeFilename, double threshold, boolean objects, HashSet<String> compStrs)
	{
		HashSet<Pair> ret = new HashSet<Pair>();
		BasicDiskTable in = new BasicDiskTable(new File(mergeFilename));
		
		String compArg = objects ? GeneralUtility._ARG1_ : GeneralUtility._REL_;
		in.open();
		String [] p1 = null;
		String [] p2 = null;
		Pair p       = null;
		double score = 0;
		for(String [] line = in.readLine(); line!=null; line = in.readLine()) {
			score = Double.parseDouble(line[4]);
			if(score>=threshold && compStrs.contains(line[5]) && compStrs.contains(line[6])) {
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
	
	public HashSet<Pair> readInClustering(String clusterFilename, boolean forObjects) 
	{
		int thresholdCount = thresholdObjectCount;
		if(!forObjects) {
			thresholdCount = thresholdRelationCount;
		}
		
		HashSet<Pair> ret = new HashSet<Pair>();
		
		BasicDiskTable clusters = new BasicDiskTable(new File(clusterFilename));
		clusters.open();
		
		String [] line = clusters.readLine();
		HashSet<String []> hc = new HashSet<String []>();
		boolean aboveThreshold = false;
		while(line!=null) {
			if(line.length>0 && line[0].trim().length()>0) {
				//System.out.println(join(line, " :: "));
				
				hc.add(line);
				if(line.length>2 &&
				   Integer.parseInt(line[1])>=thresholdCount) 
				{
						aboveThreshold = true;
				}
					
			}
			else { 
				if(hc!=null && hc.size()>1) {
					if(aboveThreshold) {
						//System.out.println("Found cluster above threshold:");
						//printCluster(hc);
						addPairs(hc, ret);
					}
					hc = new HashSet<String []>();
				}
				hc = new HashSet<String []>();
				aboveThreshold = false;
			}
			
			line = clusters.readLine();
		}
		if(hc!=null && hc.size()>0) {
			if(aboveThreshold) {
				//System.out.println("Found cluster above threshold:");
				//printCluster(hc);
				addPairs(hc, ret);
			}
		}
		
		
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
	
	// returns an array containing 7 doubles:
	// threshold parameter, num correct hyp, num hyp, num gold, precision, recall, and F1
	public double [][] getPrecisionRecallCurveOld(
			double paramStart,
			double paramEnd,
			double paramStep,
			boolean objects,
			String mergeInstructionFile,
			String goldClusterFile) 
	{
		int numSteps                   = (int) ((paramEnd-paramStart) / paramStep);
		double [][] ret                = new double[numSteps][numPRCurveParameters];
		
		HashSet<Pair> goldPairs        = readInClustering(goldClusterFile);
		HashSet<String> includeStrings = readInClusterStrings(goldClusterFile); 
		HashSet<Pair> hypPairs         = null;
		double param            = 0;
		int [] prStats          = null;
		for(int i=0; i<numSteps; i++) {
			param = paramStart + (paramStep * i);
			ret[i][0] = param;
			
			hypPairs  = readInMergePairs(mergeInstructionFile, param, objects, includeStrings);
			prStats   = getPrecisionRecallStats(hypPairs, goldPairs);
			ret[i][1] = prStats[0];
			ret[i][2] = prStats[1];
			ret[i][3] = prStats[2];
			ret[i][4] = getPrecision(prStats);
			ret[i][5] = getRecall(prStats);
			ret[i][6] = getF1(ret[i][4],ret[i][5]);
			
			for(int j=0; j<numPRCurveParameters; j++) {
				System.out.print(ret[i][j]);
				System.out.print("\t");
			}
			System.out.println("\n");
		}
		
		return ret;
	}

	private boolean includePair(String m1, String m2, HashSet<String> include)
	{
		return include.contains(m1) && include.contains(m2);
	}
	
	private boolean scorePair(String [] merge, HashSet<Pair> goldPairs)
	{
		String [] p1 = new String[6];
		p1[0] = merge[5];
		p1[1] = merge[0];
		p1[2] = merge[2];
		p1[3] = merge[2];
		p1[4] = getArgFromEntityName(merge[5]);
		p1[5] = "P";
		
		String [] p2 = new String[6];
		p2[0] = merge[6];
		p2[1] = merge[1];
		p2[2] = merge[3];
		p2[3] = merge[3];
		p2[4] = getArgFromEntityName(merge[6]);
		p2[5] = "P";
		
		Pair p = new Pair(p1, p2);
		return goldPairs.contains(p);
	}
	
	// returns an array containing 7 doubles:
	// threshold parameter, num correct hyp, num hyp, num gold, precision, recall, and F1
	// assumes merge file is sorted in descending order by scores
	public double [][] getPrecisionRecallCurve(
			double paramStart,
			double paramEnd,
			double paramStep,
			boolean objects,
			String mergeInstructionFile,
			String goldClusterFile) 
	{
		int numSteps                   = (int) ((paramEnd-paramStart) / paramStep);
		double [][] ret                = new double[numSteps][numPRCurveParameters];
		
		HashSet<Pair> goldPairs        = readInClustering(goldClusterFile);
		HashSet<String> includeStrings = readInClusterStrings(goldClusterFile); 
		
		int    c1, c2;
		int    numHypPairs   = 0;
		int    numCorrectHyp = 0;
		int    numGoldPairs  = goldPairs.size();
		int    i             = numSteps-1;
		double score         = 0;
		double param         = paramStart + (numSteps-1) * paramStep;
		
		BasicDiskTable inHyp = new BasicDiskTable(new File(mergeInstructionFile));
		inHyp.open();
		for(String [] line = inHyp.readLine(); line!=null; line = inHyp.readLine()) {
			c1 = Integer.parseInt(line[0]);
			c2 = Integer.parseInt(line[1]);
			if(c1<=c2) {
				continue;
			}
			score = Double.parseDouble(line[4]);
			if(score < param) {
				while(score<param) {
					param -= paramStep;
					i--;
				}
				i++;
				param += paramStep;
				ret[i][0] = param;
				ret[i][1] = numCorrectHyp;
				ret[i][2] = numHypPairs;
				ret[i][3] = numGoldPairs;
				ret[i][4] = getPrecision(numCorrectHyp, numHypPairs);
				ret[i][5] = getRecall(numCorrectHyp, numGoldPairs);
				ret[i][6] = getF1(ret[i][4], ret[i][5]);
				for(int j=0; j<numPRCurveParameters; j++) {
					System.out.print(ret[i][j]);
					System.out.print("\t");
				}
				System.out.println("\n");
				
				i--;
				param -= paramStep;
			}
			
			if(includePair(line[5],line[6], includeStrings)) {
				numHypPairs++;
				if(scorePair(line, goldPairs)) {
					numCorrectHyp++;
				}
			}
		}
		ret[0][0] = 0;
		ret[0][1] = numCorrectHyp;
		ret[0][2] = numHypPairs;
		ret[0][3] = numGoldPairs;
		ret[0][4] = getPrecision(numCorrectHyp, numHypPairs);
		ret[0][5] = getRecall(numCorrectHyp, numGoldPairs);
		ret[0][6] = getF1(ret[0][4], ret[0][5]);
		for(int j=0; j<numPRCurveParameters; j++) {
			System.out.print(ret[i][j]);
			System.out.print("\t");
		}
		System.out.println("\n");
		
		return ret;
	}	
	
	// returns an array containing 7 doubles:
	// threshold parameter, num correct hyp, num hyp, num gold, precision, recall, and F1
	// assumes merge file is sorted in descending order by scores
	public LinkedList<double []> getPrecisionRecallCurve(
			boolean objects,
			String mergeInstructionFile,
			String goldClusterFile) 
	{
		LinkedList<double []> ret = new LinkedList<double []>();
		
		HashSet<Pair> goldPairs        = readInClustering(goldClusterFile);
		HashSet<String> includeStrings = readInClusterStrings(goldClusterFile); 
		
		int    c1, c2;
		int    numHypPairs   = 0;
		int    numCorrectHyp = 0;
		int    numGoldPairs  = goldPairs.size();
		double score         = 0;
		double [] stats      = null;
		double lastRecall    = -1;
		double lastPrecision = -1;
		double precision     = 0;
		double recall        = 0;
		double threshold     = 0.01;
		
		BasicDiskTable inHyp = new BasicDiskTable(new File(mergeInstructionFile));
		inHyp.open();
		for(String [] line = inHyp.readLine(); line!=null; line = inHyp.readLine()) {
			c1 = Integer.parseInt(line[0]);
			c2 = Integer.parseInt(line[1]);
			if(c1<=c2) {
				//continue;
			}
			score = Double.parseDouble(line[4]);
			if(includePair(line[5],line[6], includeStrings)) {
				numHypPairs++;
				if(scorePair(line, goldPairs)) {
					numCorrectHyp++;
				}
			}
			
			precision = getPrecision(numCorrectHyp, numHypPairs);
			recall    = getRecall(numCorrectHyp, numGoldPairs);
			
			if(includePair(line[5], line[6], includeStrings) &&
					( (Math.abs(precision-lastPrecision)>=threshold) ||
					  (Math.abs(recall-lastRecall)>=threshold))) {
				lastPrecision = precision;
				lastRecall = recall;
				stats = new double [numPRCurveParameters];
				stats[0] = score;
				stats[1] = numCorrectHyp;
				stats[2] = numHypPairs;
				stats[3] = numGoldPairs;
				stats[4] = precision;
				stats[5] = recall;
				stats[6] = getF1(precision, recall);
				for(int j=0; j<numPRCurveParameters; j++) {
					System.out.print(stats[j]);
					System.out.print("\t");
				}
				System.out.println("\n");
				ret.add(stats);
			}
		
		}
		stats = new double [numPRCurveParameters];
		stats[0] = score;
		stats[1] = numCorrectHyp;
		stats[2] = numHypPairs;
		stats[3] = numGoldPairs;
		stats[4] = precision;
		stats[5] = recall;
		stats[6] = getF1(precision, recall);
		for(int j=0; j<numPRCurveParameters; j++) {
			System.out.print(stats[j]);
			System.out.print("\t");
		}
		System.out.println("\n");
		ret.add(stats);
		
		return ret;
	}
	
	public void writePrecisionRecallCurve(double [][] prCurve, String outFile)
	{
		BasicDiskTable out = new BasicDiskTable(new File(outFile));
		out.openForWriting();
		
		String [] outline = new String[numPRCurveParameters];
		for(int i=0; i<prCurve.length; i++) {
			if(prCurve[i][2]>0) {
				for(int j=0; j<numPRCurveParameters; j++) {
					outline[j] = String.valueOf(prCurve[i][j]);
				}
				out.println(outline);
			}
		}
		
		out.flush();
		out.closeForWriting();
	}
	
	public void writePrecisionRecallCurve(LinkedList<double []> prCurve, String outFile)
	{
		BasicDiskTable out = new BasicDiskTable(new File(outFile));
		out.openForWriting();
		
		String [] outline = new String[numPRCurveParameters];
		double [] point = prCurve.removeLast();
		while(point!=null) {
			for(int j=0; j<numPRCurveParameters; j++) {
				outline[j] = String.valueOf(point[j]);
			}
			out.println(outline);
			point = prCurve.removeLast();
		}
		
		out.flush();
		out.closeForWriting();
	}
	
	public void runPRC(String workdir) 
	{
		boolean objects        = true;
		String outFileBase     = workdir + "ESP_" + (objects ? "object" : "relation") + "_similarity_prc_pM_";
		String mergeFileBase   = workdir + "merge_instruction_file_arg" + (objects ? 0 : 1) + "_pM_";
		String goldClusterFile = workdir + (objects ? goldObjectClusterFile : goldRelationClusterFile);
		double paramStart      = 0.0;
		double paramEnd        = 1500.125;
		double paramStep       = 1/8.0;
		
		double [][] prCurve;
		String outFile;
		String mergeFile;
		for(int pM = 35; pM<=45; pM+=5) {
			outFile = outFileBase + pM + ".txt";
			mergeFile = mergeFileBase + pM + ".txt";
			prCurve = getPrecisionRecallCurve(
					paramStart, paramEnd, paramStep, objects, mergeFile, goldClusterFile);
			writePrecisionRecallCurve(prCurve, outFile);
		}
	}
	
	public void calculatePR(HashSet<Pair> h, 
							HashSet<Pair> g,
							String param,
							int mergeIter,
							BasicDiskTable out, 
							boolean compObject)
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
		
		double precision = (numCorrectHypBinary / ((double)numHypBinary));
		double recall    = (numCorrectHypBinary / ((double)numGoldBinary));
		double F1        = 2 * precision * recall / (precision + recall); 
		
		System.out.println("input parameter:  " + param);
		System.out.println("merge iteration:  " + mergeIter);
		System.out.println("num gold binary:  " + numGoldBinary);
		System.out.println("num hyp binary:   " + numHypBinary);
		System.out.println("num correct:      " + numCorrectHypBinary);
		System.out.println("precision =       " + precision);
		System.out.println("recall =          " + recall);
		System.out.println("F1 =              " + F1);
		
		out.openForWriting(true);
		String objRel = "OBJ";
		if(!compObject) objRel = "REL";
		String [] outline = {objRel, 
							 String.valueOf(param),
							 String.valueOf(mergeIter),
							 String.valueOf(numGoldBinary),
							 String.valueOf(numHypBinary),
							 String.valueOf(numCorrectHypBinary),
							 String.valueOf(precision),
							 String.valueOf(recall),
							 String.valueOf(F1)};
		out.println(outline);
		out.flush();
		out.closeForWriting();
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
		BinaryPrecisionRecallCalculator prc = new BinaryPrecisionRecallCalculator();
		//String workdir = args[0];
		//prc.runPRC(workdir);
		//System.exit(1);
		
		boolean isObjects = false; //args[5].equals("objects");
		
		//String clusterdir2 = "/home/stephen/Documents/Classes/Fall2011/NLP/resolver-export/MyReverbData/clustersTest.txt";
		//String clusterdir = "/home/stephen/Documents/Classes/Fall2011/NLP/resolver-export/MyReverbData/clusters.txt";
		//String hypClusterFile = "relation_scoring_clusters_artificial.txt";
		String hypClusterFile = "hyprel.txt";
		String goldClusterFile = "goldrel.txt";
		
				
		
		
		HashSet<Pair> hyp  = prc.readInClustering(hypClusterFile, isObjects);
		HashSet<Pair> gold = prc.readInClustering(goldClusterFile, isObjects);

		String param = "1"; //args[2];
		
		//double objParam = Double.parseDouble(args[4]);
		//double relParam = Double.parseDouble(args[5]);
		
		int mergeIteration = 0; //Integer.parseInt(args[3]);
		BasicDiskTable out = new BasicDiskTable(new File("justusethisandthendeleteme.txt"));  //args[4]));
		
		prc.calculatePR(hyp, gold, param, mergeIteration, out, isObjects);

		
		//hyp  = prc.readInClustering(args[1], false);
		//gold = prc.readInClustering(args[3], false);
		
		//prc.calculatePR(hyp, gold, relParam, mergeIteration, out, false);
	}

}
