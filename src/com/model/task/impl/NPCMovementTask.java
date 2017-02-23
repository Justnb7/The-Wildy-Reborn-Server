package com.model.task.impl;

import com.model.game.World;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.NPCHandler;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

/**
 * An {@link EventListener} implementation that will update an walking npc.
 * 
 * @author Patrick van Elderen
 */
public final class NPCMovementTask extends ScheduledTask {

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
		for (int index = 0; index < World.getWorld().getNpcs().capacity(); index++) {
			Npc npc = World.getWorld().getNpcs().get(index);
			if (npc == null) {
				continue;
			}
			if (!npc.isDead && npc.walkingHome) {
				npc.facePlayer(0);
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
					NPCHandler.walkToNextTile(npc, npc.makeX, npc.makeY);
				}
			} else if (npc.randomWalk && (npc.getDefinition() == null || npc.walking_type == 1)) {
				if (npc.walking_type == 1337 && ((npc.absX != npc.walkX) || (npc.absY != npc.walkY))) {
					npc.moveX = GetMove(npc.absX, npc.walkX);
					npc.moveY = GetMove(npc.absY, npc.walkY);
					npc.getNextNPCMovement(npc);
					npc.updateRequired = true;
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
					NPCHandler.walkToNextTile(npc, npc.absX + npc.moveX, npc.absY + npc.moveY);
				}
			}
		}
	}

}
