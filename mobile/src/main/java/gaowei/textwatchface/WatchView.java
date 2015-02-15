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

import com.google.android.gms.wearable.DataMap;

import gaowei.commom.BackgroundArtist;
import gaowei.commom.ColockTimeDigitsArtist;
import gaowei.commom.ColorAwareBackgroundArtist;
import gaowei.commom.GradientWatchFaceService;
import gaowei.commom.RainbowBackgroundArtist;
import gaowei.commom.SunTimeDigitsArtist;
import gaowei.commom.TextTimeDigitsArtist;
import gaowei.commom.TimeDigitsArtist;

/**
 * Created by David on 2/12/2015.
 */
public class WatchView extends View {

    GradientWatchFaceService service = new GradientWatchFaceService();
    GradientWatchFaceService.Engine engine = service.onCreateEngine();
    DataMap config;

    int width;
    int height;
    int midX;
    int midY;
    Time mTime;

    private boolean isAmbient;
    public void setAmbient(boolean a) {
        this.isAmbient = a;
    }

    public WatchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTime = new Time();

    }


    @Override
    protected void onDraw(Canvas canvas) {
//        drawWatch(canvas, new Rect(0, 0, width, height));
        if(isAmbient)
            engine.onAmbientModeChanged(true);
        GradientWatchFaceService.drawWatch(canvas, new Rect(0, 0, width, height), mTime, engine.getBackgroundArtist(), engine.getTimeDigitsArtist(), engine.getHourDigitPaint());
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        width = parentWidth;
        height = width;
        midX = width / 2;
        midY = height/2;
        this.setMeasuredDimension(parentWidth, parentWidth);
        engine.init();
        engine.setDimension(width,width);
        if(config == null || config.keySet().size() == 0) {
            config = new DataMap();
            config.putInt(GradientWatchFaceService.KEY_COLOR_1,GradientWatchFaceService.DEFAULT_COLOR_1);
            config.putInt(GradientWatchFaceService.KEY_COLOR_2,GradientWatchFaceService.DEFAULT_COLOR_2);
            config.putInt(GradientWatchFaceService.KEY_BG_STYLE,GradientWatchFaceService.DEFAULT_BG_STYLE);
            config.putInt(GradientWatchFaceService.KEY_TIME_STYLE,GradientWatchFaceService.DEFAULT_TIME_STYLE);
            config.putInt(GradientWatchFaceService.KEY_COLOR_TEXT,GradientWatchFaceService.DEFAULT_COLOR_TEXT);
        }
        engine.updateUiForConfigDataMap(config);
        engine.setView(this);
        engine.updateTimer();
        if(isAmbient)
            engine.onAmbientModeChanged(true);
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void setConfig(DataMap config) {
        this.config = config;
            engine.updateUiForConfigDataMap(config);
    }


}
