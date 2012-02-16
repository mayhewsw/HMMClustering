import random

filename = "clusters6.txt"

def randomize():
	''' This takes a gold standard file
	and randomizes the data in a file.'''
	fname = filename
	f = open(fname, "r")

	out = open("randomized_" + fname, 'w')

	# Read in every line.
	# Split every line
	# Reorder every line
	# Insert into list in random order
	# Write list to file

	inList = f.readlines()
	outlist = []

	for l in inList:
		if l == "\n":
			continue
		sl = l.split("\t")
		# We want ...
		outs = sl[4] + " :::: " + sl[5] + " :::: " + sl[6] + " :::: " + sl[0] + " :::: 0 :::: 0 :::: R :::: P\n"
		randInsertion = random.randint(0, len(outlist))
		outlist.insert(randInsertion, outs)
	
	for s in outlist:
		out.write(s)

	f.close()
	out.close()

def convertType():
	''' This creates the gold standard file in the
	correct format '''
	fname = filename
	f = open(fname, "r")

	out = open("converted_" + fname, 'w')

	inList = f.readlines()
	outlist = []

	for l in inList:
		if l == "\n":
			outlist.append(l)
			continue
		sl = l.split("\t")
		# We want ...
		outs = sl[4] + " :::: " + sl[5] + " :::: " + sl[6] + " :::: " + sl[0] + " :::: 0 :::: 0 :::: R :::: P\n"
		outlist.append(outs)
	
	for s in outlist:
		out.write(s)

	f.close()
	out.close()

if __name__ == "__main__":
	randomize()
	convertType()
