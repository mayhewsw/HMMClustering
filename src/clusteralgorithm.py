from ngram import NGram
from math import log

def simple_score_strings(iline, jline, N):
	iString = " ".join(iline.split(" :::: ")[:3])
	jString = " ".join(jline.split(" :::: ")[:3])

	return NGram.compare(iString, jString, N=N)

def score_oro_string(iline, jline, N):
	scores = [NGram.compare(x, y, N=N) for x,y in zip(iline.split(" :::: ")[:3], jline.split(" :::: ")[:3])]
	scores += [NGram.compare(" ".join(iline.split(" :::: ")[:3]), " ".join(jline.split(" :::: ")[:3]), N=N) * 10]
	return sum([log(x) if x > 0 else -10 for x in scores])

def build_clusters(inlines, threshold=0.3, N=3, scorer=simple_score_strings):
	clusters = []
	ignoreus = []

	for i, iline in enumerate(inlines):
		if i in ignoreus:
			continue

		iString = " ".join(iline.split(" :::: ")[:3])

		ignoreus.append(i)

		icluster = {}
		icluster[iline] = -1

		for j in range(i, len(inlines)):
			if j in ignoreus:
				continue
		
			jline = inlines[j]
			jString = " ".join(jline.split(" :::: ")[:3])
			
			score = scorer(iline, jline, N)

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

def cluster_to_file(infilename, outfilename, *args, **kwargs):
	with open(infilename, "r") as infile:
		clusters = build_clusters(infile.readlines(), *args, **kwargs)
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
