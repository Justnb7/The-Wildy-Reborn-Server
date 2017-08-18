package com.venenatis.game.content.skills.cooking;

import com.venenatis.game.cache.definitions.AnyRevObjectDefinition;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.input.InputAmount;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;


/**
 * The cooking skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @version 2.0 @date 06-03-2017, updated on 18-8-2017
 */
public class Cooking extends Task {
	
	/**
	 * Action buttons
	 */
	private static final int[][] ACTION_BUTTON = { { 53152, 1 }, { 53151, 5 }, {53150, -1}, {53150, -1} };
	
	/**
	 * Pulls the cookable data
	 */
	private final Cookables cookables;
	
	/**
	 * The player performing the cooking skill
	 */
	private Player player;
	
	/**
	 * The dish being cooked
	 */
	private int used;

	/**
	 * The object we're trying to use
	 */
	private int usedOn;

	/**
	 * 
	 */
	private int amountToCook;

	/**
	 * The task
	 * @param player
	 *         The player cooking the cookable
	 * @param delay
	 *         The timer of the task, a.k.a the delay
	 * @param cookables
	 *         The cookable data
	 */
	public Cooking(Player player, Cookables data, int used, int usedOn, int amount) {
		super(player, 3, true, StackType.NEVER_STACK, BreakType.ON_MOVE);
		this.player = player;
		cookables = data;
		this.used = used;
		this.usedOn = usedOn;
		amountToCook = amount;
	}

	/**
	 * * Have we passed all requirements?
	 * 
	 * @param player
	 *            The player to check
	 * @param cookable
	 *            The cookable item from the [{@code Cookables}
	 * @param used
	 *            The food being cooked
	 * @param usedOn
	 *            Are we trying to cook the food on a stove, range or fire
	 *            perhaps?
	 * @return
	 */
	private static boolean meetsRequirements(Player player, Cookables cookable, int used, int usedOn) {
		int cookingLevel = player.getSkills().getLevel(Skills.COOKING);
		if (cookingLevel < cookable.getLvl()) {
			player.getActionSender().sendMessage("You need a cooking level of " + cookable.getLvl() + " to cook " + Item.getDefinition(used).getName() + ".");
			player.getActionSender().removeAllInterfaces();
			return false;
		}
		if (!player.getInventory().contains(used)) {
			player.getActionSender().sendMessage("You have ran out of food to cook");
			return false;
		}
		return true;
	}
	
	/**
	 * Can we start cooking yet?
	 * 
	 * @param player
	 *            The player attempting to cook
	 * @param cookable
	 *            The cookable item
	 * @param object
	 *            The object we're trying to cook on
	 * @param amount
	 *            The amount we want to cook
	 */
	public static void attemptCooking(Player player, int cookable, int object, int amount) {
		Cookables data = Cookables.forId(cookable);
		if (data == null) {
			return;
		}
		if (!meetsRequirements(player, data, cookable, object)) {
			return;
		}

		World.getWorld().schedule(new Cooking(player, data, cookable, object, amount));
		player.getActionSender().removeAllInterfaces();
	}
	
	/**
	 * Opens the cooking interface
	 * 
	 * @param player
	 *            The player starting the cooking skill
	 * @param usedOn
	 *            The item used on the cooking object
	 * @param used
	 *            The food being cooked
	 */
	public static void showInterface(Player player, GameObject usedOn, Item used) {
		if (used == null || Cookables.forId(used.getId()) == null) {
			player.getActionSender().sendMessage("You cannot cook this!");
			return;
		}

		player.getActionSender().sendChatBoxInterface(1743);
		player.getActionSender().sendItemOnInterface(13716, 250, used.getId());
		player.getActionSender().sendString("\\n\\n\\n\\n\\n" + used.getDefinition().getName(), 13717);

		player.getAttributes().put("cookingobject", Integer.valueOf(usedOn.getId()));
		player.getAttributes().put("cookingitem", Integer.valueOf(used.getId()));
	}
	
