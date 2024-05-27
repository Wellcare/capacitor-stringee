package vn.wellcare.plugins.capacitor.stringee;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class OutgoingCallService extends Service {

  public static final String CHANNEL_ID = "OutgoingCallServiceChannel";

  public OutgoingCallService() {}

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Intent notificationIntent = new Intent(this, OutgoingCallActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(
      this,
      0,
      notificationIntent,
      PendingIntent.FLAG_IMMUTABLE
    );

    String name = intent.getStringExtra("name");
    createNotificationChannel();
    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle(name)
      .setContentText("Calling ...")
      .setContentIntent(pendingIntent)
      .setSmallIcon(R.drawable.ic_answer_call)
      .setOngoing(true)
      .build();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      startForeground(1, notification, FOREGROUND_SERVICE_TYPE_PHONE_CALL);
    } else {
      startForeground(1, notification);
    }

    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(Common.TAG, "service destroy");
    if (
      Common.client != null && Common.client.isConnected()
    ) Common.client.disconnect();
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    Log.d(Common.TAG, "service closed");
    if (
      Common.client != null && Common.client.isConnected()
    ) Common.client.disconnect();
    stopSelf();
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel serviceChannel = new NotificationChannel(
        CHANNEL_ID,
        "Outgoing Call Service Channel",
        NotificationManager.IMPORTANCE_DEFAULT
      );

      NotificationManager manager = getSystemService(NotificationManager.class);
      if (manager != null) {
        manager.createNotificationChannel(serviceChannel);
      }
    }
  }
}
