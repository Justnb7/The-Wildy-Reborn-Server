package cache.definitions.r317;

import cache.definitions.AnyRevObjectDefinition;
import com.model.utility.Utility;
import cache.io.r317.ByteStreamExt;

import java.util.logging.Logger;

public final class ObjectDefinition317 extends AnyRevObjectDefinition {
	
	/**
	 * The logger for the class
	 */
	private static Logger logger = Logger.getLogger(ObjectDefinition317.class.getSimpleName());

	private static ByteStreamExt buffer;
	public static int[] streamIndices;
	public static ObjectDefinition317 definition;

	private static int objects = 0;

	public static int getObjects() {
		return objects;
	}

	public static AnyRevObjectDefinition get(int id) {
		if (id > streamIndices.length) {
			id = streamIndices.length - 1;
		}

		for (int index = 0; index < 20; index++) {
			if (cache[index].id == id) {
				return cache[index];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		definition = cache[cacheIndex];

		if (id > streamIndices.length - 1 || id < 0) {
			return null;
		}

		buffer.currentOffset = streamIndices[id];

		definition.id = id;
		definition.setDefaults();
		definition.readValues(buffer);

		return definition;
	}

	@Override
	public boolean projectileClipped() {
		return projectileClipped;
	}

	@Override
	public boolean hasActions() {
		return hasActions || actions != null;
	}

	@Override
	public boolean hasName() {
		return name != null && name.length() > 1;
	}

	@Override
	public int xLength() {
		return sizeX;
	}

	@Override
	public int yLength() {
		return sizeY;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String[] getActions() {
		String[] allActions = new String[actions.length];
		for(int i = 0; i < actions.length; i++) {
			if(actions[i] == null)
				continue;
			allActions[i] = actions[i];
		}
		return allActions;
	}

	@Override
	public int getId() {
		return id;
	}

	private void setDefaults() {
		anIntArray773 = null;
		anIntArray776 = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		sizeX = 1;
		sizeY = 1;
		aBoolean767 = true;
		projectileClipped = true;
		hasActions = false;
		aBoolean762 = false;
		aBoolean764 = false;
		anInt781 = -1;
		anInt775 = 16;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		r317_cliptype = true;
		walkToFlag = 0;
		aBoolean736 = false;
		ignoreClipOnAlternativeRoute = false;
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
		
		cache = new ObjectDefinition317[20];
		for (int index = 0; index < 20; index++) {
			cache[index] = new ObjectDefinition317();
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
				sizeX = buffer.readUnsignedByte();
			else if (type == 15)
				sizeY = buffer.readUnsignedByte();
			else if (type == 17)
				aBoolean767 = false;
			else if (type == 18)
				projectileClipped = false;
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
				walkToFlag = buffer.readUnsignedByte();
			else if (type == 70)
				buffer.readSignedWord();
			else if (type == 71)
				buffer.readSignedWord();
			else if (type == 72)
				buffer.readSignedWord();
			else if (type == 73)
				aBoolean736 = true;
			else if (type == 74)
				ignoreClipOnAlternativeRoute = true;
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
		if (ignoreClipOnAlternativeRoute) {
			aBoolean767 = false;
			projectileClipped = false;
		}
		if (anInt760 == -1)
			anInt760 = aBoolean767 ? 1 : 0;
	}

	private ObjectDefinition317() {
		id = -1;
	}

	public boolean aBoolean736;
	public int anInt746;
	int[] originalModelColors;
	public int anInt749;
	public static boolean lowMem;
	public int anInt758;
	public int childrenIDs[];
	private int anInt760;
	public boolean aBoolean762;
	public boolean aBoolean764;
	public boolean aBoolean767;
	private static int cacheIndex;
	int[] anIntArray773;
	public int anInt774;
	public int anInt775;
	int[] anIntArray776;
	public byte description[];
	public int anInt781;
	private static ObjectDefinition317[] cache;
	int[] modifiedModelColors;
	public boolean projectileClipped;
	public boolean r317_cliptype;
	public int sizeX = 1;
	public int sizeY = 1;
	public boolean ignoreClipOnAlternativeRoute;
	public String name;
	public String[] actions;
	public boolean hasActions;
	public int id;
	public int walkToFlag;

	@Override
	public int getWalkToFlag() {
		return walkToFlag;
	}

	@Override
	public boolean ignoreAlt() {
		return ignoreClipOnAlternativeRoute;
	}

	@Override
	public boolean clips() {
		return r317_cliptype || projectileClipped;
	}

	@Override
	public boolean roofclips() {
		return r317_cliptype;
	}

	@Override
	public boolean rangableObject() {
		int[] rangableObjects = {3457, 21369, 21600, 21376, 21366, 21365, 21381, 21364, 23268, 1264, 1246, 23265, 23273, 1257, 12928, 12929, 12930, 12925, 12932, 12931, 26975, 26977, 26978, 26979, 23271, 11754, 3007, 980, 997, 4262, 14437, 14438, 4437, 4439, 3487, 23053};
		for (int i = 0; i < rangableObjects.length; i++) {
			if (rangableObjects[i] == getId()) {
				return true;
			}
		}
		if (getName() != null && !getName().equalsIgnoreCase("")) {
			final String name1 = getName().toLowerCase();
			String[] rangables = {"grass", "daises", "fungus", "mushroom", "sarcophagus", "counter", "plant", "altar", "pew", "log", "stump", "stool", "sign", "cart", "chest", "rock", "bush", "hedge", "chair", "table", "crate", "barrel", "box", "skeleton", "corpse", "vent", "stone", "rockslide"};
			for (int i = 0; i < rangables.length; i++) {
				if (name1.contains(rangables[i]) || name1.equalsIgnoreCase(rangables[i]) || name1.endsWith(rangables[i])) {
					return true;
				}
			}
		}
		return false;
	}

}