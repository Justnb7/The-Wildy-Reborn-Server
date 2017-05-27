package com.model.game.character.npc;
 
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
 
/**
 * The static utility class that handles the behavior of aggressive NPCs within
 * a certain radius of players.
 *
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public final class NPCAggression {
 
    public static final Map<Integer, Integer> AGGRESSION = new HashMap<>();
 
    /**
     * The absolute distance that players must be within to be targeted by
     * aggressive NPCs.
     */
    private static final int TARGET_DISTANCE = 10;
 
    private static final int COMBAT_LEVEL_TOLERANCE = 100;
 
    private static List<Integer> aggressiveNpcs = Arrays.asList(6476, 5421, 3359, 5535);
 
    /**
     * The sequencer that will prompt all aggressive NPCs to attack
     * {@code player}.
     *
     * @param player
     *            the player that will be targeted by aggressive NPCs.
     */
    public static void process(Player player) {
    	//System.out.println("agro check for "+player.getName());
        for (NPC npc : player.getLocalNPCs()) {
            if (npc == null)
                continue;
            // Can the Npc attack the <player>? Will check distance, clipping, slayer level req etc. 
            if (validate(npc, player)) {
            	//System.out.println("npc "+npc.getName()+" will agro "+player.getName());
                npc.targetId = player.getIndex();
            }
        }
    }
 
    /**
     * Determines if {@code npc} is able to target {@code player}.
     *
     * @param npc
     *            the npc trying to target the player.
     * @param p
     *            the player that is being targeted by the NPC.
     * @return {@code true} if the player can be targeted, {@code false}
     *         otherwise.
     */
    // Aggression check for the circumstance where a player might run past us. Does NOT
    // have anything to do with retaliation/target switching.
    private static boolean validate(NPC npc, Player p) {
    	// We're already attacking something or under attack.
    	// When we get it, retalition handles changing target, not this agro code.
    	if (npc.targetId > 0 || npc.isDead() || npc.underAttack || Combat.incombat(p)) {
    		return false;
    	}
    	if (!npc.getDefinition().isAggressive()) {
    		return false;
    	}
        if(npc.isPet) {
            return false;
        }
        if (p.heightLevel != npc.heightLevel || !p.isVisible()) {
            return false;
        }
        if (p.aggressionTolerance.elapsed(5, TimeUnit.MINUTES) && !npc.inMulti() && npc.getDefinition().getCombatLevel() < COMBAT_LEVEL_TOLERANCE) {
        	return false;
        }
        // Bad distance
        if (!npc.distance(p.absX, p.absY, npc.absX, npc.absY, AGGRESSION.getOrDefault(npc.getId(), TARGET_DISTANCE))) {
            return false;
        }
        // At a most basic level, if you get to here, the npc is alive, in distance etc
    	if (npc.aggressive || alwaysAggressive(npc) || npc.getDefinition().isAggressive()) {
    		return true;
    	}
        return false;
    }

	private static boolean alwaysAggressive(NPC npc) {
        return aggressiveNpcs.contains(npc.getId());
    }
}