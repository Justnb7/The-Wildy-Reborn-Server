package com.model.game.character.npc.combat.combat_scripts;

import java.util.Random;

import com.model.Server;
import com.model.game.character.Graphic;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.location.Position;
import com.model.task.ScheduledTask;

public class Chaos_Elemental extends Boss {
	
	private static int attackStyle = 0;

	private static Random r = new Random();

	public Chaos_Elemental(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		int offX = (npc.getY() - player.getY()) * -1;
		int offY = (npc.getX() - player.getX()) * -1;
		int attack = r.nextInt(2);
		attackStyle = attack;
		if (attack == 0) {
			npc.playGraphics(Graphic.create(550, 0, 100));
			player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 75, 551, 31, 31, -player.getId() - 1, 55, 0);
		}
		if (attack == 1) {
			npc.playGraphics(Graphic.create(553, 0, 100));
			player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 75, 554, 31, 31, -player.getId() - 1, 55, 0);
		}
		if (attack == 2) {
			npc.playGraphics(Graphic.create(556, 0, 100));
			player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 75, 557, 31, 31, -player.getId() - 1, 55, 0);
		}
		Server.getTaskScheduler().schedule(new ScheduledTask(5) {

			@Override
			public void execute() {
				if (player.getSkills().getLevel(Skills.HITPOINTS) <= 0 || player.isDead || player == null) {
					this.stop();
					return;
				}
				if (attackStyle == 0) {
					player.playGraphics(Graphic.create(552, 0, 100));
					int itemToRemove = r.nextInt(player.playerEquipment().length - 1);
					if (player.playerEquipment()[itemToRemove] > 0 && player.getItems().getFreeSlots() > 0) {
						player.getItems().removeEquipment(player.playerEquipment()[itemToRemove], itemToRemove);
						if (itemToRemove == player.getEquipment().getWeaponId() || itemToRemove == player.getEquipment().getQuiverId()) {
							Combat.resetCombat(player);
						}
						this.stop();
						player.write(new SendMessagePacket("The chaos elemental removes some of your equipment."));
						return;
					}
				}
				if (attackStyle == 1) {
					player.playGraphics(Graphic.create(555, 0, 100));
					Position randomLocation = teleportLocation(npc, player.getX(), player.getY());
					player.getPA().movePlayer(randomLocation.getX(), randomLocation.getY(), 0);
				}
				this.stop();
			}

		});
	}
	
	public Position teleportLocation(Npc npc, int otherX, int otherY) {
		int x = npc.absX - otherX;
		int y = npc.absY - otherY;
		if (x > 0) {
			otherX += 5;
		} else if (x < 0) {
			otherX -= 5;
		}
		if (y > 0) {
			otherY += 5;
		} else if (y < 0) {
			otherY -= 5;
		}
		return new Position(otherX, otherY, 0);
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		if (player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
			return damage /2;
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return 28;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return npc.getDefinition().getAttackAnimation();
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		return false;
	}

	@Override
	public boolean switchesAttackers() {
		return true;
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
	public int offSet(Npc npc) {
		return 0;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return 8;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}

}