from clusteralgorithm import cluster_to_file

if __name__ == "__main__":
	infilename = "randomized_clusters6.txt"

	for n in xrange(1, 7):
		for thres in [x*0.1 for x in xrange(2, 9)]:
			print "Running for n=%s, thres=%s" % (n, thres)
			outfilename = "out/PyNGram_auto_%s_%s.txt" % (n, thres)
			cluster_to_file(infilename, outfilename, threshold=thres, N=n) 
