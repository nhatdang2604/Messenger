package com.nhatdang2604.server.service;

import com.nhatdang2604.server.dao.ClientDAO;
import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.Message;
import com.nhatdang2604.server.model.entities.Room;
import com.nhatdang2604.server.model.formModel.LoginFormModel;

public enum ClientService {

	INSTANCE;
	
	private ClientDAO clientDAO;
	private MessageService messageService;
	
	private ClientService() {
		
		//Inject the session factory from hibernate utility
		clientDAO = ClientDAO.INSTANCE;
		messageService = MessageService.INSTANCE;
	}
	
	//Helper to compare a model from login form and client
	public boolean isAuthenticated(LoginFormModel model, Client client) {
		
		if (null == model || null == client) return false;
		
		//Compare the username
		boolean isTheSameUsername = client.getUsername().equals(model.getUsername());
			
		//Return false if the username is different
		if (!isTheSameUsername) return false;
			
		//Compare the password hashing
		boolean isTheSamePassword = 
				client.getEncryptedPassword()
				.equals(model.getEncryptedPassword());
			
		return isTheSamePassword;
	}

	//Login by submitting model from the login form
	public Client login(LoginFormModel model) {
		
		Client client = clientDAO.getUserByUserLoginModel(model);
		client = (isAuthenticated(model, client)?client:null);
		return client;
	}

	public Message send(Client client, Room room, Message message) {
		
		message.setClient(client);
		message.setRoom(room);
		
		return messageService.send(message);
	}
	
	
	//Register a client account
	public Client register(Client client) {
		return clientDAO.createClient(client);
	}
	
	//Run the server
	public int run() {
		int errorCode = 0;
		
		
		
		return errorCode;
	}
	
}
