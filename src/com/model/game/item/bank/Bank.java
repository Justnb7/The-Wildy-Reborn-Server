package com.model.game.item.bank;

import java.util.Arrays;
import java.util.Iterator;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.out.SendSoundPacket;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;

public class Bank {

	Player player;
	
	private long lastSearch;

	public Bank(Player player) {
		this.player = player;
	}

	public BankSearch bankSearch;

	public BankSearch getBankSearch() {
		if (bankSearch == null)
			bankSearch = new BankSearch(player);
		return this.bankSearch;
	}

	private BankTab[] bankTabs = { new BankTab(0), new BankTab(1), new BankTab(2), new BankTab(3), new BankTab(4), new BankTab(5), new BankTab(6), new BankTab(7), new BankTab(8) };

	public BankTab[] getBankTab() {
		return bankTabs;
	}

	public BankTab getBankTab(int tabId) {
		for (BankTab tab : bankTabs)
			if (tab.getTabId() == tabId)
				return tab;
		return bankTabs[0];
	}

	public void setBankTab(int tabId, BankTab tab) {
		this.bankTabs[tabId] = tab;
	}

	private BankTab currentTab = getBankTab()[0];

	public BankTab getCurrentBankTab() {
		if (currentTab == null)
			currentTab = getBankTab()[0];
		return this.currentTab;
	}

	public BankTab setCurrentBankTab(BankTab bankTab) {
		return this.currentTab = bankTab;
	}

	public long getLastSearch() {
		return lastSearch;
	}

	public void setLastSearch(long lastSearch) {
		this.lastSearch = lastSearch;
	}

	public void deleteAll() {
		for (BankTab tab : bankTabs) {
			if (tab == null) {
				continue;
			}
			if (tab.size() > 0) {
				tab.bankItems.clear();
			}
		}
	}
	
