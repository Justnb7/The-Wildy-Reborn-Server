package com.model.game.character.player.skill.smithing;

import java.util.Arrays;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendChatBoxInterface;
import com.model.game.character.player.packets.encode.impl.SendString;
import com.model.game.character.player.packets.encode.impl.SendInterfaceModel;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.SkillHandler;
import com.model.game.item.Item;
import com.model.task.ScheduledTask;

/**
 * @author Ochroid | Scott
 */

public class Smelting extends SkillHandler {

    private static int COPPER = 436, TIN = 438, IRON = 440, COAL = 453, MITH = 447, ADDY = 449, RUNE = 451, GOLD = 444, SILVER = 442;
    private final static int[] SMELT_FRAME = { 2405, 2406, 2407, 2409, 2410, 2411, 2412, 2413 };
    private final static int[] SMELT_BARS = { 2349, 2351, 2355, 2353, 2357, 2359, 2361, 2363, 2355 };

    public enum SmeltingData {
        BRONZE_BAR(1, 6, 2349, new Item(COPPER), new Item(TIN)),
        IRON_BAR(15, 12, 2351, new Item(IRON)),
        STEEL_BAR(20, 17, 2353, new Item(IRON), new Item(COAL, 2)),
        MITHRIL_BAR(50, 30, 2359, new Item(MITH), new Item(COAL, 4)),
        ADAMANT_BAR(70, 37, 2361, new Item(ADDY), new Item(COAL, 6)),
        RUNITE_BAR(85, 50, 2363, new Item(RUNE), new Item(COAL, 8)),
        SILVER_BAR(20, 13, 2355, new Item(SILVER)),
        GOLD_BAR(40, 22, 2357, new Item(GOLD));

        public final int level, experience, product;
        public final Item[] items;

        private SmeltingData(int level, int experience, int product, Item... items) {
            this.level = level;
            this.experience = experience;
            this.product = product;
            this.items = items;
        }

        public static SmeltingData get(int index) {
            return Arrays.stream(values()).filter(s -> s.ordinal() == index).findFirst().orElse(null);
        }
    }

    /**
     * Sends the interface
     * 
     * @param c
     */
    public static void startSmelting(Player player, int object) {
        for (int j = 0; j < SMELT_FRAME.length; j++) {
            player.write(new SendInterfaceModel(SMELT_FRAME[j], 150, SMELT_BARS[j]));
        }
        player.write(new SendString(" ", 7441));
        player.write(new SendChatBoxInterface(2400));
        player.playerisSmelting = true;
    }

    /**
     * Sets the amount of bars that can be smelted. (EG. 5,10,28 times)
     * 
     * @param c
     * @param amount
     */
    public static void doAmount(Player player, int amount, int bartype) {
        player.doAmount = amount;
        smeltBar(player, bartype - 1);
    }

    /**
     * Main method. Smelting
     * 
     * @param c
     */
    private static void smeltBar(final Player player, int bartype) {
        SmeltingData data = SmeltingData.get(bartype);
        if (data == null)
            return;
        if (player.getSkills().getLevel(Skills.SMITHING) < data.level) {
            player.getDialogueHandler().sendStatement(player, "You need a smithing level of at least " + data.level + " in order smelt this bar.");
            return;
        }

        if (!player.getItems().playerHasItems(data.items)) {
            int slot = 0;
            StringBuilder sb = new StringBuilder("You need ");
            
            for (Item item : data.items) {
                sb.append(item.amount + " " + item.getDefinition().getName());
                slot++;
                if (slot != data.items.length)
                    sb.append(" and ");

            }
            sb.append(" to make this bar.");
            player.write(new SendClearScreen());
            player.write(new SendMessagePacket(sb.toString()));
            return;
        }

        if (player.isSkilling) {
            return;
        }

        player.isSkilling = true;
        player.stopPlayerSkill = true;
        player.write(new SendClearScreen());
        player.playAnimation(Animation.create(899));
        player.isSkilling = true;
        Server.getTaskScheduler().schedule(new ScheduledTask(6) {
            @Override
            public void execute() {
                if (!player.isSkilling) {
                    this.stop();
                    resetSmelting(player);
                    return;
                }
                deleteTime(player);
                player.getItems().deleteItems(data.items);
                player.write(new SendMessagePacket("You receive an " + player.getItems().getItemName(data.product).toLowerCase() + "."));
                player.getSkills().addExperience(Skills.SMITHING, data.experience);
                player.getItems().addItem(data.product, 1);// item
                if (!player.getItems().playerHasItems(data.items)) {
                    player.write(new SendMessagePacket("You don't have enough ores to continue smelting!"));
                    resetSmelting(player);
                    stop();
                    return;
                }
                if (player.doAmount <= 0) {
                    resetSmelting(player);
                    stop();
                    return;
                }
                if (!player.isSkilling) {
                    resetSmelting(player);
                    stop();
                    return;
                }
                if (!player.stopPlayerSkill) {
                    resetSmelting(player);
                    stop();
                }

            }
        }.attach(player));
        Server.getTaskScheduler().schedule(new ScheduledTask(6) {
            @Override
            public void execute() {
                if (!player.isSkilling) {
                    resetSmelting(player);
                    stop();
                    return;
                }
                player.playAnimation(Animation.create(899));
                if (!player.stopPlayerSkill) {
                    stop();
                }
            }
        }.attach(player));
    }

