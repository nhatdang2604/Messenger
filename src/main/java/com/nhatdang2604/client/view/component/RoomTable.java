package com.nhatdang2604.client.view.component;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.nhatdang2604.client.view.component.widget.ButtonEditor;
import com.nhatdang2604.client.view.component.widget.ButtonRenderer;
import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.model.entities.Room;

public class RoomTable extends JTable {

	protected JButton joinButton;
	protected DefaultTableModel tableModel;
	protected List<Room> rooms;
	
	public static final String[] COLUMN_NAMES = {
			"STT", "Tên Room", "Số user đang online", "Hành động"
	};
	
	public static final int COLUMN_INDEX = 0;
	public static final int NAME_COLUMN_INDEX = 1;
	public static final int ONLINE_USER_COUNT_COLUMN_INDEX = 2;
	public static final int JOIN_COLUMN_INDEX = 3;
	
	protected void setupModelTable() {
		//Make uneditable table
		tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {			
					
			@Override
			public boolean isCellEditable(int row, int column) {				
							
				//Make Join button cell editable
				if (JOIN_COLUMN_INDEX  == column) {
					return true;
				}
				
				//all cells false
				return false;
			}
			
			@Override
		    public Class<?> getColumnClass(int columnIndex) {
				Class clazz = String.class;
				switch (columnIndex) {
				case COLUMN_INDEX:
					clazz = Integer.class;
					break;
				case JOIN_COLUMN_INDEX:
					clazz = Boolean.class;
					break;
		      }
		      return clazz;
		    }
			
		};
				
		//Enable table model
		this.setModel(tableModel);
		
		
	}
	
	protected void initJoinButton() {
		
		//Setup for the Update button in cell
		String buttonName = "Tham gia";
		joinButton = new JButton(buttonName);
		TableColumn joinColumn = this.getColumn(COLUMN_NAMES[JOIN_COLUMN_INDEX]);
		joinColumn.setCellRenderer(new ButtonRenderer(buttonName));
		joinColumn.setCellEditor(new ButtonEditor(joinButton));			
	
	}
		
	public RoomTable() {
		setupModelTable();
		initJoinButton();
	}
	
	public RoomTable clearData() {
		
		//Clear the model
		tableModel.setRowCount(0);
		
		return this;
	}
	
	public RoomTable setRooms(List<Room> rooms) {
		this.rooms = rooms;
		this.update();
		return this;
	}
	
	public RoomTable update() {
		
		clearData();
		int size = rooms.size();
		for (int index = 0; index < size; ++index) {
			Room room = rooms.get(index);
			
			//Count the number of online user in a room
			int onlineCount = 0;
			
			if(null != room.getClients()) {
				for (Client client: room.getClients()) {
					if (null != client.getIsOnline() && client.getIsOnline()) {
						++onlineCount;
					}
				}
			}
			Object[] row = {index + 1, room.getName(), onlineCount};
			tableModel.addRow(row);		
		}
		return this;
	}
	
	public Room getSelectedRoom() {
		int selectedIndex = this.getSelectedRow();
		return rooms.get(selectedIndex);
	}

	public JButton getJoinButton() {
		return joinButton;
	}
}
