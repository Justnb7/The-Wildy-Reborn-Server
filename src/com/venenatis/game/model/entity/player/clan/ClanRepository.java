package com.venenatis.game.model.entity.player.clan;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.venenatis.game.constants.Constants;

/**
 * Class handles the saving and loading of all clan chat channels.
 * 
 * @author Daniel
 * @author Michael
 * 
 */
public class ClanRepository {

	/**
	 * Map of all active clan chat channels.
	 */
	private static Map<String, Clan> CLANS = new HashMap<>();

	/**
	 * Returns the clan.
	 * 
	 * @param name
	 * @return
	 */
	public static Clan get(String name) {
		return CLANS.get(name);
	}

	/**
	 * Adds the clan.
	 * 
	 * @param clan
	 */
	public static void add(Clan clan) {
		CLANS.put(clan.getOwner(), clan);
	}

	/**
	 * Loads all clans and puts them into the map.
	 */
	public static void load() {
		Type type = new TypeToken<Map<String, Clan>>() { }.getType();

		Path path = Paths.get(Constants.DATA_DIR, "/clans/world_clan_list.json");
		try (FileReader reader = new FileReader(path.toFile())) {
			JsonParser parser = new JsonParser();
			CLANS = new GsonBuilder().create().fromJson(parser.parse(reader), type);
			for (Clan clan : CLANS.values()) {
			      clan.init();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves all clans into a json file.
	 */
	public static void save() {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		try (FileWriter fw = new FileWriter("./Data/clans/world_clan_list.json")) {
			fw.write(gson.toJson(CLANS));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}