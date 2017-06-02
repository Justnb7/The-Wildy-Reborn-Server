package com.model.game.character.player.content.multiplayer.duel;

import com.model.Server;
import com.model.game.Constants;
import com.model.game.character.Animation;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.PrayerHandler;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSession;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.game.character.player.serialize.PlayerSerialization;
import com.model.game.definitions.ItemDefinition;
import com.model.game.item.GameItem;
import com.model.game.item.Item;
import com.model.game.item.container.impl.EquipmentContainer;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.location.Location;
import com.model.net.packet.out.SendSoundPacket;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventContainer;
import com.model.task.events.CycleEventHandler;
import com.model.utility.Utility;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DuelSession extends MultiplayerSession {

	DuelSessionRules rules = new DuelSessionRules();
	private Optional<Player> winner = Optional.empty();
	private boolean attackingOperationable;
	private Boundary arenaBoundary;
	
	static final Boundary NO_OBSTACLE_ARENA = Boundary.DUEL_ARENAS[0];

	static final Boundary OBSTACLE_ARENA = Boundary.DUEL_ARENAS[1];
	
	public static final Boundary[] DUEL_ARENAS = new Boundary[] {
			new Boundary(3332, 3244, 3359, 3259),
			new Boundary(3364, 3244, 3389, 3259) };

	private long lastRuleModification;

	public DuelSession(List<Player> players, MultiplayerSessionType type) {
		super(players, type);
	}

	@Override
	public void give() {
		if (!winner.isPresent()) {
			return;
		}
		players.forEach(player -> moveAndClearAttributes(player));
		if (!Objects.equals(getOther(winner.get()), winner.get())) {
			items.get(winner.get()).addAll(items.get(getOther(winner.get())));
			if (items.get(winner.get()).size() > 0) {
				for (GameItem item : items.get(winner.get())) {
					long totalSum = (long) winner.get().getInventory().getAmount(item.id) + item.amount;
					if (winner.get().getInventory().getFreeSlots() == 0 || winner.get().getInventory().contains(item.id) && totalSum > Integer.MAX_VALUE) {
						//TODO sent item to bank or drop on floor
						//winner.get().getBank().sendItemToAnyTabOrDrop(new BankItem(item.id, item.amount), Constants.DUELING_RESPAWN_X + (Utility.exclusiveRandom(Constants.RANDOM_DUELING_RESPAWN)), Constants.DUELING_RESPAWN_Y + (Utility.exclusiveRandom(Constants.RANDOM_DUELING_RESPAWN)));
					} else {
						winner.get().getInventory().add(new Item(item.id, item.amount));
					}
				}
			}
			winner.get().write(new SendSoundPacket(77, 1, 2));
			showRewardComponent(winner.get());
		} else {
			winner.get().getActionSender().sendMessage("You cannot be the winner and the loser of a duel.");
		}
		GroundItemHandler.reloadGroundItems(winner.get());
		items.clear();
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void withdraw() {
		for (Player player : items.keySet()) {
			if (Objects.isNull(player)) {
				continue;
			}
			if (items.get(player).size() <= 0) {
				continue;
			}
			for (GameItem item : items.get(player)) {
				player.getInventory().add(new Item(item.id, item.amount));
			}
		}
	}

	@Override
	public void logSession(MultiplayerSessionFinalizeType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void accept(Player player, Player recipient, int stageId) {
		switch (stageId) {
		case MultiplayerSessionStage.OFFER_ITEMS:
			if (System.currentTimeMillis() - lastRuleModification < 5_000) {
				player.getActionSender().sendMessage("<col=CC0000>A rule was changed in the last 5 seconds, you cannot accept yet.");
				player.getActionSender().sendString("A rule was changed in recently, you cannot accept yet.", 6684);
				return;
			}
			if (recipient.getInventory().getFreeSlots() < getItems(player).size()) {
				player.getActionSender().sendString("You have offered more items than " + recipient.getName() + " has free space.", 6684);
				recipient.getActionSender().sendString("You do not have enough inventory space to continue.", 6684);
				return;
			}
			if (recipient.getInventory().getFreeSlots() < getDisabledEquipmentCount(recipient)) {
				player.getActionSender().sendString("Player doesn't have enough space to unequip the disabled items.", 6684);
				recipient.getActionSender().sendString("Not enough space to remove the disabled equipped items.", 6684);
				return;
			}
			if (rules.contains(Rule.NO_MELEE) && rules.contains(Rule.NO_MAGE) && rules.contains(Rule.NO_WEAPON)) {
				player.getActionSender().sendString("You cannot have no melee, no mage and no weapon selected.", 6684);
				recipient.getActionSender().sendString("You cannot have no melee, no mage and no weapon selected.", 6684);
				return;
			}
			for (Player p : players) {
				GameItem overlap = getOverlappedItem(p);
				if (overlap != null) {
					p.getActionSender().sendString("Too many of one item! The other player has " + Utility.getValueRepresentation(overlap.amount) + " " + ItemDefinition.get(overlap.id) + " in their inventory.", 6684);
					getOther(p).getActionSender().sendString("The other player has offered too many of one item, they must remove some.", 6684);
					return;
				}
			}
			if (stage.hasAttachment() && stage.getAttachment() != player) {
				stage.setStage(MultiplayerSessionStage.CONFIRM_DECISION);
				stage.setAttachment(null);
				updateMainComponent();
				return;
			}
			player.getActionSender().sendString("Waiting for other player...", 6684);
			stage.setAttachment(player);
			recipient.getActionSender().sendString("Other player has accepted", 6684);
			break;

		case MultiplayerSessionStage.CONFIRM_DECISION:
			if (stage.hasAttachment() && stage.getAttachment() != player) {
				stage.setStage(MultiplayerSessionStage.FURTHER_INTERACTION);
				Player opponent = getOther(player);
				clearPlayerAttributes(player);
				clearPlayerAttributes(opponent);
				arenaBoundary = rules.contains(Rule.OBSTACLES) ? OBSTACLE_ARENA : NO_OBSTACLE_ARENA;
				int teleportX = arenaBoundary.getMinimumX() + 6 + Utility.exclusiveRandom(12);
				int teleportY = arenaBoundary.getMinimumY() + 1 + Utility.exclusiveRandom(11);
				player.movePlayer(new Location(teleportX, teleportY, 0));
				opponent.movePlayer(new Location(teleportX, teleportY - 1, 0));
				player.getActionSender().createPlayerHint(10, opponent.getIndex());
				opponent.getActionSender().createPlayerHint(10, player.getIndex());
				player.getActionSender().removeAllInterfaces();
				opponent.getActionSender().removeAllInterfaces();
				removeDisabledEquipment(player);
				removeDisabledEquipment(opponent);
				CycleEventHandler.getSingleton().addEvent(this, new AttackingOperation(), 2);
				return;
			}
			stage.setAttachment(player);
			player.getActionSender().sendString("Waiting for other player...", 6571);
			recipient.getActionSender().sendString("Other player has accepted", 6571);
			break;

		default:
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			break;
		}
	}

	@Override
	public boolean itemAddable(Player player, GameItem item) {

		if (item.id == 12926 || item.id == 12931 || item.id == 12904) {
			player.getActionSender().sendMessage("You cannot stake this item, it is deemed as untradable.");
			return false;
		}
		
		if (stage.getStage() != MultiplayerSessionStage.OFFER_ITEMS) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		return true;
	}

	@Override
	public boolean itemRemovable(Player player, GameItem item) {
		if (!Server.getMultiplayerSessionListener().inAnySession(player)) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		if (stage.getStage() != MultiplayerSessionStage.OFFER_ITEMS) {
			return false;
		}
		return true;
	}

	@Override
	public void updateMainComponent() {
		if (stage.getStage() == MultiplayerSessionStage.OFFER_ITEMS) {
			for (Player player : players) {
				Player recipient = getOther(player);
				player.getActionSender().sendUpdateItems(3322, player.getInventory().toArray());
				refreshItemContainer(player, player, 6669);
				refreshItemContainer(player, player, 6670);
				player.getActionSender().sendString("Dueling with: " + recipient.getName() + " (level-" + recipient.combatLevel + ")", 6671);
				player.getActionSender().sendString("", 6684);
				player.getActionSender().sendInterfaceWithInventoryOverlay(6575, 3321);
				player.getActionSender().sendFrame87(286, 0);
			}
		} else if (stage.getStage() == MultiplayerSessionStage.CONFIRM_DECISION) {
			for (Player player : players) {
				Player recipient = getOther(player);
				player.getActionSender().sendUpdateItems(3214, player.getInventory().toArray());
				StringBuilder itemList = new StringBuilder();
				List<GameItem> items = getItems(player);
				for (GameItem item : items) {
					if (item.id > 0 && item.amount > 0) {
						itemList.append(ItemDefinition.get(item.id) + " x " + Utility.getValueRepresentation(item.amount) + "\\n");
					}
				}
				player.getActionSender().sendString(itemList.toString(), 6516);
				itemList = new StringBuilder();
				items = getItems(recipient);
				for (GameItem item : items) {
					if (item.id > 0 && item.amount > 0) {
						itemList.append(ItemDefinition.get(item.id) + " x " + Utility.getValueRepresentation(item.amount) + "\\n");
					}
				}
				player.getActionSender().sendString(itemList.toString(), 6517);
				player.getActionSender().sendString("", 8242);
				for (int i = 8238; i <= 8253; i++) {
					player.getActionSender().sendString("", i);
				}
				player.getActionSender().sendString("Hitpoints will be restored.", 8250);
				player.getActionSender().sendString("Boosted stats will be reset.", 8238);
				int offset = 0;
				for (Rule rule : rules.rules) {
					if (!rule.getDetails().isEmpty()) {
						player.getActionSender().sendString(rule.getDetails(), 8242 + offset);
						offset++;
					}
				}
				player.getActionSender().sendString("", 6571);
				player.getActionSender().sendInterfaceWithInventoryOverlay(6412, 197);
			}
		}
	}

	@Override
	public void updateOfferComponents() {
		for (Player player : items.keySet()) {
			Player recipient = getOther(player);
			player.getActionSender().sendUpdateItems(3322, player.getInventory().toArray());
			refreshItemContainer(player, player, 6669);
			refreshItemContainer(player, getOther(player), 6670);
			player.getActionSender().sendString("", 6684);
			player.getActionSender().sendString("Dueling with: " + recipient.getName() + " (level-" + recipient.combatLevel + ")", 6671);
		}
	}

	public void setWinner(Player winner) {
		this.winner = Optional.of(winner);
	}

	public Optional<Player> getWinner() {
		return winner;
	}

	public boolean isAttackingOperationable() {
		return attackingOperationable;
	}
	
	public Boundary getArenaBoundary() {
		return arenaBoundary;
	}

	class AttackingOperation extends CycleEvent {

		int time = 3;

		@Override
		public void execute(CycleEventContainer container) {
			for (Player player : players) {
				if (Objects.isNull(player)) {
					finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
					container.stop();
					return;
				}
			}
			if (time <= 0) {
				players.stream().filter(Objects::nonNull).forEach(p -> p.sendForcedMessage("FIGHT!"));
				attackingOperationable = true;
				container.stop();
			} else if (time > 0) {
				players.stream().filter(Objects::nonNull).forEach(p -> p.sendForcedMessage(Integer.toString(time)));
				time--;
			} else {
				container.stop();
			}
		}
	}

	public void moveAndClearAttributes(Player player) {
		player.getWeaponInterface().sendSpecialBar(player.getEquipment().get(EquipmentContainer.WEAPON_SLOT));
		player.getActionSender().createPlayerHint(10, -1);
		player.movePlayer(new Location(Constants.DUELING_RESPAWN_X, Constants.DUELING_RESPAWN_Y, 0));
		restorePlayerAttributes(player);
		player.getWeaponInterface().restoreWeaponAttributes();
	}

	private void restorePlayerAttributes(Player player) {
		//reset special attack
		player.setUsingSpecial(false);
		player.setSpecialAmount(100);
		player.getWeaponInterface().restoreWeaponAttributes();
		
		//Reset our combat variables
		player.setVengeance(false);
		player.lastVeng.reset();
		
		player.infection = 0;
		player.infected = false;
		player.poisonDamage = 0;
		player.venomDamage = 0;
		player.freeze(0);
		Combat.resetCombat(player);
		player.attackedPlayers.clear();
		player.getDamageMap().resetDealtDamage();
		
		//refresh prayers
		PrayerHandler.resetAllPrayers(player);
		
		//Refresh skills
		for (int i = 0; i < 20; i++) {
        	player.getSkills().setLevel(i, player.getSkills().getLevelForExperience(i));
        }
		
		//Save our player
		//PlayerSerialization.saveGame(player);
		PlayerSerialization.save(player);
		
		//Close all open windows
		player.getActionSender().removeAllInterfaces();
		
		//reset player variables
		player.playAnimation(Animation.create(65535));
		player.setFollowing(null);
	}

	private void clearPlayerAttributes(Player player) {
		restorePlayerAttributes(player);
		player.getWeaponInterface().restoreWeaponAttributes();
	}

	public void showRewardComponent(Player player) {
		if (Objects.isNull(player) || Objects.isNull(getOther(player))) {
			return;
		}
		List<GameItem> itemList = items.get(getOther(player));
		if (itemList.size() > 28) {
			itemList.subList(0, 27);
		}
		player.getActionSender().sendString(Integer.toString(getOther(player).combatLevel), 6839);
		player.getActionSender().sendString(getOther(player).getName(), 6840);
		player.getActionSender().sendInterface(6733);
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(6822);
		player.getOutStream().writeWord(itemList.size());
		for (GameItem item : itemList) {
			if (item.amount > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(item.amount);
			} else {
				player.getOutStream().writeByte(item.amount);
			}
			if (item.id > Constants.MAX_ITEMS || item.id < 0) {
				item = new GameItem(Constants.MAX_ITEMS, item.id);
			}
			player.getOutStream().writeWordBigEndianA(item.id + 1);
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}

	/*public void sendDuelEquipment() {
		players.stream().filter(Objects::nonNull).forEach(c -> {
			for (int i = 0; i < c.playerEquipment.length; i++) {
				c.getOutStream().createFrameVarSizeWord(34);
				c.getOutStream().writeWord(13824);
				c.getOutStream().writeByte(i);
				c.getOutStream().writeWord(c.playerEquipment[i] + 1);
				if (c.playerEquipment[i] > -1) {
					if (c.playerEquipmentN[i] > 254) {
						c.getOutStream().writeByte(255);
						c.getOutStream().putInt(c.playerEquipmentN[i]);
					} else {
						c.getOutStream().writeByte(c.playerEquipmentN[i]);
					}
				} else {
					c.getOutStream().writeByte(0);
				}
				c.getOutStream().endFrameVarSizeWord();
				c.flushOutStream();
			}
		});
	}*/

	public void toggleRule(Player player, Rule rule) {
		if (stage.getStage() != MultiplayerSessionStage.OFFER_ITEMS) {
			player.getActionSender().sendMessage("You cannot change rules whilst on the second interface.");
			return;
		}
		if (rule.equals(Rule.NO_MELEE) || rule.equals(Rule.NO_RANGE) || rule.equals(Rule.NO_MAGE)) {
			long count = rules.rules.stream().filter(r -> r.equals(Rule.NO_MELEE) || r.equals(Rule.NO_RANGE) || r.equals(Rule.NO_MAGE)).count();
			if (count >= 2 && !rules.contains(rule)) {
				player.getActionSender().sendString("You must fight with at least one combat style.", 6684);
				return;
			}
		}
		if (rules.contains(rule)) {
			rules.setTotalValue(rules.getTotalValue() - rule.getValue());
			rules.remove(rule);
		} else {
			rules.setTotalValue(rules.getTotalValue() + rule.getValue());
			rules.add(rule);
		}
		if (rules.contains(Rule.WHIP_AND_DDS) && rule != Rule.WHIP_AND_DDS && rule != Rule.NO_SPECIAL_ATTACK) {
			if (rule.equals(Rule.NO_MELEE) || rule.equals(Rule.OBSTACLES) || rule.equals(Rule.NO_WEAPON)) {
				rules.remove(Rule.WHIP_AND_DDS);
				rules.setTotalValue(rules.getTotalValue() - Rule.WHIP_AND_DDS.getValue());
			}
		}
		lastRuleModification = System.currentTimeMillis();
		stage.setAttachment(null);
		player.getActionSender().sendString("", 6684);
		getOther(player).getActionSender().sendString("", 6684);
		refreshRules();
	}

	private int getDisabledEquipmentCount(Player player) {
		final int MINIMUM_VALUE = Rule.NO_HELM.ordinal();
		int count = 0;
		for (Rule rule : Rule.values()) {
			if (rule.ordinal() < MINIMUM_VALUE) {
				continue;
			}
			if (!rules.contains(rule)) {
				continue;
			}
			int equipmentSlot = rule.ordinal() - MINIMUM_VALUE;
			if (equipmentSlot >= 6) {
				equipmentSlot++;
				if (equipmentSlot >= 8) {
					equipmentSlot++;
					if (equipmentSlot >= 11) {
						equipmentSlot++;
					}
				}
			}
			if (equipmentSlot == 6 || equipmentSlot == 8 || equipmentSlot == 11) {
				continue;
			}
			/*if (player.playerEquipment[equipmentSlot] > -1) {
				count++;
			}*/
		}
		return count;
	}

	private void removeDisabledEquipment(Player player) {
		final int MINIMUM_VALUE = Rule.NO_HELM.ordinal();
		for (Rule rule : Rule.values()) {
			if (rule.ordinal() < MINIMUM_VALUE) {
				continue;
			}
			if (!rules.contains(rule)) {
				continue;
			}
			int equipmentSlot = rule.ordinal() - MINIMUM_VALUE;
			if (equipmentSlot >= 6) {
				equipmentSlot++;
				if (equipmentSlot >= 8) {
					equipmentSlot++;
					if (equipmentSlot >= 11) {
						equipmentSlot++;
					}
				}
			}
			if (equipmentSlot == 6 || equipmentSlot == 8 || equipmentSlot == 11) {
				continue;
			}
			/*if (player.playerEquipment[equipmentSlot] > -1) {
				player.getItems().removeEquipment(player.playerEquipment[equipmentSlot], equipmentSlot);
			}*/
		}
	}

	public DuelSessionRules getRules() {
		return rules;
	}

	public void refreshRules() {
		players.stream().filter(Objects::nonNull).forEach(p -> p.getActionSender().sendFrame87(286, rules.getTotalValue()));
	}

}