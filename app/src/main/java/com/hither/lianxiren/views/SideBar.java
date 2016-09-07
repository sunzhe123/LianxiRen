package com.hither.lianxiren.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hither.lianxiren.R;

public class SideBar extends View {
    // touching event
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    // 26 letters
    public static String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    // if choosed
    private int choose = -1;
    private Paint paint = new Paint();

    private TextView mTextDialog;

    public void setmTextDialog(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context) {
        super(context);
    }

    // override onDraw function
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // get the height
        int height = getHeight();
        // get the width
        int width = getWidth();
        // get one letter height
        int singleHeight = height / b.length;

        for (int i = 0; i < b.length; i++) {
            // 设置画笔(字体)颜色
            paint.setColor(Color.rgb(33, 65, 98));
            // 加粗字体
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            // 设置抗锯齿(边缘的锯齿)
            paint.setAntiAlias(true);
            // 设置画笔字体的大小
            paint.setTextSize(20);

            // if choosed
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true);// 设置字体是否为为粗体
            }

            // draw text
            // paint.measureText-->得到文字的宽度
            // (个人理解) 减掉之后就是文字距边框的距离
            float x = width / 2 - paint.measureText(b[i]) / 2;
            // 当前要画的文字的高度,也就是所在的位置
            float y = singleHeight * i + singleHeight;
            canvas.drawText(b[i], x, y, paint);
            // 清空笔刷
            paint.reset();
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY(); // get the Y
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener changedListener = onTouchingLetterChangedListener;
        final int letterPos = (int) (y / getHeight() * b.length);

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;
                // 请求重新draw()
                invalidate();

                // 大块字母view显示
                if (mTextDialog != null)
                    mTextDialog.setVisibility(View.INVISIBLE);
                break;

            default:
                setBackgroundResource(R.drawable.bg_sidebar);
                if (oldChoose != letterPos) {
                    if (letterPos >= 0 && letterPos < b.length) {
                        if (changedListener != null)
                            changedListener.onTouchingLetterChanged(b[letterPos]);
                        if (mTextDialog != null) {
                            mTextDialog.setText(b[letterPos]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        choose = letterPos;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener changedListener) {
        this.onTouchingLetterChangedListener = changedListener;
    }

    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String str);
    }
}
