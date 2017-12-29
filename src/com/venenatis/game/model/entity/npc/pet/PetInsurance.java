package com.venenatis.game.model.entity.npc.pet;

import java.util.ArrayList;
import java.util.Arrays;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;

/**
 * 
 * The class which represents functionality for the pet insurance system.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van  Elderen</a>
 * @author Stan Jansen main editor of this class
 */
public class PetInsurance {
	
	/**
	 * The player
	 */
	private Player player;

	public PetInsurance(Player player) {
		this.player = player;
	}

	/**
	 * The insured pets
	 */
	private ArrayList<Integer> insuredPets = new ArrayList<Integer>();
	
	/**
	 * Check if we have already insured this pet
	 * 
	 * @param id
	 *            The pet being insured
	 */
	public boolean isInsured(int id) {
		return insuredPets.contains(id);
	}
	
	/**
	 * Opens the insurance container
	 */
	public void openInsuranceInterface() {
		
		//Sents an empty string to the container if we have no pets insured
		if (getInsuredPets() == null || getInsuredPets().isEmpty()) {
			player.getActionSender().sendString("None", 27008);
		} else {
			player.getActionSender().sendString("", 27008);
		}
		
		ArrayList<Integer> reclaimablePets = getReclaimablePets();
		
		//Sents an empty string the the container if we have no pets to claim
		if (reclaimablePets == null || reclaimablePets.isEmpty()) {
			player.getActionSender().sendString("None", 27009);
		} else {
			player.getActionSender().sendString("", 27009);
		}
		
		
		if (getInsuredPets() != null && !getInsuredPets().isEmpty()) {
			Item[] insuredItems = new Item[getInsuredPets().size()];
			int i = 0;
			for (int id : getInsuredPets()) {
				insuredItems[i] = new Item(id);
				i++;
			}
			//Sets the scrollbar based on pets on the container
			player.getActionSender().setScrollPosition(27010, 0, (int) (Math.ceil(insuredItems.length / 7) + 1) * 42);
			player.getActionSender().sendItemsOnInterface(27011, Arrays.asList(insuredItems));
		} else {
			player.getActionSender().setScrollPosition(27010, 0, 0);
			player.getActionSender().sendItemsOnInterface(27011, Arrays.asList());
		}
		
		if (reclaimablePets != null && !reclaimablePets.isEmpty()) {
			Item[] reclaimableItems = new Item[reclaimablePets.size()];
			int i = 0;
			for (int id : reclaimablePets) {
				reclaimableItems[i] = new Item(id);
				i++;
			}
			//Sets the scrollbar based on pets on the container
			player.getActionSender().sendItemsOnInterface(27013, Arrays.asList(reclaimableItems));
			player.getActionSender().setScrollPosition(27012, 0, (int) (Math.ceil(reclaimableItems.length / 7) + 1) * 42);
		} else {
			player.getActionSender().setScrollPosition(27012, 0, 0);
			player.getActionSender().sendItemsOnInterface(27013, Arrays.asList());
		}
		
		player.getActionSender().sendInterface(27000);
	}
	
	/**
	 * Reclaim a pet by its identifier
	 * 
	 * @param id
	 *            The pet Id
	 */
	public void reclaimPet(int id) {
		int npcId = Follower.getBasePetNpcId(Pet.from(id).getNpc());
		id = Pet.fromNpc(npcId).getItem();
		if (getReclaimablePets().contains(id)) {
			if (player.getInventory().getAmount(995) < 10000000) {
				player.getActionSender().sendMessage("@red@You don't have enough coins to reclaim your pet!");
			} else if (player.getInventory().getFreeSlots() == 0 && player.getInventory().getAmount(995) != 10000000) {
				player.getActionSender().sendMessage("@red@You don't have enough room in your inventory to reclaim your pet!");
			} else {
				player.getActionSender().sendMessage("You've reclaimed your pet!");
				player.getInventory().remove(new Item(995, 10000000));
				player.getInventory().add(new Item(id));
				openInsuranceInterface();
			}
		}
	}
	
	public ArrayList<Integer> getInsuredPets() {
		return insuredPets;
	}
	
	/**
	 * Checks if we have any reclaimable pets
	 */
	public ArrayList<Integer> getReclaimablePets() {
		ArrayList<Integer> reclaimablePets = new ArrayList<Integer>();
		
		for (int id : getInsuredPets()) {
			boolean caught = false;
			for (int i : Follower.getItemAlternatives(id)) {
				if (player.getInventory().contains(i) || player.getBank().contains(i) || (player.getPet() > 0 && Pet.fromNpc(player.getPet()).getItem() == i)) {
					caught = true;
				}
			}
			if (!caught) {
				reclaimablePets.add(id);
			}
		}
		
		return reclaimablePets;
	}
	
	public void addInsuredPet(int id) {
		this.insuredPets.add(id);
	}
	
	public void setInsuredPets(ArrayList<Integer> insuredPets) {
		this.insuredPets = insuredPets;
	}
}