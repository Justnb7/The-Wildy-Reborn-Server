package com.model.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.model.game.character.player.Player;
import com.model.game.character.player.PlayerUpdating;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;

public class StoreDatabase {

	private static Connection connection;
    private static long lasConnection = System.currentTimeMillis();
	static {
		createConnection();
	}

	public static void init() {
		createConnection();
	}

	public static void createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			connection = DriverManager.getConnection("jdbc:mysql://Venenatis.com/Venenatis1_store", "Venenatis1_store", "oG(T!squ9]Ua");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void destroyConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
		public static void claimPayment(final Player player, final String name) {
		try {
			if (System.currentTimeMillis() - lasConnection > 10000) {
				destroyConnection();
				createConnection();
				lasConnection = System.currentTimeMillis();
			}
			Statement s = connection.createStatement();
			String name2 = name.replaceAll(" ", "_");
			String query = "SELECT * FROM donation WHERE username = '"+name2+"'";
			ResultSet rs = s.executeQuery(query);
			boolean claimed = false;
			while(rs.next()) {
				int prod = Integer.parseInt(rs.getString("productid"));
				int price = Integer.parseInt(rs.getString("price"));
				if (prod == 1 && price == 1) {
					player.updateRank();
					player.setAmountDonated(player.getAmountDonated() + 1);
					player.setTotalAmountDonated(player.getTotalAmountDonated() + 1);
					claimed = true;
					PlayerUpdating.executeGlobalMessage("<col=255>[Store]</col> <col=ff0033>" + player.getName() + " Has just donated for 1X Credits.");
				} else if (prod == 2 && price == 5) {
					player.updateRank();
					player.setAmountDonated(player.getAmountDonated() + 5);
					player.setTotalAmountDonated(player.getTotalAmountDonated() + 5);
					claimed = true;
					PlayerUpdating.executeGlobalMessage("<col=255>[Store]</col> <col=ff0033>" + player.getName() + " Has just donated for 5X Credits.");
				}
			}
				if (claimed) {
					s.execute("DELETE FROM `donation` WHERE `username` = '"+name2+"';");
					System.out.println("Item claimed");
				}
				else
					player.write(new SendMessagePacket("Your name was not in our donation records"));
			
				
			} catch (Exception e) {
				e.printStackTrace();
				player.write(new SendMessagePacket("Unable to claim your item at this time"));
			}
		}
		
	}