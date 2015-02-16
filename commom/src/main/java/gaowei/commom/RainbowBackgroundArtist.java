package gaowei.commom;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.Log;

/**
 * Created by David on 2/14/2015.
 */
public class RainbowBackgroundArtist extends TimeAwareBackgroundArtist implements ColorAwareBackgroundArtist {
    public static final String TAG = RainbowBackgroundArtist.class.getSimpleName();
    private int mColor1;
    private int mColor2;
    private float mHourDiskWidth;
    private float mMinDiskWidth;


    public RainbowBackgroundArtist(float w, float h, int c1, int c2) {
        super(w, h);
        mHourDiskWidth = w/320*40;
        mMinDiskWidth = mHourDiskWidth * 2;
        setColor1(c1);
        setColor2(c2);
    }

    public void setColor1(int c1) {
        mColor1 = c1;
    }

    public void setColor2(int c2) {
        mColor2 = c2;
    }

    @Override
    public void draw(Canvas canvas) {
        float hourDegree = -90 + mHour%12 * 30;
        drawRainbow(canvas, 0, hourDegree, paint);
        float minDegree = -90 + mMin * 6 + mSec / 60f * 6;
        drawRainbow(canvas, mHourDiskWidth, minDegree, paint);
        if(mMode != TimeDigitsArtist.MODE_AMBIENT) {
            float secondDegree = -90 + mSec * 6;
            drawRainbow(canvas, mMinDiskWidth, secondDegree, paint);
        }

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

    @Override
    public void setColors(int... colors) {
        if(colors.length != 2) {
            Log.e(TAG, "setColors colors length less than 2");
        } else {
            mColor1 = colors[0];
            mColor2 = colors[1];
        }
    }
}
