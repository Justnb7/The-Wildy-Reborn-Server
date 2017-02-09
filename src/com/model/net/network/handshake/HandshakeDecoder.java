package com.model.net.network.handshake;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.model.net.network.NetworkConstants;
import com.model.net.network.login.RS2LoginProtocol;

/**
 * Decodes the handshake
 * 
 * @author Mobster
 *
 */
public class HandshakeDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
		if (buffer.isReadable()) {
			int handshake = buffer.readUnsignedByte();
			if (handshake == NetworkConstants.LOGIN_REQUEST) {
				ctx.pipeline().replace("decoder", "decoder", new RS2LoginProtocol());
			}
			out.add(new HandshakeMessage(handshake));
		}
	}

}
