package com.venenatis.game.util.parser.impl;

import java.io.BufferedReader;
import java.io.IOException;

import com.venenatis.game.util.parser.TextFileParser;
import com.venenatis.game.world.World;

/**
 * @Author Seven on 2/22/2016.
 */
public class MacBanParser extends TextFileParser {

	public MacBanParser() {
		super("punishment/mac_bans");
	}

	@Override
	public void parse(BufferedReader reader) throws IOException {
		String macAddress = reader.readLine();
		World.getWorld().getMacBans().add(macAddress);
	}

}
