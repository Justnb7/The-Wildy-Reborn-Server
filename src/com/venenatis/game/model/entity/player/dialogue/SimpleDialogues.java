package com.venenatis.game.model.entity.player.dialogue;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

import com.google.common.collect.Iterables;
import com.venenatis.game.model.entity.player.Player;

/**
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class SimpleDialogues {

	public static void sendStatement(Player player, Object... strings) {
		player.getDialogueManager().start(new Dialogue() {
			@Override
			protected void start(Object... parameters) {
				send(DialogueType.STATEMENT, strings);
			}
		});
	}

	public static void sendMobStatement(Player player, int id, String... strings) {
		player.getDialogueManager().start(new Dialogue() {
			@Override
			protected void start(Object... parameters) {
				// XXX: Solution to ClassCastException with multiple params
				Deque<Object> objs = new LinkedList<>(Arrays.asList(strings));
				objs.addFirst(Expression.ANGRY);
				objs.addFirst(id);
				send(DialogueType.NPC, Iterables.toArray(objs, Object.class));
			}
		});
	}

	private SimpleDialogues() {
		throw new UnsupportedOperationException();
	}

}