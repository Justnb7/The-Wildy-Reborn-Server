package com.model.game.character.player.content;

import com.model.game.item.Item;

public class CombineItems {
	
	public enum Combine {
		GODSWORD_SHARD_1_AND_2(),
		GODSWORD_SHARD_1_AND_3(),
		GODSWORD_SHARD_2_AND_3(),
		GODSWORD_BLADE();
		
		private Item useWith;
	}

}
