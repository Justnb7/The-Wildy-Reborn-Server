package com.venenatis.game.content.teleportation;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.magic.spell.SpellBook;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.Task.BreakType;
import com.venenatis.game.task.Task.StackType;
import com.venenatis.server.Server;

public class Teleport {
	
	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * The Magic skill.
	 * 
	 * @param player
	 */
	public Teleport(Player player) {
		this.player = player;
	}
	
	/**
	 * Check to see if player is teleporting.
	 */
	private boolean isTeleporting = false;
	
	/**
	 * Activate the players teleporting boolean
	 * 
	 * @param isTeleporting
	 */
	public void setTeleporting(boolean isTeleporting) {
		this.isTeleporting = isTeleporting;
	}
	
	/**
	 * Checks if we're teleporting
	 * @return {@code true} if we are, {@code false} otherwise.
	 */
	public boolean isTeleporting() {
		return isTeleporting;
	}

	/**
	 * Enum for all the teleporting types.
	 *
	 */
	public enum TeleportTypes {
		SPELL_BOOK,
		MODERN,
		LUNAR,
		ANCIENT,
		TABLET,
		TELEOTHER,
		OBELISK,
		LEVER;
	}
	
	/**
	 * The buttons stored in an array
	 */
	public static final int[] teleport_button_ids = new int[] {
			117048, 75010, 84237, 4171, 50056, 4140, 4143, 4146, 4150, 6004, 6005, 29031, 72038, 50235, 50245, 50253, 51005, 51013, 51023, 51031, 51039, 117112, 117154, 117162, 117123, 117131
	};

	/**
	 * Are we clicking on an actual teleport button
	 * 
	 * @param player
	 *            The player trying to teleport
	 * @param button
	 *            The button being pressed
	 */
	public static boolean isTeleportButton(Player player, int button) {
		for (int btn : teleport_button_ids) {
			if (button == btn) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if player is allowed to teleport.
	 * 
	 * @return
	 */
	public boolean canTeleport(boolean override) {
		if (!player.getController().canTeleport()) {
			return false;
		}

		if (isTeleporting()) {
			return false;
		}

		if (!MinigameHandler.execute(player, true, $it -> $it.canTeleport(player))) {
			return false;
		}

		if (player.getDuelArena().isDueling()) {
			player.getActionSender().sendMessage("You cannot teleport while you are dueling.");
			return false;
		}

		if (!override) {
			if (player.getWildLevel() > 30 && !player.getRights().isOwner(player)) {
				player.getActionSender().sendMessage("You can not teleport past 30 wilderness!");
				return false;
			}
		}

		if (player.getCombatState().isTeleblocked()) {
			player.getActionSender().sendMessage("You are currently teleblocked and can not teleport!");
			return false;
		}

		return true;
	}
	
	/**
	 * Handles clicking buttons for Magic.
	 * 
	 * @param button
	 * @return
	 */
	public boolean handleButtons(int button) {
		switch (button) {

		/* Home Telport */
		case 75010:
		case 21741:
		case 117048:
			player.getTeleportAction().teleport(Constants.RESPAWN_PLAYER_LOCATION, TeleportTypes.SPELL_BOOK, false);
			break;
			
			//TODO add other teleport types such as pvp minigames etc
		}
		return false;
	}
	
	/**
	 * Handles teleporting a player to a location.
	 * 
	 * @param location
	 * @param teleportType
	 */
	public void teleport(final Location location, TeleportTypes teleportType, boolean override) {
		if (!canTeleport(override)) {
			return;
		}
		
		TeleportTypes type = teleportType;

		if (type == TeleportTypes.SPELL_BOOK) {
			if (player.getSpellBook() == SpellBook.MODERN_MAGICS) {
				type = TeleportTypes.MODERN;
			} else if (player.getSpellBook() == SpellBook.ANCIENT_MAGICKS) {
				type = TeleportTypes.ANCIENT;
			} else if (player.getSpellBook() == SpellBook.LUNAR_MAGICS) {
				type = TeleportTypes.MODERN;
			} else {
				type = TeleportTypes.MODERN;
			}
		}

		final Teleportation data = Teleportation.forTeleport(type);

		if (data == null) {
			return;
		}

		player.getWalkingQueue().lock(10, false);
		player.getActionSender().removeAllInterfaces();
		player.playAnimation(data.getStartAnimation());
		player.playGraphics(data.getStartGraphic());
		player.getActionSender().sendSound(data.getSound(), 0, 0);

		setTeleporting(true);

		Server.getTaskScheduler().submit(new Task(player, data.getDelay(), false, StackType.STACK, BreakType.NEVER) {
			@Override
			public void execute() {
				player.setTeleportTarget(location);
				player.playAnimation(data.getEndAnimation());
				player.playGraphics(data.getEndGraphic());
				stop();
			}

			@Override
			public void onStop() {
				setTeleporting(false);
				player.getWalkingQueue().lock(0, false);
			}
		});
	}

	public void teleport(final Location location) {
		final Teleportation data = Teleportation.forTeleport(TeleportTypes.MODERN);

		if (data == null) {
			return;
		}

		player.getWalkingQueue().lock(10, false);
		player.getActionSender().removeAllInterfaces();
		player.playAnimation(data.getStartAnimation());
		player.playGraphics(data.getStartGraphic());
		player.getActionSender().sendSound(data.getSound(), 0, 0);

		setTeleporting(true);

		Server.getTaskScheduler().submit(new Task(player, data.getDelay(), false, StackType.STACK, BreakType.ON_MOVE) {
			@Override
			public void execute() {
				player.setTeleportTarget(location);
				player.playAnimation(data.getEndAnimation());
				player.playGraphics(data.getEndGraphic());
				stop();
			}

			@Override
			public void onStop() {
				setTeleporting(false);
				player.getWalkingQueue().lock(0, false);
			}
		});
	}
}
