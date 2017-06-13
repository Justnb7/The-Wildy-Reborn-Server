package com.model.game.character.combat;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.CombatData;
import com.model.game.character.combat.combat_data.CombatRequirements;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.effect.SkullType;
import com.model.game.character.combat.magic.MagicCalculations;
import com.model.game.character.combat.magic.MagicData;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.pvm.PlayerVsNpcCombat;
import com.model.game.character.combat.pvp.PlayerVsPlayerCombat;
import com.model.game.character.combat.range.ArrowRequirements;
import com.model.game.character.combat.range.RangeData;
import com.model.game.character.combat.weaponSpecial.Special;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.music.sounds.PlayerSounds;
import com.model.game.definitions.ItemDefinition;
import com.model.game.definitions.WeaponDefinition;
import com.model.game.item.Item;
import com.model.game.item.container.impl.equipment.EquipmentConstants;
import com.model.game.location.Location;
import com.model.server.Server;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;
import hyperion.PathFinder;
import hyperion.impl.VariablePathFinder;

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
            player.spellId = -1;
			player.setCombatType(null);
			player.faceEntity(null);
	        player.getCombatState().reset();
	        player.setFollowing(null);
			player.getActionSender().sendString("", 35000);
		}
    }

    public static void playerVsEntity(Player player) {
        if (player.getCombatState().noTarget())
            return;

        // Establish what style we'd be using this cycle
        Combat.setCombatStyle(player);
        Entity target = player.getCombatState().getTarget();
        player.debug("style: "+player.getCombatType()+" vs "+target);


        /*if (target.isPlayer()) {
            Player ptarg = (Player) target;
            player.getActionSender().sendString(ptarg.getName() + "-" + player.getSkills().getLevelForExperience(Skills.HITPOINTS) + "-" + ptarg.getSkills().getLevel(Skills.HITPOINTS) + "-" + player.getName(), 35000);
        } else {
            NPC npc = (NPC) target;
            if (npc.getId() != 493 || npc.getId() != 496 || npc.getId() != 5534) {
                Player attacker = World.getWorld().PLAYERS.get(npc.underAttackBy);
                //System.out.println(Npc.getName(npc.npcType).replaceAll("_", " ") + " - "+ npc.maximumHealth +" - "+ npc.HP +" - "+ ((attacker != null) ? "-"+attacker.getUsername() : "null"));
                player.getActionSender().sendString(NPC.getName(npc.getId()).replaceAll("_", " ") + "-" + npc.getMaxHitpoints() + "-" + npc.getHitpoints() + ((attacker != null) ? "-" + attacker.getName() : ""), 35000);
            }
        }*/

        if (player.getCombatType() == CombatStyle.RANGE) {
            rangeAttack(player, target);
        } else if (player.getCombatType() == CombatStyle.MAGIC) {
            magicAttack(player, target);
        } else {
            meleeAttack(player, target);
        }
    }

    private static void meleeAttack(Player player, Entity target) {
        if (!touches(player, target))
            return;
        if (!attackable(player, target))
            return;

        if (usingHalberd(player) && player.goodDistance(player.getX(), player.getY(), target.getX(), target.getY(), 2) && !target.moving()) {
            player.getMovementHandler().reset();
        }
        if (player.getCombatState().getAttackDelay() > 0) {
            // don't attack as our timer hasnt reached 0 yet
            return;
        }
        if (player.isUsingSpecial()) {
            Special.handleSpecialAttack(player, target);
            onAttackDone(player, target);
            return;
        }
        onAttackDone(player, target);
        doAttackAnim(player, target);

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
    }

    private static void magicAttack(Player player, Entity target) {
        if (!touches(player, target))
            return;
        if (!attackable(player, target))
            return;
        /*if (!player.getCombatState().checkMagicReqs(player.getSpellId())) {
            player.getMovementHandler().reset();
            Combat.resetCombat(player);
            return;
        }*/
        if (player.getSpellBook() != SpellBook.MODERN && (player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == 2415 || player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == 2416 || player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == 2417)) {
            player.getActionSender().sendMessage("You must be on the modern spellbook to cast this spell.");
            return;
        }
        if (player.touchDistance(target, 7))
            player.getMovementHandler().reset();

        if (player.getCombatState().getAttackDelay() > 0) {
            // don't attack as our timer hasnt reached 0 yet
            return;
        }
        int spell = player.spellId;
        // TODO check all magic pre requisits here.. rune requirements and rune deleting!
        // Magic attack anim
        player.playAnimation(Animation.create(player.MAGIC_SPELLS[spell][2]));

        if (!player.autoCast) {
            // One time attack
            player.getCombatState().setTarget(null);
        }
        int wepId = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
        int hitDelay = CombatData.getHitDelay(player, ItemDefinition.get(wepId).getName().toLowerCase());
        player.setCombatType(CombatStyle.MAGIC);

        if (player.MAGIC_SPELLS[spell][3] > 0) {
            if (player.getCombatState().getStartGfxHeight() == 100) {
                player.playGraphics(Graphic.create(player.MAGIC_SPELLS[spell][3], 0, 0));
            } else {
                player.playGraphics(Graphic.create(player.MAGIC_SPELLS[spell][3], 0, 0));
            }
        }

        if (player.MAGIC_SPELLS[spell][4] > 0) {
            player.playProjectile(Projectile.create(player.getCentreLocation(), target.getCentreLocation(), player.MAGIC_SPELLS[spell][4], player.getCombatState().getStartDelay(), 50, 78, player.getCombatState().getStartHeight(), player.getCombatState().getEndHeight(), target.getProjectileLockonIndex(), 16, 64));
        }

        if (wepId == 11907 || wepId == 12899) {
            return;
        }

        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            if (player.MAGIC_SPELLS[spell][0] == 12891 && ptarg.getMovementHandler().isMoving()) {
                player.playProjectile(Projectile.create(player.getCentreLocation(), target.getCentreLocation(), 368, player.getCombatState().getStartDelay(), 50, 85, 25, 25, target.getProjectileLockonIndex(), 16, 64));
            }
        }

        boolean splash = !CombatFormulae.getAccuracy(player, target, 2, 1.0);

        int spellFreezeTime = MagicData.getFreezeTime(player, spell);
        if (spellFreezeTime > 0 && !target.frozen() && !splash) {

            target.freeze(spellFreezeTime);
            if (target.isPlayer()) {
                ((Player) target).getMovementHandler().reset();
                ((Player) target).getActionSender().sendMessage("You have been frozen.");
                ((Player) target).frozenBy = player.getIndex();
            }
        }
        // One time attack!
        if (!player.autoCast) {
            player.getCombatState().reset();
        }

        int dam1 = MagicCalculations.magicMaxHitModifier(player);

        // Graphic that appears when hit appears.
        final int endGfx = player.MAGIC_SPELLS[spell][5];
        final int endH = MagicData.getEndGfxHeight(player);
        Server.getTaskScheduler().schedule(new ScheduledTask(hitDelay) {
            @Override
            public void execute() {
                if (splash)
                    target.playGraphics(Graphic.create(85, 0, 100));
                else
                    target.playGraphics(Graphic.create(endGfx, 0, endH));
                this.stop();
            }
        });

        if (splash) {
            dam1 = 0;
        } else {
            switch (player.MAGIC_SPELLS[spell][0]) {
                case 12445: // teleblock
                    if (target.isPlayer()) {
                        Player defender = (Player) target;
                        if (defender.teleblock.elapsed(defender.teleblockLength)) {
                            defender.teleblock.reset();
                            defender.getActionSender().sendMessage("You have been teleblocked.");
                            defender.putInCombat(1);
                            if (defender.isActivePrayer(PrayerHandler.Prayers.PROTECT_FROM_MAGIC))
                                defender.teleblockLength = 150000;
                            else
                                defender.teleblockLength = 300000;
                        }
                    }
                    break;
                case 12901:
                case 12919: // blood spells
                case 12911:
                case 12929:
                    int heal = dam1 / 4;
                    if (player.getSkills().getLevel(Skills.HITPOINTS) + heal > player.getMaximumHealth()) {
                        player.getSkills().setLevel(Skills.HITPOINTS, player.getMaximumHealth());
                    } else {
                        player.getSkills().setLevel(Skills.HITPOINTS, player.getSkills().getLevel(Skills.HITPOINTS) + heal);
                    }
                    break;
            }
        }

        Combat.hitEvent(player, target, hitDelay, new Hit(dam1), CombatStyle.MAGIC);
        onAttackDone(player, target);
        // MUST BE THE LAST PIECE OF CODE IN THIS METHOD. Spellid is used in other methods as a reference.
        player.spellId = -1;
    }

    private static void rangeAttack(Player player, Entity target) {
        if (!touches(player, target))
            return;
        if (!attackable(player, target))
            return;

        if (player.touchDistance(target, calculateAttackDistance(player, target)))
            player.getMovementHandler().reset();
        int wepId = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
        int ammoId = player.getEquipment().get(EquipmentConstants.AMMO_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId();
        boolean crystal = wepId >= 4212 && wepId <= 4223;
        boolean bp = wepId == 12926;
        if (!crystal && !bp && ammoId == -1) {
            player.getActionSender().sendMessage("There is no ammo left in your quiver.");
            player.getMovementHandler().reset();
            player.getCombatState().reset();
            return;
        }

        if (!ArrowRequirements.canUseArrowWithBow(player))
            return;

        if (player.getCombatState().getAttackDelay() > 0) {
            // don't attack as our timer hasnt reached 0 yet
            return;
        }
        if (!target.moving())
            player.getMovementHandler().reset();
        if (player.isUsingSpecial()) {
            Special.handleSpecialAttack(player, target);
            onAttackDone(player, target);
            return;
        }
        // Normal attack
        doAttackAnim(player, target);
        onAttackDone(player, target);

        int hitDelay = CombatData.getHitDelay(player, ItemDefinition.get(wepId).getName().toLowerCase());

        player.playGraphics(Graphic.create(player.getCombatState().getRangeStartGFX(), 0, 100));
        player.getCombatState().fireProjectileAtTarget();

        RangeData.loseAmmo(player, target, wepId, ammoId);

        // Random dmg
        int dam1 = Utility.getRandom(player.getCombatState().calculateRangeMaxHit());

        // Bolt special increases damage.
        boolean boltSpec = EquipmentConstants.isCrossbow(player) && Utility.getRandom(target.isPlayer() ? 10 : 8) == 1;
        if (boltSpec && dam1 > 0)
            dam1 = Combat.boltSpecialVsEntity(player, target, dam1);

        // Missed?
        if (!player.hasAttribute("ignore defence") && !CombatFormulae.getAccuracy(player, target, 1, 1.0)) {
            dam1 = 0;
        }
        player.removeAttribute("ignore defence");

        // Apply dmg.
        Hit hitInfo = target.take_hit(player, dam1, CombatStyle.RANGE, false, false).giveXP(player);
        Combat.hitEvent(player, target, 1, hitInfo, CombatStyle.RANGE);

        int[] endGfx = RangeData.getRangeEndGFX(player, wepId);
        // Graphic that appears when hit appears.
        Server.getTaskScheduler().schedule(new ScheduledTask(hitDelay) {
            @Override
            public void execute() {
                if (endGfx[0] > -1)
                    target.playGraphics(Graphic.create(endGfx[0], 0, endGfx[1]));
                this.stop();
            }
        });
    }

    private static boolean attackable(Player player, Entity target) {
        // Can we attack the target? Not distance checks (yet) as they come later.
        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            if (!PlayerVsPlayerCombat.validateAttack(player, ptarg)) {
                return false;
            }
        } else {
            NPC npc = (NPC) target;
            // Can attack check
            if (!PlayerVsNpcCombat.canAttackNpc(player, npc)) {
                return false;
            }
        }
        return true;
    }

    private static void doAttackAnim(Player player, Entity target) {
        Item wep = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
        int wepId = wep == null ? -1 : wep.getId();
        final WeaponDefinition def = WeaponDefinition.get(wepId);
        if (wepId <= 0) { // defaults no weapon hands
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
    }

    /**
     * Stuff done when an attack executes, regardless of combat style. SOUNDS, SKULLING, VENOM
     */
    private static void onAttackDone(Player player, Entity target) {
        Item wep = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);

		// Set our attack timer so we dont instantly hit again
        player.getCombatState().setAttackDelay(WeaponDefinition.sendAttackSpeed(player));

        // Rapid reduce delay by 1 faster
        if (player.getCombatType() == CombatStyle.RANGE) {
            if (player.getAttackStyle() == 2)
                player.getCombatState().setAttackDelay(player.getCombatState().getAttackDelay() - 1);
        }

        // Add a skull if needed
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

        if (wep != null && player.getCombatType() != CombatStyle.MAGIC) {
            PlayerSounds.SendSoundPacketForId(player, player.isUsingSpecial(), wep.getId());
        }

        if (target.isNPC()) {
            NPC npc = (NPC) target;
            npc.underAttackBy = player.getIndex();
            npc.lastDamageTaken = System.currentTimeMillis();
        }
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
    }

    private static int boltSpecialVsEntity(Player attacker, Entity defender, int dam1) {
        if (dam1 == 0) return dam1;
        Item ammo = attacker.getEquipment().get(EquipmentConstants.AMMO_SLOT);
        switch (ammo.getId()) {
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
                attacker.setAttribute("ignore defence", true); // always hits
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

        if (player.autoCast && (player.getSpellBook() == SpellBook.MODERN || player.getSpellBook() == SpellBook.ANCIENT)) {
            player.spellId = player.autocastId;
            player.setCombatType(CombatStyle.MAGIC);
        }
        int wep = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null?-1:player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
        if (wep == 11907) {
            player.spellId = 52;
            player.setCombatType(CombatStyle.MAGIC);
        }
        if (wep == 12899) {
            player.spellId = 53;
            player.setCombatType(CombatStyle.MAGIC);
        }
        // Spell id set when packet: magic on player
        if (player.spellId > 0) {
            player.setCombatType(CombatStyle.MAGIC);
        }

		// Check if we are using ranged
        if (player.getCombatType() != CombatStyle.MAGIC) {
            if (EquipmentConstants.usingRange(player)) {
                player.setCombatType(CombatStyle.RANGE);
            }
        }
        // hasn't been set to magic/range.. must be melee.
        if (player.getCombatType() == null) {
            player.setCombatType(CombatStyle.MELEE);
        }
    }

    public static void hitEvent(Entity attacker, Entity target, int delay, Hit hit, CombatStyle combatType) {
        final int blockAnim = target.isPlayer() ? WeaponDefinition.sendBlockAnimation(target.asPlayer()) : target.asNpc().getDefendAnimation();
        Animation a = Animation.create(blockAnim);

        // Schedule a task
        attacker.run(new ScheduledTask(delay) {
            public void execute() {
            	if (attacker.isPlayer() && hit != null)
            		PlayerSounds.sendBlockOrHitSound((Player)attacker, hit.getDamage() > 0);
            	
            	// Apply the damage inside Hit
                if (!(hit.getDamage() == 0 && combatType == CombatStyle.MAGIC)) // dont show 0 for splash
                    target.damage(hit);

                if (attacker.isPlayer()) {
	                // Range attack invoke block emote when hit appears.
	                if (hit.cbType == CombatStyle.RANGE && target.getCombatState().getAttackDelay() < 5) {
                        target.playAnimation(a);
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
		if(player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null)
			return false;
		String weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getName().toLowerCase();
		return weapon.contains("halberd");
	}
	
	/**
	 * Determines the distance required before you can attack the other player
	 * 
	 * @param player
	 *            The {@link Player} who is following/attacking the other player
	 * @param victim
	 *            The {@link Player} we are following/attacking
	 * @return We are within distance to interact with the other player
	 */
	public static int calculateAttackDistance(Player player, Entity victim) {
		int distance = 1;
		if (player.getCombatType() == CombatStyle.RANGE) {
		    if (EquipmentConstants.isThrowingWeapon(player))
			    distance = 4;
		    else if (EquipmentConstants.wearingBlowpipe(player))
		        distance = 4;
		    else
		        distance = 7;
		} else if (usingHalberd(player) && player.getCombatType() == CombatStyle.MELEE) {
			distance = 2;
		} else if (player.getCombatType() == CombatStyle.MAGIC) {
			distance = 10;
		} else if (player.getCombatType() == CombatStyle.MELEE) {
			if (player.getX() != victim.getX() && player.getY() != victim.getY() && player.distanceToPoint(victim.getX(), victim.getY()) < 2) {
				distance = 2;
			} else {
				distance = CombatRequirements.extraMovingTilesDistance(player);
			}
		}
		if (player.getMovementHandler().isMoving()) {
			distance += victim.isPlayer() && ((Player)victim).getMovementHandler().isMoving() ? 3 : 2;
		}
		return distance;
	}

    /**
     * If you're able to move, it'll re-calculate a path to your target if you're not in range.
     */
	public static boolean touches(Player player, Entity target) {

        //player.getPlayerFollowing().follow(true, target); // PI =)

        // HYPERION TIME XX

        int walkToData = 0;
        if (target.isNPC()) {
            walkToData = 0x80000000;//can move through entities
        }
        PathFinder.doPath(new VariablePathFinder(-1, walkToData, 0, target.size(), target.size()), player, target.getX(), target.getY());

        // Above - path was executed, now check for line of sight, and distance

        if (!hyperion.impl.ProjectilePathFinder.hasLineOfSight(player, target)) {
            if (!Location.standingOn(player, target)) {
                return false;
            } else if (Location.standingOn(player, target)) {
                if (player.frozen()) {
                    return false;
                }
            }
        }
        // projectile path clear is PI's line of sight.. apparently? idk was already there
		return /*ProjectilePathFinder.isProjectilePathClear(player.getLocation(), target.getLocation()) && */player.touchDistance(target, calculateAttackDistance(player, target));
	}
	
	/**
	 * Is the attacker wearing an ava's device?
	 * @return
	 */
	public boolean wearingAccumulator(Player player) {
		Item cape = player.getEquipment().get(EquipmentConstants.CAPE_SLOT);
		return (cape.getId() == 10499);
	}
	
	/**
	 * Is the attacker wearing an attractor device?
	 * @return
	 */
	public boolean wearingAttractor(Player player) {
		Item cape = player.getEquipment().get(EquipmentConstants.CAPE_SLOT);
		return (cape.getId() == 10498);
	}
}