package com.nhatdang2604.client.controller;

import com.nhatdang2604.client.view.LoginView;
import com.nhatdang2604.client.view.MenuView;
import com.nhatdang2604.client.view.RegistrationView;
import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.Packet;

public class Controller {

	//Configuration
	private Configuration config;
	
	//Views
	private LoginView loginView;
	private RegistrationView registrationView;
	private MenuView menuView;
	
	public Controller() {
		config = Configuration.INSTANCE;
		loginView = new LoginView();
		registrationView = new RegistrationView(loginView);
		menuView = new MenuView();
	}

	public void run() {
		gotoLogin();
		gotoRegistration();
		gotoMenu();
	}
	
	private void gotoLogin() {
		loginView.setVisible(true);
	}
	
	private void gotoRegistration() {
		loginView.getRegistrateButton().addActionListener(event -> {
			registrationView.clear();
			registrationView.setVisible(true);
		});
		
		registrationView.getOkButton().addActionListener(event -> {
			registrateProcess();
		});
	}
	
	private void gotoMenu() {
		loginView.getLoginButton().addActionListener(event -> {
			loginProcess();
		});
	}
	
	private void registrateProcess() {
		
		//Validate for the form
		if (registrationView.areThereAnyEmptyField()) {
			registrationView.setError(RegistrationView.EMPTY_FIELD_ERROR);
			registrationView.clear();
			return;
		} else if (registrationView.isPasswordMismatch()){
			registrationView.setError(RegistrationView.PASSWORD_MISMATCH_ERROOR);
			registrationView.clear();
			return;
		}
		
		Client registrateClient = registrationView.submit();
		registrateClient.connect(config.getIp(), config.getPort());
		
		try {
			Packet packet = new Packet();
			packet.setSendable(registrateClient);
			packet.setSendType(Packet.TYPE_CREATE);
			registrateClient.getWriter().writeObject(packet);
			registrateClient.getWriter().flush();
			
			Client foundClient = (Client) registrateClient.getReader().readObject();
			
			//Login failed
			if (null == foundClient) {
				registrationView.setError(RegistrationView.EXISTED_USERNAME_ERROR);
			} else {
				
				//Registrate sucessfully
				foundClient.connect(config.getIp(), config.getPort());
				
				//Close the reg form => there is only remain the login form
				registrationView.setVisible(false);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			//Clear field in the form
			registrationView.clear();
		}
		
	}
	
	private void loginProcess() {
		
		Client loginClient = loginView.submit();
		loginClient.connect(config.getIp(), config.getPort());
	
		
		try {
			
			Packet packet = new Packet();
			packet.setSendable(loginClient);
			packet.setSendType(Packet.TYPE_POST);
			
			loginClient.getWriter().writeObject(packet);
			loginClient.getWriter().flush();
			
			Client foundClient = (Client) loginClient.getReader().readObject();
			
			//Login failed
			if (null == foundClient) {
				loginView.setError(LoginView.WRONG_ACCOUNT_ERROR);
			} else {
				
				//Login sucessfully
				foundClient.connect(config.getIp(), config.getPort());
				menuView.setClient(foundClient);
				menuView.setVisible(true);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			//Clear field in login form
			loginView.clear();
		}
	}
	
}
