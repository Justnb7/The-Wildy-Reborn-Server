package mysql.voting;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.RandomGenerator;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class Voting implements Runnable {

	private static int VOTES;

	Player player = null;
	String auth = null;

	public Voting(String auth, Player player) {
		this.player = player;
		this.auth = auth;
	}

	public void run() {

		Connection connection = null;
		Statement stmt = null;

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			connection = null;
			stmt = null;
		}

		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://66.85.79.60.net/venenasn_vote",
					"venenasn_user", "Tvzm6vpo8n2{");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}

		if (connection != null) {
			try {
				stmt = (Statement) connection.createStatement();
				String sql;
				sql = "SELECT COUNT(*) FROM auth WHERE auth='"
						+ auth.replaceAll("'", "\\\\'") + "'";
				ResultSet rs;
				rs = stmt.executeQuery(sql);
				rs.next();
				int count;
				count = rs.getInt(1);
				if (count > 0) {

					Item item = new Item(19670, 1);
					player.getInventory().add(item, true);
					player.getActionSender().sendMessage("Auth redeemed, thanks for voting!");
					
					//Achievements.doProgress(player, AchievementData.VOTE_100_TIMES);
					int bonus = RandomGenerator.nextInt(2000000);

					player.getInventory().add(995, Utility.getRandom(bonus));
					player.getActionSender().sendMessage("You a bonus " + bonus + " coins");
					if (Utility.getRandom(15) == 7) {
						player.getInventory().add(6199, 1);
						player.getActionSender().sendMessage("You recieve a bonus mystery box!");
					}

					if (VOTES >= 20) {
						World.getWorld().sendWorldMessage("@red@[VOTING]@blu@ Another 20 votes have been claimed! Vote now using ::vote!", false);
						VOTES = 0;
					}
					VOTES++;
				}
				sql = "DELETE FROM auth WHERE auth='" + auth.replaceAll("'", "\\\\'") + "'";
				stmt.execute(sql);
				rs.close();
				stmt.close();
				connection.close();
				return;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else {
			System.out.println("Failed to make connection!");
			return;
		}
		return;
	}
}