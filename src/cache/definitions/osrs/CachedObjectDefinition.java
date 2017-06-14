package cache.definitions.osrs;

import cache.OpenRsUnpacker;
import cache.definitions.AnyRevObjectDefinition;
import cache.definitions.r317.ObjectDefinition317;
import cache.io.osrs.InputStream;

public final class CachedObjectDefinition extends AnyRevObjectDefinition {

	public static CachedObjectDefinition[] objectDefinitions;

	public int anInt1582;
	public String aString1549 = "null";
	int[] anIntArray1548;
	int[] anIntArray1588;
	public int anInt1554 = 1;
	public int anInt1555 = 1;
	public int anInt1556 = 2;
	public boolean aBool1573 = true;
	public int anInt1550 = -1;
	int anInt1559 = -1;
	boolean aBool1560 = false;
	public boolean aBool1561 = false;
	public int anInt1562 = -1;
	public int anInt1563 = 16;
	int anInt1564 = 0;
	int anInt1565 = 0;
	short[] aShortArray1587;
	short[] aShortArray1551;
	short[] aShortArray1552;
	short[] aShortArray1553;
	public int anInt1546 = -1;
	boolean aBool1569 = false;
	public boolean aBool1570 = true;
	int anInt1568 = 128;
	int anInt1572 = 128;
	int anInt1557 = 128;
	public int anInt1547 = -1;
	int anInt1540 = 0;
	int anInt1575 = 0;
	int anInt1576 = 0;
	public boolean aBool1544 = false;
	boolean aBool1541 = false;
	public int anInt1579 = -1;
	int anInt1581 = -1;
	int anInt1567 = -1;
	public int anInt1583 = -1;
	public int anInt1584 = 0;
	public int anInt1585 = 0;
	public int anInt1586 = 0;
	public int[] anIntArray1577;
	public int[] anIntArray1578;
	public String[] aStringArray1566 = new String[5];
	public static boolean aBool1574 = false;
    
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
		if(this.anInt1550 == -1) {
			this.anInt1550 = 0;
			if(this.anIntArray1588 != null && (null == this.anIntArray1548 || this.anIntArray1548[0] == 10)) {
				this.anInt1550 = 1;
			}

			for(int var1 = 0; var1 < 5; ++var1) {
				if(this.aStringArray1566[var1] != null) {
					this.anInt1550 = 1;
				}
			}
		}

