package com.model.game.character.player.content;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.model.game.character.npc.NPC;
import com.model.game.character.npc.drops.Drop;
import com.model.game.character.npc.drops.Table;
import com.model.game.character.npc.drops.TableGroup;
import com.model.game.character.npc.drops.TablePolicy;
import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.character.player.packets.out.SendInterfacePacket;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.utility.Location3D;
import com.model.utility.Utility;
import com.model.utility.json.definitions.NpcDefinition;

public class DropManager {

	private static final DecimalFormat PERCENTILE_FORMAT = new DecimalFormat("#.###");
	
	private static final Comparator<Integer> COMPARE_NAMES = new Comparator<Integer>() {
		
		@Override
		public int compare(Integer o1, Integer o2) {
				String name1 = NpcDefinition.get(o1).getName();
				String name2 = NpcDefinition.get(o2).getName();
				return name1.compareToIgnoreCase(name2);
		}
	};

	private Map<List<Integer>, TableGroup> groups = new HashMap<>();

	private List<Integer> ordered = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public void read() {
		JSONParser parser = new JSONParser();
		try {
			JSONArray data = (JSONArray) parser.parse(new FileReader("./Data/json/npc_droptable.json"));
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
						table.add(new Drop(id, minimumAmount, maximumAmount));
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
					String name = NpcDefinition.get(id).getName();
					if (ordered.stream().noneMatch(i -> NpcDefinition.get(i).getName().equals(name))) {
						ordered.add(id);
					}
				}
			}
			
			ordered.sort(COMPARE_NAMES);
			Utility.println("Loaded " + ordered.size() + " drop tables.");
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
	public void create(Player player, NPC npc, Location3D location, int repeats) {
		Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npc.getId())).findFirst();
		
		group.ifPresent(g -> {
			double modifier = getModifier(player);
			List<Item> drops = g.access(player, modifier, repeats);
			
			for (Item item : drops) {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), location.getX(), location.getY(), location.getZ(), player));
			}
			if (Utility.random(150) == 1) {
				if (npc.getDefinition().getCombatLevel() > 0 && npc.getDefinition().getCombatLevel() <= 80) {
					GroundItemHandler.createGroundItem(new GroundItem(new Item(2677, 1), location.getX(), location.getY(), location.getZ(), player));
				} else if (npc.getDefinition().getCombatLevel() > 80 && npc.getDefinition().getCombatLevel() <= 160) {
					GroundItemHandler.createGroundItem(new GroundItem(new Item(2801, 1), location.getX(), location.getY(), location.getZ(), player));
				} else if (npc.getDefinition().getCombatLevel() > 160) {
					GroundItemHandler.createGroundItem(new GroundItem(new Item(2722, 1), location.getX(), location.getY(), location.getZ(), player));
				}
			}
			
			if (Utility.random(115) == 1) {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(989, 1), location.getX(), location.getY(), location.getZ(), player));
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
		if (player.getRights().contains(Rights.ELITE_DONATOR)) {
			modifier -= 0.050;
		} else if (player.getRights().contains(Rights.SUPER_DONATOR)) {
			modifier -= 0.035;
		} else if (player.getRights().contains(Rights.DONATOR)) {
			modifier -= 0.020;
		}
		return modifier;
	}
	
	public void open(Player player) {
		if (!player.dropListSorted) {
			for (int index = 0; index < ordered.size(); index++) {
				player.getActionSender().sendString(StringUtils.capitalize(NpcDefinition.get(ordered.get(index)).getName().toLowerCase().replaceAll("_", " ")), 42531 + index);
			}
			player.dropListSorted = true;
		}
		player.write(new SendInterfacePacket(42500));
	}

	public void select(Player player, int button) {
		int listIndex = button - 166035;
		if (listIndex < 0 || listIndex > ordered.size() - 1) {
			return;
		}
		int npcId = ordered.get(listIndex);

		Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npcId)).findFirst();

		group.ifPresent(g -> {
			if (System.currentTimeMillis() - player.lastDropTableSelected < TimeUnit.SECONDS.toMillis(5)) {
				player.getActionSender().sendMessage("You can only do this once every 5 seconds.");
				return;
			}
	
			player.lastDropTableSelected = System.currentTimeMillis();
			String name = StringUtils.capitalize(NpcDefinition.get(npcId).getName().toLowerCase().replaceAll("_", " "));
			player.getActionSender().sendString(name + " (" + npcId + ")", 42502);
			double modifier = getModifier(player);
			for (TablePolicy policy : TablePolicy.POLICIES) {
				Optional<Table> table = g.stream().filter(t -> t.getPolicy() == policy).findFirst();
				if (table.isPresent()) {
					double chance = (1.0 / (double) (table.get().getAccessibility() * modifier)) * 100D;
					if (chance > 100.0) {
						chance = 100.0;
					}
					player.getActionSender().sendString(PERCENTILE_FORMAT.format(chance) + "%", 42514 + policy.ordinal());
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

	public void test(Player player, int npcId, int amount) {
		TableGroup group = groups.get(npcId);
		if (group == null) {
			return;
		}
		while (amount-- > 0) {
			List<Item> drops = group.access(player, 1.0, 1);
			drops.forEach(item -> player.getBank().add(new Item(item.getId(), item.getAmount())));
		}
	}

}