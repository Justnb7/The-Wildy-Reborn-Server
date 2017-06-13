package clipmap;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.model.game.World;
import com.model.game.location.Location;
import com.model.game.object.GameObject;
import com.model.utility.Utility;
import cache.io.r317.ByteStream;

public class MapLoading {
	
	/**
	 * The logger for the class
	 */
	private static final Logger LOGGER = Logger.getLogger(MapLoading.class.getSimpleName());

	public static void load() {
		try {
			File f = new File("./data/map/map_index");
			byte[] buffer = new byte[(int) f.length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(f));
			dis.readFully(buffer);
			dis.close();
			ByteStream in = new ByteStream(buffer);
			int size = in.length() / 6;
			in.readUnsignedWord();
			LOGGER.info(Utility.format(size) + " Maps about to load...");
			int[] regionIds = new int[size];
			int[] mapGroundFileIds = new int[size];
			int[] mapObjectsFileIds = new int[size];
			int successfull = 0;
			for (int i = 0; i < size; i++) {
				regionIds[i] = in.getUShort();
				mapGroundFileIds[i] = in.getUShort();
				mapObjectsFileIds[i] = in.getUShort();
			}
			for (int i = 0; i < size; i++) {
				byte[] file1 = getBuffer(new File("./data/map/mapdata/" + mapObjectsFileIds[i] + ".gz"));
				byte[] file2 = getBuffer(new File("./data/map/mapdata/" + mapGroundFileIds[i] + ".gz"));
				if (file1 == null || file2 == null) {
					continue;
				}
				try {
					loadMaps(regionIds[i], new ByteStream(file1), new ByteStream(file2));
					successfull++;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error loading map region: " + regionIds[i] + ", ids: " + mapObjectsFileIds[i] + " and " + mapGroundFileIds[i]);
				}
			}

			LOGGER.info(Utility.format(successfull) + " Maps have been loaded successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadMaps(int regionId, ByteStream str1, ByteStream str2) {
		//System.out.println("Clipload loading region "+regionId);
		int rX = (regionId >> 8) * 64;
		int rY = (regionId & 0xff) * 64;
		int[][][] someArray = new int[4][64][64];
		for (int i = 0; i < 4; i++) {
			for (int i2 = 0; i2 < 64; i2++) {
				for (int i3 = 0; i3 < 64; i3++) {
					while (true) {
						int v = str2.getUByte();
						if (v == 0) {
							break;
						} else if (v == 1) {
							str2.skip(1);
							break;
						} else if (v <= 49) {
							str2.skip(1);
						} else if (v <= 81) {
							someArray[i][i2][i3] = v - 49;
						}
					}
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			for (int i2 = 0; i2 < 64; i2++) {
				for (int i3 = 0; i3 < 64; i3++) {
					if ((someArray[i][i2][i3] & 1) == 1) {
						int height = i;
						if ((someArray[1][i2][i3] & 2) == 2) {
							height--;
						}
						if (height >= 0 && height <= 3) {
							Region.addClipping( rX + i2, rY + i3, height, 0x200000);
						}
					}
				}
			}
		}
		int objectId = -1;
		int incr;
		while ((incr = str1.getUSmart()) != 0) {
			objectId += incr;
			int location = 0;
			int incr2;
			while ((incr2 = str1.getUSmart()) != 0) {
				location += incr2 - 1;
				int localX = (location >> 6 & 0x3f);
				int localY = (location & 0x3f);
				int height = location >> 12;
				int objectData = str1.getUByte();
				int type = objectData >> 2;
				int direction = objectData & 0x3;
				if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64) {
					continue;
				}
				if ((someArray[1][localX][localY] & 2) == 2) {
					height--;
				}
				if (height >= 0 && height <= 3) {
					GameObject obj = new GameObject(objectId, rX + localX, rY + localY, height, direction, type);
					Region.addObject(obj);
					World.getWorld().regions.getRegionByLocation(Tile.create(obj.getX(), obj.getY(), obj.getHeight())).addObject(obj);
				}
			}
		}
		System.out.println("Region "+regionId+" has "+World.getWorld().regions.getRegionByLocation(Tile.create(rX+1, rY+1, 0)).getGameObjects().size()+" map objects.");
	}

	public static byte[] getBuffer(File f) throws Exception {
		if (!f.exists()) {
			return null;
		}
		byte[] buffer = new byte[(int) f.length()];
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		dis.readFully(buffer);
		dis.close();
		byte[] gzipInputBuffer = new byte[999999];
		int bufferlength = 0;
		GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(buffer));
		do {
			if (bufferlength == gzipInputBuffer.length) {
				System.out.println("Error inflating data.\nGZIP buffer overflow.");
				break;
			}
			int readByte = gzip.read(gzipInputBuffer, bufferlength, gzipInputBuffer.length - bufferlength);
			if (readByte == -1) {
				break;
			}
			bufferlength += readByte;
		} while (true);
		byte[] inflated = new byte[bufferlength];
		System.arraycopy(gzipInputBuffer, 0, inflated, 0, bufferlength);
		buffer = inflated;

		gzip.close();

		if (buffer.length < 10) {
			return null;
		}
		return buffer;
	}
}