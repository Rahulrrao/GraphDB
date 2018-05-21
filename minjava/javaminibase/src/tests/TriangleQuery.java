package tests;

import java.io.IOException;

import diskmgr.PCounter;

import bufmgr.PageNotReadException;
import edgeheap.EScan;
import edgeheap.Edge;
import edgeheap.EdgeHeapfile;

import global.AttrType;
import global.EID;
import global.NID;
import global.RID;
import global.SystemDefs;
import global.TupleOrder;
import heap.FieldNumberOutOfBoundException;
import heap.FileAlreadyDeletedException;
import heap.HFBufMgrException;
import heap.HFDiskMgrException;
import heap.HFException;
import heap.Heapfile;
import heap.InvalidSlotNumberException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Scan;
import heap.SpaceNotAvailableException;
import heap.Tuple;
import index.IndexException;
import iterator.DuplElim;
import iterator.FileScan;
import iterator.FldSpec;
import iterator.Iterator;
import iterator.JoinsException;
import iterator.LowMemException;
import iterator.PredEvalException;
import iterator.RelSpec;
import iterator.SmjEdge;
import iterator.Sort;
import iterator.UnknowAttrType;
import iterator.UnknownKeyTypeException;
import nodeheap.Node;


public class TriangleQuery {

	TriangleQuery() {
		
	}

	private Tuple setHdr(Tuple t) throws InvalidTypeException, InvalidTupleSizeException, IOException {
		AttrType[] attrs = new AttrType[6];
        short[] str_sizes = new short[1];
        attrs[0] = new AttrType(AttrType.attrString);
        attrs[1] = new AttrType(AttrType.attrInteger); //source pg no.
        attrs[2] = new AttrType(AttrType.attrInteger); //source slot no.
        attrs[3] = new AttrType(AttrType.attrInteger); //dest pg no.
        attrs[4] = new AttrType(AttrType.attrInteger); //dest slot no.
        attrs[5] = new AttrType(AttrType.attrInteger);
        str_sizes[0] = (short)44;
        t.setHdr((short)6, attrs, str_sizes);
        return t;
	}
	
	private Tuple setTriHdr(Tuple t) throws InvalidTypeException, InvalidTupleSizeException, IOException {
		AttrType[] attrs = new AttrType[18];
        short[] str_sizes = new short[3];
        attrs[0] = new AttrType(AttrType.attrString);
        attrs[1] = new AttrType(AttrType.attrInteger); //source pg no.
        attrs[2] = new AttrType(AttrType.attrInteger); //source slot no.
        attrs[3] = new AttrType(AttrType.attrInteger); //dest pg no.
        attrs[4] = new AttrType(AttrType.attrInteger); //dest slot no.
        attrs[5] = new AttrType(AttrType.attrInteger);
        attrs[6] = new AttrType(AttrType.attrString);
        attrs[7] = new AttrType(AttrType.attrInteger); //source pg no.
        attrs[8] = new AttrType(AttrType.attrInteger); //source slot no.
        attrs[9] = new AttrType(AttrType.attrInteger); //dest pg no.
        attrs[10] = new AttrType(AttrType.attrInteger); //dest slot no.
        attrs[11] = new AttrType(AttrType.attrInteger);
        attrs[12] = new AttrType(AttrType.attrString);
        attrs[13] = new AttrType(AttrType.attrInteger); //source pg no.
        attrs[14] = new AttrType(AttrType.attrInteger); //source slot no.
        attrs[15] = new AttrType(AttrType.attrInteger); //dest pg no.
        attrs[16] = new AttrType(AttrType.attrInteger); //dest slot no.
        attrs[17] = new AttrType(AttrType.attrInteger);
        str_sizes[0] = (short)44;
        str_sizes[1] = (short)44;
        str_sizes[2] = (short)44;
        t.setHdr((short)18, attrs, str_sizes);
        return t;
	}
	
