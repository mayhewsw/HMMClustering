package edu.washington.cs.uei.disktable;

import edu.washington.cs.uei.util.GeneralUtility;

public abstract class AbstractComparisonObject implements ComparisonObject {
	private static final String _sep_ = BasicDiskTable.getSeparatorString();

	private GeneralUtility gu = new GeneralUtility();
	
	public String str = null;

	public void setString(String [] line) {
		str = gu.join(line, _sep_, 0, line.length-1);
	}
	
	public String getFlatLine() {
		return str;
	}

	public String[] getLine() {
		return str.split(_sep_);
	}
}
