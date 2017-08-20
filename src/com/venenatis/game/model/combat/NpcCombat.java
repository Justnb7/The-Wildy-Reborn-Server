package com.venenatis.game.model.combat;

import java.util.Arrays;
import java.util.List;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.skills.slayer.Slayer;
import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.definitions.WeaponDefinition;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.instance.impl.KrakenInstance;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.ProjectilePathFinder;
import com.venenatis.game.world.pathfinder.RouteFinder;
import com.venenatis.server.Server;

/**
 * Handles Player Vs Npc combat
 * 
 * @author Sanity
 * @author Mobster
 * @author Patrick van Elderen
 */
public class NpcCombat {
	
	/**
	 * A list of unspawnable npcs on death
	 */
	private static List<Integer> unspawnableNpcs = Arrays.asList(4303, 4304, 1605, 1606, 1607, 1608, 1609, 5054);

	/**
	 * Gets the list of unspawnwable npcs
	 * @return The list of unspawnable npcs
	 */
	public static List<Integer> getUnspawnableNpcs() {
		return unspawnableNpcs;
	}

	/**
	* Distanced required to attack
	**/	
	public static int distanceRequired(NPC npc) {
		if (AbstractBossCombat.isBoss(npc.getId())) {
			return AbstractBossCombat.get(npc.getId()).distance(null);
		}
		return 1;
	}

