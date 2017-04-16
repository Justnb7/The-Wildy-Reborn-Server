package com.model.game.character.combat.weapon;

import com.model.game.character.player.Player;
import com.model.game.item.container.impl.Equipment;

/**
 * The class which represents functionality for the attack style.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @date 13-4-2016, edited on 1-7-2016
 */
public class AttackStyle {
	
	public static final int ACCURATE = 0, AGGRESSIVE = 1, DEFENSIVE = 2, CONTROLLED = 3/*, RANGE_ACCURATE = 4, RANGE_RAPID = 5, RANGE_LONGRANGE = 6*/;
	
	
	public enum FightType {
		
		UNARMED_PUNCH(43, 0, 22228, ACCURATE),
		UNARMED_KICK(43, 1, 22230, AGGRESSIVE),
		UNARMED_BLOCK(43, 2, 22229, DEFENSIVE),
		SCIMITAR_CHOP(43, 0, 9125, ACCURATE),
		SCIMIRAR_SLASH(43, 1, 9128, AGGRESSIVE),
		SCIMITAR_LUNGE(43, 2, 9127, CONTROLLED),
		SCIMITAR_BLOCK(43, 3, 9126, DEFENSIVE),
		MACE_POUND(43, 0, 14218, ACCURATE),
		MACE_PUMMEL(43, 1, 14221, AGGRESSIVE),
		MACE_SPIKE(43, 2, 14220, CONTROLLED),
		MACE_BLOCK(43, 3, 14219, DEFENSIVE),
		GRANITE_MAUL_POUND(43, 0, 1177, ACCURATE),
		GRANITE_MAUL_PUMMEL(43, 1, 1176, AGGRESSIVE),
		GRANITE_MAUL_BLOCK(43, 2, 1175, DEFENSIVE),
		STAFF_BASH(43, 0, 1080, ACCURATE),
		STAFF_POUND(43, 1, 1079, AGGRESSIVE),
		STAFF_FOCUS(43, 2, 1078, DEFENSIVE),
		SWORD_STAB(43, 0, 8234, ACCURATE),
		SWORD_LUNGE(43, 1, 8237, AGGRESSIVE),
		SWORD_SLASH(43, 2, 8236, AGGRESSIVE),
		SWORD_BLOCK(43, 3, 8235, DEFENSIVE),
		WHIP_FLICK(43, 0, 48010, ACCURATE),
		WHIP_LASH(43, 1, 48009, CONTROLLED),
		WHIP_DEFLECT(43, 2, 48008, DEFENSIVE),
		SARADOMIN_SWORD_CHOP(43, 0, 18103, ACCURATE),
		SARADOMIN_SWORD_SLASH(43, 1, 18106, AGGRESSIVE),
		SARADOMIN_SWORD_SMASH(43, 2, 18105, AGGRESSIVE),
		SARADOMIN_SWORD_BLOCK(43, 3, 18104, DEFENSIVE),
		GODSWORD_CHOP(43, 0, 18103, ACCURATE),
		GODSWORD_SLASH(43, 1, 18106, AGGRESSIVE),
		GODSWORD_SMACK(43, 2, 18105, AGGRESSIVE),
		GODSWORD_BLOCK(43, 3, 18104, DEFENSIVE),
		HALBERD_JAB(43, 0, 33018, ACCURATE),
		HALBERD_SWIPE(43, 1, 33020, AGGRESSIVE),
		HALBERD_FEND(43, 2, 33019, DEFENSIVE),
		DART_ACCURATE(43, 0, 17102, ACCURATE),
		DART_RAPID(43, 1, 17101, AGGRESSIVE),
		DART_LONGRANGE(43, 2, 17100, CONTROLLED),
		BOW_ACCURATE(43, 0, 6236, ACCURATE),
		BOW_RAPID(43, 1, 6235, AGGRESSIVE),
		BOW_LONGRANGE(43, 2, 6234, CONTROLLED),
		GREATAXE_CHOP(43, 0, 6168, ACCURATE),
		GREATAXE_HACK(43, 1, 6171, AGGRESSIVE),
		GREATAZE_SMASH(43, 2, 6170, AGGRESSIVE),
		GREATAXE_BLOCK(43, 3, 6169, DEFENSIVE),
		SPEAR_LUNGE(43, 0, 18077, ACCURATE),
		SPEAR_SWIPE(43, 1, 18080, AGGRESSIVE),
		SPEAR_POUND(43, 2, 18079, CONTROLLED),
		SPEAR_BLOCK(43, 3, 18078, DEFENSIVE),
		DAGGER_STAB(43, 0, 8234, ACCURATE),
	    DAGGER_LUNGE(43, 1, 8237, AGGRESSIVE),
	    DAGGER_SLASH(43, 2, 8236, AGGRESSIVE),
	    DAGGER_BLOCK(43, 3, 8235, DEFENSIVE),
	    CLAWS_CHOP(43, 0, 30088, ACCURATE),
	    CLAWS_SLASH(43, 1, 30091, AGGRESSIVE),
	    CLAWS_LUNGE(43, 2, 30090, CONTROLLED),
	    CLAWS_BLOCK(43, 3, 30089, DEFENSIVE);
		// must be some missing then 
		//check