	private void filterTupleLabels(EdgeHeapfile hf,  String label, String outheapfile) throws 
			HFException, HFBufMgrException, HFDiskMgrException, IOException, InvalidTupleSizeException,
			InvalidSlotNumberException, SpaceNotAvailableException,	FieldNumberOutOfBoundException,
			edgeheap.InvalidTupleSizeException, InvalidTypeException {
		System.out.println("Selection on " + hf.get_file_name() + " using label: " + label + " => " + outheapfile);
		
		Heapfile outhf = new Heapfile(outheapfile);
		EScan fscan = new EScan(hf);
		EID eid = new EID();
		Edge edge = fscan.getNext(eid);
        while(edge != null){
            Tuple t = new Tuple(edge.getTupleByteArray(), 0, edge.getLength());
            t = setHdr(t);
            String tupleLabel = t.getStrFld(1); 
            if(tupleLabel.equals(label)){
            	//Add tuples to the new heapfile
                outhf.insertRecord(t.getTupleByteArray());
            }
            edge = fscan.getNext(eid);
        }
        fscan.closescan();
	}
	
	
	private void filterTupleWeights(EdgeHeapfile hf,  int max_weight, String outheapfile) throws 
		HFException, HFBufMgrException, HFDiskMgrException, IOException, InvalidTupleSizeException,
		InvalidSlotNumberException, SpaceNotAvailableException,	FieldNumberOutOfBoundException,
		edgeheap.InvalidTupleSizeException, InvalidTypeException {
		System.out.println("Selection on " + hf.get_file_name() + " using weight: " + max_weight + " => " + outheapfile);
		
		Heapfile outhf = new Heapfile(outheapfile);
		EScan fscan = new EScan(hf);
		EID eid = new EID();
		Edge edge = fscan.getNext(eid);
		while(edge != null){
		    Tuple t = new Tuple(edge.getTupleByteArray(), 0, edge.getWeight());
		    t = setHdr(t);
		    int tupleWeight = t.getIntFld(6); 
		    if(tupleWeight <= max_weight){
		    	//Add tuples to the new heapfile
		        outhf.insertRecord(t.getTupleByteArray());
		    }
		    edge = fscan.getNext(eid);
		}
		fscan.closescan();
	}


	private void filterTupleByNID(String heapfile, String resheapfile, String nhf) throws 
		nodeheap.InvalidSlotNumberException, nodeheap.InvalidTupleSizeException, nodeheap.HFException, 
		nodeheap.HFDiskMgrException, nodeheap.HFBufMgrException, Exception {
		Heapfile hf = new Heapfile(heapfile);
		Heapfile reshf = new Heapfile(resheapfile);
		Scan fscan = new Scan(hf);
		RID rid = new RID();
		Heapfile nheapfile = new Heapfile(nhf);
				
		Tuple tuple = fscan.getNext(rid);
		while(tuple != null){
		    tuple = setTriHdr(tuple);
		    // Checking common NID of 1st and 3rd edge
		    if((tuple.getIntFld(2) == tuple.getIntFld(16))
		    && (tuple.getIntFld(3) == tuple.getIntFld(17))) {	
		    	//Add tuples to the final heapfile
		    	getNodeLabels(tuple, nheapfile);
		    	reshf.insertRecord(tuple.getTupleByteArray());
		    }
		    tuple = fscan.getNext(rid);
		}
		fscan.closescan();
	}
	
	
	public void getNodeLabels(Tuple tuple, Heapfile nhf) 
			throws nodeheap.InvalidSlotNumberException, nodeheap.InvalidTupleSizeException, Exception{
		
		StringBuilder sb = new StringBuilder();
		
		AttrType[] attrs = new AttrType[4];
		attrs[0] = new AttrType(AttrType.attrString);
		attrs[1] = new AttrType(AttrType.attrString);
		attrs[2] = new AttrType(AttrType.attrString);
		attrs[3] = new AttrType(AttrType.attrString);
		short[] str_sizes = new short[4];
		
		Tuple t = new Tuple();
		
		str_sizes[0] = (short)44;
		str_sizes[1] = (short)44;
		str_sizes[2] = (short)44;
		str_sizes[3] = (short)44;
		
		t.setHdr((short)4, attrs, str_sizes);
		
    	NID nid = new NID();
    	Node node = new Node();
    	String[] nodes = new String[4];
    	
    	node.setHdr((short)2, attrs, str_sizes);
    	nid.pageNo.pid = tuple.getIntFld(2);
    	nid.slotNo = tuple.getIntFld(3);
    	nodes[0] = SystemDefs.JavabaseDB.nodeHeapfile.getNode(nid).getStrFld(1);
    	sb.append(nodes[0]);
    	t.setStrFld(1, nodes[0]);
    	
    	nid.pageNo.pid = tuple.getIntFld(8);
    	nid.slotNo = tuple.getIntFld(9);
    	nodes[1] = SystemDefs.JavabaseDB.nodeHeapfile.getNode(nid).getStrFld(1);
    	sb.append(nodes[1]);
    	t.setStrFld(2, nodes[1]);
    	
    	nid.pageNo.pid = tuple.getIntFld(14);
    	nid.slotNo = tuple.getIntFld(15);
    	nodes[2] = SystemDefs.JavabaseDB.nodeHeapfile.getNode(nid).getStrFld(1);
    	sb.append(nodes[2]);
    	t.setStrFld(3, nodes[2]);
    	
    	nodes[3] = sb.toString();
    	t.setStrFld(4, nodes[3]);
    	
    	nhf.insertRecord(t.getTupleByteArray());
	}

