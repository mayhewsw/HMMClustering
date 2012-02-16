# Import the corpus and functions used from nltk library
from nltk.corpus import reuters
from nltk.corpus import genesis
from nltk.probability import LidstoneProbDist
from nltk.model import NgramModel
from nltk.util import ingrams


infilename = "randomized_clusters6.txt"
outfilename = "converted_clusters6.txt"
infile = open(infilename, "r")
outfile = open(outfilename, "w")

inlines = infile.readlines()

for i, iString in enumerate(inlines):
    iString = iString.split(" :::: ")[:3]

    for j in range(i, len(inlines)):
        jString = inlines[j].split(" :::: ")[:3]
        print jString

        # Compare iString and jString
    



# Tokens contains the words for Genesis and Reuters Trade
#tokens = list(genesis.words('english-kjv.txt'))
#tokens.extend(list(reuters.words(categories = 'trade')))

# estimator for smoothing the N-gram model
estimator = lambda fdist, bins: LidstoneProbDist(fdist, 0.2)

sent = "abraham lincoln be bear feb 12 1809"
tokens = sent.split()
splitNgrams = list(ingrams(list(sent), 3))

tokens = ["".join(x) for x in splitNgrams]

# N-gram language model with 3-grams
# Without an estimator, it assumes Good-Turing.
model = NgramModel(3, tokens, estimator)
print "Model: " + str(model)

sent2 = "abe lincoln was born in 1809"

splitNgrams2 = list(ingrams(list(sent2), 3))
tokens2 = ["".join(x) for x in splitNgrams2]


print "Word: " + tokens2[-1]
context = " ".join(tokens2[:-1])
print "Context: " + context


print model.prob(tokens2[-1], [sent2])

# Apply the language model to generate 50 words in sequence
#text_words = model.generate(50)

# Concatenate all words generated in a string separating them by a space.
#text = ' '.join([word for word in text_words])

# print the text
#print text


infile.close()
outfile.close()
