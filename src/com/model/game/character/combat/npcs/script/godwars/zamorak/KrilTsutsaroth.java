package com.model.game.character.combat.npcs.script.godwars.zamorak;

import java.util.Random;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.PoisonType;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.Projectile;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.npc.pet.Pets;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.item.Item;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

/**
 * The K'ril Tsutsaroth combat script.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class KrilTsutsaroth extends AbstractBossCombat {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	/**
	 * The timer of K'rils shouts
	 */
	private long lastMessage;
	
	/**
	 * All K'rils message stored in a single String
	 */
	private static final String[] MESSAGES = { "Attack them, you dogs!", "Forward!", "Death to Saradomin's dogs!",
			"Kill them, you cowards!", "The Dark One will have their souls!", "Zamorak curse them!",
			"Rend them limb from limb!", "No retreat!", "Flay them all!" };

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		// Sent a random shout
		if (random.nextInt(3) == 2 && System.currentTimeMillis() - lastMessage > 3000) {
			attacker.sendForcedMessage(MESSAGES[random.nextInt(MESSAGES.length)]);
			lastMessage = System.currentTimeMillis();
		}

		NPC npc = (NPC) attacker;
		
		Player player = (Player) victim;

		CombatStyle style = CombatStyle.MAGIC;
		
		int maxHit = 0;
		int randomHit;
		int hitDelay;
		final int hit;
		
		if(attacker.getLocation().isWithinDistance(attacker, victim, 2)) {
			if (random.nextInt(6) == 4)
				style = CombatStyle.MELEE;
		}

		switch(style) {
		case MELEE:
			attacker.playAnimation(Animation.create(npc.getAttackAnimation()));
			hitDelay = 2;
			boolean troughPrayer = false;
			if(player.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
				//Special attack, hits trough prayer
				if(Utility.random(8) == 7) {
					maxHit = 49;
					troughPrayer = true;
					attacker.sendForcedMessage("YARRRRRRR!");
					victim.getActionSender().sendMessage("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
					player.getSkills().decreaseLevel(Skills.PRAYER, player.getSkills().getLevel(Skills.PRAYER) / 2);
				}
			} else {
				maxHit = 47;
			}
			
			//Apply random poison attack
			if(random.nextInt(5) == 2) {
                player.setPoisonType(PoisonType.SUPER_NPC);
            }
			
			randomHit = Utility.random(maxHit);
			hit = randomHit;
			
			// Create the hit instance
			Hit hitInfo = victim.take_hit(attacker, hit, style, false, troughPrayer);

			// Send the hit task
			Combat.hitEvent(attacker, victim, hitDelay, hitInfo, style);
			break;
		case MAGIC:
			maxHit = 30;
			
			int gfxSpeed;
			int gfxDelay;
			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				gfxSpeed = 70;
				gfxDelay = 80;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				gfxSpeed = 90;
				gfxDelay = 100;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				gfxSpeed = 110;
				gfxDelay = 120;
			} else {
				gfxSpeed = 130;
				gfxDelay = 140;
			}
			hitDelay = (gfxDelay / 20) - 1;
			attacker.playAnimation(Animation.create(6950));
			attacker.playGraphics(Graphic.create(1224, 0, 100));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1225, 45, 50, gfxSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			
			randomHit = Utility.random(maxHit);
            hit = randomHit;
			
			// Create the hit instance
			hitInfo = victim.take_hit(attacker, hit, style, false, false);

			// Send the hit task
			Combat.hitEvent(attacker, victim, hitDelay, hitInfo, style);
			
			//Send the player gfx
			Server.getTaskScheduler().schedule(new ScheduledTask(hitDelay) {
				@Override
				public void execute() {
					this.stop();
					victim.playGraphics(Graphic.create(1225));
				}
			});
			
			break;
		default:
			break;
		
		}
		attacker.getCombatState().setAttackDelay(4);
	}

	@Override
	public int distance(Entity attacker) {
		return 5;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		int random = Utility.random(250);
		if (random == 1) {
			if (player.isPetSpawned()) {
				if (player.getInventory().getFreeSlots() < 1) {
					player.getInventory().add(new Item(12652));
				} else {
					player.getBank().add(new Item(12652));
				}
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received the K'ril pet.", false);
			} else {
				Pets pets = Pets.KRIL_TSUTSAROTH;
				Pet pet = new Pet(player, pets.getNpc());
				player.setPetSpawned(true);
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received the K'ril pet.", false);
			}
		}
	}

}
