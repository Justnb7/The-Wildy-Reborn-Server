package com.model.game.character.player.content.cluescrolls;

import java.util.Arrays;
import java.util.Optional;

import org.omicron.jagex.runescape.CollisionMap;

import com.model.Server;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.item.IntervalItem;
import com.model.utility.Utility;

/**
 * @author lare96 <http://github.com/lare96>
 */
public enum ClueDifficulty {
	
	
	EASY(2677, 1, 2, 1, 3, ClueScrollHandler.EASY_CLUE_REWARDS, new int[] { 6732, 6733, 7258, 6725, 6380 }) {
		@Override
		public boolean dropClue(Player player, Npc npc) {
			return npc.combatLevel >= 0 && npc.combatLevel <= 40;
		}
	},
	MEDIUM(2801, 2, 4, 2, 4, ClueScrollHandler.MEDIUM_CLUE_REWARDS, new int[] { 6732, 6733, 7258, 6725, 6380 }) {
		@Override
		public boolean dropClue(Player player, Npc npc) {
			return npc.combatLevel > 40 && npc.combatLevel <= 100;
		}
	},
	HARD(2722, 4, 6, 3, 5, ClueScrollHandler.HARD_CLUE_REWARDS, new int[] { 6732, 6733, 7258, 6725, 6380 }) {
		@Override
		public boolean dropClue(Player player, Npc npc) {
			return npc.combatLevel > 100 && !Arrays.stream(ClueScrollHandler.ELITE_CLUE_DROPS).anyMatch(id -> id == npc.npcId);
		}
	},
	ELITE(12073, 7, 10, 5, 9, ClueScrollHandler.ELITE_CLUE_REWARDS, new int[] { 6615, 6610, 6609, 6611 }) {
		@Override
		public boolean dropClue(Player player, Npc npc) {
			return Arrays.stream(ClueScrollHandler.ELITE_CLUE_DROPS).anyMatch(id -> id == npc.npcId);
		}
	};
	

	public final int clueId;
	public final int minLeft;
	public final int maxLeft;
	public final int minReward;
	public final int maxReward;
	public final IntervalItem[] rewards;
	public final int[] bosses;

	private ClueDifficulty(int clueId, int minLeft, int maxLeft, int minReward, int maxReward, IntervalItem[] rewards, int[] bosses) {
		this.clueId = clueId;
		this.minLeft = minLeft;
		this.maxLeft = maxLeft;
		this.minReward = minReward;
		this.maxReward = maxReward;
		this.rewards = rewards;
		this.bosses = bosses;
	}


	public abstract boolean dropClue(Player player, Npc npc);

	public void createBoss(Player player) {
		int boss = Utility.randomElement(bosses);
		Optional<Npc> npc = Optional.empty();

		if (!CollisionMap.isEastBlocked(player.heightLevel, player.absX - 1, player.absY)) {
			npc = NPCHandler.spawnNpc3(player, boss, player.absX - 1, player.absY, player.heightLevel, this);
			npc.ifPresent(n -> Server.getTaskScheduler().schedule(new ClueNpcDistanceEventListener(n, player)));
		} else if (!CollisionMap.isWestBlocked(player.heightLevel, player.absX + 1, player.absY)) {
			npc = NPCHandler.spawnNpc3(player, boss, player.absX + 1, player.absY, player.heightLevel, this);
			npc.ifPresent(n -> Server.getTaskScheduler().schedule(new ClueNpcDistanceEventListener(n, player)));
		} else {
			npc = NPCHandler.spawnNpc3(player, boss, player.absX, player.absY, player.heightLevel, this);
			npc.ifPresent(n -> Server.getTaskScheduler().schedule(new ClueNpcDistanceEventListener(n, player)));
		}

		npc.ifPresent(n -> {
			n.forceChat("How dare you try and steal my treasure!");
			player.write(new SendMessagePacket("Defeat the boss in order to claim your reward!"));
			player.bossDifficulty = this;
			n.forClue = true;
		});
	}

	public static Optional<ClueDifficulty> determineClue(Player player, Npc npc) {
		return Arrays.stream(values()).filter(c -> c.dropClue(player, npc)).findAny();
	}

	public static Optional<ClueDifficulty> getDifficulty(int clueId) {
		return Arrays.stream(values()).filter(c -> c.clueId == clueId).findFirst();
	}

	public static boolean isClue(int id) {
		return Arrays.stream(values()).anyMatch(c -> c.clueId == id);
	}

	public static int[] getClueIds() {
		return Arrays.stream(values()).mapToInt(i -> i.clueId).toArray();
	}
}