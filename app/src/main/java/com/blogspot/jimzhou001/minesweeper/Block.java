package com.blogspot.jimzhou001.minesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class Block extends ImageView {

    public boolean isClickable;
    public boolean hasMine;
    public boolean isFlaggedMine;
    public boolean isFlaggedPuzzled;
    public int numberOfMinesInSurrounding;

    public Block(Context context) {
        super(context);
    }

    public Block(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Block(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Block(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void show() {
        if (isFlaggedMine) {
            setImageDrawable(getResources().getDrawable(R.drawable.flag));
        } else if (isFlaggedPuzzled) {
            setImageDrawable(getResources().getDrawable(R.drawable.puzzled));
        } else if (!isClickable) {
            setBackgroundColor(Color.GRAY);
            if (hasMine) {
                setImageDrawable(getResources().getDrawable(R.drawable.mineblock));
            } else {
                switch (numberOfMinesInSurrounding) {
                    case 1:
                        setImageDrawable(getResources().getDrawable(R.drawable.num1));
                        break;
                    case 2:
                        setImageDrawable(getResources().getDrawable(R.drawable.num2));
                        break;
                    case 3:
                        setImageDrawable(getResources().getDrawable(R.drawable.num3));
                        break;
                    case 4:
                        setImageDrawable(getResources().getDrawable(R.drawable.num4));
                        break;
                    case 5:
                        setImageDrawable(getResources().getDrawable(R.drawable.num5));
                        break;
                    case 6:
                        setImageDrawable(getResources().getDrawable(R.drawable.num6));
                        break;
                    case 7:
                        setImageDrawable(getResources().getDrawable(R.drawable.num7));
                        break;
                    case 8:
                        setImageDrawable(getResources().getDrawable(R.drawable.num8));
                        break;
                    default:
                }
            }
        } else {
            setImageDrawable(null);
            setBackgroundColor(Color.DKGRAY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect=canvas.getClipBounds();
        --rect.bottom;
        --rect.right;
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawRect(rect, paint);
    }

}
