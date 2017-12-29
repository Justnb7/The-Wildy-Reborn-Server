package com.venenatis.game.model.entity.player.clan;

public enum ClanRank {
    ANYONE("Member", -1),
    FRIEND("Friend", 0),
    RECRUIT("Recruit", 1),
    CORPORAL("Corporal", 2),
    SERGEANT("Sergeant", 3),
    LIEUTENANT("Lieutenant", 4),
    CAPTAIN("Captain", 5),
    GENERAL("General", 6),
    LEADER("Leader", 7);

    private final String name;

    private final int rankIndex;

    private ClanRank(String name, int rank) {
        this.name = name;
        this.rankIndex = rank;
    }

    public String getName() {
        return name;
    }

    public int getRankIndex() {
        return rankIndex;
    }

    public static ClanRank forId(int id) {
        for (ClanRank rank : ClanRank.values()) {
            if (rank.ordinal() == id) {
                return rank;
            }
        }
        return null;
    }
    
    public boolean lessThan(ClanRank other) {
        return rankIndex < other.rankIndex;
    }

}