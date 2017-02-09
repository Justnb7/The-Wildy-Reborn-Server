package com.model.game.character.player.dialogue;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendChatBoxInterface;
import com.model.game.character.player.packets.encode.impl.SendFrame185;
import com.model.game.character.player.packets.encode.impl.SendFrame200;
import com.model.game.character.player.packets.encode.impl.SendFrame75;
import com.model.game.character.player.packets.encode.impl.SendString;


public class DialogueHandler {

	private Player player;
	
	public DialogueHandler(Player player) {
		this.player = player;
	}

	/**
	 * Handles all talking
	 * 
	 * @param dialogue
	 *            The dialogue you want to use
	 * @param npcId
	 *            The npc id that the chat will focus on during the chat
	 */
	public void sendDialogues(int dialogue, int npcId) {
		player.talkingNpc = npcId;
		switch (dialogue) {
			
		case 0:
			player.talkingNpc = -1;
			player.write(new SendClearScreen());
			player.nextChat = 0;
			break;
			
			// Dagannoth Rex
					case 3500:
						sendPlayerChat1("Do you have any berserker rings?");
						player.nextChat = 3501;
						break;
						
					case 3501:
						sendNpcChat("Nope.", 6630, "Dagganoth Rex Jr");
						player.nextChat = 3502;
						break;
						
					case 3502:
						sendPlayerChat1("You sure?");
						player.nextChat = 3503;
						break;
						
					case 3503:
						sendNpcChat("Yes.", 6630, "Dagganoth Rex Jr");
						player.nextChat = 3504;
						break;
						
					case 3504:
						sendPlayerChat2("So, if I tipped you upside down and shook you,", "you'd not drop any berserker rings?");
						player.nextChat = 3505;
						break;
						
					case 3505:
						sendNpcChat("Nope.", 6630, "Dagganoth Rex Jr");
						player.nextChat = 3506;
						break;
						
					case 3506:
						sendPlayerChat2("What if I endlessly killed your father for weeks on end,", "would I get one then.");
						player.nextChat = 3507;
						break;
						
					case 3507:
						sendNpcChat("Been done by someone, nope.", 6630, "Dagganoth Rex Jr");
						player.nextChat = 0;
						break;
						
					// Dagannoth Prime
					case 3508:
						sendPlayerChat2("So despite there being three kings,", "you're clearly the leader, right?");
						player.nextChat = 3509;
						break;
						
					case 3509:
						sendNpcChat("Definitely.", 6629, "Dagganoth Prime Jr");
						player.nextChat = 3510;
						break;
						
					case 3510:
						sendPlayerChat1("I'm glad I got you as a pet.");
						player.nextChat = 3511;
						break;
						
					case 3511:
						sendNpcChat("Ugh. Human, I'm not a pet.", 6629, "Dagganoth Prime Jr");
						player.nextChat = 3512;
						break;
						
					case 3512:
						sendPlayerChat1("Stop following me then.");
						player.nextChat = 3513;
						break;
						
					case 3513:
						sendNpcChat("I can't seem to stop.", 6629, "Dagganoth Prime Jr");
						player.nextChat = 3514;
						break;
						
					case 3514:
						sendPlayerChat1("Pet.");
						player.nextChat = 0;
						break;	
						
					// Dagannoth Supreme
					case 3515:
						sendPlayerChat1("Hey, so err... I kind of own you now.");
						player.nextChat = 3516;
						break;
						
					case 3516:
						sendNpcChat2("Tsssk. Next time you enter those caves,", "human, my father will be having words.", 6628, "Dagganoth Supreme Jr");
						player.nextChat = 3517;
						break;
						
					case 3517:
						sendPlayerChat1("Maybe next time I'll add your brothers to my collection.");
						player.nextChat = 0;
						break;
						
					// Giant Mole
					case 3518:
						sendPlayerChat1("Hey, Mole. How is life above ground?");
						player.nextChat = 3519;
						break;
						
					case 3519:
						sendNpcChat3("Well, the last time I was above ground,", "I was having to contend with people throwing snow at ", "some weird yellow duck in my park.", 6635, "Baby mole");
						player.nextChat = 3520;
						break;
						
					case 3520:
						sendPlayerChat1("Why were they doing that?");
						player.nextChat = 3521;
						break;
						
					case 3521:
						sendNpcChat3("No idea,", "I didn't stop to ask as an angry mob", "was closing in on them pretty quickly.", 6635, "Baby mole");
						player.nextChat = 3522;
						break;
						
					case 3522:
						sendPlayerChat1("Sounds awful.");
						player.nextChat = 0;
						break;
						
					case 3523:
						sendNpcChat("Anyway, keep Molin'!", 6635, "Baby mole");
						player.nextChat = 0;
						break;
						
					// Prince Black Dragon
					case 3524:
						sendPlayerChat1("Shouldn't a prince only have two heads?");
						player.nextChat = 3525;
						break;
						
					case 3525:
						sendNpcChat("Why is that?", 6636, "Prince black dragon");
						player.nextChat = 3526;
						break;
						
					case 3526:
						sendPlayerChat3("Well,", "a standard Black dragon has one,", "the King has three so inbetween must have two?");
						player.nextChat = 3527;
						break;
						
					case 3527:
						sendNpcChat("You're overthinking this.", 6636, "Prince black dragon");
						player.nextChat = 0;
						break;
					// Pet dark core
					case 3528:
						sendPlayerChat1("Got any sigils for me?");
						player.nextChat = 3529;
						break;
					case 3529:
						sendPlayerChat1("Damnit Core-al!");
						player.nextChat = 3530;
						break;
					case 3530:
						sendPlayerChat1("Let's bounce!");
						player.nextChat = 0;
						break;
						
					// Kalphite princess
					case 3531:
						sendPlayerChat1("What is it with your kind and potato cactus?");
						player.nextChat = 3532;
						break;
						
					case 3532:
						sendNpcChat("Truthfully?", 6638, "Kalphite Princess");
						player.nextChat = 3533;
						break;
						
					case 3533:
						sendPlayerChat1("Yeah, please.");
						player.nextChat = 3534;
						break;
						
					case 3534:
						sendNpcChat("Soup. We make a fine soup with it.", 6638, "Kalphite Princess");
						player.nextChat = 3535;
						break;
						
					case 3535:
						sendPlayerChat1("Kalphites can cook?");
						player.nextChat = 3536;
						break;
						
					case 3536:
						sendNpcChat3("Nah, we just collect it and put it there because", "we know fools like yourself will come", "down looking for it then inevitably be killed by my mother.", 6638, "Kalphite Princess");
						player.nextChat = 3537;
						break;
						
					case 3537:
						sendPlayerChat1("Evidently not, that's how I got you!");
						player.nextChat = 3538;
						break;
						
					case 3538:
						sendNpcChat("Touché", 6638, "Kalphite Princess");
						player.nextChat = 0;
						break;
						
					// Snakeling green
					case 3539:
						sendPlayerChat1("Hey little snake!");
						player.nextChat = 3540;
						break;
						
					case 3540:
						sendNpcChat("Soon, Zulrah shall establish dominion over this plane.", 2130, "Snakeling");
						player.nextChat = 3541;
						break;
						
					case 3541:
						sendPlayerChat1("Wanna play fetch?");
						player.nextChat = 3542;
						break;
						
					case 3542:
						sendNpcChat("Submit to the almighty Zulrah.", 2130, "Snakeling");
						player.nextChat = 3543;
						break;
						
					case 3543:
						sendPlayerChat1("Walkies? Or slidies...?");
						player.nextChat = 3544;
						break;
						
					case 3544:
						sendNpcChat("Zulrah's wilderness as a God will soon be demonstrated.", 2130, "Snakeling");
						player.nextChat = 3545;
						break;
						
					case 3545:
						sendPlayerChat1("I give up...");
						player.nextChat = 0;
						break;
						
					// Snakeling red
					case 3546:
						sendPlayerChat1("Hey little snake!");
						player.nextChat = 3547;
						break;
						
					case 3547:
						sendNpcChat("Soon, Zulrah shall establish dominion over this plane.", 2131, "Snakeling");
						player.nextChat = 3548;
						break;
						
					case 3549:
						sendPlayerChat1("Wanna play fetch?");
						player.nextChat = 3550;
						break;
						
					case 3550:
						sendNpcChat("Submit to the almighty Zulrah.", 2131, "Snakeling");
						player.nextChat = 3551;
						break;
						
					case 3551:
						sendPlayerChat1("Walkies? Or slidies...?");
						player.nextChat = 3552;
						break;
						
					case 3552:
						sendNpcChat("Zulrah's wilderness as a God will soon be demonstrated.", 2131, "Snakeling");
						player.nextChat = 3553;
						break;
						
					case 3553:
						sendPlayerChat1("I give up...");
						player.nextChat = 0;
						break;
						
					// Snakeling blue
					case 3554:
						sendPlayerChat1("Hey little snake!");
						player.nextChat = 3555;
						break;
						
					case 3556:
						sendNpcChat("Soon, Zulrah shall establish dominion over this plane.", 2132, "Snakeling");
						player.nextChat = 3557;
						break;
						
					case 3558:
						sendPlayerChat1("Wanna play fetch?");
						player.nextChat = 3558;
						break;
						
					case 3559:
						sendNpcChat("Submit to the almighty Zulrah.", 2132, "Snakeling");
						player.nextChat = 3560;
						break;
						
					case 3560:
						sendPlayerChat1("Walkies? Or slidies...?");
						player.nextChat = 3561;
						break;
						
					case 3561:
						sendNpcChat("Zulrah's wilderness as a God will soon be demonstrated.", 2132, "Snakeling");
						player.nextChat = 3562;
						break;
						
					case 3562:
						sendPlayerChat1("I give up...");
						player.nextChat = 0;
						break;
						
					// TzRek-Jad
					case 3563:
						sendPlayerChat1("Do you miss your people?");
						player.nextChat = 3564;
						break;
						
					case 3564:
						sendNpcChat("Mej-TzTok-Jad Kot-Kl! (TzTok-Jad will protect us!)", 5892, "TzRek-Jad");
						player.nextChat = 3565;
						break;
						
					case 3565:
						sendPlayerChat1("I don't think so.");
						player.nextChat = 3566;
						break;
						
					case 3566:
						sendNpcChat("Jal-Zek Kl? (Foreigner hurt us?)", 5892, "TzRek-Jad");
						player.nextChat = 3567;
						break;
						
					case 3567:
						sendPlayerChat1("No, no, I wouldn't hurt you.");
						player.nextChat = 0;
						break;
						
					case 3568:
						sendPlayerChat1("Are you hungry?");
						player.nextChat = 3569;
						break;
						
					case 3569:
						sendNpcChat("Kl-Kra!", 5892, "TzRek-Jad");
						player.nextChat = 3570;
						break;
						
					case 3570:
						sendPlayerChat1("Ooookay...");
						player.nextChat = 0;
						break;	
						
					// Chaos Elemental Jr
					case 3571:
						sendPlayerChat1("Is it true a level 3 skiller caught one of your siblings?");
						player.nextChat = 3572;
						break;	
			
					case 3572:
						sendNpcChat3("Yes, they killed my mummy,", "kidnapped my brother,", "smiled about it and went to sleep.", 5907, "Chaos Elemental Jr.");
						player.nextChat = 3573;
						break;
						
					case 3573:
						sendPlayerChat3("Aww, well you have me now!", "I shall call you Squishy and you shall be mine", "and you shall be my Squishy");
						player.nextChat = 3574;
						break;
						
					case 3574:
						sendPlayerChat1("Come on, Squishy come on, little Squishy!");
						player.nextChat = 0;
						break;
					// Callisto cub
					case 3575:
						sendPlayerChat1("Why the grizzly face?");
						player.nextChat = 3576;
						break;	
			
					case 3576:
						sendNpcChat("You're not funny...", 497, "Callisto cub");
						player.nextChat = 3577;
						break;
						
					case 3577:
						sendPlayerChat1("You should get in the.... sun more.");
						player.nextChat = 3578;
						break;	
			
					case 3578:
						sendNpcChat("You're really not funny...", 497, "Callisto cub");
						player.nextChat = 3579;
						break;	
						
					case 3579:
						sendPlayerChat2("One second,", "let me take a picture of you with my.... kodiak camera.");
						player.nextChat = 3580;
						break;	
			
					case 3580:
						sendNpcChat(".....", 497, "Callisto cub");
						player.nextChat = 3581;
						break;	
						
					case 3581:
						sendPlayerChat1("Feeling.... blue.");
						player.nextChat = 3582;
						break;	
			
					case 3582:
						sendNpcChat2("If you don't stop,", "I'm going to leave some... brown... at your feet, human.", 497, "Callisto cub");
						player.nextChat = 0;
						break;
					// Scorpia's offspring
					case 3583:
						sendPlayerChat3("At night time,", "if I were to hold ultraviolet light over you,", "would you glow?");
						player.nextChat = 3584;
						break;	
			
					case 3584:
						sendNpcChat1("Two things wrong there, human.", 5547, "Scorpia's offspring");
						player.nextChat = 3585;
						break;
						
					case 3585:
						sendPlayerChat1("Oh?");
						player.nextChat = 3586;
						break;	
			
					case 3586:
						sendNpcChat2("One,", "When has it ever been night time here?", 5547, "Scorpia's offspring");
						player.nextChat = 3587;
						break;
						
					case 3587:
						sendNpcChat2("Two,", "When have you ever seen ultraviolet light around here?", 5547, "Scorpia's offspring");
						player.nextChat = 3588;
						break;
						
					case 3588:
						sendPlayerChat1("Hm...");
						player.nextChat = 3589;
						break;	
			
					case 3589:
						sendNpcChat2("In answer to your question though.", "Yes I, like every scorpion, would glow.", 5547, "Scorpia's offspring");
						player.nextChat = 0;
						break;
					// Venenatis spiderling
					case 3590:
						sendPlayerChat1("It's a damn good job I don't have arachnophobia.");
						player.nextChat = 3591;
						break;	
			
					case 3591:
						sendNpcChat3("We're misunderstood.", "Without us in your house, you'd be infested with flies and ", "other REAL nasties.", 495, "Venenatis spiderling");
						player.nextChat = 3592;
						break;
						
					case 3592:
						sendPlayerChat1("Thanks for that enlightening fact.");
						player.nextChat = 3593;
						break;	
			
					case 3593:
						sendNpcChat1("Everybody gets one.", 495, "Venenatis spiderling");
						player.nextChat = 0;
						break;
					// Vet'ion Jr. purple
					case 3594:
						sendPlayerChat1("Who is the true lord and king of the lands?");
						player.nextChat = 3595;
						break;	
			
					case 3595:
						sendNpcChat1("The mighty heir and lord of the Wilderness.", 5536, "Vet'ion Jr.");
						player.nextChat = 3596;
						break;
						
					case 3596:
						sendPlayerChat1("Where is he? Why hasn't he lifted your burden?");
						player.nextChat = 3597;
						break;	
			
					case 3597:
						sendNpcChat1("I have not fulfilled my purpose.", 5536, "Vet'ion Jr.");
						player.nextChat = 3598;
						break;
						
					case 3598:
						sendPlayerChat1("What is your purpose?");
						player.nextChat = 3599;
						break;	
			
					case 3599:
						sendNpcChat4("Not what is,", "what was. A great war tore this land apart and,", "for my failings in protecting this ", "land, I carry the burden of its waste.", 5536, "Vet'ion Jr.");
						player.nextChat = 0;
						break;
						
					// Vet'ion Jr. orange
					case 3600:
						sendPlayerChat1("Who is the true lord and king of the lands?");
						player.nextChat = 3601;
						break;	
			
					case 3601:
						sendNpcChat1("The mighty heir and lord of the Wilderness.", 5537, "Vet'ion Jr.");
						player.nextChat = 3602;
						break;
						
					case 3602:
						sendPlayerChat1("Where is he? Why hasn't he lifted your burden?");
						player.nextChat = 3603;
						break;	
			
					case 3603:
						sendNpcChat1("I have not fulfilled my purpose.", 5537, "Vet'ion Jr.");
						player.nextChat = 3604;
						break;
						
					case 3604:
						sendPlayerChat1("What is your purpose?");
						player.nextChat = 3605;
						break;	
			
					case 3605:
						sendNpcChat4("Not what is,", "what was. A great war tore this land apart and,", "for my failings in protecting this ", "land, I carry the burden of its waste.", 5537, "Vet'ion Jr.");
						player.nextChat = 0;
						break;
						
					//General Graardor Jr.
					case 3606:
						sendPlayerChat2("Not sure this is going to be worth my time but... how are", "you?");
						player.nextChat = 3607;
						break;	
			
					case 3607:
						sendNpcChat2("SFudghoigdfpDSOPGnbSOBNfdbdnopbdn", "opbddfnopdfpofhdARRRGGGGH", 6632, "General Graardor Jr.");
						player.nextChat = 3608;
						break;
						
					case 3608:
						sendPlayerChat1("Nope. Not worth it.");
						player.nextChat = 0;
						break;
						
					//K'ril Tsutsaroth Jr.
					case 3609:
						sendPlayerChat1("How's life in the light?");
						player.nextChat = 3610;
						break;	
			
					case 3610:
						sendNpcChat("Burns slightly.", 6634, "K'ril Tsutsaroth Jr.");
						player.nextChat = 3611;
						break;
						
					case 3611:
						sendPlayerChat1("You seem much nicer than your father. He's mean.");
						player.nextChat = 3612;
						break;
						
					case 3612:
						sendNpcChat2("If you were stuck in a very dark cave for centuries", "you'd be pretty annoyed too.", 6634, "K'ril Tsutsaroth Jr.");
						player.nextChat = 3613;
						break;
						
					case 3613:
						sendPlayerChat1("I guess.");
						player.nextChat = 3614;
						break;
						
					case 3614:
						sendNpcChat("He's actually quite mellow really.", 6634, "K'ril Tsutsaroth Jr.");
						player.nextChat = 3615;
						break;
						
					case 3615:
						sendPlayerChat1("Uh.... Yeah.");
						player.nextChat = 0;
						break;
						
					//Kree'arra Jr.
					case 3616:
						sendPlayerChat1("Huh... that's odd... I thought that would be big news.");
						player.nextChat = 3617;
						break;	
			
					case 3617:
						sendNpcChat("You thought what would be big news?", 6631, "Kree'arra Jr.");
						player.nextChat = 3618;
						break;
						
					case 3618:
						sendPlayerChat3("Well there seems to be an absence of a certain", "ornithological piece: a headline regarding mass", "awareness of a certain avian variety.");
						player.nextChat = 3619;
						break;	
			
					case 3619:
						sendNpcChat("What are you talking about?", 6631, "Kree'arra Jr.");
						player.nextChat = 3620;
						break;
						
					case 3620:
						sendPlayerChat2("Oh have you not heard?", "It was my understanding that everyone had heard....");
						player.nextChat = 3621;
						break;	
			
					case 3621:
						sendNpcChat("Heard wha...... OH NO!!!!?!?!!?!", 6631, "Kree'arra Jr.");
						player.nextChat = 3622;
						break;
						
					case 3622:
						sendPlayerChat4("OH WELL THE BIRD,", "BIRD, BIRD, BIRD BIRD IS THE WORD.", "OH WELL THE BIRD, BIRD, BIRD,", "BIRD BIRD IS THE WORD.");
						player.nextChat = 0;
						break;
						
					//Zilyana Jr.
					case 3623:
						sendPlayerChat1("FIND THE GODSWORD!");
						player.nextChat = 3624;
						break;	
			
					case 3624:
						sendNpcChat("FIND THE GODSWORD!", 6633, "Zilyana Jr");
						if (player.getItems().playerHasItem(11806)) {
							player.nextChat = 3625;
						} else {
							player.nextChat = 0;
						}
						break;
						
					case 3625:
						sendPlayerChat1("I FOUND THE GODSWORD!");
						player.nextChat = 3626;
						break;	
			
					case 3626:
						sendNpcChat("GOOD!!!!!", 6633, "Zilyana Jr");
						player.nextChat = 0;
						break;
						
					//Kraken
					case 3627:
						sendPlayerChat1("What's Kraken?");
						player.nextChat = 3628;
						break;	
			
					case 3628:
						sendNpcChat("Not heard that one before.", 6640, "Kraken");
						player.nextChat = 3629;
						break;
						
					case 3629:
						sendPlayerChat1("How are you actually walking on land?");
						player.nextChat = 3630;
						break;	
			
					case 3630:
						sendNpcChat3("We have another leg,", "just below the center of our body that", "we use to move across solid surfaces.", 6640, "Kraken");
						player.nextChat = 3631;
						break;
						
					case 3631:
						sendPlayerChat1("That's.... interesting.");
						player.nextChat = 0;
						break;
						
		default:
			break;
		}
	}

