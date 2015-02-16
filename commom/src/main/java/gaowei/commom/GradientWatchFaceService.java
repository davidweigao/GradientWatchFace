package gaowei.commom;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.TextPaint;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;

import com.google.android.gms.wearable.DataMap;

/**
 * Created by David on 2/10/2015.
 */
public class GradientWatchFaceService extends BaseWatchFaceService{
    private static final String TAG = "GWF";
    public static final String KEY_COLOR_1 = "gaowei.watchface.gradientwatchface_color1";
    public static final String KEY_COLOR_2 = "gaowei.watchface.gradientwatchface_color2";
    public static final String KEY_TIME_STYLE = "gaowei.watchface.gradientwatchface.timestyle";
    public static final String KEY_BG_STYLE = "gaowei.watchface.gradientwatchface.bgstyle";
    public static final String KEY_COLOR_TEXT = "gaowei.watchface.gradientwatchface_color_text";
    public static final int DEFAULT_COLOR_1 = 0xff222222;
    public static final int DEFAULT_COLOR_2 = 0xff0c83ce;
    public static final int DEFAULT_TIME_STYLE = Engine.TIME_STYLE_SUN;
    public static final int DEFAULT_BG_STYLE = Engine.BG_STYLE_RAINBOW;
    public static final int DEFAULT_COLOR_TEXT = 0xffffffff;

    public static void drawWatch(Canvas canvas,
                                 Rect bounds,
                                 Time mTime,
                                 BackgroundArtist backgroundArtist,
                                 TimeDigitsArtist timeDigitsArtist) {
        mTime.setToNow();
        int hour = mTime.hour;
        int min = mTime.minute;
        int sec = mTime.second;

        if (backgroundArtist instanceof TimeAwareBackgroundArtist) {
            ((TimeAwareBackgroundArtist) backgroundArtist).setTime(hour, min, sec);
        }
        backgroundArtist.draw(canvas);
        timeDigitsArtist.draw(canvas, hour, min, sec);
    }

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    public final class Engine extends BaseWatchFaceService.Engine {
        public static final int TIME_STYLE_BOUNCY = 1;
        public static final int TIME_STYLE_SUN = 2;
        public static final int TIME_STYLE_CLOCK = 3;
        public static final int TIME_STYLE_TEXT = 4;
        public static final int TIME_STYLE_COUNT = 4;
        public static final int BG_STYLE_RAINBOW = 1;
        int mColor1;
        int mColor2;
        int mColorText;
        private int currentTimeDigitsArtistType = -1;
        private int currentBackgroundArtistType = -1;
        private TimeDigitsArtist mTimeDigitsArtist;
        private BackgroundArtist mBackgroundArtist;

        View mView;
        public void setView(View v) {
            mView = v;
        }

        public BackgroundArtist getBackgroundArtist() {
            return mBackgroundArtist;
        }

        public TimeDigitsArtist getTimeDigitsArtist() {
            return mTimeDigitsArtist;
        }

        public void setTimeDigitsArtist(int type) {
            if(currentTimeDigitsArtistType == type) return;
            switch (type) {
                case TIME_STYLE_BOUNCY:
                    mTimeDigitsArtist = new BouncyTimeDigitsArtist(width, height, mColorText);
                    break;
                case TIME_STYLE_SUN:
                    mTimeDigitsArtist = new SunTimeDigitsArtist(width, height, mColorText);
                    break;
                case TIME_STYLE_CLOCK:
                    mTimeDigitsArtist = new ColockTimeDigitsArtist(width, height, mColorText);
                    break;
                case TIME_STYLE_TEXT:
                    mTimeDigitsArtist = new TextTimeDigitsArtist(width, height, mColorText);
                    break;
            }

        }

        public void setBackgroundArtist(int type) {
            if(currentBackgroundArtistType == type) return;
            switch (type) {
                case BG_STYLE_RAINBOW:
                    mBackgroundArtist = new RainbowBackgroundArtist(width, height, mColor1, mColor2);
                    break;
            }
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            Log.e(TAG, "onAmbientModeChanged()");
            if (inAmbientMode) {
                mTimeDigitsArtist.setMode(TimeDigitsArtist.MODE_AMBIENT);
                mBackgroundArtist.setMode(TimeDigitsArtist.MODE_AMBIENT);
            } else {
                mTimeDigitsArtist.setMode(TimeDigitsArtist.MODE_NORMAL);
                mBackgroundArtist.setMode(TimeDigitsArtist.MODE_NORMAL);
            }
            if(mView != null)
                super.onAmbientModeChanged(inAmbientMode);

            super.onAmbientModeChanged(inAmbientMode);
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
            init();
            setWatchFaceStyle(new WatchFaceStyle.Builder(GradientWatchFaceService.this)
                    .setStatusBarGravity(Gravity.BOTTOM)
                    .setHotwordIndicatorGravity(Gravity.BOTTOM).build());
        }

        @Override
        protected boolean shouldTimerBeRunning() {
            if(mView == null)
                return super.shouldTimerBeRunning();
            else
                return true;
        }

        public void init() {

            mTime = new Time();
            mTimeDigitsArtist = new TimeDigitsArtist() {
                @Override
                public void setMode(int mode) {

                }

                @Override
                public void draw(Canvas canvas, int hour, int min, int sec) {
                    // draw nothing, dummy class for initialization
                }

                @Override
                public void setColor(int color) {

                }
            };
            mBackgroundArtist = new BackgroundArtist() {
                @Override
                public void draw(Canvas canvas) {

                }

                @Override
                public void setMode(int mode) {

                }
            };
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            Log.e(TAG, "onDraw()");
            drawWatch(canvas, bounds, mTime, mBackgroundArtist, mTimeDigitsArtist);
        }

        @Override
        public void invalidate() {
            if(mView == null)
                super.invalidate();
            else mView.invalidate();
        }

        public void setColor1(int color) {
            Log.e(TAG, "setColor1: " + color);
            mColor1 = color;
            updateBackground();
        }

        public void setColor2(int color) {
            mColor2 = color;
            updateBackground();

        }

        public void setColorText(int color) {
            mColorText = color;
            if(mTimeDigitsArtist != null) {
                mTimeDigitsArtist.setColor(color);
            }
            invalidate();
        }

        public void updateBackground() {
            if(mBackgroundArtist instanceof ColorAwareBackgroundArtist) {
                ((ColorAwareBackgroundArtist) mBackgroundArtist).setColors(mColor1, mColor2);
            }
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
            } else if(configKey.equals(KEY_BG_STYLE)) {
                setBackgroundArtist(value);
            } else if (configKey.equals(KEY_COLOR_TEXT)) {
                setColorText(value);
            }
            else {
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
            addIntKeyIfMissing(config, KEY_BG_STYLE, DEFAULT_BG_STYLE);
            addIntKeyIfMissing(config, KEY_COLOR_TEXT, DEFAULT_COLOR_TEXT);
        }
    }
}
