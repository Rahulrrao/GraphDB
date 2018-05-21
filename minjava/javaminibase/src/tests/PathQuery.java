package tests;

import java.io.IOException;

import btree.BTFileScan;
import btree.ConstructPageException;
import btree.IndexLeafIterator;
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
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import catalog.Utility;
import diskmgr.PCounter;
import global.AttrOperator;
import global.AttrType;
import global.Descriptor;
import global.NID;
import global.SystemDefs;
import global.TupleOrder;
import heap.FieldNumberOutOfBoundException;
import heap.Tuple;
import iterator.CondExpr;
import iterator.DuplElim;
import iterator.EdgeScan;
import iterator.FldSpec;
import iterator.Iterator;
import iterator.NestedLoopExtended;
import iterator.NestedLoopExtendedEdge;
import iterator.NodeScan;
import iterator.RelSpec;
import iterator.Sort;
import iterator.SortException;
import nodeheap.Node;
import zIndex.DescriptorKey;

public class PathQuery {
	private String exp;
	private boolean OK = true;
	private boolean FAIL = false;
	private Iterator nextNodeIterator;
	private Iterator nextEdgeIterator;

	public PathQuery(String exp) {
		this.exp = exp;
	}

	public void evaluate(String choice) throws ScanIteratorException, KeyNotMatchException, IteratorException,
			ConstructPageException, PinPageException, UnpinPageException, IOException, InvalidFrameNumberException,
			ReplacerException, PageUnpinnedException, HashEntryNotFoundException {
		String[] n = exp.split("/");
		String element = n[0];
		element = element.trim();
		AttrType[] Ntypes = new AttrType[2];
		Ntypes[0] = new AttrType(AttrType.attrString);
		Ntypes[1] = new AttrType(AttrType.attrDesc);
		short[] Nsizes = new short[2];
		Nsizes[0] = 44;
		BTFileScan scan = null;
		// node label
		if (element.startsWith("L")) {
			String label = element.substring(1).trim();
			scan = SystemDefs.JavabaseDB.nodeLabelIndexFile.new_scan(new StringKey(label), new StringKey(label));
			if (scan != null) {
				KeyDataEntry entry = scan.get_next();
				while (entry != null) {
					// Collect node data
					LeafData leafData = (LeafData) entry.data;
					NID nid = new NID();
					nid.copyRid(leafData.getData());
					// print node
					try {
						Node startNode = SystemDefs.JavabaseDB.nodeHeapfile.getNode(nid);
						Iterator tailNodes = task3Method(startNode.getLabel(), n);
						printResults(choice, startNode, tailNodes);
					} catch (Exception e) {
						System.err.println("" + e);
					}

					entry = scan.get_next();
				}
				scan.DestroyBTreeFileScan();
			}
		} else if (element.startsWith("D")) {
			// node desc
			Descriptor desc = Utility.convertToDescriptor(element.substring(1).trim());
			scan = SystemDefs.JavabaseDB.nodeDescriptorIndexFile.new_scan(new DescriptorKey(desc),
					new DescriptorKey(desc));
			IndexLeafIterator it = new IndexLeafIterator(scan);
			TupleOrder ascending1 = new TupleOrder(TupleOrder.Ascending);
			Nsizes[0] = 44;
			if(choice.charAt(0) == 'a'){
				try{
				if(it!=null){
					Tuple startNode = null;

					startNode = it.get_next();
					while (startNode != null) {
						// Collect node data
						try {
							Iterator tailNodes = task3Method(startNode.getStrFld(1), n);
							printResults(choice, startNode, tailNodes);
						} catch (Exception e) {
							System.err.println("" + e);
						}

						startNode = it.get_next();
					}

					it.close();

				}
			} catch (Exception e) {
				System.err.println("" + e);
			}
			}
			else{
				Sort sort_names = null;
				try {
					sort_names = new Sort(Ntypes, (short) 2, Nsizes, it, 1, ascending1, Nsizes[0], 12, 0, null);
				} catch (SortException e) {
					System.err.println("" + e);
				}
				try {
					if (sort_names != null) {
						Tuple startNode = null;

						startNode = sort_names.get_next();
						while (startNode != null) {
							// Collect node data
							try {
								Iterator tailNodes = task3Method(startNode.getStrFld(1), n);
								printResults(choice, startNode, tailNodes);
							} catch (Exception e) {
								System.err.println("" + e);
							}

							startNode = sort_names.get_next();
						}

						sort_names.close();

					}
				} catch (Exception e) {
					System.err.println("" + e);
				}
			}

		}

		// ignore
		/*
		 * for(String element : n){ element = element.trim(); //node label
		 * if(element.startsWith("L")){ path.add(element.substring(1).trim()); }
		 * //node desc else if(element.startsWith("D")){ Descriptor desc =
		 * Utility.convertToDescriptor(element.substring(1).trim());
		 * path.add(desc); } //ignore }
		 */

		//
	}

