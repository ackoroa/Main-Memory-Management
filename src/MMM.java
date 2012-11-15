public class MMM {
	MemoryManager MM;
	int nRequest;

	public enum SearchStrategy {
		FF, NF, BF, WF
	}

	public MMM(SearchStrategy ss) {
		nRequest = 0;
		
		switch (ss) {
		case FF:
			MM = new FFManager();
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
		nRequest++;
		
		int index = MM.request(n+2);
		if(index<0) return index;
		return index + 1;
	}

	// analogous to function free()
	// releases a previously requested block back to mm
	public boolean release(int p) {
		return MM.release(p-1);
	}
	
	public double getMemUtil(){
		return MM.getMemoryUtil();
	}
	
	public double getAvgSearchTime(){
		double ans =  (double) MM.getSearchTime() / (double) nRequest;
		nRequest = 0;
		
		return ans;
	}

	public String toString() {
		return MM.toString();
	}
}
