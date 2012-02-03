package edu.washington.cs.uei.disktable;

import java.io.File;


public class DiskTableSorter {
	
	
	public static void sort(String [] args) {
		BasicDiskTable in = new BasicDiskTable(new File(args[0]));

		File out = new File(args[1]);
		
		int [] argsToSortOn = new int[args.length-2];
		for(int i=2; i<args.length; i++) {
			argsToSortOn[i-2] = Integer.parseInt(args[i]);
		}
		
		Comparator comp = new ArgSetComparator(argsToSortOn);
		
		in.sort(comp, out);		
	}
	
	public static void main(String[] args) {
		BasicDiskTable in = new BasicDiskTable(new File(args[0]));

		File out = new File(args[1]);
		
		int [] argsToSortOn = new int[args.length-2];
		for(int i=2; i<args.length; i++) {
			argsToSortOn[i-2] = Integer.parseInt(args[i]);
		}
		
		Comparator comp = new ArgSetComparator(argsToSortOn);
		
		in.sort(comp, out);
	}
}
