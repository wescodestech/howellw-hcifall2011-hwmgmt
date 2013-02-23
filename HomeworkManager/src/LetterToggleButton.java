import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.UIManager;


public class LetterToggleButton extends JToggleButton {
	
	private static final long serialVersionUID = 1L;
	
	private char letter;
	private int textWidth;
	private int textHeight;
	
	public LetterToggleButton(char letter) {
		this(letter, 0);
	}
	
	public LetterToggleButton(char letter, int minWidth) {
		this.letter = letter;
		
		setRolloverEnabled(false);
		setMargin(new Insets(0,0,0,0));
		
		Font font = (Font)UIManager.get("ToggleButton.font");
		Frame frame = (Frame)JOptionPane.getRootFrame();
		FontMetrics fm = frame.getFontMetrics(font);
		textHeight = fm.getHeight() - fm.getMaxDescent();
		textWidth = fm.charWidth(letter);
		
		setFont(font.deriveFont(Font.BOLD));
		
		int height = textHeight + 15;
		int width = 0;
		if (minWidth == 0)
			width = height;
		else
			width = Math.max(textWidth + 15, minWidth);

		setPreferredSize(new Dimension(width, height));
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int x = (getWidth() - textWidth) / 2;
		int y = getHeight() - 2 - (getHeight() - textHeight) / 2;
		
		g.drawString(letter + "", x, y);
	}
}
