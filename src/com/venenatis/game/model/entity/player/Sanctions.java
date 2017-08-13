package com.venenatis.game.model.entity.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is used to IO write player punishments
 * 
 * @author Patrick van Elderen
 *
 */
public class Sanctions {

	/**
	 * A set of stored banned ips
	 */
	public static Set<String> bannedIps = new HashSet<>();

	/**
	 * A set of banned users
	 */
	public static ArrayList<String> bannedNames = new ArrayList<String>();

	/**
	 * A set of banned mac adresses
	 */
	public static Set<String> macBannedUsers = new HashSet<>();

	/**
	 * A set of muted ips
	 */
	public static Set<String> mutedIps = new HashSet<>();

	/**
	 * A set of locked accounts (used for stolen accounts)
	 */
	public static Set<String> lockedAccounts = new HashSet<>();

	/**
	 * A set of banned user IDS
	 */
	public static Collection<String> bannedIds = new ArrayList<String>();

	/**
	 * 
	 */
	private static final File identity_bans = new File("./data/bans/", "identitybanned.txt");

	public static void initialize() {
		banUsers();
		macBannedUsers();
		banIps();
		muteIps();
		lockAccounts();
	}

	/**
	 * Populates the Locked Accounts list with names from the
	 * <code>HashSet</code>.
	 */
	public static void lockAccounts() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("./data/bans/LockedAccounts.txt"));
			String data = null;
			try {
				while ((data = in.readLine()) != null) {
					lockedAccounts.add(data);
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes a banned user from the banned list.
	 **/
	public static void removeNameFromBanList(String name) {
		bannedNames.remove(name.toLowerCase());
		deleteFromFile("./data/bans/UsersBanned.txt", name);
	}

	/**
	 * Removes an IP address from the IPmuted list.
	 */
	public static void unIPMuteUser(String name) {
		mutedIps.remove(name);
		deleteFromFile("./data/bans/IpsMuted.txt", name);
	}

	/**
	 * Removes an IP address from the IPBanned list.
	 **/
	public static void removeIpFromBanList(String IP) {
		bannedIps.remove(IP);
	}

	/**
	 * Adds a user to the banned list.
	 **/
	public static void addNameToBanList(String name) {
		bannedNames.add(name.toLowerCase());
	}

	/**
	 * Adds an IP address to the IPBanned list.
	 **/
	public static void addIpToBanList(String IP) {
		bannedIps.add(IP);
	}

	/**
	 * Adds an IP address to the IPMuted list.
	 */
	public static void addIpToMuteList(String IP) {
		mutedIps.add(IP);
		addIpToMuteFile(IP);
	}

	/**
	 * Contains banned IP addresses.
	 **/
	public static boolean isIpBanned(String IP) {
		if (bannedIps.contains(IP)) {
			return true;
		}
		return false;
	}

	/**
	 * Reload the list of banned ips.
	 */
	public static void resetIpBans() {
		bannedIps = new HashSet<>();
		banIps();
	}

	public static void addIdentityToList(String IP) {
		bannedIds.add(IP);
	}

	public static void removeIdentityBan(String identity) {
		bannedIds.remove(identity);
		deleteFromFile(identity_bans, identity);
	}

	public static boolean isMacBanned(String address) {
		return macBannedUsers.contains(address);
	}

	/**
	 * Contains banned users.
	 **/
	public static boolean isNamedBanned(String name) {
		if (bannedNames.contains(name.toLowerCase())) {
			return true;
		}
		return false;
	}

	/**
	 * Writes the user into the text file when using the ::ban command.
	 **/
	public static void addNameToBanFile(String name) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("./data/bans/UsersBanned.txt", true));
			try {
				out.newLine();
				out.write(name);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the IP into the text file when using the ::ipban command.
	 **/
	public static void addIpToFile(String Name) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("./data/bans/IpsBanned.txt", true));
			try {
				out.newLine();
				out.write(Name);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the IP into the text file when using the ::mute command.
	 */
	public static void addIpToMuteFile(String Name) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("./data/bans/IpsMuted.txt", true));
			try {
				out.newLine();
				out.write(Name);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void bannedIds() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(identity_bans));
			String data;
			try {
				while ((data = in.readLine()) != null) {
					addIdentityToList(data);
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads all users from text file then adds them all to the ban list.
	 **/
	public static void banUsers() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("./data/bans/UsersBanned.txt"));
			String data = null;
			try {
				while ((data = in.readLine()) != null) {
					addNameToBanList(data);
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void macBannedUsers() {
		File file = new File("./data/bans/MacBanned.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			return;
		}
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			String line = null;
			while ((line = in.readLine()) != null) {
				if (!macBannedUsers.contains(line) && !line.isEmpty()) {
					macBannedUsers.add(line);
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void addMacBan(String address) {
		if (!macBannedUsers.contains(address) && !address.isEmpty()) {
			macBannedUsers.add(address);
			updateMacBanFile();
		}
	}

	public static synchronized void removeMacBan(String address) {
		if (macBannedUsers.contains(address) && !address.isEmpty()) {
			macBannedUsers.remove(address);
			updateMacBanFile();
		}
	}

	private static synchronized void updateMacBanFile() {
		try (BufferedWriter out = new BufferedWriter(new FileWriter("./data/bans/MacBanned.txt"))) {
			for (String mac : macBannedUsers) {
				out.write(mac);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads all the IPs from text file then adds them all to ban list.
	 **/
	public static void banIps() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("./data/bans/IpsBanned.txt"));
			String data = null;
			try {
				while ((data = in.readLine()) != null) {
					addIpToBanList(data);
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void removeIpBan(String remove) throws IOException {
		List<String> data = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader("./data/bans/IpsBanned.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				data.add(line);
			}
		}
		data.removeIf(s -> s.equals(remove));
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("./data/bans/IpsBanned.txt"))) {
			for (String line : data) {
				writer.write(line);
				writer.newLine();
			}
		}
	}

	/**
	 * Adds an IP address to the IPMuted list.
	 */
	public static void muteIps() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("./data/bans/IpsMuted.txt"));
			String data = null;
			try {
				while ((data = in.readLine()) != null) {
					mutedIps.add(data);
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteFromFile(File file, String name) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(file));
			ArrayList<String> contents = new ArrayList<String>();
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				} else {
					line = line.trim();
				}
				if (!line.equalsIgnoreCase(name)) {
					contents.add(line);
				}
			}
			r.close();
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			for (String line : contents) {
				w.write(line, 0, line.length());
				w.newLine();
			}
			w.flush();
			w.close();
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
	}

	/**
	 * Void needed to delete users from a file.
	 */
	public synchronized static void deleteFromFile(String file, String name) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(file));
			ArrayList<String> contents = new ArrayList<>();
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				String args[] = line.split("-");
				if (!args[0].equalsIgnoreCase(name) && !line.equals("")) {
					contents.add(line);
				}
			}
			r.close();
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			for (String line : contents) {
				w.write(line, 0, line.length());
				w.newLine();
			}
			w.flush();
			w.close();
		} catch (Exception e) {
		}
	}
}