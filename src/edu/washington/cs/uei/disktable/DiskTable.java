package edu.washington.cs.uei.disktable;

import java.io.File;

public interface DiskTable {
	public String separator();
	public File getFile();
	public File getLogFile();
	public void setLogFile(File logFile);
	public void open();
	public void close();
	public String [] readLine();
	public void openForWriting();
	public void openForWriting(boolean append);
	public void closeForWriting();
	public void flush();
	public void println(String []tuple);
	public void sort(int pos, boolean ascending, File output);
	public void sort(Comparator comp, File output);
}
