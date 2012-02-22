from clusteralgorithm import cluster_to_file
from multiprocessing import Pool


def cluster(args):
	infilename, outfilename, threshold, n = args
	print "Starting cluster n=%s, thres=%s" % (n, threshold)
	cluster_to_file(infilename, outfilename, threshold=threshold, N=n)
	return 1

#def cluster(args):
	#print args

if __name__ == "__main__":
	infilename = "randomized_clusters6.txt"
	p = Pool()
	r = p.map_async(cluster, ((infilename, ("out/cumulative/auto_%s_%s.txt" %
		(thres, n)), thres, n) for thres in
		[x*0.1 for x in xrange(2, 8)] for n in xrange(2, 15)))
	print "Finished %s clusters" % r.get()
	p.close()
	p.join()

