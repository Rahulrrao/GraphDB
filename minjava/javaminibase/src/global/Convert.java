/* file Convert.java */

package global;

import java.io.*;
import java.lang.*;

public class Convert {

	/**
	 * read 4 bytes from given byte array at the specified position convert it
	 * to an integer
	 * 
	 * @param data
	 *            a byte array
	 * @param position
	 *            in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 * @return the integer
	 */
	public static int getIntValue(int position, byte[] data) throws java.io.IOException {
		InputStream in;
		DataInputStream instr;
		int value;
		byte tmp[] = new byte[4];

		// copy the value from data array out to a tmp byte array
		System.arraycopy(data, position, tmp, 0, 4);

		/*
		 * creates a new data input stream to read data from the specified input
		 * stream
		 */
		in = new ByteArrayInputStream(tmp);
		instr = new DataInputStream(in);
		value = instr.readInt();

		return value;
	}

	/**
	 * read 4 bytes from given byte array at the specified position convert it
	 * to a float value
	 * 
	 * @param data
	 *            a byte array
	 * @param position
	 *            in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 * @return the float value
	 */
	public static float getFloValue(int position, byte[] data) throws java.io.IOException {
		InputStream in;
		DataInputStream instr;
		float value;
		byte tmp[] = new byte[4];

		// copy the value from data array out to a tmp byte array
		System.arraycopy(data, position, tmp, 0, 4);

		/*
		 * creates a new data input stream to read data from the specified input
		 * stream
		 */
		in = new ByteArrayInputStream(tmp);
		instr = new DataInputStream(in);
		value = instr.readFloat();

		return value;
	}

	/**
	 * read 2 bytes from given byte array at the specified position convert it
	 * to a short integer
	 * 
	 * @param data
	 *            a byte array
	 * @param position
	 *            the position in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 * @return the short integer
	 */
	public static short getShortValue(int position, byte[] data) throws java.io.IOException {
		InputStream in;
		DataInputStream instr;
		short value;
		byte tmp[] = new byte[2];

		// copy the value from data array out to a tmp byte array
		System.arraycopy(data, position, tmp, 0, 2);

		/*
		 * creates a new data input stream to read data from the specified input
		 * stream
		 */
		in = new ByteArrayInputStream(tmp);
		instr = new DataInputStream(in);
		value = instr.readShort();

		return value;
	}

	/**
	 * reads a string that has been encoded using a modified UTF-8 format from
	 * the given byte array at the specified position
	 * 
	 * @param data
	 *            a byte array
	 * @param position
	 *            the position in data[]
	 * @param length
	 *            the length of the string in bytes (=strlength +2)
	 * @exception java.io.IOException
	 *                I/O errors
	 * @return the string
	 */
	public static String getStrValue(int position, byte[] data, int length) throws java.io.IOException {
		InputStream in;
		DataInputStream instr;
		String value;
		byte tmp[] = new byte[length];

		// copy the value from data array out to a tmp byte array
		System.arraycopy(data, position, tmp, 0, length);

		/*
		 * creates a new data input stream to read data from the specified input
		 * stream
		 */
		in = new ByteArrayInputStream(tmp);
		instr = new DataInputStream(in);
		value = instr.readUTF();
		return value;
	}

	/**
	 * reads 2 bytes from the given byte array at the specified position convert
	 * it to a character
	 * 
	 * @param data
	 *            a byte array
	 * @param position
	 *            the position in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 * @return the character
	 */
	public static char getCharValue(int position, byte[] data) throws java.io.IOException {
		InputStream in;
		DataInputStream instr;
		char value;
		byte tmp[] = new byte[2];
		// copy the value from data array out to a tmp byte array
		System.arraycopy(data, position, tmp, 0, 2);

		/*
		 * creates a new data input stream to read data from the specified input
		 * stream
		 */
		in = new ByteArrayInputStream(tmp);
		instr = new DataInputStream(in);
		value = instr.readChar();
		return value;
	}


