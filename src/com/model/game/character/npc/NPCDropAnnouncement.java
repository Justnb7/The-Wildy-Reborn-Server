package com.model.game.character.npc;

import java.util.EnumSet;
import java.util.Set;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.definitions.ItemDefinition;
import com.model.utility.Utility;

/**
 * 
 * @author Jason MacKeigan
 * @date Aug 13, 2014, 10:32:24 AM
 */
public class NPCDropAnnouncement {
	NPC npc;

	public NPCDropAnnouncement(NPC npc) {
		this.npc = npc;
	}

	public void announce(Player player, int itemId, int amount) {
		if (this.npc == null)
			return;

		NPCName npc = NPCName.get(NPC.getName(this.npc.getId()).replaceAll("_", " "));
		
		if (npc != null) {
			for (Player regionalPlayer : World.getWorld().getPlayers()) {
				if (regionalPlayer == null) {
					continue;
				}
				if (regionalPlayer.distanceToPoint(this.npc.getX(), this.npc.getY()) < 40) {
					regionalPlayer.getActionSender().sendMessage("<col=0B6121>" + Utility.capitalize(player.getName()) + " has received " + amount + " x " + ItemDefinition.get(itemId).getName() + "</col>.");
				}
			}
		}
	}

	public enum NPCName {
		KING_BLACK_DRAGON, GENERAL_GRAARDOR, COMMANDER_ZILYANA, KREE_ARRA, KRIL_TSUTSAROTH, DAGANNOTH_REX, DAGANNOTH_SUPREME, DAGANNOTH_PRIME, BARRELCHEST, CHAOS_ELEMENTAL, KALPHITE_QUEEN, KRAKEN, GIANT_MOLE, SCORPIA, VENEATIS, CALLISTO, VETION,ZULRAH, CORPOREAL_BEAST, SMOKE_DEVIL;

		@Override
		public String toString() {
			String name = this.name().toLowerCase();
			name = name.replaceAll("_", " ");
			name = Utility.ucFirst(name);
			return name;
		}

		static final Set<NPCName> NAMES = EnumSet.allOf(NPCName.class);

		public static NPCName get(String name) {
			for (NPCName boss : NAMES) {
				if (boss.toString().equalsIgnoreCase(name))
					return boss;
			}
			return null;
		}
	}

}
