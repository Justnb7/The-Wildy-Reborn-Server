package com.model.game.character.npc;
 
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.model.game.character.combat.nvp.NPCCombatData;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
 
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
        for (Npc npc : player.localNpcs) {
            if (npc == null)
                continue;
            if (validate(npc, player)) {
                npc.killerId = player.getIndex();
            }
        }
    }
 
    /**
     * Determines if {@code npc} is able to target {@code player}.
     *
     * @param npc
     *            the npc trying to target the player.
     * @param player
     *            the player that is being targeted by the NPC.
     * @return {@code true} if the player can be targeted, {@code false}
     *         otherwise.
     */
    private static boolean validate(Npc npc, Player p) {
    	if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
			return true;
		}
    	if (npc.aggressive) {
    		return true;
    	}
    	if (!npc.getDefinition().isAggressive())
    		return false;
        if(npc.isPet)
            return false;
        if (!npc.getDefinition().isAggressive() && !npc.inMulti())
            return false;
        if (p.underAttackBy > 0 || p.underAttackBy2 > 0 && !p.getArea().inMulti())
            return false;
        if (p.heightLevel != npc.heightLevel || !p.isVisible())
            return false;
        if (alwaysAggressive(npc))
            return true;
        if (p.aggressionTolerance.elapsed(5, TimeUnit.MINUTES) && !npc.inMulti() && npc.getDefinition().getCombatLevel() < COMBAT_LEVEL_TOLERANCE) {
            return false;
        }
        if (!NPCHandler.goodDistance(p.absX, p.absY, npc.absX, npc.absY, AGGRESSION.getOrDefault(npc.npcId, TARGET_DISTANCE)))
            return false;
        if (npc.underAttack && !NPCCombatData.switchesAttackers(npc) || npc.isDead)
            return false;
        return true;
    }

	private static boolean alwaysAggressive(Npc npc) {
        return aggressiveNpcs.contains(npc.getId());
    }
}