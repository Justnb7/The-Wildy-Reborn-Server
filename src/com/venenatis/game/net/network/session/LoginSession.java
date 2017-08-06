package com.venenatis.game.net.network.session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Sanctions;
import com.venenatis.game.model.entity.player.save.PlayerSave;
import com.venenatis.game.model.entity.player.save.PlayerSave.PlayerSaveDetail;
import com.venenatis.game.model.entity.player.updating.PlayerUpdating;
import com.venenatis.game.net.LoginManager;
import com.venenatis.game.net.LoginManager.LoginRequest;
import com.venenatis.game.net.network.NetworkConstants;
import com.venenatis.game.net.network.codec.RS2Decoder;
import com.venenatis.game.net.network.codec.RS2Encoder;
import com.venenatis.game.net.network.login.LoginCredential;
import com.venenatis.game.net.network.login.LoginResponse;
import com.venenatis.game.util.NameUtils;
import com.venenatis.game.world.World;
import com.venenatis.server.GameEngine;
import com.venenatis.server.Server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class LoginSession extends Session {

	private static final Logger logger = LoggerFactory.getLogger(LoginSession.class);
	
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
