package com.model.game.character.player.dialogue;

import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.net.packet.out.SendChatBoxInterfacePacket;
import com.model.net.packet.out.SendInterfaceAnimationPacket;
import com.model.net.packet.out.SendNpcHeadToInterfacePacket;
import com.model.net.packet.out.SendPlayerHeadToInterfacePacket;

/**
 * Represents a single dialogue
 * 
 * @author Erik Eide
 */
public abstract class Dialogue {

	/**
	 * The default option for the choice type
	 */
	protected static final String DEFAULT_OPTION_TITLE = "Select an Option";

	/**
	 * The player sending the dialogue too
	 */
	protected Player player;

	/**
	 * The current phase of the dialogue, used to keep track of where you are
	 */
	protected int phase = 0;

	/**
	 * An overrideable method for what happens when the dialogue is closed
	 */
	public void finish() {
	}

	/**
	 * An overrideable method for inputing an integer value
	 * 
	 * @param value
	 *            The value to input
	 */
	protected void input(int value) {
	}

	/**
	 * An overrideable method for inputing a string value
	 * 
	 * @param value
	 *            The value to input
	 */
	protected void input(String value) {
	}

	/**
	 * An overrideable method for handling the next dialogue, if its not
	 * overriden, it will automatically stop the dialogue
	 */
	protected void next() {
		stop();
	}

	/**
	 * An overrideable method for selecting an option on a choice dialogue
	 * 
	 * @param index
	 *            The index of the choice, can be between index 1 to 5
	 */
	protected void select(int index) {
	}

