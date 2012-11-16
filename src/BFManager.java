public class BFManager extends AbstractMemoryManager {
	int request(int n) {
		int p = holeHead;
		int bestFitIndex = -1;
		int minDiff = Integer.MAX_VALUE;

		holeExamined = 0;

		if ((n < 4) || (p < 0))
			return -1;

		do {
			holeExamined++;

			if (isHole(p) && fits(n, p)) {
				if ((Math.abs(M[p]) - n) < minDiff) {
					minDiff = Math.abs(M[p]) - n;
					bestFitIndex = p;
				}
			}
			p = M[p + 1]; // go to next hole in list

		} while (p != -1); // until end of list

		// if a hole that fits is found, allocate
		if (bestFitIndex != -1)
			allocate(n, bestFitIndex, perfectFit(n, bestFitIndex));

		return bestFitIndex;
	}
}
