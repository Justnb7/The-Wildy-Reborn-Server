package com.venenatis.game.world.object.impl.vines;

public class Vine {
	
	private int id;
	private int face;

	public Vine(int id, int face) {
		this.setId(id);
		this.setFace(face);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFace() {
		return face;
	}

	public void setFace(int face) {
		this.face = face;
	}

}