package com.venenatis.game.constants;

import java.util.Arrays;

import com.google.common.collect.ImmutableList;
import com.venenatis.game.location.Area;
import com.venenatis.game.location.impl.CircleArea;
import com.venenatis.game.location.impl.SquareArea;
import com.venenatis.game.model.Item;
import com.venenatis.game.util.chance.Chance;
import com.venenatis.game.util.chance.WeightedChance;

public class GameConstants {
	
	/**
     * The collection of areas that resemble the wilderness area.
     */
    public static final ImmutableList<Area> WILDERNESS = ImmutableList
	    .of(new SquareArea("Wilderness", 2941, 3524, 3392, 3966), new SquareArea("Wilderness underground", 2941, 9918, 3392, 10366));

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
    
    public static final ImmutableList<Area> CLAN_WARS = ImmutableList.of(new SquareArea("Clan wars", 3272, 4759, 4863, 3391));
    
    public static final ImmutableList<Area> CLAN_WARS_SAFE = ImmutableList.of(new SquareArea("Clan wars safe", 3263, 4735, 3390, 4761));

    public static final ImmutableList<Area> DAGANNOTH_MOTHER = ImmutableList.of(new SquareArea("Dagannoth Mother", 2508, 4631, 2537, 4662));
    
    public static final ImmutableList<Area> IN_JAIL = ImmutableList.of(new SquareArea("Jail", 3009, 3023, 3198, 3203));
    

    /*public static void verifyCoords() {
    	GameConstants dummy = new GameConstants();
    	int total = 0;
    	int[] ok = new int[] {0};
    	for (Field f : GameConstants.class.getDeclaredFields()) {
    		System.out.println(f.getName()+": "+f.getGenericType()+" & "+f.getType());
    		// generic type above is area.. but the raw data is sq areas
    		if (f.getType().equals(ImmutableList.class) && f.getGenericType().equals(ImmutableList.class)) {
    			total++;
    			try {
    				// this cast here probably wont work
					ImmutableList<Area> places = (ImmutableList<Area>) f.get(dummy);
					// ^ loses sqarea by casting to super class
					places.stream().forEach(System.out::println);
					
					places.forEach(p -> {
						if (p instanceof SquareArea) {
							SquareArea sp = (SquareArea) p;
							if (sp.getSwX() > sp.getNeX() || sp.getSwY() > sp.getNeY()) {
								System.out.println("Incorrect bound! of "+f.getName());
							} else {
								ok[0]++; // has to be array (we just access 1st element) because lamda's cry when
								// operating on non-final fields
							}
						}
					});
					System.out.println("Done! "+ok[0]+"/"+total+" fields checked");
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    		}
    	}
    	System.out.println("End  "+ok[0]+"/"+total+"");
    }
    
    public static void main(String[] args) {
    	// testing
    	verifyCoords();
    }*/
    
    /**
     * The collection of areas that resemble safe zones.
     */
    public static final ImmutableList<Area> SAFE_ZONES = ImmutableList.of(
    	new SquareArea("Wilderness castle", 3014, 3631, 3024, 3635),
	    new SquareArea("Varrock West Bank", 3180, 3433, 3190, 3447),
	    new SquareArea("Varrock East Bank Downstairs", 3250, 3416, 3257, 3423),
	    new SquareArea("Varrock East Bank Upstairs", 3250, 3416, 3257, 3423, 1),
	    new CircleArea("Grand Exchange", 3164.5, 3489.5, 0, 17),
	    new SquareArea("Barrows outside house", 3558, 3308, 3581, 3319),
	    new SquareArea("Tzhaar City", 2433, 5123, 2492, 5182),
	    new SquareArea("Near Fight Pits", 2398, 5176, 2426, 5182),
	    new SquareArea("Near Fight Caves", 2424, 5171, 2432, 5175),
	    new SquareArea("Yanille", 2537, 3075, 2618, 3120),
	    new SquareArea("Edgeville", 3073, 3465, 3108, 3518),
	    new SquareArea("Camelot Bank Entrance", 2724, 3487, 2727, 3489),
	    new SquareArea("Camelot Bank Lobby", 2721, 3490, 2730, 3493),
	    new SquareArea("Camelot Bank Teller", 2719, 3494, 2730, 3496),
	    new SquareArea("Catherby Bank", 2806, 3438, 2812, 3445),
	    new SquareArea("Falador West Bank", 2943, 3368, 2947, 3373),
	    new SquareArea("Falador West Bank 2", 2945, 3366, 2949, 3369),
	    new SquareArea("Falador West Bank 3", 2946, 3359, 2948, 3365),
	    new SquareArea("Falador East Bank", 3009, 3353, 3018, 3358),
	    new SquareArea("Falador East Bank 2", 3019, 3353, 3021, 3356));
    
