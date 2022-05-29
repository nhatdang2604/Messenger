package com.nhatdang2604.server.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nhatdang2604.server.entities.User;
import com.nhatdang2604.server.utils.HibernateUtil;

public enum UserDAO {
	
	INSTANCE;
	
	private SessionFactory factory;
	
	private UserDAO() {
		factory = HibernateUtil.INSTANCE.getSessionFactory();
	}
	
	public User getUserByUsername(String username) {

		Session session = factory.getCurrentSession();
		
		User user = null;
		
		try {
			session.beginTransaction();
			
//			//Parameterize the query
//			String param = "username";
//			
//			//Make the query
//			String query = "from " + User.class.getName() + " u where u.username = :" + param;
//			
//			user = (User) session.createQuery(query, User.class)
//					.setParameter(param, username)
//					.setMaxResults(1)
//					.stream()
//					.findFirst()
//					.orElse(null);
//			
//			System.out.println(user.getId());
//			
//			//Fetch room to the user
//			if (null != user) {
//				param = "id";
//				query = "select u " +
//						"from " + User.class.getName() + " u " + 
//						"join fetch u.rooms " + 
//						"where u.id = :" + param;
//				
//				user = session
//						.createQuery(query, User.class)
//						.setParameter(param, user.getId())
//						.getSingleResult();
//				
//			}
//			
			String param = "username";
			String query = "select u " +
					"from " + User.class.getName() + " u " + 
					"left join fetch u.rooms " + 
					"where u.username = :" + param;
			
			try {
				user = session
						.createQuery(query, User.class)
						.setParameter(param, username)
						.getSingleResult();
			} catch (javax.persistence.NoResultException e) {
				user = null;
			}
	
			
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
		return user;
	}
	
	//Create a client account
	public User create(User client) {
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			
			Integer id = (Integer) session.save(client);
			client = session.get(User.class, id);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
		return client;
	}
	
	//Create a client account
	public User update(User client) {
		Session session = factory.getCurrentSession();
			
		try {
			session.beginTransaction();
				
			session.update(client);
				
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
			
		return client;
	}

	public User find(Integer id) {
		
		Session session = factory.getCurrentSession();
		User client = null;
		
		try {
			session.beginTransaction();
				
			client = session.get(User.class, id);
				
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
			
		return client;
		
		
	}

	public List<User> findAll() {
		
		Session session = factory.getCurrentSession();
		
		List<User> users = new ArrayList<>();

		try {
			session.beginTransaction();
			
			users = session.createQuery("from " + User.class.getName()).list();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
		return users;
		
	}
		
}
