package com.venenatis.game.content.option_menu;

import com.venenatis.game.action.Action;

/**
 * Represents an OptionMenu that can be searched for, see
 * {@link TeleportMenuHandler} for more information.
 * 
 * @author Lennard
 *
 */
public class OptionMenu {

	/**
	 * The name of the OptionMenu.
	 */
	private final String optionName;

	/**
	 * The tags that can be searched for to find this Option.
	 */
	private final String[] tags;

	/**
	 * The {@link Action} that get's executed when selecting this option.
	 */
	private final Action action;

	public OptionMenu(String optionName, String[] tags, Action action) {
		this.optionName = optionName;
		this.tags = tags;
		this.action = action;
	}

	public String getOptionName() {
		return optionName;
	}

	public String[] getTags() {
		return tags;
	}

	public Action getAction() {
		return action;
	}

}