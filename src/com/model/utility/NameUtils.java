package com.model.utility;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.model.game.Constants;
import com.model.game.item.Item;

/**
 * Name utility class.
 * 
 * @author Graham Edgecombe
 * 
 */
public class NameUtils {
	
	/**
	 * An array containing valid characters that can be used in the server.
	 */
	public static final char VALID_CHARACTERS[] = {
			' ', '!', '"', '#', '$', '%', '&', '(', ')', '*', '+', ',', '-', '.',
			':', ';', '<', '=', '>', '?', '@', '[', ']', '^', '_',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z'
		};

	/**
	 * An array containing valid username characters.
	 */
	public static final char VALID_PLAYER_CHARACTERS[] = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '[', ']', '/', '-', ' ' };

	/**
	 * Checks if a name is valid.
	 * 
	 * @param s
	 *            The name.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public static boolean isValidName(String s) {
		return formatNameForProtocol(s).matches("[a-z0-9_]+") && s.length() <= 12 && s.length() > 0;
	}

	/**
	 * Converts a name to a long.
	 * 
	 * @param s
	 *            The name.
	 * @return The long.
	 */
	public static long nameToLong(String s) {
		long l = 0L;
		for (int i = 0; i < s.length() && i < 12; i++) {
			char c = s.charAt(i);
			l *= 37L;
			if (c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
			else if (c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
			else if (c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}
		while (l % 37L == 0L && l != 0L)
			l /= 37L;
		return l;
	}

	/**
	 * Converts a long to a name.
	 * 
	 * @param l
	 *            The long.
	 * @return The name.
	 */
	public static String longToName(long l) {
		int i = 0;
		char ac[] = new char[12];
		while (l != 0L) {
			long l1 = l;
			l /= 37L;
			ac[11 - i++] = Constants.VALID_CHARS[(int) (l1 - l * 37L)];
		}
		return new String(ac, 12 - i, i);
	}

	/**
	 * Formats a name for use in the protocol.
	 * 
	 * @param s
	 *            The name.
	 * @return The formatted name.
	 */
	public static String formatNameForProtocol(String s) {
		return s.toLowerCase().replace(" ", "_");
	}

	/**
	 * Formats a name for display.
	 * 
	 * @param s
	 *            The name.
	 * @return The formatted name.
	 */
	public static String formatName(String s) {
		return fixName(s.replace(" ", "_"));
	}

	/**
	 * Method that fixes capitalization in a name.
	 * 
	 * @param s
	 *            The name.
	 * @return The formatted name.
	 */
	private static String fixName(final String s) {
		if (s.length() > 0) {
			final char ac[] = s.toCharArray();
			for (int j = 0; j < ac.length; j++)
				if (ac[j] == '_') {
					ac[j] = ' ';
					if ((j + 1 < ac.length) && (ac[j + 1] >= 'a') && (ac[j + 1] <= 'z')) {
						ac[j + 1] = (char) ((ac[j + 1] + 65) - 97);
					}
				}

			if ((ac[0] >= 'a') && (ac[0] <= 'z')) {
				ac[0] = (char) ((ac[0] + 65) - 97);
			}
			return new String(ac);
		} else {
			return s;
		}
	}

	public static String formatInt(int num) {
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
	
	public static String getAOrAn(String nextWord) {
		String s = "a";
		final char c = nextWord.toUpperCase().charAt(0);
		if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
			s = "an";
		}
		return s;
	}

	public static String longToString(long l) {
		int i = 0;
		final char ac[] = new char[12];

		while (l != 0L) {
			final long l1 = l;

			l /= 37L;
			ac[11 - i++] = VALID_PLAYER_CHARACTERS[(int) (l1 - l * 37L)];
		}
		return new String(ac, 12 - i, i);
	}

	/**
	 * Gets the names of an array of items.
	 * 
	 * @param items
	 *            The items to get the name of.
	 * 
	 * @return A single string of the names.
	 */
	public static String getItemNames(Item[] items) {
		String tradeItems = "<col=ff9040>Absolutely nothing!";
		String tradeAmount = "";

		int count = 0;

		for (Item item : items) {
			if (item == null) {
				continue;
			}

			tradeAmount = String.format("<col=06FF7F>%s (%s)", formatPrice(item.getAmount()), formatValue(item.getAmount()));

			tradeItems = count == 0 ? "<col=ff9040>" + item.getName() : String.format("<col=ff9040>%s\\n\\n<col=ff9040>%s", tradeItems, item.getName());

			if (item.isStackable()) {
				tradeItems = tradeItems + " <col=06FF7F>x " + tradeAmount;
			}
			count++;
		}

		return tradeItems;
	}

	public static String capitalize(final String string) {
		return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}

	public static String format(int num) {
		return NumberFormat.getInstance().format(num);
	}

	/**
	 * Formats number
	 * 
	 * @param amount
	 * @return
	 */
	public static String formatPrice(int amount) {
		if (amount >= 1_000 && amount < 1_000_000) {
			return (amount / 1_000) + "K";
		}
		if (amount >= 1_000_000) {
			return (amount / 1_000_000) + "M";
		}
		if (amount >= 1_000_000_000) {
			return (amount / 1_000_000_000) + "B";
		}
		return "" + amount;
	}

	/**
	 * Formats a number into a string with commas.
	 */
	public static String formatValue(int value) {
		return new DecimalFormat("#, ###").format(value);
	}

	public static String formatPrice(long amount) {
		if (amount >= 1_000 && amount < 1_000_000) {
			return (amount / 1_000) + "K";
		}
		if (amount >= 1_000_000) {
			return (amount / 1_000_000) + "M";
		}
		if (amount >= 1_000_000_000) {
			return (amount / 1_000_000_000) + "B";
		}
		return amount + " GP";
	}

	public static String formatText(String s) {
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
		return s.replace("_", " ");
	}

}
