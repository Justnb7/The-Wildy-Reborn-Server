package com.venenatis.game.model.entity.player.clan;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Clan {

	private String name;
	private final String owner;
	private String slogan = "None";

	private ClanRank joinable = ClanRank.ANYONE;
	private ClanRank talkable = ClanRank.ANYONE;
	private ClanRank kickable = ClanRank.LEADER;
	private ClanRank managable = ClanRank.LEADER;

	private boolean lootshare = false;
	private boolean lock = false;

	private int memberLimit = 50;

	private Map<String, ClanRank> ranked = new HashMap<>();
	private transient Queue<ClanMember> members;

	public Clan(String name) {
		this.name = name;
		this.owner = name;
	}

	public void init() {
		members = new LinkedList<>();
	}

	public boolean add(ClanMember member) {
		if (members.size() >= getMemberLimit()) {
			return false;
		}

		if (member.getRank() != ClanRank.ANYONE) {
			ranked.put(member.getName(), member.getRank());
		}

		remove(member);
		
		if (members.contains(member)) {
			members.remove(member);
		}
		
		return members.add(member);
	}

	public ClanMember get(String name) {
		for (Iterator<ClanMember> it = members.iterator(); it.hasNext();) {
			ClanMember next = it.next();
			if (next.getName().equalsIgnoreCase(name)) {
				return next;
			}
		}

		return null;
	}

	public void setRank(ClanMember member) {
		ClanRank rank = ranked.get(member.getName());
		
		if (rank == null) {
			member.setRank(ClanRank.ANYONE);
			return;
		}

		member.setRank(rank);
	}

	public void remove(ClanMember member) {
		members.removeIf(other -> other != null && other.getPlayer() != null && other.getPlayer().isRegistered() && other.getName().equalsIgnoreCase(member.getName()));
	}

	public boolean contains(String username) {
		return members.stream().anyMatch(member -> member != null && member.getPlayer() != null && member.getPlayer().isRegistered() && member.getName().equalsIgnoreCase(name));
	}

	public boolean canJoin(ClanMember member) {
		return !member.getRank().lessThan(joinable);
	}

	public boolean canTalk(ClanMember member) {
		return !member.getRank().lessThan(talkable);
	}

	public boolean canKick(ClanMember member) {
		return !member.getRank().lessThan(kickable);
	}

	public boolean canManage(ClanMember member) {
		return !member.getRank().lessThan(managable);
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public String getSlogan() {
		return slogan;
	}

	public boolean isLootshare() {
		return lootshare;
	}

	public boolean getLocked() {
		return lock;
	}

	public int getMemberLimit() {
		return memberLimit;
	}

	public Queue<ClanMember> members() {
		return members;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	public void setJoinable(ClanRank joinable) {
		this.joinable = joinable;
	}

	public void setTalkable(ClanRank talkable) {
		this.talkable = talkable;
	}

	public void setKickable(ClanRank kickable) {
		this.kickable = kickable;
	}

	public void setManagable(ClanRank managable) {
		this.managable = managable;
	}

	public void setLootshare(boolean lootshare) {
		this.lootshare = lootshare;
	}

	public void setLocked(boolean lock) {
		this.lock = lock;
	}

	public void setMemberLimit(int memberLimit) {
		this.memberLimit = memberLimit;
	}

	@Override
	public String toString() {
		return String.format("CLAN[name=%s, owner=%s, ranked=%s, members=%s]", name, owner, ranked, members);
	}

}