package com.venenatis.game.model.combat;

import java.util.Random;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.activity.minigames.Minigame;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.sounds_and_music.sounds.PlayerSounds;
import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.combat.RangeConstants.ArrowType;
import com.venenatis.game.model.combat.RangeConstants.BowType;
import com.venenatis.game.model.combat.RangeConstants.RangeWeaponType;
import com.venenatis.game.model.combat.data.CombatData;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.data.SkullType;
import com.venenatis.game.model.combat.magic.MagicCalculations;
import com.venenatis.game.model.combat.magic.SpellBook;
import com.venenatis.game.model.combat.magic.spell.SpellHandler;
import com.venenatis.game.model.combat.special_attacks.Special;
import com.venenatis.game.model.definitions.EquipmentDefinition;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.definitions.WeaponDefinition;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Entity.VenomWeapons;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.following.PlayerFollowing;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.RandomGenerator;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;
import com.venenatis.game.world.pathfinder.PathFinder;
import com.venenatis.game.world.pathfinder.impl.VariablePathFinder;
import com.venenatis.server.Server;

public class Combat {
	
	/**
	 * The blowpipe attribute
	 */
	public static int BLOWPIPE = 12926;
	
	/**
	 * The random number generator.
	 */
	protected final static Random random = new Random();
	
	public static void recoil(Player defender, Entity entity_attacker, final int damage) {
		if (defender.getEquipment().get(EquipmentConstants.RING_SLOT) != null && defender.getEquipment().get(EquipmentConstants.RING_SLOT).getId() == 2550) {
			if (damage > 0) {
				int recoil = (int) Math.ceil(damage / 10);
				if (recoil < 1) {
					recoil = 1;
				}
				if (recoil > defender.getCombatState().getRingOfRecoil()) {
					recoil = defender.getCombatState().getRingOfRecoil();
				}
				
				if(entity_attacker instanceof Player) {
					if (recoil > defender.getSkills().getLevel(Skills.HITPOINTS)) {
						recoil = defender.getSkills().getLevel(Skills.HITPOINTS);
					}
				} else { 
					NPC n = (NPC)entity_attacker;
					if(recoil > n.getHitpoints()) {
						recoil = n.getHitpoints();
					}
				}
				if (recoil < 1) {
					System.out.println("r<1 btw");
					return;
				}
				defender.getCombatState().setRingOfRecoil(defender.getCombatState().getRingOfRecoil() - recoil);
				entity_attacker.take_hit(defender, recoil, null).send(1);//null style so we don't get XP and protection prayers don't reduce it
				if (defender.getCombatState().getRingOfRecoil() < 1) {
					defender.asPlayer().getEquipment().remove(new Item(2550));
					defender.getCombatState().setRingOfRecoil(40);
					defender.getActionSender().sendMessage("Your Ring of Recoil has shattered.");
				}
			}
		}
		if (random.nextFloat() <= 0.25f && CombatFormulae.fullDharok(defender) && CombatFormulae.hasAmuletOfTheDamned(defender) && damage > 0) {
			int recoil = (int) Math.ceil((float) damage * 0.15);

			if (recoil < 1) {
				recoil = 1;
			}

			if(entity_attacker instanceof Player) {
				if (recoil > defender.getSkills().getLevel(Skills.HITPOINTS)) {
					recoil = defender.getSkills().getLevel(Skills.HITPOINTS);
				}
			} else { 
				NPC n = (NPC)entity_attacker;
				if(recoil > n.getHitpoints()) {
					recoil = n.getHitpoints();
				}
			}
			if (recoil < 1) {
				return;
			}
			entity_attacker.take_hit(defender, recoil, null);
		}
	}
	
