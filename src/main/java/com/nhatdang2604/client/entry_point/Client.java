package com.nhatdang2604.client.entry_point;

import java.net.Socket;

import com.nhatdang2604.client.controller.Controller;
import com.nhatdang2604.config.Configuration;

public class Client {

	private Socket socket;
	private Controller controller;
	
	public void connect(Configuration config) {
		try {
			System.out.println("Before connecting");
			socket = new Socket(config.getIp(), config.getPort());
			System.out.println("After connecting");
			System.out.println(socket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Client() {
		connect(Configuration.INSTANCE);
		controller = new Controller(socket);
	}
	
	public void runApp() {
		controller.run();
	}
	
	
	public static void main(String[] args) {
		Client client = new Client();
		client.runApp();
	}

}
