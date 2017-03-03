package com.model.game.character.combat.effect.impl;

import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.combat.effect.DamageEffect;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.out.SendMessagePacket;

public class RingOfRecoil implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, int damage) {
		if (defender.getSkills().getLevel(Skills.HITPOINTS) <= 0 || defender.isDead) {
			return;
		}
		int delt = (int) Math.ceil(damage * 0.10);
		int ring = attacker.playerEquipment[attacker.getEquipment().getRingId()];
		if (ring == 2550 || ring == 19550 && attacker.getROSuffering() > 0 || ring == 19710) {
			defender.damage(new Hit(delt, HitType.NORMAL));
		} else {
			return;
		}
		if(ring == 2550) {
			attacker.setRecoil(attacker.getRecoil() - delt);
		} else if(ring == 19550 && attacker.getROSuffering() > 0) {
			attacker.setROSuffering(attacker.getROSuffering() - delt);
		}
		if (attacker.getRecoil() <= 0) {
			attacker.setRecoil(40);
			if (attacker.playerEquipment[attacker.getEquipment().getRingId()] == 2550) {
				attacker.playerEquipment[attacker.getEquipment().getRingId()] = -1;
				attacker.playerEquipmentN[attacker.getEquipment().getRingId()] = 0;
				attacker.getItems().wearItem(-1, 1, 12);
			}
			attacker.write(new SendMessagePacket("<col=9A289E>Your ring of recoil has shattered!"));
		}
	}

	@Override
	public void execute(Player attacker, Npc defender, int damage) {
		if (defender.getDefinition().getHitpoints() == 0 || defender.isDead) {
			return;
		}
		int delt = (int) Math.ceil(damage * 0.10);
		int ring = attacker.playerEquipment[attacker.getEquipment().getRingId()];
		if (ring == 2550 || ring == 19550 && attacker.getROSuffering() > 0 || ring == 19710) {
			defender.damage(new Hit(delt, HitType.NORMAL));
		} else {
			return;
		}
		if(ring == 2550) {
			attacker.setRecoil(attacker.getRecoil() - delt);
		} else if(ring == 19550) {
			attacker.setROSuffering(attacker.getROSuffering() - delt);
		}
		if (attacker.getRecoil() <= 0) {
			attacker.setRecoil(40);
			if (attacker.playerEquipment[attacker.getEquipment().getRingId()] == 2550) {
				attacker.playerEquipment[attacker.getEquipment().getRingId()] = -1;
				attacker.playerEquipmentN[attacker.getEquipment().getRingId()] = 0;
				attacker.getItems().wearItem(-1, 1, 12);
			}
			attacker.write(new SendMessagePacket("<col=9A289E>Your ring of recoil has shattered!"));
		}
	}

	@Override
	public boolean isExecutable(Player operator) {
		return true;
	}

}
