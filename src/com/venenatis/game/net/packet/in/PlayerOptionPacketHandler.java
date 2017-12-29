package com.venenatis.game.net.packet.in;

import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import com.venenatis.game.content.minigames.multiplayer.duel_arena.DuelArena.DuelOptions;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.boudary.BoundaryManager;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.task.impl.DistancedActionTask;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class PlayerOptionPacketHandler implements IncomingPacketListener {
	
	 /**
     * Send when a player uses the attack right-click option on another player.
     */
    public static final int ATTACK_PLAYER = 73;
    
    /**
     * Sent when a player requests a trade with another player.
     */
    public static final int TRADE_REQUEST = 139;
    
    /**
     * Sent when a player uses the right-click challenge option to challenge
     * another player.
     */
    public static final int DUEL_REQUEST = 153;
	
	/**
     * Sent when a player attempts to cast magic on another player.
     */
    public static final int MAGIC_ON_PLAYER = 249;

    /**
     * Sent when a player uses the right-click follow option to follow another
     * player.
     */
	private static final int FOLLOW_PLAYER = 39;
    
    /**
     * OP 1 = Unknown
     * OP 2 = Dueling send request (153)
     * OP 3 = Attack packet (73)
     * OP 4 = Follow packet (39)
     * OP 5 = Trade with packet (139)
     */

	@Override
	public void handle(Player player, int packet, int size) {
		checkState(player != null, "Player is null");
		
		if (player.getCombatState().isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
			return;
		}
		
		if (player.getAttribute("busy") != null) {
			return;
		}
		
		player.debug(String.format("[PlayerInteractionPacket] Opcode: %d%n", packet));
		
		if (player.getCombatState().inCombat()) {
			if (packet != ATTACK_PLAYER && packet != MAGIC_ON_PLAYER) {
				player.getCombatState().reset();
			}
		}
		
		switch (packet) {
		case ATTACK_PLAYER:
			handleAttackPlayer(player, packet);
			break;

		case MAGIC_ON_PLAYER:
			handleMagicOnPlayer(player, packet);
			break;

		case DUEL_REQUEST:
			handleDuelRequest(player, packet);
			break;

		case FOLLOW_PLAYER:
			handleFollowPlayer(player, packet);
			break;

		case TRADE_REQUEST:
			handleTradeRequest(player, packet);
			break;

		}
	}
	
	private void handleDuelRequest(Player player, int packet) {
		final int otherPlayerIndex = player.getInStream().readSignedWordBigEndian();
		player.getDuelArena().setOtherPlayer(World.getWorld().getPlayers().get(otherPlayerIndex));
		
		final Player other = player.getDuelArena().getOtherPlayer();
		
		if (other == null || Objects.equals(player, other)) {
			player.message("Either the requester or the requestee is null, notify an Administrator.");
			return;
		}
		
		player.faceEntity(other);
		
		player.setDistancedTask(new DistancedActionTask() {
			@Override
			public void onReach() {
				if (!BoundaryManager.isWithinBoundary(other.getLocation(), "DuelArena")) {
					player.getActionSender().sendMessage("Unable to find the requestee.");
					return;
				}
				if (!player.getDuelArena().canChallenge()) {
					return;
				}
				player.getDuelArena().sendChallenge();
				stop();
			}

			@Override
			public boolean reached() {
				player.face(other.getLocation());
				return player.distanceToPoint(other.getX(), other.getY()) < 2;
			}
		});
	}

	private void handleFollowPlayer(Player follower, int packet) {
		final int otherPlayerIndex = follower.getInStream().readUnsignedWordBigEndian();
		
		checkState(World.getWorld().getPlayers().search(otherPlayerIndex).isPresent(), "%s tried following a player that doesn't exist. [index= %s]", follower.getUsername(), otherPlayerIndex);

		if (follower.isTrading()) {			
			follower.getActionSender().sendMessage("Please close what you're doing before trying to follow a player.");
			return;
		}

		final Player leader = World.getWorld().getPlayers().search(otherPlayerIndex).get();

		follower.getCombatState().reset();
		follower.following().setFollowing(leader);
		follower.faceEntity(leader);
	}
	
	/**
	 * Handles the event of a {@link Player} attacking another {@link Player}.
	 * 
	 * @param player
	 *            The attacking player.
	 * 
	 * @param packet
	 *            The packet for this action.
	 */
	private void handleAttackPlayer(Player player, int packet) {
		int otherPlayerIndex = player.getInStream().readSignedWordBigEndian();
		
		checkState(World.getWorld().getPlayers().search(otherPlayerIndex).isPresent(), "%s tried attacking a player that doesn't exist. [index= %s]", player.getUsername(), otherPlayerIndex);
				
		Player other = World.getWorld().getPlayers().search(otherPlayerIndex).get();

		if (!player.getController().canAttackPlayer(player, other)) {
			return;
		}
		
		if (player.getTimedAttribute("duel_count") != null || other.getTimedAttribute("duel_count") != null) {
			player.getCombatState().reset();
			player.getActionSender().sendMessage("The duel has not started yet!");
			return;
		}
		
		if (player.getDuelArena().getOptionActive()[DuelOptions.NO_MELEE.getId()] && player.getCombatType() == CombatStyle.MELEE) {
			player.getActionSender().sendMessage("You can't use melee during this stake.");
			player.getCombatState().reset();
			return;
		}
		
		if (player.getDuelArena().getOptionActive()[DuelOptions.NO_RANGED.getId()] && player.getCombatType() == CombatStyle.RANGE) {
			player.getActionSender().sendMessage("You can't use range during this stake.");
			player.getCombatState().reset();
			return;
		}

		player.faceEntity(other);
		player.getCombatState().setTarget(other);
	}
	
	/**
	 * Handles the event of a {@link Player} using magic on another
	 * {@link Player}.
	 * 
	 * @param player
	 *            The player using magic on another player.
	 * 
	 * @param packet
	 *            The packet for this action.
	 */
	private void handleMagicOnPlayer(Player player, int packet) {
		int otherPlayerIndex = player.getInStream().readSignedWordA();
		
		checkState(World.getWorld().getPlayers().search(otherPlayerIndex).isPresent(), "%s tried casting spells on a player that doesn't exist. [index= %s]", player.getUsername(), otherPlayerIndex);
		
		Player other = World.getWorld().getPlayers().search(otherPlayerIndex).get();	
		
		final int spell = player.getInStream().readSignedWordBigEndian();

		if (!BoundaryManager.isWithinBoundary(other.getLocation(), "PvP Zone")) {
			player.getActionSender().sendMessage(Utility.formatName(other.getUsername()) + " is currently in a safe zone and can not be attacked.");
			return;
		}

		if (!BoundaryManager.isWithinBoundary(player.getLocation(), "PvP Zone")) {
			player.getActionSender().sendMessage("You can not attack players while in a safe zone!");
			return;
		}

		if (!player.getController().canAttackPlayer(player, other)) {
			return;
		}
		
		if (player.getDuelArena().getOptionActive()[DuelOptions.NO_MAGIC.getId()]) {
			player.getActionSender().sendMessage("You can't use magic during this stake.");
			player.getCombatState().reset();
			return;
		}

		for (int spellId = 0; spellId < player.MAGIC_SPELLS.length; spellId++) {
			if (spell == player.MAGIC_SPELLS[spellId][0]) {
				player.setSpellId(spellId);
				player.setCombatType(CombatStyle.MAGIC);
				break;
			}
		}
		
		if (other.getCombatState().isTeleblocked() && player.MAGIC_SPELLS[player.getSpellId()][0] == 12445) {
			player.getActionSender().sendMessage("That player is already affected by this spell.");
			player.getWalkingQueue().reset();
			Combat.resetCombat(player);
		}
		
		if (player.getCombatType() == CombatStyle.MAGIC) {
			player.getCombatState().setTarget(other);
		} else {
			System.err.println("Unsupported combat situation, is the spell you're using supported?");
		}
		
		player.faceEntity(other);
		player.getWalkingQueue().reset();
	}
	
	private void handleTradeRequest(Player player, int packet) {
		final int otherPlayerTradeIndex = player.getInStream().readSignedWordBigEndian();
		
		checkState(World.getWorld().getPlayers().search(otherPlayerTradeIndex).isPresent(), "%s tried trading a player that doesn't exist. [index= %s]", player.getUsername(), otherPlayerTradeIndex);

		if (otherPlayerTradeIndex == player.getIndex()) {
			return;
		}

		Player other = World.getWorld().getPlayers().search(otherPlayerTradeIndex).get();

		if (!other.isRegistered() || other.getTeleportAction().isTeleporting() || other.getCombatState().isDead()) {
			return;
		}
		
		player.setDistancedTask(new DistancedActionTask() {
			@Override
			public void onReach() {
				player.setOtherPlayerTradeIndex(otherPlayerTradeIndex);
				player.getTradeSession().requestTrade(player, other);
				stop();
			}

			@Override
			public boolean reached() {
				player.face(other.getLocation());
				return player.distanceToPoint(other.getX(), other.getY()) < 2;
			}

		});

	}

}
