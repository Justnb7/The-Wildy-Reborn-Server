package com.model.game.character.player.content.music.sounds;

import com.model.game.character.player.Player;
import com.model.game.definitions.ItemDefinition;
import com.model.game.item.Item;
import com.model.game.item.container.impl.equipment.EquipmentConstants;
import com.model.net.packet.out.SendSoundPacket;
import com.model.utility.Utility;

public class PlayerSounds {
	
	private static final Sound[][] sounds = new Sound[22000][];
	
	public static final int[] RANDOM_METAL_ARMOR_SOUNDS = new int[] { 405, 406,
			785, 786, 790, 1052, 1066 };

	public static void SendSoundPacketForId(Player player, boolean spec, int id) {
		if (id < 1) {
			return;
		}
		if (sounds[id] != null) {
			if (spec) {
				player.write(new SendSoundPacket(sounds[id][4].getId(), sounds[id][4].getType(), sounds[id][4].getDelay()));
			} else {
				player.sendGlobalSound(player, sounds[id][player.getAttackStyle()].getId(), sounds[id][player.getAttackStyle()].getType(), sounds[id][player.getAttackStyle()].getDelay());
			}
		}
	}

	public static void sendBlockOrHitSound(Player player, boolean hit) {
		
		Item shield = player.getEquipment().get(EquipmentConstants.SHIELD_SLOT);
		if (shield != null && sounds[shield.getId()] != null) {
			int sound = Utility.inclusiveRandom(sounds[shield.getId()].length - 1);
			player.sendGlobalSound(player, sounds[shield.getId()][sound].id, 0, 0);
		}

		if (hit) {
			int id = player.getAppearance().getGender() == 0 ? 816 : 818;
			player.sendGlobalSound(player, id, 0, 0);
		} else {
			player.sendGlobalSound(player, 75, 10, 0);
		}
	}

