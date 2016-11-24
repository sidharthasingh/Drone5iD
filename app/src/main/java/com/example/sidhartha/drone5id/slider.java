package com.example.sidhartha.drone5id;

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Paint;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.ViewGroup.LayoutParams;

public class slider {
    public int STICK_ALPHA = 200;
    public int LAYOUT_ALPHA = 200;
    public int OFFSET = 0;
    public float thrust=0;

    public Context mContext;
    public ViewGroup mLayout;
    public LayoutParams params;
    public int stick_width, stick_height,indicator_height,indicator_width;
    public int position_x = 0, position_y = 0;
    public DrawCanvas draw,draw1;
    public Paint paint,paint1;
    public Bitmap stick,indicator;

    public boolean touch_state = false;

    public slider (Context context, ViewGroup layout, int stick_res_id)
    {
        thrust=0;
        mContext = context;

        stick = BitmapFactory.decodeResource(mContext.getResources(),
                stick_res_id);
        indicator = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.thrustindicator);

        stick_width = stick.getWidth();
        stick_height = stick.getHeight();
        indicator_height=30;
        indicator_width=150;


        draw = new DrawCanvas(mContext);
        draw.setid(0);
        draw1=new DrawCanvas(mContext);
        draw1.setid(1);
        paint1=new Paint();
        paint = new Paint();

        mLayout = layout;
        params = mLayout.getLayoutParams();
        position_x = (params.width*1/3);
        position_y=(int)(params.height-stick_height/2);
        thrust=1-(((float)position_y-(float)stick_height/2))/((float)params.height-(float)stick_height);
        draw.position(position_x, position_y);
        draw();
    }
    public void drawStick(MotionEvent arg1)
    {
        position_x = (params.width*1/3);
        position_y=(int)(arg1.getY());
        if(position_y<stick_height/2)
            position_y=stick_height/2;
        if(position_y>params.height-stick_height/2)
            position_y=params.height-stick_height/2;

        thrust=1-(((float)position_y-(float)stick_height/2))/((float)params.height-(float)stick_height);
        draw.position(position_x,position_y);
        draw1.position(params.width-indicator_width/2,((float)position_y-(float)stick_height/2)*(((float)params.height-(float)indicator_height)/((float)params.height-(float)stick_height))+(float)indicator_height/2);
        draw();
        touch_state=true;
        if(arg1.getAction() == MotionEvent.ACTION_MOVE && touch_state)
        {
            if(position_y<stick_height/2)
                position_y =stick_height/2;
            if(position_y>params.height-stick_height/2)
                position_y=params.height-stick_height/2;
            thrust=1-(((float)position_y-(float)stick_height/2))/((float)params.height-(float)stick_height);
            draw.position(position_x, position_y);
            draw1.position(params.width-indicator_width/2,((float)position_y-(float)stick_height/2)*(((float)params.height-(float)indicator_height)/((float)params.height-(float)stick_height))+(float)indicator_height/2);
            draw();
        }
        else if(arg1.getAction() == MotionEvent.ACTION_UP)
        {
            touch_state = false;
        }
    }

    public void setOffset(int offset) {
        OFFSET = offset;
    }

    public void setStickAlpha(int alpha) {
        STICK_ALPHA = alpha;
        paint.setAlpha(alpha);
    }


    public void setLayoutAlpha(int alpha) {
        LAYOUT_ALPHA = alpha;
        mLayout.getBackground().setAlpha(alpha);
    }

    public void setStickSize(int width, int height) {
        stick = Bitmap.createScaledBitmap(stick, width, height, false);
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();
    }
    public void setIndicatorSize(int width, int height) {
        indicator = Bitmap.createScaledBitmap(indicator, width, height, false);
        indicator_width = indicator.getWidth();
        indicator_height = indicator.getHeight();
    }

    public void setThrust(int x)
    {
        thrust=x;
        position_x = (params.width*1/3);
        position_y=(int)(params.height-stick_height/2)-x;
        draw.position(position_x, position_y);
        draw1.position(params.width-indicator_width/2,((float)position_y-(float)stick_height/2)*(((float)params.height-(float)indicator_height)/((float)params.height-(float)stick_height))+(float)indicator_height/2);
        draw();
    }

    public void draw() {
        try
        {
            mLayout.removeView(draw);
            mLayout.removeView(draw1);
        }
        catch (Exception e)
        { }
        mLayout.addView(draw);
        mLayout.addView(draw1);
    }

    public void setLayoutSize(int width, int height) {
        params.width = width;
        params.height = height;
    }

    public class DrawCanvas extends View
    {
        float x, y,id;

        public DrawCanvas(Context mContext)
        {
            super(mContext);
        }

        public void onDraw(Canvas canvas)
        {
            if(id==0)
                canvas.drawBitmap(stick, x, y, paint);
            else
                canvas.drawBitmap(indicator,x,y,paint1);
        }

        public void position(float pos_x, float pos_y)
        {
            if(id==0) {
                x = pos_x - (stick_width / 2);
                y = pos_y - (stick_height / 2);
            }
            else
            {
                x=pos_x-indicator_width/2;
                y=pos_y-indicator_height/2;
            }
        }
        public void setid(int x)
        {
            id=x;
        }
    }
}