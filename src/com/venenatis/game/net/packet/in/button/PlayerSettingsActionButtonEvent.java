package com.venenatis.game.net.packet.in.button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.venenatis.game.model.entity.player.Player;

/**
 * Handles the buttons for the player settings tab
 * 
 * @author Mobster
 *
 */
public class PlayerSettingsActionButtonEvent implements ActionButtonEvent {

	/**
	 * An array of ids for the music buttons
	 */
	private final int[][] MUSIC_BUTTONS = new int[][] { { 3162, 4 }, { 3163, 3 }, { 3164, 2 }, { 3165, 1 }, { 3166, 0 } };

	/**
	 * An array of ids for the sound buttons
	 */
	private final int[][] SOUND_BUTTONS = new int[][] { { 3173, 4 }, { 3174, 3 }, { 3175, 2 }, { 3176, 1 }, { 3177, 0 } };

	@Override
	public void onActionButtonClick(Player player, ActionButton button) {
		/*
		 * The id for the button clicked
		 */
		int id = button.getId();
		
		for (int[] i : MUSIC_BUTTONS) {
			if (id == i[0]) {
				player.setAttribute("music_volume", i[1]);
				break;
			}
		}
		
		for (int[] i : SOUND_BUTTONS) {
			if (id == i[0]) {
				player.setAttribute("sound_volume", i[1]);
				break;
			}
		}
	}

	@Override
	public List<ActionButton> getButtons() {
		List<ActionButton> buttons = new ArrayList<>();
		for (int[] i : MUSIC_BUTTONS) {
			buttons.add(new ActionButton(i[0], this));
		}
		for (int[] i : SOUND_BUTTONS) {
			buttons.add(new ActionButton(i[0], this));
		}
		return Collections.unmodifiableList(buttons);
	}

}
