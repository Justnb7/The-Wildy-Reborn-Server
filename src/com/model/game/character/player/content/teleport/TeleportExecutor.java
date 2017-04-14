package com.model.game.character.player.content.teleport;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.teleport.Teleport.TeleportType;
import com.model.game.location.Location;
import com.model.task.ScheduledTask;



/**
 * Executes a new teleport.
 * @date Aug 24, 2015 10:03:43 PM
 *
 */
public class TeleportExecutor {
	
	/**
	 * Starts a teleport process for the player with just a location using the
	 * players current magic book to determine the type
	 * 
	 * @param player
	 *            The {@link Player} trying to teleport
	 * @param location
	 *            The {@link Location} the player is teleporting too
	 */
	public static void teleport(Player player, Location location) {
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST) {
			player.getActionSender().sendMessage("You can't teleport while being in a duel.");
			player.getActionSender().sendRemoveInterfacePacket();
			return;
		}
		//ken if statement, end dueling session if we aren't beyond request stage and want to teleport away
		if (Objects.nonNull(duelSession) && !(duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST)) {
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS); 
		}
		player.getFightCave().exitCave(2);
		player.getSkillCyclesTask().stop();
		TeleportType type = player.getSpellBook() == SpellBook.MODERN ? TeleportType.NORMAL : player.getSpellBook() == SpellBook.ANCIENT ? TeleportType.ANCIENT : player.usingObelisk ?  TeleportType.OBELISK : TeleportType.NORMAL;
		teleport(player, new Teleport(location, type), true);
	}
	
	/**
	 * Starts a teleport process for the player
	 * 
	 * @param player
	 *            The {@link Player} attempting to teleport
	 * @param teleport
	 *            The {@link Teleport} type to the location
	 */
	public static void teleport(Player player, Teleport teleport) {
		teleport(player, teleport, true);
	}

	/**
	 * Starts a teleport process for the player
	 * 
	 * @param player
	 *            The {@link Player} attempting to teleport
	 * @param teleport
	 *            The {@link Teleport} to the location
	 * @param requirements
	 *            Check requirements before attempting to teleport
	 */
	public static void teleport(Player player, Teleport teleport, boolean requirements) {
		if (player.teleTimer > 0) {
			return;
		}

		/**
		 * Check if we need to perform requirements on our teleport, if so
		 * validate our teleport, this allows us to perform an unchecked
		 * teleport for teleports such as the lever in the wilderness
		 */
		if (requirements && !canTeleport(player) && teleport.getType() != TeleportType.LEVER && !player.getRights().isAdministrator()) {
			return;
		}

		/**
		 * The start animation of our teleport
		 */
		final int startAnim = teleport.getType().getStartAnimation();

		/**
		 * The end animation of our teleport
		 */
		final int endAnim = teleport.getType().getEndAnimation();

		/**
		 * The start graphic of our teleport
		 */
		int startGraphic = teleport.getType().getStartGraphic();

		/**
		 * The end graphic of our teleport
		 */
		final int endGraphic = teleport.getType().getEndGraphic();

		/**
		 * The initial delay of our teleport, the time it takes to start the
		 * animation till your able to walk again
		 */
		final int initialDelay = teleport.getType().getStartDelay() + teleport.getType().getEndDelay();

		/**
		 * Check if we need to play our start animation
		 */
		if (startAnim != -1) {
			player.playAnimation(Animation.create(startAnim));
		}

		/**
		 * Check if we need to play our start graphic
		 */
		if (startGraphic != -1) {
			if (startGraphic > 65535) {
				startGraphic = (startGraphic - 65535);
				player.playGraphics(Graphic.create(startGraphic, 0, 0));
			} else {
				player.playGraphics(Graphic.create(startGraphic, 0, 0));
			}
		}
		Combat.resetCombat(player);
		player.stopMovement();
		player.getActionSender().sendRemoveInterfacePacket();
		player.getCombat().reset();
		player.resetFace();
		player.teleTimer = initialDelay;
		player.setTeleporting(true);
		Server.getTaskScheduler().schedule(new ScheduledTask(1) { 
			
			/**
			 * A modifiable end gfx due to the nature of having to finalize the
			 * included variables so we can differentiate between the height
			 * levels of the graphic
			 */
			int endGfx = endGraphic;

			@Override
			public void execute() {
				player.teleTimer--;
				if (player.teleTimer == initialDelay - teleport.getType().getStartDelay()) {

					/**
					 * Finalize our location by setting our coordinates
					 */
					player.move(new Location(teleport.getLocation().getX(), teleport.getLocation().getY(), teleport.getLocation().getZ()));

					/**
					 * Check if we need to play our end animation
					 */
					if (endAnim != -1) {
						player.playAnimation(Animation.create(endAnim));
					}

					/**
					 * Check if we need to play our end graphic
					 */
					if (endGfx != -1) {
						if (endGfx > 65535) {
							// differentiate between height levels
							endGfx = (endGfx - 65535);
							player.playGraphics(Graphic.create(endGfx, 0, 0));
						} else {
							player.playGraphics(Graphic.create(endGfx, 0, 0));
						}
					}

				}
				
				if (player.teleTimer == 0) {
					player.teleTimer = -1;
					player.setTeleporting(false);
					stop();
				}
			}
			
		}.attach(player));
	}

	/**
	 * Determines if the player can teleport under the current circumstances
	 * 
	 * @param player
	 *            The {@link Player} attempting to teleport
	 * @param teleport
	 *            The {@link Teleport} the player is currently doing
	 * @return If the player can teleport
	 */
	public static boolean canTeleport(Player player) {
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST) {
			player.getActionSender().sendMessage("You can't teleport while being in a duel.");
			return false;
		}
		if (!player.teleblock.elapsed(player.teleblockLength)) {
			player.getActionSender().sendMessage("You are teleblocked and can't teleport.");
			return false;
		}
		if (player.getArea().inWild() && player.wildLevel > 20 && !player.usingObelisk && !isUsingLever()) {
			player.getActionSender().sendMessage("You can't teleport above level " + 20 + " in the wilderness.");
			return false;
		}
			
		if (!player.lastSpear.elapsed(4000)) {
			player.getActionSender().sendMessage("You are stunned and can not teleport!");
			return false;
		}
		if (player.isBusy()) {
			player.getActionSender().sendMessage("Please finish what you're doing first.");
			return false;
		}
		if (player.isDead || player.isTeleporting()) {
			return false;
		}
		if (!player.teleblock.elapsed(player.teleblockLength)) {
			player.getActionSender().sendMessage("You are teleblocked and can't teleport.");
			return false;
		}
		if (!player.getCombat().noTarget()) {
			Combat.resetCombat(player);
		}
		player.getActionSender().sendRemoveInterfacePacket();
		player.getCombat().reset();
		player.faceEntity(player);
		return true;
	}
	
	private static boolean lever = false;
	
	private static boolean isUsingLever() {
		return lever;
	}
	
	/**
	 * Executes the teleport action when the player pulls a lever.
	 * @param player
	 */
	public static void executeLeverTeleport(Player player, Teleport teleport) {
		player.playAnimation(Animation.create(2140));
		player.getActionSender().sendMessage("You pull the lever...");
		lever = true;
		Server.getTaskScheduler().schedule(new ScheduledTask(2) { 
		@Override
		public void execute() {
			teleport(player, teleport);
			lever = false;
			if (player.teleTimer > 0)
				stop();
		}
		});
	}
	
	public static void executeObeliskTeleport(Player player, Teleport teleport) {
		player.playAnimation(Animation.create(2140));
		Server.getTaskScheduler().schedule(new ScheduledTask(1) { 
		@Override
		public void execute() {
			teleport(player, teleport);
			if (player.teleTimer > 0)
				stop();
		}
		});
	}
}
