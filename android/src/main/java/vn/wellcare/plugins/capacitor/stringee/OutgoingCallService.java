package vn.wellcare.plugins.capacitor.stringee;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

// docs: https://developer.android.com/guide/components/foreground-services
public class OutGoingCallService extends Service {
    public OutGoingCallService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, OutgoingCallActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = null;
        String name = intent.getStringExtra("name");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, OutgoingCallActivity.CHANNEL_ID)
                    .setContentTitle(name)
                    .setContentText("Calling ...")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_answer_call)
                    .setOngoing(true)
                    .build();
        }

        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Common.TAG, "service destroy");
        if(Common.client != null && Common.client.isConnected()) Common.client.disconnect();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(Common.TAG, "service closed");
        if(Common.client != null && Common.client.isConnected()) Common.client.disconnect();
        stopSelf();
    }
}