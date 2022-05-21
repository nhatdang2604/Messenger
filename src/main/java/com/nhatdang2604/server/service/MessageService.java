package com.nhatdang2604.server.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.nhatdang2604.server.dao.MessageDAO;
import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.Message;

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
			message = (Message) inStream.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	//Send a message into the database and broadcasting
	public Message send(Message message) {
		
		Socket socket = message.getClient().getSocket();
		
		try {
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
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
