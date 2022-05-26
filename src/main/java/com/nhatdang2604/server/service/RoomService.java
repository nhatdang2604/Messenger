package com.nhatdang2604.server.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.nhatdang2604.server.dao.RoomDAO;
import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.ISendable;
import com.nhatdang2604.server.model.entities.Message;
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
	
	public Room findRoomById(Integer id) {
		return roomDAO.find(id);
	}
	
	//Recieve a message from a client
	public Room recieve(Socket socket) {
			
		Room room = null;
		try {
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			ISendable pack = (ISendable) inStream.readObject();
			if (ISendable.TYPE_ROOM == pack.getType()) {
				room = (Room) inStream.readObject();
			    room = findRoomById(room.getId());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return room;
	}
	
}
