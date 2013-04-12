package com.example.smsread;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

public class TextImageButton extends ImageButton{
	private String text = null;  //要显示的文字
    private int color;               //文字的颜色
    Paint paint;
    private static final String TAG = "TextImageButton";
    public TextImageButton(Context context, AttributeSet attrs) {
        super(context,attrs);
        //paint=new Paint();
        Log.d(TAG, "Create OK");
    }
     
    public void setText(String text){
        this.text = text;       //设置文字
    }
     
    public void setColor(int color){
        this.color = color;    //设置文字颜色
    }
     
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");
        /*paint=new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(color);
        canvas.drawText(text, 15, 20, paint);  //绘制文字*/
    }
}
