package global;

/**
 * Created by revu on 2/17/17.
 */
public class task1TestMain {
	/**
	 * To test the working of Task 1
	 */
	public static void main(String[] args) {
		Descriptor desc1 = new Descriptor();
		desc1.set(0, 1, 2, 3, 4);
		Descriptor desc2 = new Descriptor();
		desc2.set(0, 0, 0, 0, 0);
		System.out.println("distance is " + desc1.distance(desc2));
		System.out.println("equals is " + desc1.equal(desc2));
		desc1.set(0, 0, 0, 0, 0);
		System.out.println("equals is " + desc1.equal(desc2));

	}
}
