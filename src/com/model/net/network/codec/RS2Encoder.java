package com.model.net.network.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.model.net.network.Packet;

public final class RS2Encoder extends MessageToMessageEncoder<Packet> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> out) throws Exception {
		try {
			out.add(packet.getPayload());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