	/*
	 * Statements
	 */

	public void sendStatement(Player player, String s) {
		player.write(new SendString(s, 357));
		player.write(new SendString("Click here to continue", 358));
		player.write(new SendChatBoxInterface(356));;
	}

	/*
	 * Npc Chatting
	 */

	public void sendNpcChat(String s, int ChatNpc, String name) {
		player.write(new SendFrame200(4883, 591));
		player.write(new SendString(name, 4884));
		player.write(new SendString(s, 4885));
		player.write(new SendFrame75(ChatNpc, 4883));;
		player.write(new SendChatBoxInterface(4882));
	}

	public void sendNpcChat1(String s, int ChatNpc, String name) {
		player.write(new SendFrame200(4883, 591));
		player.write(new SendString(name, 4884));
		player.write(new SendString(s, 4885));
		player.write(new SendFrame75(ChatNpc, 4883));;
		player.write(new SendChatBoxInterface(4882));
	}

	public void sendNpcChat2(String s, String s1, int ChatNpc, String name) {
		player.write(new SendFrame200(4888, 591));
		player.write(new SendString(name, 4889));
		player.write(new SendString(s, 4890));
		player.write(new SendString(s1, 4891));
		player.write(new SendFrame75(ChatNpc, 4888));;
		player.write(new SendChatBoxInterface(4887));
	}

