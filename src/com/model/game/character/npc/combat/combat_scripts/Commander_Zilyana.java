package com.model.game.character.npc.combat.combat_scripts;

import java.util.Random;

import com.model.Server;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.nvp.NpcVsPlayerCombat;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class Commander_Zilyana extends Boss {

	public Commander_Zilyana(int npcId) {
		super(npcId);
	}

	private static Random r = new Random();
	
	private final String[] MESSAGES = { "eath to the enemies of the light!", "Slay the evil ones!", "Saradomin lend me strength!",
			"By the power of Saradomin!",
			"May Saradomin be my sword!",
			"Good will always triumph!",
			"Forward! Our allies are with us!", "Saradomin is with us!", "In the name of Saradomin!",
			"Attack! Find the Godsword!"};

	@Override
	public void execute(Npc npc, Player player) {

		if (r.nextInt(3) == 0) {
			npc.forceChat(MESSAGES[(int) (Math.random() * MESSAGES.length)]);
			npc.forcedChatRequired = true;
			npc.updateRequired = true;
		}

		int attack = r.nextInt(20);
		//player.sendGameMessage("random Attack: "+attack);
		if (attack > 15) {
			//player.sendGameMessage("Melee attack.");
			npc.attackStyle = 0;
			melee(npc, player);
		} else {
			//player.sendGameMessage("Magic attack");
			npc.attackStyle = 2;
			magicAttack(player, npc);
		}
	}
	
	private void melee(Npc npc, Player player) {
		int damage = Utility.getRandom(31);
		
		if (npc.attackStyle == 0) {
			if (!CombatFormulae.getAccuracy(npc, player, 0, 1.0)) {
				damage = 0;
			}	
			if (player.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
					damage = 0;
				} else {
					damage = Utility.getRandom(damage);
			}
			if (player.playerEquipment[player.getEquipment().getShieldId()] == 12817) {
				if (Utility.getRandom(100) > 30 && damage > 0) {
					damage *= .75;
				}
			}
		}
		send_dmg_on_player(npc, player, damage);
	}
	
	private void magicAttack(Player player, Npc npc) {
		int damage = Utility.getRandom(31);
		int secondDamage = Utility.getRandom(31);
		
		if (!CombatFormulae.getAccuracy(npc, player, 2, 1.0)) {
			damage = 0;
		}
		if (player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
			damage = 0;
		} else {
			damage = Utility.getRandom(damage);
		}
		send_dmg_on_player(npc, player, damage);
		Server.getTaskScheduler().schedule(new ScheduledTask(2) {
			
			@Override
			public void execute() {
				send_dmg_on_player(npc, player, secondDamage);
				this.stop();
			}
		});
		player.playGraphics(Graphic.create(1221));
		//npc.endGfx = 1221;
	}
	
	// This method tries to damage the player - instead of the traditional applyDamage() method
	public static void send_dmg_on_player(Npc npc, Player player, int dmg) {

		// last stage - apply the damage (make it show on player) - dont do this if they're teleporting
		if (player.teleporting) {
			return;
		}
		// adjust the damage if it's above the available HP
		if (player.getSkills().getLevel(Skills.HITPOINTS) - dmg < 0) {
			player.getSkills().setLevel(Skills.HITPOINTS, dmg);
		}
		// make the damage show up
		player.damage(new Hit(dmg));
		// trigger stuff that happens when a hit appears
		NpcVsPlayerCombat.on_damage_delt(npc, dmg);
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return 31;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return npc.attackStyle == 0 ? 6967 : npc.attackStyle == 2 ? 6970 : 6967;
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.getDefinition().getAttackSpeed();
	}

	@Override
	public int getHitDelay(Npc npc) {
		return npc.attackStyle == 0 ? 4 : 6;
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		if (npc.attackStyle == 2)
			return true;
		else 
			return false;
	}

	@Override
	public boolean switchesAttackers() {
		return true;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return 8;
	}

	@Override
	public int offSet(Npc npc) {
		return 0;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return true;
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		return 0;
	}

}