    /**
     * The collection of areas that resemble multi-combat zones.
     */
    public static final ImmutableList<Area> MULTI_COMBAT_ZONES = ImmutableList.of(
    	new SquareArea("Giant mole", 1700, 5100, 1780, 5230),	
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
	    new SquareArea("King Black Dragon lair", 2256, 4680, 2287, 4711),
	    new SquareArea("Scorpia pit", 3216, 10329, 3248, 10354),
	    new SquareArea("Corporeal beast", 2972, 4370, 2999, 4397));

	public static Chance < Item > PVP_LOOTS = new Chance<> (Arrays.asList(
		    new WeightedChance < Item > (10, new Item(4153, 1)), //Granite maul
				
		    new WeightedChance < Item > (1, new Item(4716, 1)), //Dharok's helm
	        new WeightedChance < Item > (1, new Item(4718, 1)), //Dharok's greataxe
	        new WeightedChance < Item > (1, new Item(4720, 1)), //Dharok's platebody
	        new WeightedChance < Item > (1, new Item(4722, 1)), //Dharok's platelegs
	        new WeightedChance < Item > (1, new Item(4724, 1)), //Guthan's helm
	        new WeightedChance < Item > (1, new Item(4726, 1)), //Guthan's warspear
	        new WeightedChance < Item > (1, new Item(4728, 1)), //Guthan's platebody
	        new WeightedChance < Item > (1, new Item(4730, 1)), //Guthan's chainskirt
	        new WeightedChance < Item > (1, new Item(4753, 1)), //Verac's helm
	        new WeightedChance < Item > (1, new Item(4755, 1)), //Verac's flail
	        new WeightedChance < Item > (1, new Item(4757, 1)), //Verac's brassard
	        new WeightedChance < Item > (1, new Item(4759, 1)), //Verac's plateskirt
	        new WeightedChance < Item > (1, new Item(4732, 1)), //Karil's coif
	        new WeightedChance < Item > (1, new Item(4734, 1)), //Karil's crossbow
	        new WeightedChance < Item > (1, new Item(4736, 1)), //Karil's leathertop
	        new WeightedChance < Item > (1, new Item(4738, 1)), //Karil's leatherskirt
	        new WeightedChance < Item > (1, new Item(4708, 1)), //Ahrim's hood
	        new WeightedChance < Item > (1, new Item(4710, 1)), //Ahrim's staff
	        new WeightedChance < Item > (1, new Item(4712, 1)), //Ahrim's robetop
	        new WeightedChance < Item > (1, new Item(4714, 1)), //Ahrim's robeskirt
	        new WeightedChance < Item > (1, new Item(4745, 1)), //Torag's helm
	        new WeightedChance < Item > (1, new Item(4747, 1)), //Torag's hammers
	        new WeightedChance < Item > (1, new Item(4749, 1)), //Torag's platebody
	        new WeightedChance < Item > (1, new Item(4751, 1)), //Torag's platelegs			
			new WeightedChance < Item > (1, new Item(11840, 1)), //Dragon boots        
			
			new WeightedChance < Item > (0.3, new Item(12829, 1)), //Spirit shield
			new WeightedChance < Item > (0.3, new Item(6731, 1)), //Seers ring
			new WeightedChance < Item > (0.3, new Item(6733, 1)), //Archers ring
			new WeightedChance < Item > (0.3, new Item(6735, 1)), //Warrior ring
			new WeightedChance < Item > (0.3, new Item(6585, 1)), //Amulet of fury
			new WeightedChance < Item > (0.3, new Item(11283, 1)), //Dragonfire shield
			new WeightedChance < Item > (0.3, new Item(6737, 1)), //Berserker ring
			new WeightedChance < Item > (0.3, new Item(12926, 1)), //Blowpipe
			new WeightedChance < Item > (0.3, new Item(11335, 1)), //Dragon full helm
			new WeightedChance < Item > (0.3, new Item(11791, 1)), //Staff of the dead
			new WeightedChance < Item > (0.3, new Item(12904, 1)), //Toxic staff of the dead
			new WeightedChance < Item > (0.3, new Item(12899, 1)), //Trident of the swamp
			new WeightedChance < Item > (0.3, new Item(11907, 1)), //Trident of the seas
			new WeightedChance < Item > (0.3, new Item(11235, 1)), //Dark bow
			new WeightedChance < Item > (0.3, new Item(12931, 1)), //Serpentine helm
			new WeightedChance < Item > (0.3, new Item(13199, 1)), //Magma helm
			new WeightedChance < Item > (0.3, new Item(13197, 1)) //Tanzanite helm
		));
}
