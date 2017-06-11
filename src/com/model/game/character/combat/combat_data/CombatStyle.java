package com.model.game.character.combat.combat_data;

/**
 * The enumerated type whose elements represent the different types of combat.
 *
 * @author lare96 <http://github.com/lare96>
 */
public enum CombatStyle {

    /**
     * The melee combat type, includes things like swords and daggers.
     */
    MELEE,

    /**
     * The ranged combat type, includes things like bows and crossbows.
     */
    RANGE,
    
    /**
     * The magic combat type, includes things like fire blast and ice barrage.
     */
    MAGIC,
    
    /**
     * The dragon fire combat type.
     */
    DRAGON_FIRE, 
    
    /**
     * Callisto's special attack
     */
    SET_BACK,
    
    /**
     * The Chaos elementals teleporting attack
     */
    TELEOTHER,
    
    /**
     * The Chaos elementals disarm attack
     */
	DISARM;
}