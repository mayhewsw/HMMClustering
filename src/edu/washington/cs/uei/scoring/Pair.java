package edu.washington.cs.uei.scoring;

public class Pair {
		public String [] s1, s2;

		
		public Pair(String [] str1, String [] str2)
		{
			s1 = str1;
			s2 = str2;
			
			if((s1==null && s2!=null) ||
			   (s1!=null && s2!=null && s1.length==0 && s2.length>0) ||
			   (s1!=null && s1.length>0 && s2!=null && s2.length>0 && s1[0].compareTo(s2[0])<0)) 
			{
				s1 = str2;
				s2 = str1;
			}
		}
		
		private String join(String [] splitArr, String sep)
		{
			if(splitArr==null ||splitArr.length<=0) {
				return "";
			}
			StringBuffer ret = new StringBuffer(splitArr[0]);
			for(int i=1; i<splitArr.length; i++) {
				ret.append(sep);
				ret.append(splitArr[i]);
			}
			return ret.toString();
		}

		
		public String toString() {
			return join(s1, " -- ") + " :::: " + join(s2, " -- ");
		}
		
		public int hashCode() {
			int ret = 0;
			if(s1!=null && s1.length>0) {
				ret += s1[0].hashCode();
			}
			if(s2!=null && s2.length>0) {
				ret += s2[0].hashCode();
			}
			return ret;
		}
		
		public boolean equals(Object o) {
			if(!o.getClass().getName().equals("edu.washington.cs.uei.scoring.Pair")) {
				return false;
			}
			Pair p = (Pair)o;
			if((this.s1==null && p.s1!=null) || this.s1!=null && p.s1==null) {
				return false;
			}
			if((this.s1.length>0 && p.s1.length<=0) || this.s1.length<=0 && p.s1.length>0) {
				return false;
			}
			if(!this.s1[0].equals(p.s1[0])) {
				return false;
			}
			if((this.s2==null && p.s2!=null) || this.s2!=null && p.s2==null) {
				return false;
			}
			if((this.s2.length>0 && p.s2.length<=0) || this.s2.length<=0 && p.s2.length>0) {
				return false;
			}
			if(!this.s2[0].equals(p.s2[0])) {
				return false;
			}
			return true;
		}
}