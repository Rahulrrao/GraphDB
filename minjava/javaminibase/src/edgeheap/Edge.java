/* File Edge.java */

package edgeheap;
import heap.FieldNumberOutOfBoundException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Tuple;
import java.io.*;
import java.lang.*;
import global.*;

public class Edge extends Tuple {

    /**
     * Maximum size of any Edge
     */
    public static final int max_size = 82;

    /**
     * Class constructor Create a new Edge with length = max_size,Edge offset =
     * 0.
     * @throws IOException 
     * @throws InvalidTupleSizeException 
     * @throws InvalidTypeException 
     */

    public Edge() throws InvalidTypeException, InvalidTupleSizeException, IOException {
        // Creat a new tuple
    	super(max_size);
        setEdgeHdr();
    }

    /**
     * Constructor
     *
     *  anode
     *            a byte array which contains the Edge
     * @param offset
     *            the offset of the Edge in the byte array
     *
     *            the length of the Edge
     * @throws IOException 
     * @throws InvalidTupleSizeException 
     * @throws InvalidTypeException 
     */

    public Edge(byte[] aedge, int offset) throws InvalidTypeException, InvalidTupleSizeException, IOException {
    	super(aedge, offset, max_size);
        setEdgeHdr();
        // fldCnt = getShortValue(offset, data);
    }
    
    public Edge(byte[] aedge, int offset, int size) throws InvalidTypeException, InvalidTupleSizeException, IOException {
    	super(aedge, offset, size);
        if(size == 82) setEdgeHdr();
        // fldCnt = getShortValue(offset, data);
    }
    
    private void setEdgeHdr() throws InvalidTypeException, InvalidTupleSizeException, IOException{
        AttrType[] attrs = new AttrType[6];
        short[] str_sizes = new short[1];
        attrs[0] = new AttrType(AttrType.attrString);
        attrs[1] = new AttrType(AttrType.attrInteger); //source pg no.
        attrs[2] = new AttrType(AttrType.attrInteger);//source slot no.
        attrs[3] = new AttrType(AttrType.attrInteger);//dest pg no.
        attrs[4] = new AttrType(AttrType.attrInteger);//dest slot no.
        attrs[5] = new AttrType(AttrType.attrInteger);
        str_sizes[0] = (short)44;
        this.setHdr((short)6, attrs, str_sizes);
    }

    /**
     * Constructor(used as Edge copy)
     *
     *  fromNode
     *            a byte array which contains the Edge
     *
     */
    public Edge(Edge fromEdge) {
    	super(fromEdge);
    }

    /**
     * Class constructor Creat a new Edge with length = size,Edge offset = 0.
     */
//
//    public Node(int size) {
//        // Creat a new tuple
//        data = new byte[size];
//        edge_offset = 0;
//        edge_length = size;
//    }

    /**
     * Copy a tuple to the current Edge position you must make sure the Edge
     * lengths must be equal
     *
     *  fromNode
     *            the tuple being copied
     */
    public void edgeCopy(Edge fromEdge) {
    	tupleCopy(fromEdge);
    }

    /**
     * This is used when you don't want to use the constructor
     *
     *  anode
     *            a byte array which contains the Edge
     * @param offset
     *            the offset of the tuple in the byte array
     *
     *            the length of the tuple
     */

    public void edgeInit(byte[] aedge, int offset) {
    	tupleInit(aedge, offset, max_size);
    }

    /**
     * Set an edge with the given edge length and offset
     *
     * @param fromEdge
     *            a byte array contains the edge
     * @param offset
     *            the offset of the edge ( =0 by default)
     *
     *            the length of the edge
     */
    public void edgeSet(byte[] fromEdge, int offset) {
        tupleSet(fromEdge, offset, max_size);
    }


    /**
     * Copy the edge byte array out
     *
     * @return byte[], a byte array contains the edge the length of byte[] =
     *         length of the edge
     */

    public byte[] getEdgeByteArray() {
        return getTupleByteArray();
    }

