

/*TODO
 * - Should the task update or revert when the user clicks the X to close
 *    instead of clicking the Done button? (currently: update)
 */

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import org.jbundle.thin.base.screen.jcalendarbutton.*;
import com.explodingpixels.macwidgets.IAppWidgetFactory;


public class TaskEntryDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	/* A not-typedef for GridBagConstraints, since Java has no typedef. */
	@SuppressWarnings("serial")
	private class GBC extends GridBagConstraints {
	}
	
	private Task task;
	
	private HomeworkThingListModel taskModel;
	private HomeworkThingListModel subjectModel;
	
	private JPanel mainPanel;
	
	private JTextField taskName;
	
	private JComboBox subjectCombo;

	private SpinnerDateModel startModel;
	private JSpinner startSpinner;
	private JCalendarButton startCalButton;

	private SpinnerDateModel dueModel;
	private JSpinner dueSpinner;
	private JCalendarButton dueCalButton;
	
	private JSpinner hoursNeeded;
	private JSpinner minutesNeeded;
	
	private DateRangePicker workTimes;

	private JComboBox typeCombo;

	private JRadioButton[] priorityOptions;
	private ButtonGroup priorityGroup;
	
	private JTextArea notesText;
	
	private JButton doneButton;
	private JButton cancelButton;

	private Vector<Subject> addedSubjects;
	private Vector<String> addedTypes;
	
	private int state;
	
	public static final int OPEN = 0;
	public static final int CANCEL = 1;
	public static final int ACCEPT = 2;
	
	private static final int LIR = 5; // label insets right
	private static final int SMALL_SPACE = 3;
	private static final int BIG_SPACE = 14;
	
	private static final Insets TF_LABEL_INSETS = new Insets(0, 0, 0, LIR);
	private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);
	private static final Insets TF_INSETS = new Insets(0, 5, 0, 0);


	public TaskEntryDialog() {
		this(null);
	}
	
	public TaskEntryDialog(Task t) {
		this(t, null, null);
	}
	
	public TaskEntryDialog(Task t, HomeworkThingListModel tasks) {
		this(t, tasks, null, null);
	}
	
	public TaskEntryDialog(HomeworkThingListModel subjects, 
			DefaultComboBoxModel types) {
		this(null, subjects, types);
	}

	public TaskEntryDialog(Task t, HomeworkThingListModel subjects, 
			DefaultComboBoxModel types) {
		this(t, null, subjects, types);
	}
	
	public TaskEntryDialog(Task t, HomeworkThingListModel tasks,
			HomeworkThingListModel subjects, DefaultComboBoxModel types) {
		state = OPEN;
		
		if (t == null)
			this.task = new Task();
		else
			this.task = t;
		
		if (tasks == null)
			this.taskModel = new HomeworkThingListModel();
		else
			this.taskModel = tasks;
		
		if (subjects == null)
			subjectModel = new HomeworkThingListModel();
		else
			subjectModel = subjects;
		
		if (types == null)
			types = new DefaultComboBoxModel();
		
		addedSubjects = new Vector<Subject>(4);
		addedTypes = new Vector<String>(4);

		setTitle("Edit Task");
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(mainPanel);
		mainPanel.setLayout(new GridBagLayout());
		
		int verticalPos = 0;

		
		//////// Name ////////
		addWithConstraints(mainPanel, new JLabel("Task name"), 0, verticalPos, 
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		taskName = new JTextField(task.getName(), 20);
		addWithConstraints(mainPanel, taskName, 1, verticalPos++, 1, 1, 
				TF_INSETS, GBC.WEST, GBC.HORIZONTAL);
		
		addSpacerPanel(verticalPos++, 0, SMALL_SPACE);
		
		
		//////// Subject ////////
		addWithConstraints(mainPanel, new JLabel("Subject"), 0, verticalPos, 
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		subjectCombo = new JComboBox(subjects);
		subjectCombo.setRenderer(new ColorListCellRenderer());
		setupEditableCombo(subjectCombo);
		if (task.getSubject() == null || task.getSubjectName().isEmpty())
			subjectCombo.setSelectedIndex(-1);
		else
			subjectCombo.setSelectedItem(task.getSubjectName());
		
		addWithConstraints(mainPanel, subjectCombo, 1, verticalPos++, 1, 1, 
				TF_INSETS, GBC.WEST, GBC.HORIZONTAL);
		
		addSpacerPanel(verticalPos++, 0, BIG_SPACE);
		
		
		//////// Start date ////////
		addWithConstraints(mainPanel, new JLabel("Start date"), 0, verticalPos, 
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		addWithConstraints(mainPanel, startPanel, 1, verticalPos++, 1, 1, 
				NO_INSETS, GBC.WEST, GBC.BOTH);
		
		startModel = new SpinnerDateModel();
		startModel.setValue(task.getStartDate());
		
		startSpinner = new JSpinner(startModel);
		startSpinner.setPreferredSize(new Dimension(155, 26));
		startPanel.add(startSpinner);
		
		startCalButton = new JCalendarButton(task.getStartDate());
		startPanel.add(startCalButton);
		startCalButton.setPreferredSize(new Dimension(20, 20));
		startCalButton.setMinimumSize(new Dimension(20, 20));
		startCalButton.setMaximumSize(new Dimension(20, 20));
		
		startCalButton.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getNewValue() instanceof Date) {
					// We only want to set the day, month, and year based on 
					// the date the user chose with the button. So, awkwardness!
					GregorianCalendar buttonCal = new GregorianCalendar();
					buttonCal.setTime((Date)e.getNewValue());
					
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime((Date)startModel.getValue());
					cal.set(buttonCal.get(Calendar.YEAR), 
							buttonCal.get(Calendar.MONTH),
							buttonCal.get(Calendar.DAY_OF_MONTH));
					startModel.setValue(cal.getTime());
				}
			}
		});
		
		addSpacerPanel(verticalPos++, 0, SMALL_SPACE);
		
		
		//////// Due date ////////
		addWithConstraints(mainPanel, new JLabel("Due date"), 0, verticalPos, 
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		JPanel duePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		addWithConstraints(mainPanel, duePanel, 1, verticalPos++, 1, 1,
				NO_INSETS, GBC.WEST, GBC.BOTH);
		
		dueModel = new SpinnerDateModel();
		dueModel.setValue(task.getDueDate());
		
		dueSpinner = new JSpinner(dueModel);
		dueSpinner.setPreferredSize(new Dimension(155, 26));
		duePanel.add(dueSpinner);
		
		dueCalButton = new JCalendarButton(task.getDueDate());
		dueCalButton.setPreferredSize(new Dimension(20, 20));
		dueCalButton.setMinimumSize(new Dimension(20, 20));
		dueCalButton.setMaximumSize(new Dimension(20, 20));
		duePanel.add(dueCalButton);
		
		dueCalButton.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getNewValue() instanceof Date) {
					// We only want to set the day, month, and year based on 
					// the date the user chose with the button. So, awkwardness!
					GregorianCalendar buttonCal = new GregorianCalendar();
					buttonCal.setTime((Date)e.getNewValue());
					
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime((Date)dueModel.getValue());
					cal.set(buttonCal.get(Calendar.YEAR), 
							buttonCal.get(Calendar.MONTH),
							buttonCal.get(Calendar.DAY_OF_MONTH));
					dueModel.setValue(cal.getTime());
				}
			}
		});
		
		startModel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				startCalButton.setTargetDate((Date)startModel.getValue());
				workTimes.setStart(startModel.getDate());
			}
		});
		
		dueModel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				dueCalButton.setTargetDate((Date)dueModel.getValue());
				workTimes.setEnd(dueModel.getDate());
			}
		});
		
		addSpacerPanel(verticalPos++, 0, SMALL_SPACE);
		
		
		//////// Time needed ////////
		addWithConstraints(mainPanel, new JLabel("Time needed"), 0, verticalPos, 
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);

		JPanel timeNeededPanel = new JPanel(
				new FlowLayout(FlowLayout.LEFT, 5, 0));
		addWithConstraints(mainPanel, timeNeededPanel, 1, verticalPos++, 1, 1,
				NO_INSETS, GBC.WEST, GBC.HORIZONTAL);
		
		hoursNeeded = new JSpinner(
				new SpinnerNumberModel(task.getHoursNeeded(), 0, 99, 1));
		timeNeededPanel.add(hoursNeeded);
		
		timeNeededPanel.add(new JLabel("hours "));
		
		minutesNeeded = new JSpinner(
				new SpinnerNumberModel(task.getMinutesNeeded(), 0, 59, 1));
		timeNeededPanel.add(minutesNeeded);
		
		timeNeededPanel.add(new JLabel("minutes"));
		
		addSpacerPanel(verticalPos++, 0, 1);

		
		//////// Work times ////////
		addWithConstraints(mainPanel, new JLabel("Work times"), 0, verticalPos, 
				1, 1, new Insets(5, 0, 0, LIR), GBC.NORTHEAST, GBC.NONE);

		JPanel workTimePanel = new JPanel();
		workTimePanel.setBackground(new Color(220, 220, 220));
		addWithConstraints(mainPanel, workTimePanel, 1, verticalPos++, 1, 1, 
				new Insets(0, 3, 0, 0), GBC.NORTHEAST, GBC.BOTH);
		
		workTimes = new DateRangePicker(startModel.getDate(), 
				dueModel.getDate());
		workTimes.setDateRanges(task.getWorkTimes());
		JScrollPane scrollPane = new JScrollPane(workTimes);
		IAppWidgetFactory.makeIAppScrollPane(scrollPane);
		workTimePanel.add(scrollPane);
		
		addSpacerPanel(verticalPos++, 0, BIG_SPACE);
		
		
		//////// Priority ////////
		addWithConstraints(mainPanel, new JLabel("Priority"), 0, verticalPos, 
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		priorityGroup = new ButtonGroup();
		priorityOptions = new JRadioButton[4];
		priorityOptions[0] = new JRadioButton("None");
		priorityOptions[1] = new JRadioButton("Low");
		priorityOptions[2] = new JRadioButton("Medium");
		priorityOptions[3] = new JRadioButton("High");
		
		priorityOptions[task.getPriority().getNumber()].setSelected(true);
		
		JPanel priorityPanel = new JPanel(
				new FlowLayout(FlowLayout.LEFT, 8, 0));
		for (int i = 0; i < priorityOptions.length; ++i) {
			priorityPanel.add(priorityOptions[i]);
			priorityGroup.add(priorityOptions[i]);
		}
		addWithConstraints(mainPanel, priorityPanel, 1, verticalPos++, 1, 1, 
				NO_INSETS, GBC.WEST, GBC.NONE);
		
		addSpacerPanel(verticalPos++, 0, BIG_SPACE);

		
		//////// Type ////////
		addWithConstraints(mainPanel, new JLabel("Type"), 0, verticalPos, 1, 1,
				TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		typeCombo = new JComboBox(types);
		if (task.getType().isEmpty())
			typeCombo.setSelectedIndex(-1);
		else
			typeCombo.setSelectedItem(task.getType());
		setupEditableCombo(typeCombo);
		addWithConstraints(mainPanel, typeCombo, 1, verticalPos++, 1, 1,
				TF_INSETS, GBC.WEST, GBC.HORIZONTAL);
		
		addSpacerPanel(verticalPos++, 0, BIG_SPACE);

		
		//////// Notes ////////
		addWithConstraints(mainPanel, new JLabel("Notes"), 0, verticalPos, 1, 1,
				new Insets(2, 0, 2, LIR), GBC.NORTHEAST, GBC.NONE);
		
		JScrollPane notesScroll = new JScrollPane();
		notesScroll.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		IAppWidgetFactory.makeIAppScrollPane(notesScroll);
		addWithConstraints(mainPanel, notesScroll, 1, verticalPos++, 1, 1, 
				new Insets(0, 8, 0, 3), GBC.NORTHWEST, GBC.BOTH);
		
		notesText = new JTextArea();
		notesText.setRows(3);
		notesText.setText(task.getNotes());
		notesText.setLineWrap(true);
		notesText.setWrapStyleWord(true);
		notesScroll.setViewportView(notesText);
		
		//////// Buttons ////////
		JPanel buttonPanel = new JPanel();
		addWithConstraints(mainPanel, buttonPanel, 0, verticalPos++, 2, 1, 
				new Insets(BIG_SPACE-2, 0, 0, 0), GBC.CENTER, GBC.HORIZONTAL);
		
		doneButton = new JButton("Done");
		buttonPanel.add(doneButton);
		
		cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state = ACCEPT;
				updateTask();
				dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state = CANCEL;
				
				// Deregister the WindowListeners that usually would update
				// the task when the window is closed
				WindowListener[] listeners = getWindowListeners();
				for (WindowListener l : listeners)
					removeWindowListener(l);
						
				subjectCombo.setSelectedIndex(-1);
				typeCombo.setSelectedIndex(-1);
				removeUnusedOptions(subjectCombo, addedSubjects);
				removeUnusedOptions(typeCombo, addedTypes);
				
				dispose();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				state = ACCEPT;
				updateTask();
				setVisible(false);
				dispose();
			}
		});
		
		pack();
	}
	
	public int getState() {
		return state;
	}
	
	public Task getTask() {
		return task;
	}
	
	public HomeworkThingListModel getSubjectModel() {
		return (HomeworkThingListModel)subjectCombo.getModel();
	}
	
	public DefaultComboBoxModel getTypeModel() {
		return (DefaultComboBoxModel)typeCombo.getModel();
	}
	
	public HomeworkThingListModel getTaskModel() {
		return taskModel;
	}

	private void addSpacerPanel(int y_pos, int width, int height) {
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(width, height));
		addWithConstraints(mainPanel, spacer, 0, y_pos, 2, 1, NO_INSETS, 
				GBC.CENTER, GBC.NONE);
	}
	
	private void setupEditableCombo(final JComboBox combo) {
		// The final modifier in the method signature is required to be able
		// to use combo in anonymous classes.
		combo.setEditable(true);
		combo.getEditor().getEditorComponent().addKeyListener(
				new KeyAdapter() {
					public void keyReleased(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER)
							addListItem(combo);
					}
				});
		combo.getEditor().getEditorComponent().addFocusListener(
				new FocusAdapter() {
					// On losing focus, add whatever is typed in the box
					// to the list.
					public void focusLost(FocusEvent e) {
						addListItem(combo);
					}
				});
	}
	
	public void updateTask() {
		task.setName(taskName.getText());

		// This line is important. If whatever is currently selected is a 
		// string not a subject, make it into a subject, and set that new
		// subject to be the selected item.
		addListItem(subjectCombo);
		
		/* The code here is meant to prevent duplicate subjects from
		 being added.
		 If the task's original subject is null, or the string of the
		 task's original subject is different from the string of the 
		 selected one, set the task's subject to the selected subject. */
		if (task.getSubject() == null 
				|| subjectCombo.getSelectedItem().toString().equals(
						task.getSubject().toString()) == false) {
			task.setSubject((Subject)subjectCombo.getSelectedItem());
		} else {
			/* If we're here, that means the string of the task's original
			 subject is the same as the string of the selected subject.
			 This most likely means we've somehow ended up with a duplicate of
			 the task's original subject selected.
			 If the task's original subject somehow isn't in the model
			 already, add it. */
			if (subjectModel.getIndexOf(task.getSubject()) == -1)
				subjectModel.addElement(task.getSubject());
			
			// Now set the selected item to the task's orignal subject.
			subjectModel.setSelectedItem(task.getSubject());
		}
		
		// Remove any elements added to the models (probably by side effects of
		// FocusListeners) that didn't end up being chosen
		removeUnusedOptions(subjectCombo, addedSubjects);
		removeUnusedOptions(typeCombo, addedTypes);
		
		DatePair startDue = null;
		try {
			startDue = new DatePair((Date)startModel.getValue(), 
					(Date)dueModel.getValue());
			task.setStartDue(startDue);
		} catch (Exception ex) {
			System.err.println("Invalid start and end dates.");
		}
		
		task.setWorkTimes(workTimes.getDateRanges());
		
		task.setHoursMinutesNeeded(((Integer)hoursNeeded.getValue()).intValue(),
				((Integer)minutesNeeded.getValue()).intValue());
		
		for (int i = 0; i < priorityOptions.length; ++i) {
			if (priorityOptions[i].isSelected()) {
				task.setPriority(Task.Priority.getPriority(i));
				break;
			}
		}
		
		task.setType((String)typeCombo.getSelectedItem());
		
		task.setNotes(notesText.getText());
		
		if (taskModel.getIndexOf(task) == -1)
			taskModel.addElement(task);
		
		//TODO delete this later
		System.out.println(task.toLongString());
	}
	
	private <T> void removeUnusedOptions(JComboBox combo, Vector<T> options) {
		DefaultComboBoxModel model = (DefaultComboBoxModel)combo.getModel();
		for (T t : options) {
			if (t.equals(combo.getSelectedItem()) == false)
				model.removeElement(t);
		}
	}
	
	private boolean addListItem(JComboBox combo) {
		// If the combo box is the subject one, and the selected item is 
		// already an instance of Subject (not an instance of String),
		// it's already been added to the model
		if (combo.getSelectedItem() instanceof Subject) 
			return false;
		
		if (combo.getSelectedItem() == null) return false;
		
		if (combo.getSelectedItem() instanceof String == false) {
			// This should be impossible, but if it somehow happens, remove
			// the offending object.
			System.err.println(combo.getSelectedItem().getClass().getName()
					+ " detected in combo box.");
			((DefaultComboBoxModel)combo.getModel()).removeElement(
					combo.getSelectedItem());
			return false;
		}
		
		String newEntry = (String)combo.getSelectedItem();

		// Prevent addition of empty entries
		if (newEntry.isEmpty()) return false;
		
		DefaultComboBoxModel model = (DefaultComboBoxModel)combo.getModel();
		
		// See if the entry already exists in the shared model
		// Entries could be either Strings or Subjects, but toString() will
		// make sure that we're getting a String for comparison
		for (int i = 0; i < model.getSize(); ++i) {
			if (newEntry.equals(model.getElementAt(i).toString())) {
				combo.setSelectedIndex(i);
				return false;
			}
		}
		
		if (combo == subjectCombo) {
			Subject s = new Subject(newEntry);
			model.addElement(s);
			addedSubjects.add(s);
			combo.setSelectedItem(s);
		} else if (combo == typeCombo) {
			model.addElement(newEntry);
			addedTypes.add(newEntry);
			combo.setSelectedItem(newEntry);
		}
			
		return true;
	}
	
	/**
	 * Add a component to a panel with a GridBagLayout with the specified 
	 * constraint parameters.
	 * 
	 * @param panel		The panel to add the component to
	 * @param comp		The component to add
	 * @param x			Horizontal grid position (gridx)
	 * @param y			Vertical grid position (gridy)
	 * @param width		Number of columns to span (gridwidth)
	 * @param height	Number of rows to span (gridheight)
	 * @param insets	External padding
	 * @param anchor	Anchor
	 * @param fill		Fill direction
	 */
	private void addWithConstraints(JPanel panel, Component comp, int x, int y,
			int width, int height, Insets insets, int anchor, int fill) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.gridheight = height;
		c.insets = insets;
		c.anchor = anchor;
		c.fill = fill;
		
		panel.add(comp, c);
	}
}
