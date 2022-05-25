package com.nhatdang2604.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import com.nhatdang2604.server.model.entities.Client;
import com.nhatdang2604.server.utils.HashingUtil;

public class RegistrationView extends JDialog {

	private JPanel contentPanel;
	private JPanel centerPanel;
	private JPanel footerPanel;
	
	private JLabel warningText;	
	
	private List<JLabel> labels;
	private JTextField usernameField;
	private List<JPasswordField> passwordFields;
	
	private JCheckBox showPasswordCheckbox;
	private JButton okButton;
	private JButton cancelButton;
	
	public static final int EMPTY_FIELD_ERROR = 0;
	public static final int EXISTED_USERNAME_ERROR = 1;
	public static final int PASSWORD_MISMATCH_ERROOR = 2;
	
	private static final String[] ERRORS = {
			"Có ít nhất một ô trống",
			"Tên đăng nhập đã tồn tại",
			"<html><body>Nhập lại mật khẩu và<br>mật khẩu mới không trùng khớp</body></html>"
	};
	
	
	public RegistrationView setError(int errorCode) {
		if (0 <= errorCode && errorCode < ERRORS.length) {
			warningText.setText(ERRORS[errorCode]);
		}
		return this;
	}
	
	private void initComponents() {
		
		warningText = new JLabel();					
		warningText.setForeground(Color.RED);		//Warning have red text
		
		contentPanel = new JPanel();
		centerPanel = new JPanel();
		footerPanel = new JPanel();
		
		usernameField = new JTextField();
		passwordFields = new ArrayList<>(Arrays.asList(
				new JPasswordField(),
				new JPasswordField()));
		
		showPasswordCheckbox = new JCheckBox("Hiện mật khẩu");
		okButton = new JButton("Đăng ký");
		cancelButton = new JButton("Hủy");
		
		centerPanel = new JPanel();
		labels = new ArrayList<>(Arrays.asList(
				new JLabel("Tên đăng nhập"),
				new JLabel("Mật khẩu"),
				new JLabel("Nhập lại mật khẩu")
		));
		
		initCheckbox();
		initButtons();
	}
	
	private void initCheckbox() {
		showPasswordCheckbox.addActionListener((event) -> {
				
			//If the check box is selected
			//	=> Show password
			if (showPasswordCheckbox.isSelected()) {
				passwordFields.forEach(field -> {
					field.setEchoChar((char)0);
				});
			} else {
					
				//If the check box is not selected
				//	=> Hide password by setting echo character with (char)'\u2022'
				passwordFields.forEach(field -> {
					field.setEchoChar('\u2022');
				});
			}
				
		});

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
		footerPanel.add(showPasswordCheckbox);
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		for (int i = 0; i<labels.size(); ++i) {
			String metaLayout = "4, " + (i+2)*2 + ", right, default";
			centerPanel.add(labels.get(i), metaLayout);
		}
		
		centerPanel.add(warningText, "6, 2, center, default");
		centerPanel.add(usernameField, "6, 4, fill, default");
		for (int i = 0; i < passwordFields.size(); ++i) {
			centerPanel.add(passwordFields.get(i), "6, " + (i + 3) * 2 + ", fill, default");
		}
		
	}
	
	public void init() {
		initComponents();
		setLayout();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 250, 200);
		setContentPane(contentPanel);
		setTitle("Đăng ký");
		
	}
	
	
	public RegistrationView() {
		init();
	}
	
	public RegistrationView(JFrame owner) {
		super(owner, true);
		init();
	}
	
	
	public JButton getOkButton() {return okButton;}
	
	public boolean areThereAnyEmptyField() {
		
		String username = usernameField.getText().trim();
		if (null == username || username.equals("")) {
			return true;
		}
		
		String emptyStringEncryption = HashingUtil.passwordEncryption("");
		for (JPasswordField field: passwordFields) {
			if (null == new String(field.getPassword()).trim()) {
				return true;
			}
			String hashing = HashingUtil.passwordEncryption(new String(field.getPassword()).trim());
			if (emptyStringEncryption.equals(hashing)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isPasswordMismatch() {
		
		String hash0 = HashingUtil.passwordEncryption(
				new String(passwordFields.get(0).getPassword()).trim());
		
		String hash1 = HashingUtil.passwordEncryption(
				new String(passwordFields.get(1).getPassword()).trim());
		
		return !hash0.equals(hash1);
	}
	
	public Client getModel() {
		
		Client client = new Client();
		client.setUsername(usernameField.getText().trim());
		String encryptedPassword = HashingUtil.passwordEncryption(
				new String(passwordFields.get(0).getPassword()).trim());
		client.setEncryptedPassword(encryptedPassword);
		client.setIsOnline(false);
		client.setRooms(new TreeSet<>());
	
		return client;
	}
	
	public void clear() {
		usernameField.setText("");
		passwordFields.forEach(field -> {
			field.setText("");
		});
	}
}
