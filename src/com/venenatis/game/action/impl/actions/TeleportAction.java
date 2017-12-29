package com.venenatis.game.action.impl.actions;

import com.venenatis.game.action.Action;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;

/**
 * Action that teleports the Player to the specified Location.
 * 
 * @author Lennard
 *
 */
public class TeleportAction extends Action {

	/**
	 * The {@link Location} that the Entity is being teleported to.
	 */
	private final Location location;

	public TeleportAction(final Entity entity, final Location location) {
		super(entity, 1);
		this.location = location;
	}

	@Override
	public void execute() {
		if (getEntity().isPlayer()) {
			final Player player = (Player) getEntity();
			player.getTeleportAction().teleport(location);
		}
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ONLY_ON_WALK;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}

}