package com.littlebighead.exploding;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public class Limb {	//extends uk.ac.horizon.ug.exploding.client.model.Limb {
	
	private Canvas canvas;
	public double xradius;
	public double yradius;
	public double x;
	public double y;
	
	public static Limb curr = null;
	public static Limb prev = null;
	
	
	public Limb(double x, double y, double xradius, double yradius, boolean head) {
		this.x = x;
		this.y = y;
		this.xradius = xradius;
		this.yradius = yradius;
	}
	
	
	public void draw(Canvas canvas) {
        ShapeDrawable mDrawable = new ShapeDrawable(new OvalShape());
        Paint paint = mDrawable.getPaint();
        paint.setColor(0xffff0000);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(2);
        mDrawable.setBounds((int)(x-xradius), (int)(y-yradius), (int)(x + xradius*2.0d), (int)(y + yradius*2.0d));
//        mDrawable.setBounds((int)(x), (int)(y), (int)(xradius), (int)(yradius));
        mDrawable.draw(canvas);	//draws mDrawable to canvas
        
 /*       
		Graphics gfx = graphics;
		gfx.clear();
		gfx.beginFill(0xff0000,0.5);
		gfx.drawCircle(0, 0, radius);
		gfx.endFill(); */
	}
	
	
	public void drawEyes(Canvas canvas) {
        ShapeDrawable mDrawable = new ShapeDrawable(new OvalShape());
        mDrawable.getPaint().setColor(0xffffffff);
        
        double ypos = y-(Math.random()*xradius-(xradius*0.5d));
//        setOval(mDrawable, x-(xradius*0.3d), ypos, xradius*0.1d, xradius*0.2d);
        setCircle(mDrawable, x-(xradius*0.3d), ypos, xradius*0.1d);
        setCircle(mDrawable, x-(xradius*0.3d), ypos, xradius*0.1d);
        mDrawable.draw(canvas);	//draws mDrawable to canvas

//        setOval(mDrawable, x+(xradius*0.3d), ypos, xradius*0.1d, xradius*0.2d);
        setCircle(mDrawable, x+(xradius*0.3d), ypos+xradius*0.1d, xradius*0.1d);
        setCircle(mDrawable, x+(xradius*0.3d), ypos+xradius*0.1d, xradius*0.1d);
//        mDrawable.setBounds(x+radius, y-radius, x + radius*2, y + radius*2);
        mDrawable.draw(canvas);	//draws mDrawable to canvas

//		gfx.drawCircle(-radius*0.5, Math.random()*radius, radius*(Math.random()*0.4+0.3));
//		gfx.drawCircle(radius*0.5, Math.random()*radius, radius*(Math.random()*0.4+0.3));

	}
	
	public static void setCircle(ShapeDrawable drawable, double x, double y, double r) {
//		drawable.setBounds((int)(x-r), (int)(y-r), (int)(x+r*2d), (int)(y+r*2d));
		drawable.setBounds((int)(x-r), (int)(y-r), (int)(x+r*2d), (int)(y+r*2d));
	}

	public static void setOval(ShapeDrawable drawable, double x, double y, double xr, double yr) {
		drawable.setBounds((int)(x-xr), (int)(y-yr), (int)(x+xr*2d), (int)(y+yr*2d));
	}

}
