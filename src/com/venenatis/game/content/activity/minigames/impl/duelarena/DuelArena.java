package com.venenatis.game.content.activity.minigames.impl.duelarena;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.activity.minigames.Minigame;
import com.venenatis.game.content.activity.minigames.MinigameType;
import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;
import com.venenatis.game.location.impl.SquareArea;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.definitions.EquipmentDefinition;
import com.venenatis.game.model.definitions.WeaponDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.PlayerOption;
import com.venenatis.game.model.entity.player.save.PlayerSave;
import com.venenatis.game.task.impl.DeathTask;
import com.venenatis.game.util.StringUtils;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.clipmap.Region;

/**
 * Represents a minigame in which {@link Player}s fight in a arena and can choose to stake their items for their opponents.
 *
 * @author Seven
 */
public final class DuelArena extends Minigame {

	/**
	 * The enumerated types of stages for dueling.
	 *
	 * @author SeVen
	 */
	public enum DuelStage {

		/**
		 * The stage before the session is created.
		 */
		REQUEST,

		/**
		 * The first stage in which both players are able to offer items to
		 * stake.
		 */
		FIRST_SCREEN,

		/**
		 * The second stage in which both players carefully look over the items being staked and the rules or this stake.
		 */
		SECOND_SCREEN,

		/**
		 * The third stage in which players fight in the arena to win any items that were staked.
		 */
		ARENA,

		/**
		 * The fourth and last stage in which rewards are given to the player who
		 * won the stake.
		 */
		REWARD;
	}

	/**
	 * The default locations a player will respawn to if they died in the duel
	 * arena.
	 */
	public static final ImmutableList<SquareArea> RESPAWN_LOCATIONS = ImmutableList.of(
			new SquareArea(3356, 3268, 3359, 3274),
			new SquareArea(3360, 3274, 3378, 3277),
			new SquareArea(3355, 3274, 3357, 3278),
			new SquareArea(3373, 3264, 3373, 3268));
	
	/**
	 * The collection of arena coordinates.
	 */
	public static final ImmutableList<Area> ARENAS = ImmutableList.of(new SquareArea("North West Arena", 3331, 3243, 3359, 3259), new SquareArea("South West Arena", 3331, 3205, 3358, 3219), new SquareArea("East Arena", 3363, 3226, 3390, 3240));
	
	public static final ImmutableList<Area> OBSTACLE_ARENAS = ImmutableList.of(new SquareArea("North West Obstacle Arena", 3331, 3224, 3358, 3238), new SquareArea("South East Obstacle Arena", 3362, 3205, 3390, 3221), new SquareArea("North East Obstacle Arena", 3363, 3245, 3390, 3259));

	/**
	 * The player initiating the duel.
	 */
	private Player player;
	
	/**
	 * The other person in this session.
	 */
	private Optional<Player> other;
	
	/**
	 * The current stage of this duel.
	 */
	private DuelStage stage = DuelStage.REQUEST;
	
	/**
	 * The flag that denotes this {@code player} sent a duel request.
	 */
	private boolean duelRequest;
	
	/**
	 * The rules for this duel.
	 */
	private DuelRules rules = new DuelRules();

	/**
	 * The time required in seconds before players can move and attack each
	 * other in the arena.
	 */
	private int waitCounter = 5;
	
	/**
	 * The time in seconds it took to kill a player in the arena.
	 */
	private int arenaTime = 0;
	
	/**
	 * The flag that denotes this {@code player} won the duel.
	 */
	private boolean won;
	
	/**
	 * The flag that denotes a {@code player} during the first or second screen stages.
	 */
	private boolean accepted;
	
	/**
	 * The items that were won during the stake.
	 */
	private Item[] reward;

	/**
	 * Creates a new {@link DuelArena}.
	 *
	 * @param player The player initiating the duel.
	 */
	public DuelArena(Player player) {
		super("Dueling", RunType.STANDALONE, MinigameType.PVP, Classification.SAFE);
		this.player = player;
	}

	/**
	 * Determines if a player is in a arena.
	 */
	public static boolean inArena(Player player) {
		return ARENAS.stream().anyMatch($it -> $it.inArea(player.getLocation()));
	}

	/**
	 * Determines if a player is in a obstacle arena.
	 */
	public static boolean inObstacleArena(Player player) {
		return OBSTACLE_ARENAS.stream().anyMatch($it -> $it.inArea(player.getLocation()));
	}

