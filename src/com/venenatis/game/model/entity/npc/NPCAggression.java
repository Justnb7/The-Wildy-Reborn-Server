package com.venenatis.game.model.entity.npc;
 
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.venenatis.game.model.boudary.BoundaryManager;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.NpcCombat;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;
 
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
    private static final int TARGET_DISTANCE = 4;
 
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
    	final Collection<NPC> npcs = RegionStoreManager.get().getLocalNpcs(player);
        for (NPC npc : npcs) {
    	// for (NPC npc : player.getLocalNPCs()) {
            if (npc == null)
                continue;
            // Can the Npc attack the <player>? Will check distance, clipping, slayer level req etc. 
            if (validate(npc, player)) {
            	//System.out.println("npc "+npc.getName()+" will agro "+player.getName());
            	npc.getCombatState().setTarget(player);
                npc.faceEntity(player);
            } else {
          /*  	//added this so the npc's return to randomwalking..
            	npc.resetFaceEntity();
            	npc.getCombatState().reset();
            	npc.randomWalk = true;*/
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
    	//previous code would not allow all npc's in multi to attack. Would cause some to sit and watch with no attack.
    	if (npc.getCombatState().getTarget() != null && (!BoundaryManager.isWithinBoundaryNoZ(p.getLocation(), "multi_combat")) || npc.getCombatState().isDead() || Combat.incombat(p) && (!BoundaryManager.isWithinBoundaryNoZ(p.getLocation(), "multi_combat"))) {
    		//npc.sendForcedMessage("Agro 2");
    		return false;
    	}
    	if (!npc.getDefinition().isAggressive()) {
    		return false;
    	}
        if (p.getZ() != npc.getZ() || !p.isVisible()) {
            return false;
        }
        if (p.aggressionTolerance.elapsed(5, TimeUnit.MINUTES) && !BoundaryManager.isWithinBoundaryNoZ(npc.getLocation(), "multi_combat") && npc.getDefinition().getCombatLevel() < COMBAT_LEVEL_TOLERANCE) {
        	//p.getActionSender().sendMessage("Agro 3");
        	return false;
        }
        // Bad distance
        
        //if the npc has a greater target distance then agro range will vary.
       /* if(npc.getCombatLevel() < 150) {
        if(NpcCombat.distanceRequired(npc) <= TARGET_DISTANCE) {*/
        if (!npc.distance(p.getX(), p.getY(), npc.getX(), npc.getY(), AGGRESSION.getOrDefault(npc.getId(), TARGET_DISTANCE))) {
        	//p.getActionSender().sendMessage("Agro 4");
        	return false;
       // }}
       } 
        // At a most basic level, if you get to here, the npc is alive, in distance etc
    	if (npc.aggressive || alwaysAggressive(npc) || npc.getDefinition().isAggressive()) {
    		//p.getActionSender().sendMessage("Agro 5");
    		return true;
    	}
        return false;
    }

	private static boolean alwaysAggressive(NPC npc) {
        return aggressiveNpcs.contains(npc.getId());
    }
}