package com.venenatis.game.world.pathfinder.clipmap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.venenatis.game.cache.OpenRsUnpacker;
import com.venenatis.game.cache.definitions.osrs.CachedObjectDefinition;
import com.venenatis.game.cache.fs.CacheManager;
import com.venenatis.game.cache.io.r317.ByteStream;
import com.venenatis.game.location.Location;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;

public class MapLoading {


	private static String getFileNameWithoutExtension(String fileName) {
		File tmpFile = new File(fileName);
		tmpFile.getName();
		int whereDot = tmpFile.getName().lastIndexOf('.');
		if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
			return tmpFile.getName().substring(0, whereDot);
		}
		return "";
	}

	public static Map<Integer, int[]> textKeys() throws Exception {
		File path = new File("./data/osrs124xtea/");
		if (!path.exists())
			throw new Exception("No xtea folder found for release ");
		File[] xteas = path.listFiles();
		Map<Integer, int[]> keys = new HashMap<>();
		for (File xteaFile : xteas) {
			int region = Integer.parseInt(getFileNameWithoutExtension(xteaFile.getName()));
			BufferedReader reader = new BufferedReader(new FileReader(xteaFile));
			int[] xtea = new int[4];
			String line;
			int i = 0;
			while ((line = reader.readLine()) != null) {
				if (line.equals(""))
					continue;
				xtea[i] = Integer.parseInt(line);
				i++;
			}
			reader.close();
			keys.put(region, xtea);
		}
		return keys;
	}

	public static Map<Integer, int[]> jakxteas() {
		Map<Integer, int[]> keys = new HashMap<>();
		File from = new File("./binary-xtea-dump.bin");

		ByteBuffer buffer = null;
		try {
			buffer = ByteBuffer.wrap(Files.readAllBytes(from.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (buffer.remaining() > 0) {
			int map = buffer.getShort() & 0xFFFF;
			int[] k = new int[4];
			for (int i = 0; i < 4; i++)
				k[i] = buffer.getInt();
			keys.put(map, k);
		}
		return keys;
	}

	public static void main(String[] bla) {
		// dump map_index

		CacheManager hyperionCache = new CacheManager("./data/osrs124/");

		try {
			RandomAccessFile raf = new RandomAccessFile("./map_index_osrs124", "rw");
			for (int regionId = 0; regionId < 16384; regionId++) {
				int regionX = (regionId >> 8) * 64;
				int regionY = (regionId & 0xff) * 64;
				int aX = ((regionX >> 3) / 8), bY = ((regionY >> 3) / 8);
				int mapFile = hyperionCache.getReferenceTables()[5].getArchiveByName("m"+aX+"_"+bY);
				int landscapeFile = hyperionCache.getReferenceTables()[5].getArchiveByName("l"+aX+"_"+bY);
				if (mapFile == -1 || landscapeFile == -1) {
					continue;
				}
				raf.writeShort(regionId);
				raf.writeShort(mapFile);
				raf.writeShort(landscapeFile);
			}
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The logger for the class
	 */
	private static final Logger LOGGER = Logger.getLogger(MapLoading.class.getSimpleName());

	public static void load() {
		try {
			File f = new File("./map_index_osrs124"); // ./data/map/map_index
			byte[] buffer = new byte[(int) f.length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(f));
			dis.readFully(buffer);
			dis.close();
			ByteStream in = new ByteStream(buffer);
			int size = in.length() / 6;
			//in.readUnsignedWord(); // map_index found in usual RSPS.. this one is custom. uses file.length to determine loopcount instead of a header short(aka word)
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
			Map<Integer, int[]> xteaMap = textKeys();
			for (int i = 0; i < size; i++) {
				byte[] file1 = OpenRsUnpacker.hyperionCache.getArchive(5, mapObjectsFileIds[i], xteaMap.getOrDefault(regionIds[i], new int[4])).getData(); // getBuffer(new File("./data/map/mapdata/" + mapObjectsFileIds[i] + ".gz"));
				byte[] file2 = OpenRsUnpacker.hyperionCache.getArchive(5, mapGroundFileIds[i]).getData(); // getBuffer(new File("./data/map/mapdata/" + mapGroundFileIds[i] + ".gz"));
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

					Region.addClipping(obj);
					World.getWorld().regions.getRegionByLocation(Location.create(obj.getX(), obj.getY(), obj.getZ())).addObject(obj);

					if (obj.getId() >= CachedObjectDefinition.objectDefinitions.length) {
						System.err.println("Object id "+obj.getId()+" not supported! at "+obj.getX()+","+obj.getY()+","+obj.getZ()+" type "+obj.getType()+","+obj.getFace());
					}
				}
			}
		}
		//System.out.println("Region "+regionId+" has "+World.getWorld().regions.getRegionByLocation(Tile.create(rX+1, rY+1, 0)).getGameObjects().size()+" map objects.");
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