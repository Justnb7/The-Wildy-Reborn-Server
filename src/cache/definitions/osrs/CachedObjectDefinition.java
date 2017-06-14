package cache.definitions.osrs;

import cache.OpenRsUnpacker;
import cache.definitions.AnyRevObjectDefinition;
import cache.definitions.r317.ObjectDefinition317;
import cache.io.osrs.InputStream;

public final class CachedObjectDefinition extends AnyRevObjectDefinition {

	public static CachedObjectDefinition[] objectDefinitions;
	//public static int[] renderIds = new int[CacheManager.getDefinitionsCacheManager().getReferenceTables()[2].getArchiveInfomation()[6].getArchiveEntryInfomation().length];
	//public static int[] renderIdsViseVersa = new int[CacheManager.getDefinitionsCacheManager().getReferenceTables()[2].getArchiveInfomation()[6].getArchiveEntryInfomation().length];
	public int anInt1641 = -1;
    public int anInt1642;
    public int shading;
    public int anInt1644 = 0;
    public int[] childrenIDs;
    public int groundDecorationSprite;
    public boolean aBoolean1648;
    public boolean aBoolean1654;
    public int scaleX;
    public int[] originalModelColors;
    public int anInt1658;
    public boolean aBoolean1660;
    public int animationId;
    public int[] modifiedModelColors;
    public int mapSceneSprite;
    public boolean aBoolean1665;
    public boolean aBoolean1666;
    public int anInt1670;
    public int anInt1671;
    public int scaleY;
    public int anInt1673;
    public int[] types;
    public int scaleZ;
    public boolean aBoolean1677;
    public int[] models;
    public int lightness;
    
	public static final AnyRevObjectDefinition forId(int objId) {
		if (objId < 0 || objId >= objectDefinitions.length) {
			//System.err.println("bad osrs objdef id : "+objId);
			// fall back to 317 defs
			return ObjectDefinition317.get(objId);
		}
		CachedObjectDefinition def = objectDefinitions[objId];
		if (def == null)
			objectDefinitions[objId] = def = new CachedObjectDefinition(objId, OpenRsUnpacker.readData(2, 6, objId));

		if ((def.name != null && (def.name.equalsIgnoreCase("bank booth") || def.name.equalsIgnoreCase("counter")))) {
			def.projectileClipped = false;
		}
		if (def.rangableObject()) {
			def.projectileClipped = false;
		}
		if (def.unclipped()) {
			def.projectileClipped = false;
			def.osrs_clipType = 0;
		}
		if (def.id == 14432 || def.id == 2292) {
			def.osrs_clipType = 1;
		}
		if (def.id == 14435 || def.id == 2311) {
			def.osrs_clipType = 1;
		}
		return def;
	}

	private CachedObjectDefinition(int id, byte[] data) {
		this.id = id;
		setDefaultsVariableValues();
		readOpcodes(new InputStream(data));
		if(ignoreClipOnAlternativeRoute) {
			projectileClipped = false;
		}
	}

	private void setDefaultsVariableValues() {
		groundDecorationSprite = -1;
		scaleX = 128;
		animationId = -1;
		name = "null";
		aBoolean1654 = false;
		mapSceneSprite = -1;
		aBoolean1660 = false;
		aBoolean1665 = false;
		aBoolean1666 = true;
		hasActions = false;
		anInt1642 = 0;
		scaleY = 128;
		shading = 0;
		anInt1658 = -1;
		aBoolean1648 = false;
		sizeY = 1;
		ignoreClipOnAlternativeRoute = false;
		anInt1671 = -1;
		walkToFlag = 0;
		actions = new String[5];
		anInt1670 = 0;
		scaleZ = 128;
		anInt1673 = 16;
		aBoolean1677 = false;
		lightness = 0;
		projectileClipped = true;
	}

