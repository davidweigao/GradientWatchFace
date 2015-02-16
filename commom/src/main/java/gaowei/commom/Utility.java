package gaowei.commom;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.wearable.DataMap;

/**
 * Created by David on 2/15/2015.
 */
public class Utility {
    public static void addData(Intent intent, DataMap map) {
        for(String key : map.keySet()) {
            if(map.get(key) instanceof Integer)
            intent.putExtra(key, (int)map.get(key));
        }
    }

    public static DataMap getDataMap(Intent intent) {
        DataMap config = new DataMap();
        Bundle bundle = intent.getExtras();
        for(String key : bundle.keySet()) {
            if(bundle.get(key) instanceof Integer) {
                config.putInt(key, (Integer)bundle.get(key));
            }
        }
        return config;
    }
}
