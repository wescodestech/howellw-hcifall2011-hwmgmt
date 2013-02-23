import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Subject implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int SUNDAY = 0;
	public static final int MONDAY = 1;
	public static final int TUESDAY = 2;
	public static final int WEDNESDAY = 3;
	public static final int THURSDAY = 4;
	public static final int FRIDAY = 5;
	public static final int SATURDAY = 6;
	
	private ArrayList<ChangeListener> changeListenerList;
	
	private String name;
	private String abbrev;
	
	private String location;
	
	private boolean[] days;
	private DatePair time;
	
	private String instructor;
	private String email;

	private String notes;
	
	private Color color;
	
	private static SimpleDateFormat timeFormat = 
			new SimpleDateFormat("h:mm a");
	
	// lighter blue, dark green, red, dark blue, orange, purple
	private static Color[] goodColors = { new Color(0, 137, 222), 
		new Color(0, 161, 0), Color.RED, new Color(0, 0, 175), 
		new Color(255, 128, 0), new Color(133, 50, 166),
	};
	
	private static int lastColorIndex = -1;
	
	public Subject() {
		this("", "");
	}
	
	public Subject(String name) {
		this(name, guessAbbrev(name));
	}
	
	public Subject(String name, String abbrev) {
		this(name, abbrev, null);
	}
	
	public Subject(String name, String abbrev, Color color) {
		changeListenerList = new ArrayList<ChangeListener>();
		
		if (name == null)
			this.name = "";
		else 
			this.name = name;
		
		if (abbrev == null)
			this.abbrev = guessAbbrev(this.name);
		else
			this.abbrev = abbrev;
		
		if (color == null)
			this.color = getNextGoodColor();
		else
			this.color = color;
		
		location = "";
		
		days = new boolean[7];
		for (int i = 0; i < 7; ++i) 
			days[i] = false;
		
		time = null;
		
		instructor = "";
		email = "";
		
		notes = "";
	}
	
	public Subject(String name, String abbrev, Color color, boolean[] days, 
			DatePair time, String location, String instructor, String email, 
			String notes) {
		// initialize most things to default values in case of errors
		this(name, abbrev, color); 
		
		// Try to initialize everything to the requested values. If one of the
		// requested values is null or nonsensical, use the default value.
		setLocation(location);
		setDays(days);
		setTime(time);
		setInstructor(instructor);
		setEmail(email);
		setNotes(notes);
	}
	
	public String getName() {
		return name;
	}

	/**
	 * Sets the name AND guesses the abbreviation
	 * @param name
	 */
	public void setName(String name) {
		if (name == null) name = "";

		this.name = name;
		this.abbrev = guessAbbrev(name);
		processEvent(new ChangeEvent(this));
	}
	
	public void setNameOnly(String name) {
		if (name == null) name = "";
		
		this.name = name;
		processEvent(new ChangeEvent(this));
	}

	public String getAbbrev() {
		return abbrev;
	}

	public void setAbbrev(String abbrev) {
		if (abbrev == null) abbrev = "";

		this.abbrev = abbrev;
		processEvent(new ChangeEvent(this));
	}
	
	public void setNameAbbrev(String name, String abbrev) {
		if (name == null) name = "";
		if (abbrev == null) abbrev = "";
		
		this.name = name;
		this.abbrev = abbrev;
		processEvent(new ChangeEvent(this));
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		if (location == null) location = "";
		this.location = location;
		processEvent(new ChangeEvent(this));
	}
	
	public boolean isDaySet() {
		for (boolean day : days) {
			if (day) return true;
		}
		return false;
	}
	
	public boolean[] getDays() {
		return days;
	}
	
	public void setDays(boolean[] days) {
		if (days != null && days.length == 7) {
			this.days = days;
			processEvent(new ChangeEvent(this));
		}
	}
	
	public boolean getDay(int day) {
		return days[day];
	}
	
	public void setDay(int day, boolean value) {
		days[day] = value;
		processEvent(new ChangeEvent(this));
	}
	
	public String getDaysString() {
		StringBuilder sb = new StringBuilder(16);
		if (days[0])	sb.append("S ");
		if (days[1])	sb.append("M ");
		if (days[2]) 	sb.append("T ");
		if (days[3])	sb.append("W ");
		if (days[4])	sb.append("R ");
		if (days[5])	sb.append("F ");
		if (days[6])	sb.append("S ");
		
		return sb.toString();
	}
	
	public DatePair getTime() {
		return time;
	}
	
	public void setTime(DatePair time) {
		this.time = time;
	}
	
	public String getTimeString() {
		return timeFormat.format(time.getStart()) + " to " +
				timeFormat.format(time.getEnd());
	}
	
	public String getInstructor() {
		return instructor;
	}

	public void setInstructor(String instructor) {
		if (instructor != null)
			this.instructor = instructor;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (email != null)
			this.email = email;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		if (notes != null)
			this.notes = notes;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		if (color != null)
			this.color = color;
	}

	public String toString() {
		if (name.isEmpty()) return " ";
		return name;
	}
	
	//TODO change things that need toMediumString()
	public String toMediumString() {
		if (abbrev.isEmpty())
			return name;
		return '[' + abbrev + "] " + name;
	}
	
	public String toColoredString() {
		if (abbrev.isEmpty())
			return name;

		return "<html><b><font color=\"" + getColorString(color) + "\">[" 
				+ abbrev + "]</font></b> " + name;
	}
	
	public String toLongString() {
		StringBuilder sb = new StringBuilder(200);
		
		// abbreviation and name
		sb.append("--- ");
		if (abbrev.isEmpty())
			sb.append(name);
		else
			sb.append('[' + abbrev + "] " + name);
		sb.append(" ---\n");
		
		if (isDaySet())
			sb.append(getDaysString());
		
		if (time != null) 
			sb.append(getTimeString() + '\n');
		
		sb.append("Location: " + location + '\n');
		sb.append("Instructor: " + instructor + '\n');
		sb.append("Instructor e-mail: " + email + '\n');
		sb.append("Notes: " + notes + '\n');
		sb.append("Display color: R=" + color.getRed() + ", G=" + 
				color.getGreen() + ", B=" + color.getBlue());
		
		
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if ((o instanceof Subject) == false) return false;
		if (this == o) return true;
		
		Subject s = (Subject)o;

		boolean[] sDays = s.getDays();
		for (int i = 0; i < 7; ++i)
			if (days[i] != sDays[i])
				return false;
		
		return name.equals(s.getName())
				&& abbrev.equals(s.getAbbrev())
				&& color.equals(s.getColor())
				&& time.equals(s.getTime())
				&& location.equals(s.getLocation())
				&& instructor.equals(s.getInstructor())
				&& email.equals(s.getEmail())
				&& notes.equals(s.getNotes());
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
	
	/**
	 * Guesses an abbreviation for the given subject string. If the subject
	 * is empty or consists only of white space, the empty string is returned.
	 * If the subject has two or more words, the abbreviation will be the
	 * first letter/number of each word, up to four words. If the subject has
	 * only one word, the first four letters of the word will be used.
	 * If this was going to be a real program, it might be a good idea to add
	 * a filter to make sure this doesn't generate anything rude, but that
	 * doesn't really matter for a class project.
	 */
	public static String guessAbbrev(String subject) {
		if (subject.isEmpty()) return "";
		
		// Split the string into tokens around any type of white space.
		// The + means one or more whitespace characters.
		String[] words = subject.split("\\s+");
		String abbrev = "";
		
		if (words.length > 1) {
			// first character of each word, up to 4 words
			for (int i = 0; i < words.length && i < 4; ++i)
				abbrev += words[i].charAt(0);
		} else {
			// up to first 4 letters of one-word names
			if (words[0].length() < 4)
				abbrev = words[0];
			else
				abbrev = words[0].substring(0, 4);
		}
		
		return abbrev;
	}
	
	protected static Color getNextGoodColor() {
		lastColorIndex = (lastColorIndex + 1) % goodColors.length;
		return goodColors[lastColorIndex];
	}
	
	protected static String getColorString(Color c) {
		if (c == null) return "#000000";
		
		String s = Integer.toHexString(c.getRGB() & 0xffffff);
		if (s.length() < 6) // pad on left with 0s
			s = "000000".substring(0, 6 - s.length()) + s;

		return '#' + s;
	}
}
