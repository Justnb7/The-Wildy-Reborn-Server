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
import com.model.game.character.combat.magic.MagicCalculations;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.nvp.NPCCombatData;
import com.model.game.character.combat.pvm.PlayerVsNpcCombat;
import com.model.game.character.combat.pvp.PlayerVsPlayerCombat;
import com.model.game.character.combat.range.RangeData;
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
    public static final String[] BONUS_NAMES = {"Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range",
            "Strength", "Prayer", "", ""};

    public static void resetCombat(Player player) {
        player.usingMagic = false;
        player.faceEntity(null);
        player.getCombat().reset();
        player.setFollowing(null);
        player.setInCombat(false);
        player.getActionSender().sendString("", 35000);
    }

    public static void playerVsEntity(Player player) {
        if (player.getCombat().noTarget())
            return;

        Entity target = player.getCombat().target;

        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            if (!PlayerVsPlayerCombat.validateAttack(player, ptarg)) { // TODO split this?
                return;
            }
        } else {
            Npc npc = (Npc) target;
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
            return;
        }
        if (target.isNPC()) {
            PlayerVsNpcCombat.moveOutFromUnderLargeNpc(player, (Npc) target);
        }

        if (target.isPlayer()) {
            if (!player.getController().canAttackPlayer(player, (Player) target) && Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL) == null) {
                return;
            }
        }
        if (target.isNPC() && !PlayerVsNpcCombat.inDistance(player, (Npc) target)) {
            return;
        }


		/*
         * Verify if we have the proper arrows/bolts
		 */
        if (player.getCombatType() == CombatType.RANGED) {
            int wep = player.playerEquipment[player.getEquipment().getWeaponId()];
            int ammo = player.playerEquipment[player.getEquipment().getQuiverId()];
            boolean crystal = wep >= 4212 && wep <= 4223;
            boolean blowp = wep == 12926;
            if (!crystal && !blowp && ammo < 1) {
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

            if (player.getEquipment().wearingBallista(player) && !player.getCombat().properJavalins()) {
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
            if (player.getSpellBook() != SpellBook.MODERN && (player.playerEquipment[player.getEquipment().getWeaponId()] == 2415 || player.playerEquipment[player.getEquipment().getWeaponId()] == 2416 || player.playerEquipment[player.getEquipment().getWeaponId()] == 2417)) {
                player.message("You must be on the modern spellbook to cast this spell.");
                return;
            }
        }

		/*
		 * Since we can attack, lets verify if we're close enough to attack
		 */
        if (target.isPlayer() && !CombatData.isWithinAttackDistance(player, (Player) target)) {
            return;
        }

        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            if (!player.getMovementHandler().isMoving() && !ptarg.getMovementHandler().isMoving()) {
                if (player.getX() != ptarg.getX() && ptarg.getY() != player.getY()
                        && player.getCombatType() == CombatType.MELEE) {
                    PlayerAssistant.stopDiagonal(player, ptarg.getX(), ptarg.getY());
                    return;
                }
            }
        }

        if (target.isNPC()) {
            Npc npc = (Npc) target;
            if (npc.getSize() == 1) {
                if (player.getX() != npc.getX() && npc.getY() != player.getY()
                        && player.getCombatType() == CombatType.MELEE) {
                    PlayerAssistant.stopDiagonal(player, npc.getX(), npc.getY());
                    return;
                }
            }
            npc.underAttackBy = player.getIndex();
            npc.lastDamageTaken = System.currentTimeMillis();
        }


        if (player.getCombatType() == CombatType.MAGIC || player.getCombatType() == CombatType.RANGED ||
                (CombatData.usingHalberd(player) && player.goodDistance(player.getX(), player.getY(), target.getX(), target.getY(), 2))) {
            player.stopMovement();
        }

        //player.getCombat().checkVenomousItems();

        if (player.attackDelay > 0) {
            // don't attack as our timer hasnt reached 0 yet
            return;
        }

        // ##### BEGIN ATTACK - WE'RE IN VALID DISTANCE AT THIS POINT #######
		/*
		 * Set our attack timer so we dont instantly hit again
		 */
        player.attackDelay = CombatData.getAttackDelay(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase());
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
            if (ptarg.infection != 2 && player.getEquipment().canInfect(player)) {
                int inflictVenom = Utility.getRandom(5);
                //System.out.println("Venom roll: "+inflictVenom);
                if (inflictVenom == 0 && ptarg.isSusceptibleToVenom()) {
                    new Venom(ptarg);
                }
            }
        } else if (target.isNPC()) {
            Npc npc = (Npc) target;
            if (!npc.infected && player.getEquipment().canInfect(player) && !Venom.venomImmune(npc)) {
                if (Utility.getRandom(10) == 5) {
                    new Venom(npc);
                }
            }
        }

        int wep = player.playerEquipment[player.getEquipment().getWeaponId()];
        if (wep > -1 && !player.usingMagic) {
            PlayerSounds.SendSoundPacketForId(player, player.isUsingSpecial(), wep);
        }

		/*
		 * Check if we are using a special attack
		 */
        if (player.isUsingSpecial() && player.getCombatType() != CombatType.MAGIC) {
            Special.handleSpecialAttack(player, target);
            return;
        }

        // ####### WASNT A SPECIAL ATTACK -- DO NORMAL COMBAT STYLES HERE #####

		/*
		 * Start the attack animation
		 */
        if (!player.usingMagic && player.playerEquipment[player.getEquipment().getWeaponId()] != 22494 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2415 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2416 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2417) {
            player.playAnimation(Animation.create(CombatAnimation.getAttackAnimation(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase())));


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

            if (!player.autoCast) { // Not autocast = a one-time attack. Doesn't continue following.
                player.stopMovement();
                player.setFollowing(null);
            }
        }

		/*
		 * Set the target in combat since we just attacked him/her
		 */
        if (target.isPlayer()) {
            ((Player) target).putInCombat(player.getIndex());
            ((Player) target).killerId = player.getIndex();
            target.getActionSender().sendRemoveInterfacePacket();
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


            // First, calc hits.
            int dam1 = Utility.getRandom(player.getCombat().calculateMeleeMaxHit());

            // Second, calc accuracy. If miss, dam=0
            if (!(CombatFormulae.getAccuracy(player, target, 0, 1.0))) {
                dam1 = 0;
            }

            Hit hitInfo = target.take_hit(player, dam1, CombatType.MELEE, false);
            Combat.hitEvent(player, target, 1, hitInfo, CombatType.MELEE);

        } else if (player.getCombatType() == CombatType.RANGED) {
            player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];

            if (player.getAttackStyle() == 2)
                player.attackDelay--;

            player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
            player.getCombat().fireProjectileAtTarget();

            boolean hand_thrown = false;
            if (hand_thrown) {

                if (player.playerEquipment[3] == 21000) {
                    player.getItems().removeEquipment();
                } else {
                    player.getItems().deleteAmmo(); // here
                }
            } else {

                if (player.playerEquipment[3] == 11235 || player.playerEquipment[3] == 12765 || player.playerEquipment[3] == 12766
                        || player.playerEquipment[3] == 12767 || player.playerEquipment[3] == 12768) {
                    player.getItems().deleteArrow();
                }

                //Arrows check
                boolean dropArrows = true;
                if (player.lastWeaponUsed == 12926 || player.lastWeaponUsed == 4222) {
                    dropArrows = false;
                }

                if (dropArrows) {
                    player.getItems().dropArrowUnderTarget();
                    player.getItems().deleteArrow();
                    if (player.playerEquipment[3] == 11235) { // Dark bow, 2nd arrow
                        player.getItems().dropArrowUnderTarget();
                    }
                }
            }

            // Random dmg
            int dam1 = Utility.getRandom(player.getCombat().calculateRangeMaxHit());

            // Bolt special increases damage.
            boolean boltSpec = player.getEquipment().isCrossbow(player) && Utility.getRandom(target.isPlayer() ? 10 : 8) == 1;
            if (boltSpec && dam1 > 0)
                dam1 = Combat.boltSpecialVsEntity(player, target, dam1);

            // Missed?
            if (!player.hasAttribute("ignore defence") && !CombatFormulae.getAccuracy(player, target, 1, 1.0)) {
                dam1 = 0;
            }
            player.rangeEndGFX = RangeData.getRangeEndGFX(player);

            // Apply dmg.
            Hit hitInfo = target.take_hit(player, dam1, CombatType.RANGED, false);
            Combat.hitEvent(player, target, 1, hitInfo, CombatType.RANGED);

        } else if (player.getCombatType() == CombatType.MAGIC) {
            player.oldSpellId = player.getSpellId();
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
                player.followDistance = 5;
            }

            if (player.playerEquipment[player.getEquipment().getWeaponId()] == 11907 || player.playerEquipment[player.getEquipment().getWeaponId()] == 12899) {
                return;
            }


            if (target.isPlayer()) {
                Player ptarg = (Player) target;
                if (player.MAGIC_SPELLS[player.oldSpellId][0] == 12891 && ptarg.getMovementHandler().isMoving()) {
                    player.getProjectile().createPlayersProjectile(pX, pY, offX, offY, 50, 85, 368, 25, 25, targetIndex,
                            player.getCombat().getStartDelay());
                }
            }

            boolean splash = !CombatFormulae.getAccuracy(player, target, 2, 1.0);

            int spellFreezeTime = player.getCombat().getFreezeTime();
            if (spellFreezeTime > 0 && !target.frozen() && !splash) {

                target.freeze(spellFreezeTime);
                if (target.isPlayer()) {
                    ((Player) target).getMovementHandler().resetWalkingQueue();
                    ((Player) target).write(new SendMessagePacket("You have been frozen."));
                    ((Player) target).frozenBy = player.getIndex();
                }
            }
            // One time attack!
            if (!player.autoCast) {
                player.getCombat().reset();
            }

            int dam1 = MagicCalculations.magicMaxHitModifier(player);
            if (player.getCombat().godSpells()) {
                if (System.currentTimeMillis() - player.godSpellDelay < 300000) {
                    dam1 += 10;
                }
            }

            // Graphic that appears when hit appears.
            Server.getTaskScheduler().schedule(new ScheduledTask(hitDelay) {
                @Override
                public void execute() {
                    if (splash)
                        target.playGraphics(Graphic.create(85, 0, 100));
                    else
                        target.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.oldSpellId][5], 0, player.getCombat().getEndGfxHeight()));
                    this.stop();
                }
            });

            if (splash) {
                dam1 = 0;
            } else {
                switch (player.MAGIC_SPELLS[player.oldSpellId][0]) {
                    case 12445: // teleblock
                        if (target.isPlayer()) {
                            Player defender = (Player) target;
                            if (defender.teleblock.elapsed(defender.teleblockLength)) {
                                defender.teleblock.reset();
                                defender.write(new SendMessagePacket("You have been teleblocked."));
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

            Combat.hitEvent(player, target, hitDelay, new Hit(dam1), CombatType.MAGIC);
            player.setSpellId(0);
        }
    }

    private static int boltSpecialVsEntity(Player attacker, Entity defender, int dam1) {
        if (dam1 == 0) return dam1;
        switch (attacker.playerEquipment[attacker.getEquipment().getQuiverId()]) {
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
                    int opHP = defender.isPlayer() ? ((Player) defender).getSkills().getLevel(Skills.HITPOINTS)
                            : ((Npc) defender).currentHealth;
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
                int shield = defender.isPlayer() ? ((Player) defender).playerEquipment[((Player) defender).getEquipment().getShieldId()] : -1;
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
        boolean spellQueued = player.usingMagic && player.getCombatType() == CombatType.MAGIC && player.spellId > 0;

        player.usingMagic = player.usingBow;
        player.setCombatType(null); // reset

        int followDist = 1;
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

        // Spell id set when packet: magic on player
        if (player.getSpellId() > 0) {
            player.usingMagic = true;
            player.setCombatType(CombatType.MAGIC);
        }
        if (player.usingMagic) {
            player.setCombatType(CombatType.MAGIC);
            followDist = 8;
        }

		/*
		 * Check if we are using ranged
		 */
        if (player.getCombatType() != CombatType.MAGIC) {
            player.usingBow = player.getEquipment().isBow(player);
            boolean handthrown = player.getEquipment().isThrowingWeapon(player);
            player.usingCross = player.getEquipment().isCrossbow(player);
            boolean bolt = player.getEquipment().isBolt(player);
            boolean javalin = player.getCombat().properJavalins();

            if (handthrown || player.usingCross || player.usingBow || player.getEquipment().wearingBallista(player) || player.getEquipment().wearingBlowpipe(player)) {
                player.setCombatType(CombatType.RANGED);
                followDist = handthrown ? 4 : 7;
            }
        }
        // hasn't been set to magic/range.. must be melee.
        if (player.getCombatType() == null) {
            player.setCombatType(CombatType.MELEE);
            if (CombatData.usingHalberd(player))
                followDist = 2;
        }
        player.followDistance = followDist;
        //player.message("style: "+player.getCombatType()+"  dist:"+followDist+"  atkDelay:"+player.attackDelay);

    }

    public static void hitEvent(Player player, Entity target, int delay, Hit hit, CombatType combatType) {

        // Schedule a task
        Server.getTaskScheduler().schedule(new ScheduledTask(delay) {
            public void execute() {
                PlayerSounds.sendBlockOrHitSound(player, hit.getDamage() > 0);
                target.damage(hit);

                // Range attack invoke block emote when hit appears.
                if (hit.cbType == CombatType.RANGED && target.isNPC()) {
                    if (((Npc) target).attackTimer < 5)
                        target.playAnimation(Animation.create(NPCCombatData.getNPCBlockAnimation(((Npc) target))));

                    // Graphics that happen when hit appears
                    player.setAttribute("ignore defence", false);
                    if (player.rangeEndGFX > 0) {
                        if (player.rangeEndGFXHeight) {
                            target.playGraphics(Graphic.create(player.rangeEndGFX, 0, 100));
                        } else {
                            target.playGraphics(Graphic.create(player.rangeEndGFX, 0, 0));
                        }
                    }
                }
                if (hit.cbType == CombatType.MAGIC) {
                    if (player.getCombat().getEndGfxHeight() == 100 && !player.magicFailed) { // end GFX
                        target.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.oldSpellId][5], 0, 100));
                    } else if (!player.magicFailed) {
                        target.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.oldSpellId][5], 0, player.getCombat().getEndGfxHeight()));
                    } else if (player.magicFailed) {
                        target.playGraphics(Graphic.create(85, 0, 100));
                    }

                }
                this.stop();
            }
        });
    }
}