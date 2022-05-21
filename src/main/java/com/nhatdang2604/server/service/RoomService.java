package com.nhatdang2604.server.service;

import java.util.List;

import com.nhatdang2604.server.dao.RoomDAO;
import com.nhatdang2604.server.model.entities.Room;

public enum RoomService {

	INSTANCE;
	
	private RoomDAO roomDAO;
	
	private RoomService() {
		
		roomDAO = RoomDAO.INSTANCE;
	}
	
	public Room createRoom(Room room) {
		return roomDAO.create(room);
	}
	
	public List<Room> findAllRooms() {
		return roomDAO.findAll();
	}
}
