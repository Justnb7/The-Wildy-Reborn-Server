package cache.io.osrs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Buffer utility class.
 * 
 * @author Graham Edgecombe
 *
 */
public class Buffers {

	/**
	 * Reads a RuneScape string from a buffer.
	 * 
	 * @param buf
	 *            The buffer.
	 * @return The string.
	 */
	public static String getRS2String(ByteBuffer buf) {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while (buf.hasRemaining() && (b = buf.get()) != 0) {
			bldr.append((char) b);
		}
		return bldr.toString();
	}
	
	/**
	 * Reads a character from a buffer.
	 * @param buf
	 * @return
	 */
	public static String getCString(ByteBuffer buf) {
		StringBuilder bldr = new StringBuilder();
		char c;
		while((c = (char) buf.get()) != 0) {
			bldr.append(c);
		}
		return bldr.toString();
	}
	
	/**
	 * Reads a null terminated string from a byte buffer.
	 * 
	 * @param buffer
	 *            The buffer.
	 * @return The string.
	 */
	public static String readString(ByteBuffer buffer) {
		StringBuilder bldr = new StringBuilder();
		while (buffer.hasRemaining()) {
			byte b = buffer.get();
			if (b == 0) {
				break;
			}
			bldr.append((char) b);
		}
		return bldr.toString();
	}
	
	/**
	 * Reads a RuneScape string from the specified
	 * <code>InputStream</code>.
	 * @param in The input stream.
	 * @return The string.
	 * @throws IOException if an I/O error occurs, such as the stream closing.
	 */
	public static String readSavedAccountString(InputStream in) throws IOException {
		StringBuilder bldr = new StringBuilder();
		while(true) {
			int b = in.read();
			if(b == -1 || b == 0) {
				break;
			} else {
				bldr.append((char) ((byte) b));
			}
		}
		return bldr.toString();
	}

	public static int getMediumInt(ByteBuffer buffer) {
		return ((buffer.get() & 0xff) << 16) | ((buffer.get() & 0xff) << 8)
				| (buffer.get() & 0xff);
	}

	public static int getSmartInt(ByteBuffer buffer) {
		if ((buffer.get(buffer.position()) ^ 0xffffffff) <= -1) {
			int i = buffer.getShort() & 0xffff;
			if (i == 32767) {
				return -1;
			}

			return i;
		}
		return buffer.getInt() & 0x7fffffff;
	}

}
