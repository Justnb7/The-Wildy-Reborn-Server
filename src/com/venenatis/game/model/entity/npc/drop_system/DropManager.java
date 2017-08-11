package com.venenatis.game.model.entity.npc.drop_system;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.definitions.NPCDefinitions;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;

public class DropManager {
	
	/**
	 * The logger that will print important information.
	 */
	private static final Logger LOGGER = Logger.getLogger(DropManager.class.getName());

	private static final DecimalFormat PERCENTILE_FORMAT = new DecimalFormat("#.###");

	public static int AMOUNT_OF_TABLES = 0;

	private static final Comparator<Integer> COMPARE_NAMES = new Comparator<Integer>() {

		@Override
		public int compare(Integer o1, Integer o2) {
			String name1 = NPCDefinitions.get(o1).getName(); 
			String name2 = NPCDefinitions.get(o2).getName();
			return name1.compareToIgnoreCase(name2);
		}
	};

	private Map<List<Integer>, TableGroup> groups = new HashMap<>();

	private List<Integer> ordered = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public void read() {
		JSONParser parser = new JSONParser();
		try {
			FileReader fileReader = new FileReader("./data/def/mob/npc_droptable.json");
			JSONArray data = (JSONArray) parser.parse(fileReader);
			Iterator<?> drops = data.iterator();

			while (drops.hasNext()) {
				JSONObject drop = (JSONObject) drops.next();

				List<Integer> npcIds = new ArrayList<>();

				if (drop.get("npc_id") instanceof JSONArray) {
					JSONArray idArray = (JSONArray) drop.get("npc_id");
					idArray.forEach(id -> npcIds.add(((Long) id).intValue()));
				} else {
					npcIds.add(((Long) drop.get("npc_id")).intValue());
				}

				TableGroup group = new TableGroup(npcIds);

				for (TablePolicy policy : TablePolicy.POLICIES) {
					if (!drop.containsKey(policy.name().toLowerCase())) {
						continue;
					}
					JSONObject dropTable = (JSONObject) drop.get(policy.name().toLowerCase());
					Table table = new Table(policy, ((Long) dropTable.get("accessibility")).intValue());
					JSONArray tableItems = (JSONArray) dropTable.get("items");
					Iterator<?> items = tableItems.iterator();

					while (items.hasNext()) {
						JSONObject item = (JSONObject) items.next();
						int id = ((Long) item.get("item")).intValue();
						int minimumAmount = ((Long) item.get("minimum")).intValue();
						int maximumAmount = ((Long) item.get("maximum")).intValue();
						table.add(new Drop(npcIds, id, minimumAmount, maximumAmount));
					}
					group.add(table);
				}
				groups.put(npcIds, group);
			}
			ordered.clear();

			for (TableGroup group : groups.values()) {
				if (group.getNpcIds().size() == 1) {
					ordered.add(group.getNpcIds().get(0));
					continue;
				}
				for (int id : group.getNpcIds()) {
					String name = NPCDefinitions.get(id).getName();
					if (ordered.stream().noneMatch(i -> NPCDefinitions.get(i).getName().equals(name))) {
						ordered.add(id);
					}
				}
			}

			ordered.sort(COMPARE_NAMES);
			LOGGER.info("Loaded " + ordered.size() + " drop tables.");
			AMOUNT_OF_TABLES = ordered.size();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to create a drop for a player after killing a non-playable
	 * character
	 * 
	 * @param player
	 *            the player receiving a possible drop
	 * @param npc
	 *            the npc dropping the items
	 */
	public void create(Player player, NPC npc, Location location, int repeats) {
		
		Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npc.getId())).findFirst();
		
		group.ifPresent(g -> {
			double modifier = getModifier(player);
			List<Item> drops = g.access(player, modifier, repeats);
			
			for (Item item : drops) {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), location, player));
				player.debug(String.format("drop %s on location %s%n", new Item(item.getId(), item.getAmount()), location));
			}
			