    /**
     * Gets the index from DATA for which bar to smelt
     */
    public static void getBar(Player player, int i) {
        switch (i) {
        case 15147: // bronze (1)
            doAmount(player, 1, 1);// (player,amount,Index in data)
            break;
        case 15146: // bronze (5)
            doAmount(player, 5, 1);
            break;
        case 10247: // bronze (10)
            doAmount(player, 10, 1);
            break;
        case 9110:// bronze (X)
            doAmount(player, 28, 1);
            break;

        case 15151: // iron (1)
            doAmount(player, 1, 2);
            break;
        case 15150: // iron (5)
            doAmount(player, 5, 2);
            break;
        case 15149: // iron (10)
            doAmount(player, 10, 2);
            break;
        case 15148:// Iron (X)
            doAmount(player, 28, 2);
            break;

        case 15159: // Steel (1)
            doAmount(player, 1, 3);
            break;
        case 15158: // Steel (5)
            doAmount(player, 5, 3);
            break;
        case 15157: // Steel (10)
            doAmount(player, 10, 3);
            break;
        case 15156:// Steel (X)
            doAmount(player, 28, 3);
            break;

        case 29017: // mith (1)
            doAmount(player, 1, 4);
            break;
        case 29016: // mith (5)
            doAmount(player, 5, 4);
            break;
        case 24253: // mith (10)
            doAmount(player, 10, 4);
            break;
        case 16062:// Mith (X)
            doAmount(player, 28, 4);
            break;

        case 29022: // Addy (1)
            doAmount(player, 1, 5);
            break;
        case 29020: // Addy (5)
            doAmount(player, 5, 5);
            break;
        case 29019: // Addy (10)
            doAmount(player, 10, 5);
            break;
        case 29018:// Addy (X)
            doAmount(player, 28, 5);
            break;

        case 29026: // RUNE (1)
            doAmount(player, 1, 6);
            break;
        case 29025: // RUNE (5)
            doAmount(player, 5, 6);
            break;
        case 29024: // RUNE (10)
            doAmount(player, 10, 6);
            break;
        case 29023:// Rune (X)
            doAmount(player, 28, 6);
            break;

        case 15155: // SILVER (1)
            doAmount(player, 1, 7);
            break;
        case 15154: // SILVER (1)
            doAmount(player, 5, 7);
            break;
        case 15153: // SILVER (1)
            doAmount(player, 10, 7);
            break;
        case 15152: // SILVER (1)
            doAmount(player, 28, 7);
            break;

        case 15163: // Gold (1)
            doAmount(player, 1, 8);
            break;

        case 15162: // Gold (1)
            doAmount(player, 5, 8);
            break;

        case 15161: // Gold (1)
            doAmount(player, 10, 8);
            break;

        case 15160: // Gold (1)
            doAmount(player, 28, 8);
            break;
        /** TODO Finish X Amount and Gold/Silver 5/10/X amounts **/
        }

    }

    /**
     * Resets Smelting
     */
    public static void resetSmelting(Player player) {
        player.isSkilling = false;
        player.stopPlayerSkill = false;
        player.playerisSmelting = false;
        for (int i = 0; i < 7; i++) {
            player.playerSkillProp[13][i] = -1;
        }
    }

}