	private final void readOpcodesOld(InputStream buffer, int opcode) {
		if (opcode != 1) {
			if ((opcode ^ 0xffffffff) == -3)
				name = buffer.readString();
			else if (opcode == 5) {
				int size = buffer.readUnsignedByte();
	    		if (size > 0) {
	    			if (models == null) {
	    				types = null;
	    				models = new int[size];
	    				for (int id = 0; id < size; id++)
	    					models[id] = buffer.readUnsignedShort();
	    			} else
	    				buffer.offset += size * 2;
	    		}
			} else if (opcode == 14)
				sizeX = buffer.readUnsignedByte();
			else if ((opcode ^ 0xffffffff) == -16)
				sizeY = buffer.readUnsignedByte();
			else if ((opcode ^ 0xffffffff) != -18) {
				if ((opcode ^ 0xffffffff) == -19) {
					projectileClipped = false;
				} else if (opcode == 19)
					hasActions = buffer.readUnsignedByte() == 1;
				else if (opcode == 21)
					aBoolean1654 = true;
				else if (opcode == 22)
					aBoolean1648 = true;
				else if ((opcode ^ 0xffffffff) != -24) {
					if (opcode != 24) {
						if (opcode == 27) {
							osrs_clipType = 1;
							projectileClipped = true;
						} else if (opcode != 28) {
							if ((opcode ^ 0xffffffff) == -30)
								lightness = buffer.readByte();
							else if (opcode != 39) {
								if (opcode < 30 || (opcode ^ 0xffffffff) <= -36) {
									if (opcode != 40) {
										if(opcode == 41) {
											int var4 = buffer.readUnsignedByte();
											short[] aShortArray1495 = new short[var4];
											short[] aShortArray1476 = new short[var4];
											for (int var5 = 0; ~var5 > ~var4; ++var5) {
												aShortArray1476[var5] = (short) buffer.readUnsignedShort();
												aShortArray1495[var5] = (short) buffer.readUnsignedShort();
											}
										} else if (opcode == 42) {
											int var4 = buffer.readUnsignedByte();
											byte[] aByteArray1513 = new byte[var4];
											for (int var5 = 0; ~var5 > ~var4; ++var5) {
												aByteArray1513[var5] = (byte) buffer.readByte();
											}
										} else if (opcode != 60) {
											if (opcode != 62) {
												if ((opcode ^ 0xffffffff) == -65)
													aBoolean1666 = false;
												else if ((opcode ^ 0xffffffff) == -66)
													scaleX = (buffer
															.readUnsignedShort());
												else if (opcode != 66) {
													if (opcode != 67) {
														if (opcode == 68)
															mapSceneSprite = (buffer.readUnsignedShort());
														else if (opcode == 69)
															walkToFlag = (buffer.readUnsignedByte());
														else if ((opcode ^ 0xffffffff) == -71)
															anInt1644 = (buffer.readShort());
														else if (opcode == 71)
															anInt1670 = (buffer.readShort());
														else if (opcode != 72) {
															if (opcode == 73)
																aBoolean1665 = true;
															else if ((opcode ^ 0xffffffff) == -75)
																ignoreClipOnAlternativeRoute = true;
															else if (opcode != 75) {
																if (opcode == 77 || opcode == 92) {
																	anInt1641 = buffer.readUnsignedShort();
																	if (anInt1641 == 65535)
																		anInt1641 = -1;
																	anInt1658 = buffer.readUnsignedShort();
																	if ((anInt1658 ^ 0xffffffff) == -65536)
																		anInt1658 = -1;
																	if (92 == opcode) {
																		int var4 = buffer
																				.readUnsignedShort();
																		if (var4 == '\uffff') {
																			var4 = -1;
																		}
																	}
																	int i = buffer.readUnsignedByte();
																	childrenIDs = new int[i + 1];
																	for (int i_2_ = 0; (i_2_ ^ 0xffffffff) >= (i ^ 0xffffffff); i_2_++) {
																		childrenIDs[i_2_] = buffer.readUnsignedShort();
																		if ((childrenIDs[i_2_] ^ 0xffffffff) == -65536)
																			childrenIDs[i_2_] = -1;
																	}
																} else if (opcode == 78) {
																	buffer.readUnsignedShort();
																	buffer.readUnsignedByte();
																} else if ((opcode ^ 0xffffffff) == -80) {
																	buffer.readUnsignedShort();
																	buffer.readUnsignedShort();
																	buffer.readUnsignedByte();
																	int paris = buffer.readUnsignedByte();
																	for(int i = 0; i < paris; i++)
																		buffer.readUnsignedShort();
																} else {
																	System.err.println("UNKNOWN OPCOCDE! "+(opcode ^ 0xffffffff));
																}
															} else
																anInt1671 = (buffer.readByte());
														} else
															anInt1642 = (buffer.readShort());
													} else
														scaleZ = (buffer.readUnsignedShort());
												} else
													scaleY = (buffer.readUnsignedShort());
											} else
												aBoolean1660 = true;
										} else
											groundDecorationSprite = (buffer.readUnsignedShort());
									} else {
										int i_34_ = buffer.readUnsignedByte();
							    		modifiedModelColors = new int[i_34_];
							    		originalModelColors = new int[i_34_];
							    		for (int i_35_ = 0; i_34_ > i_35_; i_35_++) {
							    			originalModelColors[i_35_] = buffer.readUnsignedShort();
							    			modifiedModelColors[i_35_] = buffer.readUnsignedShort();
							    		}
									}
								} else {
									actions[opcode - 30] = buffer.readString();
						    		if (actions[opcode - 30].equalsIgnoreCase("hidden"))
						    			actions[opcode - 30] = null;
								}
							} else
								shading = buffer.readByte() * 5;
						} else
							anInt1673 = buffer.readUnsignedByte();
					} else {
						animationId = buffer.readUnsignedShort();
						if (animationId == 65535)
							animationId = -1;
					}
				} else
					aBoolean1677 = true;
			} else {
				osrs_clipType = 0;
				projectileClipped = false;
			}
		} else {
			int i = buffer.readByte();
			if ((i ^ 0xffffffff) < -1) {
				if (models != null)
					buffer.offset += 3 * i;
				else {
					models = new int[i];
					types = new int[i];
					for (int i_5_ = 0; (i ^ 0xffffffff) < (i_5_ ^ 0xffffffff); i_5_++) {
						models[i_5_] = buffer.readUnsignedShort();
						types[i_5_] = buffer.readByte();
					}
				}
			}
		}
	}
	
