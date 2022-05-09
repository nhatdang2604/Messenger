package com.nhatdang2604.entities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
public class Client implements Serializable, Comparable<Client> {

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
	private BufferedReader reader;
	private BufferedWriter writer;
	
	public Client() {
		socket = null;
		reader = null;
		writer = null;
	}

	//Utilities
	
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
			
			reader = new BufferedReader(new InputStreamReader(is));
			writer = new BufferedWriter(new OutputStreamWriter(os));
			
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

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}
	
}