package org.openrs.cache.util.crypto;

import java.nio.ByteBuffer;

/**
 * An implementation of the XTEA block cipher.
 * @author Graham
 * @author `Discardedx2
 */
public final class Xtea {

	/**
	 * The golden ratio.
	 */
	public static final int GOLDEN_RATIO = 0x9E3779B9;

	/**
	 * The number of rounds.
	 */
	public static final int ROUNDS = 32;

	/**
	 * Deciphers the specified {@link ByteBuffer} with the given key.
	 * @param buffer The buffer.
	 * @param key The key.
	 * @throws IllegalArgumentException if the key is not exactly 4 elements
	 * long.
	 */
	public static void decipher(ByteBuffer buffer, int[] key) {
		if (key.length != 4)
			throw new IllegalArgumentException();

		for (int i = 0; i < buffer.limit(); i += 8) {
			int sum = GOLDEN_RATIO * ROUNDS;
			int v0 = buffer.getInt(i * 4);
			int v1 = buffer.getInt(i * 4 + 4);
			for (int j = 0; j < ROUNDS; j++) {
				v1 = (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);
				sum -= GOLDEN_RATIO;
				v0 = (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);
			}
			buffer.putInt(i * 4, v0);
			buffer.putInt(i * 4 + 4, v1);
		}
	}
	
	public static void decipher(ByteBuffer bb, int[] keys, int startOffset) {
		bb.position(startOffset);
		int qword_count = (bb.capacity() - startOffset) / 8;
		for (int qword_pos = 0; qword_pos < qword_count; qword_pos++) {
			int dword_1 = bb.getInt();
			int dword_2 = bb.getInt();
			int const_1 = -957401312;
			int const_2 = -1640531527;
			int run_count = 32;
			while ((run_count-- ^ 0xffffffff) < -1) {
				dword_2 -= ((dword_1 >>> -1563092443 ^ dword_1 << 611091524) + dword_1
						^ const_1 + keys[const_1 >>> -1002502837 & 0x56c00003]);
				const_1 -= const_2;
				dword_1 -= ((dword_2 >>> 1337206757 ^ dword_2 << 363118692) - -dword_2
						^ const_1 + keys[const_1 & 0x3]);
			}
			bb.position(bb.position() - 8);
			bb.putInt(dword_1);
			bb.putInt(dword_2);
		}
		bb.rewind();
	}

	/**
	 * Enciphers the specified {@link ByteBuffer} with the given key.
	 * @param buffer The buffer.
	 * @param key The key.
	 * @throws IllegalArgumentException if the key is not exactly 4 elements
	 * long.
	 */
	public static void encipher(ByteBuffer buffer, int[] key) {
		if (key.length != 4)
			throw new IllegalArgumentException();

		for (int i = 0; i < buffer.limit(); i += 8) {
			int sum = 0;
			int v0 = buffer.getInt(i * 4);
			int v1 = buffer.getInt(i * 4 + 4);
			for (int j = 0; j < ROUNDS; j++) {
				v0 = (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);
				sum += GOLDEN_RATIO;
				v1 = (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);
			}
			buffer.putInt(i * 4, v0);
			buffer.putInt(i * 4 + 4, v1);
		}
	}

	/**
	 * Default private constructor to prevent instantiation.
	 */
	private Xtea() {
		
	}

}
