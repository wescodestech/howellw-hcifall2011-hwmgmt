import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Task implements java.io.Serializable { 
	
	private static final long serialVersionUID = 1L;

	/**
	 * Priority enum with automatic toString
	 */
	public enum Priority {
		NONE		("None", 0),
		LOW			("Low", 1),
		MEDIUM		("Medium", 2),
		HIGH		("High", 3);
		
		private final String name;		
		private final int number;
		private Priority(String name, int number) {
			this.name = name;
			this.number = number;
		}
		
		public int getNumber() {
			return number;
		}
		
		public String getName() {
			return name;
		}
		
		public static Priority getPriority(int number) {
			switch (number) {
			case 0:		return NONE;
			case 1: 	return LOW;
			case 2: 	return MEDIUM;
			case 3:		return HIGH;
			default:	return null;
			}
		}
		
		public static Priority getPriority(String name) {
			if (name.equalsIgnoreCase("None"))		return NONE;
			if (name.equalsIgnoreCase("Low"))		return LOW;
			if (name.equalsIgnoreCase("Medium"))	return MEDIUM;
			if (name.equalsIgnoreCase("HIGH"))		return HIGH;
			return null;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	private ArrayList<ChangeListener> changeListenerList;
	
	private String name;

	private DatePair startDue;
	
	Subject subject;
	
	private int hoursNeeded;
	private int minutesNeeded;
	
	private ArrayList<DatePair> workTimes;
	
	private Priority priority;
	
	private String type;
	
	private String notes;
	
	private static SimpleDateFormat format = 
			new SimpleDateFormat("M/d/yyyy h:mm a");
	private static SimpleDateFormat timeFormat = 
			new SimpleDateFormat("h:mm a");
	private static SimpleDateFormat dayTimeFormat =
			new SimpleDateFormat("h:mm a, EEE. MMM. d");
	
	public Task() {
		this("");
	}
	
	public Task(String name) {
		changeListenerList = new ArrayList<ChangeListener>();
		
		if (name == null)
			this.name = "";
		else
			this.name = name;

		// Make the due date 1 day after the start date
		long start = System.currentTimeMillis();
		GregorianCalendar dueCal = new GregorianCalendar();
		dueCal.setTimeInMillis(start);
		// This works properly on last day of month. roll() doesn't.
		dueCal.add(Calendar.DATE, 1); 
		startDue = new DatePair(start, dueCal.getTimeInMillis());
		
		subject = null;
		
		hoursNeeded = 0;
		minutesNeeded = 0;
		
		workTimes = new ArrayList<DatePair>();
		
		priority = Priority.NONE;
		
		type = "";
		
		notes = "";
	}
	
	public Task(String name, DatePair startDue, Subject subject,
			int hoursNeeded, int minutesNeeded, ArrayList<DatePair> workTimes,
			Priority priority, String type, String notes) {
		// Initialize everything to default values in case of errors
		this(name);
		
		// Try to set everything to the requested values, and use defaults for
		// nonsensical values
		setStartDue(startDue);
		setSubject(subject);
		setHoursNeeded(hoursNeeded);
		setMinutesNeeded(minutesNeeded);
		setWorkTimes(workTimes);
		setPriority(priority);
		setType(type);
		setNotes(notes);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null || name.isEmpty())
			this.name = "Task";
		else
			this.name = name;
		
		processEvent(new ChangeEvent(this));
	}

	public DatePair getStartDue() {
		return startDue;
	}
	
	public void setStartDue(DatePair startDue) {
		if (startDue != null) {
			this.startDue = startDue;
			processEvent(new ChangeEvent(this));
		}
	}
	
	public Date getStartDate() {
		return startDue.getStart();
	}

	public void setStartDate(Date startDate) {
		try {
			startDue.setStart(startDate);
			processEvent(new ChangeEvent(this));
		} catch (Exception ex) {
			System.err.println("Illegal start date.");
		}
	}

	public Date getDueDate() {
		return startDue.getEnd();
	}

	public void setDueDate(Date dueDate) {
		try {
			startDue.setEnd(dueDate);
			processEvent(new ChangeEvent(this));
		} catch (Exception ex) {
			System.err.println("Illegal end date.");
		}
	}

	public Subject getSubject() {
		return subject;
	}
	
	public void setSubject(Subject subject) {
		this.subject = subject;
		subject.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				processEvent(e);
			}
		});
		processEvent(new ChangeEvent(this));
	}
	
	public String getSubjectName() {
		return subject.getName();
	}

	public void setSubjectName(String subjectName) {
		if (subjectName == null) return;
		if (subject == null)
			subject = new Subject(subjectName);
		else
			subject.setName(subjectName);
		processEvent(new ChangeEvent(this));
	}

	public String getSubjectAbbrev() {
		return subject.getAbbrev();
	}
	
	public void setSubjectAbbrev(String subjectAbbrev) {
		if (subject != null && subjectAbbrev != null) {
			subject.setAbbrev(subjectAbbrev);
			processEvent(new ChangeEvent(this));
		}
	}

	public int getHoursNeeded() {
		return hoursNeeded;
	}

	public void setHoursNeeded(int hours) {
		this.hoursNeeded = hours;
		processEvent(new ChangeEvent(this));
	}

	public int getMinutesNeeded() {
		return minutesNeeded;
	}

	public void setMinutesNeeded(int minutes) {
		this.hoursNeeded += minutes / 60;
		this.minutesNeeded += minutes % 60;
		processEvent(new ChangeEvent(this));
	}
	
	public void setHoursMinutesNeeded(int hours, int minutes) {
		this.hoursNeeded = hours + (minutes / 60);
		this.minutesNeeded = minutes % 60;
		processEvent(new ChangeEvent(this));
	}
	
	public ArrayList<DatePair> getWorkTimes() {
		return workTimes;
	}
	
	public void setWorkTimes(ArrayList<DatePair> workTimes) {
		if (workTimes != null) {
			this.workTimes = workTimes;
			processEvent(new ChangeEvent(this));
		}
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		if (priority != null) {
			this.priority = priority;
			processEvent(new ChangeEvent(this));
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type == null)
			this.type = "";
		else
			this.type = type;
		processEvent(new ChangeEvent(this));
	}
	
	public String getNotes() {
		return notes;
	}
	
	public void setNotes(String notes) {
		if (notes == null)
			this.notes = "";
		else
			this.notes = notes;
		processEvent(new ChangeEvent(this));
	}
	
	public String getWorkTimeString() {
		if (workTimes == null || workTimes.isEmpty())
			return "";
		
		StringBuilder sb = new StringBuilder(200);

		GregorianCalendar startCal = new GregorianCalendar();
		GregorianCalendar endCal = new GregorianCalendar();
		
		for (DatePair dp : workTimes) {
			if (dp == null) continue;
			
			sb.append(dayTimeFormat.format(dp.getStart()) + " to ");
			
			startCal.setTime(dp.getStart());
			endCal.setTime(dp.getEnd());
			
			// If the work time starts and ends on the same day, don't include
			// the date with the end time.
			if (startCal.get(GregorianCalendar.DATE) == 
					endCal.get(GregorianCalendar.DATE))
				sb.append(timeFormat.format(dp.getEnd()) + '\n');
			else
				sb.append(dayTimeFormat.format(dp.getEnd()) + '\n');
		}
		
		sb.deleteCharAt(sb.length() - 1); // remove last newline
		
		return sb.toString();
	}
	
	//TODO is this what toString should do?
	public String toString() {
		if (subject == null || subject.getAbbrev().isEmpty()) 
			return name;
		
		return '[' + subject.getAbbrev() + "] " + name;
	}
	
	public String toColoredString() {
		if (subject == null || subject.getAbbrev().isEmpty()) 
			return name;
		
		return "<html><b><font color=\"" + getColorString(subject.getColor()) + "\">[" 
				+ subject.getAbbrev() + "]</font></b> " + name;
	}

	public String toLongString() {
		StringBuilder sb = new StringBuilder(200);
	
		// abbreviation and name
		sb.append("--- " + toString() + " ---\n");
		
		if (subject != null)
			sb.append("Subject: " + subject.getName() + '\n');
		
		sb.append("Start date: " + format.format(startDue.getStart()) + '\n');
		sb.append("Due date: " + format.format(startDue.getEnd()) + '\n');
		
		if (hoursNeeded != 0 && minutesNeeded != 0) {
			sb.append("Time needed: ");
			if (hoursNeeded != 0)
				sb.append(hoursNeeded + " hours ");
			if (minutesNeeded != 0)
				sb.append(minutesNeeded + " minutes");
			sb.append('\n');
		}
		
		if (workTimes != null && workTimes.isEmpty() == false)
			sb.append(getWorkTimeString() + '\n');
		
		sb.append("Priority: " + priority + '\n');
		
		if (type.isEmpty() == false)
			sb.append("Type: " + type + '\n');
		
		if (notes.isEmpty() == false)
			sb.append("Notes: " + notes);
		
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if ((o instanceof Task) == false) return false;
		if (this == o) return true;

		Task t = (Task)o;
		
		if (subject == null && t.getSubject() != null)
			return false;
		
		if ((t.getWorkTimes() == null && workTimes != null)
				|| (t.getWorkTimes() != null && workTimes == null)
				|| t.getWorkTimes().size() != workTimes.size())
			return false;
		
		for (int i = 0; i < workTimes.size(); ++i)
			if (workTimes.get(i).equals(t.getWorkTimes().get(i)) == false)
				return false;
		
		return name.equals(t.getName())
			&& startDue.equals(t.getStartDue())
			&& subject.equals(t.getSubject())
			&& hoursNeeded == t.getHoursNeeded()
			&& minutesNeeded == t.getMinutesNeeded()
			&& priority.equals(t.getPriority())
			&& type.equals(t.getType())
			&& notes.equals(t.getNotes());
	}
	
	public synchronized void addChangeListener(ChangeListener l) {
		if (changeListenerList == null)
			changeListenerList = new ArrayList<ChangeListener>();
		
		changeListenerList.add(l);
	}
	
	public synchronized void removeChangeListener(ChangeListener l) {
		if (changeListenerList != null && changeListenerList.contains(l))
			changeListenerList.remove(l);
	}
	
	@SuppressWarnings("unchecked")
	private void processEvent(ChangeEvent e) {
		ArrayList<ChangeListener> list;
		
		synchronized(this) {
			if (changeListenerList == null) return;
			list = (ArrayList<ChangeListener>)changeListenerList.clone();
		}
		
		for (int i = 0; i < list.size(); i++) {
			ChangeListener listener = (ChangeListener)list.get(i);
			listener.stateChanged(e);
		}
	}
	
	protected static String getColorString(Color c) {
		if (c == null) return "#000000";
		
		String s = Integer.toHexString(c.getRGB() & 0xffffff);
		if (s.length() < 6) // pad on left with 0s
			s = "000000".substring(0, 6 - s.length()) + s;

		return '#' + s;
	}
}

