package com.nhatdang2604.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.nhatdang2604.server.entities.Room;
import com.nhatdang2604.server.entities.User;

public class CreateRoomView extends JDialog {

	private JPanel contentPanel;
	private JPanel centerPanel;
	private JPanel footerPanel;
	
	private JLabel warningText;	
	
	private AddUsersView addUsersView;
	private List<JLabel> labels;
	private List<JTextField> textFields;
	private List<JButton> fieldButtons;
	
	private JButton okButton;
	private JButton cancelButton;
	
	public static final int NO_ERROR = 0;
	public static final int EMPTY_FIELD_ERROR = 1;
	
	private static final String[] ERRORS = {
			"",
			"Có ít nhất một ô trống",
	};
	
	
	public CreateRoomView setError(int errorCode) {
		if (0 <= errorCode && errorCode < ERRORS.length) {
			warningText.setText(ERRORS[errorCode]);
		}
		return this;
	}
	
	private void initComponents() {
		
		addUsersView = new AddUsersView(this);
		
		warningText = new JLabel();					
		warningText.setForeground(Color.RED);		//Warning have red text
		
		contentPanel = new JPanel();
		centerPanel = new JPanel();
		footerPanel = new JPanel();
		
		
		textFields = new ArrayList<>(Arrays.asList(
				new JTextField()));
		
		fieldButtons = new ArrayList<>(Arrays.asList(
				new JButton("Thêm")));
		
		okButton = new JButton("Tạo");
		cancelButton = new JButton("Hủy");
		
		centerPanel = new JPanel();
		labels = new ArrayList<>(Arrays.asList(
				new JLabel("Tên phòng"),
				new JLabel("Thêm thành viên")
		));
		
		initButtons();
	}
	
	
	private void initButtons() {
		cancelButton.addActionListener((event)->{
			this.dispose();
		});
		
	}
	
	private void setLayout() {
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		contentPanel.add(footerPanel, BorderLayout.SOUTH);
		footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		footerPanel.add(okButton);
		footerPanel.add(cancelButton);
		
		contentPanel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		for (int i = 0; i<labels.size(); ++i) {
			String metaLayout = "4, " + (i+2)*2 + ", right, default";
			centerPanel.add(labels.get(i), metaLayout);
		}
		
		centerPanel.add(warningText, "6, 2, center, default");
		int offset = 2;
		int size = textFields.size();
		for (int i = 0; i < size ; ++i) {
			centerPanel.add(textFields.get(i), "6, " + (i + offset) * 2 + ", fill, default");
		}
		offset += size;
		size = fieldButtons.size();
		for (int i = 0; i < size; ++i) {
			centerPanel.add(fieldButtons.get(i), "6, " + (i + offset) * 2 + ", fill, default");
		}
		
		
	}
	
	public void init() {
		initComponents();
		setLayout();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 250, 200);
		setContentPane(contentPanel);
		setTitle("Tạo phòng chat");
		
	}
	
	
	public CreateRoomView() {
		init();
	}
	
	public CreateRoomView(JFrame owner) {
		super(owner, true);
		init();
	}
	
	
	public JButton getOkButton() {return okButton;}
	public JButton getAddUsersButton() {return fieldButtons.get(0);}
	public AddUsersView getAddUsersView() {return addUsersView;}
	
	public boolean areThereAnyEmptyField() {
		
		for (JTextField field: textFields)  {
			String text = field.getText().trim();
			if (null == text || text.equals("")) {
				return true;
			}
		}
		
		return false;
	}
	
	public Room submit() {
		
		Room room = new Room();
		room.setName(textFields.get(0).getText().trim());
		room.setUsers(addUsersView.submit());
		room.setMessages(new TreeSet<>());
		
		return room;
	}
	
	public void clear() {
		textFields.forEach(field -> {
			field.setText("");
		});
		addUsersView.clear();
	}
	
	public void setTotalUsers(List<User> users) {
		addUsersView.setTotalUsers(users);
	}
}
