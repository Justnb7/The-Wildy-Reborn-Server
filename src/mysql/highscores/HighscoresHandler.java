package mysql.highscores;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;

public class HighscoresHandler implements Runnable {

	private Player player;

	public HighscoresHandler(Player player) {
		this.player = player;
	}

	/**
	 * Function that handles everything, it inserts or updates user data in
	 * database
	 */
	@Override
	public void run() {

		if (player.getRights() == Rights.OWNER || player.getRights() == Rights.ADMINISTRATOR)
			return;
		
		/**
		 * Players username
		 */
		final String username = player.getUsername();
		
		/**
		 * Represents game mode If you want to set game modes do this:
		 */
		final int gameMode = 0;
		
		/**
		 * Represents overall xp
		 */
		final long overallXp = player.getSkills().getTotalLevel();
		
		/**
		 * Represents attack xp
		 */
		final long attackXp = (long) player.getSkills().getExperience(Skills.ATTACK);
		
		/**
		 * Represents defence xp
		 */
		final long defenceXp = (long) player.getSkills().getExperience(Skills.DEFENCE);
		
		/**
		 * Represents strength xp
		 */
		final long strengthXp = (long) player.getSkills().getExperience(Skills.STRENGTH);
		
		/**
		 * Represents constitution xp
		 */
		final long constitutionXp = (long) player.getSkills().getExperience(Skills.HITPOINTS);
		
		/**
		 * Represents ranged xp
		 */
		final long rangedXp = (long) player.getSkills().getExperience(Skills.RANGE);
		
		/**
		 * Represents prayer xp
		 */
		final long prayerXp = (long) player.getSkills().getExperience(Skills.PRAYER);
		
		/**
		 * Represents magic xp
		 */
		final long magicXp = (long) player.getSkills().getExperience(Skills.MAGIC);
		
		/**
		 * Represents cooking xp
		 */
		final long cookingXp = (long) player.getSkills().getExperience(Skills.COOKING);
		
		/**
		 * Represents woodcutting xp
		 */
		final long woodcuttingXp = (long) player.getSkills().getExperience(Skills.WOODCUTTING);
		
		/**
		 * Represents fletching xp
		 */
		final long fletchingXp = (long) player.getSkills().getExperience(Skills.FLETCHING);
		
		/**
		 * Represents fishing xp
		 */
		final long fishingXp = (long) player.getSkills().getExperience(Skills.FISHING);
		
		/**
		 * Represents firemaking xp
		 */
		final long firemakingXp = (long) player.getSkills().getExperience(Skills.FIREMAKING);
		
		/**
		 * Represents crafting xp
		 */
		final long craftingXp = (long) player.getSkills().getExperience(Skills.CRAFTING);
		
		/**
		 * Represents smithing xp
		 */
		final long smithingXp = (long) player.getSkills().getExperience(Skills.SMITHING);
		
		/**
		 * Represents mining xp
		 */
		final long miningXp = (long) player.getSkills().getExperience(Skills.MINING);
		
		/**
		 * Represents herblore xp
		 */
		final long herbloreXp = (long) player.getSkills().getExperience(Skills.HERBLORE);
		
		/**
		 * Represents agility xp
		 */
		final long agilityXp = (long) player.getSkills().getExperience(Skills.AGILITY);
		
		/**
		 * Represents thieving xp
		 */
		final long thievingXp = (long) player.getSkills().getExperience(Skills.THIEVING);
		
		/**
		 * Represents slayer xp
		 */
		final long slayerXp = (long) player.getSkills().getExperience(Skills.SLAYER);
		
		/**
		 * Represents farming xp
		 */
		final long farmingXp = (long) player.getSkills().getExperience(Skills.FARMING);
		
		/**
		 * Represents runecrafting xp
		 */
		final long runecraftingXp = (long) player.getSkills().getExperience(Skills.RUNECRAFTING);
		
		/**
		 * Represents hunter xp
		 */
		final long hunterXp = (long) player.getSkills().getExperience(Skills.HUNTER);
		
		/**
		 * Represents construction xp
		 */
		final long constructionXp = (long) player.getSkills().getExperience(Skills.CONSTRUCTION);
		
		/**
		 * Creates new instance of jdbc driver if that driver exists
		 */
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		/**
		 * Sets Connection variable to null
		 */
		Connection connection = null;
		/**
		 * Sets Statement variable to null
		 */
		Statement stmt = null;

		/**
		 * Attempts connecting to database
		 */
		try {
			connection = DriverManager
					.getConnection(
							"jdbc:mysql://ns536923.ip-144-217-68.net:3306/venenasn_hiscores",
							"venenasn_main", "Z9&^;h$tTi%N");
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		/**
		 * Checks if connection isnt null
		 */
		if (connection != null) {
			try {
				stmt = (Statement) connection.createStatement();
				ResultSet rs = stmt
						.executeQuery("SELECT COUNT(*) AS count FROM `hs_users` WHERE username='"
								+ username + "'");
				if (rs.next()) {
					if (rs.getInt("count") > 0) {
						stmt.executeUpdate("UPDATE `hs_users` SET overall_xp = '" + overallXp + "', attack_xp = '" + attackXp  + "', defence_xp = '" + defenceXp  + "', strength_xp = '" + strengthXp + "', constitution_xp = '" + constitutionXp + "', ranged_xp = '" + rangedXp + "', prayer_xp = '" + prayerXp + "', magic_xp = '" + magicXp + "', cooking_xp = '" + cookingXp + "', woodcutting_xp = '" + woodcuttingXp + "', fletching_xp = '" + fletchingXp + "', fishing_xp = '" + fishingXp + "', firemaking_xp = '" + firemakingXp + "', crafting_xp = '" + craftingXp + "', smithing_xp = '" + smithingXp + "', mining_xp = '" + miningXp + "', herblore_xp = '" + herbloreXp + "', agility_xp = '" + agilityXp + "', thieving_xp = '" + thievingXp + "', slayer_xp = '" + slayerXp + "', farming_xp = '" + farmingXp + "', runecrafting_xp = '" + runecraftingXp + "', hunter_xp = '" + hunterXp + "', construction_xp = '" + constructionXp + "' WHERE username = '" + username + "'");
					} else {
						stmt.executeUpdate("INSERT INTO `hs_users` (username, rights, overall_xp, attack_xp, defence_xp, strength_xp, constitution_xp, ranged_xp, prayer_xp, magic_xp, cooking_xp, woodcutting_xp, fletching_xp, fishing_xp, firemaking_xp, crafting_xp, smithing_xp, mining_xp, herblore_xp, agility_xp, thieving_xp, slayer_xp, farming_xp, runecrafting_xp, hunter_xp, construction_xp, summoning_xp, dungeoneering_xp) VALUES ('" + username + "', '" + gameMode + "', '" + overallXp + "', '" + attackXp + "', '" + defenceXp + "', '" + strengthXp + "', '" + constitutionXp + "', '" + rangedXp + "', '" + prayerXp + "', '" + magicXp + "', '" + cookingXp + "', '" + woodcuttingXp + "', '" + fletchingXp + "', '" + fishingXp + "', '" + firemakingXp + "', '" + craftingXp + "', '" + smithingXp + "', '" + miningXp + "', '" + herbloreXp + "', '" + agilityXp + "', '" + thievingXp + "', '" + slayerXp + "', '" + farmingXp + "', '" + runecraftingXp + "', '" + hunterXp + "', '" + constructionXp + "')");
					}
				}
				stmt.close();
				connection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("Failed to make connection!");
		}

		return;
	}
}