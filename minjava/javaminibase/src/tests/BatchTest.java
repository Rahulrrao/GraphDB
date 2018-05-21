package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

import bufmgr.BufMgrException;
import bufmgr.HashEntryNotFoundException;
import bufmgr.HashOperationException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageNotFoundException;
import bufmgr.PagePinnedException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import diskmgr.GraphDB;
import diskmgr.PCounter;
import global.GlobalConst;
import global.SystemDefs;
import nodeheap.NodeHeapfile;

class BatchDriver implements GlobalConst {
	protected String dbpath = "data.minibase-db";
	public void exitClean() throws PageUnpinnedException, InvalidFrameNumberException, HashEntryNotFoundException, ReplacerException, HashOperationException, PagePinnedException, PageNotFoundException, BufMgrException, IOException{
		//close index files
		SystemDefs.JavabaseDB.nodeDescriptorIndexFile.close();
		SystemDefs.JavabaseDB.nodeLabelIndexFile.close();
		SystemDefs.JavabaseDB.edgeLabelIndexFile.close();
		SystemDefs.JavabaseDB.edgeWeightIndexFile.close();
		SystemDefs.JavabaseDB.edgeSourceIndexFile.close();
		SystemDefs.JavabaseDB.edgeDestinationIndexFile.close();
		
		SystemDefs.JavabaseBM.flushAllPages();
		
		SystemDefs.JavabaseDB.closeDB();
		
	}
	
	public void init(){
		System.out.println("\n" + "Running " + " tests...." + "\n");
		File f = new File(dbpath);
		if(f.exists()) { 
			SystemDefs.MINIBASE_RESTART_FLAG = true;
		}
		new SystemDefs(dbpath, 500000, 30000, "Clock");		
	}
	
	public void clearFiles(){
		System.out.println("\n" + "Clearing db files.\n");
		Random random = new Random();
		String logpath = "BTREE" + random.nextInt() + ".minibase-log";

		// Kill anything that might be hanging around
		String newdbpath;
		String newlogpath;
		String remove_logcmd;
		String remove_dbcmd;
		String remove_cmd = "/bin/rm -rf ";

		newdbpath = dbpath;
		newlogpath = logpath;

		remove_logcmd = remove_cmd + logpath;
		remove_dbcmd = remove_cmd + dbpath;

		// Commands here is very machine dependent. We assume
		// user are on UNIX system here
		try {
			Runtime.getRuntime().exec(remove_logcmd);
			Runtime.getRuntime().exec(remove_dbcmd);
		} catch (IOException e) {
			System.err.println("IO error: " + e);
		}

		remove_logcmd = remove_cmd + newlogpath;
		remove_dbcmd = remove_cmd + newdbpath;

		// This step seems redundant for me. But it's in the original
		// C++ code. So I am keeping it as of now, just in case I
		// I missed something
		try {
			Runtime.getRuntime().exec(remove_logcmd);
			Runtime.getRuntime().exec(remove_dbcmd);
		} catch (IOException e) {
			System.err.println("IO error: " + e);
		}

		// Clean up again
		try {
			Runtime.getRuntime().exec(remove_logcmd);
			Runtime.getRuntime().exec(remove_dbcmd);
		} catch (IOException e) {
			System.err.println("IO error: " + e);
		}

		System.out.print("\n" + "..." + " Finished ");
		System.out.println(".\n\n");
	}

	public void menu() {
		System.out.println("-------------------------- MENU ------------------");
		System.out.println("\n\n[0] Batch Node Insert");
		System.out.println("[1] Batch Node Delete");
		System.out.println("[2] Batch Edge Insert");
		System.out.println("[3] Batch Edge Delete\n");
		System.out.println("[4] Simple Node Query");
		System.out.println("[5] Simple Edge Query\n");
		System.out.println("[6] Task 6: Path Query");
		System.out.println("[7] Task 7: Path Query 2");
		System.out.println("[8] Task 9: Triangle Query");
		System.out.println("\n[9] Quit");
	}

