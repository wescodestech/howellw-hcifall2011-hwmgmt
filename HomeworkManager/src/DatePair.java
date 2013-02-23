
import java.util.Date;

/** 
 * This class represents a pair of Dates. It's meant for use with start and
 * end dates, so it requires that the start date entered be less than the
 * end date.
 * 
 * TODO At the moment this class handles whether or not to throw exceptions
 * in a way that might be sort of strange. If null Dates are given to the
 * constructor, the most logical thing for it to do is throw an exception.
 * However, the most logical thing to do for setters isn't so obvious.
 * Right now, they throw IllegalArgumentExceptions for illogical start or
 * end dates, but ignore null (including the method that sets the start and
 * end dates simultaneously, which seems like it sort of contradicts the
 * constructor behavior). Or maybe this isn't a problem at all.
 */
public class DatePair implements Comparable<DatePair> {
	private Date start;
	private Date end;
	
	/** Create a new DatePair with start and end initialized to the 
	 * current date.
	 */
	public DatePair() {
		start = new Date();
		end = new Date(start.getTime());
	}
	
	/** Create a new DatePair with the specified start and end dates.
	 * Does not except null dates.
	 * @param 	start 	Start date. Must be less than or equal to end date.
	 * @param 	end		End date. Must be greater than or equal to start date.
	 * @throws 	IllegalArgumentException if start date is greater than 
	 * end date, or if either date is null
	 */
	public DatePair(Date start, Date end) {
		if (start == null || end == null)
			throw new IllegalArgumentException("Start and end dates must " +
					"not be null.");
		if (start.compareTo(end) > 0)
			throw new IllegalArgumentException("Start date cannot be " +
					"greater than end date.");
		
		this.start = start;
		this.end = end;
	}
	
	/** Create a new DatePair with the specified start and end dates.
	 * @param 	start 	Start date. Must be less than or equal to end date.
	 * @param 	end		End date. Must be greater than or equal to start date.
	 * @throws 	IllegalArgumentException if start date is greater than 
	 * end date
	 */
	public DatePair(long start, long end) {
		if (start > end)
			throw new IllegalArgumentException("Start date cannot be " +
					"greater than end date.");
		
		this.start = new Date(start);
		this.end = new Date(end);
	}
	
	public Date getStart() {
		return this.start;
	}
	
	public Date getEnd() {
		return this.end;
	}
	
	/** Set the start date. Does nothing if given Date is null.
	 * @param 	start 	Must be less than or equal to end current date.
	 * @throws 	IllegalArgumentException if requested start date is greater 
	 * than current end date
	 */
	public void setStart(Date start) {
		if (start == null) return;
		if (start.compareTo(this.end) > 0)
			throw new IllegalArgumentException("Start date cannot be "
					+ "greater than end date.");
		
		this.start = start;
	}
	
	/** Set the start date.
	 * @param 	start 	Must be less than or equal to end current date.
	 * @throws 	IllegalArgumentException if requested start date is greater
	 * than current end date
	 */
	public void setStart(long start) {
		setStart(new Date(start));
	}
	
	/** Set the end date. Does nothing if the given Date is null.
	 * @param 	end 	Must be greater than or equal to start current date.
	 * @throws 	IllegalArgumentException if requested end date is less than
	 * current start date
	 */
	public void setEnd(Date end) {
		if (end == null) return;
		if (end.compareTo(this.start) < 0)
			throw new IllegalArgumentException("End date cannot be less "
					+ "than start date.");
		
		this.end = end;
	}
	
	/**
	 * Set the end date.
	 * @param 	end		Must be greater than or equal to current start date.
	 * @throws	IllegalArgumentException if requested end date is less than
	 * current start date
	 */
	public void setEnd(long end) {
		setEnd(new Date(end));
	}
	
	/**
	 * Set the start and end dates. Does nothing if either date is null.
	 * @param start	Start date. Must be greater than or equal to end date.
	 * @param end	End date. Must be less than or equal to start date.
	 */
	public void setStartEnd(Date start, Date end) {
		if (start == null || end == null)
			return;
		if (start.compareTo(end) > 0)
			throw new IllegalArgumentException("Start date cannot be " +
					"greater than end date.");
		
		this.start = start;
		this.end = end;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if ((o instanceof DatePair) == false) return false;
		if (this == o) return true;
		
		DatePair dp = (DatePair)o;
		
		return this.start.equals(dp.getStart()) 
			&& this.end.equals(dp.getEnd());
	}
	
	@Override
	public int compareTo(DatePair dp) {
		return start.compareTo(dp.getStart());
	}
}
