package com.venenatis.game.model.combat.special_attacks;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.minigames.multiplayer.duel_arena.DuelArena.DuelOptions;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.special_attacks.impl.*;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;

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
		
		SpecialAttack gm = new GraniteMaul();
		for (int i : gm.weapons()) {
			specials.put(i, gm);
		}
		
		SpecialAttack ds = new DragonSpear();
		for (int i : ds.weapons()) {
			specials.put(i, ds);
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
		specials.put(11785, new ArmadylCrossbow());
		specials.put(19780, new KorasiSword());
		specials.put(19481, new Ballista());
		specials.put(13899, new VestaLongsword());
		specials.put(13902, new StatiusWarhammer());
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
		
		if(player.getDuelArena().getOptionActive()[DuelOptions.NO_SPECIAL_ATTACK.getId()]) {
			player.message("The rights to use special attacks has been revoked during this duel.");
			return false;
		}
		
		for (int i : BUTTONS) {
			if (buttonId == i) {
				if (player.getSpecialAmount() <= 0) {
					player.message("You do not have the required special amount.");
					return false;
				}
				
				Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);

				if (weapon.getId() == 4153 || weapon.getId() == 12848) {
					if(player.lastAttacker == null) { // nobody hit us
						return false;
					}
					SpecialAttack special = SpecialAttackHandler.forId(weapon.getId());

					if (special == null) {
						System.out.println("Invalid special attack: " + weapon);
						Special.resetSpecial(player);
						return false;
					}

					if (player.getSpecialAmount() >= special.amountRequired()) {
						Entity target = player.getCombatState().getLastTarget();
						if (special.meetsRequirements(player, target)) {
							player.setSpecialAmount(player.getSpecialAmount() - special.amountRequired() + player.getVigour());
							special.handleAttack(player, target);
							Special.refreshSpecial(player);
						}
					} else {
						player.message("You do not have the required special amount.");
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