package com.venenatis.game.model.combat.combat_effects;

import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.HitType;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.server.Server;

/**
 * The class which handles Venom activity
 * @author https://www.rune-server.ee/members/max+_/
 * @date 14-08-2015
 *
 */
public class Venom {
	
	/**
	 * Default venom damage
	 */
	private int damage = 6;
	
	public Venom(Player player){
		playerVenom(player);
	}
	
	public Venom(NPC npc){
		npcVenom(npc);
	}
	
	/**
	 * This method venoms players.
	 * @param player
	 */
	private void playerVenom(Player player) {
		player.infection = 2;
		player.infected = true;
		if(player != null){
			player.damage(new Hit(damage, HitType.VENOM));
			damage = (damage + 2 > 20 ? 20 : damage + 2);
			Server.getTaskScheduler().schedule(new Task(20) {
				@Override
				public void execute() {
					if(player.infection == 0) {
						stop();
						return;
					}
					player.damage(new Hit(damage, HitType.VENOM));
					player.getActionSender().sendMessage("You have been hit by the venom infection.");
					damage = (damage + 2 > 20 ? 20 : damage + 2);
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				}
			}.attach(player));
		}
	}
	
	/**
	 * This method venoms npcs.
	 * @param npc
	 */
	private void npcVenom(NPC npc) {
		if(npc != null && !npc.isDead()) {
			npc.damage(new Hit(damage, HitType.VENOM));
			damage = (damage + 2 > 20 ? 20 : damage + 2);
			npc.infected = true;
			Server.getTaskScheduler().schedule(new Task(20) {
				@Override
				public void execute() {
					if(npc.isDead()) {
						stop();
					}
					npc.damage(new Hit(damage, HitType.VENOM));
					damage = (damage + 2 > 20 ? 20 : damage + 2);
				}
			}.attach(npc));
		}
	}
	
	/**
	 * A boolean to determine if the npcs can get effected by venom.
	 * @param npc
	 * @return {@code true} if the npc can get venomed, {@code false} otherwise.
	 */
	public static boolean venomImmune(NPC npc) {
		switch (npc.getId()) {
		case 6610:
		case 6611:
		case 6612:
		case 6613:
		case 6614:
		case 5779:
		case 3127:
		case 3848:
		case 4234:
		case 2054:
		case 2265:
		case 2266:
		case 2267:
		case 2215:
		case 2216:
		case 2217:
		case 2218:
		case 2205:
		case 3162:
		case 3163:
		case 3164:
		case 3165:
		case 3129:
		case 2919:
			return true;
		}
		return false;
	}
}