	/**
	 * Sends a request duel request to another player.
	 *
	 * @param other The other player receiving the message.
	 */
	public void requestDuel(Player other) {
		if (player.equals(other)) {
			player.getActionSender().sendMessage("You cannot duel yourself.");
			return;
		}

		if (isDueling()) {
			player.getActionSender().sendMessage("You are already dueling.");
			return;
		}

		if (other.getDuelArena().getStage() != DuelStage.REQUEST) {
			player.getActionSender().sendMessage("That player is currently busy.");
			return;
		}

		if (stage != DuelStage.REQUEST) {
			player.getActionSender().sendMessage("You are currently busy and cannot request a duel.");
			return;
		}

		if (validRequest(other)) {			
			this.other = Optional.of(other);
			other.getDuelArena().setOther(Optional.of(player));	
			
			this.player.getDuelContainer().setOther(this.other);
			this.other.get().getDuelContainer().setOther(Optional.of(this.player));
			
			setDuelRequest(false);
			
			this.stage = DuelStage.FIRST_SCREEN;
			other.getDuelArena().setStage(DuelStage.FIRST_SCREEN);			
		
			resetRules();
			other.getDuelArena().resetRules();
			
			this.player.getDuelContainer().clear(true);			
			other.getDuelContainer().clear(true);
			
			execute(stage);
			other.getDuelArena().execute(stage);
			return;
		}
			player.getActionSender().sendMessage("Sending duel offer...");
			other.getActionSender().sendMessage(player.getUsername() + ":duelreq:");
			setDuelRequest(true);
			stage = DuelStage.REQUEST;
	}

	private boolean validRequest(Player other) {
		return player.getOtherPlayerDuelIndex() == other.getIndex()
				&& other.getOtherPlayerDuelIndex() == player.getIndex() && other.getDuelArena().isDuelRequest();
	}

	/**
	 * Executes the various stages throughout dueling.
	 *
	 * @param stage The current stage of this duel.
	 */
	public void execute(DuelStage stage) {
		switch (stage) {
			case REQUEST:
				break;

			case FIRST_SCREEN:
				player.getActionSender().sendString("Dueling with: " + player.getRights().getStringForRights(player) + " " + Utility.formatName(other.get().getUsername()), 31005);
				player.getActionSender().sendString("Opponent's combat level: <col=ff7000>" + other.get().getCombatLevel(), 31006);
				player.getActionSender().sendString("", 31009);
				player.getActionSender().sendItemOnInterface(3322, player.getInventory().toArray());
				player.getActionSender().sendItemOnInterface(InterfaceConstants.PLAYER_STAKE_CONTAINER, new Item[]{});
				player.getActionSender().sendItemOnInterface(InterfaceConstants.OTHER_STAKE_CONTAINER, new Item[]{});
				player.getActionSender().sendInterfaceWithInventoryOverlay(InterfaceConstants.FIRST_DUEL_SCREEN, 3321);
				break;

			case SECOND_SCREEN:
				player.getActionSender().sendString("Some worn items will be taken off.", 31505);
				player.getActionSender().sendString("Boosted stats will be restored.", 31506);
				player.getActionSender().sendString("Existing prayers will be stopped.", 31507);
				IntStream.range(31509, 31520).forEach(i -> player.getActionSender().sendString("", i)); // rules
				IntStream.range(DuelRule.RANGED.ordinal(), DuelRule.RINGS.ordinal()).filter($it -> rules.get(DuelRule.values()[$it])).forEach($it -> player.getActionSender().sendString(getRuleText(DuelRule.values()[$it]), 31509 + $it));
				IntStream.range(31531, 31560).forEach(i -> player.getActionSender().sendString("", i));
				IntStream.range(31561, 31589).forEach(i -> player.getActionSender().sendString("", i));
				player.getActionSender().sendString(StringUtils.getItemNames(player.getDuelContainer().toArray()), 31531);
				player.getActionSender().sendString(StringUtils.getItemNames(other.get().getDuelContainer().toArray()), 31561);
				player.getActionSender().sendString("", 31526);
				player.getActionSender().sendItemOnInterface(3322, player.getInventory().toArray());
				player.getActionSender().sendInterface(InterfaceConstants.SECOND_DUEL_SCREEN);
				break;

			case ARENA:
				break;

			case REWARD:
				player.getActionSender().sendString("<col=E1981F>Total Value: " + other.get().getDuelContainer().containerValue(), 31709);
				player.getActionSender().sendString(Utility.formatName(other.get().getUsername()), 31706);
				player.getActionSender().sendString("" + other.get().getCombatLevel(), 31707);
				player.getActionSender().sendInterface(31700);
				break;

			default:
				throw new IllegalArgumentException("Invalid duel stage!");
		}
		
		heal(other.get());
		heal(player);

	}
	