	    /**
	     * The parent config identification.
	     */
	    private final int parent;

	    /**
	     * The child config identification.
	     */
	    private final int child;

	    /**
	     * The buttonId.
	     */
	    private final int button;
	    
	    /**
	     * The style active when this type is active.
	     */
	    private final int style;

	    /**
	     * Creates a new {@link FightType}.
	     *
	     * @param parent
	     *            the parent config identification.
	     * @param child
	     *            the child config identification.
	     * @param button
	     *            the type of button this type will apply.
	     * @param style
         *            the style active when this type is active.
	     */
	    private FightType(int parent, int child, int button, int style) {
	        this.parent = parent;
	        this.child = child;
	        this.button = button;
	        this.style = style;
	    }
	    
	    /**
	     * Gets the parent config identification.
	     *
	     * @return the parent config.
	     */
	    public final int getParent() {
	        return parent;
	    }

	    /**
	     * Gets the child config identification.
	     *
	     * @return the child config.
	     */
	    public final int getChild() {
	        return child;
	    }

	    /**
	     * Gets the clicked button
	     *
	     * @return the button.
	     */
	    public final int getButton() {
	        return button;
	    }
	    
	    /**
	     * Gets the style active when this type is active.
	     *
	     * @return the fighting style.
	     */
	    public final int getStyle() {
	        return style;
	    }
	    
	    public static boolean setStyle(Player player, int buttonId) {
	    	
	    	for(FightType type : FightType.values()) {
	    		if (type.getButton() == buttonId) {
                    player.setAttackStyleConfig(type.getChild());
                    player.setAttackStyle(type.getStyle());
                    player.getActionSender().sendConfig(type.getParent(), type.getChild());
                    //player.write(new SendMessagePacket("Setting config ID: "+type.getParent()+" to child id: "+type.getChild()+" attack style: "+type.getStyle()));
                    return true;
                }
	    		//return false;
	    	}
	    	return false;
	    }
	}

	/**
	 * Adjusts the player's attack style.
	 */
	public static void adjustAttackStyle(Player player) {
		if (player.getEquipment().getId(Equipment.WEAPON_SLOT) == -1) {
			switch(player.getAttackStyleConfig()) {
			case 0:
				 player.getActionSender().sendConfig(43, 0);
				break;
			case 1:
				player.getActionSender().sendConfig(43, 1);
				break;
			case 2:
				player.getActionSender().sendConfig(43, 2);
				break;
			}
		} else if(player.getEquipment().getId(Equipment.WEAPON_SLOT) > 0) {
			switch(player.getAttackStyle()) {
			case 0:
				 player.getActionSender().sendConfig(43, 0);
				break;
			case 1:
				player.getActionSender().sendConfig(43, 1);
				break;
			case 2:
				player.getActionSender().sendConfig(43, 2);
				break;
			case 3:
				player.getActionSender().sendConfig(43, 3);
				break;
			}
		} else {
			player.getActionSender().sendConfig(43, 0);
		}
	}

	/**
	 * Adjusts the attack style upon login.
	 * 
	 * @param player
	 */
	public static void adjustAttackStyleOnLogin(Player player) {
		if (player.getEquipment().getId(Equipment.WEAPON_SLOT) == -1) {
			player.setAttackStyle(0);
			player.getActionSender().sendConfig(43, 0);
		} else {
			adjustAttackStyle(player);
		}
	}
}