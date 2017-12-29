package com.venenatis.game.model.combat.npcs.impl.dagannoths;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Follower;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class DagannothRex extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		//The npc instance
		NPC npc = (NPC) attacker;
		
		//The player instance
		Player player = (Player) victim;
		
		//Attack style
		CombatStyle style = CombatStyle.MELEE;
		
		//Are we in attack distance
		if(Utility.getDistance(attacker.getLocation(), victim.getLocation()) > 4) {
			return;
		}
		
		int maxHit;
		int hitDelay;
		int randomHit;
		int hit;
		switch(style) {
		default:
		case MELEE:
			maxHit = npc.getDefinition().getMaxHit();
			hitDelay = 1;
			attacker.playAnimation(Animation.create(npc.getDefinition().getAttackAnimation()));

			randomHit = Utility.random(maxHit);
			if(randomHit > player.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = player.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			
			// Create the hit instance
			victim.take_hit(attacker, hit, style).send(hitDelay);
			break;
		}		
		
		attacker.getCombatState().setAttackDelay(6);
	}

	@Override
	public int distance(Entity attacker) {
		return 1;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		/**
		 * Players have a one in 1000 chance of dropping the pet table.
		 */
		int random = Utility.random(1000);
		
		Pet pets = Pet.DAGANNOTH_REX;
		Follower pet = new Follower(player, pets.getNpc());
		if (player.alreadyHasPet(player, 12645) || player.getPet() == pets.getNpc()) {
			return;
		}

		if (random == 1) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(12645));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Dagannoth rex pet.", false);
			} else {
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Dagannoth rex pet.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}

}
