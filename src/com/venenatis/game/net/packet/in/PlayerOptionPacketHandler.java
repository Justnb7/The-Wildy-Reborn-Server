package com.venenatis.game.net.packet.in;

import static com.google.common.base.Preconditions.checkState;

import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelRule;
import com.venenatis.game.location.Area;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.task.impl.DistancedActionTask;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class PlayerOptionPacketHandler implements PacketType {
	
	 /**
     * Send when a player uses the attack right-click option on another player.
     */
    public static final int ATTACK_PLAYER = 73;
    
    /**
     * Sent when a player requests a trade with another player.
     */
    public static final int TRADE_REQUEST = 139;
    
    public static final int DUEL_REQUEST = 153;
    
    /**
     * Sent when a player uses the right-click challenge option to challenge
     * another player.
     */
    public static final int ACCEPT_CHALLENGE = 128;
	
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
     * OP 1 = dueling (128)
     * OP 2 = UNK (153)
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
		
		player.debug(String.format("[PlayerInteractionPacket] Opcode: ", packet));
		
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

		case ACCEPT_CHALLENGE:
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
		final int otherPlayerIndex = player.getInStream().readUnsignedWord();
		
		checkState(World.getWorld().getPlayers().search(otherPlayerIndex).isPresent(), "%s tried dueling a player that doesn't exist. [index= %s]", player.getUsername(), otherPlayerIndex);

		if (!player.canDuel()) {
			player.getActionSender().sendMessage("You are currently already in a session.");
			return;
		}

		Player other = World.getWorld().getPlayers().search(otherPlayerIndex).get();
		
		if (!other.canDuel()) {
			player.getActionSender().sendMessage("The other player is currently busy.");
			return;
		}

		player.setDistancedTask(new DistancedActionTask() {
			@Override
			public void onReach() {
				player.setOtherPlayerDuelIndex(otherPlayerIndex);
				player.getDuelArena().requestDuel(other);
				stop();
			}

			@Override
			public boolean reached() {
				player.face(player, other.getLocation());
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
		follower.setFollowing(leader);
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

		if (player.getDuelArena().isDueling()) {
			if (player.getDuelArena().getWaitTime() > 0) {
				player.getActionSender().sendMessage("The duel has not started yet.");
				return;
			}
		}

		if (!player.getController().canAttackPlayer(player, other)) {
			return;
		}
		
		if (!Area.inWilderness(player) && !player.getDuelArena().isDueling()) {
			player.getActionSender().sendMessage("You're not in the wilderness.");
			player.getWalkingQueue().reset();
			Combat.resetCombat(player);
			return;
		}
		
		if (!Area.inWilderness(other) && !player.getDuelArena().isDueling()) {
			player.getActionSender().sendMessage(Utility.formatName(other.getUsername()) + " is not in the wilderness.");
			player.getWalkingQueue().reset();
			Combat.resetCombat(player);
			return;
		}

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
		
		if (player.getDuelArena().isDueling()) {
			if (player.getDuelArena().getWaitTime() > 0) {
				player.getActionSender().sendMessage("The duel has not started yet.");
				return;
			}
		}
		
		if (player.getDuelArena().isDueling()) {
			if (player.getDuelArena().getRules().get(DuelRule.MAGIC)) {
				player.getActionSender().sendMessage("Magic is disabled in this duel.");
				return;
			}
		}

		if (!Area.inWilderness(other) && !MinigameHandler.execute(other, true, $it -> $it.canAttack(other))) {
			player.getActionSender().sendMessage(Utility.formatName(other.getUsername()) + " is currently in a safe zone and can not be attacked.");
			return;
		}

		if (!Area.inWilderness(player) && !MinigameHandler.execute(player, true, $it -> $it.canAttack(player))) {
			player.getActionSender().sendMessage("You can not attack players while in a safe zone!");
			return;
		}

		if (!player.getController().canAttackPlayer(player, other)) {
			return;
		}

		for (int spellId = 0; spellId < player.MAGIC_SPELLS.length; spellId++) {
			if (spell == player.MAGIC_SPELLS[spellId][0]) {
				player.debug("using spell: "+spellId);
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
	}
	
	private void handleTradeRequest(Player player, int packet) {
		final int otherPlayerTradeIndex = player.getInStream().readSignedWordBigEndian();
		
		checkState(World.getWorld().getPlayers().search(otherPlayerTradeIndex).isPresent(), "%s tried trading a player that doesn't exist. [index= %s]", player.getUsername(), otherPlayerTradeIndex);

		/*if ((Boolean) player.getAttributes().get(PlayerAttributes.TRADING)) {
			return;
		}*/

		if (otherPlayerTradeIndex == player.getIndex()) {
			return;
		}

		Player other = World.getWorld().getPlayers().search(otherPlayerTradeIndex).get();

		if (!other.isRegistered() || other.isTeleporting() || other.getCombatState().isDead()) {
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
				player.face(player, other.getLocation());
				return player.distanceToPoint(other.getX(), other.getY()) < 2;
			}

		});

	}

}
