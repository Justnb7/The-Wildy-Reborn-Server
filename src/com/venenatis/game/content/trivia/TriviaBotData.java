package com.venenatis.game.content.trivia;

public enum TriviaBotData {

	QUESTION_1("What is the maximum combat level?", "126"),
	QUESTION_2("What is the maximum amount of friends allowed?", "200", "two hundred", "two-hundred"),
	QUESTION_3("What item can be rewarded from the Fight caves minigame?", "fire cape"),
	QUESTION_4("What is the maximum level of any skill?", "99", "ninety nine", "ninety-nine"),
	QUESTION_5("How many hitpoints do Sharks heal?", "20"),
	QUESTION_6("What magic level is needed to cast Ice Barrage", "94"),
	QUESTION_7("What is the required attack level needed to wield the Abyssal whip?", "70", "seventy"),
	QUESTION_8("What is the required strength level needed to wield the Tzhaar-ket-om?", "60", "sixty"),
	QUESTION_9("What is the required range level needed to wield Red chinchompas?", "55", "fifty-five"),
	QUESTION_10("What is the required slayer level needed to kill skeletal Wyverns?", "72", "seventy-two"),
	QUESTION_11("How many waves are there in the Fight Caves minigame?", "15"),
	QUESTION_12("How many charges can an amulet of glory hold?", "6", "six"),
	QUESTION_13("What is the required range level needed to wield Red chinchompas?", "55", "fifty-five"),
	QUESTION_14("What herb is required to make a prayer potion?", "ranarr"),
	QUESTION_15("How many hitpoints does Corporeal Best have?", "2000", "two-thousand"),
	QUESTION_16("What boss drops the elysian sigil?", "corporeal beast"),
	QUESTION_17("What level is Zulrah?", "725"),
	QUESTION_18("Name 1 rune required to cast the tele-block spell.", "chaos", "law", "death"),
	QUESTION_19("How many nature runes are needed to cast the Entangle spell?", "4"),
	QUESTION_20("The amulet of fury is created using which gem?", "onyx"),
	QUESTION_21("What range level is required to wield the toxic blowpipe?", "75", "seventy-five"),
	QUESTION_22("What attack level is required to wield the Armadyl godsword?", "75", "seventy-five"),
	QUESTION_23("How many godswords are there?", "4", "four"),
	QUESTION_24("How many marks of grace are needed to purchace the full graceful set?", "260"),
	QUESTION_25("What is the prayer requirement needed to wield the elysian spirit shield?", "75", "seventy-five"),
	QUESTION_26("In what year did Jagex release dungeoneering?", "2010"),
	QUESTION_27("Who is the creator of RuneScape?", "Andrew Gower"),
	QUESTION_28("What was the first minigame in RuneScape?", "Clan wars"),
	QUESTION_29("What gaming genre is Runescape?", "MMORPG"),
	QUESTION_30("In what year was RuneScape established?", "2001"),
	QUESTION_31("In what year was RuneScape HD released?", "2008"),
	QUESTION_32("Is a tomato a fruit or a vegetable?", "Fruit"),
	QUESTION_33("How many legs does a spider have?", "8"),
	QUESTION_34("Did RuneScape ever obtain the item Life rune? (Yes/No)", "Yes"),
	QUESTION_35("What was the Falador Massacre?", "A glitch"),
	QUESTION_36("In what year was the Duplication Glitch in RuneScape?", "2003"),
	QUESTION_37("What is the attack requirement for Goliath Gloves?", "80"),
	QUESTION_40("Where is the Wise Old Man located at?", "Draynor Village"),
	QUESTION_41("What is the most powerful prayer in Runescape 2007?", "Piety"),
	QUESTION_42("How much of a percentage does a dragon dagger special require?", "25%"),
	QUESTION_43("What's the name of the dungeon master?", "Thok"),
	QUESTION_44("What is the best free to play armour?", "Rune"),
	QUESTION_45("Where can you earn Zeals?", "Soul wars"),
	QUESTION_46("Fill out the good part of the name in the omitted part, `...... the mad�?", "Melzar"),
	QUESTION_47("What do you receive when a fire disappears?", "Ashes"),
	QUESTION_48("What amulet does the quest Imp Catcher reward you?", "Amulet of Accuracy"),
	QUESTION_49("What is the name of the new firecape?", "TokHaar-Kal"),
	QUESTION_50("How many scorpions are residing in the scorpia cave?", "28"),
	QUESTION_51("How many kills do you need to enter a boss room in the GWD?", "40"),
	QUESTION_52("Which item can you use to enter the GWD boss room without killcount?", "Ecumenical key"),
	QUESTION_53("What's 9 + 10?", "21"),
	QUESTION_54("What is the entrance fee for Brimhaven's Agility Arena?", "200 coins"),
	QUESTION_55("Which color is fire?", "Red"),
	QUESTION_56("How much ammo can the crystal bow use?", "Infinite"),
	QUESTION_57("What is the attack requirement for Chaotics?", "80"),
	QUESTION_58("What are the attack and Strength requirements for Korasi?", "78"),
	QUESTION_59("What level must you be to smith a Steel Scimitar?", "35"),
	QUESTION_60("This skill involves capturing different creatures using traps. What skill is it?", "Hunter"),
	QUESTION_61("I make potions. What skill am I?", "Herblore"),
	QUESTION_62("What is the maximum level for dungeoneering?", "120"),
	QUESTION_63("What tool do you need to catch Swordfish?", "Harpoon"),
	QUESTION_64("What tool do you need to mine?", "Pickaxe"),
	QUESTION_65("What is the value of the character's initial combat level minus 1?", "2"),
	QUESTION_66("In which skill you can go around planting and harvesting your own plants?", "Farming"),
	QUESTION_67("What magic level is required to perform the spell Wind Wave?", "62"),
	QUESTION_68("Is the skill herblore trainable in F2P (Yes/No)", "No");
	
	private final String question;
	private final String[] answers;
	
	private TriviaBotData(String question, String... answers) {
		this.question = question;
		this.answers = answers;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String[] getAnswers() {
		return answers;
	}
	
}