import java.util.Arrays;

public abstract class AbstractMemoryManager {
	static final boolean LOG = false;

	int M[]; // main memory
	int memSize;
	int holeHead, holeTail;
	int holeExamined;

	abstract int request(int n);

	public double getMemoryUtil() {
		int p = 0;
		double totalBlockSize = 0;

		// from 0, jump to each block using size
		while (p < memSize) {
			// if aloocated block
			if (M[p] > 0)
				totalBlockSize += M[p] - 2;

			// go to next block
			p = p + Math.abs(M[p]);
		}

		return totalBlockSize / (double) memSize;
	}

	public int getSearchTime() {
		return holeExamined;
	}

	int init(int memSize) {
		this.memSize = memSize;
		M = new int[memSize];
		Arrays.fill(M, -1);

		M[0] = -memSize; // beginning tag
		M[1] = -1; // predecessor pointer
		M[2] = -1; // ancestor pointer
		M[memSize - 1] = -memSize; // end tag
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

	// allocate memory block of reqSize to a process
	protected void allocate(int requestSize, int currentBlock,
			boolean perfectFit) {
		int initHoleSize = Math.abs(M[currentBlock]);
		int newHoleSize = initHoleSize - requestSize;

		int newHole = currentBlock + requestSize;
		int nextHole = M[currentBlock + 1];
		int prevHole = M[currentBlock + 2];

		// if requested memory perfectly fits the hole
		// remove hole from list
		if (!perfectFit) {
			// make new hole
			M[newHole] = -newHoleSize; // beginningTag
			M[newHole + newHoleSize - 1] = -newHoleSize; // endTag
			M[newHole + 1] = nextHole; // next pointer
			M[newHole + 2] = prevHole; // prev pointer

			// update prev's next pointer
			if (prevHole != -1) {
				M[prevHole + 1] = newHole;
			}

			// update next's prev pointer
			if (nextHole != -1) {
				M[nextHole + 2] = newHole;
			}

			if (nextHole == -1) {
				holeTail = newHole;
			}
			if (prevHole == -1) {
				holeHead = newHole;
			}
		}
		// if not perfect fit, make a new hole with the remaining space
		else {
			if ((nextHole != -1) && (prevHole != -1)) {
				M[prevHole + 1] = nextHole;
				M[nextHole + 2] = prevHole;
			} else if ((nextHole == -1) && (prevHole != -1)) {
				M[prevHole + 1] = -1;
				holeTail = prevHole;
			} else if ((nextHole != -1) && (prevHole == -1)) {
				M[nextHole + 2] = -1;
				holeHead = nextHole;
			} else {
				holeHead = -1;
				holeTail = -1;
			}
		}

		// allocate block to process
		M[currentBlock] = requestSize; // beginning tag;
		M[currentBlock + requestSize - 1] = requestSize; // end tag;
	}

	// release allocated memory, making it into a hole
	// and insert to list
	public boolean release(int p) {
		int leftP = p - 1;
		int rightP = p + M[p];

		int leftTag, rightTag;

		// check left block's tag
		if (leftP > 0) {
			leftTag = M[leftP];
			leftP = leftP + leftTag + 1;
		} else {
			leftTag = 1;
		}

		// check right block's tag
		if (rightP < memSize) {
			rightTag = M[rightP];
		} else {
			rightTag = 1;
		}

		// no coalesce
		if ((leftTag > 0) && (rightTag > 0)) {
			noCoalesce(p);
		}
		// right coalesce
		else if ((leftTag > 0) && (rightTag < 0)) {
			rightCoalesce(p, rightP, rightTag);
		}

		// left coalesce
		else if ((leftTag < 0) && (rightTag > 0)) {
			leftCoalesce(p, leftP, leftTag);
		}

		// both coalesce
		else if ((leftTag < 0) && (rightTag < 0)) {
			bothCoalesce(p, leftP, rightP, leftTag);
		}

		return true;
	}

	private void bothCoalesce(int p, int leftP, int rightP, int leftTag) {
		leftCoalesce(p, leftP, leftTag);

		// remove right from linked list
		int rightNextPointer = M[rightP + 1];
		int rightPrevPointer = M[rightP + 2];
		if (rightPrevPointer != -1) {
			M[rightPrevPointer + 1] = rightNextPointer;
			if (rightNextPointer == -1)
				holeTail = rightPrevPointer;
		}
		if (rightNextPointer != -1) {
			M[rightNextPointer + 2] = rightPrevPointer;
			if (rightPrevPointer == -1)
				holeHead = rightNextPointer;
		}

		leftCoalesce(rightP, leftP, M[leftP]);
	}

	private void leftCoalesce(int p, int leftP, int leftTag) {
		int leftHoleSize = -leftTag;
		int newHoleSize = Math.abs(M[p]) + leftHoleSize;

		M[leftP] = -newHoleSize; // beginning tag
		M[leftP + newHoleSize - 1] = -newHoleSize; // end tag
	}

	private void rightCoalesce(int p, int rightP, int rightTag) {
		int rightHoleSize = -rightTag;
		int newHoleSize = M[p] + rightHoleSize;

		M[p] = -newHoleSize; // beginning tag
		M[p + newHoleSize - 1] = -newHoleSize; // end tag

		M[p + 1] = M[rightP + 1]; // next pointer
		M[p + 2] = M[rightP + 2]; // prev pointer

		// update rightHole's prev
		int rightHolePrevPointer = M[rightP + 2];

		if (LOG) {
			System.err.println("p:" + p + " " + M[p] + " " + M[p + 1] + " "
					+ M[p + 2] + " nextp:" + rightP + " " + M[rightP] + " "
					+ M[rightP + 1] + " " + M[rightP + 2]);
			System.err
					.println("rightHolePrevPointer = " + rightHolePrevPointer);
		}

		// update rightHole's prev
		if (rightHolePrevPointer == -1) {
			holeHead = p;
		} else {
			M[rightHolePrevPointer + 1] = p;
		}

		// update rightHole's next
		int rightHoleNextPointer = M[rightP + 1];
		if (rightHoleNextPointer == -1) {
			holeTail = p;
		} else {
			M[rightHoleNextPointer + 2] = p;
		}
	}

	private void noCoalesce(int p) {
		int holeSize = M[p];

		M[p] = -holeSize; // beginning tag
		M[p + holeSize - 1] = -holeSize; // end tag

		M[p + 1] = -1; // next pointer
		M[p + 2] = holeTail; // prev pointer

		// update old tail
		if (holeTail != -1) {
			M[holeTail + 1] = p;
		}
		holeTail = p;

		// update head
		if (holeHead == -1) {
			holeHead = p;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		int p = 0, nH = 0;

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

		p = holeHead;
		while (p != -1) {
			nH++;
			p = M[p + 1];
		}
		sb.append("nH = " + nH + "\n");

		// System.out.println(Arrays.toString(M));
		return sb.toString();
	}

}
