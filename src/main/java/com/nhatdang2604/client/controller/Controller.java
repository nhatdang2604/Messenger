package com.nhatdang2604.client.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.nhatdang2604.client.view.LoginView;
import com.nhatdang2604.client.view.MenuView;
import com.nhatdang2604.client.view.RegistrationView;
import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.entities.ISendable;
import com.nhatdang2604.server.entities.Message;
import com.nhatdang2604.server.entities.Packet;
import com.nhatdang2604.server.entities.User;

public class Controller {

	//Configuration
	private Configuration config;
	
	//Views
	private LoginView loginView;
	private RegistrationView registrationView;
	private MenuView menuView;
	
	//The login user
	private User user;
	
	//Network stuffs
	private Socket socket;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	
	private void initNetwork(Socket socket) {
		this.socket = socket;
		try {
//			System.out.println("Writer");
//			writer = new ObjectOutputStream(this.socket.getOutputStream());
//			this.socket.getOutputStream().flush();
//			writer.flush();
//			System.out.println("Reader");
//			
//			reader = new ObjectInputStream(this.socket.getInputStream());
//			//Read the handshake message
//			try {
//				Message message = (Message) reader.readObject();
//				System.out.println("Connected");
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//			System.out.println("End reader");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Controller(Socket socket) {
		config = Configuration.INSTANCE;
		loginView = new LoginView();
		registrationView = new RegistrationView(loginView);
		menuView = new MenuView();
		
		user = null;
		initNetwork(socket);
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
	
	private void send(Packet packet) {
		try {
			writer = new ObjectOutputStream(this.socket.getOutputStream());
			writer.writeObject(packet);
			writer.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Object recieved() {
		try {
			reader = new ObjectInputStream(this.socket.getInputStream());
			return reader.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
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
		
		User registrateUser = registrationView.submit();
		
		try {
			Packet packet = new Packet();
			packet.setSendable(registrateUser);
			packet.setSendType(Packet.TYPE_CREATE);
			
			System.out.println(packet.getSendable());
			send(packet);
			
			
			System.out.println("Send from the registration");
			User foundClient = (User) recieved();
			System.out.println("Recieved to the registration");
			
			//Login failed
			if (null == foundClient) {
				registrationView.setError(RegistrationView.EXISTED_USERNAME_ERROR);
			} else {
				
				//Close the reg form => there is only remain the login form
				registrationView.dispose();
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
		
		User loginUser = loginView.submit();
		
		try {
			
			Packet packet = new Packet();
			packet.setSendable(loginUser);
			packet.setSendType(Packet.TYPE_POST);
			
			send(packet);
			
			User foundUser = (User) recieved();
			
			//Login failed
			if (null == foundUser) {
				loginView.setError(LoginView.WRONG_ACCOUNT_ERROR);
			} else {
				
				//Login sucessfully
				this.user = foundUser;
				menuView.setClient(foundUser);
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
