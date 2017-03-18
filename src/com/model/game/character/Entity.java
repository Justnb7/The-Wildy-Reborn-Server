package com.model.game.character;

import com.google.common.base.Preconditions;
import com.model.Server;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.effect.CombatEffect;
import com.model.game.character.combat.effect.impl.RingOfRecoil;
import com.model.game.character.combat.pvm.PlayerVsNpcCombat;
import com.model.game.character.combat.pvp.PlayerVsPlayerCombat;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.ActionSender;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.music.sounds.MobAttackSounds;
import com.model.game.character.player.content.music.sounds.PlayerSounds;
import com.model.game.character.player.minigames.pest_control.PestControl;
import com.model.game.location.Position;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick van Elderen
 * @author Jak
 */
public abstract class Entity {

	public Entity followTarget;

    public void setFollowing(Entity following) {
        this.followTarget = following;
    }

    public enum EntityType {
		PLAYER, NPC,
	}

	public int absX;
	public int absY;
	public int lastX;
	public int lastY;
	public int heightLevel;
	public transient Object distanceEvent;
	private boolean registered;
	public int poisonDamage;
	public int infection;
	public boolean infected;
	public Hit primary;
	public Hit secondary;
	public boolean hitUpdateRequired;
	public boolean hitUpdateRequired2;
	public Animation anim;
	public boolean animUpdateRequired;
	public Graphic gfx;
	public boolean gfxUpdateRequired;
	public boolean forcedChatUpdateRequired;
	public boolean updateRequired = true;
	public boolean appearanceUpdateRequired = true;
	public boolean faceUpdateRequired = false;
	public int entityFaceIndex = -1;
	public int faceTileX = -1, faceTileY = -1;

	/**
	 * The characters combat type, MELEE by default
	 */
	private CombatType combatType = CombatType.MELEE;
	
