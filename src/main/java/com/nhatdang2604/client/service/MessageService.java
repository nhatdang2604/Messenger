package com.nhatdang2604.client.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.nhatdang2604.server.dao.MessageDAO;
import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.Message;
import com.nhatdang2604.server.model.entities.Room;

public enum MessageService {

	INSTANCE;
	
	private MessageService() {
		//do nothing
	}

	//Recieve a message from a client
	public Message recieve(Client client) {
		
		Message message = null;
		try {
			
			ObjectInputStream reader = client.getReader();
			message = (Message) reader.readObject();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return message;
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
