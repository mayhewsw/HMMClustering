package edu.washington.cs.uei.util;

import java.util.Set;

public interface Counter {
	public void add(Object o);
	public void removeOne(Object o);
	public void removeAll(Object o);
	public int getCount(Object o);
	public Set keySet();
	public int numDistinctObjects();
	public int numTotalObjects();
}
