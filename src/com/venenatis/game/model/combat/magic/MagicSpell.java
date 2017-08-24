package com.venenatis.game.model.combat.magic;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;

/**
 * Handles casting magic spells.
 * 
 * @author Daniel
 *
 */
public interface MagicSpell {

    /**
     * Executes the magic spell.
     * 
     * @param player
     * @return
     */
    public boolean execute(Player player);

    /**
     * The experience given once spell is casted.
     * 
     * @return
     */
    public double getExperience();

    /**
     * Gets the level required to cast spell.
     * 
     * @return
     */
    public int getLevel();

    /**
     * Gets the name of the spell.
     * 
     * @return
     */
    public String getName();

    /**
     * Gets the runes required to cast the spell.
     * 
     * @return
     */
    public Item[] getRunes();

}