	public void sortLabels(String nodelabelheapfile, String sortedResFile) 
			throws JoinsException, IndexException, InvalidTupleSizeException, InvalidTypeException, 
			PageNotReadException, PredEvalException, LowMemException, UnknowAttrType, UnknownKeyTypeException, Exception{
		Heapfile sortedresfile = new Heapfile(sortedResFile);	
		Iterator resSort;
		AttrType[] attrs = new AttrType[4];
		attrs[0] = new AttrType(AttrType.attrString);
		attrs[1] = new AttrType(AttrType.attrString);
		attrs[2] = new AttrType(AttrType.attrString);
		attrs[3] = new AttrType(AttrType.attrString);
		short[] str_sizes = new short[4];
		str_sizes[0] = (short)44;
		str_sizes[1] = (short)44;
		str_sizes[2] = (short)44;
		str_sizes[3] = (short)44;
		
		Tuple t = new Tuple();
		t.setHdr((short)4, attrs, str_sizes);
		FldSpec[] projlist = new FldSpec[4];
		projlist[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		projlist[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);
		projlist[2] = new FldSpec(new RelSpec(RelSpec.outer), 3);
		projlist[3] = new FldSpec(new RelSpec(RelSpec.outer), 4);
		TupleOrder order = new TupleOrder(TupleOrder.Ascending);
		FileScan sorted = new FileScan(nodelabelheapfile, attrs, str_sizes, (short) 4, 4, projlist, null);
		System.out.println("Sort operation");
		resSort = new Sort(attrs, (short) 4, str_sizes, sorted, 4, order, 44, 12 , -1, null);
		
		t = resSort.get_next();
		while (t != null) {
			try {
				sortedresfile.insertRecord(t.getTupleByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
			t = resSort.get_next();
		}
		
		sorted.close();
		resSort.close();
	}
	
	public void distinctLabels(String nodelabelheapfile, String distinctResFile)
			throws JoinsException, IndexException, InvalidTupleSizeException, InvalidTypeException, 
			PageNotReadException, PredEvalException, LowMemException, UnknowAttrType, UnknownKeyTypeException, Exception{
		
		Heapfile distinctresfile = new Heapfile(distinctResFile);
		
		Iterator resSort;
		Iterator dupeli;
		AttrType[] attrs = new AttrType[4];
		attrs[0] = new AttrType(AttrType.attrString);
		attrs[1] = new AttrType(AttrType.attrString);
		attrs[2] = new AttrType(AttrType.attrString);
		attrs[3] = new AttrType(AttrType.attrString);
		short[] str_sizes = new short[4];
		
		str_sizes[0] = (short)44;
		str_sizes[1] = (short)44;
		str_sizes[2] = (short)44;
		str_sizes[3] = (short)44;
		
		Tuple t = new Tuple();
		t.setHdr((short)4, attrs, str_sizes);
		FldSpec[] projlist = new FldSpec[4];
		projlist[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		projlist[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);
		projlist[2] = new FldSpec(new RelSpec(RelSpec.outer), 3);
		projlist[3] = new FldSpec(new RelSpec(RelSpec.outer), 4);
		
		TupleOrder order = new TupleOrder(TupleOrder.Ascending);
		
		FileScan sorted = new FileScan(nodelabelheapfile, attrs, str_sizes, (short) 4, 4, projlist, null);
		
		System.out.println("Sort operation");
		resSort = new Sort(attrs, (short) 4, str_sizes, sorted, 4, order, 44, 12 , -1, null);
		
		System.out.println("Distinct operation");
		dupeli = new DuplElim(attrs, (short) 4, str_sizes, resSort, 12, true, -1, null);
		
		t = dupeli.get_next();
		
		while (t != null) {
			try {
				distinctresfile.insertRecord(t.getTupleByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
			t = dupeli.get_next();
		}
		sorted.close();
		resSort.close();
		dupeli.close();
	}
	
	
	public void printTuplesInRelation(String heapfilename) throws FieldNumberOutOfBoundException, 
		IOException, InvalidTupleSizeException, HFException, HFBufMgrException, 
		HFDiskMgrException, InvalidTypeException{
		
		int count = 0;
		AttrType[] attrs = new AttrType[4];
		attrs[0] = new AttrType(AttrType.attrString);
		attrs[1] = new AttrType(AttrType.attrString);
		attrs[2] = new AttrType(AttrType.attrString);
		attrs[3] = new AttrType(AttrType.attrString);
		
		short[] str_sizes = new short[4];
		str_sizes[0] = (short)44;
		str_sizes[1] = (short)44;
		str_sizes[2] = (short)44;
		str_sizes[3] = (short)44;
		
		Heapfile hf = new Heapfile(heapfilename);
		Scan fscan = new Scan(hf);
		RID rid = new RID();
		Tuple t = fscan.getNext(rid);
        while(t != null){
        	count++;
    		t.setHdr((short)4, attrs, str_sizes);
            System.out.println(t.getStrFld(1) + " " + t.getStrFld(2)+ " "+ t.getStrFld(3));
            t = fscan.getNext(rid);
        }
        System.out.println("Total count = "+ count);
        fscan.closescan();
	}

	public void startTriangleQuery(String[] args, String[] values, String query_type) 
			throws UnknowAttrType, LowMemException, JoinsException, Exception{
		EdgeHeapfile hf = SystemDefs.JavabaseDB.edgeHeapfile;
		int joinOperationType = 0;		
		String nodeheapfile = "nodeheapfile1";
		String sortedResFile = "sortedResFile";
		String distinctResFile = "distinctResFile";

		//From the edge relation filter label1 from R relation and label2 from S relation
		String rheapfile = "edges_filter1";
		String sheapfile = "edges_filter2";
		System.out.println("-------- Query Plan -----");
		SystemDefs.JavabaseDB.resetPageCounter();
		if(args[0].equals("w")){
			filterTupleWeights(hf, Integer.parseInt(values[0]), rheapfile);
		}else if(args[0].equals("l")){
			filterTupleLabels(hf, values[0], rheapfile);
		}
		System.out.println("No of pages read: " + PCounter.rcounter + "\nNo of pages written: " + PCounter.wcounter);
		
		SystemDefs.JavabaseDB.resetPageCounter();
		if(args[1].equals("w")){
			filterTupleWeights(hf, Integer.parseInt(values[1]), sheapfile);
		}else if(args[1].equals("l")){
			filterTupleLabels(hf, values[1], sheapfile);
		}
		System.out.println("No of pages read: " + PCounter.rcounter + "\nNo of pages written: " + PCounter.wcounter);

		String joinheapfile1 = "joinheapfile1";
		SystemDefs.JavabaseDB.resetPageCounter();
		SmjEdge smj1 = new SmjEdge();
		smj1.joinOperation(rheapfile, sheapfile, joinheapfile1, joinOperationType, true);
		System.out.println("No of pages read: " + PCounter.rcounter + "\nNo of pages written: " + PCounter.wcounter);
//		smj1.printTuplesInRelation(joinheapfile1, 0);

		//Pass the already joined heapfile and the file filtered on label3 as input to smj
		SystemDefs.JavabaseDB.resetPageCounter();
		joinOperationType = 1;
		String sheapfile_s = "edges_filter3";
		if(args[2].equals("w")){
			filterTupleWeights(hf, Integer.parseInt(values[2]), sheapfile_s);
		}else if(args[2].equals("l")){
			filterTupleLabels(hf, values[2], sheapfile_s);
		}
		System.out.println("No of pages read: " + PCounter.rcounter + "\nNo of pages written: " + PCounter.wcounter);
		
		SystemDefs.JavabaseDB.resetPageCounter();
		String joinheapfile2 = "joinheapfile2";
		SmjEdge smj2 = new SmjEdge();	
		smj2.joinOperation(joinheapfile1, sheapfile_s, joinheapfile2, joinOperationType, true);
		System.out.println("No of pages read: " + PCounter.rcounter + "\nNo of pages written: " + PCounter.wcounter);
//		smj2.printTuplesInRelation(resFileName, 1);
		
		//Filter by checking NID of 3rd edge and 1st edge
		SystemDefs.JavabaseDB.resetPageCounter();
		String resFileName = "resultTriangels";
		System.out.println("Selection based on NID and Projection of node labels");
		filterTupleByNID(joinheapfile2, resFileName, nodeheapfile);
		System.out.println("No of pages read: " + PCounter.rcounter + "\nNo of pages written: " + PCounter.wcounter);
		
		SystemDefs.JavabaseDB.resetPageCounter();
		if(query_type.equals("a")){
			System.out.println("------------------");
			System.out.println("------- Task 9: TQa - Insertion order -------");
			printTuplesInRelation(nodeheapfile);
		}

		if(query_type.equals("b")){
			 sortLabels(nodeheapfile, sortedResFile);
			 System.out.println("------------------");
			 System.out.println("------ Task 9: TQb - Sorted order -------");
			 printTuplesInRelation(sortedResFile);
		}
		
		if(query_type.equals("c")){
			distinctLabels(nodeheapfile, distinctResFile);
			System.out.println("------------------");
			System.out.println("------ Task 9: TQc - Distinct nodes -------");
			printTuplesInRelation(distinctResFile);
		}
		System.out.println("No of pages read: " + PCounter.rcounter + "\nNo of pages written: " + PCounter.wcounter);
		
		smj1.close();
		smj2.close();
		cleanup(nodeheapfile, sortedResFile, distinctResFile, resFileName, joinheapfile1, joinheapfile2, rheapfile, sheapfile_s, sheapfile);
	}
	
	public void cleanup(String nodeheapfile, String sortedResFile, String distinctResFile, String resFileName, String joinheapfile1, String joinheapfile2, String rheapfile, String sheapfile_s, String sheapfile) 
			throws HFException, HFBufMgrException, HFDiskMgrException, IOException, InvalidSlotNumberException, FileAlreadyDeletedException, InvalidTupleSizeException{
		Heapfile nhf = new Heapfile(nodeheapfile);
		Heapfile srf = new Heapfile(sortedResFile);
		Heapfile drf = new Heapfile(distinctResFile);
		Heapfile rfn = new Heapfile(resFileName);
		Heapfile join1 = new Heapfile(joinheapfile1);
		Heapfile join2 = new Heapfile(joinheapfile2);
		Heapfile rfilter = new Heapfile(rheapfile);
		Heapfile sfilter = new Heapfile(sheapfile);
		Heapfile ssfilter = new Heapfile(sheapfile_s);
		
		nhf.deleteFile();
		srf.deleteFile();
		drf.deleteFile();
		rfn.deleteFile();
		join1.deleteFile();
		join2.deleteFile();
		rfilter.deleteFile();
		sfilter.deleteFile();
		ssfilter.deleteFile();
	}
}
