package com.venenatis.game.cache;

import org.openrs.cache.Cache;
import org.openrs.cache.tools.BasicByteUnpacker;

import com.venenatis.game.cache.definitions.AnyRevObjectDefinition;
import com.venenatis.game.cache.definitions.osrs.CachedObjectDefinition;
import com.venenatis.game.cache.definitions.r317.ObjectDefinition317;
import com.venenatis.game.cache.fs.CacheArchive;
import com.venenatis.game.cache.fs.CacheManager;

import java.io.IOException;

/**
 * Created by Jak on 13/06/2017.
 */
public class OpenRsUnpacker {

    private final static BasicByteUnpacker[] idx = new BasicByteUnpacker[17];

    private static Cache cache = null;
    public static CacheManager hyperionCache = null;
    private static boolean openRS = false;

    public static void unpack() {
        if (!openRS) {
            hyperionCache = new CacheManager("./data/osrs124/");
            int count = hyperionCache.getReferenceTables()[2].getArchiveInfomation()[6].getArchiveEntryInfomation().length;
            CachedObjectDefinition.objectDefinitions = new CachedObjectDefinition[count];
            System.out.println("Objects in cache: "+count);
            return;
        }

        // open RS not in use
        /*try {
            cache = new Cache(FileStore.open("./data/osrscache/"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            // Unpack object definitions from cache
            idx[2] = new BasicByteUnpacker(cache, 2);

            // Initiate arrays
            CachedObjectDefinition.objectDefinitions = new CachedObjectDefinition[idx[2].getArchive()[6].size()];
            CachedItemDefinition.definitions = new CachedItemDefinition[idx[2].getArchive()[10].size()];
            CachedNpcDefinition.npcDefinitions = new CachedNpcDefinition[idx[2].getArchive()[9].size()];

            System.out.printf("[OpenRS] Cached loaded using the Open-RS cache library! Objects:%s | items:%s | npcs:%s%n", CachedObjectDefinition.objectDefinitions.length,
                    CachedItemDefinition.definitions.length, CachedNpcDefinition.npcDefinitions.length);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    static CacheArchive[][] lol = new CacheArchive[16][2000];

    public static byte[] readData(int idx, int archive, int file) {
        byte[] data = null;
        try {
            if (!openRS) {
                if (lol[idx][archive] == null) {
                    lol[idx][archive] = hyperionCache.getArchive(idx, archive);
                }
                data = lol[idx][archive].getChildData(file);
            }
            else if (cache != null) { // using openRS
                if (idx < OpenRsUnpacker.idx.length) {
                    if (OpenRsUnpacker.idx[idx] == null)
                        OpenRsUnpacker.idx[idx] = new BasicByteUnpacker(cache, idx);
                    data = OpenRsUnpacker.idx[idx].getDefinition(archive, file).array();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static AnyRevObjectDefinition objectdef(int object) {
        return cache != null || hyperionCache != null ? CachedObjectDefinition.forId(object) : ObjectDefinition317.get(object);
    }
}
