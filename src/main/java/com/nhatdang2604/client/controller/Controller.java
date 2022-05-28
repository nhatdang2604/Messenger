package com.nhatdang2604.client.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.nhatdang2604.client.view.CreateRoomView;
import com.nhatdang2604.client.view.LoginView;
import com.nhatdang2604.client.view.MenuView;
import com.nhatdang2604.client.view.RegistrationView;
import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.entities.Packet;
import com.nhatdang2604.server.entities.Room;
import com.nhatdang2604.server.entities.User;

public class Controller {

	//Configuration
	private Configuration config;
	
	//Views
	private LoginView loginView;
	private RegistrationView registrationView;
	private MenuView menuView;
	private CreateRoomView createRoomView;
	
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
		createRoomView = new CreateRoomView(menuView);
		
		user = null;
		initNetwork(socket);
	}

	public void run() {
		gotoLogin();
		gotoRegistration();
		gotoMenu();
		gotoCreateRoom();
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
		
		createRoomView.getOkButton().addActionListener(event -> {
			createRoomProcess();
		});
	}
	
	private void gotoCreateRoom() {
		menuView.getCreateRoomButton().addActionListener(event -> {
			createRoomView.clear();
			createRoomView.setVisible(true);
		});
		
		gotoAddUser();
	}
	
	private void gotoAddUser() {
		createRoomView.getAddUsersButton().addActionListener(event -> {
			
			List<User> users = getAllUsers();
			users.removeIf(u -> u.equals(user));
			
			createRoomView.setTotalUsers(users);
			createRoomView.getAddUsersView().update();
			createRoomView.getAddUsersView().setVisible(true);
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
	
	private void createRoomProcess() {
		
		//Validate the form first
		if (createRoomView.areThereAnyEmptyField()) {
			createRoomView.setError(RegistrationView.EMPTY_FIELD_ERROR);
			createRoomView.clear();
			return;
		}
		
		//Get the room from the form
		Room room = createRoomView.submit();
		
		//Add the creator of the room is also a member of the room
		room.getUsers().add(user);
		
		//Create packet to send through network
		Packet packet = new Packet();
		packet.setSendable(room);
		packet.setSender(user);
		packet.setSendType(Packet.TYPE_CREATE);
		
		//Send the packet
		send(packet);
		
		//Recieved the current user from the server
		User foundUser = (User) recieved();
		
		System.out.println(foundUser.getRooms().size());
		
		if (null == foundUser) {
			//Some error happend
		} else {
			user = foundUser;
			menuView.setClient(user);
		}
		
		createRoomView.setVisible(false);
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
	
	private List<User> getAllUsers() {
		
		Packet packet = new Packet();
		packet.setSender(user);
		packet.setSendType(Packet.TYPE_GET_ALL_USERS);
		
		//Send packet to recived all users
		send(packet);
		
		//Recieved all users
		List<User> users = (List<User>) recieved();
		
		return users;
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
				loginView.setVisible(false);
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