	private void heal(Player heal) {
		
		/*Heal the player before entering the duel*/
		heal.getSkills().setLevel(Skills.HITPOINTS, heal.getSkills().getLevelForExperience(Skills.HITPOINTS));
		
		/*Recharge prayer points before entering the duel*/
		heal.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
		heal.getSkills().setPrayerPoints(player.getSkills().getLevelForExperience(Skills.PRAYER), true);
		
		/*Reset the players run energy*/
		heal.setRunEnergy(100);
		heal.getActionSender().sendRunEnergy();
		
		/*Reset the players special attack back to 100%*/
		heal.setSpecialAmount(100);
		heal.getSpecial().update();
	}

	/**
	 * Gets the text for a {@code rule}.
	 *
	 * @return The text for a specified {@code rule}.
	 */
	private String getRuleText(DuelRule rule) {
		switch(rule) {
			case RANGED:
				return "You can not use ranged attacks.";
			case MELEE:
				return "You can not use melee attacks.";
			case MAGIC:
				return "You can not use magic attacks.";
			case SPECIAL_ATTACKS:
				return "You can not use special attacks.";
			case FUN_WEAPONS:
				return "You can only attack with `fun` weapons.";
			case FORFEIT:
				return "You can not forfeit the duel.";
			case PRAYER:
				return "You can not use prayer.";
			case DRINKS:
				return "You can not use drinks.";
			case FOOD:
				return "You can not use food.";
			case MOVEMENT:
				return "You can not move during the duel.";
			case OBSTACLES:
				return "There will be obsticals in the arena.";
			case WHIP_DDS:
				return "Only whips and dragon daggers are allowed for weapons.";
			case HEAD:
				return "You can not wear items on your head.";
			case CAPE:
				return "You can not wear items on your back.";
			case NECKLACE:
				return "You can not wear items on your neck.";
			case AMMO:
				return "You can not wear items in your quiver.";
			case WEAPON:
				return "You can not wear weapons.";
			case BODY:
				return "You can not wear items on your chest.";
			case SHIELD:
				return "You can not wear items on your offhand(Includes 2h weapons)";
			case LEGS:
				return "You can not wear items on your legs.";
			case GLOVES:
				return "You can not wear items on your hands.";
			case BOOTS:
				return "You can not wear items on your feet.";
			case RINGS:
				return "You can not wear items on your fingers.";
		}
		return "";
	}

	private void moveToArena() {
		if (stage == DuelStage.ARENA && other.get().getDuelArena().getStage() == DuelStage.ARENA) {
			onStart();
			
			SquareArea arena;

			if (rules.get(DuelRule.MOVEMENT)) {
				arena = (SquareArea) ARENAS.get(Utility.random(ARENAS.size(), false));

				if (arena.getName().contains("East Arena")) {
					Location tile = findUnclipped(new Location(3376, 3232), 14, 5);
					player.setTeleportTarget(tile);
					other.get().setTeleportTarget(tile.transform(0, 1));
				} else {
					Location tile = findUnclipped(new Location(3345, 3251), 14, 5);
					player.setTeleportTarget(tile);
					other.get().setTeleportTarget(tile.transform(0, 1));
				}
			} else if  (rules.get(DuelRule.OBSTACLES)) {
				int randomIndex = Utility.random(OBSTACLE_ARENAS.size(), false);

				arena = (SquareArea) OBSTACLE_ARENAS.get(randomIndex);

				if (arena.getName().contains("South East Obstacle Arena")) {
					player.setTeleportTarget(new Location(3366, 3213));
					other.get().setTeleportTarget(new Location(3386, 3213));
				} else if (arena.getName().contains("North East Obstacle Arena")) {
					player.setTeleportTarget(new Location(3366, 3251));
					other.get().setTeleportTarget(new Location(3386, 3251));
				} else {
					player.setTeleportTarget(new Location(3333, 3231));
					other.get().setTeleportTarget(new Location(3355, 3232));
				}

			} else {
				int randomIndex = Utility.random(ARENAS.size(), false);

				arena = (SquareArea) ARENAS.get(randomIndex);

				if (arena.getName().contains("East Arena")) {
					Location tile = findUnclipped(new Location(3376, 3232), 14, 5);
					
					Location random_spawn_tile = findUnclipped(new Location(3376, 3232), 14, 5);
					while (tile.equals(random_spawn_tile))
						random_spawn_tile = findUnclipped(new Location(3376, 3232), 14, 5);
					player.setTeleportTarget(tile);
					other.get().setTeleportTarget(random_spawn_tile);
				} else {
					Location tile = findUnclipped(new Location(3345, 3251), 14, 5);
					
					Location random_spawn_tile = findUnclipped(new Location(3345, 3251), 14, 5);
					while (tile.equals(random_spawn_tile))
						random_spawn_tile = findUnclipped(new Location(3345, 3251), 14, 5);
					
					player.setTeleportTarget(tile);
					other.get().setTeleportTarget(random_spawn_tile);
				}
			}

			new DuelArenaTask();

			player.getActionSender().createPlayerHint(10, other.get().getIndex());
			other.get().getActionSender().createPlayerHint(10, player.getIndex());
		}
	}

