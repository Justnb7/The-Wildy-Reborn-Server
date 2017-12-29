package com.venenatis.game.net.packet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.emotes.Emotes;
import com.venenatis.game.content.minigames.singleplayer.barrows.BarrowsHandler;
import com.venenatis.game.content.option_menu.ClientOptionMenu;
import com.venenatis.game.content.quest_tab.QuestTabPageHandler;
import com.venenatis.game.content.quest_tab.QuestTabPages;
import com.venenatis.game.content.skill_guides.SkillGuideContent;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.PrayerHandler.PrayerData;
import com.venenatis.game.model.combat.data.AttackStyle;
import com.venenatis.game.model.combat.magic.SpellBook;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.container.impl.inventory.InventoryContainer;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Entity.EntityType;
import com.venenatis.game.model.entity.npc.pet.Follower;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.clan.Clan;
import com.venenatis.game.model.entity.player.dialogue.input.InputAmount;
import com.venenatis.game.model.entity.player.dialogue.input.InputString;
import com.venenatis.game.model.entity.player.updating.PlayerUpdating;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.net.network.rsa.GameBuffer;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.server.Server;

/**
 * A utility class for sending packets.
 *
 * @author Patrick van Elderen, credits to Graham Edgecombe
 */
public class ActionSender {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates an action sender for the specified player.
	 *
	 * @param player
	 *            The player to create the action sender for.
	 */
	public ActionSender(Player player) {
		this.player = player;
	}
	
	/**
	 * Sends the quick prayers to the client
	 * 
	 * @param activated
	 *            Did we activate them?
	 */
	public ActionSender sendQuickPrayersState(boolean activated) {
		player.getOutStream().writeFrame(111);
		player.getOutStream().writeByte(activated ? 1 : 0);
		player.flushOutStream();
		return this;
	}
	
	/**
	 * Sends the entity feed overlay
	 * 
	 * @param entity_name
	 *            The entity we're fighting
	 * @param hp
	 *            their health
	 * @param max_hp
	 *            their max health
	 * @return send the health overlay
	 */
	public ActionSender sendEntityFeed(String entity_name, int hp, int max_hp) {
		player.getOutStream().putFrameVarByte(175);
		int offset = player.getOutStream().offset;
		player.getOutStream().putRS2String(entity_name == null ? "" : entity_name);
		player.getOutStream().writeShort(hp);
		player.getOutStream().writeShort(max_hp);
		player.getOutStream().putFrameSizeByte(offset);
		return this;
	}

	/**
	 * This packet is used for sending timers such as veng timers, antifire,
	 * barrage and teleblock.
	 * 
	 * @param type
	 *            The action type
	 * @param seconds
	 *            The display timer
	 * @return 1 - Anti-fire 2 - venge 3 - barrage 4 - teleblock
	 */
	public ActionSender sendWidget(int type, int seconds) {
		player.getOutStream().writeFrame(178);
		player.getOutStream().writeByte(type);
		player.getOutStream().writeShort(seconds * 50);
		player.flushOutStream();
		return this;
	}

	public ActionSender sendMapRegionPacket() {
		player.setLastKnownRegion(player.getLocation());
		player.getOutStream().writeFrame(73);
		player.getOutStream().writeWordA(player.getLastKnownRegion().getRegionX() + 6);
		player.getOutStream().writeShort(player.getLastKnownRegion().getRegionY() + 6);
		player.flushOutStream();
		return this;
	}

	public ActionSender sendProgressInterface() {
		sendConfig(406, player.getProgressBar());
		sendInterfaceConfig(1, 12224);
		sendInterfaceConfig(1, 12225);
		sendInterfaceConfig(1, 12226);
		sendInterfaceConfig(1, 12227);
		sendInterfaceConfig(0, 12161);
		sendString("% Done", 12224);
		sendWalkableInterface(8680);
		return this;
	}

	public ActionSender sendTab(int id) {
		player.getOutStream().writeFrame(106);
		player.getOutStream().writeByteC(id);
		player.flushOutStream();
		return this;
	}

	/**
	 * Sends the player's skills.
	 *
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSkills() {
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			sendSkillLevel(i);
		}
		return this;
	}

	public ActionSender sendString(String message, int interfaceId) {
		player.getOutStream().putFrameVarShort(126);
		int offset = player.getOutStream().offset;
		player.getOutStream().putRS2String(message == null ? "" : message);
		player.getOutStream().writeWordA(interfaceId);
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
		return this;
	}

	/**
	 * Represents the state a minimap can be in.
	 *
	 * @author Seven
	 */
	public enum MinimapState {
		/**
		 * The default state where the map is visible and clicking is enabled.
		 */
		NORMAL(0),

		/**
		 * The state where the map is visible, but clicking is disabled.
		 */
		UNCLICKABLE(1),

		/**
		 * The state where the map is pitch black, and clicking is disabled.
		 */
		HIDDEN(2);

		private final int code;

		private MinimapState(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}

	public ActionSender sendMinimapState(MinimapState state) {
		player.getOutStream().writeFrame(99);
		player.getOutStream().writeByte(state.getCode());
		return this;
	}

	/**
	 * Sends a hint above an entity's head.
	 * @param entity	The target entity to draw hint for.
	 * @return			The PacketSender instance.
	 */
	public ActionSender sendEntityHint(Entity entity, boolean active) {
		int type = entity instanceof Player ? 10 : 1;
		player.outStream.writeFrame(254);
		player.outStream.writeByte(active ? type : -1);
		player.outStream.writeShort(entity.getIndex());
		player.outStream.writeShort(0); // dummy data packet size must be 6
		player.outStream.writeByte(0); // dummy data packet size must be 6
		player.flushOutStream();
		return this;
	}

