package com.nhatdang2604.server.service;

import com.nhatdang2604.server.dao.MessageDAO;
import com.nhatdang2604.server.model.entities.Message;

public enum MessageService {

	INSTANCE;
	
	private MessageDAO messageDAO;
	
	
	private MessageService() {
		
		//Inject the session factory from hibernate utility
		messageDAO = MessageDAO.INSTANCE;
	}

	public Message send(Message message) {
		return messageDAO.createMessage(message);
	}
	
}