	private Location findUnclipped(Location base, int x, int y) {
		
		Location possible = base.transform(-x, -y).transform(Utility.random(x*2), Utility.random(y*2)); 
		// repeatedly check if this position has no clip mask
		
		while (Region.getClippingMask(possible.getX(), possible.getY(), possible.getZ()) != 0) {
			// if it is clipped it's invalid, so lets randomize the initial position 
			// start by making base 3722 3232 into base x-14 and y-5 then add a random number 0-(14*2=28) to x and 5*2=10 to y
			possible = base.transform(-x, -y).transform(Utility.random(x*2), Utility.random(y*2));
			// so ends anywhere where the clip of (3722,3232) and (basex+28, basey+10) is unclipped
		}
		System.out.printf("possible %s base %s%n", possible, base);
		return possible;
	}

	/**
	 * Resets all the rules to their default values.
	 */
	private void resetRules() {
		IntStream.range(631, 643).forEach(i -> player.getActionSender().sendConfig(i, 0));
		player.getActionSender().sendToggle(286, 0);
		rules.resetConfigValue();
		rules.clear();
	}

	/**
	 * Handles the action of clicking on a button on the duel interface.
	 *
	 * @param button The id of the button that is being clicked.
	 */
	public void handleButton(int button) {
		
		Optional<DuelRule> rule = DuelRule.forButton(button);

		rule.ifPresent(r -> r.set(player));

		switch (button) {

			case 121042:
			case 123035:
				declineDuel(true);
				break;

			case 121039:
			case 123032:
				acceptOffer();
				break;

			case 123222: // claim reward
			case 123214: // exit claim reward
				claimReward(won);
				break;

		}

	}

	public void claimReward(boolean won) {
		if (won) {
			player.getInventory().add(getReward());
		}		
		reset();
	}

	/**
	 * Accepts a duel offer.
	 */
	private void acceptOffer() {
		if (other.get().getInventory().getFreeSlots() < player.getDuelContainer().getTakenSlots() + other.get().getDuelContainer().getTakenSlots()) {
			player.getActionSender().sendMessage("The other player does not have enough space.");
			return;
		}

		if (player.getInventory().getFreeSlots() < other.get().getDuelContainer().getTakenSlots() + player.getDuelContainer().getTakenSlots()) {
			player.getActionSender().sendMessage("You do not have enough space.");
			return;
		}

		switch (stage) {
			case FIRST_SCREEN:
				other.get().getActionSender().sendString("@whi@Other player has accepted.", 31009);
				player.getActionSender().sendString("@whi@Waiting for other player...", 31009);
				setAccepted(true);
				if (other.get().getDuelArena().isAccepted() && other.get().getDuelArena().getStage() == DuelStage.FIRST_SCREEN) {
					other.get().getActionSender().sendString("", 31009);
					player.getActionSender().sendString("", 31009);
					setAccepted(false);
					other.get().getDuelArena().setAccepted(false);
					setStage(DuelStage.SECOND_SCREEN);
					other.get().getDuelArena().setStage(DuelStage.SECOND_SCREEN);
					execute(DuelStage.SECOND_SCREEN);
					other.get().getDuelArena().execute(DuelStage.SECOND_SCREEN);
				}
				break;

			case SECOND_SCREEN:
				other.get().getActionSender().sendString("Other player has accepted the duel.", 31526);
				player.getActionSender().sendString("Waiting for other player...", 31526);

				setAccepted(true);

				if (other.get().getDuelArena().isAccepted() && other.get().getDuelArena().getStage() == DuelStage.SECOND_SCREEN) {
					setAccepted(false);
					other.get().getDuelArena().setAccepted(false);

					if (validateLastAccept()) {
						player.getActionSender().removeAllInterfaces();
						other.get().getActionSender().removeAllInterfaces();

						setStage(DuelStage.ARENA);
						other.get().getDuelArena().setStage(DuelStage.ARENA);

						executeRules();
						other.get().getDuelArena().executeRules();

						moveToArena();
					}
				}
				break;
		default:
			throw new IllegalArgumentException("Invalid duel stage while accepting offers: " + stage.name());
		}

	}