	/**
	 * Skulls the specified player
	 * @param player
	 * @param type
	 * @param seconds
	 */
	public static void skull(Player player, SkullType type, int seconds) {
		//Certain minigames allows skulling
		if (!MinigameHandler.execute(player, true, Minigame::canSkull)) {
			return;
		}
		
		//We can't skull in the duel arena
		if (player.getDuelArena().isDueling()) {
			return;
		}
		
		//Apply the skull and timer
		player.setSkullType(type);
		player.setSkullTimer(seconds);
		if(type == SkullType.RED_SKULL) {
			player.message("@bla@You have received a @red@red skull@bla@! You can no longer use the protect item prayer!");
			PrayerHandler.deactivatePrayer(player, Prayers.PROTECT_ITEM);		
		} else if(type == SkullType.SKULL) {
			player.message("You've been skulled!");
		}
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

    /**
     * The names of all the bonuses in their exact identified slots.
     */
    public static final String[] BONUS_NAMES = {"Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range",
            "Melee Strength", "Ranged strength", "Magic damage", "Prayer", "Undead", "Slayer"};

    /**
	 * Resets the combat for the entity
	 * 
	 * @param entity
	 */
    public static void resetCombat(Entity entity) {
		entity.getCombatState().setInCombat(false);
		
		if (entity instanceof Player) {
			Player player = (Player) entity;
            player.setSpellId(-1);
			player.setCombatType(null);
			player.faceEntity(null);
	        player.getCombatState().reset();
	        player.setFollowing(null);
		}
    }

    public static void playerVsEntity(Player player) {
        if (player.getCombatState().noTarget())
            return;

        // Establish what style we'd be using this cycle
        Combat.setCombatStyle(player);
        Entity target = player.getCombatState().getTarget();
        player.faceEntity(target);
        player.debug("style: "+player.getCombatType()+" vs "+target);
        
        if (target.isPlayer()) {
			if (player.getDuelArena().isDueling()) {
				if (!player.getDuelArena().canAttack()) {
					return;
				}
			}
		}

        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            player.getActionSender().sendEntityFeed(Utility.formatName(ptarg.getUsername()), ptarg.getSkills().getLevel(Skills.HITPOINTS), ptarg.getSkills().getLevelForExperience(Skills.HITPOINTS));
        } else {
            NPC npc = (NPC) target;
            if (npc.getId() != 493 || npc.getId() != 496 || npc.getId() != 5534) {
                String name = target.isNPC() ? npc.asNpc().getName() : "null";
                player.getActionSender().sendEntityFeed(name, npc.getHitpoints(), npc.getMaxHitpoints());
                //System.out.println("name: "+name+" current health: "+npc.getHitpoints()+" max health: "+npc.getMaxHitpoints());
            }
        }

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
            player.getWalkingQueue().reset();
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
        target.take_hit(player, dam1, CombatStyle.MELEE).giveXP(player).send();
    }

