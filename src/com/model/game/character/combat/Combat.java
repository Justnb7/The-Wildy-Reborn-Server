package com.model.game.character.combat;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.CombatData;
import com.model.game.character.combat.combat_data.CombatRequirements;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.effect.SkullType;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.pvm.PlayerVsNpcCombat;
import com.model.game.character.combat.pvp.PlayerVsPlayerCombat;
import com.model.game.character.combat.range.ArrowRequirements;
import com.model.game.character.combat.weaponSpecial.Special;
import com.model.game.character.following.PlayerFollowing;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.music.sounds.PlayerSounds;
import com.model.game.definitions.ItemDefinition;
import com.model.game.definitions.WeaponDefinition;
import com.model.game.item.Item;
import com.model.game.item.container.impl.equipment.EquipmentConstants;
import com.model.server.Server;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class Combat {
	
	/**
	 * Skulls the specified player
	 * @param player
	 * @param type
	 * @param seconds
	 */
	public static void skull(Player player, SkullType type, int seconds) {
		player.setSkullType(type);
		player.setSkullTimer(seconds);
		if(type == SkullType.RED_SKULL) {
			player.getActionSender().sendMessage("@bla@You have received a @red@red skull@bla@! You can no longer use the protect item prayer!");
			PrayerHandler.deactivatePrayer(player, Prayers.PROTECT_ITEM);		
		} else if(type == SkullType.SKULL) {
			player.getActionSender().sendMessage("You've been skulled!");
		}
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

    /**
     * The names of all the bonuses in their exact identified slots.
     */
    public static final String[] BONUS_NAMES = {"Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range",
            "Melee Strength", "Ranged strength",/* "Magic damage", "Prayer", "Undead", "Slayer"*/};

    /**
	 * Resets the combat for the entity
	 * 
	 * @param entity
	 */
    public static void resetCombat(Entity entity) {
		entity.setInCombat(false);
		
		if (entity instanceof Player) {
			Player player = (Player) entity;
			player.setSpellId(-1);
			player.faceEntity(null);
	        player.getCombatState().reset();
	        player.setFollowing(null);
			player.getActionSender().sendString("", 35000);
		}
    }

    public static void playerVsEntity(Player player) {
        if (player.getCombatState().noTarget())
            return;

        Entity target = player.getCombatState().getTarget();

        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            if (!PlayerVsPlayerCombat.validateAttack(player, ptarg)) { // TODO split this?
                return;
            }
        } else {
            NPC npc = (NPC) target;
            // Clip check first. Get line of sight.
            if (!PlayerVsNpcCombat.canTouch(player, npc, true)) {
            	return;
            }
            // Can attack check
            if (!PlayerVsNpcCombat.canAttackNpc(player, npc)) {
                return;
            }
        }

        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            player.getActionSender().sendString(ptarg.getName() + "-" + player.getSkills().getLevelForExperience(Skills.HITPOINTS) + "-" + ptarg.getSkills().getLevel(Skills.HITPOINTS) + "-" + player.getName(), 35000);
        } else {
            NPC npc = (NPC) target;
            if (npc.getId() != 493 || npc.getId() != 496 || npc.getId() != 5534) {
                Player attacker = World.getWorld().PLAYERS.get(npc.underAttackBy);
                //System.out.println(Npc.getName(npc.npcType).replaceAll("_", " ") + " - "+ npc.maximumHealth +" - "+ npc.HP +" - "+ ((attacker != null) ? "-"+attacker.getUsername() : "null"));
                player.getActionSender().sendString(NPC.getName(npc.getId()).replaceAll("_", " ") + "-" + npc.getMaxHitpoints() + "-" + npc.getHitpoints() + ((attacker != null) ? "-" + attacker.getName() : ""), 35000);
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
                player.getPlayerFollowing().walkTo(0, 1); // TODO following Npcs properly
            return;
        }
        if (target.isNPC()) {
            PlayerVsNpcCombat.moveOutFromUnderLargeNpc(player, (NPC) target);
        }

        if (target.isPlayer()) {
            if (!player.getController().canAttackPlayer(player, (Player) target) && Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL) == null) {
                return;
            }
        }
        if (target.isNPC() && !PlayerVsNpcCombat.inDistance(player, (NPC) target)) {
            return;
        }


		/*
         * Verify if we have the proper arrows/bolts
		 */
        if (player.getCombatType() == CombatStyle.RANGE) {
        	Item wep = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
            Item ammo = player.getEquipment().get(EquipmentConstants.AMMO_SLOT);
            boolean crystal = wep.getId() >= 4212 && wep.getId() <= 4223;
            boolean blowp = wep.getId() == 12926;
            if (!crystal && !blowp && ammo.getId() < 1) {
                player.getActionSender().sendMessage("There is no ammo left in your quiver.");
                player.getMovementHandler().stopMovement();
                player.getCombatState().reset();
                return;
            }

            ArrowRequirements.canUseArrowWithBow(player);

        }
		/*
		 * Verify we can use the spell
		 */
        if (player.getCombatType() == CombatStyle.MAGIC) {
            /*if (!player.getCombatState().checkMagicReqs(player.getSpellId())) {
                player.getMovementHandler().stopMovement();
                Combat.resetCombat(player);
                return;
            }*/
            if (player.getSpellBook() != SpellBook.MODERN && (player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == 2415 || player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == 2416 || player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == 2417)) {
                player.getActionSender().sendMessage("You must be on the modern spellbook to cast this spell.");
                return;
            }
        }

		/*
		 * Since we can attack, lets verify if we're close enough to attack
		 */
        if (target.isPlayer() && !isWithinAttackDistance(player, (Player) target)) {
            return;
        }

        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            if (!player.getMovementHandler().isMoving() && !ptarg.getMovementHandler().isMoving()) {
                if (player.getX() != ptarg.getX() && ptarg.getY() != player.getY()
                        && player.getCombatType() == CombatStyle.MELEE) {
                    PlayerFollowing.stopDiagonal(player, ptarg.getX(), ptarg.getY());
                    return;
                }
            }
        }

        if (target.isNPC()) {
            NPC npc = (NPC) target;
            if (npc.getSize() == 1) {
                if (player.getX() != npc.getX() && npc.getY() != player.getY()
                        && player.getCombatType() == CombatStyle.MELEE) {
                    PlayerFollowing.stopDiagonal(player, npc.getX(), npc.getY());
                    return;
                }
            }
            npc.underAttackBy = player.getIndex();
            npc.lastDamageTaken = System.currentTimeMillis();
        }


        if (player.getCombatType() == CombatStyle.MAGIC || player.getCombatType() == CombatStyle.RANGE ||
                (usingHalberd(player) && player.goodDistance(player.getX(), player.getY(), target.getX(), target.getY(), 2))) {
            player.getMovementHandler().stopMovement();
        }

        //player.getCombat().checkVenomousItems();

        if (player.getCombatState().getAttackDelay() > 0) {
            // don't attack as our timer hasnt reached 0 yet
            return;
        }

        // ##### BEGIN ATTACK - WE'RE IN VALID DISTANCE AT THIS POINT #######
        int wep = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
		/*
		 * Set our attack timer so we dont instantly hit again
		 */
        player.getCombatState().setAttackDelay(WeaponDefinition.sendAttackSpeed(player));

		/*
		 * Add a skull if needed
		 */
        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            if (player != null && player.attackedPlayers != null && ptarg.attackedPlayers != null && !ptarg.getArea().inDuelArena()) {
                if (!player.attackedPlayers.contains(target.getIndex()) && !ptarg.attackedPlayers.contains(player.getIndex())) {
                    player.attackedPlayers.add(target.getIndex());
                    skull(player, SkullType.SKULL, 300);
                }
            }
            /*if (ptarg.infection != 2 && player.getEquipment().canInfect(player)) {
                int inflictVenom = Utility.getRandom(5);
                //System.out.println("Venom roll: "+inflictVenom);
                if (inflictVenom == 0 && ptarg.isSusceptibleToVenom()) {
                    new Venom(ptarg);
                }
            }*/
        } else if (target.isNPC()) {
            NPC npc = (NPC) target;
            /*if (!npc.infected && player.getEquipment().canInfect(player) && !Venom.venomImmune(npc)) {
                if (Utility.getRandom(10) == 5) {
                    new Venom(npc);
                }
            }*/
        }

        if (wep > -1 && player.getCombatType() != CombatStyle.MAGIC) {
            PlayerSounds.SendSoundPacketForId(player, player.isUsingSpecial(), wep);
        }

		/*
		 * Check if we are using a special attack
		 */
        if (player.isUsingSpecial() && player.getCombatType() != CombatStyle.MAGIC) {
            Special.handleSpecialAttack(player, target);
            return;
        }

        // ####### WASNT A SPECIAL ATTACK -- DO NORMAL COMBAT STYLES HERE #####

		/*
		 * Start the attack animation
		 */
        if (player.getCombatType() != CombatStyle.MAGIC && wep != 22494 && wep != 2415 && wep != 2416 && wep != 2417) {
        	final WeaponDefinition def = WeaponDefinition.get(wep);
        	if(wep <= 0) {
        		switch(player.getAttackStyle()) {
        		case 0:
        			player.playAnimation(Animation.create(422));
        			break;
        		case 1:
        			player.playAnimation(Animation.create(423));
        			break;
        		case 2:
        			player.playAnimation(Animation.create(422));
        			break;
        		}
        	} else {
        		if (def != null) {
        			player.playAnimation(Animation.create(def.getAnimations()[player.getAttackStyle()]));
        		}
        	}

            // Npc block anim
            if (target.isNPC()) {
                NPC npc = (NPC) target;
                if (npc.getMaxHitpoints() > 0 && npc.getCombatState().getAttackDelay() > 3) {
                    if (npc.getId() != 2042 && npc.getId() != 2043 && npc.getId() != 2044 && npc.getId() != 3127 || npc.getId() != 1739 || npc.getId() != 1740 || npc.getId() != 1741 || npc.getId() != 1742) {
                        npc.playAnimation(Animation.create(npc.getDefendAnimation()));
                    }
                }
            }
        } else {
            // Magic attack anim
            
        }

		/*
		 * Set the target in combat since we just attacked him/her
		 */
        if (target.isPlayer()) {
            ((Player) target).putInCombat(player.getIndex());
            target.getActionSender().removeAllInterfaces();
        }
        player.updateLastCombatAction();
        player.setInCombat(true);
        target.lastAttacker = player;
        target.lastWasHitTime = System.currentTimeMillis();
		/*if (player.petBonus) {
			player.getCombat().handlePetHit(World.getWorld().getPlayers().get(player.playerIndex));
		}*/

		/*
		 * Set the delay before the damage is applied
		 */
        int hitDelay = CombatData.getHitDelay(player, ItemDefinition.get(wep).getName().toLowerCase());


		/*
		 * Set our combat values based on the combat style
		 */

        if (player.getCombatType() == CombatStyle.MELEE) {


            // First, calc hits.
            int dam1 = Utility.getRandom(player.getCombatState().calculateMeleeMaxHit());

            // Second, calc accuracy. If miss, dam=0
            if (!(CombatFormulae.getAccuracy(player, target, 0, 1.0))) {
                dam1 = 0;
            }
            
            //setup the Hit
            Hit hitInfo = target.take_hit(player, dam1, CombatStyle.MELEE, false, false).giveXP(player);
            // (2) Here: submit an event that applies the Hit X ticks later
            Combat.hitEvent(player, target, 1, hitInfo, CombatStyle.MELEE);

        } else if (player.getCombatType() == CombatStyle.RANGE) {

			//TODO range combat

        } else if (player.getCombatType() == CombatStyle.MAGIC) {
            //TODO magic combat
        }
    }

    private static int boltSpecialVsEntity(Player attacker, Entity defender, int dam1) {
        if (dam1 == 0) return dam1;
        switch (attacker.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId()) {
            case 9236: // Lucky Lightning
                defender.playGraphics(Graphic.create(749, 0, 0));
                break;
            case 9237: // Earth's Fury
                defender.playGraphics(Graphic.create(755, 0, 0));
                break;
            case 9238: // Sea Curse
                defender.playGraphics(Graphic.create(750, 0, 0));
                break;
            case 9239: // Down to Earth
                defender.playGraphics(Graphic.create(757, 0, 0));
                break;
            case 9240: // Clear Mind
                defender.playGraphics(Graphic.create(751, 0, 0));
                break;
            case 9241: // Magical Posion
                defender.playGraphics(Graphic.create(752, 0, 0));
                break;
            case 9242: // Blood Forfiet
                defender.playGraphics(Graphic.create(754, 0, 0));
                int selfDamage = (int) (attacker.getSkills().getLevel(Skills.HITPOINTS) * 0.1);
                if (selfDamage < attacker.getSkills().getLevel(Skills.HITPOINTS)) {
                    int opHP = defender.isPlayer() ? ((Player) defender).getSkills().getLevel(Skills.HITPOINTS) : ((NPC) defender).getHitpoints();
                    dam1 += opHP * 0.2;
                    attacker.damage(new Hit(selfDamage));
                }
                break;
            case 9243: // Armour Piercing
                defender.playGraphics(Graphic.create(758, 0, 100));
                attacker.setAttribute("ignore defence", true);
                if (CombatFormulae.wearingFullVoid(attacker, 2)) {
                    dam1 = Utility.random(45, 57);
                } else {
                    dam1 = Utility.random(42, 51);
                }
                if (attacker.isActivePrayer(PrayerHandler.Prayers.EAGLE_EYE)) {
                    dam1 *= 1.15;
                }
                break;
            case 9244: // Dragon's Breath
                defender.playGraphics(Graphic.create(756, 0, 0));
                if (CombatFormulae.wearingFullVoid(attacker, 2)) {
                    dam1 = Utility.random(45, 57);
                } else {
                    dam1 = Utility.random(42, 51);
                }
                if (attacker.isActivePrayer(PrayerHandler.Prayers.EAGLE_EYE)) {
                    dam1 *= 1.15;
                }
                boolean fire = true;
                int shield = defender.isPlayer() ? ((Player) defender).getEquipment().get(EquipmentConstants.SHIELD_SLOT).getId() : -1;
                if (shield == 11283 || shield == 1540) {
                    fire = false;
                }
                if (fire) {
                    if (CombatFormulae.wearingFullVoid(attacker, 2)) {
                        dam1 = Utility.random(45, 57);
                    } else {
                        dam1 = Utility.random(42, 51);
                    }
                    if (attacker.isActivePrayer(PrayerHandler.Prayers.EAGLE_EYE)) {
                        dam1 *= 1.15;
                    }
                    if (defender.isPlayer()) {
                        double protectionPrayer = ((Player) defender).isActivePrayer(PrayerHandler.Prayers.EAGLE_EYE) ? 0.40 : 1;
                        if (protectionPrayer != 1) {
                            double protectionHit = dam1 * protectionPrayer; // +1 as its exclusive
                            dam1 -= protectionHit;
                            if (dam1 < 1)
                                dam1 = 0;
                        }
                    }
                }
                break;
            case 9245: // Life Leech
                defender.playGraphics(Graphic.create(753, 0, 0));
                if (CombatFormulae.wearingFullVoid(attacker, 2)) {
                    dam1 = Utility.random(45, 57);
                } else {
                    dam1 = Utility.random(42, 51);
                }
                if (attacker.isActivePrayer(PrayerHandler.Prayers.EAGLE_EYE)) {
                    dam1 *= 1.15;
                }
                break;
        }

        return dam1;
    }


    public static void setCombatStyle(Player player) {
      
        player.setCombatType(null); // reset

        int followDist = 1;
		/*
		 * Check if we are using magic
		 */
        if (player.autoCast && (player.getSpellBook() == SpellBook.MODERN || player.getSpellBook() == SpellBook.ANCIENT)) {
            player.spellId = player.autocastId;
            player.setCombatType(CombatStyle.MAGIC);
        }
		/*
		 * Check if we are using ranged
		 */
        if (player.getCombatType() != CombatStyle.MAGIC) {
           //TODO add range combat
        }
        // hasn't been set to magic/range.. must be melee.
        if (player.getCombatType() == null) {
            player.setCombatType(CombatStyle.MELEE);
            if (usingHalberd(player))
                followDist = 2;
        }
        player.followDistance = followDist;
        //player.message("style: "+player.getCombatType()+"  dist:"+followDist+"  atkDelay:"+player.attackDelay);

    }

    public static void hitEvent(Entity attacker, Entity target, int delay, Hit hit, CombatStyle combatType) {

        // Schedule a task
        Server.getTaskScheduler().schedule(new ScheduledTask(delay) {
            public void execute() {
            	if (attacker.isPlayer() && hit != null)
            		PlayerSounds.sendBlockOrHitSound((Player)attacker, hit.getDamage() > 0);
            	
            	// Apply the damage inside Hit
                target.damage(hit);

                if (attacker.isPlayer()) {
                	Player player = (Player) attacker;

	                // Range attack invoke block emote when hit appears.
	                if (hit.cbType == CombatStyle.RANGE) {
                        player.setAttribute("ignore defence", false);

	                    if (target.isNPC() && ((NPC) target).getCombatState().getAttackDelay() < 5)
	                        target.playAnimation(Animation.create(target.asNpc().getDefendAnimation()));
	                    else if (target.isPlayer() && ((Player)target).getCombatState().getAttackDelay() < 5)
	                        target.playAnimation(Animation.create(WeaponDefinition.sendBlockAnimation(target.asPlayer())));

	                }
	                if (hit.cbType == CombatStyle.MAGIC) {
	                   
	                }
                }
                this.stop();
            }
        });
    }

    public static boolean hitRecently(Entity target, int timeframe) {
        return System.currentTimeMillis() - target.lastWasHitTime <= timeframe;
    }

    public static boolean incombat(Player player) {
        return System.currentTimeMillis() - player.lastWasHitTime < 4000;
    }
    
    /**
	 * Checks wether the player has equiped a halberd.
	 * @param player
	 *            The {@link Player} who has the halberd equiped.
	 * @return We are checking for an equiped halberd.
	 */
	public static boolean usingHalberd(Player player) {
		String weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getName().toLowerCase();
		
		if (weapon.contains("halberd")) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Determines the distance required before you can attack the other player
	 * 
	 * @param player
	 *            The {@link Player} who is following/attacking the other player
	 * @param victim
	 *            The {@link Player} we are following/attacking
	 * @param follow
	 *            Determine if you are following the player
	 * @return We are within distance to interact with the other player
	 */
	public static int calculateAttackDistance(Player player, Player victim, boolean follow) {
		int distance = 1;
		if (usingHalberd(player) && player.getCombatType() == CombatStyle.MELEE) {
			distance = 2;
			//TODO add range distance checks
		} else if (player.getCombatType() == CombatStyle.MAGIC) {
			distance = 10;
		} else if (player.getCombatType() == CombatStyle.MELEE) {
			if (player.getX() != victim.getX() && player.getY() != victim.getY() && player.distanceToPoint(victim.getX(), victim.getY()) < 2) {
				distance = 2;
			} else {
				distance = CombatRequirements.getRequiredDistance(player);
			}
		}
		if (victim.getMovementHandler().isMoving() && !follow) {
			distance += 2;
		}
		return distance;
	}
	
	/**
	 * Checks for attack distance.
	 * @param player
	 * @param victim
	 * @return
	 */
	public static boolean isWithinAttackDistance(Player player, Player victim) {
		return player.goodDistance(player.getX(), player.getY(), victim.getX(), victim.getY(), calculateAttackDistance(player, victim, false));
	}
	
	/**
	 * Checks for follow distance.
	 * @param player
	 * @param victim
	 * @return
	 */
	public static boolean isWithinAttackDistanceForStopFollow(Player player, Player victim) {
		return player.goodDistance(player.getX(), player.getY(), victim.getX(), victim.getY(),
				calculateAttackDistance(player, victim, true));
	}
	
	/**
	 * Is the attacker wearing an ava's device?
	 * @param mob
	 * @return
	 */
	public boolean wearingAccumulator(Player player) {
		return (player.getEquipment().get(EquipmentConstants.CAPE_SLOT).getId() == 10499);
	}
	
	/**
	 * Is the attacker wearing an attractor device?
	 * @param mob
	 * @return
	 */
	public boolean wearingAttractor(Player player) {
		return (player.getEquipment().get(EquipmentConstants.CAPE_SLOT).getId() == 10498);
	}
}