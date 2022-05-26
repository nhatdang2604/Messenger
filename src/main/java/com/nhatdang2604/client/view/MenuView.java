package com.nhatdang2604.client.view;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.nhatdang2604.client.view.component.RoomTable;
import com.nhatdang2604.server.entities.Room;
import com.nhatdang2604.server.entities.User;

public class MenuView extends JFrame {

	final protected int HEIGHT = 400;
	final protected int WIDTH = 500;
	
	private JButton createRoomButton;
	private RoomTable table;
	
	private JPanel headerPanel;
	private JPanel roomPanel;
	private JScrollPane scrollPane;
	
	private User client;
	
	private void initComponents() {
		createRoomButton = new JButton("Tạo phòng chat");
		table = new RoomTable();
		
		headerPanel = new JPanel();
		roomPanel = new JPanel();
		scrollPane = new JScrollPane(table);
	}
	
	private void setLayout() {
		
		setLayout(new BorderLayout());
		headerPanel.setLayout(new BorderLayout());
		roomPanel.setLayout(new BorderLayout());
		
		headerPanel.add(createRoomButton, BorderLayout.EAST);
		roomPanel.add(scrollPane, BorderLayout.CENTER);
		
		add(headerPanel, BorderLayout.NORTH);
		add(roomPanel, BorderLayout.CENTER);
		
	}
	
	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		initComponents();
		setLayout();
	}
	
	public MenuView(User client) {
		init();
		setClient(client);
	}
	
	public MenuView() {
		init();
	}
	
//	public static void main(String[] args) {
//		Client client = new Client();
//		
//		client.setId(0);
//		
//		Room room = new Room();
//		
//		room.setId(0);
//		room.setName("hihhi");
//		Set<Room> rooms = new TreeSet<>();
//		rooms.add(room);
//		Set<Client> clients = new TreeSet<>();
//		clients.add(client);
//		client.setRooms(rooms);
//		
//		room.setClients(clients);
//		
//		for (Client _client: clients) {
//			_client.setRooms(rooms);
//		}
//		
//		
//		MenuView view = new MenuView(client);
//		view.setVisible(true);
//	}
	
	
	public void setClient(User client) {
		this.client = client;
		this.table.setRooms(new ArrayList<>(this.client.getRooms()));
	}
	
	public JButton getCreateRoomButton() {return createRoomButton;}
	
}
