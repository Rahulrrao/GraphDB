package tests;

import java.io.IOException;

import edgeheap.Edge;

import nodeheap.Node;
import iterator.CondExpr;
import iterator.EdgeScan;
import iterator.FileScan;
import iterator.FldSpec;
import iterator.NestedLoopExtended;
import iterator.NestedLoopExtendedEdge;
import iterator.NestedLoopsJoins;
import iterator.NodeScan;
import iterator.RelSpec;
import iterator.Sort;
import iterator.SortMerge;
import global.AttrOperator;
import global.AttrType;
import global.Descriptor;
import global.GlobalConst;
import global.TupleOrder;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Tuple;

class JoinsDriver implements GlobalConst {

	private boolean OK = true;
	private boolean FAIL = false;
	public boolean runTests() {

		//Disclaimer();
		Query1();
		//Query2();
		//Query3();
		//Query4();
		Query5();

		System.out.print("Finished joins testing" + "\n");

		return true;
	}
	private void Query1() {
		// TODO Auto-generated method stub
		System.out.print("**********************Query1 starting *********************\n");
		boolean status = OK;

		// Sailors, Boats, Reserves Queries.
		System.out.print("Query: Find all the node Descriptors which are source nodes of edges with weight less than or equal to 23");

		System.out.print("\n(Tests NodeScan, Projection, and Nested Loop Join)\n");

		CondExpr[] outFilter = new CondExpr[2];
		outFilter[0] = new CondExpr();
		
		CondExpr[] rightFilter = new CondExpr[2];
		rightFilter[0] = new CondExpr();
		
		Query1_CondExpr(outFilter, rightFilter);
		Tuple t = new Tuple();
		Node n;
		try {
			n = new Node();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		AttrType[] Ntypes = new AttrType[2];
		Ntypes[0] = new AttrType(AttrType.attrString);
		Ntypes[1] = new AttrType(AttrType.attrDesc);
		
		// SOS
		short[] Nsizes = new short[2];
		Nsizes[0] = 44;
		//Nsizes[1] = 20;// first elt. is 30

		FldSpec[] Nprojection = new FldSpec[2];
		Nprojection[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		Nprojection[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);//wrong
		
		CondExpr[] selects = new CondExpr[1];
		selects = null;

		NodeScan am = null;
		try {
			am = new NodeScan("nodeheapfile", Ntypes, Nsizes, (short) 2, (short) 2, Nprojection, null);
		} catch (Exception e) {
			status = FAIL;
			System.err.println("" + e);
		}

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for Node");
			Runtime.getRuntime().exit(1);
		}
		AttrType[] jtype12 = new AttrType[2];
		jtype12[0] = new AttrType(AttrType.attrString);
		jtype12[1] = new AttrType(AttrType.attrDesc);
//		try {
//			while ((n = am.get_next()) != null) {
//				n.print();
//
//				//qcheck1.Check(t);
//			}
//		} catch (Exception e) {
//			System.err.println("" + e);
//			e.printStackTrace();
//			status = FAIL;
//		}

		AttrType[] Etypes = new AttrType[6];
		Etypes[0] = new AttrType(AttrType.attrString);//label
		Etypes[1] = new AttrType(AttrType.attrInteger);//pgidsource
		Etypes[2] = new AttrType(AttrType.attrInteger);//slotidsource
		Etypes[3] = new AttrType(AttrType.attrInteger);//pgiddest
		Etypes[4] = new AttrType(AttrType.attrInteger);//slotiddest
		Etypes[5] = new AttrType(AttrType.attrInteger);//weight

		short[] Esizes = new short[1];
		Esizes[0] = 44;
		FldSpec[] Eprojection = new FldSpec[6];
		Eprojection[0] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		Eprojection[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
		Eprojection[2] = new FldSpec(new RelSpec(RelSpec.innerRel), 3);
		Eprojection[3] = new FldSpec(new RelSpec(RelSpec.innerRel), 4);
		Eprojection[4] = new FldSpec(new RelSpec(RelSpec.innerRel), 5);
		Eprojection[5] = new FldSpec(new RelSpec(RelSpec.innerRel), 6);
		
		AttrType[] JJtype = { new AttrType(AttrType.attrString),new AttrType(AttrType.attrDesc), new AttrType(AttrType.attrString), new AttrType(AttrType.attrInteger)};

		short[] JJsize = new short[4];
		JJsize[0] = 44;
		JJsize[2] = 44;

//		FileScan am2 = null;
//		try {
//			am2 = new FileScan("edgeheapfile", Etypes, Esizes, (short) 6, (short) 6, Eprojection, null);
//		} catch (Exception e) {
//			status = FAIL;
//			System.err.println("" + e);
//		}

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for reserves");
			Runtime.getRuntime().exit(1);
		}

//		FldSpec[] proj_list = new FldSpec[2];
//		proj_list[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
//		proj_list[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);

		AttrType[] jtype = new AttrType[2];
		jtype[0] = new AttrType(AttrType.attrString);
		jtype[1] = new AttrType(AttrType.attrString);

		TupleOrder ascending = new TupleOrder(TupleOrder.Ascending);
//		SortMerge sm = null;
//		try {
//			sm = new SortMerge(Ntypes, 4, Nsizes, Etypes, 6, Esizes, 1, 4, 1, 4, 10, am, am2, false, false, ascending,
//					outFilter, proj_list, 2);
//		} catch (Exception e) {
//			System.err.println("*** join error in SortMerge constructor ***");
//			status = FAIL;
//			System.err.println("" + e);
//			e.printStackTrace();
//		}
		
		FldSpec[] proj1 = new FldSpec[4];
		proj1[0] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		proj1[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);
		proj1[2] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		proj1[3] = new FldSpec(new RelSpec(RelSpec.innerRel), 6);
		
		NestedLoopExtended inl = null;
		try {
			inl = new NestedLoopExtended(Ntypes, 2, Nsizes, Etypes, 6, Esizes, 10, am, "edgeSourceIndexFile", outFilter, rightFilter, Eprojection, 6);
		} catch (Exception e) {
			System.err.println("*** Error preparing for nested_loop_join");
			System.err.println("" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		if(inl==null){
			System.out.print("inl null");
		}

		System.out.print("After nested loop join Node and Edge\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error constructing SortMerge");
			Runtime.getRuntime().exit(1);
		}

		//QueryCheck qcheck1 = new QueryCheck(1);
		TupleOrder ascending1 = new TupleOrder(TupleOrder.Ascending);
		Sort sort_names = null;
		try {
			sort_names = new Sort(JJtype, (short) 1, JJsize, (iterator.Iterator) inl, 1, ascending1, JJsize[0], 10, 0, null);
		} catch (Exception e) {
			System.err.println("*** Error preparing for nested_loop_join");
			System.err.println("" + e);
			Runtime.getRuntime().exit(1);
		}
		t = null;

		try {
			
			while ((t = inl.get_next()) != null) {
				t.print(Etypes);

				//qcheck1.Check(t);
			}
		} catch (Exception e) {
			System.err.println("" + e);
			e.printStackTrace();
			status = FAIL;
		}
		if (status != OK) {
			// bail out
			System.err.println("*** Error in get next tuple ");
			Runtime.getRuntime().exit(1);
		}

		//qcheck1.report(1);
		try {
			inl.close();
		} catch (Exception e) {
			status = FAIL;
			e.printStackTrace();
		}
		System.out.println("\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error in closing ");
			Runtime.getRuntime().exit(1);
		}
	}
	private void Query2() {
		// TODO Auto-generated method stub
		System.out.print("**********************Query2 starting *********************\n");
		boolean status = OK;

		// Sailors, Boats, Reserves Queries.
		System.out.print("Query: Find all the node Descriptors which are destination nodes of edges with weight less than or equal to 23");

		System.out.print("\n(Tests NodeScan, Projection, and Nested Loop Join)\n");

		CondExpr[] outFilter = new CondExpr[2];
		outFilter[0] = new CondExpr();
		
		CondExpr[] rightFilter = new CondExpr[2];
		rightFilter[0] = new CondExpr();
		
		Query2_CondExpr(outFilter, rightFilter);
		Tuple t = new Tuple();
		Node n;
		try {
			n = new Node();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		AttrType[] Ntypes = new AttrType[2];
		Ntypes[0] = new AttrType(AttrType.attrString);
		Ntypes[1] = new AttrType(AttrType.attrDesc);
		
		// SOS
		short[] Nsizes = new short[2];
		Nsizes[0] = 44;
		//Nsizes[1] = 20;// first elt. is 30

		FldSpec[] Nprojection = new FldSpec[2];
		Nprojection[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		Nprojection[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);//wrong
		
		CondExpr[] selects = new CondExpr[1];
		selects = null;

		NodeScan am = null;
		try {
			am = new NodeScan("nodeheapfile", Ntypes, Nsizes, (short) 2, (short) 2, Nprojection, null);
		} catch (Exception e) {
			status = FAIL;
			System.err.println("" + e);
		}

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for Node");
			Runtime.getRuntime().exit(1);
		}
		AttrType[] jtype12 = new AttrType[2];
		jtype12[0] = new AttrType(AttrType.attrString);
		jtype12[1] = new AttrType(AttrType.attrDesc);
//		try {
//			while ((n = am.get_next()) != null) {
//				n.print();
//
//				//qcheck1.Check(t);
//			}
//		} catch (Exception e) {
//			System.err.println("" + e);
//			e.printStackTrace();
//			status = FAIL;
//		}

		AttrType[] Etypes = new AttrType[6];
		Etypes[0] = new AttrType(AttrType.attrString);//label
		Etypes[1] = new AttrType(AttrType.attrInteger);//pgidsource
		Etypes[2] = new AttrType(AttrType.attrInteger);//slotidsource
		Etypes[3] = new AttrType(AttrType.attrInteger);//pgiddest
		Etypes[4] = new AttrType(AttrType.attrInteger);//slotiddest
		Etypes[5] = new AttrType(AttrType.attrInteger);//weight

		short[] Esizes = new short[1];
		Esizes[0] = 44;
		FldSpec[] Eprojection = new FldSpec[6];
		Eprojection[0] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		Eprojection[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
		Eprojection[2] = new FldSpec(new RelSpec(RelSpec.innerRel), 3);
		Eprojection[3] = new FldSpec(new RelSpec(RelSpec.innerRel), 4);
		Eprojection[4] = new FldSpec(new RelSpec(RelSpec.innerRel), 5);
		Eprojection[5] = new FldSpec(new RelSpec(RelSpec.innerRel), 6);
		
		AttrType[] JJtype = { new AttrType(AttrType.attrString),new AttrType(AttrType.attrDesc), new AttrType(AttrType.attrString), new AttrType(AttrType.attrInteger)};

		short[] JJsize = new short[4];
		JJsize[0] = 44;
		JJsize[2] = 44;

//		FileScan am2 = null;
//		try {
//			am2 = new FileScan("edgeheapfile", Etypes, Esizes, (short) 6, (short) 6, Eprojection, null);
//		} catch (Exception e) {
//			status = FAIL;
//			System.err.println("" + e);
//		}

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for reserves");
			Runtime.getRuntime().exit(1);
		}

//		FldSpec[] proj_list = new FldSpec[2];
//		proj_list[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
//		proj_list[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);

		AttrType[] jtype = new AttrType[2];
		jtype[0] = new AttrType(AttrType.attrString);
		jtype[1] = new AttrType(AttrType.attrString);

		TupleOrder ascending = new TupleOrder(TupleOrder.Ascending);
//		SortMerge sm = null;
//		try {
//			sm = new SortMerge(Ntypes, 4, Nsizes, Etypes, 6, Esizes, 1, 4, 1, 4, 10, am, am2, false, false, ascending,
//					outFilter, proj_list, 2);
//		} catch (Exception e) {
//			System.err.println("*** join error in SortMerge constructor ***");
//			status = FAIL;
//			System.err.println("" + e);
//			e.printStackTrace();
//		}
		
		FldSpec[] proj1 = new FldSpec[4];
		proj1[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		proj1[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);
		proj1[2] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		proj1[3] = new FldSpec(new RelSpec(RelSpec.innerRel), 6);
		
		NestedLoopExtended inl = null;
		try {
			inl = new NestedLoopExtended(Ntypes, 2, Nsizes, Etypes, 6, Esizes, 10, am, "edgeDestinationIndexFile", outFilter, rightFilter, Eprojection, 6);
		} catch (Exception e) {
			System.err.println("*** Error preparing for nested_loop_join");
			System.err.println("" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		if(inl==null){
			System.out.print("inl null");
		}

		System.out.print("After nested loop join Node and Edge\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error constructing SortMerge");
			Runtime.getRuntime().exit(1);
		}

		//QueryCheck qcheck1 = new QueryCheck(1);
		TupleOrder ascending1 = new TupleOrder(TupleOrder.Ascending);
		Sort sort_names = null;
		try {
			sort_names = new Sort(JJtype, (short) 1, JJsize, (iterator.Iterator) inl, 1, ascending1, JJsize[0], 10, 0, null);
		} catch (Exception e) {
			System.err.println("*** Error preparing for nested_loop_join");
			System.err.println("" + e);
			Runtime.getRuntime().exit(1);
		}
		t = null;

		try {
			while ((t = inl.get_next()) != null) {
				t.print(Etypes);

				//qcheck1.Check(t);
			}
		} catch (Exception e) {
			System.err.println("" + e);
			e.printStackTrace();
			status = FAIL;
		}
		if (status != OK) {
			// bail out
			System.err.println("*** Error in get next tuple ");
			Runtime.getRuntime().exit(1);
		}

		//qcheck1.report(1);
		try {
			inl.close();
		} catch (Exception e) {
			status = FAIL;
			e.printStackTrace();
		}
		System.out.println("\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error in closing ");
			Runtime.getRuntime().exit(1);
		}
	}
	
	private void Query3() {
		// TODO Auto-generated method stub
		System.out.print("**********************Query3 starting *********************\n");
		boolean status = OK;

		// Sailors, Boats, Reserves Queries.
		System.out.print("Query: Find all the Edge which are source nodes of Node with descriptor equal to [1 26 42 14 2]");

		System.out.print("\n(Tests EdgeScan, Projection, and Nested Loop Join)\n");

		CondExpr[] outFilter = new CondExpr[2];
		outFilter[0] = new CondExpr();
		
		CondExpr[] rightFilter = new CondExpr[2];
		rightFilter[0] = new CondExpr();
		
		Query3_CondExpr(outFilter, rightFilter);
		outFilter[0].updateDesc();
		rightFilter[0].updateDesc();
		Tuple t = new Tuple();
		Edge e;
		try {
			e = new Edge();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		AttrType[] Etypes = new AttrType[6];
		Etypes[0] = new AttrType(AttrType.attrString);//label
		Etypes[1] = new AttrType(AttrType.attrInteger);//pgidsource
		Etypes[2] = new AttrType(AttrType.attrInteger);//slotidsource
		Etypes[3] = new AttrType(AttrType.attrInteger);//pgiddest
		Etypes[4] = new AttrType(AttrType.attrInteger);//slotiddest
		Etypes[5] = new AttrType(AttrType.attrInteger);//weight

		short[] Esizes = new short[1];
		Esizes[0] = 44;
		FldSpec[] Eprojection = new FldSpec[6];
		Eprojection[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		Eprojection[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);
		Eprojection[2] = new FldSpec(new RelSpec(RelSpec.outer), 3);
		Eprojection[3] = new FldSpec(new RelSpec(RelSpec.outer), 4);
		Eprojection[4] = new FldSpec(new RelSpec(RelSpec.outer), 5);
		Eprojection[5] = new FldSpec(new RelSpec(RelSpec.outer), 6);
		CondExpr[] selects = new CondExpr[1];
		selects = null; 

		EdgeScan am = null;
		try {
			am = new EdgeScan("edgeheapfile", Etypes, Esizes, (short) 6, (short) 6, Eprojection, null);
		} catch (Exception e1) {
			status = FAIL;
			System.err.println("" + e1);
		}
		
		
		

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for Edge");
			Runtime.getRuntime().exit(1);
		}
		AttrType[] Ntypes = new AttrType[2];
		Ntypes[0] = new AttrType(AttrType.attrString);
		Ntypes[1] = new AttrType(AttrType.attrDesc);
		
		// SOS
		short[] Nsizes = new short[2];
		Nsizes[0] = 44;
		//Nsizes[1] = 20;// first elt. is 30

		AttrType[] jtype12 = new AttrType[2];
		jtype12[0] = new AttrType(AttrType.attrString);
		jtype12[1] = new AttrType(AttrType.attrDesc);
//		try {
//			while ((n = am.get_next()) != null) {
//				n.print();
//
//				//qcheck1.Check(t);
//			}
//		} catch (Exception e) {
//			System.err.println("" + e);
//			e.printStackTrace();
//			status = FAIL;
//		}

				
		AttrType[] JJtype = { new AttrType(AttrType.attrString),new AttrType(AttrType.attrDesc)};

		short[] JJsize = new short[1];
		JJsize[0] = 44;
		

//		FileScan am2 = null;
//		try {
//			am2 = new FileScan("edgeheapfile", Etypes, Esizes, (short) 6, (short) 6, Eprojection, null);
//		} catch (Exception e) {
//			status = FAIL;
//			System.err.println("" + e);
//		}

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for reserves");
			Runtime.getRuntime().exit(1);
		}

//		FldSpec[] proj_list = new FldSpec[2];
//		proj_list[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
//		proj_list[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);

		AttrType[] jtype = new AttrType[2];
		jtype[0] = new AttrType(AttrType.attrString);
		jtype[1] = new AttrType(AttrType.attrDesc);

		TupleOrder ascending = new TupleOrder(TupleOrder.Ascending);
//		SortMerge sm = null;
//		try {
//			sm = new SortMerge(Ntypes, 4, Nsizes, Etypes, 6, Esizes, 1, 4, 1, 4, 10, am, am2, false, false, ascending,
//					outFilter, proj_list, 2);
//		} catch (Exception e) {
//			System.err.println("*** join error in SortMerge constructor ***");
//			status = FAIL;
//			System.err.println("" + e);
//			e.printStackTrace();
//		}
		
		FldSpec[] proj1 = new FldSpec[2];
		proj1[0] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		proj1[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
		
		NestedLoopExtendedEdge inl = null;
		try {
			inl = new NestedLoopExtendedEdge(Etypes, 6, Esizes, Ntypes, 2, Nsizes, 10, am, "nodeLabelIndexFile", outFilter, null, proj1, 2);
		} catch (Exception e1) {
			System.err.println("*** Error preparing for nested_loop_join");
			System.err.println("" + e1);
			e1.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		if(inl==null){
			System.out.print("inl null");
		}

		System.out.print("After nested loop join Edge and Node\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error constructing SortMerge");
			Runtime.getRuntime().exit(1);
		}

		//QueryCheck qcheck1 = new QueryCheck(1);
		TupleOrder ascending1 = new TupleOrder(TupleOrder.Ascending);
//		Sort sort_names = null;
//		try {
//			sort_names = new Sort(JJtype, (short) 1, JJsize, (iterator.Iterator) inl, 1, ascending1, JJsize[0], 10, 0, null);
//		} catch (Exception e1) {
//			System.err.println("*** Error preparing for nested_loop_join");
//			System.err.println("" + e1);
//			Runtime.getRuntime().exit(1);
//		}
		t = null;

		try {
			
			while ((t = inl.get_next()) != null) {
				t.print(JJtype);

				//qcheck1.Check(t);
			}
		} catch (Exception e1) {
			System.err.println("" + e1);
			e1.printStackTrace();
			status = FAIL;
		}
		if (status != OK) {
			// bail out
			System.err.println("*** Error in get next tuple ");
			Runtime.getRuntime().exit(1);
		}

		//qcheck1.report(1);
		try {
			inl.close();
		} catch (Exception e1) {
			status = FAIL;
			e1.printStackTrace();
		}
		System.out.println("\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error in closing ");
			Runtime.getRuntime().exit(1);
		}
	}
	private void Query4() {
		// TODO Auto-generated method stub
		System.out.print("**********************Query4 starting *********************\n");
		boolean status = OK;
		//USE 990 1096 19 22 18 28 5
		// Sailors, Boats, Reserves Queries.
		System.out.print("Query: Find all the Edge which are source nodes of Node with Desc equal to [19,22,18,28,5]");

		System.out.print("\n(Tests EdgeScan, Projection, and Nested Loop Join)\n");

		CondExpr[] outFilter = new CondExpr[2];
		outFilter[0] = new CondExpr();
		
		CondExpr[] rightFilter = new CondExpr[2];
		rightFilter[0] = new CondExpr();
		
		Query4_CondExpr(outFilter, rightFilter);
		outFilter[0].updateDesc();
		rightFilter[0].updateDesc();
		Tuple t = new Tuple();
		Edge e;
		try {
			e = new Edge();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		AttrType[] Etypes = new AttrType[6];
		Etypes[0] = new AttrType(AttrType.attrString);//label
		Etypes[1] = new AttrType(AttrType.attrInteger);//pgidsource
		Etypes[2] = new AttrType(AttrType.attrInteger);//slotidsource
		Etypes[3] = new AttrType(AttrType.attrInteger);//pgiddest
		Etypes[4] = new AttrType(AttrType.attrInteger);//slotiddest
		Etypes[5] = new AttrType(AttrType.attrInteger);//weight

		short[] Esizes = new short[1];
		Esizes[0] = 44;
		FldSpec[] Eprojection = new FldSpec[6];
		Eprojection[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		Eprojection[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);
		Eprojection[2] = new FldSpec(new RelSpec(RelSpec.outer), 3);
		Eprojection[3] = new FldSpec(new RelSpec(RelSpec.outer), 4);
		Eprojection[4] = new FldSpec(new RelSpec(RelSpec.outer), 5);
		Eprojection[5] = new FldSpec(new RelSpec(RelSpec.outer), 6);
		CondExpr[] selects = new CondExpr[1];
		selects = null; 

		EdgeScan am = null;
		try {
			am = new EdgeScan("edgeheapfile", Etypes, Esizes, (short) 6, (short) 6, Eprojection, null);
		} catch (Exception e1) {
			status = FAIL;
			System.err.println("" + e1);
		}
		
		
		

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for Edge");
			Runtime.getRuntime().exit(1);
		}
		AttrType[] Ntypes = new AttrType[2];
		Ntypes[0] = new AttrType(AttrType.attrString);
		Ntypes[1] = new AttrType(AttrType.attrDesc);
		
		// SOS
		short[] Nsizes = new short[2];
		Nsizes[0] = 44;
		//Nsizes[1] = 20;// first elt. is 30

		AttrType[] jtype12 = new AttrType[2];
		jtype12[0] = new AttrType(AttrType.attrString);
		jtype12[1] = new AttrType(AttrType.attrDesc);
//		try {
//			while ((n = am.get_next()) != null) {
//				n.print();
//
//				//qcheck1.Check(t);
//			}
//		} catch (Exception e) {
//			System.err.println("" + e);
//			e.printStackTrace();
//			status = FAIL;
//		}

				
		AttrType[] JJtype = { new AttrType(AttrType.attrString),new AttrType(AttrType.attrDesc)};

		short[] JJsize = new short[1];
		JJsize[0] = 44;
		

//		FileScan am2 = null;
//		try {
//			am2 = new FileScan("edgeheapfile", Etypes, Esizes, (short) 6, (short) 6, Eprojection, null);
//		} catch (Exception e) {
//			status = FAIL;
//			System.err.println("" + e);
//		}

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for reserves");
			Runtime.getRuntime().exit(1);
		}

//		FldSpec[] proj_list = new FldSpec[2];
//		proj_list[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
//		proj_list[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);

		AttrType[] jtype = new AttrType[2];
		jtype[0] = new AttrType(AttrType.attrString);
		jtype[1] = new AttrType(AttrType.attrDesc);

		TupleOrder ascending = new TupleOrder(TupleOrder.Ascending);
//		SortMerge sm = null;
//		try {
//			sm = new SortMerge(Ntypes, 4, Nsizes, Etypes, 6, Esizes, 1, 4, 1, 4, 10, am, am2, false, false, ascending,
//					outFilter, proj_list, 2);
//		} catch (Exception e) {
//			System.err.println("*** join error in SortMerge constructor ***");
//			status = FAIL;
//			System.err.println("" + e);
//			e.printStackTrace();
//		}
		
		FldSpec[] proj1 = new FldSpec[2];
		proj1[0] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		proj1[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
		
		NestedLoopExtendedEdge inl = null;
		try {
			inl = new NestedLoopExtendedEdge(Etypes, 6, Esizes, Ntypes, 2, Nsizes, 10, am, "nodeDescIndexFile", outFilter, rightFilter, proj1, 2);
		} catch (Exception e1) {
			System.err.println("*** Error preparing for nested_loop_join");
			System.err.println("" + e1);
			e1.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		if(inl==null){
			System.out.print("inl null");
		}

		System.out.print("After nested loop join Edge and Node\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error constructing SortMerge");
			Runtime.getRuntime().exit(1);
		}

		//QueryCheck qcheck1 = new QueryCheck(1);
		TupleOrder ascending1 = new TupleOrder(TupleOrder.Ascending);
//		Sort sort_names = null;
//		try {
//			sort_names = new Sort(JJtype, (short) 1, JJsize, (iterator.Iterator) inl, 1, ascending1, JJsize[0], 10, 0, null);
//		} catch (Exception e1) {
//			System.err.println("*** Error preparing for nested_loop_join");
//			System.err.println("" + e1);
//			Runtime.getRuntime().exit(1);
//		}
		t = null;

		try {
			
			while ((t = inl.get_next()) != null) {
				t.print(JJtype);

				//qcheck1.Check(t);
			}
		} catch (Exception e1) {
			System.err.println("" + e1);
			e1.printStackTrace();
			status = FAIL;
		}
		if (status != OK) {
			// bail out
			System.err.println("*** Error in get next tuple ");
			Runtime.getRuntime().exit(1);
		}

		//qcheck1.report(1);
		try {
			inl.close();
		} catch (Exception e1) {
			status = FAIL;
			e1.printStackTrace();
		}
		System.out.println("\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error in closing ");
			Runtime.getRuntime().exit(1);
		}
	}
	private void Query5() {
		// TODO Auto-generated method stub
		System.out.print("**********************Query 5 starting *********************\n");
		boolean status = OK;
		//USE 990 1096 19 22 18 28 5
		// Sailors, Boats, Reserves Queries.
		System.out.print("Query: Find all the Edge with weight less than 23 which are source nodes of Node with Desc equal to [19,22,18,28,5]");

		System.out.print("\n(Tests EdgeScan, Projection, and Nested Loop Join)\n");

		CondExpr[] outFilter = new CondExpr[2];
		outFilter[0] = new CondExpr();
		
		CondExpr[] rightFilter = new CondExpr[2];
		rightFilter[0] = new CondExpr();
		
		Query4_CondExpr(outFilter, rightFilter);
		
		CondExpr[] outFilter1 = new CondExpr[2];
		outFilter1[0] = new CondExpr();
		
		CondExpr[] rightFilter1 = new CondExpr[2];
		rightFilter1[0] = new CondExpr();
		
		Query4_CondExpr(outFilter, rightFilter);
		Query2_CondExpr(outFilter1, rightFilter1);
		rightFilter[0].updateDesc();
		Tuple t = new Tuple();
		Edge e;
		try {
			e = new Edge();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		AttrType[] Etypes = new AttrType[6];
		Etypes[0] = new AttrType(AttrType.attrString);//label
		Etypes[1] = new AttrType(AttrType.attrInteger);//pgidsource
		Etypes[2] = new AttrType(AttrType.attrInteger);//slotidsource
		Etypes[3] = new AttrType(AttrType.attrInteger);//pgiddest
		Etypes[4] = new AttrType(AttrType.attrInteger);//slotiddest
		Etypes[5] = new AttrType(AttrType.attrInteger);//weight

		short[] Esizes = new short[1];
		Esizes[0] = 44;
		FldSpec[] Eprojection = new FldSpec[6];
		Eprojection[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		Eprojection[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);
		Eprojection[2] = new FldSpec(new RelSpec(RelSpec.outer), 3);
		Eprojection[3] = new FldSpec(new RelSpec(RelSpec.outer), 4);
		Eprojection[4] = new FldSpec(new RelSpec(RelSpec.outer), 5);
		Eprojection[5] = new FldSpec(new RelSpec(RelSpec.outer), 6);
		CondExpr[] selects = new CondExpr[1];
		selects = null; 

		EdgeScan am = null;
		try {
			am = new EdgeScan("edgeheapfile", Etypes, Esizes, (short) 6, (short) 6, Eprojection, null);
		} catch (Exception e1) {
			status = FAIL;
			System.err.println("" + e1);
		}
		
		
		

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for Edge");
			Runtime.getRuntime().exit(1);
		}
		AttrType[] Ntypes = new AttrType[2];
		Ntypes[0] = new AttrType(AttrType.attrString);
		Ntypes[1] = new AttrType(AttrType.attrDesc);
		
		// SOS
		short[] Nsizes = new short[2];
		Nsizes[0] = 44;
		//Nsizes[1] = 20;// first elt. is 30

		AttrType[] jtype12 = new AttrType[2];
		jtype12[0] = new AttrType(AttrType.attrString);
		jtype12[1] = new AttrType(AttrType.attrDesc);
//		try {
//			while ((n = am.get_next()) != null) {
//				n.print();
//
//				//qcheck1.Check(t);
//			}
//		} catch (Exception e) {
//			System.err.println("" + e);
//			e.printStackTrace();
//			status = FAIL;
//		}

				
		AttrType[] JJtype = { new AttrType(AttrType.attrString),new AttrType(AttrType.attrDesc)};

		short[] JJsize = new short[1];
		JJsize[0] = 44;
		

//		FileScan am2 = null;
//		try {
//			am2 = new FileScan("edgeheapfile", Etypes, Esizes, (short) 6, (short) 6, Eprojection, null);
//		} catch (Exception e) {
//			status = FAIL;
//			System.err.println("" + e);
//		}

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for reserves");
			Runtime.getRuntime().exit(1);
		}

//		FldSpec[] proj_list = new FldSpec[2];
//		proj_list[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
//		proj_list[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);

		AttrType[] jtype = new AttrType[2];
		jtype[0] = new AttrType(AttrType.attrString);
		jtype[1] = new AttrType(AttrType.attrDesc);

		TupleOrder ascending = new TupleOrder(TupleOrder.Ascending);
//		SortMerge sm = null;
//		try {
//			sm = new SortMerge(Ntypes, 4, Nsizes, Etypes, 6, Esizes, 1, 4, 1, 4, 10, am, am2, false, false, ascending,
//					outFilter, proj_list, 2);
//		} catch (Exception e) {
//			System.err.println("*** join error in SortMerge constructor ***");
//			status = FAIL;
//			System.err.println("" + e);
//			e.printStackTrace();
//		}
		
		FldSpec[] proj1 = new FldSpec[2];
		proj1[0] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		proj1[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
		
		NestedLoopExtendedEdge inl = null;
		try {
			inl = new NestedLoopExtendedEdge(Etypes, 6, Esizes, Ntypes, 2, Nsizes, 10, am, "nodeDescIndexFile", outFilter, rightFilter, proj1, 2);
		} catch (Exception e1) {
			System.err.println("*** Error preparing for nested_loop_join");
			System.err.println("" + e1);
			e1.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		if(inl==null){
			System.out.print("inl null");
		}

		System.out.print("After nested loop join Edge and Node\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error constructing SortMerge");
			Runtime.getRuntime().exit(1);
		}
		FldSpec[] proj2 = new FldSpec[6];
		proj2[0] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		proj2[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
		proj2[2] = new FldSpec(new RelSpec(RelSpec.innerRel), 3);
		proj2[3] = new FldSpec(new RelSpec(RelSpec.innerRel), 4);
		proj2[4] = new FldSpec(new RelSpec(RelSpec.innerRel), 5);
		proj2[5] = new FldSpec(new RelSpec(RelSpec.innerRel), 6);
		
		NestedLoopExtended inl1 = null;
		try {
			inl1 = new NestedLoopExtended(Ntypes, 2, Nsizes,Etypes, 6, Esizes,  10, inl, "edgeDestinationIndexFile", outFilter1, rightFilter1, proj2, 6);
		} catch (Exception e1) {
			System.err.println("*** Error preparing for nested_loop_join");
			System.err.println("" + e1);
			e1.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		if(inl1==null){
			System.out.print("inl null");
		}

		System.out.print("After nested loop join Edge and Node\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error constructing SortMerge");
			Runtime.getRuntime().exit(1);
		}


		//QueryCheck qcheck1 = new QueryCheck(1);
		TupleOrder ascending1 = new TupleOrder(TupleOrder.Ascending);
//		Sort sort_names = null;
//		try {
//			sort_names = new Sort(JJtype, (short) 1, JJsize, (iterator.Iterator) inl, 1, ascending1, JJsize[0], 10, 0, null);
//		} catch (Exception e1) {
//			System.err.println("*** Error preparing for nested_loop_join");
//			System.err.println("" + e1);
//			Runtime.getRuntime().exit(1);
//		}
		t = null;

		try {
			
			while ((t = inl1.get_next()) != null) {
				t.print(Etypes);

				//qcheck1.Check(t);
			}
		} catch (Exception e1) {
			System.err.println("" + e1);
			e1.printStackTrace();
			status = FAIL;
		}
		if (status != OK) {
			// bail out
			System.err.println("*** Error in get next tuple ");
			Runtime.getRuntime().exit(1);
		}

		//qcheck1.report(1);
		try {
			inl.close();
		} catch (Exception e1) {
			status = FAIL;
			e1.printStackTrace();
		}
		System.out.println("\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error in closing ");
			Runtime.getRuntime().exit(1);
		}
		try {
			inl1.close();
		} catch (Exception e1) {
			status = FAIL;
			e1.printStackTrace();
		}
		System.out.println("\n");
		if (status != OK) {
			// bail out
			System.err.println("*** Error in closing ");
			Runtime.getRuntime().exit(1);
		}
	}
	private void Query1_CondExpr(CondExpr[] expr, CondExpr[] expr2) {
		// TODO Auto-generated method stub
		expr[0].next = null;
		expr[0].op = new AttrOperator(AttrOperator.aopEQ);
		expr[0].type1 = new AttrType(AttrType.attrSymbol);
		expr[0].type2 = new AttrType(AttrType.attrSymbol);
		expr[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.outer), 1);
		expr[0].operand2.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		
		expr[1] = null;
		
		expr2[0].next = null;
		expr2[0].op = new AttrOperator(AttrOperator.aopLE);
		expr2[0].type1 = new AttrType(AttrType.attrSymbol);
		expr2[0].type2 = new AttrType(AttrType.attrInteger);
		expr2[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 6);
		expr2[0].operand2.integer = 23;
		
		expr2[1] = null;
	}	
	private void Query2_CondExpr(CondExpr[] expr, CondExpr[] expr2) {
		// TODO Auto-generated method stub
		expr[0].next = null;
		expr[0].op = new AttrOperator(AttrOperator.aopEQ);
		expr[0].type1 = new AttrType(AttrType.attrSymbol);
		expr[0].type2 = new AttrType(AttrType.attrSymbol);
		expr[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.outer), 1);
		expr[0].operand2.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		
		expr[1] = null;
		
		expr2[0].next = null;
		expr2[0].op = new AttrOperator(AttrOperator.aopGE);
		expr2[0].type1 = new AttrType(AttrType.attrSymbol);
		expr2[0].type2 = new AttrType(AttrType.attrInteger);
		expr2[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 6);
		expr2[0].operand2.integer = 23;
		
		expr2[1] = null;
	}
	private void Query3_CondExpr(CondExpr[] expr, CondExpr[] expr2) {
		// TODO Auto-generated method stub
		expr[0].next = null;
		expr[0].op = new AttrOperator(AttrOperator.aopEQ);
		expr[0].type1 = new AttrType(AttrType.attrSymbol);
		expr[0].type2 = new AttrType(AttrType.attrSymbol);
		expr[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.outer), 3);
		expr[0].operand2.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		
		expr[1] = null;
		
		expr2[0].next = null;
		expr2[0].op = new AttrOperator(AttrOperator.aopEQ);
		expr2[0].type1 = new AttrType(AttrType.attrSymbol);
		expr2[0].type2 = new AttrType(AttrType.attrDesc);
		expr2[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
		Descriptor desc1 = new Descriptor();
		desc1.set(1,26,42,14,2);
		expr2[0].operand2.desc = desc1;
		
		expr2[1] = null;
	}
	private void Query4_CondExpr(CondExpr[] expr, CondExpr[] expr2) {
		// TODO Auto-generated method stub
		expr[0].next = null;
		expr[0].op = new AttrOperator(AttrOperator.aopEQ);
		expr[0].type1 = new AttrType(AttrType.attrSymbol);
		expr[0].type2 = new AttrType(AttrType.attrSymbol);
		expr[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.outer), 3);
		expr[0].operand2.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		
		expr[1] = null;
		
		expr2[0].next = null;
		expr2[0].op = new AttrOperator(AttrOperator.aopEQ);
		expr2[0].type1 = new AttrType(AttrType.attrSymbol);
		expr2[0].type2 = new AttrType(AttrType.attrDesc);
		expr2[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
		Descriptor desc1 = new Descriptor();
		desc1.set(19,22,18,28,5);
		expr2[0].operand2.desc = desc1;
		
		expr2[1] = null;
	}
}


public class JoinTestExtended {
	boolean sortstatus;
	public JoinTestExtended(){
		sortstatus = false;
	}
	public void doTheJoin(){
		// SystemDefs global = new SystemDefs("bingjiedb", 100, 70, null);
		// JavabaseDB.openDB("/tmp/nwangdb", 5000);

		JoinsDriver jjoin = new JoinsDriver();

		sortstatus = jjoin.runTests();
		if (sortstatus != true) {
			System.out.println("Error ocurred during join tests");
		} else {
			System.out.println("join tests completed successfully");
		}
	}
}
