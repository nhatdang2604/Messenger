package com.nhatdang2604.config;

public enum Configuration {

	INSTANCE;
	
	private int port;
	private String ip;
	
	private Configuration() {
		port = 8888;
		ip = "localhost";
	}
	
	public int getPort() {return port;}
	public String getIp() {return ip;}
}
