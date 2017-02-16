package com.model.game.character;
 
import com.model.game.World;
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
			//System.out.println(player.getUsername() + " |  Here closing petspawn event");
			this.stop();
		}
		if (player.getPets().getPet(player, World.getWorld().getNpcs().get(player.petNpcIndex)) == null) {
			//System.out.println("Respawning pet for "+ player.getUsername());
			player.getPets().spawnPet(player, 0, true);
		}
	}
 
}