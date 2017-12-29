package com.venenatis.game.content.option_menu;

/**
 * Represents the OptionMenu that get's send to the client to draw the interface
 * options.
 * 
 * @author Lennard
 *
 */
public class ClientOptionMenu {

	/**
	 * The identifier of this option.
	 */
	private final int identifier;

	/**
	 * The name of this option.
	 */
	private final String optionName;

	/**
	 * The tooltip of this option.
	 */
	private final String optionTooltip;

	public ClientOptionMenu(int identifier, String optionName, String optionTooltip) {
		this.identifier = identifier;
		this.optionName = optionName;
		this.optionTooltip = optionTooltip;
	}

	public int getIdentifier() {
		return identifier;
	}

	public String getOptionName() {
		return optionName;
	}

	public String getOptionTooltip() {
		return optionTooltip;
	}

}