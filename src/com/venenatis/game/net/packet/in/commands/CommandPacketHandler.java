package com.venenatis.game.net.packet.in.commands;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.net.packet.in.commands.impl.*;
import com.venenatis.game.world.World;

/**
 * Executes player commands.
 * 
 * @author Lennard
 *
 */
public class CommandPacketHandler implements IncomingPacketListener {

	@Override
	public void handle(Player player, int type, int size) {
		if(player.isJailed()) {
			player.getActionSender().sendMessage("You can't perform any commands while being jailed.");
			return;
		}
		
		if (player.getAttribute("busy") != null) {
			return;
		}
		
		if (!player.getController().canCommand()) {
			return;
		}
		
		String playerCommand = player.getInStream().readString();

		String name = "";
		if (playerCommand.indexOf(' ') > -1) {
			name = playerCommand.substring(0, playerCommand.indexOf(' '));
		} else {
			name = playerCommand;
		}
		name = name.toLowerCase();
		if (COMMAND_MAP.get(name) != null) {
			final Command executedCommand = COMMAND_MAP.get(name);
			boolean canExecute = false;
			if (executedCommand.allowedRanks().isPresent()) {
				for (Rights rightRequired : executedCommand.allowedRanks().get()) {
					if (rightRequired == null) {
						continue;
					}
					if (rightRequired == player.getRights()) {
						canExecute = true;
						break;
					}
				}
			} else {
				canExecute = true;
			}
			if (canExecute) {
				executedCommand.executeCommand(player, playerCommand);
			} else {
				player.getActionSender().sendMessage("You do not have sufficient rights to use this command.");
			}
		} else if (name.length() == 0 || !name.startsWith("/")) {
			player.getActionSender().sendMessage("This command does not exist.");
		} else if (player.getClan() != null) {
			player.getClan().sendChat(player, playerCommand.substring(1));
		} else {
			player.getActionSender().sendMessage("You are currently not in a clan chat.");
		}
	}

	/**
	 * Map containing all the commands.
	 */
	public static final HashMap<String, Command> COMMAND_MAP = new HashMap<String, Command>();

	static {
		reload();
	}

	private static void reload() {
		COMMAND_MAP.put("sd", new NpcJsonSpawn());
		COMMAND_MAP.put("anim", new AnimationCommand());
		COMMAND_MAP.put("auth", new AuthCommand());
		COMMAND_MAP.put("ban", new BanCommand());
		COMMAND_MAP.put("claim", new ClaimCommand());
		COMMAND_MAP.put("combat", new CombatCommand());
		COMMAND_MAP.put("dzone", new DonatorZoneCommand());
		COMMAND_MAP.put("empty", new EmptyCommand());
		COMMAND_MAP.put("forums", new ForumsCommand());
		COMMAND_MAP.put("gfx", new GfxCommand());
		COMMAND_MAP.put("help", new HelpCommand());
		COMMAND_MAP.put("hide", new HideCommand());
		COMMAND_MAP.put("hs", new HighScoreCommand());
		COMMAND_MAP.put("home", new HomeCommand());
		COMMAND_MAP.put("infhp", new InfHpCommand());
		COMMAND_MAP.put("interface", new InterfaceCommand());
		COMMAND_MAP.put("item", new ItemSpawnCommand());
		COMMAND_MAP.put("jail", new JailCommand());
		COMMAND_MAP.put("Kickall", new KickAllCommand());
		COMMAND_MAP.put("kick", new KickCommand());
		COMMAND_MAP.put("level", new LevelCommand());
		COMMAND_MAP.put("owner", new MakeOwnerCommand());
		COMMAND_MAP.put("master", new MaxCommand());
		COMMAND_MAP.put("mute", new MuteCommand());
		COMMAND_MAP.put("object", new ObjectSpawnCommand());
		COMMAND_MAP.put("op", new OPCommand());
		COMMAND_MAP.put("bank", new OpenBankCommand());
		COMMAND_MAP.put("players", new PlayersCommand());
		COMMAND_MAP.put("pos", new PositionCommand());
		COMMAND_MAP.put("redskull", new RedSkullCommand());
		COMMAND_MAP.put("rules", new RulesCommand());
		COMMAND_MAP.put("save", new SaveCommand());
		COMMAND_MAP.put("config", new SendConfigCommand());
		COMMAND_MAP.put("spec", new SetSpecialPowerCommand());
		COMMAND_MAP.put("clipmap", new ShowClipMapCommand());
		COMMAND_MAP.put("simulate", new SimulateCommand());
		COMMAND_MAP.put("skull", new SkullCommand());
		COMMAND_MAP.put("task", new SlayerTaskCommand());
		COMMAND_MAP.put("cnpc", new SpawnCombatNpcCommand());
		COMMAND_MAP.put("npc", new SpawnNPCCommand());
		COMMAND_MAP.put("staff", new StaffCommand());
		COMMAND_MAP.put("staffpanel", new StaffControlPanelCommand());
		COMMAND_MAP.put("sgfx", new StillGfxCommand());
		COMMAND_MAP.put("stuck", new StuckCommand());
		COMMAND_MAP.put("switch", new SwitchCommand());
		COMMAND_MAP.put("tele", new TeleportCommand());
		COMMAND_MAP.put("teletome", new TeleToMeCommand());
		COMMAND_MAP.put("teleto", new TeleToCommand());
		COMMAND_MAP.put("transform", new TransformCommand());
		COMMAND_MAP.put("unban", new UnbanCommand());
		COMMAND_MAP.put("unjail", new UnjailCommand());
		COMMAND_MAP.put("unlockp", new UnlockPrayerCommand());
		COMMAND_MAP.put("unmute", new UnmuteCommand());
		COMMAND_MAP.put("unskull", new UnSkullCommand());
		COMMAND_MAP.put("unyellmute", new UnYellMuteCommand());
		COMMAND_MAP.put("update", new UpdateCommand());
		COMMAND_MAP.put("wmessage", new WorldMessageCommand());
		COMMAND_MAP.put("yellcolor", new YellColorCommand());
		COMMAND_MAP.put("yell", new YellCommand());
		COMMAND_MAP.put("yellmute", new YellMuteCommand());
		COMMAND_MAP.put("jews", new Command() {

			@Override
			protected void executeCommand(Player player, String command) {
				player.getActionSender().sendEntityHint(
						World.getWorld().getNPCs().stream().filter(Objects::nonNull)
						.filter(n->n != null && n.getLocation() != null && n.getLocation()
								.distance(
										player
										.getLocation()) < 10)
						.findFirst().get(), true);
				
			}

			@Override
			protected Optional<Rights[]> allowedRanks() {
				return Optional.of(new Rights[] { Rights.OWNER });
			}
		});
		COMMAND_MAP.put("reloadcommands", new Command() {

			@Override
			protected void executeCommand(Player player, String command) {
				CommandPacketHandler.COMMAND_MAP.clear();
				CommandPacketHandler.reload();
			}

			@Override
			protected Optional<Rights[]> allowedRanks() {
				return Optional.of(new Rights[] { Rights.OWNER });
			}
		});
	}
	
}