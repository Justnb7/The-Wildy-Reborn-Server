package com.venenatis.game.constants;

import com.google.common.collect.ImmutableList;
import com.venenatis.game.location.Area;
import com.venenatis.game.location.impl.CircleArea;
import com.venenatis.game.location.impl.SquareArea;

public class GameConstants {
	
	/**
     * The collection of areas that resemble the wilderness area.
     */
    public static final ImmutableList<Area> WILDERNESS = ImmutableList
	    .of(new SquareArea("Wilderness", 2941, 3518, 3392, 3966));

    /**
     * The collection of areas that resemble the duel arena.
     */
    public static final ImmutableList<Area> DUEL_ARENA = ImmutableList
	    .of(new SquareArea("Lobby", 3355, 3262, 3379, 3279),
	    		new SquareArea("Lobby Altar", 3374, 3280,  3379, 3286),
	    		new SquareArea("Lobby east bank", 3380, 3263, 3384, 3273));
    
    public static final ImmutableList<Area> BARROWS_MINIGAME = ImmutableList.of(
    		new CircleArea("Outside circle", 3565, 3289, 0, 20));
    
    public static final ImmutableList<Area> F2P_ARENA = ImmutableList.of(new SquareArea("F2P minigame", 2076, 4459, 2095, 4473));
    
    //absX > 3272 && absX < 3391 && absY > 4759 && absY < 4863
    
    //int swX, int swY, int neX, int neY
    public static final ImmutableList<Area> CLAN_WARS = ImmutableList.of(new SquareArea("Clan wars", 3272, 4759, 4863, 3391));
    
    //absX > 3263 && absX < 3390 && absY > 4735 && absY < 4761
    public static final ImmutableList<Area> CLAN_WARS_SAFE = ImmutableList.of(new SquareArea("Clan wars safe", 3263, 4735, 3390, 4761));

    /**
     * The collection of areas that resemble multi-combat zones.
     */
    public static final ImmutableList<Area> MULTI_COMBAT_ZONES = ImmutableList.of(
	    new SquareArea("Start of Varrock Wilderness", 3134, 3525, 3327, 3607),
	    new SquareArea("North of GE, near gravestones", 3190, 3648, 3327, 3839),
	    new SquareArea("Near Chaos Elemental", 3200, 3840, 3390, 3967),
	    new SquareArea("Near wilderness agility course", 2992, 3912, 3007, 3967),
	    new SquareArea("West wilderness altar", 2946, 3816, 2959, 3831),
	    new SquareArea("Deep wilderness 1", 3008, 3856, 3199, 3903),
	    new SquareArea("Near wilderness castle", 3008, 3600, 3071, 3711),
	    new SquareArea("North of varrock lumbermill", 3072, 3608, 3327, 3647),
	    new SquareArea("Pest control", 2624, 2550, 2690, 2619),
	    new SquareArea("Fight caves", 2371, 5062, 2422, 5117),
	    new SquareArea("Fight arena", 2896, 3595, 2927, 3630),
	    new SquareArea("Dagannoth lair", 2892, 4435, 2932, 4464),
	    new SquareArea("Grand Exchange", 3178, 3502, 3190, 3514),
	    new SquareArea("King Black Dragon lair", 2256, 4680, 2287, 4711));
}
