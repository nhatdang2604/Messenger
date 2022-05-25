package com.nhatdang2604.server.service;

import com.nhatdang2604.server.dao.ClientDAO;
import com.nhatdang2604.server.model.entities.Client;

public enum ClientService {

	INSTANCE;
	
	private ClientDAO clientDAO;
	private MessageService messageService;
	
	private ClientService() {
		
		//Inject the session factory from hibernate utility
		clientDAO = ClientDAO.INSTANCE;
		messageService = MessageService.INSTANCE;
	}
	
	//Helper to compare a client from login form and client
	public boolean isAuthenticated(Client givenClient, Client client) {
		
		if (null == givenClient || null == client) return false;
		
		//Compare the username
		boolean isTheSameUsername = client.getUsername().equals(givenClient.getUsername());
			
		//Return false if the username is different
		if (!isTheSameUsername) return false;
			
		//Compare the password hashing
		boolean isTheSamePassword = 
				client.getEncryptedPassword()
				.equals(givenClient.getEncryptedPassword());
			
		return isTheSamePassword;
	}

	//Login by submitting model from the login form
	public Client login(Client client) {
		
		//Format data
		client.setUsername(client.getUsername().trim());
		client.setEncryptedPassword(client.getEncryptedPassword().trim());
		
		//Authenticate
		Client foundClient = clientDAO.getUserByUsername(client.getUsername());
		foundClient = (isAuthenticated(client, foundClient)?foundClient:null);
		
		//Make the user online, if login sucessfully
		if (null != foundClient) {
			foundClient.setIsOnline(true);
			foundClient = updateClient(foundClient);
		}
		
		return foundClient;
	}

	//Register a client account
	public Client register(Client client) {
		return clientDAO.create(client);
	}
	
	public Client updateClient(Client client) {
		return clientDAO.update(client);
	}
}
 