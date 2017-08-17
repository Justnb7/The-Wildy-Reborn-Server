package com.venenatis.game.content.teleportation.teletab;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;

/**
 * The teleportation data for teletabs and teleportation scroll
 * @author Patrick van Elderen
 *
 */
public enum TabletData {
	
	VARROCK(new Item(8007), new Location(3210, 3424, 0), false),
	LUMBRIDGE(new Item(8008), new Location(3222, 3218, 0), false),
	FALADOR(new Item(8009), new Location(2964, 3378, 0), false),
	CAMELOT(new Item(8010), new Location(2757, 3477, 0), false),
	ARDOUGNE(new Item(8011), new Location(2662, 3305, 0), false),
	WATCH_TOWER(new Item(8012), new Location(2549, 3112, 0), false),
	HOME(new Item(8013), new Location(3099, 3503, 0), false),
	RIMMINGTON(new Item(11741), new Location(2956, 3223, 0), false),
	TAVERLY(new Item(11742), new Location(2896, 3456, 0), false),
	POLLNIVNEACH(new Item(11743), new Location(3356, 2966, 0), false),
	RELLEKA(new Item(11744), new Location(2669, 3636, 0), false),
	BRIMHAVEN(new Item(11745), new Location(2760, 3178, 0), false),
	YANILLE(new Item(11746), new Location(2544, 3092, 0), false),
	TROLLHEIM(new Item(11747), new Location(2890, 3676, 0), false),
	ANNAKARL(new Item(12775), new Location(3288, 3886, 0), false), 
	CARRALLANGER(new Item(12776), new Location(3156, 3666, 0), false), 
	DAREEYAK(new Item(12777), new Location(2966, 3695, 0), false), 
	GHORROCK(new Item(12778), new Location(2977, 3873, 0), false), 
	KHARYRLL(new Item(12779), new Location(3492, 3471, 0), false), 
	LASSAR(new Item(12780), new Location(3006, 3471, 0), false), 
	PADDEWWA(new Item(12781), new Location(3098, 9884, 0), false), 
	SENNTISTEN(new Item(12782), new Location(3322, 3336, 0), false), 
	NARDAH(new Item(12402), new Location(3423, 2914, 0), true),
	DIGSITE(new Item(12403), new Location(3350, 3344, 0), true),
	FELDIP_HILLS(new Item(12404), new Location(2556, 2982, 0), true),
	LUNAR_ISLE(new Item(12405), new Location(2108, 3914, 0), true),
	MORTON(new Item(12406), new Location(3487, 3284, 0), true),
	PEST_CONTROL(new Item(12407), new Location(2662, 2647, 0), true),
	PISCATORIS(new Item(12408), new Location(2342, 3692, 0), true),
	TAI_BWO_WANNAI(new Item(12409), new Location(2790, 3065, 0), true),
	ELF_CAMP(new Item(12410), new Location(2195, 3253, 0), true),
	MOSLE_HARMLESS(new Item(12411), new Location(3677, 2976, 0), true),
	LUMBERYARD(new Item(12642), new Location(3306, 3483, 0), true),
	ZUL_ANDRA(new Item(12938), new Location(2200, 3055, 0), true),
	KEY_MASTER(new Item(13249), new Location(), true),
	LUMBRIDGE_GRAVEYARD(new Item(19613), new Location(3244, 3199, 0), true),
	DRAYNOR_MANOR(new Item(19615), new Location(3111, 3326, 0), true),
	MIND_ALTAR(new Item(19617), new Location(), true),
	SALVE_GRAVEYARD(new Item(19619), new Location(), true),
	FENKENSTRAINS_CASTLE(new Item(19621), new Location(3549, 3528, 0), true),
	WEST_ARDOUGNE(new Item(19623), new Location(2524, 3306, 0), true),
	HARMONY_ISLAND(new Item(19625), new Location(3801, 2857, 0), true),
	CEMETARY(new Item(19627), new Location(2976, 3750, 0), true),
	BARROWS(new Item(19629), new Location(3565, 3306, 0), true),
	APE_ATOLL(new Item(19631), new Location(2778, 2786, 0), true),
	KOUREND(new Item(19651), new Location(1645, 3667, 0), true);

	/**
	 * The teletab id
	 */
	private final Item tablet;
	
	/**
	 * The location to teleport to
	 */
	private final Location location;
	
	private final boolean scroll;
	
	/**
	 * The TabletData constructor
	 * 
	 * @param item
	 *            The teletab or scroll
	 * @param location
	 *            The location to teleport to
	 * @param scroll
	 *            Are we using an tablet or scroll
	 */
	TabletData(final Item item, final Location location, final boolean scroll) {
		this.tablet = item;
		this.location = location;
		this.scroll = scroll;
	}

	public Item getTablet() {
		return tablet;
	}

	public Location getLocation() {
		return location;
	}

	public boolean isScroll() {
		return scroll;
	}

}
