package gaowei.textwatchface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by David on 2/12/2015.
 */
public class BouncyTimeDigitsArtist extends SimpleDigitsArtist {


    public BouncyTimeDigitsArtist(int width, int height) {
        super(width, height);
    }

    @Override
    public void draw(Canvas canvas, int hour, int min, int sec, Paint paint) {
        RectF hourTextRect = new RectF(7.5f,7.5f,width-7.5f, height-7.5f);
        Path path = new Path();
        path.addArc(hourTextRect, -110 + getRandomAngle(5), 30);
        canvas.drawTextOnPath("" + hour, path, 0, 25, paint);
        RectF minTextRect = new RectF(47.5f, 47.5f, width-47.5f, height-47.5f);
        path.reset();
        path.addArc(minTextRect, -105+ getRandomAngle(10), 30);
        canvas.drawTextOnPath("" + min, path, 0, 25, paint);
        RectF secTextRect = new RectF(87.5f, 87.5f, width-87.5f, height-87.5f);
        path.reset();
        path.addArc(secTextRect, -100+ getRandomAngle(15), 30);
        canvas.drawTextOnPath("" + sec, path, 0, 25, paint);
    }

    private float getRandomAngle( int around) {
        Random r = new Random();
        return r.nextFloat() * around * (r.nextInt() > 0.5 ? 1 : -1);
    }
}
