package com.nhatdang2604.server.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.nhatdang2604.server.entities.FileInfo;
import com.nhatdang2604.server.entities.Message;
import com.nhatdang2604.server.entities.Room;
import com.nhatdang2604.server.entities.User;

//Using enum to implement singleton pattern, for thread safe
public enum HibernateUtil {

	//Instance of the utility
	INSTANCE;
	
	//Factory for managing session in Hibernate
	private SessionFactory sessionFactory;

	//Getter for factory
	public SessionFactory getSessionFactory() {return sessionFactory;}
	
	//Hibernate configuration file name
	//Note: the file path is /src/main/resources/hibernate.cfg.xml
	private String hibernateConfigFile = "hibernate.cfg.xml";
	
	private HibernateUtil() {
		
		try {
			
			//Build the factory, base on the properties on hibernateConfigFile
			sessionFactory = new Configuration()
					.configure(hibernateConfigFile)
					.addAnnotatedClass(Room.class)
					.addAnnotatedClass(User.class)
					.addAnnotatedClass(Message.class)
					.addAnnotatedClass(FileInfo.class)
					.buildSessionFactory();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
