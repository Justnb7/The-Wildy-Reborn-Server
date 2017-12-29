package com.venenatis.game.net.packet.in;

import com.venenatis.game.cache.definitions.AnyRevObjectDefinition;
import com.venenatis.game.content.chest.impl.crystal_chest.CrystalKeyReward;
import com.venenatis.game.content.chest.impl.shiny_chest.ShinyChest;
import com.venenatis.game.content.clicking.objects.ItemOnObjectInteract;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;

public class ItemOnObjectPacketHandler implements IncomingPacketListener {
	

	@Override@SuppressWarnings("unused")
	public void handle(final Player player, int packetType, int packetSize) {
		
		int interfaceType = player.getInStream().readUnsignedShort();
		final int id = player.getInStream().readSignedWordBigEndian();
		final int y = player.getInStream().readSignedWordBigEndianA();
		final int slot = player.getInStream().readSignedWordBigEndian();
		final int x = player.getInStream().readSignedWordBigEndianA();
		final int itemId = player.getInStream().readUnsignedShort();
		
		player.debug(String.format("Item on obj %d x:%d z:%d id:%d item: %d on slot:%d%n", packetType, x, y, id, itemId, slot));

		//The object definition
		AnyRevObjectDefinition def = AnyRevObjectDefinition.get(id);
		
		//The players height position
		int z = player.getLocation().getZ();
		
		//The location of the object
		final Location loc = Location.create(x, y, z);
		
		//The object
		final GameObject obj = RegionStoreManager.get().getGameObject(loc, id);
		
		player.faceObject(obj);
        
		//The item used on the object
		final Item item = player.getInventory().get(slot);

		if (item == null || item.getId() != itemId) {
			player.debug("for what ever reason the item is null");
			return;
		}
		
		if (player.getAttribute("busy") != null) {
			return;
		}
		
		if(def.getName().toLowerCase().contains("magic chest") && def.getActions()[0].toLowerCase().contains("open")) {
			ShinyChest.searchChest(player, item, slot);
			return;
		}
		
		if(def.getName().toLowerCase().contains("open chest") && def.getActions()[0].toLowerCase().contains("search")) {
			if (player.getInventory().contains(989)) {
				player.getInventory().remove(new Item(989, 1));
				new CrystalKeyReward(player).giveReward();
			} else {
				player.message("You need a Crystal Key to open this chest.");
				return;
			}
		}
		
		switch (item.getId()) {
		}
		
		ItemOnObjectInteract.handle(player, id, loc, item);
	}

}
