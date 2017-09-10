package com.venenatis.game.task.impl;

import com.venenatis.game.content.KillTracker;
import com.venenatis.game.content.KillTracker.KillEntry;
import com.venenatis.game.content.skills.slayer.SlayerTaskManagement;
import com.venenatis.game.content.skills.slayer.SuperiorMonster;
import com.venenatis.game.content.sounds_and_music.sounds.MobAttackSounds;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.NpcCombat;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.npc.GroupRespawn;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;

/**
 * Handles respawning an {@link NPC} which has just died
 * 
 * @author Mobster
 * @author lare96 <http://github.com/lare96>
 * @author Patrick van Elderen
 */
public class NPCDeathTask extends Task {

    /**
     * The time to tick while waiting to return the npc
     */
    private int counter;

    /**
     * Constructs a new npc death task which will reset the npc after its died
     * 
     * @param npc
     *            The {@link NPC} which has died
     */
    public NPCDeathTask(NPC npc) {
        super(1, true);
        attach(npc);
        npc.getCombatState().setDead(true);
    }

    @Override
    public void execute() {
        NPC npc = (NPC) getAttachment();

        if (npc == null || World.getWorld().getNPCs().get(npc.getIndex()) == null) {
            stop();
            return;
        }
        if (counter == 0) {
            initialDeath(npc);
        } else if (counter == 5) {
            npc.removeFromTile();
            Player killer = World.getWorld().getPlayers().get(npc.spawnedBy);
            setNpcToInvisible(npc);
            if (killer != null && killer.getKraken() != null && killer.getKraken().npcs != null && killer.getKraken().npcs[0] != null) {
            	if (npc == killer.getKraken().npcs[0]) {
            		for (NPC n : killer.getKraken().npcs) {
            			if (n.getId() == 5535) {
            				// kill off tents
            				n.getCombatState().setDead(true);
            				Server.getTaskScheduler().schedule(new NPCDeathTask(n));
            			}
            		}
            		// set next wave AFTER LOGIC ABOVE
    				killer.getKraken().spawnNextWave(killer);
            	}
			}
            if (killer != null || isUnspawnableNpc(npc)) {
            	handleUnspawnableNpc(npc);
				removeMobFromWorld(killer, npc);
				stop();
				return;
			}
        } else if (counter == 6) {
        	if (GroupRespawn.is_my_boss_dead(npc)) {
        		stop();
        		return;
        	}
        	if (npc.getDefinition().getRespawnTime() == -1) {// this npc does not respawn
        		stop();
        	}
        } else if (counter == (6 + npc.getDefinition().getRespawnTime())) { //60s later
        	respawn(npc);
            GroupRespawn.on_boss_spawned(npc);
            stop();
            return;
        }
        counter++;
    }

    /**
     * Handles the unspawnable npcs upon death
     * 
     * @param npc
     *            The {@link NPC} that has died
     */
    private void handleUnspawnableNpc(NPC npc) {

    }

    /**
     * Checks if the npc is unspawnable
     * 
     * @param npc
     *            The {@link NPC} to check if its unspawnable
     * @return If the npc is unspawnable
     */
    private boolean isUnspawnableNpc(NPC npc) {
        return NpcCombat.getUnspawnableNpcs().contains(npc.getId());
    }

    /**
     * Removes custom spawned npc & minigame npc so they do not respawn
     * 
     * @param player
     *            The {@link Player} who has killed this npc
     */
	private void removeMobFromWorld(Player player, NPC npc) {
		if (player != null && !player.getCombatState().isDead())
			World.getWorld().unregister(npc);
	}