	public static void kraken(Player player, NPC npc, int damage) {
		if (npc.getId() == 5534 && npc.transformId != 5535) {
			npc.transforming = true;
			npc.playAnimation(Animation.create(3860));
			npc.requestTransform(5535);
			npc.aggressive = true;
			npc.setHitpoints(120);//reset hp when disturbed
			npc.setHitpoints(npc.getHitpoints() - damage);

			Server.getTaskScheduler().schedule(new Task(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		if (npc.getId() == 496 && npc.transformId != 494) { // big whirlpools of Kraken
			npc.transforming = true;
			npc.playAnimation(Animation.create(7135));
			npc.requestTransform(494);
			npc.aggressive = true;
			npc.setHitpoints(255);//reset hp when disturbed
			npc.setHitpoints(npc.getHitpoints() - damage);

			Server.getTaskScheduler().schedule(new Task(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		
		//Cave kraken - NPCID = 492 // whirlpool (lvl 127) -> 493
		if (npc.getId() == 493 && npc.transformId != 492) { // small whirlpools of Cave_krakens
			npc.transforming = true;
			npc.playAnimation(Animation.create(7135));
			npc.requestTransform(492);
			
			npc.setHitpoints(125);//reset hp when disturbed
			npc.setHitpoints(npc.getHitpoints() - damage);

			Server.getTaskScheduler().schedule(new Task(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		
	}

	/**
	 * Validates if the {@link Player} can attack the {@link NPC}
	 * 
	 * @param player
	 *            The {@link Player} attacking the npc
	 * @param npc
	 *            The {@link NPC} which is being attacked
	 * @return If the player can attack the npc
	 */
	public static boolean canTouch(Player player, NPC npc, boolean findpath) {
		boolean ignoreClip = npc.getId() >= 1739 && npc.getId() <= 1742 || npc.getId() == 494 || npc.getId() == 492 || npc.getId() == 493 || npc.getId() == 496
				|| npc.getId() == 5534 || npc.getId() == 5535 || npc.getId() == 2054 || npc.getId() == 5947;
		if (ignoreClip)
			return true;
		boolean projectile = player.getCombatType() == CombatStyle.RANGE || player.getCombatType() == CombatStyle.MAGIC;
		if (projectile) {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isProjectilePathClear(player.getLocation(), pos)) {
					return true;
				}
			}
		} else {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isInteractionPathClear(player.getLocation(), pos)) {
					//player.write(new SendGameMessage("debug");
					return true;
				}
			}
		}

		if (findpath) {
			RouteFinder.getPathFinder().findRoute(player, npc.getX(), npc.getY(), true, 1, 1);
		}
		//player.write(new SendGameMessage("debug");
		return false;
	}

	public static boolean canAttackNpc(Player player, NPC npc) {
		if (npc.getCombatState().isDead() || npc.getMaxHitpoints() <= 0 || player.getCombatState().isDead()) {
			player.getCombatState().reset();
			return false;
		}
		if (npc.transforming)
			return false;

		if (!Slayer.canAttack(player, npc)) {
			return false;
		}
		
		if (npc.isArmadylNpc() && player.getCombatType() == CombatStyle.MELEE) {
			player.message("You can only use range or magic against this npc.");
			Combat.resetCombat(player);
			return false;
		}
		
		if (npc.getId() == 496 && npc.transformId != 494) {
			KrakenInstance i = player.getKraken();
			if (i != null && i.npcs != null && i.npcs[0] == npc) {
				for (NPC n : i.npcs) {
					if (n.getId() == 5534) {
						player.message("You can't disturb the kraken while the whirlpools are undisturbed.");
						Combat.resetCombat(player);
						return false;
					}
				}
			}
		}

		if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
			Combat.resetCombat(player);
			player.message("You cannot attack that npc from outside the room.");
			return false;
		}
		if (npc.underAttackBy > 0 && npc.underAttackBy != player.getIndex() && !Area.inMultiCombatZone(npc)) {
			player.getCombatState().reset();
			player.message("This monster is already in combat.");
			return false;
		}

		if (Combat.incombat(player) && player.lastAttacker != npc && !Area.inMultiCombatZone(player) && !Boundary.isIn(player, Boundary.KRAKEN)) {
			Combat.resetCombat(player);
			player.message("I am already under attack.");
			return false;
		}

		if (npc.spawnedBy != player.getIndex() && npc.spawnedBy > 0) {
			Combat.resetCombat(player);
			player.message("This monster was not spawned for you.");
			return false;
		}

		if (!player.getController().canAttackNPC()) {
			//System.out.println("blocked");
			return false;
		}
		// Otherwise, we're good to go!
		return true;
	}
	
	/**
	 * Handles the npcs combat timers
	 * 
	 * @param npc
	 *            The {@link NPC} to handle combat timers for
	 */
	public static void handleCombatTimer(NPC npc) {
		//npc.forceChat("attacktimer: "+npc.attackTimer+" "+npc.walkingHome+" "+npc.targetId);

		// Delay before we can attack again
		if (npc.getCombatState().getAttackDelay() > 0) {
			npc.getCombatState().decreaseAttackDelay(1);
			//npc.forceChat("atk timer: "+npc.attackTimer+" "+npc.walkingHome+" "+npc.randomWalk);
		}

		// If we havent been attacked within last 5 secs reset who last attack us
		if (System.currentTimeMillis() - npc.lastDamageTaken > 5000) {
			npc.underAttackBy = 0;
		}

		// Call code to attack our target if we're alive
		if (!npc.getCombatState().isDead() && !npc.walkingHome && npc.targetId > 0) {
			Player player = World.getWorld().getPlayers().get(npc.targetId);

			if (npc.followTarget != player)
				npc.followTarget = player;

			if (player == null) {
				// out of range
				npc.targetId = 0;
				npc.underAttack = false;
				npc.resetFace();
			} else {
				if (npc.getCombatState().getAttackDelay() == 0) {
					attackPlayer(player, npc);
				}
				// Following called in process()
			}
		}
	}

	/**
	 * Handles an npc attacking a player
	 * 
	 * @param player
	 *            The {@link Player} being attacked
	 * @param npc
	 *            The {@link NPC} attacking the player
	 */
	public static void attackPlayer(Player player, NPC npc) {
		if (npc == null || npc.getCombatState().isDead())
			return;
		
		// Check validty of rooms
		if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
			if (!Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
				npc.targetId = 0;
				npc.underAttack = false;
				return;
			}
		}
		
		// Attacks allowed? Height, dead, in tutorial. NOT distance (.. yet)
		if (!validateAttack(player, npc)) {
			return;
		}
		
		npc.faceEntity(player);
		
		// Execute our attack if we're in range.
		if (goodDistance(npc.getX(), npc.getY(), player.getX(), player.getY(), distanceRequired(npc))) {
			npc.randomWalk = false;
			
			boolean isBoss = AbstractBossCombat.isBoss(npc.getId());
			AbstractBossCombat boss_cb = AbstractBossCombat.get(npc.getId());
			if (isBoss) {
				boss_cb.execute(npc, player);
				// don't do any code below this, boss script handles all.
			} else {
				// Default npcs use defition anim & delay
				npc.getCombatState().setAttackDelay(npc.getDefinition().getAttackSpeed());
				npc.playAnimation(Animation.create(npc.getAttackAnimation()));
			}
			player.lastAttacker = npc;
			player.lastWasHitTime = System.currentTimeMillis();
			npc.oldIndex = player.getIndex();
			player.updateLastCombatAction();
			player.getCombatState().setInCombat(true);
			player.getActionSender().removeAllInterfaces();

			// Make the target Autoretal
			if (player.getCombatState().noTarget()) {
				if (player.isAutoRetaliating()) {
					player.getCombatState().setTarget(npc);
				}
			}
			// Make our target do their block anim
			if (player.getCombatState().getAttackDelay() <= 3 || player.getCombatState().getAttackDelay() == 0) {
				//tried to make a instance didnt work ether
				player.playAnimation(Animation.create(WeaponDefinition.sendBlockAnimation(player)));
			}

			int damage = Utility.getRandom(npc.getDefinition().getMaxHit());
			// Actually damage our target
			player.take_hit(npc, damage, npc.getCombatType()).send(0);
		}
	}

	public static boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return ((objectX - playerX <= distance && objectX - playerX >= -distance)
				&& (objectY - playerY <= distance && objectY - playerY >= -distance));
	}

	/**
	 * Checks if the attack is okay on the player
	 * 
	 * @param player
	 *            The {@link Player} being attacked
	 * @param npc
	 *            The {@link NPC} attacking the player
	 * @return If the npc can attack the player
	 */
	private static boolean validateAttack(Player player, NPC npc) {
		if (npc.getCombatState().isDead() || player.getCombatState().isDead()) {
			return false;
		}
		
		if (npc.getId() == 6617) {
			return false;
		}
		if (!player.isVisible()) {
			return false;
		}
		if (npc.getId() != 5535 && npc.getId() != 494) { // small tent and kraken can attack in single
			if (!Area.inMultiCombatZone(npc) && npc.underAttackBy > 0 && npc.underAttackBy != player.getIndex()) {
				npc.targetId = 0;
				return false;
			}
			if (!Area.inMultiCombatZone(npc)) {
				if ((npc.lastAttacker != player && Combat.hitRecently(npc, 4000)) || npc != player.lastAttacker && Combat.hitRecently(player, 4000)) {
					npc.targetId = 0;
					return false;
				}
			}
		}
		/*
		 * This doesn't work.
		 */
		if (npc.getZ() != player.getZ()) {
			npc.targetId = 0;
			return false;
		}

		if (!player.receivedStarter()) {
			npc.targetId = 0;
			return false;
		}
		if (NpcCombat.canTouch(player, npc, false)) {
			return true;
		}

		boolean ignoreClip = npc.getId() == 494 || npc.getId() == 492 || npc.getId() == 5862 || npc.getId() == 493
				|| npc.getId() == 496 || npc.getId() == 2054 || npc.getId() == 5947;
		if (ignoreClip) {
			return true;
		}

		// Always last
		if (npc.getCombatType() != CombatStyle.MELEE) {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isProjectilePathClear(player.getLocation(), pos)) {
					return true;
				}
			}
		} else {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isInteractionPathClear(player.getLocation(), pos)) {
					return true;
				}
			}
		}
		return false;
	}

}