package com.venenatis.game.model.entity.player.save;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.venenatis.game.model.entity.player.Player;

public class PlayerSaveUtility {

	/**
	 * Checks if a player file currently exists.
	 * 
	 * @param name
	 * @return
	 */
	public static final boolean exists(String name) {
		try {
			final FileReader reader = new FileReader("./data/characters/details/" + name + ".json");
			reader.close();
			return true;
		} catch (final Exception e) {
		}
		return false;
	}
	
	public static int checkAddress(String host, String mac) {
		int amount = 0;
		try {
			File file = new File("./data/starters/" + host + ".txt");
			if (!file.exists()) {
				return 0;
			}
			BufferedReader in = new BufferedReader(new FileReader(file));
			
			String whatever = in.readLine();
			
			long max = Long.parseLong(whatever);
			
			if (max > Integer.MAX_VALUE) {
				amount = 2;
			} else {
				amount = (int) max;				
			}
			
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			File file = new File("./data/starters/" + mac + ".txt");
			if (!file.exists()) {
				return 0;
			}
			BufferedReader in = new BufferedReader(new FileReader(file));
			
			String whatever = in.readLine();
			
			long max = Long.parseLong(whatever);
			
			if (max > Integer.MAX_VALUE) {
				amount = 2;
			} else {
				amount = (int) max;				
			}
			
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
	}	
	
	public static boolean setStarter(Player player) {
		String host = player.getHostAddress();
		String mac = player.getMacAddress();
		
		int amount = checkAddress(host, mac);
		
		if (amount >= 2) {
			return false;
		}
		
		if (amount == 0) {
			amount = 1;
		} else if (amount == 1) {
			amount = 2;
		}
		
		try {
			File file = new File("./data/starters/" + mac + ".txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
			out.write(String.valueOf(amount));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		try {
			File file = new File("./data/starters/" + host + ".txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
			out.write(String.valueOf(amount));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
