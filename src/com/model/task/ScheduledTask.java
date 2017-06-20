package com.model.task;

import java.util.Iterator;
import java.util.Objects;

import org.eclipse.jetty.util.thread.Scheduler.Task;

import com.model.game.character.player.Player;

/**
 * Represents a periodic task that can be scheduled with a
 * 
 * @author Graham
 * @author lare96 <http://github.com/lare96>
 */
public abstract class ScheduledTask {

	/**
	 * The default attachment for every task.
	 */
	public static final Object DEFAULT_ATTACHMENT = new Object();

	/**
	 * The object attached to this task.
	 */
	private Object attachment;

	/**
	 * A flag which indicates if this task should be executed once immediately.
	 */
	private final boolean immediate;

	/**
	 * The task will stack by default
	 */
	private Stackable stackable = Stackable.STACKABLE;

	/**
	 * The task is walkable by default
	 */
	private Walkable walkable = Walkable.WALKABLE;

	/**
	 * The current 'count down' value. When this reaches zero the task will be
	 * executed.
	 */
	private int remainingTicks;
	
	/**
	 * The ticks to reset to once executed if the tickable still runs.
	 */
	private int tickDelay;

	/**
	 * A flag which indicates if this task is still running.
	 */
	private boolean running = true;

	/**
	 * Creates a new task with a delay of 1 cycle.
	 */
	public ScheduledTask() {
		this(1);
	}

	/**
	 * Creates a new task with a delay of 1 cycle and immediate flag.
	 * 
	 * @param immediate
	 *            A flag that indicates if for the first execution there should
	 *            be no delay.
	 */
	public ScheduledTask(boolean immediate) {
		this(1, immediate);
	}

	/**
	 * Creates a new task with the specified delay.
	 * 
	 * @param delay
	 *            The number of cycles between consecutive executions of this
	 *            task.
	 * @throws IllegalArgumentException
	 *             if the {@code delay} is not positive.
	 */
	public ScheduledTask(int delay) {
		this(delay, false);
	}
	
	public ScheduledTask(Player player, int delay) {
		this(delay);
	}

	public ScheduledTask(Player player, int delay, boolean immediate, Walkable walkable, Stackable stackable) {
		checkDelay(delay);
		this.tickDelay = delay;
		this.remainingTicks = delay;
		this.immediate = immediate;
		this.stackable = stackable;
		this.walkable = walkable;
		this.attach(player);
		stopNonStackableTasks(player);
	}

	/**
	 * Schedules a new {@link Task} with a walkable and stackable check
	 * 
	 * @param player
	 * @param delay
	 * @param walkable
	 * @param stackable
	 */
	public ScheduledTask(Player player, int delay, Walkable walkable, Stackable stackable) {
		this(player, delay, false, walkable, stackable);
	}

	/**
	 * Creates a new task with the specified delay and immediate flag.
	 * 
	 * @param delay
	 *            The number of cycles between consecutive executions of this
	 *            task.
	 * @param immediate
	 *            A flag which indicates if for the first execution there should
	 *            be no delay.
	 * @throws IllegalArgumentException
	 *             if the {@code delay} is not positive.
	 */
	public ScheduledTask(int delay, boolean immediate) {
		checkDelay(delay);
		this.tickDelay = delay;
		this.remainingTicks = delay;
		this.immediate = immediate;
		this.attach(DEFAULT_ATTACHMENT);
	}

	/**
	 * Checks if this task is an immediate task.
	 * 
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean isImmediate() {
		return immediate;
	}

	/**
	 * Checks if the task is running.
	 * 
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Checks if the task is stopped.
	 * 
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean isStopped() {
		return !running;
	}

	/**
	 * Attaches an object to this task. An attachment of <code>null</code> is
	 * not permitted.
	 * 
	 * @return this task for chaining.
	 */
	public ScheduledTask attach(Object attachment) {
		this.attachment = Objects.requireNonNull(attachment, "Attachments of 'null' are not permitted!");
		return this;
	}

	/**
	 * This method should be called by the scheduling class every cycle. It
	 * updates the {@link #remainingTicks} and calls the {@link #execute()} method if
	 * necessary.
	 */
	public void tick() {

		if (getAttachment() != null && getAttachment().getClass().equals(Player.class)) {
			Player player = (Player) getAttachment();
			/*
			 * If the player is moving and its a non walkable task, stop it.
			 */
			if (player.getWalkingQueue().isMoving() && walkable == Walkable.NON_WALKABLE) {
				if (running) {
					stop();
					return;
				}
			}
		}
		if(remainingTicks-- <= 1) {
			remainingTicks = tickDelay;
			if(isRunning()) {
				execute();
			}
		}
	}

	/**
	 * Stops all non stackable tasks
	 * 
	 * @param player
	 */
	public void stopNonStackableTasks(Player player) {
		if (stackable == Stackable.NON_STACKABLE) {
			for (Iterator<ScheduledTask> it$ = player.getTasks().iterator(); it$.hasNext();) {
				ScheduledTask task = it$.next();
				if (task.stackable == Stackable.NON_STACKABLE) {
					if (task.isRunning()) {
						task.stop();
					}
				}
			}
		}
		player.getTasks().add(this);
	}

	/**
	 * Performs this task's action.
	 */
	public abstract void execute();

	/**
	 * Tasks can override this method to fire logic when the task is stopped.
	 */
	public void onStop() {
	}

	/**
	 * Sets the tick delay.
	 * @param ticks The amount of ticks to set.
	 * @throws IllegalArgumentException if the delay is negative.
	 */
	public void setTickDelay(int ticks) {
		if(ticks < 0) {
			throw new IllegalArgumentException("Tick amount must be positive.");
		}
		this.tickDelay = ticks;
	}

	/**
	 * Stops this task.
	 * 
	 * @throws IllegalStateException
	 *             if the task has already been stopped.
	 */
	public void stop() {
		if (running) {
			running = false;
			onStop();
			/*
			 * remove the task for the player upon stopping TODO: Verify this
			 * won't cause issues
			 */
			if (getAttachment() != null && getAttachment().getClass().equals(Player.class)) {
				Player player = (Player) getAttachment();
				player.getTasks().remove(this);
			}
		}
	}

	/**
	 * Checks if the delay is negative and throws an exception if so.
	 * 
	 * @param delay
	 *            The delay.
	 * @throws IllegalArgumentException
	 *             if the delay is not positive.
	 */
	private void checkDelay(int delay) {
		if (delay <= 0)
			throw new IllegalArgumentException("Delay must be positive.");
	}
	
	/**
	 * Gets the tick delay.
	 * @return The delay, in ticks.
	 */
	public int getTickDelay() {
		return tickDelay;
	}

	/**
	 * Gets the object attached to this task.
	 * 
	 * @return the object attached to this task.
	 */
	public Object getAttachment() {
		return attachment;
	}

	/**
     * The method executed when {@code execute()} throws an error.
     *
     * @param t
     *            the error thrown by execution of the task.
     */
    public void onThrowable(Throwable t) {

    }
}