    /**
     * We set the npc to invisible and restore its values
     * 
     * @param npc
     */
    public static void setNpcToInvisible(NPC npc) {
        npc.removeFromTile();
        onDeath(npc);
        npc.setVisible(false);
        npc.setLocation(Location.create(npc.makeX, npc.makeY, npc.getZ()));
        npc.setHitpoints(npc.getMaxHitpoints());
		
        if (!npc.noDeathEmote) {
            npc.playAnimation(Animation.create(808));
        }
        
        npc.getCombatState().getDamageMap().resetDealtDamage();

        if (npc.getId() == 492 && npc.getCombatState().isDead()) {
        	npc.requestTransform(493);
        	npc.getUpdateFlags().flag(UpdateFlag.TRANSFORM);
        }
    }

    /**
     * The npc has just died, so we perform the death emote and reset facing
     * 
     * @param npc
     *            The {@link NPC} which as just died
     */
    private void initialDeath(NPC npc) {
        npc.resetFace();

        final Player killer = World.getWorld().lookupPlayerByName(npc.getCombatState().getDamageMap().getKiller());
        
        //Sent the cheer animation when defeating Jad
        if(killer != null || npc != null) {
        	if(npc.getId() == 3127) {
        		killer.playAnimation(Animation.create(862));
        		killer.getFightCave().reward(killer);
        	}
        }
        
		if (killer != null) {
			//killer.debug(String.format("we killed a %s and we have %s as slayer task.", npc.getName(), killer.getSlayerTask()));
			if (npc.getName().contains(killer.getSlayerTask())) {
				SlayerTaskManagement.decreaseTask(killer, npc);
				SuperiorMonster.spawnSuperior(killer, npc);
			} else if(npc.getName().equalsIgnoreCase("whirlpool") && killer.getSlayerTask().equalsIgnoreCase("kraken")) {
				killer.setSlayerTaskAmount(killer.getSlayerTaskAmount() - 1);
				killer.getSkills().addExperience(Skills.SLAYER, npc.getMaxHitpoints());
				killer.getActionSender().sendString("<img=17><col=FFFFFF>Task: <col=00CC00>" + killer.getSlayerTaskAmount() + " " + killer.getSlayerTask(), 29172);
			}
			Combat.resetCombat(killer);
		}
		
        if (npc.getId() != 6611) // vetion or somet
			npc.playAnimation(Animation.create(npc.getDeathAnimation())); // dead emote
       
        if (!npc.noDeathEmote) {
            npc.playAnimation(Animation.create(npc.getDeathAnimation())); // dead
        }
        
        if (killer != null) {
            MobAttackSounds.sendDeathSound((Player) killer, npc.getId());
        }
        
        reset(npc);
    }
    
    public static void reset(NPC npc) {
    	npc.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
        npc.freeze(0);
        npc.targetId = 0;
        npc.underAttack = false;
        npc.resetFace();
    }

    /**
     * The mob is respawned and set to visible to be updated again
     *
     */
    public static void respawn(NPC npc) {
        npc.setVisible(true);
        npc.getCombatState().setDead(false);
        if(npc.getId() == 5779) {
        	 npc.setOnTile(1762, 5184, 0);
        	 npc.makeX = 1762;
        	 npc.makeY = 5184;
        	 npc.setLocation(new Location(1762, 5184, 0));
        } else {
        	npc.setOnTile(npc.getX(), npc.getY(), npc.getZ());
        }
    }
    
    private final static void onDeath(NPC npc) {
    	
    	Player killer = World.getWorld().lookupPlayerByName(npc.getCombatState().getDamageMap().getKiller());
		
		if(killer == null) {
			return;
		}

		if (npc != null) {
			/* Add kills to tracker */
			for (int id : NPC.BOSSES) {
				if (npc.getId() == id) {
					KillTracker.submit(killer, new KillEntry(npc.getName(), 1), true);
				}
			}

			AbstractBossCombat boss = AbstractBossCombat.get(npc.getId());

			if (boss != null) {
				boss.dropLoot(killer, npc);
			}
			// get the drop table
			int amountOfDrops = 1;
			Server.getDropManager().create(killer, npc, npc.getLocation(), amountOfDrops);
		}
	}

}
