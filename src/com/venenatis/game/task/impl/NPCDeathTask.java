package com.venenatis.game.task.impl;

import com.venenatis.game.content.KillTracker;
import com.venenatis.game.content.KillTracker.KillEntry;
import com.venenatis.game.content.skills.slayer.SlayerTaskManagement;
import com.venenatis.game.content.sounds_and_music.sounds.MobAttackSounds;
import com.venenatis.game.location.Location;
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
            setNpcToInvisible(npc);
            Player killer = World.getWorld().getPlayers().get(npc.spawnedBy);
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
            if (killer != null || isUnspawnableNpc(npc) || !npc.shouldRespawn) {
            	handleUnspawnableNpc(npc);
				removeMobFromWorld(killer, npc);
				stop();
				return;
			}
        } else if (counter == 6) {
        	if (GroupRespawn.check_groups(npc)) {
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
        if (player != null && !player.getCombatState().isDead()) {}
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
        npc.setLocation(Location.create(npc.makeX, npc.makeY)); // No height level change assumed
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

        if (killer != null) {
            npc.killedBy = killer.getIndex();
        } else {
            npc.killedBy = -1;
        }
        if (npc.getId() == 6618 && npc.getCombatState().isDead()) {
        	npc.sendForcedMessage("Ow!");
        }
        if (npc.getId() == 6615 && npc.getCombatState().isDead()) {
        	npc.spawnedScorpiaMinions = false;
        }
        if (npc.getId() == 6611 && npc.transformId != 6612) {
			npc.requestTransform(6612);
			npc.getUpdateFlags().flag(UpdateFlag.TRANSFORM);
			npc.setHitpoints(255);
			npc.getCombatState().setDead(false);
			npc.spawnedVetionMinions = false;
			npc.sendForcedMessage("Do it again!!");
			stop();
			return;
		} else {
			if (npc.getId() == 6612) {
				npc.setId(6611);
				npc.spawnedVetionMinions = false;
				npc.sendForcedMessage("Got'em");
			}
		}
        if (npc.killedBy >= 0) {
			Player player = World.getWorld().getPlayers().get(npc.killedBy);
			if (player != null) {
				if (npc.getId() == player.getSlayerTask() || NPC.getName(npc.getId()).toLowerCase().equalsIgnoreCase(NPC.getName(player.getSlayerTask()).toLowerCase())) {
					SlayerTaskManagement.decreaseTask(player, npc);
				}
				Combat.resetCombat(player);
			}
		}

		if (npc.getId() == 6613 || npc.getId() == 6614) {
        	for (NPC i : World.getWorld().getNPCs()) {
        		if(i == null)
        			continue;
        		if(i.getId() == 6611 || i.getId() == 6612 && i.dogs > 0) {
        			i.dogs--;
        		}
        	}
        }
		
        if (npc.getId() != 6611) // vetion or somet
			npc.playAnimation(Animation.create(npc.getDeathAnimation())); // dead emote

        if (npc.transformId == 6612) {
			npc.requestTransform(6611);
			npc.getUpdateFlags().flag(UpdateFlag.TRANSFORM);
		}
       
        if (!npc.noDeathEmote) {
            npc.playAnimation(Animation.create(npc.getDeathAnimation())); // dead
        }
        
        if (killer != null) {
            MobAttackSounds.sendDeathSound((Player) killer, npc.getId());
        }
        
        reset(npc);
    }
    
    public static void reset(NPC npc) {
    	npc.infected = false;
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
        npc.killedBy = -1;
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
		
		Player player = World.getWorld().getPlayers().get(npc.killedBy);
		
    	if (npc.killedBy == -1) {
    		player.getActionSender().sendMessage("[Server]: ERROR, notice staff member, killerId = -1.");
			return;
		}

		if (npc != null || player != null) {
			/* Add kills to tracker */
			for (int id : NPC.BOSSES) {
				if (npc.getId() == id) {
					KillTracker.submit(player, new KillEntry(npc.getName(), 1), true);
				}
			}
			if (npc.getId() == player.getSlayerTask())
				player.getSlayerDeathTracker().add(npc);

			AbstractBossCombat boss = AbstractBossCombat.get(npc.getId());

			if (boss != null) {
				boss.dropLoot(player, npc);
			}
			// get the drop table
			int amountOfDrops = 1;//Here
			Server.getDropManager().create(player, npc, npc.getLocation(), amountOfDrops);//Maybe we move this to top?
		}
	}

}
