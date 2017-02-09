package com.model.game.character.combat.pvm;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulas;
import com.model.game.character.combat.combat_data.CombatAnimation;
import com.model.game.character.combat.combat_data.CombatData;
import com.model.game.character.combat.combat_data.CombatExperience;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.effect.CombatEffect;
import com.model.game.character.combat.effect.impl.Venom;
import com.model.game.character.combat.magic.MagicCalculations;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.nvp.NpcVsPlayerCombat;
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
import com.model.game.character.player.packets.encode.impl.SendString;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.walking.PathFinder;
import com.model.game.item.Item;
import com.model.game.location.Location;
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
	
	public static int salveDamage(Player player) {
		int damage = Utility.getRandom(player.getCombat().calculateMeleeMaxHit());
		return damage *= 1.15;
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

		// also shit using getNpcs().get MULTIPLE TIMES in the same method is just stressing the game
		// loop 2k online players 5 times in the same method = lag lag lag 
		npc.underAttack = true;
		attacker.killingNpcIndex = attacker.npcIndex;
		attacker.lastNpcAttacked = npc.getIndex(); // PI junk
		
		if (damage > 0) {
			npc.addDamageReceived(attacker.getRealUsername(), damage);
		}
		npc.damage(new Hit(damage));
	}

	/**
	 * Applies npc melee damage to an npc
	 * 
	 * @param player
	 *            The {@link Player} attacking the {@link Npc}
	 * @param npc
	 *            The {@link Npc} thats being attacked
	 * @param damageMask
	 *            The damage mask of the attack
	 * @param damage 
	 * 			  The damage to deal
	 */
	public static void applyNpcMeleeDamage(Player player, Npc npc, int damageMask, int[] damages) {

		if (World.getWorld().getNpcs().get(npc.getIndex()).npcId == 2267 || World.getWorld().getNpcs().get(npc.getIndex()).npcId == 2266 && player.playerEquipment[3] == 14484) {
			player.write(new SendMessagePacket("All your hits missed because this npc is irresistible to melee attacks."));
			return;
		}
		
		Hit[] hits = new Hit[damages.length];
		@SuppressWarnings("unused")
		int total = 0;
		for (int i = 0; i < hits.length; i++) {
			hits[i] = new Hit(damages[i]);
			total += damages[i];
		}
		World.getWorld().getNpcs().get(npc.getIndex()).damage(hits);
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
		
		CombatType style = !plr_attacker.castingMagic && plr_attacker.projectileStage > 0 ? CombatType.RANGED : plr_attacker.projectileStage > 0 ? CombatType.MAGIC : CombatType.MELEE; 
		
		int defence = npc_victim.getDefinition().getMeleeDefence() + getBonusDefence(plr_attacker, npc_victim, style);
		
		if (defence < 0) {
			defence = 0;
		}

		if (npc_victim.isDead || plr_attacker.isDead() || npc_victim == null) {
			Combat.resetCombat(plr_attacker);
			return;
		}

		npc_victim.walkingHome = false;

		if (npc_victim.underAttackBy > 0 && Server.npcHandler.getsPulled(npc_victim)) {
			npc_victim.killerId = plr_attacker.getIndex();
		} else if (npc_victim.underAttackBy < 0 && !Server.npcHandler.getsPulled(npc_victim)) {
			npc_victim.killerId = plr_attacker.getIndex();
		}

		plr_attacker.lastNpcAttacked = victim_npc_id;

		/*
		 * Apply the damage based on the combat style
		 */
		applyCombatDamage(plr_attacker, npc_victim, plr_attacker.getCombatType(), item, victim_npc_id);
		// TODO are sounds done when the attack starts or when damage appears?
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
        CombatEffect.slayerHelmetEffect(player, npc, damage);
        
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
		if (!CombatFormulas.getAccuracy(player, npc, 2, 1.0)) {
			damage = 0;
			magicFailed = true;
		} else if (npc.npcId == 2265 || npc.npcId == 2266) {
			player.write(new SendMessagePacket("The dagannoth is currently resistant to that attack!"));
			magicFailed = true;
			return;
		} else if (player.playerEquipment[player.getEquipment().getWeaponId()] == 11907 || player.playerEquipment[player.getEquipment().getWeaponId()] == 12899) {
            Utility.getRandom(npc.getDefinition().getMagicDefence());
		}
		for (Npc n : World.getWorld().getNpcs()) {
			if (n != null && n.maximumHealth > 0) {
				int nX = n.getX();
				int nY = n.getY();
				int pX = npc.getX();
				int pY = npc.getY();
				if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1) && (nY - pY == -1 || nY - pY == 0 || nY - pY == 1)) {
					if (player.getCombat().multis() && npc.inMulti()) {
						World.getWorld();
						Player p = World.getWorld().getPlayers().get(player.getIndex());
						player.getCombat().appendMultiBarrageNPC(n.getIndex(), player.magicFailed);
						NpcVsPlayerCombat.attackPlayer(p, n);
					}
				}
			}
		}
		if (npc.currentHealth - damage < 0) {
			damage = npc.currentHealth;
		}

		if (player.getCombat().getEndGfxHeight() == 100 && !magicFailed) { // end GFX
			npc.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.oldSpellId][5], 0, 100));
			if (npc.attackTimer < 5)
				npc.playAnimation(Animation.create(CombatAnimation.getNPCBlockAnimation(npc.getIndex())));
		} else if (!magicFailed) {
			npc.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.oldSpellId][5], 0, 0));
		}

		if (magicFailed) {
			if (npc.attackTimer < 5) {
				npc.playAnimation(Animation.create(CombatAnimation.getNPCBlockAnimation(npc.getIndex())));
			}
			npc.playGraphics(Graphic.create(85, 0, 100));
		}
		if (!magicFailed) {
			int freezeDelay = player.getCombat().getFreezeTime();// freeze
			if (freezeDelay > 0 && npc.freezeTimer == 0) {
				npc.freezeTimer = freezeDelay;
				//player.write(new SendGameMessage("Freeze timer: "+npc.freezeTimer+ " Freezedelay: "+freezeDelay));
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
					player.getSkills().setLevel(Skills.HITPOINTS, + heal);
				}
				break;
			}
		}
		npc.underAttack = true;
		if (MagicCalculations.magicMaxHitModifier(player) != 0) {
			if (!player.multiAttacking) {
				npc.addDamageReceived(player.getRealUsername(), damage);
				npc.damage(new Hit(damage));
			}
		}
		player.multiAttacking = false;
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
	 * @param player
	 *            The {@link Player} attacking the {@link Npc}
	 * @param npc
	 *            The {@link Npc} being attacked
	 */
	private static void applyNpcRangeDamage(Player player, Npc npc, int i) {
		//int damage = Utils.getRandom(player.getCombat().rangeMaxHit());
		int damage = Utility.getRandom(player.getCombat().calculateRangeMaxHit());
		int damage2 = -1;
		
		CombatExperience.handleCombatExperience(player, damage, CombatType.RANGED);
		CombatEffect.slayerHelmetEffect(player, npc, damage);

		if (player.lastWeaponUsed == 11235 || player.bowSpecShot == 1)
		    damage2 = player.getCombat().calculateRangeMaxHit();
		if (!player.ignoreDefence && !CombatFormulas.getAccuracy(player, npc, 1, 1.0)) {
			damage = 0;
		} else if (npc.npcId == 2265 || npc.npcId == 2267 && !player.ignoreDefence) {
			player.write(new SendMessagePacket("The dagannoth is currently resistant to that attack!"));
			return;
		}
		if (npc.npcId == 5535) {
			damage = 0;
		}
		
		kraken(player, npc, damage);

		if (player.lastWeaponUsed == 11235 || player.bowSpecShot == 1) {
			if (!CombatFormulas.getAccuracy(player, npc, 1, 1.0))
				damage2 = 0;
		}
		
		if (player.dbowSpec) {
			npc.playGraphics(Graphic.create(player.lastArrowUsed == 11212 ? 1100 : 1103, 0, 100));
			if (damage < 8)
				damage = 8;
			if (damage2 < 8)
				damage2 = 8;
			player.dbowSpec = false;
		}
		
		if (player.playerEquipment[3] == 9185 || player.playerEquipment[3] == 11785 || player.playerEquipment[3] == 18357) {
			if (Utility.getRandom(8) == 1) {
				if (damage > 0) {
					player.boltDamage = damage;
					player.getCombat().crossbowSpecial(player, npc.getIndex());
					damage *= player.crossbowDamage;
				}
			}
		}
		if (npc.currentHealth - damage < 0) {
			damage = npc.currentHealth;
		}
		if (damage2 > 0) {
			if (damage == npc.currentHealth && npc.currentHealth - damage2 > 0) {
				damage2 = 0;
			}
		}

		boolean dropArrows = true;
		for (int noArrowId : Player.NO_ARROW_DROP) {
			if (player.lastWeaponUsed == noArrowId) {
				dropArrows = false;
				break;
			}
		}
		if (dropArrows) {
			player.getItems().dropArrowNpc();
			if (player.playerEquipment[3] == 11235) {
				player.getItems().dropArrowNpc();
			}
		}
		if (npc.attackTimer < 5)
			npc.playAnimation(Animation.create(CombatAnimation.getNPCBlockAnimation(npc.getIndex())));
		player.rangeEndGFX = RangeData.getRangeEndGFX(player);
		if ((player.playerEquipment[3] == 10034 || player.playerEquipment[3] == 10033)) {
			for (Npc n : World.getWorld().getNpcs()) {
				if (n != null && n.maximumHealth > 0) {
					int nX = n.getX();
					int nY = n.getY();
					int pX = npc.getX();
					int pY = npc.getY();
					if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1)
							&& (nY - pY == -1 || nY - pY == 0 || nY - pY == 1)) {
						if (npc.inMulti()) {
							Player p = World.getWorld().getPlayers().get(player.getIndex());
							player.getCombat().appendMutliChinchompa(n.getIndex());
							NpcVsPlayerCombat.attackPlayer(p, n);
						}
					}
				}
			}
		}

		if (!player.multiAttacking) {
			npc.underAttack = true;
			npc.addDamageReceived(player.getRealUsername(), damage);
			npc.damage(new Hit(damage));
			if (damage2 > -1) {
				CombatExperience.handleCombatExperience(player, damage2, CombatType.RANGED);
				npc.addDamageReceived(player.getRealUsername(), damage2);
				npc.damage(new Hit(damage2));
			}
		}
		player.ignoreDefence = false;
		player.multiAttacking = false;
		if (player.rangeEndGFX > 0) {
			if (player.rangeEndGFXHeight) {
				npc.playGraphics(Graphic.create(player.rangeEndGFX, 0, 100));
			} else {
				npc.playGraphics(Graphic.create(player.rangeEndGFX, 0, 0));
			}
		}
		player.killingNpcIndex = player.oldNpcIndex;
	}
	
	private static int getBonusDefence(Player player, Npc npc, CombatType type) {
		if (type.equals(CombatType.MELEE)) {
			
		} else if (type.equals(CombatType.MAGIC)) {
			switch (npc.npcId) {
				case 2042:
					return -100; 
				case 2044:
					return +100;
			}
		} else if (type.equals(CombatType.RANGED)) {
			switch (npc.npcId) {
				case 2042:
					return 100; 
				case 2044:
					return -100;
			}
		}
		return 0;
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
			return;
		}
		
		if (player.inTutorial()) {
			return;
		}
		
		if (npc.transforming) {
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
			player.write(new SendString(Npc.getName(npc.npcId).replaceAll("_", " ")+ "-"+npc.maximumHealth+"-"+npc.currentHealth+ ((attacker != null) ? "-"+attacker.getName() : ""), 35000));
		}
		if (!validateAttack(player, npc, true)) {
			//player.write(new SendGameMessage("blocked");
			return;
		}
		if (npc.isDead || npc.maximumHealth <= 0 || player.isDead()) {
			player.usingMagic = false;
			player.faceUpdate(0);
			player.npcIndex = 0;
			player.write(new SendMessagePacket("You cannot attack this NPC."));
			return;
		}
		
		if (player.getBankPin().requiresUnlock()) {
			player.write(new SendMessagePacket("You cannot attack this NPC while you haven't entered your PIN."));
			Combat.resetCombat(player);
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
			return;
		}
		
		if (player.getCombatType() != CombatType.MAGIC) {
			for (int bowId : Player.BOWS) {
				if (player.playerEquipment[player.getEquipment().getWeaponId()] == bowId && player.switchDelay.elapsed(100)) {
					player.usingBow = true;
					player.setCombatType(CombatType.RANGED);
					for (int arrowId : Player.ARROWS) {
						if (player.playerEquipment[player.getEquipment().getQuiverId()] == arrowId) {
							player.usingArrows = true;
						}
					}
				}
			}
			for (int arrowId : Player.ARROWS) {
				if (player.playerEquipment[player.getEquipment().getQuiverId()] == arrowId) {
					player.usingArrows = true;
					break;
				}
			}

			for (int otherRangeId : Player.OTHER_RANGE_WEAPONS) {
				if (player.playerEquipment[player.getEquipment().getWeaponId()] == otherRangeId) {
					player.usingOtherRangeWeapons = true;
					player.setCombatType(CombatType.RANGED);
					break;
				}
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
		boolean projectiles = player.usingRangeWeapon || player.usingMagic || player.usingOtherRangeWeapons
				|| player.getCombatType() == CombatType.RANGED || player.getCombatType() == CombatType.MAGIC;
		for (Location tile : npc.getTiles()) {
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

					Location location = new Location(x3, y3, z);
					double d = location.distance(player.getLocation());
					if (d < lowDist) {
						if (ignoreClip || !projectiles || projectiles
								&& ProjectilePathFinder.isProjectilePathClear(location, npc.getLocation())) {
							if (ignoreClip || projectiles || !projectiles
									&& ProjectilePathFinder.isInteractionPathClear(location, npc.getLocation())) {
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
		player.usingBow = player.usingArrows = player.usingOtherRangeWeapons = false;
		player.usingCross = player.playerEquipment[player.getEquipment().getWeaponId()] == 9185
				|| player.playerEquipment[player.getEquipment().getWeaponId()] == 11785 || player.playerEquipment[player.getEquipment().getWeaponId()] == 18357;
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
			for (int bowId : Player.BOWS) {
				if (player.playerEquipment[player.getEquipment().getWeaponId()] == bowId) {
					player.usingBow = true;
					player.setCombatType(CombatType.RANGED);
					break;
				}
			}

			if (player.usingBow) {
				for (int arrowId : Player.ARROWS) {
					if (player.playerEquipment[player.getEquipment().getQuiverId()] == arrowId) {
						player.usingArrows = true;
						break;
					}
				}
			}

			for (int otherRangeId : Player.OTHER_RANGE_WEAPONS) {
				if (player.playerEquipment[player.getEquipment().getWeaponId()] == otherRangeId) {
					player.usingOtherRangeWeapons = true;
					player.setCombatType(CombatType.RANGED);
					break;
				}
			}
		}

		// if (!inside) {
		boolean hasDistance = npc.npcId == 5535 ? true : false; // force 5535 tents to always be hittable
		for (Location Location : npc.getTiles()) {
			double distance = Location.distance(player.getLocation());
			boolean magic = player.usingMagic;
			boolean ranged = !player.usingMagic
					&& (player.usingRangeWeapon || player.usingOtherRangeWeapons || player.usingBow || player.usingArrows);
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
					&& !player.usingOtherRangeWeapons
					&& !player.usingArrows
					&& (player.playerEquipment[player.getEquipment().getWeaponId()] < 4212 || player.playerEquipment[player.getEquipment().getWeaponId()] > 4223)
					&& player.playerEquipment[player.getEquipment().getWeaponId()] != 12926) {
				player.write(new SendMessagePacket("You have run out of arrows!"));
				player.stopMovement();
				player.npcIndex = 0;
				return;
			}

			if (player.getCombat().correctBowAndArrows() < player.playerEquipment[player.getEquipment().getQuiverId()] && player.usingBow
					&& !player.getCombat().usingCrystalBow() && player.playerEquipment[player.getEquipment().getWeaponId()] != 9185
					&& player.playerEquipment[player.getEquipment().getWeaponId()] != 11785 && player.playerEquipment[player.getEquipment().getWeaponId()] != 18357) {
				player.write(new SendMessagePacket("You can't use " + player.getItems().getItemName(player.playerEquipment[player.getEquipment().getQuiverId()]).toLowerCase() + "s with a "
						+ player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase() + "."));
				player.stopMovement();
				player.npcIndex = 0;
				return;
			}
			if (player.playerEquipment[player.getEquipment().getWeaponId()] == 11785
					&& player.playerEquipment[player.getEquipment().getWeaponId()] == 9185 && player.playerEquipment[player.getEquipment().getWeaponId()] == 18357 && !player.getCombat().properBolts()) {
				player.write(new SendMessagePacket("You must use bolts with a crossbow."));
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
				|| player.usingOtherRangeWeapons
				|| (CombatData.usingHalberd(player) && player.goodDistance(player.getX(), player.getY(), npc.getX(),
						npc.getY(), 2))) {
			player.stopMovement();
		}

		player.faceUpdate(index);
		npc.underAttackBy = player.getIndex();
		npc.lastDamageTaken = System.currentTimeMillis();
		
		if (player.isUsingSpecial()) {
			player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
			player.lastArrowUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
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
						npc.playAnimation(Animation.create(CombatAnimation.getNPCBlockAnimation(npc.getIndex())));
					}
				}
			} else {
				// magic spell block anims
				player.playAnimation(Animation.create(player.MAGIC_SPELLS[player.getSpellId()][2]));
			}
		}
		// These are exact examples of where PI is AWFUL. variables like these. so for future, remove and 
		// implement another way for them to achieve whatever they achieve. 
		player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
		player.lastArrowUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
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
		} else if (player.getCombatType() == CombatType.RANGED && !player.usingOtherRangeWeapons) { // range
			if (player.usingCross)
				player.usingBow = true;
			if (player.getAttackStyle() == 2)
				player.attackDelay--; // this is what PI is built around.. when attackDelay=0, make damage show.
			// design is awful cos as you will know with experience if your wep changes before the hit appears
			// everything fucks up.. like max hits. like ags in 1hp dh and change to axe b4 hit appears you'll
			// ags a 110
			player.followId2 = npc.getIndex();
			player.getPA().followNpc();
			player.lastArrowUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
			player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
			player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
			player.oldNpcIndex = index;
			if (player.playerEquipment[player.getEquipment().getWeaponId()] >= 4212 && player.playerEquipment[player.getEquipment().getWeaponId()] <= 4223) {
				player.rangeItemUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
				player.crystalBowArrowCount++;
				player.lastArrowUsed = 0;
				player.getCombat().fireProjectileNpc();
			} else {
				player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
				player.getItems().deleteArrow();
				if (player.playerEquipment[3] == 11235 || player.playerEquipment[3] == 12765 || player.playerEquipment[3] == 12766
						|| player.playerEquipment[3] == 12767 || player.playerEquipment[3] == 12768) {
					player.getItems().deleteArrow();
				}
				if (npc.currentHealth > 0) {
					player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
					player.getCombat().fireProjectileNpc();
				}
			}
		} else if (player.usingOtherRangeWeapons && player.getCombatType() == CombatType.RANGED) {
			player.followId2 = npc.getIndex();
			player.getPA().followNpc();
			player.rangeItemUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
			if (player.playerEquipment[3] == 21000) {
				player.getItems().removeEquipment();
			} else {
				player.getItems().deleteEquipment(); // here
			}
			player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
			player.lastArrowUsed = 0;
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
			for (Location Location : npc.getBorder()) {
				if (ProjectilePathFinder.isProjectilePathClear(player.getLocation(), Location)) {
					return true;
				}
			}
		} else {
			for (Location Location : npc.getBorder()) {
				if (ProjectilePathFinder.isInteractionPathClear(player.getLocation(), Location)) {
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