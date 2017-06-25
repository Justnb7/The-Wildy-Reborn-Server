package com.venenatis.game.model.combat.data;

/**
 * A enum representing different skull types.
 *
 *@author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public enum SkullType {
	
	NONE(-1),
	SKULL(0),
	RED_SKULL(1);
	
	/**
     * The identification for this skull type.
     */
    private final int id;

    /**
     * Create a new {@link SkullType}.
     * 
     * @param id
     *            the identification for this skull type.
     */
    private SkullType(int id) {
        this.id = id;
    }

    /**
     * Gets the identification for this skull type.
     * 
     * @return the identification for this skull type.
     */
    public final int getId() {
        return id;
    }

}
