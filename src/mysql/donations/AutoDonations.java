package mysql.donations;

import java.sql.*;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.World;

public class AutoDonations implements Runnable {

	public static Connection con = null;
	public static Statement stm;

	Player player = null;

	public AutoDonations(Player player) {
		this.player = player;
	}

	public void run() {
		Connection connection = null;
		String name = player.getUsername();
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			connection = null;
		}

		try {
			connection = DriverManager.getConnection("jdbc:mysql://66.85.79.60:3306/venenasn_donate", "venenasn_d_user", "6C-=p9{@TuDc");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}

		if (connection != null) {
			try {
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM donation WHERE username = ? AND claimed = 0");
				statement.setString(1, name);
				ResultSet rs = statement.executeQuery();
				boolean b = false;
				while (rs.next()) {
					int prod = Integer.parseInt(rs.getString("productid"));
					int price = Integer.parseInt(rs.getString("price"));
					if (prod == 1 && price == 5) {
						player.getInventory().addOrSentToBank(player, new Item(13190));
						b = true;
						World.getWorld().sendAdminMessage("@red@[ADMIN]@bla@ " + player.getUsername() + " Has just claimed donation");
					} else if (prod == 2 && price == 10) {
						player.getInventory().addOrSentToBank(player, new Item(13192));
						b = true;
						World.getWorld().sendAdminMessage("@red@[ADMIN]@bla@ " + player.getUsername() + " Has just claimed donation");
					} else if (prod == 4 && price == 20) {
						player.getInventory().addOrSentToBank(player, new Item(22006));
						b = true;
						World.getWorld().sendAdminMessage("@red@[ADMIN]@bla@ " + player.getUsername() + " Has just claimed donation");
					} else if (prod == 10 && price == 50) {
						player.getInventory().addOrSentToBank(player, new Item(22008));
						b = true;
						World.getWorld().sendAdminMessage("@red@[ADMIN]@bla@ " + player.getUsername() + " Has just claimed donation");
					} else if (prod == 20 && price == 100) {
						player.getInventory().addOrSentToBank(player, new Item(22009));
						b = true;
						World.getWorld().sendAdminMessage("@red@[ADMIN]@bla@ " + player.getUsername() + " Has just claimed donation");
					}

					if (b) {
						statement = connection.prepareStatement("UPDATE donation SET claimed = 1 WHERE username = ?");
						statement.setString(1, name);
						statement.executeUpdate();
					}
				}

				player.getActionSender().sendMessage("Thanks for donating. You have recieved your donation points!");
			} catch (Exception e) {
				player.getActionSender().sendMessage("Couldn't find donation");
				e.printStackTrace();
				connection = null;
			}
		}
		return;
	}
}