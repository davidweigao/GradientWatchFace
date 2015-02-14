package gaowei.textwatchface;

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

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;

import java.util.Random;

/**
 * Created by David on 2/10/2015.
 */
public class GradientWatchFaceService extends BaseWatchFaceService{
    private static final String TAG = "GWF";
    static final String KEY_COLOR_1 = "gaowei.watchface.gradientwatchface_color1";
    static final String KEY_COLOR_2 = "gaowei.watchface.gradientwatchface_color2";
    static final String KEY_TIME_STYLE = "gaowei.watchface.gradientwatchface.timestyle";
    static final int DEFAULT_COLOR_1 = 0xff222222;
    static final int DEFAULT_COLOR_2 = 0xff0c83ce;
    static final int DEFAULT_TIME_STYLE = Engine.TIME_STYLE_SUN;




    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private final class Engine extends BaseWatchFaceService.Engine {
        public static final int TIME_STYLE_BOUNCY = 1;
        public static final int TIME_STYLE_SUN = 2;
        public static final int TIME_STYLE_CLOCK = 3;
        Paint mHourDiskPaint;
        Paint mHourDigitPaint;
        int mColor1;
        int mColor2;
        int hourTextColor = Color.WHITE;
        private int currentTimeDigitsArtistType = -1;
        private TimeDigitsArtist mTimeDigitsArtist;

        public void setTimeDigitsArtist(int type) {
            if(currentTimeDigitsArtistType == type) return;
            switch (type) {
                case TIME_STYLE_BOUNCY:
                    mTimeDigitsArtist = new TextTimeDigitsArtist(width, height);
                    break;
                case TIME_STYLE_SUN:
                    mTimeDigitsArtist = new SunTimeDigitsArtist(width, height);
                    break;
                case TIME_STYLE_CLOCK:
                    mTimeDigitsArtist = new ColockTimeDigitsArtist(width, height);
                    break;
            }
        }


        /** Alpha value for drawing time when not in mute mode. */
        static final int NORMAL_ALPHA = 255;
        private final int NORMAL_UPDATE_RATE_MS = 1000;

        @Override
        protected int getInteractiveUpdateRateMs() {
            return NORMAL_UPDATE_RATE_MS;
        }


        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            setWatchFaceStyle(new WatchFaceStyle.Builder(GradientWatchFaceService.this)
                    .setStatusBarGravity(Gravity.BOTTOM)
                    .setHotwordIndicatorGravity(Gravity.BOTTOM).build());

            mHourDiskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mHourDiskPaint.setStyle(Paint.Style.FILL);
            mHourDigitPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mHourDigitPaint.setTextAlign(Paint.Align.CENTER);
            mHourDigitPaint.setColor(hourTextColor);
            mHourDigitPaint.setTextSize(35);

            mTime = new Time();
            mTimeDigitsArtist = new TimeDigitsArtist() {
                @Override
                public void draw(Canvas canvas, int hour, int min, int sec, Paint paint) {
                    // draw nothing, dummy class for initialization
                }
            };
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mTime.setToNow();
            int hour = mTime.hour;
            int min = mTime.minute;
            int sec = mTime.second;
            drawRambows(canvas, hour, min, sec);
            //TODO
            int style = 1;
            mTimeDigitsArtist.draw(canvas, hour,min,sec, mHourDigitPaint);
        }

        private void drawRambows(Canvas canvas, int hour, int min, int sec) {
            float hourDegree = -90 + hour%12 * 30;
            drawRainbow(canvas, 0, hourDegree, mHourDiskPaint);
            float minDegree = -90 + min * 6 + sec / 60f * 6;
            drawRainbow(canvas, 40, minDegree, mHourDiskPaint);
            float secondDegree = -90 + sec * 6;
            drawRainbow(canvas, 80, secondDegree, mHourDiskPaint);
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

        private void setColor1(int color) {
            Log.e(TAG, "setColor1: " + color);
            mColor1 = color;
        }

        private void setColor2(int color) {
            mColor2 = color;
        }

        @Override
        protected boolean updateUiForKey(String configKey, int value) {
            Log.d(TAG, "updateUiForKey");
            if (configKey.equals(KEY_COLOR_1)) {
                setColor1(value);
            } else if (configKey.equals(KEY_COLOR_2)) {
                setColor2(value);
            } else if (configKey.equals(KEY_TIME_STYLE)) {
                setTimeDigitsArtist(value);
            } else {
                Log.w(TAG, "Ignoring unknown config key: " + configKey);
                return false;
            }
            return true;
        }

        @Override
        protected void setDefaultValuesForMissingConfigKeys(DataMap config) {
            addIntKeyIfMissing(config, KEY_COLOR_1, DEFAULT_COLOR_1 );
            addIntKeyIfMissing(config, KEY_COLOR_2, DEFAULT_COLOR_2);
            addIntKeyIfMissing(config, KEY_TIME_STYLE, DEFAULT_TIME_STYLE);
        }
    }
}
