package com.nhatdang2604.server.entry_point;

import java.util.Scanner;

import com.nhatdang2604.server.service.ServerService;

public class Server {

	public static void main(String[] args) {
		ServerService service = ServerService.INSTANCE;
		Thread serverThread = new Thread(() -> {
			service.run();
		});
		
		serverThread.start();
		
		System.out.println("Wanna close the server [Y/n]: ");
		Scanner scanner = new Scanner(System.in);
		String buffer = scanner.nextLine().trim();
		
		if (buffer.toLowerCase().equals("y")) {
			service.stop();
			System.out.println("Server is stopped");
		}
		
		
	}
	

}
