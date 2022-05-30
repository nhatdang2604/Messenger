package com.nhatdang2604.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.nhatdang2604.server.entities.Message;
import com.nhatdang2604.server.entities.Room;
import com.nhatdang2604.server.entities.User;

public class ChatView extends JDialog {

	final protected int HEIGHT = 700;
	final protected int WIDTH = 450;
	
	private JButton sendButton;
	private JButton sendFileButton;
	private JFileChooser fileChooser;
	
	private JTextField typeField;
	private JTextPane messagePane;
	
	private JPanel sendButtonsPanel;
	private JPanel typePanel;
	private JPanel messagePanel;
	private JScrollPane scrollPane;
	
	private Room room;
	
	private User user;
	
	private void initComponents() {
		sendButton = new JButton("Gửi");
		sendFileButton = new JButton("Gửi file");
		fileChooser = new JFileChooser();
		
		typeField = new JTextField();
		
		//Init for message pane
		messagePane = new JTextPane();
		messagePane.setEditable(false);	//make the message aera uneditable
		messagePane.setBackground(Color.WHITE);
		
		typePanel = new JPanel();
		messagePanel = new JPanel();
		sendButtonsPanel = new JPanel();
		scrollPane = new JScrollPane(messagePane);
	}
	
	private void setLayout() {
		
		//Content pane + 2 panel parts have border layout
		setLayout(new BorderLayout());
		messagePanel.setLayout(new BorderLayout());
		typePanel.setLayout(new BorderLayout());
		sendButtonsPanel.setLayout(new FlowLayout());
		
		//Setup for content pane
		add(messagePanel, BorderLayout.CENTER);
		add(typePanel, BorderLayout.SOUTH);
		
		//Setup for message panel
		messagePanel.add(scrollPane, BorderLayout.CENTER);
		
		//Setup for buttons panel
		sendButtonsPanel.add(sendButton);
		sendButtonsPanel.add(sendFileButton);
		
		//Setup for type panel
		typePanel.add(sendButtonsPanel, BorderLayout.EAST);
		typePanel.add(typeField, BorderLayout.CENTER);
		
		
	}
	
	public ChatView(JFrame owner) {
		super(owner, true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		
		initComponents();
		setLayout();
		
	}
	
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
		
		User client = message.getUser();
		
		Color nameColor = Color.BLUE;
		Color dateColor = Color.GRAY;
		Color messageColor = Color.BLACK;
		
		//Change the color the of the name's text
		if (!client.getId().equals(user.getId())) 
		{
			nameColor = Color.RED;
		} 
		
		addTextToMessagePane(message.getDateTime().toString() + ": ", dateColor);
		addTextToMessagePane(client.getUsername() + ": ", nameColor);
		addTextToMessagePane(message.getContent() + "\r\n", messageColor);
		
		//Add message to the model
		room.getMessages().add(message);
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setRoom(Room room) {
		this.room = room;
		load(room.getMessages());
	}
	
	public Room getRoom() {
		return room;
	}
	
	public void load(Set<Message> messages) { 
		List<Message> list = new ArrayList<>(messages);
		Collections.sort(list);
		list.forEach(message -> {
			addNewMessage(message);
		});
		
	}
	
	public JButton getSendButton() {return sendButton;}
	public JButton getSendFileButton() {return sendFileButton;}
	public JFileChooser getFileChooser() {return fileChooser;}
	public JTextField getTypeField() {return typeField;}
	
	public void clearChat() {
		this.messagePane.setText("");
	}
	public void open() {
		this.setVisible(true);
	}
	
}