	public void resetBank() {
		int tabId = player.getBank().getCurrentBankTab().getTabId();
		for (int i = 0; i < player.getBank().getBankTab().length; i++) {
			if (i == 0)
				continue;
			if (i != player.getBank().getBankTab().length - 1
					&& player.getBank().getBankTab()[i].size() == 0
					&& player.getBank().getBankTab()[i + 1].size() > 0) {
				for (BankItem item : player.getBank().getBankTab()[i + 1]
						.getItems()) {
					player.getBank().getBankTab()[i].add(item);
				}
				player.getBank().getBankTab()[i + 1].getItems().clear();
			}
		}
		player.getActionSender().sendConfig(600, 0);
		player.getActionSender().sendUpdateItem(58040, -1, 0, 0);
		int newSlot = -1;
		for (int i = 0; i < player.getBank().getBankTab().length; i++) {
			BankTab tab = player.getBank().getBankTab()[i];
			if (i == tabId) {
				player.getActionSender().sendConfig(600 + i, 1);
			} else {
				player.getActionSender().sendConfig(600 + i, 0);
			}
			if (tab.getTabId() != 0 && tab.size() > 0 && tab.getItem(0) != null) {
				player.getActionSender().sendInterfaceConfig(0, 58050 + i);
				player.getActionSender()
						.sendUpdateItem(
								58040 + i,
								player.getBank().getBankTab()[i].getItem(0)
										.getId() - 1,
								0,
								player.getBank().getBankTab()[i].getItem(0)
										.getAmount());
			} else if (i != 0) {
				if (newSlot == -1) {
					newSlot = i;
					player.getActionSender()
							.sendUpdateItem(58040 + i, -1, 0, 0);
					player.getActionSender().sendInterfaceConfig(0, 58050 + i);
					continue;
				}
				player.getActionSender().sendUpdateItem(58040 + i, -1, 0, 0);
				player.getActionSender().sendInterfaceConfig(1, 58050 + i);
			}
		}
		player.getOutStream().putFrameVarShort(53);
		int offset = player.getOutStream().offset;
		player.getOutStream().writeShort(5382); // bank
		player.getOutStream().writeShort(player.BANK_SIZE);
		BankTab tab = player.getBank().getCurrentBankTab();
		for (int i = 0; i < player.BANK_SIZE; i++) {
			if (i > tab.size() - 1) {
				player.getOutStream().writeByte(0);
				player.getOutStream().writeWordBigEndianA(0);
				continue;
			} else {
				BankItem item = tab.getItem(i);
				if (item == null)
					item = new BankItem(-1, 0);
				if (item.getAmount() > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(item.getAmount());
				} else {
					player.getOutStream().writeByte(item.getAmount());
				}
				if (item.getAmount() < 1)
					item.setAmount(0);
				if (item.getId() > 25000 || item.getId() < 0)
					item.setId(-1);
				player.getOutStream().writeWordBigEndianA(item.getId());
			}
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
		player.getActionSender().sendString("" + player.getBank().getCurrentBankTab().size(), 58061);
		player.getActionSender().sendString(Integer.toString(tabId), 5292);
	}
	
	public void resetTempItems() {
		int itemCount = 0;
		for (int i = 0; i < player.playerInventory.length; i++) {
			if (player.playerInventory[i] > -1) {
				itemCount = i;
			}
		}
		player.getOutStream().putFrameVarShort(53);
		int offset = player.getOutStream().offset;
		player.getOutStream().writeShort(5064);
		player.getOutStream().writeShort(itemCount + 1);
		for (int i = 0; i < itemCount + 1; i++) {
			if (player.itemAmount[i] > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(player.itemAmount[i]);
			} else {
				player.getOutStream().writeByte(player.itemAmount[i]);
			}
			if (player.playerInventory[i] > 25000 || player.playerInventory[i] < 0) {
				player.playerInventory[i] = 25000;
			}
			player.getOutStream().writeWordBigEndianA(player.playerInventory[i]);
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
	}
	
	public boolean addToBank(int itemID, int amount, boolean updateView) {
		if (!player.isBanking())
			return false;
		if (!player.getItems().playerHasItem(itemID))
			return false;
		if (player.getBank().getBankSearch().isSearching()) {
			player.getBank().getBankSearch().reset();
			return false;
		}

		BankTab tab = player.getBank().getCurrentBankTab();
		BankItem bankItem = new BankItem(itemID + 1, amount);
		for (BankTab t : player.getBank().getBankTab()) {
			if (t == null || t.size() == 0)
				continue;
			for (BankItem i : t.getItems()) {
				if (i.getId() == bankItem.getId() && !player.getItems().isNotable(itemID)) {
					if (t.getTabId() != tab.getTabId()) {
						tab = t;
						break;
					}
				} else {
					if (player.getItems().isNotable(itemID) && i.getId() == bankItem.getId() - 1) {
						bankItem = new BankItem(itemID, amount);
						if (t.getTabId() != tab.getTabId()) {
							tab = t;
							break;
						}
					}
				}
			}
		}
		if (player.getItems().isNotable(itemID)) {
			bankItem = new BankItem(itemID, amount);
		}
		if (bankItem.getAmount() > player.getItems().getItemAmount(itemID))
			bankItem.setAmount(player.getItems().getItemAmount(itemID));
		if (tab.getItemAmount(bankItem) == Integer.MAX_VALUE) {
			player.getActionSender().sendMessage("Your bank is already holding the maximum amount of " + player.getItems().getItemName(itemID).toLowerCase() + " possible.");
			return false;
		}
		if (tab.freeSlots(player) == 0 && !tab.contains(bankItem)) {
			player.getActionSender().sendMessage("Your current bank tab is full.");
			return false;
		} else {
			long totalAmount = ((long) tab.getItemAmount(bankItem) + (long) bankItem.getAmount());
			if (totalAmount >= Integer.MAX_VALUE) {
				int difference = Integer.MAX_VALUE - tab.getItemAmount(bankItem);
				bankItem.setAmount(difference);
				player.getItems().deleteItem(itemID, difference);
			} else {
				player.getItems().deleteItem(itemID, bankItem.getAmount());
			}
			tab.add(bankItem);
			if (updateView) {
				resetTempItems();
				resetBank();
			}
			return true;
		}
	}

	/**
	 * The x and y represents the possible x and y location of the dropped item
	 * if in fact it cannot be added to the bank.
	 * 
	 * @param item
	 * @param x
	 * @param y
	 */
	public void sendItemToAnyTabOrDrop(BankItem item, int x, int y) {
		item = new BankItem(item.getId() + 1, item.getAmount());
		if (bankContains(item.getId() - 2)) {
			if (isBankSpaceAvailable(item)) {
				sendItemToAnyTab(item.getId() - 1, item.getAmount());
			} else {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getX(), player.getY(), player.getZ(), player));
			}
		} else {
			sendItemToAnyTab(item.getId() - 1, item.getAmount());
		}
	}
	
