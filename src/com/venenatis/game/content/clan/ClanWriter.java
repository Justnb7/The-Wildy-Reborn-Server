package com.venenatis.game.content.clan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Handles the clan saving/loading.
 * 
 * @author Gabriel | Wolfs Darker
 *
 */
public class ClanWriter {

	/**
	 * The directory where the clans will be saved/loaded.
	 */
	public static final String CLANS_DIRECTORY = "./data/clans/";

	/**
	 * The GSON used to load the clan chats.
	 */
	public static final Gson LOADER = new Gson();

	/**
	 * The GSON used to save the clan chats.
	 */
	public static final Gson SAVER = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Saves a clan chat in JSON format.
	 * 
	 * @param clan
	 * @return if it was successful
	 */
	public static boolean saveClan(Clan clan) {
		try (final BufferedWriter composer = new BufferedWriter(new FileWriter(CLANS_DIRECTORY + clan.getClanOwner()
				+ ".json"))) {
			composer.write(SAVER.toJson(clan));
			return true;
		} catch (IOException exception) {
			System.out.println("Error while saving clan chat: " + clan.getClanOwner() + "'s clan chat.");
			exception.printStackTrace();
			return false;
		}
	}

	/**
	 * Load all the clanc hats from the directory folder.
	 * 
	 * @return
	 */
	public static List<Clan> loadClanChats() {
		List<Clan> clan_list = new ArrayList<Clan>();

		String[] files = new File(CLANS_DIRECTORY).list();

		if (files == null || files.length == 0) {
			return clan_list;
		}

		for (int i = 0; i < files.length; i++) {
			try {
				if (files[i].endsWith("json")) {
					Clan clan = LOADER.fromJson((new FileReader(CLANS_DIRECTORY + files[i])), Clan.class);
					if (clan != null) {
						clan_list.add(clan);
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("Error loading a clan chat: " + files[i] + "");
			}
		}

		return clan_list;
	}
}
