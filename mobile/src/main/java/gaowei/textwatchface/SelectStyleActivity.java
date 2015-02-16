package gaowei.textwatchface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;

import gaowei.commom.GradientWatchFaceService;

/**
 * Created by David on 2/15/2015.
 */
public class SelectStyleActivity extends Activity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_style);
        int color1 = getIntent().getIntExtra(GradientWatchFaceService.KEY_COLOR_1, -1);
        int color2 = getIntent().getIntExtra(GradientWatchFaceService.KEY_COLOR_2, -1);
        int colorText = getIntent().getIntExtra(GradientWatchFaceService.KEY_COLOR_TEXT, -1);
        int backgroundId = GradientWatchFaceService.DEFAULT_BG_STYLE;
        List<DataMap> styles = new ArrayList<>();
        DataMap commomConfig = new DataMap();
        commomConfig.putInt(GradientWatchFaceService.KEY_COLOR_1, color1);
        commomConfig.putInt(GradientWatchFaceService.KEY_COLOR_2, color2);
        commomConfig.putInt(GradientWatchFaceService.KEY_COLOR_TEXT, colorText);
        commomConfig.putInt(GradientWatchFaceService.KEY_BG_STYLE, backgroundId);
        for(int i = 1; i < GradientWatchFaceService.Engine.TIME_STYLE_COUNT + 1; i++) {
            DataMap m = new DataMap();
            m.putAll(commomConfig);
            m.putInt(GradientWatchFaceService.KEY_TIME_STYLE, i);
            styles.add(m);
        }
        WatchViewAdatper adapter = new WatchViewAdatper(this, R.layout.watch, styles);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(GradientWatchFaceService.KEY_BG_STYLE, GradientWatchFaceService.DEFAULT_BG_STYLE);
                intent.putExtra(GradientWatchFaceService.KEY_TIME_STYLE,
                        ((DataMap)listView.getAdapter().getItem(position))
                                .getInt(GradientWatchFaceService.KEY_TIME_STYLE));
                SelectStyleActivity.this.setResult(RESULT_OK, intent);
                SelectStyleActivity.this.finish();
            }
        });
    }

    private static class WatchViewAdatper extends ArrayAdapter<DataMap> {

        Context context;
        int layoutResource;

        public WatchViewAdatper(Context context, int resource, List<DataMap> objects) {
            super(context, resource, objects);
            this.context = context;
            this.layoutResource = resource;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater ();
                convertView = inflater.inflate(layoutResource, parent, false);
            }
            WatchView v = (WatchView) convertView.findViewById(R.id.watchView);
            v.setConfig(this.getItem(position));
            return convertView;
        }
    }
}
