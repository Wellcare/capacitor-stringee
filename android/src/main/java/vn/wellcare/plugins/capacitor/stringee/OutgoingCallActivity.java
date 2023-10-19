// sources: https://github.com/stringeecom/android-sdk-samples/blob/master/OneToOneCallSample/app/src/main/java/com/stringee/apptoappcallsample/OutgoingCallActivity.java
package vn.wellcare.plugins.capacitor.stringee;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import com.squareup.picasso.Picasso;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall.MediaState;
import com.stringee.call.StringeeCall.SignalingState;
import com.stringee.call.StringeeCall.StringeeCallListener;
import com.stringee.common.StringeeAudioManager;
import com.stringee.exception.StringeeError;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import vn.wellcare.plugins.capacitor.stringee.R.drawable;
import vn.wellcare.plugins.capacitor.stringee.R.id;
import vn.wellcare.plugins.capacitor.stringee.R.layout;

class StringeeStatusListener extends com.stringee.listener.StatusListener {

  @Override
  public void onSuccess() {
    Log.d(Common.TAG, "status: success");
  }

  @Override
  public void onError(StringeeError errorInfo) {
    Log.d(Common.TAG, "error status: " + errorInfo.getMessage());
  }

  @Override
  public void onProgress(int percent) {
    Log.d(Common.TAG, "on status progress" + percent);
  }
}

