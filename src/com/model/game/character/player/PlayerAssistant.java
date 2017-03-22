package com.model.game.character.player;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.combat.combat_data.CombatAnimation;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.effect.impl.DragonfireShieldEffect;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.content.BossTracker;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.out.*;
import com.model.game.character.walking.PathFinder;
import com.model.game.item.Item;
import com.model.game.location.Position;
import com.model.utility.Utility;
import com.model.utility.cache.map.Region;
import com.model.utility.json.definitions.ItemDefinition;
import org.omicron.jagex.runescape.CollisionMap;

import java.text.DecimalFormat;

public class PlayerAssistant {

    private final Player player;
    

    public PlayerAssistant(Player Client) {
        this.player = Client;
    }

    public void playerWalk(int x, int y) {
        PathFinder.getPathFinder().findRoute(player, x, y, true, 1, 1);
    }

    public void movePlayer1(int x, int y) {
        player.getMovementHandler().reset();
        player.teleportToX = x;
        player.teleportToY = y;
        requestUpdates();
    }

    /**
     * Objects, add and remove
     */
    public void object(int objectId, int objectX, int objectY, int face, int objectType) {
        Region.addWorldObject(objectId, objectX, objectY, player.heightLevel);
        if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(85);
            player.getOutStream().writeByteC(objectY - (player.getMapRegionY() * 8));
            player.getOutStream().writeByteC(objectX - (player.getMapRegionX() * 8));
            player.getOutStream().writeFrame(101);
            player.getOutStream().writeByteC((objectType << 2) + (face & 3));
            player.getOutStream().writeByte(0);

            if (objectId != -1) { // removing
                player.getOutStream().writeFrame(151);
                player.getOutStream().writeByteS(0);
                player.getOutStream().writeWordBigEndian(objectId);
                player.getOutStream().writeByteS((objectType << 2) + (face & 3));
            }
        }
    }

    public void checkObjectSpawn(int objectId, int objectX, int objectY, int face, int objectType) {
        Region.addWorldObject(objectId, objectX, objectY, player.heightLevel);
        if (player.distanceToPoint(objectX, objectY) > 60) {
            return;
        }
        if (objectId == 1596) {
            CollisionMap.setFlag(0, objectX, objectY, 0);
            CollisionMap.setFlag(0, objectX + 1, objectY, 0);
            CollisionMap.setFlag(0, objectX, objectY + 1, 0);
            CollisionMap.setFlag(0, objectX - 1, objectY, 0);
            CollisionMap.setFlag(0, objectX, objectY - 1, 0);
        }
        if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(85);
            player.getOutStream().writeByteC(objectY - (player.getMapRegionY() * 8));
            player.getOutStream().writeByteC(objectX - (player.getMapRegionX() * 8));
            player.getOutStream().writeFrame(101);
            player.getOutStream().writeByteC((objectType << 2) + (face & 3));
            player.getOutStream().writeByte(0);
            if (objectId != -1) { // removing
                player.getOutStream().writeFrame(151);
                player.getOutStream().writeByteS(0);
                player.getOutStream().writeWordBigEndian(objectId);
                player.getOutStream().writeByteS((objectType << 2) + (face & 3));
            }
        }
    }
        
    public void resetTb() {
        player.teleblockLength = 0;
        player.teleblock.stop();
    }

    public void movePlayer(int x, int y, int h) {
        if (player == null)
            return;
        if (player.isBusy()) {
            return;
        }
		if (!player.lastSpear.elapsed(4000)) {
			player.getActionSender().sendMessage("You're trying to move too fast.");
			return;
		}
        player.getMovementHandler().reset();
        player.teleportToX = x;
        player.teleportToY = y;
        player.teleHeight = h;
        player.setTeleportTarget(Position.create(x, y, h));
        requestUpdates();
        player.getSkillCyclesTask().stop();
        //System.out.println("to "+Arrays.toString(new int[] {x,y,h}));
    }

    public void movePlayer(Position p) {
        movePlayer(p.getX(), p.getY(), p.getZ());
    }
    
    public void resetAutoCast() {
        player.autocastId = 0;
        player.onAuto = false;
        player.autoCast = false;
        player.write(new SendConfigPacket(108, 0));
    }

    /**
     * Following
     */
    public void followPlayer(boolean forCombat, Entity following) {
        int cbDist = player.followDistance;
        if (following == null || following.isDead()) {
            player.setFollowing(null);
            return;
        }
        if (player.frozen()) {
            return;
        }

        if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
            player.setFollowing(null);
            return;
        }

        int otherX = following.getX();
        int otherY = following.getY();

        if (!player.goodDistance(otherX, otherY, player.getX(), player.getY(), 25)) {
            player.setFollowing(null);
            return;
        }

        boolean sameSpot = (player.absX == otherX && player.absY == otherY);
        if (sameSpot) {
            if (Region.getClipping(player.getX() - 1, player.getY(), player.heightLevel, -1, 0)) {
                walkTo(-1, 0);
            } else if (Region.getClipping(player.getX() + 1, player.getY(), player.heightLevel, 1, 0)) {
                walkTo(1, 0);
            } else if (Region.getClipping(player.getX(), player.getY() - 1, player.heightLevel, 0, -1)) {
                walkTo(0, -1);
            } else if (Region.getClipping(player.getX(), player.getY() + 1, player.heightLevel, 0, 1)) {
                walkTo(0, 1);
            }
            return;
        }

        player.faceEntity(following);

        /**
         * Out of combat following, possibly a bug or 2?
         */
        if (!forCombat) {
            int fx = following.lastTile.getX();
            int fy = following.lastTile.getY();

            int delay = (player.getMovementHandler().isMoving() || ((Player)following).getMovementHandler().isMoving()) ? 1
                : (player.walkTutorial + 1 >= Integer.MAX_VALUE ? player.walkTutorial = 0 : player.walkTutorial++);
            int remainder = delay % 2;
            if (remainder == 1) {
                int x = fx - player.getX();
                int y = fy - player.getY();
                playerWalk(player.getX() + x, player.getY() + y);
                return;
            }
        } else {

            boolean goodCombatDistance = player.goodDistance(otherX, otherY, player.getX(), player.getY(), cbDist);
            /*
             * Check for other range weapons which require a distance of 4
             */
            if (goodCombatDistance) {
                player.getMovementHandler().stopMovement();
                return;
            }
            /*
             * Check our regular combat styles for distance
             */
            if (player.getCombatType() == CombatStyle.MELEE && player.goodDistance(otherX, otherY, player.getX(), player.getY(), 1)) {
                if (otherX != player.getX() && otherY != player.getY()) {
                    stopDiagonal(player, otherX, otherY);
                    return;
                } else {
                    player.getMovementHandler().stopMovement();
                    return;
                }
            }

            Position[] locs = { new Position(otherX + 1, otherY, player.getZ()), new Position(otherX - 1, otherY, player.getZ()), new Position(otherX, otherY + 1, player.getZ()),
                    new Position(otherX, otherY - 1, player.getZ()), };

            Position followLoc = null;

            for (Position i : locs) {
                if (followLoc == null || player.getPosition().getDistance(i) < player.getPosition().getDistance(followLoc)) {
                    followLoc = i;
                }
            }
            if (followLoc != null) {
                playerWalk(followLoc.getX(), followLoc.getY());
                player.getMovementHandler().followPath = true;
            }
        }
    }

    public static void stopDiagonal(Player player, int otherX, int otherY) {
    	if (player.frozen()) {
            return;
        }
        player.getMovementHandler().reset();
        int xMove = otherX - player.getX();
        int yMove = 0;

        if (xMove == 0) {
            yMove = otherY - player.getY();
        }

        player.getMovementHandler().addToPath(new Position(player.getX() + xMove, player.getY() + yMove, 0));
    }

    public void followNpc(Entity targ) {

        if (targ == null || targ.isDead()) {
            player.setFollowing(null);
            return;
        }
        if (player.frozen()) {
            return;
        }
        if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
            player.setFollowing(null);
            return;
        }

        int otherX = targ.getX();
        int otherY = targ.getY();

        boolean goodCombatDist = player.goodDistance(otherX, otherY, player.getX(), player.getY(), player.followDistance);

        if (!player.goodDistance(otherX, otherY, player.getX(), player.getY(), 25)) {
            player.setFollowing(null);
            return;
        }

		if (goodCombatDist) {
			return;
		}

        NPC npc = (NPC) targ;

        boolean inside = false;
        for (Position tile : npc.getTiles()) {
            if (player.absX == tile.getX() && player.absY == tile.getY()) {
                inside = true;
                break;
            }
        }

        if (!inside) {
            for (Position npcloc : npc.getTiles()) {
                double distance = npcloc.distance(player.getPosition());
                if (distance <= player.followDistance) {
                    player.stopMovement();
                    return;
                }
            }
        }

        if (inside) {
            int r = Utility.getRandom(3);
            switch (r) {
            case 0:
                walkTo(0, -1);
                break;
            case 1:
                walkTo(0, 1);
                break;
            case 2:
                walkTo(1, 0);
                break;
            case 3:
                walkTo(-1, 0);
                break;
            }
        } else {
        	Position[] locs = { new Position(otherX + 1, otherY, player.getZ()), new Position(otherX - 1, otherY, player.getZ()), new Position(otherX, otherY + 1, player.getZ()),
                    new Position(otherX, otherY - 1, player.getZ()), };

            Position followLoc = null;

            for (Position i : locs) {
                if (followLoc == null || player.getPosition().getDistance(i) < player.getPosition().getDistance(followLoc)) {
                    followLoc = i;
                }
            }

            if (followLoc != null) {
                playerWalk(followLoc.getX(), followLoc.getY());
                player.getMovementHandler().followPath = true;
            }
        }
    }

    public void walkTo(int i, int j) {
        player.getMovementHandler().reset();
        player.getMovementHandler().addToPath(new Position(player.getX() + i, player.getY() + j, player.getZ()));
        player.getMovementHandler().finish();
    }

    /**
     * reseting animation
     */
    public void resetAnimation() {
    	CombatAnimation.itemAnimations(player);
    	player.playAnimation(Animation.create(player.standTurnAnimation));
        requestUpdates();
    }

    public void requestUpdates() {
        player.updateRequired = true;
        player.appearanceUpdateRequired = true;
    }
    
    public void removeWeb(int x, int y) {
        if (player.getX() == 3105 && player.getY() == 3959) {
            object(-1, x, y, 2, 10);
            return;
        }
        if (player.getX() == 3105 && player.getY() == 3959) {
            object(-1, x, y, 2, 10);
            return;
        }
        if (player.getX() == 3106 && player.getY() == 3957) {
            object(-1, x, y, 2, 10);
            return;
        }
        if (player.getX() == 3105 && player.getY() == 3957) {
            object(-1, x, y, 2, 10);
            return;
        }

        if (player.getX() == 3158 && player.getY() == 3952) {
            object(734, x, y, 1, 10);
            return;
        }
        if (player.getX() == 3158 && player.getY() == 3950) {
            object(734, x, y, 1, 10);
            return;
        }
        if (player.getX() == 3093 && player.getY() == 3957) {
            object(734, x, y, 2, 0);
            return;
        }
        object(734, x, y, 0, 0);
    }

    public void useOperate(int itemId) {
    	
        switch (itemId) {
			
        case 2572:
        	BossTracker.open(player);
            break;
            
        case 11283:
			DragonfireShieldEffect dfsEffect = new DragonfireShieldEffect();
			
			if (player.getCombat().noTarget()) {
				return;
			}
			if (dfsEffect.isExecutable(player)) {
				int damage = Utility.getRandom(25);
                if (player.getCombat().noTarget()) {
                    return;
                }
                player.attackDelay = 7;
                if (player.getCombat().target.isPlayer())
                    dfsEffect.execute(player, (Player)player.getCombat().target, damage);
                else
                    dfsEffect.execute(player, (NPC)player.getCombat().target, damage);
                player.setLastDragonfireShieldAttack(System.currentTimeMillis());
			}
			break;
        }
    }

    public void openBank() {
    	if (!player.getAccount().getType().canBank()) {
			player.getActionSender().sendMessage("You're restricted to bank because of your account type.");
			return;
		}
        if (player.takeAsNote)
        	player.write(new SendConfigPacket(115, 1));
        else
        	player.write(new SendConfigPacket(115, 0));
        
        if (Trading.isTrading(player)) {
            Trading.decline(player);
        }
        
        if (player.getArea().inWild() && !(player.getRights().isBetween(2, 3))) {
			player.getActionSender().sendMessage("You can't bank in the wilderness!");
			return;
		}
		
        player.stopSkillTask();
        if (player.getBank().getBankSearch().isSearching()) {
            player.getBank().getBankSearch().reset();
        }
        
        player.write(new SendSoundPacket(1457, 0, 0));
        player.getActionSender().sendString("Search", 58113);
        
        if (player.getOutStream() != null && player != null) {
        	player.setBanking(true);
            player.getItems().resetItems(5064);
            player.getItems().resetBank();
            player.getItems().resetTempItems();
            player.getOutStream().writeFrame(248);
            player.getOutStream().writeWordA(5292);
            player.getOutStream().writeShort(5063);
            player.getActionSender().sendString(player.getName() + "'s Bank", 58064);
        }
    }

    public void sendFriendServerStatus(final int i) { // friends and ignore list status
        if (this.player.getOutStream() != null && this.player != null) {
            this.player.getOutStream().writeFrame(221);
            this.player.getOutStream().writeByte(i);
        }
    }
	
	DecimalFormat format = new DecimalFormat("##.##");

	public static double getRatio(int kills, int deaths) {
		double ratio = kills / Math.max(1D, deaths);
		return ratio;
	}

	public double getRatio(Player player) {
		return getRatio(player.getKillCount(), player.getDeathCount());
	}

	public String displayRatio(Player player) {
		return format.format(getRatio(player));
	}
	
	public void destroyItem(Item item) {
		player.getActionSender().sendUpdateItem(14171, item.getId(), 0, 1);
		player.getActionSender().sendString("Are you sure you want to drop this item?", 14174);
		player.getActionSender().sendString("Yes.", 14175);
		player.getActionSender().sendString("No.", 14176);
		player.getActionSender().sendString("", 14177);
		player.getActionSender().sendString("This item is valuable, you will not", 14182);
		player.getActionSender().sendString("get it back once lost.", 14183);
		player.getActionSender().sendString(ItemDefinition.forId(item.getId()).getName(), 14184);
		player.write(new SendChatBoxInterfacePacket(14170));
	}

	public void handleDestroyItem() {
		if (player.getDestroyItem() != -1) {
			if (player.getItems().playerHasItem(player.getDestroyItem())) {
				player.getItems().deleteItem(player.getDestroyItem());
				player.setDestroyItem(-1);
				player.getActionSender().sendRemoveInterfacePacket();
			}
		}
	}

	public void displayReward(Item... items) {
		player.outStream.createFrameVarSizeWord(53);
		player.outStream.writeWord(6963);
		player.outStream.writeWord(items.length);

		for (Item item : items) {
			if (item.amount > 254) {
				player.outStream.writeByte(255);
				player.outStream.writeDWord_v2(item.amount);
			} else {
				player.outStream.writeByte(item.amount);
			}
			if (item.id > 0) {
				player.outStream.writeWordBigEndianA(item.id + 1);
			} else {
				player.outStream.writeWordBigEndianA(0);
			}
		}
		player.outStream.endFrameVarSizeWord();
		player.flushOutStream();
		player.write(new SendInterfacePacket(6960));
	}

	public void sendItems(Item... items) {
		player.outStream.createFrameVarSizeWord(53);
		player.outStream.writeWord(6963);
		player.outStream.writeWord(items.length);

		for (Item item : items) {
			if (item.amount > 254) {
				player.outStream.writeByte(255);
				player.outStream.writeDWord_v2(item.amount);
			} else {
				player.outStream.writeByte(item.amount);
			}
			if (item.id > 0) {
				player.outStream.writeWordBigEndianA(item.id + 1);
			} else {
				player.outStream.writeWordBigEndianA(0);
			}
		}
		player.outStream.endFrameVarSizeWord();
		player.flushOutStream();
		player.write(new SendInterfacePacket(6960));
	}

	public void restoreHealth() {
    	player.faceEntity(player);
		player.stopMovement();
		player.setSpecialAmount(100);
		player.getWeaponInterface().restoreWeaponAttributes();
		player.lastVeng.reset();
		player.setVengeance(false);
		player.setUsingSpecial(false);
		player.attackDelay = 10;
		player.setPoisonDamage((byte) 0);
		player.infection = 0;
		player.appearanceUpdateRequired = true;
		player.skullIcon = -1;
		requestUpdates();
		requestUpdates();
		resetAnimation();
		resetTb();
        player.setFollowing(null);
		player.getActionSender().sendRemoveInterfacePacket();
    }
	
}