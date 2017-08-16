package com.venenatis.game.model.combat.npcs.impl.dagannoths;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
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

public class DagannothPrime extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC) attacker;
		
		Player player = (Player) victim;
		
		CombatStyle style = CombatStyle.MAGIC;
		
		int maxHit;
		int randomHit;
		int hitDelay;
		final int hit;
		
		switch(style) {
		default:
		case MAGIC:
			maxHit = npc.getDefinition().getMaxHit();
			int clientSpeed;
			int gfxDelay;
			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			hitDelay = (gfxDelay / 20) - 1;
			
			attacker.playAnimation(Animation.create(npc.getDefinition().getAttackAnimation()));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 162, 45, 50, clientSpeed, 70, 35, victim.getProjectileLockonIndex(), 10, 48));

			randomHit = Utility.random(maxHit);
			if(randomHit > player.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = player.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			// Create the hit instance
			victim.take_hit(attacker, hit, style).send(hitDelay);
			Server.getTaskScheduler().schedule(new Task(hitDelay) {
				@Override
				public void execute() {
					this.stop();
					victim.playGraphics(Graphic.create(163, 0, 100));
				}
			});
			break;
		}
		
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

		if (random == 1) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(12644));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Dagannoth prime pet.", false);
			} else {
				Pets pets = Pets.DAGANNOTH_PRIME;
				Pet pet = new Pet(player, pets.getNpc());
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Dagannoth prime pet.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}

}
