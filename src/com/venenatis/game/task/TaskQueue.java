package com.venenatis.game.task;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A class which schedules the execution of {@link Task}'s.
 *
 * @author Graham
 * @author lare96
 */
public final class TaskQueue implements Service {

	/**
	 * A queue of pending tasks.
	 */
	private static final Queue<Task> pendingTasks = new LinkedList<>();

	/**
	 * A list of active tasks.
	 */
	private static final List<Task> tasks = new LinkedList<>();

	/**
	 * Schedules the specified task. If this scheduler has been stopped with the
	 * {@link #terminate()} method the task will not be executed or
	 * garbage-collected.
	 *
	 * @param task
	 *            The task to schedule.
	 */
	public void schedule(final Task task) {
		if (task.isImmediate()) {
			task.execute();
		}
		pendingTasks.add(task);
	}

	public boolean running(Object key) {
		return pendingTasks.stream().anyMatch(t -> t.getAttachment().equals(key) && t.isRunning()) || tasks.stream().anyMatch(t -> t.getAttachment().equals(key) && t.isRunning());
	}

	public void submit(final Task task) {
		schedule(task);
	}

	/**
	 * This method is automatically called every cycle by the
	 * {@link ScheduledExecutorService} and executes, adds and removes
	 * {@link TaskType}s. It should not be called directly as this will lead to
	 * concurrency issues and inaccurate time-keeping.
	 */
	@Override
	public void pulse() {
		Task task;

		// add all of the pending tasks
		while ((task = pendingTasks.poll()) != null) {
			if (task.isRunning()) {
				tasks.add(task);
			}
		}

		// process all existing tasks
		Iterator<Task> it = tasks.iterator();
		while (it.hasNext()) {
			task = it.next();
			if (task.isStopped()) {
				it.remove();
				continue;
			}
			task.tick();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Tasks active: " + tasks.size() + ", Pending Tasks: " + pendingTasks.size() + "\n");
		for (Task task : tasks) {
			sb.append("Task alias: " + task.getAttachment().toString() + "\n");
		}
		return sb.toString();
	}

}
