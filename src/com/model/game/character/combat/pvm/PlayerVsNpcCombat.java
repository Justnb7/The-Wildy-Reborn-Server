package com.model.game.character.combat.pvm;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.CombatAnimation;
import com.model.game.character.combat.combat_data.CombatData;
import com.model.game.character.combat.combat_data.CombatExperience;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.effect.impl.Venom;
import com.model.game.character.combat.magic.MagicCalculations;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.nvp.NPCCombatData;
import com.model.game.character.combat.range.RangeData;
import com.model.game.character.combat.weaponSpecial.Special;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.PlayerAssistant;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.music.sounds.MobAttackSounds;
import com.model.game.character.player.instances.impl.KrakenInstance;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.walking.PathFinder;
import com.model.game.item.Item;
import com.model.game.location.Position;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

/**
 * Handles Player Vs Npc combat
 * 
 * @author Sanity
 * @author Mobster
 *
 */
public class PlayerVsNpcCombat {

	/**
	 * Applies the combat damage based on the provided {@link CombatType}
	 * 
	 * @param player
	 *            The {@link Player} attacking the npc
	 * @param npc
	 *            The {@link Npc} being attacked
	 * @param type
	 *            The {@link CombatType} of the attack
	 * @param item
	 *            The {@link Item} the player is wearing
	 */
	private static void applyCombatDamage(Player player, Npc npc, CombatType type, Item item, int index) {
		// TODO remove INDEX gtfoo
		if(!npc.infected && player.getEquipment().canInfect(player) && !Venom.venomImmune(npc)){
			if(Utility.getRandom(10) == 5){
				new Venom(npc);
			}
		}
		switch (type) {
		case MAGIC:
			applyNpcMagicDamage(player, npc);
			break;
		case MELEE:
			handleNpcMeleeHit(npc, player, item);
			break;
		case RANGED:
			applyNpcRangeDamage(player, npc, index);
			break;
		default:
			throw new IllegalArgumentException("Invalid Combat Type: " + type);

		}
	}

	/**
	 * Handles the melee hit damage towards an npc
	 * 
	 * @param index
	 *            The index of the npc
	 * @param player
	 *            The {@link Player} applying the melee damage
	 * @param item
	 *            The item the player is wearing
	 */
	public static void handleNpcMeleeHit(Npc npc, final Player player, Item item) {
		applyNpcMeleeDamage(player, npc, 1);
	}
	
	private static boolean isWearingSpear(Player player) {
		String weapon = player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase();
		if (weapon.contains("spear") || weapon.contains("hasta"))
			return true;
		return false;
	}
	
	private static int corporeal_beast_damage(Player player) {
		int damage = Utility.getRandom(player.getCombat().calculateMeleeMaxHit());
		
		if (!isWearingSpear(player)) {
			 damage /= 2;
			 //System.out.println("nope");
		} else {
			damage = (int) damage;
			//System.out.println("wearing spear");
		}
		
		return damage;
	}

	/**
	 * Applies npc melee damage to an npc
	 * 
	 * @param attacker
	 *            The {@link Player} attacking the {@link Npc}
	 * @param npc
	 *            The {@link Npc} thats being attacked
	 * @param damageMask
	 *            The damage mask of the attack
	 */
	public static void applyNpcMeleeDamage(Player attacker, Npc npc, int damageMask) {

		/*
		 * With melee, we need to calculate our damage BEFORE we apply it so you
		 * cannot switch weapons and apply the damage of the switched weapon
		 */
		int damage = damageMask == 1 ? attacker.delayedDamage : damageMask == 2 ? attacker.delayedDamage2 : 0;
		
		CombatExperience.handleCombatExperience(attacker, damage, CombatType.MELEE);
		
		if (npc.npcId == 319) {
			corporeal_beast_damage(attacker);
		}

		if (npc.npcId == 2267 || npc.npcId == 2266) {
			attacker.write(new SendMessagePacket("The dagannoth is currently resistant to that attack!"));
			return;
		}

		if (npc.currentHealth - damage < 0) {
			damage = npc.currentHealth;
		}

		npc.underAttack = true;
		attacker.killingNpcIndex = attacker.npcIndex;
		
		if (damage > 0) {
			npc.addDamageReceived(attacker.getName(), damage);
		}
		npc.damage(new Hit(damage));
	}

