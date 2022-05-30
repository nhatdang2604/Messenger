package com.nhatdang2604.config;

public enum Configuration {

	INSTANCE;
	
	private int port;
	private String ip;
	private String storagePath;
	
	private Configuration() {
		port = 8888;
		ip = "localhost";
		storagePath = "/storage/";
	}
	
	public int getPort() {return port;}
	public String getIp() {return ip;}
	public String getStoragePath() {return storagePath;}
}
