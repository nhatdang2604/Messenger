package com.nhatdang2604.server.service;

import java.io.IOException;
import java.net.ServerSocket;

import com.nhatdang2604.config.Configuration;

public enum ServerService {

	INSTANCE;
	
	private Configuration configuration;
	private ServerSocket socket;
	
	private ServerService() {
		configuration = Configuration.INSTANCE;
		
		//Try to create the socket via the port from configuration
		try {
			socket = new ServerSocket(configuration.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int run() {
		int errorCode = 0;
		
		
		return errorCode;
	}
	
}
