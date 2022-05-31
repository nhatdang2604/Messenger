package com.nhatdang2604.server.service;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.entities.ISendable;
import com.nhatdang2604.server.entities.Message;
import com.nhatdang2604.server.entities.Packet;
import com.nhatdang2604.server.entities.Room;
import com.nhatdang2604.server.entities.User;

public enum ServerService {

	INSTANCE;
	
	private boolean exitFlag = false;
	
	//Network stuffs
	private ServerSocket serverSocket;
	private Collection<Socket> connectedSockets;
	private Map<Integer, Set<Socket>> connectedUsers;	//Integer is id of the user
	
	//Configs
	private Configuration configuration;
	
	//Services
	private MessageService messageService;
	private RoomService roomService;
	private UserService userService;
	private FileService fileService;
	
	private ServerService() {
		configuration = Configuration.INSTANCE;
		messageService = MessageService.INSTANCE;
		roomService = RoomService.INSTANCE;
		userService = UserService.INSTANCE;
		fileService = FileService.INSTANCE;
		
		connectedSockets = Collections.synchronizedCollection(new ArrayList<>());
		connectedUsers = new ConcurrentHashMap<>();
		
		//Try to create the socket via the port from configuration
		try {
			serverSocket = new ServerSocket(configuration.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Set<User> getAllTheOnlineMemberWhoAreTheSameRoomWithGivenUser(User user) {
		
		Set<User> buffer = new HashSet<>();
		Set<Room> rooms = user.getRooms();
		for (Room room: rooms) {
			Set<User> members = room.getUsers();
			for (User member: members) {
			
				//Add user to buffer, if the user is already connected to the server
				if (connectedUsers.containsKey(member.getId())) {
					buffer.add(member);
				}
			}
		}
		
		return buffer;
	}
	
	public void authenticate(Packet packet, Socket socket) {
		
		User recievedUser = (User) packet.getSendable();
		User sentUser = null;
		
		if (null != recievedUser) {
			sentUser = userService.login(recievedUser);
		}
		
		if (null != sentUser) {
			
			//Make a copy identify for user
			Integer key = sentUser.getId();		
			
			if (!connectedUsers.containsKey(key)) {
				
				//Create <key, value> for the first time
				Set<Socket> buffer = new HashSet<>();
				buffer.add(socket);
				
				connectedUsers.put(key, buffer);
			} else {
				
				//Update <key, value>
				Set<Socket> buffer = connectedUsers.get(key);
				buffer.add(socket);
				
				connectedUsers.put(key, buffer);
				
			}
		}
		
		//Sent the auth status for the user
		send(sentUser, socket);
		
		//If the user login successfully, sent online status for all members which are in the same room
		if (null != sentUser) {
			
			Set<User> buffer = getAllTheOnlineMemberWhoAreTheSameRoomWithGivenUser(sentUser);
			buffer.remove(sentUser);
			
			//Send signal for all the user in the buffer
			Packet pac = new Packet();
			pac.setSendType(Packet.TYPE_LOGIN);
			sentUser.setRooms(null);	//no need room
			pac.setSendable(sentUser);
			for (User user: buffer) {
				Set<Socket> sockets = connectedUsers.get(user.getId());
				sockets.forEach(soc -> send(pac, soc));
			}
		}
		
	}
	
	public void sendAllUsers(Socket socket) {
		
		List<User> allUsers = userService.findAllUser();
		send(allUsers, socket);
		
	}
	
	public void registrate(Packet packet, Socket socket) {
		
		
		User recievedUser = (User) packet.getSendable();
		User sentUser = null;
		
		if (null != recievedUser) {
			sentUser = userService.registrate(recievedUser);
			System.out.println("Registrate: " + sentUser);
		}
		
		send(sentUser, socket);
	
	}
	
	public void openRoom(Packet packet, Socket socket) {
		
		//Send the room, if the client open an existed room
		Room room = (Room) packet.getSendable();
		
		if (null != room) {
			room = roomService.findRoomById(room.getId());
		}
		
		send(room, socket);
	}
	
	public void createRoom(Packet packet, Socket socket) {
		
		Room room = (Room) packet.getSendable();
		
		if (null != room) {
			room = roomService.createRoom(room);
			room = roomService.findRoomById(room.getId());
		}
		
//		//Send the current user state to all the members who are online
//		Set<User> members = room.getUsers();
//		for (User member: members) {
//			if (connectedUsers.containsKey(member.getId())) {
//				
//				Set<Socket> sockets = connectedUsers.get(member.getId());
//				for (Socket soc: sockets) {
//					send(member, soc);
//				}
//				
//			}
//		}
		
		//Send the current user state back to the sender
		User sender = packet.getSender();
		Set<User> members = room.getUsers();
		for (User member: members) {
			if (sender.equals(member)) {
				sender = member;
				break;
			}
		}
		Set<Socket> sockets = connectedUsers.get(sender.getId());
		for (Socket soc: sockets) {
			send(sender, soc);
		}

		
	}
	
	public void sendMessage(Packet packet, Socket socket) {

		//Send the message, if the client is messenging
		Message message = (Message) packet.getSendable();
		Packet sendPacket = new Packet();
		
		//Process if the message is a file
		if (Message.TYPE_FILE == message.getDataType()) {
			
			message = fileService.recievedFileFromClient(message);
			sendPacket.setSendType(Packet.TYPE_GET);
			
		} else if (Message.TYPE_TEXT == message.getDataType()) {
			
			//Save the message to database
			message = messageService.createMessage(message);
			sendPacket.setSendType(Packet.TYPE_GET);
		}
		
		
		sendPacket.setSendable(message);
		
		//Get all the members in the room
		Set<User> members = message.getRoom().getUsers();
		
		//Send message to all the member in the room
		for (User member: members) {
			if (connectedUsers.containsKey(member.getId())) {
				
				Set<Socket> sockets = connectedUsers.get(member.getId());
				for (Socket soc: sockets) {
					
					send(sendPacket, soc);
				}
				
			}
		}
	}
	
	public void download(Packet packet, Socket socket) {
		Message message = (Message) packet.getSendable();
		Integer id = message.getId();
		
		//Get the find with the given id
		File file = fileService.findFileById(id);
		
		//Bind the file to the message to sent
		message.setFile(file);
		
		//Pack the message to send back to client
		packet.setSendable(message);
		send(packet, socket);
		
	}
	
	//Logout a user with a given id
	public void logout(Integer id) {
		User user = userService.findUserById(id);
		logout(user);
	}
	
	public void logout(User user) {
		userService.logout(user);
	}
	
	public void logout(Packet packet, Socket socket) {
		
		//Exit while in login view
		if (null != packet.getSender()) {
			
			User sender = packet.getSender();
			Integer key = sender.getId();
			
			//Exit after authenticate
			Set<Socket> sockets = connectedUsers.get(key);
			
			sockets.removeIf(soc-> soc.equals(socket));
			
			if (sockets.isEmpty()) {
				connectedUsers.remove(key);
				logout(key);
			} else {
				connectedUsers.put(key, sockets);
			}
			
			//Send signal to all connected user who are member of the same room with the signout user
			Set<User> buffer = getAllTheOnlineMemberWhoAreTheSameRoomWithGivenUser(sender);
			
			Packet pac = new Packet();
			pac.setSendType(Packet.TYPE_LOGOUT);
			sender.setRooms(null);	//no need room
			pac.setSendable(sender);
			for (User user: buffer) {
				Set<Socket> socs = connectedUsers.get(user.getId());
				socs.forEach(soc -> send(pac, soc));
			}
			
		} 
		
//		Iterator<Map.Entry<Integer, Set<Socket>>> iterator = connectedUsers.entrySet().iterator();
//		
//		while (iterator.hasNext()) {
//			Map.Entry<Integer, Set<Socket>> entry = iterator.next();
//			Set<Socket> buffer = entry.getValue();
//			if(buffer.contains(socket)) {
//				buffer.removeIf(soc -> soc.equals(socket));	//avoid ConcurrentModificationExpection
//				
//				if (buffer.isEmpty()) {
//					Integer key = entry.getKey();
//					connectedUsers.remove(entry.getKey());
//					logout(key);
//				}
//				
//				break;
//			}
//		}
		
		connectedSockets.removeIf(soc -> soc.equals(socket));
		try {
			socket.close();
			System.out.println(socket + " is disconnected");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Packet recieve(Socket acceptanceSocket) {
		
		Packet packet = null;
		try {
			ObjectInputStream reader = new ObjectInputStream(acceptanceSocket.getInputStream());
			packet = (Packet) reader.readObject();

		} catch (Exception e) {
			e.printStackTrace();
			packet = null;
		}
		
		return packet;
	}
	
	public Object send(Object sendable, Socket socket) { 
		try {
			ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
			writer.writeObject(sendable);
			writer.flush();
			    
		} catch (IOException e) {
			e.printStackTrace();
			sendable = null;
		}
	       
		return sendable;
	}
	
	
	
	public void communicate(Socket acceptanceSocket) {
		while (true) {
			
			if (acceptanceSocket.isClosed()) {
				break;
			}
			
			//Read the package from the client
			Packet packet = recieve(acceptanceSocket);
			if (null == packet) {continue;}
			
			//Response base on the type of the packge
			if (null == packet.getSendable()) {
				
				//Get all users
				if (Packet.TYPE_GET_ALL_USERS == packet.getSendType()) {
					sendAllUsers(acceptanceSocket);
				} else if (Packet.TYPE_LOGOUT == packet.getSendType()) {
					logout(packet, acceptanceSocket);
				}
				
			} else if (ISendable.TYPE_MESSAGE == packet.getSendable().getType()) {
				
				if (Packet.TYPE_DOWNLOAD_FILE == packet.getSendType()) {
					download(packet, acceptanceSocket);
				} else if (Packet.TYPE_POST == packet.getSendType()) {
					sendMessage(packet, acceptanceSocket);
				} else if (Packet.TYPE_STOP_THREAD == packet.getSendType()) {
					Message dummy = (Message) packet.getSendable();
					Packet pac = new Packet();
					pac.setSendType(Packet.TYPE_STOP_THREAD);
					pac.setSendable(dummy);
					send(pac, acceptanceSocket);
				}
				
			} else if (ISendable.TYPE_ROOM == packet.getSendable().getType()) {
				
				if (Packet.TYPE_GET == packet.getSendType()) {
					openRoom(packet, acceptanceSocket);
				} else if (Packet.TYPE_CREATE == packet.getSendType()) {
					createRoom(packet, acceptanceSocket);
				}
				
			} else if (ISendable.TYPE_USER == packet.getSendable().getType()) {
				
				//Authenticate
				if (Packet.TYPE_POST == packet.getSendType()) {
					authenticate(packet, acceptanceSocket);
				} else if (Packet.TYPE_CREATE == packet.getSendType()) {
					registrate(packet, acceptanceSocket);
				}
				
			}
		}
	}
	
	public void run() {
		
		while (true) {
			
			//Exit condition
			if (exitFlag) 
			{
				break;
			}
			
			System.out.println("Waiting for new client...");
			try {
				Socket socket = (Socket) serverSocket.accept();
				
				if (null != socket) {
					connectedSockets.add(socket);
					System.out.println(socket + " join the server");
					Thread thread = new Thread(() -> {communicate(socket);});
					thread.start();
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
		}
	
	}

	public void disconnectAllSockets() {
		for (Socket socket: connectedSockets) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for(Iterator<Map.Entry<Integer, Set<Socket>>> it = connectedUsers.entrySet().iterator(); it.hasNext(); ) {
		    Map.Entry<Integer, Set<Socket>> entry = it.next();
		    logout(entry.getKey());
		}
		
	}
	
	public void stop() {
		try {
			disconnectAllSockets();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			exitFlag = true;
		}
	}
}
