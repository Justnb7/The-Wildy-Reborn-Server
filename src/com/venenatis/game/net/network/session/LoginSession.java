package com.venenatis.game.net.network.session;

import com.venenatis.game.net.LoginManager;
import com.venenatis.game.net.LoginManager.LoginRequest;
import com.venenatis.game.net.network.login.LoginCredential;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class LoginSession extends Session {
	
	private final ChannelHandlerContext ctx;
	private final Channel chan;

	public LoginSession(Channel channel, ChannelHandlerContext ctx) {
		super(channel);
		this.ctx = ctx;
		this.chan = channel;
	}

	@Override
	public void receiveMessage(Object object) {
		if (!(object instanceof LoginCredential)) {
			return;
		}
		LoginCredential credential = (LoginCredential) object;
		LoginManager.requestLogin(new LoginRequest(ctx, chan, credential));
	}

}
