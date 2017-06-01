package com.model.game.character.player;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.location.Location;
import com.model.net.network.rsa.GameBuffer;
import com.model.net.packet.out.SendSidebarInterfacePacket;
import com.model.net.packet.out.SendWalkableInterfacePacket;
import com.model.utility.Utility;
import com.model.utility.cache.map.Region;

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
    
    public ActionSender sendMapRegionPacket() {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(73);
			player.getOutStream().writeWordA(player.mapRegionX + 6);
			player.getOutStream().writeShort(player.mapRegionY + 6);
			player.setLastKnownRegion(player.getLocation());
		}
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
		player.write(new SendWalkableInterfacePacket(8680));
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
			player.getActionSender().sendSkillLevel(i);
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
		int[] interfaces = { 2423, 3917, 638, 3213, 1644, 5608, -1, 18128, 5065, 5715, 2449, 36000, 147, -1, -1 };//15
		for (int i = 0; i < 15; i++) {
			player.write(new SendSidebarInterfacePacket(i, interfaces[i]));
		}
		if (player.getSpellBook() == SpellBook.ANCIENT) {
			player.write(new SendSidebarInterfacePacket(6, 12855));
		} else if (player.getSpellBook() == SpellBook.MODERN) {
			player.write(new SendSidebarInterfacePacket(6, 1151));
		} else if (player.getSpellBook() == SpellBook.LUNAR) {
			player.write(new SendSidebarInterfacePacket(6, 29999));
		}
		return this;
	}
	
	public ActionSender hideAllSideBars() {
		for (int i = 0; i < 14; i++)
			player.write(new SendSidebarInterfacePacket(i, -1));
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
     * @param open
     *            the interface to open.
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
	 * @param interfaceId
	 *             The interface id.
	 * @param items
	 *             The items.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItems(int interfaceId, Item... items) {
		player.getOutStream().putFrameVarShort(53);
		int offset = player.getOutStream().offset;
		player.getOutStream().writeShort(interfaceId);
		player.getOutStream().writeShort(items.length);
		for (Item item : items) {
			if (item != null && item.getId() > -1) {
				int count = item.getAmount();
				if (count > 254) {
					player.getOutStream().writeByte((byte) 255);
					player.getOutStream().writeDWord_v2(count);
				} else {
					player.getOutStream().writeByte((byte) count);
				}
				player.getOutStream().writeWordBigEndianA(item.getId() + 1);
			} else {
				player.getOutStream().writeByte((byte) 0);
				player.getOutStream().writeWordBigEndianA(0);
			}
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
		return this;
	}
	
	public ActionSender sendObject(int id, int x, int y, int h, int face, int objectType) {
        Region.addWorldObject(id, x, y, player.heightLevel);
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
		int y = location.getY() - player.getMapRegionY() * 8;
		int x = location.getX() - player.getMapRegionX() * 8;
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
			player.getOutStream().writeByteC(y - (player.getMapRegionY() * 8));
			player.getOutStream().writeByteC(x - (player.getMapRegionX() * 8));
			
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
        //player.getActionSender().sendMessage("dif "+offsetX+"|"+offsetY+" from "+start+" to "+finish+" dist "+start.distance(finish));
        return this;
    }
	
	public ActionSender sendLocalCoordinates(Location position, int xOffset, int yOffset) {
		player.getOutStream().writeFrame(85);
		
		int difx = position.getX(), dify = position.getY();
		int regionX = player.getMapRegionX(), regionY = player.getMapRegionY();
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
	 * @param configId
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
	
}
