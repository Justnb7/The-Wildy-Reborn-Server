package com.venenatis.game.model.combat.data;

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
	DISARM, 
	
	/**
	 * Chaos fanatic special attack
	 */
	GREEN_BOMB, 
	
	/**
	 * Giant mole special move
	 */
	DIG,
	
	/**
	 * Veng recoil self-damage (rock cake) 
	 */
	GENERIC,
	
	/**
	 * Barrelchest's special move
	 */
	ZIP, 
	
	/**
	 * Venenatis prayer drain attack
	 */ 
	DRAIN_PRAYER, 
	
	/**
	 * Venenatis special attack
	 */
	WEB,
	
	;
}