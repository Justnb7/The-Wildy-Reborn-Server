package com.model.game.character.walking;
 
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.task.Stackable;
import com.model.task.Walkable;
 
public class RegionalPetCheck extends ScheduledTask {
 
	private Player player;
	public RegionalPetCheck(Player p, boolean login){
		super(p, 5, login, Walkable.WALKABLE, Stackable.NON_STACKABLE);
		this.player = p;
	}
 
	@Override
	public void execute() {
		if (player.petId < 1) {
			System.out.println(player.getName() + " |  Here closing petspawn event");
			this.stop();
		}
		if (player.getPets().getPet(player) == null) {
			System.out.println("Respawning pet for "+ player.getName());
			player.getPets().spawnPet(player, 0, true);
		}
	}
 
}