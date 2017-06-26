package com.venenatis.game.net.network.login;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * A {@link MessageToMessageEncoder} which encodes the values received from the
 * login protocol.
 *
 * @author Mobster
 */
public final class LoginEncoder extends MessageToMessageEncoder<LoginResponse> {

	@Override
	protected void encode(ChannelHandlerContext ctx, LoginResponse response, List<Object> out) throws Exception {
		ByteBuf buffer = Unpooled.buffer(3);
		buffer.writeByte(response.getResponse());
		if (response.getResponse() == 2) {
			buffer.writeByte(response.getRights());
			buffer.writeByte(response.getFlagStatus());
		}

		out.add(buffer);
	}

}
