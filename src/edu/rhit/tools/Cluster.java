package edu.rhit.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/*
 * This class represents a cluster of strings. Every string in a cluster has the same meaning.
 * All strings must have this form (tab separated string, with 9 elements when split on tabs):
 * String s = "2626	Abraham Lincoln	was known as	Honest Abe	abraham lincoln	be know as	honest abe	2	0.94723";
 * 
 */
public class Cluster {

	private HashSet<String> hc;

	public Cluster() {
		this.hc = new HashSet<String>();
	}

	public Cluster(HashSet<String> hc) {
		this.hc = hc;
	}

	private String convertToClusterForm(String s) {
		int desiredLength = 9;
		return convertToClusterForm(s, desiredLength);
	}

	/*
	 * This takes a string, splits it on tab, checks to make sure the split list
	 * has the right length. Returns the string.
	 */
	private String convertToClusterForm(String s, int desiredLength) {
		String[] splitS = s.split("\t");
		if (splitS.length < desiredLength) {
			System.out.println(s);
			throw new IllegalArgumentException("String too short.");
		}

		// splitS = Arrays.copyOfRange(splitS, 0, desiredLength);

		String ret = "";
		for (int i = 0; i < desiredLength; i++) {
			ret += splitS[i] + "\t";
		}
		return ret.trim();

	}

	/*
	 * Add string to Cluster. This will split the string. This should only
	 * accept strings of a certain form.
	 */
	public void addString(String s) {
		this.hc.add(convertToClusterForm(s));
	}

	public void addStringNoConvert(String s) {
		this.hc.add(s);
	}

	/*
	 * Simple contains function Takes a string, and splits it
	 */
	public boolean contains(String s) {
		//s = convertToClusterForm(s);
		return this.hc.contains(s);

		// Can't do normal hc.contains because it doesn't do deep checking

		// for (Iterator<String[]> it = this.hc.iterator(); it.hasNext();) {
		// String[] stringArr = it.next();
		// if (Arrays.deepEquals(splitS, stringArr)) {
		// return true;
		// }
		// }
		// return false;

	}

	/*
	 * This method compares c against the current object and returns all strings
	 * that are in both
	 */
	public Cluster getIntersection(Cluster c) {
		Cluster inter = new Cluster();
		for (Iterator<String> it = this.hc.iterator(); it.hasNext();) {
			String s = it.next();
			if (c.contains(s)) {
				inter.addString(s);
			}
		}
		return inter;
	}

	public int size() {
		return hc.size();
	}

	public void clear() {
		this.hc.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cluster other = (Cluster) obj;
		if (hc == null) {
			if (other.hc != null)
				return false;
		} else if (!hc.equals(other.hc))
			return false;

		// If length of intersection is the
		// same as the length of this, return true
		Cluster inter = this.getIntersection(other);
		return inter.size() == this.size();

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String s = ""; //"==== Cluster, size: " + this.hc.size() + " ====\n";

		for (Iterator<String> it = this.hc.iterator(); it.hasNext();) {
			String t = it.next();
			//s += convertToClusterForm(t, 4) + "\n";
			s += t + "\n";
		}
		return s;
	}

}
