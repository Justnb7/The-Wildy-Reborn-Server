package com.model.game.character.combat.nvp;

import java.util.Arrays;
import java.util.List;

import com.model.game.character.combat.npcs.BossScripts;
import com.model.game.character.npc.Npc;
import com.model.utility.json.definitions.NpcDefinition;

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
	 * Get's the npcs defending animation.
	 */
	public static int getNPCBlockAnimation(Npc npc) {
		return NpcDefinition.getDefinitions()[npc.npcId].getDefenceAnimation();
	}
	
	/**
	 * Emotes
	 */
	public static int getAttackEmote(Npc npc) {
		if (npc.npcId >= 1694 && npc.npcId <= 1703) {
			return 3901;
		}
		if (npc.npcId >= 1704 && npc.npcId <= 1708) {
			return 3915;
		}
		return npc.getDefinition().getAttackAnimation();
	}
	
	public static int getDeadEmote(Npc npc) {
		return npc.getDefinition().getDeathAnimation();
	}
	
	/**
	 * Gets the projectile speed for the npc
	 * 
	 * @param npc
	 *            The {@link Npc} to fetch the projectile speed for
	 * @return The projectile speed for the npc
	 */
	public static int getProjectileSpeed(Npc npc) {
		switch (npc.npcId) {
		
		case 5535:
			return 100;
		
		case 6581:
		case 6580:
			return 85;
			
		case 7497:
			return 85;

		case 2265:
		case 2266:
		case 2054:
		case 2837:
			return 85;
		case 4303:
		case 4304:
			return 90;
		case 3127:
			return 130;
		case 742:
		case 3590:
		case 5779:
			return 90;

		case 1672:
			return 85;

		case 1675:
			return 80;

		default:
			return 85;
		}
	}
	
	public static int getProjectileStartHeight(int npcType, int projectileId) {
		switch (npcType) {
		case 3127:
			return 110;
		case 2044:
			return 60;
		case 3163:
		case 3164:
		case 3165:
			return 60;
		case 492:
			return 30;
		case 6610:
			switch (projectileId) {
			case 165:
				return 20;
			}
			break;
		}
		return 43;
	}

	public static int getProjectileEndHeight(int npcType, int projectileId) {
		switch (npcType) {
		case 6610:
			switch (projectileId) {
			case 165:
				return 30;
			}
			break;
		}
		return 31;
	}

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
	public static int distanceRequired(Npc npc) {
		if (BossScripts.isBoss(npc.npcId)) {
			return BossScripts.get(npc.npcId).distance(null);
		}
		switch (npc.npcId) {
		
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
