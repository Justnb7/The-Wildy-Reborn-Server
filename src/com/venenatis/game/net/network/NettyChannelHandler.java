package com.venenatis.game.net.network;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.network.handshake.HandshakeMessage;
import com.venenatis.game.net.network.session.LoginSession;
import com.venenatis.game.net.network.session.Session;
import com.venenatis.game.world.World;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class NettyChannelHandler extends ChannelInboundHandlerAdapter {

	private final ImmutableList<String> IGNORED = ImmutableList
			.of("An existing connection was forcibly closed by the remote host",
					"An established connection was aborted by the software in your host machine",
					"De externe host heeft een verbinding verbroken");

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
			throws Exception {
		if (!IGNORED.stream().anyMatch(it -> Objects.equal(it, e.getMessage()))) {
			e.printStackTrace();
		}
		ctx.channel().close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) 
			throws Exception {
		try {
			Session session = ctx.attr(NetworkConstants.KEY).get();
			if (session == null) {

				HandshakeMessage message = (HandshakeMessage) msg;
				if (message.getId() == NetworkConstants.LOGIN_REQUEST) {
					ctx.attr(NetworkConstants.KEY).set(
							new LoginSession(ctx.channel(), ctx));
				}
			} else {
				session.receiveMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception { // try now
		try {
			Session session = ctx.attr(NetworkConstants.KEY).getAndRemove();
			if (session != null) {
				final Player player = (Player) session.getAttachment();
				if (player != null) {

					if (Combat.incombat(player)) {
						player.setXLogDelay(System.currentTimeMillis());
					}
					World.getWorld().queueLogout(player);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
