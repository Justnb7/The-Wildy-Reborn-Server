package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.clicking.objects.ItemOnObjectInteract;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;

public class ItemOnObjectPacketHandler implements PacketType {
	

	@Override@SuppressWarnings("unused")
	public void handle(final Player player, int packetType, int packetSize) {
		
		int interfaceType = player.getInStream().readUnsignedWord();
		final int id = player.getInStream().readSignedWordBigEndian();
		final int y = player.getInStream().readSignedWordBigEndianA();
		final int slot = player.getInStream().readUnsignedWord();
		final int x = player.getInStream().readSignedWordBigEndianA();
		final int itemId = player.getInStream().readUnsignedWord();
		
		System.out.printf("Item on obj %d x:%d z:%d id:%d%n", packetType, x, y, id);

		int z = player.getLocation().getZ();
		final Location loc = Location.create(x, y, z);
        
        final Item item = player.getInventory().get(slot);
        if(item == null) {
        	player.debug("for what ever reason the item is null");
        	return;
        }
        player.face(player, loc);
		ItemOnObjectInteract.handle(player, id, loc, item);
	}

}
