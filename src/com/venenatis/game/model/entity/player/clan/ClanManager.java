package com.venenatis.game.model.entity.player.clan;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.World;

public class ClanManager {
	
	private long lastUpdate = System.currentTimeMillis();

	public LinkedList<Clan> clans;

	public ClanManager() {
		clans = new LinkedList<Clan>();
	}

	public int getActiveClans() {
		return this.clans.size();
	}

	public int getTotalClans() {
		File localFile = new File("/Data/clan/");
		return localFile.listFiles().length;
	}

	public void create(Player paramClient) {
		if (paramClient.getClan() != null) {
			paramClient.message("@or2@You must leave your current clan-chat before making your own.");
			return;
		}
		Clan localClan = new Clan(paramClient);
		this.clans.add(localClan);
		localClan.addMember(paramClient);
		localClan.save();
		localClan.updateInterface(paramClient);
		paramClient.message("@or2@You may change your clan settings by clicking the 'Clan Setup' button.");
	}

	public Clan getClan(String paramString) {
		for (int i = 0; i < this.clans.size(); i++) {
			if (this.clans.get(i).getFounder().equalsIgnoreCase(paramString)) {
				return this.clans.get(i);
			}
		}

		Clan localClan = read(paramString);
		if (localClan != null) {
			this.clans.add(localClan);
			return localClan;
		}
		return null;
	}

	/**
	 * Returns the Help clan or creates it if it doesn't exist yet.
	 * 
	 * @return The Help clan.
	 */
	public Clan getHelpClan() {
		for (int i = 0; i < this.clans.size(); i++) {
			if (clans.get(i).getFounder().equalsIgnoreCase("Help")) {
				return clans.get(i);
			}
		}

		Clan localClan = read("Help");
		if (localClan != null) {
			clans.add(localClan);
			return localClan;
		}
		localClan = new Clan("Help", "Help");
		clans.add(localClan);
		localClan.save();
		return localClan;
	}

	public void delete(Clan paramClan) {
		if (paramClan == null) {
			return;
		}
		File localFile = new File("Data/clan/" + paramClan.getFounder() + ".cla");
		if (localFile.delete()) {
			Player localClient = World.getWorld().lookupPlayerByName(paramClan.getFounder());
			if (localClient != null) {
				localClient.message("Your clan has been deleted.");
			}
			this.clans.remove(paramClan);
		}
	}

	public void save(Clan paramClan) {
		if (paramClan == null) {
			return;
		}
		File localFile = new File("Data/clan/" + paramClan.getFounder() + ".cla");
		try {
			RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "rwd");

			localRandomAccessFile.writeUTF(paramClan.getTitle());
			localRandomAccessFile.writeByte(paramClan.joinable);
			localRandomAccessFile.writeByte(paramClan.talkable);
			localRandomAccessFile.writeByte(paramClan.kickable);
			localRandomAccessFile.writeByte(paramClan.managable);
			localRandomAccessFile.writeBoolean(paramClan.isLootShare());
			if ((paramClan.rankedMembers != null) && (paramClan.rankedMembers.size() > 0)) {
				localRandomAccessFile.writeShort(paramClan.rankedMembers.size());
				for (int i = 0; i < paramClan.rankedMembers.size(); i++) {
					localRandomAccessFile.writeUTF(paramClan.rankedMembers.get(i));
					localRandomAccessFile.writeShort(paramClan.ranks.get(i).intValue());
				}
			} else {
				localRandomAccessFile.writeShort(0);
			}
			if ((paramClan.bannedMembers != null) && (paramClan.bannedMembers.size() > 0)) {
				localRandomAccessFile.writeShort(paramClan.bannedMembers.size());
				for (int i = 0; i < paramClan.bannedMembers.size(); i++) {
					localRandomAccessFile.writeUTF(paramClan.bannedMembers.get(i));
				}
			} else {
				localRandomAccessFile.writeShort(0);
			}
			if ((paramClan.lootSharePoints != null) && (paramClan.lootSharePoints.size() > 0)) {
				localRandomAccessFile.writeShort(paramClan.lootSharePoints.size());
				for (Entry<String, Integer> entry : paramClan.lootSharePoints.entrySet()) {
					localRandomAccessFile.writeUTF(entry.getKey());
					localRandomAccessFile.writeInt(entry.getValue());
				}
			} else {
				localRandomAccessFile.writeShort(0);
			}

			localRandomAccessFile.close();
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
	}

	private Clan read(String paramString) {
		File localFile = new File("Data/clan/" + paramString + ".cla");
		if (!localFile.exists()) {
			return null;
		}
		try {
			RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "rwd");

			Clan localClan = new Clan(localRandomAccessFile.readUTF(), paramString);
			localClan.joinable = localRandomAccessFile.readByte();
			localClan.talkable = localRandomAccessFile.readByte();
			localClan.kickable = localRandomAccessFile.readByte();
			localClan.managable = localRandomAccessFile.readByte();
			localClan.setLootShare(localRandomAccessFile.readBoolean());
			int i = localRandomAccessFile.readShort();
			if (i != 0) {
				for (int j = 0; j < i; j++) {
					localClan.rankedMembers.add(localRandomAccessFile.readUTF());
					localClan.ranks.add(Integer.valueOf(localRandomAccessFile.readShort()));
				}
			}
			int j = localRandomAccessFile.readShort();
			if (j != 0) {
				for (int k = 0; k < j; k++) {
					localClan.bannedMembers.add(localRandomAccessFile.readUTF());
				}
			}
			int k = localRandomAccessFile.readShort();
			if (k != 0) {
				for (int l = 0; l < j; l++) {
					localClan.lootSharePoints.put(localRandomAccessFile.readUTF(), localRandomAccessFile.readInt());
				}
			}
			localRandomAccessFile.close();

			return localClan;
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		return null;
	}

	public boolean clanExists(String paramString) {
		File localFile = new File("Data/clan/" + paramString + ".cla");
		return localFile.exists();
	}

	public LinkedList<Clan> getClans() {
		return this.clans;
	}

	public void process() {
		if (lastUpdate + 60000 * 15 < System.currentTimeMillis()) {
			lastUpdate = System.currentTimeMillis();
			for (Clan clan : clans) {
				Iterator<Entry<String, Integer>> it = clan.lootSharePoints.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Integer> entry = it.next();
					int newValue = (int) (entry.getValue() * 0.90);
					if (newValue < 100_000 && newValue > -100_000) {
						clan.lootSharePoints.remove(entry.getKey());
					} else {
						clan.lootSharePoints.put(entry.getKey(), newValue);
					}
				}
			}
		}
	}

}