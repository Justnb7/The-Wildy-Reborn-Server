package com.venenatis.game.model.combat.npcs.impl.zulrah;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.venenatis.game.event.CycleEvent;
import com.venenatis.game.event.CycleEventContainer;
import com.venenatis.game.event.CycleEventHandler;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.impl.zulrah.impl.*;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.entity.player.instance.InstancedArea;
import com.venenatis.game.model.entity.player.instance.InstancedAreaManager;
import com.venenatis.game.model.entity.player.instance.SingleInstancedArea;
import com.venenatis.game.model.entity.player.instance.impl.SingleInstancedZulrah;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;
import com.venenatis.server.data.SerializablePair;

public class Zulrah {

	/**
	 * The minion snake npc id
	 */
	public static final int SNAKELING = 2045;

	/**
	 * The relative lock for this event
	 */
	private final Object EVENT_LOCK = new Object();

	/**
	 * The player associated with this event
	 */
	private final Player player;

	/**
	 * The single instance of zulrah
	 */
	private SingleInstancedArea zulrahInstance;

	/**
	 * The boundary of zulrah's location
	 */
	public static final Boundary BOUNDARY = new Boundary(2248, 3059, 2283, 3084);

	/**
	 * The zulrah npc
	 */
	private NPC npc;

	/**
	 * The current stage of zulrah
	 */
	private int stage;

	/**
	 * Determines if the npc is transforming or not.
	 */
	private boolean transforming;

	/**
	 * The stopwatch for tracking when the zulrah npc fight starts.
	 */
	private Stopwatch stopwatch = Stopwatch.createUnstarted();

	/**
	 * A mapping of all the stages
	 */
	private Map<Integer, ZulrahStage> stages = new HashMap<>();

	/**
	 * Creates a new Zulrah event for the player
	 * 
	 * @param player the player
	 */
	public Zulrah(Player player) {
		this.player = player;
		stages.put(0, new SpawnZulrahStageZero(this, player));
		stages.put(1, new CreateToxicStageOne(this, player));
		stages.put(2, new MeleeStageTwo(this, player));
		stages.put(3, new MageStageThree(this, player));
		stages.put(4, new RangeStageFour(this, player));
		stages.put(5, new MageStageFive(this, player));
		stages.put(6, new MeleeStageSix(this, player));
		stages.put(7, new RangeStageSeven(this, player));
		stages.put(8, new MageStageEight(this, player));
		stages.put(9, new RangeStageNine(this, player));
		stages.put(10, new MeleeStageTen(this, player));
		stages.put(11, new RangeStageEleven(this, player));
	}

	public void initialize() {
		if (zulrahInstance != null) {
			InstancedAreaManager.getSingleton().disposeOf(zulrahInstance);
		}
		int height = InstancedAreaManager.getSingleton().getNextOpenHeight(BOUNDARY);
		zulrahInstance = new SingleInstancedZulrah(player, BOUNDARY, height);
		InstancedAreaManager.getSingleton().add(height, zulrahInstance);
		if (zulrahInstance == null) {
			SimpleDialogues.sendStatement(player, "The zulrah boss is currently being played by a high amount", "of players. Please try again shortly.");
			return;
		}
		stage = 0;
		stopwatch = Stopwatch.createStarted();
		player.getActionSender().removeAllInterfaces();
		player.getActionSender().sendScreenFade("Welcome to Zulrah's shrine", 1, 5);
		CycleEventHandler.getSingleton().addEvent(EVENT_LOCK, stages.get(0), 1);
	}

