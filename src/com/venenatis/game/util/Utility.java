package com.venenatis.game.util;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Range;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.server.Server;

public class Utility {
	
	public static final RandomGenerator r = new RandomGenerator();
	
	/**
	 * Random instance, used to generate pseudo-random primitive types.
	 */
	public static final Random RANDOM = new Random(System.currentTimeMillis());
	
    private static final String VALID_PASSWORD_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789243 ";

    public static byte[] toPrimitive(Byte[] data) {
        byte[] primitive = new byte[data.length];

        for (int index = 0; index < primitive.length; index++) {
            primitive[index] = data[index];
        }

        return primitive;
    }

    public static boolean isNumeric(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean validPassword(String password) {
        for (Character c : password.toCharArray()) {
            if (!validPasswordCharacter(c))
                return false;
        }

        if (password.length() > 20) {
            return false;
        }
        return true;
    }

    public static boolean validPasswordCharacter(int key) {
        for (int i2 = 0; i2 < VALID_PASSWORD_CHARACTERS.length(); i2++) {
            if (key == VALID_PASSWORD_CHARACTERS.charAt(i2))
                return true;
        }
        return false;
    }

    public static String longToPlayerName(long l) {
        int i = 0;
        char ac[] = new char[12];
        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            ac[11 - i++] = playerNameXlateTable[(int) (l1 - l * 37L)];
        }
        return new String(ac, 12 - i, i);
    }

    public static int getCurrentHP(int i, int i1, int i2) {
        double x = (double) i / (double) i1;
        return (int) Math.round(x * i2);
    }
    
    /**
	 * Formats a number into a string with commas.
	 */
	public static String formatValue(int value) {
		return new DecimalFormat("#, ###").format(value);
	}

    public static String format(long num) {
        return NumberFormat.getInstance().format(num);
    }
    
    /**
	 * Formats digits for integers.
	 * 
	 * @param amount
	 * @return
	 */
	public static String formatDigits(final int amount) {
		return NumberFormat.getInstance().format(amount);
	}

