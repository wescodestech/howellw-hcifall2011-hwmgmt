import java.awt.Color;

public class TaskDetailPanel extends DetailPanel {
	private static final long serialVersionUID = 1L;
	
	private Task task;
	
	TaskDetailPanel() {
		super();
		task = null;
	}
	
	TaskDetailPanel(Task task) {
		super();
		setTask(task);
	}
	
	public Task getTask() {
		return task;
	}
	
	public void setTask(Task task) {
		this.task = task;
		
		if (task == null) {
			setTitle("");
			setColor(Color.WHITE);
			clearTableRows();
			return;
		}
		
		setTitle(task.toString());
		
		if (task.getSubject() == null)
			setColor(Color.BLACK);
		else
			setColor(task.getSubject().getColor());
		
		clearTableRows();
		
		if (task.getSubject() != null 
				&& task.getSubject().getName().isEmpty() == false) 
			addRow("Subject", task.getSubject().getName());
		
		addRow("Start", dayTimeFormat.format(task.getStartDate()));
		addRow("Due", dayTimeFormat.format(task.getDueDate()));
		
		if (task.getMinutesNeeded() != 0 || task.getHoursNeeded() != 0) {
			String temp = "";
			if (task.getHoursNeeded() != 0)
				temp += task.getHoursNeeded() + " hours ";
			if (task.getMinutesNeeded() != 0)
				temp += task.getMinutesNeeded() + " minutes";
			addRow("Needed", temp);
		}
		
		if (task.getWorkTimes() != null && task.getWorkTimes().size() != 0) 
			addRow("Work", task.getWorkTimeString());
		
		if (task.getPriority() != Task.Priority.NONE)
			addRow("Priority", task.getPriority().toString());

		if (task.getType().isEmpty() == false)
			addRow("Type", task.getType());

		if (task.getNotes().isEmpty() == false)
			addRow("Notes", task.getNotes());
	}
}
