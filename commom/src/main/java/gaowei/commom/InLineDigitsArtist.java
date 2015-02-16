package gaowei.commom;

import android.graphics.RectF;

/**
 * Created by David on 2/16/2015.
 */
public abstract class InLineDigitsArtist extends SimpleDigitsArtist {
    protected float mOffSet;
    protected RectF mHourTextRect;
    protected RectF mMinTextRect;
    protected RectF mSecTextRect;

    public InLineDigitsArtist(int width, int height, int color) {
        super(width, height, color);
        float hourDiskWidth = 40 * width/320;
        float hourMinDiskWidth = hourDiskWidth * 2;
        paint.setTextSize(hourDiskWidth * 0.8f);
        mOffSet = hourDiskWidth * 0.9f;
        mHourTextRect = new RectF(0,0,width, height);
        mMinTextRect = new RectF(hourDiskWidth, hourDiskWidth,
                width-hourDiskWidth, height-hourDiskWidth);
        mSecTextRect = new RectF(hourMinDiskWidth, hourMinDiskWidth,
                width - hourMinDiskWidth, height - hourMinDiskWidth);
    }


}
