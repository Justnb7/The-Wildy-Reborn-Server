package com.venenatis.game.model.entity.player.controller;

import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;

public abstract class Controller {

    public abstract boolean canAttackNPC();

    public abstract boolean canAttackPlayer(Player player, Player opponent);

    public abstract boolean canClickButton(int button);
    
    public abstract boolean canClickEntity(Entity entity);
    
    public abstract boolean canClickObject(int object);
    
    public abstract boolean canInteract();

    public abstract boolean canDrink();

    public abstract boolean canDrop(int item);

    public abstract boolean canEat();

    public abstract boolean canEquip(int item, int slot);

    public abstract boolean canLogout();

    public abstract boolean canMove();

    public abstract boolean canPickup(int item);

    public abstract boolean canPray(Prayers prayer);

    public abstract boolean canSave();

    public abstract boolean canTalk();

    public abstract boolean canTeleport();

    public abstract boolean canTrade();

    public abstract boolean canUnequip(int item, int slot);

    public abstract boolean canUseSpecial(Player player);

    public abstract boolean isSafe();

    public abstract void onDeath(Player player);
    
    public abstract boolean canCommand();
    
    public abstract void onWalk(Player player);

    public abstract void onExit(Player player);

    public abstract void onLogout(Player player);

    public abstract void onStartup(Player player);

    public abstract void onTeleport(Player player);

    public abstract void process(Player player);
    
    public abstract void onStep(Player player);

    @Override
    public abstract String toString();

}
