package oscache;

import java.nio.ByteBuffer;

/**
 * 
 * @author Richard(Flamable)
 */
public class ReferenceTable {

	private final int index;
	private final CacheArchive archive;
	private int protocolRevision;
	private int revision;
	private int settingMask;
	private ArchiveInfomation[] archiveInfomation;

	public ReferenceTable(int index, CacheArchive archive) {
		this.index = index;
		this.archive = archive;
	}

	public void decode() {
		if (archive == null)
			return;
		
		
		byte[] is = archive.getData();
		
		if (is == null) {
			System.out.println("Null idx@ "+archive.getData());
			return;
		}
		
		ByteBuffer buffer = ByteBuffer.wrap(archive.getData());

		protocolRevision = buffer.get();
		if (protocolRevision >= 6)
			revision = buffer.getInt();
		settingMask = buffer.get() & 0xff;
		
		final int activeArchiveCount = protocolRevision >= 7 ? Buffers.getSmartInt(buffer) : buffer.getShort() & 0xFFFF;

		/**
		 * Get the last active Archive id
		 */
		buffer.mark();
		int realCount = 0;
		for (int i = 0; i < activeArchiveCount; i++) {
			realCount += (buffer.getShort() & 0xFFFF);
		}
		archiveInfomation = new ArchiveInfomation[realCount + 1];
		buffer.reset();

		/**
		 * Init the valid archives
		 */
		int id = 0;
		for (int i = 0; i < activeArchiveCount; i++) {
			id += (buffer.getShort() & 0xffff);
			archiveInfomation[id] = new ArchiveInfomation();
		}

		/**
		 * get the nameHash if archives are named
		 */
		if ((0x1 & settingMask) != 0){
			for (int i = 0; i < archiveInfomation.length; i++) {
				final ArchiveInfomation arch = archiveInfomation[i];
				if (arch == null)
					continue;
				arch.setNameHash(buffer.getInt());
			}
		}

		/**
		 * Get the Crc32 values
		 */
		for(int i = 0; i < archiveInfomation.length; i++) {
			if (archiveInfomation[i] == null)
				continue;
			archiveInfomation[i].setCrc32Value(buffer.getInt());
		}

		/**
		 * Get the digests
		 */
		if ((0x2 & settingMask) != 0) {
			for(int i = 0; i < archiveInfomation.length; i++) {
				if (archiveInfomation[i] == null)
					continue;
				byte[] digest = new byte[64];	
				for(int i2 = 0; i2 < digest.length; i2++) {
					digest[i2] = buffer.get();
				}
				archiveInfomation[i].setDigests(digest);
			}
		}

		/**
		 * Get the version
		 */
		for (int i = 0; i < archiveInfomation.length; i++) {
			if (archiveInfomation[i] == null)
				continue;
			archiveInfomation[i].setVersion(buffer.getInt());
		}


		final int[] activeArchiveEntrys = new int[archiveInfomation.length];
		/**
		 * Get archiveEntry size
		 */
		for (int i = 0; i < archiveInfomation.length; i++) {
			if (archiveInfomation[i] == null)
				continue;
			activeArchiveEntrys[i] = protocolRevision >= 7 ? Buffers.getSmartInt(buffer) : buffer.getShort() & 0xffff;
		}

		/**
		 * Get real childCount
		 */
		buffer.mark();
		for (int i = 0; i < archiveInfomation.length; i++) {
			if (archiveInfomation[i] == null)
				continue;
			int entryCount = 0;
			for (int j = 0; j < activeArchiveEntrys[i]; j++) {
				entryCount += (buffer.getShort() & 0xffff);
			}
			entryCount++;
			archiveInfomation[i].setArchiveEntryInfomation(new ArchiveEntryInfomation[entryCount]);
		}
		buffer.reset();

		/**
		 * Init the active archive entrys
		 */
		for (int i = 0; i < archiveInfomation.length; i++) {
			if (archiveInfomation[i] == null)
				continue;
			int archiveEntryId = 0;
			for (int j = 0; j < activeArchiveEntrys[i]; j++) {
				archiveEntryId += (buffer.getShort() & 0xffff);
				archiveInfomation[i].getArchiveEntryInfomation()[archiveEntryId] = new ArchiveEntryInfomation();
			}
		}

		/**
		 * Set entry names
		 */
		if ((0x1 & settingMask) != 0){
			for (int i = 0; i < archiveInfomation.length; i++) {
				final ArchiveInfomation arch = archiveInfomation[i];
				if (arch == null)
					continue;
				for (int j = 0; j < archiveInfomation[i].getArchiveEntryInfomation().length; j++) {
					if (archiveInfomation[i].getArchiveEntryInfomation()[j] == null)
						continue;
					archiveInfomation[i].getArchiveEntryInfomation()[j].setNameHash(buffer.getInt());
				}
			}
		}
	}
	
	public int getArchiveByName(String name) {
		int hash = calcJaghash(name);
		for (int i = 0; i < archiveInfomation.length;i++) {
			if (archiveInfomation[i] == null)
				continue;
			if (archiveInfomation[i].getNameHash() == hash)
				return i;
		}
		return -1;
	}

	public static int calcJaghash(String n) {
		int i_77_ = 0;
		byte[] characters = n.getBytes();
		for (int i_78_ = 0; i_78_ < n.length(); i_78_++)
			i_77_ = (characters[i_78_] & 0xff) + ((i_77_ << -1325077051)
					+ -i_77_);
		return i_77_;
	}
	
	public int getIndex() {
		return index;
	}

	public int getProtocolRevision() {
		return protocolRevision;
	}

	public int getRevision() {
		return revision;
	}

	public boolean isNamed() {
		return (0x1 & settingMask) != 0;
	}

	public boolean iswhirlpooled() {
		return (0x2 & settingMask) != 0;
	}

	public ArchiveInfomation[] getArchiveInfomation() {
		return archiveInfomation;
	}

	public int getArchiveCount() {
		return archiveInfomation == null ? 0 : archiveInfomation.length;
	}

}
