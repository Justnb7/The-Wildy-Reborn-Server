package com.venenatis.game.content.minigames.singleplayer;

import java.util.Random;

import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;

public interface Minigame {

	public Random random = new Random();

	boolean enter(Player player);

	void exit(Player player);

	void tick(Player player);

	default void onLogout(Player player) {
		exit(player);
	}

	default boolean onDamage(Player player, Hit damage) {
		return false;
	}

	default boolean onDamage(Player player, Entity entity) {
		return false;
	}

	default boolean onDropItems(Player player, NPC npc) {
		return false;
	}

	public void onDeath(Player player);
}