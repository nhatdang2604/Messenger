package com.nhatdang2604.server.entities;

import java.io.Serializable;

public class Packet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5953557509590120828L;

	public static final int TYPE_GET = 0;
	public static final int TYPE_POST = 1;
	public static final int TYPE_CREATE = 2;
	public static final int TYPE_GET_ALL_USERS = 3;
	public static final int TYPE_LOGOUT = 4;
	
	private User sender;
	private ISendable sendable;
	private int sendType;
	
	public Packet() {
		//do nothing
	}

	public ISendable getSendable() {
		return sendable;
	}

	public void setSendable(ISendable sendable) {
		this.sendable = sendable;
	}

	public int getSendType() {
		return sendType;
	}

	public void setSendType(int sendType) {
		this.sendType = sendType;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	
}
