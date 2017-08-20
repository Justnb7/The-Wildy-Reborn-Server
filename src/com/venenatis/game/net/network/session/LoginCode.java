package com.venenatis.game.net.network.session;

/**
 * Represents the login response codes.
 * 
 * @author Patrick van Elderen
 */
public final class LoginCode {
	
	public static int EXCHANGE_DATA = 0;
	public static int DELAY = 1;
    public static int NORMAL = 2;
    public static int INVALID_CREDENTIALS = 3;
    public static int ACCOUNT_DISABLED = 4;
    public static int ACCOUNT_ONLINE = 5;
    public static int GAME_UPDATED = 6;
    public static int WORLD_FULL = 7;
    public static int LOGIN_SERVER_OFFLINE = 8;
    public static int LOGIN_LIMIT_EXCEEDED = 9;
    public static int BAD_SESSION_ID = 10;
    public static int LOGIN_SERVER_REJECTED_SESSION = 11;
    public static int MEMBERS_ACCOUNT_REQUIRED = 12;
    public static int COULD_NOT_COMPLETE_LOGIN = 13;
    public static int SERVER_BEING_UPDATED = 14;
    public static int RECONNECTING =15;
    public static int LOGIN_ATTEMPTS_EXCEEDED = 16;
    public static int MEMBERS_ONLY_AREA = 17;
    public static int OUT_OF_DATE_CLIENT = 23;
    public static int INVALID_LOGIN_SERVER = 20;
    public static int TRANSFERING_PROFILE = 21;
    public static int BAD_USERNAME = 22;
    public static int SHORT_USERNAME = 3;
}