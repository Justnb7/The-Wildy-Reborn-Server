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
	OWNER("Owner", Order.STAFF, 2, "ED2624"),

	/* Donator */
	DONATOR("Donator", Order.DONATOR, 3, "9C5B31"),

	/* Super donator */
	SUPER_DONATOR("Super Donator", Order.DONATOR, 4, "31383B"),

	/* Elite donator */
	ELITE_DONATOR("Elite Donator", Order.DONATOR, 5, "FFC55B"),

	/* Extreme donator */
	EXTREME_DONATOR("Extreme Donator", Order.DONATOR, 6, "00BF3F"),

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

	private Rights(String name, Order order, int crown, String color) {
		this.name = name;
		this.crown = crown;
		this.order = order;
		this.color = color;
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
		if (player.getRights() == Rights.OWNER) {
			return true;
		}
		return false;
	}

	public boolean isIron(Player player) {
		if (player.getRights() == IRON_MAN || player.getRights() == ULTIMATE_IRON_MAN || player.getRights() == HARDCORE_IRON_MAN) {
			return true;
		}
		return false;
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

}