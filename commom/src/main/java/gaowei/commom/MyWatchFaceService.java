package gaowei.commom;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.TextPaint;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;

import java.lang.Override;import java.lang.String;import java.lang.System;

/**
 * Created by David on 2/5/2015.
 */
public class MyWatchFaceService extends CanvasWatchFaceService {

    private static final String TAG = MyWatchFaceService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }


    private final class Engine extends CanvasWatchFaceService.Engine {

        Time mTime;
        int width;
        int height;
        float midX;
        float midY;

        Paint mBackgroundPaint;

        Paint mHourDiskPaint;
        Paint mHourDigitPaint;
        int hourDiskColor1 = 0xff66dbf0;
        int hourDiskColor2 = 0xff0c83ce;
        int hourTextColor = Color.WHITE;

        Paint mMinDiskPaint;
        Paint mMinDigitPaint;
        int minDiskColor1 = 0xff5649cd;
        int minDiskColor2 = 0xff1e0cce;
        int minTextColor = Color.WHITE;

        static final int MSG_UPDATE_TIME = 0;
        /** Alpha value for drawing time when not in mute mode. */
        static final int NORMAL_ALPHA = 255;
        private final long NORMAL_UPDATE_RATE_MS = 1000;

        /** How often {@link #mUpdateTimeHandler} ticks in milliseconds. */
        long mInteractiveUpdateRateMs = NORMAL_UPDATE_RATE_MS;
        /** Handler to update the time periodically in interactive mode. */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        if (Log.isLoggable(TAG, Log.VERBOSE)) {
                            Log.v(TAG, "updating time");
                        }
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs =
                                    mInteractiveUpdateRateMs - (timeMs % mInteractiveUpdateRateMs);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFaceService.this)
                    .setStatusBarGravity(Gravity.CENTER).build());
            mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBackgroundPaint.setColor(Color.WHITE);

            mHourDiskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mHourDiskPaint.setStyle(Paint.Style.FILL);
            mHourDigitPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mHourDigitPaint.setTextAlign(Paint.Align.CENTER);
            mHourDigitPaint.setColor(hourTextColor);
            mHourDigitPaint.setTextSize(20);

            mMinDiskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mMinDiskPaint.setStyle(Paint.Style.FILL);
            mMinDigitPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mMinDigitPaint.setTextAlign(Paint.Align.CENTER);
            mMinDigitPaint.setColor(minTextColor);

            mTime = new Time();
        }


        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mTime.setToNow();
            int hour = mTime.hour == 12 ? 12 : mTime.hour % 12;
            int min = mTime.minute;
            int sec = mTime.second;
            int width = bounds.width();
            int height = bounds.height();
            RectF hourRect = new RectF(0, 0, width, height);
            RectF hourTextRect = new RectF(7.5f,7.5f,width-7.5f, height-7.5f);
            Path path = new Path();
            mHourDiskPaint.setColor(hour % 2 == 0 ? hourDiskColor1 : hourDiskColor2);
            int hourToDraw = hour;
            for(int i = 0; i < 12; i++) {
                mHourDiskPaint.setColor(mHourDiskPaint.getColor() == hourDiskColor1 ?
                        hourDiskColor2 : hourDiskColor1);
                canvas.drawArc(hourRect, -105 + i * 30, 30, true, mHourDiskPaint);
                path.reset();
                path.addArc(hourTextRect, -105 + i * 30, 30);
                if (hourToDraw == 13) hourToDraw = 1;
                canvas.drawTextOnPath("" + hourToDraw++, path, 0, 20, mHourDigitPaint);
            }

            int[] colors =  new int[]{hourDiskColor1, hourDiskColor2};
            // draw hour disk
            // draw minute disk
            float minDegree = min * 6;
//            float minDegree = min * 6 + sec / 60f * 6;
            drawUmbrella(canvas, 30, colors, minDegree, mMinDiskPaint);
            RectF minTextRect = new RectF(37.5f, 37.5f, width-37.5f, height-37.5f);
            path.reset();
            path.addArc(minTextRect, -105, 30);
            canvas.drawTextOnPath("" + min, path, 0, 20, mHourDigitPaint);
            // draw second disk
//            float secondDegree = sec * 6 + System.currentTimeMillis() % 1000 /1000f * 6;
            float secondDegree = sec * 6;
            drawUmbrella(canvas, 60, colors, secondDegree, mMinDiskPaint);
            RectF secTextRect = new RectF(67.5f, 67.5f, width-67.5f, height-67.5f);
            path.reset();
            path.addArc(secTextRect, -105, 30);
            canvas.drawTextOnPath("" + sec, path, 0, 20, mHourDigitPaint);
            // draw fast disk
//            float d3 = 60*(sec * 6 + System.currentTimeMillis() % 1000 /1000f * 6);
//            drawRainbow(canvas, 90, colors , d3,mMinDiskPaint);


        }

        private void drawUmbrella(Canvas canvas, float margin,  int[] colors, float startingAngle, Paint paint) {
            startingAngle %= 360;
            RectF hourRect = new RectF(margin,margin,width-margin, width-margin);
            int amount = colors.length;
            for(int i = 0; i < 12; i++) {
                paint.setColor(colors[i%amount]);
                canvas.drawArc(hourRect, startingAngle + i * 30, 30, true, paint);
            }

        }

        private void drawRainbow(Canvas canvas, float margin, int[] colors,float startingAngle, Paint paint) {
            RectF hourRect = new RectF(margin,margin,width-margin, width-margin);
            SweepGradient gradient = new SweepGradient(midX, midY, colors[0],colors[1]);
            paint.setShader(gradient);
            canvas.rotate(startingAngle,midX,midY);
            canvas.drawArc(hourRect, 0, 360, true, paint);
//            canvas.rotate(startingAngle,midX,midY);
            paint.setShader(null);
        }



        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {

            super.onVisibilityChanged(visible);
            updateTimer();
        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            this.width = width;
            this.height = height;
            midX = width/2;
            midY = height/2;
        }

        private boolean shouldTimerBeRunning() {
            return true;
        }
}
}
