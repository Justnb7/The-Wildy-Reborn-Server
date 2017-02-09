package com.model.game.item;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ItemHandler {

	public ItemList[] ItemList;

	public ItemHandler() {
		ItemList = new ItemList[25000];
		for (int i = 0; i < 25000; i++) {
			ItemList[i] = null;
		}
		loadItemList("item.cfg");
	}

	/**
	 * Item List
	 **/
	public void newItemList(int ItemId, String ItemName, String ItemDescription, double ShopValue, double LowAlch, double HighAlch, int Bonuses[]) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 0; i < 11740; i++) {
			if (ItemList[i] == null) {
				slot = i;
				break;
			}
		}

		if (slot == -1)
			return; // no free slot found
		ItemList newItemList = new ItemList(ItemId);
		newItemList.itemName = ItemName;
		newItemList.itemDescription = ItemDescription;
		newItemList.ShopValue = ShopValue;
		newItemList.LowAlch = LowAlch;
		newItemList.HighAlch = HighAlch;
		newItemList.Bonuses = Bonuses;
		ItemList[slot] = newItemList;
	}

	public ItemList getItemList(int i) {
		for (int j = 0; j < ItemList.length; j++) {
			if (ItemList[j] != null) {
				if (ItemList[j].itemId == i) {
					return ItemList[j];
				}
			}
		}
		return null;
	}

	public boolean loadItemList(String FileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[10];
		ItemList = new ItemList[25000];
		for (int i = 0; i < 25000; i++) {
			ItemList[i] = null;
		}
		try (BufferedReader file = new BufferedReader(new FileReader("./Data/cfg/" + FileName))) {
			while ((line = file.readLine()) != null && !line.equals("[ENDOFITEMLIST]")) {
				line = line.trim();
				int spot = line.indexOf("=");
				if (spot > -1) {
					token = line.substring(0, spot);
					token = token.trim();
					token2 = line.substring(spot + 1);
					token2 = token2.trim();
					token2_2 = token2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token3 = token2_2.split("\t");
					if (token.equals("item")) {
						int[] Bonuses = new int[12];
						for (int i = 0; i < 12; i++) {
							if (token3[(6 + i)] != null) {
								Bonuses[i] = Integer.parseInt(token3[(6 + i)]);
							} else {
								break;
							}
						}
						newItemList(Integer.parseInt(token3[0]), token3[1].replaceAll("_", " "), token3[2].replaceAll("_", " "),
								Double.parseDouble(token3[4]), Double.parseDouble(token3[4]), Double.parseDouble(token3[6]), Bonuses);
					}
				}
			}
		} catch (FileNotFoundException fileex) {
			return false;
		} catch (IOException ioexception) {
			return false;
		}
		return true;
	}
}
