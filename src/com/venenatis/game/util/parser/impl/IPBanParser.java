package com.venenatis.game.util.parser.impl;

import java.io.BufferedReader;
import java.io.IOException;

import com.venenatis.game.util.parser.TextFileParser;
import com.venenatis.game.world.World;

/**
 * Parses the ip bans
 */
public class IPBanParser extends TextFileParser {

	/**
	 * Creates a new {@link IPBanParser}.
	 */
	public IPBanParser() {
		super("punishment/ip_bans");
	}

	@Override
	public void parse(BufferedReader reader) throws IOException {
		String ip_bans = reader.readLine();
		World.getWorld().getIpBans().add(ip_bans);
	}

}