	/**
	 * reads 4 Integer bytes from the given byte array at the specified position
	 * convert it to a RID object
	 *
	 * @param data
	 *            a byte array
	 * @param position
	 *            the position in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 * @return the RID object
	 */
	public static RID getRIDValue (int position, byte[] data) throws java.io.IOException {
		InputStream in;
		DataInputStream instr;
		int value[];
		value = new int[2];

		for (int i = 0; i < 2; i++) {

			byte tmp[] = new byte[4];
			System.arraycopy(data, position, tmp, 0, 4);
			/*
			 * creates a new data input stream to read data from the specified
			 * input stream
			 */
			in = new ByteArrayInputStream(tmp);
			instr = new DataInputStream(in);
			value[i] = instr.readInt();

		}
		int pageNo = value[0];
		int slotNo = value[1];
		PageId page = new PageId(pageNo);
		RID retRID = new RID(page, slotNo);

		//retDesc.set(value[0], value[1], value[2], value[3], value[4]);
		return retRID;

	}




	/**
	 * reads 4 Integer bytes from the given byte array at the specified position
	 * convert it to a Descriptor Object
	 * 
	 * @param data
	 *            a byte array
	 * @param position
	 *            the position in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 * @return the Descriptor object
	 */
	public static Descriptor getDescValue(int position, byte[] data) throws java.io.IOException {
		InputStream in;
		DataInputStream instr;
		Descriptor retDesc = new Descriptor();
		int value[];
		value = new int[5];

		for (int i = 0; i < 5; i++) {

			byte tmp[] = new byte[4];
			System.arraycopy(data, position, tmp, 0 , 4);
			/*
			 * creates a new data input stream to read data from the specified
			 * input stream
			 */
			in = new ByteArrayInputStream(tmp);
			instr = new DataInputStream(in);
			value[i] = instr.readInt();
			position += 4;

		}
		retDesc.set(value[0], value[1], value[2], value[3], value[4]);
		return retDesc;

	}

	/**
	 * update a Descriptor value in the given byte array at the specified position
	 * 
	 * @param data
	 *            a byte array
	 * @param descValue
	 *            the value to be copied into the data[]
	 * @param position
	 *            the position of that value in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 */
	public static void setDescValue(Descriptor descValue, int position, byte[] data) throws java.io.IOException {
		/*
		 * creates a new data output stream to write data to underlying output
		 * stream
		 */

		OutputStream out = new ByteArrayOutputStream();
		DataOutputStream outstr = new DataOutputStream(out);

		for (int i = 0; i < 5; i++) {
			int value = descValue.get(i);

			// write the value to the output stream
			outstr.writeInt(value);

			// creates a byte array with this output stream size and the
			// valid contents of the buffer have been copied into it
			byte[] B = ((ByteArrayOutputStream) out).toByteArray();

			if(i == 4){
			// copies the first 4 bytes of this byte array into data[]
				System.arraycopy(B, 0, data, position, 20);
			}
			//position += 4;

		}

	}



	/**
	 * update a RID value in the given byte array at the specified position
	 *
	 * @param data
	 *            a byte array
	 * @param rid
	 *            the RID to be copied into the data[]
	 * @param position
	 *            the position of that value in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 */
	public static void setRIDValue(RID rid, int position, byte[] data) throws java.io.IOException {
		/*
		 * creates a new data output stream to write data to underlying output
		 * stream
		 */

		OutputStream out = new ByteArrayOutputStream();
		DataOutputStream outstr = new DataOutputStream(out);

		int pageNo = rid.pageNo.pid;
		int slotNo = rid.slotNo;

		// write the value to the output stream
		outstr.writeInt(pageNo);

		// creates a byte array with this output stream size and the
		// valid contents of the buffer have been copied into it
		byte[] B = ((ByteArrayOutputStream) out).toByteArray();

		// copies the first 4 bytes of this byte array into data[]
		System.arraycopy(B, 0, data, position, 4);
		position += 4;

		// write the value to the output stream
		outstr.writeInt(slotNo);

		// creates a byte array with this output stream size and the
		// valid contents of the buffer have been copied into it
		byte[] B1 = ((ByteArrayOutputStream) out).toByteArray();

		// copies the first 4 bytes of this byte array into data[]
		System.arraycopy(B1, 0, data, position, 4);
		position += 4;



	}


