package com.nhatdang2604.server.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.model.entities.Room;

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

	public Socket accept() {
		try {
			return socket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int run() {
		int errorCode = 0;
		
		List<Room> rooms = new ArrayList<>();
		
		
		return errorCode;
	}
	
}
