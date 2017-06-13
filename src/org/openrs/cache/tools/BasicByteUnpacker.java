package org.openrs.cache.tools;

import org.openrs.cache.Archive;
import org.openrs.cache.Cache;
import org.openrs.cache.Container;
import org.openrs.cache.ReferenceTable;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Jak on 13/06/2017.
 */
public class BasicByteUnpacker {

    /**
     * The file system cache;
     */
    private final Cache cache;

    /**
     * The cache's archive file.
     */
    private final Archive[] archive;

    /**
     * The definitions cache index of where the config definitions are stored
     * at.
     */
    private final int type;

    /**
     * Main constructor.
     * @param cache The file system cache.
     * @param type The definitions cache index.
     * @throws IOException If an io exception occurred.
     */
    public BasicByteUnpacker(Cache cache, int type) throws IOException {
        this.cache = cache;
        this.type = type;
        Container tableContainer = Container.decode(cache.getStore().read(255, type));
        ReferenceTable table = ReferenceTable.decode(tableContainer.getData());
        int archiveCount = table.capacity();
        archive = new Archive[archiveCount];
        for (int i = 0; i < archiveCount; i++) {
            try {
                archive[i] = Archive.decode(cache.read(type, i).getData(), table.getEntry(i).size());
                //System.out.println("idx "+type+" archive "+i+" has "+table.getEntry(i).size()+" entries.");
            } catch (Exception e) {
                System.out.println("[OpenRS][ERROR] Failed to read index: "+type+" Archive: "+i);
            }
        }
    }

    /**
     * Gets the definition from the archive and converts it into
     * {@link ByteBuffer}. This is used to read definitions.
     * @param archiveId The definition Id.
     * @return The definition converted into {@link ByteBuffer}.
     * @throws IOException
     */
    public ByteBuffer getDefinition(int archiveId, int entryId) throws IOException {
        if (archiveId >= this.archive.length)
            throw new IndexOutOfBoundsException("[OpenRS] Error loading Archive \"" + archiveId + "\"");
        if (entryId >= archive[archiveId].size())
            return null;
        return archive[archiveId].getEntry(entryId);
    }

    public int getDefinitionsCount(int maxEntrysArchive) {
        int s = (archive.length - 1) * maxEntrysArchive;
        s += archive[archive.length - 1].size();
        return s;
    }

    /**
     * Gets the file system cache.
     * @return The file system cache.
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Gets the cache's archive file.
     * @return The cache's archive file.
     */
    public Archive[] getArchive() {
        return archive;
    }

    /**
     * Gets the cache index of where the definition's are stored at.
     * @return The cache index.
     */
    public int getCacheIndex() {
        return type;
    }
}