	private void printResults(String choice, Tuple startNode, Iterator tailNodes) {
		AttrType[] Ntypes = new AttrType[2];
		Ntypes[0] = new AttrType(AttrType.attrString);
		Ntypes[1] = new AttrType(AttrType.attrDesc);
		short[] Nsizes = new short[2];
		Nsizes[0] = 44;
		int pageRead = PCounter.rcounter;
		int pageWrite = PCounter.wcounter;
		Tuple startTuple = startNode;
		System.out.println("Printing all tail nodes corresp to head");
		if (choice.charAt(0) == 'a') {
			Tuple t = null;
			try {
				while ((t = tailNodes.get_next()) != null) {
					System.out.println("Head node:");
					printNode(startTuple);
					System.out.println("Tail node:");
					printNode(t);
					System.out.println();
				}
			} catch (Exception e1) {
				System.err.println("" + e1);
				e1.printStackTrace();
			}
			try {
				tailNodes.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (choice.charAt(0) == 'b') {
			TupleOrder ascending1 = new TupleOrder(TupleOrder.Ascending);

			Sort sort_names = null;
			System.out.println("Start sorting tail nodes");
			try {
				sort_names = new Sort(Ntypes, (short) 2, Nsizes, (iterator.Iterator) tailNodes, 1, ascending1,
						Nsizes[0], 10, 0, null);
			} catch (Exception e1) {
				System.err.println("*** Error preparing for Sort");
				System.err.println("" + e1);
				Runtime.getRuntime().exit(1);
			}
			Tuple t = null;
			try {
				System.out.println("Sorting completed");
				while ((t = sort_names.get_next()) != null) {
					System.out.println("Head node:");
					printNode(startTuple);
					System.out.println("Tail node:");
					printNode(t);
				}
			} catch (Exception e1) {
				System.err.println("" + e1);
				e1.printStackTrace();
			}
			try {
				sort_names.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (choice.charAt(0) == 'c') {
			TupleOrder ascending1 = new TupleOrder(TupleOrder.Ascending);
			System.out.println("Start sorting tail nodes");
			Sort sort_names = null;
			try {
				sort_names = new Sort(Ntypes, (short) 2, Nsizes, (iterator.Iterator) tailNodes, 1, ascending1,
						Nsizes[0], 10, 0, null);
			} catch (Exception e1) {
				System.err.println("*** Error preparing for Sort");
				System.err.println("" + e1);
				Runtime.getRuntime().exit(1);
			}
			System.out.println("Sorting completed");
			DuplElim duplElm = null;
			try {
				duplElm = new DuplElim(Ntypes, (short) 2, Nsizes, (iterator.Iterator) sort_names, 12, true, 0, null);
			} catch (Exception e1) {
				System.err.println("*** Error preparing for DuplElim");
				System.err.println("" + e1);
				Runtime.getRuntime().exit(1);
			}

			Tuple t = null;
			try {
				System.out.println("Starting duplicate elimination");
				while ((t = duplElm.get_next()) != null) {
					System.out.println("Head node:");
					printNode(startTuple);
					System.out.println("Tail node:");
					printNode(t);
				}
			} catch (Exception e1) {
				System.err.println("" + e1);
				e1.printStackTrace();
			}
			System.out.println("Duplicate elimination completed.");
			try {
				duplElm.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		pageRead = PCounter.rcounter - pageRead;
		pageWrite = PCounter.wcounter - pageWrite;
		System.out.println("No of pages read: " + pageRead + "\nNo of pages written: " + pageWrite + "\n");

	}

	private void printNode(Tuple startTuple) throws FieldNumberOutOfBoundException, IOException {
		System.out.println(startTuple.getStrFld(1));
	}

	private Iterator task3Method(String label, String[] n) {
		nextNodeIterator = null;
		nextEdgeIterator = null;
		// do nid join edge and edge join node
		int i = 0;
		while (i < n.length - 1) {
			doJoinNodeEdge(label, n[i++]);

			if (i >= n.length) {
				break;
			}
			doJoinEdgeNode(n[i]);

		}
		return nextNodeIterator;
	}

	private void doJoinNodeEdge(String startLabel, String n) {
		int pageRead = PCounter.rcounter;
		int pageWrite = PCounter.wcounter;
		boolean status = OK;

		char type = n.charAt(0);
		CondExpr[] outFilter = new CondExpr[2];
		outFilter[0] = new CondExpr();

		if (nextNodeIterator == null) {
			// first node join edge. should found all edges with given NID/label
			// as source.
			System.out.println("Node join Edge with source label" + startLabel + " No edge conditions");
			outFilter[0].next = null;
			outFilter[0].op = new AttrOperator(AttrOperator.aopEQ);
			outFilter[0].type1 = new AttrType(AttrType.attrSymbol);
			outFilter[0].type2 = new AttrType(AttrType.attrString);
			outFilter[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.outer), 1);
			outFilter[0].operand2.string = startLabel;
			outFilter[1] = null;
		} else {
			if (type == 'L') {
				String label = n.substring(1).trim();
				System.out.println("Node join Edge with source node label " + label + ". No edge conditions");
				outFilter[0].next = null;
				outFilter[0].op = new AttrOperator(AttrOperator.aopEQ);
				outFilter[0].type1 = new AttrType(AttrType.attrSymbol);
				outFilter[0].type2 = new AttrType(AttrType.attrString);
				outFilter[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.outer), 1);
				outFilter[0].operand2.string = label;
				outFilter[1] = null;
			} else {
				// descriptor node join edge
				Descriptor desc1 = new Descriptor();
				String descstr = n.substring(1).trim();
				desc1 = Utility.convertToDescriptor(descstr);
				System.out.println("Node join Edge with source node descriptor: " + descstr + ". No edge conditions");
				outFilter[0].next = null;
				outFilter[0].op = new AttrOperator(AttrOperator.aopEQ);
				outFilter[0].type1 = new AttrType(AttrType.attrSymbol);
				outFilter[0].type2 = new AttrType(AttrType.attrDesc);
				outFilter[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.outer), 2);
				outFilter[0].operand2.desc = desc1;
				outFilter[1] = null;

			}
		}

		AttrType[] Ntypes = new AttrType[2];
		Ntypes[0] = new AttrType(AttrType.attrString);
		Ntypes[1] = new AttrType(AttrType.attrDesc);

		// SOS
		short[] Nsizes = new short[2];
		Nsizes[0] = 44;
		// Nsizes[1] = 20;// first elt. is 30

		FldSpec[] Nprojection = new FldSpec[2];
		Nprojection[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		Nprojection[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);// wrong

		Iterator am = null;
		if (nextNodeIterator == null) {
			try {
				am = new NodeScan("nodeheapfile", Ntypes, Nsizes, (short) 2, (short) 2, Nprojection, null);
			} catch (Exception e) {
				status = FAIL;
				System.err.println("" + e);
			}
		} else {
			am = nextNodeIterator;
		}

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for Node");
			Runtime.getRuntime().exit(1);
		}
		AttrType[] jtype12 = new AttrType[2];
		jtype12[0] = new AttrType(AttrType.attrString);
		jtype12[1] = new AttrType(AttrType.attrDesc);

		AttrType[] Etypes = new AttrType[6];
		Etypes[0] = new AttrType(AttrType.attrString);// label
		Etypes[1] = new AttrType(AttrType.attrInteger);// pgidsource
		Etypes[2] = new AttrType(AttrType.attrInteger);// slotidsource
		Etypes[3] = new AttrType(AttrType.attrInteger);// pgiddest
		Etypes[4] = new AttrType(AttrType.attrInteger);// slotiddest
		Etypes[5] = new AttrType(AttrType.attrInteger);// weight

		short[] Esizes = new short[1];
		Esizes[0] = 44;
		FldSpec[] Eprojection = new FldSpec[6];
		Eprojection[0] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		Eprojection[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
		Eprojection[2] = new FldSpec(new RelSpec(RelSpec.innerRel), 3);
		Eprojection[3] = new FldSpec(new RelSpec(RelSpec.innerRel), 4);
		Eprojection[4] = new FldSpec(new RelSpec(RelSpec.innerRel), 5);
		Eprojection[5] = new FldSpec(new RelSpec(RelSpec.innerRel), 6);

		short[] JJsize = new short[4];
		JJsize[0] = 44;
		JJsize[2] = 44;

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for Node");
			Runtime.getRuntime().exit(1);
		}

		AttrType[] jtype = new AttrType[2];
		jtype[0] = new AttrType(AttrType.attrString);
		jtype[1] = new AttrType(AttrType.attrString);

		NestedLoopExtended inl = null;
		try {
			// no inner filter. get all edges.
			inl = new NestedLoopExtended(Ntypes, 2, Nsizes, Etypes, 6, Esizes, 10, am, "edgeSourceIndexFile", outFilter,
					null, Eprojection, 6);
		} catch (Exception e) {
			System.err.println("*** Error preparing for nested_loop_join");
			System.err.println("" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		if (inl == null) {
			System.out.print("inl null");
		}

		if (status != OK) {
			// bail out
			System.err.println("*** Error constructing SortMerge");
			Runtime.getRuntime().exit(1);
		}
		// try {
		// //startTuple.print(Ntypes);
		// System.out.println("");
		// while ((t = inl.get_next()) != null) {
		//
		// t.print(Etypes);
		//
		// //qcheck1.Check(t);
		// }
		// } catch (Exception e1) {
		// System.err.println("" + e1);
		// e1.printStackTrace();
		// //status = FAIL;
		// }
		//

		// set Iterator
		nextEdgeIterator = inl;

		System.out.println("Node Join Edge completed");
		pageRead = PCounter.rcounter - pageRead;
		pageWrite = PCounter.wcounter - pageWrite;
		System.out.println("No of pages read: " + pageRead + "\nNo of pages written: " + pageWrite + "\n");
	}

	private void doJoinEdgeNode(String n) {
		int pageRead = PCounter.rcounter;
		int pageWrite = PCounter.wcounter;

		boolean status = OK;

		CondExpr[] outFilter = new CondExpr[2];
		outFilter[0] = new CondExpr();

		outFilter[0].next = null;
		outFilter[0].op = new AttrOperator(AttrOperator.aopEQ);
		outFilter[0].type1 = new AttrType(AttrType.attrSymbol);
		outFilter[0].type2 = new AttrType(AttrType.attrSymbol);
		outFilter[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.outer), 3);
		outFilter[0].operand2.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 1);

		outFilter[1] = null;

		CondExpr[] rightFilter = new CondExpr[2];
		rightFilter[0] = new CondExpr();

		if (n.startsWith("D")) {
			String desc_string = n.substring(1).trim();
			System.out.println("Edge join node with destination node descriptor: " + desc_string);
			rightFilter[0].next = null;
			rightFilter[0].op = new AttrOperator(AttrOperator.aopEQ);
			rightFilter[0].type1 = new AttrType(AttrType.attrSymbol);
			rightFilter[0].type2 = new AttrType(AttrType.attrDesc);
			rightFilter[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
			Descriptor desc1 = new Descriptor();
			desc1 = Utility.convertToDescriptor(n.substring(1).trim());
			rightFilter[0].operand2.desc = desc1;

			rightFilter[1] = null;
			outFilter[0].updateDesc();
			rightFilter[0].updateDesc();
		} else if (n.startsWith("L")) {
			String label = n.substring(1).trim();
			System.out.println("Edge join node with destination node label: " + label);
			rightFilter[0].next = null;
			rightFilter[0].op = new AttrOperator(AttrOperator.aopEQ);
			rightFilter[0].type1 = new AttrType(AttrType.attrSymbol);
			rightFilter[0].type2 = new AttrType(AttrType.attrString);
			rightFilter[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 1);

			rightFilter[0].operand2.string = label;

			rightFilter[1] = null;
		}
		// Query3_CondExpr(outFilter, rightFilter);

		AttrType[] Etypes = new AttrType[6];
		Etypes[0] = new AttrType(AttrType.attrString);// label
		Etypes[1] = new AttrType(AttrType.attrInteger);// pgidsource
		Etypes[2] = new AttrType(AttrType.attrInteger);// slotidsource
		Etypes[3] = new AttrType(AttrType.attrInteger);// pgiddest
		Etypes[4] = new AttrType(AttrType.attrInteger);// slotiddest
		Etypes[5] = new AttrType(AttrType.attrInteger);// weight

		short[] Esizes = new short[1];
		Esizes[0] = 44;
		FldSpec[] Eprojection = new FldSpec[6];
		Eprojection[0] = new FldSpec(new RelSpec(RelSpec.outer), 1);
		Eprojection[1] = new FldSpec(new RelSpec(RelSpec.outer), 2);
		Eprojection[2] = new FldSpec(new RelSpec(RelSpec.outer), 3);
		Eprojection[3] = new FldSpec(new RelSpec(RelSpec.outer), 4);
		Eprojection[4] = new FldSpec(new RelSpec(RelSpec.outer), 5);
		Eprojection[5] = new FldSpec(new RelSpec(RelSpec.outer), 6);
		// no outer edge filter.

		Iterator am = null;
		if (nextEdgeIterator == null) {
			try {
				am = new EdgeScan("edgeheapfile", Etypes, Esizes, (short) 6, (short) 6, Eprojection, null);
			} catch (Exception e) {
				status = FAIL;
				System.err.println("" + e);
			}
		} else {
			am = nextEdgeIterator;
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
		// Nsizes[1] = 20;// first elt. is 30

		AttrType[] jtype12 = new AttrType[2];
		jtype12[0] = new AttrType(AttrType.attrString);
		jtype12[1] = new AttrType(AttrType.attrDesc);

		AttrType[] JJtype = { new AttrType(AttrType.attrString), new AttrType(AttrType.attrDesc) };

		short[] JJsize = new short[1];
		JJsize[0] = 44;

		// FileScan am2 = null;
		// try {
		// am2 = new FileScan("edgeheapfile", Etypes, Esizes, (short) 6, (short)
		// 6, Eprojection, null);
		// } catch (Exception e) {
		// status = FAIL;
		// System.err.println("" + e);
		// }

		if (status != OK) {
			// bail out
			System.err.println("*** Error setting up scan for reserves");
			Runtime.getRuntime().exit(1);
		}

		//
		AttrType[] jtype = new AttrType[2];
		jtype[0] = new AttrType(AttrType.attrString);
		jtype[1] = new AttrType(AttrType.attrDesc);

		FldSpec[] proj1 = new FldSpec[2];
		proj1[0] = new FldSpec(new RelSpec(RelSpec.innerRel), 1);
		proj1[1] = new FldSpec(new RelSpec(RelSpec.innerRel), 2);

		NestedLoopExtendedEdge inl = null;
		try {
			// if(n.startsWith("L")){
			inl = new NestedLoopExtendedEdge(Etypes, 6, Esizes, Ntypes, 2, Nsizes, 10, am, "nodeLabelIndexFile",
					outFilter, rightFilter, proj1, 2);
			// }
			// else{
			// inl = new NestedLoopExtendedEdge(Etypes, 6, Esizes, Ntypes, 2,
			// Nsizes, 10, am, "nodeDescIndexFile", outFilter, rightFilter,
			// proj1, 2);
			// }

		} catch (Exception e1) {
			System.err.println("*** Error preparing for nested_loop_join");
			System.err.println("" + e1);
			e1.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		if (inl == null) {
			System.out.print("inl null");
		}

		System.out.print("");
		if (status != OK) {
			// bail out
			System.err.println("*** Error constructing SortMerge");
			Runtime.getRuntime().exit(1);
		}

		nextNodeIterator = inl;
		System.out.println("Edge Join Node completed.");
		pageRead = PCounter.rcounter - pageRead;
		pageWrite = PCounter.wcounter - pageWrite;
		System.out.println("No of pages read: " + pageRead + "\nNo of pages written: " + pageWrite + "\n");

	}
}
