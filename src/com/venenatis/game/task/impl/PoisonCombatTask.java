package com.venenatis.game.task.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.HitType;
import com.venenatis.game.model.equipment.PoisonType;
import com.venenatis.game.task.ScheduledTask;

/**
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public final class PoisonCombatTask extends ScheduledTask {

	public static final Map<Integer, PoisonType> WEAPON_TYPES = new HashMap<>();
	public static final Map<Integer, PoisonType> NPC_TYPES = new HashMap<>();
	private final Entity entity;
	
	/**
     * The amount of times this player has been hit.
     */
    private int amount;

	public PoisonCombatTask(Entity entity) {
		super(60, false);
		this.entity = entity;
	}

	@Override
	public void execute() {
		if (!entity.isRegistered()) {
			stop();
			return;
		}
		if (entity.getPoisonDamage().get() <= 0) {
			stop();
			return;
		}
		entity.damage(new Hit(entity.getPoisonDamage().get(), HitType.POISON));
			amount--;
			entity.damage(new Hit(entity.getPoisonDamage().get(), HitType.POISON));
	        if (amount == 0) {
	            amount = 4;
	            entity.getPoisonDamage().decrementAndGet();
	        }
		if (entity.getPoisonDamage().get() <= 0) {
			stop();
		}
	}

	/**
	 * Gets the {@link PoisonType} for {@code item} wrapped in an optional. If a
	 * poison type doesn't exist for the item then an empty optional is
	 * returned.
	 * 
	 * @param item
	 *            the item to get the poison type for.
	 * @return the poison type for this item wrapped in an optional, or an empty
	 *         optional if no poison type exists.
	 */
	public static Optional<PoisonType> getPoisonType(Item item) {
		if (item == null || item.getId() < 1 || item.getAmount() < 1)
			return Optional.empty();
		return Optional.ofNullable(WEAPON_TYPES.get(item.getId()));
	}

	/**
	 * Gets the {@link PoisonType} for {@code npc} wrapped in an optional. If a
	 * poison type doesn't exist for the NPC then an empty optional is returned.
	 * 
	 * @param npc
	 *            the NPC to get the poison type for.
	 * @return the poison type for this NPC wrapped in an optional, or an empty
	 *         optional if no poison type exists.
	 */
	public static Optional<PoisonType> getPoisonType(int npc) {
		if (npc < 0)
			return Optional.empty();
		return Optional.ofNullable(NPC_TYPES.get(npc));
	}
}