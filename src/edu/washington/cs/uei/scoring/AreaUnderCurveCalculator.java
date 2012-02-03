package edu.washington.cs.uei.scoring;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

import edu.washington.cs.uei.disktable.BasicDiskTable;

public class AreaUnderCurveCalculator {

	private static final int xCoordinateIndex = 5;
	private static final int yCoordinateIndex = 4;
	
	// assumes 0 < xCoords < 1, 0 < yCoords < 1, and xCoords are sorted in increasing order 
	public double calculateAreaUnderCurve(List<Double> xCoords, List<Double> yCoords)
	{
		if(xCoords==null || yCoords==null) {
			return 0;
		}
		
		double ret = 0;
		
		double prevX, prevY, curX, curY, yMin, yMax, xDiff;
		Iterator<Double> itX = xCoords.iterator();
		Iterator<Double> itY = yCoords.iterator();
		if(itX.hasNext()) {
			prevX = itX.next().doubleValue();
		}
		else {
			return 0;
		}
		if(itY.hasNext()) {
			prevY = itY.next().doubleValue();
		}
		else {
			return 0;
		}
		while(itX.hasNext() && itY.hasNext()) {
			curX = itX.next().doubleValue();
			curY = itY.next().doubleValue();
			yMin  = Math.min(prevY, curY);
			yMax  = Math.max(prevY, curY);
			xDiff = curX - prevX;
			
			ret += yMin * xDiff;
			ret += (yMax-yMin) / 2 * xDiff;
			
			prevX = curX;
			prevY = curY;
		}
		
		return ret;
	}
	
	public void getCoordinates(
			LinkedList<Double> xCoords, LinkedList<Double> yCoords, BasicDiskTable in)
	{
		in.open();
		
		for(String [] line = in.readLine(); line!=null; line = in.readLine()) 
		{
			xCoords.addFirst(Double.parseDouble(line[xCoordinateIndex]));
			yCoords.addFirst(Double.parseDouble(line[yCoordinateIndex]));
		}
		
		in.close();
	}
	
	public static void main(String[] args) {
		String workspace = 
			"C:\\Documents and Settings\\ayates\\My Documents\\textrunner\\corpus\\small_test_hist_sci\\under_1000\\jair_results\\";
		String infile = workspace + "ESP_relation_similarity_prc.txt";
		
		BasicDiskTable in = new BasicDiskTable(new File(infile));
		
		AreaUnderCurveCalculator aucc = new AreaUnderCurveCalculator();
		
		LinkedList<Double> xs = new LinkedList<Double>();
		LinkedList<Double> ys = new LinkedList<Double>();

		aucc.getCoordinates(xs, ys, in);
		System.out.println("Area under the curve is:");
		System.out.println(aucc.calculateAreaUnderCurve(xs, ys));
	}

}
