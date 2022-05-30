package com.nhatdang2604.client.controller;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

import com.nhatdang2604.client.view.ChatView;
import com.nhatdang2604.client.view.CreateRoomView;
import com.nhatdang2604.client.view.LoginView;
import com.nhatdang2604.client.view.MenuView;
import com.nhatdang2604.client.view.RegistrationView;
import com.nhatdang2604.client.view.component.action.ChatLinkListener;
import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.entities.Message;
import com.nhatdang2604.server.entities.Packet;
import com.nhatdang2604.server.entities.Room;
import com.nhatdang2604.server.entities.User;
import com.nhatdang2604.server.utils.FileUtil;

public class Controller {

	//Configuration
	private Configuration config;
	
	//Download file buffer
	private File buffer;
	
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
	
	private void chatThreadImpl() {
		
		//Run until the client app is closed
		while(isOpenChatView) {
			Packet packet = (Packet) recieved();
			Message message = (Message) packet.getSendable();
			
			if (Packet.TYPE_GET == packet.getSendType()) {
				if (chatView.getRoom().getId().equals(message.getRoom().getId())) {
					chatView.addNewMessage(message);
				}
			} else if (Packet.TYPE_DOWNLOAD_FILE == packet.getSendType()) {
				File file = message.getFile();		//Extract the recieved file
				FileUtil.copyFile(file, buffer);	//Copy the file to the buffer
				buffer = null;						//Clear the buffer after copy file
			}
		}
		
	}
	
	public Controller(Socket socket) {
		this.socket = socket;
		config = Configuration.INSTANCE;
		loginView = new LoginView();
		registrationView = new RegistrationView(loginView);
		menuView = new MenuView();
		createRoomView = new CreateRoomView(menuView);
		chatView = new ChatView(menuView);
		isOpenChatView = false;
		user = null;
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
		menuView.getLogoutButton().addActionListener(event -> {
			exitProcess();
			
			//Setup owner of the controller to null;
			user = null;
			
			//Create new connection
			try {
				socket = new Socket(config.getIp(), config.getPort());
				System.out.println(socket);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Close menuView
			menuView.setVisible(false);
			
			//Open loginView
			loginView.setVisible(true);
			
		});
		
		//Setup for exit, after closing login view
		loginView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				exitProcess();
			}
		});
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
				System.out.println("TRY TO STOPPPPPPPPP");
				
				//Send a dummy message to the socket, forcing the recived() stop blocking and end the chatThread
				Message dummy = new Message();
				Room dummyRoom = new Room();
				dummy.setRoom(dummyRoom);
				
				//Send the message to the server
				Packet packet = new Packet();
				packet.setSendType(Packet.TYPE_STOP_THREAD);
				packet.setSendable(dummy);
				send(packet);
				
			}
		});
	}
	

	private void sendMessageProcessSetup() {
		chatView.getSendButton().addActionListener(event -> {
			
			String content = chatView.getTypeField().getText().trim();
			
			//Not send empty text
			if (null != content && !content.equals("")) {
				
				//Set data of the message
				Message message = new Message();
				message.setUser(user);
				message.setDataType(Message.TYPE_TEXT);
				message.setRoom(chatView.getRoom());
		
				message.setContent(content);
				
				//Send the message to the server
				Packet packet = new Packet();
				packet.setSendType(Packet.TYPE_POST);
				packet.setSendable(message);
				packet.setSender(user);
				send(packet);
				
			}
			
			//Clear the type field
			chatView.getTypeField().setText("");
		});
		
		//Setup for send file button
		chatView.getSendFileButton().addActionListener(event -> {
			
			//Open file chooser
			int state = chatView.getFileChooser().showSaveDialog(chatView);
			if (JFileChooser.APPROVE_OPTION == state) {
				File file = chatView.getFileChooser().getSelectedFile();
				
				//Set data of the message
				Message message = new Message();
				message.setUser(user);
				message.setDataType(Message.TYPE_FILE);
				message.setRoom(chatView.getRoom());
				message.setFile(file);
				
				//Send the message to the server
				Packet packet = new Packet();
				packet.setSendType(Packet.TYPE_POST);
				packet.setSendable(message);
				packet.setSender(user);
				send(packet);
			}
		});
		
		
		//Setup for downloading file
		chatView.getMessagePane().addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				JTextPane messagePane = chatView.getMessagePane();
				StyledDocument doc = messagePane.getStyledDocument();
				Element element = doc.getCharacterElement(messagePane.viewToModel(e.getPoint()));
	            AttributeSet as = element.getAttributes();
	            ChatLinkListener listener = (ChatLinkListener)as.getAttribute(ChatView.CHAT_LINK_ACTION_NAME);
	            
	            //Try to send the message id, to download the file
	            if(listener != null) {
	            	
	            	//Original file's name
	            	String name = listener.getMessage().getContent();
	            	
	            	JFileChooser fileChooser = chatView.getFileChooser();
	            	
	            	//Open file chooser
	            	fileChooser.setSelectedFile(new File(name));
	    			int state = fileChooser.showSaveDialog(chatView);
	    			if (JFileChooser.APPROVE_OPTION == state) {
	    				
	    				//Create the file to transfer data into
	    				buffer = fileChooser.getSelectedFile();
	    				try {
							buffer.createNewFile();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
	    				
	    				//Make the message to send to server
	    				Message message = new Message();
	    				message.setId(listener.getMessage().getId());
	
	    				//Pack the message
	    				Packet packet = new Packet();
	    				packet.setSendType(Packet.TYPE_DOWNLOAD_FILE);
	    				packet.setSendable(message);
	    				
	    				//Send
	    				send(packet);
	    			}
	                
	            }
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				//do nothing
			}

			@Override
			public void mouseExited(MouseEvent e) {
				//do nothing
			}

			@Override
			public void mousePressed(MouseEvent e) {
				//do nothing
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				//do nothing
			}
			
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
		chatView.clearChat();
		chatView.setRoom(room);
		
		//Start the thread to recieved messages
		chatThread = new Thread(() -> {chatThreadImpl();});
		chatThread.start();
		
		//Open the chat room
		chatView.open();
	}
	
	private void createRoomProcess() {
		
		//Validate the form first
		if (createRoomView.areThereAnyEmptyField()) {
			createRoomView.setError(CreateRoomView.EMPTY_FIELD_ERROR);
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
		
//		System.out.println(foundUser.getRooms().size());
		
//		if (null == foundUser) {
//			//Some error happend
//		} else {
//			user = foundUser;
//			menuView.setClient(user);
//			chatView.setUser(user);
//		}
//		
		user = foundUser;
		menuView.setClient(user);
		chatView.setUser(user);
		
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
			
			User foundClient = (User) recieved();
			
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
		
		isOpenChatView = false;
		
		//Create packet to send through network
		Packet packet = new Packet();
		packet.setSendType(Packet.TYPE_LOGOUT);
						
		//Send the packet
		send(packet);
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
