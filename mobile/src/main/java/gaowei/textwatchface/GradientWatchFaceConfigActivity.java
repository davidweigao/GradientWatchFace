package gaowei.textwatchface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import gaowei.commom.GradientWatchFaceService;
import gaowei.commom.Utility;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by David on 2/11/2015.
 */
public class GradientWatchFaceConfigActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<DataApi.DataItemResult>, View.OnClickListener{

    private static final String TAG = "ConfigActivity";
    private static final String PATH_WITH_FEATURE = "/aaaaa/bbbbb";
    private static final int REQUEST_CODE_STYLE = 1;


    private GoogleApiClient mGoogleApiClient;
    private String mPeerId;
    private Button updateButton;
    private View pickColor1Button;
    private View pickColor2Button;
    private View pickColorTextButton;
    private Button cancelButton;
    private WatchView watchViewNormal;
    private boolean isInitialized = false;

    private DataMap config = new DataMap();


    @Override
    public void onConnected(Bundle bundle) {
        if (true) {
            Log.d(TAG, "onConnected: " + bundle);
        }

        if(!isInitialized) {
            if (mPeerId != null) {
                Uri.Builder builder = new Uri.Builder();
                Uri uri = builder.scheme("wear").path(PATH_WITH_FEATURE).authority(mPeerId).build();
                Wearable.DataApi.getDataItem(mGoogleApiClient, uri).setResultCallback(this);
            } else {
                displayNoConnectedDeviceDialog();
            }
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
        updateButton = (Button) findViewById(R.id.updateButton);
        pickColor1Button =  findViewById(R.id.pickColor1Button);
        pickColor2Button =  findViewById(R.id.pickColor2Button);
        pickColorTextButton =  findViewById(R.id.pickColorTextButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        updateButton.setOnClickListener(this);
        pickColor1Button.setOnClickListener(this);
        pickColor2Button.setOnClickListener(this);
        pickColorTextButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        watchViewNormal = (WatchView) findViewById(R.id.watchViewNormal);
        watchViewNormal.setOnClickListener(this);
        updateWatchViews();


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
        isInitialized = true;
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
        this.config.putAll(config);
        pickColor1Button.setBackgroundColor(this.config.getInt(GradientWatchFaceService.KEY_COLOR_1));
        pickColor2Button.setBackgroundColor(this.config.getInt(GradientWatchFaceService.KEY_COLOR_2));
        pickColorTextButton.setBackgroundColor(this.config.getInt(GradientWatchFaceService.KEY_COLOR_TEXT));
        updateWatchViews();
    }

    private void updateWatchViews() {
        if(watchViewNormal != null) {
            watchViewNormal.updateConfig(config);
        }
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

    private void sendConfig() {
        if (mPeerId != null) {
            byte[] rawData = config.toByteArray();
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, PATH_WITH_FEATURE, rawData);
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.pickColor1Button:
            case R.id.pickColor2Button:
            case R.id.pickColorTextButton:
                AmbilWarnaDialog.OnAmbilWarnaListener listener = new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        v.setBackgroundColor(color);
                        if(v.getId() == R.id.pickColor1Button) {
                            config.putInt(GradientWatchFaceService.KEY_COLOR_1, color);
                        } else if(v.getId() == R.id.pickColorTextButton) {
                            config.putInt(GradientWatchFaceService.KEY_COLOR_TEXT, color);
                        } else {
                            config.putInt(GradientWatchFaceService.KEY_COLOR_2, color);
                        }
                        updateWatchViews();
                    }
                };
                int currentColor;
                if(v.getId() == R.id.pickColor1Button) {
                    currentColor = config.getInt(GradientWatchFaceService.KEY_COLOR_1);
                } else if(v.getId() == R.id.pickColorTextButton) {
                    currentColor = config.getInt(GradientWatchFaceService.KEY_COLOR_TEXT);
                } else {
                    currentColor = config.getInt(GradientWatchFaceService.KEY_COLOR_2);
                }
                new AmbilWarnaDialog(GradientWatchFaceConfigActivity.this,currentColor,listener).show();
                break;
            case R.id.updateButton:
                sendConfig();
                break;
            case R.id.cancelButton:
                finish();
                break;
            case R.id.watchViewNormal:
                Intent intent = new Intent(this, SelectStyleActivity.class);
                Utility.addData(intent , config);
                startActivityForResult(intent, REQUEST_CODE_STYLE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_STYLE:
                if(resultCode == RESULT_OK) {
                    setUpAllPickers(Utility.getDataMap(data));
                }
                break;
        }
    }
}
