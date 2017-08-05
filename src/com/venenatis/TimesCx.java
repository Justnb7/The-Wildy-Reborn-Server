package com.venenatis;

public class TimesCx {
	
	public static class WorldPro {
		public long player_pre, npc_pre, update, player_post, npc_post;
	}
	
	public static class PlrPre {
		public long process, walk, coverage;
	}
	
	public WorldPro wp;
	public PlrPre pp;
	
	public TimesCx() {
		wp = new WorldPro();
		pp = new PlrPre();
	}
	
	public long login, objs, tasks, world, items, total;

	public void print() {
		boolean debug = false;
		if(debug) {
        System.out.printf("[GameEngine] %sms for a game cycle. [login:%s, objs:%s, tasks:%s, world:%s, items:%s] ~~ [%s, %s, %s, %s, %s | %s %s %s] %n",
        		total, login, objs, tasks, world, items, 
        		wp.player_pre, wp.npc_pre, wp.update, wp.player_post, wp.npc_post,
        		pp.process, pp.walk, pp.coverage);
        System.out.println();
		}
	}

}
