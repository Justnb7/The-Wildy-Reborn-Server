package com.model.game.character.combat.nvp;

import java.util.Arrays;
import java.util.List;

import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;

/**
 * Holds all of the combat data for npc combat
 * 
 * @author Mobster
 * @author Patrick van Elderen
 *
 */
public class NPCCombatData {

	/**
	 * A list of unspawnable npcs on death
	 */
	private static List<Integer> unspawnableNpcs = Arrays.asList(5779, 4303, 4304, 1605, 1606, 1607, 1608, 1609, 5054);

	/**
	 * Gets the list of unspawnwable npcs
	 * @return The list of unspawnable npcs
	 */
	public static List<Integer> getUnspawnableNpcs() {
		return unspawnableNpcs;
	}

	/**
	* Distanced required to attack
	**/	
	public static int distanceRequired(NPC npc) {
		if (AbstractBossCombat.isBoss(npc.getId())) {
			return AbstractBossCombat.get(npc.getId()).distance(null);
		}
		switch (npc.getId()) {
		
		case 1672: // Ahrim the Blighted
		case 1675: // Karil the Tainted
			return 6;
		case 2044: // Zulrah
		case 2043: // Zulrah
		case 2042: // Zulrah
			return 20;
		case 494: // Kraken
		case 492: // Cave Kraken
		case 5535: // Enormous tentacle
			return 20;
		case 3130: // Tstanon Karlak
		case 2206: // Starlight
			return 2;
		case 3121: // Tok-Xil
		case 3125: // Ket-Zek
		case 2167: // TzHaar-Xil
		case 3127: // TzTok-Jad
		case 2218:
		case 2217:
		case 6618:
		case 3164:
		case 3163:
		case 3162:
		case 319:
		case 2207:
			return 4;
		case 6616:
			return 4;

		case 6615:
			return 5;

		case 6766:
			return 3;

		case 6611:
		case 6612:
		case 2265:
		case 3428:
		case 5961:
		case 5947:
			return 12;

		case 2054:
			return 6;

		case 3165:
			return 2;
		default:
			return 1;
		}
	}

}
