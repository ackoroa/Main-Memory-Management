import java.util.Arrays;

public abstract class MemoryManager {
	int M[];
	int memSize;
	int holeHead, holeTail;
	int searchPointer;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		int p = 0;

		while (p < memSize) {
			sb.append("Size = " + M[p]);
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

	abstract int request(int n);

	abstract int release(int p);
}
