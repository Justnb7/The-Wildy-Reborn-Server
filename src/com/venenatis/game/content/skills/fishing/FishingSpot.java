package com.venenatis.game.content.skills.fishing;

import java.util.HashMap;
import java.util.Map;

public enum FishingSpot {

	SMALL_NET_OR_BAIT(1518, new FishableData.Fishable[] { FishableData.Fishable.SHRIMP, FishableData.Fishable.ANCHOVIES }, new FishableData.Fishable[] { FishableData.Fishable.SARDINE, FishableData.Fishable.HERRING, FishableData.Fishable.PIKE }),
	LURE_OR_BAIT(1526, new FishableData.Fishable[] { FishableData.Fishable.TROUT, FishableData.Fishable.SALMON }, new FishableData.Fishable[] { FishableData.Fishable.SARDINE, FishableData.Fishable.HERRING, FishableData.Fishable.PIKE }),
	CAGE_OR_HARPOON(1519, new FishableData.Fishable[] { FishableData.Fishable.LOBSTER }, new FishableData.Fishable[] { FishableData.Fishable.TUNA, FishableData.Fishable.SWORD_FISH }),
	LARGE_NET_OR_HARPOON(1520, new FishableData.Fishable[] { FishableData.Fishable.MACKEREL, FishableData.Fishable.COD, FishableData.Fishable.BASS }, new FishableData.Fishable[] { FishableData.Fishable.SHARK }),
	HARPOON_OR_SMALL_NET(1534, new FishableData.Fishable[] { FishableData.Fishable.MONK_FISH }, new FishableData.Fishable[] { FishableData.Fishable.TUNA, FishableData.Fishable.SWORD_FISH }),
	MANTA_RAY(3019, new FishableData.Fishable[] { FishableData.Fishable.MANTA_RAY }, new FishableData.Fishable[] { FishableData.Fishable.MANTA_RAY }),
	DARK_CRAB(1536, new FishableData.Fishable[] { FishableData.Fishable.DARK_CRAB }, new FishableData.Fishable[] { FishableData.Fishable.DARK_CRAB }),
	KARAMBWAN(635, new FishableData.Fishable[] { FishableData.Fishable.KARAMBWAN }, new FishableData.Fishable[] { FishableData.Fishable.KARAMBWAN });

	private int id;
	private FishableData.Fishable[] option_1;
	private FishableData.Fishable[] option_2;
	private static Map<Integer, FishingSpot> fishingSpots = new HashMap<Integer, FishingSpot>();

	public static final void declare() {
		for (FishingSpot spots : values())
			fishingSpots.put(Integer.valueOf(spots.getId()), spots);
	}

	public static FishingSpot forId(int id) {
		return fishingSpots.get(Integer.valueOf(id));
	}

	private FishingSpot(int id, FishableData.Fishable[] option_1, FishableData.Fishable[] option_2) {
		this.id = id;
		this.option_1 = option_1;
		this.option_2 = option_2;
	}

	public int getId() {
		return id;
	}

	public FishableData.Fishable[] getOption_1() {
		return option_1;
	}

	public FishableData.Fishable[] getOption_2() {
		return option_2;
	}
}