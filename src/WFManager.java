public class WFManager extends MemoryManager {
	int request(int n) {
		int p = holeHead;
		int worstFitIndex = -1;
		int maxDiff = Integer.MIN_VALUE;

		holeExamined = 0;

		if ((n < 2) || (p < 0))
			return -1;

		do {
			holeExamined++;

			if (isHole(p) && fits(n, p)) {
				if ((Math.abs(M[p]) - n) > maxDiff) {
					maxDiff = Math.abs(M[p]) - n;
					worstFitIndex = p;
				}
			}
			p = M[p + 1];

		} while (p != -1);

		if (worstFitIndex != -1)
			allocate(n, worstFitIndex, perfectFit(n, worstFitIndex));

		return worstFitIndex;
	}

}
