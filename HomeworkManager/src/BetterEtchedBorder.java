import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.EtchedBorder;

/**
 * Same as EtchedBorder, but with the option to draw only certain sides
 *
 * @author Elizabeth Craig
 */
public class BetterEtchedBorder extends EtchedBorder {

	private static final long serialVersionUID = 1L;

	/** Paint the top border? */
	protected boolean paintTop;
	/** Paint the left border? */
	protected boolean paintLeft;
	/** Paint the bottom border? */
	protected boolean paintBottom;
	/** Paint the right border? */
	protected boolean paintRight;

	/**
	 * Creates a lowered etched border on all sides.
	 */
	public BetterEtchedBorder() {
		this(true, true, true, true, LOWERED);
	}

	/**
	 * Creates a lowered etched border on only the sides specified.
	 * @param top		Border on the top?
	 * @param left		Border on the left?
	 * @param bottom	Border on the bottom?
	 * @param right		Border on the right?
	 */
	public BetterEtchedBorder(boolean top, boolean left, boolean bottom, 
			boolean right) {
		this(top, left, bottom, right, LOWERED);
	}

	/**
	 * Creates an etched border, on only the sides specified, with the 
	 * specified etch-type, and colors derived from the background color 
	 * of the component passed into the paintBorder method.
	 * @param top		Border on the top?
	 * @param left		Border on the left?
	 * @param bottom	Border on the bottom?
	 * @param right		Border on the right?
	 * @param etchType the type of etch to be drawn by the border
	 */
	public BetterEtchedBorder(boolean top, boolean left, boolean bottom, 
			boolean right, int etchType) {
		this(top, left, bottom, right, etchType, null, null);
	}

	/**
	 * Creates a lowered etched border on only the sides specified with the 
	 * specified highlight and shadow colors.
	 * @param top		Border on the top?
	 * @param left		Border on the left?
	 * @param bottom	Border on the bottom?
	 * @param right		Border on the right?
	 * @param highlight the color to use for the etched highlight
	 * @param shadow the color to use for the etched shadow
	 */
	public BetterEtchedBorder(boolean top, boolean left, boolean bottom,
			boolean right, Color highlight, Color shadow) {
		this(top, left, bottom, right, LOWERED, highlight, shadow);
	}

	/**
	 * Creates an etched border on only the sides specified, with the 
	 * specified etch-type, highlight and shadow colors.
	 * @param top		Border on the top?
	 * @param left		Border on the left?
	 * @param bottom	Border on the bottom?
	 * @param right		Border on the right?
	 * @param etchType the type of etch to be drawn by the border
	 * @param highlight the color to use for the etched highlight
	 * @param shadow the color to use for the etched shadow
	 */
	public BetterEtchedBorder(boolean top, boolean left, boolean bottom, 
			boolean right, int etchType, Color highlight, Color shadow) {
		super(etchType, highlight, shadow);
		this.paintTop = top;
		this.paintLeft = left;
		this.paintBottom = bottom;
		this.paintRight = right;
	}

	/**
	 * Get whether the top border will be painted
	 * @return Whether the top border will be painted
	 */
	public boolean getPaintTop() {
		return paintTop;
	}

	/**
	 * Get whether the left border will be painted
	 * @return Whether the left border will be painted
	 */
	public boolean getPaintLeft() {
		return paintLeft;
	}

	/**
	 * Get whether the bottom border will be painted
	 * @return Whether the bottom border will be painted
	 */
	public boolean getPaintBottom() {
		return paintBottom;
	}

	/**
	 * Get whether the right border will be painted
	 * @return Whether the right border will be painted
	 */
	public boolean getPaintRight() {
		return paintRight;
	}

	/**
	 * Paints the border for the specified component with the 
	 * specified position and size.
	 * @param c the component for which this border is being painted
	 * @param g the paint graphics
	 * @param x the x position of the painted border
	 * @param y the y position of the painted border
	 * @param width the width of the painted border
	 * @param height the height of the painted border
	 */
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, 
			int width,int height) {
		int w = width;
		int h = height;

		g.translate(x, y);

		g.setColor(etchType == LOWERED ? getShadowColor(c) 
				: getHighlightColor(c));
		if (paintLeft)		g.drawLine(0, 2, 0, h-3);
		if (paintTop)		g.drawLine(2, 0, w-3, 0);
		if (paintRight)		g.drawLine(w-2, h-3, w-2, 2);
		if (paintBottom)	g.drawLine(w-3, h-2, 2, h-2);

		// fill in corners
		if (paintLeft && paintTop) {
			g.drawLine(0, 0, 0, 1);
			g.drawLine(0, 0, 1, 0);
		}
		if (paintLeft && paintBottom)	g.drawLine(0, h-2, 1, h-2);
		if (paintRight && paintTop) 	g.drawLine(w-2, 0, w-2, 1);
		if (paintRight && paintBottom)	g.drawLine(w-2, h-2, w-2, h-2);

		g.setColor(etchType == LOWERED ? getHighlightColor(c) 
				: getShadowColor(c));
		if (paintLeft)		g.drawLine(1, 2, 1, h-3);
		if (paintTop)		g.drawLine(2, 1, w-3, 1);
		if (paintRight)		g.drawLine(w-1, h-3, w-1, 2);
		if (paintBottom)	g.drawLine(w-3, h-1, 2, h-1);

		if (paintLeft && paintTop)		g.drawLine(1, 1, 1, 1);
		if (paintLeft && paintBottom)	g.drawLine(0, h-1, 1, h-1);
		if (paintRight && paintTop)		g.drawLine(w-1, 0, w-1, 1);
		if (paintRight && paintBottom) {
			g.drawLine(w-1, h-1, w-1, h-2);
			g.drawLine(w-1, h-1, w-2, h-1);
		}

		g.translate(-x, -y);
	}
}
