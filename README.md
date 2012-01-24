# HMM Clustering

## Stephen Mayhew, Nicholas Kamper

This is the final project for CSSE474 - Hidden Markov Models. The idea for this project is to use HMMs
to cluster relations. A relation is a triple, of the form (object, relation, object). The data set we will use
is a set of extractions from web text using the [ReVerb relation extractor](http://reverb.cs.washington.edu/). Our
system will use HMMs to group (or cluster) these relations together by meaning. 

For example, a file with relations might look like the following:

    Abe Lincoln	      became	   president
    Facebook	      is	   a waste of time
    Lincoln	      was	   a president
    Abraham Lincoln      was elected  president
    Facebook	      is clearly   a huge time-waster

The output of our system might look like:

    Abe Lincoln	      became	   president
    Lincoln	      was	   a president
    Abraham Lincoln      was elected  president

    Facebook	      is	   a waste of time
    Facebook	      is clearly   a huge time-waster