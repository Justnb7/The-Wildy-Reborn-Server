package com.venenatis.game.model.entity.player;

public class Appearance {

	private boolean invisible = false;
	private boolean asNpc = false;
	private int npcId = -1;
	public int gender = 0;
	public int[] look = new int[7];
	public int[] colour = new int[5];

	public Appearance() {
		look[1] = 10;
		look[2] = 18;
		look[3] = 26;
		look[4] = 33;
		look[5] = 36;
		look[6] = 42;
		for (int i = 0; i < 5; i++) {
			colour[i] = 0;
		}
	}

	public void toDefault() {
		switch (gender) {
		case 0:
			look[0] = 0;
			look[1] = 10;
			look[2] = 18;
			look[3] = 26;
			look[4] = 33;
			look[5] = 36;
			look[6] = 42;
			break;
		case 1:
			look[0] = 45; // Hair
			look[1] = 1000; // Beard
			look[2] = 57; // Torso
			look[3] = 64; // Arms
			look[4] = 68; // Bracelets
			look[5] = 77; // Legs
			look[6] = 80; // Shoes
			break;
		}
		for (int i = 0; i < 5; i++) {
			colour[i] = 0;
		}
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public void setLook(int index, int look) {
		this.look[index] = look;
	}

	public void setColour(int index, int colour) {
		this.colour[index] = colour;
	}

	public boolean isNpc() {
		return asNpc;
	}

	public int getNpcId() {
		return npcId;
	}

	public int getGender() {
		return gender;
	}

	public void setNpcId(int i) {
		npcId = i;
		asNpc = i != -1;
	}

	public int getLook(int id) {
		return look[id];
	}

	public int getColour(int id) {
		return colour[id];
	}

	public int[] getColoursArray() {
		return colour.clone();
	}

	public int[] getLookArray() {
		return look.clone();
	}

	public int[] getLook() {
		return look;
	}

	public String[] getAllLook() {
		return new String[] { "" + look[0] + "," + look[1] + "," + look[2] + "," + look[3] + "," + look[4] + ","
				+ look[5] + "," + look[6] + "" };
	}

	public String[] getAllColors() {
		return new String[] {
				"" + colour[0] + "," + colour[1] + "," + colour[2] + "," + colour[3] + "," + colour[4] + "" };
	}

	public int[] getColors() {
		return colour;
	}

	public void setColoursArray(int[] colours) {
		this.colour = colours;
	}

	public void setLookArray(int[] look) {
		this.look = look;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public String getLooksToString() {
		return look[0] + "," + look[1] + "," + look[2] + "," + look[3] + "," + look[4] + "," + look[5] + "," + look[6];
	}

	public String getColorsToString() {
		return colour[0] + "," + colour[1] + "," + colour[2] + "," + colour[3] + "," + colour[4];
	}

}