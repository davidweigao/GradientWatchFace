package gaowei.commom;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by David on 2/14/2015.
 */
public interface BackgroundArtist {
    public void draw(Canvas canvas);

    public void setMode(int mode);
}
