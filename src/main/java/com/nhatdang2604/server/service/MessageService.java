package com.nhatdang2604.server.service;

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
	
	private MessageDAO messageDAO;
	
	
	private MessageService() {
		
		//Inject the session factory from hibernate utility
		messageDAO = MessageDAO.INSTANCE;
	}

	//Recieve a message from a client
	public Message recieve(Client client) {
		
		Socket socket = client.getSocket();
		Message message = null;
		try {
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			try {
				message = (Message) inStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	//Send a message, and inject the client, room into the message
	public Message send(Message message, Client client, Room room) {
		
		Socket socket = client.getSocket();
		
		//Inject attributes for the message
		message.setClient(client);
		message.setRoom(room);
		
		ObjectOutputStream output;
		try {
			output = new ObjectOutputStream(socket.getOutputStream());
			output.writeObject(message);
		    output.flush();
		    message = messageDAO.createMessage(message);
		    
		} catch (IOException e) {
			e.printStackTrace();
			message = null;
		}
       
		
		return message;
	}
	
}
