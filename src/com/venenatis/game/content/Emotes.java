package com.venenatis.game.content;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Skills.SkillCape;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;

/**
 * 
 * @author Mack
 *
 */
public class Emotes {

	/**
	 * Checks if the player can use an emote
	 * 
	 * @param player
	 * @return
	 */
	private static boolean canEmote(Player player) {
		if (player.getCombatState().isDead()) {
			return false;
		}
		if (!player.canEmote()) {
			player.getActionSender().sendMessage("You can't perform that emote right now.");
			return false;
		}
		return true;
	}

	/**
	 * Executes the emote
	 * 
	 * @param player
	 * @param button
	 */
	public static void execute(Player player, int button) {
		if (!canEmote(player)) {
			return;
		}

		Task emotes = new Task(2) {
			@Override
			public void execute() {
				Emote emote = Emote.forId(button);
				if (emote != null) {
					if (emote.getAnimation() != null) {
						player.playAnimation(emote.getAnimation());
					}
					if (emote.getGraphic() != null) {
						player.playGraphic(emote.getGraphic());
					}
					player.setEmote(true);
				} else {
					switch (button) {
					case 74108:
						if (player.getEquipment().get(EquipmentConstants.CAPE_SLOT) == null) {
							player.setEmote(true);
							player.getActionSender().sendMessage("You need to be wearing a skillcape in order to perform that emote.");
							return;
						}
						SkillCape skillCape = SkillCape.forId(player.getEquipment().get(EquipmentConstants.CAPE_SLOT));
						if (skillCape == null) {
							player.setEmote(true);
							player.getActionSender().sendMessage("You need to be wearing a skillcape in order to perform that emote.");
						} else {
							if (skillCape.getAnimation() != null) {
								player.playAnimation(skillCape.getAnimation());
							}
							if (skillCape.getGraphic() != null) {
								player.playGraphic(skillCape.getGraphic());
							}
							World.getWorld().schedule(new Task(skillCape.getAnimateTimer()) {
								@Override
								public void execute() {
									player.setEmote(true);
									this.stop();
								}
							});
						}
						break;

					}
				}
				this.stop();
			}
		};
		player.setEmote(false);
		if (player.getWalkingQueue().isMoving()) {
			player.getWalkingQueue().reset();
			World.getWorld().schedule(emotes);
		} else {
			emotes.execute();
		}
	}

	/**
	 * A {@link enum} storing all supported emotes a player can perform within the "emote tab". It stores the animation - graphic
	 * respectively.
	 * @author Mack
	 *
	 */
	public enum Emote {

		YES(168, Animation.create(855), null),

		NO(169, Animation.create(856), null),

		BOW(164, Animation.create(858), null),

		ANGRY(167, Animation.create(859), null),

		THINK(162, Animation.create(857), null),

		WAVE(163, Animation.create(863), null),

		SHRUG(52058, Animation.create(2113), null),

		CHEER(171, Animation.create(862), null),

		BECKON(165, Animation.create(864), null),

		LAUGH(170, Animation.create(861), null),

		JUMP_FOR_JOY(52054, Animation.create(2109), null),

		YAWN(52056, Animation.create(2111), null),

		DANCE(166, Animation.create(866), null),

		JIG(52051, Animation.create(2106), null),

		SPIN(52052, Animation.create(2107), null),

		HEADBANG(52053, Animation.create(2108), null),

		CRY(161, Animation.create(860), null),

		BLOW_KISS(43092, Animation.create(1368), Graphic.create(574)),

		PANIC(52050, Animation.create(2105), null),

		RASPBERRY(52055, Animation.create(2110), null),

		CLAP(172, Animation.create(865), null),

		SALUTE(52057, Animation.create(2112), null),

		GOBLIN_BOW(52071, Animation.create(2127), null),

		GOBLIN_SALUTE(52072, Animation.create(2128), null),

		GLASS_BOX(2155, Animation.create(1131), null),

		CLIMB_ROPE(25103, Animation.create(1130), null),

		LEAN(25106, Animation.create(1129), null),

		GLASS_WALL(2154, Animation.create(1128), null),

		SLAP_HEAD(31, Animation.create(4275), null),

		STOMP(88061, Animation.create(4278), null),

		FLAP(88062, Animation.create(4280), null),

		IDEA(88060, Animation.create(4276), Graphic.create(712)),

		ZOMBIE_WALK(72032, Animation.create(3544), null),

		ZOMBIE_DANCE(72033, Animation.create(3543), null),

		SCARED(59062, Animation.create(2836), null),

		BUNNY_HOP(72254, Animation.create(6111), null),
		
		ZOMBIE_HAND(88065, Animation.create(111), null), //TODO
		
		
		;//end of enum

		private int button;

		private Animation animation;

		private Graphic graphic;

		public static Emote forId(int button) {
			for (Emote emote : Emote.values()) {
				if (emote.getButton() == button) {
					return emote;
				}
			}
			return null;
		}

		private Emote(int button, Animation animation, Graphic graphic) {
			this.button = button;
			this.animation = animation;
			this.graphic = graphic;
		}

		/**
		 * @return the button
		 */
		public int getButton() {
			return button;
		}

		/**
		 * @return the animation
		 */
		public Animation getAnimation() {
			return animation;
		}

		/**
		 * @return the graphic
		 */
		public Graphic getGraphic() {
			return graphic;
		}
	}

}
