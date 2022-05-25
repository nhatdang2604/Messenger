package com.nhatdang2604.client.controller;

import com.nhatdang2604.client.view.LoginView;
import com.nhatdang2604.client.view.MenuView;
import com.nhatdang2604.client.view.RegistrationView;

public class Controller {

	//Views
	private LoginView loginView;
	private RegistrationView registrationView;
	private MenuView menuView;
	
	public Controller() {
		loginView = new LoginView();
		registrationView = new RegistrationView(loginView);
		menuView = new MenuView();
	}
	
	public void gotoRegistration() {
		loginView.getRegistrateButton().addActionListener(event -> {
			registrationView.clear();
			registrationView.setVisible(true);
		});
	}
	
	
}
