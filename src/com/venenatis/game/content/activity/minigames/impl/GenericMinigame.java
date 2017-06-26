package com.venenatis.game.content.activity.minigames.impl;

import com.venenatis.game.content.activity.minigames.Minigame;
import com.venenatis.game.content.activity.minigames.MinigameType;

public abstract class GenericMinigame extends Minigame {

      public GenericMinigame(String name, RunType runType, MinigameType type,  Classification classification) {
            super(name, runType, type, classification);
      }

}