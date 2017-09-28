package com.couchbase;

import com.couchbase.bucket.PlayerBucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;

/**
 * 
 * @author Nick Hartskeerl <apachenick@hotmail.com>
 *
 */
public class CouchbaseManager {
	
	/**
	 * 
	 */
	private static Cluster cluster;

	/**
	 * 
	 */
	public static void initialize() {
		
		if(getCluster() != null) {
			throw new RuntimeException("There already is an active connection");
		}
		
		setCluster(CouchbaseCluster.create());
		PlayerBucket.setSingleton(new PlayerBucket());
		
	}

	/**
	 * 
	 * @return
	 */
	public static Cluster getCluster() {
		return cluster;
	}

	/**
	 * 
	 * @param cluster
	 */
	public static void setCluster(Cluster cluster) {
		CouchbaseManager.cluster = cluster;
	}
	
}