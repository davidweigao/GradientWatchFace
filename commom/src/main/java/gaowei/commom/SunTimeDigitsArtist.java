package gaowei.commom;

        import android.graphics.Canvas;
        import android.graphics.Paint;
        import android.graphics.Path;
        import android.graphics.RectF;

/**
 * Created by David on 2/13/2015.
 */
public class SunTimeDigitsArtist extends SimpleDigitsArtist {

    float offset;

    public SunTimeDigitsArtist(int width, int height, int color) {
        super(width, height, color);
        float hourMinDiskWidth = 40 * width / 320;
        offset = hourMinDiskWidth * 0.9f;
        paint.setTextSize(hourMinDiskWidth * 0.8f);
    }

    @Override
    public void draw(Canvas canvas, int hour, int min, int sec) {
        String time = "";
        switch (mode) {
            case MODE_NORMAL:
                time += String.format("%02d:%02d:%02d", hour, min, sec);
                break;
            case MODE_AMBIENT:
                time += String.format("%02d:%02d", hour, min);
        }
        RectF hourTextRect = new RectF(0,0,width, height);
        Path path = new Path();
        float sunAngle = -180*(hour*3600+min*60+sec)/(24*60*60);
        path.addArc(hourTextRect, sunAngle - 32, 64);
        canvas.drawTextOnPath(time, path, 0, offset, paint);
    }
}
