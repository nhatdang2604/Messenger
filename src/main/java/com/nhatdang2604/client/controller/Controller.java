package com.nhatdang2604.client.controller;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import com.nhatdang2604.client.view.ChatView;
import com.nhatdang2604.client.view.CreateRoomView;
import com.nhatdang2604.client.view.LoginView;
import com.nhatdang2604.client.view.MenuView;
import com.nhatdang2604.client.view.RegistrationView;
import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.entities.Message;
import com.nhatdang2604.server.entities.Packet;
import com.nhatdang2604.server.entities.Room;
import com.nhatdang2604.server.entities.User;

public class Controller {

	//Configuration
	private Configuration config;
	
	//Flag
	private boolean isOpenChatView;
	
	//Views
	private LoginView loginView;
	private RegistrationView registrationView;
	private MenuView menuView;
	private CreateRoomView createRoomView;
	private ChatView chatView;
	
	//The login user
	private User user;
	
	//Network stuffs
	private Socket socket;
	private Thread chatThread;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	
	private Queue<Message> messageQueue;
	
	private void initChatThread() {
		messageQueue = new PriorityBlockingQueue<>();
		this.chatThread = new Thread(() -> {
			
			//Run until the client app is closed
			while(isOpenChatView) {
				Message message = (Message) recieved();
				
				if (message.getRoom().getId().equals(chatView.getRoom().getId())) {
					chatView.addNewMessage(message);
				}
				
			}
			
		});
	}
	
	private void initNetwork(Socket socket) {
		this.socket = socket;
	}
	
	public Controller(Socket socket) {
		config = Configuration.INSTANCE;
		loginView = new LoginView();
		registrationView = new RegistrationView(loginView);
		menuView = new MenuView();
		createRoomView = new CreateRoomView(menuView);
		chatView = new ChatView(menuView);
		isOpenChatView = false;
		user = null;
		initNetwork(socket);
		initChatThread();
	}

	public void run() {
		gotoLogin();
		gotoRegistration();
		gotoMenu();
		gotoCreateRoom();
		gotoRoom();
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
	
	private void gotoRoom() {
		menuView.getRoomTable().getJoinButton().addActionListener(event -> {
			isOpenChatView = true;
			joinRoomProcess();
		});
		sendMessageProcessSetup();
		exitRoomSetup();
	}
	
	private void gotoMenu() {
		loginView.getLoginButton().addActionListener(event -> {
			loginProcess();
		});
		
		createRoomView.getOkButton().addActionListener(event -> {
			createRoomProcess();
		});
		
		//Setup for exit, after close menu
		menuView.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent we) {
		    	logoutProcess();
		    	exitProcess();
		    }
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
	
	private void exitRoomSetup() {
		chatView.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				isOpenChatView = false;
				chatThread.stop();
			}
		});
	}
	
	private void sendMessageProcessSetup() {
		chatView.getSendButton().addActionListener(event -> {
			
			//Set data of the message
			Message message = new Message();
			message.setUser(user);
			message.setDataType(Message.TYPE_TEXT);
			message.setRoom(chatView.getRoom());
	
			String content = chatView.getTypeField().getText();
			message.setContent(content);
			
			//Send the message to the server
			Packet packet = new Packet();
			packet.setSendType(packet.TYPE_POST);
			packet.setSendable(message);
			packet.setSender(user);
			send(packet);
			
		});
		
	}
	
	private void joinRoomProcess() {
		
		//Get the room to join
		Room room = menuView.getRoomTable().getSelectedRoom();
		
		//Packet to send through the internet;
		Packet packet = new Packet();
		packet.setSendable(room);
		packet.setSendType(Packet.TYPE_GET);
		
		
		//Send the packet
		send(packet);
		
		//Recieved the room from the server
		// Recieved until get a room
		while (true) {
			try {
				room = (Room) recieved();
				break;
			} catch (Exception e) {
				// do nothing
			}
		}
		//Set data of the room the view
		chatView.setRoom(room);
		
		//Start the thread to recieved messages
		chatThread.start();
		
		//Open the chat room
		chatView.open();
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
		
		User foundUser = null;
		
		//Recieved the current user from the server
		// Recieved until get a user
		while (true) {
			try {
				foundUser = (User) recieved();
				break;
			} catch (Exception e) {
				// do nothing
			}
		}
		
		System.out.println(foundUser.getRooms().size());
		
		if (null == foundUser) {
			//Some error happend
		} else {
			user = foundUser;
			menuView.setClient(user);
			chatView.setUser(user);
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
		
		List<User> users = null;
		
		//Recieved all users
		while (true) {
			try {
				users = (List<User>) recieved();
				break;
			} catch (Exception e) {
				//do nothing
			}
		}
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
				loginView.setError(LoginView.NO_ERROR);
				
				this.user = foundUser;
				menuView.setClient(foundUser);
				menuView.setVisible(true);
				loginView.setVisible(false);
				
				chatView.setUser(user);
				
			}
			
			System.out.println(foundUser);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			//Clear field in login form
			loginView.clear();
		}
	}
	
	private void exitProcess() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		isOpenChatView = false;
	}
	
	private void logoutProcess() {
		
		//Create packet to send through network
		Packet packet = new Packet();
		packet.setSendType(Packet.TYPE_LOGOUT);
				
		//Send the packet
		send(packet);
	}
}
