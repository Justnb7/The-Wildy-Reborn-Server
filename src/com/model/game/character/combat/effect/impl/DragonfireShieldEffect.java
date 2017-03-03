package com.model.game.character.combat.effect.impl;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.combat.effect.DamageEffect;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;


/**
 * @author Jason MacKeigan
 * @date Dec 11, 2014, 4:44:33 AM
 */
public class DragonfireShieldEffect implements DamageEffect {
	
	static final long ATTACK_DELAY_REQUIRED = 120_000;
	
	private int cycle;

	@Override
	public void execute(Player attacker, Player defender, int damage) {
		
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			@Override
			public void execute() {
				if (Objects.isNull(attacker) || Objects.isNull(defender)) {
					this.stop();
					return;
				}
				if (defender.getSkills().getLevel(Skills.HITPOINTS) <= 0 || defender.isDead) {
					this.stop();
					return;
				}
				cycle++;
				if (cycle == 1) {
					attacker.playAnimation(Animation.create(6696));
					attacker.playGraphics(Graphic.create(1165, 0, 0));
					attacker.write(new SendMessagePacket("You unleash dragonfire from your shield."));
				} else if (cycle == 4) {
					attacker.getProjectile().createPlayersProjectile2(attacker.getX(), attacker.getY(), (attacker.getY() - defender.getY()) * -1,
							(attacker.getX() - defender.getX()) * -1, 50, 50, 1166, 30, 30, - attacker.oldPlayerIndex - 1, 30, 5);
				} else if (cycle >= 5) {
					if (defender.playerEquipment[defender.getEquipment().getShieldId()] == 11283) {
						defender.damage(new Hit((damage / 2) + (Utility.getRandom(damage / 2)), HitType.NORMAL));
						this.stop();
						return;
					}
					defender.damage(new Hit(damage, HitType.NORMAL));
					this.stop();
				}
			}
		});
	}

	@Override
	public boolean isExecutable(Player operator) {
		if (System.currentTimeMillis() - operator.getLastDragonfireShieldAttack() < ATTACK_DELAY_REQUIRED) {
			operator.write(new SendMessagePacket("You must let your dragonfire shield cool down before using it again."));
			return false;
		}
		return true;
	}

	@Override
	public void execute(Player attacker, Npc defender, int damage) {
		
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			@Override
			public void execute() {
				if (Objects.isNull(attacker) || Objects.isNull(defender)) {
					this.stop();
					return;
				}
				if (defender.currentHealth <= 0 || defender.isDead || attacker.teleTimer > 0) {
					this.stop();
					return;
				}
				if (Utility.distanceToPoint(attacker.getX(), attacker.getY(), defender.getX(), defender.getY()) > 12) {
					this.stop();
					return;
				}
				cycle++;
				if (cycle == 1) {
					attacker.playAnimation(Animation.create(6696));
					attacker.playGraphics(Graphic.create(1165, 0, 0));
					attacker.write(new SendMessagePacket("You unleash dragonfire from your shield."));
				} else if (cycle == 4) {
					attacker.getProjectile().createPlayersProjectile2(attacker.getX(), attacker.getY(), (attacker.getY() - defender.getY()) * -1, (attacker.getX() - defender.getX()) * -1, 50, 50, 1166, 30, 30, - attacker.oldNpcIndex - 1, 30, 5);
				} else if (cycle >= 5) {
					defender.underAttack = true;
					defender.currentHealth -= damage;
					defender.hitUpdateRequired = true;
					defender.updateRequired = true;
					this.stop();
				}
			}
		});
	}

}
