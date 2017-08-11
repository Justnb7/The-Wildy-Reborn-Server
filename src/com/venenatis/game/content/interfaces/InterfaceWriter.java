package com.venenatis.game.content.interfaces;

import com.venenatis.game.model.entity.player.Player;

/**
 * Handles the interfaces
 * 
 * @author Daniel
 *
 */
public abstract class InterfaceWriter {

	public static void write(InterfaceWriter interfacetext) {
		int line = interfacetext.startingLine();
		for (int i1 = 0; i1 < interfacetext.text().length; i1++) {
			interfacetext.player.getActionSender().sendString(interfacetext.text()[i1], line++);
		}
	}

	protected Player player;

	public InterfaceWriter(Player player) {
		this.player = player;
	}

	protected abstract int startingLine();

	protected abstract String[] text();
}