package com.couchbase.bucket;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Query;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.CouchbaseBucket;
import com.couchbase.CouchbaseManager;
import com.venenatis.game.util.NameUtils;

/**
 * 
 * @author Nick Hartskeerl <apachenick@hotmail.com>
 *
 */
public class PlayerBucket extends CouchbaseBucket {
	
	/**
	 * 
	 */
	public static final String BUCKET_NAME = "default";
	
	/**
	 * 
	 */
	private static PlayerBucket singleton;

	/**
	 * 
	 */
	public PlayerBucket() {
		super(CouchbaseManager.getCluster(), BUCKET_NAME);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public String get(String name) {

		JsonDocument user = getBucket().get(getKey(name));
		
		return user.content().toString();
		
	}
	
	/**
	 * 
	 * @param name
	 * @param json
	 */
	public void store(String name, String json) {
		JsonObject user = JsonObject.fromJson(json);
		getBucket().upsert(JsonDocument.create(getKey(name), user));
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean exists(String name) {
		
		QueryResult result = getBucket().query(Query.simple("SELECT COUNT(*) AS `count` FROM `default` WHERE META(default).id = '"+getKey(name)+"'"));
		JsonObject object = result.allRows().get(0).value();
		int count = object.getInt("count");
		
		return count > 0;
		
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	private String getKey(String name) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("u:");
		builder.append(NameUtils.formatNameForProtocol(name));
		
		return builder.toString();
		
	}
	
	/**
	 * 
	 * @param total
	 * @param wilderness
	 */
	public void playersOnline(int total, int wilderness) {
		
		JsonObject content = JsonObject.empty().put("total", total).put("wilderness", wilderness);
		JsonDocument doc = JsonDocument.create("s:players_online", content);
		
		getBucket().upsert(doc);
		
	}

	/**
	 * 
	 * @return
	 */
	public static PlayerBucket getSingleton() {
		return singleton;
	}

	/**
	 * 
	 * @param singleton
	 */
	public static void setSingleton(PlayerBucket singleton) {
		PlayerBucket.singleton = singleton;
	}

}