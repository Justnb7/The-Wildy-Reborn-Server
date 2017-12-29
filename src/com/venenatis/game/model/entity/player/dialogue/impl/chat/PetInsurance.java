package com.venenatis.game.model.entity.player.dialogue.impl.chat;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.definitions.NPCDefinitions;
import com.venenatis.game.model.entity.npc.pet.Follower;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class PetInsurance extends Dialogue {
	
	private static final int NPC_ID = 5906;
	
	private int petId;
	
	@Override
	protected void start(Object... parameters) {
		send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "Welcome to the pet insurance bureau.", "How can I help you?");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		if(getPhase() == 0) {
			send(DialogueType.PLAYER, Expression.CALM_TALK, "Tell me about pet insurance.");
			setPhase(1);
		} else if(getPhase() == 1) {
			send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "My insurance fee is 5,000,000 coins. Once you've paid", "that, the pet's insured forever, and you can reclaim it", "here unlimited times for a reclamation fee of 10,000,000", "coins whenever you lose your pet upon death.");
			setPhase(2);
		} else if(getPhase() == 2) {
			send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, "I've lost a pet. Have you got it?", "I have a pet that I'd like to insure.");
			setPhase(3);
		} else if(getPhase() == 4) {
			if (player.getPet() > 0) {
				petId = Follower.getBasePetNpcId(player.getPet());
				if (player.getPetInsurance().getInsuredPets().contains(Pet.fromNpc(petId).getNpc())) {
					send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "You already insured your " + NPCDefinitions.get(petId).getName() + ".");
				} else if (Pet.fromNpc(petId) != null) {
					send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "My insurance fee is 5,000,000 coins. Once you've paid", "that, the pet's insured forever, and you can reclaim it", "here unlimited times for a reclamation fee of 10,000,000", "coins whenever you lose your pet upon death.");
					setPhase(6);
				}
			} else {
				send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "You don't have any pet that is following you", "at the moment.");
				setPhase(8);
			}
		} else if(getPhase() == 5) {
			stop();
			player.getPetInsurance().openInsuranceInterface();
		} else if(getPhase() == 6) {
			send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, "Insure your " + NPCDefinitions.get(petId).getName() + " for 5,000,000 coins.", "Maybe another time.");
			setPhase(7);
		} else if(getPhase() == 8) {
			stop();
		}
	}
	
	@Override
	public void select(int index) {
		if (getPhase() == 3) {
			switch(index) {
			case 1:
				send(DialogueType.PLAYER, Expression.CALM_TALK, "What pets have I insured?");
				setPhase(5);
				break;
			case 2:
				send(DialogueType.PLAYER, Expression.CALM_TALK, "I have a pet that I'd like to insure.");
				setPhase(4);
				break;
			}
		} else if(getPhase() == 7) {
			switch(index) {
			case 1:
				if (player.getInventory().getAmount(995) < 5000000) {
					send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "You don't have enough coins to do this.");
					setPhase(8);
				} else {
					player.getInventory().remove(new Item(995, 5000000));
					player.getPetInsurance().addInsuredPet(Pet.fromNpc(petId).getItem());
					send(DialogueType.ITEM, Pet.fromNpc(petId).getItem(), "", "Your "  + NPCDefinitions.get(petId).getName() + " is now insured.", "You can reclaim it from Probita if you ever lose it.");
					setPhase(8);
				}
				break;
			case 2:
				stop();
				break;
			}
		}
	}

}