    /**
     * Capitalizes the first character of the argued string. Any leading or
     * trailing whitespace in the argued string should be trimmed before using
     * this method.
     * 
     * @param s
     *            the string to capitalize.
     * @return the capitalized string.
     */
    public static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase().concat(s.substring(1, s.length()));
    }
    
    /**
	 * Capitalized all words split by a space char.
	 * @param name	The string to format.
	 */
	public static String capitalizeWords(String name) {
		StringBuilder builder = new StringBuilder(name.length());
		String[] words = name.split("\\s");
		for(int i = 0, l = words.length; i < l; ++i) {
			if(i > 0)
				builder.append(" ");      
			builder.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1));

		}
		return builder.toString();
	}
	
	/**
	 * Capitalize each letter after .
	 * 
	 * @param string
	 * @return
	 */
	public static String capitalizeSentence(final String string) {
		int pos = 0;
		boolean capitalize = true;
		StringBuilder sb = new StringBuilder(string);
		while (pos < sb.length()) {
			if (sb.charAt(pos) == '.') {
				capitalize = true;
			} else if (capitalize && !Character.isWhitespace(sb.charAt(pos))) {
				sb.setCharAt(pos, Character.toUpperCase(sb.charAt(pos)));
				capitalize = false;
			}
			pos++;
		}
		return sb.toString();
	}

    /**
     * Returns a pseudo-random {@code int} value between inclusive
     * <code>min</code> and inclusive <code>max</code> excluding the specified
     * numbers within the {@code excludes} array.
     * 
     * @param min
     *            The minimum inclusive number.
     * @param max
     *            The maximum inclusive number.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If {@code max - min + 1} is less than <tt>0</tt>.
     * @see {@link #inclusiveRandom(int, int)}.
     */
    public static int inclusiveRandomExcludes(int min, int max, int... exclude) {
        Arrays.sort(exclude);

        int result = inclusiveRandom(min, max);
        while (Arrays.binarySearch(exclude, result) >= 0) {
            result = inclusiveRandom(min, max);
        }

        return result;
    }

    /**
	 * Picks a random element out of any array type.
	 * 
	 * @param collection
	 *            the collection to pick the element from.
	 * @return the element chosen.
	 */
	public static <T> T randomElement(Collection<T> collection) {
		return new ArrayList<T>(collection).get((int) (RANDOM.nextDouble() * collection.size()));
	}

	/**
	 * Picks a random element out of any list type.
	 * 
	 * @param list
	 *            the list to pick the element from.
	 * @return the element chosen.
	 */
	public static <T> T randomElement(List<T> list) {
		return list.get((int) (RANDOM.nextDouble() * list.size()));
	}

	/**
	 * Picks a random element out of any array type.
	 * 
	 * @param array
	 *            the array to pick the element from.
	 * @return the element chosen.
	 */
	public static <T> T randomElement(T[] array) {
		return array[(int) (RANDOM.nextDouble() * array.length)];
	}

	/**
	 * Picks a random element out of any array type.
	 * 
	 * @param array
	 *            the array to pick the element from.
	 * @return the element chosen.
	 */
	public static int randomElement(int[] array) {
		return array[(int) (RANDOM.nextDouble() * array.length)];
	}

    /**
     * Returns a pseudo-random {@code int} value between inclusive
     * <code>min</code> and inclusive <code>max</code>.
     * 
     * @param min
     *            The minimum inclusive number.
     * @param max
     *            The maximum inclusive number.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If {@code max - min + 1} is less than <tt>0</tt>.
     * @see {@link #exclusiveRandom(int)}.
     */
    public static int inclusiveRandom(int min, int max) {
        if (max < min) {
            max = min + 1;
        }
        return exclusiveRandom((max - min) + 1) + min;
    }

    public static int inclusiveRandom(int range) {
        return (int) (ThreadLocalRandom.current().nextDouble() * (range + 1));
    }

    /**
     * Returns a pseudo-random {@code int} value between inclusive
     * <code>min</code> and exclusive <code>max</code>.
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
     * 
     *             <p>
     *             We use {@link ThreadLocalRandom#current()} to produce this
     *             random {@code int}, it is faster than a standard
     *             {@link Random} instance as we do not have to wait on
     *             {@code AtomicLong}.
     *             </p>
     */
    public static int exclusiveRandom(int min, int max) {
        if (max <= min) {
            max = min + 1;
        }
        return RANDOM.nextInt((max - min)) + min;
    }

    /**
     * Returns a pseudo-random {@code int} value between inclusive <tt>0</tt>
     * and exclusive <code>range</code>.
     * 
     * <br>
     * <br>
     * This method is thread-safe. </br>
     * 
     * @param range
     *            The exclusive range.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If the specified range is less <tt>0</tt>
     * 
     *             <p>
     *             We use {@link ThreadLocalRandom#current()} to produce this
     *             random {@code int}, it is faster than a standard
     *             {@link Random} instance as we do not have to wait on
     *             {@code AtomicLong}.
     *             </p>
     */
    public static int exclusiveRandom(int range) {
        return exclusiveRandom(0, range);
    }

    /**
	 * Returns the delta coordinates. Note that the returned Position is not an
	 * actual position, instead it's values represent the delta values between
	 * the two arguments.
	 * 
	 * @param a
	 *            the first position.
	 * @param b
	 *            the second position.
	 * @return the delta coordinates contained within a position.
	 */
	public static Location delta(Location a, Location b) {
		return new Location(b.getX() - a.getX(), b.getY() - a.getY());
	}

    public static String md5Hash(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatName(String str) {
        str = ucFirst(str);
        str.replace("_", " ");
        return str;
    }

    public static String longToReportPlayerName(long l) {
        int i = 0;
        final char ac[] = new char[12];
        while (l != 0L) {
            final long l1 = l;
            l /= 37L;
            ac[11 - i++] = Utility.playerNameXlateTable[(int) (l1 - l * 37L)];
        }
        return new String(ac, 12 - i, i);
    }

    public static String basicEncrypt(String s) {
        String toReturn = "";
        for (int j = 0; j < s.length(); j++) {
            toReturn += (int) s.charAt(j);
        }
        return toReturn;
    }

    public static String longToPlayerName2(long l) {
        int i = 0;
        char ac[] = new char[99];
        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            ac[11 - i++] = playerNameXlateTable[(int) (l1 - l * 37L)];
        }
        return new String(ac, 12 - i, i);
    }

    public static final char playerNameXlateTable[] = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    public static String format(int num) {
        return NumberFormat.getInstance().format(num);
    }

    public static String ucFirst(String str) {
        str = str.toLowerCase();
        if (str.length() > 1) {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        } else {
            return str.toUpperCase();
        }
        return str;
    }

    public static void println(String str) {
        System.out.println(str);
    }

    public static int hexToInt(byte data[], int offset, int len) {
        int temp = 0;
        int i = 1000;
        for (int cntr = 0; cntr < len; cntr++) {
            int num = (data[offset + cntr] & 0xFF) * i;
            temp += num;
            if (i > 1) {
                i = i / 1000;
            }
        }
        return temp;
    }

    public static int getRandom(int range) {
		return (int)(java.lang.Math.random() * (range+1));
	}
    
    public static int random2(int range) { 
		return (int)((java.lang.Math.random() * range) + 1);
	}

    public static int random3(int range) {
        return (int) ((java.lang.Math.random() * range));
    }
    
    public static int randomRange(int min, int max) {
		return (int) new Random().nextInt((max + 1) - min) + min;
	}

    public static int randomNoZero(int range) {
        int val = getRandom(range);

        if (val == 0)
            val++;

        return val;
    }

    public static long playerNameToInt64(String s) {
        long l = 0L;
        for (int i = 0; i < s.length() && i < 12; i++) {
            char c = s.charAt(i);
            l *= 37L;
            if (c >= 'A' && c <= 'Z') {
                l += (1 + c) - 65;
            } else if (c >= 'a' && c <= 'z') {
                l += (1 + c) - 97;
            } else if (c >= '0' && c <= '9') {
                l += (27 + c) - 48;
            }
        }
        while (l % 37L == 0L && l != 0L) {
            l /= 37L;
        }
        return l;
    }

    private static char decodeBuf[] = new char[4096];

    /*
     * public static String textUnpack(byte packedData[], int size) { int idx =
     * 0, highNibble = -1; for (int i = 0; i < size * 2; i++) { int val =
     * packedData[i / 2] >> (4 - 4 * (i % 2)) & 0xf; if (highNibble == -1) { if
     * (val < 13) { decodeBuf[idx++] = xlateTable[val]; } else { highNibble =
     * val; } } else { decodeBuf[idx++] = xlateTable[((highNibble << 4) + val) -
     * 195]; highNibble = -1; } }
     * 
     * 
     * return new String(decodeBuf, 0, idx); }
     */
    public static String textUnpack(final byte packedData[], final int size) {
        int idx = 0, highNibble = -1;
        for (int i = 0; i < size * 2; i++) {
            final int val = packedData[i / 2] >> 4 - 4 * (i % 2) & 0xf;
            if (highNibble == -1) {
                if (val < 13) {
                    Utility.decodeBuf[idx++] = Utility.xlateTable[val];
                } else {
                    highNibble = val;
                }
            } else {
                Utility.decodeBuf[idx++] = Utility.xlateTable[(highNibble << 4) + val - 195];
                highNibble = -1;
            }
        }
        return new String(Utility.decodeBuf, 0, idx);
    }
    
	public static String optimizeText(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
			}
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
				}
			}
		}
		return s;
	}

    public static char xlateTable[] = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p',
            'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(',
            ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[', ']', '>', '<', '^', '/', '_' };

    public static final byte[] DIRECTION_DELTA_X = new byte[] { -1, 0, 1, -1, 1, -1, 0, 1 };
    public static final byte[] DIRECTION_DELTA_Y = new byte[] { 1, 1, 1, 0, 0, -1, -1, -1 };

    public static int direction(int srcX, int srcY, int destX, int destY) {
        int dx = destX - srcX, dy = destY - srcY;

        if (dx < 0) {
            if (dy < 0) {
                if (dx < dy) {
                    return 11;
                }
                if (dx > dy) {
                    return 9;
                }
                return 10;
            }
            if (dy > 0) {
                if (-dx < dy) {
                    return 15;
                }
                if (-dx > dy) {
                    return 13;
                }
                return 14;
            }
            return 12;
        }
        if (dx > 0) {
            if (dy < 0) {
                if (dx < -dy) {
                    return 7;
                }
                if (dx > -dy) {
                    return 5;
                }
                return 6;
            }
            if (dy > 0) {
                if (dx < dy) {
                    return 1;
                }
                if (dx > dy) {
                    return 3;
                }
                return 2;
            }
            return 4;
        }
        if (dy < 0) {
            return 8;
        }
        if (dy > 0) {
            return 0;
        }
        return -1;
    }

    public static byte directionDeltaX[] = new byte[] { 0, 1, 1, 1, 0, -1, -1, -1 };
    public static byte directionDeltaY[] = new byte[] { 1, 1, 0, -1, -1, -1, 0, 1 };

    public static byte xlateDirectionToClient[] = new byte[] { 1, 2, 4, 7, 6, 5, 3, 0 };

    /**
	 * Generates an inclusive pseudo-random number with (approximately) equal
	 * probability.
	 *
	 * @param min
	 *            The minimum value.
	 *
	 * @param max
	 *            The maximum value.
	 *
	 * @return The random generated number.
	 */
	public static int random(int min, int max) {
		return Utility.random(min, max, true);
	}

    public static int getIndex(int id, int[] array) {
        for (int i = 0; i < array.length; i++)
            if (id == array[i])
                return i;
        return -1;
    }

    public static String getNumberFormat(int number) {
        return NumberFormat.getInstance().format(number);
    }

    public static final String sendCashToString(long j) {
        if (j >= 0 && j < 10000)
            return String.valueOf(j);
        else if (j >= 10000 && j < 10000000)
            return j / 1000 + "K";
        else if (j >= 10000000 && j < 999999999)
            return j / 1000000 + "M";
        else
            return Long.toString(j);
    }

    public static String insertCommas(String str) {
        if (str.length() < 4) {
            return str;
        }
        return insertCommas(str.substring(0, str.length() - 3)) + "," + str.substring(str.length() - 3, str.length());
    }
    
    public static String getValueRepresentation(long amount) {
		StringBuilder bldr = new StringBuilder();
		if (amount < 1_000) {
			bldr.append(amount);
		} else if (amount >= 1_000 && amount < 1_000_000) {
			bldr.append("@cya@" + Long.toString(amount / 1_000) + "K @whi@("
					+ insertCommas(Long.toString(amount)) + ")");
		} else if (amount >= 1_000_000) {
			bldr.append("@gre@" + Long.toString(amount / 1_000_000)
					+ "M @whi@(" + insertCommas(Long.toString(amount)) + ")");
		}
		return bldr.toString();
	}

    /**
     * An inclusive or exclusive interval.
     * 
     * @author lare96
     */
    public static class Interval {

        /** The starting point. */
        private int start;

        /** The ending point. */
        private int end;

        /**
         * Creates a new inclusive {@link Interval}.
         * 
         * @param start
         *            the starting point.
         * @param end
         *            the ending point.
         * @return the inclusive interval.
         */
        public Interval inclusiveInterval(int start, int end) {
            if (start > end) {
                throw new IllegalArgumentException("End value must be higher than start value!");
            }

            this.start = start;
            this.end = end;
            return this;
        }

        /**
         * Creates a new exclusive {@link Interval}.
         * 
         * @return the exclusive interval.
         */
        public Interval exclusiveInterval(int start, int end) {
            if (start > end) {
                throw new IllegalArgumentException("End value must be higher than start value!");
            }

            this.start = start + 1;
            this.end = end - 1;
            return this;
        }

        private static Random r = new Random();

        /**
         * Gets a random value based on the interval.
         * 
         * @return the random value.
         */
        public int calculate() {
            int difference = end - start;

            return (start + r.nextInt(difference));
        }

        /**
         * The starting point.
         * 
         * @return the starting point.
         */
        public int getStart() {
            return start;
        }

        /**
         * The ending point.
         * 
         * @return the ending point.
         */
        public int getEnd() {
            return end;
        }
    }

    public static boolean arrayEquals(int element, int[] array) {
        for (int i : array)
            if (i == element)
                return true;
        return false;
    }

    /**
     * A linear search is conducted by looping through the elements array and
     * looking for the index that contains the value specified.
     *
     * @param elements
     *            the elements we're searching through
     * @param value
     *            the value we're searching for
     * @return -1 if the value cannot be found in the array, otherwise the index
     *         of the value.
     */
    public static int linearSearch(int[] elements, int value) {
        for (int index = 0; index < elements.length; index++) {
            if (elements[index] == value) {
                return index;
            }
        }
        return -1;
    }

	public static String findCommand(String message) {
		if (!message.contains(" ") && !message.contains("-")) {
			return message;
		} else if (!message.contains(" ")) {
			return message.substring(0, message.indexOf("-"));
		} else if (!message.contains("-")) {
			return message.substring(0, message.indexOf(" "));
		}
		int seperatorIndex = message.indexOf(" ") < message.indexOf("-") ? message.indexOf(" ") : message.indexOf("-");
		return message.substring(0, seperatorIndex);
	}

	public static String findInput(String message) {
		if (!message.contains(" ") && !message.contains("-")) {
			return "";
		} else if (!message.contains(" ")) {
			return message.substring(message.indexOf("-") + 1);
		} else if (!message.contains("-")) {
			return message.substring(message.indexOf(" ") + 1);
		}
		int seperatorIndex = message.indexOf(" ") < message.indexOf("-") ? message.indexOf(" ") : message.indexOf("-");
		return message.substring(seperatorIndex + 1);
	}

	public static int stringToInt(String value) throws NumberFormatException {
		value = value.toLowerCase();
		value = value.replaceAll("k", "000");
		value = value.replaceAll("m", "000000");
		value = value.replaceAll("b", "000000000");
		BigInteger bi = new BigInteger(value);
		if (bi.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
			return Integer.MAX_VALUE;
		} else if (bi.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {
			return Integer.MIN_VALUE;
		} else {
			return bi.intValue();
		}
	}

	public static String toFormattedHMS(long time) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time), 
				TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
				TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
	}
	
	public static String toFormattedMS(long time) {
		return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(time),
				TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
	}

	public static int distanceBetween(Player c1, Player c2) {
		int x = (int) Math.pow(c1.getX() - c2.getX(), 2.0D);
		int y = (int) Math.pow(c1.getY() - c2.getY(), 2.0D);
		return (int) Math.floor(Math.sqrt(x + y));
	}

	public static long toCycles(long time, TimeUnit unit) {
		return TimeUnit.MILLISECONDS.convert(time, unit) / 600;
	}
	
	public static int toCyclesOrDefault(long time, int def, TimeUnit unit) {
		if (time > Integer.MAX_VALUE) {
			time = def;
		}
		return (int) TimeUnit.MILLISECONDS.convert(time, unit) / 600;
	}
	
	public static long cyclesToMinutes(int cycles) {
		return cycles / 100;
	}
	
	private static long timeCorrection;
	private static long lastTimeUpdate;

	public static synchronized long currentTimeMillis() {
		long l = System.currentTimeMillis();
		if (l < lastTimeUpdate)
			timeCorrection += lastTimeUpdate - l;
		lastTimeUpdate = l;
		return l + timeCorrection;
	}

	public static int distanceToPoint(int x1, int y1, int x2, int y2) {
		int x = (int) Math.pow(x1 - x2, 2.0D);
		int y = (int) Math.pow(y1 - y2, 2.0D);
		return (int) Math.floor(Math.sqrt(x + y));
	}

	public static int combatDifference(int combat1, int combat2) {

		if (combat1 > combat2) {
			return (combat1 - combat2);
		}

		if (combat2 > combat1) {
			return (combat2 - combat1);
		}

		return 0;
	}

	public static double doubleDistanceBetween(Player a1, Player a2) {
        double x = Math.pow(a1.getX() - a2.getX(), 2);
        double y = Math.pow(a1.getY() - a2.getY(), 2);
        return Math.sqrt((x + y));
    }

    public static double doubleDistanceBetween(Player a1, NPC a2) {
        double x = Math.pow(a1.getX() - a2.getX(), 2);
        double y = Math.pow(a1.getY() - a2.getY(), 2);
        return Math.sqrt((x + y));
    }

    public static String fixChatMessage(String message) {
		StringBuilder newText = new StringBuilder();
		boolean wasSpace = true;
		boolean exception = false;
		for (int i = 0; i < message.length(); i++) {
			if(!exception) {
				if (wasSpace) {
					newText.append(("" + message.charAt(i)).toUpperCase());
					if (!String.valueOf(message.charAt(i)).equals(" "))
						wasSpace = false;
				} else {
					newText.append(("" + message.charAt(i)).toLowerCase());
				}
			} else {
				newText.append(("" + message.charAt(i)));
			}
			if (String.valueOf(message.charAt(i)).contains(":"))
				exception = true;
			else
			if (String.valueOf(message.charAt(i)).contains(".")
					|| String.valueOf(message.charAt(i)).contains("!")
					|| String.valueOf(message.charAt(i)).contains("?"))
				wasSpace = true;
		}
		return newText.toString();
	}
	
	/**
	 * Gets the date of server.
	 * 
	 * @return
	 */
	public static String getDate() {
		return new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
	}

	/**
	 * Gets the current server time and formats it
	 * 
	 * @return
	 */
	public static String getTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
		String formattedDate = dateFormat.format(new Date()).toString();
		return formattedDate;
	}

	/**
	 * Gets the current uptime of server and formats it
	 * 
	 * @return
	 */
	public static String getUptime() {
		long totalMinutes = Server.getUptime().elapsed(TimeUnit.MINUTES);		

		long minutes = totalMinutes % 60;		
		long totalHours = totalMinutes / 60;
		long hours = (totalMinutes / 60) % 60;		
		long days = (totalHours / 24) % 24;		

		return String.format("<col=75C934>%d<col=ff9040>Day <col=75C934>%d<col=ff9040>Hour <col=75C934>%d<col=ff9040>Min", days, hours, minutes);
	}
	
	public static String formatTime(long time) {
		final int sec = (int) (time / 1000), h = sec / 3600, m = sec / 60 % 60, s = sec % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);	    
	}
	
	public static boolean isWeekend() {
		Calendar calendar = new GregorianCalendar();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		return day > 6 ? true : false;
	}
	
	private static ZonedDateTime zonedDateTime;

	/**
	 * Gets server time
	 * 
	 * @return
	 */
	public static String getCurrentServerTime() {
		zonedDateTime = ZonedDateTime.now();
		int hour = zonedDateTime.getHour();
		String hourPrefix = hour < 10 ? "0" + hour + "" : "" + hour + "";
		int minute = zonedDateTime.getMinute();
		String minutePrefix = minute < 10 ? "0" + minute + "" : "" + minute + "";
		String prefix = hour > 12 ? "PM" : "AM";
		return "" + hourPrefix + ":" + minutePrefix + " " + prefix;
	}

	
	/**
	 * Determines the indefinite article of a 'thing'.
	 * 
	 * @param thing
	 *            the thing.
	 * @return the indefinite article.
	 */
	public static String determineIndefiniteArticle(String thing) {
		char first = thing.toLowerCase().charAt(0);
		boolean vowel = first == 'a' || first == 'e' || first == 'i' || first == 'o' || first == 'u';
		return vowel ? "an" : "a";
	}
	
	/**
     * Appends the determined indefinite article to {@code thing}.
     *
     * @param thing
     *            the thing to append.
     * @return the {@code thing} after the indefinite article has been appended.
     */
    public static String appendIndefiniteArticle(String thing) {
        return determineIndefiniteArticle(thing).concat(" " + thing);
    }
    
    public static int random_drop_system(int range) {
		return (int) (java.lang.Math.random() * (range + 1));
	}

	/**
	 * Generates an inclusive pseudo-random number with (approximately) equal
	 * probability. This random number has a minimum value of {@code 0} and a
	 * maximum of {@code max}.
	 *
	 * @param max
	 *            The maximum value.
	 *
	 * @return The random generated number.
	 */
	public static int random(int max) {
		return Utility.random(0, max);
	}

	/**
	 * Generates a pseudo-random number with (approximately) equal probability.
	 * This random number has a minimum value of {@code 0} and a maximum of
	 * {@code max}.
	 *
	 * @param max
	 *            The maximum value.
	 *
	 * @param inclusive
	 *            The flag that denotes this number is inclusive.
	 *
	 * @return The random generated number.
	 */
	public static int random(int max, boolean inclusive) {
		return Utility.random(0, max, inclusive);
	}

	/**
	 * Generates a pseudo-random number with (approximately) equal probability
	 *
	 * @param min
	 *            The minimum value.
	 *
	 * @param max
	 *            The maximum value.
	 *
	 * @param inclusive
	 *            The flag that denotes this value is inclusive.
	 *
	 * @return The random generated number.
	 */
	public static int random(int min, int max, boolean inclusive) {
		return new Random().nextInt(max - min + (inclusive ? 1 : 0)) + min;
	}

	/**
	 * Gets Manhattan distance
	 * 
	 * @param x
	 * @param y
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static int getManhattanDistance(int x, int y, int x2, int y2) {
	return Math.abs(x - x2) + Math.abs(y - y2);
	}

	/**
	 * Gets the distance between 2 points
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int getManhattanDistance(Location a, Location b) {
	return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}

	public static Item[] concat(Item[] inventory, Item[] equipment) {
		int inventory_items = inventory.length;
		int equipment_items = equipment.length;
		Item[] c= new Item[inventory_items+equipment_items];
		System.arraycopy(inventory, 0, c, 0, inventory_items);
		System.arraycopy(equipment, 0, c, inventory_items, equipment_items);
		return c;
	}

	public static <T> int sumNullElements(T[] array) {
		return Math.toIntExact(Arrays.stream(array).filter(Objects::isNull).count());
	}

	public static <T> int sumNonNullElements(T[] array) {
		return Math.toIntExact(Arrays.stream(array).filter(Objects::nonNull).count());
	}

	public static double preciseRandom(Range<Double> range) {
		Preconditions.checkArgument(range.getMinimum() <= range.getMaximum(), "The maximum range cannot be less than the minimum range.");
		return range.getMinimum() + (new Random().nextDouble() * (range.getMaximum() - range.getMinimum()));
	}

	public static String getValueWithoutRepresentation(long amount) {
		StringBuilder bldr = new StringBuilder();
		if (amount < 1_000) {
			bldr.append(amount);
		} else if (amount >= 1_000 && amount < 1_000_000) {
			bldr.append(Long.toString(amount / 1_000) + "K");
		} else if (amount >= 1_000_000) {
			bldr.append(Long.toString(amount / 1_000_000) + "M");
		}
		return bldr.toString();
	}

	public static <T> List<T> jsonArrayToList(Path path, Class<T[]> clazz) {
		try {
			T[] collection = new Gson().fromJson(Files.newBufferedReader(path), clazz);
			return new ArrayList<T>(Arrays.asList(collection));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> List<T> jsonObjectToList(Path path, Class<T> clazz) {
		try {
			T collection = new Gson().fromJson(Files.newBufferedReader(path), clazz);
			return new ArrayList<T>(Arrays.asList(collection));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getDistance(Location loc1, Location loc2) {
		int deltaX = loc2.getX() - loc1.getX();
		int deltaY = loc2.getY() - loc1.getY();
		return ((int) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
	}

	/**
	 * A or an
	 * 
	 * @param nextWord
	 * @return
	 */
	public static String getAOrAn(String nextWord) {
		String s = "a";
		char c = nextWord.toUpperCase().charAt(0);
		if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
			s = "an";
		}
		return s;
	}

	public static <T> int findNextNullIndex(T[] array) {
		for (int index = 0; index < array.length; index++) {
			if (array[index] == null) {
				return index;
			}
		}
		return -1;
	}

	public static <K, V> Map<K, V> map(Collection<K> keys, Collection<V> values) {

	    Map<K, V> map = new HashMap<K, V>();
	    Iterator<K> keyIt = keys.iterator();
	    Iterator<V> valueIt = values.iterator();
	    while (keyIt.hasNext() && valueIt.hasNext()) {
	        K k = keyIt.next();
	        if (null != map.put(k, valueIt.next())){
	            throw new IllegalArgumentException("Keys are not unique! Key " + k + " found more then once.");
	        }
	    }
	    if (keyIt.hasNext() || valueIt.hasNext()) {
	        throw new IllegalArgumentException("Keys and values collections have not the same size");
	    };

	    return map;
	}

	/**
	 * Formats name of enum.
	 * 
	 * @param string
	 * @return
	 */
	public static String formatEnumName(final String string) {
		return capitalizeSentence(string.toLowerCase().replaceAll("_", " "));
	}

	/**
	 * Gets random number
	 * 
	 * @param length
	 * @return
	 */
	public static int randomNumber(int length) {
		return (int) (java.lang.Math.random() * length);
	}

	public static String capitalizeJustFirst(String str) {
		str = str.toLowerCase();
		if (str.length() > 1) {
			str = str.substring(0, 1).toUpperCase() + str.substring(1);
		} else {
			return str.toUpperCase();
		}
		return str;
	}
	
	private static DecimalFormat formatter = new DecimalFormat("#,###.##");

	public static String formatNumbers(int amount) {
		return formatter.format(amount).toString();
	}

}