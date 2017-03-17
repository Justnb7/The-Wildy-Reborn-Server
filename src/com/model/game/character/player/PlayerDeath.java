package com.model.game.character.player;

import java.util.Objects;

import com.model.Server;
import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.PrayerHandler;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.effect.PKHandler;
import com.model.game.character.player.content.achievements.AchievementType;
import com.model.game.character.player.content.achievements.Achievements;
import com.model.game.character.player.content.bounty_hunter.BountyHunter;
import com.model.game.character.player.content.bounty_hunter.BountyHunterConstants;
import com.model.game.character.player.content.bounty_hunter.BountyHunterEmblem;
import com.model.game.character.player.content.bounty_hunter.BountyTierHandler;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.minigames.pest_control.PestControl;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.player.serialize.PlayerSerialization;
import com.model.utility.Utility;

public class PlayerDeath {

	private final Player player;

	public PlayerDeath(Player player) {
		this.player = player;
	}
	/**
	 * While this is an Entity.. we don't have the code to check if an Npc was the one that
	 * did the final blow (yet)
	 */
	public Entity killedBy;

	public void characterDeath() {

		/**
		 * Player is death
		 */
		player.setDead(true);

		/**
		 * Represents the killer
		 */
		Player killer = null;

		/**
		 * p1 = dead p2 = killer p3 = most damage (gets drop)
		 */

		// last person to attack us will get pkp.
		if (player.killerId > -1) {
			killer = World.getWorld().getPlayers().get(player.killerId);
		}

		// Safety check
		if (killer != null && player != null) {

			killedBy = killer;
			player.killerId = killer.getIndex();

			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
				duelSession = null;
			}

			if (Objects.isNull(duelSession)) {
				
				/**
				 * Check if killer and opponent are both in the wilderness
				 */
				if (player.getArea().inWild() || killer.getArea().inWild() && player.killerId != player.getIndex()) {
					killedPlayer(player, killer);
					BountyHunter.handleOnDeath(player, killer);
					PlayerSerialization.saveGame(killer);
					PlayerSerialization.saveGame(player);
				}
				
				if (player.getAccount().getType().loseStatusOnDeath()) {
					player.message("");
				}
				
				player.write(new SendMessagePacket("Oh dear you are dead!"));
				player.setAttribute(BountyHunterConstants.HUNTER_CURRENT, 0);
				player.setAttribute(BountyHunterConstants.ROGUE_CURRENT, 0);
			}

			if (duelSession != null && duelSession.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERACTION) {
				if (!duelSession.getWinner().isPresent()) {
					player.write(new SendMessagePacket("You have lost the duel!"));
					Player opponent = duelSession.getOther(player);
					if (!duelSession.getWinner().isPresent()) {
						duelSession.setWinner(opponent);
					}
					PlayerSerialization.saveGame(opponent);
				} else {
					player.write(new SendMessagePacket("Congratulations, you have won the duel."));
				}
			}
		}

