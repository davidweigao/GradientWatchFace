package gaowei.commom;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by David on 2/13/2015.
 */
public class ColockTimeDigitsArtist extends InLineDigitsArtist {

    public ColockTimeDigitsArtist(int width, int height, int color) {
        super(width, height, color);
    }


    @Override
    public void draw(Canvas canvas, int hour, int min, int sec) {

        float hourDegree = -90 + hour%12 * 30;
        float minDegree = -90 + min * 6 + sec / 60f * 6;
        float secondDegree = -90 + sec * 6;
        paint.setTextAlign(Paint.Align.LEFT);
        Path path = new Path();

        drawTime(canvas, path, hour, mHourTextRect, hourDegree);
        drawTime(canvas, path, min, mMinTextRect, minDegree);
        if(MODE_NORMAL == mode) {
            drawTime(canvas, path, sec, mSecTextRect, secondDegree);
        }
    }

    private void drawTime(Canvas canvas, Path path, int digits, RectF rect, float degree) {
        path.reset();
        if(degree < 0) degree += 360;
        if(degree%360 > 180){
            path.addArc(rect, degree, 30);
            canvas.drawTextOnPath(String.format("%2d", digits), path, 0, mOffSet, paint);
        }
        else {
            path.addArc(rect, degree+30, -30);
            canvas.drawTextOnPath(String.format("%2d", digits), path, 0, paint.getTextSize()-mOffSet, paint);

        }
    }
}
