package com.venenatis.game.model.combat.range;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;



public class RangeData {

	public static void fireProjectileAtTarget(Player player) {
		Entity target = player.getCombatState().getTarget();
		int gfx = player.getCombatState().getRangeProjectileGFX();
		if (target == null || gfx == -1) {
			System.err.println("bad projectile");
			return;
		}

		player.playProjectile(Projectile.create(player.getCentreLocation(), target.getCentreLocation(), gfx,
				player.getCombatState().getProjectileSpeed(), 50, getProjectileShowDelay(player), 43, 31, target.getProjectileLockonIndex(), 16, 64));

		if (player.getCombatState().usingDbow())
			player.playProjectile(Projectile.create(player.getCentreLocation(), target.getCentreLocation(), gfx,
					100, 50, getProjectileShowDelay(player), 53, 31, target.getProjectileLockonIndex(), 16, 64));
		
	}

	public static void msbSpecProjectile(Player player) {
		Entity target = player.getCombatState().getTarget();
		
		player.playProjectile(Projectile.create(player.getCentreLocation(), target.getCentreLocation(), player.getCombatState().getRangeProjectileGFX(),
				player.getCombatState().getProjectileSpeed(), 50, getProjectileShowDelay(player), 43, 31, target.getProjectileLockonIndex(), 10, 64));
	}

	public static int getRangeStartGFX(Player player) {
		int ammo = player.getEquipment().get(EquipmentConstants.AMMO_SLOT) == null ? -1 :  player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId();
		int wepId = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
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
		if(wepId == 11235 || wepId == 12765 || wepId == 12766 || wepId == 12767 || wepId == 12768) {
			int[][] moreD = {
				{882, 1104}, {884, 1105}, {886, 1106}, {888, 1107},
				{890, 1108}, {892, 1109}, {11212, 1111},
			};
			for(int l = 0; l < moreD.length; l++) {
				if(ammo == moreD[l][0]) {
					str = moreD[l][1];
				}
			}
		}
		return str;
	}

	public static int getRangeProjectileGFX(Player player) {
		int ammo = player.getEquipment().get(EquipmentConstants.AMMO_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId();
		int wep = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
		boolean spec = player.isUsingSpecial();
		if (wep == 12926) {
			return 1123;
		}
		
		if (spec && wep == 11235) {
			return ammo == 11212 ? 1099 : 1101;
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
		boolean castingMagic = (player.getCombatType() == CombatStyle.MAGIC || player.getSpellId() > 0);
		if(castingMagic) {
			return -1;
		}
		if (EquipmentConstants.isCrossbow(player))
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

	public static int[] getRangeEndGFX(Player player, int wepId) {
		int[] info = new int[] {-1, -1};
		int[][] data = {
			{10033, 157, 100}, {10034, 157, 100},
		};
		for(int l = 0; l < data.length; l++) {
			if(wepId == data[l][0]) {
				info[0] = data[l][1];
				info[1] = data[l][2];
			}
		}
		return info;
	}

	public static int getProjectileSpeed(Player player) {
		int wepId = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
		if (wepId == 11235)
			return 100;
		switch(wepId) {
			case 10033:
			case 10034:
				return 60;
		}
		return 70;
	}

	public static int getProjectileShowDelay(Player player) {
		int wepId = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
		int[] data = {
			806, 806, 808, 809, 810, 811,
			10033, 10034, 11230,
		};
		int str = 53;
		for(int i = 0; i < data.length; i++) {
			if(wepId == data[i]) {
				str = 32;
			}
		}
		return str;
	}

	/**
	 * TODO Add support for deleting (1) bolts or (2) darts [either ammo or hand slot] depending on range type
	 * plus chance for that ammo dropping to the floor
	 */
	public static void loseAmmo(Player player, Entity target, int wepId, int ammoId) {
		if (wepId == 11235 || wepId == 12765 || wepId == 12766 || wepId == 12767 || wepId == 12768) {
			//TODO add arrow removement
		}

		//Arrows check
		boolean dropArrows = true;
		if (wepId == 12926 || wepId == 4222) {
			dropArrows = false;
		}

		if (dropArrows) {
			//TODO add arrow removement
		}
	}
}