package com.venenatis.game.content.activity.minigames;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.venenatis.game.content.activity.Activity;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Stopwatch;

/**
 * Represents a single minigame that a player can participate in.
 * 
 * @author Seven
 */
public abstract class Minigame extends Activity {

  /**
   * The enumerated types of a minigames classification.
   */
  public enum Classification {

    /**
     * The type of minigame that denotes a safe minigame.
     */
    SAFE,

    /**
     * The type of minigame that denotes a dangerous minigame.
     */
    DANGEROUS;
  }
  
  public enum RunType {
    
    /**
     * A minigame that can run by itself.
     * 
     * e.g duel arena, barrows.
     */
    STANDALONE,
    
    /**
     * A minigame that runs in a particular order of events.
     * 
     * e.g fight caves, pest control
     */
    SEQUENCED;
    
  }
  
  /**
   * How this minigame runs.
   */
  private final RunType runType;

  /**
   * The type of this minigame.
   */
  private final MinigameType type;

  /**
   * The classification of this minigame. (Safe or dangerous)
   */
  private final Classification classication;

  /**
   * The stopwatch for this minigame.
   */
  private final Stopwatch stopwatch = new Stopwatch();

  /**
   * The flag that denotes this minigame has been beat.
   */
  private boolean won;

  /**
   * Creates a new {@link Minigame}.
   * 
   * @param name
   *            The name of this minigame.
   * 
   * @param type
   *            The type of minigame.
   */
  public Minigame(String name, RunType runType, MinigameType type, Classification classification) {
    super(name);
    this.runType = runType;
    this.type = type;
    this.classication = classification;
  }
  
  /**
   * The method that starts the minigame.
   */ 
  public abstract void onStart();

  /**
   * The method that ends the minigame.
   */
  public abstract void onEnd();

  /**
   * Determines if a player is currently in this minigame.
   */
  public abstract boolean inArea(Player player);

  /**
   * Determines if this minigame is currently active.
   */
  public abstract boolean isActive(); 
  
  /**
   * The method called to display a walkable interfacoe for this player in this minigame.
   *
   * @param player
   *    The player to display for.
   */
  public abstract void onDisplay(Player player);

  public void onMobDeath(Entity entity) {

  }

  public boolean canSkull() {
    return false;
  }
  
  public boolean canAttack(Player player) {
	  return false;
  }
  
  /**
   * Gets the current state of this minigame.
   */
  public abstract Optional<String> getState();

  /**
   * Determines if a {@code player} is present in a minigame.
   * 
   * @param player
   *            The player to check.
   * 
   * @return {@code true} If a player is present in this minigame.
   *         {@code false} Otherwise.
   */
  public abstract boolean contains(Player player);
  
  /**
   * The method called when a {@code defender} dies.
   */
  public void onDeath(Player player) {

  }

  /**
   * Determines if a player can keep their items on death.
   * 
   * @return {@code true} If this minigame is a safe minigame. {@code false} otherwise.
   */
  public boolean canKeepItems() {
    return getClassication() == Classification.SAFE;
  }
  
  /**
   * Gets the respawn location of this minigame if a player dies.
   * 
   * @param player
   *    The player who died.
   */
  public Location respawnLocation(Player player) {
    return new Location(3093, 3244);    
  }

  /**
   * @return the type
   */
  public MinigameType getType() {
    return type;
  }

  /**
   * @return the classication
   */
  public Classification getClassication() {
    return classication;
  }

  /**
   * @return the stopwatch
   */
  public Stopwatch getStopwatch() {
    return stopwatch;
  }
  
  /**
   * @return the runType
   */
  public RunType getRunType() {
    return runType;
  }

  public boolean hasWon() {
    return won;
  }

  public void setWon(boolean won) {
    this.won = won;
  }

  @Override
  public String toString() {
    return "[minigame] - " + "[name= " + this.getName() + "]" + " [state=" + this.getState().orElse("NONE").toLowerCase()  + "] [time_active= " + this.getStopwatch().elapsed(TimeUnit.SECONDS) + "]";
  }

}