    /**
     * return the data byte array
     *
     * @return data byte array
     */
//
//    public byte[] returnTupleByteArray() {
//        return data;
//    }


    /**
     * To get the Label of the Edge
     * @return String Label
     * @throws IOException
     * @throws heap.FieldNumberOutOfBoundException
     */
    public String getLabel() throws IOException, heap.FieldNumberOutOfBoundException{
        return getStrFld(1);
    }
    
    /**
     * To get the source node of the edge
     * @return Source NID
     * @throws IOException
     * @throws heap.FieldNumberOutOfBoundException
     */
    public NID getSource() throws IOException, heap.FieldNumberOutOfBoundException {
         int pid = getIntFld(2);
         int slotno = getIntFld(3);
         return new NID(new PageId(pid), slotno);
    }

    /**
     * To get the Destination node of the edge
     * @return Destination NID
     * @throws IOException
     * @throws heap.FieldNumberOutOfBoundException
     */
    public NID getDestination() throws IOException, heap.FieldNumberOutOfBoundException {
        int pid = getIntFld(4);
        int slotno = getIntFld(5);
        return new NID(new PageId(pid), slotno);    }
    
    /**
     * To get the weight of the edge
     * @return weight integer
     * @throws IOException
     * @throws heap.FieldNumberOutOfBoundException
     */
    public int getWeight() throws IOException, heap.FieldNumberOutOfBoundException{
        return getIntFld(6);
    }



	/**
	 * To get the NID
	 * @param fldNo
	 * @return NID
	 * @throws IOException
	 * @throws FieldNumberOutOfBoundException
	 */
	private NID getNIDFld(int fldNo) throws IOException, FieldNumberOutOfBoundException {
		NID val = new NID();
		RID rid = getRIDFld(fldNo);
		val.pageNo = rid.pageNo;
		val.slotNo = rid.slotNo;
		return val;
	}


    /**
     * To set the label of the edge
     * @param val
     * @return Edge
     * @throws IOException
     * @throws heap.FieldNumberOutOfBoundException
     */
    public Edge setLabel(String val) throws IOException, heap.FieldNumberOutOfBoundException {
        return (Edge)setStrFld(1, val);
    }
    /**
     * To set the source of the edge
     * @param sourceID
     * @return Edge
     * @throws IOException
     * @throws heap.FieldNumberOutOfBoundException
     */
    public Edge setSource(NID sourceID) throws IOException, heap.FieldNumberOutOfBoundException {
    	setIntFld(2, sourceID.pageNo.pid);
    	return (Edge)setIntFld(3,sourceID.slotNo);
    }
    /**
     * To set the Destination of the edge
     * @param destID
     * @return Edge
     * @throws IOException
     * @throws heap.FieldNumberOutOfBoundException
     */
    public Edge setDestination(NID destID) throws IOException, heap.FieldNumberOutOfBoundException {
    	setIntFld(4, destID.pageNo.pid);
    	return (Edge)setIntFld(5,destID.slotNo);    }
    /**
     * To set the weight of the edge
     * @param val
     * @return Edge
     * @throws IOException
     * @throws heap.FieldNumberOutOfBoundException
     */
    public Edge setWeight(int val) throws IOException, heap.FieldNumberOutOfBoundException {
        return (Edge)setIntFld(6, val);
    }



    /**
     * To print the contents of the Edge object
     * @throws IOException
     * @throws heap.FieldNumberOutOfBoundException
     */
    public void print() throws IOException, heap.FieldNumberOutOfBoundException {
        System.out.println("label is "+ getLabel());
        System.out.println("Weight is : "+ getWeight());
        NID source = getSource();
        NID dest = getDestination();
        System.out.println("Source pid: " + source.pageNo + ", slotnum "+ source.slotNo);
        System.out.println("Dest pid: " + dest.pageNo + ", slotnum "+ dest.slotNo);
        System.out.println("\n");
    }
}
