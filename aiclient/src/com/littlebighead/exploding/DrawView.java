package com.littlebighead.exploding;


import java.util.ArrayList;
import java.util.List;

//import Body;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class DrawView extends View implements OnTouchListener {
    private static final String TAG = "DrawView";
    
    public Body body;
    //private Limb currLimb = null;
    private int canvW;
    private int canvH;
    //private Canvas canvas;

    List<Point> points = new ArrayList<Point>();
    Paint paint = new Paint();

    public DrawView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);

        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
 
        body = new Body();

    }

    @Override
    public void onDraw(Canvas canvas) {
    	this.canvW = canvas.getWidth()/2;
    	this.canvH = canvas.getWidth()/2;
        canvas.translate(canvW,canvH);
        body.draw(canvas);
        if (Limb.curr != null) {
        	body.drawLimbs(canvas);
        } else if (Limb.prev != null) {
        	Limb.prev.draw(canvas);
        }
//    	body = new Body(canvas);
    	//}
    	/*
        for (Point point : points) {
            canvas.drawCircle(point.x, point.y, 5, paint);
            // Log.d(TAG, "Painting: "+point);
        }
        */
    }

    public boolean onTouch(View view, MotionEvent event) {
        switch(event.getAction()) {
        case MotionEvent.ACTION_DOWN:
	        	for (Limb l: Body.limbs) {
	        		if (Math.abs((l.x+canvW)-event.getX()) > 50d) continue;
	        		if (Math.abs((l.y+canvH)-event.getY()) > 50d) continue;
        			Limb.curr = l;
        			break;
	        	}
	        	break;
        	case MotionEvent.ACTION_MOVE:
        		if (Limb.curr != null) {
        			if (event.getX()-Limb.curr.xradius > 0) {
        				if (event.getX()+Limb.curr.xradius < view.getWidth()) {
        					Limb.curr.x = event.getX()-canvW;
        				}
        			}
        			if (event.getY()-Limb.curr.yradius > 0) {
        				if (event.getY()+Limb.curr.yradius < view.getHeight()) {
        					Limb.curr.y = event.getY()-canvH;
        				}
        			}
        		} /*else {
    	        	for (Limb l: body.limbs) {
    	        		if (Math.abs((l.x+canvW)-event.getX()) > 50d) continue;
    	        		if (Math.abs((l.y+canvH)-event.getY()) > 50d) continue;
            			currLimb = l;
            			break;
    	        	}
        		}*/
        		invalidate();
        		break;
        		
        	case MotionEvent.ACTION_UP:
        		Limb.prev = Limb.curr;
        		Limb.curr = null;
        		invalidate();
        		break;
        }
        		
        // return super.onTouchEvent(event);
        /*
        Point point = new Point();
        point.x = event.getX();
        point.y = event.getY();
        points.add(point);
        invalidate();
        Log.d(TAG, "point: " + point);
        */
        return true;
    }
    
    
    public boolean onTouchEvent(MotionEvent me) {  		
    	return true;    	
    }
}

class Point {
    float x, y;

    @Override
    public String toString() {
        return x + ", " + y;
    }
}