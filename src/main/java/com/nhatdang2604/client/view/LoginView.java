package com.nhatdang2604.client.view;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.nhatdang2604.server.entities.User;
import com.nhatdang2604.server.utils.HashingUtil;

public class LoginView extends JFrame {
	
	//Main panel to add components into
	private JPanel contentPane;
	
	//Components
	private JButton btnLogin;				//Login button
	private JButton btnRegistrate;			//Registrate button
	private JTextField txtUsername;			//Username text field
	private JPasswordField passtxtPassword;	//Password text field
	private JLabel jlbUsername;				//Label for username field
	private JLabel jlbPassword;				//Label for password field
	private JCheckBox chckbxShowPassword;	//Checkbox to show/hide password
	
	//Display when: 
	//	1.) Wrong password or username: Type = 0
	private JLabel jlbWarningText;		
	
	public static final int NO_ERROR = 0;
	public static final int WRONG_ACCOUNT_ERROR = 1;
	private static final String[] ERRORS = {
		"",
		"Sai mật khẩu hoặc tên đăng nhập"
	};
	
	//Change the warning text of jlbWarningText;
	public LoginView setError(int errorCode) {
		if (0 <= errorCode && errorCode < ERRORS.length) {
			jlbWarningText.setText(ERRORS[errorCode]);
		}
		
		return this;
	}
	
	//Create and add Show/Hide feature for chckbxShowPassword
	private void initCheckbox() {
		chckbxShowPassword = new JCheckBox("Hiện mật khẩu");
		chckbxShowPassword.addActionListener((event) -> {
				
			//If the check box is selected
			//	=> Show password of passtxtPassword
			if (chckbxShowPassword.isSelected()) {
				passtxtPassword.setEchoChar((char)0);
			} else {
					
				//If the check box is not selected
				//	=> Hide password of passtxtPassword
				// by setting echo character with (char)'\u2022'
				passtxtPassword.setEchoChar('\u2022');
			}
				
		});

	}
	
	//Create all components;
	private void initComponents() {
		btnLogin = new JButton("Đăng nhập");
		btnRegistrate = new JButton("Đăng ký");
		
		jlbUsername = new JLabel("Tên đăng nhập:");
		jlbPassword = new JLabel("Mật khẩu:");
		
		jlbWarningText = new JLabel();					//No warning when start login form
		jlbWarningText.setForeground(Color.RED);		//Warning have red text
		
		txtUsername = new JTextField();
		passtxtPassword = new JPasswordField();
		initCheckbox();									//Create and add Show/Hide feature for chckbxShowPassword
	}
	
	//Set size and location of each component
	private void setComponentSizeAndLocation() {
		btnLogin.setBounds(144, 128, 100, 30);
		btnRegistrate.setBounds(249,128,100,30);
		
		jlbUsername.setBounds(50,46,100,30);
	    jlbPassword.setBounds(50,87,100,30);
	    jlbWarningText.setBounds(130, 11, 190, 30);
	    txtUsername.setBounds(148,46,200,30);
	    passtxtPassword.setBounds(148,87,200,30);
	    chckbxShowPassword.setBounds(35, 132, 110, 23);
	}
	
	//Connect all components into contentPane
	private void addComponents() {
		contentPane.add(btnRegistrate);
		contentPane.add(btnLogin);
		contentPane.add(jlbUsername);
		contentPane.add(jlbPassword);
		contentPane.add(jlbWarningText);
		contentPane.add(txtUsername);
		contentPane.add(passtxtPassword);
		contentPane.add(chckbxShowPassword);
	} 
	
	//Create and set properties of the login form
	private void initLoginView() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(10, 10, 420, 208);
		this.setTitle("Đăng nhập");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		this.setResizable(false);
	}
	
	public LoginView() {
		initLoginView();
		initComponents();
		setComponentSizeAndLocation();
		addComponents();
		
		this.setContentPane(contentPane);
	}

	public User submit() {
		
		String username = txtUsername.getText().trim();
		String password = (null == passtxtPassword.getPassword()?
				HashingUtil.passwordEncryption(""):
				HashingUtil.passwordEncryption(new String(passtxtPassword.getPassword())));
		
		User client = new User();
		client.setUsername(username);
		client.setEncryptedPassword(password);
		
		return client;
	}
	
	public JButton getLoginButton() {return btnLogin;}
	public JButton getRegistrateButton() {return btnRegistrate;}
	
	public void clear() {
		txtUsername.setText("");
		passtxtPassword.setText("");
	}
	
	public static void main(String[] args) {
		
		LoginView view = new LoginView();
		RegistrationView regView = new RegistrationView(view);
		view.getRegistrateButton().addActionListener(event -> {
			regView.setVisible(true);
		});
		
		view.setVisible(true);
	}
}
