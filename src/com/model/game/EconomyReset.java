package com.model.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class EconomyReset {

	/**
	**	Config
	**/
	private static File charDir = new File("Data/characters/");
	public static int items[] = {11838, 13887, 13893, 13902, 13896, 13884, 13890, 12381, 12383, 12385, 12387, 12417, 12414, 12415, 12416, 12418};
	public static boolean displayAllNames = false;
	
	
	public static void main(String[] args) {
		System.out.println("Now starting the Economy Cleaner!");
		if(charDir.exists() && charDir.isDirectory()) {
			File[] charFiles = charDir.listFiles();
			for(int i = 0; i < charFiles.length; i++) {
				resetEcoChar(charFiles[i], charFiles[i].toString());
				if (displayAllNames)
					System.out.println(charFiles[i].toString());
			}
			System.out.println("Economy Cleaner complete.");
			System.out.println("Amount of lines removed from all characters: " + counter);
			System.out.println("That is " + badCharacter + " characters out of a total of " + (charFiles.length) + " characters.");
		} else {
			System.out.println("Error - Could not find character directory, check path");
		}
	}
	
	public static int counter = 0, badCharacter = 0;
	public static boolean bad = false;
	@SuppressWarnings("deprecation")
	private static void resetEcoChar(File charFile, String name) {
		try {
			String tempData, tempAdd = ""; int curEquip = 0;
			File tempCharFile = new File(charDir.toString() + "ECOBOOST$TEMP");
			DataInputStream fileStream = new DataInputStream(new FileInputStream(charFile));
			BufferedWriter tempOut = new BufferedWriter(new FileWriter(tempCharFile));

			while((tempData = fileStream.readLine()) != null) {
				boolean goodLine = true;
				for(int i = 0; i < items.length; i++) {
					if((tempData.trim().startsWith("character-item =") || tempData.trim().startsWith("bank-tab =")) && (tempData.trim().matches("(.*)" + items[i] + "(.*)"))) {
						goodLine = false;
						System.out.println("Item " + items[i] + " found on " + name + ", line deleted.");
						counter++;
					} else if(tempData.trim().startsWith("character-equip =") && tempData.trim().matches("(.*)" + items[i] + "(.*)")) {
						goodLine = false;
						tempAdd = "character-equip = " + curEquip + "\t-1\t0";
						System.out.println("Item " + items[i] + " found on " + name + ", line deleted.");
						curEquip++;
						counter++;
						tempOut.write(tempAdd); 
						tempOut.newLine();
					}
				}
				if (goodLine) {
					tempAdd = tempData.trim();
					tempOut.write(tempAdd); 
					tempOut.newLine();
				} else {
					bad = true;
				}
			}
			if (bad) {
				badCharacter++;
				bad = false;
			}
			fileStream.close(); 
			tempOut.close();
			charFile.delete();
			tempCharFile.renameTo(charFile);
		}
			catch(Exception e) { e.printStackTrace(); 
		}
	}
}