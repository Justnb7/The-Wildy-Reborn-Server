package com.model.game.character.combat.weaponSpecial;

import com.model.game.character.Entity;
import com.model.game.character.combat.weaponSpecial.impl.*;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.Equipment;

import java.util.HashMap;
import java.util.Map;

/**
 * The class which represents functionality to load the data for the special attacks.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @date 13-12-2016
 */
public class SpecialAttackHandler {
	
	public static SpecialAttack get(int weapon) {
		if (!specials.containsKey(weapon))
			return null;
		return specials.get(weapon);
	}

	/**
	 * A map containing all of the special attacks
	 */
	private static Map<Integer, SpecialAttack> specials = new HashMap<Integer, SpecialAttack>();

	static {
		SpecialAttack whip = new AbyssalWhip();
		for (int i : whip.weapons()) {
			specials.put(i, whip);
		}
		SpecialAttack dd = new DragonDagger();
		for (int i : dd.weapons()) {
			specials.put(i, dd);
		}
		SpecialAttack msb = new MagicShortbow();
		for (int i : msb.weapons()) {
			specials.put(i, msb);
		}
		SpecialAttack db = new DarkBow();
		for (int i : db.weapons()) {
			specials.put(i, db);
		}
		SpecialAttack ad = new AbyssalDagger();
		for (int i : ad.weapons()) {
			specials.put(i, ad);
		}
		SpecialAttack ss = new SaradominSword();
		for (int i : ss.weapons()) {
			specials.put(i, ss);
		}
		
		SpecialAttack ch = new CrystalHalberd();
		for (int i : ch.weapons()) {
			specials.put(i, ch);
		}
		specials.put(3204, new DragonHalberd());
		specials.put(13263, new AbyssalBludgeon());
		specials.put(12006, new AbyssalTentacle());
		specials.put(11802, new ArmadylGodsword());
		specials.put(13652, new DragonClaws());
		specials.put(13576, new DragonWarhammer());
		specials.put(11806, new SaradominGodsword());
		specials.put(11808, new ZamorakGodsword());
		specials.put(12926, new ToxicBlowpipe());
		specials.put(11804, new BandosGodsword());
		specials.put(10887, new BarrelchestAnchor());
		specials.put(1305, new DragonLongsword());
		specials.put(1434, new DragonMace());
		specials.put(4587, new DragonScimitar());
		specials.put(4153, new GraniteMaul());
		specials.put(11785, new ArmadylCrossbow());
		specials.put(19780, new KorasiSword());
		specials.put(19481, new Ballista());
	}

	/**
	 * An array containing all of the special attack buttons
	 */
	private static final int[] BUTTONS = { 7462, 7512, 12311, 7562, 7537, 7667, 7687, 7587, 7612, 29138, 29163, 29199, 29074, 33033, 29238, 30007, 30108, 48034, 29049, 30043, 29124, 29213, 29063 };

	/**
	 * Handles all of the special attack buttons
	 * 
	 * @param player
	 *            The player activating the special attack
	 * @param buttonId
	 *            The id of the button pressed
	 * @return If a button has been pressed
	 */
	public static boolean handleButtons(Player player, int buttonId) {
		for (int i : BUTTONS) {
			if (buttonId == i) {
				if (player.getSpecialAmount() <= 0) {
					player.getActionSender().sendMessage("You do not have the required special amount.");
					return false;
				}
				
				int weapon = player.getEquipment().getId(Equipment.WEAPON_SLOT);

				if (weapon == 4153) {
					if(player.lastAttacker == null) { // nobody hit us
						return false;
					}
					SpecialAttack special = SpecialAttackHandler.forId(weapon);

					if (special == null) {
						System.out.println("Invalid special attack: " + weapon);
						Special.resetSpecial(player);
						return false;
					}

					if (player.getSpecialAmount() >= special.amountRequired()) {
						Entity target = player.getCombatState().getTarget();
						if (special.meetsRequirements(player, target)) {
							player.setSpecialAmount(player.getSpecialAmount() - special.amountRequired());
							special.handleAttack(player, target);
						}
					} else {
						player.getActionSender().sendMessage("You do not have the required special amount.");
					}
					Special.resetSpecial(player);
					return true;
				}
				player.setUsingSpecial(!player.isUsingSpecial());
				player.getWeaponInterface().refreshSpecialAttack();
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the special attack for the specific weapon id
	 * 
	 * @param id
	 * @return
	 */
	public static SpecialAttack forId(int id) {
		return specials.get(id);
	}
}