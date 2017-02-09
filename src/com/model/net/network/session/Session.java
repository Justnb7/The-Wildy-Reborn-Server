package com.model.net.network.session;

import io.netty.channel.Channel;

/**
 * Represents a single {@link Session} for the channel
 * 
 * @author Mobster
 *
 */
public abstract class Session {

	/**
	 * The channel bound to this session
	 */
	private final Channel channel;

	/**
	 * The attachment of the object
	 */
	private Object attachment;

	/**
	 * Constructs a new {@link Session}
	 * 
	 * @param channel
	 *            The {@link Channel} to bind to this session
	 */
	public Session(Channel channel) {
		this.channel = channel;
	}

	/**
	 * Constructs a new {@link Session} instance
	 * 
	 * @param channel
	 *            The {@link Channel} we are binding this session tooi
	 * @param attachment
	 *            The attached object of this session
	 */
	public Session(Channel channel, Object attachment) {
		this.channel = channel;
		this.attachment = attachment;
	}

	/**
	 * Gets the {@link Channel} bound to this session
	 * 
	 * @return The {@link Channel} bound to this session
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * Gets the object attachment to this session
	 * 
	 * @return The object attachment to this session
	 */
	public Object getAttachment() {
		return attachment;
	}

	/**
	 * Receives a message from the channel
	 * 
	 * @param object
	 *            The message received
	 */
	public abstract void receiveMessage(Object object);

}
