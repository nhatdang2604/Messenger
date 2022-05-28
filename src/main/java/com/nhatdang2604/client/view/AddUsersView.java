package com.nhatdang2604.client.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.nhatdang2604.client.view.component.AddUsersTable;
import com.nhatdang2604.server.entities.User;

public class AddUsersView extends JDialog {

	private JPanel footerPanel;
	private JPanel contentPane;
	
	private JButton okButton;
	private JButton cancelButton;
	private JScrollPane scrollPane;
	private AddUsersTable table;
	
	private Set<User> users;
	
	private void initComponents() {
		
		contentPane = new JPanel();
		footerPanel = new JPanel();
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		table = new AddUsersTable();
		scrollPane = new JScrollPane(table);
		
		//Setup for cancel button
		cancelButton.addActionListener((event)->{
			this.dispose();
		});
	}
	
	private void setLayout() {
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		contentPane.add(footerPanel, BorderLayout.SOUTH);
		footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		footerPanel.add(okButton);
		footerPanel.add(cancelButton);
		
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}
	
	public void init() {
		initComponents();
		setLayout();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 350);
		setContentPane(contentPane);
	}


//	public AddUsersView(JDialog owner, Set<User> users) {
//		super(owner, true);
//		init();
//		setUsers(users);
//		this.table.update();
//	}
//	
	public AddUsersView(JDialog owner) {
		super(owner, true);
		init();
		this.table.update();
	}
	
	public JButton getOkButton() {return okButton;}
	
	public AddUsersView setChosenUsers(Set<User> chosenUsers) {
		this.table.setChosenUsers(chosenUsers);
		return this;
	}
	
	public AddUsersView setUsers(Set<User> users) {
		this.users = users;
		this.table.setTotalUsers(new ArrayList<>(users));
		return this;
	}
	
	public Set<User> submit() {
		return table.getSelecteUsers();
	}
	
	public AddUsersView update() {
		table.update();
		return this;
	}
	
	public AddUsersView clear() {
		table.clear();
		return this;
	}
}
