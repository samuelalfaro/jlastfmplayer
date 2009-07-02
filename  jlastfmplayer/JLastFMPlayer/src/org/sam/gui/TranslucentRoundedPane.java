/*
 * InternalOptionPane.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of JLastFMPlayer.
 * 
 * JLastFMPlayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JLastFMPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JLastFMPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.gui;

import java.awt.*;

import javax.swing.JComponent;

public class TranslucentRoundedPane extends JComponent {

	private static final long serialVersionUID = -5167049154953353709L;

	public TranslucentRoundedPane() {
		this(new FlowLayout());
	}

	public TranslucentRoundedPane(LayoutManager layout) {
        setLayout(layout);
        setDoubleBuffered(true);
		setOpaque(false);
	}
	
	private static Color deriveColor(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(deriveColor(getBackground().brighter(), 128));
		Composite compsite = g2.getComposite();
		g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
		g2.setComposite(compsite);
		g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2.setColor(getForeground());
		g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);
		g2.setColor(getBackground());
		g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 4, 20, 20);
		g2.dispose();
	}
}