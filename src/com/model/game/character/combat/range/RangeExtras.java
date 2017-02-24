package com.model.game.character.combat.range;

import java.util.Random;

import com.model.game.World;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;

public class RangeExtras {

	private static void createCombatGFX(Player c, int i, int gfx, boolean height100) {
		Player p = World.getWorld().getPlayers().get(i);
		Npc n = World.getWorld().getNpcs().get(i);
		if (c.playerIndex > 0) {
			if (height100) {
				p.playGraphics(Graphic.create(gfx, 0, 0));
			} else {
				p.playGraphics(Graphic.create(gfx, 0, 0));
			}
		} else if (c.npcIndex > 0) {
			if (height100) {
				n.playGraphics(Graphic.create(gfx, 0, 100));
			} else {
				n.playGraphics(Graphic.create(gfx, 0, 0));
			}
		}
	}
	
	/**
	 * The random number generator.
	 */
	protected final static Random random = new Random();

	public static void crossbowSpecial(Player player, int i) {
		if (i > World.getWorld().getPlayers().capacity() || i > World.getWorld().getNpcs().capacity()) {
			return;
		}
		Player p = World.getWorld().getPlayers().get(i);
		Npc npc = World.getWorld().getNpcs().get(i);
		switch (player.lastArrowUsed) {
		case 9236: // Lucky Lightning
			createCombatGFX(player, i, 749, false);
			break;
		case 9237: // Earth's Fury
			createCombatGFX(player, i, 755, false);
			break;
		case 9238: // Sea Curse
			createCombatGFX(player, i, 750, false);
			break;
		case 9239: // Down to Earth
			createCombatGFX(player, i, 757, false);
			break;
		case 9240: // Clear Mind
			createCombatGFX(player, i, 751, false);
			if (player.playerIndex > 0) {
				int prayerReduction = random.nextInt(9);
				int before = p.getSkills().getLevel(Skills.PRAYER);
				p.getSkills().decreaseLevelToZero(Skills.PRAYER, prayerReduction);
				int addition = before - p.getSkills().getLevel(Skills.PRAYER);
				player.getSkills().increaseLevelToMaximum(Skills.PRAYER, addition);
			}
			break;
		case 9241: // Magical Posion
			createCombatGFX(player, i, 752, false);
			break;
		case 9242: // Blood Forfiet
			createCombatGFX(player, i, 754, false);
			int damageCap = (int) (npc.currentHealth * 0.2);
			if (npc.npcIndex > 0) {
				if (npc.npcId == 319 && damageCap > 100)
					damageCap = 100;
				npc.damage(new Hit(damageCap));
				player.playGraphics(Graphic.create(754, 0, 0));
			} else if (player.playerIndex > 0) {
				player.playGraphics(Graphic.create(754, 0, 0));
			}
			break;
		case 9243: // Armour Piercing
			createCombatGFX(player, i, 758, true);
			player.ignoreDefence = true;
			break;
		case 9244: // Dragon's Breath
			createCombatGFX(player, i, 756, false);
			break;
		case 9245: // Life Leech
			createCombatGFX(player, i, 753, false);
			break;
		}
	}
}