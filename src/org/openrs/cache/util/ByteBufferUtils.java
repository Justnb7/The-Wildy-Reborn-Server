package org.openrs.cache.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.openrs.cache.util.crypto.Whirlpool;


/**
 * Contains {@link ByteBuffer}-related utility methods.
 * @author Graham
 * @author `Discardedx2
 */
public final class ByteBufferUtils {

	/**
	 * The modified set of 'extended ASCII' characters used by the client.
	 */
	private static char CHARACTERS[] = { '\u20AC', '\0', '\u201A', '\u0192',
		'\u201E', '\u2026', '\u2020', '\u2021', '\u02C6', '\u2030',
		'\u0160', '\u2039', '\u0152', '\0', '\u017D', '\0', '\0', '\u2018',
		'\u2019', '\u201C', '\u201D', '\u2022', '\u2013', '\u2014',
		'\u02DC', '\u2122', '\u0161', '\u203A', '\u0153', '\0', '\u017E',
		'\u0178' };

	/**
	 * Calculates the CRC32 checksum of the specified buffer.
	 * @param buffer The buffer.
	 * @return The CRC32 checksum.
	 */
	public static int getCrcChecksum(ByteBuffer buffer) {
		Checksum crc = new CRC32();
		for (int i = 0; i < buffer.limit(); i++) {
			crc.update(buffer.get(i));
		}
		return (int) crc.getValue();
	}

	/**
	 * Gets a null-terminated string from the specified buffer, using a
	 * modified ISO-8859-1 character set.
	 * @param buf The buffer.
	 * @return The decoded string.
	 */
	public static String getJagexString(ByteBuffer buf) {
		StringBuilder bldr = new StringBuilder();
		int b;
		while ((b = buf.get()) != 0) {
			if (b >= 127 && b < 160) {
				char curChar = CHARACTERS[b - 128];
				if (curChar != 0) {
					bldr.append(curChar);
				}
			} else {
				bldr.append((char) b);
			}
		}
		return bldr.toString();
	}
	
	/**
	 * Gets a smart integer from the buffer.
	 * @param buffer The buffer.
	 * @return The value.
	 */
	public static int getSmartInt(ByteBuffer buffer) {
		if (buffer.get(buffer.position()) < 0)
			return buffer.getInt() & 0x7fffffff;
		return buffer.getShort() & 0xFFFF;
	}

	/**
	 * Gets a smart.
	 * @param buf The buffer.
	 * @return The smart.
	 */
	public static int getSmart(ByteBuffer buf) {
		int peek = buf.get(buf.position()) & 0xFF;
		if(peek < 128) {
			return buf.get() & 0xFF;
		} else {
			return (buf.getShort() & 0xFFFF) - 32768;
		}
	}
	
	/**
	 * Reads a 'tri-byte' from the specified buffer.
	 * @param buf The buffer.
	 * @return The value.
	 */
	public static int getTriByte(ByteBuffer buf) {
		return ((buf.get() & 0xFF) << 16) | ((buf.get() & 0xFF) << 8) | (buf.get() & 0xFF);
	}

	/**
	 * Calculates the whirlpool digest of the specified buffer.
	 * @param buf The buffer.
	 * @return The 64-byte whirlpool digest.
	 */
	public static byte[] getWhirlpoolDigest(ByteBuffer buf) {
		byte[] bytes = new byte[buf.limit()];
		buf.get(bytes);
		return Whirlpool.whirlpool(bytes, 0, bytes.length);
	}

	/**
	 * Writes a 'tri-byte' to the specified buffer.
	 * @param buf The buffer.
	 * @param value The value.
	 */
	public static void putTriByte(ByteBuffer buf, int value) {
		buf.put((byte) (value >> 16));
		buf.put((byte) (value >> 8));
		buf.put((byte) value);
	}

	/**
	 * Converts the contents of the specified byte buffer to a string, which is
	 * formatted similarly to the output of the {@link Arrays#toString()}
	 * method.
	 * @param buffer The buffer.
	 * @return The string.
	 */
	public static String toString(ByteBuffer buffer) {
		StringBuilder builder = new StringBuilder("[");
		for (int i = 0; i < buffer.limit(); i++) {
			String hex = Integer.toHexString(buffer.get(i) & 0xFF).toUpperCase();
			if (hex.length() == 1)
				hex = "0" + hex;

			builder.append("0x").append(hex);
			if (i != buffer.limit() - 1) {
				builder.append(", ");
			}
		}
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * Gets an RS2 string from the buffer.
	 * @param buf The buffer.
	 * @return The RS2 string.
	 */
	public static String getString(ByteBuffer buf) {
		StringBuilder bldr = new StringBuilder();
		char c;
		while((c = (char) buf.get()) != 10) {
			bldr.append(c);
		}
		return bldr.toString();
	}
	
	/**
	 * Puts a string on the byte buffer.
	 * @param s The string to put.
	 * @param buffer The byte buffer.
	 */
	public static void putString(String s, ByteBuffer buffer) {
		buffer.put(s.getBytes()).put((byte) 0);
	}


	/**
	 * Default private constructor to prevent instantiation.
	 */
	private ByteBufferUtils() {
		
	}

}
