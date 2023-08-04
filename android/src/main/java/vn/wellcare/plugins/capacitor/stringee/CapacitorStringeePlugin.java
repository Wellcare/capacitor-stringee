package vn.wellcare.plugins.capacitor.stringee;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StringeeConnectionListener;
import org.json.JSONObject;

@CapacitorPlugin(name = "CapacitorStringee")
public class CapacitorStringeePlugin extends Plugin {

  private Context context;

  public void CapacitorStringee(Context context) {
    this.context = context;
  }

  @PluginMethod
  public void StringeeConnect(PluginCall call) {
    String token = call.getString("token");
    // issue 1
    // String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InN0cmluZ2VlLWFwaTt2PTEifQ.eyJqdGkiOiJTS3NXTXlPZjhqUm51c3dPS1pEOXI5alZtVHE2bXB2MzhCXzE2ODM2ODcwMzA4MDgiLCJpc3MiOiJTS3NXTXlPZjhqUm51c3dPS1pEOXI5alZtVHE2bXB2MzhCIiwiZXhwIjoxNjgzNzczNDMwODA4LCJ1c2VySWQiOiIzODcyODMiLCJpYXQiOjE2ODM2ODcwMzB9.LHTymQDrGSwTKSAkZfddq-w5S_7Y0M8nAbDjr3jasIo";
    Log.d(Common.TAG, "StringeeConnect : " + token);

    if (token == null || token.isEmpty()) {
      call.reject("Token is missing or empty");
      return;
    }
    Context context = getContext();
    // Create a StringeeConnectionActivity object with the current context
    // StringeeConnectionActivity connectionActivity = new StringeeConnectionActivity(context);

    // Initialize and connect the StringeeClient with the given token
    // StringeeClient client = connectionActivity.initAndConnectStringee(token);

    if (Common.client == null) {
      // Connection succeeded, return success result
      JSObject result = new JSObject();
      result.put("message", "Connected to Stringee server");
      call.resolve(result);
      Common.client = new StringeeClient(context);
      Common.client.setConnectionListener(
        new StringeeConnectionListener() {
          @Override
          public void onConnectionConnected(
            StringeeClient stringeeClient,
            boolean b
          ) {
            Log.d(Common.TAG, "onConnectionConnected - isReconnected: " + b);
            Log.d(Common.TAG, "user id: " + stringeeClient.getUserId());
            JSObject ret = new JSObject();
            ret.put("client", stringeeClient);
            ret.put("isReconnected", b);
            call.resolve(ret);
            notifyListeners("onConnectionConnected", ret);
          }

          @Override
          public void onConnectionDisconnected(
            StringeeClient stringeeClient,
            boolean isReconnecting
          ) {
            Log.d(
              Common.TAG,
              "onConnectionDisconnected - isReconnecting: " + isReconnecting
            );
            JSObject ret = new JSObject();
            ret.put("client", stringeeClient);
            ret.put("isReconnecting", isReconnecting);
            notifyListeners("onConnectionDisconnected", ret);
          }

          @Override
          public void onIncomingCall(StringeeCall stringeeCall) {
            Log.d(
              Common.TAG,
              "onIncomingCall: callId - " + stringeeCall.getCallId()
            );
            JSObject ret = new JSObject();
            ret.put("callId", stringeeCall.getCallId());
            notifyListeners("onIncomingCall", ret);
          }

          @Override
          public void onIncomingCall2(StringeeCall2 stringeeCall2) {
            Log.d(
              Common.TAG,
              "onIncomingCall2: callId - " + stringeeCall2.getCallId()
            );
            JSObject ret = new JSObject();
            ret.put("callId", stringeeCall2.getCallId());
            notifyListeners("onIncomingCall", ret);
          }

          @Override
          public void onConnectionError(
            StringeeClient stringeeClient,
            StringeeError stringeeError
          ) {
            Log.d(
              Common.TAG,
              "onConnectionError: " + stringeeError.getMessage()
            );
            JSObject ret = new JSObject();
            ret.put("client", stringeeClient);
            ret.put("message", stringeeError.getMessage());
            ret.put("error", stringeeError);
            notifyListeners("onConnectionError", ret);
          }

          @Override
          public void onRequestNewToken(StringeeClient stringeeClient) {
            Log.d(Common.TAG, "onRequestNewToken");
            JSObject ret = new JSObject();
            ret.put("client", stringeeClient);
            notifyListeners("onRequestNewToken", ret);
            // Get new token here and connect to Stringe server
          }

          @Override
          public void onCustomMessage(String from, JSONObject msg) {
            Log.d(
              Common.TAG,
              "onCustomMessage: from - " + from + " - msg - " + msg
            );
            JSObject ret = new JSObject();
            ret.put("from", from);
            ret.put("msg", msg);
            notifyListeners("onCustomMessage", ret);
          }

          @Override
          public void onTopicMessage(String from, JSONObject msg) {
            Log.d(
              Common.TAG,
              "onTopicMessage: from - " + from + " - msg - " + msg
            );
            JSObject ret = new JSObject();
            ret.put("from", from);
            ret.put("msg", msg);
            notifyListeners("onTopicMessage", ret);
          }
        }
      );
    } else {
      // Connection failed, return error result
      call.reject("Unable to connect to Stringee server");
    }
    Common.client.connect(token);
  }

