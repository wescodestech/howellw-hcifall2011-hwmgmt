
import java.io.*;
import javax.swing.*;

public class HomeworkManager implements Serializable {
	
	private static final long serialVersionUID = 1L;

	// Contains Tasks
	private HomeworkThingListModel tasks;
	
	// Contains Strings
	private DefaultComboBoxModel taskTypes; // is also a ListModel for JList
	private String[] initialTaskTypes;
	
	// Contains Subjects
	private HomeworkThingListModel subjects; 
	
	
	public HomeworkManager() {
		tasks = new HomeworkThingListModel();
		
		//TODO add more task types?
		initialTaskTypes = new String[3];
		initialTaskTypes[0] = "Homework";
		initialTaskTypes[1] = "Reading";
		initialTaskTypes[2] = "Project";
		
		taskTypes = new DefaultComboBoxModel(initialTaskTypes);
		
		subjects = new HomeworkThingListModel();
	}
	
	public void addTask(Task task) {
		if (task != null)
			tasks.addElement(task);
	}
	
	public Task getTask(Task task) {
		int index = tasks.getIndexOf(task);
		
		if (index == -1)
			return null;
		else
			return (Task)tasks.getElementAt(index);
	}
	
	public HomeworkThingListModel getTaskModel() {
		return tasks;
	}
	
	public void setTaskModel(HomeworkThingListModel tasks) {
		if (tasks != null)
			this.tasks = tasks;
	}
	
	public DefaultComboBoxModel getTypeModel() {
		return taskTypes;
	}
	
	public void setTypeModel(DefaultComboBoxModel taskTypes) {
		if (taskTypes != null)
			this.taskTypes = taskTypes;
	}
	
	public void resetTypeModel() {
		taskTypes = new DefaultComboBoxModel(initialTaskTypes);
	}
	
	public HomeworkThingListModel getSubjectModel() {
		return subjects;
	}
	
	public void setSubjectModel(HomeworkThingListModel subjects) {
		if (subjects != null)
			this.subjects = subjects;
	}
}