	/**
	 * Applies the hit to the npc
	 * 
	 * @param plr_attacker
	 *            The {@link Player} applying the damage
	 * @param victim_npc_id
	 *            The index of the npc
	 * @param item
	 *            Th
	 */
	public static void applyNpcHit(final Player plr_attacker, final int victim_npc_id, Item item) {
		// TODO once again eradicate uses of npc_id .. use ENTITY or MOBILE CHAR instead aka 'entity based' combat
		Npc npc_victim = World.getWorld().getNpcs().get(victim_npc_id);
		
		if (npc_victim == null || npc_victim.isDead || npc_victim.currentHealth <= 0) {
			return;
		}
		
		int defence = npc_victim.getDefinition().getMeleeDefence();
		
		if (defence < 0) {
			defence = 0;
		}

		if (npc_victim.isDead || plr_attacker.isDead() || npc_victim == null) {
			Combat.resetCombat(plr_attacker);
			return;
		}

		npc_victim.walkingHome = false;

		// now this code needs to be in the damaging methods there are a few cos its a bit messy
		if (NPCCombatData.switchesAttackers(npc_victim)) {
			System.out.println("targetId: "+npc_victim.targetId+" index: "+plr_attacker.getIndex());
			npc_victim.targetId = plr_attacker.getIndex();
			npc_victim.faceEntity(npc_victim);
		}//we tried this last time didnt work

		//to kinda narrow it down basicly its not resetting the targetId right?
		// yeah it'd be ok if targetId well this literally makes 0 sence
		// printing as 0 but ingame is 1
		
		
		/*
		 * Apply the damage based on the combat style
		 */
		applyCombatDamage(plr_attacker, npc_victim, plr_attacker.getCombatType(), item, victim_npc_id);
		MobAttackSounds.sendBlockSound(plr_attacker, npc_victim.getId());
		if (plr_attacker.bowSpecShot <= 0) {
			plr_attacker.oldNpcIndex = 0;
			plr_attacker.lastWeaponUsed = 0;
		}
		plr_attacker.bowSpecShot = 0;
	}

