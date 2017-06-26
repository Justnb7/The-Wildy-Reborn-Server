package com.venenatis.game.content.bounty;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles the bounty hunter's emblems
 *
 * @author Gabriel | Wolfs Darker && Jason
 */
public enum BountyHunterEmblem
{

	MYSTERIOUS_EMBLEM_1(12746, 50_000, 0),
	MYSTERIOUS_EMBLEM_2(12748, 100_000, 1),
	MYSTERIOUS_EMBLEM_3(12749, 200_000, 2),
	MYSTERIOUS_EMBLEM_4(12750, 400_000, 3),
	MYSTERIOUS_EMBLEM_5(12751, 750_000, 4),
	MYSTERIOUS_EMBLEM_6(12752, 1_250_000, 5),
	MYSTERIOUS_EMBLEM_7(12753, 1_750_000, 6),
	MYSTERIOUS_EMBLEM_8(12754, 2_500_000, 7),
	MYSTERIOUS_EMBLEM_9(12755, 3_500_000, 8),
	MYSTERIOUS_EMBLEM_10(12756, 5_000_000, 9);

	private final int itemId;
	private final int bounties;
	private final int index;

	private BountyHunterEmblem(int itemId, int bounties, int index)
	{
		this.itemId = itemId;
		this.bounties = bounties;
		this.index = index;
	}

	public static final Set<BountyHunterEmblem> EMBLEMS = Collections.unmodifiableSet(EnumSet.allOf(BountyHunterEmblem.class));
	
	private static Map<Integer, BountyHunterEmblem> emblem = new HashMap<>();
	
	static {
		for (BountyHunterEmblem em : values()) {
			emblem.put(em.getItemId(), em);
		}
	}
	
	public static BountyHunterEmblem get(int id) {
		return emblem.get(id);
	}

	public static BountyHunterEmblem valueOf(int index)
	{
		if (index >= EMBLEMS.size())
		{
			return BountyHunterEmblem.MYSTERIOUS_EMBLEM_10;
		}

		for (BountyHunterEmblem emblem : EMBLEMS)
		{
			if (emblem.getIndex() == index)
			{
				return emblem;
			}
		}

		return BountyHunterEmblem.MYSTERIOUS_EMBLEM_1;
	}

	public static BountyHunterEmblem getNext(BountyHunterEmblem emblem)
	{
		return valueOf(emblem.getIndex() + 1);
	}

	public static BountyHunterEmblem getPrevious(BountyHunterEmblem emblem)
	{
		return valueOf(emblem.getIndex() - 1);
	}

	public int getItemId()
	{
		return itemId;
	}

	public int getBounties()
	{
		return bounties;
	}

	public int getIndex()
	{
		return index;
	}


}