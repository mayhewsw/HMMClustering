package edu.rhit.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Integer;

public class NgramLM {
	private HashMap<Integer, HashMap<String, Integer>> ngramModels;
	private int maxLength = -1;

	public NgramLM() {
		this.ngramModels = new HashMap<Integer, HashMap<String, Integer>>();
	}

	public void trainModel(ArrayList<String> strings) {
		for (String s: strings) {
			this.trainString(s);
		}
	}
	
	public void trainString(String s) {
		if (this.maxLength > 0) {
			int oldMaxLength = this.maxLength;
			this.maxLength = Math.min(s.length() + 1, this.maxLength);

			// if oldMaxLength > maxLength, remove all hashmaps that are too long.
			if (oldMaxLength > this.maxLength){
				for(int i= this.maxLength+1; i < oldMaxLength; i++){
					ngramModels.remove(i);
				}
			}
		} else {
			this.maxLength = s.length() + 1;
		}

		// Split for all ngram lengths
		for (int i = 1; i < this.maxLength; i++) {
			if (!ngramModels.containsKey(i)) ngramModels.put(i, new HashMap<String, Integer>());
			HashMap<String, Integer> model = ngramModels.get(i);
			String[] segments = splitString(s, i);
			for (String segment : segments) {
				int count = (model.containsKey(segment)) ? model.get(segment) : 0;
				model.put(segment, ++count);
			}
			System.out.println(i + "\n" + model);
		}
	}

	private int lenShortestString(ArrayList<String> strings){
		if(strings.size() == 0){
			throw new IllegalArgumentException("Strings is empty");
		}

		int min = strings.get(0).length();
		for (String s : strings){
			if (s.length() < min ){
				min = s.length();
			}
		}

		return min;
	}

	public float getSentProb(String s) {
		// split it up
		int splitLen = Math.min(s.length(), this.maxLength);

		String[] splitString = this.splitString(s, splitLen);

		HashMap<String, Integer> currModel = ngramModels.get(splitLen);

		// Check to see if all elements of splitString are in the model
		
		
		// If so, get counts and probability. Done.
		
		// Otherwise, backoff, discounting
		// get counts and probability
		// Done.
		return 0;
	}

	// In this case, we do not consider word-based ngrams, but character-based.
	// That is, "abc" is a trigram.
	public float KatzBackoff(String s){
		int lens = s.length();
		
		// This is an error
		if (lens > this.maxLength){
			System.out.println("Whoops, input string is too long for the model.");
			return -1;
		}
		
		// Base case
		if(lens == 1){
			return ngramModels.get(1).get(s);
		}
		
		HashMap<String, Integer> contextModel = ngramModels.get(lens-1);
		HashMap<String, Integer> stringModel = ngramModels.get(lens);
		String context = s.substring(0, lens-1);
		
		// If the context exists...
		// TODO: make this discounted somehow.
		if (contextModel.containsKey(context)){
			float contextCounts = contextModel.get(context);
			
			float stringCounts;
			if (stringModel.containsKey(s)){
				stringCounts = stringModel.get(s);
			}else{
				stringCounts = 0;
			}
			
			return stringCounts / contextCounts;
			
		}
		
		// else, there is no context....
		float alpha = 1; //getAlpha(context)
		return alpha * KatzBackoff(s.substring(1, lens));
		
	}
	
	
	public float getSentProbWithN(String s, int n){
		String[] splitString = this.splitString(s, n);
		
		HashMap<String, Integer> currModel = ngramModels.get(n);
		
		ArrayList<String> backoffList = new ArrayList<String>();
		
		// Find those strings that are not in currModel, and back off/discount
		for (String hyp : splitString){
			if( !currModel.containsKey(hyp)){
				// this one will be backed off
				backoffList.add(hyp);
			}
		}
		
		for (String h : backoffList){
			// if h is the only string with that prefix to be missing stuff, add it back by stealing from the others.
			
			// if there are other strings in backofflist that have the same prefix as h, 
			// then steal from strings with prefix, and back off again to get relative probabilities.
						
		}
		
//		for (String t : splitString){
//			System.out.println(t);
//		}
//		
//		System.out.println(currModel);
		
		
		return 0;
	}
	
	private String[] splitString(String s, int n) { 
		ArrayList<String> segments = new ArrayList<String>();
		for (int i = 0; i < s.length() - n + 1; i++) {
			segments.add(s.substring(i, i + n));
		}
		return segments.toArray(new String[0]);
	}

	public static void main(String[] argv) {
		NgramLM model = new NgramLM();
		ArrayList<String> s1 = new ArrayList<String>(); 
		s1.add("bacbccbcca"); //s1.add("abraham lincoln");
		model.trainModel(s1);

		//s1.clear();
		//s1.add("baabbabaccbca");
		//System.out.println("\n\nRetrain");
		//model.trainModel(s1);
		
		//model.getSentProbWithN("baabbabaccbca", 2);
		float prob = model.KatzBackoff("bacbccba");
		System.out.println(prob);

	}
}
