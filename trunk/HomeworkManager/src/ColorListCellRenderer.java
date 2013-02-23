import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


public class ColorListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getListCellRendererComponent(JList list, Object value,
	        int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, 
				cellHasFocus);
		
		if (value instanceof Subject) 
			setText(((Subject)value).toColoredString());
		else if (value instanceof Task) 
			setText(((Task)value).toColoredString());
		
		return this;
	}
	
	//TODO anything else to override?
}