	/**
	 * We start cooking based on button clicks, each click has a different
	 * action
	 * 
	 * @param player
	 *            The player cooking
	 * @param buttonId
	 *            The action button
	 * @return
	 */
	public static boolean cook(Player player, int buttonId) {
		int amount = 0;
		for (int buttonIndex = 0; buttonIndex < ACTION_BUTTON.length; buttonIndex++) {
			if (ACTION_BUTTON[buttonIndex][0] == buttonId) {
				amount = ACTION_BUTTON[buttonIndex][1];
				break;
			}
		}
		if (amount == 0) {
			return false;
		}
		if (buttonId == 53152 || buttonId == 53153) {
			attemptCooking(player, ((Integer) player.getAttributes().get("cookingitem")).intValue(), ((Integer) player.getAttributes().get("cookingobject")).intValue(), amount);
		} else if(buttonId == 53149) {
			attemptCooking(player, ((Integer) player.getAttributes().get("cookingitem")).intValue(), ((Integer) player.getAttributes().get("cookingobject")).intValue(), player.getInventory().getAmount((int) player.getAttributes().get("cookingitem")));
		} else {
			player.getActionSender().sendInput((InputAmount) input -> {
				attemptCooking(player, ((Integer) player.getAttributes().get("cookingitem")).intValue(), ((Integer) player.getAttributes().get("cookingobject")).intValue(), input);
			});
		}
		return true;
	}

	/**
	 * Is the object a cookable object
	 * 
	 * @param object
	 *            The object we're trying to use
	 * @return
	 */
	public static boolean isCookableObject(GameObject object) {
		if (object == null) {
			return false;
		}

		AnyRevObjectDefinition def = AnyRevObjectDefinition.get(object.getId());

		if (def == null || def.getName() == null) {
			return false;
		}

		String name = def.getName().toLowerCase();

		return name.equals("range") || name.equals("fire") || name.contains("oven") || name.contains("stove") || name.contains("cooking range") || name.contains("fireplace");
	}
	
	/**
	 * Burning the food based on equipment and levels
	 * @param cook
	 *         The item we're we cooking
	 * @param player
	 *         The player cooking the cookable
	 * @return
	 */
	private boolean burned(Cookables cook, Player player) {
		int level = player.getSkills().getLevel(Skills.COOKING);
		if (player.getEquipment().contains(775)) {
			if (level >= (cook.getBurningLvl() - (cook.getProduct() == 391 ? 0 : 6)))
				return false;
		}
		int levelsToStopBurn = cook.getBurningLvl() - level;
		if (levelsToStopBurn > 20) {
			levelsToStopBurn = 20;
		}
		return Utility.getRandom(34) <= levelsToStopBurn;
	}

	@Override
	public void execute() {
		if (!meetsRequirements(player, cookables, used, usedOn) || player == null || !player.isActive()) {
			stop();
			return;
		}
		player.playAnimation(Animation.create(883));

		if ((player.getSkills().getLevel(Skills.COOKING) >= cookables.getBurningLvl()) ? false : burned(cookables, player)) {
			player.getInventory().remove(new Item(cookables.getRawItem(), 1));
			player.getInventory().add(new Item(cookables.getBurntId(), 1));
			player.getActionSender().sendMessage("Oops.. you have accidentally burnt a " + ItemDefinition.get(cookables.getRawItem()).getName().toLowerCase() + "");
		} else {
			player.getInventory().remove(new Item(cookables.getRawItem(), 1));
			player.getInventory().add(new Item(cookables.getProduct(), 1));
			player.getActionSender().sendMessage("You successfully cook the " + ItemDefinition.get(cookables.getRawItem()).getName().toLowerCase() + ".");
			player.getSkills().addExperience(Skills.COOKING, cookables.getXp());
		}
		amountToCook -= 1;
		if (amountToCook == 0)
			stop();
	}
	
}
