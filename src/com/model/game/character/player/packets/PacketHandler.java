package com.model.game.character.player.packets;


import com.model.game.World;
import com.model.game.character.player.CommandPacketHandler;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.PrivateMessaging;
import com.model.game.character.player.packets.actions.BankModifiableX;
import com.model.game.character.player.packets.actions.BankX1;
import com.model.game.character.player.packets.actions.ActionButtonPacketHandler;
import com.model.game.character.player.packets.actions.NpcInteractionPacketHandler;
import com.model.game.character.player.packets.actions.ClickingInGame;
import com.model.game.character.player.packets.actions.ClickingObject;
import com.model.game.character.player.packets.actions.ClickingStuff;
import com.model.game.character.player.packets.actions.ItemActionPacketHandler;
import com.model.game.character.player.packets.actions.ItemOnGroundItem;
import com.model.game.character.player.packets.actions.ItemOnItemPacketHandler;
import com.model.game.character.player.packets.actions.ItemOnNpc;
import com.model.game.character.player.packets.actions.ItemOnObjectPacketHandler;
import com.model.game.character.player.packets.actions.ItemOnPlayerPacketHandler;
import com.model.game.character.player.packets.actions.MagicOnItemPacketHandler;
import com.model.game.character.player.packets.actions.SecondGroundOption;
import com.model.game.character.player.packets.actions.SecondItemOption;
import com.model.game.character.player.packets.actions.ThirdItemOption;
import com.model.game.character.player.packets.actions.Withdraw10Action;
import com.model.game.character.player.packets.actions.Withdraw1Action;
import com.model.game.character.player.packets.actions.Withdraw5Action;
import com.model.game.character.player.packets.actions.WithdrawAllAction;
import com.model.game.character.player.packets.actions.WithdrawAllButOneAction;
import com.model.game.character.player.packets.in.AttackPlayer;
import com.model.game.character.player.packets.in.ChallengePlayer;
import com.model.game.character.player.packets.in.RegionChangePacketHandler;
import com.model.game.character.player.packets.in.CharDesignPacketHandler;
import com.model.game.character.player.packets.in.ChatPacketHandler;
import com.model.game.character.player.packets.in.DialoguePacketHandler;
import com.model.game.character.player.packets.in.DropItemPacketHandler;
import com.model.game.character.player.packets.in.EnterAmountPacketHandler;
import com.model.game.character.player.packets.in.FollowPlayer;
import com.model.game.character.player.packets.in.IdleLogoutPacketHandler;
import com.model.game.character.player.packets.in.InputDialogueStringPacketHandler;
import com.model.game.character.player.packets.in.InputFieldOther;
import com.model.game.character.player.packets.in.SwitchItemPacketHandler;
import com.model.game.character.player.packets.in.PickupGroundItemPacketHandler;
import com.model.game.character.player.packets.in.DefaultPacketHandler;
import com.model.game.character.player.packets.in.Trade;
import com.model.game.character.player.packets.in.WalkingPacketHandler;
import com.model.game.character.player.packets.in.WieldPacketHandler;

public class PacketHandler {

	private static PacketType packetId[] = new PacketType[256];
	private static SubPacketType subPacketId[] = new SubPacketType[256];

	static {

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
		packetId[142] = new InputFieldOther();
		packetId[202] = new IdleLogoutPacketHandler();
		packetId[253] = new SecondGroundOption();
		packetId[14] = new ItemOnPlayerPacketHandler();
		packetId[40] = new DialoguePacketHandler();
		ClickingObject co = new ClickingObject();
		packetId[132] = co;
		packetId[252] = co;
		packetId[70] = co;
		packetId[57] = new ItemOnNpc();
		NpcInteractionPacketHandler cn = new NpcInteractionPacketHandler();
		packetId[72] = cn;
		packetId[131] = cn;
		packetId[155] = cn;
		packetId[17] = cn;
		packetId[18] = cn;
		packetId[21] = cn;
		packetId[16] = new SecondItemOption();
		packetId[75] = new ThirdItemOption();
		packetId[122] = new ItemActionPacketHandler();
		packetId[241] = new ClickingInGame();
		packetId[4] = new ChatPacketHandler();
		packetId[236] = new PickupGroundItemPacketHandler();
		packetId[87] = new DropItemPacketHandler();
		packetId[185] = new ActionButtonPacketHandler();
		packetId[130] = new ClickingStuff();
		packetId[103] = new CommandPacketHandler();
		packetId[214] = new SwitchItemPacketHandler();
		packetId[237] = new MagicOnItemPacketHandler();
		AttackPlayer ap = new AttackPlayer();
		packetId[73] = ap;
		packetId[249] = ap;
		packetId[128] = new ChallengePlayer();
		packetId[39] = new Trade();
		packetId[139] = new FollowPlayer();
		packetId[140] = new WithdrawAllButOneAction();
		packetId[141] = new BankModifiableX();
		subPacketId[41] = new WieldPacketHandler();
		packetId[145] = new Withdraw1Action();
		packetId[117] = new Withdraw5Action();
		packetId[43] = new Withdraw10Action();
		packetId[129] = new WithdrawAllAction();
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
		packetId[53] = new ItemOnItemPacketHandler();
		packetId[192] = new ItemOnObjectPacketHandler();
		packetId[25] = new ItemOnGroundItem();
		RegionChangePacketHandler cr = new RegionChangePacketHandler();
		packetId[121] = cr;
		packetId[210] = cr;
		packetId[60] = new InputDialogueStringPacketHandler();
		packetId[213] = u;
		// packetId[127] = cr;
	}

	public static void processPacket(Player c, int packetType, int packetSize) {
		if (packetType == -1 || packetType == 181) {
			return;
		}
		PacketType p = packetId[packetType];

		if (p != null) {
			try {
				if (p != null) {
					p.processPacket(c, packetType, packetSize);
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
