package gaowei.commom;

import android.graphics.Paint;

/**
 * Created by David on 2/14/2015.
 */
public abstract class TimeAwareBackgroundArtist implements BackgroundArtist{

    float width;
    float height;
    float midX;
    float midY;
    int mHour;
    int mMin;
    int mSec;
    int mMode;
    Paint paint;

    @Override
    public void setMode(int mode) {
        mMode = mode;
    }

    public void setTime(int h, int m, int s) {
        mHour = h;
        mMin = m;
        mSec = s;
    }

    public TimeAwareBackgroundArtist(float w, float h ) {
        this.width = w;
        this.height = h;
        midX = width / 2;
        midY = height / 2;
        paint = new Paint();
    }
}
