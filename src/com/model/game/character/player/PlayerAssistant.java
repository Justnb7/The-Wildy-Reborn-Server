package com.model.game.character.player;

import java.text.DecimalFormat;
import java.util.Objects;

import org.omicron.jagex.runescape.CollisionMap;

import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.combat.combat_data.CombatAnimation;
import com.model.game.character.combat.combat_data.CombatData;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.effect.impl.DragonfireShieldEffect;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.content.BossTracker;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.out.SendChatBoxInterfacePacket;
import com.model.game.character.player.packets.out.SendConfigPacket;
import com.model.game.character.player.packets.out.SendInterfacePacket;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.player.packets.out.SendSoundPacket;
import com.model.game.character.walking.PathFinder;
import com.model.game.item.Item;
import com.model.game.location.Position;
import com.model.utility.Utility;
import com.model.utility.cache.map.Region;
import com.model.utility.json.definitions.ItemDefinition;

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
			player.write(new SendMessagePacket("You're trying to move too fast."));
			return;
		}
        player.getMovementHandler().reset();
        player.teleportToX = x;
        player.teleportToY = y;
        player.teleHeight = h;
        player.setTeleportTarget(Position.create(x, y, h));
        requestUpdates();
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
    public void followPlayer(boolean forCombat) {
        Player following = World.getWorld().getPlayers().get(player.followId);
        if (following == null || following.isDead()) {
            resetFollow();
            return;
        }
        if (player.frozen()) {
            return;
        }

        if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
            resetFollow();
            return;
        }

        int otherX = following.getX();
        int otherY = following.getY();
        boolean sameSpot = (player.absX == otherX && player.absY == otherY);
        boolean hallyDistance = player.goodDistance(otherX, otherY, player.getX(), player.getY(), 2);
        boolean rangeWeaponDistance = player.goodDistance(otherX, otherY, player.getX(), player.getY(), 4);
        boolean bowDistance = player.goodDistance(otherX, otherY, player.getX(), player.getY(), 7);
        boolean magicDistance = player.goodDistance(otherX, otherY, player.getX(), player.getY(), 7);
        boolean castingMagic = (player.spellId > 0 || player.autocastId > 0 || player.oldSpellId > 0 || player.usingMagic || player.autoCast || player.getCombatType() == CombatType.MAGIC  && magicDistance);

		boolean playerRanging = (player.throwingAxe) && rangeWeaponDistance;
		boolean playerBowOrCross = (player.usingBow) && bowDistance;

        if (!player.goodDistance(otherX, otherY, player.getX(), player.getY(), 25)) {
            resetFollow();
            return;
        }

		if (!sameSpot) {
			if (!player.isUsingSpecial() && player.getArea().inWild()) {
				if (player.isUsingSpecial() && (playerRanging || playerBowOrCross)) {
					player.stopMovement();
					return;
				}
				if (playerRanging || playerBowOrCross) {
					player.stopMovement();
					return;
				}
				if (castingMagic) {
					return;
				}
				if (CombatData.usingHalberd(player) && hallyDistance) {
					player.stopMovement();
					return;
				}
			}
		}
        
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
            int fx = following.lastX;
            int fy = World.getWorld().getPlayers().get(player.followId).lastY;

            int delay = (player.getMovementHandler().isMoving() || following.getMovementHandler().isMoving()) ? 1
                : (player.walkTutorial + 1 >= Integer.MAX_VALUE ? player.walkTutorial = 0 : player.walkTutorial++);
            int remainder = delay % 2;
            if (remainder == 1) {
                int x = fx - player.getX();
                int y = fy - player.getY();
                playerWalk(player.getX() + x, player.getY() + y);
                return;
            }
        } else {

            /*
             * Check for other range weapons which require a distance of 4
             */
            if (player.throwingAxe && rangeWeaponDistance) {
                player.getMovementHandler().stopMovement();
                return;
            }

            if (CombatData.usingHalberd(player) && hallyDistance) {
                player.getMovementHandler().stopMovement();
                return;
            }

            /*
             * Check our regular combat styles for distance
             */
            switch (player.getCombatType()) {
            case MAGIC:

                if (magicDistance) {
                    player.getMovementHandler().stopMovement();
                    return;
                }

                if ((player.spellId > 0 || player.oldSpellId > 0) && magicDistance) {
                    if (player.spellId > 0 || player.oldSpellId > 0) {
                        resetFollow();
                        return;
                    }
                }
                break;
            case MELEE:
                if (player.goodDistance(otherX, otherY, player.getX(), player.getY(), 1)) {
                    if (otherX != player.getX() && otherY != player.getY()) {
                        player.faceEntity(following);
                        stopDiagonal(player, otherX, otherY);
                        return;
                    } else {
                        player.getMovementHandler().stopMovement();
                        return;
                    }
                }
                break;
            case RANGED:
                if (bowDistance) {
                    player.getMovementHandler().reset();
                    return;
                }
                break;
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

    public void followNpc() {

        if (World.getWorld().getNpcs().get(player.npcFollowIndex) == null || World.getWorld().getNpcs().get(player.npcFollowIndex).isDead) {
            player.npcFollowIndex = 0;
            return;
        }
        if (player.frozen()) {
            return;
        }
        if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0)
            return;

        int otherX = World.getWorld().getNpcs().get(player.npcFollowIndex).getX();
        int otherY = World.getWorld().getNpcs().get(player.npcFollowIndex).getY();
        player.goodDistance(otherX, otherY, player.getX(), player.getY(), 1);
        boolean hallyDistance = player.goodDistance(otherX, otherY, player.getX(), player.getY(), 2);
        boolean bowDistance = player.goodDistance(otherX, otherY, player.getX(), player.getY(), 7);
        boolean rangeWeaponDistance = player.goodDistance(otherX, otherY, player.getX(), player.getY(), 4);
        boolean sameSpot = player.absX == otherX && player.absY == otherY;
        boolean mageDistance = player.goodDistance(otherX, otherY, player.getX(),player.getY(), 7);
        boolean castingMagic = false, playerRanging = false;
        if (!player.goodDistance(otherX, otherY, player.getX(), player.getY(), 25)) {
            player.npcFollowIndex = 0;
            return;
        }

        if((player.playerEquipment[player.getEquipment().getWeaponId()] == 13022 || player.playerEquipment[player.getEquipment().getWeaponId()] == 15041 || player.playerEquipment[player.getEquipment().getWeaponId()] == 11785 || player.playerEquipment[player.getEquipment().getWeaponId()] == 9185) && rangeWeaponDistance)
			playerRanging = true;
		if((player.playerEquipment[player.getEquipment().getWeaponId()] == 22494 || player.playerEquipment[player.getEquipment().getWeaponId()] == 2415 || player.playerEquipment[player.getEquipment().getWeaponId()] == 2416 || player.playerEquipment[player.getEquipment().getWeaponId()] == 2417) && mageDistance)
			castingMagic = true;
		
		if((player.usingBow || playerRanging) && bowDistance && !sameSpot) {
			return;
		}
		if((castingMagic || player.mageFollow || (player.npcIndex > 0 && player.autocastId > 0)) && mageDistance && !sameSpot) {
			return;
		}
		if(CombatData.usingHalberd(player) && hallyDistance && !sameSpot) {
			return;
		}

		if(player.usingBow || player.usingCross && rangeWeaponDistance && !sameSpot) {
			return;
		}

        Npc npc = World.getWorld().getNpcs().get(player.npcFollowIndex);

        boolean inside = false;
        for (Position tile : npc.getTiles()) {
            if (player.absX == tile.getX() && player.absY == tile.getY()) {
                inside = true;
                break;
            }
        }

        if (!inside) {
            for (Position Location : npc.getTiles()) {
                double distance = Location.distance(player.getPosition());
                boolean magic = player.usingMagic;
                boolean ranged = !player.usingMagic && (player.usingBow || player.usingCross || player.throwingAxe);
                boolean melee = !magic && !ranged;
                if (melee) {
                    if (distance <= 1) {
                        player.stopMovement();
                        return;
                    }
                } else {
                    if (distance <= (ranged ? 7 : 10)) {
                        player.stopMovement();
                        return;
                    }
                }
            }
        }

        player.faceEntity(npc);

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
        player.faceEntity(npc);
    }

    public void resetFollow() {
        player.followId = 0;
        player.followId2 = 0;
        player.faceEntity(player);
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
			
			if (player.npcIndex <= 0 && player.playerIndex <= 0) {
				return;
			}
			if (dfsEffect.isExecutable(player)) {
				int damage = Utility.getRandom(25);
				if (player.playerIndex > 0) {
					Player target = World.getWorld().getPlayers().get(player.playerIndex);
					if (Objects.isNull(target)) {
						return;
					}
					player.attackDelay = 7;
					dfsEffect.execute(player, target, damage);
					player.setLastDragonfireShieldAttack(System.currentTimeMillis());
				} else if (player.npcIndex > 0) {
					Npc target = NPCHandler.npcs[player.npcIndex];
					if (Objects.isNull(target)) {
						return;
					}
					player.attackDelay = 7;
					dfsEffect.execute(player, target, damage);
					player.setLastDragonfireShieldAttack(System.currentTimeMillis());
				}
			}
			break;
        }
    }

    public void openBank() {
        
        if (player.takeAsNote)
        	player.write(new SendConfigPacket(115, 1));
        else
        	player.write(new SendConfigPacket(115, 0));
        
        if (Trading.isTrading(player)) {
            Trading.decline(player);
        }
        
        if (player.getArea().inWild() && !(player.getRights().isBetween(2, 3))) {
			player.write(new SendMessagePacket("You can't bank in the wilderness!"));
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
		resetFollow();
		player.getActionSender().sendRemoveInterfacePacket();
    }
	
}