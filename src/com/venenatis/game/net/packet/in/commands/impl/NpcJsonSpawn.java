package com.venenatis.game.net.packet.in.commands.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.venenatis.game.content.titles.Title;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.save.PlayerSave;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.net.packet.in.button.ActionButton;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Makes the player perform the given animation.
 * 
 * @author Lennard
 *
 */
public class NpcJsonSpawn extends Command {

	private Location location;
	private static List<Location> buttons = new ArrayList<>();
	@Override
	protected void executeCommand(Player player, String command) {
		try {
		location = player.getLocation();
		File file = new File("./data/spawnDump.txt");
		if(!file.exists())
			file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		writer.write("{");
		writer.newLine();
		writer.write("\"id\": 5942,");
		writer.newLine();
		writer.write("\"position\": {");
		writer.newLine();
		writer.write("\"x\": "+player.getLocation().getX()+",");writer.newLine();
		writer.write("\"y\": "+player.getLocation().getY()+",");writer.newLine();
		writer.write("\"z\": "+player.getLocation().getZ());writer.newLine();
		writer.write("},");writer.newLine();
		writer.write("\"facing\": \"NORTH\",");writer.newLine();
		writer.write("\"radius\": \"3\",");writer.newLine();
		writer.write("\"name\": \"Wallaski\"");writer.newLine();
		writer.write("},");writer.newLine();
		//writer.write(npc.getId() + " " + npc.getPosition().getX() + " " + npc.getPosition().getY());
		writer.flush();
		writer.close();

	} catch (Exception e) {
		e.printStackTrace();
	}
			}
		

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}