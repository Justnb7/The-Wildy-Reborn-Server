package com.venenatis.game.model.combat.npcs.impl.zulrah;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.venenatis.game.event.CycleEvent;
import com.venenatis.game.event.CycleEventContainer;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.server.Server;

public class SpawnDangerousEntity extends CycleEvent {

	private Zulrah zulrah;

	private Player player;

	private Queue<DangerousLocation> locations = new LinkedList<>();

	private DangerousEntity entity;

	private int duration;

	public SpawnDangerousEntity(Zulrah zulrah, Player player, List<DangerousLocation> locations, DangerousEntity entity) {
		this.player = player;
		this.zulrah = zulrah;
		this.entity = entity;
		this.locations.addAll(locations);
	}

	public SpawnDangerousEntity(Zulrah zulrah, Player player, List<DangerousLocation> locations, DangerousEntity entity, int duration) {
		this(zulrah, player, locations, entity);
		this.duration = duration;
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (player == null || zulrah == null || zulrah.getNpc() == null || zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		zulrah.getNpc().setFacePlayer(false);
		int ticks = container.getTotalTicks();
		DangerousLocation location = locations.peek();
		if (location == null) {
			container.stop();
			zulrah.getNpc().faceEntity(player);
			return;
		}
		if (ticks == 4 || ticks == 8 || ticks == 12 || ticks == 16) {
			for (Point point : location.getPoints()) {
				if (entity.equals(DangerousEntity.TOXIC_SMOKE)) {
					Server.getGlobalObjects().add(new GameObject(11700, point.x, point.y, zulrah.getInstancedZulrah().getHeight(), 0, 10, duration, -1));
				} else if (entity.equals(DangerousEntity.MINION_NPC)) {
					Server.npcHandler.spawn(player, Zulrah.SNAKELING, new Location(point.x, point.y, zulrah.getInstancedZulrah().getHeight()), 0, true, false);
				}
			}
			locations.remove();
		}
		if (ticks == 2 || ticks == 6 || ticks == 10 || ticks == 14) {
			zulrah.getNpc().face(player, new Location(location.getPoints()[0].x, location.getPoints()[0].y));
			int npcX = zulrah.getNpc().getX();
			int npcY = zulrah.getNpc().getY();
			for (Point point : location.getPoints()) {
				int targetY = (npcX - (int) point.getX()) * -1;
				int targetX = (npcY - (int) point.getY()) * -1;
				int projectile = entity.equals(DangerousEntity.TOXIC_SMOKE) ? 1044 : 1047;
				player.getActionSender().sendProjectile(new Location(npcX, npcY), new Location(targetX, targetY), projectile, 65, 50, 85, 70, 0, 16, 64, -1);
			}
			zulrah.getNpc().playAnimation(new Animation(5068));
		}
	}

}