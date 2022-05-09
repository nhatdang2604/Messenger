package com.nhatdang2604.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "message")
public class Message implements Serializable, Comparable<Message> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -20568082083086111L;

	//Types of message
	public static final int TYPE_TEXT = 0;
	public static final int TYPE_FILE = 1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne(
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE,
					CascadeType.DETACH,
					CascadeType.REFRESH},
			fetch = FetchType.EAGER)
	@JoinColumn(
			name = "client_id", 
			referencedColumnName = "id",
			nullable = true)
	private Client client;
	
	@ManyToOne(
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE,
					CascadeType.DETACH,
					CascadeType.REFRESH},
			fetch = FetchType.EAGER)
	@JoinColumn(
			name = "room_id", 
			referencedColumnName = "id",
			nullable = true)
	private Room room;
	
	@Column(name = "content")
	private String content;
	
	@Column(name = "type")
	private Integer type;
	
	@Column(name = "date_time")
	private LocalDateTime datetime;
	
	public Message() {
		//do nothing
	}

	//Compare for using Set, by implementing Comparable
	public int compareTo(Message another) {
		if (null == another) return 1;
					
		int result = 
				(this.getId() > another.getId()?1:
					(this.getId() < another.getId()? -1: 0));
					
		return result;
	}


	//Getters and setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public LocalDateTime getDateTime() {
		return datetime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.datetime = dateTime;
	}
	
}