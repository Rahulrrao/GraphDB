package diskmgr;
public class PCounter {
	public static int rcounter;
	public static int wcounter;
	public static int prcounter;
	public static int pwcounter;
	public static void initialize() {
	rcounter =0;
	wcounter =0;
	prcounter =0;
	pwcounter =0;
	}
	public static void readIncrement() {
		rcounter++;
	}
	public static void writeIncrement() {
		wcounter++;
	}
	
	public static void preadIncrement() {
		prcounter++;
	}
	public static void pwriteIncrement() {
		pwcounter++;
	}
}