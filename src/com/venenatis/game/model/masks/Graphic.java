package com.venenatis.game.model.masks;

/**
 * Represents a single graphic request.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Graphic {

	/**
	 * Creates an graphic with no delay.
	 * 
	 * @param id
	 *            The id.
	 * @return The new graphic object.
	 */
	public static Graphic create(int id) {
		return create(id, 0);
	}

	/**
	 * Creates a graphic.
	 * 
	 * @param id
	 *            The id.
	 * @param delay
	 *            The delay.
	 * @return The new graphic object.
	 */
	public static Graphic create(int id, int delay) {
		return create(id, delay, 0);
	}

	/**
	 * Creates a graphic.
	 * 
	 * @param id
	 *            The id.
	 * @param delay
	 *            The delay.
	 * @param height
	 *            The height.
	 * @return The new graphic object.
	 */
	public static Graphic create(int id, int delay, int height) {
		return new Graphic(id, delay, height);
	}

	/**
	 * Creates a high graphic with no delay
	 * 
	 * @param id
	 * @return
	 */
	public static Graphic highGraphic(int id) {
		return new Graphic(id, 0, 100);
	}

	/**
	 * Creates a high graphic with a delay
	 * 
	 * @param id
	 * @param delay
	 * @return
	 */
	public static Graphic highGraphic(int id, int delay) {
		return new Graphic(id, delay, 100);
	}

	/**
	 * The id.
	 */
	private int id;

	/**
	 * The delay.
	 */
	private int delay;

	/**
	 * The height.
	 */
	private int height;

	/**
	 * Creates a graphic.
	 * 
	 * @param id
	 *            The id.
	 * @param delay
	 *            The delay.
	 */
	public Graphic(int id, int delay, int height) {
		this.id = id;
		this.delay = delay;
		this.height = height;
	}

	/**
	 * Gets the id.
	 * 
	 * @return The id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the delay.
	 * 
	 * @return The delay.
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Gets the height.
	 * 
	 * @return The height.
	 */
	public int getHeight() {
		return height;
	}
}