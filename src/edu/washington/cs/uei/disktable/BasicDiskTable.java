package edu.washington.cs.uei.disktable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class BasicDiskTable implements DiskTable {
	private final static String _sep_ = " :::: ";
	private final static int maxOpenFiles = 1000;
	private static String _temp_file_base_ = 
		(new File("scratch/__temp__")).getAbsolutePath();
	private int maxMemData = 1000000;
	private File _table_file_ = null;
	private File _log_file_ = null;
	private BufferedReader br = null;
	private PrintWriter pw = null;
	
	private class FilenamePatternFilter implements java.io.FilenameFilter
	{
		String __prefix;
		FilenamePatternFilter(String pre) {
			__prefix = pre;
		}
		public boolean accept(File dir, String name) {
			return (name!=null && name.startsWith(__prefix));
		}
		
	}
	
	public static String getSeparatorString() {
		return _sep_;
	}
	
	public static void setScratchSpace(File scratchDir) {
		_temp_file_base_ = (new File(scratchDir, "__temp__")).getAbsolutePath();
	}
	
	public BasicDiskTable(File tableFile) {
		_table_file_ = tableFile;
	}
	
	public BasicDiskTable(File tableFile, File logFile) {
		_table_file_ = tableFile;
		_log_file_   = logFile;
	}
	
	public String separator() {
		return _sep_;
	}

	public File getFile() {
		return _table_file_;
	}

	public File getLogFile() {
		return _log_file_;
	}

	public void setLogFile(File logFile) {
		_log_file_ = logFile;
		
	}

	public void sort(int pos, boolean ascending, File output) {
		SingleArgumentComparator sac = new SingleArgumentComparator(pos, ascending);
		sort(sac, output);
	}

	
	private void sortArray(
			ComparisonObject [] arr,
			int maxRow, 
			Comparator comp)
	{
		quicksort(arr, 0, maxRow-1, comp);
	}
	
	private void quicksort(ComparisonObject [] x, int first, int last, Comparator comp) {
	    int [] topOfLower = new int[1];
	    int [] bottomOfUpper = new int[1];
	    if(first < last) {
	    	if(first+8 < last) {
	    		partition(x, first, last, topOfLower, bottomOfUpper, comp);
	    		quicksort(x, first, topOfLower[0], comp);
	    		quicksort(x, bottomOfUpper[0], last, comp);
	    	}
	    	else {
	    		insertSort(x, first, last, comp);
	    	}
	    }		
	}

	int partition(
			ComparisonObject [] y, 
			int f, 
			int l, 
			int [] topOfLower, 
			int [] bottomOfUpper, 
			Comparator comp) 
	{
	    int up,down;
	    ComparisonObject temp = null;
	    int pos = (int) (Math.random()*(l-f) + f);
	    //System.out.println("Partition:  " + f + ", " + pos + ", " + l);
	    ComparisonObject piv = y[pos];
	    
	    up = f;
	    down = l;

        while (comp.compare(y[up], piv) <= 0 && up < down) {
            up++;
        }
        while (comp.compare(y[down], piv) > 0 && down > up ) {
            down--;
        }
	    
	    do { 
	        temp = y[up];
	        y[up] = y[down];
	        y[down] = temp;
	    
	        while (comp.compare(y[up], piv) <= 0 && up < l) {
	            up++;
	        }
	        while (comp.compare(y[down], piv) > 0 && down > f ) {
	            down--;
	        }
	    } while (down > up);
	    //y[f] = y[down];
	    //y[down] = piv;
	    
	    bottomOfUpper[0] = down + 1;
	    
	    // separate items equal to piv from items less than piv
	    
	    up = f;
	    while(comp.compare(y[up], piv)<0 && up < down) {
	    	up++;
	    }
	    while(comp.compare(y[down], piv)>=0 && down > up) {
	    	down--;
	    }
	    do {
	    	temp = y[up];
	    	y[up] = y[down];
	    	y[down] = temp;

	        while (comp.compare(y[up], piv) < 0 && up < l) {
	            up++;
	        }
	        while (comp.compare(y[down], piv) >= 0 && down > f ) {
	            down--;
	        }
	    	
	    } while(down > up);
	    
	    topOfLower[0] = up - 1;
	    
	    //System.out.println("Partition:  " + f + ", " + topOfLower[0] + ", " + bottomOfUpper[0] + ", " + l);
	    
	    return down;
	}
	
	private void insertSort(
			ComparisonObject [] arr,
			int start,
			int end,
			Comparator comp)
	{
	     int indPost, indAnt; 
	     ComparisonObject nextElement;
	 
	     for(indPost = start + 1; indPost < end+1; indPost++) {
	         nextElement = arr[indPost];
	         for (indAnt = indPost  - 1; 
	         	  indAnt  >= 0 && comp.compare(arr[indAnt], nextElement) > 0; 
	         	  --indAnt) 
	         {
	             arr[indAnt + 1] = arr[indAnt];
	         }
	         arr[indAnt + 1] = nextElement ;
	     }
	}
	
	
	
	private void sortArray(
			String [][]arr, 
			int maxRow,
			Comparator comp) 
	{
		quicksort(arr, 0, maxRow-1, comp);
	}
		
	 void insertSort(String [][] arr,
			         int start,
			         int end,
			         Comparator comp) 
	 {
	     int indPost, indAnt; 
	     String [] nextElement;
	 
	     for(indPost = start + 1; indPost < end+1; indPost++) {
	         nextElement = arr[indPost];
	         for (indAnt = indPost  - 1; 
	         	  indAnt  >= 0 && comp.compare(arr[indAnt], nextElement) > 0; 
	         	  --indAnt) 
	         {
	             arr[indAnt + 1] = arr[indAnt];
	         }
	         arr[indAnt + 1] = nextElement ;
	     }
	 }
	 
	 
	void quicksort(String [][] x, int first, int last, Comparator comp) {
		//System.out.println("quicksort " + first + " " + last);
		//System.out.flush();
		
	    //int pivIndex = 0;
	    int [] topOfLower = new int[1];
	    int [] bottomOfUpper = new int[1];
	    if(first < last) {
	    	if(first+8 < last) {
	    		//pivIndex = partition(x,first, last, topOfLower, bottomOfUpper, comp);
	    		partition(x,first, last, topOfLower, bottomOfUpper, comp);
	    		quicksort(x,first,topOfLower[0], comp);
	    		quicksort(x,bottomOfUpper[0],last, comp);
	    	}
	    	else {
	    		insertSort(x, first, last, comp);
	    	}
	    }
	}

	int partition(String [][] y, 
			      int f, 
			      int l, 
			      int [] topOfLower, 
			      int [] bottomOfUpper, 
			      Comparator comp) 
	{
	    int up,down;
	    String [] temp = null;
	    int pos = (int) (Math.random()*(l-f) + f);
	    //System.out.println("Partition:  " + f + ", " + pos + ", " + l);
	    String [] piv = y[pos];
	    //String [] piv = y[f];
	    
	    up = f;
	    down = l;

        while (comp.compare(y[up], piv) <= 0 && up < down) {
            up++;
        }
        while (comp.compare(y[down], piv) > 0 && down > up ) {
            down--;
        }
	    
	    do { 
	        temp = y[up];
	        y[up] = y[down];
	        y[down] = temp;
	    
	        while (comp.compare(y[up], piv) <= 0 && up < l) {
	            up++;
	        }
	        while (comp.compare(y[down], piv) > 0 && down > f ) {
	            down--;
	        }
	    } while (down > up);
	    //y[f] = y[down];
	    //y[down] = piv;
	    
	    bottomOfUpper[0] = down + 1;
	    
	    // separate items equal to piv from items less than piv
	    
	    up = f;
	    while(comp.compare(y[up], piv)<0 && up < down) {
	    	up++;
	    }
	    while(comp.compare(y[down], piv)>=0 && down > up) {
	    	down--;
	    }
	    do {
	    	temp = y[up];
	    	y[up] = y[down];
	    	y[down] = temp;

	        while (comp.compare(y[up], piv) < 0 && up < l) {
	            up++;
	        }
	        while (comp.compare(y[down], piv) >= 0 && down > f ) {
	            down--;
	        }
	    	
	    } while(down > up);
	    
	    topOfLower[0] = up - 1;
	    
	    //System.out.println("Partition:  " + f + ", " + topOfLower[0] + ", " + bottomOfUpper[0] + ", " + l);
	    
	    return down;
	}
	
	private BasicDiskTable writeArray(
			ComparisonObject [] arr,
			int maxRow,
			File fout) 
	{
		BasicDiskTable ret = new BasicDiskTable(fout);
		ret.openForWriting();
		for(int i=0; i<maxRow; i++) {
			ret.printFlatLine(arr[i].getFlatLine());	// assumes _sep_ in ComparisonObject
														// is same as _sep_ in this class
			//ret.println(arr[i].getLine());
		}
		ret.flush();
		ret.closeForWriting();
		return ret;
	}
	
	private BasicDiskTable writeArray(String [][]arr, 
									  int maxRow,
									  File fout) 
	{
		BasicDiskTable ret = new BasicDiskTable(fout);
		ret.openForWriting();
		for(int i=0; i<maxRow; i++) {
			ret.println(arr[i]);
		}
		ret.flush();
		ret.closeForWriting();
		return ret;
	}
	
	private BasicDiskTable mergeSortedDiskTables(
			BasicDiskTable[] toMerge,
			Comparator comp,
			File outFile)
	{
		BasicDiskTable ret = new BasicDiskTable(outFile);
		ret.openForWriting();
		
		String[][] inlines = new String[toMerge.length][];
		String [] bestNextLine = null;
		for(int i=0; i<toMerge.length; i++) {
			toMerge[i].open();
			inlines[i] = toMerge[i].readLine();
		}
		bestNextLine = null;
		int bestPos = -1;

		boolean remainingInputs = true;
		while(remainingInputs) {
			remainingInputs = false;
			bestNextLine = null;
			bestPos = -1;
			
			for(int i=0; i<inlines.length; i++) {
				if(inlines[i]!=null) {
					remainingInputs = true;
					if(bestNextLine==null || comp.compare(inlines[i], bestNextLine)<0) {
						bestPos = i;
						bestNextLine = inlines[i];
					}
				}
			}

			if(bestPos>=0) {
				ret.println(bestNextLine);
				inlines[bestPos] = toMerge[bestPos].readLine();
			}
		}
		
		ret.flush();
		ret.closeForWriting();
		
		for(int i=0; i<toMerge.length; i++) {
			toMerge[i].close();
		}
		
		return ret;
	}
	
	private BasicDiskTable mergeSortedDiskTables(
			BasicDiskTable dt1, 
			BasicDiskTable dt2,
			Comparator comp,
			File outFile)
	{
		BasicDiskTable ret = new BasicDiskTable(outFile);
		ret.openForWriting();
		
		dt1.open();
		dt2.open();
		String [] t1 = dt1.readLine();
		String [] t2 = dt2.readLine();
		
		ComparisonObject co1 = null, co2 = null;
		if(t1!=null && t2!=null) {
			co1 = comp.getComparisonObject(t1);
			co2 = comp.getComparisonObject(t2);
		}
		while(t1!=null && t2!=null) {
			if(comp.compare(co1, co2)<=0) {
				ret.printFlatLine(co1.getFlatLine());		// assumes _sep_ in ComparisonObject
															// is same as _sep_ in this class
				//ret.println(t1);
				t1 = dt1.readLine();
				if(t1!=null) {
					co1 = comp.getComparisonObject(t1);
				}
			}
			else {
				ret.printFlatLine(co2.getFlatLine());		// same assumption as above
				//ret.println(t2);
				t2 = dt2.readLine();
				if(t2!=null) {
					co2 = comp.getComparisonObject(t2);
				}
			}
		}
		
		if(t1!=null) {
			ret.printFlatLine(co1.getFlatLine());
			//ret.println(t1);
			String t = dt1.readFlatLine();
			while(t!=null) {
				ret.printFlatLine(t);
				t = dt1.readFlatLine();
			}
		}
		dt1.close();
		
		if(t2!=null) {
			ret.printFlatLine(co2.getFlatLine());
			//ret.println(t2);
			String t = dt2.readFlatLine();
			while(t!=null) {
				ret.printFlatLine(t);
				t = dt2.readFlatLine();
			}
		}
		dt2.close();
		
		ret.flush();
		ret.closeForWriting();
		
		return ret;
	}
	
	private BasicDiskTable oldMergeSortedDiskTables(BasicDiskTable dt1, 
												 BasicDiskTable dt2,
												 Comparator comp,
												 File outFile) 
	{
		
		BasicDiskTable ret = new BasicDiskTable(outFile);
		ret.openForWriting();
		
		dt1.open();
		dt2.open();
		String [] t1 = dt1.readLine();
		String [] t2 = dt2.readLine();
		while(t1!=null && t2!=null) {
			if(comp.compare(t1, t2)<=0) {
				ret.println(t1);
				t1 = dt1.readLine();
			}
			else {
				ret.println(t2);
				t2 = dt2.readLine();
			}
		}
		
		while(t1!=null) {
			ret.println(t1);
			t1 = dt1.readLine();
		}
		dt1.close();
		
		while(t2!=null) {
			ret.println(t2);
			t2 = dt2.readLine();
		}
		dt2.close();
		
		ret.flush();
		ret.closeForWriting();
		
		return ret;
	}
	
	private void deleteListOfFiles(List<BasicDiskTable> dts, List<BasicDiskTable> notDTs) {
		System.gc();
		for(Iterator<BasicDiskTable> it = dts.iterator(); it.hasNext(); ) {
			BasicDiskTable dt = it.next(); 
			File f = dt.getFile();
			if(notDTs!=null && notDTs.contains(dt)) {
				continue;
			}
			System.out.println("Deleting file " + f.getAbsolutePath());
			if(f.delete()) {
				System.out.println("Successfully deleted.");
			}
			else {
				System.out.println("Could not DELETE!");
			}
		}
	}
	
	public void doOneIterationOfMerges(Comparator comp, File output, int iter)
	{
		
		File temp_file = new File(_temp_file_base_);
		File work_dir = temp_file.getParentFile();
		String tempFname = temp_file.getName();
		FilenamePatternFilter fpf = new FilenamePatternFilter(tempFname+iter+"_");
		File [] infiles = work_dir.listFiles(fpf);
		if(infiles==null || infiles.length<=0) {
			return;
		}
		
		System.out.println("work_dir = " + work_dir.getAbsolutePath());
		System.out.println("tempFname = " + tempFname);
		for(int i=0; i<infiles.length; i++) {
			System.out.println(infiles[i].getAbsolutePath());
		}
		System.out.flush();
		//System.exit(1);
		
		int chunkNum = 0;
		for(int i=0; i<infiles.length-1; i+=2) {
			BasicDiskTable dt1 = new BasicDiskTable(infiles[i]);
			BasicDiskTable dt2 = new BasicDiskTable(infiles[i+1]);
			File mergeFile = new File(_temp_file_base_ + (iter+1) + "_" + chunkNum);
			mergeSortedDiskTables(dt1, dt2, comp, mergeFile);
			chunkNum++;
		}
	}
	
	public void sort(Comparator comp, File output) 
	{
		LinkedList<BasicDiskTable> dtList = new LinkedList<BasicDiskTable>();
		open();
		String [][] chunk = new String[maxMemData][];
		int totalLine = 0;
		int arrayLine = 0;
		int chunkNum = 0;
		String [] line = readLine();
		while(line!=null) {
			totalLine++;
			
			if(arrayLine>=maxMemData) {
				sortArray(chunk, maxMemData, comp);
				dtList.add(writeArray(chunk, maxMemData, new File(_temp_file_base_ +"1_"+chunkNum)));
				chunk = null;
				chunk = new String[maxMemData][];
				arrayLine = 0;
				chunkNum++;
			}

			chunk[arrayLine] = line;
			arrayLine++;
			
			line = readLine();
		}
		close();
		
		sortArray(chunk, arrayLine, comp);
		if(chunkNum==0) {
			writeArray(chunk, arrayLine, output);
			return;
		}
		dtList.add(writeArray(chunk, arrayLine, new File(_temp_file_base_+"1_"+chunkNum)));
		chunk = null;
		
		// merge the sorted disktables
		int numDTs = dtList.size();
		int iter = 2;
		while(numDTs>maxOpenFiles) {
			LinkedList<BasicDiskTable> newDTList = new LinkedList<BasicDiskTable>();
			
			chunkNum = 0;
			for(Iterator<BasicDiskTable> it = dtList.iterator(); it.hasNext(); ) {
				BasicDiskTable dt1 = it.next();
				if(!it.hasNext()) {
					newDTList.add(dt1);
					break;
				}
				BasicDiskTable dt2 = (BasicDiskTable)it.next();
				File mergeFile = new File(_temp_file_base_ + iter + "_" + chunkNum);
				newDTList.add(mergeSortedDiskTables(dt1, dt2, comp, mergeFile));
				chunkNum++;
			}
			
			deleteListOfFiles(dtList, newDTList);
			dtList = newDTList;
			numDTs = (numDTs / 2) + (numDTs % 2);
			iter++;
		}
		
		if(numDTs==1) {
			System.err.println("Error!  Ran out of things to merge!");
		}
		else {
			BasicDiskTable[] dts = new BasicDiskTable[numDTs];
			int i=0;
			for(Iterator<BasicDiskTable> it = dtList.iterator(); it.hasNext(); ) {
				dts[i] = it.next();
				i++;
			}
			mergeSortedDiskTables(dts, comp, output);
			deleteListOfFiles(dtList, null);
		}
	}	
	
	public void oldSort2(Comparator comp, File output) 
	{
		LinkedList<BasicDiskTable> dtList = new LinkedList<BasicDiskTable>();
		open();
		ComparisonObject [] chunk = new ComparisonObject[maxMemData];
		int totalLine = 0;
		int arrayLine = 0;
		int chunkNum = 0;
		String [] line = readLine();
		while(line!=null) {
			totalLine++;
			
			if(arrayLine>=maxMemData) {
				sortArray(chunk, maxMemData, comp);
				dtList.add(writeArray(chunk, maxMemData, new File(_temp_file_base_ +"1_"+chunkNum)));
				chunk = null;
				chunk = new ComparisonObject[maxMemData];
				arrayLine = 0;
				chunkNum++;
			}

			chunk[arrayLine] = comp.getComparisonObject(line);
			arrayLine++;
			
			/*
			if(arrayLine%100000==0) {
				System.out.println("reached line " + arrayLine);
				System.out.flush();
			}*/
			
			line = readLine();
		}
		close();
		
		sortArray(chunk, arrayLine, comp);
		if(chunkNum==0) {
			writeArray(chunk, arrayLine, output);
			return;
		}
		dtList.add(writeArray(chunk, arrayLine, new File(_temp_file_base_+"1_"+chunkNum)));
		chunk = null;
		
		// merge the sorted disktables
		int numDTs = dtList.size();
		int iter = 2;
		while(numDTs>2) {
			LinkedList<BasicDiskTable> newDTList = new LinkedList<BasicDiskTable>();
			
			chunkNum = 0;
			for(Iterator<BasicDiskTable> it = dtList.iterator(); it.hasNext(); ) {
				BasicDiskTable dt1 = it.next();
				if(!it.hasNext()) {
					newDTList.add(dt1);
					break;
				}
				BasicDiskTable dt2 = (BasicDiskTable)it.next();
				File mergeFile = new File(_temp_file_base_ + iter + "_" + chunkNum);
				newDTList.add(mergeSortedDiskTables(dt1, dt2, comp, mergeFile));
				chunkNum++;
			}
			
			deleteListOfFiles(dtList, newDTList);
			dtList = newDTList;
			numDTs = (numDTs / 2) + (numDTs % 2);
			iter++;
		}
		
		if(numDTs==1) {
			System.err.println("Error!  Ran out of things to merge!");
		}
		else {
			BasicDiskTable dt1 = (BasicDiskTable)dtList.get(0);
			BasicDiskTable dt2 = (BasicDiskTable)dtList.get(1);
			mergeSortedDiskTables(dt1, dt2, comp, output);
			deleteListOfFiles(dtList, null);
		}
	}
	
	public void oldSort(Comparator comp, File output) 
	{
		LinkedList<BasicDiskTable> dtList = new LinkedList<BasicDiskTable>();
		open();
		String [][] chunk = new String[maxMemData][];
		int totalLine = 0;
		int arrayLine = 0;
		int chunkNum = 0;
		String [] line = readLine();
		while(line!=null) {
			totalLine++;
			
			if(arrayLine>=maxMemData) {
				sortArray(chunk, maxMemData, comp);
				dtList.add(writeArray(chunk, maxMemData, new File(_temp_file_base_ +"1_"+chunkNum)));
				chunk = null;
				chunk = new String[maxMemData][];
				arrayLine = 0;
				chunkNum++;
			}

			chunk[arrayLine] = line;
			arrayLine++;
			
			/*
			if(arrayLine%100000==0) {
				System.out.println("reached line " + arrayLine);
				System.out.flush();
			}*/
			
			line = readLine();
		}
		
		sortArray(chunk, arrayLine, comp);
		if(chunkNum==0) {
			writeArray(chunk, arrayLine, output);
			return;
		}
		dtList.add(writeArray(chunk, arrayLine, new File(_temp_file_base_+"1_"+chunkNum)));
		chunk = null;
		
		// merge the sorted disktables
		int numDTs = dtList.size();
		int iter = 2;
		while(numDTs>2) {
			LinkedList<BasicDiskTable> newDTList = new LinkedList<BasicDiskTable>();
			
			chunkNum = 0;
			for(Iterator<BasicDiskTable> it = dtList.iterator(); it.hasNext(); ) {
				BasicDiskTable dt1 = it.next();
				if(!it.hasNext()) {
					newDTList.add(dt1);
					break;
				}
				BasicDiskTable dt2 = (BasicDiskTable)it.next();
				File mergeFile = new File(_temp_file_base_ + iter + "_" + chunkNum);
				newDTList.add(oldMergeSortedDiskTables(dt1, dt2, comp, mergeFile));
				chunkNum++;
			}
			
			deleteListOfFiles(dtList, newDTList);
			dtList = newDTList;
			numDTs = (numDTs / 2) + (numDTs % 2);
			iter++;
		}
		
		if(numDTs==1) {
			System.err.println("Error!  Ran out of things to merge!");
		}
		else {
			BasicDiskTable dt1 = (BasicDiskTable)dtList.get(0);
			BasicDiskTable dt2 = (BasicDiskTable)dtList.get(1);
			oldMergeSortedDiskTables(dt1, dt2, comp, output);
			deleteListOfFiles(dtList, null);
		}
	}

	public void open() {
		try {
			br = new BufferedReader(new FileReader(_table_file_));
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			if(br!=null) {
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String [] readLine() {
		try {
			if(br!=null) {
				String line = br.readLine();
				if(line!=null) {
					/*
					String [] lineArr = line.split(_sep_);
					if(lineArr==null || lineArr.length<13) {
						System.err.println("Found line with length < 13");
						System.err.println(line);
						System.err.flush();
					}*/
					return line.split(_sep_);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String [] readLine(String sep) {
		try {
			if(br!=null) {
				String line = br.readLine();
				if(line!=null) {
					/*
					String [] lineArr = line.split(_sep_);
					if(lineArr==null || lineArr.length<13) {
						System.err.println("Found line with length < 13");
						System.err.println(line);
						System.err.flush();
					}*/
					return line.split(sep);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String readFlatLine() {
		try {
			if(br!=null) {
				String line = br.readLine();
				if(line!=null) {
					//return line.trim();
					return line;
				}
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void openForWriting() {
		try {
			pw = new PrintWriter(new FileWriter(_table_file_));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void openForWriting(boolean append) {
		try {
			pw = new PrintWriter(new FileWriter(_table_file_, append));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeForWriting() {
		pw.close();
	}

	public void flush() {
		pw.flush();
	}

	private String join(String [] arr, String sep) {
		if(arr==null || arr.length==0) {
			return "";
		}
		StringBuffer sb = new StringBuffer(arr[0]);
		for(int i=1; i<arr.length; i++) {
			sb.append(sep);
			sb.append(arr[i]);
		}
		return sb.toString();
	}
	
	public void printFlatLine(String tuple) {
		pw.println(tuple);
	}
	
	public void println(String[] tuple) {
		pw.println(join(tuple, _sep_));
	}

	public boolean checkIfSorted(Comparator cmp) {
		boolean ret = true;
		
		this.open();
		String [] first = this.readLine();
		if(first==null) {
			return true;
		}
		String [] second = this.readLine();
		int num_errors = 0;
		int num_lines = 2;
		while(second!=null) {
			if(cmp.compare(first, second)>0) {
				ret = false;
				System.err.println("FIRST:   " + join(first, " :::: "));
				System.err.println("SECOND:  " + join(second, " :::: "));
				num_errors++;
			}
			
			first = second;
			second = this.readLine();
			num_lines++;
		}
		
		System.err.println("num errors = " + num_errors);
		System.err.println("num lines = " + num_lines);
		
		return ret;
	}
	
	public boolean checkIfSorted(int arg, boolean ascending) {
		SingleArgumentComparator sac = new SingleArgumentComparator(arg, ascending);
		return checkIfSorted(sac);
	}
	
	public static void main(String [] args) {
		//String workspace = "C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\disk_table_0\\";
		String workspace = "C:\\Documents and Settings\\ayates\\My Documents\\eclipse_workspace\\object_identification1\\scratch\\";
		//String workspace = "C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\fallback\\";
		//String workspace = "C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\all_domain\\";
		
		
		int [] compArgs = new int[4];
		compArgs[0] = 5;
		compArgs[1] = 13;
		compArgs[2] = 4;
		compArgs[3] = 12;
		Comparator cmp = new ArgSetComparator(compArgs);
		
		
		File outFile = new File(workspace + "temp_file.txt");
		BasicDiskTable temp = new BasicDiskTable(new File("temp"));
		//temp.doOneIterationOfMerges(cmp, outFile, 3);
		temp.doOneIterationOfMerges(cmp, outFile, 5);
		//System.exit(1);
		
		
		File f = new File(workspace + "__temp__6_0");
		BasicDiskTable dt = new BasicDiskTable(f);
		dt.checkIfSorted(cmp);
		System.exit(1);
		
		
		
		
		File infile = new File(workspace + "object_file_0.txt");
		File outfile = new File(workspace + "temp_file.txt");
		BasicDiskTable in = new BasicDiskTable(infile);
		BasicDiskTable out = new BasicDiskTable(outfile);
		
		//int [] sortArgs = { 4, 2, 0 };
		//ArgSetComparator comp = new ArgSetComparator(sortArgs);
		SingleArgumentComparator comp = new SingleArgumentComparator(4, false);
		
		
		in.maxMemData = 2000000;
		long timeStart1 = System.currentTimeMillis();
		in.sort(comp, outfile);
		long timeEnd1 = System.currentTimeMillis();
		out.checkIfSorted(comp);
		
		in.maxMemData = 1000000;
		long timeStart2 = System.currentTimeMillis();
		in.oldSort(comp, outfile);
		long timeEnd2 = System.currentTimeMillis();
		out.checkIfSorted(comp);
		
		System.out.println("new sort:  " + timeStart1 + " -- " + timeEnd1);
		System.out.println("elapsed sort time:  " + ((timeEnd1-timeStart1)/1000.0) + " seconds");
		System.out.println("old sort:  " + timeStart2 + " -- " + timeEnd2);
		System.out.println("elapsed sort time:  " + ((timeEnd2-timeStart2)/1000.0) + " seconds");
		
		//File indexDir = new File(args[0]);
		//File firstTupleFile = new File(args[1]);
		//File sortedTupleFile1 = new File(args[2]);
		//File sortedTupleFile2 = new File(args[3]);
		
		/*
		Data tuples = Tuple.readTupleDataFromIndexDirectory(indexDir);
		BasicDiskTable origTuplesDT = new BasicDiskTable(firstTupleFile);
		origTuplesDT.openForWriting();
		
		int i=0;
		for(Iterator it = tuples.iterator(); it.hasNext(); ) {
			Tuple t = (Tuple) it.next();
			origTuplesDT.println(t.getDataArray());
			
			i++;
			if(i%100000 == 0) {
				System.out.println(""+ i + " tuples written");
			}
		}
		origTuplesDT.flush();
		origTuplesDT.closeForWriting();
		*/
		//BasicDiskTable origTuplesDT = new BasicDiskTable(firstTupleFile);
		
		//System.out.println("Sorting for arg 1");
		//origTuplesDT.sort(1, true, sortedTupleFile1);
		//System.out.println("Sorting for arg 2");
		//origTuplesDT.sort(2, true, sortedTupleFile2);
	}
	
}
