package com.model.net.network;

import io.netty.buffer.ByteBuf;

public final class Packet {

	private final int opcode;
	private final ByteBuf buffer;

	public Packet(int opcode, ByteBuf buffer) {
		this.opcode = opcode;
		this.buffer = buffer;
	}

	public int getOpcode() {
		return opcode;
	}

	public ByteBuf getBuffer() {
		return buffer;
	}

}