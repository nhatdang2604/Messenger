package com.nhatdang2604.client.view.component.widget;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ButtonRenderer extends JButton implements TableCellRenderer  {
  
	private String name;
	
	public ButtonRenderer(String name) {
	this.name = name;
    setOpaque(true);
  }
  
  @Override
  public Component getTableCellRendererComponent(
		  JTable table, 
		  Object value,
		  boolean isSelected, 
		  boolean hasFocus, 
		  int row, 
		  int column) {
	  
    setText(name);
    
    return this;
  }
}