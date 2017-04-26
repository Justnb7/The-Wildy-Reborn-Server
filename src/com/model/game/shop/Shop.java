package com.model.game.shop;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.model.Server;
import com.model.game.Constants;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.item.container.ItemContainerPolicy;
import com.model.utility.Utility;

/**
 * The container that represents a shop players can buy and sell items from.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class Shop {

	public static final Object GENERAL_STORE = new Object();

	/**
	 * The map that holds all of the shop names mapped to their shop instances.
	 */
	public static final Map<String, Shop> SHOPS = new HashMap<>();

	/**
	 * The name of this current shop.
	 */
	private final String name;

	/**
	 * The item container that contains the items within this shop.
	 */
	private final Container container = new Container(ItemContainerPolicy.STACK_ALWAYS, 40);

	/**
	 * The flag that determines if this shop will restock its items.
	 */
	private final boolean restock;

	/**
	 * The flag that determines if items can be sold to this shop.
	 */
	private final boolean canSell;

	/**
	 * The currency that items within this shop will be bought with.
	 */
	private final Currency currency;

	/**
	 * The set of players that are currently viewing this shop.
	 */
	private final Set<Player> players = new HashSet<>();

	/**
	 * The map of cached shop item identifications and their amounts.
	 */
	private final Map<Integer, Integer> itemCache;

	/**
	 * Creates a new {@link Shop}.
	 *
	 * @param name
	 *            the name of this current shop.
	 * @param items
	 *            the items within this shop.
	 * @param restock
	 *            the flag that determines if this shop will restock its items.
	 * @param canSell
	 *            the flag that determines if items can be sold to this shop.
	 * @param currency
	 *            the currency that items within this shop will be bought with.
	 */
	public Shop(String name, Item[] items, boolean restock, boolean canSell, Currency currency) {
		this.name = name;
		this.restock = restock;
		this.canSell = canSell;
		this.currency = currency;
		this.container.setItems(items);
		this.itemCache = new HashMap<>(container.capacity());
		Arrays.stream(items).filter(Objects::nonNull).forEach(item -> itemCache.put(item.getId(), item.getAmount()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Shop))
			return false;
		Shop other = (Shop) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * Opens this shop by displaying the interface for {@code player}.
	 *
	 * @param player
	 *            the player to open the shop for.
	 */
	public void openShop(Player player) {
		if (Server.getMultiplayerSessionListener().inAnySession(player)) {
			return;
		}
		if (name.equals("Bounty Hunter Store")) {
			player.getActionSender().sendInterfaceConfig(0, 28050);
			player.getActionSender().sendString("Bounties: " + Utility.insertCommas(Integer.toString(player.getBountyPoints())), 28052);
		} else {
			player.getActionSender().sendInterfaceConfig(1, 28050);
		}
		player.setShopping(true);
		player.getItems().resetItems(3823);
		player.getActionSender().sendItemsOnInterface(3900, container.container(), container.size());
		player.setOpenShop(name);
		player.getActionSender().sendInterfaceWithInventoryOverlay(3824, 3822);
		player.getActionSender().sendString(name, 3901);
		players.add(player);
	}

	/**
	 * Updates the items and the containers that display items for
	 * {@code player}.
	 *
	 * @param player
	 *            the player this shop will be updated for.
	 * @param checkStock
	 *            if the stock should be checked.
	 */
	public void updateShop(Player player, boolean checkStock) {
		player.getItems().resetItems(3823);
		int size = container.size();
		players.stream().filter(Objects::nonNull).forEach(p -> p.getActionSender().sendItemsOnInterface(3900, container.container(), size));

		if (checkStock && restock) {
			if (Server.getTaskScheduler().running(this) || !needsRestock())
				return;
			Server.getTaskScheduler().submit(new ShopRestockTask(this));
		}
		if (!checkStock && name.equals("General Store")) {
			if (Server.getTaskScheduler().running(GENERAL_STORE))
				return;
			List<Item> list = getAlienItems(this);
			Server.getTaskScheduler().submit(new GeneralStoreTask(this, list));
		}
	}

	private static List<Item> getAlienItems(Shop shop) {
		List<Item> list = new LinkedList<>();
		for (Item item : shop.container) {
			if (item == null)
				continue;
			if (!shop.itemCache.containsKey(item.id))
				list.add(item);
		}
		return list;
	}

	/**
	 * Sends the determined selling value of {@code item} to {@code player}.
	 *
	 * @param player
	 *            the player to send the value to.
	 * @param item
	 *            the item to send the value of.
	 */
	public void sendSellingPrice(Player player, Item item) {
		String itemName = item.getDefinition().getName();
		if(player.getArea().inWild()) {
			return;
		}
		if (!canSell) {
			player.getActionSender().sendMessage("You cannot sell items here.");
			return;
		}
		if (Arrays.stream(Constants.SPAWNABLES).anyMatch($it -> $it.contains(itemName))) {
			player.getActionSender().sendMessage("<col=ff0000>" + Utility.formatPlayerName(itemName) + " <col=0>may not be sold!");
			return;
		}
		if (!container.contains(item.getId()) && !name.equalsIgnoreCase("General Store")) {
			player.getActionSender().sendMessage("You can't sell " + itemName + " " + "to this store.");
			return;
		}
		
		String formatPrice = Utility.sendCashToString((int) Math.floor(determinePrice(player, item)));
		player.getActionSender().sendMessage(itemName + ": shop will buy for " + formatPrice + " " + currency + ".");
	}

	/**
	 * Sends the determined purchase value of {@code item} to {@code player}.
	 *
	 * @param player
	 *            the player to send the value to.
	 * @param item
	 *            the item to send the value of.
	 */
	public void sendPurchasePrice(Player player, Item item) {
		if(player.getArea().inWild()) {
			return;
		}
		Item shopItem = container.searchItem(item.getId()).orElse(null);
		if (shopItem == null)
			return;
		if (shopItem.getAmount() <= 0) {
			player.getActionSender().sendMessage("There is none of this item left in stock!");
			return;
		}
		if (!player.getAccount().getType().shopAccessible(player.getOpenShop())) {
			player.getActionSender().sendMessage("You are not permitted to use this shop because of a restriction on your account.");
			return;
		}
		if (player.getOpenShop().equals("Vote Point shop")) {
			player.getActionSender().sendMessage(item.getDefinition().getName() + " currently costs " + Utility.insertCommas(Integer.toString(determinePrice(player, item))) + " Vote points.");
			return;
		}
		if (player.getOpenShop().equals("Gear Point Store")) {
			player.getActionSender().sendMessage(item.getDefinition().getName() + " currently costs " + Utility.insertCommas(Integer.toString(determinePrice(player, item))) + " Gear points.");
			return;
		}
		if (player.getOpenShop().equals("Slayer Shop")) {
			player.getActionSender().sendMessage("You need a slayer level of " + slayerLevelReq(item.getId()) + " to buy "+player.getItems().getItemName(item.getId())+"");
			return;
		}
		if (player.getOpenShop().equals("Achievement Rewards")) {
			if (!player.getAchievements().hasBoughtItem(item.getId())) {
				player.getActionSender().sendMessage(item.getDefinition().getName() + ": shop will sell for " + Utility.sendCashToString(determinePrice(player, item)) + " APs. You only need to buy this once.");
			} else {
				player.getActionSender().sendMessage(item.getDefinition().getName() + ": shop will sell for " + Utility.sendCashToString(determinePrice(player, item)) + " coins.");
			}
			return;
		}
		//player.sendMessage("" + item.getDefinition().getId());
		if (item.getDefinition() == null) {
			player.getActionSender().sendMessage("NULLED ITEM: please notify the staff.");
			return;
		} else {
			player.getActionSender().sendMessage(item.getDefinition().getName() + ": " + "shop will sell for " + Utility.sendCashToString(determinePrice(player, item)) + " " + currency + ".");
		}
	}

	/**
	 * The method that allows {@code player} to purchase {@code item}.
	 *
	 * @param player
	 *            the player who will purchase this item.
	 * @param item
	 *            the item that will be purchased.
	 * @return {@code true} if the player purchased the item, {@code false}
	 *         otherwise.
	 */
	public boolean purchase(Player player, Item item) {
		Item shopItem = container.searchItem(item.getId()).orElse(null);
		if (shopItem == null)
			return false;

		if (shopItem.getAmount() <= 0) {
			player.getActionSender().sendMessage("There is none of this item left in stock!");
			return false;
		}
		if (shopItem.getDefinition() == null) {
			player.getActionSender().sendMessage("NULL_ITEM: shop will not sell this item, please notify the staff.");
			return false;
		}
		if (!player.getAccount().getType().shopAccessible(player.getOpenShop())) {
			player.getActionSender().sendMessage("You are not permitted to use this shop because of a restriction on your account.");
			return false;
		}
		if (player.getInventory().alreadyHasItem(12791) && shopItem.getId() == 12791) {
			player.getActionSender().sendMessage("You cannot own more then one rune pouch at the time.");
			return false;
		}
		if (player.getOpenShop().equals("Achievement Rewards")) {
			player.getAchievements().setBoughtItem(item.getId());
		} else if (player.getOpenShop().equals("Bounty Hunter Store")) {
			player.getActionSender().sendString("Bounties: " + Utility.insertCommas(Integer.toString(player.getBountyPoints())), 28052);
		} else if (player.getOpenShop().equals("Blood money rewards")) {
			if (item.getId() == 19484) {
				player.getInventory().add(new Item(item.getId(), 9));
			}
		}
		if (item.getAmount() > shopItem.getAmount())
			item.setCount(shopItem.getAmount());
		if (!player.getItems().spaceFor(item)) {
			item.setCount(player.getItems().freeSlots());
			if (item.getAmount() == 0) {
				player.getActionSender().sendMessage("You do not have enough space" + " in your inventory to buy this item!");
				return false;
			}
		}
		int value = determinePrice(player, item);
		long totalCost = (long)value * (long)item.getAmount();
		if (currency.getCurrency().currencyAmount(player) < totalCost) {
			player.getActionSender().sendMessage("You do not have enough " + currency + " to buy this item.");
			return false;
		}
		if (player.getItems().freeSlots() >= item.getAmount() && !item.getDefinition().isStackable()
				|| player.getItems().freeSlots() >= 1 && item.getDefinition().isStackable()
				|| player.getInventory().playerHasItem(item.getId()) && item.getDefinition().isStackable()) {

			if (itemCache.containsKey(item.getId()) && !player.getOpenShop().equals("Gear Point Store") && !player.getOpenShop().equals("Donator Ticket Shop") && !player.getOpenShop().equals("Bounty Hunter Store")) {
				container.searchItem(item.getId()).ifPresent(i -> i.decrementAmountBy(item.getAmount()));
			} else if (!itemCache.containsKey(item.getId())) {
				container.remove(item);
			}
			currency.getCurrency().takeCurrency(player, (int)totalCost);
			player.getInventory().add(item);
		} else {
			player.getActionSender().sendMessage("You don't have enough space in your inventory.");
			return false;
		}
		updateShop(player, true);
		return true;
	}

	/**
	 * The method that allows {@code player} to sell {@code item}.
	 *
	 * @param player
	 *            the player who will sell this item.
	 * @param item
	 *            the item that will be sold.
	 * @return {@code true} if the player sold the item, {@code false}
	 *         otherwise.
	 */
	public boolean sell(Player player, Item item, int fromSlot) {
		String itemName = item.getDefinition().getName();
		if (!Item.valid(item))
			return false;
		if (!canSell) {
			player.getActionSender().sendMessage("You cannot sell items here.");
			return false;
		}
		if (player.getOpenShop().equals("Skillcape Shop")) {
			return false;
		}
		if (item.getDefinition().getGeneralPrice() > 100_000_000) {
			player.getActionSender().sendMessage("The shop keeper cannot afford your, "+item.getDefinition().getName()+".");
			return false;
		}
		if (!player.getAccount().getType().shopAccessible(player.getOpenShop())) {
			player.getActionSender().sendMessage("You are not permitted to use this shop because of a restriction on your account.");
			return false;
		}
		if (!player.getInventory().playerHasItem(item.getId()))
			return false;
		if (Arrays.stream(Constants.SPAWNABLES).anyMatch($it -> $it.equalsIgnoreCase(itemName))) {
			player.getActionSender().sendMessage("<col=ff0000>" + Utility.formatPlayerName(itemName) + " <col=0>may not be sold!");
			return false;
		}
		if (!container.contains(item.getId()) && !name.equalsIgnoreCase("General Store")
				&& !name.equalsIgnoreCase("Iron Store")) {
			player.getActionSender().sendMessage("You can't sell " + item.getDefinition().getName() + " to this store.");
			return false;
		}
		if (!container.spaceFor(item)) {
			player.getActionSender().sendMessage("There is no room in this store for the item you are trying to sell!");
			return false;
		}
		if (player.getItems().freeSlots() == 0 && !currency.getCurrency().canRecieveCurrency(player)) {
			player.getActionSender().sendMessage("You do not have enough space in your inventory to sell this item!");
			return false;
		}
		if (player.isBusy()) {
			return false;
		}

		int amount = player.getInventory().amount(item.getId());
		if (item.getAmount() > amount && !item.getDefinition().isStackable()) {
			item.setCount(amount);
		} else if (item.getAmount() > player.itemAmount[fromSlot] && item.getDefinition().isStackable()) {
			item.setCount(player.itemAmount[fromSlot]);
		}
		player.getInventory().remove(item);
		currency.getCurrency().recieveCurrency(player, item.getAmount() * (int) Math.floor(determinePrice(player, item)));
		if (!player.getOpenShop().equals("Iron Store")) {
			if (container.contains(item.getId())) {
				container.searchItem(item.getId()).ifPresent(i -> i.incrementAmountBy(item.getAmount()));
			} else {
				container.add(item);
			}
		}
		updateShop(player, false);
		return true;
	}

	public static void openSkillCape(Player player) {
		Shop shop = new Shop("Skillcape Shop", getSkillcapes(player), true, false, Currency.COINS);
		shop.openShop(player);
		// setupSkillCapes(player, get99Count(player));
	}

	private static Item[] getSkillcapes(Player player) {
		List<Item> capes = new LinkedList<>();
		for (int i = 0; i < skillCapes.length; i++) {
			if (player.getSkills().getLevelForExperience(i) >= 99 && i != 21)
				capes.add(new Item(skillCapes[i] + 1, 1));
		}
		return capes.toArray(new Item[0]);
	}

	public static int[] skillCapes = { 9747, 9753, 9750, 9768, 9756, 9759, 9762, 9801, 9807, 9783, 9798, 9804, 9780,
			9795, 9792, 9774, 9771, 9777, 9786, 9810, 9765, 1293, 9948 };

	public static int get99Count(Player player) {
		int count = 0;
		for (int j = 0; j < player.getSkills().getLevel(j); j++) {//TODO make sure this works
			if (player.getSkills().getLevelForExperience(j) >= 99) {
				count++;
			}
		}
		return count;
	}

	public static void skillBuy(Player player, int item) {
		int nn = get99Count(player);
		if (nn > 1) {
			nn = 1;
		} else {
			nn = 0;
		}
		for (int j = 0; j < skillCapes.length; j++) {
			if (skillCapes[j] == item || skillCapes[j] + 1 == item) {
				if (player.getItems().freeSlots() > 1) {
					if (player.getInventory().playerHasItem(995, 99000)) {
						if (player.getSkills().getLevelForExperience(j) >= 99) {
							player.getInventory().remove(new Item(995, player.getItems().getItemSlot(995)), 99000);
							player.getInventory().add(new Item(skillCapes[j] + nn, 1));
							player.getInventory().add(new Item(skillCapes[j] + 2, 1));
						} else {
							player.getActionSender().sendMessage("You must have 99 in the skill of the cape you're trying to buy.");
						}
					} else {
						player.getActionSender().sendMessage("You need 99,000 coins to buy this item.");
					}
				} else {
					player.getActionSender().sendMessage("You must have at least 1 free inventory space to buy this item.");
				}
			}
		}
		player.getItems().resetItems(3823);
	}

	

	/**
	 * Determines if the items in the container need to be restocked.
	 *
	 * @return {@code true} if the items need to be restocked, {@code false}
	 *         otherwise.
	 */
	protected boolean needsRestock() {
		return container.stream().filter(Objects::nonNull)
				.anyMatch(i -> i.getAmount() <= 0 && itemCache.containsKey(i.getId()));
	}

	/**
	 * Determines if the items in the container no longer need to be restocked.
	 *
	 * @return {@code true} if the items don't to be restocked, {@code false}
	 *         otherwise.
	 */
	protected boolean restockCompleted() {
		return container.stream().filter(Objects::nonNull)
				.allMatch(i -> itemCache.containsKey(i.getId()) && i.getAmount() >= itemCache.get(i.getId()));
	}

	/**
	 * Determines the price of {@code item} based on the currency.
	 *
	 * @param item
	 *            the item to determine the price of.
	 * @return the price of the item based on the currency.
	 */
	private int determinePrice(Player player, Item item) {
		if (item == null) {
			return 0;
		}
		if (player.getOpenShop().equals("Death store")) {
			return Currency.COINS.calculateCurrency(player, item.id) / 3;
		}
		if (player.getOpenShop().equals("Achievement Rewards") && !player.getAchievements().hasBoughtItem(item.getId())) {
			return Currency.ACHIEVEMENT_POINTS.calculateCurrency(player, item.id);
		} else if (player.getOpenShop().equals("Achievement Rewards")
				&& player.getAchievements().hasBoughtItem(item.getId())) {
			return Currency.COINS.calculateCurrency(player, item.id);
		}
		return currency.calculateCurrency(player, item.id);
	}

	/**
	 * Gets the name of this current shop.
	 *
	 * @return the name of this shop.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the item container that contains the items within this shop.
	 *
	 * @return the container that contains the items.
	 */
	public Container getContainer() {
		return container;
	}

	/**
	 * Determines if this shop will restock its items.
	 *
	 * @return {@code true} if this shop will restock, {@code false} otherwise.
	 */
	public boolean isRestock() {
		return restock;
	}

	/**
	 * Determines if items can be sold to this shop.
	 *
	 * @return {@code true} if items can be sold, {@code false} otherwise.
	 */
	public boolean isCanSell() {
		return canSell;
	}

	/**
	 * Gets the currency that items within this shop will be bought with.
	 *
	 * @return the currency that items will be bought with.
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Gets the the set of players that are currently viewing this shop.
	 *
	 * @return the set of players viewing the shop.
	 */
	public Set<Player> getPlayers() {
		return players;
	}

	/**
	 * Gets an unmodifiable version of the map of cached shop item
	 * identifications and their amounts.
	 *
	 * @return the map of cached shop items.
	 */
	public Map<Integer, Integer> getItemCache() {
		return Collections.unmodifiableMap(itemCache);
	}
	
	public static int getDonateValue(int id) {// Donator tickets
		switch (id) {
		// Armour casket
		case 21999:
			return 8;
		// Weapon casket
		case 22000:
			return 14;
		// Pet casket
		case 22001:
			return 50;
		// Cosmetic casket
		case 22002:
			return 100;
		// Venom casket
		case 22003:
			// Zenyte casket
		case 22004:
			return 75;
		// Advanced items casket
		case 22005:
			return 125;
		}
		// Don't allow people to buy items that have no value set, instead set
		// the 'buy'value at max integer 2.147.483.647
		return Integer.MAX_VALUE;
	}
	
	public static int getSlayerValue(int id) {// Slayer task points
		switch (id) {
		case 6798:
			return 100;
		case 85:
			return 50;
		case 989:
			return 35;
		case 2572:
			return 150;
		case 11864:
			return 500;
		}
		return Integer.MAX_VALUE;
	}
	
	public static int getVoteValue(int id) {// Vote points
		switch (id) {
		case 12887:
		case 12888:
		case 12889:
		case 12890:
		case 12891:
		case 12892:
		case 12893:
		case 12894:
		case 12895:
		case 12896:
			return 5;
		case 4151:
		case 2572:
		case 6585:
			return 3;
		case 12357:
			return 10;
		case 12359:
			return 20;
		case 12371:
			return 20;
		case 12373:
			return 20;
		case 12389:
			return 15;
		case 12391:
			return 15;
		case 6199:
			return 20;
		}
		return Integer.MAX_VALUE;
	}
	
	public static int getTargetValue(int id) {// Target points
		switch (id) {
		case 4153:
			return 5;
		case 14484:
			return 50;
		case 10887:
			return 175;
		case 3757:
			return 225;
		case 12821:
			return 20;
		case 12825:
			return 30;
		case 12817:
			return 50;
		case 12827:
			return 250;
		case 11808:
		case 11806:
		case 11804:
			return 35;
		case 1802:
			return 75;
		case 12954:
		case 11128:
		case 15126:
			return 5;
		case 3758:
			return 60;
		case 4151:
		case 6570:
			return 1;
		case 11235:
			return 25;
		case 4212:
		case 19111:
			return 75;
			
			
		}
		return 90;
	}
	
	public static int slayerLevelReq(int id) {// Slayer level req
		switch (id) {
		case 4170:
			return 90;
		}
		return 90;
	}

	public static int getTokkulValue(int id) {
		switch (id) {
		case 19111:
			return 500000;
		case 6571: // Uncut Onyx
			return 210000;
		case 6528: // Tzhaar-ket-om
			return 6000;

		case 6524: // Toktz-ket-xil
			return 6000;

		case 6568: // Obsidian cape
			return 6000;

		case 6525: // Toktz-xil-ek
			return 3000;

		case 6526: // Toktz-mej-tal
			return 3000;

		case 6522: // Toktz-xil-ul
			return 15;

		case 3488: // Gilded kiteshield
		case 3486: // Gilded full helm
		case 3481: // Gilded platebody
		case 3483: // Gilded platelegs
		case 3485: // Gilded plateskirt
			return 8200;

		case 9005: // Fancy boots
		case 9006: // Fighting boots
			return 2000;
		case 13092:
			return 80;
		}
		return 500;
	}
	

	public static int getDonatorTicketValue(int id) {
		switch (id) {

		
		}
		return Integer.MAX_VALUE;
	}
	
	public static int getFreePointsValue(int id) {
		switch (id) {
		
		case 1323:
			return 25;
		case 1712:
			return 35;
		case 2550:
			return 10;
		case 7458:
			return 30;
		case 7459:
			return 35;
		case 7460:
			return 40;
		case 1127:
			return 100;
		case 1163:
			return 40;
		case 1201:
			return 50;
		case 1079:
			return 100;
		case 4131:
			return 90;
		case 3105:
			return 50;
		case 1305:
			return 100;
		case 4597:
			return 100;
		case 1434:
			return 100;
		case 1215:
			return 20;
		case 5698:
			return 25;
		case 861:
			return 5;
		case 9185:
			return 50;
		case 9243:
			return 10;
		case 2503:
			return 15;
		case 2497:
			return 10;
		case 2491:
			return 5;
		case 10499:
			return 25;
		case 4089:
			return 30;
		case 4091:
			return 50;
		case 4093:
			return 40;
		case 5574:
			return 25;
		case 5575:
			return 25;
		case 5576:
			return 25;
		case 3122:
			return 75;
		case 4587:
			return 100;
		}
		return Integer.MAX_VALUE;
	}
	
	public static int getPKShopValue(int id) {
		switch (id) {
		case 4151:
			return 15;
		case 12006:
			return 150;
		case 3204:
			return 75;
		case 4153:
			return 5;
		case 13265:
			return 350;
		case 13271:
			return 375;
		case 13263:
			return 425;
		case 6737:
		case 6735:
		case 6733:
		case 6731:
			return 55;
		case 6585:
			return 15;
		case 12002:
			return 100;
		case 11791:
			return 150;
		case 12904:
			return 250;
		case 6914:
		case 6889:
			return 25;
		case 6916:
		case 6918:
		case 6920:
		case 6922:
		case 6924:
			return 15;
		case 11235:
			return 35;
		case 11785:
			return 215;
		case 2577:
		case 2581:
			return 45;
		case 12596:
			return 75;
		case 12926:
			return 300;
		case 12931:
			return 225;
		case 12905:
			return 1;
		case 12913:
			return 2;
		case 10548:
			return 15;
		case 10551:
			return 35;
		case 6570:
			return 35;
		case 7462:
			return 35;
		case 13576:
			return 300;
		}
		return Integer.MAX_VALUE;
	}
	
	public static int getBMShopValue(int id) {
		switch (id) {
		case 19484:
			return 10;
		case 12006:
			return 525;
		case 4151:
		case 4153:
			return 50;
		case 3204:
			return 35;
		case 13265:
		case 13269:
		case 13271:
			return 850;
		case 13263:
			return 950;
		case 6737:
		case 6735:
		case 6733:
		case 6731:
			return 50;
		case 6585:
			return 50;
		case 12002:
			return 125;
		case 6914:
		case 6889:
			return 75;
		case 6916:
		case 6918:
		case 6920:
		case 6922:
		case 6924:
			return 35;
		case 11235:
			return 75;
		case 2577:
		case 2581:
			return 50;
		case 12596:
			return 100;
		case 12931:
			return 1000;
		case 12905:
			return 100;
		case 12913:
			return 200;
		case 10548:
			return 50;
		case 10551:
			return 50;
		case 6570:
			return 50;
		case 7462:
			return 35;
		case 13576:
			return 650;
		}
		return Integer.MAX_VALUE;
	}
	
	public static int getBounties(int id) {
		switch (id) {
		
		case 12783:
			return 500_000;
		case 12804:
			return 25_000_000;
		case 12853:
			return 10_000_000;
		case 12855:
		case 12856:
			return 2_500_000;
		case 12833:
			return 50_000_000;
		case 12831:
			return 35_000_000;
		case 12829:
			return 25_000_000;
		case 14484:
			return 125_000_000;
		case 12800:
		case 12802:
			return 350_000;
		case 12786:
			return 100_000;
		case 10926:
			return 2_500;
		case 12846:
			return 8_000_000;
		case 12420:
		case 12421:
		case 12419:
		case 12457:
		case 12458:
		case 12459:
			return 10_000_000;
		case 12757:
		case 12759:
		case 12761:
		case 12763:
		case 12771:
			return 500_000;
		case 12526:
			return 1_500_000;
		case 12849:
		case 12798:
			return 250_000;
		case 12608:
		case 12610:
		case 12612:
			return 350_000;
		case 12775:
		case 12776:
		case 12777:
		case 12778:
		case 12779:
		case 12780:
		case 12781:
		case 12782:
			return 5_000;

		}
		return Integer.MAX_VALUE;
	}
}