	/**
	 * Sends all the sidebar interfaces.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender setSideBarInterfaces() {
		sendSidebarInterface(Constants.SKILL_TAB, 3917);
		sendSidebarInterface(Constants.QUEST_TAB, 638);
		sendSidebarInterface(Constants.INVENTORY_TAB, 3213);
		sendSidebarInterface(Constants.EQUIPMENT_TAB, 1644);
		boolean curses = false;
		if (curses)
			sendSidebarInterface(Constants.PRAYER_TAB, 21356);
		else
			sendSidebarInterface(Constants.PRAYER_TAB, 5608);
		if (player.getSpellBook() == SpellBook.MODERN_MAGICS) {
			sendSidebarInterface(Constants.MAGIC_TAB, 1151);
		} else if (player.getSpellBook() == SpellBook.ANCIENT_MAGICKS) {
			sendSidebarInterface(Constants.MAGIC_TAB, 12855);
		} else {
			sendSidebarInterface(Constants.MAGIC_TAB, 29999);
		}
		sendSidebarInterface(Constants.CLAN_TAB, 28128/*33800*/);
		sendSidebarInterface(Constants.FRIENDS_TAB, 5065);
		sendSidebarInterface(Constants.IGNORE_TAB, 5715);
		sendSidebarInterface(Constants.WRENCH_TAB, 45580);
		sendSidebarInterface(Constants.EMOTE_TAB, 147);
		sendSidebarInterface(Constants.MUSIC_TAB, 42150);
		sendSidebarInterface(Constants.ATTACK_TAB, 2423);
		sendSidebarInterface(Constants.LOGOUT_TAB, 2449);
		return this;
	}

	public ActionSender hideAllSideBars() {
		for (int i = 0; i < 14; i++)
			sendSidebarInterface(i, -1);
		return this;
	}

	public ActionSender createObjectHint(int x, int y, int height, int pos) {
		player.getOutStream().writeFrame(254);
		player.getOutStream().writeByte(pos);
		player.getOutStream().writeShort(x);
		player.getOutStream().writeShort(y);
		player.getOutStream().writeByte(height);
		return this;
	}

	public ActionSender createPlayerHint(int type, int id) {
		player.getOutStream().writeFrame(254);
		player.getOutStream().writeByte(type);
		player.getOutStream().writeShort(id);
		player.getOutStream().write3Byte(0);
		return this;
	}

	public ActionSender sendInput(InputString inputString) {
		player.getOutStream().writeFrame(187);
		player.setInputString(inputString);
		player.flushOutStream();
		return this;
	}

	public ActionSender sendInput(InputAmount inputAmount) {
		player.getOutStream().writeFrame(27);
		player.setInputAmount(inputAmount);
		player.flushOutStream();
		return this;
	}

	/**
	 * Sends the player an option.
	 * 
	 * @param slot
	 *            The slot to place the option in the menu.
	 * @param top
	 *            Flag which indicates the item should be placed at the top.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInteractionOption(String option, int slot, boolean top) {
		player.getOutStream().putFrameVarByte(104);
		int offset = player.getOutStream().offset;
		player.getOutStream().writeByte((byte) -slot);
		player.getOutStream().putByteA(top ? (byte) 1 : (byte) 0);
		player.getOutStream().putRS2String(option);
		player.getOutStream().putFrameSizeByte(offset);
		player.flushOutStream();
		return this;
	}

	public ActionSender sendInterfaceConfig(int interfaceId, int state) {

		player.getOutStream().writeFrame(171);
		player.getOutStream().writeByte(interfaceId);
		player.getOutStream().writeShort(state);
		return this;
	}

	/**
	 * zoom is usually 175
	 */
	public ActionSender sendItemOnInterface(int id, int zoom, int model) {
		player.getOutStream().writeFrame(246);
		player.getOutStream().writeWordBigEndian(id);
		player.getOutStream().writeShort(zoom);
		player.getOutStream().writeShort(model);
		return this;
	}

	public ActionSender sendChatInterface(int frame) {
		player.getOutStream().writeFrame(164);
		player.getOutStream().writeWordBigEndian_dup(frame);
		return this;
	}

	public ActionSender sendItemOnInterfaceSlot(int interfaceId, Item item, int slot) {
		player.outStream.putFrameVarShort(34);
		int offset = player.getOutStream().offset;
		player.outStream.writeShort(interfaceId);
		player.outStream.writeByte(slot);
		if (item != null) {
			player.outStream.writeShort(item.getId() + 1);
			player.outStream.writeByte(255);
			player.outStream.putInt(item.getAmount());
		} else {
			player.outStream.writeShort(0);
			player.outStream.writeByte(255);
			player.outStream.writeByte(0);
		}
		player.outStream.putFrameSizeShort(offset);
		return this;
	}

	public ActionSender sendUpdateItem(int frame, int item, int slot, int amount) {
		player.outStream.putFrameVarShort(34);
		int offset = player.getOutStream().offset;
		player.outStream.writeShort(frame);
		player.outStream.writeByte(slot);
		player.outStream.writeShort(item + 1);
		player.outStream.writeByte(255);
		player.outStream.putInt(amount);
		player.outStream.putFrameSizeShort(offset);
		return this;
	}
	
	public ActionSender sendItemOnInterface(final int frame, final int slot, final int id, final int amount) {
		player.outStream.createFrameVarSizeWord(34);
		player.outStream.writeShort(frame);
		player.outStream.writeByte(slot);
		player.outStream.writeShort(id + 1);
		player.outStream.writeByte(255);
		player.outStream.putInt(amount);
		player.outStream.endFrameVarSizeWord();
		return this;
	}

	public ActionSender clearItemOnInterface(int frame) {
		player.outStream.putFrameVarShort(72);
		int offset = player.getOutStream().offset;
		player.outStream.writeShort(frame);
		player.outStream.putFrameSizeShort(offset);
		return this;
	}

	public ActionSender sendMultiIcon(int icon) {
		player.outStream.writeFrame(61);
		player.outStream.writeByte(icon);
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		return this;
	}

	public ActionSender sendPm(long name, int rights, byte[] chatMessage, int messageSize) {
		player.getOutStream().putFrameVarByte(196);
		int offset = player.getOutStream().offset;
		player.getOutStream().putLong(name);
		player.getOutStream().putInt(player.lastChatId++);
		player.getOutStream().writeByte(rights);
		player.getOutStream().writeBytes(chatMessage, messageSize, 0);
		player.getOutStream().putFrameSizeByte(offset);
		Utility.textUnpack(chatMessage, messageSize);
		Utility.longToPlayerName(name);
		return this;
	}

	public ActionSender removeAllInterfaces() {
		player.getInterfaceState().interfaceClosed();
		player.getOutStream().writeFrame(219);
		return this;
	}

	public ActionSender sendStringColor(int id, int color) {
		player.getOutStream().writeFrame(122);
		player.getOutStream().writeShort(id);
		player.getOutStream().putInt(color);
		player.flushOutStream();
		return this;
	}

	public ActionSender clearScreen() {
		player.getOutStream().writeFrame(107);
		return this;
	}

	public ActionSender sendToggle(int id, int state) {
		player.getOutStream().writeFrame(87);
		player.getOutStream().writeWordBigEndian_dup(id);
		player.getOutStream().writeDWord_v1(state);
		return this;
	}

	public ActionSender sendExperienceCounter(int skill, int experience) {
		GameBuffer stream = player.getOutStream();
		stream.writeFrame(127);
		stream.writeByte(skill);
		stream.putInt(experience);
		stream.putInt(player.getSkills().getExpCounter());
		// System.out.println("skill: "+skill+ " exp given "+experience);
		player.flushOutStream();
		return this;
	}

	public ActionSender moveComponent(int x, int y, int componentId) {
		player.getOutStream().writeFrame(70);
		player.getOutStream().writeShort(x);
		player.getOutStream().writeWordBigEndian(y);
		player.getOutStream().writeWordBigEndian(componentId);
		return this;
	}

	/**
	 * Changes the main displaying sprite on an interface. The index represents
	 * the location of the new sprite in the index of the sprite array.
	 * 
	 * @param componentId
	 *            the interface
	 * @param index
	 *            the index in the array
	 */
	public ActionSender sendChangeSprite(int componentId, byte index) {
		GameBuffer stream = player.getOutStream();
		stream.writeFrame(7);
		stream.putInt(componentId);
		stream.writeByte(index);
		player.flushOutStream();
		return this;
	}

	/**
	 * The message that opens an interface and displays another interface over
	 * the inventory area.
	 *
	 * @param overlay
	 *            the interface to send on the inventory area.
	 * @return an instance of this encoder.
	 */
	public ActionSender sendInterfaceWithInventoryOverlay(int interfaceId, int overlay) {
		player.getOutStream().writeFrame(248);
		player.getOutStream().writeWordA(interfaceId);
		player.getOutStream().writeShort(overlay);
		player.getInterfaceState().interfaceOpened(interfaceId);
		return this;
	}
	
	/**
	 * Sends a new Option menu packet.
	 * 
	 * @param childId
	 *            The childId of the option menu interface.
	 * @param optionMenus
	 *            A {@link Collection} of {@link ClientOptionMenu}s containing
	 *            the updates.
	 */
	public void sendOptionMenuInterface(final int childId, final Collection<ClientOptionMenu> optionMenus) {
		if (player == null || player.getOutStream() == null) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(223);
		player.getOutStream().writeShort(childId);
		if (optionMenus.isEmpty()) {
			player.getOutStream().writeByte(0);
		} else {
			player.getOutStream().writeByte(optionMenus.size());
			for (ClientOptionMenu menu : optionMenus) {
				if (menu == null) {
					continue;
				}
				player.getOutStream().writeByte(menu.getIdentifier());
				player.getOutStream().putRS2String(menu.getOptionName());
				player.getOutStream().putRS2String(menu.getOptionTooltip());
			}
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}

	/**
	 * Sends some information to the client about screen fading.
	 * 
	 * @param text
	 *            the text that will be displayed in the center of the screen
	 * @param state
	 *            the state should be either 0, -1, or 1.
	 * @param seconds
	 *            the amount of time in seconds it takes for the fade to
	 *            transition.
	 *            <p>
	 *            If the state is -1 then the screen fades from black to
	 *            transparent. When the state is +1 the screen fades from
	 *            transparent to black. If the state is 0 all drawing is
	 *            stopped.
	 */
	public ActionSender sendScreenFade(String text, int state, int seconds) {
		if (seconds < 1 && state != 0) {
			throw new IllegalArgumentException("The amount of seconds cannot be less than one.");
		}
		player.getOutStream().putFrameVarShort(9);
		int offset = player.getOutStream().offset;
		player.getOutStream().putRS2String(text);
		player.getOutStream().writeByte(state);
		player.getOutStream().writeByte(seconds);
		player.getOutStream().putFrameSizeShort(offset);
		return this;
	}

	public ActionSender sendScrollBar(int scrollbar, int size) {
		player.getOutStream().writeFrame(204);
		player.getOutStream().putInt(scrollbar);
		player.getOutStream().putInt(size);
		return this;
	}

	public ActionSender sendShakeScreen(int verticleAmount, int verticleSpeed, int horizontalAmount,
			int horizontalSpeed) {
		player.outStream.writeFrame(35);
		player.outStream.writeByte(verticleAmount);
		player.outStream.writeByte(verticleSpeed);
		player.outStream.writeByte(horizontalAmount);
		player.outStream.writeByte(horizontalSpeed);
		return this;
	}

	public void clientConfigs(Map<Integer, Integer> values) {
		if (values.isEmpty()) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(233);
		player.getOutStream().writeByte(values.size());
		for (Entry<Integer, Integer> value : values.entrySet()) {
			player.getOutStream().writeShort(value.getKey());
			player.getOutStream().writeByte(value.getValue());
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}
	
	/**
	 * Sends a new skill guide packet
	 * 
	 * @param skillGuide
	 *            {@link SkillGuideContent} for the guide
	 */
	public void sendSkillGuideInterface(final SkillGuideContent skillGuide, int optionLength) {
		if (player == null || player.getOutStream() == null) {
			return;
		}

		if (skillGuide.getLevels().length == 0) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(227);
		player.getOutStream().writeByte(optionLength);
		player.getOutStream().writeByte(skillGuide.getLevels().length);
		for (int i = 0; i < skillGuide.getLevels().length; i++) {
			player.getOutStream().writeByte(skillGuide.getLevels()[i]);
			player.getOutStream().putRS2String(i < skillGuide.getDescriptions1().length ? skillGuide.getDescriptions1()[i] : "");
			player.getOutStream().putRS2String(i < skillGuide.getDescriptions2().length ? skillGuide.getDescriptions2()[i] : "");
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}
	
	/**
	 * Sends packet 79 to set an interface's scroll position.
	 * 
	 * @param childId
	 *            The childId of the interface.
	 * @param scrollPosition
	 *            The value of the scroll position.
	 */
	public void setScrollPosition(final int childId, final int scrollPosition, final int scrollMax) {
		if (player == null || player.getOutStream() == null) {
			return;
		}
		player.getOutStream().writeFrame(79);
		player.getOutStream().writeShort(childId);
		player.getOutStream().writeShort(scrollPosition);
		player.getOutStream().writeShort(scrollMax);
	}
	
	/**
	 * Sends the given collection of items on the specified frameId.
	 * 
	 * @param frameId
	 *            The client interface frame id that the items are being sent
	 *            to.
	 * @param items
	 *            The collection of items that is being sent.
	 */
	public void sendItemsOnInterface(final int frameId, final Collection<Item> items) {
		if (player == null || player.getOutStream() == null) {
			return;
		}
		if (items == null) {
			return;
		}
		player.getOutStream().putFrameVarShort(53);
		int offset = player.getOutStream().offset;

		player.getOutStream().writeShort(frameId);
		player.getOutStream().writeShort(items.size());

		for (Item item : items) {
			if (item != null) {
				int amount = item.getAmount();
				if (amount > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(item.getAmount());
				} else {
					player.getOutStream().writeByte(item.getAmount());
				}
				player.getOutStream().writeWordBigEndianA(item.getId() < 1 ? 0 : item.getId() + 1);
			} else {
				player.getOutStream().writeByte(0);
				player.getOutStream().writeWordBigEndianA(0);
			}
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
	}

	/**
	 * Sends a packet to update a group of items.
	 * 
	 * @param id
	 *            The interface id.
	 * @param items
	 *            The items.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendItemOnInterface(int id, Item... items) {
		player.getOutStream().putFrameVarShort(53);
		int offset = player.getOutStream().offset;
		player.getOutStream().writeShort(id);
		player.getOutStream().writeShort(items.length);
		for (Item item : items) {
			if (item != null) {
				int amount = item.getAmount();
				if (amount > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(item.getAmount());
				} else {
					player.getOutStream().writeByte(item.getAmount());
				}
				player.getOutStream().writeWordBigEndianA(item.getId() + 1);
			} else {
				player.getOutStream().writeByte(0);
				player.getOutStream().writeWordBigEndianA(0);
			}
		}
		if (id == InterfaceConstants.WITHDRAW_BANK && player.getBank().getTabAmounts() != null) {
			for (final int amount : player.getBank().getTabAmounts()) {
				player.getOutStream().writeByte(amount >> 8);
				player.getOutStream().writeShort(amount & 0xFF);
			}
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
		return this;
	}

	public ActionSender sendObject(int id, int x, int y, int h, int face, int objectType) {
		sendCoordinates(Location.create(x, y, h));
		System.out.println("Object");
		// removing object
		player.getOutStream().writeFrame(101);
		player.getOutStream().writeByteC((objectType << 2) + (face & 3));
		player.getOutStream().writeByte(0);

		if (id != -1) { // adding object
			player.getOutStream().writeFrame(151);
			player.getOutStream().writeByteS(0);
			player.getOutStream().writeWordBigEndian(id);
			player.getOutStream().writeByteS((objectType << 2) + (face & 3));
		}
		return this;
	}

	public ActionSender sendCoordinates(Location location) {
		player.getOutStream().writeFrame(85);
		int y = location.getY() - player.getLastKnownRegion().getRegionY() * 8;
		int x = location.getX() - player.getLastKnownRegion().getRegionX() * 8;
		player.getOutStream().writeByteC(y);
		player.getOutStream().writeByteC(x);
		player.flushOutStream();
		return this;
	}

	/**
	 * Sends a ground item
	 * 
	 * @param groundItem
	 * @return
	 */
	public ActionSender sendGroundItem(GroundItem groundItem) {
		sendCoordinates(groundItem.getLocation());
		player.getOutStream().writeFrame(44);
		player.getOutStream().writeWordBigEndianA(groundItem.getItem().getId());
		player.getOutStream().writeShort(groundItem.getItem().getAmount());
		player.getOutStream().writeByte(0);
		player.flushOutStream();
		return this;
	}

	public ActionSender sendRemoveGroundItem(GroundItem groundItem) {
		sendCoordinates(groundItem.getLocation());
		player.getOutStream().writeFrame(156);
		player.getOutStream().writeByteS(0);
		player.getOutStream().writeShort(groundItem.getItem().getId());
		player.flushOutStream();
		return this;
	}
	
	public ActionSender sendStillGFX(final int id, final int height, final Location loc) {
        sendLocalCoordinates(loc, 0, 0);
		player.getOutStream().writeFrame(4);
		player.getOutStream().writeByte((byte) 0);
		player.getOutStream().writeShort(id);
		player.getOutStream().writeByte((byte) height);
		player.getOutStream().writeShort(0);
		player.flushOutStream();
        return this;
    }
	
	public ActionSender stillGfx(int id, Location location, int time) {
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().writeFrame(85);
			player.getOutStream().writeByteC(location.getY() - (player.getLastKnownRegion().getRegionY() * 8));
			player.getOutStream().writeByteC(location.getX() - (player.getLastKnownRegion().getRegionX() * 8));
			player.getOutStream().writeFrame(4);
			player.getOutStream().writeByte(0);
			player.getOutStream().writeShort(id);
			player.getOutStream().writeByte(location.getZ());
			player.getOutStream().writeShort(time);
			player.flushOutStream();
		}
		return this;
	}
	
	public ActionSender shoot(int casterY, int casterX, int offsetY, int offsetX, int gfxMoving, int StartHeight, int endHeight, int speed, Entity e) {
    	// interesting thing about projectile packet, if we're using the client's PLAYER index array, the id is short.MAX_VAL + id (32k + 2048 max players)
    	// otherwise for npcs its just id (range 0-short.max)
    	int attack_index = e.getEntityType() == EntityType.PLAYER ? -(e.getIndex() + 1) : e.getIndex() + 1;
        for (int i = 1; i < World.getWorld().getPlayers().capacity(); i++) {
            if (World.getWorld().getPlayers().get(i) == null) {
            	continue;
            }
            Player player = World.getWorld().getPlayers().get(i);
            if (!player.getLocation().isWithinDistance(player.getLocation(), 60)) {
            	continue;
            }
    		int regionX = player.getLastKnownRegion().getRegionX(), regionY = player.getLastKnownRegion().getRegionY();
            player.outStream.writeFrame(85); // set base packet
            player.outStream.writeByteC((casterY - (regionY * 8)) - 2);
            player.outStream.writeByteC((casterX - (regionX * 8)) - 3);
            player.outStream.writeFrame(117); // actual projectile packet
            player.outStream.writeByte(50);
            player.outStream.writeByte(offsetY);
            player.outStream.writeByte(offsetX);
            player.outStream.writeShort(attack_index); // target index
            player.outStream.writeShort(gfxMoving);
            player.outStream.writeByte(StartHeight);
            player.outStream.writeByte(endHeight);
            player.outStream.writeShort(51);
            player.outStream.writeShort(speed);
            player.outStream.writeByte(16); // slope (?)
            player.outStream.writeByte(64); // angle (?)
        }
        return this;
    }
	
	public ActionSender sendProjectile(Location start, Location finish, int projectileId, int speed, int delay) {
		sendProjectile(start, finish, projectileId, delay, 50, speed, 83, 33, 0, 36, -player.getId() - 1);
		return this;
	}
	
	
	public ActionSender createSplatterProjectile(int projectileId, int locationX, int locationY, int startTime, int endTime) {
		int nX = player.getX();
		int nY = player.getY();
		int pX = locationX;
		int pY = locationY;
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;
		sendProjectile(new Location(nX, nY), new Location(offX, offY), projectileId, endTime, 50, startTime, 33, 0, 0, 36, -1);
		return this;
	}

	/**
	 * Sends a projectile to a location.
	 *
	 * @param start
	 *            The starting location.
	 * @param finish
	 *            The finishing location.
	 * @param id
	 *            The graphic id.
	 * @param delay
	 *            The delay before showing the projectile.
	 * @param angle
	 *            The angle the projectile is coming from.
	 * @param speed
	 *            The speed the projectile travels at.
	 * @param startHeight
	 *            The starting height of the projectile.
	 * @param endHeight
	 *            The ending height of the projectile.
	 * @param lockon
	 *            The lockon index of the projectile, so it follows them if they
	 *            move.
	 * @param slope
	 *            The slope at which the projectile moves.
	 * @param radius
	 *            The radius from the centre of the tile to display the
	 *            projectile from.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendProjectile(Location start, Location finish, int id, int delay, int angle, int speed,
			int startHeight, int endHeight, int slope, int radius, int lockon) {
		int offsetX = (start.getX() - finish.getX());
		int offsetY = (start.getY() - finish.getY());

		// sendLocalCoordinates(start, -3, -2);
		player.getOutStream().writeFrame(85);

		// player.getOutStream().writeByteC(start.getY() -
		// player.getLastKnownRegion().getY() * 8 - 2);
		// player.getOutStream().writeByteC(start.getX() -
		// player.getLastKnownRegion().getX() * 8 - 3);

		int difx = start.getX(), dify = start.getY();
		int regionX = player.getLastKnownRegion().getRegionX(), regionY = player.getLastKnownRegion().getRegionY();
		player.getOutStream().writeByteC((dify - regionY * 8 - 2));
		player.getOutStream().writeByteC((difx - regionX * 8 - 3));
		player.getOutStream().writeFrame(117);
		player.getOutStream().writeByte(angle);
		player.getOutStream().writeByte(offsetY);
		player.getOutStream().writeByte(offsetX);
		player.getOutStream().writeShort(lockon);
		player.getOutStream().writeShort(id);
		player.getOutStream().writeByte(startHeight);
		player.getOutStream().writeByte(endHeight);
		player.getOutStream().writeShort(delay);
		player.getOutStream().writeShort(speed);
		player.getOutStream().writeByte(slope);
		player.getOutStream().writeByte(radius);

		player.flushOutStream();
		// player.sendMessage("dif "+offsetX+"|"+offsetY+" from "+start+" to
		// "+finish+" dist "+start.distance(finish));
		return this;
	}

	public ActionSender sendLocalCoordinates(Location position, int xOffset, int yOffset) {
		player.getOutStream().writeFrame(85);

		int difx = position.getX(), dify = position.getY();
		int regionX = player.getLastKnownRegion().getRegionX(), regionY = player.getLastKnownRegion().getRegionY();
		player.getOutStream().writeByteC((dify - (regionY * 8)) + yOffset);
		player.getOutStream().writeByteC((difx - (regionX * 8)) + xOffset);

		player.flushOutStream();
		return this;
	}

	public ActionSender sendMessage(String message) {
		player.outStream.putFrameVarByte(253);
		int offset = player.getOutStream().offset;
		player.outStream.putRS2String(message);
		player.outStream.writeByte(0);
		player.outStream.putFrameSizeByte(offset);
		return this;
	}
	
	public ActionSender sendMessage(String message, int color) {
		player.outStream.putFrameVarByte(253);
		int offset = player.getOutStream().offset;
		message = "<col=" + color + ">" + message + "</col>";
		player.outStream.putRS2String(message);
		player.outStream.writeByte(0);
		player.outStream.putFrameSizeByte(offset);
		return this;
	}

	/**
	 * Sends a configuration button's state.
	 * 
	 * @param id
	 *            The id of the configuration button.
	 * @param state
	 *            The state to set it to.
	 * @return The ActionSender instance.
	 */
	public ActionSender sendConfig(int id, int state) {
		// System.out.println("Varp update will be opcode "+(state<128 ?
		// OPCODE:87)+" based on state:"+state);
		if (state < 128) {
			player.getOutStream().writeFrame(36);
			player.getOutStream().writeWordBigEndian(id);
			player.getOutStream().writeByte(state);
		} else {
			player.getOutStream().writeFrame(87);
			player.getOutStream().writeWordBigEndian_dup(id);
			player.getOutStream().writeDWord_v1(state);
		}
		return this;
	}
	
	/**
	 * Sends a massive string update
	 * 
	 * @param startId
	 *            the interface id the strings start at
	 *            
	 * @param endId
	 *            the interface id the strings ends at
	 *            
	 * @param strings
	 *            the strings array
	 */
	public void sendStrings(int startId, int endId, String[] strings) {
		if (player == null || player.getOutStream() == null) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(229);
		player.getOutStream().writeShort(startId);
		player.getOutStream().writeShort(startId + strings.length);
		player.getOutStream().writeByte(strings.length);
		for (String s : strings)
			player.getOutStream().putRS2String(s);
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}
	
	public void sendStrings(int startId, int endId, List<String> strings) {
		if (player == null || player.getOutStream() == null) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(229);
		player.getOutStream().writeShort(startId);
		player.getOutStream().writeShort(endId);
		player.getOutStream().writeByte(strings.size());
		for (String s : strings) {
			player.getOutStream().putRS2String(s);
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}
	
	/**
	 * Sends a massive string emptier
	 * 
	 * @param startId
	 *            the interface id the strings start at
	 *            
	 * @param endId
	 *            the interface id the strings ends at
	 *            
	 */
	public void emptyStrings(int startId, int endId) {
		if (player == null || player.getOutStream() == null) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(231);
		player.getOutStream().writeShort(startId);
		player.getOutStream().writeShort(endId);
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}

	public ActionSender sendFriendServerStatus(final int status) {
		this.player.getOutStream().writeFrame(221);
		this.player.getOutStream().writeByte(status);
		return this;
	}

	public ActionSender sendSkillLevel(int skill) {
		player.getOutStream().writeFrame(134);
		player.getOutStream().writeByte((byte) skill);
		player.getOutStream().writeDWord_v1((int) player.getSkills().getExperience(skill));
		player.getOutStream().writeByte((byte) player.getSkills().getLevel(skill));
		// System.out.printf("skill - %s exp %s lvl %s %n", skill,
		// player.getSkills().getExperience(skill),
		// player.getSkills().getLevel(skill));
		return this;
	}

	/**
	 * Sends a packet to the client in order to open an interface
	 * 
	 * @param id
	 *            The interface
	 */
	public ActionSender sendInterface(int id) {
		player.getOutStream().writeFrame(97);
		player.getOutStream().writeShort(id);
		player.getInterfaceState().interfaceOpened(id);
		return this;
	}

	public ActionSender sendSong(int songId) {
		player.getOutStream().writeFrame(74);
		player.getOutStream().writeWordBigEndian(songId);
		return this;
	}

	public ActionSender sendSound(int id, int type, int delay) {
		player.getOutStream().writeFrame(174);
		player.getOutStream().writeShort(id);
		player.getOutStream().writeByte(type);
		player.getOutStream().writeShort(delay);
		return this;
	}

	public ActionSender sendTemporarySong(int songId, int songDelay) {
		player.getOutStream().writeFrame(121);
		player.getOutStream().writeWordBigEndian(songId);
		player.getOutStream().writeWordBigEndian(songDelay);
		return this;
	}

	public ActionSender sendWalkableInterface(int id) {
		player.getOutStream().writeFrame(208);
		player.getOutStream().writeWordBigEndian_dup(id);
		return this;
	}

	public ActionSender sendSidebarInterface(int menu, int id) {
		player.outStream.writeFrame(71);
		player.outStream.writeShort(id);
		player.outStream.putByteA(menu);
		player.flushOutStream();
		return this;
	}

	public ActionSender sendPlayerHeadToInterface(int id) {
		player.getOutStream().writeFrame(185);
		player.getOutStream().writeWordBigEndianA(id);
		return this;
	}

	public ActionSender sendNpcHeadToInterface(int id, int child) {
		player.getOutStream().writeFrame(75);
		player.getOutStream().writeWordBigEndianA(id);
		player.getOutStream().writeWordBigEndianA(child);
		return this;
	}

	public ActionSender sendInterfaceAnimation(int id, Animation animation) {
		player.getOutStream().writeFrame(200);
		player.getOutStream().writeShort(id);
		player.getOutStream().writeShort(animation.getId());
		player.flushOutStream();
		return this;
	}

	public ActionSender sendFriend(long username, int world) {
		player.getOutStream().writeFrame(50);
		player.getOutStream().putLong(username);
		player.getOutStream().writeByte(world);
		player.flushOutStream();
		return this;
	}

	public ActionSender sendChatBoxInterface(int id) {
		player.getOutStream().writeFrame(164);
		player.getOutStream().writeWordBigEndian_dup(id);
		player.flushOutStream();
		return this;
	}

	public ActionSender sendRunEnergy() {
		player.getOutStream().writeFrame(110);
		player.getOutStream().writeByte(player.getRunEnergy());
		player.flushOutStream();
		return this;
	}

	public ActionSender sendBanner(String title, String message, int color) {
		if (title != null) {
			player.outStream.putFrameVarByte(202);
			int offset = player.getOutStream().offset;
			player.getOutStream().putRS2String(title);
			player.getOutStream().putRS2String(message);
			player.getOutStream().putInt(color);
			player.outStream.putFrameSizeByte(offset);
		}
		return this;
	}

	/**
	 * Animates an object.
	 *
	 * @param obj
	 *            The object.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender animateObject(GameObject obj, int animationId) {
		sendLocalCoordinates(obj.getLocation(), 0, 0);
		player.getOutStream().writeFrame(160);
		int ot = ((obj.getType() << 2) + (obj.getDirection() & 3));

		byte jew = (byte) (((obj.getX() & 7) << 4) + (obj.getY() & 7));
		player.getOutStream().writeByteS(jew);
		player.getOutStream().writeByteS((byte) ot);
		player.getOutStream().writeWordA(animationId);
		player.flushOutStream();
		System.out.printf("jew btw %s %s anim:%s%n", jew, ot, animationId);
		return this;
	}

	/**
	 * Sends all the login packets.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendLogin() {

		// Activate our players session
		player.setActive(true);

		// Finalize our ignore and friends list
		player.getFAI().handleLogin();

		// Update the map packet
		sendMapRegionPacket();

		// We can go ahead and update out sidebars
		setSideBarInterfaces();

		// Update the magic book
		if (player.getSpellBook() == SpellBook.ANCIENT_MAGICKS) {
			sendSidebarInterface(6, 12855);
		} else if (player.getSpellBook() == SpellBook.MODERN_MAGICS) {
			sendSidebarInterface(6, 1151);
		} else if (player.getSpellBook() == SpellBook.LUNAR_MAGICS) {
			sendSidebarInterface(6, 29999);
		}

		// Update our attack style
		AttackStyle.adjustAttackStyleOnLogin(player);

		player.getController().onStartup(player);
		
		player.checkForSkillcapes();

		// Reset prayers
		PrayerHandler.resetAll(player);

		// unlock/lock special case prayers
		sendConfig(709, PrayerHandler.canUse(player, PrayerData.PRESERVE, false) ? 1 : 0);
		sendConfig(711, PrayerHandler.canUse(player, PrayerData.RIGOUR, false) ? 1 : 0);
		sendConfig(713, PrayerHandler.canUse(player, PrayerData.AUGURY, false) ? 1 : 0);

		// Update inventory
		player.getInventory().refresh();

		// Update equipment
		player.getEquipment().refresh();

		// Update the weapon attributes
		player.getWeaponInterface().restoreWeaponAttributes();

		// Send the interaction options
		sendInteractionOption("Follow", 4, false);
		sendInteractionOption("Trade With", 5, false);

		// We can go ahead and finalize the game configs
		updateConfigs();
		
		player.setInfection(0);

		// Update the skills
		sendSkills();
		player.setCombatLevel(player.getSkills().getCombatLevel());
		player.getSkills().updateTotalLevel();

		sendMessage("Welcome to " + Constants.SERVER_NAME + " Alpha.");
		updateAfterLogin();

		// activate login delay
		player.setAttribute("login_delay", System.currentTimeMillis());
		return this;
	}

	public void updateConfigs() {
		sendConfig(207, player.isEnableMusic() ? 1 : 0);
		sendConfig(206, player.isEnableSound() ? 1 : 0);
		sendConfig(172, player.isAutoRetaliating() ? 1 : 0);
		sendConfig(152, player.getWalkingQueue().isRunningToggled() ? 1 : 0);
		sendString(player.getRunEnergy() + "%", 149);
		sendRunEnergy();
		Emotes.update(player);
		BarrowsHandler.getSingleton().updateOverlayInterface(player);
	}

	public void updateAfterLogin() {
		Server.getTaskScheduler().schedule(new Task(2) {

			@Override
			public void execute() {
				if (player == null || !player.isActive()) {
					stop();
					return;
				}
				
				int pk_points = player.getPkPoints();
				if(pk_points > 0) {
					player.getActionSender().sendMessage("[Server]: Your PK points where replaced by our new blood money currency.");
					player.setPkPoints(0);
					player.getInventory().addOrSentToBank(player, new Item(13307, pk_points));
				}

				if (!player.receivedStarter()) {
					player.getDialogueManager().start("STARTER");
					PlayerUpdating.executeGlobalMessage("<col=255>" + Utility.capitalize(player.getUsername()) + "</col> Has joined The Wildy for the first time.");
				}

				// We can update our kills tracker after login
				player.getKillTracker().loadDefault();

				// Update the quest tab info
				QuestTabPageHandler.write(player, QuestTabPages.HOME_PAGE);

				// If we had a pet spawned, we spawn it after the login protocol
				if (player.getPet() > -1)
					Follower.drop(player, null, true);

				// If we're a Owner we choose to play in debug mode
				if (player.getRights().isOwner(player) && !player.getUsername().equalsIgnoreCase("Matthew")) {
					player.setDebugMode(true);
				}
				
				//Update favorite teleports
				player.getTeleportMenuHandler().updateFavorites();

				// If the player is not in a clan chat we'll add them in the
				// server clan chat
				if (player.getClan() != null && Server.getClanManager().clanExists(player.getClan().getFounder())) {
					player.getClan().addMember(player);
				} else {
					Clan.resetInterface(player);
				}

				// Update farming
				player.getFarming().doCalculations();
				player.getFarming().getAllotment().updateAllotmentsStates();
				player.getFarming().getHerbs().updateHerbsStates();
				player.getFarming().getTrees().updateTreeStates();
				player.getFarming().getFruitTrees().updateFruitTreeStates();
				player.getFarming().getFlowers().updateFlowerStates();
				player.getFarming().getSpecialPlantOne().updateSpecialPlants();
				player.getFarming().getSpecialPlantTwo().updateSpecialPlants();
				player.getFarming().getHops().updateHopsStates();
				player.getFarming().getBushes().updateBushesStates();

				for (int i = 0; i < 4; i++) {
					player.getFarming().getCompost().updateCompostBin(i);
				}
				this.stop();
			}
		}.attach(this));
	}

	//This one packet 8
	public ActionSender sendMediaInterface(int mediaType, int mediaId) {
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().writeFrame(8); // not sure why it needs a custom size
			player.getOutStream().writeWordBigEndianA(mediaType);
			player.getOutStream().writeShort(mediaId);
			player.flushOutStream();
		}
		return this;
	}
	
	public ActionSender setModelOSVEOS(int interfaceId, int modelId) {
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().writeFrame(8);
			player.getOutStream().writeWordBigEndianA(interfaceId);
			player.getOutStream().writeShort(modelId);
			player.flushOutStream();
		}
		return this;
	}
	
	public ActionSender refreshContainer(int frameId) {
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrameVarSizeWord(53);
			player.getOutStream().writeShort(frameId);
			player.getOutStream().writeShort(InventoryContainer.SIZE);
			for (int i = 0; i < InventoryContainer.SIZE; i++) {
				if (player.getInventory().get(i).getAmount() > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(player.getInventory().get(i).getAmount());
				} else {
					player.getOutStream().writeByte(player.getInventory().get(i).getAmount());
				}
				player.getOutStream().writeWordBigEndianA(player.getInventory().get(i).getId() < 1 ? 0 : player.getInventory().get(i).getId() + 1);
			}
			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}
		return this;
	}

}
