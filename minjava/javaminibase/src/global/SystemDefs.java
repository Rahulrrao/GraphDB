package global;

import bufmgr.*;
import diskmgr.*;
import catalog.*;

public class SystemDefs {
	public static BufMgr JavabaseBM;
	public static GraphDB JavabaseDB;
	public static Catalog JavabaseCatalog;

	public static String JavabaseDBName;
	public static String JavabaseLogName;
	public static boolean MINIBASE_RESTART_FLAG = false;
	public static String MINIBASE_DBNAME;

	/**
	 * Default constructor
	 */
	public SystemDefs() {
	};

	/** Constructor for the class SystemDefs
	 * @param dbname - name of the database
	 * @param num_pgs - number of pages
	 * @param bufpoolsize - buffer pool size
	 * @param replacement_policy - the replacement algorithm used
	 */
	public SystemDefs(String dbname, int num_pgs, int bufpoolsize, String replacement_policy) {
		int logsize;

		String real_logname = new String(dbname);
		String real_dbname = new String(dbname);

		if (num_pgs == 0) {
			logsize = 500;
		} else {
			logsize = 3 * num_pgs;
		}

		if (replacement_policy == null) {
			replacement_policy = new String("Clock");
		}

		init(real_dbname, real_logname, num_pgs, logsize, bufpoolsize, replacement_policy);
	}

	/** Initialize method
	 * This method initializes the Buffer Manager and the Database
	 * @param dbname - Database name
	 * @param logname
	 * @param num_pgs - Number of pages
	 * @param maxlogsize 
	 * @param bufpoolsize - Buffer pool size
	 * @param replacement_policy - Replacement algorithm
	 */
	public void init(String dbname, String logname, int num_pgs, int maxlogsize, int bufpoolsize,
			String replacement_policy) {

		boolean status = true;
		JavabaseBM = null;
		JavabaseDB = null;
		JavabaseDBName = null;
		JavabaseLogName = null;
		JavabaseCatalog = null;

		try {
			JavabaseBM = new BufMgr(bufpoolsize, replacement_policy);
			JavabaseDB = new GraphDB(0);
			/*
			 * JavabaseCatalog = new Catalog();
			 */
		} catch (Exception e) {
			System.err.println("" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

		JavabaseDBName = new String(dbname);
		JavabaseLogName = new String(logname);
		MINIBASE_DBNAME = new String(JavabaseDBName);

		// create or open the DB

		if ((MINIBASE_RESTART_FLAG) || (num_pgs == 0)) {
			// open an existing database
			try {
				JavabaseDB.openDB(dbname);
			} catch (Exception e) {
				System.err.println("" + e);
				e.printStackTrace();
				Runtime.getRuntime().exit(1);
			}
		} else {
			try {
				JavabaseDB.openDB(dbname, num_pgs);
			} catch (Exception e) {
				System.err.println("" + e);
				e.printStackTrace();
				Runtime.getRuntime().exit(1);
			}
		}
	}
}