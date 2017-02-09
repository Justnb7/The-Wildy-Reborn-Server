package com.model.game.character.combat.effect;

import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;

public interface DamageEffect {

	/**
	 * Executes some effect during the damage step of a player attack
	 * @param attacker	the attacking player in combat
	 * @param defender	the defending player in combat
	 * @param damage	the damage dealt during this step
	 */
	public void execute(Player attacker, Player defender, int damage);
	
	/**
	 * Executes some effect during the damage step of a player attack
	 * @param attacker	the attacking player in combat
	 * @param defender	the defending npc in combat
	 * @param damage	the damage dealt during this step
	 */
	public void execute(Player attacker, Npc defender, int damage);
	
	/**
	 * Determines if the event is executable by the operator
	 * @param operator	the player executing the effect
	 * @return	true if it can be executed based on some operation, otherwise false 
	 */
	public boolean isExecutable(Player operator);
	
}
