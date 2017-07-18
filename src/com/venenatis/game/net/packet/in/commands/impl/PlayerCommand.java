package com.venenatis.game.net.packet.in.commands.impl;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.content.trivia.TriviaBot;
import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.SkullType;
import com.venenatis.game.model.combat.magic.spell.SpellBook;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.net.packet.in.commands.CommandParser;
import com.venenatis.game.net.packet.in.commands.Yell;
import com.venenatis.game.world.World;

/**
 * A list of commands accessible to all players disregarding rank.
 * 
 * @author Michael | Chex
 */
public class PlayerCommand implements Command {

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {

		switch (parser.getCommand()) {
		
		case "donze":
		case "dz":
			if(player.getTotalAmountDonated() >= 10) {
				player.getTeleportAction().teleport(new Location(2518, 3369));
			} else {
				player.getActionSender().sendMessage("Only donators can use ::unskull.");
				return false;
			}
			return true;
		
		case "skull":
			Combat.skull(player, SkullType.SKULL, 300);
			return true;
			
		case "redskull":
		case "red":
			Combat.skull(player, SkullType.RED_SKULL, 300);
			return true;
			
		case "unkskull":
			if(player.getTotalAmountDonated() >= 10) {
				player.setSkullType(SkullType.NONE);
				player.setSkullTimer(-1);
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.getActionSender().sendMessage("You've been unskulled.");
			} else {
				player.getActionSender().sendMessage("Only donators can use ::unskull.");
				return false;
			}
			return true;
			
		case "food":
			//Regular donator
			if(player.getTotalAmountDonated() >= 10) {
				//TODO create a fill method, fill the empty slots rather then spawning 28 items
				player.getInventory().add(new Item(385, 28), true);
				player.getActionSender().sendMessage("Did you know? Super donators receive dark crabs instead.");
			//Super
			} else if(player.getTotalAmountDonated() >= 30) {
				player.getInventory().add(new Item(11936, 28), true);
				player.getActionSender().sendMessage("Did you know? Elite donators receive anglerfish instead.");
			//elite
			} else if(player.getTotalAmountDonated() >= 100 || player.getRights().isOwner(player)) {
				player.getInventory().add(new Item(13441, 28), true);
			}
			return true;
			
		case "pots":
			if(player.getTotalAmountDonated() >= 30) {
				player.getInventory().add(new Item(12695, 1), true);
			} else if(player.getTotalAmountDonated() >= 100) {
				player.getInventory().add(new Item(11730, 1), true);
			} else {
				player.getInventory().add(new Item(2440, 1), true);
				player.getInventory().add(new Item(2436, 1), true);
				player.getInventory().add(new Item(2442, 1), true);
			}
			player.getInventory().add(new Item(3024, 2), true);
			player.getInventory().add(new Item(6685, 2), true);
			return true;
			
		case "restore":
		case "srestore":
		case "pray":
			player.getInventory().add(new Item(3024, 1), true);
			return true;
			
		case "brew":
		case "sbrew":
		case "sarabrew":
			player.getInventory().add(new Item(6685, 1), true);
			return true;
			
		case "range":
		case "rpot":
			player.getInventory().add(new Item(2444, 1), true);
			return true;
			
		case "mage":
		case "mpot":
			player.getInventory().add(new Item(3040, 1), true);
			return true;
			
		case "empty":
			if (Area.inWilderness(player)) {
				player.getActionSender().sendMessage("Please enter safe zone to do this!");
				return true;
			}

			if (player.getCombatState().inCombat()) {
				player.getActionSender().sendMessage("Please get out of combat to do this.");
				return true;
			}
			player.getInventory().clear(true);
			player.getActionSender().sendMessage("You have cleared your inventory.");
			return true;
		
		/* Home Teleport */
		case "home":
			player.getTeleportAction().teleport(Constants.RESPAWN_PLAYER_LOCATION, TeleportTypes.SPELL_BOOK, false);
			return true;

		/* Online Players */
		case "players":
			player.getActionSender().sendMessage("<col=255>There are currently " + World.getWorld().getPlayerCount() + " players online!");
			return true;
		
		/* Vengeance Runes */
		case "veng":
		case "venge":
		case "vengeance":
		case "vengerune":
		case "vengerunes":
			player.getInventory().add(new Item(557, 1000));
			player.getInventory().add(new Item(560, 1000));
			player.getInventory().add(new Item(9075, 1000));
			player.setSpellBook(SpellBook.LUNAR_MAGICS);
			player.getActionSender().sendSidebarInterface(6, 29999);
			return true;

		/* Barrage Runes */
		case "barrage":
		case "barragerune":
		case "barragerunes":
			player.getInventory().add(new Item(555, 1000));
			player.getInventory().add(new Item(560, 1000));
			player.getInventory().add(new Item(565, 1000));
			player.setSpellBook(SpellBook.ANCIENT_MAGICKS);
			player.getActionSender().sendSidebarInterface(6, 12855);
			return true;
		
		/* Yell */
		case "yell":
			if (parser.hasNext()) {
				try {
					String message = parser.nextString();
					while (parser.hasNext()) {
						message += " " + parser.nextString();
					}
					Yell.yell(player, message.trim());
				} catch (final Exception e) {
					player.getActionSender().sendMessage("Invalid yell format, syntax: -messsage");
				}
			}
			return true;

		/* TriviaBot */
		case "answer":
			if (parser.hasNext()) {
				String answer = "";
				while (parser.hasNext()) {
					answer += parser.nextString() + " ";
				}
				TriviaBot.answer(player, answer.trim());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return true;
	}
}