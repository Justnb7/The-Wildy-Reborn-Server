package com.model.game.character.combat.range;

import com.model.game.character.Entity;
import com.model.game.character.combat.Projectile;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.Equipment;



public class RangeData {

	public static void fireProjectileAtTarget(Player player) {
		Entity target = player.getCombat().target;

		player.playProjectile(Projectile.create(player.getCentreLocation(), target.getCentreLocation(), player.getCombat().getRangeProjectileGFX(), player.getCombat().getProjectileSpeed(), 50, getProjectileShowDelay(player), 43, 31, target.getProjectileLockonIndex(), 16, 64));

		if (player.getCombat().usingDbow())
			player.playProjectile(Projectile.create(player.getCentreLocation(), target.getCentreLocation(), player.getCombat().getRangeProjectileGFX(), 100, 50, getProjectileShowDelay(player), 53, 31, target.getProjectileLockonIndex(), 16, 64));
		
	}

	public static void msbSpecProjectile(Player player) {
		Entity target = player.getCombat().target;
		
		player.playProjectile(Projectile.create(player.getCentreLocation(), target.getCentreLocation(), player.getCombat().getRangeProjectileGFX(), player.getCombat().getProjectileSpeed(), 50, getProjectileShowDelay(player), 43, 31, target.getProjectileLockonIndex(), 10, 64));
	}

	public static int getRangeStr(int i) {
		int str = 0;
		int[][] data = {

				{ 9140, 46 }, { 9145, 36 }, { 9141, 64 }, { 9142, 82 }, { 9143, 100 }, { 9144, 115 }, { 9236, 14 },
				{ 9237, 30 }, { 9238, 48 }, { 9239, 66 }, { 9240, 83 }, { 9241, 85 }, { 9242, 103 }, { 9243, 105 },
				{ 9244, 117 }, { 11875, 100 }, { 9245, 120 },

				{ 877, 10 }, { 882, 7 }, { 884, 10 }, { 886, 16 }, { 888, 22 },
				{ 890, 31 }, { 892, 49 }, { 4740, 44 }, { 11212, 60 }, { 806, 1 }, { 807, 3 }, { 808, 4 }, { 809, 7 },
				{ 810, 10 }, { 811, 14 }, { 11230, 18 }, { 864, 3 }, { 863, 4 }, { 865, 7 }, { 866, 10 }, { 867, 14 },
				{ 868, 24 }, { 825, 25 }, { 826, 42 }, { 827, 64 }, { 828, 85 }, { 829, 107 }, { 830, 124 },
				{ 19484, 150 }, { 800, 5 }, { 801, 7 }, { 802, 11 }, { 803, 16 }, { 804, 23 }, { 805, 36 }, { 9976, 0 },
				{ 9977, 15 }, { 4212, 80 }, { 4214, 70 }, { 4215, 70 }, { 4216, 70 }, { 4217, 70 }, { 4218, 70 },
				{ 4219, 70 }, { 4220, 70 }, { 4221, 70 }, { 4222, 70 }, { 4223, 70 }, { 6522, 49 }, { 10034, 15 } };
		for (int l = 0; l < data.length; l++) {
			if (i == data[l][0]) {
				str = data[l][1];
			}
		}
		return str;
	}

	public static int getRangeStartGFX(Player player) {
		int ammo = player.getEquipment().getId(Equipment.ARROWS_SLOT);
		switch (ammo) {
		case 4212:
				return -1;
		}
		int str = -1;
		int[][] data = {
			//	KNIFES
			{863, 220}, {864, 219}, {865, 221}, {866, 223},
			{867, 224}, {868, 225}, {869, 222},

			//	DARTS
			{806, 1234}, {807, 1235}, {808, 1236}, 
			{809, 1238}, {810, 1239}, {811, 1240},
			{11230, 1242},

			//	JAVELIN
			{825, 206}, {826, 207}, {827, 208}, {828, 209},
			{829, 210}, {830, 211}, /*{19484, 1301},*/

			//	AXES
			{800, 42}, {801, 43}, {802, 44}, {803, 45},
			{804, 46}, {805, 48},

			//	ARROWS
			{882, 19}, {884, 18}, {886, 20}, {888, 21},
			{890, 22}, {892, 24},

			//	CRYSTAL_BOW
			{12926, 1242},
			{4212, 250}, {4214, 250}, {4215, 250}, {4216, 250},
			{4217, 250}, {4218, 250}, {4219, 250}, {4220, 250},
			{4221, 250}, {4222, 250}, {4223, 250},
		};
		for(int l = 0; l < data.length; l++) {
			if(ammo == data[l][0]) {
				str = data[l][1];
			}
		}
		if(player.getEquipment().getId(Equipment.WEAPON_SLOT) == 11235 || player.getEquipment().getId(Equipment.WEAPON_SLOT) == 12765 || player.getEquipment().getId(Equipment.WEAPON_SLOT) == 12766 || player.getEquipment().getId(Equipment.WEAPON_SLOT) == 12767 || player.getEquipment().getId(Equipment.WEAPON_SLOT) == 12768) {
			int[][] moreD = {
				{882, 1104}, {884, 1105}, {886, 1106}, {888, 1107},
				{890, 1108}, {892, 1109}, {11212, 1111},
			};
			for(int l = 0; l < moreD.length; l++) {
				if(player.getEquipment().getId(Equipment.ARROWS_SLOT) == moreD[l][0]) {
					str = moreD[l][1];
				}
			}
		}
		return str;
	}