			/**
			 * Crystal keys
			 */
			if (Utility.random(115) == 1) {
				player.getActionSender().sendMessage("@pur@You sense a crystal key being dropped to the ground.");
				GroundItemHandler.createGroundItem(new GroundItem(new Item(989, 1), location, player));
			}
		});
	}

	private double getModifier(Player player) {
		double modifier = 1.0;
		if (player.getEquipment().contains(2572)) {
			modifier -= .03;
		} else if (player.getEquipment().contains(12785)) {
			modifier -= .05;
		}
		if (player.getRights().isDonator(player)) {
			modifier -= 0.020;
		}
		return modifier;
	}

	public void clear(Player player) {
		for(int i = 0; i < 150; i++) {
			player.getActionSender().sendString("", 42531 + i);
		}
		player.searchList.clear();
	}

	public void open(Player player) {
		clear(player);
		
		for (int index = 0; index < ordered.size(); index++) {
			player.getActionSender().sendString(StringUtils.capitalize(NPCDefinitions.get(ordered.get(index)).getName().toLowerCase().replaceAll("_", " ")), 42531 + index);
		}

		player.getActionSender().sendInterface(42500);
	}

	public void search(Player player, String name) {
		if(name.matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]+$")) {
			player.getActionSender().sendMessage("You may not search for alphabetical and numerical combinations.");
			return;
		}
		
		clear(player);

		List<Integer> definitions = ordered.stream().filter(Objects::nonNull).filter(def -> NPCDefinitions.get(def).getName() != null).filter(def -> NPCDefinitions.get(def).getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());

		if(definitions.isEmpty()) {
			definitions = ordered.stream().filter(Objects::nonNull).collect(Collectors.toList());
			List<Integer> npcs = new ArrayList<>();
			int count = 0;
			for(Integer index : definitions) {
				Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(NPCDefinitions.get(index).getId())).findFirst();
				if(group.isPresent()) {
					TableGroup g = group.get();
					
					for(TablePolicy policy : TablePolicy.values()) {
						Optional<Table> table = g.stream().filter(t -> t.getPolicy() == policy).findFirst();
						if(table.isPresent()) {
							for(Drop drop : table.get()) {
								if(drop == null) {
									continue;
								}
								
								if(ItemDefinition.get(drop.getItemId()).getName().toLowerCase().contains(name.toLowerCase())) {
									//player.debug(String.format("drop %s vs name %s%n", ItemDefinition.get(drop.getItemId()).getName().toLowerCase(), name.toLowerCase()));
									npcs.add(index);
									player.getActionSender().sendString(StringUtils.capitalize(NPCDefinitions.get(NPCDefinitions.get(index).getId()).getName().toLowerCase().replaceAll("_", " ")), 42531 + count);
									count++;
								}
							}
							
							
						}
					}
				};

			}
			
			player.searchList = npcs;
			return;
			
		}
		
		for(int npcId = 0; npcId < definitions.size(); npcId++) {
			//player.debug(String.format("npcId %d%n", npcId));
			if(npcId >= 150) {
				break;
			}
			//player.debug(String.format("npc name: %s%n", NPCDefinitions.get(definitions.get(npcId)).getName()));
			player.getActionSender().sendString(StringUtils.capitalize(NPCDefinitions.get(definitions.get(npcId)).getName().toLowerCase().replaceAll("_", " ")), 42531 + npcId);
		}

		player.searchList = definitions;
	}

	public void select(Player player, int button) {
		int listIndex = button - 166035;
		if (listIndex < 0 || listIndex > ordered.size() - 1) {
			System.out.println("index is below 0");
			return;
		}

		int npcId = player.searchList.isEmpty() ? ordered.get(listIndex) : player.searchList.get(listIndex);

		Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npcId)).findFirst();

		group.ifPresent(g -> {
			if (System.currentTimeMillis() - player.lastDropTableSelected < TimeUnit.SECONDS.toMillis(5)) {
				player.getActionSender().sendMessage("You can only do this once every 5 seconds.");
				return;
			}
			player.lastDropTableSelected = System.currentTimeMillis();
			String name = StringUtils.capitalize(NPCDefinitions.get(npcId).getName().toLowerCase().replaceAll("_", " "));
			player.getActionSender().sendString(name, 42502);
			double modifier = getModifier(player);
			for (TablePolicy policy : TablePolicy.POLICIES) {
				Optional<Table> table = g.stream().filter(t -> t.getPolicy() == policy).findFirst();
				if (table.isPresent()) {
					double chance = (1.0 / (double) (table.get().getAccessibility() * modifier)) * 100D;
					int in_kills = (int) (100 / chance);
					if (chance > 100.0) {
						chance = 100.0;
					}
					if (in_kills == 0) {
						in_kills = 1;
					}
					if (player.dropRateInKills) {
						player.getActionSender().sendString("1/"+in_kills+"" + "", 42514 + policy.ordinal());
					} else {
						player.getActionSender().sendString(PERCENTILE_FORMAT.format(chance) + "%", 42514 + policy.ordinal());
					}
					updateAmounts(player, policy, table.get());
					updateTable(player, table.get());
				} else {
					player.getActionSender().sendString("-", 42514 + policy.ordinal());
					updateAmounts(player, policy, new ArrayList<Drop>());
					updateTable(player, new Table(policy, -1));
				}
			}
		});
	}

	public static void updateTable(Player player, Table table) {
		if (player == null || player.getOutStream() == null) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(42733 + table.getPolicy().ordinal());
		int length = table.size();
		int current = 0;

		player.getOutStream().writeWord(length);
		for (Drop drop : table) {
			if (drop.getMaximumAmount() > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(drop.getMaximumAmount());
			} else {
				player.getOutStream().writeByte(drop.getMaximumAmount());
			}
			player.getOutStream().writeWordBigEndianA(drop.getItemId() + 1);
			current++;
		}

		for (; current < 50; current++) {
			player.getOutStream().writeByte(1);
			player.getOutStream().writeWordBigEndianA(-1);
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}

	private void updateAmounts(Player player, TablePolicy policy, List<Drop> drops) {
		int collumnOffset = policy.ordinal() * 100;

		for (int index = 0; index < drops.size(); index++) {
			Drop drop = drops.get(index);
			int minimum = drop.getMinimumAmount();
			int maximum = drop.getMaximumAmount();
			int frame = 42739 + collumnOffset + (index * 2);
			if (minimum == maximum) {
				player.getActionSender().sendString("", frame);
			} else {
				player.getActionSender().sendString(Utility.getValueWithoutRepresentation(drop.getMinimumAmount()) + " - " + Utility.getValueWithoutRepresentation(drop.getMaximumAmount()), frame);
			}
		}

		for (int index = drops.size(); index < 50; index++) {
			player.getActionSender().sendString("", 42739 + collumnOffset + (index * 2));
		}
	}
}
