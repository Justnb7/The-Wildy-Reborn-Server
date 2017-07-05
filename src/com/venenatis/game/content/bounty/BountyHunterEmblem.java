package com.venenatis.game.content.bounty;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;

/**
 * Handles the bounty hunter's emblems
 *
 * @author Gabriel | Wolfs Darker && Jason
 */
public enum BountyHunterEmblem {

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

	private BountyHunterEmblem(int itemId, int bounties, int index) {
		this.itemId = itemId;
		this.bounties = bounties;
		this.index = index;
	}

	public int getItemId() {
		return itemId;
	}

	public int getBounties() {
		return bounties;
	}

	public int getIndex() {
		return index;
	}
	
	public static Optional<BountyHunterEmblem> forId(int id) {
		return Arrays.stream(values()).filter(a -> a.itemId == id).findAny();
	}

	public BountyHunterEmblem getNextOrLast() {
		return valueOf(index + 1).orElse(MYSTERIOUS_EMBLEM_10);
	}

	public BountyHunterEmblem getPreviousOrFirst() {
		return valueOf(index - 1).orElse(MYSTERIOUS_EMBLEM_1);
	}

	public static final Set<BountyHunterEmblem> EMBLEMS = Collections.unmodifiableSet(EnumSet.allOf(BountyHunterEmblem.class));

	public static Optional<BountyHunterEmblem> valueOf(int index) {
		return EMBLEMS.stream().filter(emblem -> emblem.index == index).findFirst();
	}

	static final Comparator<BountyHunterEmblem> BEST_EMBLEM_COMPARATOR = (first, second) -> Integer.compare(first.itemId, second.itemId);

	public static Optional<BountyHunterEmblem> getBest(Player player, boolean exclude) {
		List<BountyHunterEmblem> emblems = EMBLEMS.stream().filter(exclude(player, exclude)).collect(Collectors.toList());

		if (emblems.isEmpty()) {
			return Optional.empty();
		}

		return emblems.stream().max(BEST_EMBLEM_COMPARATOR);
	}

	private static Predicate<BountyHunterEmblem> exclude(Player player, boolean exclude) {
		return emblem -> player.getInventory().contains(new Item(emblem.getItemId())) && (!exclude || exclude && !emblem.equals(MYSTERIOUS_EMBLEM_10));
	}
}