package iterator;

import java.io.IOException;

import global.AttrOperator;
import global.AttrType;
import global.RID;
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

public class SmjEdge {
	private Heapfile joinHeapfile;

	public SmjEdge() throws UnknowAttrType, LowMemException, JoinsException, Exception {
	}

	private void Query1_CondExpr(CondExpr[] expr) {
		expr[0].next = null;
		expr[0].op = new AttrOperator(AttrOperator.aopEQ);
		expr[0].type1 = new AttrType(AttrType.attrSymbol);
		expr[0].type2 = new AttrType(AttrType.attrSymbol);
		expr[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.outer), 4);
		expr[0].operand2.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
		expr[1] = null;
	}
	
	private AttrType[] setAttrs(int joinRelationsType) {
		AttrType[] attrs = null;
		int numOfAttrs = (joinRelationsType == 0 ? 6 : 12);
		attrs = new AttrType[numOfAttrs];
		switch(joinRelationsType) {
		case 0:
			attrs[0] = new AttrType(AttrType.attrString);
			attrs[1] = new AttrType(AttrType.attrInteger); //source pg no.
			attrs[2] = new AttrType(AttrType.attrInteger);//source slot no.
			attrs[3] = new AttrType(AttrType.attrInteger);//dest pg no.
			attrs[4] = new AttrType(AttrType.attrInteger);//dest slot no.
			attrs[5] = new AttrType(AttrType.attrInteger);
			break;
		case 1:
			attrs[0] = new AttrType(AttrType.attrString);
			attrs[1] = new AttrType(AttrType.attrInteger); //source pg no.
			attrs[2] = new AttrType(AttrType.attrInteger);//source slot no.
			attrs[3] = new AttrType(AttrType.attrInteger);//dest pg no.
			attrs[4] = new AttrType(AttrType.attrInteger);//dest slot no.
			attrs[5] = new AttrType(AttrType.attrInteger);
			attrs[6] = new AttrType(AttrType.attrString);
			attrs[7] = new AttrType(AttrType.attrInteger); //source pg no.
			attrs[8] = new AttrType(AttrType.attrInteger);//source slot no.
			attrs[9] = new AttrType(AttrType.attrInteger);//dest pg no.
			attrs[10] = new AttrType(AttrType.attrInteger);//dest slot no.
			attrs[11] = new AttrType(AttrType.attrInteger);
			break;
		}
		
		return attrs;
	}

	private short[] setAttrSizes(int joinRelationsType) {
		short[] attrSize = null;
		switch(joinRelationsType) {
		case 0:
			attrSize = new short[1];
			attrSize[0] = 44;
			break;
		case 1:
			attrSize = new short[2];
			attrSize[0] = 44;
			attrSize[1] = 44;
			break;
		}
		return attrSize;
	}

	private FldSpec[] setFieldSpecs(int joinRelationsType, boolean is_res) {
		FldSpec[] rel_projList = null;
		RelSpec rel_in = new  RelSpec(RelSpec.innerRel);
		RelSpec rel_type = new RelSpec(RelSpec.outer);
		int fieldSpecSize = 0;
		if (joinRelationsType == 0) {
			if (is_res)
				fieldSpecSize = 12;
			else
				fieldSpecSize = 6;
		}
		else if (joinRelationsType == 1) {
			if (is_res)
				fieldSpecSize = 18;
			else
				fieldSpecSize = 12;
		}
		rel_projList = new FldSpec[fieldSpecSize];
		switch(joinRelationsType) {
		case 0:
			rel_projList[0] = new FldSpec(rel_type, 1);
			rel_projList[1] = new FldSpec(rel_type, 2);
			rel_projList[2] = new FldSpec(rel_type, 3);
			rel_projList[3] = new FldSpec(rel_type, 4);
			rel_projList[4] = new FldSpec(rel_type, 5);
			rel_projList[5] = new FldSpec(rel_type, 6);
			if (is_res) {
				rel_projList[6] = new FldSpec(rel_in, 1);
				rel_projList[7] = new FldSpec(rel_in, 2);
				rel_projList[8] = new FldSpec(rel_in, 3);
				rel_projList[9] = new FldSpec(rel_in, 4);
				rel_projList[10] = new FldSpec(rel_in, 5);
				rel_projList[11] = new FldSpec(rel_in, 6);
			}
			break;
		case 1:
			rel_projList[0] = new FldSpec(rel_type, 1);
			rel_projList[1] = new FldSpec(rel_type, 2);
			rel_projList[2] = new FldSpec(rel_type, 3);
			rel_projList[3] = new FldSpec(rel_type, 4);
			rel_projList[4] = new FldSpec(rel_type, 5);
			rel_projList[5] = new FldSpec(rel_type, 6);
			rel_projList[6] = new FldSpec(rel_type, 7);
			rel_projList[7] = new FldSpec(rel_type, 8);
			rel_projList[8] = new FldSpec(rel_type, 9);
			rel_projList[9] = new FldSpec(rel_type, 10);
			rel_projList[10] = new FldSpec(rel_type, 11);
			rel_projList[11] = new FldSpec(rel_type, 12);
			if (is_res) {
				rel_projList[12] = new FldSpec(rel_in, 1);
				rel_projList[13] = new FldSpec(rel_in, 2);
				rel_projList[14] = new FldSpec(rel_in, 3);
				rel_projList[15] = new FldSpec(rel_in, 4);
				rel_projList[16] = new FldSpec(rel_in, 5);
				rel_projList[17] = new FldSpec(rel_in, 6);
			}
			break;
		}
		return rel_projList;
	}

	private Tuple setJTupleHdr(Tuple t, int joinType) throws InvalidTypeException, InvalidTupleSizeException, IOException {
		int numAttrs = 0;
		int numStr = 0;
		if (joinType == 0) {
			numAttrs = 12;
			numStr = 2;
		} else if (joinType == 1) {
			numAttrs = 18;
			numStr = 3;
		}
		AttrType[] attrs = new AttrType[numAttrs];
        short[] str_sizes = new short[numStr];
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
        str_sizes[0] = (short)44;
        str_sizes[1] = (short)44;
        if (joinType == 1) {
			str_sizes[2] = (short)44;
			attrs[12] = new AttrType(AttrType.attrString);
			attrs[13] = new AttrType(AttrType.attrInteger); //source pg no.
			attrs[14] = new AttrType(AttrType.attrInteger); //source slot no.
			attrs[15] = new AttrType(AttrType.attrInteger); //dest pg no.
			attrs[16] = new AttrType(AttrType.attrInteger); //dest slot no.
			attrs[17] = new AttrType(AttrType.attrInteger);        	
        }
        t.setHdr((short)numAttrs, attrs, str_sizes);
        return t;
	}
	
	public void printJTuple(Tuple tuple, int resNumCols) throws FieldNumberOutOfBoundException, IOException {
		System.out.print("[ ");
		for (int i = 1; i <= resNumCols; i++) {
			if (i == 1 || i == 7 || i == 13)
				System.out.print(tuple.getStrFld(i) + " ");
			else
				System.out.print(tuple.getIntFld(i) + " ");
			if ((i % 6 == 0) && (i != resNumCols))
				System.out.print(" | ");
		}
		System.out.println("]");
	}

	public void printTuplesInRelation(String heapfilename, int joinRelationsType) throws FieldNumberOutOfBoundException, 
		IOException, InvalidTupleSizeException, HFException, HFBufMgrException, 
		HFDiskMgrException, InvalidTypeException {
		Heapfile hf = new Heapfile(heapfilename);
		Scan fscan = new Scan(hf);
		RID rid = new RID();
		Tuple t = fscan.getNext(rid);
		int counter = 0;
		int resNumCols = (joinRelationsType == 0 ? 12 : 18);
		while(t != null){
			try {	
				t = setJTupleHdr(t, joinRelationsType);
				printJTuple(t, resNumCols);
				t = fscan.getNext(rid);
				counter++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    System.out.println("Total count = " + counter);
	    fscan.closescan();
	}

	//
	//JoinTRelationsType = 0 for outer = edge and inner = edge ; out_col1 = 12
	//JoinTRelationsType = 1 for outer = out_col1(12) and inner = edge ; out_col2 = 18
	//
	public void joinOperation(String filename1, String filename2, String res_filename, int joinRelationsType, 
			boolean asc_order) throws UnknowAttrType, LowMemException, JoinsException, Exception {
		System.out.println("Sort-Merge Join on " + filename1 + " and " + filename2 + " => " + res_filename);
		
		AttrType[] attrs = setAttrs(joinRelationsType);
		short[] attrSize = setAttrSizes(joinRelationsType);
		FldSpec[] projlist = setFieldSpecs(joinRelationsType, false);
		FldSpec[] res_projlist = setFieldSpecs(joinRelationsType, true);
		int rnumCols = 0;
		int snumCols = 0;
		int resNumCols = 0;
		int r_col = 0;
		int s_col = 3;
		if (joinRelationsType == 0) {
			rnumCols = 6;
			snumCols = 6;
			resNumCols = 12;
			r_col = 5;
		}
		else {
			rnumCols = 12;
			snumCols = 6;
			resNumCols = 18;
			r_col = 11;
		}
		FileScan r_fscan = new FileScan(filename1, attrs, attrSize, (short) rnumCols, rnumCols, projlist, null);
		FileScan s_fscan  = new FileScan(filename2, attrs, attrSize, (short) snumCols, snumCols, projlist, null);
		TupleOrder order = new TupleOrder((asc_order ? TupleOrder.Ascending : TupleOrder.Descending));
		SortMerge sm = null;
		joinHeapfile = new Heapfile(res_filename);
		
//		CondExpr[] outFilter = new CondExpr[3];
//		outFilter[0] = new CondExpr();
//		outFilter[1] = new CondExpr();
//		outFilter[2] = new CondExpr();
//		Query1_CondExpr(outFilter);
		int numBuf = 0;
		if (joinRelationsType == 0){
			numBuf = 40;
		} else{
			numBuf = 46;
		}
		
		try {
			sm = new SortMerge(
					attrs, rnumCols, attrSize, attrs, snumCols, attrSize, 
					r_col, 4, s_col, 4,
					numBuf, r_fscan, s_fscan, false, false, order,
					null, res_projlist, resNumCols, -1.0, null);
		} catch (Exception e) {
			System.err.println("*** join error in SortMerge constructor ***");
			System.err.println("" + e);
			e.printStackTrace();
		}
		Tuple t = sm.get_next();
		while (t != null) {
			try {
				joinHeapfile.insertRecord(t.getTupleByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
			t = sm.get_next();
		}
		r_fscan.close();
		s_fscan.close();
		sm.close();
	}
	
	public void close() throws InvalidSlotNumberException, FileAlreadyDeletedException, 
		InvalidTupleSizeException, HFBufMgrException, HFDiskMgrException, IOException {
		if (joinHeapfile != null)
			joinHeapfile.deleteFile();
	}
}
