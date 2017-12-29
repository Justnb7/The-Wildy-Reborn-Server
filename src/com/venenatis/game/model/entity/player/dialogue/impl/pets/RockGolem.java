package com.venenatis.game.model.entity.player.dialogue.impl.pets;

import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class RockGolem extends Dialogue {
	
	/**
	 * The pet identifier
	 */
	private int petId;

	@Override
	protected void start(Object... parameters) {
		petId = player.getPet();
		if (petId == Pet.ROCK_GOLEM_TIN.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I feel strangely emotionless and empty. Maybe I should feel sad about it, but I can't.");
			setPhase(5);
		} else if (petId == Pet.ROCK_GOLEM_COPPER.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I have an idea for a song.");
			setPhase(8);
		} else if (petId == Pet.ROCK_GOLEM_IRON.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Truth is, I am Iron Golem.");
			setPhase(11);
		} else if (petId == Pet.ROCK_GOLEM_BLURITE.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I have an idea for a song.");
			setPhase(12);
		} else if (petId == Pet.ROCK_GOLEM_SILVER.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Oh dear, I've gone all pale.");
			setPhase(15);
		} else if (petId == Pet.ROCK_GOLEM_DAEYALT.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Did you ever hear the tragedy of Queen Efaritay the Fair?");
			setPhase(17);
		} else if (petId == Pet.ROCK_GOLEM_COAL.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "So near and yet so far... if my atoms were arranged a little differently, I could be diamond.");
			setPhase(20);
		} else if (petId == Pet.ROCK_GOLEM_ELEMENTAL.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I am so in my element right now.");
			setPhase(22);
		} else if (petId == Pet.ROCK_GOLEM_GOLD.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I'm totally showing the bling today.");
			setPhase(23);
		} else if (petId == Pet.ROCK_GOLEM_GRANITE.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "No-one appreciates granite.");
			setPhase(26);
		} else if (petId == Pet.ROCK_GOLEM_MITHRIL.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I feel sad today. Very blue.");
			setPhase(31);
		} else if (petId == Pet.ROCK_GOLEM_LOVAKITE.getNpc()) {
			send(DialogueType.PLAYER, Expression.DEFAULT, "So how do you pronounce Lovakengj?");
			setPhase(37);
		} else if (petId == Pet.ROCK_GOLEM_ADAMANTITE.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I may be green, but I'm not an environmentalist.");
			setPhase(40);
		} else if (petId == Pet.ROCK_GOLEM_RUNITE.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I'm confused. It takes incredible skill to smith anything from my ore, yet the items you'd get are", "terribly mediocre.");
			setPhase(45);
		} else if (petId == Pet.ROCK_GOLEM_AMETHYST.getNpc()) {
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Your world is amazing. I truly am in awe.");
			setPhase(47);
		} else {
			send(DialogueType.PLAYER, Expression.DEFAULT, "So you're made entirely of rocks?");
			setPhase(0);
		}
	}
	
	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Not quite, my body is formed mostly of minerals.");
			setPhase(1);
			break;
		case 1:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Aren't minerals just rocks?");
			setPhase(2);
			break;
		case 2:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "No, rocks are rocks, minerals are minerals. I am formed from minerals.");
			setPhase(3);
			break;
		case 3:
			send(DialogueType.PLAYER, Expression.DEFAULT, "But you're a Rock Golem...");
			setPhase(4);
			break;
		case 4:
			stop();
			break;
		case 5:
			send(DialogueType.PLAYER, Expression.DEFAULT, "You can't feel sad?");
			setPhase(6);
			break;
		case 6:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Correct. Now, if I only had a heart...");
			setPhase(7);
			break;
		case 7:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I'm not sure it works like that around here.");
			setPhase(4);
			break;
		case 8:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Oh?");
			setPhase(9);
			break;
		case 9:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Copper-copper-copper Cophelia - you come and go, you come and go...");
			setPhase(10);
			break;
		case 10:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Mmmhmm.");
			setPhase(4);
			break;
		case 11:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I can see that.");
			setPhase(4);
			break;
		case 12:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Oh?");
			setPhase(13);
			break;
		case 13:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I'm blue, da-ba-dee da-ba-da.");
			setPhase(14);
			break;
		case 14:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I can see that.");
			setPhase(4);
			break;
		case 15:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Are you okay?");
			setPhase(16);
			break;
		case 16:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "It must be something I ate.");
			setPhase(4);
			break;
		case 17:
			send(DialogueType.PLAYER, Expression.DEFAULT, "No?");
			setPhase(18);
			break;
		case 18:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I thought not.");
			setPhase(19);
			break;
		case 19:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "It's not a story the Myreque would tell you.");
			setPhase(4);
			break;
		case 20:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Would you enjoy being a diamond?");
			setPhase(21);
			break;
		case 21:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I expect I would, until someone tried chipping bits off me with a chisel. I'm probably safer as coal.");
			setPhase(4);
			break;
		case 22:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Sigh...");
			setPhase(4);
			break;
		case 23:
			send(DialogueType.PLAYER, Expression.DEFAULT, "That's nice for you.");
			setPhase(24);
			break;
		case 24:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "A little. Though sometimes I just feel like I'm being used as a status symbol, and no-one appreciates", "me for me.");
			setPhase(25);
			break;
		case 25:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Oh dear.");
			setPhase(4);
			break;
		case 26:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Why do you say that?");
			setPhase(27);
			break;
		case 27:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I know how it works. No-one actually wants granite. They just chop us up and throw our pieces on", "the floor.");
			setPhase(28);
			break;
		case 28:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Oh, I can see that must be upsetting for you.");
			setPhase(29);
			break;
		case 29:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "When you've seen your relatives cut into pieces, with their severed limbs cast aside like junk, THEN", "you will understand how I feel.");
			setPhase(30);
			break;
		case 30:
			send(DialogueType.PLAYER, Expression.DEFAULT, "I'll bear it in mind.");
			setPhase(4);
			break;
		case 31:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Oh dear.");
			setPhase(32);
			break;
		case 32:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "No-one understands me.");
			setPhase(33);
			break;
		case 33:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Why not?");
			setPhase(34);
			break;
		case 34:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Because argle gargle gooble goop.");
			setPhase(35);
			break;
		case 35:
			send(DialogueType.PLAYER, Expression.DEFAULT, "... I don't understand you either.");
			setPhase(36);
			break;
		case 36:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "*sigh*");
			setPhase(4);
			break;
		case 37:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Silly human, it's pronounced Lova-Kane.");
			setPhase(38);
			break;
		case 38:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "How did you not know that?");
			setPhase(39);
			break;
		case 39:
			send(DialogueType.PLAYER, Expression.DEFAULT, "You know, it really isn't that obvious.");
			setPhase(4);
			break;
		case 40:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Why not?");
			setPhase(41);
			break;
		case 41:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "There's no need. Whatever you may have read, even coal is a renewable energy source - just wait a", "minute and the rocks respawn.");
			setPhase(42);
			break;
		case 42:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "You can burn as much as you like, too, without needing to worry about it affecting the climate - we", "don't have a lot of weather here.");
			setPhase(43);
			break;
		case 43:
			send(DialogueType.PLAYER, Expression.DEFAULT, "That's handy.");
			setPhase(44);
			break;
		case 44:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Yes, I pity anyone whose world doesn't work like this one. I don't know how they can possibly cope.");
			setPhase(4);
			break;
		case 45:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Is that something that worries you?");
			setPhase(46);
			break;
		case 46:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "It just feels like my world doesn't make sense, sometimes. But I suppose it's always been like this,", "and I've got used to it.");
			setPhase(4);
			break;
		case 47:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Did you just make an ore joke?");
			setPhase(48);
			break;
		case 48:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Maybe...");
			setPhase(49);
			break;
		case 49:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Well it was awful.");
			setPhase(50);
			break;
		case 50:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "Ha! Now you're making them as well.");
			setPhase(51);
			break;
		case 51:
			send(DialogueType.PLAYER, Expression.DEFAULT, "Are we really doing this? Amethyst isn't an ore anyway.");
			setPhase(51);
			break;
		case 52:
			send(DialogueType.NPC, petId, Expression.DEFAULT, "I can dream, "+player.getUsername()+"!");
			setPhase(4);
			break;
		}
	}

}