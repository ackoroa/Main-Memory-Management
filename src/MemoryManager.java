public abstract class MemoryManager {
	int M[];
	int searchPointer;
	
	int init(int memSize){
		M = new int[memSize];
		M[0] = -memSize;
		M[1] = 0;
		M[2] = 0;
		M[memSize-1] = -memSize;
		searchPointer = 0;
		
		return 0;
	}

	abstract int request(int n);

	abstract int release(int p);
}
