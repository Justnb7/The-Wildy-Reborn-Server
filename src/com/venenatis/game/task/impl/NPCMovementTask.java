package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.following.NPCFollowing;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

/**
 * An {@link EventListener} implementation that will update an walking npc.
 * 
 * @author Patrick van Elderen
 */
public final class NPCMovementTask extends Task {

	public NPCMovementTask() {
		super(1);
	}
	
	public int GetMove(int Place1, int Place2) {
		if ((Place1 - Place2) == 0) {
			return 0;
		} else if ((Place1 - Place2) < 0) {
			return 1;
		} else if ((Place1 - Place2) > 0) {
			return -1;
		}
		return 0;
	}

	@Override
	public void execute() {
		for (int index = 0; index < World.getWorld().getNPCs().capacity(); index++) {
			NPC npc = World.getWorld().getNPCs().get(index);
			if (npc == null) {
				continue;
			}
			if (!npc.getCombatState().isDead() && npc.walkingHome) {
				npc.resetFace();
				//System.out.println(npc.getDefinition().getName());
				npc.targetId = 0;
				int walkingDistance = 18;
				if (npc.getDefinition() != null && npc.walking_type > 5) {
					walkingDistance = npc.walking_type;
				}
				
				if (npc.spawnedBy == 0) {
					if ((npc.getX() > npc.makeX + walkingDistance) || (npc.getX() < npc.makeX - walkingDistance) || (npc.getY() > npc.makeY + walkingDistance)
							|| (npc.getY() < npc.makeY - walkingDistance)) {
						npc.walkingHome = true;
					}
				}
				if (npc.walkingHome && npc.getX() == npc.makeX && npc.getY() == npc.makeY) {
					npc.walkingHome = false;
					npc.randomWalk = true;
				} else if (npc.walkingHome) {
					NPCFollowing.walkToNextTile(npc, npc.makeX, npc.makeY);
				}
			} else if (npc.randomWalk && (npc.getDefinition() == null || npc.walking_type == 1)) {
				if (npc.walking_type == 1337 && ((npc.getX() != npc.walkX) || (npc.getY() != npc.walkY))) {
					npc.moveX = GetMove(npc.getX(), npc.walkX);
					npc.moveY = GetMove(npc.getY(), npc.walkY);
					npc.getNextNPCMovement(npc);
				}
				int random = Utility.getRandom(3);
				
				if (random == 1) {
					int MoveX = 0;
					int MoveY = 0;
					int Rnd = Utility.getRandom(9);
					int walking_type = npc.getDefinition() == null ? 1 : npc.walking_type;
					switch (Rnd) {
					case 1:
						MoveX = walking_type;
						MoveY = walking_type;
						break;
					case 2:
						MoveX = -walking_type;
						break;
					case 3:
						MoveY = -walking_type;
						break;
					case 4:
						MoveX = walking_type;
						break;
					case 5:
						MoveY = walking_type;
						break;
					case 6:
						MoveX = -walking_type;
						MoveY = -walking_type;
						break;
					case 7:
						MoveX = -walking_type;
						MoveY = walking_type;
						break;
					case 8:
						MoveX = walking_type;
						MoveY = -walking_type;
						break;
					}

					if (MoveX == walking_type) {
						if (npc.getX() + MoveX < npc.makeX + 1) {
							npc.moveX = MoveX;
						} else {
							npc.moveX = 0;
						}

					}
					if (MoveX == -walking_type) {
						if (npc.getX() - MoveX > npc.makeX - 1) {
							npc.moveX = MoveX;
						} else {
							npc.moveX = 0;
						}

					}
					if (MoveY == walking_type) {
						if (npc.getY() + MoveY < npc.makeY + 1) {
							npc.moveY = MoveY;
						} else {
							npc.moveY = 0;
						}

					}
					if (MoveY == -walking_type) {
						if (npc.getY() - MoveY > npc.makeY - 1) {
							npc.moveY = MoveY;
						} else {
							npc.moveY = 0;
						}

					}
					NPCFollowing.walkToNextTile(npc, npc.getX() + npc.moveX, npc.getY() + npc.moveY);
				}
			}
		}
	}

}