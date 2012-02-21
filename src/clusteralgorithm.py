# Import the corpus and functions used from nltk library
#from nltk.corpus import reuters
#from nltk.corpus import genesis
#from nltk.probability import LidstoneProbDist
#from nltk.model import NgramModel
#from nltk.util import ingrams

# Tokens contains the words for Genesis and Reuters Trade
#genesis_tokens = list(genesis.words('english-kjv.txt'))
#tokens.extend(list(reuters.words(categories = 'trade')))

# estimator for smoothing the N-gram model
#estimator = lambda fdist, bins: LidstoneProbDist(fdist, 5)

#sent = "abraham lincoln be bear feb 12 1809"
#tokens = sent.split()
#splitNgrams = list(ingrams(list(sent), 3))

#tokens = ["".join(x) for x in splitNgrams]

# N-gram language model with 3-grams
# Without an estimator, it assumes Good-Turing.
#model = NgramModel(3, tokens, estimator)
#print "Model: " + str(model)

#sent2 = "abe lincoln was born in 1809"

#splitNgrams2 = list(ingrams(list(sent2), 3))
#tokens2 = ["".join(x) for x in splitNgrams2]

#print "Word: " + tokens2[-1]
#context = " ".join(tokens2[:-1])
#print "Context: " + context

#print model.prob(tokens2[-1], [sent2])

# Apply the language model to generate 50 words in sequence
#text_words = model.generate(50)

# Concatenate all words generated in a string separating them by a space.
#text = ' '.join([word for word in text_words])

# print the text
#print text


import ngram

infilename = "randomized_clusters6.txt"
outfilename = "PyNGram_clustered_clusters6.txt"
infile = open(infilename, "r")
outfile = open(outfilename, "w")

inlines = infile.readlines()


clusters = []
ignoreus = []

threshold = 0.4

for i, iline in enumerate(inlines):
    iString = " ".join(iline.split(" :::: ")[:3])
    
    print iString

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
        score = ngram.NGram.compare(jString, iString, N=2)
        
        if score > threshold:
            icluster[jline] = score
            ignoreus.append(j)

    clusters.append(icluster)


# Write output
for c in clusters:
    outfile.write("".join([x for x,y in sorted(c.items(), key=lambda x: x[1])]))
    outfile.write("\n")
    
infile.close()
outfile.close()
