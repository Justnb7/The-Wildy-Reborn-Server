package oscache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 
 * @author Richard(Flamable)
 */
public class CacheFile {

	private ByteBuffer tempBuffer = ByteBuffer.allocateDirect(520);

	private int index;
	private FileChannel indexChannel;
	private FileChannel dataChannel;
	private int maxSize;

	public CacheFile(int index, FileChannel dataChannel, FileChannel indexChannel, int maxSize) {
		this.index = index;
		this.dataChannel = dataChannel;
		this.indexChannel = indexChannel;
		this.maxSize = maxSize;
	}

	public ByteBuffer get(int file) {
		try {
			if (file * 6 + 6 > indexChannel.size()) {
				return null;
			}
			tempBuffer.position(0).limit(6);
			indexChannel.read(tempBuffer, file * 6);
			tempBuffer.flip();
			int size = Buffers.getMediumInt(tempBuffer);
			int block = Buffers.getMediumInt(tempBuffer);

			if (size < 0 || size > maxSize) {
				return null;
			}
			if (block <= 0 || block > dataChannel.size() / 520) {
				return null;
			}
			ByteBuffer fileBuffer = ByteBuffer.allocate(size);
			int remaining = size;
			int chunk = 0;
			int blockLen = file <= 0xffff ? 512 : 510;
			int headerLen = file <= 0xffff ? 8 : 10;
			while (remaining > 0) {
				if (block == 0) {
					return null;
				}
				int blockSize = remaining > blockLen ? blockLen : remaining;
				tempBuffer.position(0).limit(blockSize + headerLen);
				dataChannel.read(tempBuffer, block * 520);
				tempBuffer.flip();

				final int currentFile = file <= 65535 ? (tempBuffer.getShort() & 0xffff) : tempBuffer.getInt();
				final int currentChunk = tempBuffer.getShort() & 0xffff;
				final int nextBlock = Buffers.getMediumInt(tempBuffer);
				final int currentIndex = tempBuffer.get() & 0xff;

				if (file != currentFile || chunk != currentChunk || index != currentIndex) {
					return null;
				}
				if (nextBlock < 0 || nextBlock > dataChannel.size() / 520) {
					return null;
				}

				fileBuffer.put(tempBuffer);
				remaining -= blockSize;
				block = nextBlock;
				chunk++;
			}

			fileBuffer.flip();
			return fileBuffer;
		} catch (IOException ex) {
			return null;
		}
	}

}
