package zIndex;

import btree.KeyClass;
import global.Descriptor;

public class DescriptorKey extends KeyClass{

	private Descriptor key;
	
	public DescriptorKey(Descriptor desc){
		this.key = desc;
		desc_str = this.getZVal(desc);
	}

	public DescriptorKey(String descStr){
		this.desc_str = descStr;
		this.key = this.getDescKey(descStr);
	}
	public String desc_str;
	public Long zorder;

	public static Long getZorder(String zString){
		int index = 0;
		Long currVal = (long)0;
		for(int j = 0; j<zString.length(); j++){
			currVal = currVal*2;
			if(zString.charAt(j) == '1'){
				currVal += 1;
			}
		}
		//value[i] = currVal;
		return currVal;

	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append(key.get(0));
		result.append(key.get(1));
		result.append(key.get(2));
		result.append(key.get(3));
		result.append(key.get(4));
		return result.toString();
	}

	public Descriptor getKey() {
		return key;
	}
	
	public String getDescString() {
		return desc_str;
	}

	public void setKey(Descriptor key) {
		this.key = key;
	}


	public static Descriptor getDescKey(String descStr){
		Descriptor constKey = new Descriptor();
		StringBuilder[] val = new StringBuilder[5];
		for (int i = 0; i < val.length; i++) {
			val[i] = new StringBuilder("");
		}
		for(int i = 0; i < 32; i++) {
			val[0].append(descStr.charAt(i*5));
			val[1].append(descStr.charAt(i*5+1));
			val[2].append(descStr.charAt(i*5+2));
			val[3].append(descStr.charAt(i*5+3));
			val[4].append(descStr.charAt(i*5+4));
		}

		int value[] = new int[5];

		for(int i=0; i< val.length; i++){
			value[i] = (int)(long)getZorder(val[i].toString());
		}
		constKey.set(value[0], value[1], value[2], value[3], value[4]);
		return constKey;
	}
	
	public static String getZVal(Descriptor desc){
		StringBuilder result = new StringBuilder();
		
		int val0 = desc.get(0);
		int val1 = desc.get(1);
		int val2 = desc.get(2);
		int val3 = desc.get(3);
		int val4 = desc.get(4);
		
		for(int i = 0; i < 32; i++){
			//TODO take out prefix of 0's.
			result.append(val4%2);
			result.append(val3%2);
			result.append(val2%2);
			result.append(val1%2);
			result.append(val0%2);
			
			val0 /= 2;
			val1 /= 2;
			val2 /= 2;
			val3 /= 2;
			val4 /= 2;
		}
		
		return result.reverse().toString();
		
	}
}