	/**
	 * The permanent attributes map. Items set here are only removed when told to.
	 */
	protected Map<String, Object> attributes = new HashMap<String, Object>();

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getEntityType().hashCode();
		result = prime * result + this.getIndex();
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Entity))
			return false;
		Entity other = (Entity) obj;
		if (this.getEntityType() != other.getEntityType())
			return false;
		if (this.getIndex() != other.getIndex())
			return false;
		return true;
	}

	/**
	 * The mobile character is visible
	 */
	private boolean visible = true;

	public abstract Hit decrementHP(Hit hit);

	public void clear() {
		primary = null;
		secondary = null;
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		updateRequired = false;
	}

	private boolean inCombat;

	public boolean inCombat() {
		return inCombat;
	}

	public void setInCombat(boolean inCombat) {
		this.inCombat = inCombat;
	}

	/**
	 * Actually apply the hit. Makes it show in Player Updating and also reduces your HITPOINTS (can kill you)
	 */
	public void damage(Hit... hits) {
		Preconditions.checkArgument(hits.length >= 1 && hits.length <= 4);

		switch (hits.length) {
		case 1:
			//System.out.println("hits "+hits[0].getDamage());
			sendDamage(hits[0]);
			break;
		case 2:
			sendDamage(hits[0], hits[1]);
			break;
		case 3:
			sendDamage(hits[0], hits[1], hits[2]);
			break;
		case 4:
			sendDamage(hits[0], hits[1], hits[2], hits[3]);
			break;
		}
	}

	private void primaryDamage(Hit hit) {
		primary = decrementHP(hit);
		updateRequired = true;
		hitUpdateRequired = true;
	}

	private void secondaryDamage(Hit hit) {
		secondary = decrementHP(hit);
		updateRequired = true;
		hitUpdateRequired2 = true;
	}

	private void sendDamage(Hit hit) {
		if (hitUpdateRequired) {
			secondaryDamage(hit);
			return;
		}
		primaryDamage(hit);
	}

	private void sendDamage(Hit hit, Hit hit2) {
		sendDamage(hit);
		secondaryDamage(hit2);
	}

	/**
	 * Sets the first two hitsplats, then the second ONE TICK later
	 */
	private void sendDamage(Hit hit, Hit hit2, Hit hit3) {
		sendDamage(hit, hit2); // two hitsplats

		Server.getTaskScheduler().submit(new ScheduledTask(1, false) {
			@Override
			public void execute() {
				this.stop();
				if (!registered) { // still online/active
					return;
				}
				sendDamage(hit3); // a single hitsplat
			}
		});
	}

	/**
	 * Sets two hitsplats for this update cycle, then the other two 1 tick later
	 */
	private void sendDamage(Hit hit, Hit hit2, Hit hit3, Hit hit4) {
		sendDamage(hit, hit2);

		Server.getTaskScheduler().submit(new ScheduledTask(1, false) {
			@Override
			public void execute() {
				this.stop();
				if (!registered) {
					return;
				}
				sendDamage(hit3, hit4);
			}
		});
	}
	
	public boolean hasAttribute(String string) {
		return attributes.containsKey(string);
	}
	
	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T removeAttribute(String key) {
		return (T) attributes.remove(key);
	}

	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @return The old value.
	 */
	public void removeAllAttributes() {
		if (attributes != null && attributes.size() > 0 && attributes.keySet().size() > 0) {
			attributes = new HashMap<String, Object>();
		}
	}

	/**
	 * Sets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>   The type of the value.
	 * @param key   The key.
	 * @param value The value.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T setAttribute(String key, T value) {
		return (T) attributes.put(key, value);
	}

	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return (T) attributes.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key, T fail) {
		if (attributes.containsKey(key))
			return (T) attributes.get(key);
		else
			return (T) fail;
	}

	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @return The value.
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public int getX() {
		return absX;
	}

	public int getY() {
		return absY;
	}

	public int getLastX() {
		return lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public void setAbsX(int absX) {
		this.lastX = this.absX;
		this.absX = absX;
	}

	public void setAbsY(int absY) {
		this.lastY = this.absY;
		this.absY = absY;
	}
	
	public void setAbsZ(int absZ) {
		this.heightLevel = absZ;
	}

	public boolean isRegistered() {
		return registered;
	}

	protected void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public Player asPlayer() {
		return (Player) this;
	}

	public Npc asNpc() {
		return (Npc) this;
	}

	/**
	 * The mobile character is visible
	 * 
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets this MobileCharacters visibility
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Determines the characters {@link CombatType}
	 * 
	 * @return The {@link CombatType} this character is attacking with
	 */
	public CombatType getCombatType() {
		return combatType;
	}

	/**
	 * Sets the characters new {@link CombatType}
	 * 
	 * @param type
	 *            The {@link CombatType} this character is attacking with
	 */
	public void setCombatType(CombatType type) {
		this.combatType = type;
	}

	/**
	 * Processes the entity every 600ms
	 */
	public abstract void process();
	
	/**
	 * Is this entity a player.
	 */
	public abstract boolean isPlayer();
	
	/**
	 * Is this entity an NPC.
	 */
	public abstract boolean isNPC();
	
	public Npc toNPC() {
		return isNPC() ? (Npc) this : null;
	}
	
	public Player toPlayer() {
		return isNPC() ? null : (Player) this;
	}
	
	/**
	 * Returns the packet sender for the entity, mainly used for players
	 * 
	 * @return
	 */
	public abstract ActionSender getActionSender();
	
	public Hit take_hit(Entity attacker, int damage) {
		return take_hit(attacker, damage, null);
	}

	// Since damage gets reduced you need to add XP after this method.
	public Hit take_hit(Entity attacker, int damage, CombatType combat_type) {
		return take_hit(attacker, damage, combat_type, true);
	}

	public Hit take_hit(Entity attacker, int damage, CombatType combat_type, boolean applyInstantly) {

		// ALWAYS: FIRST APPLY DAMAGE REDUCTIONS, ABSORBS ETC. Protection pray/ely.
		// The entity taking damage is a player. 
		if (this.isPlayer()) {
			Player player_me = (Player) this;
			player_me.putInCombat(attacker.getIndex()); // we're taking a hit. we can't logout for 10s.
			
			// The victim (this) has protection prayer enabled.
			if (combat_type != null) {
				// 40% Protection from player attacks, 100% protection from Npc attacks
				double prayProtection = attacker.isPlayer() ? 0.6D : 0.0D;
				if (combat_type == CombatType.MELEE && player_me.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
					damage *= prayProtection;
				}
				if (combat_type == CombatType.RANGED && player_me.isActivePrayer(Prayers.PROTECT_FROM_MISSILE)) {
					damage *= prayProtection;
				}
				if (combat_type == CombatType.MAGIC && player_me.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
					damage *= prayProtection;
				}
			}
			
			// TODO special reduction effects can go here, like Ely
			if (player_me.playerEquipment[player_me.getEquipment().getShieldId()] == 12817) {
				if (Utility.getRandom(100) > 30 && damage > 0) {
					damage *= .75;
				}
			}

			if (player_me.isTeleporting()) {
				damage = 0;
			}

		} else if (this.isNPC()) {
			Npc victim_npc = (Npc) this;
			// You can't hit over an Npcs current health. Recent update on 07 means you can in PVP though.
			if (victim_npc.currentHealth - damage < 0) {
				damage = victim_npc.currentHealth;
			}
			if (attacker.isPlayer())
				PlayerVsNpcCombat.kraken((Player)attacker, victim_npc, damage);
			if (victim_npc.npcId == 319) {
				if (attacker.isNPC() || (attacker.isPlayer() && !PlayerVsNpcCombat.isWearingSpear(((Player)attacker)))) {
					damage /= 2;
				}
			}
			if (victim_npc.npcId == 5535) {
				damage = 0;
			}
			if (combat_type == CombatType.MELEE && (victim_npc.npcId == 2267 || victim_npc.npcId == 2266)) {
				if (attacker.isPlayer())
					((Player)attacker).message("The dagannoth is currently resistant to that attack!");
				damage = 0;
			}
			//Rex and Supreme do not take range damage
			if (combat_type == CombatType.RANGED && (victim_npc.npcId == 2265 || victim_npc.npcId == 2267)) {
				((Player)attacker).message("The dagannoth is currently resistant to that attack!");
				damage = 0;
			}
			if (combat_type == CombatType.MAGIC && (victim_npc.npcId == 2265 || victim_npc.npcId == 2266)) {
				((Player)attacker).message("The dagannoth is currently resistant to that attack!");
				damage = 0;
			}
			if (combat_type == CombatType.MAGIC && victim_npc.npcId == 5535) {
				damage = 0;
			}
		}
		
		// At this point damage accurately reduced by stuff like prots/ely. 
		// Now we can use it to give XP/add to npcs damage tracker.

		if (isPlayer()) {
			Player me = (Player)this;

			if (damage > 0) {
				// Trigger veng once the damage has been reduced by effects/protection prayers
				if (me.hasVengeance()) {
					me.getCombat().vengeance(attacker, damage, 1);
				}

				RingOfRecoil recoil = new RingOfRecoil();
				if (recoil.isExecutable(me)) {
					if (attacker.isPlayer())
						recoil.execute(me, (Player)attacker, damage);
					else
						recoil.execute(me, (Npc)attacker, damage);
				}

			}

			if (attacker.isPlayer()) {
				Player pAttacker = (Player)attacker;
				CombatEffect.applyRandomEffect(pAttacker, me, damage);
				pAttacker.getCombat().applySmite(me, damage);

				for (int i : PlayerVsPlayerCombat.poisonous) {
					if (pAttacker.playerEquipment[pAttacker.getEquipment().getWeaponId()] == i) {
						if (me.isSusceptibleToPoison() && Utility.getRandom(4) == 0) {
							me.setPoisonDamage((byte) 6);
						}
					}
				}
			}
		}

		// This Entity is an npc taking damage from a player. 
		if (this.isNPC() && attacker.isPlayer()) {
			Player attacker_player = (Player)attacker;
			Npc victim_npc = (Npc) this;
			victim_npc.retaliate(attacker);
			victim_npc.addDamageReceived(attacker_player.getName(), damage);
			if (Boundary.isIn(attacker_player, PestControl.GAME_BOUNDARY)) {
				attacker_player.pestControlDamage += damage;
			}
			MobAttackSounds.sendBlockSound(attacker_player, victim_npc.getId()); // TODO use npc not npcid
		} else if (isPlayer() && attacker.isPlayer()) {
			((Player)this).addDamageReceived(((Player)attacker).getName(), damage);
		}

		// Update hit instance since we've changed the 'damage' value
		Hit hit = new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED).type(combat_type);

		// NOTE: If not instantly applied, use EventManager.event(2) { entity.damage(hit) }
		if (applyInstantly) {
			PlayerSounds.sendBlockOrHitSound((Player)this, damage > 0);
			this.damage(hit);
		}
		// Returning hit: might be helpful in the future. For chaining. Such as hit.x().y()..
		return hit;
	}
	
	/**
	 * The default, i.e. spawn, location.
	 */
	public static final Position DEFAULT_LOCATION = new Position(3087, 3495, 0);

    /**
     * The index of the entity
     */
    private int index;
    
    /**
	 * The current location.
	 */
	private Position location;
	
	/**
	 * The teleportation target.
	 */
	private Position teleportTarget = null;

	/**
	 * The last known map region.
	 */
	private Position lastKnownRegion = this.getPosition();

    private final EntityType type;

    public Entity(EntityType type) {
    	setLocation(DEFAULT_LOCATION);
		this.lastKnownRegion = location;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public EntityType getEntityType() {
        return type;
    }
    
    /**
	 * Sets the last known map region.
	 * 
	 * @param lastKnownRegion
	 *            The last known map region.
	 */
	public void setLastKnownRegion(Position lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
	}

	/**
	 * Gets the last known map region.
	 * 
	 * @return The last known map region.
	 */
	public Position getLastKnownRegion() {
		return lastKnownRegion;
	}
	
	/**
	 * Makes this entity face a position.
	 * 
	 * @param position
	 *            The position to face.
	 */
	public void face(Entity entity, Position position) {
		//Faces the player
		if(entity.getEntityType() == EntityType.PLAYER) {
			faceTileX = 2 * position.getX() + 1;
			faceTileY = 2 * position.getY() + 1;
		//Faces the npc
		} else if(entity.getEntityType() == EntityType.NPC) {
			faceTileX = position.getX();
			faceTileY = position.getY();
		}
		faceUpdateRequired = true;
		updateRequired = true;
	}
	
	/**
	 * Sets the entity facing index
	 * @param e
	 *   The entity
	 */
	public void faceEntity(Entity e) {
		//forceChat("face: "+e);
		if (e == null || e == this) {
			//System.out.println("wtf");
			this.resetFace();
			return;
		}
		// If WE are an npc, faceIndex is 'raw' - not +32k. 
		// If we're a player, facing players = 32k+pid.. facing npcs= raw index
		entityFaceIndex = e.clientIndex();
		faceUpdateRequired = true;
		updateRequired = true;
		//System.out.println((this.isNPC() ? "npc" : "player")+" FACING "+e.isNPC()+" facd req to -> "+entityFaceIndex);
	}

	public abstract int clientIndex();

	/**
	 * Resets the facing position.
	 */
	public void resetFace() {
		this.entityFaceIndex = -1;
		faceUpdateRequired = true;
		updateRequired = true;
		//System.out.println(this.isNPC()+ " why "+System.currentTimeMillis() / 1000);
	}
	
	/**
	 * Checks if this entity has a target to teleport to.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasTeleportTarget() {
		return teleportTarget != null;
	}

	/**
	 * Gets the teleport target.
	 * 
	 * @return The teleport target.
	 */
	public Position getTeleportTarget() {
		return teleportTarget;
	}

	/**
	 * Sets the teleport target.
	 * 
	 * @param teleportTarget
	 *            The target location.
	 */
	public void setTeleportTarget(Position teleportTarget) {
		this.teleportTarget = teleportTarget;
	}

	/**
	 * Resets the teleport target.
	 */
	public void resetTeleportTarget() {
		this.teleportTarget = null;
	}
	
	/**
	 * Sets the current location.
	 * 
	 * @param location
	 *            The current location.
	 */
	public void setLocation(Position location) {
		this.location = location;
	}

	/**
	 * Gets the current location.
	 * 
	 * @return The current location.
	 */
	public Position getPosition() {
		return location;
	}
	
	/**
	 * The text to display with the force chat mask.
	 */
	private String forcedChat;
	
	/**
	 * Creates the force chat mask.
	 *
	 * @param message
	 */
	public void forceChat(String message) {
		forcedChat = message;
		forcedChatUpdateRequired = true;
		updateRequired = true;
	}
	
	/**
	 * Creates the force chat mask.
	 *
	 * @param message
	 */
	public void setForceChat(String message) {
		forcedChat = message;
	}

	/**
	 * Gets the message to display with the force chat mask.
	 *
	 * @return The message to display with the force chat mask.
	 */
	public String getForcedChatMessage() {
		return forcedChat;
	}

	public void playGraphics(Graphic graphic) {
		gfx = graphic;
		this.gfxUpdateRequired = true;
		updateRequired = true;
	}
	
	/**
	 * Animations
	 *            The animation id.
	 */
	public void playAnimation(Animation animation) {
		// Purpose: anims are unique to npcs to this shops the npc deforming after transforming.
		if (this.isNPC() && ((Npc)this).transformUpdateRequired) { 
			return;
		}
		anim = animation;
		this.animUpdateRequired = true;
		updateRequired = true;
	}
	
	public int frozenForTicks, refreezeTicks;
	public Entity frozenBy;
	
	// example: barrage = freeze(20s/.6 ticks = 33 ticks)
	public void freeze(int ticks) {
		if (this.refreezeTicks > 0) // we're immune
			return;
		this.frozenForTicks = ticks;
		this.refreezeTicks = ticks + 3; // 3 ticks of immuity
	}
	
	public void frozenBy(Entity mager) {
		frozenBy = mager;
	}

	public void frozen_process() {
		// Reduce timers
		if (frozenForTicks > 0)
			this.frozenForTicks--;
		if (this.refreezeTicks > 0)
			this.refreezeTicks--;
		
		check_should_unfreeze();
		
	}

	private void check_should_unfreeze() {
		// Purpose: if whoever froze you is off screen (or null, they logged off) you get unfrozen.
		
		if (frozenBy == null)
			return;
		int opX = frozenBy.absX;
		int opY = frozenBy.absY;
		
		boolean out_of_dist = !goodDistance(getX(), getY(), opX, opY, 20);
		
		if (!frozenBy.registered || out_of_dist) {
			this.frozenForTicks = 0;
			this.refreezeTicks = 0;
			frozenBy = null;
		}
	}

	public boolean frozen() {
		return this.frozenForTicks > 0;
	}


	public int distanceToPoint(int pointX, int pointY) {
		return (int) Math.sqrt(Math.pow(getX() - pointX, 2) + Math.pow(getY() - pointY, 2));
	}

	public boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return ((objectX - playerX <= distance && objectX - playerX >= -distance) && (objectY - playerY <= distance && objectY - playerY >= -distance));
	}
	
	/**
	 * Gets the width of the entity.
	 * @return The width of the entity.
	 */
	public abstract int getWidth();
	
	/**
	 * Gets the width of the entity.
	 * @return The width of the entity.
	 */
	public abstract int getHeight();

	public abstract boolean isDead();
	
}
