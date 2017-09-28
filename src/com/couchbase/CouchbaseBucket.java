package com.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

/**
 * 
 * @author Nick Hartskeerl <apachenick@hotmail.com>
 *
 */
public class CouchbaseBucket {
	
	/**
	 * 
	 */
	private Cluster cluster;

	/**
	 * 
	 */
	private Bucket bucket;
	
	/**
	 * 
	 * @param cluster
	 * @param name
	 */
	public CouchbaseBucket(Cluster cluster, String name) {
		setCluster(cluster);
		setBucket(cluster.openBucket(name));
	}

	/**
	 * 
	 * @return
	 */
	public Bucket getBucket() {
		return bucket;
	}

	/**
	 * 
	 * @param bucket
	 */
	public void setBucket(Bucket bucket) {
		this.bucket = bucket;
	}

	/**
	 * 
	 * @return
	 */
	public Cluster getCluster() {
		return cluster;
	}

	/**
	 * 
	 * @param cluster
	 */
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	
}