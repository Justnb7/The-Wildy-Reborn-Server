package com.venenatis.game.model.combat.npcs.impl;

import java.util.Random;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.npc.pet.Pets;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class CorporealBeast extends AbstractBossCombat {
	
	private static Random r = new Random();

	/**
	 * The melee animation.
	 */
	private final Animation MELEE_ANIMATION = Animation.create(1682);

	/**
	 * The magic animation.
	 */
	private final Animation MAGIC_ANIMATION = Animation.create(1680);
	
	private final int DARK_CORE_ID = 320;

	private final int MAX_HIT_MELEE = 51;
	private final int MAX_HIT_MAGIC = 65;

	private final int SMALL_MAGIC_PROJECTILE_ID = 314;
	private final int REGULAR_MAGIC_PROJECTILE_ID = 315;
	private final int BIG_MAGIC_PROJECTILE_ID = 316;
	private final int SPLATTER_GFX_ID = 317;
	private final int EXPLOSION_GFX_ID = 318;

	private final int DARK_CORE_PROJECTILE_ID = 319;

	@Override
	public void execute(Entity attacker, Entity victim) {
		
		if (!attacker.isNPC()) {
            return; //this should be an NPC!
        }
		
		NPC npc = (NPC)attacker;
		
		Player player = (Player)victim;
		
		if (npc.getHitpoints() < (npc.getMaxHitpoints() / 2)) {
			int speed = 90 + (npc.distanceToPoint(player.getX(), player.getY()) * 5);
			NPC[] darkCores = npc.getNpcsById(DARK_CORE_ID);
			if (darkCores.length <= 0) {
				player.getActionSender().sendProjectile(attacker.getCentreLocation(), player.getCentreLocation(), DARK_CORE_PROJECTILE_ID, speed, 50);
				World.getWorld().schedule(new Task(4) {
					@Override
					public void execute() {
						npc.spawn(player, DARK_CORE_ID, new Location(player.getX() + 1, player.getY(), player.getZ()), 1, true);
						stop();
					}
				});
			}
		} else {
			removeDarkCore(npc);
		}
		
        CombatStyle style = attacker.getLocation().distance(victim.getLocation()) <= 2 ? CombatStyle.MELEE : CombatStyle.MAGIC;
        
        int randomHit = style == CombatStyle.MELEE ? Utility.random(MAX_HIT_MELEE) : Utility.random(MAX_HIT_MAGIC);

        switch (style) {
		case MELEE:
			npc.playAnimation(MELEE_ANIMATION);
			player.playGraphic(new Graphic(EXPLOSION_GFX_ID));
			victim.take_hit(attacker, randomHit, CombatStyle.MELEE).send(1);
			break;
		case MAGIC:
			int gfxDelay;

			if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				gfxDelay = 80;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				gfxDelay = 100;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				gfxDelay = 120;
			} else {
				gfxDelay = 140;
			}
			int hitDelay = (gfxDelay / 20) - 1;
			
			npc.playAnimation(MAGIC_ANIMATION);
			int attack = r.nextInt(4);
			if (attack == 0) {
				player.getActionSender().sendProjectile(attacker.getCentreLocation(), player.getCentreLocation(), BIG_MAGIC_PROJECTILE_ID, gfxDelay, 50);
			} else if (attack == 1) {
				player.getActionSender().sendProjectile(attacker.getCentreLocation(), player.getCentreLocation(), REGULAR_MAGIC_PROJECTILE_ID, gfxDelay, 50);
				if (randomHit > 0) {
					sendSplatter(player, npc);
				}
			} else {
				player.getActionSender().sendProjectile(attacker.getCentreLocation(), player.getCentreLocation(), SMALL_MAGIC_PROJECTILE_ID, gfxDelay, 50);
			}
			victim.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(hitDelay);
			break;
		default:
			break;
        
        }
        attacker.getCombatState().setAttackDelay(4);
	}

	@Override
	public int distance(Entity attacker) {
		return 15;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		Pets pets = Pets.DARK_CORE;
		Pet pet = new Pet(player, pets.getNpc());
		if (player.alreadyHasPet(player, 12816) || player.getPet() == pets.getNpc()) {
			return;
		}
		/**
		 * Players have a one in 1000 chance of dropping the pet table.
		 */
		int random = Utility.random(1000);
		
		if (random == 1) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(12816));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Dark core pet.", false);
			} else {
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Dark core pet.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}
	
	private void sendSplatter(Player player, NPC npc) {
		int splatters = 4 + Utility.random(3);
		for (int i = 0; i < splatters; i++) {
			createSplatter(player, -3 + Utility.random(6), -3 + Utility.random(6));
		}
	}

	private void createSplatter(Player player, int offsetX, int offsetY) {
		World.getWorld().schedule(new Task(5) {

			@Override
			public void execute() {
				player.getActionSender().createSplatterProjectile(REGULAR_MAGIC_PROJECTILE_ID, player.getX() + offsetX, player.getY() + offsetY, 0, 21);
				player.getActionSender().stillGfx(SPLATTER_GFX_ID, player.getX() + offsetX, player.getY() + offsetY, 0, 21);
				stop();
			}
		});
	}
	
	private void removeDarkCore(NPC npc) {
		for (NPC darkCore : npc.getNpcsById(DARK_CORE_ID)) {
			darkCore.remove(npc);
		}
	}

}
