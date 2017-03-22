package com.model.game.character.player.dialogue.impl.chat;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;

/**
 * @author lare96 <http://github.com/lare96>
 */
public class DeathShopDialogue2 extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(Type.NPC, 5567, Expression.EVIL, "Hello, how may I help you?");
        setPhase(0);
    }

    @Override
    public void next() {
        if (getPhase() == 0 || getPhase() == 1) {
            send(Type.CHOICE, null, "I'd like to see my death shop.", "Can I hear how the store works again please? (Will delete all items).");
        }
        if (getPhase() == 2) {
            stop();
        }
    }

    @Override
    protected void select(int index) {
        if (index == 1) {
            player.deathShop.openShop(player);
            setPhase(0);
        } else if(index == 2) {
            player.deathShop.getContainer().clear();
            player.deathShopChat = false;
            player.getActionSender().sendMessage("Your death shop has been cleared and disabled.");
            stop();
            setPhase(0);
        }
    }
}

