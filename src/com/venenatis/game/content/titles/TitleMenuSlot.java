package com.venenatis.game.content.titles;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Represents a single slot on the interface. Please note that although the slots are ordered in numberical order and that the button id's do have a pattern. However, in case of
 * change I have provided this enum.
 * 
 * @author Jason MacKeigan
 * @date Jan 22, 2015, 11:00:55 PM
 */
public enum TitleMenuSlot {
	SLOT_1(1, 37051, 144186), 
	SLOT_2(2, 37053, 144188), 
	SLOT_3(3, 37055, 144190), 
	SLOT_4(4, 37057, 144192), 
	SLOT_5(5, 37059, 144194), 
	SLOT_6(6, 37061, 144196), 
	SLOT_7(7, 37063, 144198), 
	SLOT_8(8, 37065, 144200), 
	SLOT_9(9, 37067, 144202), 
	SLOT_10(10, 37069, 144204), 
	SLOT_11(11, 37071, 144206), 
	SLOT_12(12, 37073, 144208), 
	SLOT_13(13, 37075, 144210), 
	SLOT_14(14, 37077, 144212), 
	SLOT_15(15, 37079, 144214), 
	SLOT_16(16, 37081, 144216), 
	SLOT_17(17, 37083, 144218), 
	SLOT_18(18, 37085, 144220), 
	SLOT_19(19, 37087, 144222), 
	SLOT_20(20, 37089, 144224), 
	SLOT_21(21, 37091, 144226), 
	SLOT_22(22, 37093, 144228), 
	SLOT_23(23, 37095, 144230), 
	SLOT_24(24, 37097, 144232), 
	SLOT_25(25, 37099, 144234),
	SLOT_26(26, 37101, 144236),
	SLOT_27(27, 37103, 144238),
	SLOT_28(28, 37105, 144240),
	SLOT_29(29, 37107, 144242),
	SLOT_30(30, 37109, 144244),
	SLOT_31(31, 37111, 144246),
	SLOT_32(32, 37113, 144248),
	SLOT_33(33, 37115, 144250),
	SLOT_34(34, 37117, 144252);

	/**
	 * The index on the menu this slot resides
	 */
	private final int index;

	/**
	 * The identification value of the component the string is displayed on
	 */
	private final int stringId;

	/**
	 * The button id that when clicked triggers an action
	 */
	private final int buttonId;

	/**
	 * Creates a new slot with an index and button id
	 * 
	 * @param index the index on the menu
	 * @param buttonId the button for triggering an action
	 */
	private TitleMenuSlot(int index, int stringId, int buttonId) {
		this.index = index;
		this.stringId = stringId;
		this.buttonId = buttonId;
	}

	/**
	 * Retrieves the index for this slot on the menu
	 * 
	 * @return the index on the menu
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * The identification value of the component the string is displayed on
	 * 
	 * @return the identification value
	 */
	public int getStringId() {
		return stringId;
	}

	/**
	 * The button id on the menu for this slot
	 * 
	 * @return the button id
	 */
	public int getButtonId() {
		return buttonId;
	}

	/**
	 * The slot with the equivellent button id
	 * 
	 * @param buttonId the button id
	 * @return A {@link TitleMenuSlot} object if the button matches any of the button values for any of the elements.
	 */
	public static TitleMenuSlot get(int buttonId) {
		return SLOTS.stream().filter(s -> s.buttonId == buttonId).findFirst().orElse(null);
	}

	/**
	 * A set of all elements in the {@linkplain TitleMenuSlot} enum.
	 */
	private static final Set<TitleMenuSlot> SLOTS = Collections.unmodifiableSet(EnumSet.allOf(TitleMenuSlot.class));

}