	private void executeRules() {
		if (player.getEquipment().hasWeapon()) {
			Item item = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
			if (item != null) {
				String name = item.getName().toLowerCase();

				CombatStyle type = WeaponDefinition.get(item.getId()).getCombatType();

				if (rules.get(DuelRule.MELEE)) {
					if (type == CombatStyle.MELEE) {
						player.getEquipment().unequip(EquipmentConstants.WEAPON_SLOT);
					}
				}

				if (rules.get(DuelRule.MAGIC)) {
					if (type == CombatStyle.MAGIC) {
						player.getEquipment().unequip(EquipmentConstants.WEAPON_SLOT);
					}
				}

				if (rules.get(DuelRule.RANGED)) {
					if (type == CombatStyle.RANGE) {
						player.getEquipment().unequip(EquipmentConstants.WEAPON_SLOT);
					}
				}

				if (rules.get(DuelRule.WHIP_DDS)) {
					if (!name.contains("whip") && !name.contains("abyssal tentacle") && !name.contains("dragon dagger")) {
						player.getEquipment().unequip(EquipmentConstants.WEAPON_SLOT);
					}
				}

				if (rules.get(DuelRule.WEAPON)) {
					player.getEquipment().unequip(EquipmentConstants.WEAPON_SLOT);
				}
			} else {
				player.getEquipment().unequip(EquipmentConstants.WEAPON_SLOT);
			}
		}

		if (player.getEquipment().hasHead()) {
			if (rules.get(DuelRule.HEAD)) {
				player.getEquipment().unequip(EquipmentConstants.HELM_SLOT);
			}
		}

		if (player.getEquipment().hasTorso()) {
			if (rules.get(DuelRule.BODY)) {
				player.getEquipment().unequip(EquipmentConstants.TORSO_SLOT);
			}
		}

		if (player.getEquipment().hasCape()) {
			if (rules.get(DuelRule.CAPE)) {
				player.getEquipment().unequip(EquipmentConstants.CAPE_SLOT);
			}
		}

		if (player.getEquipment().hasNecklace()) {
			if (rules.get(DuelRule.NECKLACE)) {
				player.getEquipment().unequip(EquipmentConstants.NECKLACE_SLOT);
			}
		}

		if (player.getEquipment().hasAmmo()) {
			if (rules.get(DuelRule.AMMO)) {
				player.getEquipment().unequip(EquipmentConstants.AMMO_SLOT);
			}
		}

		if (player.getEquipment().hasLegs()) {
			if (rules.get(DuelRule.LEGS)) {
				player.getEquipment().unequip(EquipmentConstants.LEGS_SLOT);
			}
		}

		if (player.getEquipment().hasGloves()) {
			if (rules.get(DuelRule.GLOVES)) {
				player.getEquipment().unequip(EquipmentConstants.GLOVES_SLOT);
			}
		}

		if (player.getEquipment().hasBoots()) {
			if (rules.get(DuelRule.BOOTS)) {
				player.getEquipment().unequip(EquipmentConstants.BOOTS_SLOT);
			}
		}

		if (player.getEquipment().hasRing()) {
			if (rules.get(DuelRule.RINGS)) {
				player.getEquipment().unequip(EquipmentConstants.RING_SLOT);
			}
		}

		if (player.getEquipment().hasShield()) {
			if (rules.get(DuelRule.SHIELD)) {
				Item item = player.getEquipment().get(EquipmentConstants.SHIELD_SLOT);
				player.getEquipment().unequip(EquipmentConstants.SHIELD_SLOT);

				if (item != null) {
					if (WeaponDefinition.get(item.getId()).isTwoHanded()) {
						player.getEquipment().unequip(EquipmentConstants.WEAPON_SLOT);
					}
				}
			}
		}
	}

