package tests;

import edgeheap.EScan;
import edgeheap.Edge;
import edgeheap.EdgeHeapfile;
import global.EID;
import global.NID;
import global.SystemDefs;
import heap.FieldNumberOutOfBoundException;
import heap.InvalidTupleSizeException;
import heap.Scan;
import nodeheap.*;

import java.io.IOException;

import btree.IntegerKey;
import btree.StringKey;

/**
 * Created by revu on 3/10/17.
 */
public class BatchEdgeDelete {

    BatchEdgeDelete(){

    }

    public EID getEdge(String edgeLabel) throws edgeheap.InvalidTupleSizeException, IOException, FieldNumberOutOfBoundException {
        EScan escan = new EScan(SystemDefs.JavabaseDB.edgeHeapfile);
        EID eid = new EID();
        boolean done = true;
        while(done){
            Edge e  = escan.getNext(eid);
            if(e == null){
                done = false;
                escan.closescan();
                break;
            }
            if(e.getLabel().equals(edgeLabel)){
            	escan.closescan();
                return eid;
            }
        }
        return null;
    }

    //Delete the edge from the edge heap file
    //Delete the edge labels and edge weights from index files 
    public void doSingleBatchEdgeDelete  (String sourceLabel, String destLabel, String edgeLabel) throws Exception {
        
    	//scan the edge heap file and get the eid
    	EID eid = getEdge(edgeLabel);
        if(eid != null){
	        Edge edge = SystemDefs.JavabaseDB.edgeHeapfile.getEdge(eid);
	        if (edge == null){
	            System.out.println("We did not find this "+edgeLabel);
	        }
	        NID sourceNID = edge.getSource();
	        String edgeSource = SystemDefs.JavabaseDB.nodeHeapfile.getNode(sourceNID).getLabel();
	        NID destNID = edge.getDestination();
	        String edgeDest = SystemDefs.JavabaseDB.nodeHeapfile.getNode(destNID).getLabel();
	        if(edgeSource.equals(sourceLabel) && edgeDest.equals(destLabel)){
				SystemDefs.JavabaseDB.edgeLabelIndexFile.Delete(new StringKey(edge.getLabel()), eid);
				SystemDefs.JavabaseDB.edgeWeightIndexFile.Delete(new IntegerKey(edge.getWeight()), eid);
				SystemDefs.JavabaseDB.edgeSourceIndexFile.Delete(new StringKey(sourceLabel), eid);
	            SystemDefs.JavabaseDB.edgeDestinationIndexFile.Delete(new StringKey(destLabel), eid);
				SystemDefs.JavabaseDB.edgeHeapfile.deleteEdge(eid);
	        }
	    } else{
	    	System.out.println("Edge not found");
	    }
    }
}
