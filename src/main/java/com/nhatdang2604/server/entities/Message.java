package com.nhatdang2604.server.entities;

import java.io.File;
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
import javax.persistence.Transient;

@Entity
@Table(name = "message")
public class Message implements ISendable, Serializable, Comparable<Message> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -20568082083086111L;

	//Types of message
	public static final int TYPE_TEXT = 0;
	public static final int TYPE_FILE = 1;
	
	@Override
	public int getType() {return ISendable.TYPE_MESSAGE;}
	
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
			name = "user_id", 
			referencedColumnName = "id",
			nullable = true)
	private User user;
	
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
	
	@Column(name = "data_type")
	private Integer dataType;
	
	@Column(name = "date_time")
	private LocalDateTime datetime;
	
	@Transient
	private File file;
	
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public LocalDateTime getDateTime() {
		return datetime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.datetime = dateTime;
	}

	public LocalDateTime getDatetime() {
		return datetime;
	}

	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
}
