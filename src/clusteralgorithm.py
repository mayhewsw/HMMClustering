import ngram

__DEBUG__ = True

def debug_print(msg, *args):
	if __DEBUG__:
		print msg % (args)

def build_clusters(inlines, threshold=0.4, N=2):
	clusters = []
	ignoreus = []

	for i, iline in enumerate(inlines):
		iString = " ".join(iline.split(" :::: ")[:3])

		debug_print("Built cluster for string %s", iString)

		if i in ignoreus:
			continue

		ignoreus.append(i)

		icluster = {}
		icluster[iline] = -1

		for j in range(i, len(inlines)):
			if j in ignoreus:
				continue


			jline = inlines[j]
			jString = " ".join(jline.split(" :::: ")[:3])
			score = ngram.NGram.compare(jString, iString, N=N)

			debug_print("Testing string %s...score %s", jString, score)
			if score > threshold:
				icluster[jline] = score
				ignoreus.append(j)

		clusters.append(icluster)
	return clusters

def write_clusters(outfile, clusters):
	# Write output
	for c in clusters:
		outfile.write("".join([x for x,y in sorted(c.items(), key=lambda x: x[1])]))
		outfile.write("\n")

def cluster_to_file(infilename, outfilename):
	with open(infilename, "r") as infile:
		clusters = build_clusters(infile.readlines())
	with open(outfilename, "w") as outfile:
		write_clusters(outfile, clusters)

if __name__ == "__main__":
	import sys
	if len(sys.argv) == 2:
		infilename = sys.argv[1]
		outfilename = sys.argv[2]
	else:
		infilename = "randomized_clusters6.txt"
		outfilename = "PyNGram_clustered_clusters6.txt"

	cluster_to_file(infilename, outfilename)
