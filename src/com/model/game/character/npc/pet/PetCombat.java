package com.model.game.character.npc.pet;

import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.utility.Utility;

/**
 * 
 * @author Patrick van Elderen | https://rune-server.org/members/_patrick_/
 * @date 28-4-2016
 *
 */
public class PetCombat {
	
	private final Player player;
	
	public PetCombat(Player player) {
		this.player = player;
	}
	
	/**
	 * Stores all the attack styles in an enum.
	 */
	public enum combatStyle {
		MELEE, RANGE, MAGIC;
	}
	
	 enum petsData {
		KREE_ARRA(6631, 1198, combatStyle.RANGE, 15);
		
		private final int petId;
		
		private final int graphic;
		
		private final combatStyle attackStyle;
		
		private final int maxDamage;
		
		petsData(final int petId, final int graphic, final combatStyle attackStyle, int maxDamage) {
			this.petId = petId;
			this.graphic = graphic;
			this.attackStyle = attackStyle;
			this.maxDamage = maxDamage;
		}
		
		public int getPet() {
			return petId;
		}
		
		public int getGraphic() {
			return graphic;
		}
		
		public combatStyle getStyle() {
			return attackStyle;
		}
		
		public int getDamage() {
			return maxDamage;
		}
	}
	
	/**
	 * This method is for handling the pet damage on foes.
	 * @param petOwner
	 * @param defender
	 */
	public void handlePetDamage(Player defender) {
		if(player.petId < 1)
			return;
		if(player.getPets().getPet(player) != null){
			Npc pet = player.getPets().getPet(player);
			if(!pet.getDelay().elapsed(30_000)){
				return;
			}
			pet.getDelay().reset();
			for (petsData data : petsData.values()) {
				if (player.petId == data.getPet()) {
					int damage = Utility.getRandom(data.getDamage());
					defender.playGraphics(Graphic.create(data.getGraphic(), 0, 0));
					defender.damage(new Hit(damage));
					defender.write(new SendMessagePacket(""+player.getName()+"'s follower has dealt "+damage+" extra damage."));
				}
				break;
			}
		}
	}

}
