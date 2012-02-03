package edu.washington.cs.uei.disktable;

public class SingleArgumentComparator 
extends AbstractStringArrayComparator
implements Comparator
{
	int _arg_ = -1;
	byte _asc_ = 1;
	
	
	class SingleArgComparisonObject 
	extends AbstractComparisonObject 
	implements ComparisonObject 
	{
		public String str;
		public String compareStr;
	}
	
	
	public SingleArgumentComparator(int arg, boolean ascending) {
		_arg_ = arg;
		if(!ascending) {
			_asc_ = -1;
		}
	}	
			
	public int compare(String[] s1, String[] s2) {
		if(s1==null) {
			return -2 * _asc_;
		}
		else if(s2==null) {
			return +2 * _asc_;
		}
		else if(s1[_arg_]==null) {
			return -2 * _asc_;
		}
		return _asc_ * s1[_arg_].compareTo(s2[_arg_]);
	}

	public SingleArgComparisonObject getComparisonObject(String[] s1) {
		SingleArgComparisonObject ret = new SingleArgComparisonObject();
		ret.setString(s1);
		ret.compareStr = s1[_arg_];
		return ret;
	}

	public int compare(SingleArgComparisonObject saco1, SingleArgComparisonObject saco2) {
		return _asc_ * saco1.compareStr.compareTo(saco2.compareStr);
	}
	
	public int compare(ComparisonObject o1, ComparisonObject o2) {
		if(o1.getClass().getName().equals(SingleArgComparisonObject.class.getName()) &&
				o2.getClass().getName().equals(SingleArgComparisonObject.class.getName())) {
			return compare((SingleArgComparisonObject)o1, (SingleArgComparisonObject)o2);
		}
		return 0;
	}	
}

	
