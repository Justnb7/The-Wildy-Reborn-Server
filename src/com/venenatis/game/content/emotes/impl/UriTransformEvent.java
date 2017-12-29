package com.venenatis.game.content.emotes.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;

public class UriTransformEvent extends Task {
	
	public int ticks = 0;
	
    public UriTransformEvent(Player player) {
        super(1, true);
        attach(player);
    }

	@Override
	public void execute() {
		Player player = (Player) getAttachment();
		if (!player.getWalkingQueue().isEmpty()) {
			player.playGraphic(Graphic.create(-1));
			player.playAnimation(Animation.RESET_ANIMATION);
			player.setPnpc(-1);
			player.setPlayerTransformed(false);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			stop();
			return;
		}
		switch (ticks) {
		case 0:
			player.playGraphic(Graphic.create(86, 0, 100));
			player.setPnpc(7311);
			player.setPlayerTransformed(true);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			break;

		case 1:
			player.playGraphic(Graphic.create(1306));
			player.playAnimation(Animation.create(7278));
			player.setPnpc(7313);
			player.setPlayerTransformed(true);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			break;

		case 11:
			player.playAnimation(Animation.create(4069));
			break;

		case 12:
			player.playGraphic(Graphic.create(678));
			player.playAnimation(Animation.create(4071));
			break;

		case 14:
			player.playGraphic(Graphic.create(86, 0, 100));
			player.playAnimation(Animation.RESET_ANIMATION);
			player.setPnpc(-1);
			player.setPlayerTransformed(false);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			stop();
			break;
		}
		ticks++;
	}

}
