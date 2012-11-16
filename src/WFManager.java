public class WFManager extends AbstractMemoryManager {
	int request(int n) {
		int p = holeHead;
		int worstFitIndex = -1;
		int maxDiff = Integer.MIN_VALUE;

		holeExamined = 0;

		if ((n < 4) || (p < 0))
			return -1;

		do {
			holeExamined++;

			if (isHole(p) && fits(n, p)) {
				if ((Math.abs(M[p]) - n) > maxDiff) {
					maxDiff = Math.abs(M[p]) - n;
					worstFitIndex = p;
				}
			}
			p = M[p + 1]; // go to next hole in list

		} while (p != -1); // until end of list

		// if a hole that fits is found
		if (worstFitIndex != -1)
			allocate(n, worstFitIndex, perfectFit(n, worstFitIndex));

		return worstFitIndex;
	}

}