  @PluginMethod
  public void StringeeCall(PluginCall call) {
    Log.d(Common.TAG, "StringeeCall");
    String from = call.getString("from");
    String to = call.getString("to");
    String name = call.getString("displayName", "User");
    String avatar = call.getString("displayImage", "");
    Context context = getContext();
    Intent intent = new Intent(context, OutgoingCallActivity.class);
    intent.putExtra("from", from);
    intent.putExtra("to", to);
    intent.putExtra("is_video_call", false);
    intent.putExtra("name", name);
    intent.putExtra("avatar", avatar);
    context.startActivity(intent);
    //    OutgoingCallActivity outgoingCallActivity = new OutgoingCallActivity();
    //    StringeeCall stringeeCall = outgoingCallActivity.startStringeeCall(from, to, false);
    //    stringeeCall.setCallListener(
    //      new StringeeCallListener() {
    //        @Override
    //        public void onSignalingStateChange(
    //          StringeeCall stringeeCall,
    //          final SignalingState signalingState,
    //          String reason,
    //          int sipCode,
    //          String sipReason
    //        ) {
    //          JSObject ret = new JSObject();
    //          ret.put("stringeeCall", stringeeCall);
    //          ret.put("reason", reason);
    //          ret.put("sipCode", sipCode);
    //          ret.put("sipReason", sipReason);
    //          call.resolve(ret);
    //          notifyListeners("onSignalingStateChange", ret);
    //        }
    //
    //        @Override
    //        public void onError(StringeeCall stringeeCall, int code, String desc) {
    //          JSObject ret = new JSObject();
    //          ret.put("stringeeCall", stringeeCall);
    //          ret.put("code", code);
    //          ret.put("desc", desc);
    //          call.resolve(ret);
    //        }
    //
    //        @Override
    //        public void onHandledOnAnotherDevice(
    //          StringeeCall stringeeCall,
    //          SignalingState signalingState,
    //          String desc
    //        ) {
    //          JSObject ret = new JSObject();
    //          ret.put("stringeeCall", stringeeCall);
    //          ret.put("signalingState", signalingState);
    //          ret.put("desc", desc);
    //          call.resolve(ret);
    //          notifyListeners("onHandledOnAnotherDevice", ret);
    //        }
    //
    //        @Override
    //        public void onMediaStateChange(
    //          StringeeCall stringeeCall,
    //          final MediaState mediaState
    //        ) {
    //          JSObject ret = new JSObject();
    //          ret.put("stringeeCall", stringeeCall);
    //          ret.put("mediaState", mediaState);
    //          call.resolve(ret);
    //          notifyListeners("onMediaStateChange", ret);
    //        }
    //
    //        @Override
    //        public void onLocalStream(final StringeeCall stringeeCall) {
    //          JSObject ret = new JSObject();
    //          ret.put("stringeeCall", stringeeCall);
    //          call.resolve(ret);
    //          notifyListeners("onLocalStream", ret);
    //        }
    //
    //        @Override
    //        public void onRemoteStream(final StringeeCall stringeeCall) {
    //          JSObject ret = new JSObject();
    //          ret.put("stringeeCall", stringeeCall);
    //          call.resolve(ret);
    //          notifyListeners("onRemoteStream", ret);
    //        }
    //
    //        @Override
    //        public void onCallInfo(
    //          StringeeCall stringeeCall,
    //          final JSONObject jsonObject
    //        ) {
    //          JSObject ret = new JSObject();
    //          ret.put("stringeeCall", stringeeCall);
    //          ret.put("data", jsonObject);
    //          call.resolve(ret);
    //          notifyListeners("onCallInfo", ret);
    //        }
    //      }
    //    );
  }

  @PluginMethod
  public void StringeeReject(PluginCall call) {
    Log.d(Common.TAG, "StringeeReject");
  }
  // @PluginMethod
  // public void StringeeHangup(PluginCall call) {
  //   implementation.StringeeHangup((data) -> {
  //     JSObject ret = new JSObject();
  //     ret.put("data", data);
  //     call.resolve(ret);
  //   });
  // }
}
