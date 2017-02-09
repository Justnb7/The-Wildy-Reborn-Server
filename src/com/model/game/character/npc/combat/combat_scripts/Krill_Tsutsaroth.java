package com.model.game.character.npc.combat.combat_scripts;

import java.util.Random;

import com.model.game.character.combat.PrayerHandler.Prayer;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.ProtectionPrayer;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;

public class Krill_Tsutsaroth extends Boss {

	public Krill_Tsutsaroth(int npcId) {
		super(npcId);
	}

	private static Random r = new Random();
	
	private final String[] MESSAGES = { "Attack them, you dogs!", "Forward!", "Death to Saradomin's dogs!",
			"Kill them, you cowards!",
			"The Dark One will have their souls!",
			"Zamorak curse them!",
			"Rend them limb from limb!", "No retreat!", "Flay them all!"};

	@Override
	public void execute(Npc npc, Player player) {
		int message = r.nextInt(15);
		int attack = r.nextInt(20);
		int poison = r.nextInt(10);
		if (message > 12 && message <= 20) {
			npc.forceChat(MESSAGES[(int) (Math.random() * MESSAGES.length)]);
			npc.forcedChatRequired = true;
			npc.updateRequired = true;
		}
		if (attack <= 14) {
			if (poison > 8 && poison <= 10) {
				player.setPoisonDamage((byte) 16);
			}
			npc.attackStyle = 0;
		/*} else if (attack > 15 && attack < 28) {
			npc.attackType = 2;
			npc.gfx0(1210);
			int offX = (npc.getY() - player.getY()) * -1;
			int offY = (npc.getX() - player.getX()) * -1;
			player.getPA().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 106, 1211, 0, 0, -player.getId() - 1, 76, 0);
			player.playGraphic(Graphic.create(1225, 0, 0));
		} else {*/
		} else {
			npc.attackStyle = 4;
			if (player.isActivePrayer(Prayer.PROTECT_FROM_MELEE)) {
				sendSpecialAttack(player, npc);
			}
		}
	}

	private void sendSpecialAttack(Player player, Npc npc) {
		npc.forceChat("YARRRRRRR!");
		npc.forcedChatRequired = true;
		npc.updateRequired = true;
		int prayerReduction = player.getSkills().getLevel(Skills.PRAYER) / 2;
		if (prayerReduction < 1) {
			return;
		}
		player.getSkills().setLevel(Skills.PRAYER, prayerReduction);
		
		if (player.getSkills().getLevel(Skills.PRAYER) < 0) {
			player.getSkills().setLevel(Skills.PRAYER, 0);
		}
		player.write(new SendMessagePacket("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained."));
	}

	@Override
	public int getProtectionDamage(ProtectionPrayer protectionPrayer, int damage) {
		switch (protectionPrayer) {
		case MAGE:
			return 0;
		case MELEE:
			return 0;
		case RANGE:
			break;
		default:
			break;

		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 2 ? 30 : attackType == 0 ? 47 : 49;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return npc.attackStyle == 0 ? 6948 : 6950;
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.getDefinition().getAttackSpeed();
	}

	@Override
	public int getHitDelay(Npc npc) {
		return 4;
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		return false;
	}

	@Override
	public boolean switchesAttackers() {
		return false;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return 8;
	}

	@Override
	public int offSet(Npc npc) {
		return 0;
	}


	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}
}