package gaowei.textwatchface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by David on 2/13/2015.
 */
public class ColockTimeDigitsArtist extends SimpleDigitsArtist {
    ColockTimeDigitsArtist(int width, int height) {
        super(width, height);
    }

    @Override
    public void draw(Canvas canvas, int hour, int min, int sec, Paint paint) {
        float hourDegree = -90 + hour%12 * 30;
        float minDegree = -90 + min * 6 + sec / 60f * 6;
        float secondDegree = -90 + sec * 6;
        paint.setTextAlign(Paint.Align.LEFT);
        Path path = new Path();

        drawTime(canvas, path, hour, paint, 0, hourDegree);
        drawTime(canvas, path, min, paint, 40, minDegree);
        drawTime(canvas, path, sec, paint, 80, secondDegree);
    }

    private void drawTime(Canvas canvas,Path path, int digits, Paint paint, float d, float degree) {
        path.reset();
        RectF secTextRect = new RectF(d, d, width-d, height-d);
        if(degree < 0) degree += 360;
        if(degree%360 > 180){
            path.addArc(secTextRect, degree, 30);
            canvas.drawTextOnPath(String.format("%2d", digits), path, 0, 28, paint);
        }
        else {
            path.addArc(secTextRect, degree+30, -30);
            canvas.drawTextOnPath(String.format("%2d", digits), path, 0, 0, paint);

        }
    }
}