	public void sendNpcChat3(String s, String s1, String s2, int ChatNpc, String name) {
		player.write(new SendFrame200(4894, 591));
		player.write(new SendString(name, 4895));
		player.write(new SendString(s, 4896));
		player.write(new SendString(s1, 4897));
		player.write(new SendString(s2, 4898));
		player.write(new SendFrame75(ChatNpc, 4894));;
		player.write(new SendChatBoxInterface(4893));
	}

	public void sendNpcChat4(String s, String s1, String s2, String s3, int ChatNpc, String name) {
		player.write(new SendFrame200(4901, 591));
		player.write(new SendString(name, 4902));
		player.write(new SendString(s, 4903));
		player.write(new SendString(s1, 4904));
		player.write(new SendString(s2, 4905));
		player.write(new SendString(s3, 4906));
		player.write(new SendFrame75(ChatNpc, 4901));
		player.write(new SendChatBoxInterface(4900));
	}

	/*
	 * Player Chating Back
	 */
	public void sendPlayerChat1(String s) {
		player.write(new SendFrame200(969, 591));
		player.write(new SendString(player.getName(), 970));
		player.write(new SendString(s, 971));
		player.write(new SendFrame185(969));
		player.write(new SendChatBoxInterface(968));
	}

	public void sendPlayerChat2(String s, String s1) {
		player.write(new SendFrame200(974, 591));
		player.write(new SendString(player.getName(), 975));
		player.write(new SendString(s, 976));
		player.write(new SendString(s1, 977));
		player.write(new SendFrame185(974));
		player.write(new SendChatBoxInterface(973));
	}

	public void sendPlayerChat3(String s, String s1, String s2) {
		player.write(new SendFrame200(980, 591));
		player.write(new SendString(player.getName(), 981));
		player.write(new SendString(s, 982));
		player.write(new SendString(s1, 983));
		player.write(new SendString(s2, 984));
		player.write(new SendFrame185(980));
		player.write(new SendChatBoxInterface(979));
	}

	public void sendPlayerChat4(String s, String s1, String s2, String s3) {
		player.write(new SendFrame200(987, 591));
		player.write(new SendString(player.getName(), 988));
		player.write(new SendString(s, 989));
		player.write(new SendString(s1, 990));
		player.write(new SendString(s2, 991));
		player.write(new SendString(s3, 992));
		player.write(new SendFrame185(987));
		player.write(new SendChatBoxInterface(986));
	}
	
}
