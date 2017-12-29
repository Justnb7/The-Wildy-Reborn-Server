package com.venenatis.game.model.entity.npc.drops;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;

public enum RareDropTable {
	
	LAW_RUNE(563, 45, 64),
	DEATH_RUNE(560, 45, 64),
	NATURE_RUNE(561, 67, 43),
	STEEL_ARROW(886, 150, 64),
	RUNE_ARROW(886, 42, 64),
	UNCUT_SAPPHIRE(1623, 1, 1),
	UNCUT_EMERALD(1621, 1, 20),
	UNCUT_RUBY(1619, 1, 20),
	UNCUT_DIAMOND(1617, 1, 64),
	DRAGONSTONE(1631, 1, 64),
	RUNITE_BAR(2363, 1, 20),
	SILVER_ORE(443, 100, 64),
	COINS(995, 3000, 1),
	CHAOS_TALISMAN(1452, 1, 1),
	NATURE_TALISMAN(1462, 1, 20),
	LOOP_HALF_OF_KEY(987, 6, 1),
	TOOTH_HALF_OF_KEY(985, 6, 1),
	ADAMANT_JAVELIN(829, 20, 64),
	RUNE_JAVELIN(830, 5, 33),
	RUNE_2H_SWORD(1319, 1, 43),
	RUNE_BATTLEAXE(1373, 1, 43),
	RUNE_SQUARE_SHIELD(1185, 1, 64),
	RUNE_KITE_SHIELD(1201, 1, 128),
	DRAGON_MED_HELM(1149, 1, 128),
	RUNE_SPEAR(1247, 1, 137),
	SHIELD_LEFT_HALF(2366, 1, 273),
	DRAGON_SPEAR(1249, 1, 364);

	private int itemId;
	private int amount;
	private int chance;

	private RareDropTable(int itemId, int amount, int chance) {
		this.itemId = itemId;
		this.amount = amount;
		this.chance = chance;
	}

	public int getItemId() {
		return itemId;
	}

	public int getAmount() {
		return amount;
	}
	
	public int getChance() {
		return chance;
	}

	public static Item RDTRoll(Player player, int npcId) {
		Random rand = new Random();
		NPCDropDefinitions dropDef = NPCDropDefinitions.get(npcId);
		if (dropDef != null && dropDef.hasRdtAccess() && rand.nextInt(dropDef.getRdtChance()) == 0) {
			boolean row = false;
			if (player.getEquipment().isSlotUsed(EquipmentConstants.RING_SLOT) && 
					player.getEquipment().getItems()[EquipmentConstants.RING_SLOT].getDefinition().getName().contains("Ring of wealth")) {
				row = true;
			}
			
			List<RareDropTable> possibleDrops = new ArrayList<RareDropTable>();
			
			int roll = rand.nextInt(row ? 7_500 : 10_000) + 1;
			
			for (RareDropTable drop : values()) {
				if ((row ? 7_500 : 10_000) / roll >= drop.getChance() / 3) {
					if (possibleDrops.isEmpty()) {
						possibleDrops.add(drop);
					} else if (possibleDrops.get(0).getChance() <= drop.getChance()) {
						if (possibleDrops.get(0).getChance() < drop.getChance())
							possibleDrops.clear();
						possibleDrops.add(drop);
					}
				}
			}
			
			if (possibleDrops.isEmpty())
				return null;
			
			RareDropTable item = possibleDrops.get(rand.nextInt(possibleDrops.size()));			
			return new Item(item.getItemId(), item.getAmount());
		}
		return null;
	}
}
