package gaowei.commom;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by David on 2/12/2015.
 */
public class BouncyTimeDigitsArtist extends InLineDigitsArtist {


    public BouncyTimeDigitsArtist(int width, int height, int color) {
        super(width, height, color);
    }

    @Override
    public void draw(Canvas canvas, int hour, int min, int sec) {
        // hour
        Path path = new Path();
        path.addArc(mHourTextRect, -110 + getRandomAngle(5), 30);
        canvas.drawTextOnPath("" + hour, path, 0, mOffSet, paint);
        // min
        path.reset();
        path.addArc(mMinTextRect, -105+ getRandomAngle(10), 30);
        canvas.drawTextOnPath("" + min, path, 0, mOffSet, paint);
        // sec
        path.reset();
        path.addArc(mSecTextRect, -100+ getRandomAngle(15), 30);
        canvas.drawTextOnPath("" + sec, path, 0, mOffSet, paint);
    }

    private float getRandomAngle( int around) {
        Random r = new Random();
        return r.nextFloat() * around * (r.nextInt() > 0.5 ? 1 : -1);
    }
}
