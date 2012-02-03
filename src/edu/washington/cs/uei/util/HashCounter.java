package edu.washington.cs.uei.util;

import java.util.HashMap;
import java.util.Set;

public class HashCounter implements Counter {
	private HashMap hm = null;
	private int numDistinct = -1;
	private int numTotal = -1;
	
	public HashCounter() {
		hm = new HashMap();
		numDistinct = 0;
		numTotal = 0;
	}
	
	public Set keySet() {
		return hm.keySet();
	}
	
	public void add(Object o) {
		numTotal++;
		if(hm.containsKey(o)) {
			int count = ((Integer)hm.get(o)).intValue();
			hm.put(o, new Integer(count+1));
		}
		else {
			hm.put(o, new Integer(1));
			numDistinct++;
		}
	}

	public void removeOne(Object o) {
		if(hm.containsKey(o)) {
			numTotal--;
			int count = ((Integer)hm.get(o)).intValue();
			if(count > 1) {
				hm.put(o, new Integer(count -1));
			}
			else {
				hm.remove(o);
				numDistinct--;
			}
		}
	}

	public void removeAll(Object o) {
		if(hm.containsKey(o)) {
			int count = ((Integer)hm.get(o)).intValue();
			numDistinct--;
			numTotal -= count;
		}
		hm.remove(o);
	}

	public int getCount(Object o) {
		if(hm.containsKey(o)) {
			return ((Integer)hm.get(o)).intValue();
		}
		return 0;
	}

	public int numDistinctObjects() {
		return numDistinct;
	}

	public int numTotalObjects() {
		return numTotal;
	}
}
