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
	PLAYER("Player", Order.PLAYER, 0, "6FE019"),

	/* Moderator */
	MODERATOR("Moderator", Order.MODERATOR, 1, "4A7AA7"),

	/* Administrator */
	ADMINISTRATOR("Administrator", Order.STAFF, 2, "D17417"),

	/* Owner */
	OWNER("Owner", Order.STAFF, 3, "ED2624"),

	/* Developer */
	DEVELOPER("Developer", Order.STAFF, 4, "994E94"),

	/* Bronze Member */
	BRONZE_MEMBER("Bronze Member", Order.MEMBER, 5, "9C5B31"),

	/* Silver Member */
	SILVER_MEMBER("Silver Member", Order.MEMBER, 6, "31383B"),

	/* Gold Member */
	GOLD_MEMBER("Gold Member", Order.MEMBER, 7, "FFC55B"),

	/* Premium Member */
	PREMIUM_MEMBER("Premium Member", Order.MEMBER, 8, "00BF3F"),

	/* Dope Member */
	DOPE_MEMBER("Dope Member", Order.MEMBER, 9, "E32973"),

	/* Veteran */
	VETERAN("Veteran", Order.PLAYER, 10, "B1800A"),

	/* YouTube */
	YOUTUBER("Youtuber", Order.PLAYER, 11, "91111A"),

	/* Helper Member */
	HELPER("Helper", Order.PLAYER, 12, "3AB3D9"),

	/* Iron Man */
	IRON_MAN("Iron Man", Order.PLAYER, 13, "7A6F74"),

	/* Ultimate Iron Man */
	ULTIMATE_IRON_MAN("Ultimate Iron Man", Order.PLAYER, 14, "7A6F74"),

	/* Graphic */
	GRAPHIC("Graphic", Order.PLAYER, 17, "CE795A");

	public static enum Order {
		PLAYER(0),

		MEMBER(1),

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

	private Rights(String name, Order order, int crown, String color) {
		this.name = name;
		this.crown = crown;
		this.order = order;
		this.color = color;
	}
	
	public static Optional<Rights> forValue(int id) {
		return Arrays.stream(values()).filter(a -> a.getOrder().getCode() == id).findAny();
	}
	
	public static Optional<Rights> get(int id) {
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

	public static String getStringForRights(Player player) {
		if (player.getRights().getCrown() == 0) {
			return "";
		}
		return "<img=" + (player.getRights().getCrown() - 1) + ">";
	}

	public static boolean isMember(Player player) {
		return player.getRights() != PLAYER;
	}
	
	public static boolean isPrivileged(Player player) {
		if (player.getRights().getOrder() == Order.MODERATOR || player.getRights().getOrder() == Order.STAFF) {
			return true;
		}
		return false;
	}

	public static boolean isHighclass(Player player) {
		if (player.getRights() == Rights.ADMINISTRATOR || player.getRights() == Rights.DEVELOPER || player.getRights() == Rights.OWNER) {
			return true;
		}
		return false;
	}
	
	public static boolean isOwner(Player player) {
		if (player.getRights() == Rights.DEVELOPER || player.getRights() == Rights.OWNER) {
			return true;
		}
		return false;
	}

	public static boolean isIron(Player player) {
		if (player.getRights() == IRON_MAN || player.getRights() == ULTIMATE_IRON_MAN) {
			return true;
		}
		return false;
	}

	public static int getKillCoin(Player player) {
		Rights rights = player.getRights();

		if (rights == BRONZE_MEMBER)
			return 15_000;
		if (rights == SILVER_MEMBER)
			return 20_000;
		if (rights == GOLD_MEMBER)
			return 25_000;
		if (rights == PREMIUM_MEMBER)
			return 30_000;
		if (rights == DOPE_MEMBER)
			return 40_000;

		return 10_000;
	}
	
	public static void upgrade(Player player) {
		if (isIron(player)) {
			return;
		}
		
		if (isPrivileged(player)) {
			return;
		}
		
		Rights rights = Rights.PLAYER;
		
		if (player.getTotalAmountDonated() >= 5)
			rights = Rights.BRONZE_MEMBER;
		if (player.getTotalAmountDonated() >= 35) 
			rights = Rights.SILVER_MEMBER;
		if (player.getTotalAmountDonated() >= 75)
			rights = Rights.GOLD_MEMBER;
		if (player.getTotalAmountDonated() >= 150)
			rights = Rights.PREMIUM_MEMBER;
		if (player.getTotalAmountDonated() >= 500)
			rights = Rights.DOPE_MEMBER;
		
		if (rights != Rights.PLAYER && player.getRights() != rights) {
			player.setRights(rights);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			player.getActionSender().sendMessage("You have upgraded to rank: " + rights.getName() + ". Please re-log for effect.");
		}
	}

}