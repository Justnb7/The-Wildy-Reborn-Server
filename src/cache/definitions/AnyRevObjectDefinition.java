package cache.definitions;

import cache.OpenRsUnpacker;
import cache.definitions.osrs.CachedObjectDefinition;
import cache.definitions.r317.ObjectDefinition317;

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

    // BELOW ISNT NEEDED FOR CLIPPING JUST FOR SHARED REFERENCE
    public String name;
    public String[] actions;
    public boolean hasActions;
    public int id;

    public static AnyRevObjectDefinition get(int object) {
        return OpenRsUnpacker.cache != null ? CachedObjectDefinition.forId(object) : ObjectDefinition317.get(object);
    }

    public String getName() {
        return name;
    }

    public boolean hasName() {
        return name != null && name.length() > 1;
    }

    public int xLength() {
        return sizeX;
    }

    public int yLength() {
        return sizeY;
    }

    public int actionCount() {
    	int count = 0;
    	for(int i = 0; i < actions.length; i++) {
    		if(actions[i] == null)
    			continue;
    		if(!actions[i].equalsIgnoreCase("null") || !actions[i].equalsIgnoreCase("hidden"))
    			count++;
    	}
        return count;
    }

    public boolean hasActions() {
        if (this instanceof ObjectDefinition317)
            return hasActions || actions != null;

        for(int i = 0; i < actions.length; i++) {
            if(actions[i] == null)
                continue;
            if(!actions[i].equalsIgnoreCase("null") || !actions[i].equalsIgnoreCase("hidden"))
                return true;
        }
        return false;
    }

    public String[] getActions() {
        String[] allActions = new String[actions.length];
        for(int i = 0; i < actions.length; i++) {
            if(actions[i] == null)
                continue;
            allActions[i] = actions[i];
        }
        return allActions;
    }

    public boolean rangableObject() {
        int[] rangableObjects = {3007, 980, 4262, 14437, 14438, 4437, 4439, 3487, 3457};
        for (int i : rangableObjects) {
            if (i == id) {
                return true;
            }
        }
        if (name != null) {
            final String name1 = name.toLowerCase();
            String[] rangables = {"altar", "pew", "log", "stump", "stool", "sign", "cart", "chest", "rock", "bush", "hedge", "chair", "table", "crate", "barrel", "box", "skeleton", "corpse", "vent", "stone", "rockslide"};
            for (String i : rangables) {
                if (name1.contains(i)) {
                    return true;
                }
            }
        }
        return false;
    }
}
