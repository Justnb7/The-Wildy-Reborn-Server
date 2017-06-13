package oscache;

/**
 * 
 * @author Richard(Flamable)
 */
public final class ArchiveInfomation {
	
	private int nameHash;
	
	private int crc32Value;
	
	private int version;
	
	private ArchiveEntryInfomation[] archiveEntryInfomation;
	
	private byte[] digests;
	
	public int getActiveEntryCount() {
		int i = 0;
		for (ArchiveEntryInfomation info : archiveEntryInfomation) {
			if (info != null)
				i++;
		}
		return i;
	}

	public int getNameHash() {
		return nameHash;
	}

	public void setNameHash(int nameHash) {
		this.nameHash = nameHash;
	}

	public int getCrc32Value() {
		return crc32Value;
	}

	public void setCrc32Value(int crc32Value) {
		this.crc32Value = crc32Value;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public ArchiveEntryInfomation[] getArchiveEntryInfomation() {
		return archiveEntryInfomation;
	}

	public void setArchiveEntryInfomation(ArchiveEntryInfomation[] archiveEntryInfomation) {
		this.archiveEntryInfomation = archiveEntryInfomation;
	}

	public byte[] getDigests() {
		return digests;
	}

	public void setDigests(byte[] digests) {
		this.digests = digests;
	}

}