	/**
	 * Sends an item to the bank in any tab possible.
	 *
	 * @param itemId
	 *            the item id
	 * @param amount
	 *            the item amount
	 */
	public void sendItemToAnyTab(int itemId, int amount) {
		if (player.getArea().inWild()) {
			player.getActionSender().sendMessage("You can't do that in the wilderness.");
			return;
		}
		BankItem item = new BankItem(itemId, amount);
		for (BankTab tab : player.getBank().getBankTab()) {
			if (tab.freeSlots(player) > 0 || tab.contains(item)) {
				player.getBank().setCurrentBankTab(tab);
				addToBank(itemId, amount, false);
				return;
			}
		}
		addToBank(itemId, amount, false);
	}
	
	public boolean isBankSpaceAvailable(BankItem item) {
		for (BankTab tab : player.getBank().getBankTab()) {
			if (tab.contains(item)) {
				return tab.spaceAvailable(item);
			}
		}
		return false;
	}
	
	public boolean bankContains(int itemId) {

		for (BankTab tab : player.getBank().getBankTab())
			if (tab.contains(new BankItem(itemId + 1)))
				return true;
		return false;
	}

	public void removeFromBank(int itemId, int itemAmount, boolean updateView) {
		System.out.println("enter");
		BankTab tab = player.getBank().getCurrentBankTab();
		BankItem bankItem = new BankItem(itemId + 1, itemAmount);
		boolean noted = false;
		if (!player.isBanking()) {
			return;
		}
		if (itemAmount <= 0)
			return;

		if (System.currentTimeMillis() - player.lastBankDeposit < 250)
			return;
		if (!player.isBusy()) {
			System.out.println("block: " + player.isBanking());
			player.getActionSender().sendRemoveInterfacePacket();
			return;
		}
		if (!tab.contains(bankItem))
			return;
		if (player.takeAsNote) {
			if (player.getItems().getItemName(itemId).trim().equalsIgnoreCase(player.getItems().getItemName(itemId + 1).trim()) && player.getItems().isNotable(itemId + 1)) {
				noted = true;
			} else
				player.getActionSender().sendMessage("This item cannot be taken out as noted.");
		}
		if (player.getItems().freeSlots() == 0 && !player.getItems().playerHasItem(itemId)) {
			player.getActionSender().sendMessage("There is not enough space in your inventory.");
			return;
		}
		if (player.getItems().getItemAmount(itemId) == Integer.MAX_VALUE) {
			player.getActionSender().sendMessage("Your inventory is already holding the maximum amount of " + player.getItems().getItemName(itemId).toLowerCase() + " possible.");
			return;
		}
		if (player.getItems().isStackable(bankItem.getId() - 1) || noted) {
			long totalAmount = (long) player.getItems().getItemAmount(itemId) + (long) itemAmount;
			if (totalAmount > Integer.MAX_VALUE)
				bankItem.setAmount(tab.getItemAmount(bankItem) - player.getItems().getItemAmount(itemId));
		}
		if (tab.getItemAmount(bankItem) < itemAmount) {
			bankItem.setAmount(tab.getItemAmount(bankItem));
		}
		if (!player.getItems().isStackable(bankItem.getId() - 1) && !noted) {
			if (player.getItems().freeSlots() < bankItem.getAmount())
				bankItem.setAmount(player.getItems().freeSlots());
		}
		if (bankItem.getAmount() < 0)
			bankItem.setAmount(0);
		if (!noted)
			player.getItems().addItem(bankItem.getId() - 1, bankItem.getAmount());
		else
			player.getItems().addItem(bankItem.getId(), bankItem.getAmount());
		tab.remove(bankItem);
		if (tab.size() == 0) {
			player.getBank().setCurrentBankTab(player.getBank().getBankTab(0));
		}
		if (updateView) {
			resetBank();
		}
		player.getItems().resetItems(5064);
	}

