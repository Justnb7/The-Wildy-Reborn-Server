package com.model.game.character.player.packets.in;


import com.model.game.character.combat.Combat;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.bounty_hunter.BountyHunter;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendSoundPacket;
import com.model.game.item.ground.GroundItemHandler;
import com.model.task.impl.DistancedActionTask;

/**
 * Pickup Item
 */
public class PickupGroundItemPacketHandler implements PacketType {

	@Override
	public void processPacket(final Player player, int packetType, int packetSize) {
		final int y = player.getInStream().readSignedWordBigEndian();
		final int id = player.getInStream().readUnsignedWord();
		final int x = player.getInStream().readSignedWordBigEndian();
		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return;
		}
		if (Math.abs(player.getX() - x) > 25 || Math.abs(player.getY() - y) > 25 || player.playerIsFiremaking) {
			player.resetWalkingQueue();
			return;
		}
		Combat.resetCombat(player);

		if (player.onSpot(x, y, player.getZ())) {
			pickup(player, id, x, y, player.getZ());
		} else {
			player.setDistancedTask(new DistancedActionTask() {

				@Override
				public void onReach() {
					pickup(player, id, x, y, player.getZ());
					stop();
				}

				@Override
				public boolean reached() {
					return player.onSpot(x, y, player.getZ());
				}
			}.attach(player));
		}
	}

	/**
	 * Handles picking up the item
	 * 
	 * @param player
	 *            The {@link Player} picking up the item
	 * @param id
	 *            The id of the item
	 * @param amount
	 *            The amount of the item
	 * @param x
	 *            The x coordinate of the item
	 * @param y
	 *            The y coordinate of the item
	 * @param z
	 *            The z coordinate of the item
	 */
	private void pickup(Player player, int id, int x, int y, int z) {
		if (GroundItemHandler.get(id, x, y, z) != null) {
			player.write(new SendSoundPacket(356, 0, 0));
			GroundItemHandler.pickup(player, id, x, y, z);
			BountyHunter.determineWealth(player);
		}
	}
}