	/**
	 * Determines if the player is standing in a toxic location
	 * 
	 * @return true of the player is in a toxic location
	 */
	public boolean isInToxicLocation() {
		for (int x = player.getX() - 1; x < player.getX() + 1; x++) {
			for (int y = player.getY() - 1; y < player.getY() + 1; y++) {
				if (Server.getGlobalObjects().exists(11700, new Location(x, y, player.getZ()))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Stops the zulrah instance and concludes the events
	 */
	public void stop() {
		CycleEventHandler.getSingleton().stopEvents(EVENT_LOCK);
		if (stage < 1) {
			return;
		}
		stopwatch.stop();
		long time = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		long best = player.getBestZulrahTime();
		String duration = best < (60_000 * 60) ? Utility.toFormattedMS(time) : Utility.toFormattedHMS(time);
		player.getActionSender().sendMessage("Fight duration: <col=CC0000>" + duration + "</col> " + (time < player.getBestZulrahTime() ? "(New personal best)" : "") + ".");
		if (time < player.getBestZulrahTime()) {
			player.setBestZulrahTime(time);
		}
		SerializablePair<String, Long> globalBest = Server.getServerData().getZulrahTime();
		if (globalBest.getFirst() == null || globalBest.getSecond() == null || time < globalBest.getSecond() && globalBest.getSecond() != 0) {
			World.getWorld().sendWorldMessage("<img=24></img>[<col=255>News</col>] <col=CC0000>" + Utility.capitalize(player.getUsername()) + "</col> set the record for best time against zulrah with " + duration + ".", false);
			if (globalBest.getFirst() != null && globalBest.getSecond() != null) {
				World.getWorld().sendWorldMessage("<img=24></img>[<col=255>News</col>] The old record was set by <col=CC0000>" + globalBest.getFirst() + "</col> with a time of <col=CC0000>" + Utility.toFormattedMS(globalBest.getSecond()) + "</col>.", false);
			}
			Server.getServerData().setSerializablePair(new SerializablePair<>(player.getUsername(), time));
		}
		zulrahInstance.onDispose();
		InstancedAreaManager.getSingleton().disposeOf(zulrahInstance);
		zulrahInstance = null;
	}

	public void changeStage(int stage, CombatStyle combatType, ZulrahLocation location) {
		this.stage = stage;
		CycleEventHandler.getSingleton().stopEvents(EVENT_LOCK);
		CycleEventHandler.getSingleton().addEvent(EVENT_LOCK, stages.get(stage), 1);
		if (stage == 1) {
			return;
		}
		int type = combatType == CombatStyle.MELEE ? 2043 : combatType == CombatStyle.MAGIC ? 2044 : 2042;
		npc.playAnimation(new Animation(5072));
		npc.getCombatState().setAttackDelay(8);
		transforming = true;
		Combat.resetCombat(player);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				Combat.resetCombat(player);
				if (container.getTotalTicks() == 2) {
					npc.requestTransform(6709);
				} else if (container.getTotalTicks() == 3) {
					//npc.setLocation(new Location(location.getLocation().x, location.getLocation().y));
					npc.teleport(new Location(location.getLocation().x, location.getLocation().y));
					//TODO ask Jak what this is?
					//player.rebuildNPCList = true;
				} else if (container.getTotalTicks() == 5) {
					npc.requestTransform(type);
					npc.playAnimation(new Animation(5071));
					npc.faceEntity(player);
					transforming = false;
					container.stop();
				}
			}

		}, 1);
	}

	/**
	 * Determines if any of the events alive contains the event lock
	 * 
	 * @return true if any of the events are active with this as the owner
	 */
	public boolean isActive() {
		return CycleEventHandler.getSingleton().isAlive(EVENT_LOCK);
	}

	/**
	 * The {@link SingleInstancedArea} object for this class
	 * 
	 * @return the zulrah instance
	 */
	public InstancedArea getInstancedZulrah() {
		return zulrahInstance;
	}

	/**
	 * The reference to zulrah, the npc
	 * 
	 * @return the reference to zulrah
	 */
	public NPC getNpc() {
		return npc;
	}

	/**
	 * The instance of the Zulrah {@link NPC}
	 * 
	 * @param npc the zulrah npc
	 */
	public void setNpc(NPC npc) {
		this.npc = npc;
	}

	/**
	 * The stage of the zulrah event
	 * 
	 * @return the stage
	 */
	public int getStage() {
		return stage;
	}

	/**
	 * Determines if the NPC is transforming or not
	 * 
	 * @return {@code true} if the npc is in a transformation stage
	 */
	public boolean isTransforming() {
		return transforming;
	}

}