package com.model.game.character.player.packets;


import com.model.game.World;
import com.model.game.character.player.CommandPacketHandler;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.PrivateMessaging;
import com.model.game.character.player.packets.in.ActionButtonPacketHandler;
import com.model.game.character.player.packets.in.AttackPlayer;
import com.model.game.character.player.packets.in.BankModifiableX;
import com.model.game.character.player.packets.in.BankX1;
import com.model.game.character.player.packets.in.ChallengePlayer;
import com.model.game.character.player.packets.in.RegionChangePacketHandler;
import com.model.game.character.player.packets.in.SecondGroundOption;
import com.model.game.character.player.packets.in.CharDesignPacketHandler;
import com.model.game.character.player.packets.in.ChatPacketHandler;
import com.model.game.character.player.packets.in.ClickOnGameScreen;
import com.model.game.character.player.packets.in.ObjectOptionPacketHandler;
import com.model.game.character.player.packets.in.CloseInterfacePacketHandler;
import com.model.game.character.player.packets.in.DialoguePacketHandler;
import com.model.game.character.player.packets.in.EnterAmountPacketHandler;
import com.model.game.character.player.packets.in.FollowPlayer;
import com.model.game.character.player.packets.in.IdleLogoutPacketHandler;
import com.model.game.character.player.packets.in.InputDialogueStringPacketHandler;
import com.model.game.character.player.packets.in.InputFieldPacketHandler;
import com.model.game.character.player.packets.in.ItemOnNpc;
import com.model.game.character.player.packets.in.ItemOnObjectPacketHandler;
import com.model.game.character.player.packets.in.ItemOnPlayerPacketHandler;
import com.model.game.character.player.packets.in.ItemOptionPacket;
import com.model.game.character.player.packets.in.NpcInteractionPacketHandler;
import com.model.game.character.player.packets.in.SwitchItemPacketHandler;
import com.model.game.character.player.packets.in.DefaultPacketHandler;
import com.model.game.character.player.packets.in.Trade;
import com.model.game.character.player.packets.in.WalkingPacketHandler;
import com.model.game.character.player.packets.in.WieldPacketHandler;
import com.model.game.character.player.packets.in.WithdrawActionsPacketHandler;
import com.model.game.character.player.packets.in.WithdrawAllButOneAction;

public class PacketHandler {

	private static PacketType packetId[] = new PacketType[256];
	private static SubPacketType subPacketId[] = new SubPacketType[256];

	static {

		//Redone by Patrick van Elderen
		
		//Item options
		ItemOptionPacket iop = new ItemOptionPacket();
		packetId[122] = iop;
		packetId[16] = iop;
		packetId[75] = iop;
		packetId[87] = iop;
		packetId[236] = iop;
		packetId[53] = iop;
		packetId[237] = iop;
		packetId[25] = iop;
		
		//Object interactions
		ObjectOptionPacketHandler co = new ObjectOptionPacketHandler();
		packetId[132] = co;
		packetId[252] = co;
		packetId[70] = co;
		
		//Npc interactions
		NpcInteractionPacketHandler cn = new NpcInteractionPacketHandler();
		packetId[72] = cn;
		packetId[131] = cn;
		packetId[155] = cn;
		packetId[17] = cn;
		packetId[18] = cn;
		packetId[21] = cn;
		
		//Withdraw actions
		WithdrawActionsPacketHandler wap = new WithdrawActionsPacketHandler();
		packetId[145] = wap;
		packetId[117] = wap;
		packetId[43] = wap;
		packetId[129] = wap;
		
		//PI
		DefaultPacketHandler u = new DefaultPacketHandler();
		subPacketId[41] = new WieldPacketHandler();
		packetId[241] = new ClickOnGameScreen();
		
		packetId[3] = u;
		packetId[202] = u;
		packetId[77] = u;
		packetId[86] = u;
		packetId[78] = u;
		packetId[36] = u;
		packetId[228] = u;
		packetId[226] = u;
		packetId[246] = u;
		packetId[218] = u;
		packetId[148] = u;
		packetId[183] = u;
		packetId[230] = u;
		packetId[136] = u;
		packetId[189] = u;
		packetId[152] = u;
		packetId[200] = u;
		packetId[85] = u;
		packetId[165] = u;
		packetId[238] = u;
		packetId[234] = u;
		packetId[150] = u;
		packetId[142] = new InputFieldPacketHandler();
		packetId[202] = new IdleLogoutPacketHandler();
		packetId[253] = new SecondGroundOption();
		packetId[14] = new ItemOnPlayerPacketHandler();
		packetId[40] = new DialoguePacketHandler();
		packetId[57] = new ItemOnNpc();
		packetId[4] = new ChatPacketHandler();
		packetId[185] = new ActionButtonPacketHandler();
		packetId[130] = new CloseInterfacePacketHandler();
		packetId[103] = new CommandPacketHandler();
		packetId[214] = new SwitchItemPacketHandler();
		AttackPlayer ap = new AttackPlayer();
		packetId[73] = ap;
		packetId[249] = ap;
		packetId[128] = new ChallengePlayer();
		packetId[39] = new Trade();
		packetId[139] = new FollowPlayer();
		packetId[140] = new WithdrawAllButOneAction();
		packetId[141] = new BankModifiableX();
		packetId[101] = new CharDesignPacketHandler();
		final PrivateMessaging pm = new PrivateMessaging();
		packetId[188] = pm;
		packetId[126] = pm;
		packetId[215] = pm;
		packetId[59] = pm;
		packetId[95] = pm;
		packetId[133] = pm;
		packetId[74] = pm;
		packetId[135] = new BankX1();
		packetId[208] = new EnterAmountPacketHandler();
		WalkingPacketHandler w = new WalkingPacketHandler();
		packetId[98] = w;
		packetId[164] = w;
		packetId[248] = w;
		packetId[192] = new ItemOnObjectPacketHandler();
		RegionChangePacketHandler cr = new RegionChangePacketHandler();
		packetId[121] = cr;
		packetId[210] = cr;
		packetId[60] = new InputDialogueStringPacketHandler();
		packetId[213] = u;
		// packetId[127] = cr;
	}

	public static void processPacket(Player c, int packetType, int packetSize) {
		//System.out.println("Unhandled packet type: " + packetType + " - size: " + packetSize);
		if (packetType == -1 || packetType == 181) {
			return;
		}
		PacketType p = packetId[packetType];

		if (p != null) {
			try {
				if (p != null) {
					p.handle(c, packetType, packetSize);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Unhandled packet type: " + packetType + " - size: " + packetSize);
			World.getWorld().queueLogout(c);
		}
	}

	public static void processSubPacket(Player c, int packetType, int packetSize) {
		if (packetType == -1 || packetType == 181) {
			return;
		}
		SubPacketType p = subPacketId[packetType];

		if (p != null) {
			try {
				if (p != null) {
					p.processSubPacket(c, packetType, packetSize);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Unhandled subpacket type: " + packetType + " - size: " + packetSize);
			World.getWorld().queueLogout(c);
		}
	}
}
