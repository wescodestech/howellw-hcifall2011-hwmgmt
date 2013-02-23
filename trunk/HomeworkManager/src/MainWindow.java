import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXPanel;
import com.explodingpixels.macwidgets.IAppWidgetFactory;

/**
 * IMPORTANT NOTE ABOUT SIDEBAR:
 * Currently, everything works roughly correctly when two panels are open at once.
 * If you try to open three panels at once, the bottom one will be cut off.
 */

public class MainWindow extends JFrame {

	public static void main(String[] args) {

		System.out.println(System.getProperty("os.name"));
		for (java.util.Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
            System.out.println(e);
        }

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			System.err.println("Couldn't set look and feel.");
		}

		HomeworkManager hm = new HomeworkManager();

		new MainWindow(hm.getTaskModel(), hm.getTypeModel(), hm.getSubjectModel());
	}

	// JXCollapsiblePane's sizing code makes a MESS of multiple stacked panes.
	private class JXCollapsiblePaneFixed extends JXCollapsiblePane {
		private static final long serialVersionUID = 1L;
		
		private final Dimension MIN = new Dimension(0,0);
		public Dimension getMinimumSize() {
			if (isCollapsed())
				return MIN;
			else {
				Dimension temp = super.getMinimumSize();
				return new Dimension(temp.width, temp.height - 25);
			}
		}
		public Dimension getPreferredSize() {
			if (isCollapsed())
				return MIN;
			else {
				Dimension temp = super.getPreferredSize();
				return new Dimension(temp.width, temp.height - 25);
			}
		}
		public Dimension getMaximumSize() {
			// This override is the most essential in getting the panels to
			// behave, at least in a BoxLayout.
			if (isCollapsed())
				return MIN;
			else {
				Dimension temp = super.getMaximumSize();
				return new Dimension(temp.width, temp.height - 25);
			}
		}
	}

	private static final long ONE_DAY = getNumDays(1);
	private static final long THREE_DAYS = getNumDays(3);
	private static final long FIVE_DAYS = getNumDays(5);
	private static final long SEVEN_DAYS = getNumDays(7);
	
	private static final int SIDEBAR_WIDTH = 220;
	private static final int SIDEBAR_TOP_HEIGHT = 410;
	private static final int TOGGLE_HEIGHT = 25;
	private static final Dimension TOGGLE_DIM = 
			new Dimension(SIDEBAR_WIDTH, TOGGLE_HEIGHT);
	private static final int DETAIL_HEIGHT = 220;
	private static final int RIGHT_WIDTH = 780;
	
	private static final long serialVersionUID = 1L;

	private WindowAdapter subjectUpdateListener = new WindowAdapter() {
		public void windowClosed(WindowEvent e) {
			if (sed.getState() == SubjectEntryDialog.ACCEPT) {
				subjectList.setSelectedValue(sed.getSubject(), true);
				showDetail("subject");
			}
		}
	};
	
	private WindowAdapter taskUpdateListener = new WindowAdapter() {
		public void windowClosed(WindowEvent e) {
			if (ted.getState() == TaskEntryDialog.ACCEPT) {
				tasks.setSelectedIndex(tasks.getIndexOf(ted.getTask()));
				showDetail("task"); 
			}
		}
	};
	
	// Listener for anything involving adding tasks (buttons/menu items)
	private ActionListener addTaskListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// When the dialog finishes, the task will be added if the user
			// accepts or not added if the user cancels
			ted = new TaskEntryDialog(new Task(), tasks, subjects, taskTypes);
			ted.setVisible(true);
			ted.addWindowListener(taskUpdateListener);
		}
	};
	
	private HomeworkThingListModel tasks;
	private DefaultComboBoxModel taskTypes; // is also a ListModel for JList
	private HomeworkThingListModel subjects; 
	private DefaultListModel toDoModel;
	private TaskView mainView;
	private SubjectEntryDialog sed;
	private TaskEntryDialog ted;
	
	private JList subjectList;
	private JList scheduleList;
	private JList toDoList;
	
	private JPanel mainPanel;
	private JPanel sidebarTop;
	private JButton subjectToggle;
	private JXCollapsiblePane subjectCPane;
	private JButton addSubjectButton;
	private JButton editSubjectButton;
	private JButton deleteSubjectButton;
	private JButton scheduleToggle;
	private JXCollapsiblePane scheduleCPane;
	private JButton addScheduleButton;
	private JButton editScheduleButton;
	private JButton deleteScheduleButton;
	private JButton toDoToggle;
	private JXCollapsiblePane toDoCPane;
	private JButton addToDoButton;
	private JButton editToDoButton;
	private JButton deleteToDoButton;
	private CardLayout detailCards;
	private JButton detailEditButton;
	private JButton detailDeleteButton;
	private JButton addTaskButton;
	private JButton backButton;
	private JButton todayButton;
	private JButton forwardButton;
	private JToggleButton threeDaysButton;
	private JToggleButton fiveDaysButton;
	private JToggleButton sevenDaysButton;
	private JPanel sidebarBottom;
	private JPanel detailPanel;
	private JPanel blankPanel;
	private TaskDetailPanel taskDetailPanel;
	private SubjectDetailPanel subjectDetailPanel;
	private JPanel detailButtonPanel;
	
	private String detailPanelState;
	
	public MainWindow(HomeworkThingListModel taskModel, DefaultComboBoxModel typeModel,
			HomeworkThingListModel subjectModel) {
		this.tasks = taskModel;
		this.taskTypes = typeModel;
		this.subjects = subjectModel;
		
		setTitle("Homework Manager");
		setResizable(false);

		mainPanel = new JPanel(new BorderLayout(10, 10));
		getContentPane().add(mainPanel);
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel sidebar = new JPanel(new BorderLayout(0, 20));
		mainPanel.add(sidebar, BorderLayout.WEST);
		
		JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.add(rightPanel, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel(new BorderLayout());
		buttons.setPreferredSize(new Dimension(new Dimension(RIGHT_WIDTH, 25)));
		rightPanel.add(buttons, BorderLayout.NORTH);
		
		
		//////////////// Sidebar ////////////////
		sidebarTop = new JXPanel();
		BoxLayout sidebarLayout = new BoxLayout(sidebarTop, BoxLayout.Y_AXIS);
		sidebarTop.setLayout(sidebarLayout);
		Dimension sidebarTopDim = new Dimension(SIDEBAR_WIDTH, SIDEBAR_TOP_HEIGHT);
		sidebarTop.setPreferredSize(sidebarTopDim);
		sidebarTop.setMaximumSize(sidebarTopDim);
		sidebarTop.setMinimumSize(sidebarTopDim);
		sidebar.add(sidebarTop, BorderLayout.CENTER);

		//////// Subjects ////////
		subjectCPane = new JXCollapsiblePaneFixed();
		subjectToggle = new JButton();
		subjectList = new JList(subjects);
		JScrollPane subjectScroll = new JScrollPane(subjectList);
		addSubjectButton = new JButton("Add");
		editSubjectButton = new JButton("Edit");
		deleteSubjectButton = new JButton("Delete");
		setupCollapsiblePane(subjectToggle, subjectCPane, subjectList, 
				subjectScroll, addSubjectButton, editSubjectButton, 
				deleteSubjectButton);
		
		subjectToggle.setText("Subjects");
		subjectCPane.setCollapsed(false);
		subjectList.setVisibleRowCount(8);
		subjectList.setCellRenderer(new ColorListCellRenderer());
		
		addSubjectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sed = new SubjectEntryDialog(new Subject(), subjects);
				sed.setVisible(true);
				sed.addWindowListener(subjectUpdateListener);
			}
		});
		
		editSubjectButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sed = new SubjectEntryDialog(
						(Subject)subjectList.getSelectedValue(),
						subjects);
				sed.setVisible(true);
				sed.addWindowListener(subjectUpdateListener);
			}
		});
		
		deleteSubjectButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int option = JOptionPane.showConfirmDialog(mainPanel, 
						"Are you sure you want to delete " + 
						subjectList.getSelectedValue().toString() + "?", 
						"Confirm Delete", JOptionPane.YES_NO_CANCEL_OPTION);

				if (option != JOptionPane.YES_OPTION) 
					return;
				subjects.removeElement(subjectList.getSelectedValue());
				showDetail("blank");
			}
		});
		
		subjectList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (subjectList.getSelectedValue() == null) {
					showDetail("blank");
					editSubjectButton.setEnabled(false);
					deleteSubjectButton.setEnabled(false);
				} else {
					showDetail("subject");
					editSubjectButton.setEnabled(true);
					deleteSubjectButton.setEnabled(true);
				}
			}
		});
		
		
		//////// Schedule ////////
		scheduleCPane = new JXCollapsiblePaneFixed();
		scheduleToggle = new JButton();
		scheduleList = new JList(new String[]{"Monday", " ", "Tuesday", " ",
				"Wednesday", " ", "Thursday", " ", "Friday", " "});
		JScrollPane scheduleScroll = new JScrollPane(scheduleList);
		addScheduleButton = new JButton("Add");
		editScheduleButton = new JButton("Edit");
		deleteScheduleButton = new JButton("Delete");

		setupCollapsiblePane(scheduleToggle, scheduleCPane, scheduleList, 
				scheduleScroll, addScheduleButton, editScheduleButton, 
				deleteScheduleButton);
		
		scheduleToggle.setText("Schedule");
		scheduleCPane.setCollapsed(true);
		scheduleList.setVisibleRowCount(8);
		
		scheduleList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//TODO check all this
				if (scheduleList.getSelectedValue() != null)
					showDetail("blank");
			}
		});
		
		
		//////// To do list ////////
		toDoCPane = new JXCollapsiblePaneFixed();
		toDoToggle = new JButton();
		toDoModel = new DefaultListModel();
		toDoList = new JList(toDoModel);
		JScrollPane toDoScroll = new JScrollPane(toDoList);
		addToDoButton = new JButton("Add");
		editToDoButton = new JButton("Edit");
		deleteToDoButton = new JButton("Delete");
		
		setupCollapsiblePane(toDoToggle, toDoCPane, toDoList, toDoScroll,
				addToDoButton, editToDoButton, deleteToDoButton);
		
		toDoToggle.setText("To Do");
		toDoCPane.setCollapsed(true);
		toDoList.setVisibleRowCount(8);
		
		addToDoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String item = JOptionPane.showInputDialog(mainPanel, 
						"Add an item to your To Do List", "Add To Do", 
						JOptionPane.PLAIN_MESSAGE);
				if (item != null && item.isEmpty() == false)
					toDoModel.addElement(item);
			}
		});
		
		editToDoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = toDoList.getSelectedIndex();
				String item = (String)toDoList.getSelectedValue();
				toDoModel.remove(index);
				item = JOptionPane.showInputDialog(mainPanel, "Edit this item",
						"Edit To Do", JOptionPane.PLAIN_MESSAGE);
				toDoModel.add(index, item);
			}
		});
		
		deleteToDoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toDoModel.remove(toDoList.getSelectedIndex());
			}
		});
		
		toDoList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (toDoList.getSelectedValue() == null) {
					editToDoButton.setEnabled(false);
					deleteToDoButton.setEnabled(false);
				} else {
					showDetail("blank");
					editToDoButton.setEnabled(true);
					deleteToDoButton.setEnabled(true);
				}
			}
		});
		
		
		//TODO did this help anything?
		sidebarTop.add(Box.createVerticalGlue());
		

		//////// Detail panel ////////
		sidebarBottom = new JPanel(new BorderLayout(0, 5));
		sidebar.add(sidebarBottom, BorderLayout.SOUTH);
		
		detailPanel = new JPanel();
		Dimension detailDim = new Dimension(SIDEBAR_WIDTH, DETAIL_HEIGHT);
		detailPanel.setPreferredSize(detailDim);
		detailPanel.setMinimumSize(detailDim);
		detailPanel.setMaximumSize(detailDim);
		sidebarBottom.add(detailPanel, BorderLayout.CENTER);

		detailCards = new CardLayout(0, 0);
		detailPanel.setLayout(detailCards);
		
		blankPanel = new JPanel();
		blankPanel.setBackground(Color.WHITE);
		detailPanel.add(blankPanel, "blank");
		
		taskDetailPanel = new TaskDetailPanel();
		detailPanel.add(taskDetailPanel, "task");

		subjectDetailPanel = new SubjectDetailPanel();
		detailPanel.add(subjectDetailPanel, "subject");
		
		//////// Detail buttons ////////
		detailButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		sidebarBottom.add(detailButtonPanel, BorderLayout.SOUTH);
		
		detailEditButton = new JButton("Edit");
		detailButtonPanel.add(detailEditButton);
		
		detailDeleteButton = new JButton("Delete");
		detailButtonPanel.add(detailDeleteButton);
		
		detailEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (detailPanelState.equals("blank")) 
					return;
				else if (detailPanelState.equals("subject")) {
					sed = new SubjectEntryDialog(subjectDetailPanel.getSubject(), 
							subjects);
					sed.setVisible(true);
					sed.addWindowListener(subjectUpdateListener);
				} else if (detailPanelState.equals("task")) {
					ted = new TaskEntryDialog(taskDetailPanel.getTask(), tasks,
							subjects, taskTypes);
					ted.setVisible(true);
					ted.addWindowListener(taskUpdateListener);
				}
			}
		});
		
		detailDeleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (detailPanelState.equals("blank"))
					return;
				else if (detailPanelState.equals("subject")) {
					int option = JOptionPane.showConfirmDialog(mainPanel, 
							"Are you sure you want to delete " + 
							subjectDetailPanel.getSubject().toString() + "?", 
							"Confirm Delete", JOptionPane.YES_NO_CANCEL_OPTION);

					if (option != JOptionPane.YES_OPTION) 
						return;
					subjects.removeElement(subjectDetailPanel.getSubject());
					showDetail("blank");
				} else if (detailPanelState.equals("task")) {
					int option = JOptionPane.showConfirmDialog(mainPanel,
							"Are you sure you want to delete " + 
							taskDetailPanel.getTask().toString() + "?",
							"Confirm Delete", JOptionPane.YES_NO_CANCEL_OPTION);
					if (option != JOptionPane.YES_OPTION)
						return;
					tasks.removeElement(taskDetailPanel.getTask());	
					showDetail("blank");
				}
			}
		});
		
		showDetail("blank");
		
		
		//////////////// Buttons ////////////////

		//////// Add task ////////
		addTaskButton = new JButton("Add New Task");
		addTaskButton.setIcon(new ImageIcon("images/plus.png"));
		addTaskButton.setIconTextGap(6);
		addTaskButton.addActionListener(addTaskListener);
		
		JPanel addTaskPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		addTaskPanel.add(addTaskButton);
		buttons.add(addTaskPanel, BorderLayout.WEST);
		
		//////// Navigation ////////
		JPanel navButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		buttons.add(navButtonPanel, BorderLayout.CENTER);
		
		backButton = new JButton("<<");
		navButtonPanel.add(backButton);
		
		todayButton = new JButton("Today");
		navButtonPanel.add(todayButton);
		
		forwardButton = new JButton(">>");
		navButtonPanel.add(forwardButton);
		
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainView.setDateRange(new DatePair(
						mainView.getStartTime() - ONE_DAY,
						mainView.getEndTime() - ONE_DAY));
			}
		});
		
		forwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainView.setDateRange(new DatePair(
						mainView.getStartTime() + ONE_DAY,
						mainView.getEndTime() + ONE_DAY));
			}
		});
		
		todayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainView.setDateRange(new DatePair(new Date().getTime(),
						mainView.getEndTime() - mainView.getStartTime() 
						+ new Date().getTime()));
			}
		});
		
		//////// Setting view length ////////
		JPanel dayButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		buttons.add(dayButtonPanel, BorderLayout.EAST);
		
		ButtonGroup bg = new ButtonGroup();
		
		threeDaysButton = new JToggleButton("3 Days");
		threeDaysButton.setSelected(true);
		dayButtonPanel.add(threeDaysButton);
		
		fiveDaysButton = new JToggleButton("5 Days");
		dayButtonPanel.add(fiveDaysButton);
		
		sevenDaysButton = new JToggleButton("7 Days");
		dayButtonPanel.add(sevenDaysButton);
		
		bg.add(threeDaysButton);
		bg.add(fiveDaysButton);
		bg.add(sevenDaysButton);
	
		threeDaysButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (threeDaysButton.isSelected() == false) return;
				mainView.setDateRange(new DatePair(
						mainView.getDateRange().getStart().getTime(),
						mainView.getDateRange().getStart().getTime() + THREE_DAYS));
			}
		});
		
		fiveDaysButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fiveDaysButton.isSelected() == false) return;
				mainView.setDateRange(new DatePair(
						mainView.getDateRange().getStart().getTime(),
						mainView.getDateRange().getStart().getTime() + FIVE_DAYS));
			}
		});
		
		sevenDaysButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (sevenDaysButton.isSelected() == false) return;
				mainView.setDateRange(new DatePair(
						mainView.getDateRange().getStart().getTime(),
						mainView.getDateRange().getStart().getTime() + SEVEN_DAYS));
			}
		});
		
		//////////////// Display ////////////////
		mainView = new TaskView(tasks, new DatePair(System.currentTimeMillis(),
				System.currentTimeMillis() + THREE_DAYS));
		rightPanel.add(mainView, BorderLayout.CENTER);
		
		mainView.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (mainView.getSelectedItem() == null) {
					showDetail("blank");
				} else {
					showDetail("task");
				}
			}
		});
		
	
		setupMenus();
		pack();
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void showDetail(String name) {
		if ((name.equals("blank") || name.equals("subject") 
				|| name.equals("task")) == false)
			return;

		detailPanelState = name;
		
		if (name.equals("blank")) {
			detailEditButton.setEnabled(false);
			detailDeleteButton.setEnabled(false);
		} else if (name.equals("subject")) {
			subjectDetailPanel.setSubject((Subject)subjectList.getSelectedValue());
			detailEditButton.setEnabled(true);
			detailDeleteButton.setEnabled(true);
		} else if (name.equals("task")) {
			taskDetailPanel.setTask((Task)mainView.getSelectedItem());
			detailEditButton.setEnabled(true);
			detailDeleteButton.setEnabled(true);
		}
		detailCards.show(detailPanel, name);
		repaint();
	}
	
	private void setupCollapsiblePane(final JButton toggle, 
			final JXCollapsiblePane cpane, JList list, JScrollPane scroll, 
			JButton addButton, JButton editButton, JButton deleteButton) {

		cpane.setBorder(new CompoundBorder(
				new BetterEtchedBorder(false, true, true, true),
				new EmptyBorder(0,2,2,2)));
		cpane.setLayout(new BorderLayout(0,0));

		toggle.setAction(
				cpane.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
		toggle.setHorizontalAlignment(SwingConstants.LEFT);
		toggle.setPreferredSize(TOGGLE_DIM);
		toggle.setMaximumSize(TOGGLE_DIM);
		toggle.setMinimumSize(TOGGLE_DIM);
		sidebarTop.add(toggle);
		toggle.setAlignmentX(CENTER_ALIGNMENT);
		toggle.setAlignmentY(TOP_ALIGNMENT);

		sidebarTop.add(cpane);
		cpane.setAlignmentX(CENTER_ALIGNMENT);
		cpane.setAlignmentY(TOP_ALIGNMENT);
		cpane.setMinimumSize(new Dimension(0,0));
		
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		IAppWidgetFactory.makeIAppScrollPane(scroll);
		cpane.add(scroll, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttons.add(addButton);
		buttons.add(editButton);
		buttons.add(deleteButton);
		editButton.setEnabled(false);
		deleteButton.setEnabled(false);
		
		cpane.add(buttons, BorderLayout.SOUTH);
		cpane.setAnimated(false);
	}

	private void setupMenus() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		//////// File Menu ////////
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem newTaskItem = new JMenuItem("New Task...");
		fileMenu.add(newTaskItem);

		JMenuItem newToDoItem = new JMenuItem("New To Do...");
		fileMenu.add(newToDoItem);

		JMenuItem newCourseItem = new JMenuItem("New Course...");
		fileMenu.add(newCourseItem);

		JSeparator separator = new JSeparator();
		fileMenu.add(separator);

		JMenuItem printItem = new JMenuItem("Print");
		fileMenu.add(printItem);

		fileMenu.add(new JSeparator());

		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(quitItem);

		/*
		 * Edit Menu
		 */
		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);

		JMenuItem mntmAddAssignment = new JMenuItem("Undo");
		editMenu.add(mntmAddAssignment);

		JMenuItem mntmEditAssignment = new JMenuItem("Redo");
		editMenu.add(mntmEditAssignment);

		editMenu.add(new JSeparator());

		JMenuItem mntmSchedule = new JMenuItem("Cut");
		editMenu.add(mntmSchedule);

		JMenuItem mntmCopy = new JMenuItem("Copy");
		editMenu.add(mntmCopy);

		JMenuItem mntmPaste = new JMenuItem("Paste");
		editMenu.add(mntmPaste);

		JMenuItem mntmDuplicate = new JMenuItem("Duplicate");
		editMenu.add(mntmDuplicate);

		JMenuItem mntmDelete = new JMenuItem("Delete");
		editMenu.add(mntmDelete);

		JMenuItem mntmSelectAll = new JMenuItem("Select All");
		editMenu.add(mntmSelectAll);

		editMenu.add(new JSeparator());

		JMenuItem mntmEditSelectedItem = new JMenuItem("Edit Selected Task...");
		editMenu.add(mntmEditSelectedItem);

		editMenu.add(new JSeparator());

		JMenuItem mntmPreferences = new JMenuItem("Preferences...");
		editMenu.add(mntmPreferences);

		/*
		 * View Menu
		 */
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		JCheckBoxMenuItem chckbxmntmShowCompletedTask = new JCheckBoxMenuItem("Show Completed Task");
		mnView.add(chckbxmntmShowCompletedTask);

		mnView.add(new JSeparator());

		JCheckBoxMenuItem chckbxmntmDays = new JCheckBoxMenuItem("3 Days");
		mnView.add(chckbxmntmDays);

		JCheckBoxMenuItem chckbxmntmDays_1 = new JCheckBoxMenuItem("5 Days");
		mnView.add(chckbxmntmDays_1);

		JCheckBoxMenuItem chckbxmntmDays_2 = new JCheckBoxMenuItem("7 Days");
		mnView.add(chckbxmntmDays_2);

		mnView.add(new JSeparator());

		JMenu mnSortBy = new JMenu("Sort By");
		mnView.add(mnSortBy);

		JCheckBoxMenuItem chckbxmntmDueDate = new JCheckBoxMenuItem("Due Date");
		mnSortBy.add(chckbxmntmDueDate);

		JCheckBoxMenuItem chckbxmntmStartDate = new JCheckBoxMenuItem("Start Date");
		mnSortBy.add(chckbxmntmStartDate);

		JCheckBoxMenuItem chckbxmntmNextWorkTime = new JCheckBoxMenuItem("Next Work Time");
		mnSortBy.add(chckbxmntmNextWorkTime);

		JCheckBoxMenuItem chckbxmntmPriority = new JCheckBoxMenuItem("Priority");
		mnSortBy.add(chckbxmntmPriority);

		mnView.add(new JSeparator());

		JMenu mnNewMenu = new JMenu("Show Subjects");
		mnView.add(mnNewMenu);

		JCheckBoxMenuItem chckbxmntmCalculus = new JCheckBoxMenuItem("Calculus");
		mnNewMenu.add(chckbxmntmCalculus);

		JCheckBoxMenuItem chckbxmntmHistory = new JCheckBoxMenuItem("History");
		mnNewMenu.add(chckbxmntmHistory);

		JCheckBoxMenuItem chckbxmntmHumancomputerInteraction = new JCheckBoxMenuItem("Human-Computer Interaction");
		mnNewMenu.add(chckbxmntmHumancomputerInteraction);

		JCheckBoxMenuItem chckbxmntmPublicSpeaking = new JCheckBoxMenuItem("Public Speaking");
		mnNewMenu.add(chckbxmntmPublicSpeaking);

		/*
		 * Help Menu
		 */
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmHelpInformation = new JMenuItem("Help...");
		mnHelp.add(mntmHelpInformation);

		JMenuItem mntmAbout = new JMenuItem("About...");
		mnHelp.add(mntmAbout);

		final PopupMenuSubject classPopupMenu = new PopupMenuSubject();
		subjectList.add(classPopupMenu.getMenu());
		subjectList.addMouseListener(classPopupMenu);
//		sch1.add(classPopupMenu.getMenu());
//		sch1.addMouseListener(classPopupMenu);
//
//		final PopupMenuInfo infoPopupMenu = new PopupMenuInfo();
//		lblNewLabel_1.add(infoPopupMenu.getMenu());
//		lblNewLabel_1.addMouseListener(infoPopupMenu);
//
//		final PopupMenuAssignment assignmentPopupMenu = new PopupMenuAssignment();
//		lblNewLabel.add(assignmentPopupMenu.getMenu());
//		lblNewLabel.addMouseListener(assignmentPopupMenu);
	}
	
	private static long getNumDays(int num) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date(0)); // start date is epoch
		cal.add(GregorianCalendar.DATE, num);
		return cal.getTimeInMillis();
		//TODO make sure this is correct
	}
}
