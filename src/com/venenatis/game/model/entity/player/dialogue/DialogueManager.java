package com.venenatis.game.model.entity.player.dialogue;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.venenatis.game.constants.Constants;
import com.venenatis.game.model.definitions.NPCDefinitions;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.JsonLoader;

/**
 * Manages the loading and start of dialogues.
 * 
 * @author relex lawl
 */

public class DialogueManager {

	/**
	 * Contains all dialogues loaded from said file.
	 */
	public static Map<Integer, Dialogue> dialogues = new HashMap<Integer, Dialogue>();

	/**
	 * A value representing the interface id
	 * for a dialogue.
	 */
	public static final int CHATBOX_INTERFACE_ID = 50;
	/**
	 * Parses the information from the dialogue file.
	 */
	public static JsonLoader parse() {

		return new JsonLoader() {
			@Override
			public void load(JsonObject reader, Gson builder) {

				final int id = reader.get("id").getAsInt();
				final DialogueType type = DialogueType.valueOf(reader.get("type").getAsString());
				final DialogueExpression anim = reader.has("anim") ? DialogueExpression.valueOf(reader.get("anim").getAsString()) : null;
				final int lines = reader.get("lines").getAsInt();
				String[] dialogueLines = new String[lines];
				for(int i = 0; i < lines; i++) {
					dialogueLines[i] = reader.get("line" + (i+1)).getAsString();
				}
				final int next = reader.get("next").getAsInt();
				final int npcId = reader.has("npcId") ? reader.get("npcId").getAsInt() : -1;

				Dialogue dialogue = new Dialogue() {
					@Override
					public int id() {
						return id;
					}

					@Override
					public DialogueType type() {
						return type;
					}

					@Override
					public DialogueExpression animation() {
						return anim;
					}

					@Override
					public String[] dialogue() {
						return dialogueLines;
					}

					@Override
					public int nextDialogueId() {
						return next;
					}

					@Override
					public int npcId() {
						return npcId;
					}

					@Override
					public String[] item() {
						return null;
					}
				};
				dialogues.put(id, dialogue);

			}

			@Override
			public String filePath() {
				return Constants.DATA_DIR + "/def/misc/dialogues.json";
			}
		};

	}

	/**
	 * Starts a dialogue gotten from the dialogues map.
	 * @param player	The player to dialogue with.
	 * @param id		The id of the dialogue to retrieve from dialogues map.
	 */
	public static void start(Player player, int id) {
		Dialogue dialogue = dialogues.get(id);
		start(player, dialogue);
	}

	/**
	 * Starts a dialogue.
	 * @param player	The player to dialogue with.	
	 * @param dialogue	The dialogue to show the player.
	 */
	public static void start(Player player, Dialogue dialogue) {

		//If player isn't currently in a dialogue and they are busy,
		//simply send interface removal.
		if(player.getDialogue() == null) {
			if(player.isBusy()) {
				player.getActionSender().removeAllInterfaces();
			}
		}

		//Update our dialogue state
		player.setDialogue(dialogue);

		//If dialogue is null, send interface removal.
		//Otherwise, show the dialogue!
		if (dialogue == null || dialogue.id() < 0) {
			player.getActionSender().removeAllInterfaces();
		} else {
			showDialogue(player, dialogue);
			dialogue.specialAction();
		}
	}

	/**
	 * Handles the clicking of 'click here to continue', option1, option2 and so on.
	 * @param player	The player who will continue the dialogue.
	 */
	public static void next(Player player) {

		//Make sure we are currently in a dialogue..
		if (player.getDialogue() == null) {
			player.getActionSender().removeAllInterfaces();
			return;
		}

		//Fetch next dialogue..
		Dialogue next = player.getDialogue().nextDialogue();
		if (next == null)
			next = dialogues.get(player.getDialogue().nextDialogueId());

		//Make sure the next dialogue is valid..
		if (next == null || next.id() < 0) {
			player.getActionSender().removeAllInterfaces();
			if(player.inTutorial()) {
				player.getGameModeSelection().open(player);
			}
			return;
		}

		//Start the next dialogue.
		start(player, next);
	}

