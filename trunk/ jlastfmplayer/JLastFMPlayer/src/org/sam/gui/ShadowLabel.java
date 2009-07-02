/*
 * ShadowLabel.java
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
import java.awt.image.WritableRaster;

import javax.swing.*;

import org.sam.util.Imagen;

@SuppressWarnings("serial") 
public class ShadowLabel extends JLabel{
	
	private static final int blurBorder = 2;

	private Paint shadowPaint;
	private Composite shadowComposite;
	
	private int shadowOffsetX;
	private int shadowOffsetY;
	
	public ShadowLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		this.setOpaque(false);
		setShadowPaint(Color.BLACK);
		setShadowComposite(Imagen.BlendComposite.FUSIONAR_MUL);
		setShadowOffset(3,3);
	}

	/**
	 * Creates a <code>ShadowLabel</code> instance with the specified
	 * text and horizontal alignment.
	 * The label is centered vertically in its display area.
	 *
	 * @param text  The text to be displayed by the label.
	 * @param horizontalAlignment  One of the following constants
	 *           defined in <code>SwingConstants</code>:
	 *           <code>LEFT</code>,
	 *           <code>CENTER</code>,
	 *           <code>RIGHT</code>,
	 *           <code>LEADING</code> or
	 *           <code>TRAILING</code>.
	 */
	public ShadowLabel(String text, int horizontalAlignment) {
		this(text, null, horizontalAlignment);
	}

	/**
	 * Creates a <code>ShadowLabel</code> instance with the specified text.
	 * The label is aligned against the leading edge of its display area,
	 * and centered vertically.
	 *
	 * @param text  The text to be displayed by the label.
	 */
	public ShadowLabel(String text) {
		this(text, null, LEADING);
	}

	/**
	 * Creates a <code>ShadowLabel</code> instance with the specified
	 * image and horizontal alignment.
	 * The label is centered vertically in its display area.
	 *
	 * @param image  The image to be displayed by the label.
	 * @param horizontalAlignment  One of the following constants
	 *           defined in <code>SwingConstants</code>:
	 *           <code>LEFT</code>,
	 *           <code>CENTER</code>, 
	 *           <code>RIGHT</code>,
	 *           <code>LEADING</code> or
	 *           <code>TRAILING</code>.
	 */
	public ShadowLabel(Icon image, int horizontalAlignment) {
		this(null, image, horizontalAlignment);
	}

	/**
	 * Creates a <code>ShadowLabel</code> instance with the specified image.
	 * The label is centered vertically and horizontally
	 * in its display area.
	 *
	 * @param image  The image to be displayed by the label.
	 */
	public ShadowLabel(Icon image) {
		this(null, image, CENTER);
	}

	/**
	 * Creates a <code>ShadowLabel</code> instance with 
	 * no image and with an empty string for the title.
	 * The label is centered vertically 
	 * in its display area.
	 * The label's contents, once set, will be displayed on the leading edge 
	 * of the label's display area.
	 */
	public ShadowLabel() {
		this("", null, LEADING);
	}

	public void setShadowPaint(Paint shadowPaint) {
		this.shadowPaint = shadowPaint;
	}
	
	public void setShadowComposite(Composite shadowComposite) {
		this.shadowComposite = shadowComposite;
	}
	
	public void setShadowOffset(int x, int y) {
		this.shadowOffsetX = x;
		this.shadowOffsetY = y;
		
		int absX = Math.abs(x) + blurBorder;
		int absY = Math.abs(y) + blurBorder;
		
		int left   = x < 0 ? absX : blurBorder;
		int right  = x > 0 ? absX : blurBorder;
		
		int top    = y < 0 ? absY : blurBorder;
		int bottom = y > 0 ? absY : blurBorder;
		
		this.setBorder(BorderFactory.createEmptyBorder(top,left,bottom,right));
	}
	
	private static final Color CLEAR_COLOR = new Color(0,0,0,0);

	private Rectangle prevBounds;
	private BufferedImage backBuffer;
	private BufferedImage blurBuffer;
	
	private void createShadow() {
		if(this.getPreferredSize().width/2 == 0 || this.getPreferredSize().height/2 == 0)
			return;

		Graphics2D g2;
		int width2  = ((this.getPreferredSize().width  + 1)>>1)<<1;
		int height2 = ((this.getPreferredSize().height + 1)>>1)<<1;
		if( backBuffer == null ||
				backBuffer.getWidth() != width2 || backBuffer.getHeight() != height2 ){
			backBuffer = new BufferedImage( width2, height2, BufferedImage.TYPE_INT_ARGB); 
			blurBuffer = new BufferedImage( width2/2 + blurBorder*2, height2/2 + blurBorder*2, BufferedImage.TYPE_INT_ARGB);
		}

		Rectangle bounds = this.getBounds();
		Rectangle currentBounds = new Rectangle(0, 0, bounds.width<width2 ? bounds.width:width2,height2);
		
		if(prevBounds == null || prevBounds.width != currentBounds.width  || prevBounds.height != currentBounds.height){

			this.setBounds( currentBounds );

			g2 = backBuffer.createGraphics();
			g2.setBackground(CLEAR_COLOR);
			g2.clearRect(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
			g2.setFont(this.getFont());
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

			super.paint(g2);
			g2.dispose();
			this.setBounds(bounds);

			g2 = blurBuffer.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.setBackground(CLEAR_COLOR);
			g2.clearRect(0, 0, blurBuffer.getWidth(), blurBuffer.getHeight());
			g2.setPaint(shadowPaint);
			g2.fillRect(blurBorder, blurBorder, blurBuffer.getWidth()-blurBorder*2, blurBuffer.getHeight()-blurBorder*2);
			g2.setComposite(Imagen.BlendComposite.ALPHA_MASK);
			g2.drawImage(backBuffer, blurBorder, blurBorder, blurBuffer.getWidth()-blurBorder*2, blurBuffer.getHeight()-blurBorder*2, null);
			g2.dispose();

			WritableRaster src = blurBuffer.getAlphaRaster();
			WritableRaster dst = src.createCompatibleWritableRaster();
			Imagen.BLUR_FILTER.filter(src, dst);
			src.setDataElements(src.getMinX(), src.getMinY(), dst);
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible){
		if(visible)
			createShadow();
		super.setVisible(visible);
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#doLayout()
	 */
	@Override
	public void doLayout(){
		if(isVisible())
			createShadow();
		super.doLayout();
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#validate()
	 */
	@Override
	public void validate(){
		if(isVisible())
			createShadow();
		super.validate();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		if (isVisible() && blurBuffer != null) {
			Graphics2D g2 = (Graphics2D) g.create();

			int tx;
			if(this.getWidth() < backBuffer.getWidth())
				tx = 0;
			else{
				int horizontalAlignment  = this.getHorizontalAlignment();
				switch(horizontalAlignment){
				case SwingConstants.LEADING:
					horizontalAlignment = ComponentOrientation.getOrientation(getLocale()).isLeftToRight()? 
							SwingConstants.LEFT : SwingConstants.RIGHT;
					break;
				case SwingConstants.TRAILING:
					horizontalAlignment = ComponentOrientation.getOrientation(getLocale()).isLeftToRight()? 
							SwingConstants.RIGHT : SwingConstants.LEFT;
					break;
				}
				switch(horizontalAlignment){
				case SwingConstants.LEFT:
					tx = 0;
					break;
				case SwingConstants.RIGHT:
					tx = (this.getWidth() - backBuffer.getWidth());
					break;
				case SwingConstants.CENTER:
				default:
					tx = (this.getWidth() - backBuffer.getWidth())/2;
				}
			}
			
			int ty;
			switch(this.getVerticalAlignment()){
			case SwingConstants.TOP:
				ty = 0;
				break;
			case SwingConstants.BOTTOM:
				ty = (this.getHeight()-backBuffer.getHeight());
				break;
			case SwingConstants.CENTER:
			default:
				ty = (this.getHeight()-backBuffer.getHeight())/2;
			}

			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			
			Composite prevComposite = null;
			if( shadowComposite != null ){
				prevComposite = g2.getComposite();
				g2.setComposite(shadowComposite);
			}
			g2.drawImage(
					blurBuffer,
					tx + shadowOffsetX, ty + shadowOffsetY, tx + backBuffer.getWidth() + shadowOffsetX, ty + backBuffer.getHeight() + shadowOffsetY,
					blurBorder, blurBorder, blurBuffer.getWidth() - blurBorder, blurBuffer.getHeight() - blurBorder,
					this
			);
			if( prevComposite != null)
				g2.setComposite(prevComposite);
			g2.drawImage(backBuffer, tx, ty, this);
			g2.dispose();
		}
	}
}