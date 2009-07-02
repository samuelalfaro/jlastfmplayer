/*
 * BlurGlassPane.java
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
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import org.sam.util.Imagen;

@SuppressWarnings("serial")
public class BlurGlassPane extends JComponent {

	public BlurGlassPane() {
		this.setOpaque(true);
		setLayout( new GridBagLayout() );
		/*
		// This component keeps the focus until is made hidden         
		setInputVerifier(new InputVerifier() { 
			public boolean verify(JComponent input) { 
				return !isVisible(); 
			} 
		});
		*/ 
		// Attach mouse listeners
		MouseInputAdapter adapter = new MouseInputAdapter() {
		};
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}

	private BufferedImage backBuffer;
	private BufferedImage blurBuffer;
	
	private void createBlur() {
		Graphics2D g2;
		int width2  = ((this.getWidth()  + 1)>>1)<<1;
		int height2 = ((this.getHeight() + 1)>>1)<<1;
		if( backBuffer == null ||
				backBuffer.getWidth() != width2 || backBuffer.getHeight() != height2 ){
			backBuffer = new BufferedImage( width2, height2, BufferedImage.TYPE_INT_ARGB); 
			blurBuffer = new BufferedImage( width2/2, height2/2, BufferedImage.TYPE_INT_RGB);
		}
		
		g2 = backBuffer.createGraphics();
		SwingUtilities.getRootPane(this).getContentPane().paint(g2);
		g2.dispose();

		g2 = blurBuffer.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2.drawImage(backBuffer, 0, 0, blurBuffer.getWidth(), blurBuffer.getHeight(), null);
		g2.setComposite(Imagen.BlendComposite.FUSIONAR_MUL);
		g2.setColor(new Color(0,0,0,0.5f));
		g2.fillRect(0, 0, blurBuffer.getWidth(), blurBuffer.getHeight());
		g2.dispose();

		blurBuffer = Imagen.BLUR_FILTER.filter(blurBuffer, null);
		
		g2 = backBuffer.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.drawImage(blurBuffer, 0, 0, getWidth(), getHeight(), this);
		g2.dispose();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible){
		if(visible)
			createBlur();
		super.setVisible(visible);
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#doLayout()
	 */
	@Override
	public void doLayout(){
		if(isVisible())
			createBlur();
		super.doLayout();
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#validate()
	 */
	@Override
	public void validate(){
		if(isVisible())
			createBlur();
		super.validate();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		if (isVisible() && backBuffer != null)
			g.drawImage(backBuffer, 0, 0, this);
	}
}