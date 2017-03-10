package com.model.game.character.combat;

import com.model.Server;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;

public class Combat {
    
    /**
     * The names of all the bonuses in their exact identified slots.
     */
    public static final String[] BONUS_NAMES = { "Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range",
            "Strength", "Prayer", "", "" };
	
	public static void resetCombat(Player player) {
		player.usingMagic = false;
		player.faceEntity(player); // face urself wtf l0l
		player.npcIndex = 0;
		player.getCombat().reset();
		player.getPA().resetFollow();
		player.setInCombat(false);
		player.getActionSender().sendString("", 35000);
	}

	public static void playerVsEntity(Player player) {

	}


	public static void setCombatStyle(Player player) {

		/*
		 * Check if we are using magic
		 */
		if (player.autoCast && (player.getSpellBook() == SpellBook.MODERN || player.getSpellBook() == SpellBook.ANCIENT)) {
			player.spellId = player.autocastId;
			player.usingMagic = true;
			player.setCombatType(CombatType.MAGIC);
		}

		if (player.playerEquipment[player.getEquipment().getWeaponId()] == 11907) {
			player.spellId = 52;
			player.castingMagic = true;
		}

		if (player.playerEquipment[player.getEquipment().getWeaponId()] == 12899) {
			player.spellId = 53;
			player.castingMagic = true;
		}

		if (player.getSpellId() > 0) {
			player.usingMagic = true;
			player.setCombatType(CombatType.MAGIC);
		}
		if (player.usingMagic) {
			player.setCombatType(CombatType.MAGIC);
		}

		/*
		 * Check if we are using ranged
		 */
		if (player.getCombatType() != CombatType.MAGIC) {
			player.usingBow = player.getEquipment().isBow(player);
			player.throwingAxe = player.getEquipment().isThrowingWeapon(player);
			player.usingCross = player.getEquipment().isCrossbow(player);
			player.usingArrows = player.getEquipment().isArrow(player);
			boolean bolt = player.getEquipment().isBolt(player);
			boolean javalin = player.getCombat().properJavalins();

			if(player.throwingAxe || player.usingCross || player.usingBow || player.getEquipment().wearingBallista(player) || player.getEquipment().wearingBlowpipe(player)) {
				player.setCombatType(CombatType.RANGED);
			}

			if(player.throwingAxe) {
				player.throwingAxe = true;
			}

			if(bolt || javalin || player.usingArrows) {
				player.usingArrows = true;
			}
		}
	}

	public static void hitEvent(Player player, Entity target, int delay, Hit hit) {
		Server.getTaskScheduler().schedule(new ScheduledTask(delay) {
			public void execute() {
				// TODO hit code which i put in notepad
				this.stop();
			}
		});
	}
}