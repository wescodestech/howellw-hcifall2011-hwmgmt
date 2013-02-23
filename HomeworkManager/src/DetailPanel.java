import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

import java.awt.*;
import java.text.SimpleDateFormat;

public class DetailPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JPanel titlePanel;
	private JLabel titleLabel;
	
	private static Font titleFont = 
			((Font)UIManager.getFont("Label.font")).deriveFont(14.0f);
	
	private JTable table;
	private DefaultTableModel tableModel;
	
	protected static SimpleDateFormat timeFormat = 
			new SimpleDateFormat("h:mm a");
	protected static SimpleDateFormat dayTimeFormat =
			new SimpleDateFormat("h:mm a, EEE. MMM. d");
	
	@SuppressWarnings("serial")
	public DetailPanel() {
		setLayout(new BorderLayout(0,0));
		
		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		titlePanel.setBorder(new EmptyBorder(3,8,3,3));
		
		titleLabel = new JLabel(" "); // to make label maintain height
		titleLabel.setFont(titleFont);
		titleLabel.setForeground(Color.WHITE);
		titlePanel.add(titleLabel);
		add(titlePanel, BorderLayout.NORTH);
		
		// 10 (or more) will be the initial row vector capacity
		tableModel = new DefaultTableModel(10,2) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tableModel.setColumnIdentifiers(new String[]{"labels","values"});
		tableModel.setRowCount(0); // initial row vector size is 0
		
		table = new JTable(tableModel);
		table.setTableHeader(null);
		table.setFillsViewportHeight(true);
		
		// No selecting stuff. EVER. (Or at least for now, unless we decide to
		// make this editable later.)
		table.setSelectionModel(new NoSelectingStuff());
		
		DefaultTableCellRenderer labelRenderer = new DefaultTableCellRenderer();
		labelRenderer.setBackground(Color.WHITE);
		labelRenderer.setForeground(Color.GRAY);
		labelRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		labelRenderer.setVerticalAlignment(SwingConstants.TOP);
		
		TextAreaRenderer valueRenderer = new TextAreaRenderer();
		TextAreaEditor valueEditor = new TextAreaEditor();
		
		table.getColumn("labels").setCellRenderer(labelRenderer);
		table.getColumn("values").setCellRenderer(valueRenderer);
		table.getColumn("values").setCellEditor(valueEditor);
		
		table.getColumn("labels").setMaxWidth(70);
		
		JScrollPane tableScroll = new JScrollPane(table);
		tableScroll.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		IAppWidgetFactory.makeIAppScrollPane(tableScroll);
		
		add(tableScroll, BorderLayout.CENTER);
	}

	protected void setTitle(String newTitle) {
		if (newTitle.isEmpty())
			titleLabel.setText(" "); // to make label maintain height
		else
			// Make the title bold
			titleLabel.setText("<html><b>" + newTitle + "</b></html>");
	}
	
	protected void setColor(Color color) {
		titlePanel.setBackground(color);
	}
	
	protected void addRow(String label, String value) {
		if (label == null || value == null)
			return;
		
		// Make the label cell text bold
		label = "<html><b>" + label + "</b></html>";
		tableModel.addRow(new String[]{label, value});
	}
	
	protected void clearTableRows() {
		tableModel.setRowCount(0);
	}
}


/** Exactly what it sounds like. */
class NoSelectingStuff implements ListSelectionModel {
	public NoSelectingStuff() { }
	public void setSelectionInterval(int index0, int index1) { }
	public void addSelectionInterval(int index0, int index1) { }
	public void removeSelectionInterval(int index0, int index1) { }
	public int getMinSelectionIndex() { return -1; }
	public int getMaxSelectionIndex() { return -1; }
	public boolean isSelectedIndex(int index) { return false; }
	public int getAnchorSelectionIndex() { return -1; }
	public void setAnchorSelectionIndex(int index) { }
	public int getLeadSelectionIndex() { return -1; }
	public void setLeadSelectionIndex(int index) { }
	public void clearSelection() { }
	public boolean isSelectionEmpty() { return true; }
	public void insertIndexInterval(int index, int length,
			boolean before) { }
	public void removeIndexInterval(int index0, int index1) { }
	public void setValueIsAdjusting(boolean valueIsAdjusting) { }
	public boolean getValueIsAdjusting() { return false; }
	public void setSelectionMode(int selectionMode) { }
	public int getSelectionMode() { return 0; }
	public void addListSelectionListener(ListSelectionListener x) { }
	public void removeListSelectionListener(ListSelectionListener x) { }
}
