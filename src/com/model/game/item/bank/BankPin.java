package com.model.game.item.bank;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendRemoveInterface;
import com.model.game.character.player.packets.out.SendInterface;
import com.model.game.character.player.packets.out.SendInterfaceConfig;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.player.packets.out.SendString;

/**
 * 
 * @author Jason MacKeigan
 * @date July 10th, 2014, 2:41:21 PM
 */
public class BankPin {

	private Player player;
	private String pin = "";
	private boolean locked = true;
	private long cancellationDelay = -1;
	private boolean appendingCancellation;
	private int attempts;
	private PinState pinState;

	public BankPin(Player player) {
		this.player = player;
	}

	public enum PinState {
		CREATE_NEW, UNLOCK, CANCEL_PIN, CANCEL_REQUEST
	};

	public void open(int state) {
		player.write(new SendString("", 39507));
		switch (state) {
		case 1:
			pinState = PinState.CREATE_NEW;
			player.write(new SendString("You do not have a pin set.", 39503));
			player.write(new SendString("Choose any 4-8 character combination.", 39504));
			player.write(new SendString("Make sure caps lock isn't enabled.", 39505));
			player.write(new SendString("Press enter to continue", 39506));
            player.write(new SendInterfaceConfig(1, 39511));
			break;
		case 2:
			pinState = PinState.UNLOCK;
			player.write(new SendString("You currently have a pin set.", 39503));
			player.write(new SendString("Type in your 4-8 character combination.", 39504));
			player.write(new SendString("Hit enter after you've typed your pin.", 39505));
			player.write(new SendString("Press the button to continue", 39506));
            player.write(new SendInterfaceConfig(1, 39511));
			break;
		case 3:
			pinState = PinState.CANCEL_PIN;
			player.write(new SendString("If you wish to cancel your pin, ", 39503));
			player.write(new SendString("click the button below. If not", 39504));
			player.write(new SendString("click the x button in the corner.", 39505));
			player.write(new SendString("Press the button to continue", 39506));
            player.write(new SendInterfaceConfig(0, 39511));
			break;
		case 4:
			pinState = PinState.CANCEL_REQUEST;
			player.write(new SendString("Your current pin cancellation is", 39503));
			player.write(new SendString("pending. Press continue to cancel", 39504));
			player.write(new SendString("this and keep your bank pin.", 39505));
			player.write(new SendString("Press the button to continue", 39506));
            player.write(new SendInterfaceConfig(1, 39511));
			break;
		}
		player.write(new SendInterface(39500));
	}

	public void create(String pin) {
		if (this.pin.length() > 0) {
			player.write(new SendMessagePacket("You already have a pin, you cannot create another one."));
			return;
		}
		if (pin.length() < 4) {
			player.write(new SendMessagePacket("Your pin must be atleast 4 characters in length."));
			return;
		}
		if (pin.length() > 8) {
			player.write(new SendMessagePacket("Your pin cannot be longer than 8 characters in length."));
			return;
		}
		if (!pin.matches("[A-Za-z0-9]+")) {
			player.write(new SendMessagePacket("Your bank pin contains illegal characters. Pins can only contain numbers,"));
			player.write(new SendMessagePacket("and uppercase, and lowercase case letters."));
			return;
		}
		if (pin.contains(" ")) {
			player.write(new SendMessagePacket("Your bank pin contains 1 or more spaces, bank pins cannot contain spaces."));
			return;
		}
		if (pin.equalsIgnoreCase(player.getName())) {
			player.write(new SendMessagePacket("Your bank pin cannot match your username."));
			return;
		}
		player.write(new SendMessagePacket("You have sucessfully created a bank pin. We urge you to keep this combination"));
		player.write(new SendMessagePacket("to yourself as sharing it may jepordize the items you have in your bank."));
		this.pin = pin;
		this.locked = true;
		this.attempts = 0;
	}

	public void unlock(String pin) {
		if (!this.locked) {
			return;
		}
		if (!this.pin.equals(pin)) {
			player.write(new SendMessagePacket("The pin you entered does not match your current bank pin, please try again."));
			this.attempts++;
			return;
		}
		this.player.write(new SendRemoveInterface());
		this.attempts = 0;
		this.locked = false;
		this.player.playerStun = false;
		this.player.attackable = false;
		this.player.inTask = false;
		player.write(new SendMessagePacket("You have successfully entered your " + this.pin.length() + " character pin"));
		player.aggressionTolerance.reset();
		// this.update();
	}

	public void cancel(String pin) {
		if (!this.pin.equals(pin)) {
			player.write(new SendMessagePacket("The pin you entered does not match your current bank pin, please try again."));
			this.attempts++;
			return;
		}
		if (this.pinState == PinState.CANCEL_PIN) {
			this.setAppendingCancellation(true);
			this.update();
		} else if (this.pinState == PinState.CANCEL_REQUEST) {
			this.setAppendingCancellation(false);
			this.cancellationDelay = -1;
			this.player.write(new SendMessagePacket("Your pin is no longer going to be cancelled."));
		}
	}

	public void update() {
		if (this.appendingCancellation) {
			this.pin = "";
			this.cancellationDelay = -1;
			this.attempts = 0;
			this.locked = false;
			this.appendingCancellation = false;
			this.setAppendingCancellation(false);
			player.write(new SendMessagePacket("Your pin has successfully been reset. If you wish to set another pin, you may do so."));
		} else
			player.write(new SendMessagePacket("Your pin is still pending its cancellation and will be reset 3 days after the initial date."));
	}

	public boolean requiresUnlock() {
		return locked && pin.length() > 0;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public long getCancellationDelay() {
		return cancellationDelay;
	}

	public void setCancellationDelay(long cancellationDelay) {
		this.cancellationDelay = cancellationDelay;
	}

	public boolean isAppendingCancellation() {
		return appendingCancellation;
	}

	public void setAppendingCancellation(boolean appendingCancellation) {
		this.appendingCancellation = appendingCancellation;
	}

	public PinState getPinState() {
		return pinState;
	}

	public void setPinState(PinState pinState) {
		this.pinState = pinState;
	}

}
