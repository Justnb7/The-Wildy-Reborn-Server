package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendShakeScreen implements PacketEncoder {
	
	private final int OPCODE = 35;
	
	private final int verticleAmount, verticleSpeed, horizontalAmount, horizontalSpeed;

	public SendShakeScreen(int verticleAmount, int verticleSpeed, int horizontalAmount, int horizontalSpeed) {
		this.verticleAmount = verticleAmount;
		this.verticleSpeed = verticleSpeed;
		this.horizontalAmount = horizontalAmount;
		this.horizontalSpeed = horizontalSpeed;
	}
	
	@Override
	public void encode(Player player) {
		if (player != null && player.getOutStream() != null) {
	    	player.outStream.writeFrame(OPCODE);
	        player.outStream.writeByte(verticleAmount);
	        player.outStream.writeByte(verticleSpeed);
	        player.outStream.writeByte(horizontalAmount);
	        player.outStream.writeByte(horizontalSpeed);
        }
	}

}
