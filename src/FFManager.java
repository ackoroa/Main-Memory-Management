public class FFManager extends AbstractMemoryManager {
	public int request(int n) {
		int p = holeHead;
		
		holeExamined = 0;
		
		if ((n < 4) || (p < 0))
			return -1;

		do {
			holeExamined++;
			if (isHole(p) && fits(n, p)) {
				// hole found, allocate
				allocate(n, p, perfectFit(n, p));
				return p;
			} else {
				p = M[p + 1]; // go to next hole in list
			}
		} while (p != -1); // until end of list

		return -1;
	}

}
