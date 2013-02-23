

/*TODO
 * - Should the subject update or revert when the user clicks the X to close
 *    instead of clicking the Done button? (currently: update)
 * - How should invalid start/end times be handled on attempting to update
 *    the subject? (currently: start/end dates stay as whatever they were
 *    when the dialog was opened)
 */

import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.explodingpixels.macwidgets.IAppWidgetFactory;


public class SubjectEntryDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	/* A not-typedef for GridBagConstraints, since Java has no typedef. */
	@SuppressWarnings("serial")
	private class GBC extends GridBagConstraints {
	}
	
	private Subject subject;
	
	private HomeworkThingListModel subjectModel;
	
	private JPanel mainPanel;
	
	private JTextField subjectName;
	private JTextField subjectAbbrev;
	
	private JButton colorButton;
	
	private LetterToggleButton[] dayButtons;
	private JSpinner startSpinner;
	private SpinnerDateModel startModel;
	private JSpinner endSpinner;
	private SpinnerDateModel endModel;
	private SpinnerListModel blankModel;
	private FocusListener timeSetListener;
	private JButton clearButton;
	
	private JTextField locationField;
	private JTextField instructorField;
	private JTextField emailField;
	
	private JTextArea notesText;
	
	private JButton doneButton;
	private JButton cancelButton;
	
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

	
	public SubjectEntryDialog() {
		this(null);
	}
	
	public SubjectEntryDialog(Subject subj) {
		this(null, null);
	}
	
	public SubjectEntryDialog(Subject subj, HomeworkThingListModel model) {
		state = OPEN;
		
		if (subj == null)
			subject = new Subject();
		else
			subject = subj;
		
		if (model == null) 
			subjectModel = new HomeworkThingListModel();
		else 
			subjectModel = model;
		
		setTitle("Edit Subject");
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(mainPanel);
		mainPanel.setLayout(new GridBagLayout());
		
		int verticalPos = 0;
		
		
		//////// Name and color ////////
		addWithConstraints(mainPanel, new JLabel("Subject"), 0, verticalPos, 
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		subjectAbbrev = new JTextField(subject.getAbbrev(), 4);
		subjectName = new JTextField(subject.getName(), 20);
		
		colorButton = new JButton();
		colorButton.setBackground(subject.getColor());
		colorButton.setPreferredSize(new Dimension(40, 20));
		
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Color temp = JColorChooser.showDialog(mainPanel, 
						"Subject Color", subject.getColor());
				
				if (temp != null) {
					colorButton.setBackground(temp);
					repaint();
				}
			}
		});
		
		JPanel subjectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		subjectPanel.add(new JLabel("["));
		subjectPanel.add(subjectAbbrev);
		subjectPanel.add(new JLabel("]"));
		subjectPanel.add(getSpacerPanel(5, 5));
		subjectPanel.add(subjectName);
		
		addWithConstraints(mainPanel, subjectPanel, 1, verticalPos++, 1, 1, 
				TF_INSETS, GBC.WEST, GBC.HORIZONTAL);
		
		JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		colorPanel.add(new JLabel("Display color:"));
		colorPanel.add(colorButton);
		addWithConstraints(mainPanel, colorPanel, 1, verticalPos++, 1, 1,
				new Insets(SMALL_SPACE, 5, 0, 0), GBC.WEST, GBC.HORIZONTAL);
		
		addSpacerPanel(verticalPos++, 0, BIG_SPACE);

		
		//////// Days ////////
		addWithConstraints(mainPanel, new JLabel("Days"), 0, verticalPos, 
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		dayButtons = new LetterToggleButton[7];
		dayButtons[0] = new LetterToggleButton('S');
		dayButtons[1] = new LetterToggleButton('M');
		dayButtons[2] = new LetterToggleButton('T');
		dayButtons[3] = new LetterToggleButton('W');
		dayButtons[4] = new LetterToggleButton('R');
		dayButtons[5] = new LetterToggleButton('F');
		dayButtons[6] = new LetterToggleButton('S');
		
		boolean[] days = subject.getDays();
		for (int i = 0; i < 7; ++i) {
			dayPanel.add(dayButtons[i]);
			dayButtons[i].setSelected(days[i]);
		}
			
		addWithConstraints(mainPanel, dayPanel, 1, verticalPos++, 1, 1,
				TF_INSETS, GBC.WEST, GBC.HORIZONTAL);
		
		addSpacerPanel(verticalPos++, 0, SMALL_SPACE);
		
		
		//////// Times ////////
		addWithConstraints(mainPanel, new JLabel("Times"), 0, verticalPos,
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		
		startModel = new SpinnerDateModel();
		startSpinner = new JSpinner();
		startSpinner.setPreferredSize(new Dimension(90, 26)); 
		
		endModel = new SpinnerDateModel();
		endSpinner = new JSpinner();
		endSpinner.setPreferredSize(new Dimension(90, 26));
		
		blankModel = new SpinnerListModel(new String[]{""});
		
		timeSetListener = new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				setupSpinners(true, null);
			}
		};
		
		clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setupSpinners(false, null);
			}
		});
		
		// A subject's time starts out as null. If the given subject's time 
		// is null, don't add a time until the user at least focuses on
		// the time components.
		setupSpinners(subject.getTime() != null, subject.getTime());
		
		timePanel.add(startSpinner);
		timePanel.add(new JLabel("to"));
		timePanel.add(endSpinner);
		timePanel.add(getSpacerPanel(2, 1));
		timePanel.add(clearButton);
		
		addWithConstraints(mainPanel, timePanel, 1, verticalPos++, 
				1, 1, NO_INSETS, GBC.WEST, GBC.HORIZONTAL);
		
		addSpacerPanel(verticalPos++, 0, BIG_SPACE);

		
		//////// Location ////////
		addWithConstraints(mainPanel, new JLabel("Location"), 0, verticalPos,
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		locationField = new JTextField(20);
		addWithConstraints(mainPanel, locationField, 1, verticalPos++, 1, 1,
				TF_INSETS, GBC.WEST, GBC.HORIZONTAL);
		
		addSpacerPanel(verticalPos++, 0, BIG_SPACE);

		
		//////// Instructor ////////
		addWithConstraints(mainPanel, new JLabel("Instructor"), 0, verticalPos, 
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		instructorField = new JTextField(20);
		addWithConstraints(mainPanel, instructorField, 1, verticalPos++, 1, 1,
				TF_INSETS, GBC.WEST, GBC.HORIZONTAL);
		
		addSpacerPanel(verticalPos++, 0, SMALL_SPACE);
		
		
		//////// Email ////////
		addWithConstraints(mainPanel, new JLabel("E-mail"), 0, verticalPos,
				1, 1, TF_LABEL_INSETS, GBC.EAST, GBC.NONE);
		
		emailField = new JTextField(20);
		addWithConstraints(mainPanel, emailField, 1, verticalPos++, 1, 1,
				TF_INSETS, GBC.WEST, GBC.HORIZONTAL);
		
		addSpacerPanel(verticalPos++, 0, BIG_SPACE);

		
		//////// Notes ////////
		addWithConstraints(mainPanel, new JLabel("Notes"), 0, verticalPos, 
				1, 1, new Insets(2, 0, 2, LIR), GBC.NORTHEAST, GBC.NONE);
		
		JScrollPane notesScroll = new JScrollPane();
		notesScroll.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		IAppWidgetFactory.makeIAppScrollPane(notesScroll);
		addWithConstraints(mainPanel, notesScroll, 1, verticalPos++, 1, 1, 
				new Insets(0, 8, 0, 3), GBC.NORTHWEST, GBC.BOTH);
		
		notesText = new JTextArea();
		notesText.setRows(3);
		notesText.setText(subject.getNotes());
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
				updateSubject();
				setVisible(false);
				dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state = CANCEL;
				
				// Deregister the WindowListeners that usually would update
				// the subject when the window is closed
				WindowListener[] listeners = getWindowListeners();
				for (WindowListener l : listeners)
					removeWindowListener(l);

				setVisible(false);
				dispose();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				state = ACCEPT;
				updateSubject();
				setVisible(false);
				dispose();
			}
		});
		
		pack();
	}
	
	
	public Subject getSubject() {
		return subject;
	}
	
	public HomeworkThingListModel getSubjectModel() {
		return subjectModel;
	}
	
	public int getState() {
		return state;
	}
	
	private void addSpacerPanel(int y_pos, int width, int height) {
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(width, height));
		addWithConstraints(mainPanel, spacer, 0, y_pos, 2, 1, NO_INSETS, 
				GBC.CENTER, GBC.NONE);
	}
	
	
	// useTime tells whether to make the spinners show times or be blank.
	// If useTime is true, use the start and end times specified by time.
	// If time is null, use a default start and end time.
	private void setupSpinners(boolean useTime, DatePair time) {
		if (useTime) {
			startSpinner.setModel(startModel);
			startSpinner.setEditor(
					new JSpinner.DateEditor(startSpinner, "h:mm a"));
			endSpinner.setModel(endModel);
			endSpinner.setEditor(
					new JSpinner.DateEditor(endSpinner, "h:mm a"));
			clearButton.setEnabled(true);
		}
		
		if (useTime && time == null) {
			// Pick an arbitrary time: 1-2 PM
			GregorianCalendar cal = new GregorianCalendar();
			cal.set(Calendar.HOUR, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.AM_PM, Calendar.PM);
			startModel.setValue(cal.getTime());

			cal.add(Calendar.HOUR, 1); // don't use roll() here
			endModel.setValue(cal.getTime());
			
			getSpinnerField(startSpinner).removeFocusListener(timeSetListener);
			getSpinnerField(endSpinner).removeFocusListener(timeSetListener);
		} else if (useTime) {
			startModel.setValue(subject.getTime().getStart());
			startSpinner.setModel(startModel);
			startSpinner.setEditor(
					new JSpinner.DateEditor(startSpinner, "h:mm a"));
			
			endModel.setValue(subject.getTime().getEnd());
			endSpinner.setModel(endModel);
			endSpinner.setEditor(
					new JSpinner.DateEditor(endSpinner, "h:mm a"));	
		} else {
			startSpinner.setModel(blankModel);
			startSpinner.setEditor(new JSpinner.ListEditor(startSpinner));
			getSpinnerField(startSpinner).addFocusListener(timeSetListener);
			endSpinner.setModel(blankModel);
			endSpinner.setEditor(new JSpinner.ListEditor(endSpinner));
			getSpinnerField(endSpinner).addFocusListener(timeSetListener);
			clearButton.setEnabled(false);
		}
	}
	
	private static JPanel getSpacerPanel(int width, int height) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(width, height));
		return panel;
	}
	
	private static JFormattedTextField getSpinnerField(JSpinner spinner) {
		if (spinner.getEditor() instanceof JSpinner.DefaultEditor)
			return ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
		else
			return null;
	}
	
	/**
	 * Updates the Subject object to reflect the changes made in the dialog.
	 * Also adds the Subject to the model if it's not already present.
	 */
	public void updateSubject() {
		if (subjectAbbrev.getText().isEmpty())
			subject.setName(subjectName.getText());
		else
			subject.setNameAbbrev(subjectName.getText(), 
					subjectAbbrev.getText());
		
		subject.setColor(colorButton.getBackground());
		
		boolean[] days = new boolean[7];
		for (int i = 0; i < 7; ++i)
			days[i] = dayButtons[i].isSelected();
		subject.setDays(days);
		
		if (startSpinner.getModel() == blankModel) {
			subject.setTime(null);
		} else {
			DatePair time = null;
			try {
				time = new DatePair((Date)startModel.getValue(), 
						(Date)endModel.getValue());
				subject.setTime(time);
			} catch (Exception ex) {
				System.err.println("Invalid start and end dates.");
			}
		}
		
		subject.setLocation(locationField.getText());
		subject.setInstructor(instructorField.getText());
		subject.setEmail(emailField.getText());
		
		subject.setNotes(notesText.getText());

		// Add the subject to the model if it's not already present
		if (subjectModel.getIndexOf(subject) == -1)
			subjectModel.addElement(subject);

		//TODO delete this later
		System.out.println(subject.toLongString());
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
