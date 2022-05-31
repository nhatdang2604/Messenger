package com.nhatdang2604.server.entry_point;

import java.util.Scanner;

import com.nhatdang2604.server.service.ServerService;

public class Server {

	public static void userControl(ServerService service) {
		while (true) {
			System.out.println("Wanna close the server [Y]: ");
			Scanner scanner = new Scanner(System.in);
			String buffer = scanner.nextLine().trim();
			
			if (buffer.toLowerCase().equals("y")) {
				service.stop();
				System.out.println("Server is stopped");
				break;
			} else {
				System.out.println("Invalid: You should only enter 'y' or 'Y' to close the server");
			}
		}
	}
	
	public static void main(String[] args) {
		ServerService service = ServerService.INSTANCE;
		Thread serverThread = new Thread(() -> {
			service.run();
		});
		
		serverThread.start();
		userControl(service);
		
	}
	

}
