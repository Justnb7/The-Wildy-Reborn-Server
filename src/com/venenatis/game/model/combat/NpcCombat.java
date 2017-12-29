package com.venenatis.game.model.combat;

import java.util.Objects;

import com.venenatis.game.content.skills.slayer.Slayer;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.boudary.BoundaryManager;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.definitions.WeaponDefinition;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.instance.impl.KrakenInstance;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
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
	 * Distanced required to attack
	 * 
	 * @param npc
	 *            The npc we're trying to attack
	 * @return
	 */
	public static int distanceRequired(NPC npc) {
		if (AbstractBossCombat.isBoss(npc.getId())) {
			return AbstractBossCombat.get(npc.getId()).distance(null);
		}
		if(npc.getName().contains("Whirlpool"))
			return 8;
		return 1;
	}

	public static void kraken(Player player, NPC npc, int damage) {
		if (npc.getId() == 5534 && npc.getId() != 5535) {
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
		if (npc.getId() == 496 && npc.getId() != 494) { // big whirlpools of Kraken
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
		if (npc.getId() == 493 && npc.getId() != 492) { // small whirlpools of Cave_krakens
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
	 * @param entity
	 *            The {@link Player} attacking the npc
	 * @param npc
	 *            The {@link NPC} which is being attacked
	 * @return If the player can attack the npc
	 */
	public static boolean canTouch(Entity entity, NPC npc, boolean findpath) {
		boolean ignoreClip = npc.getName().equalsIgnoreCase("Whirlpool") || npc.getName().equalsIgnoreCase("Zulrah")
				|| npc.getName().equalsIgnoreCase("Portal") || npc.getName().equalsIgnoreCase("Kraken")
				|| npc.getName().equalsIgnoreCase("Spinolyp") || npc.getName().equalsIgnoreCase("Enormous Tentacle");
		if (ignoreClip)
			return true;
		boolean projectile = entity.getCombatType() == CombatStyle.RANGE || entity.getCombatType() == CombatStyle.MAGIC;
		if (projectile) {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isProjectilePathClear(entity.getLocation(), pos)) {
					return true;
				}
			}
		} else {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isInteractionPathClear(entity.getLocation(), pos)) {
					//player.write(new SendGameMessage("debug");
					return true;
				}
			}
		}

		if (findpath) {
			RouteFinder.getPathFinder().findRoute(entity, npc.getX(), npc.getY(), true, 1, 1);
		}
		//player.write(new SendGameMessage("debug");
		return false;
	}

	public static boolean canAttackNpc(Player player, NPC npc) {
		if (npc.getCombatState().isDead() || npc.getMaxHitpoints() <= 0 || player.getCombatState().isDead()) {
			System.out.println("Npc is dead, we can't attack him anymore.");
			player.getCombatState().reset();
			return false;
		}
		
		if (npc.transforming) {
			System.out.println("Transforming mask.");
			return false;
		}

		if (!Slayer.canAttack(player, npc)) {
			System.out.println("Slayer level says no.");
			return false;
		}
		
		if (npc.getId() == 3162 || npc.getId() == 3163 || npc.getId() == 3164 || npc.getId() == 3165 && player.getCombatType() == CombatStyle.MELEE) {
			if (player.getActionSender() != null) {
				player.getActionSender().sendMessage("That NPC is flying too high to be attaked!");
			}
			return false;
		}
		
		if (npc.getId() == 496 && npc.getId() != 494) {
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
		
		if (!BoundaryManager.isWithinBoundaryNoZ(npc.getLocation(), "multi_combat")) {
			if (npc.getCombatState().getUnderAttackBy() != null && !Objects.equals(player, npc.getCombatState().getUnderAttackBy())) {
				player.message("Someone else is fighting that.");
				player.getCombatState().reset();
				return false;
			}
		}

		if (Combat.incombat(player) && player.lastAttacker != npc && !BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "multi_combat") && !Boundary.isIn(player, Boundary.KRAKEN)) {
			Combat.resetCombat(player);
			player.message("I'm already under attack.");
			return false;
		}

		if (npc.spawnedByPresentAndWrong(player)) {
			Combat.resetCombat(player);
			player.message("This monster was not spawned for you.");
			return false;
		}

		if (!player.getController().canAttackNPC()) {
			System.out.println("We're not allowed to fight npcs in this controller.");
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
		Entity player = npc.getCombatState().getTarget();

		if (player != null) {
			
			// Delay before we can attack again
			if (npc.getCombatState().getAttackDelay() > 0) {
				npc.getCombatState().decreaseAttackDelay(1);
				//npc.sendForcedMessage("atk timer: "+npc.getCombatState().getAttackDelay()+" "+npc.walkingHome+" " +npc.randomWalk);
			}
			
			if (npc.getCombatState().getSpellDelay() > 0) {
				npc.getCombatState().decreaseSpellDelay(1);
			}

			// If we havent been attacked within last 5 secs reset who last
			// attack us
			if (System.currentTimeMillis() - npc.getCombatState().getLastHit() > 5000) {
				npc.getCombatState().setUnderAttackBy(null);
				
				//We shouldn't reset the target because the npc stops attacking.
				//npc.getCombatState().setTarget(null);
			}

			if (!npc.getCombatState().isDead() && !npc.walkingHome && npc.getCombatState().getTarget() != null) {
				//stop following once good distance
				if (!goodDistance(npc.getX(), npc.getY(), player.getX(), player.getY(), distanceRequired(npc))) {
				npc.following().setFollowing(player);
				} else {
					npc.following().setFollowing(null);
					npc.resetFaceTile();
				}
				if (npc.getCombatState().getAttackDelay() == 0) {
					attackPlayer(player, npc);
				}
			}
		} else {
			// System.out.println("Im null");
			// System.out.println("player index nulled "+player.getIndex());
			npc.getCombatState().setTarget(null);
			npc.resetFaceTile();
			npc.getCombatState().setUnderAttackBy(null);
			npc.getUpdateFlags().flag(UpdateFlag.FACE_ENTITY);
		}
	}

	/**
	 * Handles an npc attacking a player
	 * 
	 * @param entity
	 *            The {@link Player} being attacked
	 * @param npc
	 *            The {@link NPC} attacking the player
	 */
	public static void attackPlayer(Entity entity, NPC npc) {
		Player player = (Player) entity;
		
		if (npc == null || npc.getCombatState().isDead()) {
			System.out.println("Unable to attack because the player is death");
			return;
		}
		
		if (npc.hasAttribute("busy") || npc.hasAttribute("stunned") || npc.hasAttribute("attack")) {
			System.out.println("We can't attack the player because of the following reasons (busy, stunned or under attack by Shaman)");
			return;
		}
		
		// Check validty of rooms
		if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
			if (!Boundary.isIn(entity, Boundary.GODWARS_BOSSROOMS)) {
				npc.getCombatState().setTarget(null);
				System.out.println("Invalid attack");
				return;
			}
		}
		
		// Attacks allowed? Height, dead, in tutorial. NOT distance (.. yet)
		if (!validateAttack(entity, npc)) {
			System.out.println("Stop");
			return;
		}
		
		npc.face(entity.getLocation());
		
		// Execute our attack if we're in range.
		if (goodDistance(npc.getX(), npc.getY(), entity.getX(), entity.getY(), distanceRequired(npc))) {
			npc.randomWalk = false;
			
			boolean isBoss = AbstractBossCombat.isBoss(npc.getId());
			AbstractBossCombat boss_cb = AbstractBossCombat.get(npc.getId());
			if (isBoss) {
				boss_cb.execute(npc, entity);
				// don't do any code below this, boss script handles all.
			} else {
				// Default npcs use defition anim & delay
				npc.getCombatState().setAttackDelay(npc.getDefinition().getAttackSpeed());
				npc.playAnimation(Animation.create(npc.getAttackAnimation()));
			}
			entity.lastAttacker = npc;
			entity.lastWasHitTime = System.currentTimeMillis();
			player.updateLastCombatAction();
			entity.getCombatState().setInCombat(true);
			entity.getActionSender().removeAllInterfaces();
			npc.faceEntity(entity);
			// Make the target Autoretal
			if (entity.getCombatState().noTarget()) {
				if (player.isAutoRetaliating()) {
					entity.getCombatState().setTarget(npc);
				}
			}
			// Make our target do their block anim
			if (entity.getCombatState().getAttackDelay() <= 3 || entity.getCombatState().getAttackDelay() == 0) {
				//tried to make a instance didnt work ether
				entity.playAnimation(Animation.create(WeaponDefinition.sendBlockAnimation(player)));
			}
			int damage = Utility.getRandom(npc.getDefinition().getMaxHit());
			
		/*	if (!(CombatFormulae.getAccuracy(npc, player, 0, 1.0))) {
				damage = 0;
			}*/
			// Actually damage our target
			if(!isBoss)
			entity.take_hit(npc, damage, npc.getCombatType()).send(0);
		} 
	}

	public static boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return ((objectX - playerX <= distance && objectX - playerX >= -distance)
				&& (objectY - playerY <= distance && objectY - playerY >= -distance));
	}

	/**
	 * Checks if the attack is okay on the player
	 * 
	 * @param entity
	 *            The {@link Player} being attacked
	 * @param npc
	 *            The {@link NPC} attacking the player
	 * @return If the npc can attack the player
	 */
	private static boolean validateAttack(Entity entity, NPC npc) {
		if (npc.getCombatState().isDead() || entity.getCombatState().isDead()) {
			System.out.println("Unable to attack because the entity is dead.");
			return false;
		}
		
		if (npc.getId() == 6617) {
			//Silence no message cuz this npc doesn't attack players just follows boss!
			return false;
		}
		
		if (!entity.isVisible()) {
			System.out.println("Unable to attack because the entity is invisible.");
			return false;
		}
		
		if (npc.getId() != 5535 && npc.getId() != 494) { // small tent and kraken can attack in single
			
			if (!BoundaryManager.isWithinBoundaryNoZ(npc.getLocation(), "multi_combat") && npc.getCombatState().getUnderAttackBy() != null && !Objects.equals(entity, npc.getCombatState().getUnderAttackBy())) {
				npc.getCombatState().setTarget(null);
				System.out.println("Unable to attack because the entity is already under attack?");
				return false;
			}
			if (!BoundaryManager.isWithinBoundaryNoZ(npc.getLocation(), "multi_combat")) {
				if ((npc.lastAttacker != entity && Combat.hitRecently(npc, 4000)) || npc != entity.lastAttacker && Combat.hitRecently(entity, 4000)) {
					npc.getCombatState().setTarget(null);
					return false;
				}
			}
		}
		/*
		 * This doesn't work.
		 */
		if (npc.getZ() != entity.getZ()) {
			npc.getCombatState().setTarget(null);
			return false;
		}

		if (NpcCombat.canTouch(entity, npc, false)) {
			return true;
		}

		boolean ignoreClip = npc.getName().equalsIgnoreCase("Whirlpool") || npc.getName().equalsIgnoreCase("Zulrah")
				|| npc.getName().equalsIgnoreCase("Portal") || npc.getName().equalsIgnoreCase("Kraken")
				|| npc.getName().equalsIgnoreCase("Spinolyp") || npc.getName().equalsIgnoreCase("Enormous Tentacle");
		if (ignoreClip) {
			return true;
		}

		// Always last
		if (npc.getCombatType() != CombatStyle.MELEE) {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isProjectilePathClear(entity.getLocation(), pos)) {
					return true;
				}
			}
		} else {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isInteractionPathClear(entity.getLocation(), pos)) {
					return true;
				}
			}
		}
		return false;
	}

}