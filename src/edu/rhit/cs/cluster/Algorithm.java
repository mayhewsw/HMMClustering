package edu.rhit.cs.cluster;

import java.util.HashSet;

import edu.rhit.tools.Cluster;

public interface Algorithm {
	public int getData(String clusterFilename);
	public int produceOutput(HashSet<Cluster> clusters, String outfile);
	public void DoWork(String infile, String outfile);
}