	public boolean canEquip(EquipmentDefinition def) {
		switch (def.getType().getSlot()) {
			case EquipmentConstants.HELM_SLOT:
				return rules.get(DuelRule.HEAD) ? false : true;

			case EquipmentConstants.CAPE_SLOT:
				return rules.get(DuelRule.CAPE) ? false : true;

			case EquipmentConstants.NECKLACE_SLOT:
				return rules.get(DuelRule.NECKLACE) ? false : true;

			case EquipmentConstants.AMMO_SLOT:
				return rules.get(DuelRule.AMMO) ? false : true;

			case EquipmentConstants.WEAPON_SLOT:
				if (rules.get(DuelRule.WHIP_DDS)) {
					String name = def.getName().toLowerCase();
					if (!name.contains("whip") && !name.contains("abyssal tentacle") && !name.contains("dragon dagger")) {
						return false;
					}
				}
				return rules.get(DuelRule.WEAPON) ? false : true;

			case EquipmentConstants.TORSO_SLOT:
				return rules.get(DuelRule.BODY) ? false : true;

			case EquipmentConstants.SHIELD_SLOT:
				return rules.get(DuelRule.SHIELD) ? false : true;

			case EquipmentConstants.LEGS_SLOT:
				return rules.get(DuelRule.LEGS) ? false : true;

			case EquipmentConstants.GLOVES_SLOT:
				return rules.get(DuelRule.GLOVES) ? false : true;

			case EquipmentConstants.BOOTS_SLOT:
				return rules.get(DuelRule.BOOTS) ? false : true;

			case EquipmentConstants.RING_SLOT:
				return rules.get(DuelRule.RINGS) ? false : true;
		}
		return false;
	}

	private boolean validateLastAccept() {
		if (player != null && other != null && this.getStage() == DuelStage.SECOND_SCREEN && this.getOther().get().getDuelArena().getStage() == DuelStage.SECOND_SCREEN) {
			return true;
		}
		return false;
	}

	@Override
	public void onStart() {
		onReset(player);
		
		player.getActionSender().sendPlayerOption(PlayerOption.DUEL_REQUEST, true, true);
		other.get().getActionSender().sendPlayerOption(PlayerOption.DUEL_REQUEST, true, true);
	}

	@Override
	public void onEnd() {
		Player winner = isWon() ? player : other.get();
		Player loser = !isWon() ? player : other.get();

		winner.getDuelArena().setStage(DuelStage.REWARD);

		winner.setTeleportTarget(RESPAWN_LOCATIONS.get(Utility.random(RESPAWN_LOCATIONS.size(), false)).getRandomLocation());
		loser.setTeleportTarget(RESPAWN_LOCATIONS.get(Utility.random(RESPAWN_LOCATIONS.size(), false)).getRandomLocation());

		winner.getActionSender().createPlayerHint(10, -1);
		loser.getActionSender().createPlayerHint(10, -1);

		winner.getActionSender().sendPlayerOption(PlayerOption.DUEL_REQUEST, false, false);
		loser.getActionSender().sendPlayerOption(PlayerOption.DUEL_REQUEST, false, false);
		
		winner.getActionSender().sendPlayerOption(PlayerOption.ATTACK, true, true);
		loser.getActionSender().sendPlayerOption(PlayerOption.ATTACK, true, true);

		DeathTask.reset(loser);
		DeathTask.reset(winner);

		onReset(winner);
		onReset(loser);

		winner.getDuelArena().setReward(mergeContainers(winner, loser));

		winner.getActionSender().sendItemOnInterface(31708, winner.getDuelArena().getReward());
		loser.getActionSender().sendItemOnInterface(31708, new Item[]{});
		
		winner.getActionSender().sendString("You are victorious!", 31705);
		loser.getActionSender().sendString("You lost!", 31705);

		if (!winner.isActive()) {
			winner.getDuelArena().claimReward(true);
		} else {
			winner.getDuelArena().execute(DuelStage.REWARD);
		}

		loser.getDuelArena().reset();
	}

