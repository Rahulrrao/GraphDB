package global;

public class Descriptor {
	/**
	 * integer array to store the descriptor values
	 */
	int value[];

	
	/**
	 * Default constructor
	 */
	public Descriptor() {

		value = new int[5];
		value[0] = 0;
		value[1] = 0;
		value[2] = 0;
		value[3] = 0;
		value[4] = 0;
	}

	/**
	 * Setter method to set values of descriptor
	 * @param value0
	 * @param value1
	 * @param value2
	 * @param value3
	 * @param value4
	 */
	public void set(int value0, int value1, int value2, int value3, int value4) {
		value[0] = value0;
		value[1] = value1;
		value[2] = value2;
		value[3] = value3;
		value[4] = value4;
	}

	/**
	 * Given the index returns the value of the descriptor[index]
	 * @param idx
	 * @return value at that index
	 */
	public int get(int idx) {
		return value[idx];
	}

	/**
	 * To check if the passed descriptor is equal to the current descriptor
	 * @param desc
	 * @return 0 or 1, 1 if equal
	 */
	public double equal(Descriptor desc) {
		for (int i = 0; i < 5; i++) {
			if (this.value[i] != desc.value[i]) {
				return 0;
			}
		}
		return 1;
	}
	
	/** Returns distance between the descriptors
	 * @param desc
	 * @return
	 */
	public double distance(Descriptor desc) {
		double sum = 0;
		for (int i = 0; i < 5; i++) {
			sum += (this.value[i] - desc.value[i]) * (this.value[i] - desc.value[i]);
		}
		return Math.sqrt(sum);
	}
}

