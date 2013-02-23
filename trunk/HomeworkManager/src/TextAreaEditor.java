import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/** From http://www.javaspecialists.eu/archive/Issue106.html */
public class TextAreaEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("serial")
	public TextAreaEditor() {
		super(new JTextField());
		final JTextArea textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(null);
		editorComponent = scrollPane;

		delegate = new DefaultCellEditor.EditorDelegate() {
			public void setValue(Object value) {
				textArea.setText((value != null) ? value.toString() : "");
			}
			public Object getCellEditorValue() {
				return textArea.getText();
			}
		};
	}
}