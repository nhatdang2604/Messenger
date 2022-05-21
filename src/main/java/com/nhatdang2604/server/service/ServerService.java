package com.nhatdang2604.server.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.ISendable;
import com.nhatdang2604.server.model.entities.Message;
import com.nhatdang2604.server.model.entities.Room;

public enum ServerService {

	INSTANCE;
	
	private Configuration configuration;
	private ServerSocket socket;
	private MessageService messageService;
	private RoomService roomService;
	
	private ServerService() {
		configuration = Configuration.INSTANCE;
		messageService = MessageService.INSTANCE;
		roomService = RoomService.INSTANCE;
		
		//Try to create the socket via the port from configuration
		try {
			socket = new ServerSocket(configuration.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendClient(ISendable pack, Socket acceptanceSocket) {
		//TODO:
		
		
	}
	
	public void sendRoom(ISendable pack, Socket acceptanceSocket) {
		
		//Send the room, if the client open an existed room
		Room room = (Room) pack;
		room = roomService.findRoomById(room.getId());
		
		if (null != room) {
			Set<Client> clients = room.getClients();
			
			//Find the client of the acceptanceSocket, if not found => return null
			Client client = (Client) clients.stream()
					.filter(cli -> cli.getSocket().equals(acceptanceSocket))
					.findFirst()
					.orElse(null);
			
			//If the client is in the selected room => send the room to the client
			if (null != client) 
			{
				roomService.send(room, client);
			}
		}
		
	}
	
	public void sendMessage(ISendable pack, Socket acceptanceSocket) {

		//Send the message, if the client is messenging
		Message message = (Message) pack;
		
		//Save the message to database
		messageService.createMessage(message);
		
		//Get all the members in the room
		Set<Client> clients = message.getRoom().getClients();
		
		//Send message to all the clients in the room, except the sender
		clients.forEach(client -> {
			if(!client.getSocket().equals(acceptanceSocket)) {
				messageService.send(message, client);
			}
			
		});
	}
	
	public ISendable recieve(Socket acceptanceSocket) {
		
		ISendable pack = null;
		try {
			ObjectInputStream reader = new ObjectInputStream(acceptanceSocket.getInputStream());
			pack = (ISendable) reader.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pack;
	}
	
	public void communicate(Socket acceptanceSocket) {
		while (true) {
			
			//Read the package from the client
			ISendable pack = recieve(acceptanceSocket);
			if (null == pack) {continue;}
			
			//Response base on the type of the packge
			if (ISendable.TYPE_MESSAGE == pack.getType()) {
				sendMessage(pack, acceptanceSocket);
			} else if (ISendable.TYPE_ROOM == pack.getType()) {
				sendRoom(pack, acceptanceSocket);
			} else if (ISendable.TYPE_CLIENT == pack.getType()) {
				sendClient(pack, acceptanceSocket);
			}
			
		}
	}
	
	public void run() {
		
		while (true) {
			try {
				final Socket acceptanceSocket = socket.accept();
				if (null == acceptanceSocket) {continue;}
				Thread thread = new Thread(() -> {communicate(acceptanceSocket);});
				thread.start();
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	
	}
	
}
