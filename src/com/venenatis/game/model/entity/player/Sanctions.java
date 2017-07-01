package com.venenatis.game.model.entity.player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;
import com.venenatis.game.util.JsonSaver;
import com.venenatis.game.util.Stopwatch;
import com.venenatis.game.util.parser.GsonParser;

public class Sanctions {
	
	private static final Set<String> muted = new HashSet<>();
	private static final Set<String> banned = new HashSet<>();
	private static final Set<String> macBanned = new HashSet<>();

	private Stopwatch mute = new Stopwatch();
	private Stopwatch ban = new Stopwatch();
	private int muteLength;
	private int banLength;
	
	public Sanctions(String hostAddress, String macAddress) {
		if (muted.contains(hostAddress)) {
			mute.reset(Stopwatch.currentTime());
			muteLength = -1;
		}

		if (banned.contains(hostAddress) || macBanned.contains(macAddress)) {
			ban.reset(Stopwatch.currentTime());
			banLength = -1;
		}
	}
	
	public boolean isMuted() {
		return isPermanentMute() || muteLeft() > 0;
	}
	
	public boolean isBanned() {
		return isPermanentBan() || banLeft() > 0;
	}
	
	public boolean isPermanentMute() {
		return muteLength == -1;
	}
	
	public boolean isPermanentBan() {
		return banLength == -1;
	}
	
	public void mute(int minutes) {
		mute.reset();
		muteLength = minutes;
	}
	
	public void ban(int minutes) {
		ban.reset();
		banLength = minutes;
	}
	
	public void unMute() {
		mute.reset(Stopwatch.currentTime());
		muteLength = 0;
	}
	
	public void unBan() {
		ban.reset(Stopwatch.currentTime());
		banLength = 0;
	}
	
	public void unMute(String hostAddress) {
		mute.reset(Stopwatch.currentTime());
		muteLength = 0;
		muted.remove(hostAddress);
		save();
	}
	
	public void unBan(String hostAddress) {
		ban.reset(Stopwatch.currentTime());
		banLength = 0;
		banned.remove(hostAddress);
		save();
	}
	
	public void permanentMute(String hostAddress) {
		mute.reset(Stopwatch.currentTime());
		muteLength = -1;
		muted.add(hostAddress);
		save();
	}
	
	public void permanentBan(String hostAddress) {
		ban.reset(Stopwatch.currentTime());
		banLength = -1;
		banned.add(hostAddress);
		save();
	}
	
	public void macBan(String macAddress) {
		ban.reset(Stopwatch.currentTime());
		banLength = -1;
		macBanned.add(macAddress);
		save();
	}
	
	public long muteLeft() {
		return muteLength - mute.elapsed(TimeUnit.MINUTES);
	}
	
	public long banLeft() {
		return banLength - ban.elapsed(TimeUnit.MINUTES);
	}
	
	public static void load() {
		new GsonParser("ip_sanctions") {
			
			@Override
			protected void parse(JsonObject data) {
				String host = data.get("host").getAsString();
				String type = data.get("type").getAsString();
				
				if (type.equals("mute")) {
					muted.add(host);
				} else if (type.equals("mute")) {
					banned.add(host);
				} else if (type.equals("mac")) {
					macBanned.add(host);
				}
				
			}
		}.run();
	}
	
	private static void save() {
		JsonSaver saver = new JsonSaver();

		for (String host : muted) {
			saver.current().addProperty("host", host);
			saver.current().addProperty("type", "mute");
			saver.split();
		}

		for (String host : banned) {
			saver.current().addProperty("host", host);
			saver.current().addProperty("type", "ban");
			saver.split();
		}

		for (String host : macBanned) {
			saver.current().addProperty("host", host);
			saver.current().addProperty("type", "macBan");
			saver.split();
		}

		saver.publish("./data/ip_sanctions.json");
	}
}