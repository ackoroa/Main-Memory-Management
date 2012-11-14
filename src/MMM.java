public class MMM {
	MemoryManager MM;

	public enum SearchStrategy {
		FF, NF, BF, WF
	}

	public MMM(SearchStrategy ss) {
		switch (ss) {
		case FF:
			MM = new FFManager();
			break;
		}
	}

	// initialize character array mm to be a single hole
	public int mm_init(int mem_size) {
		return MM.init(mem_size);
	}

	// analogous to function malloc()
	// request a block of n consecutive words
	// return index of first usable word (not tag) or error if insufficient
	// memory
	public int mm_request(int n) {
		int index = MM.request(n+4);
		if(index<0) return index;
		return index + 3;
	}

	// analogous to function free()
	// releases a previously requested block back to mm
	public boolean mm_release(int p) {
		return MM.release(p-3);
	}

	public String toString() {
		return MM.toString();
	}
}
