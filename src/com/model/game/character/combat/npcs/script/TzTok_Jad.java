package com.model.game.character.combat.npcs.script;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.npcs.AbstractBossCombat;

public class TzTok_Jad extends AbstractBossCombat {

	public TzTok_Jad(int npcId) {
		super(npcId);
	}
	
	/**
	 * The ranged animation.
	 */
	private static final Animation RANGE_ANIMATION = Animation.create(2652);

	/**
	 * The range gfx.
	 */
	private static final Graphic RANGE_GFX = Graphic.create(451);

	/**
	 * The range end gfx.
	 */
	private static final Graphic RANGE_END_GFX = Graphic.create(157);

	/**
	 * The melee animation.
	 */
	private static final Animation MELEE_ANIMATION = Animation.create(2655);

	/**
	 * The magic animation.
	 */
	private static final Animation MAGIC_ANIMATION = Animation.create(2656);

	/**
	 * The magic gfx.
	 */
	private static final Graphic MAGIC_GFX = Graphic.create(448);

	private enum CombatStyle {
		MELEE,

		MAGIC,

		RANGE
	}

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return;
		}
		
		CombatStyle style = CombatStyle.MAGIC;
        CombatStyle[] styles = {CombatStyle.MAGIC, CombatStyle.RANGE, CombatStyle.MELEE};
	}

	@Override
	public int distance(Entity attacker) {
		return 8;
	}

}
