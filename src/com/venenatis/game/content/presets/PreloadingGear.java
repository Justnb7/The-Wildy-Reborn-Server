package com.venenatis.game.content.presets;
import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.location.Area;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.magic.spell.SpellBook;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.DialogueManager;
import com.venenatis.game.model.entity.player.save.PlayerSave;
import com.venenatis.game.model.entity.player.save.PlayerSave.Type;
import com.venenatis.game.util.NameUtils;
import com.venenatis.game.util.StringUtils;

/**
 * Handles player's preloading gear.
 * 
 * @author Daniel
 *
 */
public class PreloadingGear {

	private final Player player;

	public PreloadingGear(Player player) {
		this.player = player;
	}

	private int viewing;

	private boolean deathOpen;

	private boolean gearBank;

	private String[] presetTitle = new String[6];

	private Item[][] presetInventory = new Item[6][28];

	private Item[][] presetEquipment = new Item[6][15];
	
	private SpellBook[] presetSpellbook = new SpellBook[6];

	private int[][] presetSkill = new int[6][7];

	public void open() {
		open(getViewing() == -1 ? 0 : getViewing());
	}

	public void open(int preset) {

		if (!Area.inSafezone(player)) {
			player.getActionSender().sendMessage("You can only do this in a safe-zone.");
			return;
		}
		
		if (player.getCombatState().inCombat()) {
			player.getActionSender().sendMessage("You can not do this while in combat.");
			return;
		}
		
		if (Area.inDuelArena(player)) {
			player.getActionSender().sendMessage("You can not do this here.");
			return;
		}
		
		if (Area.inF2P(player)) {
			player.getActionSender().sendMessage("You can not do this here.");
			return;
		}
		
		setViewing(preset);
		player.getActionSender().sendItemOnInterface(57058, new Item[] { new Item(6889), new Item(1718), new Item(6964) });
		player.getActionSender().sendItemOnInterface(57035, presetInventory[preset]);
		player.getActionSender().sendItemOnInterface(57036, presetEquipment[preset]);
		player.getActionSender().sendString("<col=E84723>" + presetSkill[preset][0], 57043);
		player.getActionSender().sendString("<col=E84723>" + presetSkill[preset][2], 57043 + 1);
		player.getActionSender().sendString("<col=E84723>" + presetSkill[preset][1], 57043 + 2);
		player.getActionSender().sendString("<col=E84723>" + presetSkill[preset][3], 57043 + 3);
		player.getActionSender().sendString("<col=E84723>" + presetSkill[preset][5], 57043 + 4);
		player.getActionSender().sendString("<col=E84723>" + presetSkill[preset][4], 57043 + 5);
		player.getActionSender().sendString("<col=E84723>" + presetSkill[preset][6], 57043 + 6);
		player.getActionSender().sendString("Preloading Gear", 57002);
		player.getActionSender().sendString("<col=BD23E8>" + getSpellbook(preset), 57040);

		for (int index = 0; index < 6; index++) {
			player.getActionSender().sendString((preset == index ? "<col=ff7000>" : "<col=ff9040>") + "" + (presetTitle[index] == null ? "Empty" : "" + presetTitle[index]), 57024 + index);
		}

		player.getActionSender().sendString("<col=06FF7F>" + NameUtils.formatPrice(getWealth(preset)), 57042);
		player.getActionSender().sendConfig(345, player.getPresets().isDeathOpen() ? 1 : 0);
		player.getActionSender().sendConfig(346, player.getPresets().isGearBank() ? 1 : 0);
		player.getActionSender().sendInterface(57_000);
	}

	public void clear(int preset) {
		if (presetTitle[preset] == null) {
			player.getActionSender().sendMessage("There is nothing for you to clear here.");
			return;
		}
		
		for (int index = 0; index < 7; index++) {
			presetSkill[preset][index] = 0;
		}
		
		for (int index = 0; index < 15; index++) {
			presetEquipment[preset][index] = null;
		}
		
		for (int index = 0; index < 28; index++) {
			presetInventory[preset][index] = null;
		}
		
		presetTitle[preset] = null;
		presetSpellbook[preset] = null;

		player.getActionSender().sendMessage("<col=800000>You have cleared your preset.");
		open(preset);
	}

