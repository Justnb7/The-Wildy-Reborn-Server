package com.model.game.character.combat.nvp;

import java.util.Arrays;
import java.util.List;

import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Bosses;

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

	public static boolean retaliates(int npcType) {
		return !(npcType > 1531 && npcType < 1536) && (npcType < 1738 || npcType > 1743 && !(npcType >= 2440 && npcType <= 2446));
	}
	
	/**
	 * Emotes
	 */
	public static int getAttackEmote(Npc npc) {
		if (Bosses.isBoss(npc.npcId)) {
			return Bosses.get(npc.npcId).getAttackEmote(npc);
		}
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
	 * Attack delays
	 */
	public static int getNpcDelay(Npc npc) {
		if (Bosses.isBoss(npc.npcId)) {
			return Bosses.get(npc.npcId).getAttackDelay(npc);
		}
		switch (npc.npcId) {
		case 7497:
			return 6;
		case 1672:
		case 1675:
		case 5996:
			return 7;
		case 3127:
			return 8;
		case 6260:
			return 7;
		case 6204:
			return 8;
		case 6208:
		case 2043:
			return 8;
		case 2205:
			return 4;
		default:
			return npc.getDefinition().getAttackSpeed();
		}
	}

	/**
	 * Hit delays
	 */
	public static int getHitDelay(Npc npc) {
		if (Bosses.isBoss(npc.npcId)) {
			return Bosses.get(npc.npcId).getHitDelay(npc);
		}
		switch (npc.npcId) {
		
		case 7497:
			return 5;
		
		case 5535:
			return 5;
		
		case 6607:
		case 6616:
			return 4;
		
		case 492:
			return 4;
			
		case 6528:
			
		case 4303:
		case 4304:
			if (npc.attackStyle == 1 || npc.attackStyle == 2) {
				return 3;
			} else {
				return 2;
			}
			
		case 2043:
			return 6;
			
		case 3125:
		case 3121:
		case 2167:
			return 3;

		case 3127:
			if (npc.attackStyle == 1 || npc.attackStyle == 2)
				return 5;
			else
				return 2;

		case 1672:
			return 4;
			
		case 1675:
			return 3;
			
		case 6361:
			if (npc.attackStyle == 2)
				return 4;
			else if (npc.attackStyle == 1)
				return 4;
			else
				return 2;
			
		case 319:
			return 5;
			
		default:
			return 2;
		}
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

		case 6361:
			if (npc.attackStyle == 2)
				return 150;
			else if (npc.attackStyle == 1)
				return 100;
			else if (npc.attackStyle == 0)
				return 100;
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
	 * Gets the offset for the npc
	 * 
	 * @param npc
	 *            The {@link Npc} to get the offset for
	 * @return The offset for the npc
	 */
	public static int offset(Npc npc) {
		if (Bosses.isBoss(npc.npcId)) {
			return Bosses.get(npc.npcId).offSet(npc);
		}
		switch (npc.npcId) {
		case 2044:
			return 0;
			
		case 6581:
		case 6580:
			return 1;
			
		case 3127:
		case 3125:
			return 1;
			
		}
		return 0;
	}

	public static boolean switchesAttackers(Npc npc) {
		if (npc == null)
			return false;
		if (Bosses.isBoss(npc.npcId)) {
			return Bosses.get(npc.npcId).switchesAttackers();
		}
		switch (npc.npcId) {
		case 5579:
		case 3943: //Giant Sea Snake
		case 763: //Giant Roc
		case 1066: //Fear Reaper
		case 4693: //Fear Reaper
			return true;

		}

		return false;
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
		if (Bosses.isBoss(npc.npcId)) {
			return Bosses.get(npc.npcId).distanceRequired(npc);
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

		case 239:
			return npc.attackStyle > 0 ? 6 : 1;

		case 2054:
			return 6;

		case 3165:
			return 2;
		default:
			return 1;
		}
	}

}
