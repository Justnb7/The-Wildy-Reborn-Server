package com.model.game.character.combat;

import com.model.game.character.Entity;
import com.model.game.character.combat.PrayerHandler.Prayer;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.item.equipment.Equipment;
import com.model.utility.Utility;

/**
 * Handles the combat's accuracy and max hit formulas.
 * 
 * @author Gabriel | Wolfs Darker
 *
 */
public class CombatFormulas {


    /**
     * Block change modifier.
     */
    public static final double DEFENCE_MODIFIER = 0.895;

    public static final int THICK_SKIN = 0, BURST_OF_STRENGTH = 1, CLARITY_OF_THOUGHT = 2, SHARP_EYE = 3, MYSTIC_WILL = 4, ROCK_SKIN = 5,
        SUPERHUMAN_STRENGTH = 6, IMPROVED_REFLEXES = 7, RAPID_RESTORE = 8, RAPID_HEAL = 9, PROTECT_ITEM = 10, HAWK_EYE = 11,
        MYSTIC_LORE = 12, STEEL_SKIN = 13, ULTIMATE_STRENGTH = 14, INCREDIBLE_REFLEXES = 15, PROTECT_FROM_MAGIC = 16,
        PROTECT_FROM_MISSILES = 17, PROTECT_FROM_MELEE = 18, EAGLE_EYE = 19, MYSTIC_MIGHT = 20, RETRIBUTION = 21, REDEMPTION = 22,
        SMITE = 23, CHIVALRY = 24, PIETY = 25;

    /**
     * Attacks.
     */
    public static final int STAB_ATTACK = 0, SLASH_ATTACK = 1, CRUSH_ATTACK = 2, RANGE_ATTACK = 4, MAGIC_ATTACK = 3;

    /**
     * Defence.
     */
    public static final int STAB_DEF = 5, SLASH_DEF = 6, CRUSH_DEF = 7, RANGE_DEF = 9, MAGIC_DEF = 8;

    /**
     * Misc
     */
    public static final int STRENGTH_BONUS = 10;

    private static int npcDef(Npc npc, int att_type) {
        if (att_type == 0)
            return npc.getDefinition().getMeleeDefence();
        else if (att_type == 1)
            return npc.getDefinition().getRangedDefence();
        else if (att_type == 2)
            return npc.getDefinition().getMagicDefence();
        throw new IllegalStateException();
    }

