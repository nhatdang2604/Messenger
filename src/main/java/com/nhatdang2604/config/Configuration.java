package com.nhatdang2604.config;

public enum Configuration {

	INSTANCE;
	
	private int port;
	
	private Configuration() {
		port = 3200;
	}
	
	public int getPort() {return port;}
}
