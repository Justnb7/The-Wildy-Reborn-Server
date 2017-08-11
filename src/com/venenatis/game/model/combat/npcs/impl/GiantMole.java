package com.venenatis.game.model.combat.npcs.impl;

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
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class GiantMole extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}

		NPC npc = (NPC) attacker;

		CombatStyle style = CombatStyle.DIG;

		int hitDelay;
		final int hit;

		/**
		 * The Giant Mole deals a decent amount of damage, and once it reaches
		 * 50% of its Hitpoints, every attack done to it has a 25% chance of
		 * causing it to flee by burrowing in the ground, requiring players to
		 * track down the mole.
		 */
		if (npc.getHitpoints() > 100) {
			style = CombatStyle.MELEE;
		} else {
			switch (Utility.random(4)) {
			default:
				style = CombatStyle.MELEE;
				break;
			case 4:
				if(npc.getHitpoints() > 0 || !npc.getCombatState().isDead()) {
					style = CombatStyle.DIG;
				}
				break;
			}
		}

		switch (style) {
		default:
		case MELEE:
			hitDelay = 1;
			attacker.playAnimation(Animation.create(npc.getDefinition().getAttackAnimation()));
			int randomHit = Utility.random(npc.getDefinition().getMaxHit());
			hit = randomHit;
			break;
		case DIG:
			hitDelay = 0;
			hit = 0;
			attacker.setCanBeDamaged(false);
			break;
		}

		attacker.getCombatState().setAttackDelay(npc.getDefinition().getAttackSpeed());

		if (style == CombatStyle.DIG) {
			attacker.playAnimation(Animation.create(3314));
			World.getWorld().schedule(new Task(2) {
				public void execute() {
					npc.teleport(dig());
					attacker.playAnimation(Animation.create(3315));
					attacker.setCanBeDamaged(true);
					this.stop();
				}
			});
			return;
		}
		victim.take_hit(attacker, hit, style).send(hitDelay);
	}

	private final static Location locs[] = {
			Location.create(1778, 5237), Location.create(1761, 5186), Location.create(1737, 5209),
			Location.create(1737, 5227) };

	private Location dig() {
		return locs[(int) (Math.random() * locs.length)];
	}

	@Override
	public int distance(Entity attacker) {
		return 5;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		/**
		 * Players have a one in 250 chance of dropping the pet table.
		 */
		int random = Utility.random(250);
		
		if (random == 1) {
			if (player.getPet() > -1) {
				if (player.getInventory().getFreeSlots() < 1) {
					player.getInventory().add(new Item(12646));
				} else {
					player.getBank().add(new Item(12646));
				}
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Giant mole pet.", false);
			} else {
				Pets pets = Pets.BABY_MOLE;
				Pet pet = new Pet(player, pets.getNpc());
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Giant mole pett.", false);
			}
		}
	}

}