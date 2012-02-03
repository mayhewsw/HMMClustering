package edu.washington.cs.uei.util;

import java.util.regex.Pattern;

public class Morphology {
	
	private static final Pattern whitespace = Pattern.compile("\\s");
	private final static Pattern _entity_pattern_ = Pattern.compile("[a-zA-Z',.\\- ]*");
	private final static Pattern _relation_pattern_ = Pattern.compile("[a-z'\\- ]*");
	private final static Pattern _number_pattern_ = Pattern.compile("[0-9,.\\-]*");
	private final static Pattern _bad_relation_pattern_;
	
	public static final String _PROPER_ = "P";
	public static final String _NONPROPER_ = "N";
	
	static {
		String [] badWords = {
			"and",
			"that",
			"those",
			"this",
			"these",
			"he",
			"him",
			"his",
			"she",
			"her",
			"hers",
			"it",
			"its",
			"you",
			"yours",
			"i",
			"me",
			"mine",
			"we",
			"us",
			"ours",
			"they",
			"them",
			"theirs"
		};
		
		StringBuffer patternStr = new StringBuffer("");
		for(int i=0; i<badWords.length; i++) {
			patternStr.append("( " + badWords[i]+" )|");
			patternStr.append("(^" + badWords[i]+" )|");
			patternStr.append("( " + badWords[i]+"$)");
			if(i<badWords.length-1) {
				patternStr.append("|");
			}
		}
		//System.out.println(patternStr.toString());
		_bad_relation_pattern_ = Pattern.compile(patternStr.toString());
	}
	
	public static boolean isProperNoun(String s) {
		boolean ret = true;
		
		if(s==null) {
			return false;
		}
		
		String [] splitArr = s.split(" ");
		int numCommon = 0;
		for(int i=0; i<splitArr.length; i++) {
			if(splitArr[i].length()>0 && Character.isLowerCase(splitArr[i].charAt(0))) {
				if(numCommon>0 || i==0 || i==splitArr.length-1) {
					ret = false;
					break;
				}
				numCommon = 1;
			}
		}
		
		return ret;
	}
	
	
	public boolean stringValidityTest(String name, boolean isEntity) {
		if(name==null || "".equals(name)) {
			return false;
		}
		
		if(isEntity) {
			if(_entity_pattern_.matcher(name).matches()) {
				return true;
			}
			return false;
		}
		else {
			if(_relation_pattern_.matcher(name).matches() || "IS A".equals(name)) {
				return true;
			}
			return false;
		}
	}
	
	public boolean restrictiveStringValidityTest(String name, boolean isEntity)
	{
		if(name==null || "".equals(name)) {
			return false;
		}
		
		if(isEntity) {
			if(_entity_pattern_.matcher(name).matches()) {
				return true;
			}
			return false;
		}
		else {
			if(_bad_relation_pattern_.matcher(name).find()) {
				return false;
			}
			if(_relation_pattern_.matcher(name).matches() || "IS A".equals(name)) {
				return true;
			}
			return false;
		}
	}
	
	public boolean numericOrCurrency(String n) {
		if(n==null || n.length()<=0) {
			return false;
		}
		if(n.charAt(0)=='$' && _number_pattern_.matcher(n.substring(1,n.length())).matches()) {
			return true;
		}
		if(_number_pattern_.matcher(n).matches()) {
			return true;
		}
		return false;
	}
	
	public String extractProperNoun(String argString) {
		String [] tokens = whitespace.split(argString);
		
		//boolean lowercaseUsed = false;
		boolean match = true;
		
		for(int i=0; i<tokens.length; i++) {
			if(tokens[i].length()<=0) {
				match = false;
				break;
			}
			else if(!Character.isUpperCase(tokens[i].charAt(0))) {
				match = false;
				break;
			}
			/*
			else if(Character.isLowerCase(tokens[i].charAt(0))) {
				if(lowercaseUsed || i==0 || i==tokens.length-1) {
					match = false;
					break;
				}
				lowercaseUsed = true;
			}*/
			/*
			else if(!Character.isUpperCase(tokens[i].charAt(0)) &&
					!numericOrCurrency(tokens[i])) {
				match = false;
				break;
			}*/
			
		}
		
		if(match && stringValidityTest(argString, true)) {
			return argString;
		}
		return null;
	}
	
	
	public static void main(String [] args) {
		Morphology morph = new Morphology();
		System.out.println(morph.extractProperNoun("208 km northwest"));
		System.out.println(morph.extractProperNoun("things"));
		String [] test = {
				"area",
				"Copernicus",
				"Galileo Galilei",
				"January 2, 2005",
				"January 2",
				"Baron von Hofstadt",
				"Djey el Djey"
				};
		for(int i=0; i<test.length; i++) {
			System.out.println(test[i] + ":  " + morph.extractProperNoun(test[i]));
		}
		
		String [] relation_test = {
				"is",
				"was not",
				"was So",
				"add-up",
				"he gave",
				"said that",
				"was she said",
				"was sheer"
		};
		for(int i=0; i<relation_test.length; i++) {
			System.out.println(relation_test[i] + ":  " + morph.restrictiveStringValidityTest(relation_test[i], false));
		}
		
	}
}
