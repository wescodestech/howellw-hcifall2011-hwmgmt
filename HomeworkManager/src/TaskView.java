import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.AWTEvent;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TaskView extends JPanel implements Scrollable {

	private static final long serialVersionUID = 1L;
	
	private DatePair dateRange;
	private HomeworkThingListModel taskList;
	
	private ArrayList<Integer> visibleIndices;
	private LinkedList<ListSelectionListener> listeners;
	
	/**
	 * Constructor
	 * 
	 * @param taskList The list of all tasks
	 * @param dateRange A date pair specifying start and end dates. Only the days matter.
	 */
	public TaskView(HomeworkThingListModel taskList, DatePair dateRange) {
		super();
		
		this.taskList = taskList;
		final TaskView that = this;
		taskList.addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent e) {
				that.repaint();
			}
			
			public void intervalAdded(ListDataEvent e) {
				that.repaint();
			}
	        
			public void intervalRemoved(ListDataEvent e) {
				that.repaint();
			}
		});
		
		this.setPreferredSize(new Dimension(852, taskList.getSize() * 40 + 1 + 50));
		this.revalidate();
		
		setDateRange(dateRange);
		
		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		listeners = new LinkedList<ListSelectionListener>();
	}
	
	/**
	 * Get the date range
	 */
	public DatePair getDateRange() {
		return dateRange;
	}
	
	public long getStartTime() {
		return dateRange.getStart().getTime();
	}
	
	public long getEndTime() {
		return dateRange.getEnd().getTime();
	}
	
	/**
	 * Set date range
	 * @param dateRange Same as the constructor
	 */
	public void setDateRange(DatePair dateRange) {
		if (dateRange != null) this.dateRange = dateRange;
		GregorianCalendar startDate = new GregorianCalendar();
		startDate.setTime(dateRange.getStart());

		GregorianCalendar newStartDate = new GregorianCalendar();
		newStartDate.set(startDate.get(GregorianCalendar.YEAR), 
				startDate.get(GregorianCalendar.MONTH), startDate.get(GregorianCalendar.DATE), 0, 0, 0);
		
		GregorianCalendar endDate = new GregorianCalendar();
		endDate.setTime(dateRange.getEnd());
		
		GregorianCalendar newEndDate = new GregorianCalendar();
		newEndDate.set(endDate.get(GregorianCalendar.YEAR), 
				endDate.get(GregorianCalendar.MONTH), endDate.get(GregorianCalendar.DATE));
		
		this.dateRange = new DatePair(newStartDate.getTime(), newEndDate.getTime());
		
		repaint();
	}
	
	/**
	 * Register the listener that will be called when a user selects an index.
	 * 
	 * @param listener
	 */
	public void addListSelectionListener(ListSelectionListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Get the index that the user has selected
	 * 
	 * @return The selected index, or -1 if no index is selected
	 */
	public int getSelectedIndex() {
		return taskList.getSelectedIndex();
	}
	
	public Object getElementAt(int index) {
		return taskList.getElementAt(index);
	}
	
	public Object getSelectedItem() {
		return taskList.getSelectedItem();
	}
	
	public HomeworkThingListModel getTaskModel() {
		return taskList;
	}

	//Stuff for the JScrollPane
	public Dimension getPreferredScrollableViewportSize() {
		Dimension returnDimension = new Dimension(852, 649);
		return returnDimension;
	}

	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 50;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 50;
	}
	
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		visibleIndices = new ArrayList<Integer>();
		int dateLabelHeight = 30;
		// Draw date labels
		int labelRowWidth = 150;
		long startTime = getStartTime();
		int numberOfDays = (int) ((getEndTime() - startTime) / (60 * 60 * 24 * 1000D));
		final int widthOfView = this.getWidth() - labelRowWidth;
		final double scalingFactor = 1000 * 60 * 60 * 24D / (widthOfView / numberOfDays);
		
		g2.setColor(Color.lightGray);
		g2.fillRect(0, dateLabelHeight, labelRowWidth + 1, this.getHeight() - dateLabelHeight);
		
		Color bgColor;
		bgColor = UIManager.getColor("background"); // Nimbus
		if (bgColor == null) 
			bgColor = Color.lightGray;
		g2.setColor(bgColor);
		g2.fillRect(0, 0, this.getWidth(), dateLabelHeight);

		for(int i = 0; i < numberOfDays; i++) {
			GregorianCalendar tempDate = new GregorianCalendar();
			tempDate.setTime(dateRange.getStart());
			tempDate.add(GregorianCalendar.DATE, i);
			g2.setColor(Color.lightGray);
			g2.fillRect(i * (widthOfView / numberOfDays) + labelRowWidth + 2, 0, widthOfView / numberOfDays - 2, dateLabelHeight - 1);
			g2.setColor(Color.black);
			g2.drawString((tempDate.get(GregorianCalendar.MONTH) + 1) + "/" +
					tempDate.get(GregorianCalendar.DAY_OF_MONTH), i * (widthOfView / numberOfDays) + 3 + labelRowWidth, 15);
		}

		for(int i = 0; i < taskList.getSize(); ++i) {
			Task currentTask = (Task) taskList.getElementAt(i);
			
			if((currentTask.getStartDate().compareTo(dateRange.getEnd()) < 0) &&
					(currentTask.getDueDate().compareTo(dateRange.getStart()) > 0)) {
				int j = visibleIndices.size();
				
				Color taskBG;
				if (i == getSelectedIndex()) {
					taskBG = UIManager.getColor("List[Selected].textBackground"); // Nimbus
					if (taskBG == null)
						taskBG = UIManager.getColor("List.selectionBackground"); // Various other things
					if (taskBG == null)
						taskBG = Color.cyan;
					g2.setColor(taskBG.brighter().brighter().brighter().brighter());
				} else {
					g2.setColor(Color.white);
				}
				
				g2.fillRect(0, visibleIndices.size() * 38 + dateLabelHeight, this.getWidth(), 38);
				g2.setColor(Color.lightGray);
				g2.drawRect(0, visibleIndices.size() * 38 + dateLabelHeight, this.getWidth(), 38);
				
				Color subjColor = currentTask.subject != null ? currentTask.subject.getColor() : Color.black;
				g2.setColor(subjColor);
				int xStart = labelRowWidth + 5 + (int) (((currentTask.getStartDate().getTime()) - startTime) / scalingFactor);
				int xWidth = (int) (((currentTask.getDueDate().getTime()) - currentTask.getStartDate().getTime()) / scalingFactor);
				
				if(currentTask.getWorkTimes().size() != 0) {
					Point2D start = new Point2D.Float(xStart, 0);
				    Point2D end = new Point2D.Float(xStart + xWidth, 0);
				    float[] dist = new float[currentTask.getWorkTimes().size() * 3];
				    Color[] colors = new Color[currentTask.getWorkTimes().size() * 3];
				    
				    float taskLength = currentTask.getDueDate().getTime() - currentTask.getStartDate().getTime();
				    for(int k = 0; k < currentTask.getWorkTimes().size(); ++k) {
				    	DatePair currWorkTime = currentTask.getWorkTimes().get(k);
				    	if(currWorkTime.getStart().getTime() > currentTask.getStartDate().getTime()) {
				    		dist[k * 3] = (currWorkTime.getStart().getTime() - currentTask.getStartDate().getTime()) / taskLength;
				    		colors[k * 3] = Color.white;
				    	} else {
				    		dist[k * 3] = 0f;
				    		colors[k * 3] = subjColor;
				    	}
				    	
				    	if(currWorkTime.getEnd().getTime() < currentTask.getDueDate().getTime()) {
				    		dist[(k * 3) + 2] = (currWorkTime.getEnd().getTime() - currentTask.getStartDate().getTime()) / taskLength;
				    		colors[(k * 3) + 2] = Color.white;
				    	} else {
				    		dist[(k * 3) + 2] = 1f;
				    		colors[(k * 3) + 2] = subjColor;
				    	}
				    	
				    	dist[(k * 3) + 1] = (dist[k * 3] + dist[(k * 3) + 2]) / 2f;
				    	colors[(k * 3) + 1] = subjColor;
				    }
				    
				    LinearGradientPaint p =
				        new LinearGradientPaint(start, end, dist, colors);
				    g2.setPaint(p);
				    g2.fillRect(xStart, j * 38 + 1 + dateLabelHeight, xWidth, 36);
					
				    g2.setPaint(subjColor);
					g2.drawRect(xStart, j * 38 + 1 + dateLabelHeight, xWidth, 36);
				} else {
					g2.setColor(subjColor.brighter().brighter().brighter().brighter());
					g2.fillRect(xStart, j * 38 + 1 + dateLabelHeight, xWidth, 36);
					g2.setColor(subjColor);
					g2.drawRect(xStart, j * 38 + 1 + dateLabelHeight, xWidth, 36);
				}
				
				Color color;
				if(i == getSelectedIndex()) {
					color = UIManager.getColor("List[Selected].textBackground"); // Nimbus
					if (color == null) 
						color = UIManager.getColor("List.selectionBackground"); // Various other things
					if (color == null) 
						color = Color.cyan;
					g2.setColor(color);
				} else {
					g2.setColor(Color.lightGray);
				}
				
				g2.fillRect(0, visibleIndices.size() * 38 + dateLabelHeight, labelRowWidth, 38);
				g2.setColor(Color.gray);
				g2.drawRect(0, visibleIndices.size() * 38 + dateLabelHeight, labelRowWidth, 38);
				
				color = null;
				if (i == getSelectedIndex()) {
					color = UIManager.getColor("List[Selected].textForeground"); // Nimbus
					if (color == null)
						color = UIManager.getColor("List.selectionForeground"); // Various other things
					if (color == null)
						color = Color.cyan;
					g2.setColor(color);
				} else {
					g2.setColor(Color.black);
				}
				
				g2.drawString(currentTask.getName(), 3, visibleIndices.size() * 38 + 17 + dateLabelHeight);
				
				visibleIndices.add(i);
			}
		}
	}
	
	protected void processMouseEvent(MouseEvent e) {
		if(e.getID() == MouseEvent.MOUSE_RELEASED) {
			int selected = (int) Math.floor((e.getY() - 30) / 40.0);
			
			if((selected < 0) || (selected >= visibleIndices.size())) {
				taskList.setSelectedIndex(-1);
			} else {
				taskList.setSelectedIndex(visibleIndices.get(selected));
			}
			
			repaint();
			
			//Fire listener
			for(java.util.ListIterator<ListSelectionListener> itr = listeners.listIterator();
			itr.hasNext(); ) {
				itr.next().valueChanged(new ListSelectionEvent(
						this, taskList.getSelectedIndex(), taskList.getSelectedIndex(), false));
			}
		}
	}
}