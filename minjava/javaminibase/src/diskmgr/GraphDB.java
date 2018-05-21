package diskmgr;

import java.io.IOException;

import zIndex.ZTreeFile;

import btree.AddFileEntryException;
import btree.BTreeFile;
import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.PinPageException;

import edgeheap.EdgeHeapfile;
import global.AttrType;
import global.GlobalConst;
import heap.FieldNumberOutOfBoundException;
import heap.InvalidTypeException;
import nodeheap.HFBufMgrException;
import nodeheap.HFDiskMgrException;
import nodeheap.HFException;
import nodeheap.InvalidSlotNumberException;
import nodeheap.InvalidTupleSizeException;
import nodeheap.NodeHeapfile;


public class GraphDB extends DB implements GlobalConst{
	private static String NODEFILENAME = "nodeheapfile";
	private static String EDGEFILENAME = "edgeheapfile";
	public NodeHeapfile nodeHeapfile;
	public EdgeHeapfile edgeHeapfile;
	public BTreeFile nodeLabelIndexFile;
	public ZTreeFile nodeDescriptorIndexFile;
	public BTreeFile edgeLabelIndexFile;
	public BTreeFile edgeWeightIndexFile;
	public BTreeFile edgeSourceIndexFile;
	public BTreeFile edgeDestinationIndexFile;

	public GraphDB(int type) {
		super();
	}
	
	public void openDB(String fname, int num_pgs)
			throws IOException, InvalidPageNumberException, FileIOException, DiskMgrException {
		super.openDB(fname, num_pgs);
		try {
			createFiles();
			createIndexFiles();
		} catch (Exception e) {
			throw new DiskMgrException(e, e.getMessage());
		}
	}
	
	public void openDB(String fname) throws InvalidPageNumberException, FileIOException, DiskMgrException, IOException{
		super.openDB(fname);
		try {
			createFiles();
			createIndexFiles();
		} catch (Exception e) {
			throw new DiskMgrException(e, e.getMessage());
		}
	}

	private void createFiles() throws HFException, HFBufMgrException, HFDiskMgrException, IOException, 
		GetFileEntryException, ConstructPageException, AddFileEntryException, edgeheap.HFException, 
		edgeheap.HFBufMgrException, edgeheap.HFDiskMgrException, PinPageException {
		this.nodeHeapfile = new NodeHeapfile(NODEFILENAME);
		this.edgeHeapfile = new EdgeHeapfile(EDGEFILENAME);
	}
	
	private void createIndexFiles() throws GetFileEntryException, ConstructPageException, AddFileEntryException, IOException, PinPageException {
		this.nodeLabelIndexFile = new BTreeFile("NodeLabel", AttrType.attrString, 44, 1);
		this.nodeDescriptorIndexFile = new ZTreeFile("NodeDescriptor",AttrType.attrDesc,20,1);
		this.edgeLabelIndexFile = new BTreeFile("EdgeLabel", AttrType.attrString, 44, 1);
		this.edgeWeightIndexFile = new BTreeFile("EdgeWeight", AttrType.attrInteger, 4, 0);
		this.edgeSourceIndexFile = new BTreeFile("EdgeSourceLabel", AttrType.attrString, 44, 1);
		this.edgeDestinationIndexFile = new BTreeFile("EdgeDestinationLabel", AttrType.attrString, 44, 1);
		
	}
	
	public int getNodeCnt() throws InvalidSlotNumberException, InvalidTupleSizeException, HFDiskMgrException, 
		HFBufMgrException, IOException, HFException, InvalidTypeException, heap.InvalidTupleSizeException{
		int iNodeCnt = 0;
		iNodeCnt = nodeHeapfile.getNodeCnt();
		return iNodeCnt;
	}

	public int getEdgeCnt() throws edgeheap.HFException, edgeheap.HFBufMgrException, edgeheap.HFDiskMgrException,
		IOException, edgeheap.InvalidSlotNumberException, edgeheap.InvalidTupleSizeException, InvalidTypeException, 
		heap.InvalidTupleSizeException{
		int iEdgeCnt = 0;
		iEdgeCnt = edgeHeapfile.getEdgeCnt();
		return iEdgeCnt;
		
	}

	public int getSourceCnt() throws edgeheap.HFException, edgeheap.HFBufMgrException, 
		edgeheap.HFDiskMgrException, IOException, edgeheap.InvalidSlotNumberException, 
		FieldNumberOutOfBoundException, edgeheap.InvalidTupleSizeException, 
		InvalidTypeException, heap.InvalidTupleSizeException {
		int iSourceCnt = 0;
		iSourceCnt = edgeHeapfile.getSourceCnt();
		return iSourceCnt;
	}

	public int getDestinationCnt() throws edgeheap.HFException, edgeheap.HFBufMgrException, 
		edgeheap.HFDiskMgrException, IOException, edgeheap.InvalidSlotNumberException, 
		FieldNumberOutOfBoundException, edgeheap.InvalidTupleSizeException, InvalidTypeException, 
		heap.InvalidTupleSizeException{
		int iDestCnt = 0;
		iDestCnt = edgeHeapfile.getDestinationCnt();
		return iDestCnt;
	}

	public int getLabelCnt() throws InvalidSlotNumberException, InvalidTupleSizeException, 
		HFDiskMgrException, HFBufMgrException, FieldNumberOutOfBoundException, edgeheap.HFBufMgrException, 
		edgeheap.InvalidSlotNumberException, edgeheap.InvalidTupleSizeException, IOException, 
		edgeheap.HFException, edgeheap.HFDiskMgrException, HFException, InvalidTypeException, 
		heap.InvalidTupleSizeException{
		int iLabelCnt =0;
		iLabelCnt = nodeHeapfile.getLabelCnt() + edgeHeapfile.getLabelCnt();
		return iLabelCnt;
	}
}
