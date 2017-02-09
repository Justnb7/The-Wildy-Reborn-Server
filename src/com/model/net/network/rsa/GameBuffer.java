package com.model.net.network.rsa;

public final class GameBuffer {

	public GameBuffer(byte abyte0[]) {
		buffer = abyte0;
		offset = 0;
	}

	public byte readSignedByteC() {
		return (byte) (-buffer[offset++]);
	}

	public long readQWord2() {
		final long l = readDWord() & 0xffffffffL;
		final long l1 = readDWord() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public int readUnsignedByteS() {
		return 128 - buffer[offset++] & 0xff;
	}

	public void putByteA(int i) {
		ensureCapacity(1);
		buffer[offset++] = (byte) (i + 128);
	}

	public void writeByteS(int i) {
		ensureCapacity(1);
		buffer[offset++] = (byte) (128 - i);
	}

	public void writeByteC(int i) {
		ensureCapacity(1);
		buffer[offset++] = (byte) (-i);
	}

	public int readSignedWordBigEndian() {
		offset += 2;
		int i = ((buffer[offset - 1] & 0xff) << 8) + (buffer[offset - 2] & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int readSignedWordA() {
		offset += 2;
		int i = ((buffer[offset - 2] & 0xff) << 8) + (buffer[offset - 1] - 128 & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int readSignedWordBigEndianA() {
		offset += 2;
		int i = ((buffer[offset - 1] & 0xff) << 8) + (buffer[offset - 2] - 128 & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int readUnsignedWordBigEndian() {
		offset += 2;
		return ((buffer[offset - 1] & 0xff) << 8) + (buffer[offset - 2] & 0xff);
	}

	public int readUnsignedWordA() {
		offset += 2;
		return ((buffer[offset - 2] & 0xff) << 8) + (buffer[offset - 1] - 128 & 0xff);
	}

	public int readUnsignedWordBigEndianA() {
		offset += 2;
		return ((buffer[offset - 1] & 0xff) << 8) + (buffer[offset - 2] - 128 & 0xff);
	}

	public void writeWordBigEndianA(int i) {
		ensureCapacity(2);
		buffer[offset++] = (byte) (i + 128);
		buffer[offset++] = (byte) (i >> 8);
	}

	public void writeWordA(int i) {
		ensureCapacity(2);
		buffer[offset++] = (byte) (i >> 8);
		buffer[offset++] = (byte) (i + 128);
	}

	public void writeWordBigEndian_dup(int i) {
		ensureCapacity(2);
		buffer[offset++] = (byte) i;
		buffer[offset++] = (byte) (i >> 8);
	}

	public void writeDWord_v1(int i) {
		ensureCapacity(4);
		buffer[offset++] = (byte) (i >> 8);
		buffer[offset++] = (byte) i;
		buffer[offset++] = (byte) (i >> 24);
		buffer[offset++] = (byte) (i >> 16);
	}

	public void writeDWord_v2(int i) {
		ensureCapacity(4);
		buffer[offset++] = (byte) (i >> 16);
		buffer[offset++] = (byte) (i >> 24);
		buffer[offset++] = (byte) i;
		buffer[offset++] = (byte) (i >> 8);
	}

	public void writeBytes_reverse(byte abyte0[], int i, int j) {
		ensureCapacity(i);
		for (int k = (j + i) - 1; k >= j; k--)
			buffer[offset++] = abyte0[k];

	}

	public void readBytes_reverseA(byte abyte0[], int i, int j) {
		ensureCapacity(i);
		for (int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = (byte) (buffer[offset++] - 128);

	}

	public void writeFrame(int id) {
		ensureCapacity(1);
		buffer[offset++] = (byte) (id + packetEncryption.getNextKey());
	}

	public void putFrameVarByte(int id) {
		ensureCapacity(3);
		writeFrame(id);
		writeByte(0);
	}

	public void putFrameVarShort(int id) {
		ensureCapacity(2);
		writeFrame(id);
		writeShort(0);
	}
	
	private static final int frameStackSize = 10;
	private int frameStackPtr = -1;
	private int frameStack[] = new int[frameStackSize];

	public void createFrameVarSize(int id) {
		ensureCapacity(3);
		buffer[offset++] = (byte) (id + packetEncryption.getNextKey());
		buffer[offset++] = 0;
		if (frameStackPtr >= frameStackSize - 1) {
			throw new RuntimeException("Stack overflow");
		} else
			frameStack[++frameStackPtr] = offset;
	}
	
	public void writeWord(int i) {
		ensureCapacity(2);
		buffer[offset++] = (byte) (i >> 8);
		buffer[offset++] = (byte) i;
	}
	
	public void writeFrameSize(int i) {
		buffer[offset - i - 1] = (byte) i;
	}

	public void createFrameVarSizeWord(int id) {
		ensureCapacity(2);
		buffer[offset++] = (byte) (id + packetEncryption.getNextKey());
		writeWord(0);
		if (frameStackPtr >= frameStackSize - 1) {
			throw new RuntimeException("Stack overflow");
		} else
			frameStack[++frameStackPtr] = offset;
	}

	public void endFrameVarSize() {
		if (frameStackPtr < 0)
			throw new RuntimeException("Stack empty");
		else
			writeFrameSize(offset - frameStack[frameStackPtr--]);
	}
	
	public void writeFrameSizeWord(int i) {
		buffer[offset - i - 2] = (byte) (i >> 8);
		buffer[offset - i - 1] = (byte) i;
	}

	public void endFrameVarSizeWord() {
		if (frameStackPtr < 0)
			throw new RuntimeException("Stack empty");
		else
			writeFrameSizeWord(offset - frameStack[frameStackPtr--]);
	}

	public void writeByte(int i) {
		ensureCapacity(1);
		buffer[offset++] = (byte) i;
	}

	public void writeShort(int i) {
		ensureCapacity(2);
		buffer[offset++] = (byte) (i >> 8);
		buffer[offset++] = (byte) i;
	}

	public void writeWordBigEndian(int i) {
		ensureCapacity(2);
		buffer[offset++] = (byte) i;
		buffer[offset++] = (byte) (i >> 8);
	}

	public void write3Byte(int i) {
		ensureCapacity(3);
		buffer[offset++] = (byte) (i >> 16);
		buffer[offset++] = (byte) (i >> 8);
		buffer[offset++] = (byte) i;
	}

	public void putInt(int i) {
		ensureCapacity(4);
		buffer[offset++] = (byte) (i >> 24);
		buffer[offset++] = (byte) (i >> 16);
		buffer[offset++] = (byte) (i >> 8);
		buffer[offset++] = (byte) i;
	}

	public void putLong(long l) {
		ensureCapacity(8);
		buffer[offset++] = (byte) (int) (l >> 56);
		buffer[offset++] = (byte) (int) (l >> 48);
		buffer[offset++] = (byte) (int) (l >> 40);
		buffer[offset++] = (byte) (int) (l >> 32);
		buffer[offset++] = (byte) (int) (l >> 24);
		buffer[offset++] = (byte) (int) (l >> 16);
		buffer[offset++] = (byte) (int) (l >> 8);
		buffer[offset++] = (byte) (int) l;
	}

	public void putRS2String(java.lang.String s) {
		ensureCapacity(s.length());
		System.arraycopy(s.getBytes(), 0, buffer, offset, s.length());
		offset += s.length();
		buffer[offset++] = 10;
	}

	public void writeBytes(byte abyte0[], int i, int j) {
		ensureCapacity(i);
		for (int k = j; k < j + i; k++)
			buffer[offset++] = abyte0[k];
	}

	public void writeBytes(byte[] data, int len) {
		ensureCapacity(len);
		System.arraycopy(data, 0, buffer, offset, len);
		offset += len;
	}

	public int readUnsignedByte() {
		return buffer[offset++] & 0xff;
	}

	public byte readSignedByte() {
		return buffer[offset++];
	}

	public int readUnsignedWord() {
		offset += 2;
		return ((buffer[offset - 2] & 0xff) << 8) + (buffer[offset - 1] & 0xff);
	}

	public int readSignedWord() {
		offset += 2;
		int i = ((buffer[offset - 2] & 0xff) << 8) + (buffer[offset - 1] & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int readDWord() {
		offset += 4;
		return ((buffer[offset - 4] & 0xff) << 24) + ((buffer[offset - 3] & 0xff) << 16) + ((buffer[offset - 2] & 0xff) << 8) + (buffer[offset - 1] & 0xff);
	}

	public long readQWord() {
		long l = readDWord() & 0xffffffffL;
		long l1 = readDWord() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public java.lang.String readString() {
		int i = offset;
		while (buffer[offset++] != 10);
		return new String(buffer, i, offset - i - 1);
	}

	public void readBytes(byte abyte0[], int i, int j) {
		for (int k = j; k < j + i; k++)
			abyte0[k] = buffer[offset++];

	}

	public void initBitAccess() {
		bitPosition = offset * 8;
	}

	public void writeBits(int numBits, int value) {
		int numBits1 = numBits;
		ensureCapacity(((int) Math.ceil(numBits1 * 8)) * 4);
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits1;

		for (; numBits1 > bitOffset; bitOffset = 8) {
			buffer[bytePos] &= ~bitMaskOut[bitOffset];
			buffer[bytePos++] |= (value >> (numBits1 - bitOffset)) & bitMaskOut[bitOffset];

			numBits1 -= bitOffset;
		}
		if (numBits1 == bitOffset) {
			buffer[bytePos] &= ~bitMaskOut[bitOffset];
			buffer[bytePos] |= value & bitMaskOut[bitOffset];
		} else {
			buffer[bytePos] &= ~(bitMaskOut[numBits1] << (bitOffset - numBits1));
			buffer[bytePos] |= (value & bitMaskOut[numBits1]) << (bitOffset - numBits1);
		}
	}
	
	public void writeBits(boolean flag) {
		writeBits(1, flag ? 1 : 0);
	}

	public final void finishBitAccess() {
		offset = (bitPosition + 7) / 8;
	}

	public byte buffer[] = null;
	public int offset = 0;
	public int bitPosition = 0;

	private static int bitMaskOut[] = new int[32];
	static {
		for (int i = 0; i < 32; i++)
			bitMaskOut[i] = (1 << i) - 1;
	}

	private void ensureCapacity(int len) {
		if ((offset + len + 1) >= buffer.length) {
			byte[] oldBuffer = buffer;
			int newLength = (buffer.length * 2);
			buffer = new byte[newLength];
			System.arraycopy(oldBuffer, 0, buffer, 0, oldBuffer.length);
			ensureCapacity(len);
		}
	}

	public ISAACRandomGen packetEncryption = null;

	public void putFrameSizeByte(int start) {
		int size = offset - start;
		buffer[offset - size - 1] = (byte) size;
	}

	public void putFrameSizeShort(int start) {
		int size = offset - start;
		buffer[offset - size - 2] = (byte) (size >> 8);
		buffer[offset - size - 1] = (byte) size;
	}

}
