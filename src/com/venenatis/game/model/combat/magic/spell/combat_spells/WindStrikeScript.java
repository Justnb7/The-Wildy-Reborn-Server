package com.venenatis.game.model.combat.magic.spell.combat_spells;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.magic.spell.AbstractSpellScript;
import com.venenatis.game.model.combat.magic.spell.Spells.Spell;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;

public class WindStrikeScript extends AbstractSpellScript {

	@Override
	public void execute(Entity attacker, Entity victim, Spell spell, boolean autocast, int clientSpeed, int gfxDelay, int delay) {
		attacker.playAnimation(Animation.create(autocast ? 1162 : 711));
		attacker.playGraphics(Graphic.create(90, 0, 100));
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 91, 60, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 15, 48));
		//action.hitEnemy(attacker, victim, spell, Graphic.create(92, gfxDelay, 100), PoisonType.NONE, false, 2, delay, 0);
		attacker.asPlayer().getSkills().addExperience(Skills.MAGIC, Constants.EXP_MODIFIER * 5.5);
		attacker.getCombatState().setAttackDelay(4);
	}

}