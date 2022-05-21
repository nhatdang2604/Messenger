package com.nhatdang2604.server.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.nhatdang2604.server.dao.MessageDAO;
import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.ISendable;
import com.nhatdang2604.server.model.entities.Message;

public enum MessageService {

	INSTANCE;
	
	private MessageDAO messageDAO;
	
	
	private MessageService() {
		
		//Inject the session factory from hibernate utility
		messageDAO = MessageDAO.INSTANCE;
	}

	//Recieve a message from a client
//	public Message recieve(Socket socket) {
//		
//		Message message = null;
//		try {
//			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
//			ISendable pack = (ISendable) inStream.readObject();
//			if (ISendable.TYPE_MESSAGE == pack.getType()) {
//				message = (Message) inStream.readObject();
//			    message = messageDAO.create(message);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return message;
//	}
	
	public Message createMessage(Message message) {
		return messageDAO.create(message);
	}
	
	//Send a message to a client
	public Message send(Message message, Client client) {
		
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
