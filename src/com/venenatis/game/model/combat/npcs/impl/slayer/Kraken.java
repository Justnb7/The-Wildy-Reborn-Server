package com.venenatis.game.model.combat.npcs.impl.slayer;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.Projectile;
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
import com.venenatis.server.Server;

public class Kraken extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
			return;
		}

		// The npc instance
		NPC npc = (NPC) attacker;

		// Calculate max hit first
		int maxHit = 28;
		final int hit;
		
		Animation anim = Animation.create(npc.getAttackAnimation());
		attacker.playAnimation(anim);

		int randomHit = Utility.random(maxHit);
		hit = randomHit;
		int clientSpeed;
		int gfxDelay;
		if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
			clientSpeed = 110;
			gfxDelay = 120;
		} else {
			clientSpeed = 130;
			gfxDelay = 140;
		}
		int hitDelay = (gfxDelay / 20) - 1;

		attacker.playAnimation(Animation.create(3992));
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 162, 45, 50, clientSpeed, 70, 35, victim.getProjectileLockonIndex(), 10, 48));
		
		Server.getTaskScheduler().schedule(new Task(hitDelay) {
			@Override
			public void execute() {
				victim.playGraphic(Graphic.create(hit > 0 ? 163 : 85, 0, 100));
				this.stop();
			}
		});
		victim.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(hitDelay);
		attacker.getCombatState().setAttackDelay(6);
	}

	@Override
	public int distance(Entity attacker) {
		return 5;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		/**
		 * Players have a one in 1000 chance of dropping the pet table.
		 */
		int random = Utility.random(1000);
		
		Pets pets = Pets.KRAKEN;
		Pet pet = new Pet(player, pets.getNpc());
		if (player.alreadyHasPet(player, 12655) || player.getPet() == pets.getNpc()) {
			return;
		}

		if (random == 1) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(12655));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Kraken pet.", false);
			} else {
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Kraken pet.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}

}
