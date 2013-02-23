import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.*;

public class DateRangePicker extends JPanel implements Scrollable {
	
	private static final long serialVersionUID = 1L;

	private LinkedList<Date> workTimes;
	
	private Date startDate;
	private Date endDate;
	
	private GregorianCalendar firstHour;
	private GregorianCalendar lastHour;
	private int numHours;
	
	//What type of drag action should be performed 1 = insert, -1 = delete
	private int dragState;
	
	public DateRangePicker(Date startDate, Date endDate) {
		super();
		
		workTimes = new LinkedList<Date>();
		
		this.startDate = startDate;
		this.endDate = endDate;
		
		updateDates();
		
		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		this.enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}
	
	public void setStart(Date startDate) {
		this.startDate = startDate;
		updateDates();
	}
	
	public void setEnd(Date endDate) {
		this.endDate = endDate;
		updateDates();
	}
	
	public void setDateRanges(ArrayList<DatePair> dateRanges) {
		for(int i = 0; i < dateRanges.size(); ++i) {
			GregorianCalendar currentDate = new GregorianCalendar();
			currentDate.setTime(dateRanges.get(i).getStart());
			
			while(currentDate.getTime().before(dateRanges.get(i).getEnd())) {
				workTimes.add(currentDate.getTime());
				currentDate.add(GregorianCalendar.HOUR, 1);
			}
		}
		
		repaint();
	}
	
	public ArrayList<DatePair> getDateRanges() {
		ArrayList<DatePair> tempArray = new ArrayList<DatePair>();
		
		ListIterator<Date> iterator = workTimes.listIterator(0);
		Date next = null;
		Date start = null;
		Date current = null;
		
		while(iterator.hasNext()) {
			next = iterator.next();
			//If we're starting a new pair
			if(start == null) {
				start = (Date) next.clone();
				current = (Date) next.clone();
			} else {
				GregorianCalendar currCal = new GregorianCalendar();
				currCal.setTime(current);
				GregorianCalendar nextCal = new GregorianCalendar();
				nextCal.setTime(next);
				currCal.add(GregorianCalendar.HOUR_OF_DAY, 1);
				
				if(!currCal.equals(nextCal)) {
					//Create new date pair
					tempArray.add(new DatePair(start, currCal.getTime()));
					start = (Date) next.clone();
					current = (Date) next.clone();
				} else {
					current = (Date) next.clone();
				}
			}
		}
		
		if(start != null) {
			GregorianCalendar currCal = new GregorianCalendar();
			currCal.setTime(current);
			currCal.add(GregorianCalendar.HOUR_OF_DAY, 1);
			
			tempArray.add(new DatePair(start, currCal.getTime()));
		}
		
		
		return tempArray;
	}
	
	protected void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		
		GregorianCalendar tempDate = (GregorianCalendar)firstHour.clone();
		
		ListIterator<Date> iterator = workTimes.listIterator(0);
		Date next;
		
		if(iterator.hasNext()) {
			next = iterator.next();
		} else {
			next = null;
		}
		
		for(int i = 0; i < numHours; ++i) {
			
			if(tempDate.getTime().equals(next)) {
				// TODO: Nice colors
				Color color;
				color = UIManager.getColor("List[Selected].textBackground"); // Nimbus
				if (color == null) 
					color = UIManager.getColor("List.selectionBackground"); // Various other things
				if (color == null) 
					color = Color.cyan;
				g.setColor(color);

				g.fillRect(i * 50, 0, 50, 74);
				
				g.setColor(Color.black);
				g.drawRect(i * 50, 0, 50, 74);
				
				if(iterator.hasNext()) {
					next = iterator.next();
				} else {
					next = null;
				}
			} else {
				g.setColor(Color.darkGray);
				g.drawRect(i * 50, 0, 50, 74);
			}
			
			//Draw cell header
			g.setColor(Color.lightGray);
			g.fillRect(i * 50, 0, 50, 30);
			
			g.setColor(Color.darkGray);
			g.drawRect(i * 50, 0, 50, 30);
			
			if(tempDate.equals(firstHour) || tempDate.get(GregorianCalendar.HOUR_OF_DAY) == 0) {
				g.drawString((tempDate.get(GregorianCalendar.MONTH) + 1) + "/" +
						tempDate.get(GregorianCalendar.DAY_OF_MONTH), i * 50 + 3, 14);
			}
			
			g.drawString(((tempDate.get(GregorianCalendar.HOUR) == 0) ? "12" : tempDate.get(GregorianCalendar.HOUR)) + 
					((tempDate.get(GregorianCalendar.AM_PM) == GregorianCalendar.AM) ? "AM" : "PM"), i * 50 + 3, 27);
			
			tempDate.add(GregorianCalendar.HOUR_OF_DAY, 1);
		}
	}
	
	protected void processMouseEvent(MouseEvent e) {
		if(e.getID() == MouseEvent.MOUSE_PRESSED) {
			int hour = e.getX() / 50;
			GregorianCalendar tempHour = (GregorianCalendar)firstHour.clone();
			tempHour.add(GregorianCalendar.HOUR, hour);
			
			if(workTimes.contains(tempHour.getTime())) {
				workTimes.remove(tempHour.getTime());
				dragState = -1;
			} else {
				workTimes.add(tempHour.getTime());
				Collections.sort(workTimes);
				dragState = 1;
			}
			
			repaint();
		}
	}
	
	protected void processMouseMotionEvent(MouseEvent e) {
		if(e.getID() == MouseEvent.MOUSE_DRAGGED) {
			int hour = e.getX() / 50;
			GregorianCalendar tempHour = (GregorianCalendar)firstHour.clone();
			tempHour.add(GregorianCalendar.HOUR, hour);
			
			if(workTimes.contains(tempHour.getTime())) {
				if(dragState == -1) {
					workTimes.remove(tempHour.getTime());
				}
			} else {
				if(dragState == 1) {
					workTimes.add(tempHour.getTime());
					Collections.sort(workTimes);
				}
			}
			
			repaint();
		}
	}
	
	private void updateDates() {
		//First, get the hour which contains the start and end time
		firstHour = new GregorianCalendar();
		firstHour.setTime(startDate);
		firstHour.set(firstHour.get(GregorianCalendar.YEAR), firstHour.get(GregorianCalendar.MONTH),
				firstHour.get(GregorianCalendar.DAY_OF_MONTH), firstHour.get(GregorianCalendar.HOUR_OF_DAY),
				0, 0);
		
		lastHour = new GregorianCalendar();
		lastHour.setTime(endDate);
		lastHour.set(lastHour.get(GregorianCalendar.YEAR), lastHour.get(GregorianCalendar.MONTH),
				lastHour.get(GregorianCalendar.DAY_OF_MONTH), lastHour.get(GregorianCalendar.HOUR_OF_DAY),
				0, 0);
		
		//Then, get the number of milliseconds that have elapsed between the two hours
		long timePassed = lastHour.getTimeInMillis() - firstHour.getTimeInMillis();
		numHours = (int)Math.round(timePassed / (60 * 60 * 1000.0)) + 1;
		
		//Now, update the component
		this.setPreferredSize(new Dimension(numHours * 50 + 1, 75));
		this.revalidate();
		repaint();
	}

	public Dimension getPreferredScrollableViewportSize() {
		Dimension returnDimension = new Dimension(300, 75);
		return returnDimension;
	}

	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 50;
	}

	public boolean getScrollableTracksViewportHeight() {
		return true;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 50;
	}
}
