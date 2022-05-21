package com.nhatdang2604.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.Message;

public class ChatView extends JFrame {

	final protected int HEIGHT = 700;
	final protected int WIDTH = 1250;
	
	private JButton sendButton;
	private JTextField typeField;
	private JTextPane messagePane;
	private JPanel typePanel;
	private JPanel messagePanel;
	private JScrollPane scrollPane;
	
	private Client owner;
	
	private void initComponents() {
		sendButton = new JButton("Gửi");
		typeField = new JTextField();
		
		//Init for message pane
		messagePane = new JTextPane();
		messagePane.setEditable(false);	//make the message aera uneditable
		messagePane.setBackground(Color.WHITE);
		
		typePanel = new JPanel();
		messagePanel = new JPanel();
		scrollPane = new JScrollPane(messagePane);
	}
	
	private void setLayout() {
		
		//Content pane + 2 panel parts have border layout
		setLayout(new BorderLayout());
		messagePanel.setLayout(new BorderLayout());
		typePanel.setLayout(new BorderLayout());
		
		//Setup for content pane
		add(messagePanel, BorderLayout.CENTER);
		add(typePanel, BorderLayout.SOUTH);
		
		//Setup for message panel
		messagePanel.add(scrollPane, BorderLayout.CENTER);
		
		//Setup for type panel
		typePanel.add(sendButton, BorderLayout.EAST);
		typePanel.add(typeField, BorderLayout.CENTER);
		
		
	}
	
	public ChatView(Client owner) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		
		this.owner = owner;
		initComponents();
		setLayout();
		
	}
	
//	public static void main(String[] args) {
//		Client client0 = new Client();
//		client0.setUsername("u1");
//		client0.setId(1);
//		ChatView view = new ChatView(client0);
//		
//		Message message0 = new Message();
//		message0.setDateTime(LocalDateTime.now());
//		message0.setContent("Hello world");
//		message0.setClient(client0);
//		
//		Message message = new Message();
//		Client client = new Client();
//		client.setUsername("u2");
//		client.setId(2);
//		message.setDateTime(LocalDateTime.now());
//		message.setContent("Chào thế giới");
//		message.setClient(client);
//		
//		view.addNewMessage(message0);
//		view.addNewMessage(message);
//		
//		view.setVisible(true);
//	}
	
	public void addTextToMessagePane(String text, Color color) {
		
		StyledDocument doc = messagePane.getStyledDocument();
		Style style = messagePane.addStyle("", null);
		StyleConstants.setForeground(style, color);
		try {
			doc.insertString(doc.getLength(), text, style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void addNewMessage(Message message) {
		
		Client client = message.getClient();
		
		Color nameColor = Color.BLUE;
		Color dateColor = Color.GRAY;
		Color messageColor = Color.BLACK;
		
		//Change the color the of the name's text
		if (!client.getId().equals(owner.getId())) 
		{
			nameColor = Color.RED;
		} 
		
		addTextToMessagePane(message.getDateTime().toString() + ": ", dateColor);
		addTextToMessagePane(client.getUsername() + ": ", nameColor);
		addTextToMessagePane(message.getContent() + "\r\n", messageColor);
		
	}
	
	public void load(Set<Message> messages) {
		messages.forEach(message -> {
			addNewMessage(message);
		});
		
	}
	
	public JButton getSendButton() {return sendButton;}
	public JTextField getTypeField() {return typeField;}
	
}
