/*
 * Imagen.java
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
package org.sam.util;

import java.awt.*;
import java.awt.image.*;
import java.nio.*;

/**
 * @author Samuel Alfaro <samuelalfaro at gmail.com>
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Imagen {

	public static class BlendComposite{
		public static final int MODO_AND = 0;
		public static final int MODO_OR  = 1;
		public static final int MODO_XOR = 2;
		public static final int MODO_SUM = 3;
		public static final int MODO_RES = 4;
		public static final int MODO_DIF = 5;
		/**
		 El MODO_MUL produce un efecto de oscurecimento, corresponde a la fórmula:
	
		 dst = ( src1 * src2)
	
		 los valores 1 dejan el origen como estaba
		 y los valores 0 devuelven el valor 0
		 */
		public static final int MODO_MUL = 6;
		/**
		 El MODO_DIV es el modo inverso al modoMUL produce un efecto de aclarado,
		 corresponde a la fórmula:
	
		 dst = ~( ~src1 * ~src2)
	
		 los valores 1 dejan el origen como estaba
		 y los valores 0 devuelven el valor 0
		 */
		public static final int MODO_DIV = 7;
		/**
		 El MODO_SUP es el modo de superposición de la imagen con la transparecia
	
		 dst = src1*(1-alfa) + src2*alfa
	
		 Si la componente alfa es 1, o no se aplica el resultado es src2
		 */
		public static final int MODO_SUP = 8;

		private static int getScanlineStride(Raster raster){
			return ((SinglePixelPackedSampleModel)raster.getSampleModel()).getScanlineStride();
		}

		private static int getInitialIndex(Raster raster){
			int sls = getScanlineStride(raster);
			return (-raster.getSampleModelTranslateY() * sls) - raster.getSampleModelTranslateX();
		}

		private static int getScanlineOffset(Raster raster){
			return getScanlineStride(raster) - raster.getWidth();
		}

		private static abstract class Fusionador implements Composite, CompositeContext{
			protected final int alfa;

			Fusionador(){
				alfa = ALFA_SRC;
			}

			Fusionador(int alfa){
				if(alfa < ALFA_OFF && alfa > ALFA_SRC)
					throw(errorAlfa);
				this.alfa = alfa;
			}

			public void dispose() {
			}

			public final CompositeContext createContext(
					ColorModel srcColorModel,
					ColorModel dstColorModel,
					RenderingHints hints) {
				return this;
			}
		};

		public static Composite FUSIONAR_AND = new Fusionador(){
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				DataBuffer db1 = src.getDataBuffer();

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				DataBuffer db3 = dstOut.getDataBuffer();

				for(int y = 0; y < dstIn.getHeight(); y++, i1+= slo1, i2+= slo2,i3+= slo3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pixel1 = db1.getElem(i1);
						int pixel2 = db2.getElem(i2);

						int pixel3 = (pixel2 & A_MASK) | (pixel1 & pixel2 & A_MASK_I);
						db3.setElem(i3, pixel3);
					}
			}
		};

		public static Composite FUSIONAR_OR = new Fusionador(){
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				DataBuffer db1 = src.getDataBuffer();

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				DataBuffer db3 = dstOut.getDataBuffer();

				for(int y = 0; y < dstIn.getHeight(); y++, i1+= slo1, i2+= slo2,i3+= slo3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pixel1 = db1.getElem(i1);
						int pixel2 = db2.getElem(i2);

						int pixel3 = (pixel2 & A_MASK) | (pixel1 | pixel2 & A_MASK_I);
						db3.setElem(i3, pixel3);
					}
			}
		};

		public static Composite FUSIONAR_XOR = new Fusionador(){
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				DataBuffer db1 = src.getDataBuffer();

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				DataBuffer db3 = dstOut.getDataBuffer();

				for(int y = 0; y < dstIn.getHeight(); y++, i1+= slo1, i2+= slo2,i3+= slo3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pixel1 = db1.getElem(i1);
						int pixel2 = db2.getElem(i2);

						int pixel3 = (pixel2 & A_MASK) | (pixel1 ^ pixel2 & A_MASK_I);
						db3.setElem(i3, pixel3);
					}
			}
		};

		public static class Fusionador_Suma extends Fusionador{

			public Fusionador_Suma(){
				super();
			}

			public Fusionador_Suma(int alfa){
				super(alfa);
			}

			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				DataBuffer db1 = src.getDataBuffer();

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				DataBuffer db3 = dstOut.getDataBuffer();

				switch(alfa){
				case ALFA_OFF:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int r = (pDst>>16 & BYTE) + (pSrc>>16 & BYTE);
							int g = (pDst>>8 & BYTE) + (pSrc>>8 & BYTE);
							int b = (pDst & BYTE) + (pSrc & BYTE);
							db3.setElem(i3,
									( pDst & A_MASK) |
									( r > BYTE ? R_MASK : (r<<16 & R_MASK) ) |
									( g > BYTE ? G_MASK : (g<<8 & G_MASK) ) |
									( b > BYTE ? B_MASK : (b & B_MASK) )
							);
						}
					break;
				case ALFA_SRC:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int a = (pSrc>>24 & BYTE);
							int r = (pDst>>16 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
							int g = (pDst>>8 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
							int b = (pDst & BYTE) + FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
							db3.setElem(i3,
									( pDst & A_MASK) |
									( r > BYTE ? R_MASK : (r<<16 & R_MASK) ) |
									( g > BYTE ? G_MASK : (g<<8 & G_MASK) ) |
									( b > BYTE ? B_MASK : (b & B_MASK) )
							);
						}
					break;
				default:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
							int r = (pDst>>16 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
							int g = (pDst>>8 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
							int b = (pDst & BYTE) + FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
							db3.setElem(i3,
									( pDst & A_MASK) |
									( r > BYTE ? R_MASK : (r<<16 & R_MASK) ) |
									( g > BYTE ? G_MASK : (g<<8 & G_MASK) ) |
									( b > BYTE ? B_MASK : (b & B_MASK) )
							);
						}
				}
			}
		};
		public static Composite FUSIONAR_SUM = new Fusionador_Suma();

		public static class Fusionador_Resta extends Fusionador{

			public Fusionador_Resta(){
				super();
			}

			public Fusionador_Resta(int alfa){
				super(alfa);
			}

			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				DataBuffer db1 = src.getDataBuffer();

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				DataBuffer db3 = dstOut.getDataBuffer();

				switch(alfa){
				case ALFA_OFF:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int r = (pDst>>16 & BYTE) - (pSrc>>16 & BYTE);
							int g = (pDst>>8 & BYTE) - (pSrc>>8 & BYTE);
							int b = (pDst & BYTE) - (pSrc & BYTE);
							db3.setElem(i3,
									( pDst & A_MASK) |
									( r < 0 ? 0 : (r<<16 & R_MASK) ) |
									( g < 0 ? 0 : (g<<8 & G_MASK)) |
									( b < 0 ? 0 : (b & B_MASK) )
							);
						}
					break;
				case ALFA_SRC:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int a = (pSrc>>24 & BYTE);
							int r = (pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
							int g = (pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
							int b = (pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
							db3.setElem(i3,
									( pDst & A_MASK) |
									( r < 0 ? 0 : (r<<16 & R_MASK) ) |
									( g < 0 ? 0 : (g<<8 & G_MASK)) |
									( b < 0 ? 0 : (b & B_MASK) )
							);
						}
					break;
				default:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
							int r = (pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
							int g = (pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
							int b = (pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
							db3.setElem(i3,
									( pDst & A_MASK) |
									( r < 0 ? 0 : (r<<16 & R_MASK) ) |
									( g < 0 ? 0 : (g<<8 & G_MASK)) |
									( b < 0 ? 0 : (b & B_MASK) )
							);
						}
				}
			}
		};
		public static Composite FUSIONAR_RES = new Fusionador_Resta();

		public static class Fusionador_Diferencia extends Fusionador{

			public Fusionador_Diferencia(){
				super();
			}

			public Fusionador_Diferencia(int alfa){
				super(alfa);
			}

			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				DataBuffer db1 = src.getDataBuffer();

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				DataBuffer db3 = dstOut.getDataBuffer();

				switch(alfa){
				case ALFA_OFF:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int r = Math.abs((pDst>>16 & BYTE) - (pSrc>>16 & BYTE));
							int g = Math.abs((pDst>>8 & BYTE) - (pSrc>>8 & BYTE));
							int b = Math.abs((pDst & BYTE) - (pSrc & BYTE));
							db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
						}
					break;
				case ALFA_SRC:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int a = (pSrc>>24 & BYTE);
							int r = Math.abs((pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a));
							int g = Math.abs((pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a));
							int b = Math.abs((pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a));
							db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
						}
					break;
				default:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
							int r = Math.abs((pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a));
							int g = Math.abs((pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a));
							int b = Math.abs((pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a));
							db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
						}
				}
			}
		};
		public static Composite FUSIONAR_DIF = new Fusionador_Diferencia();

		public static class Fusionador_Multiplicacion extends Fusionador{

			public Fusionador_Multiplicacion(){
				super();
			}

			public Fusionador_Multiplicacion(int alfa){
				super(alfa);
			}

			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				DataBuffer db1 = src.getDataBuffer();

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				DataBuffer db3 = dstOut.getDataBuffer();

				switch(alfa){
				case ALFA_OFF:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int r = FAST_DIVIDE_BY_255((pDst>>16 & BYTE) * (pSrc>>16 & BYTE));
							int g = FAST_DIVIDE_BY_255((pDst>>8 & BYTE) * (pSrc>>8 & BYTE));
							int b = FAST_DIVIDE_BY_255((pDst & BYTE) * (pSrc & BYTE));
							db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
						}
					break;
				case ALFA_SRC:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int a = (pSrc>>24 & BYTE);
							int in_a = BYTE - a;
							int r = FAST_DIVIDE_BY_255((pDst>>16 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a) + in_a));
							int g = FAST_DIVIDE_BY_255((pDst>>8 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a) + in_a));
							int b = FAST_DIVIDE_BY_255((pDst & BYTE) * (FAST_DIVIDE_BY_255((pSrc & BYTE)*a) + in_a));
							db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
						}
					break;
				default:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
							int in_a = BYTE - a;
							int r = FAST_DIVIDE_BY_255((pDst>>16 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a) + in_a));
							int g = FAST_DIVIDE_BY_255((pDst>>8 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a) + in_a));
							int b = FAST_DIVIDE_BY_255((pDst & BYTE) * (FAST_DIVIDE_BY_255((pSrc & BYTE)*a) + in_a));
							db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
						}
				}
			}
		};
		public static Composite FUSIONAR_MUL = new Fusionador_Multiplicacion();

		public static class Fusionador_Division extends Fusionador{

			public Fusionador_Division(){
				super();
			}

			public Fusionador_Division(int alfa){
				super(alfa);
			}

			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
				if( src.getSampleModel().getDataType() != DataBuffer.TYPE_INT
						|| dstIn.getSampleModel().getDataType() != DataBuffer.TYPE_INT
						|| dstOut.getSampleModel().getDataType() != DataBuffer.TYPE_INT ){
					throw new IllegalStateException("Source and destination must store pixels as INT.");
				}

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				DataBuffer db1 = src.getDataBuffer();

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				DataBuffer db3 = dstOut.getDataBuffer();

				switch(alfa){
				case ALFA_OFF:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = ~db1.getElem(i1);
							int pDst = ~db2.getElem(i2);

							int r = FAST_DIVIDE_BY_255(~( (pDst>>16 & BYTE) * (pSrc>>16 & BYTE) ) );
							int g = FAST_DIVIDE_BY_255(~( (pDst>>8 & BYTE) * (pSrc>>8 & BYTE) ) );
							int b = FAST_DIVIDE_BY_255(~( (pDst & BYTE) * (pSrc & BYTE) ) );
							db3.setElem(i3, (~pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK));
						}
					break;
				case ALFA_SRC:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = ~db2.getElem(i2);
							int a = (pSrc>>24 & BYTE);
							int r = FAST_DIVIDE_BY_255(~((pDst>>16 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a))&BYTE)));
							int g = FAST_DIVIDE_BY_255(~((pDst>>8 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a))&BYTE)));
							int b = FAST_DIVIDE_BY_255(~((pDst & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc & BYTE)*a))&BYTE)));
							db3.setElem(i3, (~pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK));
						}
					break;
				default:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = ~db2.getElem(i2);
							int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
							int r = FAST_DIVIDE_BY_255(~((pDst>>16 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a))&BYTE)));
							int g = FAST_DIVIDE_BY_255(~((pDst>>8 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a))&BYTE)));
							int b = FAST_DIVIDE_BY_255(~((pDst & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc & BYTE)*a))&BYTE)));
							db3.setElem(i3, (~pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK));
						}
				}
			}
		};
		public static Composite FUSIONAR_DIV = new Fusionador_Division();

		public static class Fusionador_Superposicion extends Fusionador{

			public Fusionador_Superposicion(){
				super();
			}

			public Fusionador_Superposicion(int alfa){
				super(alfa);
			}

			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				DataBuffer db1 = src.getDataBuffer();

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				DataBuffer db3 = dstOut.getDataBuffer();

				switch(alfa){
				case ALFA_OFF:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i3++){
							int pSrc = db1.getElem(i1);
							db3.setElem(i3, pSrc );
						}
					break;
				case ALFA_SRC:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int a = (pSrc>>24 & BYTE);
							int r = pDst>>16 & BYTE;
						r += FAST_DIVIDE_BY_255(((pSrc>>16 & BYTE)-r)*a);
						int g = pDst>>8 & BYTE;
							g += FAST_DIVIDE_BY_255(((pSrc>>8 & BYTE)-g)*a);
							int b = pDst & BYTE;
							b += FAST_DIVIDE_BY_255(((pSrc & BYTE)-b)*a);
							db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
						}
					break;
				default:
					for (int y = 0; y < dstIn.getHeight(); y++, i1 += slo1, i2 += slo2, i3 += slo3)
						for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
							int pSrc = db1.getElem(i1);
							int pDst = db2.getElem(i2);
							int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
							int r = pDst>>16 & BYTE;
							r += FAST_DIVIDE_BY_255(((pSrc>>16 & BYTE)-r)*a);
							int g = pDst>>8 & BYTE;
							g += FAST_DIVIDE_BY_255(((pSrc>>8 & BYTE)-g)*a);
							int b = pDst & BYTE;
							b += FAST_DIVIDE_BY_255(((pSrc & BYTE)-b)*a);
							db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
						}
				}
			}
		};
		public static Composite FUSIONAR_SUP = new Fusionador_Superposicion();

		public static Composite ALPHA_CHANNEL = new Fusionador(){
			/*
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
				Rectangle srcRect = src.getBounds();
				Rectangle dstInRect = dstIn.getBounds();
				Rectangle dstOutRect = dstOut.getBounds();
				int x = 0, y = 0;
				int w = Math.min(Math.min(srcRect.width, dstOutRect.width), dstInRect.width);
				int h = Math.min(Math.min(srcRect.height, dstOutRect.height), dstInRect.height);
				Object srcPix = null, dstPix = null;
				for (y = 0; y < h; y++)
					for (x = 0; x < w; x++) {
						srcPix = src.getDataElements(x + srcRect.x, y + srcRect.y, srcPix);
						dstPix = dstIn.getDataElements(x + dstInRect.x, y + dstInRect.y, dstPix);
						int sp = srcColorModel.getRGB(srcPix);
						int dp = dstColorModel.getRGB(dstPix);
						int rp = add(sp,dp);
						dstOut.setDataElements(x + dstOutRect.x, y + dstOutRect.y, dstColorModel.getDataElements(rp, null));
					}
			}
			/*/
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				DataBuffer db1 = src.getDataBuffer();

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				DataBuffer db3 = dstOut.getDataBuffer();

				for(int y = 0; y < dstIn.getHeight(); y++, i1+= slo1, i2+= slo2,i3+= slo3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);

						int pixel3 = (pSrc & A_MASK) | (pDst & A_MASK_I);
						db3.setElem(i3, pixel3);
					}
			}
			//*/
		};

		public static Composite ALPHA_CHANNEL_PRE = new Fusionador(){
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				DataBuffer db1 = src.getDataBuffer();

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				DataBuffer db3 = dstOut.getDataBuffer();

				for(int y = 0; y < dstIn.getHeight(); y++, i1+= slo1, i2+= slo2, i3+= slo3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int a = (pSrc>>24 & BYTE);
						int r = FAST_DIVIDE_BY_255( (pDst>>16 & BYTE) * a );
						int g = FAST_DIVIDE_BY_255( (pDst>>8 & BYTE) * a );
						int b = FAST_DIVIDE_BY_255( (pDst & BYTE) * a );
						db3.setElem(i3,
								( pSrc & A_MASK) |
								( r > BYTE ? R_MASK : (r<<16 & R_MASK) ) |
								( g > BYTE ? G_MASK : (g<<8 & G_MASK) ) |
								( b > BYTE ? B_MASK : (b & B_MASK) )
						);
					}
			}
		};

		public static Composite ALPHA_MASK = new Fusionador(){
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

				int i1   = getInitialIndex(src);
				int slo1 = getScanlineOffset(src);
				int data1[] = ((DataBufferInt)src.getDataBuffer()).getData();
				//			DataBuffer db2 = dstIn.getDataBuffer(); 

				int i2   = getInitialIndex(dstIn);
				int slo2 = getScanlineOffset(dstIn);
				int data2[] = ((DataBufferInt)dstIn.getDataBuffer()).getData();
				//			DataBuffer db2 = dstIn.getDataBuffer();

				int i3   = getInitialIndex(dstOut);
				int slo3 = getScanlineOffset(dstOut);
				int data3[] = ((DataBufferInt)dstOut.getDataBuffer()).getData();
				//			DataBuffer db3 = dstOut.getDataBuffer();

				int width = Math.min(src.getWidth(), dstIn.getWidth());
				int height = Math.min(src.getHeight(), dstIn.getHeight());

				for(int y = 0; y < height; y++, i1+= slo1, i2+= slo2, i3+= slo3)
					for(int x =0; x < width; x++){
						//int pSrc = db1.getElem(i1);
						int pSrc = data1[i1++];
						//int pDst = db2.getElem(i2);
						int pDst = data2[i2++];

						int pixel3 = ( ( FAST_DIVIDE_BY_255( (pDst>>24 & BYTE) * (pSrc>>24 & BYTE) ) << 24 )& A_MASK) | (pDst & A_MASK_I);
						//db3.setElem(i3, pixel3);
						data3[i3++] = pixel3;
					}
			}
		};

		public static Image fusionar(Image src, Image dstIn, int mode, int alfaMode){
			BufferedImage dstOut = new BufferedImage(dstIn.getWidth(null), dstIn.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = dstOut.createGraphics();
			g.drawImage(dstIn,0,0,dstIn.getWidth(null), dstIn.getHeight(null), null);
			switch(mode){
			case MODO_AND:
				g.setComposite(FUSIONAR_AND);
				break;
			case MODO_OR :
				g.setComposite(FUSIONAR_OR);
				break;
			case MODO_XOR:
				g.setComposite(FUSIONAR_XOR);
				break;
			case MODO_SUM:
				g.setComposite(new Fusionador_Suma(alfaMode));
				break;
			case MODO_RES:
				g.setComposite(new Fusionador_Resta(alfaMode));
				break;
			case MODO_DIF:
				g.setComposite(new Fusionador_Diferencia(alfaMode));
				break;
			case MODO_MUL:
				g.setComposite(new Fusionador_Multiplicacion(alfaMode));
				break;
			case MODO_DIV:
				g.setComposite(new Fusionador_Division(alfaMode));
				break;
			case MODO_SUP:
				g.setComposite(new Fusionador_Superposicion(alfaMode));
				break;
			default: throw(errorModo);
			}
			g.drawImage(src,0,0,dstIn.getWidth(null), dstIn.getHeight(null), null);
			g.dispose();
			return dstOut;
		}
	}

	public static final int ALFA_OFF = -1;
	public static final int ALFA_SRC = 256;

	private static final int A_MASK = 0xFF000000;
	private static final int R_MASK = 0x00FF0000;
	private static final int G_MASK = 0x0000FF00;
	private static final int B_MASK = 0x000000FF;

	private static final int A_MASK_I = 0x00FFFFFF;
	private static final int R_MASK_I = 0xFF00FFFF;
	private static final int G_MASK_I = 0xFFFF00FF;
	private static final int B_MASK_I = 0xFFFFFF00;

	private static final int BYTE = 0xFF;
	
	private static final int FAST_DIVIDE_BY_255(int v){
		return (((v << 8) + v + BYTE) >> 16);
	}
	
	public static final int CANAL_ALFA  = 3;
	public static final int CANAL_ROJO  = 2;
	public static final int CANAL_VERDE = 1;
	public static final int CANAL_AZUL  = 0;
	
	private static final IllegalArgumentException errorModo=
		new IllegalArgumentException("Modo de fusión desconcido");
	private static final IllegalArgumentException errorAlfa=
		new IllegalArgumentException("Valor alfa fuera de rango: { ALFA_OFF, [0.255], ALFA_SRC }");
	private static final IllegalArgumentException errorCanal=
		new IllegalArgumentException("Valor canal fuera de rango:\n\t{ CANAL_ALFA = 3, CANAL_ROJO = 2,  CANAL_VERDE = 1, CANAL_AZUL = 0 }");
	private static final IllegalArgumentException errorRangos=
		new IllegalArgumentException("Argumento fuera del rango [0.0 .. 1.0]");
	private static final IllegalArgumentException errorMinMayorMax=
		new IllegalArgumentException("El valor mínimo no puede ser superio del valor máximo");

	public static Image cambiarCanal(int canal, Image img, int val){
		int ancho = img.getWidth(null);
		int alto  = img.getHeight(null);
		int pixels[];
		switch(canal){
		case CANAL_ALFA:
			pixels = cambiarCanal(A_MASK, A_MASK_I, canal*8, toPixels(img),val);
			break;
		case CANAL_ROJO:
			pixels = cambiarCanal(R_MASK, R_MASK_I, canal*8, toPixels(img),val);
			break;
		case CANAL_VERDE:
			pixels = cambiarCanal(G_MASK, G_MASK_I, canal*8, toPixels(img),val);
			break;
		case CANAL_AZUL:
			pixels = cambiarCanal(B_MASK, B_MASK_I, canal*8, toPixels(img),val);
			break;
		default:
			throw(errorCanal);
		}
		return Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(ancho, alto, pixels, 0, ancho));
	}

	private static int[] cambiarCanal(int MASK, int MASK_I, int  desp, int[] img, int val){
		val = (val << desp)& MASK;
		for (int i = 0, t= img.length; i< t; i++)
			img[i] = val | (img[i] & MASK_I);
		return img;
	}

	public static Image cambiarCanal(int canal, Image img, Image nuevoCanal){
		int ancho = img.getWidth(null);
		int alto  = img.getHeight(null);
		int pixels[];
		switch(canal){
		case CANAL_ALFA:
			pixels = cambiarCanal(A_MASK, A_MASK_I, canal*8, toPixels(img), toGrayPixels(nuevoCanal,ancho,alto));
			break;
		case CANAL_ROJO:
			pixels = cambiarCanal(R_MASK, R_MASK_I, canal*8, toPixels(img), toGrayPixels(nuevoCanal,ancho,alto));
			break;
		case CANAL_VERDE:
			pixels = cambiarCanal(G_MASK, G_MASK_I, canal*8, toPixels(img), toGrayPixels(nuevoCanal,ancho,alto));
			break;
		case CANAL_AZUL:
			pixels = cambiarCanal(B_MASK, B_MASK_I, canal*8, toPixels(img), toGrayPixels(nuevoCanal,ancho,alto));
			break;
		default:
			throw(errorCanal);
		}
		return Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(ancho, alto, pixels, 0, ancho));
	}

	private static int[] cambiarCanal(int MASK, int MASK_I, int  desp, int[] img1, int[] img2){
		for (int i = 0, t= img1.length; i< t; i++)
			img1[i] = (img2[i] << desp)& MASK | (img1[i] & MASK_I);
		return img1;
	}

	public static int[] toPixels(Image img){
		int ancho = img.getWidth(null);
		int alto  = img.getHeight(null);
		int[] pix = new int[ancho * alto];
	
		PixelGrabber pgObj = new PixelGrabber(img, 0, 0, ancho, alto, pix, 0, ancho);
		try {
			pgObj.grabPixels();
		}catch( InterruptedException e ) {
			pix = null;
		}
		return pix;
	}

	public static int[] toPixels(Image img, int ancho, int alto){
		if (ancho != img.getWidth(null) || alto != img.getHeight(null)){
			BufferedImage bi = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.drawImage(img,0,0,ancho,alto,null);
			return toPixels(bi);
		}
		return toPixels(img);
	}

	public static Image toImage(int[] img, int ancho, int alto){
		return Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(ancho, alto, img, 0,ancho));
	}

	final private static float[] blurMatrix = {
		2.0f/159,  4.0f/159,  5.0f/159,  4.0f/159, 2.0f/159,
		4.0f/159,  9.0f/159, 12.0f/159,  9.0f/159, 4.0f/159,
		5.0f/159, 12.0f/159, 15.0f/159, 12.0f/159, 5.0f/159,
		4.0f/159,  9.0f/159, 12.0f/159,  9.0f/159, 4.0f/159,
		2.0f/159,  4.0f/159,  5.0f/159,  4.0f/159, 2.0f/159
	};

	final public static ConvolveOp BLUR_FILTER = new ConvolveOp(
			new Kernel(5, 5, blurMatrix),
			ConvolveOp.EDGE_NO_OP, null
	);
	
	// pre-computations for conversion from RGB to Luminance
	static int[][] luminanceData = new int[3][256];
	static{
		for (int i = 0; i < 256; i++)
			luminanceData[0][i] = (int)(0.299f*i +.5f);
		for (int i = 0; i < 256; i++)
			luminanceData[1][i] = (int)(0.587f*i +.5f);
		for (int i = 0; i < 256; i++)
			luminanceData[2][i] = (int)(0.114f*i +.5f);
	}

	private static byte rgb2luminance(int pixel){
		int r = pixel >> 16 & BYTE;
		int g = pixel >> 8  & BYTE;
		int b = pixel       & BYTE;

		// compute the luminance
		return  (byte)(luminanceData[0][r] + luminanceData[1][g] + luminanceData[2][b]);
	}

	private static void grayPass(int[] pixels) {
		int r, g, b;
		int luminance;

		for (int index = 0,len = pixels.length; index<len; index++){
			int pixel = pixels[index];

			r = pixel >> 16 & BYTE;
			g = pixel >> 8  & BYTE;
			b = pixel       & BYTE;

			// compute the luminance
			luminance = luminanceData[0][r] + luminanceData[1][g] + luminanceData[2][b];

			pixels[index] = (pixel & A_MASK) | (luminance<<16) | (luminance<<8) | luminance;
		}
	}

	public static int[] toGrayPixels(Image img){
		int pixels[] = toPixels(img);
		grayPass(pixels);
		return pixels;
	}

	public static int[] toGrayPixels(Image img, int ancho, int alto){
		int pixels[] = toPixels(img,ancho,alto);
		grayPass(pixels);
		return pixels;
	}

	private static void brightPass(int[] pixels, int min, int max) {
		int r, g, b;
		int luminance;

		for (int index = 0,len = pixels.length; index<len; index++){
			int pixel = pixels[index];

			r = pixel >> 16 & BYTE;
			g = pixel >> 8  & BYTE;
			b = pixel       & BYTE;

			// compute the luminance
			luminance = luminanceData[0][r] + luminanceData[1][g] + luminanceData[2][b];

			if(luminance < min)
				pixel &= A_MASK;
			else if(luminance > max)
				pixel |= A_MASK_I;

			pixels[index] = pixel;
		}
	}

	public static Image brightPass(Image image, float min, float max){
		if(min < 0.0f || min > 1.0f)
			throw errorRangos;
		if(max < 0.0f || max > 1.0f)
			throw errorRangos;
		if(min > max)
			throw errorMinMayorMax;

		int pixels[] = toPixels(image);
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		brightPass(pixels, (int)(min * 255),(int)(max * 255));
		return toImage(pixels,w,h);
	}

	public static Buffer toBuffer(Image img, boolean flipY){
		Raster     rt = toBufferedImage(img).getRaster();
		DataBuffer db = rt.getDataBuffer();
		int type = db.getDataType();
		Buffer buff = null;
		switch(type){
		case DataBuffer.TYPE_BYTE:
			ByteBuffer bb = ByteBuffer.allocateDirect(db.getSize());
			DataBufferByte dbb = (DataBufferByte)db;
			if(!flipY)
				for(int i = 0, len = db.getNumBanks(); i< len; i++)
					bb.put(dbb.getData(i));
			else if(db.getNumBanks() == rt.getHeight())
				for(int i = db.getNumBanks(); i > 0; )
					bb.put(dbb.getData(--i));
			else if(db.getNumBanks() == 1)
				for(int i = (rt.getHeight()-1)*rt.getWidth(); i >= 0; i-= rt.getWidth())
					bb.put(dbb.getData(), i, rt.getWidth());
			else{
				//TODO lo dificil.
			}
			buff = bb;
			break;
		case DataBuffer.TYPE_INT:
			IntBuffer ib = ByteBuffer.allocateDirect(db.getSize()*4).order(ByteOrder.nativeOrder()).asIntBuffer();
			DataBufferInt dbi = (DataBufferInt)db;
			if(!flipY)
				for(int i = 0, len = db.getNumBanks(); i< len; i++)
					ib.put(dbi.getData(i));
			else if(db.getNumBanks() == rt.getHeight())
				for(int i = db.getNumBanks(); i > 0; )
					ib.put(dbi.getData(--i));
			else if(db.getNumBanks() == 1)
				for(int i = (rt.getHeight()-1)*rt.getWidth(); i >= 0; i-= rt.getWidth())
					ib.put(dbi.getData(), i, rt.getWidth());
			else{
				//TODO lo dificil.
			}
			buff = ib;
			break;
		}
		buff.rewind();
		return buff;
	}

	public static ByteBuffer toByteBuffer(Image img, boolean flipY){
		Raster     rt = toBufferedImage(img).getRaster();
		DataBuffer db = rt.getDataBuffer();
		int type = db.getDataType();

		ByteBuffer bb=null;

		switch(type){

		case DataBuffer.TYPE_BYTE:
			bb = ByteBuffer.allocateDirect(db.getSize());
			DataBufferByte dbb = (DataBufferByte)db;
			if(!flipY)
				for(int i = 0, len = db.getNumBanks(); i< len; i++)
					bb.put(dbb.getData(i));
			else if(db.getNumBanks() == rt.getHeight())
				for(int i = db.getNumBanks(); i > 0; )
					bb.put(dbb.getData(--i));
			else if(db.getNumBanks() == 1)
				for(int i = (rt.getHeight()-1)*rt.getWidth(); i >= 0; i-= rt.getWidth())
					bb.put(dbb.getData(), i, rt.getWidth());
			else{
				//TODO lo dificil.
			}
			break;
		case DataBuffer.TYPE_INT:
			bb = ByteBuffer.allocateDirect(db.getSize());
			DataBufferInt dbi = (DataBufferInt)db;
			if(!flipY)
				for(int i = 0, len = db.getNumBanks(); i< len; i++)
					for(int rgb: dbi.getData(i))
						bb.put(rgb2luminance(rgb));
			else if(db.getNumBanks() == rt.getHeight())
				for(int i = db.getNumBanks(); i > 0; )
					for(int rgb: dbi.getData(--i))
						bb.put(rgb2luminance(rgb));
			else if(db.getNumBanks() == 1)
				for(int i = (rt.getHeight()-1)*rt.getWidth(); i >= 0; i-= rt.getWidth()){
					int dataBank[] = dbi.getData();
					for(int j=i, k =0; k < rt.getWidth(); j++, k++)
						bb.put(rgb2luminance(dataBank[j]));
				}
			else{
				//TODO lo dificil.
			}
			break;
		}
		bb.rewind();
		return bb;
	}

	public static BufferedImage toBufferedImage(Image img){
		if(img instanceof BufferedImage)
			return (BufferedImage) img;
		return toBufferedImage(img, img.getWidth(null), img.getHeight(null));
	}
	
	public static BufferedImage toBufferedImage(Image img, int w, int h){
		BufferedImage bi;
		try{
			// Creando una imagen compatible con el sistema grafico en uso
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();

			bi = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
		} catch (HeadlessException e2) {
			// No se ha podido obtener el sistema grafico en uso
			bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		}
		Graphics2D g = bi.createGraphics();
		g.drawImage(img,0,0,w,h,null);
		return bi;
	}
	
	public static BufferedImage toBufferedImage(Image img, int type){
		BufferedImage bi = null;
		if(img instanceof BufferedImage){
			bi = (BufferedImage)img;
			if(bi.getType() == type)
				return bi;
		}
		bi = new BufferedImage(img.getWidth(null), img.getHeight(null), type);
		Graphics2D g = bi.createGraphics();
		g.drawImage(img,0,0,null);
		g.dispose();
		return bi;
	}
	
	public static BufferedImage toBufferedImage(Image img, int width, int height, int type){
		BufferedImage bi = null;
		if(img instanceof BufferedImage){
			bi = (BufferedImage)img;
			if(bi.getWidth() == width && bi.getHeight() == height && bi.getType() == type)
				return bi;
		}
		bi = new BufferedImage(width, height, type);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage( img, 0, 0, width, height, null );
		g.dispose();
		return bi;
	}
	
	public static TexturePaint toTexturePaint(Image img){
		BufferedImage bi = toBufferedImage(img);
		Rectangle r = new Rectangle(0,0,bi.getWidth(),bi.getHeight());
		return new TexturePaint(bi,r);
	}

	private static int mediaTrackerID;

	@SuppressWarnings("serial")
	private final static MediaTracker tracker = new MediaTracker(new Component() {});

	public static Image cargarImagen(String localizacion){
		Image image = Toolkit.getDefaultToolkit().getImage(localizacion);
		if (image != null)
			try {
				loadImage(image);
			} catch (InterruptedException errorCargandoImagen) {
				image = null;
			}
		return image;
	}

	public static Image cargarImagen(java.net.URL localizacion){
		Image image = Toolkit.getDefaultToolkit().getImage(localizacion);
		if (image != null)
			try {
				loadImage(image);
			} catch (InterruptedException errorCargandoImagen) {
				image = null;
			}
		return image;
	}

	private static void loadImage(Image image) throws InterruptedException{
		synchronized(tracker) {
			int id = getNextID();
			tracker.addImage(image, id);
			tracker.waitForID(id, 0);
			tracker.removeImage(image, id);
		}
	}

	private static int getNextID() {
		synchronized(tracker) {
			return ++mediaTrackerID;
		}
	}

	/*
	public static Image getRecorte(Image img, int x, int y, int ancho, int alto){
		return Toolkit.getDefaultToolkit().createImage(
				new FilteredImageSource (img.getSource(),
					new CropImageFilter (x, y, ancho, alto)));
	}*/

	public static Image getRecorte(Image img, int x, int y, int ancho, int alto){
		int tipo;
		if( img instanceof BufferedImage )
			tipo = ((BufferedImage) img).getType();
		else
			tipo = BufferedImage.TYPE_INT_ARGB;
		BufferedImage bi = new BufferedImage(ancho, alto, tipo);
		Graphics2D g = bi.createGraphics();
		g.drawImage(img, 0, 0, ancho, alto, x, y, x + ancho, y + alto, null);
		return bi;
	}
}