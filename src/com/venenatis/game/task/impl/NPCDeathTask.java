package com.venenatis.game.task.impl;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.KillTracker.KillEntry;
import com.venenatis.game.content.activity.minigames.impl.warriors_guild.AnimatedArmour;
import com.venenatis.game.content.skills.slayer.SlayerTaskManagement;
import com.venenatis.game.content.skills.slayer.SuperiorMonster;
import com.venenatis.game.content.sounds_and_music.sounds.MobAttackSounds;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.npc.GroupRespawn;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Location3D;
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

        NPC index = World.getWorld().getNPCs().get(npc.getIndex());
        
        if (index == null) {
            //System.out.println("Something went wrong the npc is null "+npc.getName()+" with an index "+npc.getIndex()+" of "+index);
        	stop();
            return;
        }
        
        
        final Player killer = World.getWorld().lookupPlayerByName(npc.getCombatState().getDamageMap().getKiller());
        
        if (counter == 0) {
            initialDeath(npc);
            //System.out.println("We passed the initial death");
        } else if (counter == 5) {
            npc.removeFromTile();
            onDeath(npc);
            setNpcToInvisible(npc);
            //System.out.println("Remove the npc from the game world");
            
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
        } else if (counter == 6) {
        	if (GroupRespawn.is_my_boss_dead(npc)) {
        		//System.out.println("Boss dead yet?");
        		stop();
        		return;
        	}
        	if (npc.getDefinition().getRespawnTime() == -1) {// this npc does not respawn
        		//System.out.println("NPC can't respawn");
                World.getWorld().unregister(npc);
        		stop();
        	}
        } else if (counter == (6 + npc.getDefinition().getRespawnTime())) { //regular respawn timer
        	respawn(npc);
            GroupRespawn.on_boss_spawned(npc);
            stop();
            //System.out.println("Respawned");
            return;
        }
        //System.out.println("Counter running "+counter+" ticks sent.");
        counter++;
    }

    /**
     * We set the npc to invisible and restore its values
     * 
     * @param npc
     */
    public static void setNpcToInvisible(NPC npc) {
        npc.removeFromTile();
        npc.setVisible(false);
        npc.setLocation(Location.create(npc.makeX, npc.makeY, npc.getZ()));
        npc.setHitpoints(npc.getMaxHitpoints());
        
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
        npc.resetFaceTile();

        final Player killer = World.getWorld().lookupPlayerByName(npc.getCombatState().getDamageMap().getKiller());
        
        npc.playAnimation(new Animation(npc.getDeathAnimation())); // death emote
        
        //Sent the cheer animation when defeating Jad
        if(killer != null || npc != null) {
        	if(npc.getId() == 3127) {
        		killer.playAnimation(new Animation(862));
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
        
        if (killer != null) {
            MobAttackSounds.sendDeathSound((Player) killer, npc.getId());
        }
        
        reset(npc);
    }
    
    public static void reset(NPC npc) {
        npc.freeze(0);
        npc.targetId = 0;
        npc.resetFaceTile();
        npc.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
    }

    /**
     * The mob is respawned and set to visible to be updated again
     *
     */
    public static void respawn(NPC npc) {
    	//System.out.println("respawn");
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
		
    	//killer.debug(String.format("killer: %s", npc.getCombatState().getDamageMap().getKiller()));

    	if(killer == null) {
    		System.out.printf("Killer is null, killed %s%n", npc.getName());
    		return;
    	}
    	
    	int x = npc.getX();
    	int y = npc.getY();
    	int z = npc.getZ();
    	
		if (npc != null) {
			
			/**
			 * Warriors guild
			 */
			killer.getWarriorsGuild().dropDefender(npc.getLocation());
			if (AnimatedArmour.isAnimatedArmourNpc(npc.getId())) {

				if (npc.getX() == 2851 && npc.getY() == 3536) {
					x = 2851;
					y = 3537;
					AnimatedArmour.dropTokens(killer, npc.getId(), new Location(npc.getX(), npc.getY() + 1, 0));
				} else if (npc.getX() == 2857 && npc.getY() == 3536) {
					x = 2857;
					y = 3537;
					AnimatedArmour.dropTokens(killer, npc.getId(), new Location(npc.getX(), npc.getY() + 1, 0));
				} else {
					AnimatedArmour.dropTokens(killer, npc.getId(),new Location(npc.getX(), npc.getY(), 0));
				}
			}
			
			/* Add kills to tracker */
			for (int id : killer.getKillTracker().BOSSES) {
				//If the npc has the same id as the boss, we can track him
				if (npc.getId() == id) {
					killer.getKillTracker().submit(new KillEntry(npc.getName(), 1), true);
				}
			}

			AbstractBossCombat boss = AbstractBossCombat.get(npc.getId());

			if (boss != null) {
				boss.dropLoot(killer, npc);
			}
			
			Location3D location = new Location3D(x, y, z);
			// get the drop table
			int amountOfDrops = 1;
			if (Constants.DOUBLE_DROPS) {
				amountOfDrops++;
			}
			Server.getDropManager().create(killer, npc, location, amountOfDrops);
		}
	}

}