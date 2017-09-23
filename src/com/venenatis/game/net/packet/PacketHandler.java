package com.venenatis.game.net.packet;


import com.venenatis.game.content.PrivateMessaging;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.in.*;
import com.venenatis.game.world.World;

public class PacketHandler {

	private static IncomingPacketListener packetId[] = new IncomingPacketListener[256];

	static {

		//Redone by Patrick van Elderen
		
		//Player options
		PlayerOptionPacketHandler pop = new PlayerOptionPacketHandler();
		packetId[128] = pop;
		packetId[39] = pop;
		packetId[139] = pop;
		packetId[73] = pop;
		packetId[249] = pop;
		
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
		packetId[135] = wap;
		packetId[135] = wap;
		packetId[208] = wap;
		
		packetId[41] = new WieldPacketHandler();
		packetId[241] = new ClickOnGameScreen();
		
		//PI
		DefaultPacketHandler u = new DefaultPacketHandler();
		
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
		packetId[214] = new MoveItemPacketHandler();
		packetId[140] = new WithdrawAllButOneAction();
		packetId[141] = new WithdrawModifiableX();
		packetId[101] = new SetAppearancePacketHandler();
		final PrivateMessaging pm = new PrivateMessaging();
		packetId[188] = pm;
		packetId[126] = pm;
		packetId[215] = pm;
		packetId[59] = pm;
		packetId[95] = pm;
		packetId[133] = pm;
		packetId[74] = pm;
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

	public static void processPacket(Player player, int packetType, int packetSize) {

		if (packetType == -1 || packetType == 181) {
			return;
		}
		
		IncomingPacketListener p = packetId[packetType];

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
