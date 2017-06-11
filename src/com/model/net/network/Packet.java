package com.model.net.network;

import io.netty.buffer.ByteBuf;

public final class Packet {

	/**
	 * The opcode for this packet.
	 */
	private final int opcode;
	
	/**
	 * The buffer that contains the data for this packet.
	 */
	private final ByteBuf payload;

	/**
	 * Creates a {@link Packet}.
	 * 
	 * @param opcode
	 *            The opcode.
	 * @param payload
	 *            The payload.
	 */
	public Packet(final int opcode, final ByteBuf payload) {
		this.opcode = opcode;
		this.payload = payload;
	}

	/**
	 * Gets the opcode.
	 * 
	 * @return The opcode.
	 */
	public int getOpcode() {
		return opcode;
	}

	/**
	 * Gets the payload.
	 * 
	 * @return The payload.
	 */
	public ByteBuf getPayload() {
		return payload;
	}
	
	/**
	 * Incoming packet sizes array.
	 */
	public static final int PACKET_SIZES[] = { 
		    0, 0, 0, 1, -1, 0, 4, 0, 4, 0, // 9
			0, 0, 0, 0, 4, 0, 6, 2, 2, 0, // 10
			0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
			0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
			2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
			0, 0, 0, 12, 0, 0, 0, 8, 8, 12, // 50
			8, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
			6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
			0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
			0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
			0, 13, 0, -1, 0, 0, 0, 0, 0, 0, // 100
			0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
			1, 0, 6, 0, 16, 0, -1, -1, 2, 6, // 120
			0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
			6, 10, -1, 0, 0, 6, 0, 0, 0, 0, // 140
			0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
			0, 0, 0, 0, -1, -1, 0, 0, 0, 0, // 160
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
			0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
			0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
			2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
			4, 0, 0, /* 0 */4, 7, 8, 0, 0, 10, 0, // 210
			0, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
			1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
			0, 4, 0, 0, 0, 0, -1, 0, -1, 4, // 240
			0, 0, 6, 6, 0, 0, 0 // 250
	};
	
	public static void main(String[] s){
		System.out.println("pkt 140 is "+PACKET_SIZES[140]+ " pkt 141 is "+PACKET_SIZES[141]+ " pkt 142 is "+PACKET_SIZES[142]);
	}

}