    /**
     * Calculates the accuracy between the attacker and the target.
     * 
     * @param attacker
     * @param target
     * @param att_type
     * @param additionalSpecMulti TODO
     * @return
     */
    public static boolean getAccuracy(Entity attacker, Entity target, int att_type, double additionalSpecMulti) {

        int att_curr_attack = attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevel(Skills.ATTACK);
        int att_curr_range = attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevel(Skills.RANGE);
        int att_curr_magic = attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevel(Skills.MAGIC);

        int tar_curr_magic = target.isNPC() ? target.toNPC().getDefinition().getMagicDefence() : target.toPlayer().getSkills().getLevel(Skills.MAGIC);
        int tar_curr_defence = target.isNPC() ? npcDef(target.toNPC(), att_type) : target.toPlayer().toPlayer().getSkills().getLevel(Skills.DEFENCE);

        int att_base_attack = attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevelForExperience(Skills.ATTACK);
        int att_base_range = attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevelForExperience(Skills.RANGE);
        int att_base_magic = attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevelForExperience(Skills.MAGIC);

        double att_prayer_bonus = 1.0;
        double att_style_bonus = 0;
        double att_spec_bonus = 1;

        double att_spell_bonus = 0;
        double att_weapon_bonus = 0;
        double att_additional_bonus = 1;
        double att_effective_attack = 0;
        double att_equipment_bonus = 1;
        double augmented_attack = 0;
        double att_hit_chance = 0;
        double att_void_bonus = 1;

        double tar_prayer_bonus = 1.0;
        double tar_style_bonus = 0;

        double tar_equipment_bonus = 1;
        double augmented_defence = 0;
        double tar_block_chance = 0;

        double tar_effective_defence = 0;
        
        double repBonus = 1;

        if (attacker.isPlayer()) {
            Player p = attacker.toPlayer();

            switch (att_type) {
            
            case 0:
            	if (p.isActivePrayer(Prayer.CLARITY_OF_THOUGHT)) {
                    att_prayer_bonus += 0.05;
                }

                if (p.isActivePrayer(Prayer.IMPROVED_REFLEXES)) {
                    att_prayer_bonus += 0.1;
                }

                if (p.isActivePrayer(Prayer.INCREDIBLE_REFLEXES)) {
                    att_prayer_bonus += 0.15;
                }

                if (p.isActivePrayer(Prayer.CHIVALRY)) {
                    att_prayer_bonus += 0.15;
                }

                if (p.isActivePrayer(Prayer.PIETY)) {
                    att_prayer_bonus += 0.2;
                }

                att_equipment_bonus = p.playerBonus[p.getAttackStyle() <= 1 ? p.getAttackStyle() : 1];
                att_void_bonus = Equipment.wearingFullVoid(p, att_type) ? 1.1 : 1;
                break;
            case 1:
            	 if (p.isActivePrayer(Prayer.SHARP_EYE)) {
                     att_prayer_bonus += 0.05;
                 }

                 if (p.isActivePrayer(Prayer.HAWK_EYE)) {
                     att_prayer_bonus += 0.1;
                 }

                 if (p.isActivePrayer(Prayer.EAGLE_EYE)) {
                     att_prayer_bonus += 0.15;
                 }

                att_equipment_bonus = p.playerBonus[4];
                att_void_bonus = Equipment.wearingFullVoid(p, att_type) ? 1.1 : 1;
                break;
            case 2:
            	 if (p.isActivePrayer(Prayer.MYSTIC_WILL)) {
                     att_prayer_bonus += 0.05;
                 }

                 if (p.isActivePrayer(Prayer.MYSTIC_LORE)) {
                     att_prayer_bonus += 0.1;
                 }

                 if (p.isActivePrayer(Prayer.MYSTIC_MIGHT)) {
                     att_prayer_bonus += 0.15;
                 }

                att_spell_bonus += ((att_base_magic - p.MAGIC_SPELLS[p.oldSpellId][1]) * 0.3);

                att_equipment_bonus = p.playerBonus[3];
                att_void_bonus = Equipment.wearingFullVoid(p, att_type) ? 1.3 : 1;
                break;
                
            default:
                break;
            }

            if (p.playerEquipment[3] > 0 && att_type != 2) {
                att_weapon_bonus += (((att_type == 0 ? att_base_attack : att_base_range) - (att_type == 0 ? p.attackLevelReq  : p.rangeLevelReq)) * 0.3);
            }

            if (att_type != 2) {
                att_style_bonus = p.getAttackStyle() == 2 ? 1 : p.getAttackStyle() == 1 ? 3 : 0;
            }

        }

        switch (att_type) {
        case 0:
            att_effective_attack = Math
                .floor(((att_curr_attack * att_prayer_bonus) * att_additional_bonus) + att_style_bonus + att_weapon_bonus);
            break;
        case 1:
            att_effective_attack = Math
                .floor(((att_curr_range * att_prayer_bonus) * att_additional_bonus) + att_style_bonus + att_weapon_bonus);
            break;
        case 2:
            att_effective_attack = Math.floor(((att_curr_magic * att_prayer_bonus) * att_additional_bonus) + att_spell_bonus);
            break;
        default:
            break;
        }

        if (target.isPlayer()) {

            Player t = target.toPlayer();

            if (t.isActivePrayer(Prayer.THICK_SKIN)) {
                tar_prayer_bonus += 0.05;
            }

            if (t.isActivePrayer(Prayer.ROCK_SKIN)) {
                tar_prayer_bonus += 0.1;
            }

            if (t.isActivePrayer(Prayer.STEEL_SKIN)) {
                tar_prayer_bonus += 0.15;
            }

            if (t.isActivePrayer(Prayer.CHIVALRY)) {
                tar_prayer_bonus += 0.2;
            }

            if (t.isActivePrayer(Prayer.PIETY)) {
                tar_prayer_bonus += 0.25;
            }

            tar_style_bonus = t.getAttackStyle() == 2 ? 1 : t.getAttackStyle() == 3 ? 3 : 0;

            switch (att_type) {
            case 0:
                tar_equipment_bonus = t.playerBonus[attacker.isPlayer() ? attacker.toPlayer().getAttackStyle() <= 1
                    ? attacker.toPlayer().getAttackStyle() + 5 : 6 : 6];
                break;
            case 1:
                tar_equipment_bonus = t.playerBonus[9];
                break;
            case 2:
                tar_equipment_bonus = t.playerBonus[8];
                break;
            default:
                break;
            }
        }

        switch (att_type) {
        case 0:
            tar_effective_defence = Math.floor(((tar_curr_defence * tar_prayer_bonus) + tar_style_bonus));
            break;
        case 1:
            tar_effective_defence = Math.floor(((tar_curr_defence * tar_prayer_bonus) + tar_style_bonus));
            break;
        case 2:
            tar_effective_defence = Math.floor(((tar_curr_defence * tar_prayer_bonus) * 0.3)) + (Math.floor(tar_curr_magic * 0.7));
            break;
        default:
            break;
        }

        augmented_attack = Math.floor((((att_effective_attack + 8) * (att_equipment_bonus + 64)) / 10));

        augmented_defence = Math.floor((((tar_effective_defence + 8) * (tar_equipment_bonus + 64)) / 10));

        double hit_chance = 0;

        if (augmented_attack < augmented_defence) {
            hit_chance = ((augmented_attack - 1) / (augmented_defence * 2));
        } else {
            hit_chance = 1 - ((augmented_defence + 1) / (augmented_attack * 2));
        }

        switch (att_type) {
        case 0:
            if (target.isPlayer() && target.toPlayer().isActivePrayer(Prayer.PROTECT_FROM_MELEE)) {
                att_hit_chance = Math.floor((((hit_chance * att_spec_bonus) * att_void_bonus) * 0.6) * 100);
                tar_block_chance = Math.floor(101 - ((((hit_chance * att_spec_bonus) * att_void_bonus) * 0.6) * 100));
            } else {
                att_hit_chance = Math.floor(((hit_chance * att_spec_bonus) * att_void_bonus) * 100);
                tar_block_chance = Math.floor(101 - (((hit_chance * att_spec_bonus) * att_void_bonus) * 100));
            }
            break;
        case 1:
            if (target.isPlayer() && target.toPlayer().isActivePrayer(Prayer.PROTECT_FROM_MISSILE)) {
                att_hit_chance = Math.floor((((hit_chance * att_spec_bonus) * att_void_bonus) * 0.6) * 100);
                tar_block_chance = Math.floor(101 - ((((hit_chance * att_spec_bonus) * att_void_bonus) * 0.6) * 100));
            } else {
                att_hit_chance = Math.floor(((hit_chance * att_spec_bonus) * att_void_bonus) * 100);
                tar_block_chance = Math.floor(101 - (((hit_chance * att_spec_bonus) * att_void_bonus) * 100));
            }
            break;
        case 2:
            if (target.isPlayer() && target.toPlayer().isActivePrayer(Prayer.PROTECT_FROM_MAGIC)) {
                att_hit_chance = Math.floor(((hit_chance * att_void_bonus) * 0.6) * 100);
                tar_block_chance = Math.floor(101 - ((((hit_chance * att_spec_bonus) * att_void_bonus) * 0.6) * 100));
            } else {
                att_hit_chance = Math.floor((hit_chance * att_void_bonus) * 100);
                tar_block_chance = Math.floor(101 - (((hit_chance * att_spec_bonus) * att_void_bonus) * 100));
            }
            break;
        default:
            break;
        }
        att_hit_chance = Utility.getRandom((int) (att_hit_chance + 20 + (!attacker.isNPC() ? attacker.toPlayer().isUsingSpecial() ? 20 * additionalSpecMulti : 0 : 0)));
        tar_block_chance = Utility.getRandom((int) tar_block_chance);
        //System.out.println("additionalSpecMulti "+additionalSpecMulti);
        return (int) att_hit_chance > (int) tar_block_chance * repBonus;
    }

}