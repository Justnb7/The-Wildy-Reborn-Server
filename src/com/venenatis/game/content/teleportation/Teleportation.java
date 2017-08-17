package com.venenatis.game.content.teleportation;

import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;

public enum Teleportation {
	MODERN(TeleportTypes.MODERN, 3, 202, new Animation(714), new Graphic(308, 4, 100), new Animation(715), new Graphic(308, 4, 100)),
	ANCIENT(TeleportTypes.ANCIENT, 3, 202, new Animation(1979), new Graphic(392, 0, 0), new Animation(65535), new Graphic(-1)),
	LUNAR(TeleportTypes.LUNAR, 3, 202, new Animation(714), new Graphic(308, 4, 100), new Animation(715), new Graphic(308, 4, 100)),
	LEVER(TeleportTypes.LEVER, 3, 202, new Animation(714), new Graphic(308, 4, 100), new Animation(715), new Graphic(308, 4, 100)),
	OBELISK(TeleportTypes.OBELISK, 3, 202, new Animation(1816), new Graphic(661, 0, 100), new Animation(715), new Graphic(65535, 0, 100)),
	TELETAB(TeleportTypes.TABLET, 3, 202, new Animation(4731), new Graphic(678, 0, 0), new Animation(65535), new Graphic(65535, 0, 0));
	
	private final TeleportTypes type;
	private final int delay;
	private final int sound;
	private final Animation startAnimation;
	private final Graphic startGraphic;
	private final Animation endAnimation;
	private final Graphic endGraphic;
	
	private Teleportation(TeleportTypes type, int delay, int sound, Animation startAnimation, Graphic startGraphic, Animation endAnimation, Graphic endGraphic) {
		this.type = type;
		this.delay = delay;
		this.sound = sound;
		this.startAnimation = startAnimation;
		this.startGraphic = startGraphic;
		this.endAnimation = endAnimation;
		this.endGraphic = endGraphic;
	}
	
	public TeleportTypes getType() {
		return type;
	}

	public int getDelay() {
		return delay;
	}
	
	public int getSound() {
		return sound;
	}
	
	public Animation getStartAnimation() {
		return startAnimation;
	}

	public Graphic getStartGraphic() {
		return startGraphic;
	}

	public Animation getEndAnimation() {
		return endAnimation;
	}

	public Graphic getEndGraphic() {
		return endGraphic;
	}

	public static Teleportation forTeleport(TeleportTypes type) {
		for (final Teleportation data : Teleportation.values()) {
			if (data.type == type) {
				return data;
			}
		}
		return null;
	}



}