package com.venenatis.server;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.venenatis.TimesCx;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Stopwatch;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItemHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A service dedicated to handling all game logic. This service executes packets
 * every {@code 300}ms instead of every {@code 600}ms to improve the speed of
 * packet processing.
 *
 * @author Mobster
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public final class GameEngine implements Runnable {
	
    /**
     * The maximum amount of {@link Player}s allowed to be logged in per cycle.
     */
    private static final int LOGIN_THRESHOLD = 25;

    /**
     * The rate in which to pulse the server.
     */
    private static final int PULSE_RATE = 600;

    /**
     * A logger which will log messages.
     */
    private static final Logger logger = Logger.getLogger(GameEngine.class.getName());

    /**
     * A queue of {@link Player}s who are waiting to be logged in.
     */
    private static final Queue<Player> loginQueue = new ConcurrentLinkedQueue<>();

    /**
     * The stopwatch which will time how long server cycles take.
     */
    private static final Stopwatch benchmark = new Stopwatch();

    /**
     * Private constructor to restrict external instantiation.
     */
    private GameEngine() {
    	
    }

    /**
     * Initializes and starts this {@link GameLogicService}.
     */
    public static void start() {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("GameLogicService").setPriority(Thread.MAX_PRIORITY).build();
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(factory);
        service.scheduleAtFixedRate(new GameEngine(), 0, PULSE_RATE, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            benchmark.reset();
            cycle();
            benchmark.ifElapsed(600, t -> logger.warning("Engine has reached maximum load! [overhead= " + t + "ms]"));
        } catch (Exception reason) {
            logger.log(Level.SEVERE, "Fatal error while handling server logic loop", reason);
            reason.printStackTrace();
        }
    }

    /**
     * Adds {@code player} to the login queue.
     *
     * @param player
     *            the player to add to the login queue.
     */
    public static void queueLogin(Player player) {
        Preconditions.checkArgument(!loginQueue.contains(player), "Login queue already contains " + player);
        loginQueue.add(player);
    }
    
    public static TimesCx profile;

    /**
     * Performs a normal cycle where the {@link World} is synchronized with the
     * game thread. These are done every {@code 600}ms.
     */
    private static void cycle() {
    	profile = new TimesCx();
    	long start = System.currentTimeMillis();
        for (int count = 0; count < LOGIN_THRESHOLD; count++) {
            Player p = loginQueue.poll();
            if (p == null)
                break;
            World.getWorld().register(p);
        }
        profile.login = System.currentTimeMillis() - start;
        
        //So all this is triggered when clicking the buttons lol
        Server.getGlobalObjects().pulse();
        Server.getTaskScheduler().pulse();
        World.getWorld().pulse();
        GroundItemHandler.pulse();
        
        profile.total = (System.currentTimeMillis() - start);
        profile.print();
    }

    /**
     * Returns an unmodifable collection of {@link Player}s that need to be
     * logged in.
     *
     * @return the queue of players that need to be logged in.
     */
    public static Collection<Player> getLoginQueue() {
        return Collections.unmodifiableCollection(loginQueue);
    }
}