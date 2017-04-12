package com.model.game.character.player;

import java.text.DecimalFormat;
import java.util.Arrays;

import com.model.game.character.player.packets.out.SendChatBoxInterfacePacket;
import com.model.game.item.Item;
import com.model.game.location.Position;
import com.model.utility.json.definitions.ItemDefinition;

public class PlayerAssistant {

    private final Player player;
    

    public PlayerAssistant(Player Client) {
        this.player = Client;
    }

    public void move(Position target) {
        if (player == null)
            return;
        if (player.isBusy()) {
            return;
        }
		if (!player.lastSpear.elapsed(4000)) {
			player.getActionSender().sendMessage("You're trying to move too fast.");
			return;
		}
        player.getMovementHandler().reset();
        player.teleportToX = target.getX();
        player.teleportToY = target.getY();
        player.teleHeight = target.getZ();
        player.setTeleportTarget(target);
        player.getSkillCyclesTask().stop();
        System.out.println("to "+Arrays.toString(new int[] {target.getX(), target.getY(), target.getZ()}));
    }
    
    public void resetAutoCast() {
        player.autocastId = 0;
        player.onAuto = false;
        player.autoCast = false;
        player.getActionSender().sendConfig(108, 0);
    }
	
	DecimalFormat format = new DecimalFormat("##.##");

	public static double getRatio(int kills, int deaths) {
		double ratio = kills / Math.max(1D, deaths);
		return ratio;
	}

	public double getRatio(Player player) {
		return getRatio(player.getKillCount(), player.getDeathCount());
	}

	public String displayRatio(Player player) {
		return format.format(getRatio(player));
	}
	
	public void destroyItem(Item item) {
		player.getActionSender().sendUpdateItem(14171, item.getId(), 0, 1);
		player.getActionSender().sendString("Are you sure you want to drop this item?", 14174);
		player.getActionSender().sendString("Yes.", 14175);
		player.getActionSender().sendString("No.", 14176);
		player.getActionSender().sendString("", 14177);
		player.getActionSender().sendString("This item is valuable, you will not", 14182);
		player.getActionSender().sendString("get it back once lost.", 14183);
		player.getActionSender().sendString(ItemDefinition.forId(item.getId()).getName(), 14184);
		player.write(new SendChatBoxInterfacePacket(14170));
	}

	public void handleDestroyItem() {
		if (player.getDestroyItem() != -1) {
			if (player.getItems().playerHasItem(player.getDestroyItem())) {
				player.getItems().deleteItem(player.getDestroyItem());
				player.setDestroyItem(-1);
				player.getActionSender().sendRemoveInterfacePacket();
			}
		}
	}
	
}