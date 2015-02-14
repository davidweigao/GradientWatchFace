package gaowei.textwatchface;

import android.graphics.Canvas;
import android.graphics.Paint;


/**
 * Created by David on 2/12/2015.
 */
public interface TimeDigitsArtist {
    public void draw(Canvas canvas, int hour, int min, int sec, Paint paint);
}

abstract class SimpleDigitsArtist implements TimeDigitsArtist {
    int width;
    int height;
    SimpleDigitsArtist(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
