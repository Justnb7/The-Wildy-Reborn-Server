package com.model.game.character.combat;

import java.util.ArrayList;

/**
 * Calculates the amount of damage done to the entity
 * 
 * @author Arithium
 * 
 */
public class DamageMap {

	private ArrayList<Damage> dealtDamage = new ArrayList<Damage>();

	public void appendDamage(String player, int damage) {
		boolean isAdded = false;
		for (Damage aDealtDamage : dealtDamage) {
			if (aDealtDamage.getPlayer().equals(player)) {
				aDealtDamage.addDamage(damage);
				isAdded = true;
				break;
			}
		}
		if (!isAdded) {
			dealtDamage.add(new Damage(player, damage));
			System.out.println("adding damage to map: "+damage);
		}
	}

	public String getKiller() {
		String killer = "";//this string isnt being filled
		int mostDamage = 0;
		System.out.println("map size: "+dealtDamage.size());
		for (Damage aDealtDamage : dealtDamage) {
			System.out.println(mostDamage+" vs. "+aDealtDamage.damage);//try
			if (aDealtDamage.getDamage() > mostDamage) {
				killer = aDealtDamage.getPlayer();
				mostDamage = aDealtDamage.getDamage();
			}
		}
		return killer;
	}

	public void resetDealtDamage() {
		dealtDamage.clear();
	}

	private static class Damage {

		public String getPlayer() {
			return player;
		}

		public int getDamage() {
			return damage;
		}

		public void addDamage(int amount) {
			this.damage += damage;
		}

		private String player = "";
		private int damage = 0;

		private Damage(String player, int damage) {
			this.player = player;
			this.damage = damage;
		}
	}
}