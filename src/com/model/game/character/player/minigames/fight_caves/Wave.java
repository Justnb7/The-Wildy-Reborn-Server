package com.model.game.character.player.minigames.fight_caves;

/**
 * The fight caves minigame waves data
 * 
 * @author Patrick van Elderen
 *
 */
public class Wave {
	
	public static final int TZ_KIH =  2189, TZ_KEK_SPAWN = 2191, TZ_KEK = 2192,
			TOK_XIL = 2193, YT_MEJKOT = 3124, KET_ZEK = 3125, TZTOK_JAD = 3127,
			YTHURKOT = 3128;

	public static final int[][] WAVES = { { TZ_KIH }, { TZ_KIH, TZ_KIH },
			{ TZ_KEK }, { TZ_KEK, TZ_KIH }, { TZ_KEK, TZ_KIH, TZ_KIH },
			{ TZ_KEK, TZ_KEK }, { TOK_XIL }, { TOK_XIL, TZ_KIH },
			{ TOK_XIL, TZ_KIH, TZ_KIH }, { TOK_XIL, TZ_KEK },
			{ TOK_XIL, TZ_KEK, TZ_KIH }, { TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH },
			{ TOK_XIL, TZ_KEK, TZ_KEK }, { TOK_XIL, TOK_XIL }, { YT_MEJKOT },
			{ YT_MEJKOT, TZ_KIH }, { YT_MEJKOT, TZ_KIH, TZ_KIH },
			{ YT_MEJKOT, TZ_KEK }, { YT_MEJKOT, TZ_KEK, TZ_KIH },
			{ YT_MEJKOT, TZ_KEK, TZ_KIH, TZ_KIH },
			{ YT_MEJKOT, TZ_KEK, TZ_KEK }, { YT_MEJKOT, TOK_XIL },
			{ YT_MEJKOT, TOK_XIL, TZ_KIH },
			{ YT_MEJKOT, TOK_XIL, TZ_KIH, TZ_KIH },
			{ YT_MEJKOT, TOK_XIL, TZ_KEK },
			{ YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH },
			{ YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH },
			{ YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KEK },
			{ YT_MEJKOT, TOK_XIL, TOK_XIL }, { YT_MEJKOT, YT_MEJKOT },
			{ KET_ZEK }, { KET_ZEK, TZ_KIH }, { KET_ZEK, TZ_KIH, TZ_KIH },
			{ KET_ZEK, TZ_KEK }, { KET_ZEK, TZ_KEK, TZ_KIH },
			{ KET_ZEK, TZ_KEK, TZ_KIH, TZ_KIH }, { KET_ZEK, TZ_KEK, TZ_KEK },
			{ KET_ZEK, TOK_XIL }, { KET_ZEK, TOK_XIL, TZ_KIH },
			{ KET_ZEK, TOK_XIL, TZ_KIH, TZ_KIH }, { KET_ZEK, TOK_XIL, TZ_KEK },
			{ KET_ZEK, TOK_XIL, TZ_KEK, TZ_KIH },
			{ KET_ZEK, TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH },
			{ KET_ZEK, TOK_XIL, TZ_KEK, TZ_KEK },
			{ KET_ZEK, TOK_XIL, TOK_XIL }, { KET_ZEK, YT_MEJKOT },
			{ KET_ZEK, YT_MEJKOT, TZ_KIH },
			{ KET_ZEK, YT_MEJKOT, TZ_KIH, TZ_KIH },
			{ KET_ZEK, YT_MEJKOT, TZ_KEK },
			{ KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KIH },
			{ KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KIH, TZ_KIH },
			{ KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KEK },
			{ KET_ZEK, YT_MEJKOT, TOK_XIL },
			{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KIH },
			{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KIH, TZ_KIH },
			{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK },
			{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH },
			{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH },
			{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KEK },
			{ KET_ZEK, YT_MEJKOT, TOK_XIL, TOK_XIL },
			{ KET_ZEK, YT_MEJKOT, YT_MEJKOT }, { KET_ZEK, KET_ZEK },
			{ TZTOK_JAD } };
	
	private int stage;

	public void set(int stage) {
		this.stage = stage;
	}

	public int[] spawns() {
		return WAVES[stage];
	}

	public int getStage() {
		return stage;
	}

}
