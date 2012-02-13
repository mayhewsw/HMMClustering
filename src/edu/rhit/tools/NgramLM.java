package edu.rhit.tools;

import java.util.ArrayList;
import java.util.HashMap;

public class NgramLM {
	private HashMap<Integer, HashMap<String, Integer>> ngramModels;
	private int maxLength = 0;
	
	// maxlength should just be the length of the string
	public NgramLM() { 
		this.ngramModels = new HashMap<Integer, HashMap<String, Integer>>();
//		this.maxLength = maxLength;
	}
	
	public void trainModel(ArrayList<String> strings) {

		int oldMaxLength = this.maxLength;
		// get shortest one
		this.maxLength = lenShortestString(strings)+1;
	
		// if oldMaxLength > maxLength, remove all hashmaps that are too long.
		if (oldMaxLength > this.maxLength){
			for(int i= this.maxLength+1; i < oldMaxLength; i++){
				ngramModels.remove(i);
			}
		}
		
		// Split for all ngram lengths
		for (int i = 1; i < this.maxLength; i++) {
			if (!ngramModels.containsKey(i)) ngramModels.put(i, new HashMap<String, Integer>());
			HashMap<String, Integer> model = ngramModels.get(i);
			for (String s : strings) {
				String[] segments = splitString(s, i);
				for (String segment : segments) {
					int count = (model.containsKey(segment)) ? model.get(segment) : 0;
					model.put(segment, ++count);
				}
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
		
		// Check to see 
		
		
		// check to see if all splits are in the model
		// if so, get counts and probability. Done.
		// otherwise, backoff, discounting
		// get counts and probability
		// Done.
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
		s1.add("abe lincoln"); s1.add("abraham lincoln");
		model.trainModel(s1);
		
		s1.add("lincoln");
		System.out.println("\n\nRetrain\n\n");
		model.trainModel(s1);
		
	}
}
