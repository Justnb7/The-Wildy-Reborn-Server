package com.model.game.character.player;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.character.Animation;
import com.model.game.character.player.content.KillTracker;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.out.*;
import com.model.game.item.Item;
import com.model.game.item.bank.BankTab;
import com.model.game.location.Position;
import com.model.utility.json.definitions.ItemDefinition;

import java.text.DecimalFormat;
import java.util.Arrays;

public class PlayerAssistant {

    private final Player player;
    

    public PlayerAssistant(Player Client) {
        this.player = Client;
    }
        
    public void resetTb() {
        player.teleblockLength = 0;
        player.teleblock.stop();
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

    public void useOperate(int itemId) {
    	
        switch (itemId) {
			
        case 2572:
        	KillTracker.open(player);
            break;
            
        }
    }

    public void openBank() {
        player.stopSkillTask();
    	
		if (!player.getAccount().getType().canBank()) {
			player.getActionSender().sendMessage("You're restricted to bank because of your account type.");
			return;
		}
		
        if (player.getArea().inWild() && !(player.getRights().isBetween(2, 3))) {
			player.getActionSender().sendMessage("You can't bank in the wilderness!");
			return;
		}
        
        if (Trading.isTrading(player)) {
            Trading.decline(player);
        }
		
        if (player.takeAsNote)
        	player.getActionSender().sendConfig(115, 1);
        else
        	player.getActionSender().sendConfig(115, 0);
    	
    	boolean openFirstTab = !player.isBanking();
    	
    	if(openFirstTab) {
    		//cheap hax for sending the main tab
        	BankTab tab = player.getBank().getBankTab(0);
    		player.getBank().setCurrentBankTab(tab);
    	}
		
        if (player.getBank().getBankSearch().isSearching()) {
            player.getBank().getBankSearch().reset();
        }
        
        player.write(new SendSoundPacket(1457, 0, 0));
        player.getActionSender().sendString("Search", 58113);
        
        if (player.getOutStream() != null && player != null) {
        	player.setBanking(true);
            player.getItems().resetItems(5064);
            player.getBank().resetBank();
            player.getBank().resetTempItems();
            player.getOutStream().writeFrame(248);
            player.getOutStream().writeWordA(5292);
            player.getOutStream().writeShort(5063);
            player.getActionSender().sendString(player.getName() + "'s Bank", 58064);
        }
    }

    public void sendFriendServerStatus(final int i) { // friends and ignore list status
        if (this.player.getOutStream() != null && this.player != null) {
            this.player.getOutStream().writeFrame(221);
            this.player.getOutStream().writeByte(i);
        }
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

	public void displayReward(Item... items) {
		player.outStream.createFrameVarSizeWord(53);
		player.outStream.writeWord(6963);
		player.outStream.writeWord(items.length);

		for (Item item : items) {
			if (item.amount > 254) {
				player.outStream.writeByte(255);
				player.outStream.writeDWord_v2(item.amount);
			} else {
				player.outStream.writeByte(item.amount);
			}
			if (item.id > 0) {
				player.outStream.writeWordBigEndianA(item.id + 1);
			} else {
				player.outStream.writeWordBigEndianA(0);
			}
		}
		player.outStream.endFrameVarSizeWord();
		player.flushOutStream();
		player.write(new SendInterfacePacket(6960));
	}

	public void sendItems(Item... items) {
		player.outStream.createFrameVarSizeWord(53);
		player.outStream.writeWord(6963);
		player.outStream.writeWord(items.length);

		for (Item item : items) {
			if (item.amount > 254) {
				player.outStream.writeByte(255);
				player.outStream.writeDWord_v2(item.amount);
			} else {
				player.outStream.writeByte(item.amount);
			}
			if (item.id > 0) {
				player.outStream.writeWordBigEndianA(item.id + 1);
			} else {
				player.outStream.writeWordBigEndianA(0);
			}
		}
		player.outStream.endFrameVarSizeWord();
		player.flushOutStream();
		player.write(new SendInterfacePacket(6960));
	}

	public void restoreHealth() {
    	player.faceEntity(player);
		player.stopMovement();
		player.setSpecialAmount(100);
		player.getWeaponInterface().restoreWeaponAttributes();
		player.lastVeng.reset();
		player.setVengeance(false);
		player.setUsingSpecial(false);
		player.attackDelay = 10;
		player.infection = 0;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		player.skullIcon = -1;
		player.playAnimation(Animation.create(65535));
		resetTb();
        player.setFollowing(null);
		player.getActionSender().sendRemoveInterfacePacket();
    }
	
}