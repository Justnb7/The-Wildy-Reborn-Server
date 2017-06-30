package com.venenatis.game.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * The {@link Random} implementation that provides additional functionality for
 * generating pseudo-random numbers.
 * 
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class RandomGenerator {
	
	private static final Random RANDOM = new Random();

	/**
	 * Creates a new {@link RandomGenerator} with a unique {@code seed}.
	 */
	public RandomGenerator() {
		super();
	}

	/**
	 * Returns a pseudo-random {@code int} value between inclusive {@code min}
	 * and exclusive {@code max}.
	 * 
	 * <br>
	 * <br>
	 * This method is thread-safe. </br>
	 * 
	 * @param min
	 *            The minimum inclusive number.
	 * @param max
	 *            The maximum exclusive number.
	 * @return The pseudo-random {@code int}.
	 * @throws IllegalArgumentException
	 *             If the specified range is less <tt>0</tt>
	 */
	public int exclusive(int min, int max) {
		if (max <= min) {
			max = min + 1;
		}
		return RANDOM.nextInt((max - min)) + min;
	}

	/**
	 * Returns a pseudo-random {@code int} value between inclusive <tt>0</tt>
	 * and exclusive {@code range}.
	 * 
	 * <br>
	 * <br>
	 * This method is thread-safe. </br>
	 * 
	 * @param range
	 *            the exclusive range.
	 * @return the pseudo-random {@code int}.
	 * @throws IllegalArgumentException
	 *             if the specified range is less <tt>0</tt>
	 */
	public int exclusive(int range) {
		return exclusive(0, range);
	}

	/**
	 * Returns a pseudo-random {@code int} value between inclusive {@code min}
	 * and inclusive {@code max}.
	 * 
	 * @param min
	 *            the minimum inclusive number.
	 * @param max
	 *            the maximum inclusive number.
	 * @return the pseudo-random {@code int}.
	 * @throws IllegalArgumentException
	 *             if {@code max - min + 1} is less than <tt>0</tt>.
	 * @see {@link #exclusive(int)}.
	 */
	public int inclusive(int min, int max) {
		if (max < min) {
			max = min + 1;
		}
		return exclusive((max - min) + 1) + min;
	}

	/**
	 * Returns a pseudo-random {@code int} value between inclusive <tt>0</tt>
	 * and inclusive {@code range}.
	 * 
	 * @param range
	 *            the maximum inclusive number.
	 * @return the pseudo-random {@code int}.
	 * @throws IllegalArgumentException
	 *             if {@code max - min + 1} is less than <tt>0</tt>.
	 * @see {@link #exclusive(int)}.
	 */
	public int inclusive(int range) {
		return inclusive(0, range);
	}

	/**
	 * Returns a pseudo-random {@code int} value between inclusive {@code min}
	 * and inclusive {@code max} excluding the specified numbers within the
	 * {@code excludes} array.
	 * 
	 * @param min
	 *            the minimum inclusive number.
	 * @param max
	 *            the maximum inclusive number.
	 * @return the pseudo-random {@code int}.
	 * @throws IllegalArgumentException
	 *             if {@code max - min + 1} is less than <tt>0</tt>.
	 * @see {@link #inclusive(int, int)}.
	 */
	public int inclusiveExcludes(int min, int max, int... exclude) {
		Arrays.sort(exclude);

		int result = inclusive(min, max);
		while (Arrays.binarySearch(exclude, result) >= 0) {
			result = inclusive(min, max);
		}

		return result;
	}

	/**
	 * Pseudo-randomly retrieves a element from {@code array}.
	 * 
	 * @param array
	 *            the array to retrieve an element from.
	 * @return the element retrieved from the array.
	 */
	public <T> T random(T[] array) {
		return array[(int) (RANDOM.nextDouble() * array.length)];
	}

	/**
	 * Pseudo-randomly retrieves an {@code int} from this {@code array}.
	 * 
	 * @param array
	 *            the array to retrieve an {@code int} from.
	 * @return the {@code int} retrieved from the array.
	 */
	public int random(int[] array) {
		return array[(int) (RANDOM.nextDouble() * array.length)];
	}

	/**
	 * Pseudo-randomly retrieves an {@code long} from this {@code array}.
	 * 
	 * @param array
	 *            the array to retrieve an {@code long} from.
	 * @return the {@code long} retrieved from the array.
	 */
	public long random(long[] array) {
		return array[(int) (RANDOM.nextDouble() * array.length)];
	}

	/**
	 * Pseudo-randomly retrieves an {@code double} from this {@code array}.
	 * 
	 * @param array
	 *            the array to retrieve an {@code double} from.
	 * @return the {@code double} retrieved from the array.
	 */
	public double random(double[] array) {
		return array[(int) (RANDOM.nextDouble() * array.length)];
	}

	/**
	 * Pseudo-randomly retrieves an {@code float} from this {@code array}.
	 * 
	 * @param array
	 *            the array to retrieve an {@code float} from.
	 * @return the {@code float} retrieved from the array.
	 */
	public float random(float[] array) {
		return array[(int) (RANDOM.nextDouble() * array.length)];
	}

	/**
	 * Pseudo-randomly retrieves a element from {@code list}.
	 * 
	 * @param list
	 *            the list to retrieve an element from.
	 * @return the element retrieved from the list.
	 */
	public <T> T random(List<T> list) {
		return list.get((int) (RANDOM.nextDouble() * list.size()));
	}

	/**
	 * Rounds and returns the {@code counter} to the {@code place}.
	 * 
	 * @param counter
	 *            the number that will be rounded.
	 * @param place
	 *            the decimal place to round to.
	 * @return the rounded number.
	 */
	public double round(double counter, double place) {
		return Math.round(counter * place) / place;
	}

	/**
	 * Generates a pseudo-random {@code double} to be rounded and rolled against
	 * the {@code bet}.
	 * 
	 * @param bet
	 *            the bet that the number will be rolled against.
	 * @param round
	 *            the {@code double} that will be used to round the bet and
	 *            pseudo-randomly generated number.
	 * @return {@code true} if the roll was successful, {@code false} otherwise.
	 */
	public boolean roll(double bet, double round) {
		double betRound = round(bet, round);
		double genRound = round(RANDOM.nextDouble(), round);
		return genRound <= betRound;
	}

	/**
	 * Generates a pseudo-random {@code double} to be rounded to <tt>100.0</tt>
	 * and rolled against the {@code bet}.
	 * 
	 * @param bet
	 *            the bet that the number will be rolled against.
	 * @return {@code true} if the roll was successful, {@code false} otherwise.
	 */
	public boolean roll(double bet) {
		return roll(bet, 100.0);
	}
	
	public static int random(int n) {
		return RandomGenerator.RANDOM.nextInt(n + 1);
	}

	public static int nextInt() {
		return RandomGenerator.RANDOM.nextInt();
	}

	public static int nextInt(int n) {
		return RandomGenerator.RANDOM.nextInt(n);
	}

	public static long nextLong() {
		return RandomGenerator.RANDOM.nextLong();
	}

	public static boolean nextBoolean() {
		return RandomGenerator.RANDOM.nextBoolean();
	}

	public static float nextFloat() {
		return RandomGenerator.RANDOM.nextFloat();
	}

	public static double nextDouble() {
		return RandomGenerator.RANDOM.nextDouble();
	}
	
	/**
	 * Returns a random integer with min as the inclusive lower bound and max as
	 * the exclusive upper bound.
	 * 
	 * @param min
	 *            The inclusive lower bound.
	 * @param max
	 *            The exclusive upper bound.
	 * @return Random integer min <= n < max.
	 */
	public static int random(int min, int max) {
		int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : RandomGenerator.RANDOM.nextInt(n));
	}

	public static boolean hunter(int req, int level) {
		req += 20;
		level += 10;
		req = req * (req - (req / 8));
		level = level * level;
		if (RandomGenerator.random(req) < RandomGenerator.random(level)) {
			return true;
		}
		return false;
	}

	public static boolean butterflyHunter(int req, int level, boolean hasNet) {
		req *= hasNet ? 1.75F : 2.0F;
		level += 10;
		req = req * (req - (req / 8));
		level = level * level;
		if (RandomGenerator.random(req) < RandomGenerator.random(level)) {
			return true;
		}
		return false;
	}
}
