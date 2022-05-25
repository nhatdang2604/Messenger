package com.nhatdang2604.server.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.utils.HibernateUtil;

public enum ClientDAO {
	
	INSTANCE;
	
	private SessionFactory factory;
	
	private ClientDAO() {
		factory = HibernateUtil.INSTANCE.getSessionFactory();
	}
	
	public Client getUserByUsername(String username) {

		Session session = factory.getCurrentSession();
		
		Client client = null;
		
		try {
			session.beginTransaction();
			
			//Parameterize the query
			String param = "username";
			
			//Make the query
			String query = "from " + Client.class.getName() + " u where u.username = :" + param;
			
			client = (Client) session.createQuery(query)
					.setParameter(param, username)
					.setMaxResults(1)
					.stream()
					.findFirst()
					.orElse(null);
			
//			if (null != client) {
//				Hibernate.initialize(Client.getUserInformation());
//			}
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
	public Client create(Client client) {
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			
			Integer id = (Integer) session.save(client);
			client = session.get(Client.class, id);
			
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
	public Client update(Client client) {
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
		
}
