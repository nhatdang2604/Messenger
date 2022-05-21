package com.nhatdang2604.client.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.ISendable;
import com.nhatdang2604.server.model.entities.Message;
import com.nhatdang2604.server.model.entities.Room;

public enum MessageService {

	INSTANCE;
	
	private MessageService() {
		//do nothing
	}

	//Recieve a package for client
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
	
	//Send a message, and inject the client, room into the message
	public Message send(Message message, Client client, Room room) {
		
		//Inject attributes for the message
		message.setClient(client);
		message.setRoom(room);
		
		try {
			ObjectOutputStream writer = client.getWriter();
			writer.writeObject(message);
		    writer.flush();
		    
		} catch (IOException e) {
			e.printStackTrace();
			message = null;
		}
       
		return message;
	}
	
}