	/**
	 * Configures the dialogue's type and shows the dialogue interface
	 * and sets its child id's.
	 * @param player		The player to show dialogue for.
	 * @param dialogue		The dialogue to show.
	 */
	private static void showDialogue(Player player, Dialogue dialogue) {
		String[] lines = dialogue.dialogue();
		switch (dialogue.type()) {
		case NPC_STATEMENT:
			int startDialogueChildId = NPC_DIALOGUE_ID[lines.length - 1];
			int headChildId = startDialogueChildId - 2;
			player.getActionSender().sendNpcHeadToInterface(dialogue.npcId(), headChildId);
			player.getActionSender().sendInterfaceAnimation(headChildId, dialogue.animation().getAnimation());
			player.getActionSender().sendString(NPCDefinitions.forId(dialogue.npcId()) != null ? NPCDefinitions.forId(dialogue.npcId()).getName().replaceAll("_", " ") : "", startDialogueChildId - 1);
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].contains("<player>"))
					lines[i] = lines[i].replaceAll("<player>", player.getUsername());
				player.getActionSender().sendString(lines[i], startDialogueChildId + i);
			}
			player.getActionSender().sendChatBoxInterface(startDialogueChildId - 3);
			break;
		case PLAYER_STATEMENT:
			startDialogueChildId = PLAYER_DIALOGUE_ID[lines.length - 1];
			headChildId = startDialogueChildId - 2;
			player.getActionSender().sendPlayerHeadToInterface(headChildId);
			player.getActionSender().sendInterfaceAnimation(headChildId, dialogue.animation().getAnimation());
			player.getActionSender().sendString(player.getUsername(), startDialogueChildId - 1);
			for (int i = 0; i < lines.length; i++) {
				player.getActionSender().sendString(lines[i], startDialogueChildId + i);
			}
			player.getActionSender().sendChatBoxInterface(startDialogueChildId - 3);
			break;
		case ITEM_STATEMENT:
			startDialogueChildId = NPC_DIALOGUE_ID[lines.length - 1];
			headChildId = startDialogueChildId - 2;
			player.getActionSender().sendItemOnInterface(headChildId, Integer.valueOf(dialogue.item()[0]), Integer.valueOf(dialogue.item()[1]));
			player.getActionSender().sendString(dialogue.item()[2], startDialogueChildId - 1);
			for (int i = 0; i < lines.length; i++) {
				player.getActionSender().sendString(lines[i], startDialogueChildId + i);
			}
			player.getActionSender().sendChatBoxInterface(startDialogueChildId - 3);
			break;
		case STATEMENT:
			sendStatement(player, dialogue.dialogue()[0]);
			break;
		case OPTION:
			int firstChildId = OPTION_DIALOGUE_ID[lines.length - 1];
			player.getActionSender().sendString("Choose an option", firstChildId - 1);
			for (int i = 0; i < lines.length; i++) {
				player.getActionSender().sendString(lines[i], firstChildId + i);
			}
			player.getActionSender().sendChatBoxInterface(firstChildId - 2);
			break;
		}
	}

	public static void sendStatement(Player p, String statement) {
		p.getActionSender().sendString(statement, 357);
		p.getActionSender().sendString("Click here to continue", 358);
		p.getActionSender().sendChatBoxInterface(356);
	}

	/**
	 * Gets an empty id for a dialogue.
	 * @return	An empty index from the map or the map's size itself.
	 */
	public static int getDefaultId() {
		int id = dialogues.size();
		for (int i = 0; i < dialogues.size(); i++) {
			if (dialogues.get(i) == null) {
				id = i;
				break;
			}
		}
		return id;
	}

	/**
	 * Retrieves the dialogues map.
	 * @return	dialogues.
	 */
	public static Map<Integer, Dialogue> getDialogues() {
		return dialogues;
	}

	/**
	 * This array contains the child id where the dialogue
	 * statement starts for npc and item dialogues.
	 */
	private static final int[] NPC_DIALOGUE_ID = {
			4885,
			4890,
			4896,
			4903
	};

	/**
	 * This array contains the child id where the dialogue
	 * statement starts for player dialogues.
	 */
	private static final int[] PLAYER_DIALOGUE_ID = {
			971,
			976,
			982,
			989
	};

	/**
	 * This array contains the child id where the dialogue
	 * statement starts for option dialogues.
	 */
	private static final int[] OPTION_DIALOGUE_ID = {
			13760,
			2461,
			2471,
			2482,
			2494,
	};
}