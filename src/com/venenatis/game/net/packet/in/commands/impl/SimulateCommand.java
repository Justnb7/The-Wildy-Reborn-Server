package com.venenatis.game.net.packet.in.commands.impl;

import java.util.ArrayList;
import java.util.Optional;

import com.venenatis.game.content.chest.impl.crystal_chest.CrystalKeyReward;
import com.venenatis.game.content.minigames.singleplayer.barrows.BarrowsHandler;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.definitions.NPCDefinitions;
import com.venenatis.game.model.entity.npc.drops.NPCDropManager;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.util.Utility;

/**
 * Used to simulate items on the item simulator interface.
 * 
 * @author Lennard
 *
 */
public class SimulateCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		final String[] args = command.split(" ");
		if (args.length < 3) {
			return;
		}
		player.getActionSender().sendMessage("Yes");
		final String simulatorType = args[1];
		final int simulationAmount = Integer.parseInt(args[2]);
		ArrayList<Item> simulatedItems = new ArrayList<Item>();
		if (simulatorType.equalsIgnoreCase("barrows")) {
			player.getBarrowsDetails().setBrothersKilled(new boolean[] { true, true, true, true, true, true });
			player.getBarrowsDetails().setCryptCombatKill(1000);
			int toSimulate = simulationAmount;
			while (toSimulate > 0) {
				simulatedItems.addAll(BarrowsHandler.getSingleton().getRewards(player));
				toSimulate--;
			}
		} else if (simulatorType.equalsIgnoreCase("crystalkey")) {
			int toSimulate = simulationAmount;
			while (toSimulate > 0) {
				simulatedItems.addAll(new CrystalKeyReward(player).getRewards());
				toSimulate--;
			}
		} else if (simulatorType.equalsIgnoreCase("drops")) {
			if (args.length < 4) {
				return;
			}
			int npcId = simulationAmount;
			int toSimulate = Integer.parseInt(args[3]);
			while (toSimulate > 0) {
				simulatedItems.addAll(NPCDropManager.getDrops(player, npcId));
				toSimulate--;
			}
		}

		if (simulatedItems.isEmpty()) {
			return;
		}
		simulatedItems = checkForDuplicates(simulatedItems);
		simulatedItems.sort((item1, item2) -> Integer.compare(item1.getId(), item2.getId()));

		player.getActionSender().sendItemsOnInterface(42101, simulatedItems);
		player.getActionSender().sendString(Utility.optimizeText(simulatorType) + " simulator", 42102);
		if (simulatorType.equalsIgnoreCase("drops")) {
			player.getActionSender().sendString("Simulated " + Integer.parseInt(args[3]) + " " + NPCDefinitions.get(Integer.parseInt(args[2])).getName() + " kills.", 42103);
		} else {
			player.getActionSender().sendString("Simulated " + Utility.formatNumbers(simulationAmount) + " rewards.", 42103);
		}
		player.getActionSender().sendInterface(42100);
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

	private ArrayList<Item> checkForDuplicates(final ArrayList<Item> toCheck) {
		final ArrayList<Item> toReturn = new ArrayList<Item>();
		for (Item item : toCheck) {
			if (item == null) {
				continue;
			}
			boolean addItem = true;
			for (Item i : toReturn) {
				if (i == null) {
					continue;
				}
				if (i.getId() == item.getId()) {
					i.incrementAmount(item.getAmount());
					addItem = false;
				}
			}
			if (addItem) {
				toReturn.add(item);
			}
		}
		return toReturn;
	}

}