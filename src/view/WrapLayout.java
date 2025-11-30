/*
 * HAPA Vehicle Rental System - Wrap Layout Utility
 * Custom layout manager that extends FlowLayout with proper wrapping support
 * Provides better component wrapping behavior than standard FlowLayout
 * Used for responsive UI components that need to wrap to new lines
 */
package view;

/**
 * WrapLayout - Custom layout manager extending FlowLayout
 * Provides enhanced wrapping capabilities for UI components
 * Properly calculates preferred and minimum sizes for wrapped components
 *
 * @author Pacifique Harerimana
 */

import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * FlowLayout subclass that fully supports wrapping of components
 * Provides better size calculations and wrapping behavior than standard FlowLayout
 */
public class WrapLayout extends FlowLayout
{
    // Cached preferred layout size for performance
    private Dimension preferredLayoutSize;

    /**
     * Default constructor
     * Creates a WrapLayout with default alignment and gaps
     */
    public WrapLayout()
    {
        super();
    }

    /**
     * Constructor with alignment specification
     * 
     * @param align Component alignment (LEFT, CENTER, RIGHT, etc.)
     */
    public WrapLayout(int align)
    {
        super(align);
    }

    /**
     * Constructor with full customization
     * 
     * @param align Component alignment
     * @param hgap Horizontal gap between components
     * @param vgap Vertical gap between rows
     */
    public WrapLayout(int align, int hgap, int vgap)
    {
        super(align, hgap, vgap);
    }

    /**
     * Calculates the preferred size for the layout
     * Takes into account component wrapping and proper spacing
     * 
     * @param target The container being laid out
     * @return Preferred dimensions for the layout
     */
    @Override
    public Dimension preferredLayoutSize(Container target)
    {
        return layoutSize(target, true);
    }

    /**
     * Calculates the minimum size for the layout
     * Provides minimum space needed for all components
     * 
     * @param target The container being laid out
     * @return Minimum dimensions for the layout
     */
    @Override
    public Dimension minimumLayoutSize(Container target)
    {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    /**
     * Core layout size calculation method
     * Calculates dimensions based on component wrapping behavior
     * 
     * @param target The container being laid out
     * @param preferred Whether to use preferred or minimum component sizes
     * @return Calculated dimensions for the layout
     */
    private Dimension layoutSize(Container target, boolean preferred)
    {
        synchronized (target.getTreeLock())
        {
            // Get target width, use max value if not set
            int targetWidth = target.getWidth();
            if (targetWidth == 0)
                targetWidth = Integer.MAX_VALUE;

            // Get layout parameters
            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();

            // Calculate available width for components
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            // Initialize layout tracking variables
            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            // Process each component for wrapping calculation
            for (int i = 0; i < nmembers; i++)
            {
                Component m = target.getComponent(i);

                if (m.isVisible())
                {
                    // Get component size (preferred or minimum)
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                    // Check if component needs to wrap to new row
                    if (rowWidth + d.width > maxWidth)
                    {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    // Add component to current row
                    rowWidth += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }

            // Add the final row
            addRow(dim, rowWidth, rowHeight);

            // Add container insets and gaps to final dimensions
            dim.width += horizontalInsetsAndGap;
            dim.height += insets.top + insets.bottom + vgap * 2;

            // Adjust for scroll pane if present
            Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
            if (scrollPane != null)
            {
                dim.width -= (hgap + 1);
            }

            return dim;
        }
    }

    /**
     * Adds a row to the overall layout dimensions
     * Updates width to maximum row width and adds height with proper gaps
     * 
     * @param dim Overall layout dimensions being calculated
     * @param rowWidth Width of the current row
     * @param rowHeight Height of the current row
     */
    private void addRow(Dimension dim, int rowWidth, int rowHeight)
    {
        // Update overall width to accommodate widest row
        dim.width = Math.max(dim.width, rowWidth);

        // Add vertical gap between rows (except for first row)
        if (dim.height > 0)
            dim.height += getVgap();

        // Add current row height to total height
        dim.height += rowHeight;
    }
}
