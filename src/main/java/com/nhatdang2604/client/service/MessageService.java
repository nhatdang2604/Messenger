package com.nhatdang2604.client.service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.nhatdang2604.server.entities.Message;
import com.nhatdang2604.server.entities.Room;
import com.nhatdang2604.server.entities.User;

public enum MessageService {

	INSTANCE;
	
	private MessageService() {
		//do nothing
	}
	
	//Send a message, and inject the client, room into the message
	public Message send(Message message, User user, Room room, Socket socket) {
		
		//Inject attributes for the message
		message.setUser(user);
		message.setRoom(room);
		
		try {
			ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
			writer.writeObject(message);
		    writer.flush();
		    
		} catch (IOException e) {
			e.printStackTrace();
			message = null;
		}
       
		return message;
	}
	
}