		/**
		 * Update player
		 */
		player.faceEntity(player);
		player.stopMovement();
		player.setPoisonDamage((byte) 0);
		player.getPA().restoreHealth();
	}

	public void giveLife() {
		player.setDead(false);
		player.resetFace();
		player.freeze(0);
		player.getActionSender().sendRemoveInterfacePacket();
		
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
		if (!player.getController().isSafe() && duelSession == null) {
			player.getItems().resetKeepItems();
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItems[i] - 1 > 0) {
					if (BountyHunterEmblem.get(player.playerItems[i] - 1) != null) {
						int newId = BountyTierHandler.downgrade(player, player.playerItems[i] - 1);
						if (newId != -1) {
							player.playerItems[i] = newId + 1;
						}
					}
				}
			}
			if (!player.isSkulled) {
				player.getItems().keepItem(0, true);
				player.getItems().keepItem(1, true);
				player.getItems().keepItem(2, true);
			}

			if (player.isActivePrayer(Prayers.PROTECT_ITEM) && player.lastProtItem.elapsed(700)) {
				player.getItems().keepItem(3, true);
			}
			player.getItems().dropAllItems();
			player.getItems().deleteAllItems();
			if (!player.isSkulled) {
				for (int i1 = 0; i1 < 3; i1++) {
					if (player.itemKeptId[i1] > 0) {
						player.getItems().addItem(player.itemKeptId[i1], 1);
					}
				}
			}
			if (player.isActivePrayer(Prayers.PROTECT_ITEM)) {
				if (player.itemKeptId[3] > 0) {
					player.getItems().addItem(player.itemKeptId[3], 1);
				}
			}
			player.getItems().resetKeepItems();
		}

		PrayerHandler.resetAllPrayers(player);
		
		for (int i = 0; i < 20; i++) {
        	player.getSkills().setLevel(i, player.getSkills().getLevelForExperience(i));
        }
		
		if (Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERACTION) {
				Player opponent = duelSession.getWinner().get();
				if (opponent != null) {
					opponent.getActionSender().createPlayerHint(10, -1);
					duelSession.finish(MultiplayerSessionFinalizeType.GIVE_ITEMS);
					PlayerSerialization.saveGame(player);
				}
			}
		} else {
			//TODO ask Jak if this is correct
			player.getController().onDeath(player);
			if (player.rights == Rights.ADMINISTRATOR && player.getName().equalsIgnoreCase("test") && killedBy != null) {
				player.getPA().movePlayer(killedBy.getPosition());
				player.message("ease of use dude");
			} else if (player.getController().getRespawnLocation(player) != null) {
				player.getPA().movePlayer(player.getController().getRespawnLocation(player));
	        } else if (Boundary.isIn(player, PestControl.GAME_BOUNDARY)) {
				player.getPA().movePlayer(2656 + Utility.random(2), 2614 - Utility.random(3), 0);
	        } else if (Boundary.isIn(player, Boundary.FIGHT_CAVE)) {
				player.getFightCave().exitCave(3);
	        }
		}

		player.isSkulled = false;
		player.skullTimer = 0;
		player.attackedPlayers.clear();
		PlayerSerialization.saveGame(player);
		Combat.resetCombat(player);
		player.getPA().resetAnimation();
		player.playAnimation(Animation.create(65535));
		player.getPA().resetTb();
		player.isSkulled = false;
		player.attackedPlayers.clear();
		player.skullIcon = -1;
		player.skullTimer = -1;
		player.killerId = -1;
		player.resetDamageReceived();
		player.getPA().requestUpdates();
		killedBy = null; // reset
	}

	private int trainedBonus;
	
	private void killedPlayer(Player player, Player killer) {

		long wealth = player.getItems().getWealth();
		double memberBonus = 1;
		int blood_Money = 3;

		if (player.equals(killer)) {
			System.out.println("The victim is the player when killed, this could be the issue.");
		}

		if (player.getArea().inWild() && player.killerId != player.getIndex()) {
			if (player.getMacAddress().equals(killer.getMacAddress())) {
				player.write(new SendMessagePacket("This fight doesn't have an effect on your PK reward."));
				killer.write(new SendMessagePacket("This fight doesn't have an effect on your PK reward."));
				return;
			}

			if (wealth > Constants.PK_POINTS_WEALTH)
				BountyHunter.handleBountyHunterKill(player, killer);

			killer.setCurrentKillStreak(killer.getCurrentKillStreak() + 1);
			killer.setWildernessKillStreak(killer.getWildernessKillStreak() + 1);

			if (killer.getCurrentKillStreak() > 2) {
				if (killer.getWildernessKillStreak() == killer.getCurrentKillStreak()) {
					PlayerUpdating.executeGlobalMessage("<img=12>[@red@Server@bla@]: @red@" + killer.getName() + "@red@ has killed @red@" + player.getName() + "@red@ and is on a @red@" + killer.getCurrentKillStreak() + "@red@ Wilderness killstreak!");
				} else {
					PlayerUpdating.executeGlobalMessage("<img=12>[@red@Server@bla@]: @red@" + killer.getName() + "@red@ has killed @red@" + player.getName() + "@red@ and is on a @red@" + killer.getCurrentKillStreak() + "@red@ killstreak! Wildy Streak: @red@" + killer.getWildernessKillStreak());
				}
			}

			if (killer.getCurrentKillStreak() > killer.getHighestKillStreak()) {
				killer.write(new SendMessagePacket("Congratulations, your highest kill streak has increased!"));
				killer.setHighestKillStreak(killer.getCurrentKillStreak());
			}

			if (wealth > Constants.PK_POINTS_WEALTH) {
				switch (killer.getRights().getValue()) {
				case 3:
					memberBonus += Utility.isWeekend() ? 4 : 8;
					break;
				case 4:
					memberBonus += Utility.isWeekend() ? 5 : 10;
					break;
				case 5:
					memberBonus += Utility.isWeekend() ? 6 : 12;
					break;
				case 6:
					memberBonus += Utility.isWeekend() ? 7 : 14;
					break;

				}
				if (player.getGameMode() == "TRAINED") {
					trainedBonus = Utility.random(1, 5);
				}
				int reward = (int) (blood_Money + memberBonus + trainedBonus);
				killer.getItems().addItemUnderAnyCircumstance(13307, reward);
				// killer.setPkPoints(killer.getPkPoints() + totalPoints);
				// killer.write(new SendMessagePacket("You now have @blu@" +
				// killer.getPkPoints() + " @red@PK Points@bla@. (@blu@+" +
				// totalPoints + "@bla@)"));
			} else {
				killer.write(new SendMessagePacket(player.getName() + " wasn't risking enough for you to gain any rewards."));
			}

			if (player.getCurrentKillStreak() >= 5) {
				PlayerUpdating.executeGlobalMessage("<img=12>[@red@Server@bla@]: @red@" + killer.getName() + "@red@ just " + killMessage[new java.util.Random().nextInt(killStreakMessage.length)] + " @red@" + player.getName() + "'s@red@ " + player.getCurrentKillStreak() + " killstreak!");
			}

			PKHandler.addKilledEntry(player.getIdentity(), killer);
			Achievements.increase(killer, AchievementType.KILL_PLAYER, 1);
			killer.setKillCount(killer.getKillCount() + 1);
			player.setDeathCount(player.getDeathCount() + 1);
			player.setCurrentKillStreak(0);
			killer.setSpecialAmount(100);
			killer.getWeaponInterface().sendSpecialBar(killer.playerEquipment[killer.getEquipment().getWeaponId()]);
			killer.getWeaponInterface().refreshSpecialAttack();
			sendKillMessage(killer, player);
			player.write(new SendMessagePacket(message_to_player[new java.util.Random().nextInt(message_to_player.length)]));
		}
	}

	/**
	 * Check's the players equipment and sents a random message
	 */
	public final void sendKillMessage(Player killer, Player player) {
		int randomMessage = 2;
		Utility.getRandom(randomMessage);
		if (randomMessage == 1) {
			killer.write(new SendMessagePacket(killer.getName() + "" + killMessage[new java.util.Random().nextInt(killMessage.length)] + "" + player.getName()));
		} else {
			killer.write(new SendMessagePacket(player.getName() + " " + message[new java.util.Random().nextInt(message.length)]));
		}
	}

	private final String[] message = { "Regrets the day they met you in battle.", "Was clearly no match for you.", "Was no match for you.", "falls before your might." };

	private final String[] killMessage = { "You were clearly a better fighter than.", "With a crushing blow you finish.", "With an almighty strike you finish off." };

	private final String[] message_to_player = { "You just got smashed.", "Wow you got obliterated." };

	private final String[] killStreakMessage = { "wrecked", "destroyed", "ended", "cleared", "ruined" };

}