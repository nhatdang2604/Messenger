package com.nhatdang2604.server.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.ISendable;
import com.nhatdang2604.server.model.entities.Message;
import com.nhatdang2604.server.model.entities.Packet;
import com.nhatdang2604.server.model.entities.Room;

public enum ServerService {

	INSTANCE;
	
	private Configuration configuration;
	private ServerSocket socket;
	private MessageService messageService;
	private RoomService roomService;
	private ClientService clientService;
	
	private ServerService() {
		configuration = Configuration.INSTANCE;
		messageService = MessageService.INSTANCE;
		roomService = RoomService.INSTANCE;
		clientService = ClientService.INSTANCE;
		
		//Try to create the socket via the port from configuration
		try {
			socket = new ServerSocket(configuration.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void authenticateClient(Packet packet, Socket acceptanceSocket) {
		
		Client sentClient = (Client) packet.getSendable();
		Client client = null;
		
		if (null != sentClient) {
			client = clientService.login(sentClient);
		}
		
		//Using the socket from the sent client to IO
		send(client, sentClient);
		
	}
	
	public void registrateClient(Packet packet, Socket acceptanceSocket) {
		
		
		Client sentClient = (Client) packet.getSendable();
		Client client = null;
		
		if (null != sentClient) {
			client = clientService.registrate(sentClient);
		}
		
		//Using the socket from the client to IO
		send(client, sentClient);
	
	}
	
	public void openRoom(Packet packet, Socket acceptanceSocket) {
		
		//Send the room, if the client open an existed room
		Room room = (Room) packet.getSendable();
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
				send(room, client);
			}
		}
		
	}
	
	public void createRoom(Packet packet, Socket acceptanceSocket) {
		
		Room room = (Room) packet.getSendable();
		Client client = null;
		
		if (null != room) {
			
			
			
			//Find the client of the acceptanceSocket, if not found => return null
			//	The first client always the first member of the creating room
			client = room.getClients().stream().findFirst().orElse(null);
			
			room = roomService.createRoom(room);
			client = clientService.findClientById(client.getId());
			
		}
		
		//Send the created room to the client
		send(room, client);
		
	}
	
	public void sendMessage(Packet packet, Socket acceptanceSocket) {

		//Send the message, if the client is messenging
		Message message = (Message) packet.getSendable();
		
		//Save the message to database
		messageService.createMessage(message);
		
		//Get all the members in the room
		Set<Client> clients = message.getRoom().getClients();
		
		//Send message to all the clients in the room, except the sender
		clients.forEach(client -> {
			if(!client.getSocket().equals(acceptanceSocket)) {
				send(message, client);
			}
			
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
	
	public ISendable send(ISendable sendable, Client client) { 
		try {
			ObjectOutputStream writer = client.getWriter();
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
				
			} else if (ISendable.TYPE_CLIENT == packet.getSendable().getType()) {
				
				//Authenticate
				if (Packet.TYPE_POST == packet.getSendType()) {
					authenticateClient(packet, acceptanceSocket);
				} else if (Packet.TYPE_CREATE == packet.getSendType()) {
					registrateClient(packet, acceptanceSocket);
				}
			}
			
		}
	}
	
	public void run() {
		
		while (true) {
			try {
				final Socket acceptanceSocket = socket.accept();
				if (null == acceptanceSocket) {continue;}
				System.out.println(acceptanceSocket + " join the server");
				Thread thread = new Thread(() -> {communicate(acceptanceSocket);});
				thread.start();
			
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
		}
	
	}

	public void stop() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
