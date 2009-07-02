/*
 * GradientSlider.java
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
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;

@SuppressWarnings("serial") 
public class GradientSlider extends JComponent{
	
	private final transient Font defaultFont;
	private float value;
	
	private final MouseAdapter adapter = new MouseAdapter(){

		private void fireEvent(int x){
	    	float newValue;
	    	if( x < centers[0].x)
	    		newValue = 0.0f;
	    	else if( x > centers[1].x)
	    		newValue = 100.0f;
	    	else
	    		newValue = 100 * (x - centers[0].x) / (centers[1].x - centers[0].x);
	    	if(newValue != value){
		    	firePropertyChange("Value", value, newValue);
	    		value = newValue;
	    		updateValue();
	    		repaint();
	    	}
		}
		
	    public void mouseReleased(MouseEvent e) {
	    	fireEvent(e.getX());
	    }

	    public void mouseDragged(MouseEvent e){
	    	fireEvent(e.getX());
	    }
	};
	
	private transient float radius, angleInDegres;
	private transient Point2D.Float[] centers;
	private transient Point2D.Float[] centers2;
	
	public GradientSlider() {
		this.value = 50;
		
		centers = new Point2D.Float[3];
		centers2 = new Point2D.Float[3];
		for(int i=0,len = centers.length; i < len; i++){
			centers[i]= new Point2D.Float();
			centers2[i]= new Point2D.Float();
		}
		this.setOpaque(false);
		this.setBorder(BorderFactory.createEmptyBorder(4,2,4,2));
		this.setLayout(new BorderLayout());
		this.defaultFont = UIManager.getFont("Label.font");
		
		this.addMouseListener(adapter);
		this.addMouseMotionListener(adapter);
	}
	
	private transient GeneralPath shape;
	private transient Paint paintShape;
	private final transient float[] paintSapheFractions =
		new float[]{0.0f,1.0f};
	private final transient Color[] paintSapheColors = 
		new Color[]{deriveColor(Color.DARK_GRAY,128),deriveColor(Color.LIGHT_GRAY,128)};

	private transient GeneralPath inerShape;
	private transient Paint paintInerShape;
	private final transient float[] paintInerSapheFractions =
		new float[]{0.5f,1.0f};
	private final transient Color[] paintInerSapheColors =
		new Color[]{deriveColor(Color.WHITE,64),deriveColor(Color.BLACK,128)};

	private transient GeneralPath valueShape;
	private transient Paint paintValueShape;
	private final transient float[] paintValueSapheFractions =
		new float[]{0.0f,0.5f,1.0f};
	private final transient Color[] paintValueSapheColors =
		new Color[]{Color.GREEN,Color.YELLOW,Color.RED};
	private final transient Color[] paintValueSapheGrays =
		new Color[]{DesaturateColor(Color.GREEN),DesaturateColor(Color.YELLOW),DesaturateColor(Color.RED)};
	
	private static Color deriveColor( Color color, int alpha ){
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
	
	private static Color DesaturateColor( Color color){
		int luminance = (int)(0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());
		return new Color(luminance, luminance, luminance, color.getAlpha());
	}
	
	private void updateShapes() {
		Insets insets = this.getInsets();
	
		int width = this.getWidth() - insets.left - insets.right;
		if( width < 0 )
			return;
		int height = this.getHeight() - insets.top - insets.bottom;
		if( height < 0 )
			return;
		
		if(shape == null || shape.getBounds().width != width || shape.getBounds().height != height ){
			
			radius = Math.min(width/3.0f, height/4.0f);
			centers[0].x = insets.left + radius;
			centers[0].y = this.getHeight() - radius - insets.bottom;
			centers[1].x = this.getWidth() - radius - insets.right;
			centers[1].y = centers[0].y;
			centers[2].x = centers[1].x; 
			centers[2].y = insets.top + radius;
	
			double angle = Math.atan2(-(centers[2].y - centers[0].y), centers[2].x - centers[0].x);
			angleInDegres = (float)(angle / Math.PI * 180);
			
			float diameter = radius * 2;
			shape = new GeneralPath();
			shape.append(new Arc2D.Float(centers[0].x - radius, centers[0].y - radius, diameter, diameter, 90 + angleInDegres, 180 - angleInDegres, Arc2D.OPEN ), false);
			shape.append(new Arc2D.Float(centers[1].x - radius, centers[1].y - radius, diameter, diameter, 270, 90, Arc2D.OPEN ), true);
			shape.append(new Arc2D.Float(centers[2].x - radius, centers[2].y - radius, diameter, diameter, 0, 90 + angleInDegres, Arc2D.OPEN ), true);
			shape.closePath();
			
			paintShape = new LinearGradientPaint(
					0, this.getHeight()/2,
					0, this.getHeight(),
					paintSapheFractions,
					paintSapheColors,
					CycleMethod.REFLECT
			); 
		
			float r = 2*radius/3;
			diameter = r *2;
			inerShape = new GeneralPath();
			inerShape.append(new Arc2D.Float(centers[0].x - r, centers[0].y - r, diameter, diameter, 90 + angleInDegres, 180 - angleInDegres, Arc2D.OPEN ), false);
			inerShape.append(new Arc2D.Float(centers[1].x - r, centers[1].y - r, diameter, diameter, 270, 90, Arc2D.OPEN ), true);
			inerShape.append(new Arc2D.Float(centers[2].x - r, centers[2].y - r, diameter, diameter, 0, 90 + angleInDegres, Arc2D.OPEN ), true);
			inerShape.closePath();
			
			paintInerShape = new RadialGradientPaint(
					new Rectangle2D.Float(-radius, -radius*2, this.getWidth()+radius*2, this.getHeight()*2 ),
					paintInerSapheFractions,
					paintInerSapheColors,
					CycleMethod.NO_CYCLE
			);
			updateValue();
		}
	}

	private void updateValue(){
		float scale = value / 100;
		
		centers2[0].x = centers[0].x;
		centers2[0].y = centers[0].y;
		centers2[1].x = (centers[1].x - centers[0].x) * scale + centers[0].x;
		centers2[1].y = centers[1].y;
		centers2[2].x = centers2[1].x; 
		centers2[2].y = this.getHeight() - ((centers[1].y - centers[2].y) * scale + centers[2].y);
		
		float r = radius/3;
		float diameter = r *2;
		valueShape = new GeneralPath();
		valueShape.append(new Arc2D.Float(centers2[0].x - r, centers2[0].y - r, diameter, diameter, 90 + angleInDegres, 180 - angleInDegres, Arc2D.OPEN ), false);
		valueShape.append(new Arc2D.Float(centers2[1].x - r, centers2[1].y - r, diameter, diameter, 270, 90, Arc2D.OPEN ), true);
		valueShape.append(new Arc2D.Float(centers2[2].x - r, centers2[2].y - r, diameter, diameter, 0, 90 + angleInDegres, Arc2D.OPEN ), true);
		valueShape.closePath();
		updatePaintValue();
	}
	
	private void updatePaintValue(){
		float scale = value / 100;
		float len = centers[1].x - centers[0].x;
		float offset = centers[0].x - len * scale;
		float x1 = offset;
		float x2 = offset + len*2;
		Color[] colors = this.isEnabled()?paintValueSapheColors : paintValueSapheGrays;
		
		paintValueShape = (x1 == x2)?
				colors[0]:
				new LinearGradientPaint(
						offset, 0,
						offset + len*2, 0,
						paintValueSapheFractions,
						colors,
						CycleMethod.NO_CYCLE
				);
	}

	private final Dimension minimunSize = new Dimension(64,16);
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	public Dimension getMinimunSize(){
		return (Dimension)minimunSize.clone();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	public Dimension getPreferredSize(){
		return (Dimension)minimunSize.clone();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Container#doLayout()
	 */
	@Override
	public void doLayout(){
		if(isVisible())
			updateShapes();
		super.doLayout();
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#validate()
	 */
	@Override
	public void validate(){
		if(isVisible())
			updateShapes();
		super.validate();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled){
		if(enabled != this.isEnabled()){
			if(enabled){
				this.addMouseListener(adapter);
				this.addMouseMotionListener(adapter);
			}else{
				this.removeMouseListener(adapter);
				this.removeMouseMotionListener(adapter);
			}
			super.setEnabled(enabled);
			updatePaintValue();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Insets insets = this.getInsets();
		if (isVisible() &&
				this.getWidth() > insets.left + insets.right &&
				this.getHeight() > insets.top + insets.bottom) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
//			g2.setRenderingHint(
//					RenderingHints.KEY_TEXT_ANTIALIASING,
//                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g2.setPaint(paintShape);
			g2.fill(shape);
			g2.setPaint(paintInerShape);
			g2.fill(inerShape);
			if(value > 0){
				g2.setPaint(paintValueShape);
				g2.fill(valueShape);
			}
			
			g2.setPaint(Color.BLACK);
			FontMetrics fmd = g2.getFontMetrics(defaultFont);
			float scaleFont = (centers[1].y - centers[2].y)/fmd.getAscent();
			g2.setFont(defaultFont.deriveFont(AffineTransform.getScaleInstance(scaleFont,scaleFont)));
			String text = String.format("%.0f %%",value);
			int posxText = (this.getWidth() - g2.getFontMetrics().stringWidth(text))/2;
			g2.drawString(text, posxText, centers[1].y+radius/4);
			g2.dispose();
		}
	}
}