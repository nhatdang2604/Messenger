package com.nhatdang2604.client.view.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.nhatdang2604.server.entities.User;

public class AddUsersTable extends JTable {

	protected DefaultTableModel tableModel;
	protected List<User> totalUsers;
	protected Set<User> chosenUsers;
	
	public static final String[] COLUMN_NAMES = {
			"STT", "Username", "Chọn"
	};
	
	public static final int COLUMN_INDEX = 0;
	public static final int USERNAME_COLUMN_INDEX = 1;
	public static final int SELECT_COLUMN_INDEX = 2;
	
	protected void setupModelTable() {
		//Make uneditable table
		tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {			
					
			@Override
			public boolean isCellEditable(int row, int column) {				
						
				//Make Update and Select button cell editable
				if (SELECT_COLUMN_INDEX == column) {
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
				case SELECT_COLUMN_INDEX:
					clazz = Boolean.class;
					break;
		      }
		      return clazz;
		    }
			
		};
				
		//Enable table model
		this.setModel(tableModel);
	
	}
	

	public AddUsersTable() {		
		setupModelTable();
		setTotalUsers(new ArrayList<>());
		setChosenUsers(new TreeSet<>());
	}
	
	public AddUsersTable clearData() {
		
		//Clear the model
		tableModel.setRowCount(0);
		
		return this;
	}
	
	public AddUsersTable setTotalUsers(List<User> data) {
		totalUsers = data;
		return this;
	}
	
	public AddUsersTable update() {
		
		clearData();
		
		//Populate all given users
		int index = 0;
		for (User user: totalUsers) {
			++index;
			Object[] row = {index, user.getUsername()};
			tableModel.addRow(row);		
		}
		
		//Scan to check the checkbox in the row which is chosen
		final int size = (null != totalUsers?totalUsers.size():0);
		if (null != chosenUsers) {
			chosenUsers.forEach(user -> {

				for (int i = 0; i < size; ++i) {
					if (user.getId().equals(totalUsers.get(i).getId())) {
						tableModel.setValueAt(new Boolean(true), i, SELECT_COLUMN_INDEX);
						break;
					}
				}
			});
		}
		
		return this;
	}
	
	public User getSelectedUser() {
		int selectedIndex = this.getSelectedRow();
		return totalUsers.get(selectedIndex);
	}
	
	public Set<User> getSelecteUsers() {
		
		//Full scan the table to get all the selected users
		Set<User> result = new TreeSet<>();
		int size = tableModel.getRowCount();
		for (int i = 0; i < size; ++i) {
			Boolean isSelected = (Boolean) tableModel.getValueAt(i, SELECT_COLUMN_INDEX);
			if (null != isSelected && isSelected) {
				result.add(totalUsers.get(i));
			}
			
		}
		
		return result;
	}
	
	public AddUsersTable clear() {
	
		int size = totalUsers.size();
		for (int i = 0; i < size; ++i) {
			tableModel.setValueAt(new Boolean(false), i, SELECT_COLUMN_INDEX);
		}
		
		return this;
	}
	
	public AddUsersTable setChosenUsers(Set<User> users) {
		this.chosenUsers = users;
		return this;
	}
	
	public Set<User> getChosenUsers() {
		return chosenUsers;
	}
}
