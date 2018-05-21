package tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import btree.AddFileEntryException;
import btree.BTreeFile;
import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.IntegerKey;
import btree.PinPageException;
import btree.StringKey;
import edgeheap.*;
import edgeheap.FieldNumberOutOfBoundException;
import edgeheap.InvalidTupleSizeException;
import global.*;
import nodeheap.*;
import nodeheap.HFBufMgrException;
import nodeheap.HFDiskMgrException;
import nodeheap.HFException;
import nodeheap.InvalidSlotNumberException;
import zIndex.DescriptorKey;
import zIndex.ZTreeFile;

public class BatchNodeDelete implements GlobalConst{
    private String nodeLabel;
    
	
    public BatchNodeDelete() {
    	
    }
    
    
    public Edge getEdge(EID targetEid) throws edgeheap.InvalidTupleSizeException, IOException, FieldNumberOutOfBoundException, heap.FieldNumberOutOfBoundException {
        EScan escan = new EScan(SystemDefs.JavabaseDB.edgeHeapfile);
        EID eid = new EID();
        Edge e = escan.getNext(eid);
        while(e != null){
    		if(eid.equals(targetEid) ){
    			escan.closescan();
    			return e;
    		}
    		e = escan.getNext(eid);
    	}
        escan.closescan();
        return null;
    }
    
    public NID getNode(String nodeLabel) throws InvalidTupleSizeException, IOException, FieldNumberOutOfBoundException, nodeheap.InvalidTupleSizeException, heap.FieldNumberOutOfBoundException{
    	NScan nScan = new NScan(SystemDefs.JavabaseDB.nodeHeapfile);
    	NID nid = new NID();
        boolean done = true;
        while(done){
            Node n  = nScan.getNext(nid);
            if(n == null){
                done = false;
                
                break;
            }
            System.out.println(nodeLabel);
            if(n.getLabel().equals(nodeLabel)){
            	nScan.closescan();
                return nid;
            }
        }
        nScan.closescan();
        return null;
    }
    
    public EID getEdgeSourceDest(NID nid) throws InvalidTupleSizeException, IOException, heap.FieldNumberOutOfBoundException{
		EScan escan = new EScan(SystemDefs.JavabaseDB.edgeHeapfile);
		EID eid = new EID();
		Edge e  = escan.getNext(eid);
		while(e != null){
    		NID sourceNid = e.getSource();
    		NID destinationNid = e.getDestination();
    		if(sourceNid.equals(nid) || destinationNid.equals(nid)){
    			escan.closescan();
    			return eid;
    		}
    		e = escan.getNext(eid);
    	}
    	escan.closescan();
    	return null;
    }
    

    public void doSingleBatchNodeDelete(String line) throws InvalidSlotNumberException, HFException, HFBufMgrException, HFDiskMgrException, Exception{
        line = line.trim();
        String [] vals = new String[5];
        vals = line.split(" ");
        this.nodeLabel = vals[0];
    	NID nid = getNode(nodeLabel);
		if (nid == null)
			return;
    	
		Node node = new Node();
        EID eid = new EID();
        // loop and get all eid
        while(true){
        	eid = getEdgeSourceDest(nid);
        	if(eid == null){
        		break;
        	} else{
        		Edge e1 = getEdge(eid);
        		String targetLabel = e1.getLabel();
        		SystemDefs.JavabaseDB.edgeLabelIndexFile.Delete(new StringKey(targetLabel), eid);
               	SystemDefs.JavabaseDB.edgeWeightIndexFile.Delete(new IntegerKey(getEdge(eid).getWeight()), eid);
               	SystemDefs.JavabaseDB.edgeHeapfile.deleteEdge(eid);
        	}
        }
        SystemDefs.JavabaseDB.nodeHeapfile.deleteNode(nid);
        SystemDefs.JavabaseDB.nodeLabelIndexFile.Delete(new StringKey(node.getLabel()), nid);
        SystemDefs.JavabaseDB.nodeDescriptorIndexFile.Delete(new DescriptorKey(node.getDesc()), nid);
    }
}

