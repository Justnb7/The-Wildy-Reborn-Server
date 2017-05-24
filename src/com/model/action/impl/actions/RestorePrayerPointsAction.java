package com.model.action.impl.actions;

import com.model.action.PlayerAction;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;

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
		if (player.isDead()) {
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