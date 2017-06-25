package com.venenatis.game.cache.io;

import java.nio.ByteBuffer;

public class XTEADecipher {

	private final int[] keys;
	private byte[] bytes;
	private final int startOffset;
	
    public XTEADecipher(int[] xtea_keys, byte[] bytes, int startOffset) {
		this.keys = xtea_keys;
		this.bytes = bytes;
		this.startOffset = startOffset;
	}

	public byte[] decipher() {
		int offset = startOffset;
		int length = bytes.length;// - startOffset;
		
		int qword_count = (length - offset) / 8;
		ByteBuffer inbb = ByteBuffer.wrap(bytes);
		ByteBuffer outbb = ByteBuffer.allocate(bytes.length);

		for (int i = 0; i < startOffset; i++) {
			outbb.put(inbb.get());
		}
		
		for (int qword_pos = 0; qword_pos < qword_count; qword_pos++) {
            int dword_1 = inbb.getInt();
            int dword_2 = inbb.getInt();
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
            outbb.putInt(dword_1);
            outbb.putInt(dword_2);
        }
		
		
		byte[] remaining = new byte[inbb.remaining()];
		inbb.get(remaining);
		outbb.put(remaining);
		
		outbb.flip();
		return outbb.array();
	}

}
