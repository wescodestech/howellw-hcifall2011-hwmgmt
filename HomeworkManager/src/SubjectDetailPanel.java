import java.awt.Color;

public class SubjectDetailPanel extends DetailPanel {

	private static final long serialVersionUID = 1L;
	
	private Subject subject;
	
	SubjectDetailPanel() {
		super();
	}
	
	SubjectDetailPanel(Subject subject) {
		super();
		setSubject(subject == null ? new Subject() : subject);
	}
	
	public Subject getSubject() {
		return subject;
	}
	
	public void setSubject(Subject subject) {
		this.subject = subject;
		
		if (subject == null) {
			setTitle("");
			setColor(Color.WHITE);
			clearTableRows();
			return;
		}
		
		setTitle(subject.toMediumString());
		setColor(subject.getColor());
		
		clearTableRows();
		
		if (subject.isDaySet() || subject.getTime() != null) {
			String temp = "";
			if (subject.isDaySet())
				temp += subject.getDaysString() + ' ';
			if (subject.getTime() != null)
				temp += subject.getTimeString();
			addRow("Time", temp);
		}
		
		if (subject.getLocation().isEmpty() == false) 
			addRow("Location", subject.getLocation());
		
		if (subject.getInstructor().isEmpty() == false)
			addRow("Instructor", subject.getInstructor());
		
		if (subject.getEmail().isEmpty() == false)
			addRow("E-mail", subject.getEmail());
		
		if (subject.getNotes().isEmpty() == false)
			addRow("Notes", subject.getNotes());
	}
}
