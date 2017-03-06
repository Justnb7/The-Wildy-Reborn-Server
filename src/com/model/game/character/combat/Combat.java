package com.model.game.character.combat;

import com.model.game.character.player.Player;
import com.model.game.location.Position;

public class Combat {
    
    /**
     * The names of all the bonuses in their exact identified slots.
     */
    public static final String[] BONUS_NAMES = { "Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range",
            "Strength", "Prayer", "", "" };
	
	public static void resetCombat(Player player) {
		player.usingMagic = false;
		player.resetFace();
		player.npcIndex = 0;
		player.playerIndex = 0;
		player.getPA().resetFollow();
		player.setInCombat(false);
		player.getActionSender().sendString("", 35000);
	}
}