package com.nhatdang2604.server.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.Room;
import com.nhatdang2604.server.utils.HibernateUtil;

public enum RoomDAO {
	
	INSTANCE;
	
	private SessionFactory factory;
	
	private RoomDAO() {
		factory = HibernateUtil.INSTANCE.getSessionFactory();
	}

	
	//Create a room
	public Room createRoom(Room room) {
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			
			Integer id = (Integer) session.save(room);
			room = session.get(Room.class, id);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
		return room;
	}
	
	
}
