package com.model.game.character.player.skill;

import com.model.game.character.player.Player;

/**
 * @author Patrick van Elderen
 */
public class SkillInterfaceButtons {

	public static void buttonClick(final Player player, int button) {
		switch (button) {
		
		case 33206:
			if (player.getGameMode() == "PKER") {
				player.outStream.writeFrame(27);
				player.attackSkill = true;
				player.strengthSkill = false;
				player.mageSkill = false;
				player.rangeSkill = false;
				player.defenceSkill = false;
				player.prayerSkill = false;
				player.healthSkill = false;
			} else {
				player.getSI().attackComplex(1);
				player.getSI().selected = 0;
			}
			break;
			
		case 33209:
			if (player.getGameMode() == "PKER") {
				player.outStream.writeFrame(27);
				player.strengthSkill = true;
				player.attackSkill = false;
				player.mageSkill = false;
				player.rangeSkill = false;
				player.defenceSkill = false;
				player.prayerSkill = false;
				player.healthSkill = false;
			} else {
				player.getSI().strengthComplex(1);
				player.getSI().selected = 1;
			}
			break;
			
		case 33212:
			if (player.getGameMode() == "PKER") {
				player.outStream.writeFrame(27);
				player.defenceSkill = true;
				player.attackSkill = false;
				player.strengthSkill = false;
				player.mageSkill = false;
				player.rangeSkill = false;
				player.prayerSkill = false;
				player.healthSkill = false;
			} else {
				player.getSI().defenceComplex(1);
				player.getSI().selected = 2;
			}
			break;
			
		case 33215:
			if (player.getGameMode() == "PKER") {
				player.outStream.writeFrame(27);
				player.rangeSkill = true;
				player.attackSkill = false;
				player.strengthSkill = false;
				player.mageSkill = false;
				player.defenceSkill = false;
				player.prayerSkill = false;
				player.healthSkill = false;
			} else {
				player.getSI().rangedComplex(1);
				player.getSI().selected = 3;
			}
			break;

		case 33218:
			if (player.getGameMode() == "PKER") {
				player.outStream.writeFrame(27);
				player.prayerSkill = true;
				player.attackSkill = false;
				player.strengthSkill = false;
				player.mageSkill = false;
				player.rangeSkill = false;
				player.defenceSkill = false;
				player.healthSkill = false;
			} else {
				player.getSI().prayerComplex(1);
				player.getSI().selected = 4;
			}
			break;

		case 33221:
			if (player.getGameMode() == "PKER") {
				player.outStream.writeFrame(27);
				player.mageSkill = true;
				player.attackSkill = false;
				player.strengthSkill = false;
				player.rangeSkill = false;
				player.defenceSkill = false;
				player.prayerSkill = false;
				player.healthSkill = false;
			} else {
				player.getSI().magicComplex(1);
				player.getSI().selected = 5;
			}
			break;

		case 33207:
			if (player.getGameMode() == "PKER") {
				player.outStream.writeFrame(27);
				player.healthSkill = true;
				player.attackSkill = false;
				player.strengthSkill = false;
				player.mageSkill = false;
				player.rangeSkill = false;
				player.defenceSkill = false;
				player.prayerSkill = false;
			} else {
				player.getSI().hitpointsComplex(1);
				player.getSI().selected = 7;
			}
			break;

		case 33224: // runecrafting
			player.getSI().runecraftingComplex(1);
			player.getSI().selected = 6;
			break;

		case 33210: // agility
			player.getSI().agilityComplex(1);
			player.getSI().selected = 8;
			break;

		case 33213: // herblore
			player.getSI().herbloreComplex(1);
			player.getSI().selected = 9;
			break;

		case 33216: // theiving
			player.getSI().thievingComplex(1);
			player.getSI().selected = 10;
			break;

		case 33219: // crafting
			player.getSI().craftingComplex(1);
			player.getSI().selected = 11;
			break;

		case 33222: // fletching
			player.getSI().fletchingComplex(1);
			player.getSI().selected = 12;
			break;

		case 47130:// slayer
			player.getSI().slayerComplex(1);
			player.getSI().selected = 13;
			break;

		case 33208:// mining
			player.getSI().miningComplex(1);
			player.getSI().selected = 14;
			break;

		case 33211: // smithing
			player.getSI().smithingComplex(1);
			player.getSI().selected = 15;
			break;

		case 33214: // fishing
			player.getSI().fishingComplex(1);
			player.getSI().selected = 16;
			break;

		case 33217: // cooking
			player.getSI().cookingComplex(1);
			player.getSI().selected = 17;
			break;

		case 33220: // firemaking
			player.getSI().firemakingComplex(1);
			player.getSI().selected = 18;
			break;

		case 33223: // woodcut
			player.getSI().woodcuttingComplex(1);
			player.getSI().selected = 19;
			break;

		case 54104: // farming
			player.getSI().farmingComplex(1);
			player.getSI().selected = 20;
			break;

		case 34142: // tab 1
			player.getSI().menuCompilation(1);
			break;

		case 34119: // tab 2
			player.getSI().menuCompilation(2);
			break;

		case 34120: // tab 3
			player.getSI().menuCompilation(3);
			break;

		case 34123: // tab 4
			player.getSI().menuCompilation(4);
			break;

		case 34133: // tab 5
			player.getSI().menuCompilation(5);
			break;

		case 34136: // tab 6
			player.getSI().menuCompilation(6);
			break;

		case 34139: // tab 7
			player.getSI().menuCompilation(7);
			break;

		case 34155: // tab 8
			player.getSI().menuCompilation(8);
			break;

		case 34158: // tab 9
			player.getSI().menuCompilation(9);
			break;

		case 34161: // tab 10
			player.getSI().menuCompilation(10);
			break;

		case 59199: // tab 11
			player.getSI().menuCompilation(11);
			break;

		case 59202: // tab 12
			player.getSI().menuCompilation(12);
			break;

		case 59203: // tab 13
			player.getSI().menuCompilation(13);
			break;
		}
	}

}
