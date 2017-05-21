package com.model.task.impl;

import com.model.Server;
import com.model.UpdateFlags.UpdateFlag;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.combat.nvp.NPCCombatData;
import com.model.game.character.npc.GroupRespawn;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.KillTracker;
import com.model.game.character.player.content.KillTracker.KillEntry;
import com.model.game.character.player.content.achievements.AchievementType;
import com.model.game.character.player.content.achievements.Achievements;
import com.model.game.character.player.content.music.sounds.MobAttackSounds;
import com.model.game.character.player.minigames.fight_caves.FightCaves;
import com.model.game.character.player.minigames.warriors_guild.AnimatedArmour;
import com.model.game.character.player.skill.slayer.SlayerTaskManagement;
import com.model.game.item.container.impl.Equipment;
import com.model.net.packet.out.SendKillFeedPacket;
import com.model.task.ScheduledTask;
import com.model.utility.Location3D;
import com.model.utility.Utility;

/**
 * Handles respawning an {@link NPC} which has just died
 * 
 * @author Mobster
 * @author lare96 <http://github.com/lare96>
 * @author Patrick van Elderen
 */
public class NPCDeathTask extends ScheduledTask {

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
        npc.isDead = true;
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
            				n.isDead = true;
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
     * @param killer
     *            The {@link Player} that has killed the npc
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
        return NPCCombatData.getUnspawnableNpcs().contains(npc.getId());
    }

    /**
     * Removes custom spawned npc & minigame npc so they do not respawn
     * 
     * @param player
     *            The {@link Player} who has killed this npc
     * @param mob
     *            The {@link NPC} who died
     */
    private void removeMobFromWorld(Player player, NPC npc) {
        if (player != null && !player.isDead()) {}
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
        npc.setAbsX(npc.makeX);
        npc.setAbsY(npc.makeY);
        npc.setHitpoints(npc.getMaxHitpoints());
		
        if (!npc.noDeathEmote) {
            npc.playAnimation(Animation.create(808));
        }
        
        npc.getDamageMap().resetDealtDamage();

        if (npc.getId() == 492 && npc.isDead) {
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

        final Player killer = World.getWorld().getPlayerByName(npc.getDamageMap().getKiller());

        if (killer != null) {
            npc.killedBy = killer.getIndex();
        } else {
            npc.killedBy = -1;
        }
        if (npc.getId() == 6618 && npc.isDead) {
        	npc.sendForcedMessage("Ow!");
        }
        if (npc.getId() == 6615 && npc.isDead) {
        	npc.spawnedScorpiaMinions = false;
        }
        if (npc.getId() == 6611 && npc.transformId != 6612) {
			npc.requestTransform(6612);
			npc.getUpdateFlags().flag(UpdateFlag.TRANSFORM);
			npc.setHitpoints(255);
			npc.isDead = false;
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
        if (killer != null) {
        	FightCaves.sendDeath(killer, npc);
		}
        if (npc.transformId == 6612) {
			npc.requestTransform(6611);
			npc.getUpdateFlags().flag(UpdateFlag.TRANSFORM);
		}
       
        if (!npc.noDeathEmote) {
            npc.playAnimation(Animation.create(npc.getDeathAnimation())); // dead
        }
        
        if (killer != null) {
            MobAttackSounds.sendDeathSound(killer, npc.getId());
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
     * @param mob2
     */
    public static void respawn(NPC npc) {
        npc.killedBy = -1;
        npc.setVisible(true);
        npc.isDead = false;
        npc.setOnTile(npc.absX, npc.absY, npc.heightLevel);
    }
    
    private final static void onDeath(NPC npc) {
    	int dropX = npc.absX;
		int dropY = npc.absY;
		int dropHeight = npc.heightLevel;
    	
		if (npc.killedBy == -1) {
			return;
		}
		
		Player player = World.getWorld().getPlayers().get(npc.killedBy);
		if (player == null) {
			return;
		}

		if (npc != null) {
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
			
			switch(npc.getId()) {
			case 6610:
				Achievements.increase(player, AchievementType.VENENATIS, 1);
				break;
			case 2054:
				Achievements.increase(player, AchievementType.CHAOS_ELEMENTAL, 1);
				break;
			case 6619:
				Achievements.increase(player, AchievementType.CHAOS_FANATIC, 1);
				break;
			case 319:
				Achievements.increase(player, AchievementType.CORPOREAL_BEAST, 1);
				break;
			case 6609:
				Achievements.increase(player, AchievementType.CALLISTO, 1);
				break;
			case 6611:
				Achievements.increase(player, AchievementType.VETION, 1);
				break;
			case 6615:
				Achievements.increase(player, AchievementType.SCORPIA, 1);
				break;
			case 494:
				Achievements.increase(player, AchievementType.KRAKEN, 1);
				break;
			case 3162:
				Achievements.increase(player, AchievementType.KREE_ARRA, 1);
				break;
			case 3131:
				Achievements.increase(player, AchievementType.KRIL_TSUTSAROTH, 1);
				break;
			case 2205:
				Achievements.increase(player, AchievementType.COMMANDER_ZILYANA, 1);
				break;
			case 2215:
				Achievements.increase(player, AchievementType.GENERAL_GRAARDOR, 1);
				break;
			case 239:
				Achievements.increase(player, AchievementType.KING_BLACK_DRAGON, 1);
				break;
			case 6342:
				Achievements.increase(player, AchievementType.BARRELCHEST, 1);
				break;
			case 3359:
				Achievements.increase(player, AchievementType.ZOMBIE_CHAMPION, 1);
				break;
			case 6618:
				Achievements.increase(player, AchievementType.CRAZY_ARCHAEOLOGIST, 1);
				break;
			}
		}
	
		
		int weapon = player.getEquipment().getId(Equipment.WEAPON_SLOT);
		player.write(new SendKillFeedPacket(Utility.formatPlayerName(player.getName()), npc.getDefinition().getName(), weapon, npc.isPoisoned()));
		
		player.getWarriorsGuild().dropDefender(npc.absX, npc.absY);
		if(AnimatedArmour.isAnimatedArmourNpc(npc.getId()))
			AnimatedArmour.dropTokens(player, npc.getId(), npc.absX, npc.absY);
		
		// get the drop table
		Location3D location = new Location3D(dropX, dropY, dropHeight);
		int amountOfDrops = 1;
		Server.getDropManager().create(player, npc, location, amountOfDrops);
	}

}
