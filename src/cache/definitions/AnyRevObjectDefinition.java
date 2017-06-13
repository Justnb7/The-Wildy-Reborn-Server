package cache.definitions;

/**
 * Created by Jak on 13/06/2017.
 *
 * Holds fields common between 317 and OSRS so theres not a fuckfest differentiating between object definitions when loading clipping.
 */
public class AnyRevObjectDefinition {
    public boolean projectileClipped;
    public int sizeX = 1;
    public int sizeY;
    public int osrs_clipType = 2;
    public boolean r317_cliptype;
    public boolean ignoreClipOnAlternativeRoute;

    public boolean clips() {
        return r317_cliptype || osrs_clipType != 0;
    }

    public boolean roofclips() {
        return r317_cliptype || osrs_clipType == 1;
    }

    public boolean projectileClipped() {
        return projectileClipped;
    }

    public boolean unclipped() {
        return ignoreClipOnAlternativeRoute;
    }
}
