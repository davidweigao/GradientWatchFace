package gaowei.commom;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 2/13/2015.
 */
public class TextTimeDigitsArtist extends InLineDigitsArtist {

    public TextTimeDigitsArtist(int width, int height, int color) {
        super(width, height, color);
        digitsToStringMap.put(0, "Zero");
        digitsToStringMap.put(1, "One");
        digitsToStringMap.put(2, "Two");
        digitsToStringMap.put(3, "Three");
        digitsToStringMap.put(4, "Four");
        digitsToStringMap.put(5, "Five");
        digitsToStringMap.put(6, "Six");
        digitsToStringMap.put(7, "Seven");
        digitsToStringMap.put(8, "Eight");
        digitsToStringMap.put(9, "Night");
        digitsToStringMap.put(10, "Ten");
        digitsToStringMap.put(11, "Eleven");
        digitsToStringMap.put(12, "Twelve");
        digitsToStringMap.put(13, "Thirteen");
        digitsToStringMap.put(14, "Fourteen");
        digitsToStringMap.put(15, "Fifteen");
        digitsToStringMap.put(16, "Sixteen");
        digitsToStringMap.put(17, "Seventeen");
        digitsToStringMap.put(18, "Eighteen");
        digitsToStringMap.put(19, "Nighteen");
        digitsToStringMap.put(20, "Twenty");
        digitsToStringMap.put(30, "Thirty");
        digitsToStringMap.put(40, "Forty");
        digitsToStringMap.put(50, "Fifty");
    }
    private Map<Integer, String> digitsToStringMap = new HashMap<>();

    @Override
    public void draw(Canvas canvas, int hour, int min, int sec) {
        String h = getStringFromDigit(hour);
        String m = getStringFromDigit(min);
        String s = getStringFromDigit(sec);
        // hour
        Path path = new Path();
        path.addArc(mHourTextRect, 180, 180);
        canvas.drawTextOnPath(h, path, 0, mOffSet, paint);
        // minute
        path.reset();
        path.addArc(mMinTextRect, 180, 180);
        canvas.drawTextOnPath(m, path, 0, mOffSet, paint);
        // second
        if(mode != MODE_AMBIENT) {
            path.reset();
            path.addArc(mSecTextRect, 180, 180);
            canvas.drawTextOnPath(s, path, 0, mOffSet, paint);
        }

    }

    private String getStringFromDigit(int n) {
        if( n >= 0 && n < 20 || n % 10 == 0) {
            return digitsToStringMap.get(n);
        } else if(n < 60) {
            return getStringFromDigit(n / 10 * 10) + "-" + getStringFromDigit(n % 10);
        } else {
            return "invalid";
        }
    }
}