		if(this.anInt1579 == -1) {
			this.anInt1579 = this.anInt1556 != 0?1:0;
		}
	}
	
	void decode(InputStream var1, int var2) {
		int var4;
		int var5;
		if(var2 == 1) {
			var4 = var1.readUnsignedByte();
			if(var4 > 0) {
				if(this.anIntArray1588 != null && !aBool1574) {
					var1.offset += 3 * var4;
				} else {
					this.anIntArray1548 = new int[var4];
					this.anIntArray1588 = new int[var4];

					for(var5 = 0; var5 < var4; ++var5) {
						this.anIntArray1588[var5] = var1.readUnsignedShort();
						this.anIntArray1548[var5] = var1.readUnsignedByte();
					}
				}
			}
		} else if(var2 == 2) {
			this.aString1549 = var1.readString();
		} else if(var2 == 5) {
			var4 = var1.readUnsignedByte();
			if(var4 > 0) {
				if(null != this.anIntArray1588 && !aBool1574) {
					var1.offset += 2 * var4;
				} else {
					this.anIntArray1548 = null;
					this.anIntArray1588 = new int[var4];

					for(var5 = 0; var5 < var4; ++var5) {
						this.anIntArray1588[var5] = var1.readUnsignedShort();
					}
				}
			}
		} else if(var2 == 14) {
			this.anInt1554 = var1.readUnsignedByte();
		} else if(var2 == 15) {
			this.anInt1555 = var1.readUnsignedByte();
		} else if(var2 == 17) {
			this.anInt1556 = 0;
			this.aBool1573 = false;
		} else if(var2 == 18) {
			this.aBool1573 = false;
		} else if(var2 == 19) {
			this.anInt1550 = var1.readUnsignedByte();
		} else if(var2 == 21) {
			this.anInt1559 = 0;
		} else if(var2 == 22) {
			this.aBool1560 = true;
		} else if(var2 == 23) {
			this.aBool1561 = true;
		} else if(var2 == 24) {
			this.anInt1562 = var1.readUnsignedShort();
			if(this.anInt1562 == '\uffff') {
				this.anInt1562 = -1;
			}
		} else if(var2 == 27) {
			this.anInt1556 = 1;
		} else if(var2 == 28) {
			this.anInt1563 = var1.readUnsignedByte();
		} else if(var2 == 29) {
			this.anInt1564 = var1.readByte();
		} else if(var2 == 39) {
			this.anInt1565 = var1.readByte() * 25;
		} else if(var2 >= 30 && var2 < 35) {
			this.aStringArray1566[var2 - 30] = var1.readString();
			if(this.aStringArray1566[var2 - 30].equalsIgnoreCase("Hidden")) {
				this.aStringArray1566[var2 - 30] = null;
			}
		} else if(var2 == 40) {
			var4 = var1.readUnsignedByte();
			this.aShortArray1587 = new short[var4];
			this.aShortArray1551 = new short[var4];

			for(var5 = 0; var5 < var4; ++var5) {
				this.aShortArray1587[var5] = (short)var1.readUnsignedShort();
				this.aShortArray1551[var5] = (short)var1.readUnsignedShort();
			}
		} else if(var2 == 41) {
			var4 = var1.readUnsignedByte();
			this.aShortArray1552 = new short[var4];
			this.aShortArray1553 = new short[var4];

			for(var5 = 0; var5 < var4; ++var5) {
				this.aShortArray1552[var5] = (short)var1.readUnsignedShort();
				this.aShortArray1553[var5] = (short)var1.readUnsignedShort();
			}
		} else if(var2 == 60) {
			this.anInt1546 = var1.readUnsignedShort();
		} else if(var2 == 62) {
			this.aBool1569 = true;
		} else if(var2 == 64) {
			this.aBool1570 = false;
		} else if(var2 == 65) {
			this.anInt1568 = var1.readUnsignedShort();
		} else if(var2 == 66) {
			this.anInt1572 = var1.readUnsignedShort();
		} else if(var2 == 67) {
			this.anInt1557 = var1.readUnsignedShort();
		} else if(var2 == 68) {
			this.anInt1547 = var1.readUnsignedShort();
		} else if(var2 == 69) {
			var1.readUnsignedByte();
		} else if(var2 == 70) {
			this.anInt1540 = var1.readShort();
		} else if(var2 == 71) {
			this.anInt1575 = var1.readShort();
		} else if(var2 == 72) {
			this.anInt1576 = var1.readShort();
		} else if(var2 == 73) {
			this.aBool1544 = true;
		} else if(var2 == 74) {
			this.aBool1541 = true;
		} else if(var2 == 75) {
			this.anInt1579 = var1.readUnsignedByte();
		} else if(var2 != 77 && var2 != 92) {
			if(var2 == 78) {
				this.anInt1583 = var1.readUnsignedShort();
				this.anInt1584 = var1.readUnsignedByte();
			} else if(var2 == 79) {
				this.anInt1585 = var1.readUnsignedShort();
				this.anInt1586 = var1.readUnsignedShort();
				this.anInt1584 = var1.readUnsignedByte();
				var4 = var1.readUnsignedByte();
				this.anIntArray1577 = new int[var4];

				for(var5 = 0; var5 < var4; ++var5) {
					this.anIntArray1577[var5] = var1.readUnsignedShort();
				}
			} else if(var2 == 81) {
				this.anInt1559 = var1.readUnsignedByte() * 256;
			}
		} else {
			this.anInt1581 = var1.readUnsignedShort();
			if(this.anInt1581 == '\uffff') {
				this.anInt1581 = -1;
			}

			this.anInt1567 = var1.readUnsignedShort();
			if(this.anInt1567 == '\uffff') {
				this.anInt1567 = -1;
			}

			var4 = -1;
			if(var2 == 92) {
				var4 = var1.readUnsignedShort();
				if(var4 == '\uffff') {
					var4 = -1;
				}
			}

			var5 = var1.readUnsignedByte();
			this.anIntArray1578 = new int[var5 + 2];

			for(int var3 = 0; var3 <= var5; ++var3) {
				this.anIntArray1578[var3] = var1.readUnsignedShort();
				if(this.anIntArray1578[var3] == '\uffff') {
					this.anIntArray1578[var3] = -1;
				}
			}

			this.anIntArray1578[1 + var5] = var4;
		}

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
	
	private final void readOpcodes(InputStream stream) {
		while (true) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				return;
			decode(stream, opcode);
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
