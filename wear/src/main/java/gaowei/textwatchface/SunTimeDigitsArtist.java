package gaowei.textwatchface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by David on 2/13/2015.
 */
public class SunTimeDigitsArtist extends SimpleDigitsArtist {


    public SunTimeDigitsArtist(int width, int height) {
        super(width, height);
    }

    @Override
    public void draw(Canvas canvas, int hour, int min, int sec, Paint paint) {
        String time = String.format("%02d:%02d:%02d", hour, min, sec);
        RectF hourTextRect = new RectF(40f,40f,width-40f, height-40f);
        Path path = new Path();
        float sunAngle = -180*(hour*3600+min*60+sec)/(24*60*60);
        path.addArc(hourTextRect, sunAngle-32, 64);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawTextOnPath(time, path, 0, -8, paint);
    }
}
