public class FFManager extends MemoryManager {
	public int request(int n) {
		int p = holeHead;
		
		holeExamined = 0;
		
		if ((n < 2) || (p < 0))
			return -1;

		do {
			holeExamined++;
			if (isHole(p) && fits(n, p)) {
				allocate(n, p, perfectFit(n, p));
				return p;
			} else {
				p = M[p + 1];
			}
		} while (p != -1);

		return -1;
	}

}
