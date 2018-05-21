package iterator;

import global.AttrType;
import global.NID;
import global.RID;
import heap.FieldNumberOutOfBoundException;
import heap.Heapfile;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Scan;
import heap.Tuple;
import index.IndexException;

import java.io.IOException;

import nodeheap.NScan;
import nodeheap.Node;
import nodeheap.NodeHeapfile;

import bufmgr.PageNotReadException;

public class NodeScan extends Iterator {
	private AttrType[] _in1;
	private short in1_len;
	private short[] s_sizes;
	private NodeHeapfile f;
	private NScan scan;
	private Node tuple1;
	private Node Jtuple;
	private int t1_size;
	private int nOutFlds;
	private CondExpr[] OutputFilter;
	public FldSpec[] perm_mat;

	/**
	 * constructor
	 * 
	 * @param file_name
	 *            heapfile to be opened
	 * @param in1[]
	 *            array showing what the attributes of the input fields are.
	 * @param s1_sizes[]
	 *            shows the length of the string fields.
	 * @param len_in1
	 *            number of attributes in the input tuple
	 * @param n_out_flds
	 *            number of fields in the out tuple
	 * @param proj_list
	 *            shows what input fields go where in the output tuple
	 * @param outFilter
	 *            select expressions
	 * @exception IOException
	 *                some I/O fault
	 * @exception FileScanException
	 *                exception from this class
	 * @exception TupleUtilsException
	 *                exception from this class
	 * @exception InvalidRelation
	 *                invalid relation
	 * @throws InvalidTupleSizeException 
	 * @throws InvalidTypeException 
	 */
	public NodeScan(String file_name, AttrType in1[], short s1_sizes[], short len_in1, int n_out_flds,
			FldSpec[] proj_list, CondExpr[] outFilter)
			throws IOException, FileScanException, TupleUtilsException, InvalidRelation, InvalidTypeException, InvalidTupleSizeException {
		_in1 = in1;
		in1_len = len_in1;
		s_sizes = s1_sizes;

		Jtuple = new Node();
		AttrType[] Jtypes = new AttrType[n_out_flds];
		short[] ts_size;
		ts_size = TupleUtils.setup_op_tuple(Jtuple, Jtypes, in1, len_in1, s1_sizes, proj_list, n_out_flds);

		OutputFilter = outFilter;
		perm_mat = proj_list;
		nOutFlds = n_out_flds;
		tuple1 = new Node();

		try {
			tuple1.setHdr(in1_len, Jtypes, s1_sizes);
		} catch (Exception e) {
			throw new FileScanException(e, "setHdr() failed");
		}
		t1_size = tuple1.size();

		try {
			f = new NodeHeapfile(file_name);

		} catch (Exception e) {
			throw new FileScanException(e, "Create new heapfile failed");
		}

		try {
			scan = f.openScan();
		} catch (Exception e) {
			throw new FileScanException(e, "openScan() failed");
		}
	}

	/**
	 * @return shows what input fields go where in the output tuple
	 */
	public FldSpec[] show() {
		return perm_mat;
	}

	/**
	 * @return the result tuple
	 * @exception JoinsException
	 *                some join exception
	 * @exception IOException
	 *                I/O errors
	 * @exception InvalidTupleSizeException
	 *                invalid tuple size
	 * @exception InvalidTypeException
	 *                tuple type not valid
	 * @exception PageNotReadException
	 *                exception from lower layer
	 * @exception PredEvalException
	 *                exception from PredEval class
	 * @exception UnknowAttrType
	 *                attribute type unknown
	 * @exception FieldNumberOutOfBoundException
	 *                array out of bounds
	 * @exception WrongPermat
	 *                exception for wrong FldSpec argument
	 * @throws nodeheap.InvalidTupleSizeException 
	 */
	public Node get_next() throws JoinsException, IOException, InvalidTupleSizeException, InvalidTypeException,
			PageNotReadException, PredEvalException, UnknowAttrType, FieldNumberOutOfBoundException, WrongPermat, nodeheap.InvalidTupleSizeException {
		NID nid = new NID();
		

		while (true) {
			if ((tuple1 = scan.getNext(nid)) == null) {
				return null;
			}
			tuple1.setHdr(in1_len, _in1, s_sizes);
			if (PredEval.Eval(OutputFilter, tuple1, null, _in1, null) == true) {
				Projection.Project(tuple1, _in1, Jtuple, perm_mat, nOutFlds);
				return Jtuple;
			}			
		}
	}

	/**
	 * implement the abstract method close() from super class Iterator to finish
	 * cleaning up
	 */
	public void close() {

		if (!closeFlag) {
			scan.closescan();
			closeFlag = true;
		}
	}
}