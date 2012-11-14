import java.util.LinkedList;


public class Driver {
	static int simStep = 10;
	
	public static void main(String[] args) {
		LinkedList<Integer> allocatedList = new LinkedList<>();
		
		MMM manager = new MMM(MMM.SearchStrategy.FF);
		
		int allocatedBlock;
		for(int i=0;i<simStep;i++){
			while((allocatedBlock = manager.mm_request(1)) != -1){
				allocatedList.add(allocatedBlock);
			}
			
		}
	}
}
