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

def backoff_score_strings(iline, jline, N, T=0.0):
	iString = " ".join(iline.split(" :::: ")[:3])
	jString = " ".join(jline.split(" :::: ")[:3])

	score = -1
	while score <= T and N >= 1:
		score = NGram.compare(iString, jString, N=N)
		N = N - 1

	return score

def cumulative_score_strings(iline, jline, N):
	iString = " ".join(iline.split(" :::: ")[:3])
	jString = " ".join(jline.split(" :::: ")[:3])

	score = 0
	while N >= 1:
		score += (NGram.compare(iString, jString, N=N)) #* N)
		N = N - 1

	return score

def build_multiclusters(inlines, threshold=0.05, N=4):
	clusters = []
	ignoreus = []

	for i, iline in enumerate(inlines):
		if i in ignoreus:
			continue

		iString = " ".join(iline.split(" :::: ")[:3])

		ignoreus.append(i)

		icluster = {}
		icluster[iline] = -1
		iModel = NGram(iString)

		for j in range(i, len(inlines)):
			if j in ignoreus:
				continue
		
			jline = inlines[j]
			jString = " ".join(jline.split(" :::: ")[:3])
		
			results = iModel.search(jString)
			score = sum([y for x,y in results]) / len(results) \
					if len(results) > 0 else 0.0
			print score

			if score > threshold:
				icluster[jline] = score
				iModel.add(jString)
				ignoreus.append(j)

		clusters.append(icluster)
	return clusters

def build_clusters(inlines, threshold=0.3, N=4, scorer=simple_score_strings):
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
			print score

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
		clusters = build_multiclusters(infile.readlines(), *args, **kwargs)
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
