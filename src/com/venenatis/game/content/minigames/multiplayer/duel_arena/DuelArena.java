package com.venenatis.game.content.minigames.multiplayer.duel_arena;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.container.impl.equipment.EquipmentContainer;
import com.venenatis.game.model.container.impl.inventory.InventoryContainer;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.save.PlayerSave;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class DuelArena {

	/**
	 * Represents the requester / player
	 */
	private final Player player;

	/**
	 * Constructs a new duel
	 * 
	 * @param player
	 */
	public DuelArena(final Player player) {
		this.player = player;
	}

	/**
	 * Duel option widget id
	 */
	private static final int DUEL_OPTION_WIDGET_ID = 48000;

	/**
	 * Duel offer widget id
	 */
	private static final int DUEL_OFFER_WIDGET_ID = 48200;

	private static final int PRIMARY_CONFIRMATION_WIDGET_ID = 48300;

	/**
	 * The amount of duel options
	 */
	public static final int DUEL_OPTION_COUNT = 13;

	/**
	 * The amount of equipment restrictions available
	 */
	public static final int EQUIPMENT_RESTRICTION_COUNT = 11;

	/**
	 * Represents the other player
	 */
	private Player otherPlayer;

	/**
	 * Represents the duel stage
	 */
	private DuelStage stage = null;

	/**
	 * Represents the options that are active
	 */
	private boolean[] optionActive = new boolean[DUEL_OPTION_COUNT];

	/**
	 * Represents the old duels options that were set
	 */
	private boolean[] temporaryOptionActive = new boolean[DUEL_OPTION_COUNT];

	/**
	 * Represents restricted equipment
	 */
	private boolean[] equipmentRestricted = new boolean[EQUIPMENT_RESTRICTION_COUNT];

	/**
	 * Represents the last duels restrictions
	 */
	private boolean[] temporaryEquipmentRestricted = new boolean[EQUIPMENT_RESTRICTION_COUNT];

	/**
	 * Offered items
	 */
	private final CopyOnWriteArrayList<Item> offeredItems = new CopyOnWriteArrayList<Item>();

	/**
	 * Checks if player can challenge
	 * 
	 * @return
	 */
	public boolean canChallenge() {
		// Checks if the other player is null
		if (otherPlayer == null) {
			return false;
		}

		// checks if you're further than 1 tile from the other player
		if (player.getLocation().distanceToPoint(otherPlayer.getLocation()) > 1) {
			player.getActionSender().sendMessage("You need to move closer to " + otherPlayer + " to challenge.");
			return false;
		}

		// checks if player has temporary duel stage attribute
		if (player.getAttributes().get("duel_stage") != null) {
			return false;
		}

		// checks if other player has temporary duel stage attribute
		if (otherPlayer.getAttributes().get("duel_stage") != null) {
			player.getActionSender().sendMessage("This player is currently busy.");
			return false;
		}
		return true;
	}

	public void sendChallenge() {
		player.getAttributes().put("request_sent", true);
		if (otherPlayer.getAttributes().get("request_sent") != null) {
			sendDuelOptionWidget();
			otherPlayer.getDuelArena().sendDuelOptionWidget();
		} else {
			player.getActionSender().sendMessage("Sending request...");
			otherPlayer.getActionSender().sendMessage(player + " :duelreq:");
		}
	}
	
	/**
	 * The duel arena interface action buttons
	 * 
	 * @param button
	 *            The button being pressed
	 * @return 
	 */
	public boolean actionButtons(int button) {
		switch (button) {
		case 187198:
			player.getDuelArena().saveDuelSettings();
			return true;

		case 187200:
			player.getDuelArena().loadSavedDuelSettings();
			return true;

		case 187164:
			player.getDuelArena().acceptDuelOptions();
			return true;

		case 188088:
		case 188202:
		case 187168:
			player.getDuelArena().decline();
			return true;

		case 188084:
			player.getDuelArena().acceptOfferWidget();
			return true;

		case 188205:
			player.getDuelArena().acceptPrimaryConfirmationWidget();
			return true;

		case 187202:
			player.getDuelArena().loadLastDuelSettings();
			return true;

		/**
		 * Duel options
		 */
		case 187172:
			player.getDuelArena().selectOption(0);
			return true;

		case 187174:
			player.getDuelArena().selectOption(1);
			return true;

		case 187176:
			player.getDuelArena().selectOption(2);
			return true;

		case 187178:
			player.getDuelArena().selectOption(3);
			return true;

		case 187180:
			player.getDuelArena().selectOption(4);
			return true;

		case 187182:
			player.getDuelArena().selectOption(5);
			return true;

		case 187184:
			player.getDuelArena().selectOption(6);
			return true;

		case 187186:
			player.getDuelArena().selectOption(7);
			return true;

		case 187188:
			player.getDuelArena().selectOption(8);
			return true;

		case 187190:
			player.getDuelArena().selectOption(9);
			return true;

		case 187192:
			player.getDuelArena().selectOption(10);
			return true;

		case 187194:
			player.getDuelArena().selectOption(11);
			return true;

		case 187196:
			player.getDuelArena().selectOption(12);
			return true;

		/**
		 * Duel Restrictions
		 */
		case 187207:
			player.getDuelArena().restrictEquipment(0);
			return true;

		case 187208:
			player.getDuelArena().restrictEquipment(1);
			return true;

		case 187209:
			player.getDuelArena().restrictEquipment(2);
			return true;

		case 187210:
			player.getDuelArena().restrictEquipment(3);
			return true;

		case 187211:
			player.getDuelArena().restrictEquipment(4);
			return true;

		case 187212:
			player.getDuelArena().restrictEquipment(5);
			return true;

		case 187213:
			player.getDuelArena().restrictEquipment(6);
			return true;

		case 187214:
			player.getDuelArena().restrictEquipment(7);
			return true;

		case 187215:
			player.getDuelArena().restrictEquipment(8);
			return true;

		case 187216:
			player.getDuelArena().restrictEquipment(9);
			return true;

		case 187217:
			player.getDuelArena().restrictEquipment(10);
			return true;
		}
		return false;
	}

	/**
	 * Sends duel option widget
	 */
	private void sendDuelOptionWidget() {
		player.getAttributes().remove("request_sent");
		player.getAttributes().put("duel_stage", DuelStage.DUEL_OPTION_SCREEN);
		resetDuelOptionWidget();
		//ken fix for dueling showing player randomly accepting at start of options before either player accepted, was confusing before
		player.getActionSender().sendString("", 48095);
		otherPlayer.getActionSender().sendString("", 48095);
		player.getActionSender().sendInterface(DUEL_OPTION_WIDGET_ID);
	}

	/**
	 * Saves current duel settings
	 */
	private void saveDuelSettings() {
		IntStream.range(0, DUEL_OPTION_COUNT).forEach(i -> player.getDuelOptions()[i] = optionActive[i]);
		IntStream.range(0, EQUIPMENT_RESTRICTION_COUNT).forEach(i -> player.getDuelEquipmentRestrictions()[i] = equipmentRestricted[i]);
		player.getActionSender().sendMessage("@or2@You have saved your current duel settings.");
		PlayerSave.save(player);
	}

	/**
	 * Loads saved duel options
	 */
	private void loadSavedDuelSettings() {
		if (player.getAttributes().get("duel_stage") != DuelStage.DUEL_OPTION_SCREEN
				|| otherPlayer.getAttributes().get("duel_stage") != DuelStage.DUEL_OPTION_SCREEN) {
			return;
		}
		int amountRestricted = 0;
		for (int index = 0; index < EQUIPMENT_RESTRICTION_COUNT; index++) {
			if(player.getDuelEquipmentRestrictions()[index])
				amountRestricted++;
		}
		if(amountRestricted > player.getInventory().getFreeSlots()) {
			player.getActionSender().sendMessage("You or your opponent don't have enough inventory space to do this.");
			otherPlayer.getActionSender().sendMessage("You or your opponent don't have enough inventory space to do this.");
			return;
		}
		if(amountRestricted > otherPlayer.getInventory().getFreeSlots()) {
			otherPlayer.getActionSender().sendMessage("You or your opponent don't have enough inventory space to do this.");
			player.getActionSender().sendMessage("You or your opponent don't have enough inventory space to do this.");
			return;
		}		
		for (int index = 0; index < EQUIPMENT_RESTRICTION_COUNT; index++) {
			if (player.getDuelEquipmentRestrictions()[index]) {
				player.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 1);
				otherPlayer.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 1);
				getEquipmentRestricted()[index] = true;
				otherPlayer.getDuelArena().getEquipmentRestricted()[index] = true;
			} else {
				player.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 0);
				otherPlayer.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 0);
				getEquipmentRestricted()[index] = false;
				otherPlayer.getDuelArena().getEquipmentRestricted()[index] = false;
			}
		}
		for (int index = 0; index < DUEL_OPTION_COUNT; index++) {
			if (player.getDuelOptions()[index]) {
				player.getActionSender().sendConfig(OPTION_ID[index], 1);
				otherPlayer.getActionSender().sendConfig(OPTION_ID[index], 1);
				getOptionActive()[index] = true;
				otherPlayer.getDuelArena().getOptionActive()[index] = true;
			} else {
				player.getActionSender().sendConfig(OPTION_ID[index], 0);
				otherPlayer.getActionSender().sendConfig(OPTION_ID[index], 0);
				getOptionActive()[index] = false;
				otherPlayer.getDuelArena().getOptionActive()[index] = false;
			}
		}
		if (getOptionActive()[DuelOptions.FUN_WEAPONS.getId()])
		{
			if ((player.getEquipment().contains(4151) && otherPlayer.getEquipment().contains(4151) && !player.getEquipment().contains(12006) && !otherPlayer.getEquipment().contains(12006))
			    || (!player.getEquipment().contains(4151) && !otherPlayer.getEquipment().contains(4151) && player.getEquipment().contains(12006) && otherPlayer.getEquipment().contains(12006)))
			{
				
			}
			else
			{
				player.getActionSender().sendMessage("You and your opponent must both be wielding either an Abyssal Whip");
				player.getActionSender().sendMessage("or an Abyssal Tentacle to enable the fun weapon rule.");
				getOptionActive()[DuelOptions.FUN_WEAPONS.getId()] = false;
				otherPlayer.getDuelArena().getOptionActive()[DuelOptions.FUN_WEAPONS.getId()] = false;
				player.getActionSender().sendConfig(OPTION_ID[DuelOptions.FUN_WEAPONS.getId()], 0);
				otherPlayer.getActionSender().sendConfig(OPTION_ID[DuelOptions.FUN_WEAPONS.getId()], 0);
				return;
			}
		}
		player.getActionSender().sendMessage("@or2@You have loaded your saved duel options.");
	}

	/**
	 * Loads last duel settings
	 */
	private void loadLastDuelSettings() {
		if (player.getAttributes().get("duel_stage") != DuelStage.DUEL_OPTION_SCREEN
				|| otherPlayer.getAttributes().get("duel_stage") != DuelStage.DUEL_OPTION_SCREEN) {
			return;
		}
		int amountRestricted = 0;
		for (int index = 0; index < EQUIPMENT_RESTRICTION_COUNT; index++) {
			if(player.getDuelEquipmentRestrictions()[index])
				amountRestricted++;
		}
		if(amountRestricted > player.getInventory().getFreeSlots()) {
			player.getActionSender().sendMessage("You or your opponent don't have enough inventory space to do this.");
			otherPlayer.getActionSender().sendMessage("You or your opponent don't have enough inventory space to do this.");
			return;
		}
		if(amountRestricted > otherPlayer.getInventory().getFreeSlots()) {
			otherPlayer.getActionSender().sendMessage("You or your opponent don't have enough inventory space to do this.");
			player.getActionSender().sendMessage("You or your opponent don't have enough inventory space to do this.");
			return;
		}		
		for (int index = 0; index < EQUIPMENT_RESTRICTION_COUNT; index++) {
			if (temporaryEquipmentRestricted[index]) {
				player.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 1);
				otherPlayer.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 1);
				getEquipmentRestricted()[index] = true;
				otherPlayer.getDuelArena().getEquipmentRestricted()[index] = true;
			} else {
				player.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 0);
				otherPlayer.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 0);
				getEquipmentRestricted()[index] = false;
				otherPlayer.getDuelArena().getEquipmentRestricted()[index] = false;
			}
		}
		for (int index = 0; index < DUEL_OPTION_COUNT; index++) {
			if (temporaryOptionActive[index]) {
				player.getActionSender().sendConfig(OPTION_ID[index], 1);
				otherPlayer.getActionSender().sendConfig(OPTION_ID[index], 1);
				getOptionActive()[index] = true;
				otherPlayer.getDuelArena().getOptionActive()[index] = true;
			} else {
				player.getActionSender().sendConfig(OPTION_ID[index], 0);
				otherPlayer.getActionSender().sendConfig(OPTION_ID[index], 0);
				getOptionActive()[index] = false;
				otherPlayer.getDuelArena().getOptionActive()[index] = false;
			}
		}
		
		player.getActionSender().sendMessage("@or2@Your last duel options has been loaded.");
	}

	/**
	 * Accept the option widget
	 */
	private void acceptDuelOptions() {
		player.getAttributes().put("option_screen_accepted", true);
		player.getActionSender().sendString("Waiting for other player to accept.", 48095);
		otherPlayer.getActionSender().sendString("Other player has accepted.", 48095);
		if (otherPlayer.getAttributes().get("option_screen_accepted") != null) {
			sendOfferWidget();
			otherPlayer.getDuelArena().sendOfferWidget();
		}
	}

	/**
	 * Accept the offer widget
	 */
	private void acceptOfferWidget() {
		player.getAttributes().put("offer_screen_accepted", true);
		player.getActionSender().sendString("@red@Waiting for other player.", 48222);
		otherPlayer.getActionSender().sendString("@red@Other player has accepted.", 48222);
		if (otherPlayer.getAttributes().get("offer_screen_accepted") != null) {
			sendPrimaryConfirmationWidget();
			otherPlayer.getDuelArena().sendPrimaryConfirmationWidget();
		}
	}

	private void acceptPrimaryConfirmationWidget() {
		player.getAttributes().put("primary_screen_accepted", true);
		player.getActionSender().sendString("Waiting for other player.", 48788);
		otherPlayer.getActionSender().sendString("other player has accepted.", 48788);
		if (otherPlayer.getAttributes().get("primary_screen_accepted") != null) {
			if ((player.getDuelArena().getOptionActive()[DuelOptions.NO_MELEE.getId()] && player.getDuelArena().getOptionActive()[DuelOptions.NO_MOVEMENT.getId()])) {
				player.setTeleportTarget(new Location(3357, 3252, 0));
				otherPlayer.setTeleportTarget(new Location(3357, 3251, 0));
			} else {
				player.setTeleportTarget(new Location(3344, 3250, 0));
				otherPlayer.setTeleportTarget(new Location(3345, 3250, 0));
			}
			invokeDuelMatch();
		}
	}

	private void invokeDuelMatch() {
		player.getActionSender().removeAllInterfaces();
		otherPlayer.getActionSender().removeAllInterfaces();
		
		player.setHeadHint(-1);
		otherPlayer.setHeadHint(-1);
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		otherPlayer.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		PrayerHandler.resetAll(player);
		PrayerHandler.resetAll(otherPlayer);
		for (int index = 0; index < Skills.SKILL_COUNT; index++) {
			player.getSkills().setLevel(index, player.getSkills().getLevelForExperience(index));
			otherPlayer.getSkills().setLevel(index, otherPlayer.getSkills().getLevelForExperience(index));
			player.getActionSender().sendSkills();
			otherPlayer.getActionSender().sendSkills();
		}
		for (int index = 0; index < player.getDuelArena().getEquipmentRestricted().length; index++) {
			if (player.getDuelArena().getEquipmentRestricted()[index]) {
				final int slot = EquipmentSlots.get(index).getSlot();
				player.getEquipment().unequip(slot);
				otherPlayer.getEquipment().unequip(slot);
			}
		}
		World.getWorld().schedule(new Task(1, true) {
			@Override
			public void execute() {
				player.removeAllAttributes();
				player.getAttributes().put("duel_stage", DuelStage.FIGHTING_STAGE);
				otherPlayer.getAttributes().put("duel_stage", DuelStage.FIGHTING_STAGE);
				player.getActionSender().sendEntityHint(otherPlayer, true);
				otherPlayer.getActionSender().sendEntityHint(player, true);
				player.createTimedAttribute("duel_count", 3);
				stop();
			}
		});
	}

	public void finishDuelMatch() {
		player.getActionSender().sendInteractionOption("null", 3, false);
		otherPlayer.getActionSender().sendInteractionOption("null", 3, false);
		player.faceEntity(null);
		otherPlayer.faceEntity(null);
		World.getWorld().schedule(new Task(1, true) {

			@Override
			public void execute() {
				PrayerHandler.resetAll(player);
				PrayerHandler.resetAll(otherPlayer);
				for (int index = 0; index < Skills.SKILL_COUNT; index++) {
					player.getSkills().setLevel(index, player.getSkills().getLevelForExperience(index));
					otherPlayer.getSkills().setLevel(index, otherPlayer.getSkills().getLevelForExperience(index));
					player.getActionSender().sendSkills();
					otherPlayer.getActionSender().sendSkills();
				}
				player.setTeleportTarget(new Location(3366 + Utility.random(3), 3266 - Utility.random(2), 0));
				otherPlayer.setTeleportTarget(new Location(3366 + Utility.random(3), 3266 - Utility.random(2), 0));
				
				player.getActionSender().sendEntityHint(otherPlayer, false);
				otherPlayer.getActionSender().sendEntityHint(player, false);
				for (int index = 0; index < EQUIPMENT_RESTRICTION_COUNT; index++) {
					player.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 0);
					otherPlayer.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 0);
					getEquipmentRestricted()[index] = false;
					otherPlayer.getDuelArena().getEquipmentRestricted()[index] = false;
				}
				for (int index = 0; index < DUEL_OPTION_COUNT; index++) {
					getOptionActive()[index] = false;
					otherPlayer.getDuelArena().getOptionActive()[index] = false;
				}
				player.removeAllAttributes();
				otherPlayer.removeAllAttributes();
				stop();
			}

		});
	}

	public void invokeDuelVictory() {
		World.getWorld().schedule(new Task(1, true) {

			public void execute() {
				sendDuelVictoryWidget();
				player.getActionSender().sendInterface(6733);
				ArrayList<Item> totalItems = new ArrayList<Item>();
				if (!offeredItems.isEmpty()) {
					totalItems.addAll(offeredItems);
					offeredItems.clear();
				}
				if (!otherPlayer.getDuelArena().offeredItems.isEmpty()) {
					totalItems.addAll(otherPlayer.getDuelArena().offeredItems);					
					otherPlayer.getDuelArena().offeredItems.clear();
				}
				if(!totalItems.isEmpty()) {
					if(totalItems.size() > player.getInventory().getFreeSlots())
						player.getActionSender().sendMessage("Your inventory is too full, stakes have been sent to your bank.");
					
					totalItems.forEach((item) -> {
						if(player.getInventory().getFreeSlots() > 0) {
							player.getInventory().add(item);
						} else {
							player.getBank().add(item.getId(), item.getAmount());
						}
					});
				}
				PrayerHandler.resetAll(player);
				player.removeAllAttributes();
				otherPlayer.getDuelArena().setOtherPlayer(null);
				setOtherPlayer(null);
				stop();
			}
		});
	}

	private void sendDuelVictoryWidget() {
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeShort(48500);
		player.getOutStream().writeShort(otherPlayer.getDuelArena().offeredItems.size());
		for (Item item : otherPlayer.getDuelArena().offeredItems) {
			if (item.amount > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(item.amount);
			} else {
				player.getOutStream().writeByte(item.amount);
			}
			if (item.id > ItemDefinition.getMaximumItems() || item.id < 0) {
				item.id = ItemDefinition.getMaximumItems();
			}
			player.getOutStream().writeWordBigEndianA(item.id + 1);
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}

	/**
	 * Sends the primary confirmation widget
	 */
	private void sendPrimaryConfirmationWidget() {
		player.getAttributes().remove("option_screen_accepted");
		player.getAttributes().put("duel_stage", DuelStage.PRIMARY_CONFIRMATION_SCREEN);
		player.getActionSender().sendString("", 48788);
		int widgetId = 48339;
		int totalValue = 0;
		String temporaryString = "";
		for (Item item : offeredItems) {
			temporaryString = temporaryString + "" + item.getAmount() + " X " + item.getDefinition().getName() + "\\n";
			totalValue += item.getDefinition().getValue() * item.getAmount();
		}
		player.getActionSender().sendString(temporaryString, widgetId);
		int secondaryWidgetId = 48370;
		int secondaryValue = 0;
		temporaryString = "";
		for (Item item : getOtherPlayer().getDuelArena().offeredItems) {
			temporaryString = temporaryString + "" + item.getAmount() + " X " + item.getDefinition().getName() + "\\n";
			secondaryValue += item.getDefinition().getValue() * item.getAmount();
		}
		player.getActionSender().sendString(temporaryString, secondaryWidgetId);
		for (int index = 0; index < EquipmentContainer.SIZE; index++) {
			final Item equipment = player.getEquipment().get(index);
			if (equipment == null)
				continue;
			sendDuelEquipmentWidget(new Item(equipment.getId(), equipment.getAmount()), index);
		}
		temporaryString = "";
		for (int index = -2; index < 6; index++) {
			if (index == -2)
				temporaryString = temporaryString + "" + otherPlayer + "\\n";
			else if (index == -1)
				temporaryString = temporaryString + "Combat Level: " + (int) otherPlayer.getSkills().getCombatLevel()
						+ "\\n";
			else
				temporaryString = temporaryString + "" + Skills.SKILL_NAME[index] + ": "
						+ otherPlayer.getSkills().getLevel(index) + "/"
						+ otherPlayer.getSkills().getLevelForExperience(index) + "\\n";
		}
		player.getActionSender().sendString(temporaryString, 48383);

		temporaryString = "";

		final boolean restrictedItems = itemsRestricted();

		if (restrictedItems) {
			temporaryString = temporaryString + "Some worn items will be,\\ntaken off.\\n";
		}
		temporaryString = temporaryString + "Boosted stats will be\\nrestored.\\nExisting prayers will be\\nstopped.";

		player.getActionSender().sendString(temporaryString, 48385);

		temporaryString = "";

		for (int index = 0; index < optionActive.length; index++) {
			if (optionActive[index])
				temporaryString = temporaryString + "" + DuelOptions.get(index).getName() + "\\n";
		}

		player.getActionSender().sendString(temporaryString, 48387);
		player.getActionSender().sendString(Utility.formatNumbers(totalValue) + " gp", 48307);
		player.getActionSender().sendString(Utility.formatNumbers(secondaryValue) + " gp", 48309);
		player.getActionSender().sendItemOnInterface(3322, player.getInventory().toArray());
		player.getActionSender().sendInterfaceWithInventoryOverlay(PRIMARY_CONFIRMATION_WIDGET_ID, 3321);
		World.getWorld().schedule(new Task(1) {
			public int timer;
			@Override
			public void execute() {
				switch(timer++) {
				case 0:
					player.getActionSender().sendString("Check", 48336);
					break;
				case 5:
					player.getActionSender().sendString("@gre@Accept", 48336);
					stop();
					break;
				}
			}
		});
	}

	private boolean itemsRestricted() {
		boolean restricted = false;
		for (int index = 0; index < equipmentRestricted.length; index++) {
			if (equipmentRestricted[index]) {
				restricted = true;
				break;
			}
		}
		return restricted;
	}

	/**
	 * Sends duel offer widget
	 */
	private void sendOfferWidget() {
		for (int index = 0; index < InventoryContainer.SIZE; index++) {
			player.getActionSender().sendItemOnInterface(48224, index, -1, 0);
			player.getActionSender().sendItemOnInterface(48226, index, -1, 0);
		}
		player.getActionSender().sendString("", 48221);
		player.getActionSender().sendString("", 48222);
		player.getAttributes().put("duel_stage", DuelStage.DUEL_OFFER_SCREEN);
		player.getActionSender().sendString(otherPlayer + "'s stake: ", 48205);
		if (getOptionActive()[12]) {
			final Collection<Item> otherPlayersInventory = Arrays.asList(otherPlayer.getInventory().toArray());
			player.getActionSender().sendItemsOnInterface(48220, otherPlayersInventory);
		}
		player.getActionSender().sendItemOnInterface(3322, player.getInventory().toArray());
		player.getActionSender().sendInterfaceWithInventoryOverlay(DUEL_OFFER_WIDGET_ID, 3321);
	}

	/**
	 * Offer items to the stake
	 */
	public void offerItem(final int id, final int slot, int count) {
		if (player.getAttributes().get("duel_stage") != DuelStage.DUEL_OFFER_SCREEN
				|| otherPlayer.getAttributes().get("duel_stage") != DuelStage.DUEL_OFFER_SCREEN) {
			return;
		}

		if (id != player.getInventory().get(slot).getId()) {
			return;
		}

		if (count > player.getInventory().getAmount(id)) {
			count = player.getInventory().getAmount(id);
		}

		final ItemDefinition itemDefinitions = ItemDefinition.get(id);

		if (Objects.isNull(itemDefinitions)) {
			return;
		}

		if (!itemDefinitions.isTradeable()) {
			player.getActionSender().sendMessage("You can't stake this item.");
			return;
		}

		player.getInventory().remove(new Item(id, count));

		boolean isItemOffered = false;

		for (Item item : offeredItems) {
			if (id == item.getId() && itemDefinitions.isStackable()) {
				isItemOffered = true;
				item.setAmount(item.getAmount() + count);
				break;
			}
		}
		if (!isItemOffered) {
			if (itemDefinitions.isStackable()) {
				offeredItems.add(new Item(id, count));
			} else {
				int temporaryCount = 0;
				temporaryCount = count;
				while (temporaryCount > 0) {
					offeredItems.add(new Item(id, 1));
					temporaryCount--;
				}
			}
		}
		int totalValue = 0;
		for (Item item : offeredItems) {
			totalValue += item.getDefinition().getValue() * item.getAmount();
		}
		player.getActionSender().sendString(Utility.formatNumbers(totalValue) + " gp", 48207);
		otherPlayer.getActionSender().sendString(Utility.formatNumbers(totalValue) + " gp", 48208);
		player.getActionSender().sendItemOnInterface(3322, player.getInventory().toArray());
		player.getActionSender().sendItemsOnInterface(48224, offeredItems);
		otherPlayer.getActionSender().sendItemsOnInterface(48226, player.getDuelArena().offeredItems);
		if (getOptionActive()[12]) {
			final Collection<Item> otherPlayersInventory = Arrays.asList(otherPlayer.getInventory().toArray());
			player.getActionSender().sendItemsOnInterface(48220, otherPlayersInventory);
			final Collection<Item> playerInventory = Arrays.asList(player.getInventory().toArray());
			otherPlayer.getActionSender().sendItemsOnInterface(48220, playerInventory);
		}
		player.getAttributes().remove("offer_screen_accepted");
		player.getActionSender().sendString("", 48222);
		otherPlayer.getActionSender().sendString("", 48222);
	}

	/**
	 * Gets total item count of a non-stackable item
	 * 
	 * @param id
	 * @return
	 */
	private final int getItemCount(final int id) {
		int count = 0;
		for (Item item : offeredItems) {
			if (id == item.getId()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Removes item from the offeredItem array
	 * 
	 * @param id
	 * @param count
	 * @param slot
	 */
	public void removeItem(final int id, int count, final int slot) {
		if (player.getAttributes().get("duel_stage") != DuelStage.DUEL_OFFER_SCREEN
				|| otherPlayer.getAttributes().get("duel_stage") != DuelStage.DUEL_OFFER_SCREEN) {
			return;
		}
		final ItemDefinition itemDefinitions = ItemDefinition.get(id);

		if (Objects.isNull(itemDefinitions)) {
			return;
		}

		if (!itemDefinitions.isStackable()) {
			if (count > getItemCount(id) && !itemDefinitions.isStackable()) {
				count = getItemCount(id);
			}
			int temporaryCount = 0;
			while (temporaryCount < count) {
				for (Item item : offeredItems) {
					if (id == item.getId()) {
						player.getInventory().add(item);
						offeredItems.remove(item);
						break;
					}
				}
				temporaryCount++;
			}
		}
		if (itemDefinitions.isStackable()) {
			if (count > offeredItems.get(slot).getAmount()) {
				count = offeredItems.get(slot).getAmount();
			}
			offeredItems.set(slot, new Item(id, offeredItems.get(slot).getAmount() - count));
			player.getInventory().add(new Item(offeredItems.get(slot).getId(), count));
			if (offeredItems.get(slot).getAmount() <= 0) {
				offeredItems.remove(slot);
			}
		}
		int totalValue = 0;
		for (Item item : offeredItems) {
			totalValue -= item.getDefinition().getValue() * count;
		}
		player.getActionSender().sendString(Utility.formatNumbers(totalValue) + " gp", 48207);
		otherPlayer.getActionSender().sendString(Utility.formatNumbers(totalValue) + " gp", 48208);
		player.getActionSender().sendItemOnInterface(3322, player.getInventory().toArray());
		player.getActionSender().sendItemsOnInterface(48224, offeredItems);
		otherPlayer.getActionSender().sendItemsOnInterface(48226, player.getDuelArena().offeredItems);
		if (getOptionActive()[12]) {
			final Collection<Item> otherPlayersInventory = Arrays.asList(otherPlayer.getInventory().toArray());
			player.getActionSender().sendItemsOnInterface(48220, otherPlayersInventory);
			final Collection<Item> playerInventory = Arrays.asList(player.getInventory().toArray());
			otherPlayer.getActionSender().sendItemsOnInterface(48220, playerInventory);
		}
		player.getAttributes().remove("offer_screen_accepted");
		player.getActionSender().sendString("", 48222);
		otherPlayer.getActionSender().sendString("", 48222);
	}

	public void decline() {
		if (!offeredItems.isEmpty()) {
			offeredItems.forEach((item) -> {
				player.getInventory().add(item);
			});
			offeredItems.clear();
		}
		if (!getOtherPlayer().getDuelArena().offeredItems.isEmpty()) {
			getOtherPlayer().getDuelArena().offeredItems.forEach((item) -> {
				getOtherPlayer().getInventory().add(item);
			});
			getOtherPlayer().getDuelArena().offeredItems.clear();
		}
		for (int index = 0; index < EQUIPMENT_RESTRICTION_COUNT; index++) {
			player.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 0);
			otherPlayer.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[index], 0);
			getEquipmentRestricted()[index] = false;
			otherPlayer.getDuelArena().getEquipmentRestricted()[index] = false;
		}
		for (int index = 0; index < DUEL_OPTION_COUNT; index++) {
			getOptionActive()[index] = false;
			otherPlayer.getDuelArena().getOptionActive()[index] = false;
		}
		player.removeAllAttributes();
		otherPlayer.removeAllAttributes();
		player.getActionSender().removeAllInterfaces();
		otherPlayer.getActionSender().removeAllInterfaces();
		setOtherPlayer(null);
	}

	/**
	 * Option id's
	 */
	private static final int[] OPTION_ID = new int[] { 48044, 48046, 48048, 48050, 48052, 48054, 48056, 48058, 48060,
			48062, 48064, 48066, 48068
	};

	/**
	 * Combat stat widget ids / frame id's
	 */
	private static final int[] STAT_WIDGET_ID = new int[] { 48006, 48010, 48014, 48018, 48022, 48026, 48030 };

	/**
	 * Combat stat widget ids / frame id's
	 */
	private static final int[] STAT_WIDGET_ID_OPPONENT = new int[] { 48008, 48012, 48016, 48020, 48024, 48028, 48032 };
	
	/**
	 * Restriction config ids.
	 */
	private static final int[] RESTRICTION_CONFIG_ID = new int[] { 48079, 48080, 48081, 48082, 48083, 48084, 48085,
			48086, 48087, 48088, 48089 };

	/**
	 * Resets the duel option widget configurations
	 */
	private void resetDuelOptionWidget() {
		IntStream.range(0, OPTION_ID.length).forEach(option -> player.getActionSender().sendConfig(OPTION_ID[option], 0));
		player.getActionSender().sendString("Dueling with: " + otherPlayer, 48003);
		player.getActionSender().sendString("Combat Level: " + (int) otherPlayer.getSkills().getCombatLevel(), 48004);
		IntStream.range(0, STAT_WIDGET_ID.length).forEach(stats -> player.getActionSender().sendString("" + player.getSkills().getLevel(stats), STAT_WIDGET_ID[stats]));
		IntStream.range(0, STAT_WIDGET_ID.length).forEach(stats -> otherPlayer.getActionSender().sendString("" + otherPlayer.getSkills().getLevel(stats), STAT_WIDGET_ID_OPPONENT[stats]));
		IntStream.range(0, EquipmentContainer.SIZE).forEach(equipment -> sendDuelEquipmentWidget(new Item(player.getEquipment().getId(equipment), player.getEquipment().getAmount(equipment)), equipment));
		IntStream.range(0, EQUIPMENT_RESTRICTION_COUNT).forEach(restrictions -> player.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[restrictions], 0));
		IntStream.range(0, DUEL_OPTION_COUNT).forEach(active_options -> getOptionActive()[active_options] = false);
		player.getActionSender().sendString("", 48079);
	}
	
	/**
	 * Sends duel equipment to the duel equipment widget
	 * 
	 * @param itemId
	 * @param amount
	 * @param slot
	 */
	private void sendDuelEquipmentWidget(Item item, int slot) {
		player.outStream.putFrameVarShort(34);
		int offset = player.getOutStream().offset;
		player.outStream.writeShort(13824);
		player.outStream.writeByte(slot);
		if (item != null) {
			player.outStream.writeShort(item.getId() + 1);
			player.outStream.writeByte(255);
			player.outStream.putInt(item.getAmount());
		} else {
			player.outStream.writeShort(0);
			player.outStream.writeByte(255);
			player.outStream.writeByte(0);
		}
		player.outStream.putFrameSizeShort(offset);
	}

	/**
	 * Selects duel options
	 * 
	 * @param optionId
	 */
	private void selectOption(final int optionId) {
		if (player.getAttributes().get("duel_stage") != DuelStage.DUEL_OPTION_SCREEN
				|| otherPlayer.getAttributes().get("duel_stage") != DuelStage.DUEL_OPTION_SCREEN) {
			return;
		}
		
		if (getOptionActive()[optionId]) {
			if (optionId == DuelOptions.SHOW_INVENTORIES.getId())
			{
				player.getActionSender().sendMessage("You can't turn that rule off.");
				return;
			}
			player.getActionSender().sendConfig(OPTION_ID[optionId], 0);
			otherPlayer.getActionSender().sendConfig(OPTION_ID[optionId], 0);
			getOptionActive()[optionId] = false;
			otherPlayer.getDuelArena().getOptionActive()[optionId] = false;
		} else {
			if ((player.getDuelArena().getOptionActive()[DuelOptions.NO_MELEE.getId()] && player.getDuelArena().getOptionActive()[DuelOptions.NO_RANGED.getId()]
					&& optionId == DuelOptions.NO_MAGIC.getId()) || 	(player.getDuelArena().getOptionActive()[DuelOptions.NO_MELEE.getId()] && player.getDuelArena().getOptionActive()[DuelOptions.NO_MAGIC.getId()]
					&& optionId == DuelOptions.NO_RANGED.getId()) || (player.getDuelArena().getOptionActive()[DuelOptions.NO_MAGIC.getId()] && player.getDuelArena().getOptionActive()[DuelOptions.NO_RANGED.getId()]
					&& optionId == DuelOptions.NO_MELEE.getId()) || (player.getDuelArena().getOptionActive()[DuelOptions.NO_MELEE.getId()] && optionId == DuelOptions.NO_FORFEIT.getId())  || (player.getDuelArena().getOptionActive()[DuelOptions.NO_FORFEIT.getId()] && optionId == DuelOptions.NO_MELEE.getId()))
			{
				player.getActionSender().sendMessage("You can't have that combination of rules.");
				return;
			}
			if (optionId == DuelOptions.OBSTACLES.getId())
			{
				player.getActionSender().sendMessage("You can't turn that rule on.");
				return;
			}
			if (optionId == DuelOptions.FUN_WEAPONS.getId())
			{
				if ((player.getEquipment().contains(4151) && otherPlayer.getEquipment().contains(4151) && !player.getEquipment().contains(12006) && !otherPlayer.getEquipment().contains(12006))
				    || (!player.getEquipment().contains(4151) && !otherPlayer.getEquipment().contains(4151) && player.getEquipment().contains(12006) && otherPlayer.getEquipment().contains(12006)))
				{
					
				}
				else
				{
					player.getActionSender().sendMessage("You and your opponent must both be wielding either an Abyssal Whip");
					player.getActionSender().sendMessage("or an Abyssal Tentacle to enable the fun weapon rule.");
					return;
				}
			}
			player.getActionSender().sendConfig(OPTION_ID[optionId], 1);
			otherPlayer.getActionSender().sendConfig(OPTION_ID[optionId], 1);
			getOptionActive()[optionId] = true;
			otherPlayer.getDuelArena().getOptionActive()[optionId] = true;
		}
		temporaryOptionActive = getOptionActive();
		otherPlayer.getDuelArena().temporaryOptionActive = getOptionActive();
		player.getActionSender().sendString("", 48095);
		otherPlayer.getActionSender().sendString("", 48095);
	}

	/**
	 * Restricts equipment
	 * 
	 * @param slot
	 */
	private void restrictEquipment(final int slot) {
		if (player.getAttributes().get("duel_stage") != DuelStage.DUEL_OPTION_SCREEN
				|| otherPlayer.getAttributes().get("duel_stage") != DuelStage.DUEL_OPTION_SCREEN) {
			return;
		}
		if (getEquipmentRestricted()[slot]) {
			player.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[slot], 0);
			otherPlayer.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[slot], 0);
			getEquipmentRestricted()[slot] = false;
			otherPlayer.getDuelArena().getEquipmentRestricted()[slot] = false;
		} else {
			int amountRestricted = 1;
			for(boolean j : getEquipmentRestricted())
				if(j) amountRestricted++;
			final Item item = player.getEquipment().get(EquipmentSlots.get(slot).getSlot());
			if (player.getInventory().getFreeSlots() < amountRestricted
					&& (!player.getInventory().contains(item.getId())
					|| player.getInventory().contains(item.getId()) && !item.getDefinition().isStackable())) {
				player.getActionSender().sendMessage("You don't have enough inventory space to do this.");
				return;
			}
			final Item secondaryItem = otherPlayer.getEquipment().get(EquipmentSlots.get(slot).getSlot());
			if (otherPlayer.getInventory().getFreeSlots() < amountRestricted
					&& (!otherPlayer.getInventory().contains(secondaryItem.getId())
					|| (otherPlayer.getInventory().contains(secondaryItem.getId()) && !secondaryItem.getDefinition().isStackable()))) {
				player.getActionSender().sendMessage(otherPlayer + " doesn't have enough inventory space to do this.");
				return;
			}
			player.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[slot], 1);
			otherPlayer.getActionSender().sendConfig(RESTRICTION_CONFIG_ID[slot], 1);
			getEquipmentRestricted()[slot] = true;
			otherPlayer.getDuelArena().getEquipmentRestricted()[slot] = true;
		}
		temporaryEquipmentRestricted = getEquipmentRestricted();
		otherPlayer.getDuelArena().temporaryEquipmentRestricted = getEquipmentRestricted();
		player.getActionSender().sendString("", 48095);
		otherPlayer.getActionSender().sendString("", 48095);
	}

	/**
	 * 
	 * @return the other player
	 */
	public Player getOtherPlayer() {
		return this.otherPlayer;
	}

	/**
	 * 
	 * @param player
	 *            the other player to set
	 */
	public void setOtherPlayer(final Player player) {
		this.otherPlayer = player;
	}

	/**
	 * @return the stage
	 */
	public DuelStage getStage() {
		return stage;
	}

	/**
	 * @param stage
	 *            the stage to set
	 */
	public void setStage(DuelStage stage) {
		this.stage = stage;
	}

	/**
	 * @return the optionActive
	 */
	public boolean[] getOptionActive() {
		return optionActive;
	}

	/**
	 * @param optionActive
	 *            the optionActive to set
	 */
	public void setOptionActive(boolean[] optionActive) {
		this.optionActive = optionActive;
	}

	/**
	 * @return the equipmentRestricted
	 */
	public boolean[] getEquipmentRestricted() {
		return equipmentRestricted;
	}

	/**
	 * @param equipmentRestricted
	 *            the equipmentRestricted to set
	 */
	public void setEquipmentRestricted(boolean[] equipmentRestricted) {
		this.equipmentRestricted = equipmentRestricted;
	}

	public enum DuelStage {
		// The option widget
		DUEL_OPTION_SCREEN,

		// The offer widget
		DUEL_OFFER_SCREEN,

		// primary confirmation screen
		PRIMARY_CONFIRMATION_SCREEN,

		// When in the fighting stage
		FIGHTING_STAGE
	}

	public enum EquipmentSlots {

		HELMET(0, EquipmentConstants.HELM_SLOT),

		AMULET(1, EquipmentConstants.NECKLACE_SLOT),

		BODY(2, EquipmentConstants.TORSO_SLOT),

		LEGS(3, EquipmentConstants.LEGS_SLOT),

		BOOTS(4, EquipmentConstants.BOOTS_SLOT),

		CAPE(5, EquipmentConstants.CAPE_SLOT),

		WEAPON(6, EquipmentConstants.WEAPON_SLOT),

		GLOVES(7, EquipmentConstants.GLOVES_SLOT),

		AMMO(8, EquipmentConstants.AMMO_SLOT),

		SHIELD(9, EquipmentConstants.SHIELD_SLOT),

		RING(10, EquipmentConstants.RING_SLOT);

		private EquipmentSlots(final int id, final int slot) {
			this.id = id;
			this.slot = slot;
		}

		private static final Set<EquipmentSlots> SET = Collections.unmodifiableSet(EnumSet.allOf(EquipmentSlots.class));

		public static final EquipmentSlots get(final int id) {
			return SET.stream().filter((set) -> set.id == id).findFirst().orElse(null);
		}

		private final int id;
		private final int slot;

		public final int getSlot() {
			return this.slot;
		}

	}

	public enum DuelOptions {
		NO_RANGED(0, "You cannot use range\\nduring the duel."),

		NO_MELEE(1, "You cannot use melee\\nduring the duel."),

		NO_MAGIC(2, "You cannot use magic\\nduring the duel."),

		NO_SPECIAL_ATTACK(3, "You cannot use special attacks\\nduring the duel."),

		FUN_WEAPONS(4, "You can't use fun weapons,\\nduring this duel."),

		NO_FORFEIT(5, "You cannot forfeit the\\nduel"),

		NO_PRAYER(6, "You cannot use prayer,\\nduring this duel"),

		NO_DRINKS(7, "You cannot drink consumables,\\nduring this duel."),

		NO_FOOD(8, "You cannot eat consumables,\\nduring this duel"),

		NO_MOVEMENT(9, "You can't move,\\nduring this duel"),

		OBSTACLES(10, "Obstacle maps"),

		NO_WEAPON_SWITCH(11, "You cannot switch weapons,\\nduring this duel"),

		SHOW_INVENTORIES(12, "Your opponents inventory will show");

		private DuelOptions(final int id, final String name) {
			this.id = id;
			this.name = name;
		}

		private static final Set<DuelOptions> SET = Collections.unmodifiableSet(EnumSet.allOf(DuelOptions.class));

		public static final DuelOptions get(final int id) {
			return SET.stream().filter((set) -> set.getId() == id).findFirst().orElse(null);
		}

		private final int id;
		private final String name;

		public final int getId() {
			return this.id;
		}

		public final String getName() {
			return this.name;
		}
	}
}