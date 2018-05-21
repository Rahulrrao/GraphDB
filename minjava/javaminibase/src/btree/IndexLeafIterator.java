package btree;

import java.io.IOException;

import bufmgr.HashEntryNotFoundException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageNotReadException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import global.NID;
import global.SystemDefs;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Tuple;
import index.IndexException;
import iterator.Iterator;
import iterator.JoinsException;
import iterator.LowMemException;
import iterator.PredEvalException;
import iterator.SortException;
import iterator.TupleUtilsException;
import iterator.UnknowAttrType;
import iterator.UnknownKeyTypeException;
import nodeheap.Node;

public class IndexLeafIterator extends Iterator{
	BTFileScan sc;
	public IndexLeafIterator(BTFileScan sc){
		this.sc = sc;
	}
	@Override
	public Tuple get_next() throws IOException, JoinsException, IndexException, InvalidTupleSizeException,
			InvalidTypeException, PageNotReadException, TupleUtilsException, PredEvalException, SortException,
			LowMemException, UnknowAttrType, UnknownKeyTypeException, Exception {
		KeyDataEntry entry = sc.get_next();
		if(entry == null) return null;
		LeafData leafData = (LeafData) entry.data;
		NID nid = new NID();
		nid.copyRid(leafData.getData());
		// print node
		try {
			Node startNode = SystemDefs.JavabaseDB.nodeHeapfile.getNode(nid);
			return startNode;
		} catch (Exception e) {
			System.err.println("" + e);
		}
		
		return null;
	}
	@Override
	public void close() throws IOException, JoinsException, SortException, IndexException {
		try {
			sc.DestroyBTreeFileScan();
		} catch (InvalidFrameNumberException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReplacerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PageUnpinnedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HashEntryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
}
