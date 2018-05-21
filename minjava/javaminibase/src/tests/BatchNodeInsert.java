package tests;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import btree.AddFileEntryException;
import btree.BT;
import btree.BTreeFile;
import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.PinPageException;
import btree.StringKey;
import global.AttrType;
import global.Convert;
import global.Descriptor;
import global.NID;
import global.SystemDefs;
import heap.FieldNumberOutOfBoundException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import diskmgr.GraphDB;
import nodeheap.Node;
import nodeheap.NodeHeapfile;
import zIndex.DescriptorKey;
import zIndex.ZTreeFile;

class DummyNode extends Node {

	// content of the node
	public String label;
	public Descriptor desc;

	// length under control
	private int reclen;

	private byte[] data;

	/**
	 * Default constructor
	 */
	public DummyNode() throws InvalidTypeException, InvalidTupleSizeException, IOException {
	}

	/**
	 * another constructor
	 */
	public DummyNode(int _reclen) throws InvalidTypeException, InvalidTupleSizeException, IOException {
		setRecLen(_reclen);
		data = new byte[_reclen];
	}

	/**
	 * convert this class objcet to a byte array this is used when you want to
	 * write this object to a byte array
	 */
	public byte[] toByteArray() throws java.io.IOException {
		Convert.setStrValue(label, 0, data);
		Convert.setDescValue(desc, 44, data);
		return data;
	}


	// Other access methods to the size of the String field and
	// the size of the record
	public void setRecLen(int size) {
		reclen = size;
	}

	public int getRecLength() {
		return reclen;
	}
}

public class BatchNodeInsert {
	
	public BatchNodeInsert() {
		
	}
	private final static int reclen = 74;
	private final static boolean OK = true;
	private final static boolean FAIL = false;
	
	/**
	 * Insert
	 * 
	 */
	
	public void doSingleBatchNodeInsert(String line, NodeHeapfile nhf, GraphDB db) throws InvalidTypeException, InvalidTupleSizeException, IOException, FieldNumberOutOfBoundException{
		boolean status = OK;
		String [] vals = new String[5];
		Descriptor temp_desc = new Descriptor();
				
		line = line.trim();
		vals = line.split(" ");
		temp_desc.set(Integer.parseInt(vals[1]),Integer.parseInt(vals[2]),Integer.parseInt(vals[3]),Integer.parseInt(vals[4]),Integer.parseInt(vals[5]));
		Node node = new Node();
		node.setLabel(vals[0]);
		node.setDesc(temp_desc);
		try {
			
			NID nid = nhf.insertNode(node.getNodeByteArray());
			
			Node node2 = new Node();
			node2 = nhf.getNode(nid);
			node2.print();
			
			SystemDefs.JavabaseDB.nodeLabelIndexFile.insert(new StringKey(node2.getLabel()), nid);
			SystemDefs.JavabaseDB.nodeDescriptorIndexFile.insert(new DescriptorKey(node2.getDesc()), nid);
			// TODO: No. of pages read/written
		} catch (Exception e) {
			status = FAIL;
			System.err.println("*** Error inserting node " + vals[0] + "\n");
			e.printStackTrace();
		}
	}
	
}
	
				
	
