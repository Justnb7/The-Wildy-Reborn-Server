package com.venenatis.game.content.activity.minigames;

/**
 * The enumerated types of {@link Minigame}s.
 * 
 * @author SeVen
 */
public enum MinigameType {

  /**
   * The type of minigame where players fight other players.
   */
  PVP,
  
  /**
   * The type of minigame where players fight npcs.
   */
  PVM,  
  
  /**
   * The type of minigame that focuses on skills.
   */
  SKILL,
  
  /**
   * The type of minigame that is a mixture of both skills and combat.
   */
  COMBAT_AND_SKILL,
  
  /**
   * The type of minigame that does not fall in the above categories.
   */
  OTHER

}