	public Item[] mergeContainers(Player winner, Player loser) {
		Item[] items = new Item[28];

		Item[] winnerItems = winner.getDuelContainer().toArray();

		Item[] loserItems = loser.getDuelContainer().toArray();

		for (int index = 0; index < winner.getDuelContainer().getSize(); index++) {
			int nextIndex = Utility.findNextNullIndex(items);
			if (winnerItems[index] != null) {
				if (nextIndex != -1) {
					items[nextIndex] = winnerItems[index];
				}
			}
		}

		for (int index = 0; index < loserItems.length; index++) {
			int nextIndex = Utility.findNextNullIndex(items);
			if (loserItems[index] != null) {
				if (nextIndex != -1) {
					items[nextIndex] = loserItems[index];
				}
			}
		}

		winner.getDuelContainer().clear(false);
		loser.getDuelContainer().clear(false);

		return items;
	}

	public boolean canAttack() {
		if (getRules().get(DuelRule.MELEE)) {
			if (player.getCombatType() == CombatStyle.MELEE) {
				player.getActionSender().sendMessage("Melee attacks are disabled in this duel.");
				return false;
			}
		}

		if (getRules().get(DuelRule.RANGED)) {
			if (player.getCombatType() == CombatStyle.RANGE) {
				player.getActionSender().sendMessage("Ranged attacks are disabled in this duel.");
				return false;
			}
		}

		if (getRules().get(DuelRule.MAGIC)) {
			if (player.getCombatType() == CombatStyle.MAGIC) {
				player.getActionSender().sendMessage("Magic attacks are disabled in this duel.");
				return false;
			}
		}
		return true;
	}

	public void onFirstClickObject(GameObject object) {
		switch (object.getId()) {
			case 3203:
				if (rules.get(DuelRule.FORFEIT)) {
					player.getActionSender().sendMessage("Forfeiting is disabled in this duel.");
					return;
				}

				if (getWaitTime() > 0) {
					player.getActionSender().sendMessage("You must wait until the duel starts to forfeit.");
					return;
				}

				player.getActionSender().sendMessage("You forfeit the duel.");
				other.get().getActionSender().sendMessage("Your opponent has forfeited the duel.");
				onEnd();
				break;
		}
	}

	public void onDeath() {
		onEnd();
	}

	/**
	 * Resets the duel to prepare for another duel.
	 *
	 * @param tellOther Sends a message to notify that the duel has been declined.
	 */
	public void declineDuel(boolean tellOther) {
		
			if (player.getInventory().getFreeSlots() >= player.getDuelContainer().getTakenSlots() && !player.getDuelContainer().isEmpty()) {
				player.getInventory().add(player.getDuelContainer().toArray());
				player.getDuelContainer().clear(false);
			}

			if (other.isPresent()) {
				if (other.get().getInventory().getFreeSlots() >= other.get().getDuelContainer().getTakenSlots() && !other.get().getDuelContainer().isEmpty()) {
					other.get().getInventory().add(other.get().getDuelContainer().toArray());
					other.get().getDuelContainer().clear(false);
				}
			}
			
		if (tellOther) {
			player.getActionSender().sendMessage("You declined the stake.");
			other.ifPresent($it -> $it.getActionSender().sendMessage("Your duel has been declined."));
		}

		other.ifPresent($it -> $it.getDuelArena().reset());
		reset();
	}

	public void reset() {
		setStage(DuelStage.REQUEST);

		setArenaTime(0);
		
		setWaitTime(6);
		
		resetRules();
		
		setWon(false);
		
		setDuelRequest(false);
		
		player.getDuelContainer().clear(true);		

		this.other = Optional.empty();

		player.setOtherPlayerDuelIndex(-1);
		player.getActionSender().removeAllInterfaces();
		PlayerSave.save(player);
	}

	/**
	 * Gets the player initiating the duel.
	 *
	 * @return The player.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the other player being interacted with.
	 *
	 * @return The other player.
	 */
	public Optional<Player> getOther() {
		return other;
	}

	/**
	 * Sets the value of the other player.
	 *
	 * @param other The other player in the duel.
	 */
	public void setOther(Optional<Player> other) {
		this.other = other;
	}

	/**
	 * Gets the current stage this duel is in.
	 *
	 * @return The current stage.
	 */
	public DuelStage getStage() {
		return stage;
	}

