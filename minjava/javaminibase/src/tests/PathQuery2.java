package tests;
// Task 4,7

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import diskmgr.PCounter;

import edgeheap.Edge;
import zIndex.DescriptorKey;
import zIndex.ZTreeFile;

import btree.BTFileScan;
import btree.BTreeFile;
import btree.ConstructPageException;
import btree.IteratorException;
import btree.KeyDataEntry;
import btree.KeyNotMatchException;
import btree.LeafData;
import btree.PinPageException;
import btree.ScanIteratorException;
import btree.StringKey;
import btree.UnpinPageException;
import bufmgr.HashEntryNotFoundException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageNotReadException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import global.AttrType;
import global.Descriptor;
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
import iterator.Sort;
import iterator.UnknowAttrType;
import iterator.UnknownKeyTypeException;
import nodeheap.Node;

public class PathQuery2 {
	private String [] edge_path;
	private int no_of_edges;
	List<String> descLabel = new ArrayList<String>();

	PathQuery2(String path) throws FieldNumberOutOfBoundException, 
		nodeheap.InvalidSlotNumberException, nodeheap.HFException, 
		nodeheap.HFDiskMgrException, nodeheap.HFBufMgrException, Exception {
		no_of_edges = path.length() - path.replace("/", "").length();
		edge_path = new String[no_of_edges + 1];
		edge_path = path.split("/");
		BTFileScan iscan;
		NID nid = null;
		// getting the label using the descriptor
		if (edge_path[0].startsWith("D")) {
			String[] node_desc = new String[5];
			String descriptor = edge_path[0].substring(1).trim();
			node_desc = descriptor.split(",");
			ZTreeFile node_index = SystemDefs.JavabaseDB.nodeDescriptorIndexFile;
			Descriptor node_key = new Descriptor();
			node_key.set(Integer.parseInt(node_desc[0]),Integer.parseInt(node_desc[1]),
					Integer.parseInt(node_desc[2]),Integer.parseInt(node_desc[3]), 
					Integer.parseInt(node_desc[4]));
			iscan = node_index.new_scan(new DescriptorKey(node_key), new DescriptorKey(node_key));
			KeyDataEntry entry = iscan.get_next();
			while(entry != null) {
				// Get NID
				LeafData leafData = (LeafData) entry.data;
				nid = new NID();
				nid.copyRid(leafData.getData());
				descLabel.add(SystemDefs.JavabaseDB.nodeHeapfile.getNode(nid).getLabel());
				entry = iscan.get_next();
			}
			iscan.DestroyBTreeFileScan();
		}else if(edge_path[0].startsWith("L")){
			String label = edge_path[0].substring(1).trim();
			iscan = SystemDefs.JavabaseDB.nodeLabelIndexFile.new_scan(new StringKey(label), new StringKey(label));
			if (iscan != null) {
				KeyDataEntry entry = iscan.get_next();
				// Collect node data
				LeafData leafData = (LeafData) entry.data;
				NID nidlabel = new NID();
				nidlabel.copyRid(leafData.getData());
				// print node
				descLabel.add(SystemDefs.JavabaseDB.nodeHeapfile.getNode(nidlabel).getLabel());		
				iscan.DestroyBTreeFileScan();
			}
		}
	}
	
	private Edge getNextindexFilterSource(BTFileScan iscan, String edgeLabel) throws 
		edgeheap.InvalidSlotNumberException, edgeheap.InvalidTupleSizeException, 
		edgeheap.HFException, edgeheap.HFDiskMgrException, edgeheap.HFBufMgrException, Exception{
		KeyDataEntry keyData = iscan.get_next();
		if (keyData == null)
			return null;
		LeafData edgeLeaf =  (LeafData)keyData.data;
		EID edgeId = new EID();
		edgeId.copyRid(edgeLeaf.getData());
		Edge e = SystemDefs.JavabaseDB.edgeHeapfile.getEdge(edgeId);
		if (edgeLabel == null )
			return e;

		if (edgeLabel.equals(e.getLabel())){
			return e;
		} else {
			return getNextindexFilterSource(iscan, edgeLabel);
		}
	}
	
	private Edge getNextindexFilterWeight(BTFileScan iscan, String edgeWeight) throws 
		edgeheap.InvalidSlotNumberException, edgeheap.InvalidTupleSizeException, 
		edgeheap.HFException, edgeheap.HFDiskMgrException, edgeheap.HFBufMgrException, Exception{
		KeyDataEntry keyData = iscan.get_next();
		if (keyData == null)
			return null;
		LeafData edgeLeaf =  (LeafData)keyData.data;
		EID edgeId = new EID();
		edgeId.copyRid(edgeLeaf.getData());
		Edge e = SystemDefs.JavabaseDB.edgeHeapfile.getEdge(edgeId);
		if (edgeWeight == null )
			return e;

		if (e.getWeight() <= Integer.parseInt(edgeWeight)){
			
			return e;
		} else {
			return getNextindexFilterWeight(iscan, edgeWeight);
		}
	}

