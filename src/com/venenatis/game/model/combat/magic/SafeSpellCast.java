package com.venenatis.game.model.combat.magic;

import java.util.function.BiFunction;

import com.venenatis.game.content.achievements.*;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public enum SafeSpellCast {

	VENGENCE_OTHER("Vengence other", 30298, 93, 108, new Item[] { new Item(9075, 3), new Item(560, 2), new Item(557, 10) }, (player, other) -> {
		if (player.getSkills().getLevel(Skills.DEFENCE) < 40) {
			player.getActionSender().sendMessage("You need a defence level of 40 to cast this spell!");
			return false;
		}
		
		if (System.currentTimeMillis() - player.getMagic().getLastVengeance() < 30_000L) {
			player.getActionSender().sendMessage("You can only cast vengeance once every 30 seconds.");
			return false;
		}
		
		player.face(player, other.getLocation());
		player.playAnimation(new Animation(4411));
		other.playGraphics(Graphic.create(725, 100));
		other.setVengeance(true);
		player.getMagic().setLastVengeance(System.currentTimeMillis());
		player.getActionSender().sendWidget(2, 30);
		player.getActionSender().sendMessage("You have given vengeance to " + Utility.formatName(other.getUsername()) + ".");
		other.getActionSender().sendMessage("You have been given vengeance by " + Utility.formatName(player.getUsername()) + ".");
		AchievementHandler.activate(player, AchievementList.THE_GIVER, 1);
		return true;
	}),
	
	;
	
	private final String name;
	private final int id;
	private final int level;
	private final double experience;
	private final Item[] runes;
	private BiFunction<Player, Player, Boolean> execute;
	
	private SafeSpellCast(String name, int id, int level, double experience, Item[] runes, BiFunction<Player, Player, Boolean> execute) {
		this.name = name;
		this.id = id;
		this.level = level;
		this.experience = experience;
		this.runes = runes;
		this.execute = execute;
	}

	public boolean execute(Player player, Player other) {
		return execute.apply(player, other);
	}

	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}
	
	public double getExperience() {
		return experience;
	}

	public Item[] getRunes() {
		return runes;
	}

	public static SafeSpellCast of(int spellId) {
		for (SafeSpellCast spell : values()) {
			if (spell.getId() == spellId) {
				return spell;
			}
		}
		return null;
	}
	
}