	public void gearUp(int preset) {
		if (!player.getInterfaceState().isInterfaceOpen(57_000)) {
			return;
		}
		
		if (!Area.inSafezone(player)) {
			player.getActionSender().sendMessage("You can only do this in a safe-zone.");
			return;
		}
		
		if (player.getCombatState().inCombat()) {
			player.getActionSender().sendMessage("You can not do this while in combat.");
			return;
		}
		
		if (Area.inF2P(player)) {
			player.getActionSender().sendMessage("You can not do this here.");
			return;
		}
		
		if (Area.inDuelArena(player)) {
			player.getActionSender().sendMessage("You can not do this here.");
			return;
		}
		
		if (isGearBank()) {
			player.getBank().depositeEquipment(false);
			player.getBank().depositeInventory(false);
		}
		
		if (player.getEquipment().getTakenSlots() != 0) {
			player.getActionSender().sendMessage("<col=800000>Please clear your equipment before doing this.");
			return;
		}
		
		if (player.getInventory().getTakenSlots() != 0) {
			player.getActionSender().sendMessage("<col=800000>Please clear your inventory before doing this.");
			return;
		}
		
		int id = 0;
		
		for (final int level : presetSkill[preset]) {
			player.getSkills().setMaxLevel(id++, level);
		}
		
		boolean missingItems = false;
		
		for (final Item item : presetInventory[preset]) {
			if (item == null) {
				continue;
			}
			if (!player.getRights().isOwner(player)) {
				int removed = player.getBank().remove(item.unnoted(), false);
				if (removed > 0) {
					player.getInventory().add(item.unnoted().getId(), removed, false);
				} else {
					missingItems = true;
				}
			} else {
				player.getInventory().add(item.unnoted(), false);
			}
		}
		
		for (final Item item : presetEquipment[preset]) {
			if (item == null) {
				continue;
			}

			if (!player.getRights().isOwner(player)) {
				int removed = player.getBank().remove(item.unnoted(), false);
				if (removed > 0) {
					player.getEquipment().wear(new Item(item.unnoted().getId(), removed), false, false);
				} else {
					missingItems = true;
				}
			} else {
				player.getEquipment().wear(item.unnoted(), false, false);
			}
		}
		
		if (missingItems) {
			player.getActionSender().sendMessage("Your bank is missing one or more items.");
		}
		
		player.setCombatLevel(player.getSkills().getCombatLevel());
		player.getInventory().refresh();
		player.getEquipment().refresh();
		player.setSpellBook(presetSpellbook[preset]);
		player.getActionSender().setSideBarInterfaces();
		player.getEquipment().updateWeapon();
		player.getActionSender().sendMessage("<col=800000>" + presetTitle[preset] + " has been activated. Go kick some ass!");
	}

	public void setTitle(int preset, String input) {
		presetTitle[preset] = StringUtils.capitalize(input);
		player.getActionSender().sendString("@or2@" + presetTitle[preset], 57024 + preset);
		player.getActionSender().sendMessage("<col=800000>You have set the title of your preset to " + input + ".");
	}
	
	public void uploadCheck(int preset) {
		if (presetTitle[preset] == null) {
			player.getActionSender().sendMessage("<col=800000>Please title your preset before doing this.");
			return;
		}
		
		/*//And then start dialogue
		DialogueManager.start(player, 7);
		//Set dialogue options
		player.setDialogueOptions(new DialogueOptions() {
			@Override
			public void handleOption(Player player, int option) {
				switch(option) {
				case 1:
					//Action 1
					upload(preset);
					break;
				case 2:
					//Action 2
					open(preset);
					break;
				}
			}
		});*/
	}