	/**
	 * Sets the current stage of this duel.
	 *
	 * @param stage The current stage of this duel.
	 */
	public void setStage(DuelStage stage) {
		this.stage = stage;
	}

	/**
	 * Determines if a duel request has been sent.
	 *
	 * @return {@code true} If a duel request has been sent. {@code false}
	 * Otherwise.
	 */
	public boolean isDuelRequest() {
		return duelRequest;
	}

	/**
	 * Sets the flag that determines a duel request has been sent.
	 *
	 * @param duelRequest The flag.
	 */
	public void setDuelRequest(boolean duelRequest) {
		this.duelRequest = duelRequest;
	}

	/**
	 * Gets the rules for this duel.
	 *
	 * @return The rules.
	 */
	public DuelRules getRules() {
		return rules;
	}

	public void setRules(DuelRules rules) {
		this.rules = rules;
	}

	/**
	 * Determines if this {@link Player} is actively dueling.
	 */
	public boolean isDueling() {
		return stage == DuelStage.ARENA;
	}

	/**
	 * Determines if this {@link Player} is actively in a session.
	 */
	public boolean isInSession() {
		return stage == DuelStage.FIRST_SCREEN || stage == DuelStage.SECOND_SCREEN;
	}

	public boolean canOffer() {
		return stage == DuelStage.FIRST_SCREEN;
	}

	public void decrementWaitTime(int seconds) {
		this.waitCounter -= seconds;
	}

	public int getWaitTime() {
		return this.waitCounter;
	}

	public void setWaitTime(int waitCounter) {
		this.waitCounter = waitCounter;
	}

	/**
	 * @return the arenaTime
	 */
	public int getArenaTime() {
		return arenaTime;
	}

	/**
	 * @param arenaTime the arenaTime to set
	 */
	public void setArenaTime(int arenaTime) {
		this.arenaTime = arenaTime;
	}

	public void incrementArenaTime(int seconds) {
		this.arenaTime += seconds;
	}

	/**
	 * @return the won
	 */
	public boolean isWon() {
		return won;
	}

	/**
	 * @param won the won to set
	 */
	public void setWon(boolean won) {
		this.won = won;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public Item[] getReward() {
		return reward;
	}

	public void setReward(Item[] reward) {
		this.reward = reward;
	}

	@Override
	public boolean inArea(Player player) {
		return false;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public void onDisplay(Player player) {

	}

	@Override
	public Optional<String> getState() {
		return Optional.of(stage.name());
	}

	@Override
	public boolean contains(Player player) {
		return false;
	}

	@Override
	public String toString() {
		return String.format("[DuelArena], [player= %s], [other= %s], [stage= %s], [other stage= %s]", player.getUsername(), !other.isPresent() ? "None" : other.get().getUsername(), getStage().name(), !other.isPresent() ? "None" : other.get().getDuelArena().getStage().name());
	}

	/**
	 * The {@link Timer} implementation that checks players in the arena every second
	 * and determines what do with them.
	 *
	 * @author SeVen
	 */
	private final class DuelArenaTask extends Timer {

		public DuelArenaTask() {
			execute();
		}

		public void execute() {
			this.schedule(new TimerTask() {

				@Override
				public void run() {
					countdown();

					if (player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
						setWon(false);
						other.get().getDuelArena().setWon(true);
					} else if (other.isPresent()) {
						if (other.get().getSkills().getLevel(Skills.HITPOINTS) <= 0) {
							setWon(true);
							other.get().getDuelArena().setWon(false);
						}
						
						if (other.get().getDuelArena().getStage() != DuelStage.ARENA) {
							this.cancel();
						}
					}

					if (player.getDuelArena().getStage() != DuelStage.ARENA) {
						this.cancel();
					}

				}

			}, 0, 1000);
		}

		private void countdown() {
			if (getWaitTime() > -1) {
				decrementWaitTime(1);
				other.ifPresent($it -> $it.getDuelArena().decrementWaitTime(1));
			}
			
			if (getWaitTime() <= 3 && getWaitTime() >= 1) {
				player.sendForcedMessage("" + getWaitTime());
				other.ifPresent($it -> $it.sendForcedMessage("" + getWaitTime()));
			} else if (getWaitTime() == 0) {
				player.sendForcedMessage("FIGHT!");
				other.ifPresent($it -> $it.sendForcedMessage("FIGHT!"));
			}
		}
	}
	
	public void onReset(Player player) {
		
	}

}