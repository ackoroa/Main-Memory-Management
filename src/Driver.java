import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class Driver {
	private static final int SIM_STEP = 100000;
	private static final int MEM_SIZE = 1000000;

	private static final double A_START = 0.05 * MEM_SIZE;
	private static final double A_LIM = 0.3 * MEM_SIZE;
	private static final double A_STEP = 0.05 * MEM_SIZE;

	private static final double D_START = 0.1 * MEM_SIZE;
	private static final double D_LIM = 0.5 * MEM_SIZE;
	private static final double D_STEP = D_START;

	static ArrayList<Double> memUtil;
	static ArrayList<Double> searchTime;
	static double avgMemUtil;
	static double avgSearchTime;

	public static void main(String[] args) {
		double a, d;
		
		File f = new File("results.txt");
		if(f.exists()){
			f.delete();
		}

		// First-Fit
		for (d = D_START; d <= D_LIM; d += D_STEP) {
			for (a = A_START; a <= A_LIM; a += A_STEP) {
				initExp();
				run(MMM.SearchStrategy.FF, a, d);
				printResults("FirstFit", a, d, f);
			}
		}

		// Best-Fit
		for (d = D_START; d <= D_LIM; d += D_STEP) {
			for (a = A_START; a <= A_LIM; a += A_STEP) {
				initExp();
				run(MMM.SearchStrategy.BF, a, d);
				printResults("BestFit", a, d,f);
			}
		}

		// Worst-Fit
		for (d = D_START; d <= D_LIM; d += D_STEP) {
			for (a = A_START; a <= A_LIM; a += A_STEP) {
				initExp();
				run(MMM.SearchStrategy.WF, a, d);
				printResults("WorstFit", a, d,f);
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
			BufferedWriter out = new BufferedWriter(new FileWriter(f,true));

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

	private static void run(MMM.SearchStrategy s, double a, double d) {
		LinkedList<Integer> allocatedList = new LinkedList<>();

		Gaussian gaussGen = new Gaussian();
		gaussGen.setSeed(System.nanoTime());

		MMM manager = new MMM(s);
		manager.init(MEM_SIZE);

		int reqSize, allocatedBlock, releasedBlock, p;
		for (int i = 0; i < SIM_STEP; i++) {
			// System.out.println("Step " + i);
			do {
				reqSize = 0;
				while (reqSize <= 0 || reqSize > MEM_SIZE) {
					reqSize = (int) Math.abs(gaussGen.gaussian(a, d));
				}

				// System.out.print("Step: " + i + " reqSize: " + reqSize);

				allocatedBlock = manager.request(reqSize);
				searchTime.add(manager.getLastSearchTime());

				if (allocatedBlock == -1)
					break;

				// System.out.println(" mem: " + manager.getMemUtil());
				// System.out.println(manager);

				allocatedList.add(allocatedBlock);
			} while (true);
			// System.out.println();
			memUtil.add(manager.getMemUtil());

			releasedBlock = -1;
			while (releasedBlock < 0 || releasedBlock >= allocatedList.size()) {
				releasedBlock = (int) (gaussGen.rnd() * allocatedList.size());
			}
			p = allocatedList.remove(releasedBlock);
			manager.release(p);

			// System.out.println("Step: " + i + " releasePos: " + p);
			// System.out.println(manager);

		}
	}
}
