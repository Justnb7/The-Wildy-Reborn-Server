package com.model.game.character.combat;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendString;

public class Combat {
	
	 /**
     * The attack stab bonus identifier.
     */
    public static final int ATTACK_STAB = 0;

    /**
     * The attack slash bonus identifier.
     */
    public static final int ATTACK_SLASH = 1;

    /**
     * The attack crush bonus identifier.
     */
    public static final int ATTACK_CRUSH = 2;

    /**
     * The attack magic bonus identifier.
     */
    public static final int ATTACK_MAGIC = 3;

    /**
     * The attack ranged bonus identifier.
     */
    public static final int ATTACK_RANGED = 4;

    /**
     * The defence stab bonus identifier.
     */
    public static final int DEFENCE_STAB = 5;

    /**
     * The defence slash bonus identifier.
     */
    public static final int DEFENCE_SLASH = 6;

    /**
     * The defence crush bonus identifier.
     */
    public static final int DEFENCE_CRUSH = 7;

    /**
     * The defence magic bonus identifier.
     */
    public static final int DEFENCE_MAGIC = 8;

    /**
     * The defence ranged bonus identifier.
     */
    public static final int DEFENCE_RANGED = 9;

    /**
     * The strength bonus identifier.
     */
    public static final int BONUS_STRENGTH = 10;

    /**
     * The prayer bonus identifier.
     */
    public static final int BONUS_PRAYER = 11;
    
    /**
     * The names of all the bonuses in their exact identified slots.
     */
    public static final String[] BONUS_NAMES = { "Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range",
            "Strength", "Prayer", "", "" };

	/**
	 * Sets a check so the players max hit will be based on the used items when
	 * the attack got performed.
	 */
	public static void setArmourCheck(Player player) {
		for (int armourSlot = 0; armourSlot < player.lastWeapon.length; armourSlot++) {
			player.lastWeapon[armourSlot] = player.playerEquipment[armourSlot];
		}
	}

	/**
	 * Checks the used item when the attack got performed.
	 */
	public static int getArmour(Player player, int slot) {
		return player.lastWeapon[slot];
	}
	
	public static void resetCombat(Player player) {
		player.usingMagic = false;
		player.faceUpdate(0);
		player.npcIndex = 0;
		player.playerIndex = 0;
		player.getPA().resetFollow();
		player.setInCombat(false);
		player.write(new SendString("", 35000));
	}
}