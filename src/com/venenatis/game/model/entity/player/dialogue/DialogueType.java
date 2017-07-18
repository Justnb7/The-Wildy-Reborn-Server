package com.venenatis.game.model.entity.player.dialogue;

/**
 *
 *
 * @author Erik Eide
 */
public enum DialogueType {

	/*
	 * Gives variable options for a player to choose.
	 */
	OPTION,
	
	/*
	 * Gives a statement.
	 */
	STATEMENT,
	
	/*
	 * Gives a dialogue said by an npc.
	 */
	NPC_STATEMENT,
	
	/*
	 * Gives a dialogue with an item model next to it.
	 */
	ITEM_STATEMENT,
	
	/*
	 * Gives a dialogue said by a player.
	 */
	PLAYER_STATEMENT;

}