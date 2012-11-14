public class FFManager extends MemoryManager {
	public int request(int n) {
		int p = holeHead;
		
		if(n<4) return -1;

		do {
			if (isHole(p) && fits(n, p)) {
				allocate(n, p, perfectFit(n, p));
				return p;
			} else {
				p = M[p + 1];
			}
		} while (p != -1);

		return -1;
	}

	private boolean perfectFit(int n, int p) {
		return n == Math.abs(M[p]);
	}

	private boolean fits(int n, int p) {
		boolean fit = (n <= Math.abs(M[p]) - 4) || (n == Math.abs(M[p]));
		return fit;
	}

	private boolean isHole(int p) {
		return M[p] < 0;
	}

	private void allocate(int requestSize, int currentBlock, boolean perfectFit) {
		int initHoleSize = Math.abs(M[currentBlock]);
		int newHoleSize = initHoleSize - requestSize;
		
		int newBlock = currentBlock + requestSize;
		int nextBlock = M[currentBlock+1];
		int prevBlock = M[currentBlock+2];

		if (!perfectFit) {
			// make new hole
			M[newBlock] = -newHoleSize; // beginningTag
			M[newBlock + newHoleSize - 1] = -newHoleSize; // endTag
			M[newBlock + 1] = nextBlock; // next pointer
			M[newBlock + 2] = prevBlock; // prev pointer

			// update head and tail
			if (M[newBlock + 1] == -1)
				holeTail = newBlock;
			if (M[newBlock + 2] == -1)
				holeHead = newBlock;
		} else {
			if ((nextBlock != -1) && (prevBlock != -1)) {
				M[prevBlock + 1] = nextBlock;
				M[nextBlock + 2] = prevBlock;
			}
			else if((nextBlock == -1) && (prevBlock != -1)){
				M[prevBlock + 1] = -1;
				holeTail = prevBlock;
			}
			else if((nextBlock != -1) && (prevBlock == -1)){
				M[nextBlock + 2] = -1;
				holeHead = nextBlock;
			}
			else{
				holeHead = -1;
				holeTail = -1;
			}
		}

		// allocate block to process
		M[currentBlock] = requestSize; // beginning tag;
		M[currentBlock + requestSize - 1] = requestSize; // end tag;
	}

	public int release(int p) {
		// TODO Auto-generated method stub
		return 0;
	}

}
