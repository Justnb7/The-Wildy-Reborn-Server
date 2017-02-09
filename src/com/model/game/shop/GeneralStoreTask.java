package com.model.game.shop;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.model.game.item.Item;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class GeneralStoreTask extends ScheduledTask {

    private final Shop container;

    private List<Item> list;

    public GeneralStoreTask(Shop container, List<Item> list) {
        super(100, false);
        super.attach(Shop.GENERAL_STORE);
        this.container = container;
        this.list = list;
    }

    @Override
    public void execute() {
        int random = Utility.inclusiveRandom(1, 5);
        for (int i = 0; i < random; i++) {
            if(list.size() == 0) {
                this.stop();
                return;
            }
            Item item = Utility.randomElement(list);
            if(item == null)
                break;
            container.getContainer().remove(item);
        }
        int size = container.getContainer().size();
        container.getPlayers().stream().filter(Objects::nonNull).forEach(p -> p.getItems().sendItemsOnInterface(3900, container.getContainer().container(), size));
        list = getAlienItems(container);
        if(list.size() == 0)
            this.stop();
    }

    private static List<Item> getAlienItems(Shop shop) {
        List<Item> list = new LinkedList<>();
        for (Item item : shop.getContainer()) {
            if (item == null)
                continue;
            if (!shop.getItemCache().containsKey(item.id))
                list.add(item);
        }
        return list;
    }

}
