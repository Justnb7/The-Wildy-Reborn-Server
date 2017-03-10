package com.model.game.character.combat;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.combat_data.CombatAnimation;
import com.model.game.character.combat.combat_data.CombatData;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.effect.impl.Venom;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.nvp.NPCCombatData;
import com.model.game.character.combat.pvm.PlayerVsNpcCombat;
import com.model.game.character.combat.pvp.PlayerVsPlayerCombat;
import com.model.game.character.combat.weaponSpecial.Special;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.PlayerAssistant;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.music.sounds.PlayerSounds;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class Combat {
    
    /**
     * The names of all the bonuses in their exact identified slots.
     */
    public static final String[] BONUS_NAMES = { "Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range",
            "Strength", "Prayer", "", "" };
	
	public static void resetCombat(Player player) {
		player.usingMagic = false;
		player.faceEntity(null);
		player.getCombat().reset();
		player.getPA().resetFollow();
		player.setInCombat(false);
		player.getActionSender().sendString("", 35000);
	}

	public static void playerVsEntity(Player player) {
		if (player.getCombat().noTarget())
			return;

		Entity target = player.getCombat().target;
		Combat.setCombatStyle(player);

		if (target.isPlayer()) {
			Player ptarg = (Player)target;
			if (!PlayerVsPlayerCombat.validateAttack(player, ptarg)) { // TODO split this?
				return;
			}
			if (ptarg.inTutorial())
				return;
		} else {
			Npc npc = (Npc)target;
			// Clip check first. Get line of sight.
			if (!PlayerVsNpcCombat.canTouch(player, npc, true)) {
				return;
			}

			if (npc.isDead || npc.maximumHealth <= 0 || player.isDead()) {
				player.usingMagic = false;
				player.faceEntity(null);
				player.getCombat().reset();
				return;
			}
			// Can attack check
			if (!PlayerVsNpcCombat.canAttackNpc(player, npc)) {
				return;
			}
		}

		player.setFollowing(target);


		if (target.isPlayer()) {
			Player ptarg = (Player) target;
			player.getActionSender().sendString(ptarg.getName()+ "-"+player.getSkills().getLevelForExperience(Skills.HITPOINTS)+"-"+ptarg.getSkills().getLevel(Skills.HITPOINTS) + "-" + player.getName() , 35000);
		} else {
			Npc npc = (Npc) target;
			if (npc.npcId != 493 || npc.npcId != 496 || npc.npcId != 5534) {
				Player attacker = World.getWorld().PLAYERS.get(npc.underAttackBy);
				//System.out.println(Npc.getName(npc.npcType).replaceAll("_", " ") + " - "+ npc.maximumHealth +" - "+ npc.HP +" - "+ ((attacker != null) ? "-"+attacker.getUsername() : "null"));
				player.getActionSender().sendString(Npc.getName(npc.npcId).replaceAll("_", " ") + "-" + npc.maximumHealth + "-" + npc.currentHealth + ((attacker != null) ? "-" + attacker.getName() : ""), 35000);
			}
		}

		boolean sameSpot = player.getX() == target.getX() && player.getY() == target.getY();
		if (sameSpot) {
			if (player.frozen()) {
				Combat.resetCombat(player);
				return;
			}
			if (target.isPlayer())
				player.setFollowing(target);
			else
				player.getPA().walkTo(0, 1); // TODO following Npcs properly
			player.attackDelay = 0;
			return;
		}
		if (target.isNPC()) {
			PlayerVsNpcCombat.moveOutFromUnderLargeNpc(player, (Npc)target);
		}

		if (target.isPlayer()) {
			if (!player.getController().canAttackPlayer(player, (Player)target) && Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL) == null) {
				return;
			}
		}
		if (target.isNPC() && !PlayerVsNpcCombat.inDistance(player, (Npc)target)) {
			return;
		}

		if (player.attackDelay > 0) {
			// don't attack as our timer hasnt reached 0 yet
			return;
		}
		// ##### BEGIN ATTACK - WE'RE IN VALID DISTANCE AT THIS POINT #######
		/*
		 * Set our attack timer so we dont instantly hit again
		 */
		player.attackDelay = CombatData.getAttackDelay(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase());

		/*
		 * Verify if we have the proper arrows/bolts
		 */
		if (player.getCombatType() == CombatType.RANGED) {
			if (!player.usingCross
					&& !player.throwingAxe
					&& !player.usingArrows
					&& (player.playerEquipment[player.getEquipment().getWeaponId()] < 4212 || player.playerEquipment[player.getEquipment().getWeaponId()] > 4223)
					&& player.playerEquipment[player.getEquipment().getWeaponId()] != 12926) {
				player.write(new SendMessagePacket("There is no ammo left in your quiver."));
				player.stopMovement();
				player.getCombat().reset();
				return;
			}

			if (player.getCombat().correctBowAndArrows() < player.playerEquipment[player.getEquipment().getQuiverId()]
					&& player.usingBow
					&& !player.getEquipment().usingCrystalBow(player)
					&& !player.getEquipment().isCrossbow(player) && !player.getEquipment().wearingBlowpipe(player)) {
				player.write(new SendMessagePacket("You can't use " + player.getItems().getItemName(player.playerEquipment[player.getEquipment().getQuiverId()]).toLowerCase() + "s with a " + player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase() + "."));
				player.stopMovement();
				player.getCombat().reset();
				return;
			}
			if (player.getEquipment().isCrossbow(player) && !player.getCombat().properBolts()) {
				player.write(new SendMessagePacket("You must use bolts with a crossbow."));
				player.stopMovement();
				Combat.resetCombat(player);
				return;
			}

			if(player.getEquipment().wearingBallista(player) && !player.getCombat().properJavalins()) {
				player.write(new SendMessagePacket("You must use javalins with a ballista."));
				player.stopMovement();
				Combat.resetCombat(player);
				return;
			}

			if (player.playerEquipment[player.getEquipment().getWeaponId()] == 4734 && !player.getCombat().properBoltRacks()) {
				player.write(new SendMessagePacket("You must use bolt racks with this bow."));
				player.stopMovement();
				Combat.resetCombat(player);
				return;
			}
		}
		/*
		 * Verify we can use the spell
		 */
		if (player.getCombatType() == CombatType.MAGIC) {
			if (!player.getCombat().checkMagicReqs(player.getSpellId())) {
				player.stopMovement();
				Combat.resetCombat(player);
				return;
			}
		}

		/*
		 * Since we can attack, lets verify if we're close enough to attack
		 */
		if (target.isPlayer() && !CombatData.isWithinAttackDistance(player, (Player)target)) {
			return;
		}

		if (target.isPlayer()) {
			Player ptarg = (Player)target;
			if (!player.getMovementHandler().isMoving() && !ptarg.getMovementHandler().isMoving()) {
				if (player.getX() != ptarg.getX() && ptarg.getY() != player.getY()
						&& player.getCombatType() == CombatType.MELEE) {
					PlayerAssistant.stopDiagonal(player, ptarg.getX(), ptarg.getY());
					return;
				}
			}
		}

		if (target.isNPC()) {
			Npc npc = (Npc)target;
			if (npc.getSize() == 1) {
				if (player.getX() != npc.getX() && npc.getY() != player.getY()
						&& player.getCombatType() == CombatType.MELEE) {
					PlayerAssistant.stopDiagonal(player, npc.getX(), npc.getY());
					return;
				}
			}
			/*
			 * Check if we are close enough to stop running towards the npc
			 */
			if (player.usingBow
					|| player.castingMagic
					|| player.throwingAxe
					|| (CombatData.usingHalberd(player) && player.goodDistance(player.getX(), player.getY(), npc.getX(),
					npc.getY(), 2))) {
				player.stopMovement();
			}
			npc.underAttackBy = player.getIndex();
			npc.lastDamageTaken = System.currentTimeMillis();
		}


		if (player.getCombatType() == CombatType.MAGIC || player.getCombatType() == CombatType.RANGED) {
			player.stopMovement();
		}
		//player.getCombat().checkVenomousItems();
		player.faceEntity(target);
		player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];

		/*
		 * Add a skull if needed
		 */
		if (target.isPlayer()) {
			Player ptarg = (Player) target;
			if (player != null && player.attackedPlayers != null && ptarg.attackedPlayers != null && !ptarg.getArea().inDuelArena()) {
				if (!player.attackedPlayers.contains(target.getIndex()) && !ptarg.attackedPlayers.contains(player.getIndex())) {
					player.attackedPlayers.add(target.getIndex());
					player.isSkulled = true;
					player.skullTimer = 500;
					player.skullIcon = 0;
					player.getPA().requestUpdates();
				}
			}
			if(ptarg.infection != 2 && player.getEquipment().canInfect(player)) {
				int inflictVenom = Utility.getRandom(5);
				//System.out.println("Venom roll: "+inflictVenom);
				if(inflictVenom == 0 && ptarg.isSusceptibleToVenom()) {
					new Venom(ptarg);
				}
			}
		} else if (target.isNPC()) {
			Npc npc = (Npc)target;
			if(!npc.infected && player.getEquipment().canInfect(player) && !Venom.venomImmune(npc)){
				if(Utility.getRandom(10) == 5){
					new Venom(npc);
				}
			}
		}

		int wep = player.playerEquipment[player.getEquipment().getWeaponId()];
		if (wep > -1) {
			PlayerSounds.SendSoundPacketForId(player, player.isUsingSpecial(), wep);
		}

		/*
		 * Check if we are using a special attack
		 */
		if (player.isUsingSpecial() && player.getCombatType() != CombatType.MAGIC) {
			Special.handleSpecialAttack(player, target);
			player.setFollowing(player.getCombat().target);
			return;
		}

		// ####### WASNT A SPECIAL ATTACK -- DO NORMAL COMBAT STYLES HERE #####

		/*
		 * Start the attack animation
		 */
		if (!player.usingMagic && player.playerEquipment[player.getEquipment().getWeaponId()] != 22494 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2415 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2416 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2417) {
			player.playAnimation(Animation.create(CombatAnimation.getAttackAnimation(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase())));
			player.mageFollow = false;

			// Npc block anim
			if (target.isNPC()) {
				Npc npc = (Npc) target;
				if (npc.maximumHealth > 0 && npc.attackTimer > 3) {
					if (npc.npcId != 2042 && npc.npcId != 2043 & npc.npcId != 2044 && npc.npcId != 3127) {
						npc.playAnimation(Animation.create(NPCCombatData.getNPCBlockAnimation(npc)));
					}
				}
			}
		} else {
			// Magic attack anim
			player.playAnimation(Animation.create(player.MAGIC_SPELLS[player.getSpellId()][2]));
			player.mageFollow = true;
			player.setFollowing(player.getCombat().target);
			if (!player.autoCast) {
				player.stopMovement();
				player.followId = 0;
				player.followId2 = 0;
			}
		}

		/*
		 * Set the target in combat since we just attacked him/her
		 */
		if (target.isPlayer()) {
			((Player)target).putInCombat(player.getIndex());
			((Player)target).killerId = player.getIndex();
		}
		player.updateLastCombatAction();
		player.setInCombat(true);
		/*if (player.petBonus) {
			player.getCombat().handlePetHit(World.getWorld().getPlayers().get(player.playerIndex));
		}*/
		player.rangeItemUsed = 0;

		/*
		 * Set the delay before the damage is applied
		 */
		int hitDelay = CombatData.getHitDelay(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase());


		/*
		 * Set our combat values based on the combat style
		 */

		if (player.getCombatType() == CombatType.MELEE) {

			player.setFollowing(player.getCombat().target);
			player.getPA().followPlayer(true);

			// First, calc hits.
			int dam1 = Utility.getRandom(player.getCombat().calculateMeleeMaxHit());

			// Second, calc accuracy. If miss, dam=0
			if (!(CombatFormulae.getAccuracy(player, target, 1, 1.0))) {
				dam1 = 0;
			}

			Combat.hitEvent(player, target, hitDelay, new Hit(dam1), CombatType.MELEE);

		} else if (player.getCombatType() == CombatType.RANGED && !player.throwingAxe) {
			player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
			player.getItems().deleteArrow();

			if (player.getAttackStyle() == 2)
				player.attackDelay--;

			if (player.usingCross)
				player.usingBow = true;

			player.usingBow = true;

			player.setFollowing(player.getCombat().target);
			player.getPA().followPlayer(true);


			player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
			player.getCombat().fireProjectileAtTarget();

			if (player.playerEquipment[3] == 11235 || player.playerEquipment[3] == 12765 || player.playerEquipment[3] == 12766
					|| player.playerEquipment[3] == 12767 || player.playerEquipment[3] == 12768) {
				player.getItems().deleteArrow();
			}

			// TODO calculate damage and accuracy
			Combat.hitEvent(player, target, hitDelay, null, CombatType.RANGED);

		} else if (player.getCombatType() == CombatType.RANGED && player.throwingAxe) {

			player.rangeItemUsed = player.playerEquipment[player.getEquipment().getWeaponId()];

			if (player.playerEquipment[3] == 21000) {
				player.getItems().removeEquipment();
			} else {
				player.getItems().deleteEquipment(); // here
			}

			player.setFollowing(player.getCombat().target);
			player.getPA().followPlayer(true);

			player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));

			if (player.getAttackStyle() == 2)
				player.attackDelay--;

			player.getCombat().fireProjectileAtTarget();

			// TODO calculate damage and accuracy
			Combat.hitEvent(player, target, hitDelay, null, CombatType.RANGED);

		} else if (player.getCombatType() == CombatType.MAGIC) {
			int pX = player.getX();
			int pY = player.getY();
			int nX = target.getX();
			int nY = target.getY();
			int offX = (pY - nY) * -1;
			int offY = (pX - nX) * -1;
			player.castingMagic = true;

			if (player.MAGIC_SPELLS[player.getSpellId()][3] > 0) {
				if (player.getCombat().getStartGfxHeight() == 100) {
					player.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.getSpellId()][3], 0, 0));
				} else {
					player.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.getSpellId()][3], 0, 0));
				}
			}

			int targetIndex = -player.getCombat().target.getIndex() - 1;

			if (player.MAGIC_SPELLS[player.getSpellId()][4] > 0) {
				player.getProjectile().createPlayersProjectile(pX, pY, offX, offY, 50, 78,
						player.MAGIC_SPELLS[player.getSpellId()][4], player.getCombat().getStartHeight(),
						player.getCombat().getEndHeight(), targetIndex, player.getCombat().getStartDelay());
			}
			if (player.autocastId > 0) {
				player.setFollowing(player.getCombat().target);
				player.followDistance = 5;
			}

			if (player.playerEquipment[player.getEquipment().getWeaponId()] == 11907 || player.playerEquipment[player.getEquipment().getWeaponId()] == 12899) {
				return;
			}

			player.oldSpellId = player.getSpellId();
			player.setSpellId(0);

			if (target.isPlayer()) {
				Player ptarg = (Player)target;
				if (player.MAGIC_SPELLS[player.oldSpellId][0] == 12891 && ptarg.getMovementHandler().isMoving()) {
					player.getProjectile().createPlayersProjectile(pX, pY, offX, offY, 50, 85, 368, 25, 25, targetIndex,
							player.getCombat().getStartDelay());
				}
			}

			player.magicFailed = !CombatFormulae.getAccuracy(player, target, 2, 1.0);

			int spellFreezeTime = player.getCombat().getFreezeTime();
			if (spellFreezeTime > 0 && !target.frozen() && !player.magicFailed) {

				target.freeze(spellFreezeTime);
				if (target.isPlayer()) {
					((Player)target).getMovementHandler().resetWalkingQueue();
					((Player)target).write(new SendMessagePacket("You have been frozen."));
					((Player)target).frozenBy = player.getIndex();
				}
			}
			if (!player.autoCast && player.spellId <= 0)
				player.getCombat().reset();

			Combat.hitEvent(player, target, hitDelay, null, CombatType.MAGIC);
		}
	}


	public static void setCombatStyle(Player player) {

		/*
		 * Check if we are using magic
		 */
		if (player.autoCast && (player.getSpellBook() == SpellBook.MODERN || player.getSpellBook() == SpellBook.ANCIENT)) {
			player.spellId = player.autocastId;
			player.usingMagic = true;
			player.setCombatType(CombatType.MAGIC);
		}

		if (player.playerEquipment[player.getEquipment().getWeaponId()] == 11907) {
			player.spellId = 52;
			player.castingMagic = true;
		}

		if (player.playerEquipment[player.getEquipment().getWeaponId()] == 12899) {
			player.spellId = 53;
			player.castingMagic = true;
		}

		if (player.getSpellId() > 0) {
			player.usingMagic = true;
			player.setCombatType(CombatType.MAGIC);
		}
		if (player.usingMagic) {
			player.setCombatType(CombatType.MAGIC);
		}

		/*
		 * Check if we are using ranged
		 */
		if (player.getCombatType() != CombatType.MAGIC) {
			player.usingBow = player.getEquipment().isBow(player);
			player.throwingAxe = player.getEquipment().isThrowingWeapon(player);
			player.usingCross = player.getEquipment().isCrossbow(player);
			player.usingArrows = player.getEquipment().isArrow(player);
			boolean bolt = player.getEquipment().isBolt(player);
			boolean javalin = player.getCombat().properJavalins();

			if(player.throwingAxe || player.usingCross || player.usingBow || player.getEquipment().wearingBallista(player) || player.getEquipment().wearingBlowpipe(player)) {
				player.setCombatType(CombatType.RANGED);
			}

			if(player.throwingAxe) {
				player.throwingAxe = true;
			}

			if(bolt || javalin || player.usingArrows) {
				player.usingArrows = true;
			}
		}
	}

	public static void hitEvent(Player player, Entity target, int delay, Hit hit, CombatType combatType) {
		Server.getTaskScheduler().schedule(new ScheduledTask(delay) {
			public void execute() {
				if (hit == null) { // Method below will calculate the hit itself after the delay.
					if (target.isPlayer()) {
						Player defender = (Player) target;
						switch (combatType) {
							case MAGIC:
								PlayerVsPlayerCombat.applyPlayerMagicDamage(player, defender);
								break;
							case RANGED:
								PlayerVsPlayerCombat.applyPlayerRangeDamage(player, defender);
								break;
							default:
								throw new IllegalArgumentException("Invalid Combat Type: " + combatType);
						}
					} else {
						Npc npc = (Npc)target;
						switch (combatType) {
							case MAGIC:
								PlayerVsNpcCombat.applyNpcMagicDamage(player, npc);
								break;
							case RANGED:
								PlayerVsNpcCombat.applyNpcRangeDamage(player, npc);
								break;
							default:
								throw new IllegalArgumentException("Invalid Combat Type: " + combatType);
						}
					}
				} else {
					// melee calculated when you swing.
					if (target.isPlayer()) {
						PlayerVsPlayerCombat.applyPlayerMeleeDamage(player, (Player)target, hit.getDamage());
					} else {
						PlayerVsNpcCombat.applyNpcMeleeDamage(player, (Npc)target, hit.getDamage());
					}
				}
				// TODO hit code which i put in notepad
				this.stop();
			}
		});
	}
}