    private static void magicAttack(Player player, Entity target) {
        if (!touches(player, target))
            return;
        if (!attackable(player, target))
            return;

        int wepId = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
        if (player.getSpellBook() != SpellBook.MODERN_MAGICS && (wepId == 2415 || wepId == 2416 || wepId == 2417)) {
            player.message("You must be on the modern spellbook to cast this spell.");
            resetCombat(player);
            return;
        }
        if(player.getAutocastId() > -1) {
        	player.setSpellId(player.getAutocastId());
        }
        
        if (player.touchDistance(target, 7))
            player.getWalkingQueue().reset();

    	if (player.getCombatState().getSpellDelay() > 0) {
			return;
		}
        
        if (player.getCombatState().getAttackDelay() > 0) {
            // don't attack as our timer hasnt reached 0 yet
            return;
        }
        int spell = player.getSpellId();
        int req = player.getMagic().requirement(spell);
        if (player.getSkills().getLevel(Skills.MAGIC) < req) {
            player.message("You need a Magic level of "+req+" to cast this spell.");
            resetCombat(player);
            return;
        }
        Item[] runes = player.getMagic().runes(spell);
        if (runes != null && runes.length > 0) {
            //Runes check
            if (!player.getMagic().checkRunes(player, true, runes) && player.getTotalAmountDonated() < 100) {
                resetCombat(player);
                return;
            }
        }

        // Magic attack anim
        player.playAnimation(Animation.create(player.MAGIC_SPELLS[spell][2]));

        int hitDelay = wepId == -1 ? 4 : CombatData.getHitDelay(player, ItemDefinition.get(wepId).getName().toLowerCase());

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
            // trident code + remove charge from weapon if you support that
            return;
        }

        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            if (player.MAGIC_SPELLS[spell][0] == 12891 && ptarg.getWalkingQueue().isMoving()) {
                player.playProjectile(Projectile.create(player.getCentreLocation(), target.getCentreLocation(), 368, player.getCombatState().getStartDelay(), 50, 85, 25, 25, target.getProjectileLockonIndex(), 16, 64));
            }
        }

        boolean splash = !CombatFormulae.getAccuracy(player, target, 2, 1.0);
        if (!target.frozen() && !splash) {
        	//After the damage was taken we activate our spell effects.
			SpellHandler.handleSpellEffect(player, target);
            if (target.isPlayer() && target.frozen()) {
                ((Player) target).getWalkingQueue().reset();
                target.message("You have been frozen.");
                ((Player) target).frozenBy = player.getIndex();
            }
        }

        int dam1 = MagicCalculations.magicMaxHitModifier(player);

        // Graphic that appears when hit appears.
        final int endGfx = player.MAGIC_SPELLS[spell][5];
        final int endH = player.getMagic().getEndGfxHeight(player);
        Server.getTaskScheduler().schedule(new Task(hitDelay) {
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
                        if (!defender.getCombatState().isTeleblocked()) {
                            defender.getCombatState().getTeleblock().reset();
                            defender.putInCombat(1);
                            if (defender.isActivePrayer(PrayerHandler.Prayers.PROTECT_FROM_MAGIC)) {
                            	defender.getCombatState().teleblock(150);				
                			} else {
                				defender.getCombatState().teleblock(300);	
                			}
                        }
                    }
                    break;
            }
        }

        target.take_hit(player, dam1, CombatStyle.MAGIC).giveXP(player).send(hitDelay);
        onAttackDone(player, target);

        // MUST BE THE LAST PIECE OF CODE IN THIS METHOD. Spellid is used in other methods as a reference.
        player.setSpellId(-1);

    }
    
	private final static boolean canWeFire(Player attacker) {
		Item weapon = attacker.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		if (weapon == null) {
			return false;
		}
		EquipmentDefinition weaponEquipDef = weapon.getEquipmentDefinition();

		BowType bowType = weaponEquipDef.getBowType();
		if (bowType != null && bowType != BowType.CRYSTAL_BOW) {
			String ammo_response = isCrossBow(bowType) ? "bolts" : "arrows";
			Item arrows = attacker.getEquipment().get(EquipmentConstants.AMMO_SLOT);
			if (arrows == null) {
				attacker.getActionSender().sendMessage("There are no " + ammo_response + " left in your quiver.");
				return false;
			}

			if (bowType == BowType.DARK_BOW) {
				if (arrows.getAmount() < 2) {
					attacker.getActionSender().sendMessage("You need atleast 2 arrows to use the Dark bow's attack.");
					return false;
				}
			}

			EquipmentDefinition arrowEquipDef = arrows.getEquipmentDefinition();
			ArrowType arrowType = arrowEquipDef.getArrowType();

			boolean hasCorrectArrows = false;
			for (ArrowType correctArrowType : bowType.getValidArrows()) {
				if (correctArrowType == arrowType) {
					hasCorrectArrows = true;
					break;
				}
			}
			if (!hasCorrectArrows) {
				attacker.getActionSender().sendMessage("You can't use " + arrows.getDefinition().getName() + "s with a " + weapon.getDefinition().getName() + ".");
				return false;
			}
		}
		return true;
	}

	private static void rangeAttack(Player player, Entity target) {
		
		if (!touches(player, target))
			return;
		if (!attackable(player, target))
			return;

		if (player.touchDistance(target,
				calculateAttackDistance(player, target)))
			player.getWalkingQueue().reset();

		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		if (weapon == null) {
			return;
		}

		EquipmentDefinition weaponEquipDef = weapon.getEquipmentDefinition();

		if (weapon.getId() == BLOWPIPE) {

			// TODO add charges
		} else {
			if(!canWeFire(player))
				return;
		}

		if (player.getCombatState().getAttackDelay() > 0) {
			// don't attack as our timer hasnt reached 0 yet
			return;
		}

		if (!target.moving())
			player.getWalkingQueue().reset();

		if (player.isUsingSpecial()) {
			Special.handleSpecialAttack(player, target);
			onAttackDone(player, target);
			return;
		}

		// Normal attack
		doAttackAnim(player, target);
		onAttackDone(player, target);
		
		Graphic pullback = null;
		int projectile = -1;
		int hitDelay = 2;

		Item ammunition = null;
		EquipmentDefinition arrowEquipDef = null;

		BowType bowType = null;
		RangeWeaponType rangeWeaponType = null;

		if (pullback == null && projectile == -1 && player.getEquipment() != null) {
			weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
			if (weapon == null) {
				return; // every range type requires a weapon
			}
			weaponEquipDef = weapon.getEquipmentDefinition();
			bowType = weaponEquipDef.getBowType();

			if (weapon.getId() == BLOWPIPE) {
				projectile = 1122;
			} else {

				if (bowType != null) { // standard bow and arrow
					ArrowType arrowType;
					if (bowType == BowType.CRYSTAL_BOW) {
						arrowType = ArrowType.CRYSTAL_ARROW;
						ammunition = null;
					} else {
						Item arrows = player.getEquipment().get(EquipmentConstants.AMMO_SLOT);
						arrowEquipDef = arrows.getEquipmentDefinition();
						arrowType = arrowEquipDef.getArrowType();
						ammunition = arrows;
					}
					if (bowType == BowType.DARK_BOW) {
						if (ammunition != null && ammunition.getId() != 11212) {
							pullback = Graphic.create(arrowType.getPullbackGraphic().getId() + 1085, arrowType.getPullbackGraphic().getDelay(), arrowType.getPullbackGraphic().getHeight());
						} else {
							pullback = arrowType.getPullbackGraphic();
						}
					} else {
						pullback = arrowType.getPullbackGraphic();
					}
					projectile = arrowType.getProjectileId();
				} else { // ranged weapon
					rangeWeaponType = weaponEquipDef.getRangeWeaponType();
					if (rangeWeaponType != null) {
						ammunition = weapon;

						pullback = rangeWeaponType.getPullbackGraphic();
						projectile = rangeWeaponType.getProjectileId();
					}
				}
			}
			player.getEquipment().setBonus();
		}

		boolean special = player.isUsingSpecial() ? true : false;

		int clientSpeed;
		int showDelay;
		int slope;
		if (!special || (bowType == BowType.BRONZE_CBOW || bowType == BowType.IRON_CBOW || bowType == BowType.STEEL_CBOW || bowType == BowType.MITH_CBOW || bowType == BowType.ADAMANT_CBOW || bowType == BowType.RUNE_CBOW || bowType == BowType.ARMADYL_CBOW)) {
			if (pullback != null) {
				player.playGraphics(pullback);
			}

			if (rangeWeaponType != null) {
				if (rangeWeaponType == RangeWeaponType.BRONZE_DART || rangeWeaponType == RangeWeaponType.IRON_DART || rangeWeaponType == RangeWeaponType.STEEL_DART || rangeWeaponType == RangeWeaponType.MITHRIL_DART || rangeWeaponType == RangeWeaponType.ADAMANT_DART || rangeWeaponType == RangeWeaponType.RUNE_DART || rangeWeaponType == RangeWeaponType.BLACK_DART) {
					clientSpeed = 35;
					showDelay = 20;
					slope = 13;
				} else {
					clientSpeed = 55;
					showDelay = 45;
					slope = 5;
				}
			} else {
				if (bowType == BowType.KARILS_XBOW || bowType == BowType.BRONZE_CBOW || bowType == BowType.IRON_CBOW || bowType == BowType.STEEL_CBOW || bowType == BowType.MITH_CBOW || bowType == BowType.ADAMANT_CBOW || bowType == BowType.RUNE_CBOW || bowType == BowType.ARMADYL_CBOW) {
					clientSpeed = 55;
					showDelay = 45;
					slope = 5;
				} else {
					int distance = player.getLocation().distanceToEntity(player, target);
					clientSpeed = 55 + (distance * 5);
					if (distance > 2) {
						hitDelay += 1;
					}
					showDelay = 45;
					slope = 15;
				}
			}
			if (bowType == BowType.DARK_BOW) {
				hitDelay += 1;
				clientSpeed += 15;
				player.playProjectile(Projectile.create(player.getLocation(), target.getCentreLocation(), projectile, showDelay, 50, clientSpeed - 10, 41, 31, target.getProjectileLockonIndex(), slope - 6, 64));
				player.playProjectile(Projectile.create(player.getLocation(), target.getCentreLocation(), projectile, showDelay, 50, clientSpeed, 46, 36, target.getProjectileLockonIndex(), slope + 3, 64));
			} else {
				player.playProjectile(Projectile.create(player.getLocation(), target.getCentreLocation(), projectile, showDelay, 50, clientSpeed, 46, 36, target.getProjectileLockonIndex(), slope, 86));
			}

		} else { // spec attacks
			if (bowType == BowType.DARK_BOW) {
				if (pullback != null) {
					player.playGraphics(pullback);
				}
				if (ammunition != null && ammunition.getId() != 11212) {
					if (player.getLocation().isWithinDistance(player, target, 1)) {
						clientSpeed = 55;
					} else if (player.getLocation().isWithinDistance(player, target, 3)) {
						clientSpeed = 55;
					} else if (player.getLocation().isWithinDistance(player, target, 8)) {
						clientSpeed = 65;
						hitDelay += 1;
					} else {
						clientSpeed = 75;
					}
					showDelay = 45;
					slope = 15;
					clientSpeed += 30;
					player.playProjectile(Projectile.create(player.getLocation(), target.getCentreLocation(), projectile, showDelay, 50, clientSpeed, 41, 31, target.getProjectileLockonIndex(), 3, 36));
					player.playProjectile(Projectile.create(player.getLocation(), target.getCentreLocation(), projectile, showDelay, 50, clientSpeed + 10, 46, 36, target.getProjectileLockonIndex(), slope + 6, 36));
				}
			}
		}
   
        loseAmmo(player, target);
        
        boolean ignoreArrows = rangeWeaponType != null && Combat.isHandWeapon(rangeWeaponType);
        player.debug("ignore arrow str:"+ignoreArrows);

        // Random dmg
        int dam1 = Utility.getRandom(player.getCombatState().calculateRangeMaxHit(ignoreArrows));

        //Random dmg2 (special case such as darkbow)
        int dam2 = Utility.getRandom(player.getCombatState().calculateRangeMaxHit(ignoreArrows));
        
        // Bolt special increases damage.
        boolean boltSpec = isCrossBow(bowType) && Utility.getRandom(target.isPlayer() ? 10 : 8) == 1;
        if (boltSpec && dam1 > 0)
            dam1 = Combat.boltSpecialVsEntity(player, target, dam1);

        // Missed?
        if (!player.hasAttribute("ignore defence") && !CombatFormulae.getAccuracy(player, target, 1, 1.0)) {
            dam1 = 0;
            dam2 = 0;
        }
        player.removeAttribute("ignore defence");

		// Apply dmg.
		target.take_hit(player, dam1, CombatStyle.RANGE).giveXP(player).send(hitDelay);

		// Apply second dmg.
		if(player.getEquipment().contains(11235))
		target.take_hit(player, dam2, CombatStyle.RANGE).giveXP(player).send(hitDelay);
	}
	
	public static boolean isCrossBow(BowType bowType) {
		return ((bowType == BowType.KARILS_XBOW || bowType == BowType.BRONZE_CBOW || bowType == BowType.IRON_CBOW
				|| bowType == BowType.STEEL_CBOW || bowType == BowType.MITH_CBOW || bowType == BowType.ADAMANT_CBOW
				|| bowType == BowType.RUNE_CBOW || bowType == BowType.ARMADYL_CBOW || bowType == BowType.CHAOTIC_CBOW
				|| bowType == BowType.DRAGON_CBOW));
	}
	
	public static boolean isBow(BowType bowType) {
		return ((bowType == BowType.SHORTBOW) || bowType == BowType.LONGBOW || bowType == BowType.OAK_SHORTBOW
				|| bowType == BowType.OAK_LONGBOW || bowType == BowType.WILLOW_SHORTBOW
				|| bowType == BowType.WILLOW_LONGBOW || bowType == BowType.MAPLE_SHORTBOW
				|| bowType == BowType.MAPLE_LONGBOW || bowType == BowType.YEW_SHORTBOW || bowType == BowType.YEW_LONGBOW
				|| bowType == BowType.MAGIC_SHORTBOW || bowType == BowType.MAGIC_LONGBOW || bowType == BowType.DARK_BOW
				|| bowType == BowType.TWISTED_BOW || bowType == BowType.CRYSTAL_BOW);
	}
	
	public static boolean isHandWeapon(RangeWeaponType rangeWeaponType) {
		return ((rangeWeaponType == RangeWeaponType.BRONZE_DART || rangeWeaponType == RangeWeaponType.IRON_DART
				|| rangeWeaponType == RangeWeaponType.STEEL_DART || rangeWeaponType == RangeWeaponType.BLACK_DART
				|| rangeWeaponType == RangeWeaponType.MITHRIL_DART) || rangeWeaponType == RangeWeaponType.ADAMANT_DART
				|| rangeWeaponType == RangeWeaponType.RUNE_DART || rangeWeaponType == RangeWeaponType.DRAGON_DART
				|| rangeWeaponType == RangeWeaponType.BRONZE_JAVELIN || rangeWeaponType == RangeWeaponType.IRON_JAVELIN
				|| rangeWeaponType == RangeWeaponType.STEEL_JAVELIN
				|| rangeWeaponType == RangeWeaponType.MITHRIL_JAVELIN
				|| rangeWeaponType == RangeWeaponType.ADAMANT_JAVELIN || rangeWeaponType == RangeWeaponType.RUNE_JAVELIN
				|| rangeWeaponType == RangeWeaponType.DRAGON_JAVELIN || rangeWeaponType == RangeWeaponType.BRONZE_KNIFE
				|| rangeWeaponType == RangeWeaponType.IRON_KNIFE || rangeWeaponType == RangeWeaponType.STEEL_KNIFE
				|| rangeWeaponType == RangeWeaponType.BLACK_KNIFE || rangeWeaponType == RangeWeaponType.MITHRIL_KNIFE
				|| rangeWeaponType == RangeWeaponType.ADAMANT_KNIFE || rangeWeaponType == RangeWeaponType.RUNE_KNIFE
				|| rangeWeaponType == RangeWeaponType.BRONZE_THROWNAXE
				|| rangeWeaponType == RangeWeaponType.IRON_THROWNAXE
				|| rangeWeaponType == RangeWeaponType.STEEL_THROWNAXE
				|| rangeWeaponType == RangeWeaponType.MITHRIL_THROWNAXE
				|| rangeWeaponType == RangeWeaponType.MITHRIL_THROWNAXE
				|| rangeWeaponType == RangeWeaponType.RUNE_THROWNAXE
				|| rangeWeaponType == RangeWeaponType.DRAGON_THROWNAXE);
	}
    
    /**
	 * Remove ammo from arrow/hands slot + plus chance for that ammo drops to the floor
	 */
	private static void loseAmmo(Player player, Entity target) {
		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		EquipmentDefinition weaponEquipDef = weapon.getEquipmentDefinition();

		BowType bowType = weaponEquipDef.getBowType();
		
		RangeWeaponType rangeWeaponType = weaponEquipDef.getRangeWeaponType();
		
		boolean avas = Combat.avas(player);
		boolean bow = player.getEquipment().contains(BLOWPIPE) || player.getEquipment().contains(4222) || player.getEquipment().contains(19481)
				|| isCrossBow(bowType) || isBow(bowType);
		boolean hand = isHandWeapon(rangeWeaponType);
		boolean dropArrows = !avas || (avas && RandomGenerator.nextInt(100) > 90);
		Item drop = null;

		// Establish do we wanna drop
		if (player.getEquipment().contains(BLOWPIPE)) {
			// if we have dart/scales support, handle that here
		} else if (bow) {
			if (dropArrows) {
				Item arrow = player.getEquipment().get(EquipmentConstants.AMMO_SLOT);
				if (arrow != null) {
					drop = new Item(arrow.id, 1);
					player.getEquipment().setSlot(EquipmentConstants.AMMO_SLOT, arrow.amount > 1 ? new Item(arrow.id, arrow.amount - 1) : null);
				}
			}
		} else if (hand) {
			if (dropArrows) {
				Item thrown = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
				if (thrown != null) {
					drop = new Item(thrown.id, 1);
					player.getEquipment().setSlot(EquipmentConstants.WEAPON_SLOT, thrown.amount > 1 ? new Item(thrown.id, thrown.amount - 1) : null);
				}
			}
		} else {
			System.err.println("UNKNOWN RANGE AMMO SITUATION"); // what wep u using?
		}
		if (drop != null && RandomGenerator.nextInt(3) == 1) {
			GroundItemHandler.createGroundItem(new GroundItem(drop, target.getLocation(), player));
		}
	}

	/**
	 * Validates that the attack can be made
	 * 
	 * @param player
	 *            The {@link Player} attacking the oppponent
	 * @param target
	 *            The {@link Player} being attacked
	 * @return If the attack is successful
	 */
	private static boolean validateAttack(Player player, Player target) {
		if (target == null || player.getCombatState().isDead() || target.getCombatState().isDead() || !target.isActive() || target.getSkills().getLevel(Skills.HITPOINTS) <= 0 || target.getZ() != player.getZ()) {
			Combat.resetCombat(player);
			player.getWalkingQueue().reset();
			return false;
		}
		if (!player.receivedStarter()) {
			Combat.resetCombat(player);
			player.getWalkingQueue().reset();
			player.debug("target in tut");
			return false;
		}
		if (!player.receivedStarter()) {
			player.message("You cannot attack this player.");
			player.getWalkingQueue().reset();
			Combat.resetCombat(player);
			return false;
		}

		boolean bypassCosImTheBest = player.getUsername().equalsIgnoreCase("test") || player.getUsername().equalsIgnoreCase("patrick");
		int myCB = player.getCombatLevel();
		int target_CB = target.getCombatLevel();
		if (Area.inWilderness(player)) {
			if (!bypassCosImTheBest && ((myCB > target_CB + target.getWildLevel()) || (myCB < target_CB - target.getWildLevel()))) {
				player.message("You can only fight players in your combat range!");
				player.getWalkingQueue().reset();
				Combat.resetCombat(player);
				return false;
			}
		} else {
			// All other places not in wildy: range is +/- 12 combat levels
			if (!bypassCosImTheBest && ((myCB > target_CB + 12) || (myCB < target_CB - 12))) {
				player.message("You can only fight players in your combat range!");
				player.getWalkingQueue().reset();
				Combat.resetCombat(player);
				return false;
			}
		}
		if (!Area.inMultiCombatZone(target)) { // single combat zones
			if (target.lastAttacker != player && Combat.hitRecently(target, 4000)) {
				player.message("That player is already in combat.");
				player.getWalkingQueue().reset();
				Combat.resetCombat(player);
				return false;
			}

			if (target != player.lastAttacker && Combat.hitRecently(player, 4000)) {
				player.message("You are already in combat.");
				player.getWalkingQueue().reset();
				Combat.resetCombat(player);
				return false;
			}
		}
		if (!player.getController().canAttackPlayer(player, target)) {
			player.getWalkingQueue().reset();
			return false;
		}
		return true;
	}

    private static boolean attackable(Player player, Entity target) {
        // Can we attack the target? Not distance checks (yet) as they come later.
        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            if (!validateAttack(player, ptarg)) {
                return false;
            }
        } else {
            NPC npc = (NPC) target;
            // Can attack check
            if (!NpcCombat.canAttackNpc(player, npc)) {
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
    	player.getCombatState().setLastTarget(target);
        Item wep = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);

		// Set our attack timer so we dont instantly hit again
        player.getCombatState().setAttackDelay(WeaponDefinition.sendAttackSpeed(player));
        
        //Set the magic delay, which is 5 by default otherwise we adjust them in the script
        player.getCombatState().setSpellDelay(Constants.MAGIC_ATTACK_DELAY);

        // Rapid reduce delay by 1 faster
        if (player.getCombatType() == CombatStyle.RANGE) {
            if (player.getAttackStyle() == 2)
                player.getCombatState().setAttackDelay(player.getCombatState().getAttackDelay() - 1);
        }
        boolean immune = false;
        
        if (player.hasAttribute("antiVenom+")) {
			immune = System.currentTimeMillis() - (long) player.getAttribute("antiVenom+", 0L) < 300000;
		}

        // Add a skull if needed
        if (target.isPlayer()) {
            Player ptarg = (Player) target;
            if (player != null && player.attackedPlayers != null && ptarg.attackedPlayers != null && !Area.inDuelArena(ptarg)) {
                if (!player.attackedPlayers.contains(target.getIndex()) && !ptarg.attackedPlayers.contains(player.getIndex())) {
                    player.attackedPlayers.add(target.getIndex());
                    skull(player, SkullType.SKULL, 300);
                }
            }
            
            if (!immune) {
            	
            	boolean attackerVenomItems = false;
    			boolean venomItems = false;
    			
    			for (Item equip : player.getEquipment().toArray()) {
    				if (equip == null) {
    					continue;
    				}
    				VenomWeapons venomWeapons = VenomWeapons.of(equip.getId());
    				if (venomWeapons != null) {
    					attackerVenomItems = true;
    				}
    			}
				
				for (Item equip : ptarg.getEquipment().toArray()) {
					if (equip == null) {
						continue;
					}
					VenomWeapons venomWeapons = VenomWeapons.of(equip.getId());
					if (venomWeapons != null) {
						venomItems = true;
					}
				}
				if (attackerVenomItems && !venomItems && player.canInflictVenom(ptarg)) {
					if (Utility.random(5) == 0 && !ptarg.hasAttribute("venom")) {
						ptarg.inflictVenom();
					}
				} else if (!attackerVenomItems && venomItems && player.canInflictVenom(ptarg)) {
					if (Utility.random(5) == 0 && !ptarg.hasAttribute("venom")) {
						ptarg.inflictVenom();
					}
				}
			}
            
        } else if (target.isNPC()) {
            NPC npc = (NPC) target;
        	//TODO ask Jak why task for npcs is fucked?
        	
        	boolean attackerVenomItems = false;
			boolean venomItems = false;
			
			for (Item equip : player.getEquipment().toArray()) {
				if (equip == null) {
					continue;
				}
				VenomWeapons venomWeapons = VenomWeapons.of(equip.getId());
				if (venomWeapons != null) {
					attackerVenomItems = true;
				}
			}
			
			if(!immune && canTakeVenom(npc)) {
				if (attackerVenomItems && !venomItems) {
					if (Utility.random(5) == 0) {
						npc.inflictVenom();
					}
				}
			}
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
        player.getCombatState().setInCombat(true);
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
                    attacker.take_hit(attacker, selfDamage, CombatStyle.GENERIC);
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
                int shield = defender != null && defender.isPlayer() ? ((Player) defender).getEquipment().get(EquipmentConstants.SHIELD_SLOT).getId() : -1;
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
    	
    	Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		if (weapon == null) {
			player.setCombatType(CombatStyle.MELEE);
			return;
		}
		EquipmentDefinition weaponEquipDef = weapon.getEquipmentDefinition();

		BowType bowType = weaponEquipDef.getBowType();
		RangeWeaponType rangeWeaponType = weaponEquipDef.getRangeWeaponType();
    	
        player.setCombatType(null); // reset
        
        int wep = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
        
        // Spell id set when packet: magic on player
        if (player.getSpellId() > 0 || player.getAutocastId() > 0) {
            player.setCombatType(CombatStyle.MAGIC);
        }
        
        if (wep == 11907) {
            player.setSpellId(52);
            player.setCombatType(CombatStyle.MAGIC);
        }
        if (wep == 12899) {
        	player.setSpellId(53);
            player.setCombatType(CombatStyle.MAGIC);
        }

		// Check if we are using ranged
        if (player.getCombatType() != CombatStyle.MAGIC) {
            if (isCrossBow(bowType) || isBow(bowType) || isHandWeapon(rangeWeaponType) || player.getEquipment().contains(BLOWPIPE)) {
                player.setCombatType(CombatStyle.RANGE);
            }
        }
        // hasn't been set to magic/range.. must be melee.
        if (player.getCombatType() == null) {
            player.setCombatType(CombatStyle.MELEE);
        }
    }

    public static void hitEvent(Entity attacker, Entity target, int delay, Hit hit, CombatStyle combatType) {
    	if (delay < 1) {
    		// Done instantly. Melee npcs damage on players appears instantly.
    		onHitExecuted(attacker, target, delay, hit, combatType);
    	} else {

	        // Schedule a task if delay is >0
	        attacker.run(new Task(delay) {
	            public void execute() {
	                this.stop();
	                onHitExecuted(attacker, target, delay, hit, combatType);
	            }
	        });
    	}
    }

    private static void onHitExecuted(Entity attacker, Entity target, int delay, Hit hit, CombatStyle combatType) {

    	if (attacker.isPlayer() && hit != null)
    		PlayerSounds.sendBlockOrHitSound((Player)attacker, hit.getDamage() > 0);
    	
    	// Apply the damage inside Hit
        if (!(hit.getDamage() == 0 && combatType == CombatStyle.MAGIC && attacker != null && attacker.isPlayer())) {// dont show 0 for splash
        	// Show damage on player. Take_hit has already been used at this point.
            target.damage(hit);
        }

        if (attacker.isPlayer()) {
            // Range attack invoke block emote when hit appears.
            if (hit.cbType == CombatStyle.RANGE && target.getCombatState().getAttackDelay() < 5) {
                final int blockAnim = target.isPlayer() ? WeaponDefinition.sendBlockAnimation(target.asPlayer()) : target.asNpc().getDefendAnimation();
                final Animation a = Animation.create(blockAnim);
                target.playAnimation(a);
            }
        }
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
			Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
			if (weapon != null) {
				if(weapon.getId() == 12926) {
					distance = 6;
				}
				EquipmentDefinition weaponEquipDef = weapon.getEquipmentDefinition();
				if (weaponEquipDef.getBowType() != null) {
					distance = 7;
				} else if (weaponEquipDef.getRangeWeaponType() != null) {
					distance = 4;
				}
			} else {
				distance = 9;
			}
		} else if (usingHalberd(player) && player.getCombatType() == CombatStyle.MELEE) {
			distance = 2;
		} else if (player.getCombatType() == CombatStyle.MAGIC) {
			distance = 10;
		} else if (player.getCombatType() == CombatStyle.MELEE) {
			if (player.getX() != victim.getX() && player.getY() != victim.getY() && player.distanceToPoint(victim.getX(), victim.getY()) < 2) {
				distance = 2;
			} else {
				distance = extraMovingTilesDistance(player);
			}
		}
		if (player.getWalkingQueue().isMoving()) {
			distance += victim.isPlayer() && ((Player)victim).getWalkingQueue().isMoving() ? 3 : 2;
		}
		return distance;
	}

	private static int extraMovingTilesDistance(Player player) {
		if (player.followTarget != null && player.frozen() && !player.getWalkingQueue().isMoving())
			return 2;
		else if (player.followTarget != null && player.frozen() && player.getWalkingQueue().isMoving()) {
			return 3;
		} else {
			return 1;
		}
	}

	/**
     * If you're able to move, it'll re-calculate a path to your target if you're not in range.
     */
	public static boolean touches(Player player, Entity target) {
		if (!target.isPlayer()) {
			NPC npc = (NPC) target;

			boolean ignoreTouches = npc.getName().equalsIgnoreCase("Whirlpool")
					|| npc.getName().equalsIgnoreCase("Zulrah") || npc.getName().equalsIgnoreCase("Portal")
					|| npc.getName().equalsIgnoreCase("Kraken") || npc.getName().equalsIgnoreCase("Spinolyp")
					|| npc.getName().equalsIgnoreCase("Enormous Tentacle");

			if (ignoreTouches && player.getCombatType() != CombatStyle.MELEE)
				return true;
		}
		
        //player.getPlayerFollowing().follow(true, target); // PI =)

        // HYPERION TIME XX
        int walkToData = 0;
        if (target.isNPC()) {
            walkToData = 0x80000000;//can move through entities
        }
        PathFinder.doPath(new VariablePathFinder(-1, walkToData, 0, target.size(), target.size()), player, target.getX(), target.getY());

        // Above - path was executed, now check for line of sight, and distance

        boolean samespot = Location.standingOn(player, target);
        if (!com.venenatis.game.world.pathfinder.impl.ProjectilePathFinder.hasLineOfSight(player, target)) {
            if (!samespot) {
                player.debug("no line of sight");
                return false;
            } else if (samespot) {
                if (player.frozen()) {
                	player.debug("we're frozen");
                    return false;
                }
            }
        }
        if (samespot)
            PlayerFollowing.moveOutFromUnderLargeNpc(player, target);
        // Now pathfinder has updated out path.. check LOS and distance.

        boolean d = player.touchDistance(target, calculateAttackDistance(player, target));
        player.debug("dist: "+d);
		return d;
	}
	
	/**
	 * A list of npcs that cannot take venom damage
	 * 
	 * @param npc
	 *            The npc that can't take venom damage
	 * @return true if we can false otherwise
	 */
	private static boolean canTakeVenom(NPC npc) {
		if (npc.getName().equalsIgnoreCase("Chaos Elemental") || npc.getName().equalsIgnoreCase("Dagannoth Prime")
				|| npc.getName().equalsIgnoreCase("Dagannoth Rex")
				|| npc.getName().equalsIgnoreCase("Dagannoth Supreme")
				|| npc.getName().equalsIgnoreCase("Demonic gorilla") || npc.getName().equalsIgnoreCase("Giant Mole")
				|| npc.getName().equalsIgnoreCase("Kalphite Queen") || npc.getName().equalsIgnoreCase("Kraken")
				|| npc.getName().equalsIgnoreCase("Obor") || npc.getName().equalsIgnoreCase("TzTok-Jad")
				|| npc.getName().equalsIgnoreCase("Abyssal Sire") || npc.getName().contains("Brutal")
				|| npc.getName().equalsIgnoreCase("Cerberus") || npc.getName().equalsIgnoreCase("Commander Zilyana")
				|| npc.getName().equalsIgnoreCase("Corporeal Beast")
				|| npc.getName().equalsIgnoreCase("General Graardor")
				|| npc.getName().equalsIgnoreCase("K'ril Tsutsaroth") || npc.getName().equalsIgnoreCase("Kree'arra")
				|| npc.getName().equalsIgnoreCase("Mithril dragon") || npc.getName().contains("Lizardman")
				|| npc.getName().contains("Snakeling") || npc.getName().equalsIgnoreCase("Smoke devil")
				|| npc.getName().equalsIgnoreCase("Thermonuclear smoke devil")
				|| npc.getName().equalsIgnoreCase("Venenatis") || npc.getName().equalsIgnoreCase("Zulrah"))
			return false;
		return true;
	}
	
	/**
	 * Is the attacker wearing an ava's device?
	 * @return
	 */
	public static boolean wearingAccumulator(Player player) {
		return player.getEquipment().contains(10499);
	}
	
	/**
	 * Is the attacker wearing an attractor device?
	 * @return
	 */
	public static boolean wearingAttractor(Player player) {
		return player.getEquipment().contains(10498);
	}

	public static boolean avas(Player player) {
	    return wearingAccumulator(player) || wearingAttractor(player);
    }
}