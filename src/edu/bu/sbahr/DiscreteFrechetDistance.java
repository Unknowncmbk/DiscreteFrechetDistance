// This class will compute the Discrete Frechet Distance given two sets of time
// series by using dynamic programming to increase performance.
//
// How to run: Run the file, and the console will ask for the first and second
// time series which should be supplied as a String. Each time series is given
// as pairs of values separated by semicolons and the values for each pair are
// separated by a comma.
//
// Pseudocode of computing DFD from page 5 of
// http://www.kr.tuwien.ac.at/staff/eiter/et-archive/cdtr9464.pdf
//
// @author - Stephen Bahr (sbahr@bu.edu)

package edu.bu.sbahr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DiscreteFrechetDistance {

	/** Scanner instance */
	private Scanner console = new Scanner(System.in);
	/** Dimensions of the time series */
	private static int DIM;
	/** Dynamic programming memory array */
	private static double[][] mem;
	/** First time series */
	private static List<Point> timeSeriesP;
	/** Second time series */
	private static List<Point> timeSeriesQ;

	public static void main(String[] args) {
		Scanner console = new Scanner(System.in);
		System.out.println("--------------------");
		System.out.println("Discrete Frechet Distance Calculator");
		System.out.println("--------------------\n");

		System.out.println("Please enter the first time series: ");
		String xSeries = console.nextLine();
		System.out.println("Please enter the second time series: ");
		String ySeries = console.nextLine();

		if (!xSeries.equalsIgnoreCase("") && !ySeries.equalsIgnoreCase("")) {
			long startTime = System.currentTimeMillis();

			timeSeriesP = parseInput(xSeries);
			timeSeriesQ = parseInput(ySeries);

			double res = computeDiscreteFrechet(timeSeriesP, timeSeriesQ);

			// how long it took to process
			long runTime = System.currentTimeMillis() - startTime;
			System.out.println("The Discrete Frechet Distance between these two time series is " + res + ". (" + runTime + " ms)");

		}
		else {
			System.out.println("ERROR: Invalid format! Please enter time series data like the following:");
			System.out.println("64,25;42,55;37,21;34,76;77,2;98,0;9,20;20,10;12,27;32,61;88,49;60,90;99,37;85,53;7,87;67,33;20,62;4,88");
			System.out.println("76,92;71,59;65,73;29,52;19,13;81,6;89,36;76,10;38,93;60,44;5,26;58,84;46,16;0,55;56,71");

			System.out.println("\nEach sequence is given as pairs of values separated by semicolons and the values for each pair are separated by a comma.");
		}
	}

	/**
	 * Wrapper that makes a call to computeDFD. Initializes mem array with all
	 * -1 values.
	 * 
	 * @param P - the first time series
	 * @param Q - the second time series
	 * 
	 * @return The length of the shortest distance that can traverse both time
	 *         series.
	 */
	private static double computeDiscreteFrechet(List<Point> P, List<Point> Q) {

		mem = new double[P.size()][Q.size()];

		// initialize all values to -1
		for (int i = 0; i < mem.length; i++) {
			for (int j = 0; j < mem[i].length; j++) {
				mem[i][j] = -1.0;
			}
		}

		return computeDFD(P.size() - 1, Q.size() - 1);
	}

	/**
	 * Compute the Discrete Frechet Distance (DFD) given the index locations of
	 * i and j. In this case, the bottom right hand corner of the mem two-d
	 * array. This method uses dynamic programming to improve performance.
	 * 
	 * Pseudocode of computing DFD from page 5 of
	 * http://www.kr.tuwien.ac.at/staff/eiter/et-archive/cdtr9464.pdf
	 * 
	 * @param i - the row
	 * @param j - the column
	 * 
	 * @return The length of the shortest distance that can traverse both time
	 *         series.
	 */
	private static double computeDFD(int i, int j) {

		// if the value has already been solved
		if (mem[i][j] > -1)
			return mem[i][j];
		// if top left column, just compute the distance
		else if (i == 0 && j == 0)
			mem[i][j] = euclideanDistance(timeSeriesP.get(i), timeSeriesQ.get(j));
		// can either be the actual distance or distance pulled from above
		else if (i > 0 && j == 0)
			mem[i][j] = max(computeDFD(i - 1, 0), euclideanDistance(timeSeriesP.get(i), timeSeriesQ.get(j)));
		// can either be the distance pulled from the left or the actual
		// distance
		else if (i == 0 && j > 0)
			mem[i][j] = max(computeDFD(0, j - 1), euclideanDistance(timeSeriesP.get(i), timeSeriesQ.get(j)));
		// can be the actual distance, or distance from above or from the left
		else if (i > 0 && j > 0) {
			mem[i][j] = max(min(computeDFD(i - 1, j), computeDFD(i - 1, j - 1), computeDFD(i, j - 1)), euclideanDistance(timeSeriesP.get(i), timeSeriesQ.get(j)));
		}
		// infinite
		else
			mem[i][j] = Integer.MAX_VALUE;

		// printMemory();
		// return the DFD
		return mem[i][j];
	}

	/**
	 * Get the max value of all the values.
	 * 
	 * @param values - the values being compared
	 * 
	 * @return The max value of all the values.
	 */
	private static double max(double... values) {
		double max = Integer.MIN_VALUE;
		for (double i : values) {
			if (i >= max)
				max = i;
		}
		return max;
	}

	/**
	 * Get the minimum value of all the values.
	 * 
	 * @param values - the values being compared
	 * 
	 * @return The minimum value of all the values.
	 */
	private static double min(double... values) {
		double min = Integer.MAX_VALUE;
		for (double i : values) {
			if (i <= min)
				min = i;
		}
		return min;
	}

	/**
	 * Given two points, calculate the Euclidean distance between them, where
	 * the Euclidean distance: sum from 1 to n dimensions of ((x - y)^2)^1/2
	 * 
	 * @param i - the first point
	 * @param j - the second point
	 * 
	 * @return The total Euclidean distance between two points.
	 */
	private static double euclideanDistance(Point i, Point j) {

		double distance = 0;
		// for each dimension
		for (int n = 0; n < DIM; n++) {
			distance += Math.sqrt(Math.pow(Math.abs((i.dimensions[n] - j.dimensions[n])), 2));
		}

		return distance;
	}

	/**
	 * Parses console input in order to construct a list of points.
	 * 
	 * @param input - the string input from the console
	 * @return A list of Points that can be evaluated into a polygonal curve.
	 */
	private static List<Point> parseInput(String input) {
		List<Point> points = new ArrayList<Point>();

		// split the input string up by semi-colon
		String[] tuples = input.split(";");
		if (tuples != null && tuples.length > 0) {

			// for each tuple pair
			for (String tup : tuples) {
				// get the dimension of each
				String[] dims = tup.split(",");

				// if valid split
				if (dims != null && dims.length > 0) {

					// construct new array of dims.length dimensions
					int[] dimensions = new int[dims.length];

					// set the global dimensional value
					if (DIM != dims.length)
						DIM = dims.length;

					// for each dimension
					for (int i = 0; i < dims.length; i++) {
						int val = Integer.parseInt(dims[i]);
						dimensions[i] = val;
					}

					Point p = new Point(dimensions);
					// add the point to list of points
					points.add(p);
					Arrays.toString(p.dimensions);
				}
			}
		}

		return points;
	}

	/**
	 * Test method that prints the 2D dynamic programming array.
	 */
	private static void printMemory() {
		System.out.println("\n\n");
		for (int row = 0; row < mem.length; row++) {
			for (int col = 0; col < mem[row].length; col++) {
				System.out.print(mem[row][col] + "\t");
			}
			System.out.println();
		}
	}
}
