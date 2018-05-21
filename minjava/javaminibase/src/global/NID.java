package global;

/*  File NID.java   */
/**
 * class NID
 */

public class NID extends RID{

	/**
	 * default constructor of class
	 */
	public NID() {
	}

	/**
	 * constructor of class
	 */
	public NID(PageId pageno, int slotno) {
		super(pageno, slotno);
	}

	/**
	 * make a copy of the given nid
	 */
	public void copyNid(NID nid) {
		pageNo = nid.pageNo;
		slotNo = nid.slotNo;
	}
	
	public String toString(){
		return "pageno:" + this.pageNo + ", slotno:"+this.slotNo;
	}
}

