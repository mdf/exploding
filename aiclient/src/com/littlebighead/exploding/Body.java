package com.littlebighead.exploding;

//import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public class Body {
	final private static double piDiv180 = Math.PI / 180.0;
	
	public static Limb limbs[];
	final private static int limbCnt = 5;
	private Canvas canvas;
	public static Bitmap bitmap;
	
	public Body() {
		limbs = new Limb[limbCnt];
		
		double width = Math.random() * 100.0d + 55.0d;
		double height = Math.random() * 100.0d + 60.0d;

		for (int f = 0; f < limbCnt; f++) {
			double ang = (f / (double)limbCnt) * 360.0d;
			
			double rad = 20;
			limbs[f] = new Limb(Math.sin(ang * piDiv180) * width, Math.cos(ang * piDiv180) * -height, rad, rad, f==0);
			if (f == 0) { limbs[f].xradius *= 2; limbs[f].yradius *= 2; limbs[f].y -= 20; }
			//if (f==2 || f==3) limbs[f].yradius *= 0.3;
		}
	}
	
	public void drawLimbs(Canvas canvas) {
		for (Limb l: limbs) {
			l.draw(canvas);
		}
        ShapeDrawable mDrawable = new ShapeDrawable(new OvalShape());
        mDrawable.getPaint().setColor(0x7fff0000);
        mDrawable.getPaint().setStyle(Style.STROKE);
        mDrawable.setBounds(-5, -5,5, 5);
        mDrawable.draw(canvas);
		
	}
		
	public void draw(Canvas canvas) {
		this.canvas = canvas;
		canvas.drawColor(0xffffffff);

		//drawCurves(false);
		drawBaseCurves(0x0000ff, 255);
		drawCurves(true);
		limbs[0].drawEyes(canvas);
	}
		
	public void drawBaseCurves(int col, int alp) {
		Limb prevLimb = null;
		Path path = new Path();

		for (int f = 0; f < limbCnt; f++) {
			if (prevLimb != null) {
				float cx = (float)(prevLimb.x+limbs[f].x) * 0.3333f;
				float cy = (float)(prevLimb.y+limbs[f].y) * 0.3333f;
				path.quadTo(cx,cy, (float)limbs[f].x, (float)limbs[f].y);
			} else {
				path.moveTo((float)limbs[f].x, (float)limbs[f].y);
			}
			prevLimb = limbs[f];
		}
		float cx = (float)(prevLimb.x+limbs[0].x) * 0.3333f;
		float cy = (float)(prevLimb.y+limbs[0].y) * 0.3333f;
		path.quadTo(cx,cy, (float)limbs[0].x, (float)limbs[0].y);
		Paint paint = new Paint();
		paint.setColor(col);
		paint.setAlpha(alp);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawPath(path, paint);
		
	}
	
	public void drawCurves(boolean fill) {
		Limb prevLimb = null;
		for (int f = 0; f < limbCnt; f++) {
			if (prevLimb != null) {
				double cx = (prevLimb.x+limbs[f].x) * 0.3333d;
				double cy = (prevLimb.y+limbs[f].y) * 0.3333d;
				myCurveTo(prevLimb.x, prevLimb.y, prevLimb.xradius, prevLimb.yradius, limbs[f].x, limbs[f].y, limbs[f].xradius, limbs[f].yradius, fill, cx,cy);
			}
			prevLimb = limbs[f];
		}
		double cx = (prevLimb.x+limbs[0].x) * 0.3333d;
		double cy = (prevLimb.y+limbs[0].y) * 0.3333d;
		myCurveTo(prevLimb.x, prevLimb.y, prevLimb.xradius, prevLimb.yradius, limbs[0].x, limbs[0].y, limbs[0].xradius, limbs[0].yradius, fill, cx,cy);
	}
	

	
//	private void myCurveTo(float x0, float y0, rad0:Number, x1:Number, y1:Number, rad1:Number, fill:Boolean = false, cx:Number=0, cy:Number=0) {
	private void myCurveTo(double x0, double y0, double xrad0, double yrad0, double x1, double y1, double xrad1, double yrad1, boolean fill, double cx, double cy) {
		PointF p0 = new PointF((float)x0, (float)y0);
		PointF p1 = new PointF((float)x1, (float)y1);
		PointF c = new PointF((float)cx, (float)cy);
		
		double xrad, yrad;
		PointF newPnt = new PointF();
		for (float mu = 0.0f; mu <= 1.0001f; mu += 0.02f) {
			curve3b(p0, c, p1, mu, newPnt);
			xrad = xrad0 + ((xrad1 - xrad0) * mu);
			yrad = yrad0 + ((yrad1 - yrad0) * mu);
		
	        ShapeDrawable mDrawable = new ShapeDrawable(new OvalShape());
	        Paint paint = mDrawable.getPaint();
			//if (fill) {
				paint.setColor(0xff0000ff);
				paint.setStyle(Paint.Style.FILL);
			//} else {
		        //paint.setStrokeWidth(8);
		    //    paint.setColor(0xff000000);
			//	paint.setStyle(Paint.Style.STROKE);	
			//}
	        Limb.setCircle(mDrawable, newPnt.x, newPnt.y, xrad);
	       // Limb.setOval(mDrawable, newPnt.x, newPnt.y, xrad, yrad);
	        mDrawable.draw(canvas);	//draws mDrawable to canvas			
		}
	}
	
	
    private static PointF curve3b(PointF p1, PointF p2, PointF p3, float mu, PointF newPnt) {
        //mu = Math.max(Math.min(t, 1), 0);
        float tp = 1 - mu;
        float t2 = mu * mu;
        float tp2 = tp * tp;
        newPnt.x = (tp2*p1.x) + (2*tp*mu*p2.x) + (t2*p3.x);
        newPnt.y = (tp2*p1.y) + (2*tp*mu*p2.y) + (t2*p3.y);
        return newPnt;    
    }
}
