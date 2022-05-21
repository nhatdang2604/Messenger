package com.nhatdang2604.server.model.entities;

public interface ISendable {

	public static final int TYPE_MESSAGE = 0;
	public static final int TYPE_ROOM = 1;
	public static final int TYPE_CLIENT = 2;
	
	public int getType();
	
}