	public void runAllTests(int choice) throws Exception {
		Scanner in = new Scanner(System.in);
		switch (choice) {
		case 0:
			System.out.println("Nodefile name: ");
			String filename = in.nextLine();
			System.out.println("Graphdb name: ");
			dbpath = in.nextLine();
			GraphDB db = SystemDefs.JavabaseDB;// new GraphDB(0);
			NodeHeapfile nhf = SystemDefs.JavabaseDB.nodeHeapfile;

			BatchNodeInsert batchNodeInsert = new BatchNodeInsert();

			for (String line : Files.readAllLines(Paths.get(filename), StandardCharsets.US_ASCII)) {
				batchNodeInsert.doSingleBatchNodeInsert(line, nhf, db);

			}
			System.out.println("Node count: " + db.getNodeCnt() + "\nEdge count:" + db.getEdgeCnt());
			break;
		case 1:
			System.out.println("Nodefile name: ");
			filename = in.nextLine();
			System.out.println("Graphdb name: ");
			dbpath = in.nextLine();
			BatchNodeDelete batchNodeDelete = new BatchNodeDelete();
			for (String line : Files.readAllLines(Paths.get(filename), StandardCharsets.US_ASCII)) {
				batchNodeDelete.doSingleBatchNodeDelete(line.trim());
			}
			System.out.println("Node count: " + SystemDefs.JavabaseDB.getNodeCnt() + "\nEdge count:"
					+ SystemDefs.JavabaseDB.getEdgeCnt());
			break;

		case 2:
			System.out.println("Edgefile name: ");
			String edgeFile = in.nextLine();
			System.out.println("Graphdb name: ");
			dbpath = in.nextLine();
			BatchEdgeInsert batchEdgeInsert = new BatchEdgeInsert();
			String[] edgeVals = new String[4];
			int i = 1;
			for (String line : Files.readAllLines(Paths.get(edgeFile), StandardCharsets.US_ASCII)) {
				line = line.trim();
				edgeVals = line.split(" ");
				batchEdgeInsert.doSingleBatchEdgInsert(edgeVals[0], edgeVals[1], edgeVals[2], edgeVals[3]);
				System.out.println("Edges inserted : " + i++);
			}
			System.out.println("Node count: " + SystemDefs.JavabaseDB.getNodeCnt() + "\nEdge count:"
					+ SystemDefs.JavabaseDB.getEdgeCnt());
			break;

		case 3:
			System.out.println("Edgefile name: ");
			edgeFile = in.nextLine();
			System.out.println("Graphdb name: ");
			dbpath = in.nextLine();
			BatchEdgeDelete batchEdgeDelete = new BatchEdgeDelete();
			String[] edgeValsDel = new String[4];
			int k = 0;
			for (String line : Files.readAllLines(Paths.get(edgeFile), StandardCharsets.US_ASCII)) {
				line = line.trim();
				System.out.println("deleting edge " + k++);
				edgeValsDel = line.split(" ");
				batchEdgeDelete.doSingleBatchEdgeDelete(edgeValsDel[0], edgeValsDel[1], edgeValsDel[2]);
			}
			System.out.println("Node count: " + SystemDefs.JavabaseDB.getNodeCnt() + "\nEdge count:"
					+ SystemDefs.JavabaseDB.getEdgeCnt());
			break;
			
		case 4:
			System.out.println("Enter Graphdb name: ");
			dbpath = in.nextLine();
			System.out.println("Enter Numbuf: ");
			in.nextInt();
			System.out.println("Enter Query Type:");
			int qtype = in.nextInt();
			System.out.println("With(1) or Without index(0):");
			int index = in.nextInt();
			String[] args = new String[2];
			switch(qtype) {
			case 0: break;
			case 1: break;
			case 2: System.out.println("Enter Descriptor as a csv val: ");
					args[0] = in.next();
					break;
			case 3: System.out.println("Enter Descriptor as a csv val: ");
					args[0] = in.next();
					System.out.println("Enter Distance: ");
					args[1] = in.next();
					break;
			case 4:System.out.println("Enter Label: ");
				   args[0] = in.next();
				   break;
			case 5:System.out.println("Enter Descriptor as a csv val: ");
				   args[0] = in.next();
			       System.out.println("Enter Distance: ");
			       args[1] = in.next();
			       break;
			default:
			}
		    NodeQuery nq = new NodeQuery();
			nq.evaluate(qtype, index, args);
			break;
			
		case 5:
			System.out.println("Enter Graphdb name: ");
			dbpath = in.nextLine();
			System.out.println("Enter Numbuf: ");
			in.nextInt();
			System.out.println("Enter Query Type:");
			qtype = in.nextInt();
			System.out.println("With(1) or Without index(0):");
			index = in.nextInt();
			args = new String[2];
			switch(qtype) {
			case 0: break;
			case 1: break;
			case 2: break;
			case 3:	break;
			case 4: break;
			case 5: System.out.println("Enter lower edge weight:");
					args[0] = in.next();
					System.out.println("Enter upper edge weight:");
					args[1] = in.next();
					break;
			default:
			}
		    EdgeQuery eq = new EdgeQuery();
		    eq.evaluate(qtype, index, args);
			break;

		case 6:
			System.out.println("Enter Graphdb name: ");
			dbpath = in.nextLine();
			//System.out.println("Enter Query Type:");
			//qtype = in.nextInt();
			//System.out.println("With(1) or Without index(0):");
			System.out.println("Enter query path: ");
			String arg = in.nextLine();
		    //PathQuery pq = new PathQuery("L0/L248/L384/L514");
			//D7,1,44,22,12/L248/L384/D41,34,28,23,41/D41,22,9,32,18/L996
			System.out.println("Enter choice a or b or c: ");
			String choice1 = in.nextLine();
			PathQuery pq = new PathQuery(arg);
		    pq.evaluate(choice1);
			break;

//		case 7:
//			System.out.println("Enter Graphdb name: ");
//			dbpath = in.nextLine();
//			JoinTestExtended jte = new JoinTestExtended();
//			jte.doTheJoin();
//			break;

		case 7:
			System.out.println("Enter Graphdb name: ");
			dbpath = in.nextLine();
			 System.out.println("Enter query type: a or b or c");
			String query = in.nextLine();
			System.out.println("Enter query path: ");
			// L1/L1/L2/L3
			String path = in.nextLine();
			PathQuery2 p = new PathQuery2(path);
			p.joinOperation(query);
			break;

		case 8:
			System.out.println("Enter Graphdb name: ");
			dbpath = in.nextLine();
			System.out.println("Enter query type: a or b or c");
			String query_type = in.nextLine();
			String[] values = new String[3];
			args = new String[3];
			try {
				System.out.println("Enter 3 values for the type of parameters; (l) for label || (w) for max weight in Semi-colon separated format");
				args[0] = in.nextLine();
				args = args[0].split(";");
				args[0] = args[0].trim();
				args[1] = args[1].trim();
				args[2] = args[2].trim();
				System.out.println("Enter 3 values for the corresponding types of parameters in Semi-colon separated format");
				values[0] = in.nextLine();
				values = values[0].split(";");
				values[0] = values[0].trim();
				values[1] = values[1].trim();
				values[2] = values[2].trim();
			}
			catch (Exception e) {
				System.out.println("Invalid input");
				break;
			}
			TriangleQuery tq = new TriangleQuery();
//			args[0] = args[1] = args[2] = "l";
//			values[0] = values[1] = values[2] = "50";
//			values[0] = "1";values[1] = "2";values[2] = "3";

			tq.startTriangleQuery(args, values, query_type);

//			args[0] = args[1] = args[2] = "w";
//			values[0] = values[1] = values[2] = "50";
//			values[0] = "1";values[1] = "2";values[2] = "3";
//			tq.startTriangleQuery(args, values, query_type);
			break;

		default:System.out.println("Invalid input");
		}

		if (choice < 6)
			System.out.println("No of pages read: " + PCounter.rcounter + "\nNo of pages written: " + PCounter.wcounter);
		//System.out.println("No of pins: " + PCounter.prcounter + "\nNo of unpins: " + PCounter.pwcounter);
	}
}

class GetStuff {
	GetStuff() {
	}

	public static int getChoice() {

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int choice = -1;

		try {
			choice = Integer.parseInt(in.readLine());
		} catch (NumberFormatException e) {
			return -1;
		} catch (IOException e) {
			return -1;
		}

		return choice;
	}
}

public class BatchTest implements GlobalConst {
	public static void main(String[] args) {
		int choice = 0;

		try {
			BatchDriver bttest = new BatchDriver();
			bttest.init();
			bttest.menu();
			while ((choice = GetStuff.getChoice()) != 9) {
				SystemDefs.JavabaseDB.resetPageCounter();
				try {
					bttest.runAllTests(choice);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("       !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					System.out.println("       !!         Something is wrong                    !!");
					System.out.println("       !!     Is your DB full? then exit. rerun it!     !!");
					System.out.println("       !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}
				bttest.menu();
			}
			//choice 9, exit.
			bttest.exitClean();
			System.out.println("Successfully Terminated...");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error encountered during tests:\n");
			Runtime.getRuntime().exit(1);
		}
	}
}