	/**
	 * Sends a dialogue to the player
	 * 
	 * @param type
	 *            The type of dialogue to send
	 * @param parameters
	 *            The parameters for the dialogue
	 */
	protected void send(Type type, Object... parameters) {
		if (type == Type.CHOICE) {
			if (parameters.length == 3) {
				player.getActionSender().sendString(parameters[0] != null ? (String) parameters[0] : DEFAULT_OPTION_TITLE, 2460);
				player.getActionSender().sendString((String) parameters[1], 2461);
				player.getActionSender().sendString((String) parameters[2], 2462);
				player.write(new SendChatBoxInterfacePacket(2459));
			} else if (parameters.length == 4) {
				player.getActionSender().sendString(parameters[0] != null ? (String) parameters[0] : DEFAULT_OPTION_TITLE, 2470);
				player.getActionSender().sendString((String) parameters[1], 2471);
				player.getActionSender().sendString((String) parameters[2], 2472);
				player.getActionSender().sendString((String) parameters[3], 2473);
				player.write(new SendChatBoxInterfacePacket(2469));
			} else if (parameters.length == 5) {
				player.getActionSender().sendString(parameters[0] != null ? (String) parameters[0] : DEFAULT_OPTION_TITLE, 2481);
				player.getActionSender().sendString((String) parameters[1], 2482);
				player.getActionSender().sendString((String) parameters[2], 2483);
				player.getActionSender().sendString((String) parameters[3], 2484);
				player.getActionSender().sendString((String) parameters[4], 2485);
				player.write(new SendChatBoxInterfacePacket(2480));
			} else if (parameters.length == 6) {
				player.getActionSender().sendString(parameters[0] != null ? (String) parameters[0] : DEFAULT_OPTION_TITLE, 2493);
				player.getActionSender().sendString((String) parameters[1], 2494);
				player.getActionSender().sendString((String) parameters[2], 2495);
				player.getActionSender().sendString((String) parameters[3], 2496);
				player.getActionSender().sendString((String) parameters[4], 2497);
				player.getActionSender().sendString((String) parameters[5], 2498);
				player.write(new SendChatBoxInterfacePacket(2492));
			} else {
				throw new IllegalArgumentException("Invalid Arguements");
			}
		} else if (type == Type.ITEM) {
			if (parameters.length == 3) {
				player.getActionSender().sendString((String) parameters[1], 4884);
				player.getActionSender().sendString((String) parameters[2], 4885);
				player.getActionSender().sendItemOnInterface(4883, 250, (Integer) parameters[0]);
				player.write(new SendChatBoxInterfacePacket(4882));
			} else if (parameters.length == 4) {
				player.getActionSender().sendString((String) parameters[1], 4889);
				player.getActionSender().sendString((String) parameters[2], 4890);
				player.getActionSender().sendString((String) parameters[3], 4891);
				player.getActionSender().sendItemOnInterface(4888, 250, (Integer) parameters[0]);
				player.write(new SendChatBoxInterfacePacket(4887));
			} else if (parameters.length == 5) {
				player.getActionSender().sendString((String) parameters[1], 4895);
				player.getActionSender().sendString((String) parameters[2], 4896);
				player.getActionSender().sendString((String) parameters[3], 4897);
				player.getActionSender().sendString((String) parameters[4], 4898);
				player.getActionSender().sendItemOnInterface(4894, 250, (Integer) parameters[0]);
				player.write(new SendChatBoxInterfacePacket(4893));
			} else if (parameters.length == 6) {
				player.getActionSender().sendString((String) parameters[1], 4902);
				player.getActionSender().sendString((String) parameters[2], 4903);
				player.getActionSender().sendString((String) parameters[3], 4904);
				player.getActionSender().sendString((String) parameters[4], 4905);
				player.getActionSender().sendString((String) parameters[5], 4906);
				player.getActionSender().sendItemOnInterface(4901, 250, (Integer) parameters[0]);
				player.write(new SendChatBoxInterfacePacket(4900));
			}
		} else if (type == Type.NPC) {
			if (parameters.length == 3) {
				player.getActionSender().sendString(NPC.getName((Integer) parameters[0]), 4884);
				player.getActionSender().sendString((String) parameters[2], 4885);
				player.write(new SendInterfaceAnimationPacket(4883, ((Expression) parameters[1]).getEmoteId()));
				player.write(new SendNpcHeadToInterfacePacket((Integer) parameters[0], 4883));
				player.write(new SendChatBoxInterfacePacket(4882));
			} else if (parameters.length == 4) {
				player.getActionSender().sendString(NPC.getName((Integer) parameters[0]), 4889);
				player.getActionSender().sendString((String) parameters[2], 4890);
				player.getActionSender().sendString((String) parameters[3], 4891);
				player.write(new SendInterfaceAnimationPacket(4888, ((Expression) parameters[1]).getEmoteId()));
				player.write(new SendNpcHeadToInterfacePacket((Integer) parameters[0], 4888));
				player.write(new SendChatBoxInterfacePacket(4887));
			} else if (parameters.length == 5) {
				player.getActionSender().sendString(NPC.getName((Integer) parameters[0]), 4895);
				player.getActionSender().sendString((String) parameters[2], 4896);
				player.getActionSender().sendString((String) parameters[3], 4897);
				player.getActionSender().sendString((String) parameters[4], 4898);
				player.write(new SendInterfaceAnimationPacket(4894, ((Expression) parameters[1]).getEmoteId()));
				player.write(new SendNpcHeadToInterfacePacket((Integer) parameters[0], 4894));
				player.write(new SendChatBoxInterfacePacket(4893));
			} else if (parameters.length == 6) {
				player.getActionSender().sendString(NPC.getName((Integer) parameters[0]), 4902);
				player.getActionSender().sendString((String) parameters[2], 4903);
				player.getActionSender().sendString((String) parameters[3], 4904);
				player.getActionSender().sendString((String) parameters[4], 4905);
				player.getActionSender().sendString((String) parameters[5], 4906);
				player.write(new SendInterfaceAnimationPacket(4901, ((Expression) parameters[1]).getEmoteId()));
				player.write(new SendNpcHeadToInterfacePacket((Integer) parameters[0], 4901));
				player.write(new SendChatBoxInterfacePacket(4900));
			} else {
				throw new InternalError();
			}
		} else if (type == Type.PLAYER) {
			if (parameters.length == 2) {
				player.getActionSender().sendString(player.getName(), 970);
				player.getActionSender().sendString((String) parameters[1], 971);
				player.write(new SendInterfaceAnimationPacket(969, ((Expression) parameters[0]).getEmoteId()));
				player.write(new SendPlayerHeadToInterfacePacket(969));
				player.write(new SendChatBoxInterfacePacket(968));
			} else if (parameters.length == 3) {
				player.getActionSender().sendString(player.getName(), 975);
				player.getActionSender().sendString((String) parameters[1], 976);
				player.getActionSender().sendString((String) parameters[2], 977);
				player.write(new SendInterfaceAnimationPacket(974, ((Expression) parameters[0]).getEmoteId()));
				player.write(new SendPlayerHeadToInterfacePacket(974));
				player.write(new SendChatBoxInterfacePacket(973));
			} else if (parameters.length == 4) {
				player.getActionSender().sendString(player.getName(), 981);
				player.getActionSender().sendString((String) parameters[1], 982);
				player.getActionSender().sendString((String) parameters[2], 983);
				player.getActionSender().sendString((String) parameters[3], 984);
				player.write(new SendInterfaceAnimationPacket(980, ((Expression) parameters[0]).getEmoteId()));
				player.write(new SendPlayerHeadToInterfacePacket(980));
				player.write(new SendChatBoxInterfacePacket(979));
			} else if (parameters.length == 5) {
				player.getActionSender().sendString(player.getName(), 988);
				player.getActionSender().sendString((String) parameters[1], 989);
				player.getActionSender().sendString((String) parameters[2], 990);
				player.getActionSender().sendString((String) parameters[3], 991);
				player.getActionSender().sendString((String) parameters[4], 992);
				player.write(new SendInterfaceAnimationPacket(987, ((Expression) parameters[0]).getEmoteId()));
				player.write(new SendPlayerHeadToInterfacePacket(987));
				player.write(new SendChatBoxInterfacePacket(986));
			} else {
				throw new InternalError();
			}
		} else if (type == Type.STATEMENT) {
			if (parameters.length == 1) {
				player.getActionSender().sendString("Click here to continue", 358);
				player.getActionSender().sendString((String) parameters[0], 357);
				player.write(new SendChatBoxInterfacePacket(356));
			} else if (parameters.length == 2) {
				player.getActionSender().sendString("Click here to continue", 362);
				player.getActionSender().sendString((String) parameters[0], 360);
				player.getActionSender().sendString((String) parameters[1], 361);
				player.write(new SendChatBoxInterfacePacket(359));
			} else if (parameters.length == 5) {
				player.getActionSender().sendString((String) parameters[0], 6180);
				player.getActionSender().sendString((String) parameters[1], 6181);
				player.getActionSender().sendString((String) parameters[2], 6182);
				player.getActionSender().sendString((String) parameters[3], 6183);
				player.getActionSender().sendString((String) parameters[4], 6184);
				player.write(new SendChatBoxInterfacePacket(6179));
			} else {
				throw new InternalError();
			}
		} else if (type == Type.STRING_INT) {
			player.getOutStream().writeFrame(219);
			player.getOutStream().writeFrame(27);
			player.flushOutStream();
		} else if (type == Type.STRING_TEXT) {
			player.getOutStream().writeFrame(219);
			player.getOutStream().writeFrame(187);
			player.flushOutStream();
		} else {
			throw new InternalError();
		}
	}

	/**
	 * Returns if this current phase is active
	 * 
	 * @param phase
	 *            The phase to check
	 * @return If the current phase matches the provided phase
	 */
	public boolean isPhase(int phase) {
		return getPhase() == phase;
	}

	/**
	 * Starts the dialogue for the player
	 * 
	 * @param parameters
	 *            The parameters to pass on to the dialogue
	 */
	protected abstract void start(Object... parameters);

	/**
	 * Stops the current dialogue where it is
	 */
	protected final void stop() {
		player.getActionSender().sendRemoveInterfacePacket();
	}

	/**
	 * Gets the current phase of the dialogue
	 * 
	 * @return The current phase of the dialogue
	 */
	public int getPhase() {
		return phase;
	}

	/**
	 * Sets the current phase of the dialogue
	 * 
	 * @param phase
	 *            The current phase of the dialogue
	 */
	public void setPhase(int phase) {
		this.phase = phase;
	}

}