	public boolean addEquipmentToBank(int itemID, int slot, int amount, boolean updateView) {
		if (player.getArea().inWild()) {
			player.getActionSender().sendMessage("You can't do that in the wilderness.");
			return false;
		}
		if (!player.isBanking())
			return false;
		if (player.playerEquipment[slot] != itemID || player.playerEquipmentN[slot] <= 0)
			return false;
		BankTab tab = player.getBank().getCurrentBankTab();
		BankItem bankItem = new BankItem(itemID + 1, amount);
		Iterator<BankTab> iterator = Arrays.asList(player.getBank().getBankTab()).iterator();
		while (iterator.hasNext()) {
			BankTab t = iterator.next();
			if (t != null && t.size() > 0) {
				Iterator<BankItem> iterator2 = t.getItems().iterator();
				while (iterator2.hasNext()) {
					BankItem i = iterator2.next();
					if (i.getId() == bankItem.getId() && !player.getItems().isNotable(itemID)) {
						if (t.getTabId() != tab.getTabId()) {
							tab = t;
							break;
						}
					} else {
						if (player.getItems().isNotable(itemID) && i.getId() == bankItem.getId() - 1) {
							bankItem = new BankItem(itemID, amount);
							if (t.getTabId() != tab.getTabId()) {
								tab = t;
								break;
							}
						}
					}
				}
			}
		}
		if (player.getItems().isNotable(itemID))
			bankItem = new BankItem(itemID, amount);
		if (bankItem.getAmount() > player.playerEquipmentN[slot])
			bankItem.setAmount(player.playerEquipmentN[slot]);
		if (tab.getItemAmount(bankItem) == Integer.MAX_VALUE) {
			player.getActionSender().sendMessage("Your bank is already holding the maximum amount of " + player.getItems().getItemName(itemID).toLowerCase() + " possible.");
			return false;
		}
		if (tab.freeSlots(player) == 0 && !tab.contains(bankItem)) {
			player.getActionSender().sendMessage("Your current bank tab is full.");
			return false;
		}
		long totalAmount = ((long) tab.getItemAmount(bankItem) + (long) bankItem.getAmount());
		if (totalAmount >= Integer.MAX_VALUE) {
			player.getActionSender().sendMessage("Your bank is already holding the maximum amount of this item.");
			return false;
		} else
			player.playerEquipmentN[slot] -= bankItem.getAmount();
		if (player.playerEquipmentN[slot] <= 0) {
			player.playerEquipmentN[slot] = -1;
			player.playerEquipment[slot] = -1;
		}
		player.getItems().wearItem(-1, 0, slot);
		tab.add(bankItem);
		if (updateView) {
			resetTempItems();
			resetBank();
			player.getItems().updateSlot(slot);
		}
		return true;
	}
	
	public void swapBankItem(int from, int to) {
		BankItem item = player.getBank().getCurrentBankTab().getItem(from);
		player.getBank().getCurrentBankTab().setItem(from, player.getBank().getCurrentBankTab().getItem(to));
		player.getBank().getCurrentBankTab().setItem(to, item);
	}
	
	public void addOrSendToBank(int item, int amount) {
		if (player.getItems().freeSlots() > 0) {
			player.getItems().addItem(item, amount);
		} else {
			addToBank(item, amount, true);
			player.getActionSender().sendMessage("Inventory full, the item was sent to your bank.");
		}
	}
	
	public void open() {
        player.stopSkillTask();
    	
		if (!player.getAccount().getType().canBank()) {
			player.getActionSender().sendMessage("You're restricted to bank because of your account type.");
			return;
		}
		
        if (player.getArea().inWild() && !(player.getRights().isBetween(2, 3))) {
			player.getActionSender().sendMessage("You can't bank in the wilderness!");
			return;
		}
        
        if (Trading.isTrading(player)) {
            Trading.decline(player);
        }
		
        if (player.takeAsNote)
        	player.getActionSender().sendConfig(115, 1);
        else
        	player.getActionSender().sendConfig(115, 0);
    	
    	boolean openFirstTab = !player.isBanking();
    	
    	if(openFirstTab) {
    		//cheap hax for sending the main tab
        	BankTab tab = player.getBank().getBankTab(0);
    		player.getBank().setCurrentBankTab(tab);
    	}
		
        if (player.getBank().getBankSearch().isSearching()) {
            player.getBank().getBankSearch().reset();
        }
        
        player.write(new SendSoundPacket(1457, 0, 0));
        player.getActionSender().sendString("Search", 58113);
        
        if (player.getOutStream() != null && player != null) {
        	player.setBanking(true);
            player.getItems().resetItems(5064);
            player.getBank().resetBank();
            player.getBank().resetTempItems();
            player.getOutStream().writeFrame(248);
            player.getOutStream().writeWordA(5292);
            player.getOutStream().writeShort(5063);
            player.getActionSender().sendString(player.getName() + "'s Bank", 58064);
        }
    }

}
