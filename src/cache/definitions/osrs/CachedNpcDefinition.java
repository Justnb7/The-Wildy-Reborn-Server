package cache.definitions.osrs;

import cache.OpenRsUnpacker;
import cache.io.osrs.InputStream;

public final class CachedNpcDefinition {

	public static CachedNpcDefinition[] npcDefinitions;

	public short aShortArray2862[];
	public short aShortArray2830[];
	public boolean aBoolean2835;
	public int turn90CWAnimation;
	public int varbitId;
	public int varpId;
	public int[] childrenIDs;
	public int width;
	public int turnValue;
	public int headIcon;
	public String[] actions;
	public boolean canRightClick;
	public int[] model;
	public int idleAnimation;
	public boolean render;
	public int turn180Animation;
	public boolean displayOnMinimap;
	public int[] originalModelColors;
	public int contrast;
	public int id;
	public int combatLevel;
	public String name;
	public int turn90CCAnimation;
	public int ambient;
	public int height;
	public int size;
	public int walkAnimation;
	public int[] headModels;
	public int[] modifiedModelColor;
	public int opcode15 = -1;
	public int opcode16 = -1;
	public boolean opcode109 = true;

	public static final CachedNpcDefinition getNPCDefinitions(int npcId) {
		if (npcId < 0 || npcId >= npcDefinitions.length) {
			return null;
		}
		CachedNpcDefinition def = npcDefinitions[npcId];
		if (def == null)
			npcDefinitions[npcId] = def = new CachedNpcDefinition(npcId, OpenRsUnpacker.readData(2, 9, npcId));
		return def;
	}

	private CachedNpcDefinition(int id, byte[] data) {
		this.id = id;
		setDefaultsVariableValues();
		if (data == null) {
			return;
		}
		readOpcodes(new InputStream(data));
	}

	private void setDefaultsVariableValues() {
		varpId = -1;
		canRightClick = true;
		idleAnimation = -1;
		actions = new String[5];
		render = false;
		turn90CWAnimation = -1;
		varbitId = -1;
		contrast = 0;
		ambient = 0;
		combatLevel = -1;
		headIcon = -1;
		height = 128;
		width = 128;
		turnValue = 32;
		walkAnimation = -1;
		displayOnMinimap = true;
		size = 1;
		turn180Animation = -1;
		name = "null";
		turn90CCAnimation = -1;
	}

