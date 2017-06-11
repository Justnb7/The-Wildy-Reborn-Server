package com.model.utility.cache;

import com.model.utility.Utility;

import java.util.logging.Logger;

public final class ObjectDefinition {
	
	/**
	 * The logger for the class
	 */
	private static Logger logger = Logger.getLogger(ObjectDefinition.class.getSimpleName());

	private static ByteStreamExt buffer;
	public static int[] streamIndices;
	public static ObjectDefinition definition;

	private static int objects = 0;

	public static int getObjects() {
		return objects;
	}

	public static ObjectDefinition get(int id) {
		if (id > streamIndices.length) {
			id = streamIndices.length - 1;
		}

		for (int index = 0; index < 20; index++) {
			if (cache[index].type == id) {
				return cache[index];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		definition = cache[cacheIndex];

		if (id > streamIndices.length - 1 || id < 0) {
			return null;
		}

		buffer.currentOffset = streamIndices[id];

		definition.type = id;
		definition.setDefaults();
		definition.readValues(buffer);

		return definition;
	}

	private void setDefaults() {
		anIntArray773 = null;
		anIntArray776 = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		objectSizeX = 1;
		objectSizeY = 1;
		aBoolean767 = true;
		aBoolean757 = true;
		hasActions = false;
		aBoolean762 = false;
		aBoolean764 = false;
		anInt781 = -1;
		anInt775 = 16;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		aBoolean779 = true;
		anInt768 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		anInt774 = -1;
		anInt749 = -1;
		childrenIDs = null;
	}

	public static void loadConfig() {
		buffer = new ByteStreamExt(getBuffer("loc.dat"));
		ByteStreamExt buffer = new ByteStreamExt(getBuffer("loc.idx"));
		objects = buffer.readUnsignedWord();
		streamIndices = new int[objects];
		
		int id = 2;
		for (int index = 0; index < objects; index++) {
			streamIndices[index] = id;
			id += buffer.readUnsignedWord();
		}
		
		cache = new ObjectDefinition[20];
		for (int index = 0; index < 20; index++) {
			cache[index] = new ObjectDefinition();
		}
		logger.info(Utility.format(objects) + " Objects have been loaded successfully.");
	}

	public static byte[] getBuffer(String s) {
		try {
			java.io.File file = new java.io.File("./data/map/objectdata/" + s);
			if (!file.exists()) {
				return null;
			}
			byte[] buffer = new byte[(int) file.length()];
			java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(file));
			dis.readFully(buffer);
			dis.close();
			return buffer;
		} catch (Exception e) {
		}
		return null;
	}

	private void readValues(ByteStreamExt buffer) {

		int flag = -1;
		do {
			int type = buffer.readUnsignedByte();
			if (type == 0)
				break;
			if (type == 1) {
				int len = buffer.readUnsignedByte();
				if (len > 0) {
					if (anIntArray773 == null || lowMem) {
						anIntArray776 = new int[len];
						anIntArray773 = new int[len];
						for (int k1 = 0; k1 < len; k1++) {
							anIntArray773[k1] = buffer.readUnsignedWord();
							anIntArray776[k1] = buffer.readUnsignedByte();
						}
					} else {
						buffer.currentOffset += len * 3;
					}
				}
			} else if (type == 2)
				name = buffer.readString();
			else if (type == 3)
				description = buffer.readBytes();
			else if (type == 5) {
				int len = buffer.readUnsignedByte();
				if (len > 0) {
					if (anIntArray773 == null || lowMem) {
						anIntArray776 = null;
						anIntArray773 = new int[len];
						for (int l1 = 0; l1 < len; l1++)
							anIntArray773[l1] = buffer.readUnsignedWord();
					} else {
						buffer.currentOffset += len * 2;
					}
				}
			} else if (type == 14)
				objectSizeX = buffer.readUnsignedByte();
			else if (type == 15)
				objectSizeY = buffer.readUnsignedByte();
			else if (type == 17)
				aBoolean767 = false;
			else if (type == 18)
				aBoolean757 = false;
			else if (type == 19)
				hasActions = (buffer.readUnsignedByte() == 1);
			else if (type == 21)
				aBoolean762 = true;
			else if (type == 22) {
			} else if (type == 23)
				aBoolean764 = true;
			else if (type == 24) {
				anInt781 = buffer.readUnsignedWord();
				if (anInt781 == 65535)
					anInt781 = -1;
			} else if (type == 28)
				anInt775 = buffer.readUnsignedByte();
			else if (type == 29)
				buffer.readSignedByte();
			else if (type == 39)
				buffer.readSignedByte();
			else if (type >= 30 && type < 39) {
				if (actions == null)
					actions = new String[5];
				actions[type - 30] = buffer.readString();
				if (actions[type - 30].equalsIgnoreCase("hidden"))
					actions[type - 30] = null;
			} else if (type == 40) {
				int i1 = buffer.readUnsignedByte();
				modifiedModelColors = new int[i1];
				originalModelColors = new int[i1];
				for (int i2 = 0; i2 < i1; i2++) {
					modifiedModelColors[i2] = buffer.readUnsignedWord();
					originalModelColors[i2] = buffer.readUnsignedWord();
				}

			} else if (type == 60)
				anInt746 = buffer.readUnsignedWord();
			else if (type == 62) {
			} else if (type == 64) {
			} else if (type == 65)
				buffer.readUnsignedWord();
			else if (type == 66)
				buffer.readUnsignedWord();
			else if (type == 67)
				buffer.readUnsignedWord();
			else if (type == 68)
				anInt758 = buffer.readUnsignedWord();
			else if (type == 69)
				anInt768 = buffer.readUnsignedByte();
			else if (type == 70)
				buffer.readSignedWord();
			else if (type == 71)
				buffer.readSignedWord();
			else if (type == 72)
				buffer.readSignedWord();
			else if (type == 73)
				aBoolean736 = true;
			else if (type == 74)
				aBoolean766 = true;
			else if (type == 75)
				anInt760 = buffer.readUnsignedByte();
			else if (type == 77) {
				anInt774 = buffer.readUnsignedWord();
				if (anInt774 == 65535)
					anInt774 = -1;
				anInt749 = buffer.readUnsignedWord();
				if (anInt749 == 65535)
					anInt749 = -1;
				int j1 = buffer.readUnsignedByte();
				childrenIDs = new int[j1 + 1];
				for (int j2 = 0; j2 <= j1; j2++) {
					childrenIDs[j2] = buffer.readUnsignedWord();
					if (childrenIDs[j2] == 65535)
						childrenIDs[j2] = -1;
				}
			}
		} while (true);
		if (flag == -1 && name != "null" && name != null) {
			hasActions = anIntArray773 != null && (anIntArray776 == null || anIntArray776[0] == 10);
			if (actions != null)
				hasActions = true;
		}
		if (aBoolean766) {
			aBoolean767 = false;
			aBoolean757 = false;
		}
		if (anInt760 == -1)
			anInt760 = aBoolean767 ? 1 : 0;
	}

	private ObjectDefinition() {
		type = -1;
	}

	public boolean hasActions() {
		return hasActions || actions != null;
	}

	public boolean hasName() {
		return name != null && name.length() > 1;
	}

	public int xLength() {
		return objectSizeX;
	}

	public int yLength() {
		return objectSizeY;
	}

	public boolean aBoolean736;
	public String name;
	public int objectSizeX;
	public int anInt746;
	int[] originalModelColors;
	public int anInt749;
	public static boolean lowMem;
	public int type;
	public boolean aBoolean757;
	public int anInt758;
	public int childrenIDs[];
	private int anInt760;
	public int objectSizeY;
	public boolean aBoolean762;
	public boolean aBoolean764;
	private boolean aBoolean766;
	public boolean aBoolean767;
	public int anInt768;
	private static int cacheIndex;
	int[] anIntArray773;
	public int anInt774;
	public int anInt775;
	int[] anIntArray776;
	public byte description[];
	public boolean hasActions;
	public boolean aBoolean779;
	public int anInt781;
	private static ObjectDefinition[] cache;
	int[] modifiedModelColors;
	public String actions[];

	public String getName() {
		return name;
	}
}