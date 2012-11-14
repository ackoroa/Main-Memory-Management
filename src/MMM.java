
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
	int mm_init(int mem_size) {
		return MM.init(mem_size);
	}

	// analogous to function malloc()
	// request a block of n consecutive words
	// return index of first usable word (not tag) or error if insufficient
	// memory
	int mm_request(int n) {
		return MM.request(n);
	}

	// analogous to function free()
	// releases a previously requested block back to mm
	int mm_release(int p) {
		return MM.release(p);
	}
	
	public String toString(){
		return MM.toString();
	}
}