	private final void readOpcodesNew(InputStream buffer, int opcode) {
		if (opcode != 1) {
			if ((opcode ^ 0xffffffff) == -3)
				name = buffer.readString();
			else if (opcode == 5) {
				int size = buffer.readUnsignedByte();
	    		if (size > 0) {
	    			if (models == null) {
	    				types = null;
	    				models = new int[size];
	    				for (int id = 0; id < size; id++)
	    					models[id] = buffer.readUnsignedShort();
	    			} else
	    				buffer.offset += size * 2;
	    		}
			} else if (opcode == 14)
				sizeX = buffer.readUnsignedByte();
			else if ((opcode ^ 0xffffffff) == -16)
				sizeY = buffer.readUnsignedByte();
			else if ((opcode ^ 0xffffffff) != -18) {
				if ((opcode ^ 0xffffffff) == -19) {
					projectileClipped = false;
				} else if (opcode == 19)
					hasActions = buffer.readUnsignedByte() == 1;
				else if (opcode == 21)
					aBoolean1654 = true;
				else if (opcode == 22)
					aBoolean1648 = true;
				else if ((opcode ^ 0xffffffff) != -24) {
					if (opcode != 24) {
						if (opcode == 27) {
							osrs_clipType = 1;
						} else if (opcode != 28) {
							if ((opcode ^ 0xffffffff) == -30)
								lightness = buffer.readByte();
							else if (opcode != 39) {
								if (opcode < 30 || (opcode ^ 0xffffffff) <= -36) {
									if (opcode != 40) {
										if(opcode == 41) {
											int var4 = buffer.readUnsignedByte();
											short[] aShortArray1495 = new short[var4];
											short[] aShortArray1476 = new short[var4];
											for (int var5 = 0; ~var5 > ~var4; ++var5) {
												aShortArray1476[var5] = (short) buffer.readUnsignedShort();
												aShortArray1495[var5] = (short) buffer.readUnsignedShort();
											}
										} else if (opcode == 42) {
											int var4 = buffer.readUnsignedByte();
											byte[] aByteArray1513 = new byte[var4];
											for (int var5 = 0; ~var5 > ~var4; ++var5) {
												aByteArray1513[var5] = (byte) buffer.readByte();
											}
										} else if (opcode != 60) {
											if (opcode != 62) {
												if ((opcode ^ 0xffffffff) == -65)
													aBoolean1666 = false;
												else if ((opcode ^ 0xffffffff) == -66)
													scaleX = (buffer
															.readUnsignedShort());
												else if (opcode != 66) {
													if (opcode != 67) {
														if (opcode == 68)
															mapSceneSprite = (buffer.readUnsignedShort());
														else if (opcode == 69)
															walkToFlag = (buffer.readUnsignedByte());
														else if ((opcode ^ 0xffffffff) == -71)
															anInt1644 = (buffer.readShort());
														else if (opcode == 71)
															anInt1670 = (buffer.readShort());
														else if (opcode != 72) {
															if (opcode == 73)
																aBoolean1665 = true;
															else if ((opcode ^ 0xffffffff) == -75)
																ignoreClipOnAlternativeRoute = true;
															else if (opcode != 75) {
																if (opcode == 77 || opcode == 92) {
																	anInt1641 = buffer.readUnsignedShort();
																	if (anInt1641 == 65535)
																		anInt1641 = -1;
																	anInt1658 = buffer.readUnsignedShort();
																	if ((anInt1658 ^ 0xffffffff) == -65536)
																		anInt1658 = -1;
																	if (92 == opcode) {
																		int var4 = buffer
																				.readUnsignedShort();
																		if (var4 == '\uffff') {
																			var4 = -1;
																		}
																	}
																	int i = buffer.readUnsignedByte();
																	childrenIDs = new int[i + 1];
																	for (int i_2_ = 0; (i_2_ ^ 0xffffffff) >= (i ^ 0xffffffff); i_2_++) {
																		childrenIDs[i_2_] = buffer.readUnsignedShort();
																		if ((childrenIDs[i_2_] ^ 0xffffffff) == -65536)
																			childrenIDs[i_2_] = -1;
																	}
																} else if (opcode == 78) {
																	buffer.readUnsignedShort();
																	buffer.readUnsignedByte();
																} else if ((opcode ^ 0xffffffff) == -80) {
																	buffer.readUnsignedShort();
																	buffer.readUnsignedShort();
																	buffer.readUnsignedByte();
																	int paris = buffer.readUnsignedByte();
																	for(int i = 0; i < paris; i++)
																		buffer.readUnsignedShort();
																}
															} else
																anInt1671 = (buffer.readByte());
														} else
															anInt1642 = (buffer.readShort());
													} else
														scaleZ = (buffer.readUnsignedShort());
												} else
													scaleY = (buffer.readUnsignedShort());
											} else
												aBoolean1660 = true;
										} else
											groundDecorationSprite = (buffer.readUnsignedShort());
									} else {
										int i_34_ = buffer.readUnsignedByte();
							    		modifiedModelColors = new int[i_34_];
							    		originalModelColors = new int[i_34_];
							    		for (int i_35_ = 0; i_34_ > i_35_; i_35_++) {
							    			originalModelColors[i_35_] = buffer.readUnsignedShort();
							    			modifiedModelColors[i_35_] = buffer.readUnsignedShort();
							    		}
									}
								} else {
									actions[opcode - 30] = buffer.readString();
						    		if (actions[opcode - 30].equalsIgnoreCase("hidden"))
						    			actions[opcode - 30] = null;
								}
							} else
								shading = buffer.readByte() * 5;
						} else
							anInt1673 = buffer.readUnsignedByte();
					} else {
						animationId = buffer.readUnsignedShort();
						if (animationId == 65535)
							animationId = -1;
					}
				} else
					aBoolean1677 = true;
			} else {
				osrs_clipType = 0;
				projectileClipped = false;
			}
		} else {
			int i = buffer.readByte();
			if ((i ^ 0xffffffff) < -1) {
				if (models != null)
					buffer.offset += 3 * i;
				else {
					models = new int[i];
					types = new int[i];
					for (int i_5_ = 0; (i ^ 0xffffffff) < (i_5_ ^ 0xffffffff); i_5_++) {
						models[i_5_] = buffer.readUnsignedShort();
						types[i_5_] = buffer.readByte();
					}
				}
			}
		}
	}

	private final void readOpcodes(InputStream stream) {
		// wtf is this first byte? must be custom structure packed!
		//boolean newRevision = stream.readUnsignedByte() == 1;
		while (true) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			//if(newRevision) {
			//	readOpcodesNew(stream, opcode);
			//} else {
				readOpcodesOld(stream, opcode);
			//}
		}
	}
	
	public boolean rangableObject() {
        int[] rangableObjects = {3457, 21369, 21600, 21376, 21366, 21365, 21381, 21364, 23268, 1264, 1246, 23265, 23273, 1257, 12928, 12929, 12930, 12925, 12932, 12931, 26975, 26977, 26978, 26979, 23271, 11754, 3007, 980, 997, 4262, 14437, 14438, 4437, 4439, 3487, 23053};
        for (int i = 0; i < rangableObjects.length; i++) {
        	if (rangableObjects[i] == id) {
        		return true;
        	}
        }
        if (name != null && !name.equalsIgnoreCase("")) {
            final String name1 = name.toLowerCase();
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
