package com.venenatis.game.content.emotes;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.emotes.impl.CrazyDanceEvent;
import com.venenatis.game.content.emotes.impl.SmoothDanceEvent;
import com.venenatis.game.content.emotes.impl.UriTransformEvent;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.world.World;

public enum Emotes {

	YES(168, 855, -1),
	NO(169, 856, -1),
	BOW(164, 858, -1),
	ANGRY(165, 864, -1),
	THINK(162, 857, -1),
	WAVE(163, 863, -1),
	SHRUG(52058, 2113, -1),
	CHEER(171, 862, -1),
	BECKON(167, 859, -1),
	LAUGH(170, 861, -1),
	JUMP_FOR_JOY(52054, 2109, -1),
	YAWN(52056, 2111, -1),
	DANCE(166, 866, -1),
	JIG(52051, 2106, -1),
	TWIRL(52052, 2107, -1),
	HEADBANG(52053, 2108, -1),
	CRY(161, 860, -1),
	BLOW_KISS(43092, 1368, 574),
	PANIC(52050, 2105, -1),
	RASPBERRY(52055, 2110, -1),
	CLAP(172, 865, -1),
	SALUTE(52057, 2112, -1),
	GOBLIN_BOW(52071, 2127, -1),
	GOBLON_SALUTE(52072, 2128, -1),
	GLASS_BOX(2155, 1131, -1),
	CLIMB_ROPE(25103, 1130, -1),
	LEAN(25106, 1129, -1),
	GLASS_WALL(2154, 1128, -1),
	IDEA(72252, 4276, 712),
	STAMP(72253, 4278, -1),
	FLAP(72254, 4280, -1),
	SLAP_HEAD(72255, 4275, -1),
	ZOMBIE_WALK(72032, 3544, -1),
	ZOMBIE_DANCE(72033, 3543, -1),
	SCARED(59062, 2836, -1),
	BUNNY_HOP(73000, 6111, -1),
	SIT_UP(73001, 2763, -1),
	PUSH_UP(73002, 2762, -1),
	STAR_JUMP(73003, 2761, -1),
	JOG(73004, 2764, -1),
	ZOMBIE_HAND(73005, 4513, 320),
	HYPERMOBILE_DRINKER(73006, 7131, -1),
	SKILL_CAPE(73007, -1, -1),
	AIR_GUITAR(73008, 4751, 1239),
	URI_TRANSFORM(73009, -1, -1),
	SMOOTH_DANCE(73010, -1, -1),
	CRAZY_DANCE(73011, -1, -1);

	private int buttonId;
	private int animId;
	private int gfxId;

	private Emotes(int buttonId, int animId, int gfxId) {
		this.buttonId = buttonId;
		this.animId = animId;
		this.gfxId = gfxId;
	}

	private static Map<Integer, Emotes> emotesMap = new HashMap<>();

	static {
		for (Emotes def : values()) {
			emotesMap.put(def.buttonId, def);
		}
	}
	
    /**
     * The buttons stored in an array
     */
	public static final int[] emote_button_ids = new int[] { 168, 169, 164, 167, 162, 163, 52058, 171, 165, 170, 52054,
			52056, 166, 52051, 52052, 52053, 161, 43092, 52050, 52055, 172, 52057, 52071, 52072, 2155, 25103, 25106,
			2154, 72252, 72253, 72254, 72255, 72032, 72033, 59062, 73000, 73001, 73002, 73003, 73004, 73005, 73006,
			73007, 73008, 73009, 73010, 73011 };

    /**
     * Are we clicking on an actual emote button
     * 
     * @param player
     *            The player trying to perform an emote
     * @param button
     *            The button being pressed
     */
    public static boolean isEmoteButton(Player player, int button) {
        for (int btn : emote_button_ids) {
            if (button == btn) {
                return true;
            }
        }
        return false;
    }
	
	public static boolean execute(Player player, int buttonId) {		
		
		Emotes def = emotesMap.get(buttonId);
		if (def == null) {
			return false;
		}
		
		if (player.getCombatState().inCombat()) {
			player.getActionSender().sendMessage("You can't perform emotes whilst in combat.");
			return true;
		}
		
		switch (def) {
		
			case SKILL_CAPE:
				SkillCapeEmotes skillDef = SkillCapeEmotes.getSkillCapeEmote(player.getEquipment().get(EquipmentConstants.CAPE_SLOT).getId());
				if (skillDef == null) {
					player.getActionSender().sendMessage("You need to be wearing a skillcape to do this.");
					return false;
				}
				player.playAnimation(Animation.create(skillDef.getAnimId()));
				if (skillDef.getGfxId() != -1)
					player.playGraphic(Graphic.create(skillDef.getGfxId()));
				return true;
		
			case AIR_GUITAR:
				
				if (!player.getEquipment().contains(13221) && !player.getEquipment().contains(13222)) {
					player.getActionSender().sendMessage("You need to be wearing a music cape to do this.");
				} else {
					player.playAnimation(Animation.create(def.getAnimId()));
					player.playGraphic(Graphic.create(def.getGfxId()));
				}
				return true;
		
			case URI_TRANSFORM:
				World.getWorld().schedule(new UriTransformEvent(player));
				return true;
				
			case SMOOTH_DANCE:
				World.getWorld().schedule(new SmoothDanceEvent(player));
				return true;
				
			case CRAZY_DANCE:
				World.getWorld().schedule(new CrazyDanceEvent(player));
				return true;
		
			default:
				if (def.getAnimId() != -1) {
					player.playAnimation(Animation.create(def.getAnimId()));
				}
				if (def.getGfxId() != -1) {
					player.playGraphic(Graphic.create(def.getGfxId()));
				}				
				return true;
		}
	}
	
	public static void update(Player player) {
		Item capeId = player.getEquipment().get(EquipmentConstants.CAPE_SLOT);
		
		if(capeId == null) {
			return;
		}

		player.getActionSender().sendConfig(18695, SkillCapeEmotes.getSkillCapeEmote(capeId.getId()) != null ? 1 : 0);
		player.getActionSender().sendConfig(18696, (capeId.getId() == 13221 || capeId.getId() == 13222) ? 1 : 0);
		
		//Uri transform TODO
		player.getActionSender().sendConfig(18697, 1);
	}
	
	public int getAnimId() {
		return animId;
	}

	public int getGfxId() {
		return gfxId;
	}

}