import java.util.LinkedList;

public class Driver {
	final static int SIM_STEP = 3;
	final static int MEM_SIZE = 30;
	final static int AVERAGE_REQ_SIZE = 2;
	final static int REQ_STD = 2;

	public static void main(String[] args) {
		LinkedList<Integer> allocatedList = new LinkedList<>();

		Gaussian gaussGen = new Gaussian();
		gaussGen.setSeed(System.nanoTime());

		MMM manager = new MMM(MMM.SearchStrategy.FF);
		manager.init(MEM_SIZE);

		int reqSize, allocatedBlock, releasedBlock, p;
		for (int i = 0; i < SIM_STEP; i++) {
			do {
				reqSize = (int) gaussGen.gaussian(AVERAGE_REQ_SIZE, REQ_STD);
				allocatedBlock = manager.request(reqSize);
				if (allocatedBlock == -1)
					break;

				allocatedList.add(allocatedBlock);
			} while (true);

			releasedBlock = (int) gaussGen.rnd() * allocatedList.size();
			p = allocatedList.remove(releasedBlock);
			manager.release(p);
		}
	}
}
