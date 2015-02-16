package gaowei.commom;

import android.graphics.Paint;

/**
 * Created by David on 2/14/2015.
 */
public abstract class SimpleDigitsArtist implements TimeDigitsArtist {
    int mode;
    int width;
    int height;
    Paint paint;
    SimpleDigitsArtist(int width, int height, int color) {
        this.width = width;
        this.height = height;
        this.mode = MODE_NORMAL;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void setColor(int color) {
        paint.setColor(color);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}