package com.nhatdang2604.client.service;

import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.ISendable;
import com.nhatdang2604.server.model.entities.Message;
import com.nhatdang2604.server.model.entities.Room;

public class ClientService {

	private MessageService messageService;
	private Client client;	//the current client
	private Room room;
	private Thread thread;
	
	public ClientService(Client client, Room room) {
		this.messageService = MessageService.INSTANCE;
		this.client = client;
		this.room = room;
	}
	
	//Getters
	public Client getClient() {return client;}
	public Room getRoom() {return room;}

	//Setters
	public void setClient(Client client) {this.client = client;}
	public void setRoom(Room room) {this.room = room;}
	
	
	public void run() {
		
		thread = new Thread(() ->  {
			while(true) {
				ISendable pack = messageService.recieve(client);
				if (null == pack) {continue;}
				
				//Only print the message, if the message's room is the same room 
				//	with the current room of the client
				if (ISendable.TYPE_ROOM == pack.getType()) {
					Message message = (Message) pack;
					if (message.getRoom().getId().equals(room.getId())) {
						//TODO:
						
					}
				}
			}
		});
		thread.start();
		
	}
	

	
	
}
