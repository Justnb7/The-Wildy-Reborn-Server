package com.model.game.character.combat.range;

import com.model.game.World;
import com.model.game.character.Entity;
import com.model.game.character.Entity.EntityType;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.location.Position;

public class Projectile {

	private final Player player;

	public Projectile(Player player) {
		this.player = player;
	}

	public void stillGfx(int id, int x, int y, int height, int time) {
		if (id >= 65535) {
			throw new IllegalArgumentException(
					"Identification value for the still graphic is prohibited; "
							+ id);
		}
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(85);
			player.getOutStream().writeByteC(y - (player.getMapRegionY() * 8));
			player.getOutStream().writeByteC(x - (player.getMapRegionX() * 8));
			player.getOutStream().writeFrame(4);
			player.getOutStream().writeByte(0);
			player.getOutStream().writeShort(id);
			player.getOutStream().writeByte(height);
			player.getOutStream().writeShort(time);
		}
	}

	// creates gfx for everyone
	public void createPlayersStillGfx(int id, int x, int y, int height, int time) {
		for (int i = 0; i < World.getWorld().getPlayers().capacity(); i++) {
			Player p = World.getWorld().getPlayers().get(i);
			if (p != null) {
				Player person = p;
				if (person.getOutStream() != null) {
					if (person.distanceToPoint(x, y) <= 25) {
						stillGfx(id, x, y, height, time);
					}
				}
			}
		}
	}

	public void createProjectile3(int casterY, int casterX, int offsetY,
			int offsetX, int gfxMoving, int StartHeight, int endHeight,
			int speed, int AtkIndex) {
		for (int i = 1; i < World.getWorld().getPlayers().capacity(); i++) {
			if (World.getWorld().getPlayers().get(i) != null) {
				Player p = World.getWorld().getPlayers().get(i);
				if (p.WithinDistance(player.absX, player.absY, p.absX, p.absY,
						60)) {
					if (p.heightLevel == player.heightLevel) {
						if (World.getWorld().getPlayers().get(i) != null) {
							p.outStream.writeFrame(85);
							p.outStream
									.writeByteC((casterY - (p.mapRegionY * 8)) - 2);
							p.outStream
									.writeByteC((casterX - (p.mapRegionX * 8)) - 3);
							p.outStream.writeFrame(117);
							p.outStream.writeByte(50);
							p.outStream.writeByte(offsetY);
							p.outStream.writeByte(offsetX);
							p.outStream.writeShort(AtkIndex);
							p.outStream.writeShort(gfxMoving);
							p.outStream.writeByte(StartHeight);
							p.outStream.writeByte(endHeight);
							p.outStream.writeShort(51);
							p.outStream.writeShort(speed);
							p.outStream.writeByte(16);
							p.outStream.writeByte(64);
						}
					}
				}
			}
		}
	}

	/**
	 * Creating projectile
	 */
	public void createProjectile(int x, int y, int offX, int offY, int angle,
			int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(85);
			player.getOutStream().writeByteC(
					(y - (player.getMapRegionY() * 8)) - 2);
			player.getOutStream().writeByteC(
					(x - (player.getMapRegionX() * 8)) - 3);

			player.getOutStream().writeFrame(117);
			player.getOutStream().writeByte(angle);
			player.getOutStream().writeByte(offY);
			player.getOutStream().writeByte(offX);
			player.getOutStream().writeShort(lockon);
			player.getOutStream().writeShort(gfxMoving);
			player.getOutStream().writeByte(startHeight);
			player.getOutStream().writeByte(endHeight);
			player.getOutStream().writeShort(time);
			player.getOutStream().writeShort(speed);
			player.getOutStream().writeByte(16);
			player.getOutStream().writeByte(64);
		}
	}

	public void createProjectile2(int x, int y, int offX, int offY, int angle,
			int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time, int slope) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(85);
			player.getOutStream().writeByteC(
					(y - (player.getMapRegionY() * 8)) - 2);
			player.getOutStream().writeByteC(
					(x - (player.getMapRegionX() * 8)) - 3);
			player.getOutStream().writeFrame(117);
			player.getOutStream().writeByte(angle);
			player.getOutStream().writeByte(offY);
			player.getOutStream().writeByte(offX);
			player.getOutStream().writeShort(lockon);
			player.getOutStream().writeShort(gfxMoving);
			player.getOutStream().writeByte(startHeight);
			player.getOutStream().writeByte(endHeight);
			player.getOutStream().writeShort(time);
			player.getOutStream().writeShort(speed);
			player.getOutStream().writeByte(slope);
			player.getOutStream().writeByte(64);
		}
	}

	// projectiles for everyone within 25 squares
	public void createPlayersProjectile(int x, int y, int offX, int offY,
			int angle, int speed, int gfxMoving, int startHeight,
			int endHeight, int lockon, int time) {
		for (int i = 0; i < World.getWorld().getPlayers().capacity(); i++) {
			Player p = World.getWorld().getPlayers().get(i);
			if (p != null) {
				Player person = p;
				if (person.getOutStream() != null) {
					if (person.distanceToPoint(x, y) <= 25) {
						if (p.heightLevel == player.heightLevel) {
							createProjectile(x, y, offX, offY,
									angle, speed, gfxMoving, startHeight,
									endHeight, lockon, time);
						}
					}
				}
			}
		}
	}

	public void createPlayersProjectile(Position p, Position offset, int angle,
			int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time) {
		createPlayersProjectile(p.getX(), p.getY(), offset.getX(),
				offset.getY(), angle, speed, gfxMoving, startHeight, endHeight,
				lockon, time);
	}

	public void createPlayersProjectile2(int x, int y, int offX, int offY,
			int angle, int speed, int gfxMoving, int startHeight,
			int endHeight, int lockon, int time, int slope) {
		// synchronized (c) {
		for (int i = 0; i < World.getWorld().getPlayers().capacity(); i++) {
			Player p = World.getWorld().getPlayers().get(i);
			if (p != null) {
				Player person = p;
				if (person.getOutStream() != null) {
					if (person.distanceToPoint(x, y) <= 25) {
						createProjectile2(x, y, offX, offY,
								angle, speed, gfxMoving, startHeight,
								endHeight, lockon, time, slope);
					}
				}
			}
		}
	}

	public void shoot(int casterY, int casterX, int offsetY, int offsetX,
			int gfxMoving, int StartHeight, int endHeight, int speed, Entity e) {
		if (e == null) {
			return;
		}
		// interesting thing about projectile packet, if we're using the
		// client's PLAYER index array, the id is short.MAX_VAL + id (32k + 2048
		// max players)
		// otherwise for npcs its just id (range 0-short.max)
		int attack_index = e.getEntityType() == EntityType.PLAYER ? -(e
				.getIndex() + 1) : e.getIndex() + 1;
		for (int i = 1; i < World.getWorld().getPlayers().capacity(); i++) {
			if (World.getWorld().getPlayers().get(i) == null) {
				continue;
			}
			Player player = World.getWorld().getPlayers().get(i);
			if (!player.WithinDistance(player.absX, player.absY, player.absX,
					player.absY, 60)
					|| player.heightLevel != player.heightLevel) {
				continue;
			}
			player.outStream.writeFrame(85); // set base packet
			player.outStream
					.writeByteC((casterY - (player.mapRegionY * 8)) - 2);
			player.outStream
					.writeByteC((casterX - (player.mapRegionX * 8)) - 3);
			player.outStream.writeFrame(117); // actual projectile packet
			player.outStream.writeByte(50);
			player.outStream.writeByte(offsetY);
			player.outStream.writeByte(offsetX);
			player.outStream.writeShort(attack_index); // target index
			player.outStream.writeShort(gfxMoving);
			player.outStream.writeByte(StartHeight);
			player.outStream.writeByte(endHeight);
			player.outStream.writeShort(51);
			player.outStream.writeShort(speed);
			player.outStream.writeByte(16); // slope (?)
			player.outStream.writeByte(64); // angle (?)
		}
	}

	public void createPlayersProjectile(int x, int y, int offX, int offY,
			int angle, int speed, int gfxMoving, int startHeight,
			int endHeight, int lockon, int time, int slope) {
		for (int i = 0; i < World.getWorld().getPlayers().capacity(); i++) {
			Player p = World.getWorld().getPlayers().get(i);
			if (p != null) {
				Player person = p;
				if (person != null) {
					if (person.getOutStream() != null) {
						if (person.distanceToPoint(x, y) <= 25) {
							if (p.heightLevel == player.heightLevel)
								createProjectile(x, y, offX, offY, angle,
										speed, gfxMoving, startHeight,
										endHeight, lockon, time, slope);
						}
					}
				}
			}
		}
	}
	
	public void createProjectile(int x, int y, int offX, int offY, int id, int delay, int angle, int speed, int startHeight, int endHeight, int slope, int radius) {
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().writeFrame(85);
			player.getOutStream().writeByteC((y - (player.getMapRegionY() * 8)) - 2);
			player.getOutStream().writeByteC((x - (player.getMapRegionX() * 8)) - 3);
			player.getOutStream().writeFrame(117);
			player.getOutStream().writeShort(id);
			player.getOutStream().writeShort(delay);
			player.getOutStream().writeByte(angle);
			player.getOutStream().writeShort(speed);
			player.getOutStream().writeByte(startHeight);
			player.getOutStream().writeByte(endHeight);
			player.getOutStream().writeByte(slope);
			player.getOutStream().writeByte(radius);
			player.flushOutStream();
		}
	}
	
	public void sendProjectile(int x, int y, int offX, int offY, int id, int delay, int angle, int speed, int startHeight, int endHeight, int slope, int radius, int lockon) {
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().writeFrame(85);
			player.getOutStream().writeByteC((y - (player.getMapRegionY() * 8)) - 2);
			player.getOutStream().writeByteC((x - (player.getMapRegionX() * 8)) - 3);
			player.getOutStream().writeFrame(117);
			player.getOutStream().writeByte(angle);
			player.getOutStream().writeByte(offY);
			player.getOutStream().writeByte(offX);
			player.getOutStream().writeShort(lockon);
			player.getOutStream().writeShort(id);
			player.getOutStream().writeByte(startHeight);
			player.getOutStream().writeByte(endHeight);
			player.getOutStream().writeShort(delay);
			player.getOutStream().writeShort(speed);
			player.getOutStream().writeByte(slope);
			player.getOutStream().writeByte(radius);
			player.flushOutStream();
		}
	}
	
	public void playProjectile(int x, int y, int offX, int offY, int id, int delay, int angle, int speed, int startHeight, int endHeight, int slope, int radius, int lockon) {
		for (int i = 0; i < World.getWorld().getPlayers().capacity(); i++) {
			Player p = World.getWorld().getPlayers().get(i);
			if (p != null) {
				Player person = p;
				if (person != null) {
					if (person.getOutStream() != null) {
						if (person.distanceToPoint(x, y) <= 25) {
							if (p.heightLevel == player.heightLevel)
								person.getProjectile().sendProjectile(x, y, offX, offY, id, delay, angle, speed, startHeight, endHeight, slope, radius, lockon);
						}
					}
				}
			}
		}
	}

	public void createPlayersProjectile(int x, int y, int offX, int offY,
			int angle, int speed, int gfxId, int startHeight,
			int endHeight, int lockon, int time, int slope, int radius) {
		for (int i = 0; i < World.getWorld().getPlayers().capacity(); i++) {
			Player p = World.getWorld().getPlayers().get(i);
			if (p != null) {
				Player person = p;
				if (person != null) {
					if (person.getOutStream() != null) {
						if (person.distanceToPoint(x, y) <= 25) {
							if (p.heightLevel == player.heightLevel)
								person.getProjectile().createProjectile(x, y, offX, offY, angle, speed, gfxId, startHeight, endHeight, lockon, time, slope, radius);
						}
					}
				}
			}
		}
	}
	
	public void createProjectile(int x, int y, int offX, int offY, int angle,
			int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time, int slope, int radius) {

		if (player.getOutStream() != null && player != null) {
			player.getOutStream().writeFrame(85);
			player.getOutStream().writeByteC((y - (player.getMapRegionY() * 8)) - 2);
			player.getOutStream().writeByteC((x - (player.getMapRegionX() * 8)) - 3);
			player.getOutStream().writeFrame(117);
			player.getOutStream().writeByte(angle);
			player.getOutStream().writeByte(offY);
			player.getOutStream().writeByte(offX);
			player.getOutStream().writeWord(lockon);
			player.getOutStream().writeWord(gfxMoving);
			player.getOutStream().writeByte(startHeight);
			player.getOutStream().writeByte(endHeight);
			player.getOutStream().writeWord(time);
			player.getOutStream().writeWord(speed);
			player.getOutStream().writeByte(slope);
			player.getOutStream().writeByte(radius);
			player.flushOutStream();
		}
	}

	public void createLocationPorjectile(int projectileId, int locationX, int locationY, int startTime, int endTime) {
		int nX = player.getX();
		int nY = player.getY();
		int pX = locationX;
		int pY = locationY;
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;
		createPlayersProjectile(nX, nY, offX, offY, 50, endTime, projectileId, 33, 0, -1, startTime, 0, 36);

	}

	public void createNpcProjectile(int projectileId, NPC npc, int offsetX, int offsetY, int delay) {
		int nX = npc.getX();
		int nY = npc.getY();
		if (player.absX > npc.absX) {
			nX += offsetX;
		}
		if (player.absY > npc.absY) {
			nY += offsetY;
		}
		int pX = player.getX();
		int pY = player.getY();
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;
		int speed = 90 + (npc.distanceTo(player.getX(), player.getY()) * 5);
		createPlayersProjectile(nX, nY, offX, offY, 50, speed, projectileId, 83, 33, -player.getId() - 1, delay, 0, 36);

	}

}
