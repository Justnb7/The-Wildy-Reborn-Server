package com.model.game.character.npc.combat;

import com.model.game.character.Entity;

/**
 * 
 * @author Patrick van Elderen
 * @version 1.2
 * @date Feb, 23-2-2016
 * @edited 5-7-2016
 */
public abstract class Boss {
	
	protected int npcId;
	
	public Boss(int npcId) {
		this.npcId = npcId;
	}
	
	//execution
	public abstract void execute(Entity attacker, Entity victim);
	
	public abstract int distance();//distance check
	
	//do i need anu more? tbh its not 
	//So basicly im using that dmg method you made before the take_hit
	//but i can't get it to work on npc combat lets se
	// what doesnt happen well it works but i belive its player base only? when
	//i have overheads dmg is still the same as without overheads
	// aah i understand and that is 1 and the other issue how do i apply delays in the combat
	//i dont think attackDelay var works, it should, show me how
	// use using it atm?
	
}
