package com.nhatdang2604.server.model.entities;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Client")
public class Client implements ISendable, Serializable, Comparable<Client> {

	//Data in database
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -937046062405840229L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "user_name")
	private String username;
	
	@Column(name = "encrypted_password")
	private String encryptedPassword;
	
	@Column(name = "is_online")
	private Boolean isOnline;
	
	@ManyToMany(
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE,
					CascadeType.DETACH,
					CascadeType.REFRESH},
			fetch = FetchType.EAGER)
	@JoinTable(
			name = "client_room",
			joinColumns = @JoinColumn(name = "client_id"),
			inverseJoinColumns = @JoinColumn(name = "room_id"))
	private Set<Room> rooms;
	
	//Network stuff
	private Socket socket;
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	
	public Client() {
		socket = null;
		reader = null;
		writer = null;
	}

	//Utilities
	@Override
	public int getType() {
		return ISendable.TYPE_CLIENT;
	}
	
	
	//Connect to a server with the given ip and port
	public Client connect(String ip, int port) {
		
		try {
			
			//Force the socket to close if the socket was opened
			if (null != socket) {
				if (socket.isConnected()) {
					socket.close();
					if (null != reader) {reader.close();}
					if (null != writer) {writer.close();}
				}
			}
			
			//Open the socket	
			socket = new Socket(ip, port);
			
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			reader = new ObjectInputStream(is);
			writer = new ObjectOutputStream(os);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	
	//Compare for using Set, by implementing Comparable
	public int compareTo(Client another) {
		if (null == another) return 1;
				
		int result = 
				(this.getId() > another.getId()?1:
					(this.getId() < another.getId()? -1: 0));
				
		return result;
	}
	
	//Getters and Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public Boolean getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ObjectInputStream getReader() {
		return reader;
	}

	public void setReader(ObjectInputStream reader) {
		this.reader = reader;
	}

	public ObjectOutputStream getWriter() {
		return writer;
	}

	public void setWriter(ObjectOutputStream writer) {
		this.writer = writer;
	}

	public Set<Room> getRooms() {
		return rooms;
	}

	public void setRooms(Set<Room> rooms) {
		this.rooms = rooms;
	}
	
	
}
