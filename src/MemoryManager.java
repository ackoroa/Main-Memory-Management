import java.util.Arrays;

public abstract class MemoryManager {
	int M[];
	int memSize;
	int holeHead, holeTail;
	int searchPointer;

	abstract int request(int n);
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int p = 0;

		sb.append("head = " + holeHead + " tail = " + holeTail + "\n");
		
		while (p < memSize) {
			sb.append("Pos = " + p);
			sb.append(" Size = " + M[p]);
			if (M[p] < 0) {
				sb.append(" next = " + M[p + 1]);
				sb.append(" prev = " + M[p + 2]);
			}
			sb.append("\n");
			p = p + Math.abs(M[p]);
		}

		System.out.println(Arrays.toString(M));
		return sb.toString();
	}

	int init(int memSize) {
		this.memSize = memSize;
		M = new int[memSize];
		Arrays.fill(M, -1);

		M[0] = -memSize; // beginning tag
		M[1] = -1; // predecessor pointer
		M[2] = -1; // ancestor pointer
		M[memSize - 1] = -memSize; // end tag
		searchPointer = 0; // where to start search
		holeHead = 0;
		holeTail = 0;

		return 0;
	}

	protected boolean perfectFit(int n, int p) {
		return n == Math.abs(M[p]);
	}

	protected boolean fits(int n, int p) {
		boolean fit = (n <= Math.abs(M[p]) - 4) || (n == Math.abs(M[p]));
		return fit;
	}

	protected boolean isHole(int p) {
		return M[p] < 0;
	}

	protected void allocate(int requestSize, int currentBlock, boolean perfectFit) {
		int initHoleSize = Math.abs(M[currentBlock]);
		int newHoleSize = initHoleSize - requestSize;

		int newBlock = currentBlock + requestSize;
		int nextBlock = M[currentBlock + 1];
		int prevBlock = M[currentBlock + 2];

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
			} else if ((nextBlock == -1) && (prevBlock != -1)) {
				M[prevBlock + 1] = -1;
				holeTail = prevBlock;
			} else if ((nextBlock != -1) && (prevBlock == -1)) {
				M[nextBlock + 2] = -1;
				holeHead = nextBlock;
			} else {
				holeHead = -1;
				holeTail = -1;
			}
		}

		// allocate block to process
		M[currentBlock] = requestSize; // beginning tag;
		M[currentBlock + requestSize - 1] = requestSize; // end tag;
	}

	public boolean release(int p) {
		int prevP = p - 1;
		int nextP = p + M[p];

		int prevTag, nextTag;
		if (prevP > 0) {
			prevTag = M[prevP];
			prevP = prevP + prevTag + 1;
		} else {
			prevTag = 1;
		}

		if (nextP < memSize) {
			nextTag = M[nextP];
		} else {
			nextTag = 1;
		}

		// no coalesce
		if ((prevTag > 0) && (nextTag > 0)) {
			noCoalesce(p);
		}
		// right coalesce
		else if ((prevTag > 0) && (nextTag < 0)) {
			rightCoalesce(p, nextP, nextTag);
		}

		// left coalesce
		else if ((prevTag < 0) && (nextTag > 0)) {
			leftCoalesce(p, prevP, prevTag);
		}

		// both coalesce
		else if ((prevTag < 0) && (nextTag < 0)) {
			bothCoalesce(p, prevP, nextP, prevTag);
		}

		return true;
	}

	private void bothCoalesce(int p, int prevP, int nextP, int prevTag) {
		leftCoalesce(p, prevP, prevTag);

		// remove right from linked list
		int rightNextPointer = M[nextP + 1];
		int rightPrevPointer = M[nextP + 2];
		if (rightPrevPointer != -1){
			M[rightPrevPointer + 1] = rightNextPointer;
			if(rightNextPointer == -1) holeTail = rightPrevPointer;
		}
		if (rightNextPointer != -1){
			M[rightNextPointer + 2] = rightPrevPointer;
			if(rightPrevPointer == -1) holeHead = rightNextPointer;
		}
		
		leftCoalesce(nextP,prevP,M[prevP]);
	}

	private void leftCoalesce(int p, int prevP, int prevTag) {
		int leftHoleSize = -prevTag;
		int newHoleSize = Math.abs(M[p]) + leftHoleSize;

		M[prevP] = -newHoleSize; // beginning tag
		M[prevP + newHoleSize - 1] = -newHoleSize; // end tag

		// update tail
		if (holeTail == p)
			holeTail = prevP;
	}

	private void rightCoalesce(int p, int nextP, int nextTag) {
		int rightHoleSize = -nextTag;
		int newHoleSize = M[p] + rightHoleSize;

		M[p] = -newHoleSize; // beginning tag
		M[p + newHoleSize - 1] = -newHoleSize; // end tag

		M[p + 1] = M[nextP + 1]; // next pointer
		M[p + 2] = M[nextP + 2]; // prev pointer

		// update rightHole's prev
		int rightHolePrevPointer = M[nextP + 2];
		if (rightHolePrevPointer < 0) {
			holeHead = p;
		} else {
			M[rightHolePrevPointer + 1] = p;
		}
	}
	
	private void noCoalesce(int p) {
		int holeSize = M[p];

		M[p] = -holeSize; // beginning tag
		M[p + holeSize - 1] = -holeSize; // end tag

		M[p + 1] = -1; // next pointer
		M[p + 2] = holeTail; // prev pointer

		// update old tail
		M[holeTail + 1] = p;
		holeTail = p;
	}
}
