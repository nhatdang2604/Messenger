package com.nhatdang2604.server.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.entities.ISendable;
import com.nhatdang2604.server.entities.Message;
import com.nhatdang2604.server.entities.Packet;
import com.nhatdang2604.server.entities.Room;
import com.nhatdang2604.server.entities.User;

public enum ServerService {

	INSTANCE;
	
	private boolean exitFlag = false;
	
	//Network stuffs
	private ServerSocket serverSocket;
	private List<Socket> sockets;
	
	//Configs
	private Configuration configuration;
	
	//Services
	private MessageService messageService;
	private RoomService roomService;
	private UserService userService;
	
	private ServerService() {
		configuration = Configuration.INSTANCE;
		messageService = MessageService.INSTANCE;
		roomService = RoomService.INSTANCE;
		userService = UserService.INSTANCE;
		
		sockets = new ArrayList<>();
		
		//Try to create the socket via the port from configuration
		try {
			serverSocket = new ServerSocket(configuration.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void authenticate(Packet packet, Socket socket) {
		
		User recievedUser = (User) packet.getSendable();
		User sentUser = null;
		
		if (null != recievedUser) {
			sentUser = userService.login(recievedUser);
		}
		
		send(sentUser, socket);
		
	}
	
	public void registrate(Packet packet, Socket socket) {
		
		
		User recievedUser = (User) packet.getSendable();
		User sentUser = null;
		
		if (null != recievedUser) {
			sentUser = userService.registrate(recievedUser);
			System.out.println("Registrate: " + sentUser);
		}
		
		send(sentUser, socket);
	
	}
	
	public void openRoom(Packet packet, Socket socket) {
		
		//Send the room, if the client open an existed room
		Room room = (Room) packet.getSendable();
		
		if (null != room) {
			room = roomService.findRoomById(room.getId());
		}
		
		send(room, socket);
	}
	
	public void createRoom(Packet packet, Socket socket) {
		
		Room room = (Room) packet.getSendable();
		
		if (null != room) {
			room = roomService.createRoom(room);
		}
		
		//Send the created room to the client
		send(room, socket);
		
	}
	
	public void sendMessage(Packet packet, Socket socket) {

		//Send the message, if the client is messenging
		Message message = (Message) packet.getSendable();
		
		//Save the message to database
		messageService.createMessage(message);
		
		//Get all the members in the room
		Set<User> users = message.getRoom().getUsers();
		
		//Send message to all the connected user
		sockets.forEach(soc -> {
			send(message, soc);
		});
	}
	
	public Packet recieve(Socket acceptanceSocket) {
		
		Packet packet = null;
		try {
			ObjectInputStream reader = new ObjectInputStream(acceptanceSocket.getInputStream());
			packet = (Packet) reader.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return packet;
	}
	
	public ISendable send(ISendable sendable, Socket socket) { 
		try {
			ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
			writer.writeObject(sendable);
			writer.flush();
			    
		} catch (IOException e) {
			e.printStackTrace();
			sendable = null;
		}
	       
		return sendable;
	}
	
	public void communicate(Socket acceptanceSocket) {
		while (true) {
			
			if (exitFlag) {
				try {
					acceptanceSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			}
			
			//Read the package from the client
			Packet packet = recieve(acceptanceSocket);
			if (null == packet) {continue;}
			
			//Response base on the type of the packge
			if (ISendable.TYPE_MESSAGE == packet.getSendable().getType()) {
				
				if (Packet.TYPE_POST == packet.getSendType()) {
					sendMessage(packet, acceptanceSocket);
				}
				
			} else if (ISendable.TYPE_ROOM == packet.getSendable().getType()) {
				
				if (Packet.TYPE_GET == packet.getSendType()) {
					openRoom(packet, acceptanceSocket);
				} else if (Packet.TYPE_CREATE == packet.getSendType()) {
					createRoom(packet, acceptanceSocket);
				}
				
			} else if (ISendable.TYPE_USER == packet.getSendable().getType()) {
				System.out.println("Client service start!");
				
				//Authenticate
				if (Packet.TYPE_POST == packet.getSendType()) {
					authenticate(packet, acceptanceSocket);
				} else if (Packet.TYPE_CREATE == packet.getSendType()) {
					registrate(packet, acceptanceSocket);
				}
				
				System.out.println("Client service end!");
				
			}
			
		}
	}
	
	public void handShake(Socket socket) {
		
		Message message = new Message();
		message.setContent("Accepted");
		send(message, socket);
		
	}
	
	public void run() {
		
		while (true) {
			
			//Exit condition
			if (exitFlag) 
			{
				break;
			}
			
			System.out.println("Waiting for new client...");
			try {
				Socket socket = (Socket) serverSocket.accept();
				//handShake(socket);
				
				if (null != socket) {
					sockets.add(socket);
					System.out.println(socket + " join the server");
					Thread thread = new Thread(() -> {communicate(socket);});
					thread.start();
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
		}
	
	}

	public void stop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			exitFlag = true;
		}
	}
}
