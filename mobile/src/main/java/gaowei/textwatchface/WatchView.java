package gaowei.textwatchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by David on 2/12/2015.
 */
public class WatchView extends View {
    int width;
    int height;
    int midX;
    int midY;
    int mColor1;
    int mColor2;
    Time mTime;
    static final int MSG_UPDATE_TIME = 0;
    private final int NORMAL_UPDATE_RATE_MS = 1000;

    Paint mHourDiskPaint;
    Paint mHourDigitPaint;
    int hourTextColor = Color.WHITE;


    protected int getInteractiveUpdateRateMs() {
        return NORMAL_UPDATE_RATE_MS;
    }

    final Handler mUpdateTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_UPDATE_TIME:
                    invalidate();
                    if (WatchView.this.getVisibility() == VISIBLE) {
                        long timeMs = System.currentTimeMillis();
                        long delayMs =
                                getInteractiveUpdateRateMs() - (timeMs % getInteractiveUpdateRateMs());
                        mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                    }
                    break;
            }
        }
    };


    public WatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mColor1 = 0xffff0000;
        mColor2 = 0xff0000ff;
        mHourDiskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourDiskPaint.setStyle(Paint.Style.FILL);
        mHourDigitPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mHourDigitPaint.setTextAlign(Paint.Align.CENTER);
        mHourDigitPaint.setColor(hourTextColor);
        mTime = new Time();
        updateTimer();
    }

    public void setColors(int color1, int color2) {
        mColor1 = color1;
        mColor2 = color2;
        invalidate();
    }

    public void setColor1(int color) {
        mColor1 = color;
        invalidate();
    }

    public void setColor2(int color){
        mColor2 = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWatch(canvas, new Rect(0,0,width,height));


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        width = parentWidth;
        height = parentHeight;
        midX = width / 2;
        midY = height/2;
        mHourDigitPaint.setTextSize(20*width/320);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void drawWatch(Canvas canvas, Rect bounds) {
        mTime.setToNow();
        int hour = mTime.hour == 12 ? 12 : mTime.hour % 12;
        int min = mTime.minute;
        int sec = mTime.second;
        int width = bounds.width();
        int height = bounds.height();
        RectF hourTextRect = new RectF(7.5f*width/320,7.5f*width/320,width-7.5f*width/320, height-7.5f*width/320);
        float hourDegree = -90 + hour * 30;
        drawRainbow(canvas, 0, hourDegree, mHourDiskPaint);

        Path path = new Path();
        path.addArc(hourTextRect, -105, 30);
        canvas.drawTextOnPath("" + hour, path, 0, 20*width/320, mHourDigitPaint);
        float minDegree = min * 6 + sec / 60f * 6;
        drawRainbow(canvas, 30*width/320, minDegree, mHourDiskPaint);
        RectF minTextRect = new RectF(37.5f*width/320, 37.5f*width/320, width-37.5f*width/320, height-37.5f*width/320);
        path.reset();
        path.addArc(minTextRect, -105, 30);
        canvas.drawTextOnPath("" + min, path, 0, 20*width/320, mHourDigitPaint);
        float secondDegree = -90 + sec * 6;
        drawRainbow(canvas, 60*width/320, secondDegree, mHourDiskPaint);
        RectF secTextRect = new RectF(67.5f*width/320, 67.5f*width/320, width-67.5f*width/320, height-67.5f*width/320);
        path.reset();
        path.addArc(secTextRect, -105, 30);
        canvas.drawTextOnPath("" + sec, path, 0, 20*width/320, mHourDigitPaint);
    }

    private void drawRainbow(Canvas canvas, float margin,float startingAngle, Paint paint) {
        RectF hourRect = new RectF(margin,margin,width-margin, width-margin);
        SweepGradient gradient = new SweepGradient(midX, midY, mColor1,mColor2);
        paint.setShader(gradient);
        canvas.rotate(startingAngle,midX,midY);
        paint.setAntiAlias(true);
        canvas.drawArc(hourRect, 0, 360, true, paint);
        canvas.rotate(-startingAngle,midX,midY);
        paint.setShader(null);
    }

    private void updateTimer() {
        mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
        if (this.getVisibility() == VISIBLE) {
            mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
        }
    }
}
