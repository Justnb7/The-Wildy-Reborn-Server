package com.model.game.character.player.content.questtab;


/**
 * Holds all of the quest tab page types
 * 
 * @author Patrick van Elderen
 *
 */
public enum QuestTabPages {

	HOME_PAGE(0, new HomeQuestTabPage());

	private final QuestTabPage page;
	private final int identifier;

	QuestTabPages(int identifier, QuestTabPage page) {
		this.identifier = identifier;
		this.page = page;
	}

	public int getIdentifier() {
		return identifier;
	}

	public QuestTabPage getPage() {
		return page;
	}

}