	private final void readOpcodes(InputStream stream, int opcode) {
		if (opcode == 1) {
			int i = stream.readUnsignedByte();
			model = new int[i];
			for (int i_2_ = 0; (i_2_ ^ 0xffffffff) > (i ^ 0xffffffff); i_2_++)
				model[i_2_] = stream.readUnsignedShort();
		} else if (opcode != 2) {
			if (opcode == 12)
				size = stream.readUnsignedByte();
			else if ((opcode ^ 0xffffffff) == -14)
				idleAnimation = stream.readUnsignedShort();
			else if (opcode != 14) {
				if ((opcode ^ 0xffffffff) != -16) {
					if (opcode == 16)
						opcode16 = stream.readUnsignedShort();
					else if ((opcode ^ 0xffffffff) == -18) {
						walkAnimation = stream.readUnsignedShort();
						turn180Animation = stream.readUnsignedShort();
						turn90CWAnimation = stream.readUnsignedShort();
						turn90CCAnimation = stream.readUnsignedShort();
					} else if (opcode >= 30 && opcode < 35) {
						actions[opcode - 30] = stream.readString();
					} else if ((opcode ^ 0xffffffff) != -41) {
						if (opcode == 41) {
							int l = stream.readUnsignedByte();
							aShortArray2862 = new short[l];
							aShortArray2830 = new short[l];
							for (int i2 = 0; l > i2; i2++) {
								aShortArray2862[i2] = (short) stream.readUnsignedShort();
								aShortArray2830[i2] = (short) stream.readUnsignedShort();
							}
						} else if (opcode == 42) {
							int var4 = stream.readUnsignedByte();
							byte[] aByteArray1247 = new byte[var4];
							for (int var5 = 0; var4 > var5; ++var5) {
								aByteArray1247[var5] = (byte) stream.readByte();
							}
						} else if (opcode == 60) {
							int count = stream.readUnsignedByte();
							headModels = new int[count];
							for (int id = 0; count > id; id++)
								headModels[id] = stream.readUnsignedShort();
						} else if ((opcode ^ 0xffffffff) != -94) {
							if ((opcode ^ 0xffffffff) != -96) {
								if (opcode != 97) {
									if ((opcode ^ 0xffffffff) != -99) {
										if ((opcode ^ 0xffffffff) == -100)
											render = true;
										else if ((opcode ^ 0xffffffff) == -101)
											ambient = stream.readByte();
										else if ((opcode ^ 0xffffffff) == -102)
											contrast = stream.readByte() * 5;
										else if ((opcode ^ 0xffffffff) == -103)
											headIcon = stream.readUnsignedShort();
										else if (opcode != 103) {
											if ((opcode ^ 0xffffffff) == -107) {
												varbitId = stream.readUnsignedShort();
												if (varbitId == 65535)
													varbitId = -1;
												varpId = stream.readUnsignedShort();
												if (varpId == 65535)
													varpId = -1;
												int len = stream.readUnsignedByte();
												childrenIDs = new int[len + 1];
												for (int id = 0; len >= id; id++) {
													childrenIDs[id] = stream.readUnsignedShort();
													if (childrenIDs[id] == 65535)
														childrenIDs[id] = -1;
												}
											} else if ((opcode ^ 0xffffffff) == -108)
												canRightClick = false;
											else if (opcode == 109) {
												opcode109 = false;
											} else if (opcode == 111) {
												aBoolean2835 = false;
											} else if (opcode == 113) {
												stream.readUnsignedShort();
												stream.readUnsignedShort();
											} else if (opcode == 114) {
												stream.readByte();
												stream.readByte();
											} else if (opcode == 115) {
												stream.readUnsignedByte();
												stream.readUnsignedByte();
											} else if (opcode == 119) {
												stream.readByte();
											} else if (opcode == 121) {
												int[][] dunno = new int[model.length][];
												int len = stream.readUnsignedByte();
												for (int i = 0; i < len; i++) {
													int var6 = stream.readUnsignedByte();
													int[] var7 = dunno[var6] = new int[3];
													var7[0] = stream.readByte();
													var7[1] = stream.readByte();
													var7[2] = stream.readByte();
												}
											} else if (opcode == 122) {
												stream.readUnsignedShort();
											} else if (opcode == 123) {
												stream.readUnsignedShort();
											} else if (opcode == 125) {
												stream.readByte();
											} else if (opcode == 126) {
												stream.readUnsignedShort();
											} else if (opcode == 249) {
												int var5 = stream.readUnsignedByte();
												for (int var6 = 0; var6 < var5; ++var6) {
													boolean var7 = stream.readUnsignedByte() == 1;
													stream.readTribyte();
													// int var8 =
													// stream.readTribyte();
													// Object var9;
													if (!var7) {
														stream.readInt();
													} else {
														stream.readString();
													}
												}
											}
										} else
											turnValue = stream.readUnsignedShort();
									} else
										height = stream.readUnsignedShort();
								} else
									width = stream.readUnsignedShort();
							} else
								combatLevel = stream.readUnsignedShort();
						} else
							displayOnMinimap = false;
					} else {
						int count = stream.readUnsignedByte();
						modifiedModelColor = new int[count];
						originalModelColors = new int[count];
						for (int id = 0; id < count; id++) {
							originalModelColors[id] = stream.readUnsignedShort();
							modifiedModelColor[id] = stream.readUnsignedShort();
						}
					}
				} else
					opcode15 = stream.readUnsignedShort();
			} else
				walkAnimation = stream.readUnsignedShort();
		} else
			name = stream.readString();
	}

	private final void readOpcodes(InputStream stream) {
		while (true) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			readOpcodes(stream, opcode);
		}
	}
}