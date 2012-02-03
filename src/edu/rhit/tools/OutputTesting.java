package edu.rhit.tools;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


public class OutputTesting {

	public static final String clusterdir = "/home/stephen/Documents/Classes/Fall2011/NLP/resolver-export/MyReverbData/";

	public OutputTesting() {
		// Get output
		// Read in gold clusters into HashSet

		// Read in Hypothesis clusters

		// Score them

		// Return score
	}

	/*
	 * This function takes the filename of a file containing clusters, and reads
	 * in the file Each cluster is stored as a HashSet inside a larger HashSet
	 * of clusters. Clusters are treated as being separated by line in the
	 * cluster file.
	 */
	public HashSet<Cluster> readInClustering(String clusterFilename)
			throws FileNotFoundException, IOException {
		HashSet<Cluster> ret = new HashSet<Cluster>();

		BufferedReader clusterfile = new BufferedReader(new FileReader(
				clusterFilename));

		String line = clusterfile.readLine();

		Cluster hc = new Cluster();
		while (line != null) {
			if (line.trim().length() > 0) {
				hc.addString(line);
			} else {
				if (hc != null && hc.size() > 1) {
					ret.add(hc);
				}
				hc = new Cluster();
			}

			line = clusterfile.readLine();
		}

		return ret;
	}

	public static String join(String[] splitArr, String sep) {
		if (splitArr == null || splitArr.length <= 0) {
			return "";
		}
		StringBuffer ret = new StringBuffer(splitArr[0]);
		for (int i = 1; i < splitArr.length; i++) {
			ret.append(sep);
			ret.append(splitArr[i]);
		}
		return ret.toString();
	}

	/*
	 * Given a gold standard cluster file and a hypothesis cluster file,
	 * this method calculates the precision and recall. 
	 */
	public static float[] calculatePR(HashSet<Cluster> goldClusters,
			HashSet<Cluster> hypClusters) {
		
		HashMap<Cluster, Cluster> hm = new HashMap<Cluster, Cluster>();
		
		// First find a mapping between clusters. Greedy?
		for (Iterator<Cluster> it = goldClusters.iterator(); it.hasNext();) {
			Cluster goldclust = it.next();
			int maxSize = 0;
			
			// Each cluster can map to exactly one other cluster
			if (hm.containsKey(goldclust)){
				continue;
			}
			
			Cluster goldPut = new Cluster();
			Cluster hypPut = new Cluster();
			
			for (Iterator<Cluster> it2 = hypClusters.iterator(); it2.hasNext();) {
				Cluster hclust = it2.next();
				
				if (hm.containsValue(hclust)){
					continue;
				}
				
				int interSize = goldclust.getIntersection(hclust).size();
				if (interSize > maxSize){
					maxSize = interSize;
					goldPut = goldclust;
					hypPut = hclust;
				}
			}
			// goldClust and hclust
			//TODO: make sure hypclust with greatest amount is mapping to goldclust.
			// That is: if intersection(h1, g) = 3, and intersection(h2, g) = 5,
			// h2 should map to g and h1 should get second best choice. I think.
			//TODO: what if there are a lot more hyp clusters than gold clusters?
			//TODO: could also be more gold clusters than hyp clusters... Hmm. 
			// Solution: some will match none.
			// Hmm. Could probably just do matching and counting right here?
			hm.put(goldPut, hypPut);
		}
		
		// Once a mapping has been found, calculate precision and recall for each pair of clusters
		
		int correct = 0;
		int totalHypInClust = 0;
		int found = 0;
		int totalGoldInClust = 0;
		
		for (Map.Entry<Cluster, Cluster> entry : hm.entrySet()) {
		    Cluster gold = entry.getKey();
		    Cluster hyp = entry.getValue();
		    
		    totalHypInClust += hyp.size();
		    totalGoldInClust += gold.size();
		    
		    Cluster inter = gold.getIntersection(hyp);
		    correct += inter.size();
		    found += inter.size();
		}
		
		// for each cluster pair: get intersection
		// intersection: correct
		// intersection: found
		
		System.out.println(totalHypInClust);
		System.out.println(totalGoldInClust);
		
		// precision = num correct hyp elements in clusters / num hyp elements in clusters
		// recall = num found gold elements in clusters / num gold elements in clusters
		float precision = (float)correct / totalHypInClust;
		float recall = (float)found / totalGoldInClust;
		double F1 = 2 * precision * recall / (precision + recall);
		
		float [] pr = {precision, recall};
		
		return pr;
	}

	/*
	 * Main function
	 */
	public static void main(String[] args) {
		OutputTesting ot = new OutputTesting();
		HashSet<Cluster> goldClusters;
		HashSet<Cluster> hypClusters;
		try {
			goldClusters = ot.readInClustering(clusterdir + "clusters.txt");
			hypClusters = ot.readInClustering(clusterdir + "clusters.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		String s = "2626	Abraham Lincoln	was known as	Honest Abe	abraham lincoln	be know as	honest abe	2	0.94723";

		//System.out.println(goldClusters.size());

		float[] stats = calculatePR(goldClusters, hypClusters);
		System.out.println("Precision: " + stats[0]);
		System.out.println("Recall:    " + stats[1]);


	}

}
