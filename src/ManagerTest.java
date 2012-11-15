public class ManagerTest {

	public static void main(String[] args) {
		MMM manager = new MMM(MMM.SearchStrategy.FF);
		manager.init(20);

		System.out.println(manager.request(18));
		
		System.out.println(manager);
		
		manager.release(1);
		
		System.out.println(manager);
	}

}
