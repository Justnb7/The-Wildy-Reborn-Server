package cache;

import cache.definitions.osrs.CachedItemDefinition;
import cache.definitions.osrs.CachedNpcDefinition;
import cache.definitions.osrs.CachedObjectDefinition;
import org.openrs.cache.Cache;
import org.openrs.cache.FileStore;
import org.openrs.cache.tools.BasicByteUnpacker;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Jak on 13/06/2017.
 */
public class OpenRsUnpacker {

    private static BasicByteUnpacker idx2 = null;
    public static Cache cache = null;

    public static Cache unpack() {
        try {
            cache = new Cache(FileStore.open("./data/osrscache/"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            // Unpack object definitions from cache
            idx2 = new BasicByteUnpacker(cache, 2);

            // Initiate arrays
            CachedObjectDefinition.objectDefinitions = new CachedObjectDefinition[idx2.getArchive()[6].size()];
            CachedItemDefinition.definitions = new CachedItemDefinition[idx2.getArchive()[10].size()];
            CachedNpcDefinition.npcDefinitions = new CachedNpcDefinition[idx2.getArchive()[9].size()];

            System.out.printf("[OpenRS] Cached loaded using the Open-RS cache library! Objects:%s | items:%s | npcs:%s%n", CachedObjectDefinition.objectDefinitions.length,
                    CachedItemDefinition.definitions.length, CachedNpcDefinition.npcDefinitions.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cache;
    }

    public static byte[] readData(int idx, int archive, int file) {
        byte[] data = null;
        try {
            switch(idx) {
                case 2:
                    data = idx2.getDefinition(archive, file).array();
                    break;
                default:throw new NullPointerException("Index "+idx+" not opened!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