	public static void declare() {
		sounds[0] = new Sound[] { new Sound(417, 18), new Sound(418, 18),
				new Sound(417, 18), new Sound(417, 18), new Sound(0) };
		sounds[1] = new Sound[] { new Sound(417, 18), new Sound(418, 18),
				new Sound(417, 18), new Sound(417, 18), new Sound(0) };

		for (int i = 0; i < ItemDefinition.ITEM_LIMIT; i++) {
			if (ItemDefinition.get(i) != null) {
				String check = ItemDefinition.get(i).getName().toLowerCase();
				if (check.contains("abyssal whip")) {
					sounds[i] = new Sound[] { new Sound(1080, 10),
							new Sound(1080, 10), new Sound(1080, 10),
							new Sound(1080, 10), new Sound(1081, 28) };
				} else if (check.contains("c'bow") || check.contains("crossbow")) {
					sounds[i] = new Sound[] { new Sound(367, 38),
							new Sound(367, 38), new Sound(367, 38),
							new Sound(367, 38), new Sound(367, 38) };
				} else if (check.contains("greataxe") || check.contains("great axe")) {
					sounds[i] = new Sound[] { new Sound(1056, 33),
							new Sound(1056, 33), new Sound(1056, 33),
							new Sound(1056, 33), new Sound(1056, 33) };
				} else if (check.contains("bow") || check.contains("seercull")) {
					if (check.contains("magic shortbow") || check.contains("magic longbow")) {
						sounds[i] = new Sound[] { new Sound(370, 38),
								new Sound(370, 38), new Sound(370, 38),
								new Sound(370, 38), new Sound(386, 18) };
					} else {
						sounds[i] = new Sound[] { new Sound(370, 38),
								new Sound(370, 38), new Sound(370, 38),
								new Sound(370, 38), new Sound(370, 38) };
					}
				} else if (check.contains("staff")) {
					sounds[i] = new Sound[] { new Sound(395, 18),
							new Sound(395, 18), new Sound(395, 18),
							new Sound(395, 18), new Sound(0) };
				} else if (check.contains("mace")) {
					sounds[i] = new Sound[] { new Sound(399, 38),
							new Sound(400, 38), new Sound(399, 38),
							new Sound(400, 38), new Sound(387, 38) };
				} else if (check.contains("2h") || check.contains("godsword") || check.contains("saradomin sword") || check.contains("longsword")) {
					if (check.contains("dragon")) {
						sounds[i] = new Sound[] { new Sound(425, 18),
								new Sound(425, 38), new Sound(425, 38),
								new Sound(425, 38), new Sound(426, 38) };
					} else {
						sounds[i] = new Sound[] { new Sound(425, 18),
								new Sound(425, 38), new Sound(425, 38),
								new Sound(425, 38), new Sound(425, 38) };
					}
				} else if (check.contains("scimitar") || check.contains("sword")) {
					if (check.contains("dragon longsword")) {
						sounds[i] = new Sound[] { new Sound(398, 28),
								new Sound(396, 28), new Sound(396, 28),
								new Sound(396, 28), new Sound(390, 28) };
					} else if (check.contains("dragon scimitar")) {
						sounds[i] = new Sound[] { new Sound(396, 28),
								new Sound(396, 28), new Sound(396, 28),
								new Sound(396, 28), new Sound(396, 28) };
					} else {
						sounds[i] = new Sound[] { new Sound(396, 28),
								new Sound(396, 28), new Sound(396, 28),
								new Sound(396, 28), new Sound(396, 28) };
					}
				} else if (check.contains("dagger")) {
					if (check.contains("dragon dagger")) {
						sounds[i] = new Sound[] { new Sound(403, 18),
								new Sound(403, 18), new Sound(403, 18),
								new Sound(403, 18), new Sound(385, 18) };
					} else {
						sounds[i] = new Sound[] { new Sound(403, 18),
								new Sound(403, 18), new Sound(403, 18),
								new Sound(403, 18), new Sound(0) };
					}
				} else if (check.contains("shield")) {
					sounds[i] = new Sound[] { new Sound(1218, 18),
							new Sound(1069, 18), new Sound(1066, 18),
							new Sound(1052, 18), new Sound(798, 18),
							new Sound(791, 18), new Sound(790, 18),
							new Sound(786, 18), new Sound(785) };
				} else if (check.contains("shield")) {
					sounds[i] = new Sound[] { new Sound(1218, 18),
							new Sound(1069, 18), new Sound(1066, 18),
							new Sound(1052, 18), new Sound(798, 18),
							new Sound(791, 18), new Sound(790, 18),
							new Sound(786, 18), new Sound(785) };
				} else if (check.contains("defender")) {
					sounds[i] = new Sound[] { new Sound(785), new Sound(412) };
				} else if (check.contains("granite maul")) {
					sounds[i] = new Sound[] { new Sound(1079, 18),
							new Sound(1079, 18), new Sound(1079, 18),
							new Sound(1079, 18), new Sound(1082) };
				} else if (check.contains("dart") || check.contains("knife") || check.contains("javelin") || check.contains("thrown axe") || check.contains("throwing axe")) {
					sounds[i] = new Sound[] { new Sound(364, 38),
							new Sound(364, 38), new Sound(364, 38),
							new Sound(364, 38), new Sound(364, 38) };
				} else if (check.contains("pickaxe")) {
					sounds[i] = new Sound[] { new Sound(398, 18),
							new Sound(398, 38), new Sound(398, 38),
							new Sound(398, 38), new Sound(398, 38) };
				} else if (check.contains("hatchet") || check.contains("axe") || check.contains("mace")) {
					sounds[i] = new Sound[] { new Sound(399, 18),
							new Sound(399, 38), new Sound(399, 38),
							new Sound(399, 38), new Sound(399, 38) };
				} else if (check.contains("halberd")) {
					sounds[i] = new Sound[] { new Sound(420, 18),
							new Sound(420, 38), new Sound(420, 38),
							new Sound(420, 38), new Sound(420, 38) };
				} else if (check.contains("halberd")) {
					sounds[i] = new Sound[] { new Sound(420, 18),
							new Sound(420, 38), new Sound(420, 38),
							new Sound(420, 38), new Sound(420, 38) };
				} else if (check.contains("flail")) {
					sounds[i] = new Sound[] { new Sound(1059, 18),
							new Sound(1059, 38), new Sound(1059, 38),
							new Sound(1059, 38), new Sound(1059, 38) };
				} else if (check.contains("hammers")) {
					sounds[i] = new Sound[] { new Sound(1062, 18),
							new Sound(1062, 38), new Sound(1062, 38),
							new Sound(1062, 38), new Sound(1062, 38) };
				} else if (check.contains("spear")) {
					sounds[i] = new Sound[] { new Sound(1064, 18),
							new Sound(1064, 38), new Sound(1064, 38),
							new Sound(1064, 38), new Sound(1064, 38) };
				}
			}

		}

		//logger.info("Loaded weapon sounds.");
	}
}