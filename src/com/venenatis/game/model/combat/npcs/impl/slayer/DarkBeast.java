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
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class DarkBeast extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
            return; //this should be an NPC!
        }
		
		NPC npc = (NPC)attacker;
		
        CombatStyle style = CombatStyle.MAGIC;
        if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
            style = CombatStyle.MELEE;
        }
        
        int damage;
        int gfxDelay;
        int clientSpeed;
        
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
        
        switch (style) {
            case MELEE:
                attacker.playAnimation(Animation.create(npc.getAttackAnimation()));
                damage = Utility.random(17);
                hitDelay = 1;
                victim.take_hit(attacker, damage, CombatStyle.MELEE).send(hitDelay);
                break;
            case MAGIC:
            	attacker.playAnimation(Animation.create(npc.getAttackAnimation()));
                attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 130, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
                damage = Utility.random(8);
                victim.playGraphic(damage <= 0 ? Graphic.create(85, gfxDelay, 100) : Graphic.create(131, gfxDelay, 100));
                victim.take_hit(attacker, damage, CombatStyle.MAGIC).send(hitDelay);
                break;
		default:
			break;
        }
  
        attacker.getCombatState().setAttackDelay(5);
	}

	@Override
	public int distance(Entity attacker) {
		return 4;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		/**
		 * Players have a one in 1000 chance of dropping the pet table.
		 */
		int random = Utility.random(1000);
		
		Pets pets = Pets.DARK_BEAST;
		Pet pet = new Pet(player, pets.getNpc());
		if (player.alreadyHasPet(player, 22013) || player.getPet() == pets.getNpc()) {
			return;
		}

		if (random == 1) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(22013));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Dark beast pet.", false);
			} else {
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received the Dark beast pet.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}

}
