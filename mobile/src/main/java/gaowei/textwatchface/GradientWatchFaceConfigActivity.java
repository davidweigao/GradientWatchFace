package gaowei.textwatchface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by David on 2/11/2015.
 */
public class GradientWatchFaceConfigActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<DataApi.DataItemResult>, View.OnClickListener{

    private static final String TAG = "ConfigActivity";
    private static final String PATH_WITH_FEATURE = "/aaaaa/bbbbb";
    static final String KEY_COLOR_1 = "gaowei.watchface.gradientwatchface_color1";
    static final String KEY_COLOR_2 = "gaowei.watchface.gradientwatchface_color2";
    static final String KEY_TIME_STYLE = "gaowei.watchface.gradientwatchface.timestyle";



    private GoogleApiClient mGoogleApiClient;
    private String mPeerId;
    private Button sendButton;
    private Button pickColor1Button;
    private Button pickColor2Button;
    private Button styleButton;
    private WatchView watchView;
    private int color1;
    private int color2;

    private int style = 1;
    @Override
    public void onConnected(Bundle bundle) {
        if (true) {
            Log.d(TAG, "onConnected: " + bundle);
        }

        if (mPeerId != null) {
            Uri.Builder builder = new Uri.Builder();
            Uri uri = builder.scheme("wear").path(PATH_WITH_FEATURE).authority(mPeerId).build();
            Wearable.DataApi.getDataItem(mGoogleApiClient, uri).setResultCallback(this);
        } else {
            displayNoConnectedDeviceDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPeerId = getIntent().getStringExtra("android.support.wearable.watchface.extra.PEER_ID");
        Log.d(TAG, "PeerID:" + mPeerId + "");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
        setContentView(R.layout.activity_gradient_watchface_config);
        sendButton = (Button) findViewById(R.id.sendButton);
        pickColor1Button = (Button) findViewById(R.id.pickColor1Button);
        pickColor2Button = (Button) findViewById(R.id.pickColor2Button);
        sendButton.setOnClickListener(this);
        pickColor1Button.setOnClickListener(this);
        pickColor2Button.setOnClickListener(this);
        styleButton = (Button) findViewById(R.id.styleButton);
        styleButton.setOnClickListener(this);
        watchView = (WatchView) findViewById(R.id.watchView);



    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onConnectionSuspended(int i) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnectionSuspended: " + i);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnectionFailed: " + connectionResult);
        }
    }

    @Override
    public void onResult(DataApi.DataItemResult dataItemResult) {
        if (dataItemResult.getStatus().isSuccess() && dataItemResult.getDataItem() != null) {
            DataItem configDataItem = dataItemResult.getDataItem();
            DataMapItem dataMapItem = DataMapItem.fromDataItem(configDataItem);
            DataMap config = dataMapItem.getDataMap();
            setUpAllPickers(config);
        } else {
            // If DataItem with the current config can't be retrieved, select the default items on
            // each picker.
            setUpAllPickers(null);
        }
    }

    private void displayNoConnectedDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String messageText = "No wearable device is currently connected.";
        String okText = "OK";
        builder.setMessage(messageText)
                .setCancelable(false)
                .setPositiveButton(okText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Sets up selected items for all pickers according to given {@code config} and sets up their
     * item selection listeners.
     *
     * @param config the {@code DigitalWatchFaceService} config {@link DataMap}. If null, the
     *         default items are selected.
     */
    private void setUpAllPickers(DataMap config) {

    }

    private void sendConfigUpdateMessage(String configKey, int color) {
        if (mPeerId != null) {
            DataMap config = new DataMap();
            config.putInt(configKey, color);
            byte[] rawData = config.toByteArray();
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, PATH_WITH_FEATURE, rawData);
            Log.d(TAG, "message sent, " + configKey + ": " + color);

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Sent watch face config message: " + configKey + " -> "
                        + Integer.toHexString(color));
            }
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.pickColor1Button:
            case R.id.pickColor2Button:
                AmbilWarnaDialog.OnAmbilWarnaListener listener = new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        v.setBackgroundColor(color);
                        if(v.getId() == R.id.pickColor1Button) {
                            color1 = color;
                            watchView.setColor1(color);
                        }
                        else {
                            color2 = color;
                            watchView.setColor2(color);
                        }
                    }
                };
                new AmbilWarnaDialog(GradientWatchFaceConfigActivity.this,0,listener).show();
                break;
            case R.id.sendButton:
                sendConfigUpdateMessage(KEY_COLOR_1, color1);
                sendConfigUpdateMessage(KEY_COLOR_2, color2);
                break;
            case R.id.styleButton:
                sendConfigUpdateMessage(KEY_TIME_STYLE, style++);
                if(style > 3) style = 1;
                break;

        }
    }
}
