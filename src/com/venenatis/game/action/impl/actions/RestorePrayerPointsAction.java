package com.venenatis.game.action.impl.actions;

import com.venenatis.game.action.PlayerAction;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;

public class RestorePrayerPointsAction extends PlayerAction {
	
	private Player player;
	private int objectId;

	public RestorePrayerPointsAction(Player player, int objectId) {
		super(player);
		this.player = player;
		this.objectId = objectId;
	}

	@Override
	public boolean validated() {
		if (player.getCombatState().isDead()) {
			this.stop();
			return false;
		}
		if (player.getPrayerPoint() >= player.getSkills().getLevelForExperience(Skills.PRAYER)) {
			this.stop();
			player.getActionSender().sendMessage("Your prayer points are already full.");
			return false;
		}
		return true;
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ALWAYS;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}

	@Override
	public void execute() {
		double prayer_points = player.getSkills().getLevel(Skills.PRAYER);
		if (validated()) {
			this.stop();
			player.playAnimation(Animation.create(645));
			player.getSkills().setPrayerPoints(prayer_points, true);
		}
	}
	
	public int getObjectId() {
		return objectId;
	}

}