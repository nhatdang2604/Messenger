package com.nhatdang2604.server.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nhatdang2604.server.entities.Room;
import com.nhatdang2604.server.entities.User;
import com.nhatdang2604.server.utils.HibernateUtil;

public enum RoomDAO {
	
	INSTANCE;
	
	private SessionFactory factory;
	
	private RoomDAO() {
		factory = HibernateUtil.INSTANCE.getSessionFactory();
	}

	
	//Create a room
	public Room create(Room room) {
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
	
	public List<Room> findAll() {
		
		Session session = factory.getCurrentSession();
		
		List<Room> rooms = new ArrayList<>();

		try {
			session.beginTransaction();
			
			rooms = session.createQuery("from " + Room.class.getName()).list();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
		return rooms;
		
		
	}

	public Room find(Integer id) {
		Session session = factory.getCurrentSession();
		Room room = null;
		
		try {
			session.beginTransaction();
			
			String param = "id";
			String query = 
					"select r " +
					"from " + Room.class.getName() + " r " + 
					"left join fetch r.messages message " +
					"left join fetch r.users " +
					"left join fetch message.user " + 
					"where r.id = :" + param;
			
			try {
				room = session
						.createQuery(query, Room.class)
						.setParameter(param, id)
						.getSingleResult();
			} catch (javax.persistence.NoResultException e) {
				room = null;
			}
			
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
