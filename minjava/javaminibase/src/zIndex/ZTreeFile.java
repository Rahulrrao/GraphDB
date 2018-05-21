package zIndex;

import btree.*;
import bufmgr.HashEntryNotFoundException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;

import global.Descriptor;
import global.NID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZTreeFile extends BTreeFile{

	public ZTreeFile(String filename, int keytype, int keysize, int delete_fashion ) throws GetFileEntryException, PinPageException, ConstructPageException, AddFileEntryException, IOException {
		super(filename, keytype,keysize,delete_fashion);
	}
	
	public ZTreeFile(String filename) throws GetFileEntryException, PinPageException, ConstructPageException, AddFileEntryException, IOException {
		super(filename);
	}

	public List<NID> zTreeFileScan() throws PinPageException, KeyNotMatchException, IteratorException, 
		IOException, ConstructPageException, UnpinPageException, ScanIteratorException,
		InvalidFrameNumberException, ReplacerException, PageUnpinnedException, HashEntryNotFoundException {
		BTFileScan scan = this.new_scan(null, null);

		List<NID> nidList = new ArrayList<NID>();
		KeyDataEntry entry = scan.get_next();
		LeafData leafData;
		while(entry != null) {
			NID nid = new NID();
			leafData = (LeafData) entry.data;
			nid.copyRid(leafData.getData());
			nidList.add(nid);
			entry = scan.get_next();
		}
		scan.DestroyBTreeFileScan();

		return nidList;
	}

	public  List<NID> zFileRangeScan(Descriptor key, int distance) throws PinPageException, KeyNotMatchException, IteratorException, IOException, ConstructPageException, UnpinPageException, ScanIteratorException, PageUnpinnedException, InvalidFrameNumberException, HashEntryNotFoundException, ReplacerException {
		ArrayList<Descriptor> retDescriptors = new ArrayList<Descriptor> ();
		//First find the lowerbound for the given key.
		int lowKeyVal[] = new int[5];
		List<NID> nidList = new ArrayList<NID>();

		lowKeyVal[0] = key.get(0) - distance;
		lowKeyVal[1] = key.get(1) - distance;
		lowKeyVal[2] = key.get(2) - distance;
		lowKeyVal[3] = key.get(3) - distance;
		lowKeyVal[4] = key.get(4) - distance;

		//Make sure that the lowKey does not cross 0;
		for(int i = 0;i<lowKeyVal.length;i++){
			if(lowKeyVal[i]<0){
				lowKeyVal[i] = 0;
			}
		}
		System.out.println("lowKeyVals are" + lowKeyVal[0] + " "+
				lowKeyVal[1]+" "+
				lowKeyVal[2]+ " "+
				lowKeyVal[3]+ " "+
				lowKeyVal[4]);
		int highKeyVal[] = new int[5];
		highKeyVal[0] = key.get(0) + distance;
		highKeyVal[1] = key.get(1) + distance;
		highKeyVal[2] = key.get(2) + distance;
		highKeyVal[3] = key.get(3) + distance;
		highKeyVal[4] = key.get(4) + distance;

		//Make sure that the highKey does not cross the maxInt
		for(int i = 0;i<highKeyVal.length;i++){
			if(highKeyVal[i]<0){
				highKeyVal[i] = Integer.MAX_VALUE;
			}
		}
		System.out.println("highKeyVal are" + highKeyVal[0] + " "+
				highKeyVal[1]+" "+
				highKeyVal[2]+ " "+
				highKeyVal[3]+ " "+
				highKeyVal[4]);

		Descriptor lowKey = new Descriptor();
		Descriptor highKey = new Descriptor();
		lowKey.set(lowKeyVal[0],lowKeyVal[1],lowKeyVal[2],lowKeyVal[3],lowKeyVal[4]);
		highKey.set(highKeyVal[0], highKeyVal[1], highKeyVal[2], highKeyVal[3], highKeyVal[4]);


		ArrayList<String> allPossible = new ArrayList<String>();

		System.out.println("all possible are ");
		for(int i = lowKeyVal[0]; i< highKeyVal[0];i++){
			for(int j = lowKeyVal[1]; j< highKeyVal[1];j++) {
				for(int k = lowKeyVal[2]; k< highKeyVal[2];k++) {
					for(int l = lowKeyVal[3]; l< highKeyVal[3];l++) {
						for(int m = lowKeyVal[4]; m< highKeyVal[4];m++) {
							//System.out.println(i+" "+ j+" "+ k+" "+l+" "+m);
							Descriptor desc = new Descriptor();
							desc.set(i,j,k,l,m);
							DescriptorKey descKey = new DescriptorKey(desc);
							String descStr = descKey.desc_str;
							allPossible.add(descStr);
							//System.out.println(" " + descStr);
						}
					}
				}
			}
		}


		//Sort all possible zorders
		Collections.sort(allPossible);
		for(int r =0; r< allPossible.size(); r++){
			int zorder = (int)(long)DescriptorKey.getZorder(allPossible.get(r));

			//System.out.print(zorder+ "  ");
		}
		int a = 0;
		//System.exit(1);
		String lowZorder = allPossible.get(0);
		int lZorder = (int)(long)DescriptorKey.getZorder(allPossible.get(0));
		String hiZorder = allPossible.get(0);
		while( a< allPossible.size()){
			hiZorder = allPossible.get(a);
			int zorder = (int)(long)DescriptorKey.getZorder(allPossible.get(a));
			//System.out.println("zorder is "+zorder);
			//Check if the next element exists
			//System.out.print(zorder+" ");

			if(a+1< allPossible.size()){
				//peek next
				int nextZorder = (int)(long)DescriptorKey.getZorder(allPossible.get(a+1));
				if(nextZorder != zorder+1){
					//System.out.println("lowZorder "+lZorder + "hiZorder "+zorder);
					DescriptorKey lowDescriptor = new DescriptorKey(lowZorder);
					DescriptorKey highDescriptor = new DescriptorKey(hiZorder);

					BTFileScan scan = this.new_scan(lowDescriptor, highDescriptor);

					KeyDataEntry entry = scan.get_next();
					LeafData leafData;
					while(entry != null) {
						NID nid = new NID();
						leafData = (LeafData) entry.data;
						nid.copyRid(leafData.getData());
						nidList.add(nid);
						entry = scan.get_next();
					}
					scan.DestroyBTreeFileScan();

					lowZorder = allPossible.get(a+1);
					lZorder = (int)(long)DescriptorKey.getZorder(allPossible.get(a+1));
				}
			}
			a+=1;
		}
		DescriptorKey lowDescriptor = new DescriptorKey(lowZorder);
		DescriptorKey highDescriptor = new DescriptorKey(hiZorder);

		BTFileScan scan = this.new_scan(lowDescriptor, highDescriptor);

		KeyDataEntry entry = scan.get_next();
		LeafData leafData;
		while(entry != null) {
			NID nid = new NID();
			leafData = (LeafData) entry.data;
			nid.copyRid(leafData.getData());
			nidList.add(nid);
			entry = scan.get_next();
		}
		scan.DestroyBTreeFileScan();

		return nidList;
	}

	public void main(String[] args) throws KeyNotMatchException, IteratorException, IOException,
		PinPageException, ConstructPageException, UnpinPageException, ScanIteratorException, 
		InvalidFrameNumberException, HashEntryNotFoundException, PageUnpinnedException, ReplacerException {
		Descriptor key = new Descriptor();
		key.set(5,4,3,7,3);
		zFileRangeScan(key, 4);
	}
}
