package com.nhatdang2604.server.model.formModel;

//User before loging
//Usage: authentication
public class LoginFormModel {

	//Attributes
	private String username;
	private String encryptedPassword;
	
	//Constructors
	public LoginFormModel() {
		//do nothing
	}
	
	public LoginFormModel(String username, String encryptedPassword) {
		this.username = username;
		this.encryptedPassword = encryptedPassword;
	}

	//Getters
	public String getUsername() {return username;}
	public String getEncryptedPassword() {return encryptedPassword;}
	
	//Setters
	public void setUsername(String username) {this.username = username;}
	public void setEncryptedPassword(String encryptedPassword) {this.encryptedPassword = encryptedPassword;}
	
	
	
}
