package com.venenatis.game.net.network.login;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import com.venenatis.game.net.network.NetworkConstants;
import com.venenatis.game.net.network.rsa.ISAACRandomGen;
import com.venenatis.game.net.packet.Packet;
import com.venenatis.game.util.Utility;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RS2LoginProtocol extends ByteToMessageDecoder {

	private enum State {
		HANDSHAKE, HEADER, LOGIN
	}

	private static final SecureRandom RANDOM = new SecureRandom();

	private State state = State.HANDSHAKE;

	private int packetLength;

	private String readRS2String(ByteBuf in) {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while (in.isReadable() && ((b = in.readByte()) != 10)) {
			bldr.append((char) b);
		}
		return bldr.toString();
	}

	public static void sendReturnCode(Channel channel, int code) {
		ByteBuf buffer = channel.alloc().buffer(Byte.BYTES).writeByte(code);
		ChannelFuture future = channel.writeAndFlush(new Packet(-1, buffer));
		future.addListener(ChannelFutureListener.CLOSE);
		buffer.release();
	}

	private static void finish(ByteBuf buf) {
		if (buf.isReadable()) {
			buf.readBytes(buf.readableBytes());
		}
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		switch (state) {
		case HANDSHAKE:
			if (buf.readableBytes() < 1) {
				return;
			}
			buf.readUnsignedByte();
			ByteBuf buffer = ctx.alloc().buffer(Long.BYTES * 2 + Byte.BYTES);
			buffer.writeLong(0).writeByte(0).writeLong(RANDOM.nextLong());
			ctx.channel().writeAndFlush(new Packet(-1, buffer));
			state = State.HEADER;
			break;
		case HEADER:
			if (buf.readableBytes() < 2) {
				finish(buf);
				ctx.channel().close();
				return;
			}

			int loginType = buf.readUnsignedByte();
			if ((loginType != 16) && (loginType != 18)) {
				System.out.println("Invalid login type: " + loginType);
				finish(buf);
				sendReturnCode(ctx.channel(), 15);
				return;
			}

			packetLength = buf.readUnsignedByte();

			if (packetLength <= 0) {
				finish(buf);
				sendReturnCode(ctx.channel(), 15);
				return;
			}
			state = State.LOGIN;
			break;
		case LOGIN:

			int encryptedBlockLength = packetLength - 40;
			if (buf.readableBytes() < packetLength) {
				finish(buf);
				sendReturnCode(ctx.channel(), 15);
				return;
			}
			buf.readByte();

			buf.readShort();

			buf.readByte();

			for (int i = 0; i < 9; i++) {
				buf.readInt();
			}

			encryptedBlockLength--;
			if (encryptedBlockLength != (buf.readUnsignedByte())) {
				System.out.println("Encrypted size mismatch.");
				finish(buf);
				sendReturnCode(ctx.channel(), 15);
				return;
			}
			byte[] encryptedBytes = new byte[encryptedBlockLength];
			buf.readBytes(encryptedBytes);
			ByteBuf encryptedBuffer = Unpooled.wrappedBuffer(new BigInteger(encryptedBytes).modPow(NetworkConstants.RSA_EXPONENT, NetworkConstants.RSA_MODULUS).toByteArray());

			int rsaOpcode = encryptedBuffer.readUnsignedByte();
			if (rsaOpcode != 10) {
				finish(buf);
				sendReturnCode(ctx.channel(), 24);
				return;
			}

			long clientHalf = encryptedBuffer.readLong();
			long serverHalf = encryptedBuffer.readLong();
			int[] isaacSeed = { (int) (clientHalf >> 32), (int) clientHalf, (int) (serverHalf >> 32), (int) serverHalf };
			ISAACRandomGen inCipher = new ISAACRandomGen(isaacSeed);
			for (int i = 0; i < isaacSeed.length; i++) {
				isaacSeed[i] += 50;
			}
			ISAACRandomGen outCipher = new ISAACRandomGen(isaacSeed);
			int uid = encryptedBuffer.readInt();
			int clientVersion = encryptedBuffer.readInt();
			String name = Utility.formatName(readRS2String(encryptedBuffer));
			String pass = readRS2String(encryptedBuffer);
			String identity = readRS2String(encryptedBuffer);
			String macAddress = readRS2String(encryptedBuffer);
			ctx.channel().pipeline().replace("encoder", "encoder", new LoginEncoder());
			out.add(new LoginCredential(name, pass, identity, macAddress, inCipher, outCipher, uid, clientVersion));
		}
	}
}
