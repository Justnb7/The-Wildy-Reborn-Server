package com.model.net.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.model.game.character.player.Player;
import com.model.net.network.Packet;
import com.model.net.network.rsa.ISAACRandomGen;

public class RS2Decoder extends ByteToMessageDecoder {

	private final ISAACRandomGen cipher;

	private int opcode = -1;
	private int size = -1;

	public RS2Decoder(ISAACRandomGen cipher) {
		this.cipher = cipher;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {

		try {
			if (opcode == -1) {
				if (buf.readableBytes() < 1) {
					return;
				}

				opcode = buf.readUnsignedByte();
				opcode = (opcode - cipher.getNextKey()) & 0xFF;
				size = Player.PACKET_SIZES[opcode];
			}

			if (size == -1) {
				if (buf.readableBytes() < 1) {
					return;
				}

				size = buf.readUnsignedByte();
			}

			if (buf.readableBytes() < size) {
				return;
			}

			try {
				out.add(new Packet(opcode, buf.readBytes(size)));
			} finally {
				opcode = size = -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
