package com.model.net.packet;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.combat.PrayerHandler;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.weapon.AttackStyle;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.KillTracker;
import com.model.game.character.player.content.clan.ClanManager;
import com.model.game.character.player.content.questtab.QuestTabPageHandler;
import com.model.game.character.player.content.questtab.QuestTabPages;
import com.model.game.character.player.serialize.PlayerSerialization;
import com.model.game.item.Item;
import com.model.game.item.container.InterfaceConstants;
import com.model.game.item.ground.GroundItem;
import com.model.game.location.Location;
import com.model.net.network.rsa.GameBuffer;
import com.model.server.Server;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

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
     * @param player The player to create the action sender for.
     */
    public ActionSender(Player player) {
        this.player = player;
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
    	player.getOutStream().writeFrame(175);
    	if (entity_name == null) {
			entity_name = "null";
		}
    	player.getOutStream().putRS2String(entity_name);
    	player.getOutStream().writeShort(hp);
		player.getOutStream().writeShort(max_hp);
    	player.flushOutStream();
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
	 * @return 
	 * 1 - Anti-fire 
	 * 2 - venge 
	 * 3 - barrage
	 * 4 - teleblock
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
		player.getOutStream().writeWordA(player.getLocation().getRegionX() + 6);
		player.getOutStream().writeShort(player.getLocation().getRegionY() + 6);
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

	public ActionSender changeSidebar(int id) {
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
	
	public ActionSender addClanMember(String username) {
		if (player.getOutStream() != null) {
            player.getOutStream().putFrameVarByte(216);
            int offset = player.getOutStream().offset;
            player.getOutStream().putRS2String(username);
            player.getOutStream().putFrameSizeByte(offset);
        }
        return this;
	}
	
	public ActionSender sendClanMessage(String member, String message, String clan, int rights) {
		if (player.getOutStream() != null) {
            player.getOutStream().putFrameVarShort(217);
            int offset = player.getOutStream().offset;
            player.getOutStream().putRS2String(member);
            player.getOutStream().putRS2String(message);
            player.getOutStream().putRS2String(clan);
            player.getOutStream().writeShort(rights);
            player.getOutStream().putFrameSizeShort(offset);
        }
		return this;
	}
	
	public ActionSender removeClanMember(String username) {
		if (player.getOutStream() != null) {
			player.getOutStream().putFrameVarByte(213);
			int offset = player.getOutStream().offset;
			player.getOutStream().putRS2String(username);
			player.getOutStream().putFrameSizeByte(offset);
		}
		return this;
	}
	
	public ActionSender sendString(String message, int interfaceId) {
		if (player.getOutStream() != null) {
			player.getOutStream().putFrameVarShort(126);
			int offset = player.getOutStream().offset;
			player.getOutStream().putRS2String(message == null ? "" : message);
			player.getOutStream().writeWordA(interfaceId);
			player.getOutStream().putFrameSizeShort(offset);
		}
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
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(99);
			player.getOutStream().writeByte(state.getCode());
		}
		return this;
	}
	
	public ActionSender drawHeadIcon(int i, int j, int k, int l) {
		player.outStream.writeFrame(254);
		player.outStream.writeByte(i);

		if (i == 1 || i == 10) {
			player.outStream.writeShort(j);
			player.outStream.writeShort(k);
			player.outStream.writeByte(l);
		} else {
			player.outStream.writeShort(k);
			player.outStream.writeShort(l);
			player.outStream.writeByte(j);
		}
		return this;
	}
	
	/**
	 * Sends all the sidebar interfaces.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSidebarInterfaces() {
		int[] interfaces = { 2423, 3917, 638, 3213, 1644, 5608, -1, 18128, 5065, 5715, 2449, 904, 147, -1, -1 };//15
		for (int i = 0; i < 15; i++) {
			sendSidebarInterface(i, interfaces[i]);
		}
		return this;
	}
	
	public ActionSender hideAllSideBars() {
		for (int i = 0; i < 14; i++)
			sendSidebarInterface(i, -1);
		return this;
	}
	
	public ActionSender createObjectHint(int x, int y, int height, int pos) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(254);
			player.getOutStream().writeByte(pos);
			player.getOutStream().writeShort(x);
			player.getOutStream().writeShort(y);
			player.getOutStream().writeByte(height);
		}
		return this;
	}
	
	public ActionSender createPlayerHint(int type, int id) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(254);
			player.getOutStream().writeByte(type);
			player.getOutStream().writeShort(id);
			player.getOutStream().write3Byte(0);
		}
		return this;
	}
	
	public ActionSender sendEnterStringInterface() {
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(187);
        }
		return this;
	}
	
	/**
	 * Sends the enter amount interface.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendEnterAmountInterface(int interfaceId, Item item) {
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(27);
        }
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
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().putFrameVarByte(104);
			int offset = player.getOutStream().offset;
			player.getOutStream().writeByte((byte) -slot);
			player.getOutStream().putByteA(top ? (byte) 0 : (byte) 1);
			player.getOutStream().putRS2String(option);
			player.getOutStream().putFrameSizeByte(offset);
			player.flushOutStream();
		}
		return this;
	}
	
	public ActionSender sendInterfaceConfig(int interfaceId, int state) {
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(171);
            player.getOutStream().writeByte(interfaceId);
            player.getOutStream().writeShort(state);
        }
		return this;
	}
	
	/**
     * zoom is usually 175
     */
	public ActionSender sendItemOnInterface(int id, int zoom, int model) {
		if (player.getOutStream() != null && player != null) {
            player.getOutStream().writeFrame(246);
            player.getOutStream().writeWordBigEndian(id);
            player.getOutStream().writeShort(zoom);
            player.getOutStream().writeShort(model);
        }
		return this;
	}
	
	public ActionSender sendChatInterface(int frame) {
		player.stopSkillTask();
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(164);
			player.getOutStream().writeWordBigEndian_dup(frame);
		}
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
	
	public ActionSender clearItemOnInterface(int frame) {
		player.outStream.putFrameVarShort(72);
        int offset = player.getOutStream().offset;
        player.outStream.writeShort(frame);
		player.outStream.putFrameSizeShort(offset);
		return this;
	}
	
	public ActionSender sendMultiway(int icon) {
		if (player != null) {
            player.outStream.writeFrame(61);
            player.outStream.writeByte(icon);
            player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
        }
		return this;
	}
	
	public ActionSender sendPm(long name, int rights, byte[] chatMessage, int messageSize) {
        if (player.getOutStream() != null) {
            player.getOutStream().putFrameVarByte(196);
            int offset = player.getOutStream().offset;
            player.getOutStream().putLong(name);
            player.getOutStream().putInt(player.lastChatId++);
            player.getOutStream().writeByte(rights);
            player.getOutStream().writeBytes(chatMessage, messageSize, 0);
            player.getOutStream().putFrameSizeByte(offset);
            Utility.textUnpack(chatMessage, messageSize);
            Utility.longToPlayerName(name);
        }
		return this;
	}
	
	public ActionSender removeAllInterfaces() {
		player.getInterfaceState().interfaceClosed();
		player.getOutStream().writeFrame(219);
		return this;
	}
	
	public ActionSender sendStringColor(int stringId, int color) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(122);
			player.getOutStream().writeWordBigEndianA(stringId);
			player.getOutStream().writeWordBigEndianA(color);	
		}
		return this;
	}
	
	public ActionSender clearScreen() {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(107);
		}
		return this;
	}
	
	public ActionSender sendFrame87(int id, int state) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(87);
			player.getOutStream().writeWordBigEndian_dup(id);
			player.getOutStream().writeDWord_v1(state);
		}
		return this;
	}
	
	public ActionSender sendExperienceCounter(int skill, int experience) {
		GameBuffer stream = player.getOutStream();
    	stream.writeFrame(127);
    	stream.writeByte(skill);
    	stream.putInt(experience);
    	stream.putInt(player.getSkills().getExpCounter());
    	//System.out.println("skill: "+skill+ " exp given "+experience);
    	player.flushOutStream();
		return this;
	}
	
	public ActionSender moveComponent(int x, int y, int componentId) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(70);
			player.getOutStream().writeShort(x);
			player.getOutStream().writeWordBigEndian(y);
			player.getOutStream().writeWordBigEndian(componentId);
		}
		return this;
	}
	
	/**
	 * Changes the main displaying sprite on an interface. The index represents
	 * the location of the new sprite in the index of the sprite array.
	 * 
	 * @param componentId	the interface
	 * @param index			the index in the array
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
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(248);
			player.getOutStream().writeWordA(interfaceId);
			player.getOutStream().writeShort(overlay);
			player.getInterfaceState().interfaceOpened(interfaceId);
		}
		return this;
	}
	
	/**
	 * Sends some information to the client about screen fading. 
	 * @param text		the text that will be displayed in the center of the screen
	 * @param state		the state should be either 0, -1, or 1. 
	 * @param seconds	the amount of time in seconds it takes for the fade
	 * to transition.
	 * <p>
	 * If the state is -1 then the screen fades from black to transparent.
	 * When the state is +1 the screen fades from transparent to black. If 
	 * the state is 0 all drawing is stopped.
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
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(204);
            player.getOutStream().putInt(scrollbar);
            player.getOutStream().putInt(size);
        }
		return this;
	}
	
	public ActionSender sendShakeScreen(int verticleAmount, int verticleSpeed, int horizontalAmount, int horizontalSpeed) {
		if (player != null && player.getOutStream() != null) {
	    	player.outStream.writeFrame(35);
	        player.outStream.writeByte(verticleAmount);
	        player.outStream.writeByte(verticleSpeed);
	        player.outStream.writeByte(horizontalAmount);
	        player.outStream.writeByte(horizontalSpeed);
        }
		return this;
	}
	
	/**
	 * Sends a packet to update a group of items.
	 * 
	 * @param id
	 *             The interface id.
	 * @param items
	 *             The items.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendItemOnInterface(int id, Item... items) {
		if (player.getOutStream() != null && player != null) {
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
		}
		return this;
	}
	
	public ActionSender sendObject(int id, int x, int y, int h, int face, int objectType) {
        if (player.getOutStream() != null) {
        	sendCoordinates(Location.create(x, y, h));
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
        }
        return this;
    }
	
	public ActionSender sendCoordinates(Location location) {
		player.getOutStream().writeFrame(85);
		int y = location.getY() - player.getLocation().getRegionY() * 8;
		int x = location.getX() - player.getLocation().getRegionX() * 8;
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
		sendCoordinates(groundItem.getPosition());
		player.getOutStream().writeFrame(44);
		player.getOutStream().writeWordBigEndianA(groundItem.getItem().getId());
		player.getOutStream().writeShort(groundItem.getItem().getAmount());
		player.getOutStream().writeByte(0);
		player.flushOutStream();
		return this;
	}
	
	public ActionSender sendRemoveGroundItem(GroundItem groundItem) {
		sendCoordinates(groundItem.getPosition());
		player.getOutStream().writeFrame(156);
		player.getOutStream().writeByteS(0);
		player.getOutStream().writeShort(groundItem.getItem().getId());
		player.flushOutStream();
		return this;
	}
	
	public ActionSender sendStillGFX(int id, int x, int y, int height, int time) {
		if (id >= 65535) {
			throw new IllegalArgumentException("Identification value for the still graphic is prohibited; " + id);
		}
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(85);
			player.getOutStream().writeByteC(y - (player.getLocation().getRegionY() * 8));
			player.getOutStream().writeByteC(x - (player.getLocation().getRegionX() * 8));
			
			//Still gfx packet
			player.getOutStream().writeFrame(4);
			player.getOutStream().writeByte(0);
			player.getOutStream().writeShort(id);
			player.getOutStream().writeByte(height);
			player.getOutStream().writeShort(time);
		}
		return this;
	}
	
	/**
     * Sends a projectile to a location.
     *
     * @param start       The starting location.
     * @param finish      The finishing location.
     * @param id          The graphic id.
     * @param delay       The delay before showing the projectile.
     * @param angle       The angle the projectile is coming from.
     * @param speed       The speed the projectile travels at.
     * @param startHeight The starting height of the projectile.
     * @param endHeight   The ending height of the projectile.
     * @param lockon      The lockon index of the projectile, so it follows them if they
     *                    move.
     * @param slope       The slope at which the projectile moves.
     * @param radius      The radius from the centre of the tile to display the
     *                    projectile from.
     * @return The action sender instance, for chaining.
     */
    public ActionSender sendProjectile(Location start, Location finish, int id, int delay, int angle, int speed, int startHeight, int endHeight, int slope, int radius, int lockon) {
    	int offsetX = (start.getX() - finish.getX()) * -1;
		int offsetY = (start.getY() - finish.getY()) * -1;

        sendLocalCoordinates(start, -3, -2);
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
        //player.sendMessage("dif "+offsetX+"|"+offsetY+" from "+start+" to "+finish+" dist "+start.distance(finish));
        return this;
    }
	
	public ActionSender sendLocalCoordinates(Location position, int xOffset, int yOffset) {
		player.getOutStream().writeFrame(85);
		
		int difx = position.getX(), dify = position.getY();
		int regionX = player.getLocation().getRegionX(), regionY = player.getLocation().getRegionY();
		player.getOutStream().writeByteC((dify - (regionY * 8)) + yOffset);
		player.getOutStream().writeByteC((difx - (regionX * 8)) + xOffset);

		player.flushOutStream();
		return this;
	}
    
	public ActionSender sendMessage(String message) {
		if (player.getOutStream() != null) {
			player.outStream.putFrameVarByte(253);
			int offset = player.getOutStream().offset;
			player.outStream.putRS2String(message);
			player.outStream.writeByte(0);
			player.outStream.putFrameSizeByte(offset);
		}
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
		if (player.getOutStream() != null && player != null) {
			//System.out.println("Varp update will be opcode "+(state<128 ? OPCODE:87)+" based on state:"+state);
			if (state < 128) {
				player.getOutStream().writeFrame(36);
				player.getOutStream().writeWordBigEndian(id);
				player.getOutStream().writeByte(state);
			} else {
				player.getOutStream().writeFrame(87);
				player.getOutStream().writeWordBigEndian_dup(id);
				player.getOutStream().writeDWord_v1(state);
			}
		}
		return this;
	}
	
	public ActionSender sendStrings(int startId, int endId, String[] strings) {
		player.getOutStream().createFrameVarSizeWord(229);
		player.getOutStream().writeWord(startId);
		player.getOutStream().writeWord(endId);
		player.getOutStream().writeByte(strings.length);
		for (String s : strings)
			player.getOutStream().putRS2String(s);
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
		return this;
	}
	
	public ActionSender sendFriendServerStatus(final int status) {
		if (this.player.getOutStream() != null && this.player != null) {
            this.player.getOutStream().writeFrame(221);
            this.player.getOutStream().writeByte(status);
        }
		return this;
	}

	public ActionSender sendSkillLevel(int skill) {
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(134);
            player.getOutStream().writeByte((byte) skill);
            player.getOutStream().writeDWord_v1((int) player.getSkills().getExperience(skill));
            player.getOutStream().writeByte((byte) player.getSkills().getLevel(skill));
            //System.out.printf("skill - %s exp %s lvl %s %n", skill, player.getSkills().getExperience(skill), player.getSkills().getLevel(skill));
        }
		return this;
	}
	
	/**
	 * Sends a packet to the client in order to open an interface
	 * 
	 * @param id
	 *            The interface
	 */
	public ActionSender sendInterface(int id) {
		if (player != null) {
        	player.stopSkillTask();
            if (player.getOutStream() != null) {
                player.getOutStream().writeFrame(97);
                player.getOutStream().writeShort(id);
                player.getInterfaceState().interfaceOpened(id);
            }
        }
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
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(208);
			player.getOutStream().writeWordBigEndian_dup(id);
		}
		return this;
	}
	
	public ActionSender sendSidebarInterface(int menu, int id) {
		if (player != null) {
			player.stopSkillTask();
			if (player.getOutStream() != null) {
				player.outStream.writeFrame(71);
				player.outStream.writeShort(id);
				player.outStream.putByteA(menu);
			}
			player.flushOutStream();
		}
		return this;
	}
	
	public ActionSender sendPlayerHeadToInterface(int id) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(185);
			player.getOutStream().writeWordBigEndianA(id);
		}
		return this;
	}
	
	public ActionSender sendNpcHeadToInterface(int id, int child) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(75);
			player.getOutStream().writeWordBigEndianA(id);
			player.getOutStream().writeWordBigEndianA(child);
		}
		return this;
	}
	
	public ActionSender sendInterfaceAnimation(int id, int child) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(200);
			player.getOutStream().writeShort(id);
			player.getOutStream().writeShort(child);
		}
		return this;
	}
	
	public ActionSender sendFriend(long username, int world) {
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(50);
            player.getOutStream().putLong(username);
            player.getOutStream().writeByte(world);
        }
		return this;
	}
	
	public ActionSender sendChatBoxInterface(int id) {
		player.stopSkillTask();
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(164);
			player.getOutStream().writeWordBigEndian_dup(id);
		}
		return this;
	}
	
	/**
	 * Sends all the login packets.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendLogin() {
		//Activate our players session
		player.setActive(true);
		
		//Update the players details
		try {
			if (!PlayerSerialization.load(player)) {
				player.setNewPlayer(true);
				player.setTutorial(true);
			} else if (PlayerSerialization.load(player)) {
				player.setNewPlayer(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Finalize our ignore and friends list
		player.getFAI().handleLogin();
		
		//Update the map packet
		sendMapRegionPacket();
		
		//We can go ahead and update out sidebars
		sendSidebarInterfaces();
		
		//Update the magic book
		if (player.getSpellBook() == SpellBook.ANCIENT) {
			sendSidebarInterface(6, 12855);
		} else if (player.getSpellBook() == SpellBook.MODERN) {
			sendSidebarInterface(6, 1151);
		} else if (player.getSpellBook() == SpellBook.LUNAR) {
			sendSidebarInterface(6, 29999);
		}
		
		//Reset prayers
		PrayerHandler.resetAllPrayers(player);
		
		//unlock/lock special case prayers
		sendConfig(709, PrayerHandler.canActivate(player, Prayers.PRESERVE, false) ? 1 : 0);
		sendConfig(711, PrayerHandler.canActivate(player, Prayers.RIGOUR, false) ? 1 : 0);
		sendConfig(713, PrayerHandler.canActivate(player, Prayers.AUGURY, false) ? 1 : 0);
		
		//Update inventory
		player.getInventory().refresh();
		
		//Update equipment
		player.getEquipment().refresh();
		
		//Update the weapon attributes
		player.getWeaponInterface().restoreWeaponAttributes();
		
		//Send the interaction options
		sendInteractionOption("Follow", 4, true);
		sendInteractionOption("Trade With", 5, true);
		
		//We can go ahead and finalize the game configs
		updateConfigs();
		
		//Update the skills
		sendSkills();
		
		//Update our attack style
		AttackStyle.adjustAttackStyleOnLogin(player);
		
		sendMessage("Welcome to " + Constants.SERVER_NAME + ".");
		updateAfterLogin();
		
		//activate login delay
		player.setAttribute("login_delay", System.currentTimeMillis());
		return this;
	}
	
	public void updateConfigs() {
		player.setScreenBrightness((byte) 4);
		sendString("100%", 149);
		sendConfig(166, player.getScreenBrightness());
		sendConfig(207, player.isEnableMusic() ? 1 : 0);
		sendConfig(206, player.isEnableSound() ? 1 : 0);
		sendConfig(287, player.getSplitPrivateChat() ? 1 : 0);
		sendConfig(205, player.getSplitPrivateChat() ? 1 : 0);
		sendConfig(200, player.getAcceptAid() ? 1 : 0);
		sendConfig(172, player.isAutoRetaliating() ? 1 : 0);
		sendConfig(152, player.getWalkingQueue().isRunningToggled() ? 1 : 0);
	}
	
	public void updateAfterLogin() {
		Server.getTaskScheduler().schedule(new ScheduledTask(2) {

			@Override
			public void execute() {
				if (player == null || !player.isActive()) {
					stop();
					return;
				}
				
				//We are new so we start the tutorial
				if (!player.receivedStarter() && player.inTutorial()) {
					player.dialogue().start("STARTER");
				}
				
				//If the player is muted we tell them after they're logged in
				if (player.isMuted()) {
					sendMessage("You are currently muted. Other players will not see your chat messages.");
				}
				
				//We can update our kills tracker after login
				KillTracker.loadDefault(player);
				//Update the quest tab info
				QuestTabPageHandler.write(player, QuestTabPages.HOME_PAGE);
				
				//If we had a pet spawned, we spawn it after the login protocol
				if (player.getPet() > -1) {
		            Pet pet = new Pet(player, player.getPet());
		            player.setPet(player.getPet());
		            World.getWorld().register(pet);
		        }
				
				//If we're a Administrator we choose to play in debug mode
				if (player.getRights().isAdministrator()) {
					player.setDebugMode(true);
				}
				
				//If the player is not in a clan chat we'll add them in the server clan chat
				if (player.getTempKey() == null || player.getTempKey().equals("") || player.getTempKey().isEmpty()) {
					player.getActionSender().sendMessage("<col=ff0033>We noticed you aren't in a clanchat, so we added you to the community clanchat!");
					player.setTempKey("patrick");
				}
				if (player.getTempKey() != null) {
					ClanManager.joinClan(player, player.getTempKey());
				}
				this.stop();
			}
		}.attach(this));
	}

	public ActionSender sendRunEnergy() {
		player.getOutStream().writeFrame(110);
		player.getOutStream().writeByte((byte) player.getWalkingQueue().getEnergy());
		return this;
	}
	
}