	public void sortLabels(String nodelabelheapfile, String sortedResFile) 
			throws JoinsException, IndexException, InvalidTupleSizeException, InvalidTypeException, 
			PageNotReadException, PredEvalException, LowMemException, UnknowAttrType, UnknownKeyTypeException, Exception{
		Heapfile sortedresfile = new Heapfile(sortedResFile);	
		Iterator resSort;
		AttrType[] attrs = new AttrType[3];
		attrs[0] = new AttrType(AttrType.attrString);
		attrs[1] = new AttrType(AttrType.attrString);
		attrs[2] = new AttrType(AttrType.attrString);
		
		short[] str_sizes = new short[3];
		str_sizes[0] = (short)44;
		str_sizes[1] = (short)44;
		str_sizes[2] = (short)44;
		
		Tuple t = new Tuple();
		t.setHdr((short)3, attrs, str_sizes);
		FldSpec[] projlist = new FldSpec[3];
		projlist[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		projlist[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);
		projlist[2] = new FldSpec(new RelSpec(RelSpec.outer), 3);
		
		TupleOrder order = new TupleOrder(TupleOrder.Ascending);
		FileScan sorted = new FileScan(nodelabelheapfile, attrs, str_sizes, (short) 3, 3, projlist, null);
		System.out.println("Sort operation");
		resSort = new Sort(attrs, (short) 3, str_sizes, sorted, 3, order, 44, 12 , -1, null);
		
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
		AttrType[] attrs = new AttrType[3];
		attrs[0] = new AttrType(AttrType.attrString);
		attrs[1] = new AttrType(AttrType.attrString);
		attrs[2] = new AttrType(AttrType.attrString);
		
		short[] str_sizes = new short[3];
		
		str_sizes[0] = (short)44;
		str_sizes[1] = (short)44;
		str_sizes[2] = (short)44;
		
		Tuple t = new Tuple();
		t.setHdr((short)3, attrs, str_sizes);
		FldSpec[] projlist = new FldSpec[3];
		projlist[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		projlist[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);
		projlist[2] = new FldSpec(new RelSpec(RelSpec.outer), 3);
		
		
		TupleOrder order = new TupleOrder(TupleOrder.Ascending);
		
		FileScan sorted = new FileScan(nodelabelheapfile, attrs, str_sizes, (short) 3, 3, projlist, null);
		
		System.out.println("Sort operation");
		resSort = new Sort(attrs, (short) 3, str_sizes, sorted, 3, order, 44, 12 , -1, null);
		
		System.out.println("Distinct operation");
		dupeli = new DuplElim(attrs, (short) 3, str_sizes, resSort, 12, true, -1, null);
		
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
	AttrType[] attrs = new AttrType[3];
	attrs[0] = new AttrType(AttrType.attrString);
	attrs[1] = new AttrType(AttrType.attrString);
	attrs[2] = new AttrType(AttrType.attrString);
	
	
	short[] str_sizes = new short[3];
	str_sizes[0] = (short)44;
	str_sizes[1] = (short)44;
	str_sizes[2] = (short)44;
	
	
	Heapfile hf = new Heapfile(heapfilename);
	Scan fscan = new Scan(hf);
	RID rid = new RID();
	Tuple t = fscan.getNext(rid);
    while(t != null){
    	count++;
		t.setHdr((short)3, attrs, str_sizes);
        System.out.println(t.getStrFld(1) + " " + t.getStrFld(2));
        t = fscan.getNext(rid);
    }
    System.out.println("Total count = "+ count);
    fscan.closescan();
}
	/*
	 * input parameters
	 * outer destination_label
	 * inner source_label
	 */
	public void NestedLoopJoin(String sourceNodeLabel, int edgeLabelIndex, String outhf, String firstLabel) throws edgeheap.InvalidSlotNumberException, 
		edgeheap.InvalidTupleSizeException, edgeheap.HFException, edgeheap.HFDiskMgrException, 
		edgeheap.HFBufMgrException, Exception {
		
		Heapfile hf = new Heapfile(outhf);
		BTreeFile sourceNodeIndexFile = SystemDefs.JavabaseDB.edgeSourceIndexFile;
		BTFileScan iscan = sourceNodeIndexFile.new_scan(new StringKey(sourceNodeLabel),
				new StringKey(sourceNodeLabel));
		Edge e = new Edge();
		if (edge_path[edgeLabelIndex].startsWith("L")){
			String label = edge_path[edgeLabelIndex].substring(1).trim();
			//System.out.println("Selection based on label");
			e = getNextindexFilterSource(iscan, label);
		}else if (edge_path[edgeLabelIndex].startsWith("W")){
			String weight = edge_path[edgeLabelIndex].substring(1).trim();
			//System.out.println("Selection based on weight");
			e = getNextindexFilterWeight(iscan, weight);
		}
		
		while (e != null) {
			
			// if all the joins are performed print the tail
			if (edgeLabelIndex == no_of_edges) {
				SystemDefs.JavabaseDB.resetPageCounter();
				System.out.println("Index Nested Loop Join operation");
				
				AttrType[] attrs = new AttrType[3];
				attrs[0] = new AttrType(AttrType.attrString);
				attrs[1] = new AttrType(AttrType.attrString);
				attrs[2] = new AttrType(AttrType.attrString);
				
				short[] str_sizes = new short[3];
				
				Tuple t = new Tuple();
				
				str_sizes[0] = (short)44;
				str_sizes[1] = (short)44;
				str_sizes[2] = (short)44;
				
				t.setHdr((short)3, attrs, str_sizes);
				
				//System.out.println("!!!!!!!");
				String destnode = SystemDefs.JavabaseDB.nodeHeapfile.getNode(e.getDestination()).getStrFld(1);
				t.setStrFld(1, firstLabel);
				t.setStrFld(2, destnode);
				t.setStrFld(3, firstLabel+destnode);
				hf.insertRecord(t.getTupleByteArray());
				System.out.println("No of pages read: " + PCounter.rcounter + "\nNo of pages written: " + 
						PCounter.wcounter);
				//e.print();				
			} else {
				String sourceLabel = SystemDefs.JavabaseDB.nodeHeapfile.getNode(
						e.getDestination()).getLabel();//e's destination which will be source to inner guy
				//edgeLabelIndex++;
				//System.out.println("Nested loop join ");
				NestedLoopJoin(sourceLabel, edgeLabelIndex+1, outhf, firstLabel);
			}
			if (edge_path[edgeLabelIndex].startsWith("L")){
				String label = edge_path[edgeLabelIndex].substring(1).trim();
				//System.out.println("Selection based on label");
				e = getNextindexFilterSource(iscan, label);
			}else if (edge_path[edgeLabelIndex].startsWith("W")){
				String weight = edge_path[edgeLabelIndex].substring(1).trim();
				//System.out.println("Selection based on weight");
				e = getNextindexFilterWeight(iscan, weight);
			}
		}
		iscan.DestroyBTreeFileScan();
	}
	
	public void joinOperation(String query) throws edgeheap.InvalidSlotNumberException, 
		edgeheap.InvalidTupleSizeException,	edgeheap.HFException, 
		edgeheap.HFDiskMgrException, edgeheap.HFBufMgrException, Exception {
		String outhf = "outputheapfile";
		String resSorthf = "sortheapfile";
		String resDistincthf = "distinctheapfile";
		
		System.out.println("Selections based on label or weight");
		
		if(edge_path[0].startsWith("L")){
			NestedLoopJoin(descLabel.get(0), 1, outhf, descLabel.get(0));
			
		}else if(edge_path[0].startsWith("D")){
			for(int i = 0 ; i< descLabel.size();i++){
				NestedLoopJoin(descLabel.get(i), 1, outhf, descLabel.get(i));
			}
		}
		
		
		SystemDefs.JavabaseDB.resetPageCounter();
		if(query.equals("a")){
			System.out.println("------------------");
			System.out.println("------- Task 7a: PQ2 - Insertion order -------");
			printTuplesInRelation(outhf);
		}
		
		if(query.equals("b")){
			System.out.println("------------------");
			System.out.println("------- Task 7b: PQ2 - Sorted order -------");
			sortLabels(outhf, resSorthf);
			printTuplesInRelation(resSorthf);
		}
		
		if(query.equals("c")){
			System.out.println("------------------");
			System.out.println("------- Task 7c: PQ2 - Distinct nodes -------");
			distinctLabels(outhf, resDistincthf);
			printTuplesInRelation(resDistincthf);
		}
		//System.out.println("No of pages read: " + PCounter.rcounter + "\nNo of pages written: " + PCounter.wcounter);
		cleanup(outhf, resSorthf, resDistincthf);
	}
	
	public void cleanup(String nodeheapfile, String sortedResFile, String distinctheapfile)
			throws HFException, HFBufMgrException, HFDiskMgrException, IOException, InvalidSlotNumberException, FileAlreadyDeletedException, InvalidTupleSizeException{
		Heapfile nhf = new Heapfile(nodeheapfile);
		Heapfile srf = new Heapfile(sortedResFile);
		Heapfile drf = new Heapfile(distinctheapfile);
		
		nhf.deleteFile();
		srf.deleteFile();
		drf.deleteFile();
	}
}
