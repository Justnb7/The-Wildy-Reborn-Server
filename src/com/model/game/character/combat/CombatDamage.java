package com.model.game.character.combat;

public class CombatDamage {
	
	private long time;
	
	private int damage;
	
	public CombatDamage(int damage) {
		this.damage = damage;
		this.time = System.currentTimeMillis();
	}
	
	public void add(int damage) {
		this.damage += damage;
	}

	public int getDamage() {
		return damage;
	}
	
	public long getTimeInMillis() {
		return time;
	}
}
