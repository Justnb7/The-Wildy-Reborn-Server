package com.venenatis.game.model.combat.npcs.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.equipment.PoisonType;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.impl.StoppingTick;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class LizardmanShaman extends AbstractBossCombat {

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	private static final Animation JUMP_ATTACK = Animation.create(7192);
	private static final Animation JUMP_HIDE = Animation.create(7152);
	private static final Animation JUMP_DOWN = Animation.create(6946);
	
	private static final int SPAWN_ID = 6768;

	List<NPC> minions = new ArrayList<>();
	
	private int hitDelay;
	private int randomHit;
	private int gfxDelay;
	private int clientSpeed;
	
	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}

		NPC npc = (NPC) attacker;
		CombatStyle style = CombatStyle.MELEE;

		if (npc.getLocation().distance(victim.getLocation()) <= 3) {
			switch (random.nextInt(10)) {
			case 1:
			case 2:
			case 3:
				style = CombatStyle.RANGE;
				break;
			case 4:
				style = CombatStyle.MAGIC;
				break;
			case 6:
				style = CombatStyle.ACID_ATTACK;
				break;
			case 7:
				style = CombatStyle.JUMP_ATTACK;
				break;
			}
		} else {
			switch (random.nextInt(10)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 7:
				style = CombatStyle.RANGE;
				break;
			case 5:
			case 10:
				style = CombatStyle.JUMP_ATTACK;
				break;
			case 6:
				style = CombatStyle.MAGIC;
				break;
			case 8:
			case 9:
				style = CombatStyle.ACID_ATTACK;
				break;
			}
		}

		switch (style) {
		case MELEE:
			handleLizardMelee(npc, victim);
			break;
		case RANGE:
			handleLizardRange(npc, victim);
			break;
		case JUMP_ATTACK:
			handleLizardJumpAttack(npc, victim);
			break;
		case MAGIC:
			handleLizardMagicAttack(npc, victim);
			break;
		case ACID_ATTACK:
			handleLizardAcidAttack(npc, victim);
			break;
		default:
			break;
		}
		
		npc.getCombatState().setAttackDelay(6);

	}
	
	private void handleLizardAcidAttack(NPC npc, Entity victim) {
		Player player = (Player)victim;
		
		npc.playAnimation(Animation.create(7193));
		if(npc.getLocation().isWithinDistance(npc, victim, 1)) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if(npc.getLocation().isWithinDistance(npc, victim, 5)) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else if(npc.getLocation().isWithinDistance(npc, victim, 8)) {
			clientSpeed = 110;
			gfxDelay = 120;
		} else {
			clientSpeed = 130;
			gfxDelay = 140;
		}
		hitDelay = (gfxDelay / 20) - 1;
		npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim.getCentreLocation(), 1293, 45, 50, clientSpeed, 100, 35, victim.getProjectileLockonIndex(), 10, 48));
		
		victim.playGraphic(Graphic.create(1294));
		
		int damage = hasShayzienArmour(player) ? 0 : Utility.random(25, 30);
		
		if(victim.getPoisonDamage().get() < 1 && damage > 0 && random.nextInt(10) < 7 && !victim.isPoisoned()) {
			victim.poison(PoisonType.DEFAULT_NPC, npc);
		}
		
		victim.take_hit(npc, randomHit, CombatStyle.ACID_ATTACK).send(hitDelay);
	}
	
	private void handleLizardMagicAttack(NPC npc, Entity victim) {
		if (victim.isPlayer()) {
			npc.playAnimation(Animation.create(7157));
			for (int i = 0; i < Utility.random(3, 4); i++) {
				minions.add(new NPC(SPAWN_ID, victim.getLocation().closestFreeTileOrSelf(npc.getLocation(), npc.getWidth(), npc.getZ()), 1));
			}
		}
		minions.forEach(World.getWorld()::register);
		minions.forEach(n -> {
			n.setFollowing(victim);
			World.getWorld().schedule(new Task(1) {
				private int ticks = 10;
				private int curTicks;
	
				@Override
				public void execute() {
					if (curTicks >= ticks) {
						this.stop();
						n.playAnimation(Animation.create(7159));
						n.playGraphic(Graphic.create(1295, 10, 0));
						World.getWorld().schedule(new Task(1) {
	
							@Override
							public void execute() {
								this.stop();
								if (n.getLocation().distance(victim.getLocation()) <= 3) {
									victim.take_hit(n, Utility.random(4, 9), CombatStyle.MAGIC, true);
								}
								World.getWorld().unregister(n);
							}
						});
					}
					curTicks++;
				}
			});
		});
		minions.clear();
	}
	
	private void handleLizardJumpAttack(NPC npc, Entity victim) {
		Location toJump = victim.getLocation();
		npc.setAttribute("attack", false);
		npc.playAnimation(JUMP_HIDE);
		World.getWorld().schedule(new StoppingTick(2) {
			@Override
			public void executeAndStop() {
				npc.setVisible(false); // removes from client view
			}
		});
		World.getWorld().schedule(new StoppingTick(6) {
			@Override
			public void executeAndStop() {
				npc.removeAttribute("attack");
				npc.setVisible(true);
				Location destination = toJump.closestFreeTileOrSelf(toJump, npc.getWidth(), npc.getZ());
				npc.teleport(destination); // just sets new location, doesn't do any npc updating changes (npc doesn't support TELEPORT like players do)
				npc.playAnimation(JUMP_DOWN);
				npc.faceEntity(victim);
				npc.getRegion().getPlayers().stream().filter(Objects::nonNull).filter(i -> i.getLocation().distance(toJump) <= 2).forEach(i -> i.take_hit(npc, Utility.random(20, 25), CombatStyle.JUMP_ATTACK, true));
				
			}
		});
	}
	
	private void handleLizardRange(NPC npc, Entity victim) {
		npc.playAnimation(new Animation(npc.getAttackAnimation()));

		if(npc.getLocation().isWithinDistance(npc, victim, 1)) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if(npc.getLocation().isWithinDistance(npc, victim, 5)) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else if(npc.getLocation().isWithinDistance(npc, victim, 8)) {
			clientSpeed = 110;
			gfxDelay = 120;
		} else {
			clientSpeed = 130;
			gfxDelay = 140;
		}
		hitDelay = (gfxDelay / 20) - 1;
		npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim.getCentreLocation(), 1291, 45, 50, clientSpeed, 100, 35, victim.getProjectileLockonIndex(), 10, 48));
		
		randomHit = Utility.random(npc.getDefinition().getMaxHit());
		
		victim.take_hit(npc, randomHit, CombatStyle.RANGE).send(hitDelay);
	}

	private void handleLizardMelee(NPC npc, Entity victim) {
		npc.playAnimation(random.nextInt(1) == 0 ? Animation.create(7192) : Animation.create(7158));
		hitDelay = 1;
		randomHit = Utility.random(npc.getDefinition().getMaxHit());

		victim.take_hit(npc, randomHit, CombatStyle.MELEE).send(1);
	}
	
	public boolean hasShayzienArmour(Player victim) {
		return victim.getEquipment() != null && victim.getEquipment().contains(13377) && victim.getEquipment().contains(13378)
				&& victim.getEquipment().contains(13379) && victim.getEquipment().contains(13380) && victim.getEquipment().contains(13381);
	}

	@Override
	public int distance(Entity attacker) {
		return 7;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}
