package com.nhatdang2604.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.security.auth.Subject;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.nhatdang2604.model.entity.Course;
import com.nhatdang2604.model.entity.Schedule;
import com.nhatdang2604.model.entity.Student;
import com.nhatdang2604.model.entity.SubjectWeek;

public class RegistrationView extends JDialog {

	private JPanel headerPanel;
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
			"Username đã tồn tại",
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
		
		headerPanel = new JPanel();
		centerPanel = new JPanel();
		footerPanel = new JPanel();
		
		showPasswordCheckbox = new JCheckBox("Hiện mật khẩu");
		okButton = new JButton("Đăng ký");
		cancelButton = new JButton("Hủy");
		
		centerPanel = new JPanel();
		labels = new ArrayList<>(Arrays.asList(
				new JLabel("Username"),
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
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		contentPane.add(footerPanel, BorderLayout.SOUTH);
		footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		footerPanel.add(okButton);
		footerPanel.add(cancelButton);
		
		contentPane.add(centerPanel, BorderLayout.CENTER);
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		centerPanel.add(warningText, "6, 2, center, default");
		for (int i = 0; i<labels.size(); ++i) {
			String metaLayout = "4, " + (i+2)*2 + ", right, default";
			centerPanel.add(labels.get(i), metaLayout);
		}
		
		centerPanel.add(subjectComboBox, "6, 4, fill, default");
		centerPanel.add(startDatePicker, "6, 6");
		centerPanel.add(endDatePicker, "6, 8");
		centerPanel.add(timePicker, "6, 10");
		centerPanel.add(weekDayComboBox, "6, 12, fill, default");
		centerPanel.add(weekButton, "6, 14, fill, default");
		centerPanel.add(addStudentButton, "6, 16, fill, default");
	}
	
	public void init() {
		initComponents();
		setLayout();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 350);
		setContentPane(contentPane);
		
	}
	
	private void initModel() {
		model = new Course();
		model.setStudents(new TreeSet<Student>());
		model.setSubject(new Subject());
		model.setSchedule(new Schedule());
		weekForm.setModel(model.getSchedule());
	}
	
	/**
	 * Create the frame.
	 */
	public RegistrationView() {
		init();
		initModel();
	}

	public RegistrationView(Course model, JFrame owner) {
		super(owner, true);
		init();
		setModel(model);
	}
	
	public RegistrationView(JFrame owner) {
		super(owner, true);
		init();
		initModel();
	}
	
	
	public JButton getOkButton() {return okButton;}
	public JDialog setModel(Course model) {
		this.model = model;
		subjectComboBox.setSelectedItem(model.getSubject());
		
		Schedule schedule = model.getSchedule();
		if (null != schedule) {
			startDatePicker.setDate(schedule.getStartDate());
			endDatePicker.setDate(schedule.getEndDate());
			timePicker.setTime(schedule.getTime());
		}
		
		return this;
	}
	public RegistrationView setAvailableSubjects(List<Subject> subjects) {
		this.availableSubjects = subjects;
		subjects.forEach(subject -> {
			subjectComboBox.addItem(subject);
		});
		
		return this;
	}
	
	public Course submit() {
		
		model.setSubject((Subject) subjectComboBox.getSelectedItem());
		Schedule schedule = model.getSchedule();
		
		Set<Student> students = addStudentForm.submit();
		model.setStudents(students);
		
		if (null == schedule) {
			schedule = new Schedule();
			schedule.setCourse(model);
			model.setSchedule(schedule);
			weekForm.setModel(schedule);
		}
		
		schedule.setStartDate(startDatePicker.getDate());
		schedule.setEndDate(endDatePicker.getDate());
		schedule.setTime(timePicker.getTime());
		schedule.setWeekDay((String) weekDayComboBox.getSelectedItem());
		
		List<SubjectWeek> weeks = weekForm.submit();
		schedule.setSubjectWeeks(weeks);
	
		return model;
	}
	
	public AddStudentToCourseForm getAddStudentForm() {return addStudentForm;}
	public ChangeSubjectWeekInCourseForm getWeekForm() {return weekForm;}
	
	public JButton getSubmitButton() {
		return okButton;
	}
}
