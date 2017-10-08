package com.venenatis.game.content.rewards;

import java.util.Arrays;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;
import com.venenatis.game.util.chance.Chance;
import com.venenatis.game.util.chance.WeightedChance;
import com.venenatis.game.world.World;

public class BossRewardChest {
	
	/**
	 * All possible loots from the boss chest
	 */
	public static Chance<Item> BOSSCHEST = new Chance<Item>(Arrays.asList( 
			
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(995, Utility.randomNumber(25_000_000))),//coins
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(995, Utility.randomNumber(50_000_000))),//coins
			new WeightedChance<Item>(WeightedChance.RARE, new Item(995, Utility.randomNumber(75_000_000))),//coins
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(995, Utility.randomNumber(100_000_000))),//coins
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(13307, Utility.randomNumber(25))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(13307, Utility.randomNumber(50))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13307, Utility.randomNumber(75))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(13307, Utility.randomNumber(100))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(13307, Utility.randomNumber(150))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(990, Utility.randomNumber(10))),//Crystal Keys
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(10551, 1)),//Fighter Torso
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12759, 1)),//Dark bow green mix
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12761, 1)),//dark bow mix
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12763, 1)),//dark bow mix
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12757, 1)),//dark bow mix
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2572, 1)),//Ring of Wealth
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2577, 1)),//Ranger boots
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13887, 1)),//vesta plate
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13893, 1)),//vesta legs
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13899, 1)),//vesta sword
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13884, 1)),//statius top
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13890, 1)),//statius legs
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13896, 1)),//statius helm
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13902, 1)),//statius hammer
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4151, 1)),//Abyssal Whip
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12004, 1)),//kraken tent
			new WeightedChance<Item>(WeightedChance.RARE, new Item(6585, 1)),//Amulet of fury
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12783, 1)),//Ring of wealth scroll
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12771, 1)),//Lava whip mix
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12769, 1)),//Frozen whip mix
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12804, 1)),//saradomins tear
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11824, 1)),//zamorakian spear
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(20851, 1)),//Olmlet pet
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(21000, 1)),//Twisted Buckler
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(20997, 1)),//Twisted Bow
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(21003, 1)),//Elder Maul
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(21015, 1)),//Dinh's Bulwark
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(21012, 1)),//Dragonhunter Crossbow
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(19553, 1)),//Amulet of torture
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(19544, 1)),//Tormented Bracelet
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(19547, 1)),//Necklace of anguish
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(19550, 1)),//Ring of suffering
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(21018, 1)),//Ancestrial
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(21021, 1)),//Ancestrial
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(21024, 1)),//Ancestrial
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(21006, 1))//Kodai Wand
			
		));
	
	public static void open(final Player player, Location position) {
		if (!player.getInventory().hasItemAmount(2944, 1)) {
			SimpleDialogues.sendStatement(player, "You need a Key of drops to loot this chest.");
			return;
		}
		player.getActionSender().sendMessage("You unlock the chest with your key.");
		Item reward = BOSSCHEST.nextObject().get(); 
		String name = reward.getDefinition().getName();
		player.getInventory().remove(new Item(2944));
		player.getInventory().addOrCreateGroundItem(player, reward);
		World.getWorld().sendWorldMessage("<img=20><col=8A2BE2>[Chest of drops]:</col> <col=7171C6>"+player.getUsername()+"</col> just received <col=3D9140>"+name+"</col>.", false);
		player.playAnimation(Animation.create(881));
	}

}
