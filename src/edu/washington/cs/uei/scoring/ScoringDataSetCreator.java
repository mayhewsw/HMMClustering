package edu.washington.cs.uei.scoring;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import edu.washington.cs.uei.disktable.BasicDiskTable;
import edu.washington.cs.uei.disktable.IntegerComparator;
//import edu.washington.cs.uei.tools.EntityAndRelationNameExtractor;
import edu.washington.cs.uei.util.GeneralUtility;
import edu.washington.cs.uei.util.HashCounter;
import edu.washington.cs.uei.util.Morphology;

public class ScoringDataSetCreator {
	private static final String workspace = 
		"C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\small_test_hist_sci\\under_1000\\";
	private static final String _object_file_str_      = "object_file_0.txt";
	private static final String _sorted_object_str_    = "sorted_objects_by_count.txt";
	private static final String _scoring_clusters_str_ = "scoring_clusters.txt";
	private static final String _workfile_objects_     = "work_file_00.txt";
	private static final String _workfile_relations_   = "work_file_01.txt";
	
	private static final int numMostCommonObjects = 200;
	private static final int minAppearances = 10;
	
	private static final int argColumn = 4;
	private static final int properColumn = 5;
	private static final int compArgColumn = 12;
	
	//private Morphology morph = new Morphology();
	
	
	public void mostCommonScoringSet(BasicDiskTable objects,
									 int samples,
									 BasicDiskTable out)
	{
		File sortedObjects = new File(workspace + _sorted_object_str_);
		IntegerComparator ic = new IntegerComparator(2, false);
		//objects.sort(ic, sortedObjects);
		BasicDiskTable sobjs = new BasicDiskTable(sortedObjects);
		
		String [][] entities  = new String [samples][];
		String [][] relations = new String [samples][];
		
		sobjs.open();
		String [] line = sobjs.readLine();
		int i=0, j=0;
		while(line!=null && (i<samples || j<samples)) {
			if(i<samples &&
			   line[argColumn].equals(GeneralUtility._ARG1_) &&
			   line[properColumn].equals(Morphology._PROPER_)) {
				entities[i] = line;
				i++;
			}
			else if(j<samples &&
					line[argColumn].equals(GeneralUtility._REL_) &&
					line[properColumn].equals(Morphology._PROPER_)) {
				relations[j] = line; 
				j++;
			}
			line = sobjs.readLine();
		}
		sobjs.close();

		System.out.println("Found top 100 objects:");
		for(int k=0; k<samples; k++) {
			if(entities[k]!=null) {
				System.out.println("" + k + ":  " + entities[k][0]  + " :::: " + entities[k][2]);
			}
		}
		for(int k=0; k<samples; k++) {
			if(relations[k]!=null) {
				System.out.println("" + k + ":  " + relations[k][0] + " :::: " + relations[k][2]);
			}
		}
		System.out.flush();
		
		//System.exit(1);

		HashCounter [] entityComps = new HashCounter[samples];
		HashCounter [] relationComps = new HashCounter[samples];
		for(int k=0; k<samples; k++) {
			entityComps[k] = new HashCounter();
			relationComps[k] = new HashCounter();
		}
		
		BasicDiskTable wf_objs = new BasicDiskTable(new File(workspace+_workfile_objects_));
		BasicDiskTable wf_rels = new BasicDiskTable(new File(workspace+_workfile_relations_));
		
		
		wf_objs.open();
		line = wf_objs.readLine();
		while(line!=null) {
			for(int k=0; k<samples; k++) {
				if(entities[k][0].equals(line[0]) &&
				   Integer.parseInt(line[compArgColumn+2])>minAppearances) {
					entityComps[k].add(line[compArgColumn] + " :::: " + line[compArgColumn+2]);
				}
			}
			line = wf_objs.readLine();
		}
		wf_objs.close();
		
		
		out.openForWriting();
		String [] outLineSmall = new String[1];
		
		for(int k=0; k<samples; k++) {
			out.println(entities[k]);
			for(Iterator it = entityComps[k].keySet().iterator(); it.hasNext(); ) {
				outLineSmall[0] = (String)it.next();
				if(entityComps[k].getCount(outLineSmall[0])>1) {
					out.println(outLineSmall);
				}
			}
		}
		entityComps = null;
		out.flush();
		
		
		wf_rels.open();
		line = wf_rels.readLine();
		while(line!=null) {
			for(int k=0; k<samples; k++) {
				if(relations[k][0].equals(line[4]) &&
				   Integer.parseInt(line[compArgColumn+2])>minAppearances) {
					relationComps[k].add(line[compArgColumn] + " :::: " + line[compArgColumn+2]);
				}
			}
			line = wf_rels.readLine();
		}
		wf_rels.close();

		for(int k=0; k<samples; k++) {
			out.println(relations[k]);
			for(Iterator it = relationComps[k].keySet().iterator(); it.hasNext(); ) {
				outLineSmall[0] = (String)it.next();
				if(relationComps[k].getCount(outLineSmall[0])>1) {
					out.println(outLineSmall);
				}
			}
		}
		relationComps = null;
		out.flush();
		out.closeForWriting();
		

	}
	
	public void randomScoringSet(BasicDiskTable objects,
			 					 int samples,
			 					 BasicDiskTable out)
	{
		
	}
	
	public static void main(String[] args) {
		File objectFile = new File(workspace + _object_file_str_);
		BasicDiskTable objects = new BasicDiskTable(objectFile);
		
		File outFile = new File(workspace + _scoring_clusters_str_);
		BasicDiskTable outDT = new BasicDiskTable(outFile);
		
		ScoringDataSetCreator sdsc = new ScoringDataSetCreator();
		sdsc.mostCommonScoringSet(objects, numMostCommonObjects, outDT);
	}

}
