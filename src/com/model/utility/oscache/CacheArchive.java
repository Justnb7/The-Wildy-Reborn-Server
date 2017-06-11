package com.model.utility.oscache;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.Inflater;

/**
 * 
 * @author Richard(Flamable)
 */
public class CacheArchive {

	private final int index;
	private final int id;
	private int compression;
	private byte[] buffer;
	private int size;
	private byte[][] childData;
	private ReferenceTable refTable;

	public CacheArchive(int index, int id, ByteBuffer buffer, ReferenceTable refTable) {
		this.index = index;
		this.id = id;
		this.refTable = refTable;
		this.compression = buffer.get();
		int compressedSize = buffer.getInt();
		this.size = compression == 0 ? compressedSize : buffer.getInt();
		this.buffer = new byte[compressedSize];
		buffer.get(this.buffer);
	}

	public byte[] getData() {
		try {
			if (compression == 0) {
				return buffer;
			}
			if (size < 0 || size > 2000000)
				return null;
			byte[] result = new byte[size];
			if (compression == 1) {
				DataInputStream stream = new DataInputStream(new BZip2CompressorInputStream(new ByteArrayInputStream(buffer)));
				stream.readFully(result);
				stream.close();
			} else {
				if (buffer[0] != 31 || buffer[1] != -117) {
					throw new IOException("Header mismatch Index:"+index+" Archive: "+id+" ~ "+Arrays.toString(buffer));
				}
				Inflater inFlatter = new Inflater(true);
				inFlatter.setInput(buffer, 10, buffer.length - 10);
				inFlatter.inflate(result);
				inFlatter.end();
			}
			return result;
		} catch (Exception ioex) {
			return null;
		}
	}
	
	public byte[] getCompressed() {
		try {
			ByteBuffer bb = ByteBuffer.allocate(buffer.length + (compression == 0 ? 5 : 9));
			bb.put((byte) compression);
			bb.putInt(buffer.length);
			if (compression != 0)
				bb.putInt(size);
			bb.put(buffer);
			
			bb.rewind();
			byte[] result = new byte[bb.capacity()];
			bb.get(result);
			return result;
		} catch (Exception ioex) {
			ioex.printStackTrace();
			return null;
		}
	}
	
	public byte[] getChildData(int childID) {
		try {
			if (childData != null) {
				return childData.length <= childID ? null : childData[childID];
			}

			final int childsize = refTable.getArchiveInfomation()[id].getArchiveEntryInfomation().length;
			childData = new byte[childsize][];

			ArchiveInfomation archieveFit = refTable.getArchiveInfomation()[id];

			final int activeEntryCount = archieveFit.getActiveEntryCount();

			byte[] rawData = getData();
			ByteBuffer buffer = ByteBuffer.wrap(rawData);
			if (activeEntryCount > 1) {
				int length = rawData.length;
				int verification = rawData[--length] & 0xff;
				length -= verification * (activeEntryCount * 4);
				buffer.position(length);

				int[] outOffset = new int[activeEntryCount];
				for (int i = 0; verification > i; i++) {
					int offset = 0;
					for (int count = 0; count < activeEntryCount; count++) {
						offset += buffer.getInt();
						outOffset[count] += offset;
					}
				}

				byte[][] newData = new byte[activeEntryCount][];
				for (int i = 0; i < activeEntryCount; i++) {
					newData[i] = new byte[outOffset[i]];
					outOffset[i] = 0;
				}
				int readPos = 0;
				buffer.position(length);
				for (int i = 0; i < verification; i++) {
					int offset = 0;
					for (int id = 0; id < activeEntryCount; id++) {
						offset += buffer.getInt();
						System.arraycopy(rawData, readPos, newData[id], outOffset[id], offset);
						readPos += offset;
						outOffset[id] += offset;
					}
				}
				int aid = 0;
				for (int i = 0; i < childsize; i++) {
					if (archieveFit.getArchiveEntryInfomation()[i] != null) {
						childData[i] = newData[aid++];
					}
				}
			} else {
				childData[0] = rawData;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return childData[childID];
	}

	public int getIndex() {
		return index;
	}

	public int getId() {
		return id;
	}

}