	public void upload(int preset) {
		presetSpellbook[preset] = player.getSpellBook();
		presetEquipment[preset][1] = player.getEquipment().get(EquipmentConstants.HELM_SLOT);
		presetEquipment[preset][3] = player.getEquipment().get(EquipmentConstants.CAPE_SLOT);
		presetEquipment[preset][4] = player.getEquipment().get(EquipmentConstants.NECKLACE_SLOT);
		presetEquipment[preset][5] = player.getEquipment().get(EquipmentConstants.AMMO_SLOT);
		presetEquipment[preset][6] = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		presetEquipment[preset][7] = player.getEquipment().get(EquipmentConstants.TORSO_SLOT);
		presetEquipment[preset][8] = player.getEquipment().get(EquipmentConstants.SHIELD_SLOT);
		presetEquipment[preset][10] = player.getEquipment().get(EquipmentConstants.LEGS_SLOT);
		presetEquipment[preset][12] = player.getEquipment().get(EquipmentConstants.GLOVES_SLOT);
		presetEquipment[preset][13] = player.getEquipment().get(EquipmentConstants.BOOTS_SLOT);
		presetEquipment[preset][14] = player.getEquipment().get(EquipmentConstants.RING_SLOT);
		for (int index = 0; index < player.getInventory().getSize(); index++) {
			if (player.getInventory().get(index) == null) {
				presetInventory[preset][index] = null;
				continue;
			}
			presetInventory[preset][index] = player.getInventory().get(index).copy();
		}
		for (int index = 0; index < 7; index++) {
			presetSkill[preset][index] = player.getSkills().getMaxLevel(index);
		}
		open(preset);
		PlayerSave.save(player, Type.PRESETS);
	}

	public String getSpellbook(int preset) {
		if (presetSpellbook[preset] == SpellBook.MODERN_MAGICS) {
			return "Modern";
		}
		if (presetSpellbook[preset] == SpellBook.ANCIENT_MAGICKS) {
			return "Ancients";
		}
		if (presetSpellbook[preset] == SpellBook.LUNAR_MAGICS) {
			return "Lunars";
		}
		return "None";
	}

	public long getWealth(int preset) {
		long inventory = 0;
		for (final Item item : presetInventory[preset]) {
			if (item != null) {
				if (inventory > Long.MAX_VALUE - item.getValue()) {
					return Long.MAX_VALUE;
				}
				inventory += item.getValue() * item.getAmount();
			}
		}
		long equipment = 0;
		for (final Item item : presetEquipment[preset]) {
			if (item != null) {
				if (equipment > Long.MAX_VALUE - item.getValue()) {
					return Long.MAX_VALUE;
				}
				equipment += item.getValue() * item.getAmount();
			}
		}
		return inventory + equipment;
	}

	public int getViewing() {
		return viewing;
	}

	public Item[][] getPresetEquipment() {
		return presetEquipment;
	}

	public Item[][] getPresetInventory() {
		return presetInventory;
	}

	public SpellBook[] getPresetSpellbook() {
		return presetSpellbook;
	}

	public String[] getPresetTitle() {
		return presetTitle;
	}

	public void setViewing(int viewing) {
		this.viewing = viewing;
	}

	public void setPresetEquipment(Item[][] presetEquipment) {
		this.presetEquipment = presetEquipment;
	}

	public void setPresetInventory(Item[][] presetInventory) {
		this.presetInventory = presetInventory;
	}

	public void setPresetSpellbook(SpellBook[] presetSpellbook) {
		this.presetSpellbook = presetSpellbook;
	}

	public void setPresetTitle(String[] presetTitle) {
		this.presetTitle = presetTitle;
	}

	public int[][] getPresetSkill() {
		return presetSkill;
	}

	public void setPresetSkill(int[][] presetSkill) {
		this.presetSkill = presetSkill;
	}

	public boolean isDeathOpen() {
		return deathOpen;
	}

	public void setDeathOpen(boolean deathOpen) {
		this.deathOpen = deathOpen;
	}

	public boolean isGearBank() {
		return gearBank;
	}

	public void setGearBank(boolean gearBank) {
		this.gearBank = gearBank;
	}
}