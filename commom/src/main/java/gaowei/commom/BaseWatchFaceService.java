package gaowei.commom;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by David on 2/10/2015.
 */
public abstract class BaseWatchFaceService extends CanvasWatchFaceService {
    public abstract class Engine extends CanvasWatchFaceService.Engine
        implements DataApi.DataListener,
                   GoogleApiClient.ConnectionCallbacks,
                   GoogleApiClient.OnConnectionFailedListener{

        private static final String TAG = "BaseWFS";

        Time mTime;
        int width;
        int height;
        float midX;
        float midY;

        public void setDimension(int w, int h) {
            width = w;
            height = h;
            midX = w/2;
            midY = h/2;
        }

        private boolean isAmbient = false;

        protected abstract int getInteractiveUpdateRateMs();

        static final int MSG_UPDATE_TIME = 0;

        /** Handler to update the time periodically in interactive mode. */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();

                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs =
                                    getInteractiveUpdateRateMs() - (timeMs % getInteractiveUpdateRateMs());
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        GoogleApiClient mGoogleApiClient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            mGoogleApiClient= new GoogleApiClient.Builder(BaseWatchFaceService.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .build();
        }

        public void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }


        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            invalidate();
            updateTimer();
            isAmbient = inAmbientMode;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if(visible) {
                mGoogleApiClient.connect();
            } else {
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Wearable.DataApi.removeListener(mGoogleApiClient, this);
                    mGoogleApiClient.disconnect();
                }
            }
            updateTimer();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            this.width = width;
            this.height = height;
            midX = width/2;
            midY = height/2;
        }

        protected boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();

        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onTimeTick: ambient = " + isInAmbientMode());
            }
            invalidate();
        }

        private void updateConfigDataItemAndUiOnStartup() {
            WatchFaceUtil.fetchConfigDataMap(mGoogleApiClient,
                    new WatchFaceUtil.FetchConfigDataMapCallback() {
                        @Override
                        public void onConfigDataMapFetched(DataMap startupConfig) {
                            // If the DataItem hasn't been created yet or some keys are missing,
                            // use the default values.
                            setDefaultValuesForMissingConfigKeys(startupConfig);
                            WatchFaceUtil.putConfigDataItem(mGoogleApiClient, startupConfig);

                            updateUiForConfigDataMap(startupConfig);
                        }
                    }
            );
        }

        public void updateUiForConfigDataMap(final DataMap config) {
            boolean uiUpdated = false;
            for (String configKey : config.keySet()) {
                if (!config.containsKey(configKey)) {
                    continue;
                }
                int color = config.getInt(configKey);
//                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.e(TAG, "Found watch face config key: " + configKey + " -> "
                            + Integer.toHexString(color));
//                }
                if (updateUiForKey(configKey, color)) {
                    uiUpdated = true;
                }
            }
            if (uiUpdated) {
                invalidate();
            }
        }

        protected abstract void setDefaultValuesForMissingConfigKeys(DataMap config);

        protected abstract boolean updateUiForKey(String configKey, int color);

        protected void addIntKeyIfMissing(DataMap config, String key, int color) {
            if (!config.containsKey(key)) {
                config.putInt(key, color);
            }
        }

        @Override // DataApi.DataListener
        public void onDataChanged(DataEventBuffer dataEvents) {
            Log.e(TAG, "Config DataItem updatedXXX:");
            try {
                Log.e(TAG, "1");
                for (DataEvent dataEvent : dataEvents) {
                    if (dataEvent.getType() != DataEvent.TYPE_CHANGED) {
                        Log.e(TAG, "2");
                        continue;
                    }
                    Log.e(TAG, "3");
                    DataItem dataItem = dataEvent.getDataItem();
                    if (!dataItem.getUri().getPath().equals(
                            WatchFaceUtil.PATH_WITH_FEATURE)) {
                        Log.e(TAG, dataItem.getUri().getPath());
                        continue;
                    }
                    Log.e(TAG, "5");
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                    Log.e(TAG, "6");
                    DataMap config = dataMapItem.getDataMap();
//                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.e(TAG, "Config DataItem updated:" + config);
//                    }
                    updateUiForConfigDataMap(config);
                }
            } finally {
                dataEvents.close();
            }
        }



//==================================================================================================
//      GoogleApiClient callbacks
//==================================================================================================

        @Override
        public void onConnected(Bundle bundle) {
//            if(Log.isLoggable(TAG, Log.DEBUG)) {
                Log.e(TAG, "onConnected: " + bundle);
//            }
            Wearable.DataApi.addListener(mGoogleApiClient, Engine.this);
            updateConfigDataItemAndUiOnStartup();
        }

        @Override
        public void onConnectionSuspended(int i) {
//            if(Log.isLoggable(TAG, Log.DEBUG)) {
                Log.e(TAG, "onConnectionSuspended: " + i);
//            }
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
//            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.e(TAG, "onConnectionFailed: " + connectionResult);
//            }
        }
    }
}
