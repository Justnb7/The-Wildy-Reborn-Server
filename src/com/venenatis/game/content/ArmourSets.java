package com.venenatis.game.content;

import java.util.Arrays;
import java.util.Optional;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;

/**
 * Handles opening of Armour sets
 * 
 * @author Daniel
 */
public enum ArmourSets {
	BRONZE_ARMOUR_LG(12960, new Item[] { new Item(1155), new Item(1117), new Item(1075), new Item(1189) }),
	BRONZE_ARMOUR_SK(12962, new Item[] { new Item(1155), new Item(1117), new Item(1087), new Item(1189) }),
	IRON_ARMOUR_LG(12972, new Item[] { new Item(1153), new Item(1115), new Item(1067), new Item(1191) }),
	IRON_ARMOUR_SK(12974, new Item[] { new Item(1153), new Item(1115), new Item(1081), new Item(1191) }),
	STEEL_ARMOUR_LG(12984, new Item[] { new Item(1157), new Item(1119), new Item(1069), new Item(1193) }),
	STEEL_ARMOUR_SK(12986, new Item[] { new Item(1157), new Item(1119), new Item(1083), new Item(1193) }),
	BLACK_ARMOUR_LG(12988, new Item[] { new Item(1165), new Item(1125), new Item(1077), new Item(1195) }),
	BLACK_ARMOUR_SK(12990, new Item[] { new Item(1165), new Item(1125), new Item(1089), new Item(1195) }),
	MITHRIL_ARMOUR_LG(13000, new Item[] { new Item(1159), new Item(1121), new Item(1071), new Item(1197) }),
	MITHRIL_ARMOUR_SK(13002, new Item[] { new Item(1159), new Item(1121), new Item(1085), new Item(1197) }),
	ADAMANT_ARMOUR_LG(13012, new Item[] { new Item(1161), new Item(1123), new Item(1073), new Item(1199) }),
	ADAMANT_ARMOUR_SK(13014, new Item[] { new Item(1161), new Item(1123), new Item(1091), new Item(1199) }),
	RUNE_ARMOUR_LG(13024, new Item[] { new Item(1163), new Item(1127), new Item(1079), new Item(1201) }),
	RUNE_ARMOUR_SK(13026, new Item[] { new Item(1163), new Item(1127), new Item(1093), new Item(1201) }),
	PROSELYTE_M(9666, new Item[] { new Item(9672), new Item(9674), new Item(9676) }),
	PROSELYTE_F(9670, new Item[] { new Item(9672), new Item(9674), new Item(9678) }),
	GREEN_DHIDE(12865, new Item[] { new Item(1065), new Item(1099), new Item(1135) }),
	BLUE_DHIDE(12867, new Item[] { new Item(2487), new Item(2493), new Item(2499) }),
	RED_DHIDE(12869, new Item[] { new Item(2489), new Item(2495), new Item(2501) }),
	BLACK_DHIDE(12871, new Item[] { new Item(2491), new Item(2497), new Item(2503) }),
	HALLOWEEN_SET(13175, new Item[] { new Item(1053), new Item(1055), new Item(1057) }),
	PARTY_HAT_SET(13173, new Item[] { new Item(1038), new Item(1040), new Item(1042), new Item(1044), new Item(1046), new Item(1048) }),
	GUTHANS(12873, new Item[] { new Item(4728), new Item(4730), new Item(4724), new Item(4726) }),
	VERACS(12875, new Item[] { new Item(4757), new Item(4759), new Item(4753), new Item(4755) }),
	DHAROKS(12877, new Item[] { new Item(4720), new Item(4722), new Item(4716), new Item(4718) }),
	TORAGS(12879, new Item[] { new Item(4749), new Item(4751), new Item(4745), new Item(4747) }),
	AHRIM(12881, new Item[] { new Item(4712), new Item(4714), new Item(4708), new Item(4710) }),
	KARIL(12883, new Item[] { new Item(4736), new Item(4738), new Item(4732), new Item(4734) });
	
	private final int armourId;

	private final Item items[];

	private ArmourSets(int armourId, Item... items) {
		this.armourId = armourId;
		this.items = items;
	}
	
	public static Optional<ArmourSets> forId(int id) {
		return Arrays.stream(values()).filter(a -> a.armourId == id).findAny();
	}

	public static boolean isSet(Player player, int item) {
		return ArmourSets.forId(item).isPresent();
	}
	
	public static void openSet(Player player, int item) {
		
		if (!ArmourSets.forId(item).isPresent()) {
			return;
		}
		
		ArmourSets data = ArmourSets.forId(item).get();

		if (player.getInventory().getFreeSlots() <= data.items.length) {
			player.getActionSender().sendMessage("You need at least " + data.items.length + " available inventory spaces to do this!");
			return;
		}
		
		player.getInventory().remove(data.armourId, 1);

		player.getInventory().add(data.items);

		player.getActionSender().sendMessage("You successfully open the " + Utility.formatEnumName(data.name()) + " set.");

		return;
	}
	
}