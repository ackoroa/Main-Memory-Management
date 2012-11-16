public class MainMemoryManager {
	AbstractMemoryManager MM;

	public enum SearchStrategy {
		FF, BF, WF
	}

	public MainMemoryManager(SearchStrategy ss) {
		switch (ss) {
		case FF:
			MM = new FFManager();
			break;
		case BF:
			MM = new BFManager();
			break;
		case WF:
			MM = new WFManager();
			break;
		}
	}

	// initialize character array mm to be a single hole
	public int init(int mem_size) {
		return MM.init(mem_size);
	}

	// analogous to function malloc()
	// request a block of n consecutive words
	// return index of first usable word (not tag) or error if insufficient
	// memory
	public int request(int n) {
		int index = MM.request(n + 3); // add tags to request size
		if (index < 0)
			return index;
		return index + 1; // offset tag
	}

	// analogous to function free()
	// releases a previously requested block back to mm
	public boolean release(int p) {
		return MM.release(p - 1); // offset tag
	}

	public double getMemUtil() {
		return MM.getMemoryUtil();
	}

	public double getLastSearchTime() {
		return (double) MM.getSearchTime();
	}

	public String toString() {
		return MM.toString();
	}
}
