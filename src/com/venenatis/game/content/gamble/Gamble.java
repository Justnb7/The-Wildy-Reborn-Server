package com.venenatis.game.content.gamble;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class Gamble {

	private final Player player;

	public Gamble(final Player player) {
		this.player = player;
	}

	private final CopyOnWriteArrayList<Item> offeredItems = new CopyOnWriteArrayList<Item>();

	private Player requestee;
	private boolean requestSent;
	private GambleStage stage = null;
	private GambleType type = null;
	private boolean accepted;

	/**
	 * @return the requestee
	 */
	public Player getRequestee() {
		return requestee;
	}

	/**
	 * @param requestee
	 *            the requestee to set
	 */
	public void setRequestee(Player requestee) {
		this.requestee = requestee;
	}

	/**
	 * @return the requestSent
	 */
	public boolean isRequestSent() {
		return requestSent;
	}

	/**
	 * @param requestSent
	 *            the requestSent to set
	 */
	public void setRequestSent(boolean requestSent) {
		this.requestSent = requestSent;
	}

	/**
	 * @return the stage
	 */
	public GambleStage getStage() {
		return stage;
	}

	/**
	 * @param stage
	 *            the stage to set
	 */
	public void setStage(GambleStage stage) {
		this.stage = stage;
	}

	/**
	 * @return the type
	 */
	public GambleType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(GambleType type) {
		this.type = type;
		if (this.type == GambleType.DICING) {
			player.getActionSender().sendConfig(FLOWER_POKER_CONFIG_ID, 0);
			player.getActionSender().sendConfig(DICING_CONFIG_ID, 1);
			getRequestee().getActionSender().sendConfig(FLOWER_POKER_CONFIG_ID, 0);
			getRequestee().getActionSender().sendConfig(DICING_CONFIG_ID, 1);
		} else if (this.type == GambleType.FLOWER_POKER) {
			player.getActionSender().sendConfig(FLOWER_POKER_CONFIG_ID, 1);
			player.getActionSender().sendConfig(DICING_CONFIG_ID, 0);
			getRequestee().getActionSender().sendConfig(FLOWER_POKER_CONFIG_ID, 1);
			getRequestee().getActionSender().sendConfig(DICING_CONFIG_ID, 0);
		}
	}

	/**
	 * @return the accepted
	 */
	public boolean isAccepted() {
		return accepted;
	}

	/**
	 * @param accepted
	 *            the accepted to set
	 */
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public void request() {
		if (getRequestee() == null || getStage() != null) {
			return;
		}
		if (!Objects.isNull(getRequestee().getGamble().getRequestee()) && !Objects.equals(getRequestee().getGamble().getRequestee(), player)) {
			setRequestee(null);
			player.getActionSender().sendMessage("This player is currently busy.");
			return;
		}

		if (player.distanceTo(getRequestee()) > 1) {
			setRequestee(null);
			player.getActionSender().sendMessage("You can't request to gamble from this distance.");
			return;
		}
		resetGambleSettings();
		setRequestSent(true);
		if (getRequestee().getGamble().isRequestSent()
				&& Objects.equals(getRequestee().getGamble().getRequestee(), player)) {
			openGambleWidget();
			getRequestee().getGamble().openGambleWidget();
		} else {
			player.getActionSender().sendMessage("Sending gamble request...");
			getRequestee().getActionSender().sendMessage(Utility.formatName(player.getUsername()) + ":gamblereq:");
		}
	}

	private void resetGambleSettings() {
		player.getActionSender().sendConfig(FLOWER_POKER_CONFIG_ID, 0);
		player.getActionSender().sendConfig(DICING_CONFIG_ID, 0);
		
		offeredItems.clear();
		for (int index = 0; index < 24; index++) {
			offeredItems.add(new Item(-1, -1));
		}
		setType(null);
		player.getActionSender().sendItemsOnInterface(GAMBLE_ITEM_GROUP_WIDGET_ID, offeredItems);
		player.getActionSender().sendItemsOnInterface(GAMBLE_ITEM_GROUP_WIDGET2_ID, offeredItems);

	}

	private static final int GAMBLE_WIDGET_ID = 56000;
	private static final int GAMBLE_SUB_WIDGET_ID = 3321;
	private static final int GAMBLE_INVENTORY_WIDGET_ID = 3322;
	private static final int GAMBLE_OPPONENT_STRING_ID = 56019;
	private static final int GAMBLE_ITEM_GROUP_WIDGET_ID = 56008;
	private static final int GAMBLE_ITEM_GROUP_WIDGET2_ID = 56009;
	private static final int VALUE_STRING_ID = 56006;
	private static final int VALUE2_STRING_ID = 56007;
	private static final int DICING_CONFIG_ID = 56021;
	private static final int FLOWER_POKER_CONFIG_ID = 56023;

	public void openGambleWidget() {
		setStage(GambleStage.OFFERING_STAGE);
		offeredItems.clear();
		player.getActionSender().sendItemsOnInterface(GAMBLE_ITEM_GROUP_WIDGET_ID, null);
		getRequestee().getActionSender().sendItemsOnInterface(GAMBLE_ITEM_GROUP_WIDGET2_ID, null);
		player.getActionSender().sendString(Utility.formatName(getRequestee().getUsername()), GAMBLE_OPPONENT_STRING_ID);
		player.getActionSender().refreshContainer(GAMBLE_INVENTORY_WIDGET_ID);
		player.getActionSender().sendInterfaceWithInventoryOverlay(GAMBLE_WIDGET_ID, GAMBLE_SUB_WIDGET_ID);
	}

	public void offerItem(final int id, int count, final int slot) {
		if (getStage() != GambleStage.OFFERING_STAGE
				|| getRequestee().getGamble().getStage() != GambleStage.OFFERING_STAGE) {
			player.getActionSender().removeAllInterfaces();
			setRequestee(null);
			setStage(null);
			return;
		}
		if (getType() == null) {
			player.getActionSender().sendMessage("@or2@Please pick a game type before offering items.");
			return;
		}
		if (id != player.getInventory().get(slot).getId()) {
			return;
		}

		if (count > player.getInventory().getAmount(id)) {
			count = player.getInventory().getAmount(id);
		}

		final ItemDefinition itemDefinitions = ItemDefinition.get(id);

		if (Objects.isNull(itemDefinitions)) {
			return;
		}

		if (!itemDefinitions.isTradeable()) {
			player.getActionSender().sendMessage("You can't gamble this item.");
			return;
		}
		player.getInventory().remove(new Item(id, count));

		boolean isItemOffered = false;

		for (Item item : offeredItems) {
			if (id == item.getId() && itemDefinitions.isStackable()) {
				isItemOffered = true;
				item.setAmount(item.getAmount() + count);
				break;
			}
		}
		if (!isItemOffered) {
			if (itemDefinitions.isStackable()) {
				offeredItems.add(new Item(id, count));
			} else {
				int temporaryCount = 0;
				temporaryCount = count;
				while (temporaryCount > 0) {
					offeredItems.add(new Item(id, 1));
					temporaryCount--;
				}
			}
		}
		int totalValue = 0;
		for (Item item : offeredItems) {
			totalValue += item.getDefinition().getValue() * item.getAmount();
		}
		player.getActionSender().sendString(Utility.formatNumbers(totalValue) + " gp", VALUE_STRING_ID);
		getRequestee().getActionSender().sendString(Utility.formatNumbers(totalValue) + " gp", VALUE2_STRING_ID);
		player.getActionSender().refreshContainer(GAMBLE_INVENTORY_WIDGET_ID);
		player.getActionSender().sendItemsOnInterface(GAMBLE_ITEM_GROUP_WIDGET_ID, offeredItems);
		getRequestee().getActionSender().sendItemsOnInterface(GAMBLE_ITEM_GROUP_WIDGET2_ID, offeredItems);
	}

	/**
	 * Gets total item count of a non-stackable item
	 * 
	 * @param id
	 * @return
	 */
	private final int getItemCount(final int id) {
		int count = 0;
		for (Item item : offeredItems) {
			if (id == item.getId()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Removes item from the trading widget
	 * 
	 * @param id
	 * @param count
	 * @param slot
	 */
	public void removeItem(final int id, int count, final int slot) {

		if (getStage() != GambleStage.OFFERING_STAGE
				|| getRequestee().getGamble().getStage() != GambleStage.OFFERING_STAGE || id <= 0
				|| id >= Constants.MAXITEM_AMOUNT || offeredItems.isEmpty()) {
			return;
		}

		final ItemDefinition itemDefinitions = ItemDefinition.get(id);

		if (Objects.isNull(itemDefinitions)) {
			return;
		}

		if (!itemDefinitions.isStackable()) {
			if (count > getItemCount(id) && !itemDefinitions.isStackable()) {
				count = getItemCount(id);
			}
			int temporaryCount = 0;
			while (temporaryCount < count) {
				for (Item item : offeredItems) {
					if (id == item.getId()) {
						player.getInventory().add(item);
						offeredItems.remove(item);
						break;
					}
				}
				temporaryCount++;
			}
		}
		if (itemDefinitions.isStackable()) {
			if (count > offeredItems.get(slot).getAmount()) {
				count = offeredItems.get(slot).getAmount();
			}
			offeredItems.set(slot, new Item(id, offeredItems.get(slot).getAmount() - count));
			player.getInventory().add(new Item(offeredItems.get(slot).getId(), count));
			if (offeredItems.get(slot).getAmount() <= 0) {
				offeredItems.remove(slot);
			}
		}
		int totalValue = 0;
		for (Item item : offeredItems) {
			totalValue -= item.getDefinition().getValue() * count;
		}
		player.getActionSender().sendString(Utility.formatNumbers(totalValue) + " gp", VALUE_STRING_ID);
		getRequestee().getActionSender().sendString(Utility.formatNumbers(totalValue) + " gp", VALUE2_STRING_ID);
		player.getActionSender().refreshContainer(GAMBLE_INVENTORY_WIDGET_ID);
		player.getActionSender().sendItemsOnInterface(GAMBLE_ITEM_GROUP_WIDGET_ID, offeredItems);
		getRequestee().getActionSender().sendItemsOnInterface(GAMBLE_ITEM_GROUP_WIDGET2_ID, offeredItems);
	}

	public void accept() {
		setAccepted(true);
		if (getRequestee().getGamble().isAccepted()) {
			if (getType() == GambleType.DICING) {
				setStage(GambleStage.GAMBLE_STAGE);
				executeDice();
			}
		} else {
			player.getActionSender().sendString("Waiting for other player to accept...", 56005);
			getRequestee().getActionSender().sendString("Other player has accepted...", 56005);
		}
	}

	public void executeDice() {
		player.getActionSender().removeAllInterfaces();
		getRequestee().getActionSender().removeAllInterfaces();
		World.getWorld().schedule(new Task(1) {
			int state = 3;
			double playerRoll = (new Random().nextDouble() * 100);
			double requesteeRoll = (new Random().nextDouble() * 100);

			@Override
			public void execute() {
				player.sendForcedMessage("" + state);
				player.getGamble().getRequestee().sendForcedMessage("" + state);
				if (state == 0) {
					player.sendForcedMessage("I rolled " + (int) playerRoll + "% on the percentile dice!");
					getRequestee().sendForcedMessage("I rolled " + (int) requesteeRoll + "% on the percentile dice!");
					if (playerRoll > requesteeRoll) {
						if (!getRequestee().getGamble().offeredItems.isEmpty()) {
							getRequestee().getGamble().offeredItems.forEach((item) -> {
								player.getInventory().add(item);
							});
							getRequestee().getGamble().offeredItems.clear();
						}
					} else {
						if (!offeredItems.isEmpty()) {
							offeredItems.forEach((item) -> {
								getRequestee().getInventory().add(item);
							});
							offeredItems.clear();
						}
					}
					player.getGamble().decline();
					stop();
					return;
				}
				state--;
			}

		});
	}

	public void decline() {
		if (!offeredItems.isEmpty()) {
			offeredItems.forEach((item) -> {
				player.getInventory().add(item);
			});
		}
		if (!getRequestee().getGamble().offeredItems.isEmpty()) {
			getRequestee().getGamble().offeredItems.forEach((item) -> {
				getRequestee().getInventory().add(item);
			});
		}
		getRequestee().getGamble().resetGambleSettings();
		getRequestee().getGamble().setRequestSent(false);
		getRequestee().getGamble().setStage(null);
		getRequestee().getGamble().setRequestee(null);
		getRequestee().getGamble().setAccepted(false);
		getRequestee().getActionSender().removeAllInterfaces();
		setStage(null);
		setRequestee(null);
		setRequestSent(false);
		setAccepted(false);
		resetGambleSettings();
		player.getActionSender().removeAllInterfaces();
	}

	public enum GambleStage {

		OFFERING_STAGE, GAMBLE_STAGE

	}

	public enum GambleType {

		DICING, FLOWER_POKER

	}

}