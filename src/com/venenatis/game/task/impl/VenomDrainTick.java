package com.venenatis.game.task.impl;

import org.joda.time.Duration;

import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.HitType;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;

/**
 * @author Clank1337
 */
public class VenomDrainTick extends Task {

	private static final int CYCLE_TIME = 34;
	public Entity entity;

	public VenomDrainTick(Entity mob) {
		super(CYCLE_TIME);
		this.entity = mob;
	}

	@Override
	public void execute() {
		long lastVenomSip = entity.hasAttribute("antiVenom+") ? entity.getAttribute("antiVenom+") : 0;
		boolean cured = Duration.millis(System.currentTimeMillis()).minus(lastVenomSip).getMillis() < 300000;
		boolean attribute = entity.hasAttribute("venom");
		boolean dead = entity.getCombatState().isDead();
		if (entity.isPlayer()) {
			Player player = (Player) entity;
			if (dead || cured || !attribute || player.getVenomDamage() >= 20) {
				stop();
				player.setVenomDamage(0);
				player.setInfection(0);
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				return;
			}
			int venomDamage = player.getVenomDamage();
			player.take_hit_generic(entity, venomDamage+2, HitType.VENOM).send();
			player.setVenomDamage(venomDamage + 2);
		} else {
			if (dead || cured || !attribute || entity.venomDamage >= 20) {
				stop();
				entity.venomDamage = 0;
				entity.removeAttribute("venom");
				return;
			}
			entity.take_hit_generic(entity, entity.venomDamage, HitType.VENOM).send();
			entity.venomDamage += 2;
		}
	}
}