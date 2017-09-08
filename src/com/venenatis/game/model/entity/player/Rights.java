package com.venenatis.game.model.entity.player;

import java.util.Arrays;
import java.util.Optional;

import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;

/**
 * The rights of a player determines their authority. Every right can be viewed
 * with a name and a value. The value is used to separate each right from one another.
 * 
 * @author Jason
 * @date January 22, 2015, 5:23:49 PM
 */
public enum Rights {
	
	/* Player */
	PLAYER("Player", Order.PLAYER, 0, "0000"),

	/* Moderator */
	MODERATOR("Moderator", Order.MODERATOR, 1, "4A7AA7"),

	/* Administrator */
	ADMINISTRATOR("Administrator", Order.STAFF, 2, "D17417", MODERATOR),

	/* Owner */
	OWNER("Owner", Order.STAFF, 2, "ED2624", MODERATOR, ADMINISTRATOR),

	/* Donator */
	DONATOR("Donator", Order.DONATOR, 3, "9C5B31"),

	/* Super donator */
	SUPER_DONATOR("Super Donator", Order.DONATOR, 4, "31383B", DONATOR),

	/* Elite donator */
	ELITE_DONATOR("Elite Donator", Order.DONATOR, 5, "FFC55B", SUPER_DONATOR, DONATOR),

	/* Extreme donator */
	EXTREME_DONATOR("Extreme Donator", Order.DONATOR, 6, "00BF3F", ELITE_DONATOR, SUPER_DONATOR, DONATOR),

	/* Helper */
	HELPER("Helper", Order.STAFF, 7, "3AB3D9"),
	
	/* YouTube */
	YOUTUBER("Youtuber", Order.PLAYER, 9, "91111A"),
	
	/* Graphic */
	GRAPHIC("Graphic", Order.PLAYER, 10, "CE795A"),

	/* Iron Man */
	IRON_MAN("Iron Man", Order.PLAYER, 11, "7A6F74"),

	/* Ultimate Iron Man */
	ULTIMATE_IRON_MAN("Ultimate Iron Man", Order.PLAYER, 12, "7A6F74"),
	
	/* Hardcore Iron Man */
	HARDCORE_IRON_MAN("Hardcore Iron Man", Order.PLAYER, 13, "7A6F74");

	public enum Order {
		PLAYER(0),

		DONATOR(1),

		MODERATOR(2),

		STAFF(3);

		private final int code;

		private Order(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}

	private final String name;

	private final int crown;
	
	private final Order order;
	
	private final String color;
	
	/**
	 * The right or rights inherited by this right
	 */
	private final Rights[] inherited;

	private Rights(String name, Order order, int crown, String color, Rights... inherited) {
		this.name = name;
		this.crown = crown;
		this.order = order;
		this.color = color;
		this.inherited = inherited;
	}
	
	public Optional<Rights> forValue(int id) {
		return Arrays.stream(values()).filter(a -> a.getOrder().getCode() == id).findAny();
	}
	
	public Optional<Rights> get(int id) {
		return Arrays.stream(values()).filter(a -> a.crown == id).findAny();
	}
	
	public final boolean equal(Rights other) {
		return this.getOrder() == other.getOrder();
	}

	public final String getName() {
		return name;
	}

	public final int getCrown() {
		return crown;
	}

	public Order getOrder() {
		return order;
	}

	public final String getColor() {
		return color;
	}
	
	/**
	 * Determines if this level of rights inherited another level of rights
	 * @param rights	the level of rights we're looking to determine is inherited
	 * @return			{@code true} if the rights are inherited, otherwise {@code false}
	 */
	public boolean inherits(Rights rights) {
		return equals(rights) || Arrays.asList(inherited).contains(rights);
	}

	public final boolean greater(Rights other) {
		return getOrder().getCode() > other.getOrder().getCode();
	}

	public final boolean greaterOrEqual(Rights other) {
		return getOrder().getCode() >= other.getOrder().getCode();
	}

	public final boolean less(Rights other) {
		return getOrder().getCode() < other.getOrder().getCode();
	}

	public final boolean lessOrEqual(Rights other) {
		return getOrder().getCode() <= other.getOrder().getCode();
	}

	public String getStringForRights(Player player) {
		if (player.getRights().getCrown() == 0) {
			return "";
		}
		return "<img=" + (player.getRights().getCrown()) + ">";
	}
	
	public boolean isRegularDonator(Player player) {
		return player.getRights() == Rights.DONATOR;
	}
	
	public boolean isSuperDonator(Player player) {
		return player.getRights() == Rights.SUPER_DONATOR;
	}
	
	public boolean isEliteDonator(Player player) {
		return player.getRights() == Rights.ELITE_DONATOR;
	}
	
	public boolean isExtremeDonator(Player player) {
		return player.getRights() == Rights.EXTREME_DONATOR;
	}

	public boolean isDonator(Player player) {
		return player.getRights().getOrder() == Order.DONATOR;
	}
	
	public boolean isStaffMember(Player player) {
		if (player.getRights().getOrder() == Order.MODERATOR || player.getRights().getOrder() == Order.STAFF) {
			return true;
		}
		return false;
	}
	
	public boolean isOwner(Player player) {
		return player.getRights() == Rights.OWNER;
	}
	
	public boolean isModerator(Player player) {
		return player.getRights() == Rights.MODERATOR;
	}
	
	public boolean isHelper(Player player) {
		return player.getRights() == Rights.HELPER;
	}

	public boolean isIron(Player player) {
		if (player.getRights() == IRON_MAN || player.getRights() == ULTIMATE_IRON_MAN || player.getRights() == HARDCORE_IRON_MAN) {
			return true;
		}
		return false;
	}
	
	public boolean isIronman(Player player) {
		return player.getRights() == Rights.IRON_MAN;
	}
	
	public boolean isUltimateIronman(Player player) {
		return player.getRights() == Rights.ULTIMATE_IRON_MAN;
	}
	
	public boolean isHardcoreIronman(Player player) {
		return player.getRights() == Rights.HARDCORE_IRON_MAN;
	}
	
	public void upgrade(Player player) {
		if (isIron(player)) {
			return;
		}
		
		if (isStaffMember(player)) {
			return;
		}
		
		Rights rights = Rights.PLAYER;
		
		if (player.getTotalAmountDonated() >= 10)
			rights = Rights.DONATOR;
		if (player.getTotalAmountDonated() >= 30) 
			rights = Rights.SUPER_DONATOR;
		if (player.getTotalAmountDonated() >= 100)
			rights = Rights.ELITE_DONATOR;
		if (player.getTotalAmountDonated() >= 300)
			rights = Rights.EXTREME_DONATOR;
		
		if (rights != Rights.PLAYER && player.getRights() != rights) {
			player.setRights(rights);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			player.getActionSender().sendMessage("You have upgraded to rank: " + rights.getName() + ". Please re-log for effect.");
		}
	}

	/**
	 * Determines if the players rights are in-between two values.
	 * @param start	the lowest range
	 * @param end	the highest range
	 * @return		true if the rights are greater than the start and lower
	 * than the end value.
	 */
	public boolean isBetween(int start, int end) {
		if (start < 0 || end < 0 || start > end || start == end) {
			throw new IllegalStateException();
		}
		return getCrown() >= start && getCrown() <= end;
	}

}