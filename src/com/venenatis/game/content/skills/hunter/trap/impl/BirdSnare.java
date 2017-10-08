package com.venenatis.game.content.skills.hunter.trap.impl;

import java.util.EnumSet;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.venenatis.game.content.skills.hunter.trap.Trap;
import com.venenatis.game.event.CycleEvent;
import com.venenatis.game.event.CycleEventContainer;
import com.venenatis.game.event.CycleEventHandler;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.impl.SizedPathFinder;
import com.venenatis.server.Server;

/**
 * The bird snare implementation of the {@link Trap} class which represents a single bird snare.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class BirdSnare extends Trap {

	/**
	 * Constructs a new {@link BirdSnare}.
	 * @param player	{@link #getPlayer()}.
	 */
	public BirdSnare(Player player) {
		super(player, TrapType.BIRD_SNARE);
	}
	
	/**
	 * The npc trapped inside this box.
	 */
	private Optional<NPC> trapped = Optional.empty();
	
	/**
	 * Determines if a bird is going to the trap.
	 */
	private Optional<CycleEvent> event = Optional.empty();

	/**
	 * The object identification for a dismantled failed snare.
	 */
	private static final int FAILED_ID = 9344;

	/**
	 * The distance the npc has to have from the snare before it gets triggered.
	 */
	private static final int DISTANCE_PORT = 3;

	/**
	 * A collection of all the npcs that can be caught with a bird snare.
	 */
	private static final ImmutableSet<Integer> NPC_IDS = ImmutableSet.of(BirdData.CRIMSON_SWIFT.npcId, BirdData.GOLDEN_WARBLER.npcId,
			BirdData.COPPER_LONGTAIL.npcId, BirdData.CERULEAN_TWITCH.npcId, BirdData.TROPICAL_WAGTAIL.npcId);

	/**
	 * Kills the specified {@code npc}.
	 * @param npc	the npc to kill.
	 */
	private void kill(NPC npc) {
		npc.remove(npc);
		trapped = Optional.of(npc);
	}
	
	@Override
	public boolean canCatch(NPC npc) {
		Optional<BirdData> data = BirdData.getBirdDataByNpcId(npc.getId());
		
		if(!data.isPresent()) {
			throw new IllegalStateException("Invalid bird id.");
		}
		if (System.currentTimeMillis() - player.lastPickup < 2500)
			return false;	
		
		if(player.getSkills().getLevel(Skills.HUNTER) < data.get().requirement) {
			player.lastPickup = System.currentTimeMillis();
			player.getActionSender().sendMessage("You do not have the required level to catch these.");
			setState(TrapState.FALLEN);
			return false;
		}
		return true;
	}

	@Override
	public void onPickUp() {
		player.getActionSender().sendMessage("You pick up your bird snare.");
	}

	@Override
	public void onSetup() {
		player.getActionSender().sendMessage("You set-up your bird snare.");
	}

	@Override
	public void onCatch(NPC npc) {
		Optional<BirdData> data = BirdData.getBirdDataByNpcId(npc.getId());
		
		if(!data.isPresent()) {
			throw new IllegalStateException("Invalid bird id.");
		}
		
		if(event.isPresent()) {
			return;
		}
		
		BirdData bird = data.get();
		
		event = Optional.of(new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				
				npc.doPath(new SizedPathFinder(), getObject().getX(), getObject().getY());

				//NPCDumbPathFinder.walkTowards(npc, getObject().getX(), getObject().getY());
				
				if(isAbandoned()) {
					container.stop();
					return;
				}
				if(npc.getX() == getObject().getX() && npc.getY() == getObject().getY()) {
					container.stop();
					int count = random.inclusive(150);
					int formula = successFormula(npc);
					if(count > formula) {
						setState(TrapState.FALLEN);
						container.stop();
						return;
					}
					
					kill(npc);
					Server.getGlobalObjects().remove(getObject());
					Server.getGlobalObjects().remove(getObject().getId(), getObject().getX(), getObject().getY(), getObject().getZ());
					setObject(bird.objectId);
					Server.getGlobalObjects().add(getObject());
					setState(TrapState.CAUGHT);
				}
			}
			
			@Override
			public void stop() {
				event = Optional.empty();
			}
		});
		
		CycleEventHandler.getSingleton().addEvent(player, event.get(), 1);
	}

	@Override
	public void onSequence(CycleEventContainer container) {
		for(NPC npc : World.getWorld().getNPCs()) {
			if(npc == null || npc.getCombatState().isDead()) {
				continue;
			}
			if(!NPC_IDS.stream().anyMatch(id -> npc.getId() == id)) {
				continue;
			}
			if(this.getObject().getZ() == npc.getZ() && Math.abs(this.getObject().getX() - npc.getX()) <= DISTANCE_PORT && Math.abs(this.getObject().getY() - npc.getY()) <= DISTANCE_PORT) {
				if(random.inclusive(100) < 20) {
					return;
				}
				if(this.isAbandoned()) {
					return;
				}
				trap(npc);
			}
		}
	}
	
	@Override
	public Item[] reward() {
		if(!trapped.isPresent()) {
			throw new IllegalStateException("No npc is trapped.");
		}
		Optional<BirdData> data = BirdData.getBirdDataByObjectId(getObject().getId());
		
		if(!data.isPresent()) {
			throw new IllegalStateException("Invalid object id.");
		}
		
		return data.get().reward;
	}
	
	@Override
	public double experience() {
		if(!trapped.isPresent()) {
			throw new IllegalStateException("No npc is trapped.");
		}
		Optional<BirdData> data = BirdData.getBirdDataByObjectId(getObject().getId());
		
		if(!data.isPresent()) {
			throw new IllegalStateException("Invalid object id.");
		}
		
		return data.get().experience;
	}
	
	@Override
	public boolean canClaim(GameObject object) {
		if(!trapped.isPresent()) {
			return false;
		}
		BirdData data = BirdData.getBirdDataByObjectId(object.getId()).orElse(null);
		
		if(data == null) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void setState(TrapState state) {
		if(state.equals(TrapState.PENDING)) {
			throw new IllegalArgumentException("Cannot set trap state back to pending.");
		}
		if(state.equals(TrapState.FALLEN)) {
			Server.getGlobalObjects().remove(getObject());
			Server.getGlobalObjects().remove(getObject().getId(), getObject().getX(), getObject().getY(), getObject().getZ());
			this.setObject(FAILED_ID);
			Server.getGlobalObjects().add(getObject());
		}
		player.getActionSender().sendMessage("Your trap has been triggered by something...");
		super.setState(state);
	}

	/**
	 * The enumerated type whose elements represent a set of constants
	 * used for bird snaring.
	 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
	 */
	public enum BirdData {
		CRIMSON_SWIFT(5549, 9373, 1, 34, new Item(526), new Item(10088), new Item(9978)),
		GOLDEN_WARBLER(5551, 9377, 5, 47, new Item(526), new Item(10090), new Item(9978)),
		COPPER_LONGTAIL(5552, 9379, 9, 61, new Item(526), new Item(10091), new Item(9978)),
		CERULEAN_TWITCH(5550, 9375, 11, 64.5, new Item(526), new Item(10089), new Item(9978)),
		TROPICAL_WAGTAIL(5548, 9348, 19, 95, new Item(526), new Item(10087), new Item(9978));

		/**
		 * Caches our enum values.
		 */
		private static final ImmutableSet<BirdData> VALUES = Sets.immutableEnumSet(EnumSet.allOf(BirdData.class));
		
		/**
		 * The npc id for this bird.
		 */
		private final int npcId;

		/**
		 * The object id for the catched bird.
		 */
		private final int objectId;

		/**
		 * The requirement for this bird.
		 */
		private final int requirement;

		/**
		 * The experience gained for this bird.
		 */
		private final double experience;

		/**
		 * The reward obtained for this bird.
		 */
		private final Item[] reward;

		/**
		 * Constructs a new {@link BirdData}.
		 * @param npcId			{@link #npcId}.
		 * @param objectId		{@link #objectId}
		 * @param requirement	{@link #requirement}.
		 * @param experience	{@link #experience}.
		 * @param reward		{@link #reward}.
		 */
		private BirdData(int npcId, int objectId, int requirement, double experience, Item... reward) {
			this.npcId = npcId;
			this.objectId = objectId;
			this.requirement = requirement;
			this.experience = experience;
			this.reward = reward;
		}
		
		/**
		 * @return the npc id.
		 */
		public int getNpcId() {
			return npcId;
		}

		/**
		 * Retrieves a {@link BirdData} enumerator dependant on the specified {@code id}.
		 * @param id	the npc id to return an enumerator from.
		 * @return a {@link BirdData} enumerator wrapped inside an optional, {@link Optional#empty()} otherwise.
		 */
		public static Optional<BirdData> getBirdDataByNpcId(int id) {
			return VALUES.stream().filter(bird -> bird.npcId == id).findAny();
		}
		
		/**
		 * Retrieves a {@link BirdData} enumerator dependant on the specified {@code id}.
		 * @param id	the object id to return an enumerator from.
		 * @return a {@link BirdData} enumerator wrapped inside an optional, {@link Optional#empty()} otherwise.
		 */
		public static Optional<BirdData> getBirdDataByObjectId(int id) {
			return VALUES.stream().filter(bird -> bird.objectId == id).findAny();
		}

	}

}