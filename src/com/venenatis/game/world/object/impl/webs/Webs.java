package com.venenatis.game.world.object.impl.webs;

public class Webs {
	
	private int id;
	private int face;

	Webs(int id, int face) {
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
