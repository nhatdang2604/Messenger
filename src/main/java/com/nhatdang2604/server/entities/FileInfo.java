package com.nhatdang2604.server.entities;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "file_info")
public class FileInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 185956186102345916L;

	@Id
	@Column(name = "id")
	private Integer id;
	
	//Attributes
	@OneToOne(
			cascade = {
					CascadeType.DETACH,
					CascadeType.MERGE,
					CascadeType.PERSIST,
					CascadeType.REFRESH
			},
			fetch = FetchType.EAGER)
	@MapsId
	@JoinColumn(name = "id")
	private Message message;
	
	@Column(name = "path")
	private String path;
	
	@Column(name = "original_name")
	private String originalName;
	
	public FileInfo() {
		//do nothing
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	
	
}
