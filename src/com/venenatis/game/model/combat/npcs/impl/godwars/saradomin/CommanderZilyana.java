package com.venenatis.game.model.combat.npcs.impl.godwars.saradomin;

import java.util.Collection;
import java.util.Random;

import com.venenatis.game.model.Item;
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
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;

public class CommanderZilyana extends AbstractBossCombat {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	private long lastMessage;
	
	private static final String[] MESSAGES = {"Death to the enemies of the light!",
		"Slay the evil ones!",
		"Saradomin lend me strength!",
		"By the power of Saradomin!",
		"May Saradomin be my sword.",
		"Good will always triumph!",
		"Forward! Our allies are with us!",
		"Saradomin is with us!",
		"In the name of Saradomin!",
		"Attack! Find the Godsword!",
		"All praise Saradomin!"};

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		if (random.nextInt(3) == 2 && System.currentTimeMillis() - lastMessage > 3000) {
			attacker.sendForcedMessage(MESSAGES[random.nextInt(MESSAGES.length)]);
			lastMessage = System.currentTimeMillis();
		}
		
		int randomHit;
		int hitDelay;
		
		NPC npc = (NPC) attacker;
		
		CombatStyle style = CombatStyle.MELEE;
		
		if(attacker.getLocation().isWithinDistance(attacker, victim, 2)) {
			switch(random.nextInt(3)) {
			case 0:
			case 1:
				style = CombatStyle.MELEE;	
				break;
			case 2:
				style = CombatStyle.MAGIC;
				break;
			}
		}
		
		switch(style) {
		case MELEE:
			Animation anim = Animation.create(npc.getAttackAnimation());
			attacker.playAnimation(anim);
			
			hitDelay = 1;
			randomHit = Utility.random(npc.getDefinition().getMaxHit());
			victim.take_hit(attacker, randomHit, CombatStyle.MELEE).send(hitDelay);
			break;
			
		case MAGIC:
			attacker.playAnimation(Animation.create(6970));
			victim.playGraphics(Graphic.create(1221, 60));
            final Collection<Player> localPlayers = RegionStoreManager.get().getLocalPlayers(attacker);
			
			for(final Player near : localPlayers) {
				if(near != null && near != attacker && near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					if (attacker.getCentreLocation().isWithinDistance(attacker, near, 10)) {
						near.playGraphics(Graphic.create(1207));
						randomHit = Utility.random(32);
						near.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(1);
						int preDouble = (int) (randomHit / 2);
						int secondHit = Utility.random(preDouble);
						near.take_hit(attacker, secondHit, CombatStyle.MAGIC).send(1);
						npc.setHitpoints(npc.getHitpoints() + Utility.random(3));
					}
				}
			}
			break;
		default:
			break;
		}
		attacker.getCombatState().setAttackDelay(npc.getDefinition().getAttackSpeed());
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

		if (random == 1) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(12651));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Commander zilyana Jr. pet.", false);
			} else {
				Pets pets = Pets.ZILYANA;
				Pet pet = new Pet(player, pets.getNpc());
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Commander zilyana Jr. pet.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}

}