	/**
	 * update an integer value in the given byte array at the specified position
	 * 
	 * @param data
	 *            a byte array
	 * @param value
	 *            the value to be copied into the data[]
	 * @param position
	 *            the position of tht value in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 */
	public static void setIntValue(int value, int position, byte[] data) throws java.io.IOException {
		/*
		 * creates a new data output stream to write data to underlying output
		 * stream
		 */

		OutputStream out = new ByteArrayOutputStream();
		DataOutputStream outstr = new DataOutputStream(out);

		// write the value to the output stream

		outstr.writeInt(value);

		// creates a byte array with this output stream size and the
		// valid contents of the buffer have been copied into it
		byte[] B = ((ByteArrayOutputStream) out).toByteArray();

		// copies the first 4 bytes of this byte array into data[]
		System.arraycopy(B, 0, data, position, 4);

	}

	/**
	 * update a float value in the given byte array at the specified position
	 * 
	 * @param data
	 *            a byte array
	 * @param value
	 *            the value to be copied into the data[]
	 * @param position
	 *            the position of tht value in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 */
	public static void setFloValue(float value, int position, byte[] data) throws java.io.IOException {
		/*
		 * creates a new data output stream to write data to underlying output
		 * stream
		 */

		OutputStream out = new ByteArrayOutputStream();
		DataOutputStream outstr = new DataOutputStream(out);

		// write the value to the output stream

		outstr.writeFloat(value);

		// creates a byte array with this output stream size and the
		// valid contents of the buffer have been copied into it
		byte[] B = ((ByteArrayOutputStream) out).toByteArray();

		// copies the first 4 bytes of this byte array into data[]
		System.arraycopy(B, 0, data, position, 4);

	}

	/**
	 * update a short integer in the given byte array at the specified position
	 * 
	 * @param data
	 *            a byte array
	 * @param value
	 *            the value to be copied into data[]
	 * @param position
	 *            the position of tht value in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 */
	public static void setShortValue(short value, int position, byte[] data) throws java.io.IOException {
		/*
		 * creates a new data output stream to write data to underlying output
		 * stream
		 */

		OutputStream out = new ByteArrayOutputStream();
		DataOutputStream outstr = new DataOutputStream(out);

		// write the value to the output stream

		outstr.writeShort(value);

		// creates a byte array with this output stream size and the
		// valid contents of the buffer have been copied into it
		byte[] B = ((ByteArrayOutputStream) out).toByteArray();

		// copies the first 2 bytes of this byte array into data[]
		System.arraycopy(B, 0, data, position, 2);

	}

	/**
	 * Insert or update a string in the given byte array at the specified
	 * position.
	 * 
	 * @param data
	 *            a byte array
	 * @param value
	 *            the value to be copied into data[]
	 * @param position
	 *            the position of tht value in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 */
	public static void setStrValue(String value, int position, byte[] data) throws java.io.IOException {
		/*
		 * creates a new data output stream to write data to underlying output
		 * stream
		 */

		OutputStream out = new ByteArrayOutputStream();
		DataOutputStream outstr = new DataOutputStream(out);

		// write the value to the output stream

		outstr.writeUTF(value);
		// creates a byte array with this output stream size and the
		// valid contents of the buffer have been copied into it
		byte[] B = ((ByteArrayOutputStream) out).toByteArray();

		int sz = outstr.size();
		// copies the contents of this byte array into data[]
		System.arraycopy(B, 0, data, position, sz);

	}

	/**
	 * Update a character in the given byte array at the specified position.
	 * 
	 * @param data
	 *            a byte array
	 * @param value
	 *            the value to be copied into data[]
	 * @param position
	 *            the position of tht value in data[]
	 * @exception java.io.IOException
	 *                I/O errors
	 */
	public static void setCharValue(char value, int position, byte[] data) throws java.io.IOException {
		/*
		 * creates a new data output stream to write data to underlying output
		 * stream
		 */

		OutputStream out = new ByteArrayOutputStream();
		DataOutputStream outstr = new DataOutputStream(out);

		// write the value to the output stream
		outstr.writeChar(value);

		// creates a byte array with this output stream size and the
		// valid contents of the buffer have been copied into it
		byte[] B = ((ByteArrayOutputStream) out).toByteArray();

		// copies contents of this byte array into data[]
		System.arraycopy(B, 0, data, position, 2);

	}
}
