package com.venenatis.game.net.packet;

import com.venenatis.game.content.PrivateMessaging;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.in.*;
import com.venenatis.game.net.packet.in.commands.CommandPacketHandler;
import com.venenatis.game.world.World;

public class PacketHandler {

	private static IncomingPacketListener PACKET_MAP[] = new IncomingPacketListener[256];

	static {

		//Redone by Patrick van Elderen
		
		//Player options
		PlayerOptionPacketHandler pop = new PlayerOptionPacketHandler();
		PACKET_MAP[153] = pop;
		PACKET_MAP[39] = pop;
		PACKET_MAP[139] = pop;
		PACKET_MAP[73] = pop;
		PACKET_MAP[249] = pop;
		
		//Item options
		ItemOptionPacket iop = new ItemOptionPacket();
		PACKET_MAP[122] = iop;
		PACKET_MAP[16] = iop;
		PACKET_MAP[75] = iop;
		PACKET_MAP[87] = iop;
		PACKET_MAP[236] = iop;
		PACKET_MAP[53] = iop;
		PACKET_MAP[237] = iop;
		PACKET_MAP[25] = iop;
		
		//Object interactions
		ObjectOptionPacketHandler co = new ObjectOptionPacketHandler();
		PACKET_MAP[132] = co;
		PACKET_MAP[252] = co;
		PACKET_MAP[70] = co;
		
		//Npc interactions
		NpcInteractionPacketHandler cn = new NpcInteractionPacketHandler();
		PACKET_MAP[72] = cn;
		PACKET_MAP[131] = cn;
		PACKET_MAP[155] = cn;
		PACKET_MAP[17] = cn;
		PACKET_MAP[18] = cn;
		PACKET_MAP[21] = cn;
		
		//Withdraw actions
		WithdrawActionsPacketHandler wap = new WithdrawActionsPacketHandler();
		PACKET_MAP[145] = wap;
		PACKET_MAP[117] = wap;
		PACKET_MAP[43] = wap;
		PACKET_MAP[129] = wap;
		PACKET_MAP[135] = wap;
		PACKET_MAP[135] = wap;
		PACKET_MAP[208] = wap;
		
		PACKET_MAP[41] = new WieldPacketHandler();
		PACKET_MAP[241] = new ClickOnGameScreen();
		
		PACKET_MAP[171] = new ExamineOptionHandler();
		PACKET_MAP[172] = new OptionMenuPacketHandler();
		
		//PI
		DefaultPacketHandler u = new DefaultPacketHandler();
		
		PACKET_MAP[3] = u;
		PACKET_MAP[202] = u;
		PACKET_MAP[77] = u;
		PACKET_MAP[86] = u;
		PACKET_MAP[78] = u;
		PACKET_MAP[36] = u;
		PACKET_MAP[228] = u;
		PACKET_MAP[226] = u;
		PACKET_MAP[246] = u;
		PACKET_MAP[218] = u;
		PACKET_MAP[148] = u;
		PACKET_MAP[183] = u;
		PACKET_MAP[230] = u;
		PACKET_MAP[136] = u;
		PACKET_MAP[189] = u;
		PACKET_MAP[152] = u;
		PACKET_MAP[200] = u;
		PACKET_MAP[85] = u;
		PACKET_MAP[165] = u;
		PACKET_MAP[238] = u;
		PACKET_MAP[234] = u;
		PACKET_MAP[150] = u;
		PACKET_MAP[142] = new InputFieldPacketHandler();
		PACKET_MAP[202] = new IdleLogoutPacketHandler();
		PACKET_MAP[253] = new SecondGroundOption();
		PACKET_MAP[14] = new ItemOnPlayerPacketHandler();
		PACKET_MAP[40] = new DialoguePacketHandler();
		PACKET_MAP[57] = new ItemOnNpc();
		PACKET_MAP[4] = new ChatPacketHandler();
		PACKET_MAP[185] = new ActionButtonPacketHandler();
		PACKET_MAP[130] = new CloseInterfacePacketHandler();
		PACKET_MAP[103] = new CommandPacketHandler();
		PACKET_MAP[214] = new MoveItemPacketHandler();
		PACKET_MAP[140] = new WithdrawAllButOneAction();
		PACKET_MAP[141] = new WithdrawModifiableX();
		PACKET_MAP[101] = new SetAppearancePacketHandler();
		final PrivateMessaging pm = new PrivateMessaging();
		PACKET_MAP[188] = pm;
		PACKET_MAP[126] = pm;
		PACKET_MAP[215] = pm;
		PACKET_MAP[59] = pm;
		PACKET_MAP[95] = pm;
		PACKET_MAP[133] = pm;
		PACKET_MAP[74] = pm;
		WalkingPacketHandler w = new WalkingPacketHandler();
		PACKET_MAP[98] = w;
		PACKET_MAP[164] = w;
		PACKET_MAP[248] = w;
		PACKET_MAP[192] = new ItemOnObjectPacketHandler();
		RegionChangePacketHandler cr = new RegionChangePacketHandler();
		PACKET_MAP[121] = cr;
		PACKET_MAP[210] = cr;
		PACKET_MAP[213] = new ActionButtonWithMultipleActionsPacketHandler();
		PACKET_MAP[60] = new InputDialogueStringPacketHandler();
		PACKET_MAP[60] = new InputDialogueStringPacketHandler();
		PACKET_MAP[127] = new StringInputClanPacketHandler();
	}

	public static void processPacket(Player player, int packetType, int packetSize) {

		if (packetType == -1 || packetType == 181) {
			return;
		}
		
		IncomingPacketListener p = PACKET_MAP[packetType];

		if (p != null) {
			try {
				if (p != null) {
					p.handle(player, packetType, packetSize);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Unhandled packet type: " + packetType + " - size: " + packetSize);
			World.getWorld().queueLogout(player);
		}
	}
}
