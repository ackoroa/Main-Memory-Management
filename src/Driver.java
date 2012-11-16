import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.LinkedList;

public class Driver {
	private static final boolean LOG = false;

	private static final int SIM_STEP = 100000;
	private static final int MEM_SIZE = 1000000;

	// List of average request size values to be used
	private static final double[] A_LIST = { 100, 500, 1000, 5000, 10000,
			50000, 100000, 150000, 200000, 250000, 300000 };

	// parameters for the values of the standard deviation
	private static final double D_START = 0.1 * MEM_SIZE;
	private static final double D_LIM = 0.5 * MEM_SIZE;
	private static final double D_STEP = D_START;

	static ArrayList<Double> memUtil;
	static ArrayList<Double> searchTime;
	static double avgMemUtil;
	static double avgSearchTime;

	public static void main(String[] args) {
		double d;

		File f = new File("results.txt");
		if (f.exists()) {
			f.delete();
		}

		// First-Fit
		for (d = D_START; d <= D_LIM; d += D_STEP) {
			for (int i = 0; i < A_LIST.length; i++) {
				initExp();
				run(MainMemoryManager.SearchStrategy.FF, A_LIST[i], d);
				printResults("FirstFit", A_LIST[i], d, f);
			}
		}

		// Best-Fit
		for (d = D_START; d <= D_LIM; d += D_STEP) {
			for (int i = 0; i < A_LIST.length; i++) {
				initExp();
				run(MainMemoryManager.SearchStrategy.BF, A_LIST[i], d);
				printResults("BestFit", A_LIST[i], d, f);
			}
		}

		// Worst-Fit
		for (d = D_START; d <= D_LIM; d += D_STEP) {
			for (int i = 0; i < A_LIST.length; i++) {
				initExp();
				run(MainMemoryManager.SearchStrategy.WF, A_LIST[i], d);
				printResults("WorstFit", A_LIST[i], d, f);
			}
		}
	}

	private static void printResults(String s, double a, double d, File f) {
		int i;

		avgMemUtil = 0;
		for (i = 0; i < memUtil.size(); i++) {
			avgMemUtil += memUtil.get(i);
		}
		avgMemUtil /= memUtil.size();

		avgSearchTime = 0;
		for (i = 0; i < searchTime.size(); i++) {
			avgSearchTime += searchTime.get(i);
		}
		avgSearchTime /= searchTime.size();

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(f, true));

			out.write(s + " " + a + " " + d + " " + avgMemUtil + " "
					+ avgSearchTime + "\r\n");
			out.close();

			System.out.println(s + " " + a + " " + d + " " + avgMemUtil + " "
					+ avgSearchTime);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void initExp() {
		memUtil = new ArrayList<Double>();
		searchTime = new ArrayList<Double>();
	}

	private static void run(MainMemoryManager.SearchStrategy s, double a,
			double d) {
		if (LOG) {
			try {
				System.setErr(new PrintStream(new File("err.txt")));
			} catch (FileNotFoundException e) {
				System.out.println("Cannot create err.txt");
			}
		}

		// list of allocated memory
		LinkedList<Integer> allocatedList = new LinkedList<>();

		Gaussian gaussGen = new Gaussian();
		gaussGen.setSeed(System.nanoTime());

		MainMemoryManager manager = new MainMemoryManager(s);
		manager.init(MEM_SIZE);

		int reqSize, allocatedBlock, releasedBlock, p;
		
		// do SIM_STEP iteration of the process
		for (int i = 0; i < SIM_STEP; i++) {
			// fill up memory until request cannot be satisfied
			do {
				// generate request size
				reqSize = 0;
				while (reqSize <= 0 || reqSize > MEM_SIZE) {
					reqSize = (int) Math.abs(gaussGen.gaussian(a, d));
				}

				if (LOG)
					System.err.print("Step: " + i + " reqSize: " + reqSize);

				// request memory and record position of allocated memory
				allocatedBlock = manager.request(reqSize);

				// record search time for this request
				searchTime.add(manager.getLastSearchTime());

				// if request cannot be satisfied exit loop
				if (allocatedBlock == -1)
					break;
				
				// add allocated memory to list
				allocatedList.add(allocatedBlock);
				
				if (LOG) {
					System.err.println(" mem: " + manager.getMemUtil());
					System.err.println(manager);
				}
			} while (true);
			
			if (LOG)
				System.err.println();

			// record memory utilization for this iteration
			memUtil.add(manager.getMemUtil());

			// generate index of the block to be released in the list
			releasedBlock = -1;
			while (releasedBlock < 0 || releasedBlock >= allocatedList.size()) {
				releasedBlock = (int) (gaussGen.rnd() * allocatedList.size());
			}

			// get position of block to be released
			p = allocatedList.remove(releasedBlock);

			if (LOG)
				System.err.println("Step: " + i + " releasePos: " + p);

			// release block
			manager.release(p);

			if (LOG)
				System.err.println(manager);

		}
	}
}
