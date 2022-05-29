package com.nhatdang2604.server.service;

import java.util.List;

import com.nhatdang2604.server.dao.UserDAO;
import com.nhatdang2604.server.entities.User;

public enum UserService {

	INSTANCE;
	
	private UserDAO userDAO;
	
	private UserService() {
		
		//Inject the session factory from hibernate utility
		userDAO = UserDAO.INSTANCE;
	}
	
	//Helper to compare a user from login form and user with the same username in the database
	public boolean isAuthenticated(User givenUser, User user) {
		
		if (null == givenUser || null == user) return false;
		
		//Compare the username
		boolean isTheSameUsername = user.getUsername().equals(givenUser.getUsername());
			
		//Return false if the username is different
		if (!isTheSameUsername) return false;
			
		//Compare the password hashing
		boolean isTheSamePassword = 
				user.getEncryptedPassword()
				.equals(givenUser.getEncryptedPassword());
			
		return isTheSamePassword;
	}

	//Login by submitting model from the login form
	public User login(User user) {
		
		//Format data
		user.setUsername(user.getUsername().trim());
		user.setEncryptedPassword(user.getEncryptedPassword().trim());
		
		//Authenticate
		User foundUser = getUserByUsername(user.getUsername());
		
		System.out.println(foundUser);
		foundUser = (isAuthenticated(user, foundUser)?foundUser:null);
		
		//Make the user online, if login sucessfully
		if (null != foundUser) {
			foundUser.setIsOnline(true);
			foundUser = updateUser(foundUser);
		}
		
		return foundUser;
	}

	public User getUserByUsername(String username) {
		return userDAO.getUserByUsername(username);
	}
	
	//Register an account
	public User registrate(User user) {
		
		User foundUser = getUserByUsername(user.getUsername());
		
		//There are no user have the same username
		if (null == foundUser) {
			user = userDAO.create(user);
		} else {
			
			//Else
			user = null;
		}
		
		return user;
	}
	
	public User updateUser(User user) {
		return userDAO.update(user);
	}
	
	public User findUserById(Integer id) {
		return userDAO.find(id);
	}
	
	public List<User> findAllUser() {
		return userDAO.findAll();
	}
}
 