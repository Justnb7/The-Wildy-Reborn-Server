package com.venenatis.game.action.impl.actions;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.action.impl.ProductionAction;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;

/**
 * Created by Tim on 11/29/2015.
 */
public class WaterSourceAction extends ProductionAction {

    private final Fillables fillables;

    public WaterSourceAction(Entity entity, Fillables fillables) {
        super(entity);
        this.fillables = fillables;
    }


    public enum Fillables {
        VIAL(229, 227),

        WATERING_CAN(5331, 5340);

        private final int empty;
        private final int full;

        Fillables(int empty, int full) {
            this.empty = empty;
            this.full = full;
        }

        private static Map<Integer, Fillables> fillables = new HashMap<Integer, Fillables>();

        public static Fillables forId(int item) {
            return fillables.get(item);
        }

        static {
            for(Fillables fill : Fillables.values()) {
                fillables.put(fill.empty, fill);
            }
        }

        public int getEmpty() {
            return empty;
        }

        public int getFull() {
            return full;
        }
    }

    @Override
    public int getCycleCount() {
        return 1;
    }

    @Override
    public int getProductionCount() {
    	Player player = (Player)getEntity();
        return player.getInventory().getAmount(fillables.getEmpty());
    }

    @Override
    public Item[] getRewards() {
        return new Item[] {new Item(fillables.getFull())};
    }

    @Override
    public Item[] getConsumedItems() {
        return new Item[] {new Item(fillables.getEmpty())};
    }

    @Override
    public int getSkill() {
        return 0;
    }

    @Override
    public int getRequiredLevel() {
        return 0;
    }

    @Override
    public double getExperience() {
        return 0;
    }

    @Override
    public String getLevelTooLowMessage() {
        return "";
    }

    @Override
    public String getSuccessfulProductionMessage() {
        return "";
    }

    @Override
    public Animation getAnimation() {
        return Animation.create(832);
    }

    @Override
    public Graphic getGraphic() {
        return null;
    }

    @Override
    public boolean canProduce() {
        return true;
    }
}