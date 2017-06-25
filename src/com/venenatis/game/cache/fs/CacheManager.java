package com.venenatis.game.cache.fs;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import com.venenatis.game.cache.io.XTEADecipher;

/**
 * 
 * @author Richard(Flamable)
 */
public class CacheManager {

	private CacheFile reference;
	private CacheFile[] cacheFiles;
	private ReferenceTable[] referenceTables;
	private ByteBuffer refTable;

	public CacheManager(String dir) {
		try {
			try {
				RandomAccessFile dataFile = new RandomAccessFile(dir+"/main_file_cache.dat2", "rw");
				RandomAccessFile referenceFile = new RandomAccessFile(dir+"/main_file_cache.idx255", "rw");
				setReference(new CacheFile(255, dataFile.getChannel(), referenceFile.getChannel(), 0x7a120));

				final int count = (int) (referenceFile.getChannel().size() / 6);
				cacheFiles = new CacheFile[count];
				for (int i = 0; i < count; i++) {
					RandomAccessFile indexFile = new RandomAccessFile(dir+"/main_file_cache.idx" + i, "rw");
					cacheFiles[i] = new CacheFile(i, dataFile.getChannel(), indexFile.getChannel(), 0xf4240);
				}

				referenceTables = new ReferenceTable[cacheFiles.length];
				for (int i = 0; i < referenceTables.length; i++) {
					referenceTables[i] = new ReferenceTable(i, getArchive(255, i));
					referenceTables[i].decode();
				}
			} finally {
				setRefTable(generateRefTable());
			}
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	public ByteBuffer generateRefTable() {
		boolean version = true;
		CRC32 crc = new CRC32();
		ByteBuffer bb = ByteBuffer.allocate(cacheFiles.length * (version ? 8 : 4) + 5);
		bb.put((byte) 0)
		.putInt(cacheFiles.length * (version ? 8 : 4));
		for(int i = 0; i < cacheFiles.length; i++) {
			ByteBuffer refbb = reference.get(i);
			crc.update(refbb.array());
			bb.putInt((int) crc.getValue());
			if (version)
			bb.putInt(referenceTables[i].getRevision());//todo get versions from table
			crc.reset();
		}
		return bb;
	}

	public CacheArchive getArchive(int index, int id) {
		final CacheFile cacheFile = index == 255 ? reference : cacheFiles[index];
		ByteBuffer bb = cacheFile.get(id);
		if (bb == null)
			return null;
		ReferenceTable refTable = null;
		if (index != 255) {
			refTable = referenceTables[index];
		}
		return new CacheArchive(index, id, bb, refTable);
	}

	public CacheArchive getArchive(int index, int id, int[] keys) {
		final CacheFile cacheFile = index == 255 ? reference : cacheFiles[index];
		ByteBuffer bb = cacheFile.get(id);
		if (bb == null)
			return null;
		ReferenceTable refTable = null;
		if (index != 255) {
			refTable = referenceTables[index];
		}
		if (keys != null && keys[0] != 0 && keys[1] != 0 && keys[2] != 0 && keys[3] != 0) {
			byte[] bytes = new byte[bb.limit()];
			bb.get(bytes);
			bytes = new XTEADecipher(keys, bytes, 5).decipher();
			return new CacheArchive(index, id, ByteBuffer.wrap(bytes), refTable);
		}
		return new CacheArchive(index, id, bb, refTable);
	}

	public int getCount(int index, int max) {
		int archiveCount = referenceTables[index].getArchiveInfomation().length;
		int c = (archiveCount - 1) * max;
		c += referenceTables[index].getArchiveInfomation()[(archiveCount - 1)].getArchiveEntryInfomation().length;
		return c;
	}

	public CacheFile getReference() {
		return reference;
	}

	public void setReference(CacheFile reference) {
		this.reference = reference;
	}

	public CacheFile[] getCacheFiles() {
		return cacheFiles;
	}

	public void setCacheFiles(CacheFile[] cacheFiles) {
		this.cacheFiles = cacheFiles;
	}

	public ReferenceTable[] getReferenceTables() {
		return referenceTables;
	}

	public void setReferenceTables(ReferenceTable[] referenceTables) {
		this.referenceTables = referenceTables;
	}

	public ByteBuffer getRefTable() {
		return refTable;
	}

	public void setRefTable(ByteBuffer refTable) {
		this.refTable = refTable;
	}

}
