package com.model.game.item.equipment;

import java.util.Arrays;

import com.model.utility.json.definitions.ItemDefinition;

public enum EquipmentSlot {
    HAT(0),
    CAPE(1),
    AMULET(2),
    WEAPON(3),
    CHEST(4),
    SHIELD(5),
    LEGS(7),
    HANDS(9),
    FEET(10),
    RING(12),
    ARROWS(13);

    private final int id;

    private EquipmentSlot(int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }


    public static EquipmentSlot get(int id) {
        return Arrays.stream(values()).filter(equipment -> equipment.id == id).findFirst().orElse(null);
    }

    public static EquipmentSlot get(String name) {
        return Arrays.stream(values()).filter(equipment -> equipment.name().equals(name)).findFirst().orElse(null);
    }
    
    public ItemDefinition getDefinition() {
		return ItemDefinition.forId(id);
	}
    
}