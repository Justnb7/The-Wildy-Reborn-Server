package com.venenatis.game.content.teleportation.lever;

import com.venenatis.game.location.Location;

public class Lever {
	
	 private final Location targetLocation;
     private final int direction1;
     private final int direction2;

     public Location getTargetLocation() {
         return targetLocation;
     }

     public int getDirection1() {
         return direction1;
     }

     public int getDirection2() {
         return direction2;
     }

     public Lever(Location target, int direction1, int direction2) {
         this.targetLocation = target;
         this.direction1 = direction1;
         this.direction2 = direction2;
     }

}
