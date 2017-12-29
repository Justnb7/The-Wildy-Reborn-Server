package com.venenatis.game.content.minigames.singleplayer.barrows;

import com.venenatis.game.util.Utility;


/**
 * Holds saved details about the Player's Barrows minigame.
 * 
 * @author Lennard
 * @author Stan
 *
 */
public class BarrowsDetails {

	/**
	 * The total amount of chests that have been openend by the player.
	 */
	private int chestsOpened = 0;
	
	/**
	 * The brother NPC identifier the player currently has summoned
	 */
	private int spawnedBrother = -1;

	/**
	 * The total amount of NPC's that have been killed this barrows round.
	 */
	private int cryptKillCount = 0;
	
	/**
	 * The total amount of NPC's that have been killed their combat levels combined
	 */
	private int cryptCombatKill = 0;

	/**
	 * Determines what brother has been killed in order from 0 to 4: 
	 * Dharok, Guthan, Verac, Torag, Ahrim and Karil respectively.
	 */
	private boolean[] brothersKilled = new boolean[6];

	/**
	 * The brother's identifier of the tunnel location. Ranges from 0 to 5:
	 * Dharok, Guthan, Verac, Torag, Ahrim and Karil respectively.
	 */
	private int tunnelLocation = -1;

	public BarrowsDetails(int chestsOpened, int cryptKillCount, boolean[] brothersKilled, int tunnelLocation, int cryptCombatKill) {
		this.chestsOpened = chestsOpened;
		this.cryptKillCount = cryptKillCount;
		this.brothersKilled = brothersKilled;
		this.tunnelLocation = tunnelLocation;
		this.cryptCombatKill = cryptCombatKill;
	}
	
	public BarrowsDetails() {
		this(0, 0, new boolean[6], Utility.random(5), 0);
	}

	/**
	 * Resets all the fields and increases the total amount of chests that have
	 * been opened after successfully finishing a barrows run.
	 */
	public void finishGame() {
		chestsOpened++;
		cryptKillCount = 0;
		brothersKilled = new boolean[6];
		tunnelLocation = Utility.random(5);
		cryptCombatKill = 0;
	}

	public int getChestsOpened() {
		return chestsOpened;
	}

	public void setChestsOpened(int chestsOpened) {
		this.chestsOpened = chestsOpened;
	}

	public int getCryptKillCount() {
		return cryptKillCount;
	}

	public void setCryptKillCount(int cryptKillCount) {
		this.cryptKillCount = cryptKillCount;
	}
	
	public void increaseCryptKillCount() {
		cryptKillCount++;
	}

	public boolean[] getBrothersKilled() {
		return brothersKilled;
	}

	public void setBrothersKilled(boolean[] brothersKilled) {
		this.brothersKilled = brothersKilled;
	}

	public int getTunnelLocation() {
		return tunnelLocation;
	}

	public void setTunnelLocation(int tunnelLocation) {
		this.tunnelLocation = tunnelLocation;
	}

	public int getSpawnedBrother() {
		return spawnedBrother;
	}

	public void setSpawnedBrother(int spawnedBrother) {
		this.spawnedBrother = spawnedBrother;
	}

	public int getCryptCombatKill() {
		return cryptCombatKill;
	}

	public void setCryptCombatKill(int cryptCombatKill) {
		this.cryptCombatKill = cryptCombatKill;
	}

}