	/**
	 * Applys magic damage to the {@link Npc}
	 * 
	 * @param player
	 *            The {@link Player} attacking the npc
	 * @param npc
	 *            The {@link Npc} being attacked
	 */
	private static void applyNpcMagicDamage(Player player, Npc npc) {
		int damage = 0;
		player.usingMagic = true;
        damage = MagicCalculations.magicMaxHitModifier(player);
        
        CombatExperience.handleCombatExperience(player, damage, CombatType.MAGIC);
        
		if (player.getCombat().godSpells()) {
			if (System.currentTimeMillis() - player.godSpellDelay < 300000) {
				damage += Utility.getRandom(10);
			}
		}
		
		kraken(player, npc, damage);
		
		if (npc.npcId == 5535) {
			damage = 0;
		}
		
		boolean magicFailed = false;
		if (!CombatFormulae.getAccuracy(player, npc, 2, 1.0)) {
			damage = 0;
			magicFailed = true;
		} else if (npc.npcId == 2265 || npc.npcId == 2266) {
			player.write(new SendMessagePacket("The dagannoth is currently resistant to that attack!"));
			magicFailed = true;
			return;
		} else if (player.playerEquipment[player.getEquipment().getWeaponId()] == 11907 || player.playerEquipment[player.getEquipment().getWeaponId()] == 12899) {
            Utility.getRandom(npc.getDefinition().getMagicDefence());
		}
		
		if (npc.currentHealth - damage < 0) {
			damage = npc.currentHealth;
		}

		if (player.getCombat().getEndGfxHeight() == 100 && !magicFailed) { // end GFX
			npc.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.oldSpellId][5], 0, 100));
			if (npc.attackTimer < 5)
				npc.playAnimation(Animation.create(NPCCombatData.getNPCBlockAnimation(npc)));
		} else if (!magicFailed) {
			npc.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.oldSpellId][5], 0, 0));
		}

		if (magicFailed) {
			if (npc.attackTimer < 5) {
				npc.playAnimation(Animation.create(NPCCombatData.getNPCBlockAnimation(npc)));
			}
			npc.playGraphics(Graphic.create(85, 0, 100));
		}
		if (!magicFailed) {
			int freezeDelay = player.getCombat().getFreezeTime();// freeze
			if (freezeDelay > 0 && npc.refreezeTicks == 0) {
				npc.freeze(freezeDelay);
				System.out.println("Freeze timer: "+npc.refreezeTicks+ " Freezedelay: "+freezeDelay);
			}
			switch (player.MAGIC_SPELLS[player.oldSpellId][0]) {
			case 12901:
			case 12919: // blood spells
			case 12911:
			case 12929:
				int heal = Utility.getRandom(damage / 2);
				if (player.getSkills().getLevel(Skills.HITPOINTS) + heal >= player.getSkills().getLevelForExperience(Skills.HITPOINTS)) {
					player.getSkills().setLevel(Skills.HITPOINTS, player.getSkills().getLevelForExperience(Skills.HITPOINTS));
				} else {
					player.getSkills().setLevel(Skills.HITPOINTS, player.getSkills().getLevel(Skills.HITPOINTS) + heal);
				}
				break;
			}
		}
		npc.underAttack = true;
		if (MagicCalculations.magicMaxHitModifier(player) != 0) {
			npc.addDamageReceived(player.getName(), damage);
			npc.damage(new Hit(damage));
		}
		player.killingNpcIndex = player.oldNpcIndex;
		npc.updateRequired = true;
		player.usingMagic = false;
		player.castingMagic = false;
		player.oldSpellId = 0;
	}

	
	private static void kraken(Player player, Npc npc, int damage) {
		
		if (npc.npcId == 5534 && npc.transformId != 5535) {
			npc.transforming = true;
			npc.playAnimation(Animation.create(3860));
			npc.requestTransform(5535);
			npc.aggressive = true;
			npc.currentHealth = 120;//reset hp when disturbed
			npc.currentHealth -= damage;

			Server.getTaskScheduler().schedule(new ScheduledTask(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		if (npc.npcId == 496 && npc.transformId != 494) { // small whirlpools of Cave_krakens
			npc.transforming = true;
			npc.playAnimation(Animation.create(7135));
			npc.requestTransform(494);
			npc.aggressive = true;
			npc.currentHealth = 255;//reset hp when disturbed
			npc.currentHealth -= damage;

			Server.getTaskScheduler().schedule(new ScheduledTask(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		
		//Cave kraken - NPCID = 492 // whirlpool (lvl 127) -> 493
		if (npc.npcId == 493 && npc.transformId != 492) { // small whirlpools of Cave_krakens
			npc.transforming = true;
			npc.playAnimation(Animation.create(7135));
			npc.requestTransform(492);
			
			npc.currentHealth = 125;//reset hp when disturbed
			npc.currentHealth -= damage;

			Server.getTaskScheduler().schedule(new ScheduledTask(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		
	}
	
	/**
	 * Applies the npc range damage
	 * 
	 * @param attacker
	 *            The {@link Player} attacking the {@link Npc}
	 * @param victim
	 *            The {@link Npc} being attacked
	 */
	private static void applyNpcRangeDamage(Player attacker, Npc victim, int i) {
		int maxHit = attacker.getCombat().calculateRangeMaxHit();
        int damage = Utility.random(maxHit);
        
        int secondHit = Utility.random(maxHit);

		if (attacker.lastWeaponUsed == 11235 || attacker.bowSpecShot == 1) {
			if (!CombatFormulae.getAccuracy(attacker, victim, 1, 1.0)) {
				secondHit = 0;
			}
			secondHit = maxHit;
		}
        
        //Don't allow players to hit tentactles
        if (victim.npcId == 5535) {
			damage = 0;
		}
        
        if (!attacker.hasAttribute("ignore defence") && !CombatFormulae.getAccuracy(attacker, victim, 1, 1.0)) {
			damage = 0;
		}
        
        //Kraken damage
        kraken(attacker, victim, damage);
        
        //Rex and Supreme do not take range damage
        if (victim.npcId == 2265 || victim.npcId == 2267 && !attacker.hasAttribute("ignore defence")) {
			attacker.write(new SendMessagePacket("The dagannoth is currently resistant to that attack!"));
			return;
		}
        
        //Crossbow damage multiplier
        if (attacker.getEquipment().isCrossbow(attacker)) {
			if (Utility.getRandom(8) == 1) {
				if (damage > 0) {
					switch(attacker.playerEquipment[attacker.getEquipment().getQuiverId()]) {
					case 9236: // Lucky Lightning
						victim.playGraphics(Graphic.create(749, 0, 0));
						break;
					case 9237: // Earth's Fury
						victim.playGraphics(Graphic.create(755, 0, 0));
						break;
					case 9238: // Sea Curse
						victim.playGraphics(Graphic.create(750, 0, 0));
						break;
					case 9239: // Down to Earth
						victim.playGraphics(Graphic.create(757, 0, 0));
						break;
					case 9240: // Clear Mind
						victim.playGraphics(Graphic.create(751, 0, 0));
						break;
					case 9241: // Magical Posion
						victim.playGraphics(Graphic.create(752, 0, 0));
						break;
					case 9242: // Blood Forfiet
						int damageCap = (int) (victim.currentHealth * 0.2);
						if (victim.npcId == 319 && damageCap > 100)
							damageCap = 100;
						victim.damage(new Hit(damageCap));
						victim.playGraphics(Graphic.create(754, 0, 0));
						break;
					case 9243: // Armour Piercing
						victim.playGraphics(Graphic.create(758, 0, 100));
						victim.setAttribute("ignore defence", true);
						if (CombatFormulae.wearingFullVoid(attacker, 2)) {
							damage = Utility.random(45, 57);
						} else {
							damage = Utility.random(42, 51);
						}
						if (attacker.isActivePrayer(Prayers.EAGLE_EYE)) {
							damage *= 1.15;
						}
						break;
					case 9244: // Dragon's Breath
						victim.playGraphics(Graphic.create(756, 0, 0));
						if (CombatFormulae.wearingFullVoid(attacker, 2)) {
							damage = Utility.random(45, 57);
						} else {
							damage = Utility.random(42, 51);
						}
						if (attacker.isActivePrayer(Prayers.EAGLE_EYE)) {
							damage *= 1.15;
						}
						break;
					case 9245: // Life Leech
						victim.playGraphics(Graphic.create(753, 0, 0));
						if (CombatFormulae.wearingFullVoid(attacker, 2)) {
							damage = Utility.random(45, 57);
						} else {
							damage = Utility.random(42, 51);
						}
						if (attacker.isActivePrayer(Prayers.EAGLE_EYE)) {
							damage *= 1.15;
						}
						break;
					}
				}
			}
		}
        
        //Damage check
        if (victim.currentHealth - damage < 0) {
			damage = victim.currentHealth;
		}
        
        if (victim.currentHealth - secondHit < 0) {
        	secondHit = victim.currentHealth;
		}
		
		//Arrows check
		boolean dropArrows = true;
		if(attacker.lastWeaponUsed == 12926 || attacker.lastWeaponUsed == 4222) {
			dropArrows = false;
		}
		
		if (dropArrows) {
			attacker.getItems().dropArrowNpc();
			attacker.getItems().deleteArrow();
			if (attacker.playerEquipment[3] == 11235) {
				attacker.getItems().dropArrowNpc();
			}
		}
		
        if (victim.attackTimer < 5)
			victim.playAnimation(Animation.create(NPCCombatData.getNPCBlockAnimation(victim)));
			attacker.rangeEndGFX = RangeData.getRangeEndGFX(attacker);
		
			
			victim.underAttack = true;
			victim.addDamageReceived(attacker.getName(), damage);
			victim.damage(new Hit(damage));
			
			if(damage > 0) {
				CombatExperience.handleCombatExperience(attacker, damage, CombatType.RANGED);
			}
			if (attacker.lastWeaponUsed == 11235) {
				victim.addDamageReceived(attacker.getName(), secondHit);
				victim.damage(new Hit(secondHit));
				if(secondHit > 0) {
					CombatExperience.handleCombatExperience(attacker, damage, CombatType.RANGED);
				}
			}
			
			attacker.setAttribute("ignore defence", false);
			
			if (attacker.rangeEndGFX > 0) {
				if (attacker.rangeEndGFXHeight) {
					victim.playGraphics(Graphic.create(attacker.rangeEndGFX, 0, 100));
				} else {
					victim.playGraphics(Graphic.create(attacker.rangeEndGFX, 0, 0));
				}
			}
			attacker.killingNpcIndex = attacker.oldNpcIndex;
			
	}

	/**
	 * Starts the attack on an {@link Npc}
	 * 
	 * @param player
	 *            The {@link Player} attacking the npc
	 * @param index
	 *            The index of the npc
	 */
	public static void attackNpc(Player player, int index) {
		Npc npc = World.getWorld().getNpcs().get(index);
		if (npc == null) {
			//System.out.println("null");
			return;
		}
		
		if (player.inTutorial()) {
			//System.out.println("tutorial");
			return;
		}
		
		if (npc.transforming) {
			//System.out.println("transform");
			return;
		}
		
		if ((npc.npcId == 6611 || npc.npcId == 6612) && npc.dogs > 0) {
			player.npcIndex = 0;
			Combat.resetCombat(player);
			player.write(new SendMessagePacket("You must vanquish Vet'ions dogs."));
			return;
		}
		
		if (npc.npcId == 496 && npc.transformId != 494) {
			KrakenInstance i = player.getKraken();
			if (i != null && i.npcs != null && i.npcs[0] == npc) {
				for (Npc n : i.npcs) {
					if (n.npcId == 5534) {
						player.write(new SendMessagePacket("You can't disturb the kraken while the whirlpools are undisturbed."));
						Combat.resetCombat(player);
						return;
					}
				}
			}
		}

		if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
			Combat.resetCombat(player);
			player.write(new SendMessagePacket("You cannot attack that npc from outside the room."));
			return;
		}
		
		if (npc.npcId != 493 || npc.npcId != 496 || npc.npcId != 5534) {
			Player attacker = World.getWorld().PLAYERS.get(npc.underAttackBy);
			//System.out.println(Npc.getName(npc.npcType).replaceAll("_", " ") + " - "+ npc.maximumHealth +" - "+ npc.HP +" - "+ ((attacker != null) ? "-"+attacker.getUsername() : "null"));
			player.getActionSender().sendString(Npc.getName(npc.npcId).replaceAll("_", " ")+ "-"+npc.maximumHealth+"-"+npc.currentHealth+ ((attacker != null) ? "-"+attacker.getName() : ""), 35000);
		}
		if (!validateAttack(player, npc, true)) {
			//System.out.println("blocked");
			//player.write(new SendGameMessage("blocked");
			return;
		}
		if (npc.isDead || npc.maximumHealth <= 0 || player.isDead()) {
			player.usingMagic = false;
			player.faceEntity(player);
			player.npcIndex = 0;
			return;
		}
        
		if (npc.underAttackBy > 0 && npc.underAttackBy != player.getIndex() && !npc.inMulti()) {
			player.npcIndex = 0;
			player.write(new SendMessagePacket("This monster is already in combat."));
			return;
		}
		
		if ((player.underAttackBy > 0 || player.underAttackBy2 > 0) && player.underAttackBy2 != npc.getIndex() && !player.getArea().inMulti() && !Boundary.isIn(player, Boundary.KRAKEN)) {
			Combat.resetCombat(player);
			player.write(new SendMessagePacket("I am already under attack."));
			return;
		}

		if (!player.getCombat().goodSlayer(npc.npcId)) {
			Combat.resetCombat(player);
			return;
		}
		if (npc.spawnedBy != player.getIndex() && npc.spawnedBy > 0) {
			Combat.resetCombat(player);
			player.write(new SendMessagePacket("This monster was not spawned for you."));
			return;
		}
		
		if (!player.getController().canAttackNPC(player)) {
			//System.out.println("blocked");
			return;
		}
		
		if (player.getCombatType() != CombatType.MAGIC) {
			player.usingBow = player.getEquipment().isBow(player);
			player.throwingAxe = player.getEquipment().isThrowingWeapon(player);
			player.usingCross = player.getEquipment().isCrossbow(player);
			player.usingArrows = player.getEquipment().isArrow(player);
			boolean bolt = player.getEquipment().isBolt(player);
			boolean javalin = player.getCombat().properJavalins();
			boolean blowpipe = player.getEquipment().wearingBlowpipe(player);
			
			if(player.throwingAxe || player.usingCross || player.usingBow || player.getEquipment().wearingBallista(player) || player.getEquipment().wearingBlowpipe(player)) {
				player.setCombatType(CombatType.RANGED);
			}
			
			if(player.throwingAxe) {
				player.throwingAxe = true;
			}
			
			if(!blowpipe && (bolt || javalin || player.usingArrows)) {
				player.usingArrows = true;
			}
		}
		
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

		if (player.getX() == npc.getX() && player.getY() == npc.getY()) {
			player.getPA().walkTo(0, 1);
		}

		/*
		 * Reset our following to make sure we are following the npc
		 */
		player.followId2 = index;
		player.followId = 0;
		boolean inside = false;
		boolean projectiles = player.usingBow || player.usingMagic || player.throwingAxe
				|| player.getCombatType() == CombatType.RANGED || player.getCombatType() == CombatType.MAGIC;
		for (Position tile : npc.getTiles()) {
			if (player.absX == tile.getX() && player.absY == tile.getY()) {
				inside = true;
				break;
			}
		}

		if (inside) {
			double lowDist = 99;
			int lowX = 0;
			int lowY = 0;
			int z = npc.heightLevel;
			int x2 = npc.getX();
			int y2 = npc.getY();
			int x3 = x2;
			int y3 = y2 - 1;
			boolean ignoreClip = npc.getId() == 494 || npc.getId() == 5535 || npc.getId() == 5534 || npc.getId() == 492 || npc.getId() == 493 || npc.getId() == 496;

			for (int k = 0; k < 4; k++) {
				for (int i = 0; i < npc.getSize() - (k == 0 ? 1 : 0); i++) {
					if (k == 0) {
						x3++;
					} else if (k == 1) {
						if (i == 0) {
							x3++;
						}
						y3++;
					} else if (k == 2) {
						if (i == 0) {
							y3++;
						}
						x3--;
					} else if (k == 3) {
						if (i == 0) {
							x3--;
						}
						y3--;
					}

					Position location = new Position(x3, y3, z);
					double d = location.distance(player.getPosition());
					if (d < lowDist) {
						if (ignoreClip || !projectiles || projectiles
								&& ProjectilePathFinder.isProjectilePathClear(location, npc.getPosition())) {
							if (ignoreClip || projectiles || !projectiles
									&& ProjectilePathFinder.isInteractionPathClear(location, npc.getPosition())) {
								lowDist = d;
								lowX = x3;
								lowY = y3;
							}
						}
					}
				}
			}

			if (lowX > 0 && lowY > 0) {
				player.getPA().playerWalk(lowX, lowY);
			}
			/*
			 * int r = Misc.random(3); switch (r) { case 0:
			 * player.getPA().walkTo(0, -1); return; case 1:
			 * player.getPA().walkTo(0, 1); return; case 2:
			 * player.getPA().walkTo(1, 0); return; case 3:
			 * player.getPA().walkTo(-1, 0); return; }
			 */

		}

		if (player.attackDelay > 0) {

			// our attack timer hasnt reached 0 so we cannot attack yet
			return;
		}

		/*
		 * Reset our combat values so we can determine our attack style again
		 */
		// all this shit
		player.usingBow = player.usingArrows = player.throwingAxe = false;
		player.usingCross = player.getEquipment().isCrossbow(player);
		player.rangeItemUsed = 0;
		player.delayedDamage = player.delayedDamage2 = 0;
		player.setCombatType(player.usingCross ? CombatType.RANGED : CombatType.MELEE);

		
		if (player.getSpellId() > 0 || player.playerEquipment[player.getEquipment().getWeaponId()] == 11907 || player.playerEquipment[player.getEquipment().getWeaponId()] == 12899) {
			player.usingMagic = true;
			player.setCombatType(CombatType.MAGIC);
		}
		/**
		 * I did this as a temp fix, not sure if it's appropriate. :: XXX
		 */
		if (player.usingMagic) {
			player.setCombatType(CombatType.MAGIC);
		}

		/*
		 * Set our attack timer again so we don't attack the npc right away
		 */
		player.attackDelay = CombatData.getAttackDelay(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase());

		/*
		 * if we aren't using magic, check if we are using a bow
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

		// if (!inside) {
		boolean hasDistance = npc.npcId == 5535 ? true : false; // force 5535 tents to always be hittable
		for (Position pos : npc.getTiles()) {
			double distance = pos.distance(player.getPosition());
			boolean magic = player.usingMagic;
			boolean ranged = !player.usingMagic
					&& (player.throwingAxe || player.usingBow || player.usingCross || player.usingArrows);
			boolean melee = !magic && !ranged;
			if(CombatData.usingHalberd(player)) {
				if(distance <= 2) {
					hasDistance = true;
					break;
				}
			}
			if (melee) {
				if (distance <= 1) {
					hasDistance = true;
					break;
				}
			} else {
				if (distance <= (ranged ? 10 : 15)) {
					hasDistance = true;
					break;
				}
			}
		}

		if (hasDistance) {
			player.stopMovement();
		} else {
			player.attackDelay = 1;
			//player.write(new SendGameMessage("No fucking distance?");
			return;
		}

		if (npc.getSize() == 1) {
			if (player.getX() != npc.getX() && npc.getY() != player.getY()
					&& player.getCombatType() == CombatType.MELEE) {
				PlayerAssistant.stopDiagonal(player, npc.getX(), npc.getY());
				return;
			}
		}

		if (player.getCombatType() == CombatType.MAGIC || player.getCombatType() == CombatType.RANGED) {
			player.stopMovement();
		}
		
		//player.getCombat().checkVenomousItems();
		
		if (NPCHandler.isArmadylNpc(npc.getIndex()) && player.getCombatType() == CombatType.MELEE) {
			player.write(new SendMessagePacket("You can only use range against this."));
			Combat.resetCombat(player);
			return;
		}

		/*
		 * Check for the correct range requirements
		 */
		if (player.getCombatType() == CombatType.RANGED) {
			if (!player.usingCross
					&& !player.throwingAxe
					&& !player.usingArrows
					&& (player.playerEquipment[player.getEquipment().getWeaponId()] < 4212 || player.playerEquipment[player.getEquipment().getWeaponId()] > 4223)
					&& player.playerEquipment[player.getEquipment().getWeaponId()] != 12926) {
				player.write(new SendMessagePacket("There is no ammo left in your quiver."));
				player.stopMovement();
				player.npcIndex = 0;
				return;
			}

			if (player.getCombat().correctBowAndArrows() < player.playerEquipment[player.getEquipment().getQuiverId()] 
					&& player.usingBow
					&& !player.getEquipment().usingCrystalBow(player)
					&& !player.getEquipment().isCrossbow(player)) {
				player.write(new SendMessagePacket("You can't use " + player.getItems().getItemName(player.playerEquipment[player.getEquipment().getQuiverId()]).toLowerCase() + "s with a " + player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase() + "."));
				player.stopMovement();
				player.npcIndex = 0;
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
		

		if (player.getCombatType() == CombatType.MAGIC) {
			if (!player.getCombat().checkMagicReqs(player.getSpellId())) {
				player.stopMovement();
				player.npcIndex = 0;
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

		player.faceEntity(npc);
		npc.underAttackBy = player.getIndex();
		npc.lastDamageTaken = System.currentTimeMillis();
		
		if (player.isUsingSpecial()) {
			player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
			Special.handleSpecialAttack(player, npc);
			return;
		}
		// All code below here needs to also be added into handleSpecialAttack. 
		
		if (player.getSkills().getLevel(Skills.HITPOINTS) > 0 && !player.isDead && npc.maximumHealth > 0) {
			//Play block animation. Magic attacks don't cause the victim to do an animation.
			if (!player.usingMagic) {
				player.playAnimation(Animation.create(CombatAnimation.getAttackAnimation(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase())));
				if (npc.attackTimer > 3) {
					if (npc.npcId != 2042 && npc.npcId != 2043 & npc.npcId != 2044 && npc.npcId != 3127) {
						npc.playAnimation(Animation.create(NPCCombatData.getNPCBlockAnimation(npc)));
					}
				}
			} else {
				// magic spell block anims
				player.playAnimation(Animation.create(player.MAGIC_SPELLS[player.getSpellId()][2]));
			}
		}
		player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
		player.hitDelay = CombatData.getHitDelay(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase());

		/*
		 * Set our calculations for our combat style
		 */
		if (player.getCombatType() == CombatType.MELEE) { // melee
			player.followId2 = npc.getIndex(); // another example of shit code.. use entity.follow(target) or somet 
			player.getPA().followNpc(); // adjust walking queue in direction of target
			player.delayedDamage = Utility.getRandom(player.getCombat().calculateMeleeMaxHit()); // same junk
			player.delayedDamage2 = Utility.getRandom(player.getCombat().calculateMeleeMaxHit());
			player.oldNpcIndex = index;
		} else if (player.getCombatType() == CombatType.RANGED && !player.throwingAxe) { // range
			if (player.usingCross)
				player.usingBow = true;
			if (player.getAttackStyle() == 2)
				player.attackDelay--;
			player.followId2 = npc.getIndex();
			player.getPA().followNpc();
			player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
			player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
			player.oldNpcIndex = index;
			if (player.playerEquipment[player.getEquipment().getWeaponId()] >= 4212 && player.playerEquipment[player.getEquipment().getWeaponId()] <= 4223) {
				player.rangeItemUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
				player.getCombat().fireProjectileNpc();
			} else {
				player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
				if (player.playerEquipment[3] == 11235 || player.playerEquipment[3] == 12765 || player.playerEquipment[3] == 12766
						|| player.playerEquipment[3] == 12767 || player.playerEquipment[3] == 12768) {
					player.getItems().deleteArrow();
				}
				if (npc.currentHealth > 0) {
					player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
					player.getCombat().fireProjectileNpc();
				}
			}
		} else if (player.throwingAxe && player.getCombatType() == CombatType.RANGED) {
			player.followId2 = npc.getIndex();
			player.getPA().followNpc();
			player.rangeItemUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
			if (player.playerEquipment[3] == 21000) {
				player.getItems().removeEquipment();
			} else {
				player.getItems().deleteEquipment(); // here
			}
			player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
			player.oldNpcIndex = index;
			if (player.getAttackStyle() == 2)
				player.attackDelay--;
			player.getCombat().fireProjectileNpc();
		} else if (player.getCombatType() == CombatType.MAGIC) { // magic hit
																	// delay
			int pX = player.getX();
			int pY = player.getY();
			int nX = npc.getX();
			int nY = npc.getY();
			int offX = (pY - nY) * -1;
			int offY = (pX - nX) * -1;
			player.castingMagic = true;
			player.setCombatType(CombatType.MAGIC);
			player.stopMovement();
			if (player.MAGIC_SPELLS[player.getSpellId()][3] > 0) {
				if (player.getCombat().getStartGfxHeight() == 100) {
					player.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.getSpellId()][3], 0, 0));
				} else {
					player.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.getSpellId()][3], 0, 0));
				}
			}
			if (player.MAGIC_SPELLS[player.getSpellId()][4] > 0) {
				player.getProjectile().createPlayersProjectile(pX, pY, offX, offY, 50, 78,
						player.MAGIC_SPELLS[player.getSpellId()][4], player.getCombat().getStartHeight(),
						player.getCombat().getEndHeight(), index + 1, 50);
			}
			player.oldNpcIndex = index;
			player.oldSpellId = player.getSpellId();
			if (player.playerEquipment[player.getEquipment().getWeaponId()] == 11907 || player.playerEquipment[player.getEquipment().getWeaponId()] == 12899) {
				return;
			}
			
			player.setSpellId(0);
			if (!player.autoCast)
				player.npcIndex = 0;
		}

	}

	/**
	 * Validates if the {@link Player} can attack the {@link Npc}
	 * 
	 * @param player
	 *            The {@link Player} attacking the npc
	 * @param npc
	 *            The {@link Npc} which is being attacked
	 * @return If the player can attack the npc
	 */
	public static boolean validateAttack(Player player, Npc npc, boolean findpath) {
		boolean ignoreClip = npc.getId() == 494 || npc.getId() == 492 || npc.getId() == 493 || npc.getId() == 496 || npc.getId() == 5534 || npc.getId() == 5535 || npc.getId() == 2054 || npc.getId() == 5947;
		if (ignoreClip)
			return true;
		boolean projectile = player.getCombatType() == CombatType.RANGED || player.getCombatType() == CombatType.MAGIC;
		if (projectile) {
			for (Position pos : npc.getBorder()) {
				if (ProjectilePathFinder.isProjectilePathClear(player.getPosition(), pos)) {
					return true;
				}
			}
		} else {
			for (Position pos : npc.getBorder()) {
				if (ProjectilePathFinder.isInteractionPathClear(player.getPosition(), pos)) {
					//player.write(new SendGameMessage("debug");
					return true;
				}
			}
		}

		if (findpath) {
			PathFinder.getPathFinder().findRoute(player, npc.absX, npc.absY, true, 1, 1);
		}
		//player.write(new SendGameMessage("debug");
		return false;
	}
}