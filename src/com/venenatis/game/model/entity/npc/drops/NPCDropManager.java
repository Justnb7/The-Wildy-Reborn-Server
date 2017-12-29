package com.venenatis.game.model.entity.npc.drops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Follower;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.Player;

/**
 * Manages all actions to do with NPC drops.
 * 
 * @author Stan
 */
public class NPCDropManager {
	
	public static Collection<Item> getDrops(Player player, NPC npc) {
		NPCDropDefinitions def = NPCDropDefinitions.get(npc.getId());
		return roll(player, npc.getId(), def);
	}
	
	public static Collection<Item> getDrops(Player player, int npcId) {
		NPCDropDefinitions def = NPCDropDefinitions.get(npcId);
		return roll(player, npcId, def);
	}
	
	public static Collection<Item> roll(Player player, int npcId, NPCDropDefinitions def) {
		List<Item> drops = new ArrayList<Item>();
		Random rand = new Random();
		
		if (def == null) {
			return drops;
		}
		
		for (NPCDrop drop : def.getAlwaysDrops())
			drops.add(new Item(drop.getItemId(), rand.nextInt(drop.getMaxAmount() - drop.getMinAmount() + 1) + drop.getMinAmount()));
		
		if (rand.nextInt(3) > 0 && def.getCommonDrops().length > 0) {
			NPCDrop drop = def.getCommonDrops()[rand.nextInt(def.getCommonDrops().length)];
			drops.add(new Item(drop.getItemId(), rand.nextInt(drop.getMaxAmount() - drop.getMinAmount() + 1) + drop.getMinAmount()));
		}
		
		List<NPCDrop> possibleHigherDrops = new ArrayList<NPCDrop>();
		
		int roll = rand.nextInt(100_000) + 1;
		
		for (NPCDrop drop : def.getAllHigherDrops()) {
			if (100_000 / roll >= drop.getChance()) {
				if (possibleHigherDrops.isEmpty()) {
					possibleHigherDrops.add(drop);
				} else if (possibleHigherDrops.get(0).getChance() <= drop.getChance()) {
					if (possibleHigherDrops.get(0).getChance() < drop.getChance())
						possibleHigherDrops.clear();
					possibleHigherDrops.add(drop);
				}
			}
		}
		
		if (!possibleHigherDrops.isEmpty())	{
			NPCDrop drop = possibleHigherDrops.get(rand.nextInt(possibleHigherDrops.size()));
			if (Pet.from(drop.getItemId()) != null) {
				boolean caught = false;
				for (int i : Follower.getItemAlternatives(drop.getItemId())) {
					if (player.getInventory().contains(i) || player.getBank().contains(i) || (player.getPet() > 0 && Pet.fromNpc(player.getPet()).getItem() == i)) {
						caught = true;
					}
				}
				if (player.getPetInsurance().isInsured(drop.getItemId()) || caught) {
					player.getActionSender().sendMessage("You have a funny feeling like you would have been followed...");
				} else if (player.getPet() <= 0) {
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed...");
					Follower.drop(player, new Item(drop.getItemId()), false);
				} else if (player.getInventory().getFreeSlots() > 0) {
					player.getActionSender().sendMessage("You feel something weird sneaking into your backpack...");
					player.getInventory().add(new Item(drop.getItemId()));
				} else {
					player.getActionSender().sendMessage("You have a funny feeling like you would have been followed...");
				}
			}
			drops.add(new Item(drop.getItemId(), rand.nextInt(drop.getMaxAmount() - drop.getMinAmount() + 1) + drop.getMinAmount()));
		}
		
		Item rdtItem = RareDropTable.RDTRoll(player, npcId);
		if (rdtItem != null)
			drops.add(rdtItem);
		
		return drops;
	}

}