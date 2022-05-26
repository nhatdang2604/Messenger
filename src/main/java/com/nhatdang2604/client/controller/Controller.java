package com.nhatdang2604.client.controller;

import java.io.IOException;

import com.nhatdang2604.client.view.LoginView;
import com.nhatdang2604.client.view.MenuView;
import com.nhatdang2604.client.view.RegistrationView;
import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.model.entities.Client;

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
	
	private void gotoRegistration() {
		loginView.getRegistrateButton().addActionListener(event -> {
			registrationView.clear();
			registrationView.setVisible(true);
		});
	}
	
	private void gotoMenu() {
		loginView.getLoginButton().addActionListener(event -> {
			loginProcess();
		});
	}
	
	private void loginProcess() {
		
		Client loginClient = loginView.submit();
		loginClient.connect(config.getIp(), config.getPort());
	
		
		try {
			loginClient.getWriter().writeObject(loginClient);
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
