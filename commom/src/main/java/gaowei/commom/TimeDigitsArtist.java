package gaowei.commom;

import android.graphics.Canvas;
import android.graphics.Paint;


/**
 * Created by David on 2/12/2015.
 */
public interface TimeDigitsArtist {
    public static final int MODE_NORMAL = 1;
    public static final int MODE_AMBIENT = 2;
    public void setMode(int mode);
    public void draw(Canvas canvas, int hour, int min, int sec);

    public void setColor(int color);
}


