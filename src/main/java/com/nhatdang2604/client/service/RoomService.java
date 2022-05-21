package com.nhatdang2604.client.service;

import java.io.ObjectInputStream;

import com.nhatdang2604.client.view.ChatView;
import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.ISendable;
import com.nhatdang2604.server.model.entities.Message;
import com.nhatdang2604.server.model.entities.Room;

public class RoomService {

	private MessageService messageService;
	private Client client;	//the current client
	private Room room;
	private Thread thread;
	private ChatView chatView;
	
	public RoomService(Client client, Room room) {
		this.messageService = MessageService.INSTANCE;
		this.client = client;
		this.room = room;
		this.chatView = new ChatView(client);
	}
	
	//Getters
	public Client getClient() {return client;}
	public Room getRoom() {return room;}

	//Setters
	public void setClient(Client client) {this.client = client;}
	public void setRoom(Room room) {this.room = room;}
	
	public ISendable recieve(Client client) {
		
		ISendable pack = null;
		try {
			
			ObjectInputStream reader = client.getReader();
			pack = (ISendable) reader.readObject();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pack;
		
	}
	
	public void recieveMessage(ISendable pack) {
		Message message = (Message) pack;
		if (message.getRoom().getId().equals(room.getId())) {
			//TODO:
			
			//Print out the new message to the frame
			chatView.addNewMessage(message);
		}
	}
	
	public void run() {
		
		thread = new Thread(() ->  {
			while(true) {
				ISendable pack = recieve(client);
				if (null == pack) {continue;}
				
				//Only print the message, if the message's room is the same room 
				//	with the current room of the client
				if (ISendable.TYPE_MESSAGE == pack.getType()) {
					recieveMessage(pack);
				}
			}
		});
		thread.start();
		
	}
	

	
	
}
