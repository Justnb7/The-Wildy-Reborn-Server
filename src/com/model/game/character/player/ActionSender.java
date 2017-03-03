package com.model.game.character.player;

import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.player.packets.out.SendInterfacePacket;
import com.model.game.character.player.packets.out.SendSidebarInterfacePacket;
import com.model.game.character.player.packets.out.SendSkillPacket;
import com.model.net.network.rsa.GameBuffer;
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
     * Sends the player's skills.
     *
     * @return The action sender instance, for chaining.
     */
	public ActionSender sendSkills() {
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			player.write(new SendSkillPacket(i));
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
	
	public ActionSender disableMap(int state, int mapStatus) {
		if (player.getOutStream() != null) {
			if (mapStatus != state) {
				mapStatus = state;
				player.getOutStream().writeFrame(99);
				player.getOutStream().writeByte(state);
			}
		}
		return this;
	}

	public ActionSender displayReward(int first_item, int first_amount, int second_item, int second_amount, int third_item, int third_amount) {
		int[] items = { first_item, second_item, third_item };
		int[] amounts = { first_amount, second_amount, third_amount };
		player.outStream.putFrameVarShort(53);
		int offset = player.getOutStream().offset;
		player.outStream.writeShort(6963);
		player.outStream.writeShort(items.length);
		for (int i = 0; i < items.length; i++) {
			if (player.playerItemsN[i] > 254) {
				player.outStream.writeByte(255);
				player.outStream.writeDWord_v2(amounts[i]);
			} else {
				player.outStream.writeByte(amounts[i]);
			}
			if (items[i] > 0) {
				player.outStream.writeWordBigEndianA(items[i] + 1);
			} else {
				player.outStream.writeWordBigEndianA(0);
			}
		}
		player.outStream.putFrameSizeShort(offset);
		player.flushOutStream();
		player.getItems().addItem(first_item, first_amount);
		player.getItems().addItem(second_item, second_amount);
		player.getItems().addItem(third_item, third_amount);
		player.write(new SendInterfacePacket(3322));
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
		int[] interfaces = { 2423, 3917, 29400, 3213, 1644, 5608, -1, 18128, 5065, 5715, 2449, 36000, 147, -1, -1 };//15
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
	
	public ActionSender sendInterfaceModel(int id, int zoom, int model) {
		if (player.getOutStream() != null && player != null) {
            player.getOutStream().writeFrame(246);
            player.getOutStream().writeWordBigEndian(id);
            player.getOutStream().writeShort(zoom);
            player.getOutStream().writeShort(model);
        }
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
	
	public ActionSender sendMultiway(int icon) {
		if (player != null) {
            player.outStream.writeFrame(61);
            player.outStream.writeByte(icon);
            player.updateRequired = true;
            player.appearanceUpdateRequired = true;
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
	
	public ActionSender sendRemoveInterfacePacket() {
		player.openInterface = -1;
		player.setBanking(false);
		player.setTrading(false);
		player.setShopping(false);
		player.dialogue().interrupt();
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().writeFrame(219);
		}
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
	
	public ActionSender sendInterfaceWithInventoryOverlay(int mainFrame, int subFrame) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(248);
			player.getOutStream().writeWordA(mainFrame);
			player.getOutStream().writeShort(subFrame);
			player.openInterface = mainFrame;
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
	
}
