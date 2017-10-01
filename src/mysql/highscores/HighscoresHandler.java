package mysql.highscores;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;

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
		final double overallXp = player.getSkills().getTotalExp();
		
		/**
		 * Represents attack xp
		 */
		final double attackXp = player.getSkills().getExperience(Skills.ATTACK);
		
		/**
		 * Represents defence xp
		 */
		final double defenceXp = player.getSkills().getExperience(Skills.DEFENCE);
		
		/**
		 * Represents strength xp
		 */
		final double strengthXp = player.getSkills().getExperience(Skills.STRENGTH);
		
		/**
		 * Represents constitution xp
		 */
		final double constitutionXp = player.getSkills().getExperience(Skills.HITPOINTS);
		
		/**
		 * Represents ranged xp
		 */
		final double rangedXp = player.getSkills().getExperience(Skills.RANGE);
		
		/**
		 * Represents prayer xp
		 */
		final double prayerXp = player.getSkills().getExperience(Skills.PRAYER);
		
		/**
		 * Represents magic xp
		 */
		final double magicXp = player.getSkills().getExperience(Skills.MAGIC);
		
		/**
		 * Represents cooking xp
		 */
		final double cookingXp = player.getSkills().getExperience(Skills.COOKING);
		
		/**
		 * Represents woodcutting xp
		 */
		final double woodcuttingXp = player.getSkills().getExperience(Skills.WOODCUTTING);
		
		/**
		 * Represents fletching xp
		 */
		final double fletchingXp = player.getSkills().getExperience(Skills.FLETCHING);
		
		/**
		 * Represents fishing xp
		 */
		final double fishingXp = player.getSkills().getExperience(Skills.FISHING);
		
		/**
		 * Represents firemaking xp
		 */
		final double firemakingXp = player.getSkills().getExperience(Skills.FIREMAKING);
		
		/**
		 * Represents crafting xp
		 */
		final double craftingXp = player.getSkills().getExperience(Skills.CRAFTING);
		
		/**
		 * Represents smithing xp
		 */
		final double smithingXp = player.getSkills().getExperience(Skills.SMITHING);
		
		/**
		 * Represents mining xp
		 */
		final double miningXp = player.getSkills().getExperience(Skills.MINING);
		
		/**
		 * Represents herblore xp
		 */
		final double herbloreXp = player.getSkills().getExperience(Skills.HERBLORE);
		
		/**
		 * Represents agility xp
		 */
		final double agilityXp = player.getSkills().getExperience(Skills.AGILITY);
		
		/**
		 * Represents thieving xp
		 */
		final double thievingXp = player.getSkills().getExperience(Skills.THIEVING);
		
		/**
		 * Represents slayer xp
		 */
		final double slayerXp = player.getSkills().getExperience(Skills.SLAYER);
		
		/**
		 * Represents farming xp
		 */
		final double farmingXp = player.getSkills().getExperience(Skills.FARMING);
		
		/**
		 * Represents runecrafting xp
		 */
		final double runecraftingXp = player.getSkills().getExperience(Skills.RUNECRAFTING);
		
		/**
		 * Represents hunter xp
		 */
		final double hunterXp = player.getSkills().getExperience(Skills.HUNTER);
		
		/**
		 * Represents construction xp
		 */
		final double constructionXp = player.getSkills().getExperience(Skills.CONSTRUCTION);
		
		/**
		 * Creates new instance of jdbc driver if that driver exists
		 */
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
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
			connection = DriverManager.getConnection("jdbc:mysql://66.85.79.59:3306/venenasn_hiscores", "venenasn_main", "Z9&^;h$tTi%N");
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
				ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM `hs_users` WHERE username='" + username + "'");
				if (rs.next()) {
					if (rs.getInt("count") > 0) {
						stmt.executeUpdate("UPDATE `hs_users` SET overall_xp = '" + overallXp + "', attack_xp = '" + attackXp  + "', defence_xp = '" + defenceXp  + "', strength_xp = '" + strengthXp + "', constitution_xp = '" + constitutionXp + "', ranged_xp = '" + rangedXp + "', prayer_xp = '" + prayerXp + "', magic_xp = '" + magicXp + "', cooking_xp = '" + cookingXp + "', woodcutting_xp = '" + woodcuttingXp + "', fletching_xp = '" + fletchingXp + "', fishing_xp = '" + fishingXp + "', firemaking_xp = '" + firemakingXp + "', crafting_xp = '" + craftingXp + "', smithing_xp = '" + smithingXp + "', mining_xp = '" + miningXp + "', herblore_xp = '" + herbloreXp + "', agility_xp = '" + agilityXp + "', thieving_xp = '" + thievingXp + "', slayer_xp = '" + slayerXp + "', farming_xp = '" + farmingXp + "', runecrafting_xp = '" + runecraftingXp + "', hunter_xp = '" + hunterXp + "', construction_xp = '" + constructionXp + "' WHERE username = '" + username + "'");
					} else {
						stmt.executeUpdate("INSERT INTO `hs_users` (username, rights, overall_xp, attack_xp, defence_xp, strength_xp, constitution_xp, ranged_xp, prayer_xp, magic_xp, cooking_xp, woodcutting_xp, fletching_xp, fishing_xp, firemaking_xp, crafting_xp, smithing_xp, mining_xp, herblore_xp, agility_xp, thieving_xp, slayer_xp, farming_xp, runecrafting_xp, hunter_xp, construction_xp) VALUES ('" + username + "', '" + gameMode + "', '" + overallXp + "', '" + attackXp + "', '" + defenceXp + "', '" + strengthXp + "', '" + constitutionXp + "', '" + rangedXp + "', '" + prayerXp + "', '" + magicXp + "', '" + cookingXp + "', '" + woodcuttingXp + "', '" + fletchingXp + "', '" + fishingXp + "', '" + firemakingXp + "', '" + craftingXp + "', '" + smithingXp + "', '" + miningXp + "', '" + herbloreXp + "', '" + agilityXp + "', '" + thievingXp + "', '" + slayerXp + "', '" + farmingXp + "', '" + runecraftingXp + "', '" + hunterXp + "', '" + constructionXp + "')");
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