public class OutgoingCallActivity
  extends AppCompatActivity
  implements View.OnClickListener {
  static final String CHANNEL_ID = "outgoingcall_service_channel";
  private FrameLayout vRemote;
  private TextView tvState, vName, textPipMode;
  private ImageButton btnMute;
  private ImageButton btnSpeaker;
  private ImageButton btnVideo;
  private ImageButton btnEnd;
  private ImageButton btnBack;
  private View vControl;

  private ImageView vAvatar;

  private StringeeCall stringeeCall;
  private SensorManagerUtils sensorManagerUtils;
  private StringeeAudioManager audioManager;
  private String from, to, name, avatar;
  private boolean isMute = false;
  private boolean isSpeaker = false;
  private boolean isVideo = false;
  private boolean isPermissionGranted = true;

  private MediaState mMediaState;
  private SignalingState mSignalingState;

  private Timer timer = new Timer();
  private int seconds = 0;

  private StringeeStatusListener statusListener = new StringeeStatusListener();

  //  public interface StringeeCallCallback {
  //    void onStringeeCallCreated(StringeeCall stringeeCall);
  //  }
  // /**
  //  * Setting up the activity UI, checking and requesting for necessary permissions, and starting the call.
  //  */
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    from = getIntent().getStringExtra("from");
    to = getIntent().getStringExtra("to");
    name = getIntent().getStringExtra("name");
    avatar = getIntent().getStringExtra("avatar");
    Log.d(Common.TAG, "on going call from: " + from);
    Log.d(Common.TAG, "on going call to: " + to);
    startStringeeCall(from, to, false);
    startService();
  }

  private void startService(){
    NotificationManager manager = null;
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      manager = getSystemService(NotificationManager.class);
    }
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
              CHANNEL_ID,
              "Channel",
              NotificationManager.IMPORTANCE_LOW
      );
      manager.createNotificationChannel(channel);
    }
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      Intent serviceIntent = new Intent(this, OutgoingCallService.class);
      serviceIntent.putExtra("name", this.name);
      startForegroundService(serviceIntent);
    }
  }

  private void stopService(){
    Intent serviceIntent = new Intent(this, OutgoingCallService.class);
    stopService(serviceIntent);
  }

  public void startStringeeCall(String from, String to, Boolean isVideoCall) {
    if (isVideoCall == null) {
      isVideoCall = false;
    }
    //add Flag for show on lockScreen and disable keyguard
    getWindow()
      .addFlags(
        LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        LayoutParams.FLAG_DISMISS_KEYGUARD |
        LayoutParams.FLAG_KEEP_SCREEN_ON |
        LayoutParams.FLAG_TURN_SCREEN_ON
      );

    if (VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
      setShowWhenLocked(true);
      setTurnScreenOn(true);
    }

    setContentView(layout.activity_outgoing_call);

    sensorManagerUtils = SensorManagerUtils.getInstance(this);
    sensorManagerUtils.acquireProximitySensor(getLocalClassName());
    sensorManagerUtils.disableKeyguard();

    Common.isInCall = true;

    initView();

    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      List<String> lstPermissions = new ArrayList<>();

      if (
        ContextCompat.checkSelfPermission(this, permission.RECORD_AUDIO) !=
        PackageManager.PERMISSION_GRANTED
      ) {
        lstPermissions.add(permission.RECORD_AUDIO);
      }

      if (isVideoCall) {
        if (
          ContextCompat.checkSelfPermission(this, permission.CAMERA) !=
          PackageManager.PERMISSION_GRANTED
        ) {
          lstPermissions.add(permission.CAMERA);
        }
      }

      if (VERSION.SDK_INT >= VERSION_CODES.S) {
        if (
          ContextCompat.checkSelfPermission(
            this,
            permission.BLUETOOTH_CONNECT
          ) !=
          PackageManager.PERMISSION_GRANTED
        ) {
          lstPermissions.add(permission.BLUETOOTH_CONNECT);
        }
      }

      if (lstPermissions.size() > 0) {
        String[] permissions = new String[lstPermissions.size()];
        for (int i = 0; i < lstPermissions.size(); i++) {
          permissions[i] = lstPermissions.get(i);
        }
        ActivityCompat.requestPermissions(
          this,
          permissions,
          Common.REQUEST_PERMISSION_CALL
        );
        return;
      }
    }
    //        if (Common.client.isConnected()) {
    //            StringeeCall stringeeCall = makeCall();
    //            return stringeeCall;
    //        } else {
    //            Utils.reportMessage(this, "Stringee session not connected");
    //            return null;
    //        }
    makeCall();
  }

  @Override
  public void onBackPressed() {}

  @Override
  public void onRequestPermissionsResult(
    int requestCode,
    @NonNull String[] permissions,
    @NonNull int[] grantResults
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    boolean isGranted = false;
    if (grantResults.length > 0) {
      for (int grantResult : grantResults) {
        if (grantResult != PackageManager.PERMISSION_GRANTED) {
          isGranted = false;
          break;
        } else {
          isGranted = true;
        }
      }
    }
    if (requestCode == Common.REQUEST_PERMISSION_CALL) {
      if (!isGranted) {
        isPermissionGranted = false;
        tvState.setText("Ended");
        dismissLayout();
      } else {
        isPermissionGranted = true;
        makeCall();
      }
    }
  }

  private boolean isValidURL(String url){
    try {
      new URL(url).toURI();
      return true;
    }
    catch (Exception e) {
      return  false;
    }
  }
  /**
   * Initialize the UI elements of the outgoing call activity.
   */
  private void initView() {
    vRemote = findViewById(id.v_remote);

    vControl = findViewById(id.v_control);

    tvState = findViewById(id.tv_state);

    textPipMode = findViewById(id.text_pip_mode);

    vName = findViewById(id.v_name);

    vAvatar = findViewById(id.v_avatar);

    btnMute = findViewById(id.btn_mute);
    btnMute.setOnClickListener(this);
    btnSpeaker = findViewById(id.btn_speaker);
    btnSpeaker.setOnClickListener(this);
    btnVideo = findViewById(id.btn_video);
    btnVideo.setOnClickListener(this);
    // btnSwitch.setOnClickListener(this);
    btnEnd = findViewById(id.btn_end);
    btnEnd.setOnClickListener(this);
    btnBack = findViewById(id.btn_back);
    btnBack.setOnClickListener(this);

    isSpeaker = false;
    vName.setText(name);
    // isSpeaker = isVideoCall;
    btnSpeaker.setBackgroundResource(
      isSpeaker ? drawable.btn_speaker_on : drawable.btn_speaker_off
    );

    isVideo = false;
    // isVideo = isVideoCall;
    btnVideo.setImageResource(
      isVideo ? drawable.btn_video : drawable.btn_video_off
    );

    btnVideo.setVisibility(isVideo ? View.VISIBLE : View.GONE);
    // btnSwitch.setVisibility(isVideo ? View.VISIBLE : View.GONE);
    if(isValidURL(avatar)) Picasso.get().load(avatar).resize(300, 300).into(vAvatar);
  }

  /**
   * Initiates an outgoing call using the Stringee SDK.
   */
  private void makeCall() {
    //create audio manager to control audio device
    Log.d(Common.TAG, "make call 246");
    Log.d(Common.TAG, "Stringee connected: " + Common.client.isConnected());
    audioManager = StringeeAudioManager.create(OutgoingCallActivity.this);
    audioManager.start(
      (selectedAudioDevice, availableAudioDevices) ->
        Log.d(
          Common.TAG,
          "selectedAudioDevice: " +
          selectedAudioDevice +
          " - availableAudioDevices: " +
          availableAudioDevices
        )
    );
    audioManager.setSpeakerphoneOn(false);

    //make new call
    //        if (Common.client.isConnected()) {
    //
    //        }
    try {
      stringeeCall = new StringeeCall(Common.client, from, to);
      stringeeCall.setVideoCall(false);
      Log.d(Common.TAG, "init stringee call");
      stringeeCall.setCallListener(
        new StringeeCallListener() {
          @Override
          public void onSignalingStateChange(
            StringeeCall stringeeCall,
            final SignalingState signalingState,
            String reason,
            int sipCode,
            String sipReason
          ) {
            runOnUiThread(
              () -> {
                Log.d(Common.TAG, "onSignalingStateChange: " + signalingState);
                mSignalingState = signalingState;
                switch (signalingState) {
                  case CALLING:
                    tvState.setText("Outgoing call");
                    textPipMode.setText("Outgoing call");
                    break;
                  case RINGING:
                    tvState.setText("Ringing");
                    textPipMode.setText("Ringing");
                    break;
                  case ANSWERED:
                    tvState.setText("Starting");
                    if (mMediaState == MediaState.CONNECTED) {
                      //                      tvState.setText("Started");
                      startTimer();
                    }
                    break;
                  case BUSY:
                    tvState.setText("Busy");
                    textPipMode.setText("Busy");
                    endCall();
                    break;
                  case ENDED:
                    tvState.setText("Ended");
                    textPipMode.setText("Ended");
                    endCall();
                    break;
                }
              }
            );
          }

          @Override
          public void onError(
            StringeeCall stringeeCall,
            int code,
            String desc
          ) {
            runOnUiThread(
              () -> {
                Log.d(Common.TAG, "onError: " + desc);
                Utils.reportMessage(OutgoingCallActivity.this, desc);
                tvState.setText("Ended");
                dismissLayout();
              }
            );
          }

          @Override
          public void onHandledOnAnotherDevice(
            StringeeCall stringeeCall,
            SignalingState signalingState,
            String desc
          ) {
            Log.d(Common.TAG, "onHandle on another device: " + signalingState);
          }

          @Override
          public void onMediaStateChange(
            StringeeCall stringeeCall,
            final MediaState mediaState
          ) {
            runOnUiThread(
              () -> {
                Log.d(Common.TAG, "onMediaStateChange: " + mediaState);
                mMediaState = mediaState;
                if (mediaState == MediaState.CONNECTED) {
                  if (mSignalingState == SignalingState.ANSWERED) {
                    tvState.setText("Started");
                  }
                }
              }
            );
          }

          @Override
          public void onLocalStream(final StringeeCall stringeeCall) {
            runOnUiThread(
              () -> {
                Log.d(Common.TAG, "onLocalStream");
                if (stringeeCall.isVideoCall()) {
                  // vLocal.removeAllViews();
                  // vLocal.addView(stringeeCall.getLocalView());
                  stringeeCall.renderLocalView(true);
                }
              }
            );
          }

          @Override
          public void onRemoteStream(final StringeeCall stringeeCall) {
            runOnUiThread(
              () -> {
                Log.d(Common.TAG, "onRemoteStream");
                if (stringeeCall.isVideoCall()) {
                  vRemote.removeAllViews();
                  vRemote.addView(stringeeCall.getRemoteView());
                  stringeeCall.renderRemoteView(false);
                }
              }
            );
          }

          @Override
          public void onCallInfo(
            StringeeCall stringeeCall,
            final JSONObject jsonObject
          ) {
            runOnUiThread(
              () -> Log.d(Common.TAG, "onCallInfo: " + jsonObject.toString())
            );
          }
        }
      );
      // ver 1.9.3
      //      stringeeCall.makeCall();
      // ver 2.0
      Log.d(Common.TAG, "stringee make call line 410");
      stringeeCall.makeCall(statusListener);
      //      Log.d(Common.TAG, "ringing ...");
      //      stringeeCall.ringing(statusListener);
      //return stringeeCall;
    } catch (Exception e) {
      Log.d(Common.TAG, "ERROR: " + e.getMessage());
      return;
    }
  }

  private void switchToPIPmode() {
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      // 16, 9
      Rational rational = new Rational(16, 10);
      enterPictureInPictureMode(
        new PictureInPictureParams.Builder().setAspectRatio(rational).build()
      );
    }
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    if (id == R.id.btn_mute) {
      isMute = !isMute;
      btnMute.setBackgroundResource(
        isMute ? drawable.btn_mute : drawable.btn_mic
      );
      if (stringeeCall != null) {
        stringeeCall.mute(isMute);
      }
    } else if (id == R.id.btn_speaker) {
//      btnSpeaker.setBackgroundResource(
//        isSpeaker ? drawable.btn_speaker_on : drawable.btn_speaker_off
//      );
//      if (audioManager != null) {
//        audioManager.setSpeakerphoneOn(isSpeaker);
//      }
      if(isSpeaker) setSpeakerOff();
      else setSpeakerOn();
    } else if (id == R.id.btn_end) {
      tvState.setText("Ended");
      endCall();
    } else if (id == R.id.btn_video) {
      isVideo = !isVideo;
      btnVideo.setImageResource(
        isVideo ? drawable.btn_video : drawable.btn_video_off
      );
      if (stringeeCall != null) {
        stringeeCall.enableVideo(isVideo);
      }
    } else if (id == R.id.btn_back) {
      switchToPIPmode();
    }
    //        else if (id == R.id.btn_switch) {
    //            if (stringeeCall != null) {
    //                stringeeCall.switchCamera(
    //                        new StatusListener() {
    //                            @Override
    //                            public void onSuccess() {
    //                            }
    //
    //                            @Override
    //                            public void onError(StringeeError stringeeError) {
    //                                super.onError(stringeeError);
    //                                runOnUiThread(
    //                                        () -> {
    //                                            Log.d(
    //                                                    Common.TAG,
    //                                                    "switchCamera error: " + stringeeError.getMessage()
    //                                            );
    //                                            Utils.reportMessage(
    //                                                    OutgoingCallActivity.this,
    //                                                    stringeeError.getMessage()
    //                                            );
    //                                        }
    //                                );
    //                            }
    //                        }
    //                );
    //            }
    //        }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(Common.TAG, "app closed");
    timer.cancel();
    try{
      // ver 1.9.0
      //     stringeeCall.hangup();
      // ver 2.0
      stringeeCall.hangup(statusListener);
      if(Common.client != null && Common.client.isConnected()) Common.client.disconnect();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void endCall() {
    // ver 1.9.0
    //    stringeeCall.hangup();
    // ver 2.0
    try {
      stringeeCall.hangup(statusListener);
      if(Common.client != null && Common.client.isConnected()) Common.client.disconnect();
      stopService();
      dismissLayout();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void dismissLayout() {
    if (audioManager != null) {
      audioManager.stop();
      audioManager = null;
    }
    sensorManagerUtils.releaseSensor();
    vControl.setVisibility(View.GONE);
    btnEnd.setVisibility(View.GONE);
    seconds = 0;
    timer.cancel();
    // btnSwitch.setVisibility(View.GONE);
    Utils.postDelay(
      () -> {
        Common.isInCall = false;
        if (!isPermissionGranted) {
          Intent intent = new Intent();
          intent.setAction("open_app_setting");
          setResult(RESULT_CANCELED, intent);
        }
        finish();
      },
      1000
    );
  }

  private void startTimer() {
    timer.scheduleAtFixedRate(
      new TimerTask() {
        @Override
        public void run() {
          seconds++;
          String time = "";
          int minus = (int) (seconds / 60);
          int second = seconds % 60;
          if (minus <= 9) time = "0" + String.valueOf(minus); else time =
            String.valueOf(minus);

          if (second <= 9) time += ":0" + String.valueOf(second); else time +=
            ":" + String.valueOf(second);

          tvState.setText(time);
          textPipMode.setText(time);
        }
      },
      0,
      1000
    );
  }

  private void setSpeakerOn() {
    isSpeaker = true;
    AudioManager aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    boolean isBluetoothConnected = aManager.isBluetoothA2dpOn();
    if(isBluetoothConnected) {
//      audioManager.setBluetoothScoOn(false)
      aManager.stopBluetoothSco();
      aManager.setBluetoothScoOn(false);
    }
    if(audioManager != null) audioManager.setSpeakerphoneOn(true);
    btnSpeaker.setBackgroundResource(drawable.btn_speaker_on);
  }

  private void setSpeakerOff() {
    isSpeaker = false;
    AudioManager aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    boolean isBluetoothConnected = aManager.isBluetoothA2dpOn();
    if(isBluetoothConnected) {
      Log.d(Common.TAG, "bluetooth connected");
      aManager.setMode(AudioManager.MODE_NORMAL);
      aManager.startBluetoothSco();
      aManager.setBluetoothScoOn(true);
    }
    else if(audioManager!=null) audioManager.setSpeakerphoneOn(false);
    btnSpeaker.setBackgroundResource(drawable.btn_speaker_off);
  }

  public void setViewInPIPmode() {
    btnBack.setVisibility(View.INVISIBLE);
    vAvatar.setVisibility(View.INVISIBLE);
    btnEnd.setVisibility(View.INVISIBLE);
    btnSpeaker.setVisibility(View.INVISIBLE);
    btnMute.setVisibility(View.INVISIBLE);

    textPipMode.setVisibility(View.VISIBLE);
  }

  public void setViewInNormalMode() {
    btnBack.setVisibility(View.VISIBLE);
    vAvatar.setVisibility(View.VISIBLE);
    btnEnd.setVisibility(View.VISIBLE);
    btnSpeaker.setVisibility(View.VISIBLE);
    btnMute.setVisibility(View.VISIBLE);

    textPipMode.setVisibility(View.INVISIBLE);
  }

  @Override
  public void onPictureInPictureModeChanged(
    boolean isInPictureInPictureMode,
    Configuration newConfig
  ) {
    super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

    Log.d(Common.TAG, String.valueOf(isInPictureInPictureMode) + " line 614");
    if (isInPictureInPictureMode) {
      // Hide the full-screen UI (controls, etc.) while in picture-in-picture mode.
      //      Log.d(Common.TAG, "enter PIP mode");
      setViewInPIPmode();
    } else {
      // Restore the full-screen UI.
      if (getLifecycle().getCurrentState() == Lifecycle.State.CREATED) {
        //      finishAndRemoveTask();
        //when user click on Close button of PIP this will trigger, do what you want here
        setViewInNormalMode();
        endCall();
        //        Log.d(Common.TAG, "click close btn");
      } else if (getLifecycle().getCurrentState() == Lifecycle.State.STARTED) {
        //when PIP maximize this will trigger
        //        Log.d(Common.TAG, "click maximum");
        setViewInNormalMode();
      }
    }
  }

  @Override
  public void onUserLeaveHint() {
    if (VERSION.SDK_INT >= VERSION_CODES.N) {
      if (!isInPictureInPictureMode()) {
        switchToPIPmode();
      }
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    Log.d(Common.TAG, "app closed");
  }
}