	public static int getRangeProjectileGFX(Player player) {
		int ammo = player.getEquipment().getId(Equipment.ARROWS_SLOT);
		int wep = player.getEquipment().getId(Equipment.WEAPON_SLOT);
		boolean spec = player.isUsingSpecial();
		if (player.getEquipment().getId(Equipment.WEAPON_SLOT) == 12926) {
			return 1123;
		}
		
		if (spec && wep == 11235) {
			return player.getEquipment().getId(Equipment.ARROWS_SLOT) == 11212 ? 1099 : 1101;
		}
		
		if (wep == 12424)
			return 806;
		if (wep == 4212)
			return 249;
			
		if (spec) {
			if (wep == 861) {
				return 249;
			}
		}
		boolean castingMagic = (player.getCombatType() == CombatStyle.MAGIC || player.usingMagic || player.getSpellId() > 0);
		if(castingMagic) {
			return -1;
		}
		if (player.getEquipment().isCrossbow(player))
			return 27;
		
		int str = -1;
		int[][] data = {
			//	KNIFES
			{863, 213}, {864, 212}, {865, 214}, {866, 216},
			{867, 217}, {868, 218}, {869, 215},

			//	DARTS
			{806, 226}, {807, 227}, {808, 228}, {809, 229},
			{810, 230}, {811, 231},

			//	JAVELINS
			{825, 200}, {826, 201}, {827, 202}, {828, 203},
			{829, 204}, {830, 205}, 

			//	AXES
			{800, 36}, {801, 35}, {802, 37}, {803, 38},
			{804, 39}, {805, 40},

			//	ARROWS
			{882, 10}, {884, 9}, {886, 11}, {888, 12},
			{890, 13}, {892, 15}, {11212, 1120},

			//	CHINCHOMPA
			{10033, 908}, {10034, 909},

			//	OTHERS
			{12926, 213}, {19481, 1301},
			{6522, 442}, {4740, 27},
			{4212, 249}, {4214, 249}, {4215, 249}, {4216, 249},
			{4217, 249}, {4218, 249}, {4219, 249}, {4220, 249},
			{4221, 249}, {4222, 249}, {4223, 249},
		};
		for(int l = 0; l < data.length; l++) {
			if(ammo == data[l][0]) {
				str = data[l][1];
			}
		}
		return str;
	}

	public static int[] getRangeEndGFX(Player player) {
		int[] info = new int[] {-1, -1};
		int[][] data = {
			{10033, 157, 100}, {10034, 157, 100},
		};
		for(int l = 0; l < data.length; l++) {
			if(player.getEquipment().getId(Equipment.WEAPON_SLOT) == data[l][0]) {
				info[0] = data[l][1];
				info[1] = data[l][2];
			}
		}
		return info;
	}

	public static int correctBowAndArrows(Player player) {
		switch(player.getEquipment().getId(Equipment.WEAPON_SLOT)) {
			
			case 839:
			case 841:
			return 884;
			
			case 843:
			case 845:
			return 884;
			
			case 847:
			case 849:
			return 886;
			
			case 851:
			case 853:
			return 888;        
			
			case 855:
			case 857:
			return 890;
			
			case 859:
			case 861:
			case 12424:
			case 12788:
			case 4212:
			case 4213:
			case 4214:
			case 4215:
			case 4216:
			case 4217:
			case 4218:
			case 4219:
			case 4220:
			case 4221:
			case 4222:
			case 4223:
			case 4224:
			
			return 892;
			case 4734:
			case 4935:
			case 4936:
			case 4937:
			return 4740;
			
			case 11235:
			case 12765:
			case 12766:
			case 12767:
			case 12768:
			case 20997:
			return 11212;
		}
		return -1;
	}

	public static int getProjectileSpeed(Player player) {
		if (player.isUsingSpecial() && player.getEquipment().getId(Equipment.WEAPON_SLOT) == 11235)
			return 100;
		switch(player.getEquipment().getId(Equipment.WEAPON_SLOT)) {
			case 10033:
			case 10034:
				return 60;
		}
		return 70;
	}

	public static int getProjectileShowDelay(Player player) {
		int[] data = {
			806, 806, 808, 809, 810, 811,
			10033, 10034, 11230,
		};
		int str = 53;
		for(int i = 0; i < data.length; i++) {
			if(player.getEquipment().getId(Equipment.WEAPON_SLOT) == data[i]) {
				str = 32;
			}
		}
		return str;
	}
}