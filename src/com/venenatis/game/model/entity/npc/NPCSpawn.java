package com.venenatis.game.model.entity.npc;

/**
 * 
 * @author Mack
 *
 */
public class NPCSpawn {
	
	/**
	 * The id of the npc that we are spawning.
	 */
	private int id;
	
	/**
	 * The x-coordinate value.
	 */
	private int x;

	/**
	 * The y-coordinate value.
	 */
	private int y;

	/**
	 * The z-coordinate value.
	 */
	private int z;

	/**
	 * The direction the npc is facing.
	 */
	private int face;

	/**
	 * This is the general radius the npc will be able to roam with. i.e if we
	 * sent the value to 4 then the npc can roam x +- 4, y+-4
	 */
	private int radius = 0;
	
	/**
	 * 
	 * @param id
	 * @param spawn
	 * @param face
	 * @param radius
	 */
	public NPCSpawn(int id, int x, int y, int z, int face, int radius) {
		this.id = id;
		this.face = face;
		this.radius = radius;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNpcId() {
		return id;
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setNpcId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the x coordinate value.
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x-coordinate value.
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Gets the y-coordinate value.
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the y-coordinate value.
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Gets the height, or "z"-coordinate on the "z" plane.
	 * @return
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Sets the height level of the registered npc.
	 * @param z
	 */
	public void setZ(int z) {
		this.z = z;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getFacing() {
		return face;
	}
	
	/**
	 * 
	 * @param face
	 */
	public void setFacing(int face) {
		this.face = face;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getWalkRadius() {
		return radius;
	}

	/**
	 * 
	 * @param radius
	 */
	public void setWalkRadius(int radius) {
		this.radius = radius;
	}
}