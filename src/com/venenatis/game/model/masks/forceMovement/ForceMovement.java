package com.venenatis.game.model.masks.forceMovement;

import com.venenatis.game.model.entity.player.Player;

/*
	Copyright (c) 2010-2011 Graham Edgecombe
	Copyright (c) 2011-2015 Major <major.emrs@gmail.com> and other apollo contributors

	Permission to use, copy, modify, and/or distribute this software for any
	purpose with or without fee is hereby granted, provided that the above
	copyright notice and this permission notice appear in all copies.

	THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
	WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
	MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
	ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
	WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
	ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
	OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
/**
 * Represents the force movement mask that will force a {@link Player} to move
 * towards a certain location.
 * 
 * @author Patrick van Elderen
 */
public class ForceMovement {

	/**
	 * The starting X position
	 */
	private final int startX;

	/**
	 * The starting Y position
	 */
	private final int startY;
	
	/**
	 * The ending X position
	 */
	private final int endX;

	/**
	 * The ending Y position
	 */
	private final int endY;

	/**
	 * The time in ticks that it will take to move across the {@code X} axis.
	 */
	private final int durationX;

	/**
	 * The time in ticks that it will take to move across the {@code Y} axis.
	 */
	private final int durationY;
	
	/**
	 * The time in ticks that it will take to perform this mask
	 */
	private final int ticks;

	/**
	 * The {@link Direction} that the {@link Player} is moving in.
	 */
	private final Direction direction;
	
	public ForceMovement(int startX, int startY, int endX, int endY, int durationX, int durationY, int ticks, Direction direction) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.durationX = durationX;
		this.durationY = durationY;
		this.ticks = ticks;
		this.direction = direction;
	}
	
	/**
	 * @return the startX
	 */
	public int getStartX() {
		return startX;
	}

	/**
	 * @return the startY
	 */
	public int getStartY() {
		return startY;
	}

	/**
	 * @return the endX
	 */
	public int getEndX() {
		return endX;
	}

	/**
	 * @return the endY
	 */
	public int getEndY() {
		return endY;
	}

	/**
	 * @return the durationX
	 */
	public int getDurationX() {
		return durationX;
	}

	/**
	 * @return the durationY
	 */
	public int getDurationY() {
		return durationY;
	}
	
	/**
	 * @return the ticks
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}

}