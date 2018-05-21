package tests;

import edgeheap.EScan;
import edgeheap.Edge;
import edgeheap.InvalidTupleSizeException;
import global.EID;
import global.NID;
import global.SystemDefs;
import heap.FieldNumberOutOfBoundException;
import nodeheap.NScan;
import nodeheap.Node;

import java.io.IOException;

import btree.IntegerKey;
import btree.StringKey;

/**
 * Created by revu on 3/10/17.
 */
public class BatchEdgeInsert {
boolean OK = true;
boolean FAIL = false;

    BatchEdgeInsert(){

    }
    public NID getNode(String nodeLabel) throws InvalidTupleSizeException, IOException, edgeheap.FieldNumberOutOfBoundException, nodeheap.InvalidTupleSizeException, heap.FieldNumberOutOfBoundException{
        NScan nScan = new NScan(SystemDefs.JavabaseDB.nodeHeapfile);
        NID nid = new NID();
        boolean done = true;
        while(done){
            Node n  = nScan.getNext(nid);
            if(n == null){
                done = false;
                break;
            }
            if(n.getLabel().equals(nodeLabel)){
                nScan.closescan();
                return nid;
            }
        }
        nScan.closescan();
        return null;
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


    public void doSingleBatchEdgInsert  (String sourceLabel, String destLabel, String edgeLabel, String edgeWeight) throws Exception {

    	boolean status = OK;
        EID eid ;//= getEdge(edgeLabel);
        Edge edge = new Edge();
        edge.setLabel(edgeLabel);
        NID sourceNID = getNode(sourceLabel);
        NID destNID = getNode(destLabel);

        
        edge.setSource(sourceNID);
        edge.setDestination(destNID);
        edge.setWeight(Integer.parseInt(edgeWeight));
        eid = SystemDefs.JavabaseDB.edgeHeapfile.insertEdge(edge.getEdgeByteArray());
		edge = SystemDefs.JavabaseDB.edgeHeapfile.getEdge(eid);
		edge.print();


        //SystemDefs.JavabaseDB.edgeHeapfile.insertEdge(edge.getEdgeByteArray());
        SystemDefs.JavabaseDB.edgeLabelIndexFile.insert(new StringKey(edge.getLabel()), eid);
        SystemDefs.JavabaseDB.edgeWeightIndexFile.insert(new IntegerKey(edge.getWeight()), eid);
        SystemDefs.JavabaseDB.edgeSourceIndexFile.insert(new StringKey(sourceLabel), eid);
        SystemDefs.JavabaseDB.edgeDestinationIndexFile.insert(new StringKey(destLabel), eid);
        

    }

}
