## Project Ideas

There are several approaches to take on this project. We are still in the process of 
researching which are the best known and most used approaches for clustering
using HMMs.

Here are a few ideas:

* Put all relations into one big cluster
* Train a single HMM using every relation as training data (probably 
use Baum-Welch algorithm?)
* Score each relation using that HMM, and begin to break apart the big cluster using
certain thresholds on the score.
* Repeat this process until the thresholds seem too small.

Another approach is to begin with each relation in it's own little cluster, and then to merge clusters. 

Since we are using HMMs, it is useful to think about what are the hidden states, and what are the outputs? What if
the meaning of each object of relation was the hidden state, and the text representing it was the output? That is,
there would be a hidden state that represented the concept of ABRAHAM LINCOLN, and it's outputs would be text such
as, 'Abe Lincoln,' 'A. Lincoln,' 'Lincoln,' and so on. 