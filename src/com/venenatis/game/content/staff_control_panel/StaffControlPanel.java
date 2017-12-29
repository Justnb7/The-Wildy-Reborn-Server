package com.venenatis.game.content.staff_control_panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.venenatis.game.content.option_menu.ClientOptionMenu;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.World;

public class StaffControlPanel {
	
	private static final int MAIN_INTERFACE_ID = 42300;

	private static final int PLAYER_INFORMATION_INTERFACE_ID = 42275;

	public static final int STAFF_PANEL_OPTION_MENU_IDENTIFIER = 42311;

	private HashMap<Integer, Player> searchMap;

	private Player inspecting;
	
	private Player player;
	
	public StaffControlPanel(Player player) {
		this.player = player;
		searchMap = new HashMap<Integer, Player>();
	}

	public void openPanel() {
		player.getActionSender().sendInterface(MAIN_INTERFACE_ID);
	}

	public void handleInputField(final String input) {
		final Collection<ClientOptionMenu> results = new ArrayList<ClientOptionMenu>();

		if (input.length() < 1) {
			player.getActionSender().sendString("Use atleast 1 character", STAFF_PANEL_OPTION_MENU_IDENTIFIER);
			player.getActionSender().sendOptionMenuInterface(STAFF_PANEL_OPTION_MENU_IDENTIFIER, results);
			player.getActionSender().setScrollPosition(42155, 0, 0);
			return;
		}

		searchMap.clear();

		int index = 0;

		for (Player p : World.getWorld().getPlayers()) {
			if (p == null || !p.isActive()) {
				continue;
			}
			if (p.getUsername().toLowerCase().contains(input.toLowerCase())) {
				results.add(new ClientOptionMenu(index, p.getUsername(), p.getRights().toString()));
				searchMap.put(index, p);
				index++;
			}
		}

		player.getActionSender().sendString(results.isEmpty() ? "No results found" : "", STAFF_PANEL_OPTION_MENU_IDENTIFIER);
		player.getActionSender().sendOptionMenuInterface(STAFF_PANEL_OPTION_MENU_IDENTIFIER, results);
		player.getActionSender().setScrollPosition(42310, 0, results.size() * 42 + 4);
	}

	public void handleOptionMenuClick(final int identifier, final int optionType) {
		if (!searchMap.containsKey(identifier)) {
			player.getActionSender().sendMessage("Something went wrong! Please refresh your search results!");
			return;
		}
		inspecting = searchMap.get(identifier);
		showPlayerInformationTab();
	}

	public boolean handleActionButtonClick(final int actionButtonId) {
		switch (actionButtonId) {
		case 165075:
			showPlayerInformationTab();
			return true;

		case 165079:
			showInfractionsTab();
			return true;
		}

		return false;
	}

	public void showPlayerInformationTab() {
		if (inspecting == null) {
			return;
		}
		player.getActionSender().sendInterface(PLAYER_INFORMATION_INTERFACE_ID);
		int textChildId = 42351;
		final int totalTextChilds = 10;
		player.getActionSender().sendString("Username: " + inspecting.getUsername(), textChildId++);
		player.getActionSender().sendString("Rank: " + inspecting.getRights().name(), textChildId++);
		player.getActionSender().sendString("Donator Status: " + "", textChildId++);
		player.getActionSender().sendString("Game mode: " + inspecting.getAccount().getType().alias(), textChildId++);
		player.getActionSender().sendString("Location: " + inspecting.getLocation().toString(), textChildId++);

		for (int index = textChildId; index <= 42350 + totalTextChilds; index++) {
			player.getActionSender().sendString("", index);
		}

	}

	public void showInfractionsTab() {
		if (inspecting == null) {
			return;
		}
	}

}