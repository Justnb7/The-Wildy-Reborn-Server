package cache.definitions;

import cache.OpenRsUnpacker;

/**
 * Created by Jak on 13/06/2017.
 *
 * Holds fields common between 317 and OSRS so theres not a fuckfest differentiating between object definitions when loading clipping.
 */
public abstract class AnyRevObjectDefinition {

    public abstract int getWalkToFlag();

    public abstract boolean ignoreAlt();

    public abstract boolean clips();

    public abstract boolean roofclips();

    public abstract boolean projectileClipped();

    public static AnyRevObjectDefinition get(int object) {
        AnyRevObjectDefinition r = OpenRsUnpacker.objectdef(object);
        if (r == null) {
            System.err.println("BAD! "+object);
        }
        return r;
    }

    public abstract String getName();

    public abstract boolean hasName();

    public abstract int xLength();

    public abstract int yLength();

    public abstract  boolean hasActions();

    public abstract String[] getActions();

    public abstract int getId();

    public abstract boolean rangableObject();
}
