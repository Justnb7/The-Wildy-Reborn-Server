package com.venenatis.game.model.combat.special_attacks;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelRule;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.definitions.WeaponDefinition;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;

/**
 * The class which represents functionality for the special attack.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @date 13-12-2016
 */
public class Special {
	
	private Player player;
	
	public Special(Player player) {
		this.player = player;
	}

	/**
	 * Handles a special attack for a specific player
	 * 
	 * @param attacker
	 *            The player performing the special attack
	 */
	public void execute(Player attacker, Entity target) {
		if (target == null) {
			return;
		}
		
		updateText();
		attacker.setUsingSpecial(true);

		Item weapon = attacker.getEquipment().get(EquipmentConstants.WEAPON_SLOT);

		if (weapon != null) {
			SpecialAttack special = SpecialAttackHandler.forId(weapon.getId());

			if (special == null) {
				System.out.println("Invalid special attack: " + weapon);
				resetSpecial();
				return;
			}
			
			if (attacker.getDuelArena().isDueling()) {
				if (attacker.getDuelArena().getRules().get(DuelRule.SPECIAL_ATTACKS)) {
					attacker.message("Special attacks are disabled in this duel.");
					return;
				}
			}
			
			if (!attacker.getController().canUseSpecial(attacker)) {
				return;
			}

			if (attacker.getSpecialAmount() >= special.amountRequired()) {
				if (special.meetsRequirements(attacker, target)) {
					attacker.setSpecialAmount(attacker.getSpecialAmount() - special.amountRequired());
					special.handleAttack(attacker, target);

					attacker.debug("speced");
					attacker.logoutDelay.reset();
					if (attacker.getCombatState().getTarget().isPlayer()) { // playerIndex is the indexId of the player we're attacking
						Player targPlayer = (Player) target; // type cast
						targPlayer.putInCombat(attacker.getIndex());
						targPlayer.logoutDelay.reset();
						attacker.updateLastCombatAction();
						attacker.setInCombat(true);
					}
				}
			} else {
				attacker.message("You do not have the required special amount.");
			}
		}
		resetSpecial();
	}

	/**
	 * Resets the players special attack
	 * 
	 * @param player
	 *            The player resetting the special attack
	 */
	public void resetSpecial() {
		player.setUsingSpecial(false);
		updateText();
		updateInterface();
	}
	
	public void update() {
		updateAmount();
		updateText();
	}
	
	public void updateAmount() {
		Item item = player.getEquipment().getWeapon();

		if (item != null) {// this is new method
			final WeaponDefinition def = WeaponDefinition.get(item.getId());
			if (SpecialAttackHandler.get(item.getId()) != null) {
				int id = def.getType().getSpecialBarId(); // u sure this is right
				System.out.println("spec bar id: "+id);
				int specialCheck = 100;
				for (int i = 0; i < 10; i++) { // ya theyre the same just using a loop to stop duplicating same line of code 10 times yeah thats what i thought
					//so issue cant be here
					id--;
					player.getActionSender().moveComponent(player.getSpecialAmount() >= specialCheck ? 500 : 0, 0, id);
					player.debug("sending barId:"+id);
					specialCheck -= 10;
				}
			}
		}
	}
	
	//old
	
	/*public void specialAmount(int weapon, int specAmount, int barId) {
		player.specBarId = barId;
		player.getActionSender().moveComponent(specAmount >= 100 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 90 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 80 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 70 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 60 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 50 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 40 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 30 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 20 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 10 ? 500 : 0, 0, (--barId));
		refreshSpecialAttack();
		sendWeapon(weapon, ItemDefinition.forId(weapon).getName());
	}*/
	
	public void updateInterface() {
		player.debug("Enter method to write special attack interface");
		Item item = player.getEquipment().getWeapon();

		if (item == null || SpecialAttackHandler.get(item.getId()) != null) {
			if (item != null) {
				WeaponDefinition def = WeaponDefinition.get(item.getId());
				player.getActionSender().sendInterfaceConfig(def.getType().getConfigId(), false);
				player.debug("send interface config: "+def.getType().getConfigId());
				updateAmount();
			}
		} else {
			player.getActionSender().sendInterfaceConfig(7549, true);
			player.getActionSender().sendInterfaceConfig(7561, true);
			player.getActionSender().sendInterfaceConfig(7574, true);
			player.getActionSender().sendInterfaceConfig(12323, true);
			player.getActionSender().sendInterfaceConfig(7599, true);
			player.getActionSender().sendInterfaceConfig(7674, true);
			player.getActionSender().sendInterfaceConfig(7474, true);
			player.getActionSender().sendInterfaceConfig(7499, true);
			player.getActionSender().sendInterfaceConfig(8493, true);
			player.getActionSender().sendInterfaceConfig(7574, true);
			player.getActionSender().sendInterfaceConfig(7624, true);
			player.getActionSender().sendInterfaceConfig(7699, true);
			player.getActionSender().sendInterfaceConfig(7800, true);
		}
	}
	
	public void updateText() {
		Item weapon = player.getEquipment().get(3);

		if (weapon != null) {
			WeaponDefinition def = WeaponDefinition.get(weapon.getId());

			if (SpecialAttackHandler.get(weapon.getId()) != null) {
				String col = player.isUsingSpecial() ? "<col=ffff00>" : "<col=0>";
				player.getActionSender().sendString(String.format("%sSpecial Attack - %s%%", col, player.getSpecialAmount()), def.getType().getSpecialBarId());
			}
		}
	}
}