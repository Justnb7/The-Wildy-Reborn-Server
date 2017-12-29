package com.venenatis.game.action.impl;

import java.util.Random;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.CycleState;

public abstract class Skill extends Task {

	/**
	 * The random instance used.
	 */
	public static final Random RANDOM = new Random();

	/**
	 * The current cycle state.
	 */
	private CycleState cycleState = CycleState.START;

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * Constructs a new skill action.
	 */
	public Skill(Player player) {
		super(1);
		this.player = player;
	}

	@Override
	public void execute() {
		switch (cycleState) {
		case START:
			if (start(player)) {
				setCycleState(CycleState.EXECUTE);
				return;
			}
			stop();
			return;
		case EXECUTE:
			if (execute(player)) {
				setCycleState(CycleState.FINALIZE);
			}
			return;
		case FINALIZE:
			if (finish(player)) {
				stop();
			}
			return;
		default:
			break;
		}
	}

	/**
	 * Attempts to start the actual skilling action.
	 *
	 * @param player The player.
	 * @return {@code True} if the player can execute this action, {@code false} if not.
	 */
	 public abstract boolean start(Player player);

	 /**
	  * Executes the skilling action.
	  *
	  * @param player The player.
	  * @return {@code True} if we can finish the skilling action, {@code false} if not.
	  */
	 public abstract boolean execute(Player player);

	 /**
	  * Attempts to finish the skilling action.
	  *
	  * @param player The player.
	  * @return {@code True} if the action has finished, {@code false} if we should re-execute.
	  */
	 public abstract boolean finish(Player player);

	 /**
	  * @param cycleState the cycleState to set
	  */
	 public void setCycleState(CycleState cycleState) {
		 this.cycleState = cycleState;
	 }

	 /**
	  * @return the cycleState
	  */
	 public CycleState getCycleState() {
		 return cycleState;
	 }

	 /**
	  * @return the player
	  */
	 public Player getPlayer() {
		 return player;
	 }

}